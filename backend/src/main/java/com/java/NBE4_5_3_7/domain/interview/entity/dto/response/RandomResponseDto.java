package com.java.NBE4_5_3_7.domain.interview.entity.dto.response;

import java.util.List;

public class RandomResponseDto {
    private List<Long> indexList;
    private InterviewResponseDto interviewResponseDto;

    public RandomResponseDto(List<Long> indexList, InterviewResponseDto interviewResponseDto) {
        this.indexList = indexList;
        this.interviewResponseDto = interviewResponseDto;
    }

    public List<Long> getIndexList() {
        return indexList;
    }

    public void setIndexList(List<Long> indexList) {
        this.indexList = indexList;
    }

    public InterviewResponseDto getInterviewResponseDto() {
        return interviewResponseDto;
    }

    public void setInterviewResponseDto(InterviewResponseDto interviewResponseDto) {
        this.interviewResponseDto = interviewResponseDto;
    }
}
