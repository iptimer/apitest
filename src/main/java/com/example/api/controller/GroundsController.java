package com.example.api.controller;

import com.example.api.dto.GroundsDTO;
import com.example.api.dto.PageRequestDTO;
import com.example.api.service.GroundsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/grounds")
@RequiredArgsConstructor
public class GroundsController {
  private final GroundsService groundsService;

  @Value("${com.example.upload.path}")
  private String uploadPath;

  private void typeKeywordInit(PageRequestDTO pageRequestDTO) {
    if (pageRequestDTO.getType().equals("null")) pageRequestDTO.setType("");
    if (pageRequestDTO.getKeyword().equals("null")) pageRequestDTO.setKeyword("");
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, Object>> list(PageRequestDTO pageRequestDTO) {
    System.out.println("pageRequestDTO: " + pageRequestDTO);
    Map<String, Object> result = new HashMap<>();
    result.put("pageResultDTO", groundsService.getList(pageRequestDTO));
    result.put("pageRequestDTO", pageRequestDTO);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping(value = "/register")
  public ResponseEntity<Long> registerGrounds(@RequestBody GroundsDTO groundsDTO) {
    Long gno = groundsService.register(groundsDTO);
    return new ResponseEntity<>(gno, HttpStatus.OK);
  }

  @GetMapping(value = {"/read/{gno}", "/modify/{gno}"}, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, GroundsDTO>> getGrounds(
      @PathVariable("gno") Long gno, @RequestBody PageRequestDTO pageRequestDTO) {
    GroundsDTO groundsDTO = groundsService.getGrounds(gno);
    typeKeywordInit(pageRequestDTO);
    Map<String, GroundsDTO> result = new HashMap<>();
    result.put("groundsDTO", groundsDTO);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping(value = "/modify", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, String>> modify(@RequestBody GroundsDTO dto,
                                                    @RequestBody PageRequestDTO pageRequestDTO) {
    log.info("modify post... dto: " + dto);
    groundsService.modify(dto);
    typeKeywordInit(pageRequestDTO);
    Map<String, String> result = new HashMap<>();
    result.put("msg", dto.getGno() + " 수정");
    result.put("gno", dto.getGno() + "");
    result.put("page", pageRequestDTO.getPage() + "");
    result.put("type", pageRequestDTO.getType());
    result.put("keyword", pageRequestDTO.getKeyword());
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping(value = "/remove/{gno}", produces = MediaType.APPLICATION_JSON_VALUE )
  public ResponseEntity<Map<String, String>> remove(
      @PathVariable Long gno, @RequestBody PageRequestDTO pageRequestDTO) {

    Map<String, String> result = new HashMap<>();
    List<String> gphotoList = groundsService.removeWithReviewsAndGphotos(gno);
    gphotoList.forEach(fileName -> {
      try {
        log.info("removeFile..." + fileName);
        String srcFileName = URLDecoder.decode(fileName, "UTF-8");
        File file = new File(uploadPath + File.separator + srcFileName);
        file.delete();
        File thumb = new File(file.getParent(), "s_" + file.getName());
        thumb.delete();
      } catch (Exception e) {
        log.info("remove file : " + e.getMessage());
      }
    });
    if (groundsService.getList(pageRequestDTO).getDtoList().size() == 0 && pageRequestDTO.getPage() != 1) {
      pageRequestDTO.setPage(pageRequestDTO.getPage() - 1);
    }
    typeKeywordInit(pageRequestDTO);
    result.put("msg", gno + " 삭제");
    result.put("page", pageRequestDTO.getPage() + "");
    result.put("type", pageRequestDTO.getType() + "");
    result.put("keyword", pageRequestDTO.getKeyword() + "");
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
