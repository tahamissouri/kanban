package com.example.mappers;

import com.example.dtos.request.CreateUserRequestDto;
import com.example.dtos.response.UserResponseDto;
import com.example.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper extends MapperContract<UserEntity,CreateUserRequestDto,UserResponseDto> {

    @Mapping(target = "password",ignore = true)
    @Mapping(target = "id",ignore = true)
    @Mapping(target="createdAt",ignore = true)
    UserEntity toEntity(CreateUserRequestDto createUserRequestDto);


    UserResponseDto toDto(UserEntity userEntity);


}
