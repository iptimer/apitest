package com.example.api.service;

import com.example.api.dto.GroundsDTO;
import com.example.api.dto.PageRequestDTO;
import com.example.api.dto.PageResultDTO;
import com.example.api.entity.Grounds;
import com.example.api.entity.Gphotos;
import com.example.api.repository.GroundsRepository;
import com.example.api.repository.GphotosRepository;
import com.example.api.repository.GroundsReviewsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class GroundsServiceImpl implements GroundsService {
  private final GroundsRepository groundsRepository;
  private final GphotosRepository gphotosRepository;
  private final GroundsReviewsRepository groundsreviewsRepository;

  @Override
  public Long register(GroundsDTO groundsDTO) {
    Map<String, Object> entityMap = dtoToEntity(groundsDTO);
    Grounds grounds = (Grounds) entityMap.get("grounds");
    List<Gphotos> gphotosList =
        (List<Gphotos>) entityMap.get("gphotosList");
    groundsRepository.save(grounds);
    if (gphotosList != null) {
      gphotosList.forEach(new Consumer<Gphotos>() {
        @Override
        public void accept(Gphotos gphotos) {
          gphotosRepository.save(gphotos);
        }
      });
    }
    return grounds.getGno();
  }

  @Override
  public PageResultDTO<GroundsDTO, Object[]> getList(PageRequestDTO pageRequestDTO) {
    Pageable pageable = pageRequestDTO.getPageable(Sort.by("gno").descending());
    Page<Object[]> result = groundsRepository.searchPage(pageRequestDTO.getType(),
        pageRequestDTO.getKeyword(),
        pageable);
    Function<Object[], GroundsDTO> fn = objects -> entityToDto(
        (Grounds) objects[0],
        (List<Gphotos>) (Arrays.asList((Gphotos) objects[1])),
        (Long) objects[2],
        (Long) objects[3]
    );
    return new PageResultDTO<>(result, fn);
  }

  @Override
  public GroundsDTO getGrounds(Long gno) {
    List<Object[]> result = groundsRepository.getGroundsWithAll(gno);
    Grounds grounds = (Grounds) result.get(0)[0];
    List<Gphotos> gphotos = new ArrayList<>();
    result.forEach(objects -> gphotos.add((Gphotos) objects[1]));
    Long nowpeople = (Long) result.get(0)[2];
    Long reviewsCnt = (Long) result.get(0)[3];

    return entityToDto(grounds, gphotos, nowpeople, reviewsCnt);
  }

  @Value("${com.example.upload.path}")
  private String uploadPath;

  @Transactional
  @Override
  public void modify(GroundsDTO groundsDTO) {
    Optional<Grounds> result = groundsRepository.findById(groundsDTO.getGno());
    if (result.isPresent()) {
      Map<String, Object> entityMap = dtoToEntity(groundsDTO);
      Grounds grounds = (Grounds) entityMap.get("grounds");
      grounds.changeTitle(groundsDTO.getGtitle());
      groundsRepository.save(grounds);
      // gphotosList :: 수정창에서 이미지 수정할 게 있는 경우의 목록
      List<Gphotos> newGphotosList =
          (List<Gphotos>) entityMap.get("gphotosList");

      List<Gphotos> oldGphotosList =
          gphotosRepository.findByMno(grounds.getGno());
      if (newGphotosList == null) {
        // 수정창에서 이미지 모두를 지웠을 때
        gphotosRepository.deleteByGno(grounds.getGno());
        for (int i = 0; i < oldGphotosList.size(); i++) {
          Gphotos oldGphotos = oldGphotosList.get(i);
          String fileName = oldGphotos.getPath() + File.separator
              + oldGphotos.getUuid() + "_" + oldGphotos.getGphotosName();
          deleteFile(fileName);
        }
      } else { // newGroundsImageList에 일부 변화 발생
        newGphotosList.forEach(gphotos -> {
          boolean result1 = false;
          for (int i = 0; i < oldGphotosList.size(); i++) {
            result1 = oldGphotosList.get(i).getUuid().equals(gphotos.getUuid());
            if (result1) break;
          }
          if (!result1) gphotosRepository.save(gphotos);
        });
        oldGphotosList.forEach(oldGphotos -> {
          boolean result1 = false;
          for (int i = 0; i < newGphotosList.size(); i++) {
            result1 = newGphotosList.get(i).getUuid().equals(oldGphotos.getUuid());
            if (result1) break;
          }
          if (!result1) {
            gphotosRepository.deleteByUuid(oldGphotos.getUuid());
            String fileName = oldGphotos.getPath() + File.separator
                + oldGphotos.getUuid() + "_" + oldGphotos.getGphotosName();
            deleteFile(fileName);
          }
        });
      }
    }
  }

  private void deleteFile(String fileName) {
    // 실제 파일도 지우기
    String searchFilename = null;
    try {
      searchFilename = URLDecoder.decode(fileName, "UTF-8");
      File file = new File(uploadPath + File.separator + searchFilename);
      file.delete();
      new File(file.getParent(), "s_" + file.getName()).delete();
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  @Transactional
  @Override
  public List<String> removeWithReviewsAndGphotos(Long gno) {
    List<Gphotos> list = gphotosRepository.findByMno(gno);
    List<String> result = new ArrayList<>();
    list.forEach(new Consumer<Gphotos>() {
      @Override
      public void accept(Gphotos t) {
        result.add(t.getPath() + File.separator + t.getUuid() + "_" + t.getGphotosName());
      }
    });
    gphotosRepository.deleteByGno(gno);
    groundsreviewsRepository.deleteByGno(gno);
    groundsRepository.deleteById(gno);
    return result;
  }

  @Override
  public void removeUuid(String uuid) {
    log.info("deleteImage... uuid: " + uuid);
    gphotosRepository.deleteByUuid(uuid);
  }
}
