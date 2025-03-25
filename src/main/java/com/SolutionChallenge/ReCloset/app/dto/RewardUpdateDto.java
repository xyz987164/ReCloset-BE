package com.SolutionChallenge.ReCloset.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RewardUpdateDto {
    private String status;
    private String rejectionReason;
    private Integer rewardGranted;
}
