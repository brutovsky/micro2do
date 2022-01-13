package com.brtvsk.todoservice.utils;

import com.brtvsk.todoservice.security.model.AuthUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.core.Authentication;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "name", target = "id", qualifiedByName = "principalNameToId")
    AuthUser fromAuthentication(Authentication auth);

    @Named("principalNameToId")
    static UUID principalNameToId(String name) {
        return UUID.fromString(name);
    }
}
