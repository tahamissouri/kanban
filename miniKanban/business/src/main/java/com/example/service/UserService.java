package com.example.service;


import com.example.dtos.request.CreateUserRequestDto;
import com.example.dtos.request.LoginUserRequestDto;
import com.example.dtos.response.UserResponseDto;
import com.example.exception.AppException;
import com.example.exception.ExceptionMessages;
import com.example.mappers.UserMapper;
import com.example.persistence.entity.UserEntity;
import com.example.persistence.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService extends CRUD<UserRepository,UserMapper,UserEntity, CreateUserRequestDto,UserResponseDto> {

    private final PasswordEncoder passwordEncoder;


    public UserService(UserMapper userMapper,UserRepository userRepository
            ,PasswordEncoder passwordEncoder) {
        super(userRepository, userMapper);
        this.passwordEncoder=passwordEncoder;

    }

    public UserResponseDto createUser(CreateUserRequestDto createUserRequestDto) {
        String email = createUserRequestDto.email();
        String userName = createUserRequestDto.userName();
        if(repository.existsByUserName(userName)) {
            throw new AppException(ExceptionMessages.USERNAME_ALREADY_EXISTS);
        } else if (repository.existsByEmail(email)) {
            throw new AppException(ExceptionMessages.EMAIL_ALREADY_EXISTS);
        }
        String encodedPassword = passwordEncoder.encode(createUserRequestDto.password());
        UserEntity userEntity = mapper.toEntity(createUserRequestDto);
        userEntity.setPassword(encodedPassword);
        return save(userEntity);
    }
    public UserResponseDto login(LoginUserRequestDto loginUserRequestDto)  {
        String email = loginUserRequestDto.email();
        UserEntity userEntity = repository.findByEmail(email).orElseThrow(
                () -> new AppException(ExceptionMessages.ACCESS_DENIED)
        );
        if(!passwordEncoder.matches(loginUserRequestDto.password(), userEntity.getPassword())) {
            throw new AppException(ExceptionMessages.ACCESS_DENIED);
        }
        return mapper.toDto(userEntity);

    }





}
