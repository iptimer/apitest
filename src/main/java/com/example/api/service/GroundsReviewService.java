package com.example.api.service;

import com.example.api.dto.GroundsReviewsDTO;
import com.example.api.entity.Grounds;
import com.example.api.entity.GroundsReviews;
import com.example.api.entity.Members;

import java.util.List;

public interface GroundsReviewService {
  List<GroundsReviewsDTO> getListOfGrounds(Long gno);
  
  Long register(GroundsReviewsDTO groundsreviewsDTO);

  void modify(GroundsReviewsDTO groundsreviewsDTO);

  void remove(Long grno);

  public default GroundsReviews dtoToEntity(GroundsReviewsDTO groundsreviewsDTO) {
    GroundsReviews groundsreviews = GroundsReviews.builder()
        .grno(groundsreviewsDTO.getGrno())
        .grounds(Grounds.builder().gno(groundsreviewsDTO.getGno()).build())
        .members(Members.builder().mid(groundsreviewsDTO.getMid()).build())
        .nowpeople(groundsreviewsDTO.getNowpeople())
        .maxpeople(groundsreviewsDTO.getMaxpeople())
        .playtime(groundsreviewsDTO.getPlaytime())
        .build();
    return groundsreviews;
  }

  default GroundsReviewsDTO entityToDto(GroundsReviews groundsreviews) {
    GroundsReviewsDTO groundsreviewsDTO = GroundsReviewsDTO.builder()
        .grno(groundsreviews.getGrno())
        .gno(groundsreviews.getGrounds().getGno())
        .mid(groundsreviews.getMembers().getMid())
        .email(groundsreviews.getMembers().getEmail())
        .nowpeople(groundsreviews.getNowpeople())
        .maxpeople(groundsreviews.getMaxpeople())
        .playtime(groundsreviews.getPlaytime())
        .regDate(groundsreviews.getRegDate())
        .modDate(groundsreviews.getModDate())
        .build();
    return groundsreviewsDTO;
  }
}
