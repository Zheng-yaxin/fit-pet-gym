package com.gym.modules.equipment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.equipment.domain.dto.EquipmentAddDTO;
import com.gym.modules.equipment.domain.dto.EquipmentQueryDTO;
import com.gym.modules.equipment.domain.dto.EquipmentUpdateDTO;
import com.gym.modules.equipment.domain.entity.Equipment;
import com.gym.modules.equipment.domain.vo.EquipmentVO;

public  interface EquipmentService extends IService<Equipment> {

    Page<EquipmentVO> pageList(EquipmentQueryDTO queryDTO);

    Page<EquipmentVO> memberOptions(EquipmentQueryDTO queryDTO);

    void add(EquipmentAddDTO addDTO);

    void update(EquipmentUpdateDTO updateDTO);

    void deleteById(Long id);

    void changeStatus(Long id, Integer status);

    EquipmentVO detail(Long id);
}
