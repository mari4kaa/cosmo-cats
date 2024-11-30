package com.example.cosmocats.mapper;

import java.util.UUID;

import org.mapstruct.Named;

public class UuidConverter {
    @Named("longToUuid")
    public static UUID longToUuid(Long id) {
        return id == null ? null : UUID.fromString(id.toString());
    }
    
    @Named("uuidToLong")
    public static Long uuidToLong(UUID uuid) {
        return uuid == null ? null : uuid.getMostSignificantBits();
    }
}