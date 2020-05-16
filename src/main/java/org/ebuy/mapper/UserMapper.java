package org.ebuy.mapper;

import org.ebuy.dto.UserDto;
import org.ebuy.model.request.UserRegisterRequest;
import org.ebuy.entity.User;
import org.mapstruct.Mapper;

/**
 * Created by Ozgur Ustun on May, 2020
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);

    User userRegReqToUser(UserRegisterRequest userRegisterRequest);


}
