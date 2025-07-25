package com.SolutionChallenge.ReCloset.app.dto;

import com.SolutionChallenge.ReCloset.app.domain.Reward;
import com.SolutionChallenge.ReCloset.app.domain.RoleType;
import com.SolutionChallenge.ReCloset.app.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RewardSummaryDto {
    private Long id;
    private String email;
    private String donationSite;
    private Status status;
    private Integer rewardGranted;
    private String detailUrl;

    public static RewardSummaryDto fromEntity(Reward reward, String role) {
        String url = "/api/rewards/list/" + reward.getId();

        return new RewardSummaryDto(
                reward.getId(),
                reward.getEmail(),
                reward.getDonationSite(),
                reward.getStatus(),
                reward.getRewardGranted(),
                url
        );
    }
}
