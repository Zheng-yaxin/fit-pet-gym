package com.gym.modules.equipment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.equipment.domain.dto.EquipmentCheckAddDTO;
import com.gym.modules.equipment.domain.dto.EquipmentCheckQueryDTO;
import com.gym.modules.equipment.domain.entity.EquipmentCheck;
import com.gym.modules.equipment.domain.vo.EquipmentCheckVO;

public interface EquipmentCheckService extends IService<EquipmentCheck> {

    void addCheck(EquipmentCheckAddDTO addDTO);

    Page<EquipmentCheckVO> pageList(EquipmentCheckQueryDTO queryDTO);
}