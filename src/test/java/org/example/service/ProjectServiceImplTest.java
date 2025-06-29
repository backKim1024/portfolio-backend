package org.example.service;

import org.example.model.Project;
import org.example.repository.projectRepository.MemoryProjectRepository;
import org.example.repository.projectRepository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceImplTest {

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        ProjectRepository projectRepository = new MemoryProjectRepository();
        projectService = new ProjectServiceImpl(projectRepository);
    }

    @Test
    void TestSaveAndFindById() {
        Project project = new Project();
        project.setTitle("Test Project");
        Project saved =projectService.save(project);

        Optional<Project> found = projectService.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Project", found.get().getTitle());
    }

    @Test
    void testFindAll() {
        Project project1 = new Project();
        project1.setTitle("A 프로젝트");
        Project project2 = new Project();
        project2.setTitle("B 프로젝트");
        projectService.save(project1);
        projectService.save(project2);

        List<Project> all = projectService.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testUpdateAllFields() {
        Project project = new Project();
        project.setTitle("A 프로젝트");
        project.setDescription("a에 대한 프로젝트");
        project.setTechStack("java");
        project.setGithubUrl("github.com/backKim1024/portfolio-backend");
//        project.setImageUrl(" ");
        Project saved = projectService.save(project);

        Project updateInfo = new Project();
        updateInfo.setTitle("A 프로젝트");
        updateInfo.setDescription("a와 B에 대한 프로젝트");
        updateInfo.setTechStack("Java와 spring");
        updateInfo.setGithubUrl("github.com/backKim1024/portfolio-backend");
//        updateInfo.setTitle("A 프로젝트");
        Project updated = projectService.update(saved.getId(),updateInfo);

        assertEquals("A 프로젝트", updated.getTitle());
        assertEquals("a와 B에 대한 프로젝트", updated.getDescription());
        assertEquals("Java와 spring", updated.getTechStack());
        assertEquals("github.com/backKim1024/portfolio-backend", updated.getGithubUrl());
    }

    @Test
    void testDeleteById() {
        Project project = new Project();
        project.setTitle("To be deleted");
        Project saved = projectService.save(project);

        boolean deleted = projectService.deleteById(saved.getId());
        assertTrue(deleted);
        assertFalse(projectService.findById(saved.getId()).isPresent());
    }

    @Test
    void testUpdateNotFound() {
        Project updateInfo = new Project();
        updateInfo.setTitle("불러오기 실패 프로젝트");

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            projectService.update(999L, updateInfo);
        });
        assertTrue(e.getMessage().contains("Project not found"));
    }
}