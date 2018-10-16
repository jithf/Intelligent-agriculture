package jit.hf.agriculture.controller;

import jit.hf.agriculture.Service.AuthService;
import jit.hf.agriculture.vo.Reponse;
import jit.hf.agriculture.util.JwtAuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Author: zj
 * Description:重写本项目的登录接口，生成/刷新token
 */
@RestController
public class AuthController {
    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/auth/login",method = RequestMethod.POST )
    public Object createAuthenticationToken(
             String username,String password) throws AuthenticationException {
        System.out.println(username);
        System.out.println(password);
         String token = authService.login( username, password );
        // Return the token
        //return ResponseEntity.ok( new JwtAuthenticationResponse( token ) );
        return new Reponse(true,"登录成功,返回token",new JwtAuthenticationResponse(token));
    }

    @RequestMapping(value = "/auth/refresh", method = RequestMethod.GET)
    //public ResponseEntity<?> refreshAndGetAuthenticationToken(
    public Object refreshAndGetAuthenticationToken(
            HttpServletRequest request) throws AuthenticationException{
        String token = request.getHeader(tokenHeader);
        String refreshedToken = authService.refresh(token);
        if(refreshedToken == null) {
            //return ResponseEntity.badRequest().body(null);
            return new Reponse(false,"刷新失败");
        } else {
            //return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
            return new Reponse(true,"刷新成功,获取新token",new JwtAuthenticationResponse(refreshedToken));
        }
    }
//    @RequestMapping(value = "/login/github",method = RequestMethod.GET)
//    public Object githubAndGetAuthenticationToken()throws AuthenticationException{
//        final String token = authService.login( username, password );
//    }
}

