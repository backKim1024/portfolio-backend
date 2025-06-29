package org.example.handler;

import org.example.model.Project;
import org.example.service.ProjectService;

import java.util.List;

public class ProjectHandler {

    private final ProjectService projectService;

    public ProjectHandler(ProjectService projectService) {
        this.projectService = projectService;
    }

    // 프로젝트 전체 조회
    public List<Project>getAllProjects() {
        return projectService.findAll();
    }

    // 프로젝트 생성
    public Project createProject(Project project) {
        return projectService.save(project);
    }

    // 프로젝트 수정
    public Project updateProject(Long id, Project project) {
        return projectService.update(id,project);
    }

    // 프로젝트 삭제
    public boolean deleteProject(long id) {
        return projectService.deleteById(id);
    }
}
