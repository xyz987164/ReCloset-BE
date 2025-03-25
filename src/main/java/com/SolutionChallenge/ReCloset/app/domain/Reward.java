package com.SolutionChallenge.ReCloset.app.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REWARDS")
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String donationSite;

    @Column(nullable = false)
    private String donationPhoto;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime acceptedAt;

    @Column
    private String rejectionReason;

    @Column
    private Integer rewardGranted;

    @Builder
    public Reward(String email, String donationSite, String donationPhoto, String status, LocalDateTime createdAt) {
        this.email = email;
        this.donationSite = donationSite;
        this.donationPhoto = donationPhoto;
        this.status = status != null ? status : "PENDING"; // status 기본값을 PENDING으로 설정
        this.createdAt = createdAt;
    }
}
