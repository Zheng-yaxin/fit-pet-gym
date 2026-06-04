package com.gym.modules.member.domain.vo;

import com.gym.modules.member.domain.entity.MemberCard;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class MemberBenefitSummaryVo {
    private Long memberId;
    private String memberName;
    private BigDecimal walletBalance;
    private Boolean active;
    private String statusLabel;
    private MemberCard card;
    private Integer daysLeft;
    private Integer groupCourseQuota;
    private Integer privateTrainingQuota;
    private Boolean unlimitedEntry;
    private Boolean lockerAccess;
    private List<String> entitlements = new ArrayList<>();
    private List<String> actions = new ArrayList<>();
    private List<BenefitAssetVo> availableAssets = new ArrayList<>();
    private List<BenefitAreaVo> recommendedAreas = new ArrayList<>();

    @Data
    public static class BenefitAssetVo {
        private Long equipmentId;
        private String name;
        private String categoryName;
        private String location;
        private Integer status;
        private String statusDesc;
        private String reason;
    }

    @Data
    public static class BenefitAreaVo {
        private Long areaId;
        private String areaName;
        private String location;
        private Integer currentCount;
        private Integer capacity;
        private Integer occupancyPercent;
        private String statusLabel;
        private String action;
    }
}
