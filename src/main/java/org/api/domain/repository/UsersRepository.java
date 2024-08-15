package org.api.domain.repository;

import org.api.domain.model.ResponseDTO.UsersDTO;
import org.api.domain.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    @Query("SELECT u FROM Users u WHERE u.email = :email")
    Users findByEmail(@Param("email") String email);

    @Query("SELECT u FROM Users u WHERE u.identity = :identity")
    Users findByIdentityUser(@Param("identity") String identity);

    @Query("SELECT NEW org.api.domain.model.ResponseDTO.UsersDTO(u.id_user, u.identity, u.first_name, u.last_name, u.email, u.status, u.password, u.role, u.subStatus, u.passwordIsCompliance, u.createdDate, u.lastPasswordUpdateDate, u.passwordExpirationDays) FROM Users u WHERE u.id_user = :id_user")
    UsersDTO findUserById(@Param("id_user") Integer id_user);

}
