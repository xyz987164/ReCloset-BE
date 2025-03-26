package com.SolutionChallenge.ReCloset.app.dto;

import com.SolutionChallenge.ReCloset.app.domain.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RewardUpdateDto {
    private Status status;
    private String rejectionReason;
    private Integer rewardGranted;
}
