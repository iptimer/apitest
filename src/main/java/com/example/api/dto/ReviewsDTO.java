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
public class ReviewsDTO {
  private Long reviewsnum;
  private Long bno; // Boards
  private Long mid; // Member
  private String email;
  private int likes;
  private String text;
  private LocalDateTime regDate, modDate;
}
