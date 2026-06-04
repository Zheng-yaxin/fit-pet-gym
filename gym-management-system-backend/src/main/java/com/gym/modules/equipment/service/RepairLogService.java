package com.gym.modules.equipment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.modules.equipment.domain.dto.RepairLogAddDTO;
import com.gym.modules.equipment.domain.dto.RepairLogHandleDTO;
import com.gym.modules.equipment.domain.dto.RepairLogQueryDTO;
import com.gym.modules.equipment.domain.entity.RepairLog;
import com.gym.modules.equipment.domain.vo.RepairLogVO;

public interface RepairLogService extends IService<RepairLog> {
    void handle(RepairLogHandleDTO handleDTO);

    Page<RepairLogVO> pageList(RepairLogQueryDTO queryDTO);

    void addReport(RepairLogAddDTO addDTO);

    Page<RepairLogVO> myRepair(RepairLogQueryDTO queryDTO);

    RepairLogVO detail(Long id);
}
