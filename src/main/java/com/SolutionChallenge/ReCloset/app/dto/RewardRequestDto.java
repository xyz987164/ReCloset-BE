package com.SolutionChallenge.ReCloset.app.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RewardRequestDto {

    private String donationSite;
    private String donationPhoto;


    // Getter, Setter
    public String getDonationSite() {
        return donationSite;
    }

    public void setDonationSite(String donationSite) {
        this.donationSite = donationSite;
    }

    public String getDonationPhoto() {
        return donationPhoto;
    }

    public void setDonationPhoto(String donationPhoto) {
        this.donationPhoto = donationPhoto;
    }
}
