package com.gym.modules.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.core.domain.model.LoginUser;
import com.gym.common.exception.ServiceException;
import com.gym.common.utils.SecurityUtils;
import com.gym.modules.equipment.domain.dto.RepairLogAddDTO;
import com.gym.modules.equipment.domain.dto.RepairLogHandleDTO;
import com.gym.modules.equipment.domain.dto.RepairLogQueryDTO;
import com.gym.modules.equipment.domain.entity.Equipment;
import com.gym.modules.equipment.domain.entity.RepairLog;
import com.gym.modules.equipment.domain.vo.RepairLogVO;
import com.gym.modules.equipment.mapper.RepairLogMapper;
import com.gym.modules.equipment.service.EquipmentService;
import com.gym.modules.equipment.service.RepairLogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepairLogServiceImpl extends ServiceImpl<RepairLogMapper, RepairLog> implements RepairLogService {

    @Autowired
    private EquipmentService equipmentService;

    private Long getUserId() {
        try {
            Authentication authentication = SecurityUtils.getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
                return loginUser.getUserId();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private RepairLogVO toVO(RepairLog log) {
        RepairLogVO vo = new RepairLogVO();
        BeanUtils.copyProperties(log, vo);
        vo.setRepairBy(log.getReporterId());
        vo.setHandleBy(log.getRepairerId());
        vo.setRepairRemark(log.getRemark());
        vo.setRepairTime(log.getReportTime());

        if (log.getEquipmentId() != null) {
            Equipment equipment = equipmentService.getById(log.getEquipmentId());
            if (equipment != null) {
                vo.setEquipmentName(equipment.getName());
            }
        }
        if (log.getStatus() != null && log.getStatus() == 2) {
            vo.setFinishTime(log.getRepairTime());
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addReport(RepairLogAddDTO addDTO) {
        RepairLog log = new RepairLog();
        BeanUtils.copyProperties(addDTO, log);
        log.setReportTime(new Date());

        Long currentUserId = getUserId();
        if (currentUserId != null) {
            log.setReporterId(currentUserId);
        } else if (log.getReporterId() == null) {
            throw new ServiceException("无法确定报修人");
        }

        log.setStatus(0);
        this.save(log);
        equipmentService.changeStatus(addDTO.getEquipmentId(), 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handle(RepairLogHandleDTO handleDTO) {
        RepairLog log = this.getById(handleDTO.getId());
        if (log == null) {
            throw new ServiceException("报修记录不存在");
        }

        if (handleDTO.getStatus() != null) {
            if (!List.of(1, 2, 3).contains(handleDTO.getStatus())) {
                throw new ServiceException("维修状态非法");
            }
            log.setStatus(handleDTO.getStatus());
            if (handleDTO.getStatus() == 2) {
                log.setRepairTime(handleDTO.getRepairTime() != null ? handleDTO.getRepairTime() : new Date());
                equipmentService.changeStatus(log.getEquipmentId(), 0);
            }
        }

        if (handleDTO.getRepairerId() != null) {
            log.setRepairerId(handleDTO.getRepairerId());
        }
        if (handleDTO.getCost() != null) {
            log.setCost(handleDTO.getCost());
        }
        if (handleDTO.getRemark() != null) {
            log.setRemark(handleDTO.getRemark());
        }

        this.updateById(log);
    }

    @Override
    public Page<RepairLogVO> pageList(RepairLogQueryDTO queryDTO) {
        Page<RepairLog> page = new Page<>(
                queryDTO.getPageNum() == null ? 1 : queryDTO.getPageNum(),
                queryDTO.getPageSize() == null ? 10 : queryDTO.getPageSize()
        );

        LambdaQueryWrapper<RepairLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getEquipmentId() != null, RepairLog::getEquipmentId, queryDTO.getEquipmentId())
                .eq(queryDTO.getReporterId() != null, RepairLog::getReporterId, queryDTO.getReporterId())
                .eq(queryDTO.getStatus() != null, RepairLog::getStatus, queryDTO.getStatus())
                .orderByDesc(RepairLog::getCreateTime);

        Page<RepairLog> entityPage = this.page(page, wrapper);
        Page<RepairLogVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<RepairLogVO> voList = entityPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public Page<RepairLogVO> myRepair(RepairLogQueryDTO queryDTO) {
        Long currentUserId = getUserId();
        if (currentUserId == null) {
            throw new ServiceException("用户未登录");
        }
        queryDTO.setReporterId(currentUserId);
        return pageList(queryDTO);
    }

    @Override
    public RepairLogVO detail(Long id) {
        RepairLog log = this.getById(id);
        if (log == null) {
            throw new ServiceException("报修记录不存在");
        }
        return toVO(log);
    }
}
