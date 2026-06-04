package com.gym.modules.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.exception.ServiceException;
import com.gym.modules.equipment.domain.dto.EquipmentCheckAddDTO;
import com.gym.modules.equipment.domain.dto.EquipmentCheckQueryDTO;
import com.gym.modules.equipment.domain.entity.EquipmentCheck;
import com.gym.modules.equipment.domain.vo.EquipmentCheckVO;
import com.gym.modules.equipment.mapper.EquipmentCheckMapper;
import com.gym.modules.equipment.mapper.UserNickNameMapper;
import com.gym.modules.equipment.service.EquipmentCheckService;
import com.gym.modules.equipment.service.EquipmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EquipmentCheckServiceImpl extends ServiceImpl<EquipmentCheckMapper, EquipmentCheck> implements EquipmentCheckService {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private UserNickNameMapper userNickNameMapper;

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
    @Transactional(rollbackFor = Exception.class)
    public void addCheck(EquipmentCheckAddDTO addDTO) {
        if (addDTO.getResult() == 1 && (addDTO.getAbnormalDesc() == null || addDTO.getAbnormalDesc().isBlank())) {
            throw new ServiceException("异常时必须填写异常描述");
        }

        EquipmentCheck check = new EquipmentCheck();
        BeanUtils.copyProperties(addDTO, check);
        this.save(check);

        if (addDTO.getResult() == 1) {
            equipmentService.changeStatus(addDTO.getEquipmentId(), 1);
        }
    }

    @Override
    public Page<EquipmentCheckVO> pageList(EquipmentCheckQueryDTO queryDTO) {
        Page<EquipmentCheck> page = new Page<>(
                queryDTO.getPageNum() == null ? 1 : queryDTO.getPageNum(),
                queryDTO.getPageSize() == null ? 10 : queryDTO.getPageSize()
        );

        LambdaQueryWrapper<EquipmentCheck> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryDTO.getEquipmentId() != null, EquipmentCheck::getEquipmentId, queryDTO.getEquipmentId())
                .eq(queryDTO.getCheckerId() != null, EquipmentCheck::getCheckerId, queryDTO.getCheckerId())
                .eq(queryDTO.getResult() != null, EquipmentCheck::getResult, queryDTO.getResult());

        Page<EquipmentCheck> entityPage = this.page(page, wrapper);
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(EquipmentCheck::getCheckTime, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(EquipmentCheck::getCheckTime, queryDTO.getEndTime());
        }
        // 批量获取巡检人姓名
        List<Long> checkerIds = entityPage.getRecords().stream()
                .map(EquipmentCheck::getCheckerId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        Map<Long, String> nickNameMap = getUserNickNameMap(checkerIds);

        Page<EquipmentCheckVO> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<EquipmentCheckVO> voList = entityPage.getRecords().stream().map(check -> {
            EquipmentCheckVO vo = new EquipmentCheckVO();
            BeanUtils.copyProperties(check, vo);

            // 巡检人姓名填充
            vo.setCheckerName(nickNameMap.getOrDefault(check.getCheckerId(), ""));

            return vo;
        }).toList();

        voPage.setRecords(voList);
        return voPage;
    }
}