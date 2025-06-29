package org.example.repository.projectRepository;

import org.example.model.Project;

import java.util.*;

public class MemoryProjectRepository implements ProjectRepository{
    private final Map<Long, Project> store = new HashMap<>();
    private long sequence = 0L;

    @Override
    public Project save(Project project) {
        // 새로운 프로젝트라면 id 생성
        if (project.getId() == null) {
            project.setId(++sequence);
        }
        store.put(project.getId(), project);
        return project;
    }

    @Override
    public Optional<Project> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Project> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }
}
