package org.api.service;

import org.api.domain.model.ResponseDTO.PasswordDTO;
import org.api.domain.model.ResponseDTO.RecoveryPasswordDTO;
import org.api.domain.model.Users;
import org.springframework.stereotype.Service;

@Service
public interface PasswordService {
    Users toUpdatePassword(Users user, PasswordDTO passwordDTO, Integer id);

    Users recoverPassword(Users user, RecoveryPasswordDTO recoveryPasswordDTO, String email);
}
