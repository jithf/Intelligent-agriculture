package jit.hf.agriculture.config;


import jit.hf.agriculture.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
        * Author: zj
        * jwt核心，filter类，配置它
        */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {


    @Autowired
     private UserDetailsService userDetailsService;

    //private JwtUserDetailsServiceImpl customUserService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    //private JwtUtil jwtUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws  IOException, ServletException {
        String authHeader = httpServletRequest.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            final String authToken = authHeader.substring(tokenHead.length()); // The part after "Bearer "
            String username = jwtTokenUtil.getUsernameFromToken(authToken);//从token中获取用户名
            //System.out.println( "========" );
            //System.out.println(username );
            logger.info("JwtAuthenticationTokenFilter[doFilterInternal] checking authentication " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {//token校验通过

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);//根据account去数据库中查询user数据，足够信任token的情况下，可以省略这一步

                if (jwtTokenUtil.validateToken(authToken, userDetails)) { //验证token
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                            httpServletRequest));
                    logger.info("JwtAuthenticationTokenFilter[doFilterInternal]  authenticated user " + username + ", setting security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);//注入Authentication模块

                }
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);//进行下一步

    }
}