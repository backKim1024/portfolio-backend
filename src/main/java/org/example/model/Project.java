package org.example.model;

import java.util.Objects;

public class Project {
    private Long id; // primary key 값
    private String title; // 해당 프로젝트 타이틀
    private String description; // 해당 프로젝트의 설명
    private String techStack; // 해당 프로젝트의 기술스택
    private String githubUrl; // 해당 프로젝트의 github주소
    private String imageUrl; // 해당 프로젝트의 썸네일

    // 기본 생성자
    public Project() {}

    // 모든 필드를 받는 생성자
    public Project (Long id, String title, String description, String techStack, String githubUrl, String imageUrl ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.techStack = techStack;
        this.githubUrl = githubUrl;
        this.imageUrl = imageUrl;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechStack() {
        return techStack;
    }

    public void setTechStack(String techStack) {
        this.techStack = techStack;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // equals 메서드 : id로만 비교 (id가 같으면 같은 프로젝트로 봄)
    @Override
    public boolean equals(Object object) {
        if (this == object) return true; // 자기 자신과 비교하면 같음
        if (object == null || getClass() != object.getClass()) return false; // null 또는 다른 클래스면 다름

        Project project = (Project) object;
        return Objects.equals(id, project.id); // id 값이 같으면 같음
    }

    // hashCode 메서드 : id 기준으로 해시값 생성
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString 메서드 : 사람이 읽기 쉬운 문자열로 변환
    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", techStack='" + techStack + '\'' +
                ", githubUrl='" + githubUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

}
