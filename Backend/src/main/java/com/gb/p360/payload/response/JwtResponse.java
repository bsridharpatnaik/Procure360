package com.gb.p360.payload.response;

import com.gb.p360.data.UserDTO;
import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UserDTO user;

    public JwtResponse(String accessToken, UserDTO username) {
        this.token = accessToken;
        this.user = username;
    }
}
