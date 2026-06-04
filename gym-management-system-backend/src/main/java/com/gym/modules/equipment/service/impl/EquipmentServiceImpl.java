package com.gym.modules.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.exception.ServiceException;

import com.gym.modules.equipment.domain.dto.*;
import com.gym.modules.equipment.domain.entity.Equipment;
import com.gym.modules.equipment.domain.entity.EquipmentCategory;
import com.gym.modules.equipment.domain.vo.EquipmentVO;
import com.gym.modules.equipment.mapper.EquipmentCategoryMapper;
import com.gym.modules.equipment.mapper.EquipmentMapper;
import com.gym.modules.equipment.mapper.UserNickNameMapper;
import com.gym.modules.equipment.service.EquipmentCategoryService;
import com.gym.modules.equipment.service.EquipmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    @Autowired
    private EquipmentCategoryService categoryService;

    @Autowired
    private UserNickNameMapper userNickNameMapper;  // 注入，用于查询用户昵称

    // 批量获取用户昵称 map
    private Map<Long, String> getUserNickNameMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        List<Map<String, Object>> userList = userNickNameMapper.selectNickNameByIds(userIds);
        return userList.stream()
                .collect(Collectors.toMap(
                        map -> (Long) map.get("userId"),
                        map -> map.get("nickName") == null ? "" : (String) map.get("nickName")
                ));
    }

    @Override
    public Page<EquipmentVO> pageList(EquipmentQueryDTO queryDTO) {
        Page<Equipment> page = new Page<>(queryDTO.getPageNum() == null ? 1 : queryDTO.getPageNum(),
                queryDTO.getPageSize() == null ? 10 : queryDTO.getPageSize());

        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isBlank()) {
            wrapper.like(Equipment::getName, queryDTO.getKeyword())
                    .or().like(Equipment::getCode, queryDTO.getKeyword());
        }
        wrapper.eq(queryDTO.getCategoryId() != null, Equipment::getCategoryId, queryDTO.getCategoryId())
                .eq(queryDTO.getStatus() != null, Equipment::getStatus, queryDTO.getStatus())
                .eq(queryDTO.getCurrentManagerId() != null, Equipment::getCurrentManagerId, queryDTO.getCurrentManagerId());

        Page<Equipment> equipmentPage = this.page(page, wrapper);

        // 批量获取所有负责人ID
        List<Long> managerIds = equipmentPage.getRecords().stream()
                .map(Equipment::getCurrentManagerId)
                .filter(id -> id != null)
                .distinct()
                .toList();

        // 批量查询昵称
        Map<Long, String> nickNameMap = getUserNickNameMap(managerIds);

        Page<EquipmentVO> voPage = new Page<>(equipmentPage.getCurrent(), equipmentPage.getSize(), equipmentPage.getTotal());
        List<EquipmentVO> voList = equipmentPage.getRecords().stream().map(equipment -> {
            EquipmentVO vo = new EquipmentVO();
            BeanUtils.copyProperties(equipment, vo);
            vo.setBuyDate(equipment.getBuyDate());
            vo.setCurrentManagerId(equipment.getCurrentManagerId());
            vo.setStatusDesc(getStatusDesc(equipment.getStatus()));

            // 修改处：增加非空判断，防止 Map.of() 抛出 NPE
            if (equipment.getCurrentManagerId() != null) {
                vo.setManagerName(nickNameMap.getOrDefault(equipment.getCurrentManagerId(), ""));
            } else {
                vo.setManagerName("");
            }

            // 分类名称填充
            if (equipment.getCategoryId() != null) {
                EquipmentCategory category = categoryService.getById(equipment.getCategoryId());
                if (category != null) {
                    vo.setCategoryName(category.getName());
                }
            }

            return vo;
        }).toList();

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Page<EquipmentVO> memberOptions(EquipmentQueryDTO queryDTO) {
        EquipmentQueryDTO safeQuery = queryDTO == null ? new EquipmentQueryDTO() : queryDTO;
        if (safeQuery.getPageSize() == null || safeQuery.getPageSize() > 100) {
            safeQuery.setPageSize(100);
        }
        return pageList(safeQuery);
    }

    @Override
    public EquipmentVO detail(Long id) {
        Equipment equipment = this.getById(id);
        if (equipment == null) {
            return null;
        }

        EquipmentVO vo = new EquipmentVO();
        BeanUtils.copyProperties(equipment, vo);
        vo.setBuyDate(equipment.getBuyDate());
        vo.setCurrentManagerId(equipment.getCurrentManagerId());
        vo.setStatusDesc(getStatusDesc(equipment.getStatus()));

        // 分类名称
        if (equipment.getCategoryId() != null) {
            EquipmentCategory category = categoryService.getById(equipment.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        // 负责人姓名（单个查询）
        if (equipment.getCurrentManagerId() != null) {
            Map<Long, String> map = getUserNickNameMap(List.of(equipment.getCurrentManagerId()));
            vo.setManagerName(map.getOrDefault(equipment.getCurrentManagerId(), ""));
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(EquipmentAddDTO addDTO) {
        Equipment equipment = new Equipment();
        BeanUtils.copyProperties(addDTO, equipment);
        equipment.setBuyDate(addDTO.getBuyDate());
        equipment.setCurrentManagerId(addDTO.getCurrentManagerId());
        equipment.setStatus(0);
        this.save(equipment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(EquipmentUpdateDTO updateDTO) {
        Equipment equipment = this.getById(updateDTO.getId());
        if (equipment == null) {
            throw new ServiceException("器材不存在");
        }
        BeanUtils.copyProperties(updateDTO, equipment);
        equipment.setBuyDate(updateDTO.getBuyDate());
        equipment.setCurrentManagerId(updateDTO.getCurrentManagerId());
        this.updateById(equipment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        if (!this.removeById(id)) {
            throw new ServiceException("删除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, Integer status) {
        if (!List.of(0, 1, 2, 3).contains(status)) {
            throw new ServiceException("状态值非法");
        }
        Equipment equipment = this.getById(id);
        if (equipment == null) {
            throw new ServiceException("器材不存在");
        }
        equipment.setStatus(status);
        this.updateById(equipment);
    }

    private String getStatusDesc(Integer status) {
        return switch (status == null ? -1 : status) {
            case 0 -> "正常";
            case 1 -> "维护中";
            case 2 -> "损坏";
            case 3 -> "报废";
            default -> "未知";
        };
    }
}
