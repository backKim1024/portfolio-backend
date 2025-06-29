package org.example.repository.projectRepository;

import org.example.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MemoryProjectRepositoryTest {

    MemoryProjectRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MemoryProjectRepository();
    }

    @Test
    @DisplayName("프로젝트 저장 및 id로 조회 테스트")
    void saveAndFindById() {
        // given
        Project project = new Project();
        project.setTitle("테스트 프로젝트");

        // when
        repository.save(project);
        Long savedId = project.getId(); // Long 타입 id 사용
        Optional<Project> found = repository.findById(savedId);

        // then
        assertTrue(found.isPresent());
        assertEquals("테스트 프로젝트", found.get().getTitle());
        assertEquals(savedId, found.get().getId());
    }

    @Test
    @DisplayName("전체 프로젝트 조회 테스트")
    void findAll() {
        // given
        Project project1 = new Project();
        project1.setTitle("A 프로젝트");
        Project project2 = new Project();
        project2.setTitle(("B 프로젝트"));
        repository.save(project1);
        repository.save(project2);

        // when
        var all = repository.findAll();

        // then
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("프로젝트 삭제 테스트")
    void deleteById() {
        // given
        Project project = new Project();
        project.setTitle("삭제 대상");
        repository.save(project);

        // when
        repository.deleteById(project.getId());

        // then
        assertTrue(repository.findById(project.getId()).isEmpty());
    }
}