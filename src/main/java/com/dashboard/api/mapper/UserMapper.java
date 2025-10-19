// src/main/java/com/dashboard/api/mapper/UserMapper.java
package com.dashboard.api.mapper;

import com.dashboard.api.dto.request.RegisterRequest;
import com.dashboard.api.dto.response.UserResponse;
import com.dashboard.api.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Password will be encoded separately
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "username", source = "name") // Use name as display username
    User toEntity(RegisterRequest request);

    @Mapping(target = "username", source = "displayUsername")
    UserResponse toResponse(User user);
}