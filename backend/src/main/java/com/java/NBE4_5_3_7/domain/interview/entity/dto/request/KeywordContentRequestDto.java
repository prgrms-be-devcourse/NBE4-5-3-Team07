package com.java.NBE4_5_3_7.domain.interview.entity.dto.request;

import java.util.List;

public class KeywordContentRequestDto {
    private List<String> keywordList;

    public KeywordContentRequestDto(List<String> keywordList) {
        this.keywordList = keywordList;
    }

    public List<String> getKeywordList() {
        return keywordList;
    }
}