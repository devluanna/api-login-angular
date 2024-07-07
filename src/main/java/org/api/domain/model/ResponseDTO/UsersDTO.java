package org.api.domain.model.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.api.domain.model.ProfileRole;
import org.api.domain.model.Status;
import org.api.domain.model.SubStatus;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsersDTO {
    private Integer id_user;
    private String identity;
    private String first_name;
    private String last_name;
    private String email;
    private Status status;
    private String password;
    private ProfileRole role;
    private SubStatus subStatus;


}