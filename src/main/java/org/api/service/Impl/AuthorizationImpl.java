package org.api.service.Impl;

import org.api.domain.repository.UsersRepository;
import org.api.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationImpl implements UserDetailsService {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    UsersService usersService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = usersRepository.findByIdentityUser(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return user;
    }


    public UserDetails loadUserById(Integer id_user) throws UsernameNotFoundException {
        return usersService.findById(id_user);
    }
}
