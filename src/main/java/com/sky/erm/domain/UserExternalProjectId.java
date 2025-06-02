package com.sky.erm.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserExternalProjectId implements Serializable {
    
    @Column(name = "id", nullable = false, length = 200)
    private String projectId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserExternalProjectId that = (UserExternalProjectId) o;
        return Objects.equals(projectId, that.projectId) && 
               Objects.equals(userId, that.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(projectId, userId);
    }
} 