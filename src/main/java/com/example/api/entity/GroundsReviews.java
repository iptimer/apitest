// 구장 상세 정보
package com.example.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"grounds", "members"})
public class GroundsReviews extends BasicEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long grno;

  @ManyToOne(fetch = FetchType.LAZY)
  private Grounds grounds;

  @ManyToOne(fetch = FetchType.LAZY)
  private Members members;

  private int nowpeople;
  private int maxpeople;
  private String playtime; // 경기시간

  public void changeNowpeople(int nowpeople) {this.nowpeople = nowpeople;}
  public void changeMaxpeople(int maxpeople) {this.maxpeople = maxpeople;}
  public void changePlaytime(String playtime) {this.playtime = this.playtime;}
}
