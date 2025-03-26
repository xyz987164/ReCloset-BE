package com.SolutionChallenge.ReCloset.app.dto;

import com.SolutionChallenge.ReCloset.app.domain.Reward;
import com.SolutionChallenge.ReCloset.app.domain.RoleType;
import com.SolutionChallenge.ReCloset.app.domain.Status;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RewardDetailDto {
    private Long id;
    private String email;
    private String donationSite;
    private Status status; // Enum 타입으로 변경
    private Boolean rewardGranted;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private String rejectionReason;

    public static RewardDetailDto fromEntity(Reward reward, String role) {
        return RewardDetailDto.builder()
                .id(reward.getId())
                .email(reward.getEmail())
                .donationSite(reward.getDonationSite())
                .status(reward.getStatus()) // Enum 그대로 사용
                .rewardGranted(reward.getRewardGranted() != null && reward.getRewardGranted() == 1) // Integer -> Boolean 변환
                .imageUrl(reward.getDonationPhoto()) // donationPhoto를 imageUrl로 사용
                .createdAt(reward.getCreatedAt())
                .acceptedAt(reward.getAcceptedAt())
                .rejectionReason(reward.getRejectionReason())
                .build();
    }
}
