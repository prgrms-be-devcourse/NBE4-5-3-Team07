package com.java.NBE4_5_3_7.domain.chat.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom {
    private Long roomId;
    private String nickname;
    private String role;
}