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

    @Enumerated(EnumType.STRING) // Enum을 String 형태로 DB에 저장
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime acceptedAt;

    @Column
    private String rejectionReason;

    @Column
    private Integer rewardGranted;

    @Builder
    public Reward(String email, String donationSite, String donationPhoto, Status status, LocalDateTime createdAt) {
        this.email = email;
        this.donationSite = donationSite;
        this.donationPhoto = donationPhoto;
        this.status = status != null ? status : Status.PENDING; // 기본값을 PENDING으로 설정
        this.createdAt = createdAt;
    }
}
