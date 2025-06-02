package com.sky.erm.mapper;

import com.sky.erm.domain.ErmUser;
import com.sky.erm.domain.UserExternalProject;
import com.sky.erm.domain.UserExternalProjectId;
import com.sky.erm.model.CreateProjectRequestDto;
import com.sky.erm.model.ProjectResponseDto;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProjectMapper {

    @Mapping(target = "id", expression = "java(createId(dto.getId(), user.getId()))")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "name", source = "dto.name")
    UserExternalProject toEntity(CreateProjectRequestDto dto, ErmUser user);

    @Mapping(target = "id", source = "id.projectId")
    ProjectResponseDto toDto(UserExternalProject entity);

    @Mapping(target = "id.projectId", source = "id")
    @Mapping(target = "user", ignore = true)
    void updateEntity(@MappingTarget UserExternalProject entity, CreateProjectRequestDto dto);

    @Named("createUserExternalProjectId")
    default UserExternalProjectId createId(String projectId, Long userId) {
        return new UserExternalProjectId(projectId, userId);
    }
} 