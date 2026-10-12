package com.maraacademy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maraacademy.entity.DemoVideo;

public interface DemoVideoRepository extends JpaRepository<DemoVideo, Long> {

    List<DemoVideo> findByCategoryIgnoreCaseOrderBySortOrderAscIdAsc(String category);

    List<DemoVideo> findAllByOrderByCategoryAscSortOrderAscIdAsc();
}
