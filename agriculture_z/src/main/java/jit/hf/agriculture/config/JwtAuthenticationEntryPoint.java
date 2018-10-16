package jit.hf.agriculture.config;

import org.json.JSONObject;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
/**
        * Author: zj
        * 登录和token异常处理
        */
@Component
@Service
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        JSONObject result=new JSONObject();
        JSONObject header=new JSONObject();
        /**身份认证未通过*/
        if(authException instanceof BadCredentialsException){
            //提示一下
            result.put("success",false);
            result.put("message","登陆失败，用户名或密码错误，请重新输入！");
            header.put("errorCode","8002");
            header.put("errorInfo","用户名或密码错误，请重新输入！");
            result.put("body",header);
        }else{
            result.put("success",false);
            result.put("message","无效的token，请重新登录！！！");
            header.put("errorCode","8001");
            header.put("errorInfo","无效的token，请重新登录！！！");
            result.put("body",header);

        }
        response.getWriter().write( JSONObject.valueToString(result) );
    }
}