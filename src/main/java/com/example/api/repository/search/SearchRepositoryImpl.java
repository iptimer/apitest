package com.example.api.repository.search;

import com.example.api.entity.Boards;
import com.example.api.entity.QBoards;
import com.example.api.entity.QBphotos;
import com.example.api.entity.QReviews;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class SearchRepositoryImpl extends QuerydslRepositorySupport
    implements SearchRepository {
  public SearchRepositoryImpl() {
    super(Boards.class);
  }

  @Override
  public Page<Object[]> searchPage(String type, String keyword, Pageable pageable) {
    log.info("searchPage...............");
    //1) q도메인을 확보
    QBoards boards = QBoards.boards;
    QBphotos bphotos = QBphotos.bphotos;
    QReviews reviews = QReviews.reviews;

    //2) q도메인을 조인
    JPQLQuery<Boards> jpqlQuery = from(boards);
    jpqlQuery.leftJoin(bphotos).on(bphotos.boards.eq(boards));
    jpqlQuery.leftJoin(reviews).on(reviews.boards.eq(boards));

    //3) Tuple생성 : 조인을 한 결과의 데이터를 tuple로 생성
    JPQLQuery<Tuple> tuple = jpqlQuery.select(boards, bphotos, reviews.likes.count(),reviews.count());

    //4) 조건절 생성
    BooleanBuilder booleanBuilder = new BooleanBuilder();
    BooleanExpression expression = boards.bno.gt(0L);
    booleanBuilder.and(expression);

    //5) 검색조건 파악
    if (type != null) {
      String[] typeArr = type.split("");
      BooleanBuilder conditionBuilder = new BooleanBuilder();
      for (String t : typeArr) {
        switch (t) {
          case "t":
            conditionBuilder.or(boards.title.contains(keyword)); break;
          case "w":
            conditionBuilder.or(reviews.members.email.contains(keyword)); break;
          case "c":
            conditionBuilder.or(reviews.text.contains(keyword)); break;
        }
      }
      booleanBuilder.and(conditionBuilder);
    }

    //6) 조인된 tuple에서 조건절에 위에서 생성한 booleanBuilder로 검색한다.
    tuple.where(booleanBuilder);

    //7) 정렬조건 생성
    Sort sort = pageable.getSort();
    sort.stream().forEach(order -> {
      Order direction = order.isAscending() ? Order.ASC : Order.DESC;
      String prop = order.getProperty();
      PathBuilder orderByExpression = new PathBuilder(Boards.class, "boards");
      tuple.orderBy(new OrderSpecifier<Comparable>(direction, orderByExpression.get(prop)));
    });

    //8) 데이터를 출력하기 위한 그룹을 생성
    tuple.groupBy(boards);

    //9) 원하는 데이터를 들고오기 위해서 시작위치즉 offset을 지정
    tuple.offset(pageable.getOffset());

    //10) offset부터 들고올 갯수 지정
    tuple.limit(pageable.getPageSize());

    //11) 최종 결과를 tuple의 fetch()함수를 통해서 컬렉션에 저장
    List<Tuple> result = tuple.fetch();
    log.info(result);

    //12) 최종결과의 갯수 출력
    long count = tuple.fetchCount();
    log.info("COUNT: " + count);

    //13) 출력하고자 하는 Page객체를 위한 PageImpl객체로 생성
    return new PageImpl<Object[]>(result.stream().map(t -> t.toArray()).collect(Collectors.toList()), pageable, count);
  }
}
