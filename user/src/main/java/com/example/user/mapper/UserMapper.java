package com.example.user.mapper;

import com.example.user.dto.request.SignUpRequest;
import com.example.user.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User signUpRequestToUser(SignUpRequest signUpRequest);
}
