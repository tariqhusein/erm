package com.sky.erm.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tb_user_external_project")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserExternalProject {
    
    @EmbeddedId
    private UserExternalProjectId id;
    
    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private ErmUser user;
    
    @Column(name = "name", nullable = false, length = 120)
    private String name;
} 