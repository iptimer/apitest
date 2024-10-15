package com.example.api.service;

import com.example.api.dto.GroundsDTO;
import com.example.api.dto.PageRequestDTO;
import com.example.api.dto.PageResultDTO;
import com.example.api.dto.GphotosDTO;
import com.example.api.entity.Grounds;
import com.example.api.entity.Gphotos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface GroundsService {
  Long register(GroundsDTO groundsDTO);

  PageResultDTO<GroundsDTO, Object[]> getList(PageRequestDTO pageRequestDTO);

  GroundsDTO getGrounds(Long gno);

  void modify(GroundsDTO groundsDTO);

  List<String> removeWithReviewsAndGphotos(Long gno);

  void removeUuid(String uuid);

  default Map<String, Object> dtoToEntity(GroundsDTO groundsDTO) {
    Map<String, Object> entityMap = new HashMap<>();
    Grounds grounds = Grounds.builder().gno(groundsDTO.getGno())
        .gtitle(groundsDTO.getGtitle()).build();
    entityMap.put("grounds", grounds);
    List<GphotosDTO> gphotosDTOList = groundsDTO.getGphotosDTOList();
    if (gphotosDTOList != null && gphotosDTOList.size() > 0) {
      List<Gphotos> gphotosList = gphotosDTOList.stream().map(
          new Function<GphotosDTO, Gphotos>() {
            @Override
            public Gphotos apply(GphotosDTO gphotosDTO) {
              Gphotos gphotos = Gphotos.builder()
                  .path(gphotosDTO.getPath())
                  .gphotosName(gphotosDTO.getGphotosName())
                  .uuid(gphotosDTO.getUuid())
                  .grounds(grounds)
                  .build();
              return gphotos;
            }
          }
      ).collect(Collectors.toList());
      entityMap.put("gphotosList", gphotosList);
    }
    return entityMap;
  }

  default GroundsDTO entityToDto(Grounds grounds, List<Gphotos> gphotosList, Long nowpeople, Long reviewsCnt) {
    // GroundsDTO 생성
    GroundsDTO groundsDTO = GroundsDTO.builder()
        .gno(grounds.getGno())
        .gtitle(grounds.getGtitle()) // 구장 제목
        .location(grounds.getLocation()) // 구장 위치
        .sports(grounds.getSports()) // 스포츠 종류
        .price(grounds.getPrice()) // 가격
        .regDate(grounds.getRegDate()) // 등록일
        .modDate(grounds.getModDate()) // 수정일
        .build();

    // GphotosDTO 리스트 변환
    List<GphotosDTO> gphotosDTOList = new ArrayList<>();
    if (gphotosList != null && !gphotosList.isEmpty()) {
      gphotosDTOList = gphotosList.stream().map(gphotos -> {
        GphotosDTO gphotosDTO = GphotosDTO.builder()
            .gphotosName(gphotos.getGphotosName())
            .path(gphotos.getPath())
            .uuid(gphotos.getUuid())
            .build();
        return gphotosDTO;
      }).collect(Collectors.toList());
    }

    groundsDTO.setGphotosDTOList(gphotosDTOList); // 사진 목록 설정
    return groundsDTO;
  }

}
