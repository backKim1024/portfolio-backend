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
    void findById() {
    }

    @Test
    void findAll() {
    }

    @Test
    void deleteById() {
    }
}