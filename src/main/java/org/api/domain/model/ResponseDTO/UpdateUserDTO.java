package org.api.domain.model.ResponseDTO;


import lombok.Data;
import org.api.domain.model.ProfileRole;
import org.api.domain.model.Status;

@Data
public class UpdateUserDTO {
    private Integer id_user;
    private String identity;
    private String first_name;
    private String last_name;
    private String email;
    private Status status;

    private ProfileRole role;

}