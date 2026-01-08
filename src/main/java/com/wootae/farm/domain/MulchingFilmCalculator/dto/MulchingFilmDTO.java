package com.wootae.farm.domain.MulchingFilmCalculator.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MulchingFilmDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Valid
    public static class Request{

        @NotNull(message = "면적을 입력해주세요.")
        private Integer area;
        @NotNull(message = "멀칭 비닐의 너비를 입력해주세요.")
        private Integer width;
        @NotNull(message = "멀칭 비닐의 길이을 입력해주세요.")
        private Integer height;
        @NotNull(message = "멀칭 비닐의 한개 가격을 입력해주세요.")
        private Integer price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private double needsCount;
        private double totalPrice;
        private String formattedPrice;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Record {
        private Request request;   // 당시 입력값
        private Response response; // 당시 결과값
    }

}
