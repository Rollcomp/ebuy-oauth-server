package org.ebuy.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Created by Ozgur Ustun on May, 2020
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangePasswordRequest {
    @NotNull
    @NotEmpty
    private String userEmail;

    @NotNull
    @NotEmpty
    private String oldPassword;

    @NotNull
    @NotEmpty
    private String newPassword;

}