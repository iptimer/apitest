package com.example.api.repository;

import com.example.api.entity.Grounds;
import com.example.api.repository.search.SearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroundsRepository extends JpaRepository<Grounds, Long>, SearchRepository {

  @Query("select g.gno, g.gtitle, g.location, g.sports, g.price, count(r.nowpeople), count(distinct r) " +
      "from Grounds g left outer join GroundsReviews r on r.grounds=g group by g ")
    // Grounds와 GroundsReviews를 조인하여 각 Grounds에 대한 제목, 위치, 스포츠 종류, 가격, 리뷰 수와 현재 인원 수를 카운트
  Page<Object[]> getListPage(Pageable pageable);

  @Query("select g.gno, g.gtitle, g.location, g.sports, g.price, p, count(r.nowpeople), count(distinct r) from Grounds g " +
      "left outer join Gphotos p on p.grounds = g " +
      "left outer join GroundsReviews r on r.grounds = g group by g ")
    // Grounds와 Gphotos, GroundsReviews를 조인하여 각 Grounds에 대한 제목, 위치, 스포츠 종류, 가격, 이미지와 리뷰 수를 가져옴
  Page<Object[]> getListPageImg(Pageable pageable);

  @Query("select g.gno, g.gtitle, g.location, g.sports, g.price, max(p), count(r.nowpeople), count(distinct r) from Grounds g " +
      "left outer join Gphotos p on p.grounds = g " +
      "left outer join GroundsReviews r on r.grounds = g group by g ")
    // 각 Grounds에 대한 제목, 위치, 스포츠 종류, 가격, 최대 이미지 번호와 리뷰 수를 가져옴
  Page<Object[]> getListPageMaxImg(Pageable pageable);

  @Query(value = "select g.gno, g.gtitle, g.location, g.sports, g.price, p.pnum, p.gphotos_name, " +
      "count(r.nowpeople), count(r.grno) " +
      "from db7.gphotos p left outer join db7.grounds g on g.gno=p.grounds_gno " +
      "left outer join db7.groundsreviews r on g.gno=r.grounds_gno " +
      "where p.pnum = " +
      "(select max(pnum) from db7.gphotos p2 where p2.grounds_gno=g.gno) " +
      "group by g.gno ", nativeQuery = true)
    // 네이티브 쿼리를 사용하여 Gphotos에서 최대 이미지 번호를 가진 사진을 가진 Grounds의 정보를 가져옴
  Page<Object[]> getListPageImgNative(Pageable pageable);

  @Query("select g.gno, g.gtitle, g.location, g.sports, g.price, p, count(r.nowpeople), count(distinct r) from Grounds g " +
      "left outer join Gphotos p on p.grounds = g " +
      "left outer join GroundsReviews r on r.grounds = g " +
      "where pnum = (select max(p2.pnum) from Gphotos p2 where p2.grounds=g) " +
      "group by g ")
    // JPQL 쿼리를 사용하여 Grounds에 대한 제목, 위치, 스포츠 종류, 가격, 최대 사진 번호를 가진 정보를 가져옴
  Page<Object[]> getListPageImgJPQL(Pageable pageable);

  //  @Query("select feeds, max(p.pnum) from Photos p group by feeds")
  //  Page<Object[]> getMaxQuery(Pageable pageable);

  @Query("select g.gno, g.gtitle, g.location, g.sports, g.price, max(p.pnum) " +
      "from Grounds g left outer join Gphotos p on p.grounds = g " +
      "group by g.gno, g.gtitle, g.location, g.sports, g.price")
    // Gphotos에서 각 Grounds에 대한 제목, 위치, 스포츠 종류, 가격, 최대 사진 번호를 가져옴
  Page<Object[]> getMaxQuery(Pageable pageable);

  @Query("select g.gno, g.gtitle, g.location, g.sports, g.price, p, count(r.nowpeople), count(r) " +
      "from Grounds g left outer join Gphotos p on p.grounds=g " +
      "left outer join GroundsReviews r on r.grounds = g " +
      "where g.gno = :gno group by p ")
    // 특정 Grounds에 대한 제목, 위치, 스포츠 종류, 가격, 이미지와 리뷰 수 등 모든 정보를 가져옴
  List<Object[]> getGroundsWithAll(Long gno); // 특정 Grounds 조회
}
