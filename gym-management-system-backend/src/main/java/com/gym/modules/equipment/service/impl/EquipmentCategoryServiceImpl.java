package com.gym.modules.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.exception.ServiceException;
import com.gym.modules.equipment.domain.dto.EquipmentCategoryAddDTO;
import com.gym.modules.equipment.domain.dto.EquipmentCategoryUpdateDTO;
import com.gym.modules.equipment.domain.entity.Equipment;
import com.gym.modules.equipment.domain.entity.EquipmentCategory;
import com.gym.modules.equipment.domain.vo.EquipmentCategoryTreeVO;
import com.gym.modules.equipment.mapper.EquipmentCategoryMapper;
import com.gym.modules.equipment.mapper.EquipmentMapper;
import com.gym.modules.equipment.service.EquipmentCategoryService;
import com.gym.modules.equipment.service.EquipmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;  // 新增
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipmentCategoryServiceImpl extends ServiceImpl<EquipmentCategoryMapper, EquipmentCategory> implements EquipmentCategoryService {

    @Autowired
    private EquipmentMapper equipmentMapper;  // 只注入 Mapper，用于查询器材数量

    @Override
    public List<EquipmentCategoryTreeVO> treeList() {
        List<EquipmentCategory> all = this.list(new LambdaQueryWrapper<EquipmentCategory>()
                .orderByAsc(EquipmentCategory::getSort)
                .orderByAsc(EquipmentCategory::getId));

        return buildTree(all, 0L);
    }

    private List<EquipmentCategoryTreeVO> buildTree(List<EquipmentCategory> list, Long parentId) {
        return list.stream()
                .filter(c -> parentId.equals(c.getParentId()))
                .map(c -> {
                    EquipmentCategoryTreeVO vo = new EquipmentCategoryTreeVO();
                    BeanUtils.copyProperties(c, vo);
                    vo.setChildren(buildTree(list, c.getId()));
                    return vo;
                })
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(EquipmentCategoryAddDTO addDTO) {
        if (addDTO.getParentId() != 0) {
            checkCycle(addDTO.getParentId(), addDTO.getParentId());
        }

        EquipmentCategory category = new EquipmentCategory();
        BeanUtils.copyProperties(addDTO, category);
        if (category.getSort() == null) category.setSort(0);
        this.save(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(EquipmentCategoryUpdateDTO updateDTO) {
        EquipmentCategory category = this.getById(updateDTO.getId());
        if (category == null) {
            throw new ServiceException("分类不存在");
        }

        if (updateDTO.getId().equals(updateDTO.getParentId())) {
            throw new ServiceException("不能将分类设置为自己的父分类");
        }

        if (updateDTO.getParentId() != 0) {
            checkCycle(updateDTO.getId(), updateDTO.getParentId());
        }

        BeanUtils.copyProperties(updateDTO, category);
        if (category.getSort() == null) category.setSort(0);
        this.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        // 检查是否有子分类
        long childCount = this.count(new LambdaQueryWrapper<EquipmentCategory>().eq(EquipmentCategory::getParentId, id));
        if (childCount > 0) {
            throw new ServiceException("存在子分类，无法删除");
        }

        // 直接使用 Mapper 查询该分类下是否有器材
        long equipmentCount = equipmentMapper.selectCount(new LambdaQueryWrapper<Equipment>().eq(Equipment::getCategoryId, id));
        if (equipmentCount > 0) {
            throw new ServiceException("分类下存在器材，无法删除");
        }

        this.removeById(id);
    }

    /** 递归检查是否会形成循环父子关系 */
    private void checkCycle(Long currentId, Long targetParentId) {
        EquipmentCategory parent = this.getById(targetParentId);
        if (parent == null) return;
        if (currentId.equals(parent.getParentId())) {
            throw new ServiceException("不能形成循环父子分类关系");
        }
        if (parent.getParentId() != 0) {
            checkCycle(currentId, parent.getParentId());
        }
    }
}