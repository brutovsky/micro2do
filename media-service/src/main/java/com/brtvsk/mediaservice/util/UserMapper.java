package com.brtvsk.mediaservice.util;

import com.brtvsk.mediaservice.security.model.AuthUser;
import com.brtvsk.mediaservice.security.model.AuthUserImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.core.Authentication;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "name", target = "id", qualifiedByName = "principalNameToId")
    AuthUserImpl fromAuthentication(Authentication auth);

    @Named("principalNameToId")
    static UUID principalNameToId(String name) {
        return UUID.fromString(name);
    }
}

