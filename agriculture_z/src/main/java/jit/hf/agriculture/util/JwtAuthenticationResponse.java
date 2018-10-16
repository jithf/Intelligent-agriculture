package jit.hf.agriculture.util;

import java.io.Serializable;

/**
 * Author: zj
 * 与jwt配合
 */
public class JwtAuthenticationResponse implements Serializable {
    private static final long serialVersionUID = 1250166508152483573L;

    private final String token;

    public JwtAuthenticationResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
