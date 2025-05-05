package com.SolutionChallenge.ReCloset.global.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {
    // 200 OK
    LOGIN_USER_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다"),

    // Server
    ANALYSIS_SUCCESS(HttpStatus.OK, "이미지 분석 성공"),

    IMAGE_UPLOAD_SUCCESS(HttpStatus.OK, "이미지 업로드 성공"),

    REWARD_CREATED(HttpStatus.CREATED, "리워드 요청에 성공하였습니다."),
    REWARD_LIST_RETRIEVED(HttpStatus.OK, "리워드 리스트 조회 성공"),
    REWARD_DETAIL_RETRIEVED(HttpStatus.OK, "리워드 디테일 조회 성공"),
    REWARD_STATUS_UPDATED(HttpStatus.OK, "리워드 수정 성공"),

    HOME_DATA_RETRIEVED(HttpStatus.OK, "홈화면으로 정보 받아오기 성공");


    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode(){
        return httpStatus.value();
    }
}
