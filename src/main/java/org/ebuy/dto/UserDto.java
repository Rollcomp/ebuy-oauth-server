package org.ebuy.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

/**
 * Created by Ozgur Ustun on May, 2020
 */
public class UserDto {

    private String id;
    private String email;

}
