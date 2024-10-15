package com.example.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "writer")
public class Boards extends BasicEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long bno;

  private String title;
//  private String content;
//
//  @ManyToOne(fetch = FetchType.LAZY)
//  private Members writer;

  public void changeTitle(String title) {
    this.title = title;
  }

//  public void changeContent(String content) {
//    this.content = content;
//  }
}
