package com.example.api.repository;

import com.example.api.entity.Members;
import com.example.api.entity.MembersRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.IntStream;

@SpringBootTest
class MembersRepositoryTests {
  @Autowired
  MembersRepository membersRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  public void insertMembers() {
    IntStream.rangeClosed(1, 100).forEach(i -> {
      Members members = Members.builder()
          .email("a" + i + "@a.com")
          .pw(passwordEncoder.encode("1"))
          .name("name" + i)
          .birth("010101")
          .phone("010-1111-1111")
          .build();
      members.addMemberRole(MembersRole.USER);
      if(i>80) members.addMemberRole(MembersRole.MANAGER);
      if(i>90) members.addMemberRole(MembersRole.ADMIN);
      membersRepository.save(members);
    });
  }
}