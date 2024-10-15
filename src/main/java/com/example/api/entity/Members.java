package com.example.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "pg_members")
public class Members extends BasicEntity{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long mid;
  private String email;
  private String pw;
  private String name;
  private String birth;
  private String phone;

  private boolean fromSocial;
  @ElementCollection(fetch = FetchType.LAZY)
  @Builder.Default
  private Set<MembersRole> roleSet = new HashSet<>();

  public void addMemberRole(MembersRole membersRole) {
    roleSet.add(membersRole);
  }
}
