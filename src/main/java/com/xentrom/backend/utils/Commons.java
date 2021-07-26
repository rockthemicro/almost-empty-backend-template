package com.xentrom.backend.utils;

import com.xentrom.backend.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class Commons {
    public static boolean checkUserDetailsRole(UserDetails userDetails, User.RoleEnum role) {

        for (GrantedAuthority ga : userDetails.getAuthorities()) {
            if (ga.getAuthority().equals(role.toString())) {
                return true;
            }
        }
        return false;
    }

    public static UserDetails getCurUserDetails() {
        return (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public static User.RoleEnum getCurUserRole() {
        for (GrantedAuthority ga : getCurUserDetails().getAuthorities()) {
            String authority = ga.getAuthority();

            for (User.RoleEnum role : User.RoleEnum.values()) {
                if (role.toString().equals(authority)) {
                    return role;
                }
            }
        }

        return User.RoleEnum.USER;
    }
}
