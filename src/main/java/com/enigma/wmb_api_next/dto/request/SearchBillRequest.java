package com.enigma.wmb_api_next.dto.request;

import lombok.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchBillRequest {
    private String daily;
    private String weeklyStart;
    private String weeklyEnd;
    private String monthly;
    private String direction;
    private String sortBy;
    private Integer page;
    private Integer size;
}
