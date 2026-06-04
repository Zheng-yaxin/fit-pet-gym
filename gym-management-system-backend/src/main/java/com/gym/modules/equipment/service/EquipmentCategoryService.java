package com.gym.modules.equipment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.equipment.domain.dto.EquipmentCategoryAddDTO;
import com.gym.modules.equipment.domain.dto.EquipmentCategoryUpdateDTO;
import com.gym.modules.equipment.domain.entity.EquipmentCategory;
import com.gym.modules.equipment.domain.vo.EquipmentCategoryTreeVO;

import java.util.List;

public interface EquipmentCategoryService extends IService<EquipmentCategory> {

    List<EquipmentCategoryTreeVO> treeList();

    void add(EquipmentCategoryAddDTO addDTO);

    void update(EquipmentCategoryUpdateDTO updateDTO);

    void deleteById(Long id);
}