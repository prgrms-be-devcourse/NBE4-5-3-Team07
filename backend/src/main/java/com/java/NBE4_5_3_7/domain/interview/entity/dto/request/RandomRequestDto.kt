package com.java.NBE4_5_3_7.domain.interview.entity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RandomRequestDto {
    private List<Long> indexList;
}
