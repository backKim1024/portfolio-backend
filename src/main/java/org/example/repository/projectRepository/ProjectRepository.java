package org.example.repository.projectRepository;

import org.example.model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    Project save(Project project); // 프로젝트 저장

    Optional<Project> findById(Long id); // Id로 프로젝트 조회
    List<Project> findAll(); // 모든 프로젝트 목록 조회
    void deleteById(Long id); // id로 프로젝트 삭제
}
