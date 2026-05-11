package com.coforge.hsbcdma.mapper;

import com.coforge.hsbcdma.dto.UserMananagementDTO.GetUserResponse;
import com.coforge.hsbcdma.entity.User;

public class UserMapper {
    public static GetUserResponse toGetUserResponse(User user){
        return new GetUserResponse(
                user.getUserId(),
                user.getName(),
                user.getEmailId(),
                user.getRole().getRole(),
                user.getPhoneNumber()
        );
    }
}
