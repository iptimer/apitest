package com.example.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroundsReviewsDTO {
  private Long grno;
  private Long gno; // Grounds
  private Long mid; // Member
  private String email;
  private int maxpeople;
  private int nowpeople;
  private String playtime; // 경기시간
  private LocalDateTime regDate, modDate;
}
