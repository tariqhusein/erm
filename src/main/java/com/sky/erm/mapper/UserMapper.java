package com.sky.erm.mapper;

import com.sky.erm.domain.ErmUser;
import com.sky.erm.model.CreateUserRequestDto;
import com.sky.erm.model.UserResponseDto;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = {ProjectMapper.class}
)
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalProjects", ignore = true)
    ErmUser toEntity(CreateUserRequestDto dto);
    
    @Mapping(target = "projects", source = "externalProjects")
    UserResponseDto toDto(ErmUser entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalProjects", ignore = true)
    void updateEntity(@MappingTarget ErmUser entity, CreateUserRequestDto dto);
} 