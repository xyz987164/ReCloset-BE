package com.SolutionChallenge.ReCloset.app.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "WITHDRAWALS")
public class WithDrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime withdrawalDate;

    @Builder
    public WithDrawal(String email, String reason) {
        this.email = email;
        this.reason = reason;
        this.withdrawalDate = LocalDateTime.now();
    }
}
