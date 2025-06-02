package com.sky.erm.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErmUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "email", nullable = false, length = 200)
    private String email;
    
    @Column(name = "password", nullable = false, length = 129)
    private String password;
    
    @Column(name = "name", length = 120)
    private String name;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserExternalProject> externalProjects = new HashSet<>();
    
    // Helper method to manage bidirectional relationship
    public void addExternalProject(UserExternalProject project) {
        externalProjects.add(project);
        project.setUser(this);
    }
    
    public void removeExternalProject(UserExternalProject project) {
        externalProjects.remove(project);
        project.setUser(null);
    }
}
