package com.java.NBE4_5_3_7.domain.interview.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterviewResponseDto {
    private Long id;
    private Long head_id;
    private Long tail_id;
    private String question;
    private String model_answer;
    private String category;
    private String keyword;
    private Long next_id;
    private Long likeCount;
    private boolean likedByUser;
}
