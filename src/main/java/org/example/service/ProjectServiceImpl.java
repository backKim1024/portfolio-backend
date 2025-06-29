package org.example.service;

import org.example.model.Project;
import org.example.repository.projectRepository.ProjectRepository;

import java.util.List;
import java.util.Optional;

public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project update(Long id, Project project) {
        Optional<Project> existing = projectRepository.findById(id);
        if (existing.isPresent()) {
            Project toUpdate = existing.get();
            toUpdate.setTitle(project.getTitle());
            toUpdate.setDescription(project.getDescription());
            toUpdate.setTechStack(project.getTechStack());
            toUpdate.setGithubUrl(project.getGithubUrl());
//            toUpdate.setImageUrl(project.getImageUrl());
            return  projectRepository.save(toUpdate);
        } else {
            throw new IllegalArgumentException("Project not found: id=" + id);
        }

    }

    @Override
    public boolean deleteById(Long id) {
        Optional<Project> existing = projectRepository.findById(id);
        if (existing.isPresent()) {
            projectRepository.deleteById(id);
            return true;
        }
        return  false;
    }
}
