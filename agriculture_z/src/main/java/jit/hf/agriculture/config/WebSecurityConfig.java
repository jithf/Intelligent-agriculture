package jit.hf.agriculture.config;

import jit.hf.agriculture.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;

/**
 * 安全配置类，security核心
 * Author: zj
 */
@Configuration
@EnableWebSecurity
@EnableOAuth2Client//oauth2,添加该注解
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)//oauth2,添加该注解
@EnableGlobalMethodSecurity(prePostEnabled = true)//打开全局开关
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Resource
    private OAuth2ClientContext oauth2ClientContext;

    // Spring会自动寻找同样类型的具体类注入，这里就是JwtUserDetailsServiceImpl了
    @Autowired
    private UserDetailsService userDetailsService;
    @Value( "${jwt.exceptUrl}" )
    private String exceptUrl;

    /**
     *  用户名密码认证方法
     * @param authenticationManagerBuilder
     * @throws Exception
     */
    @Autowired
    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                // 设置UserDetailsService
                .userDetailsService(this.userDetailsService)

                .passwordEncoder( new PasswordEncoder() {
                    @Override
                    public String encode(CharSequence rawpassword) {
                        return MD5Util.encode(rawpassword.toString());
                    }

                    @Override
                    public boolean matches(CharSequence rawpassword, String s) { //rawpassword明文  ，s 密文
                        System.out.println( rawpassword );
                        return s.equals(MD5Util.encode(rawpassword.toString()));//将密文与加了密的明文比较，从而决定登录是否成功
                    }
                } );
    }

//我采用了md5自定义加密器，故下面一种省略
//    /**
//     * 装载BCrypt密码编码器
//     * @return
//     */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    //将filter注入security配置文件中,实现jwt与security集成
    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
//重写了登录方法，故舍弃不用
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService( userDetailsService )//注入userDetailsService，需要实现userDetailsService接口
//                .passwordEncoder( new PasswordEncoder() {
//                    @Override
//                    public String encode(CharSequence rawpassword) {
//                        return MD5Util.encode(rawpassword.toString());
//                    }
//
//                    @Override
//                    public boolean matches(CharSequence rawpassword, String s) { //rawpassword明文  ，s 密文
//                        return s.equals(MD5Util.encode(rawpassword.toString()));//将密文与加了密的明文比较，从而决定登录是否成功
//                    }
//                } );
//    }

    /**
     * 详细的路由配置参数
     *
     * permitAll表示该请求任何人都可以访问，
     * .anyRequest().authenticated(),表示其他的请求都必须要有权限认证。
     * @param httpSecurity
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // 由于使用的是JWT，这里不需要csrf防护
                .csrf().disable()

                //.cors()//跨域支持，-----------===我在springmvc配置文件中已经配置过了，故这里可以省略
                //.and()
                //.antMatcher("/**") // 捕捉所有路由

                .exceptionHandling().authenticationEntryPoint( unauthorizedHandler )
                .and()
                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy( SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                //对于获取token的rest api要允许匿名访问
                .antMatchers( exceptUrl ).permitAll()
                .antMatchers( "/classifyNew","/classifyHot","/classifyNewAll","/classifyHotAll","/","/auth/**","/register","/register_android",
                        "/github/**","/login/**","/weibo/user",
                        "/configure", "/configureChangePassword",
                        "/playVideo","/download","/change","/getChoices","/test","/grade",
                        "/getComment",
                        "/css/**","/img/**","/api.html","/sortHot","/sortLatest","/searchNew4Video",
                        "/searchHot4Video","/searchNewVideo","/searchHotVideo","/searchNewVideoAll",
                        "/searchHotVideoAll","/auth/login/github","/login/github",
                        "/static/**","/config/**",
                        "/index2.html","/alipay/**",
                        "/swagger-ui.html","/webjars/**","/v2/**","/swagger-resources/**"
                ).permitAll()



                .anyRequest().authenticated(); //表示其他的请求都必须要有权限认证
//                .and()
//                .formLogin().loginPage("/pleaseLogin")
//                .loginProcessingUrl("/login").permitAll()
//                .failureUrl("/login/error").permitAll()
//                .successForwardUrl( "/login_s" )
//                .and()
//                .logout().permitAll()
//                .and()
//                .rememberMe();//没有测试，不知是否起效（需要在页面上测试）
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class); //将token验证添加在密码验证前面
        //httpSecurity
                //.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);//添加github拦截器
        // 禁用缓存
        httpSecurity.headers().cacheControl();
    }

    //忽略，如静态资源或者指定url
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/index.html", "/static/**","/templates/**","/personalcenter.html");
    }

    /**
     * 下面的是关于github拦截器的
     * @param filter
     * @return
     */
    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(
            OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        System.out.println(registration );///
        return registration;
    }

    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(ssoFilter(github(), "/login/github"));//要与回调页一致
        //filters.add(ssoFilter(weibo(),"/login/weibo" ));
        filter.setFilters(filters );
        //System.out.println(filter);
        return  filter;
    }

    private Filter ssoFilter(ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(
                client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        filter.setTokenServices(tokenServices);
        //OAuth2AccessToken accessToken = oauth2ClientContext.getAccessToken();
        //System.out.println(accessToken);
        return filter;
    }

    /**
     * github 授权连接
     *
     * @return 第三方授权连接对象
     */
    @Bean
    @ConfigurationProperties("github")
    public ClientResources github() {
        return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("weibo")
    public ClientResources weibo() {
        return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("qq")
    public ClientResources qq() {
        return new ClientResources();
    }

    @Bean
    @ConfigurationProperties("camera")
    public ClientResources camera() {
        return new ClientResources();
    }


}
