package com.example.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroundsDTO {
  private Long gno;
  private String gtitle;
  @Builder.Default // @AllArgsConstructor가 없으면 에러,기본값초기화
  private List<GphotosDTO> gphotosDTOList = new ArrayList<>();
  private String location;
  private String sports;
  private int price;
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}
