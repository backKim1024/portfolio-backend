package org.example.service;

import org.example.model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    Project save(Project project);              // 프로젝트 생성
    Optional<Project> findById(Long id);        // id로 조회
    List<Project> findAll();                    // 전체 조회
    Project update(Long id, Project project);   // 수정
    boolean deleteById(Long id);                // 삭제
}
