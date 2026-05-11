package com.coforge.hsbcdma.dto.UserMananagementDTO;


public class RoleDTO {
    public record RoleCreateRequest(String name){}

    public record RoleCreateResponse(Long id,String name
    ){}

    public record RoleResponse(Long id, String name) {}
}