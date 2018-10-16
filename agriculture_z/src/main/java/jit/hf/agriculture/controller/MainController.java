package jit.hf.agriculture.controller;

import jit.hf.agriculture.Repository.WeatherRepository;
import jit.hf.agriculture.Service.AuthService;
import jit.hf.agriculture.Service.RoleService;
import jit.hf.agriculture.Service.UserService;
import jit.hf.agriculture.config.ClientResources;
import jit.hf.agriculture.domain.SysRole;
import jit.hf.agriculture.domain.Talk;
import jit.hf.agriculture.domain.User;
import jit.hf.agriculture.domain.Weather;
import jit.hf.agriculture.util.JwtAuthenticationResponse;
import jit.hf.agriculture.util.JwtTokenUtil;
import jit.hf.agriculture.vo.Reponse;
import org.apache.commons.lang3.RandomUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static jit.hf.agriculture.util.MD5Util.encode;

/**
 * Author: zj hf
 * Description:主页控制器，登录注册模块，第三方登录模块，数据源模块
 * Date: Created in 下午1:39 18-3-26
 **/
@EnableOAuth2Client
@RestController
public class MainController {

    //自动注入 获取bean
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleservice;

    @Autowired
    private AuthService authService;

    @Resource
    private OAuth2ClientContext oauth2ClientContext;

    @Qualifier("github")
    @Resource
    private ClientResources client1;

    @Qualifier("weibo")
    @Resource
    private ClientResources client2;

    @Qualifier("qq")
    @Resource
    private ClientResources client3;

    @Value( "${qq.client.openIdUri}" )
    private String getOpenIdUrl;

    @Qualifier("camera")
    @Resource
    private ClientResources client4;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private WeatherRepository weatherRepository;

    /**
     * 从github中拉取用户信息
     * @param response
     * @throws IOException
     */
    @GetMapping("/github/user")
    public void githubAndGetToken(HttpServletResponse response) throws IOException {
        //return "redirect:/index";
        OAuth2AccessToken accessToken = oauth2ClientContext.getAccessToken();
        String tokenServices =client1.getResource().getUserInfoUri();
        //
        response.sendRedirect(tokenServices+"?access_token="+accessToken);
    }
//    @GetMapping("/weibo/user")
//    public void weiboAndGetToken(HttpServletResponse response) throws IOException {
//        //return "redirect:/index";
//        OAuth2AccessToken accessToken = oauth2ClientContext.getAccessToken();
//        String tokenServices =client2.getResource().getUserInfoUri();
//        //String tokenServices =accessToken
//        response.sendRedirect(tokenServices+"?access_token="+accessToken);
//    }

    @GetMapping("/")
    public void index(HttpServletResponse response) throws IOException {

        response.sendRedirect("index.html");//重定向到主页

    }

//    //匿名获取主页
//    @GetMapping("/index")
//    public ModelAndView index() {
//        return new ModelAndView("index"); //返回index.html
//    }

    //发送验证码 &&会判断用户是否已存在&&仅注册时调用
    @PostMapping("/configure")
    public Object configure(@RequestParam("username") String username) {
        User user1 =userService.getUserByUsername( username );
        if (user1!=null) {
            return new Reponse(false, "该用户以注册了！！！");
        }
        User user = new User(username, null);
        userService.sendMsg(user);
        return new Reponse(true, "发送验证码成功");
    }

    //发送验证码&&&仅修改密码时调用
    @PostMapping("/configureChangePassword")
    public Object configureChangePassword(@RequestParam("username") String username) {
        User user1 = userService.getUserByUsername( username );
        if (user1 == null) {
            return new Reponse(false, "该用户不存在，请先注册！！！");
        }
       //User user = new User(username, null);
        User user =userService.getUserByUsername( username );
        userService.sendMsg(user);
        return new Reponse(true, "发送验证码成功");
    }

    //注册用户(1)，仅用于安卓端!
    @PostMapping("/register_android")
    public Object register_android(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("verityCode") String verityCode) {
        User user = new User(username, password);
        user.setVerityCode(verityCode);
        User user1 = userService.getUserByUsername(user.getUsername());
//        if (user1 != null && user1.getVerityCode().equals(encode(user.getVerityCode())) ) {
        if (user1 != null && user1.getVerityCode().equals(encode(user.getVerityCode()))) {
            user.setId(user1.getId());
            user.setPassword(encode(user.getPassword()));  //密码加密
            user.setVerityCode(encode(user.getVerityCode()));  //验证码加密
            user.setLastPasswordResetDate( new Date(  ) );
            userService.saveOrUpdate(user);
            return new Reponse(true, "注册成功");
        } else {
            return new Reponse(false, "注册失败，密码错误或验证码错误");
        }
    }

    //注册用户(2),Web端
    @PostMapping("/register")
    @ResponseBody
    public Object registerUser(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("verityCode") String verityCode,
                               @RequestParam("permission") String permission) {
        User user = new User(username, password);
        user.setVerityCode(verityCode);
        User user1 = userService.getUserByUsername(user.getUsername());
//        if (user1 != null && user1.getVerityCode().equals(encode(user.getVerityCode())) ) {
        if (user1 != null && user1.getVerityCode().equals(encode(user.getVerityCode()))) {
            user.setId(user1.getId());
            SysRole sysRole =roleservice.getOneByName(permission);//通过权限名查找表中权限
            List<SysRole> roles = new ArrayList<>(); //因为我在关联表时候定义的是L..类型
            roles.add(sysRole);//所以只能通过add来添加权限
            user.setRoles(roles); //user信息添加完毕，即通过user表来更新关系表

            user.setPassword(encode(user.getPassword()));  //密码加密
            user.setVerityCode(encode(user.getVerityCode()));  //验证码加密
            user.setLastPasswordResetDate( new Date() );
            userService.saveOrUpdate(user);
            return new Reponse(true, "注册成功");
        } else {
            return new Reponse(false, "注册失败，手机号已注册或验证码错误");
        }
    }

    //修改密码
    @PostMapping("/change")
    public Object changePassword(@RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 @RequestParam("verityCode") String verityCode) {
        User user = new User(username, password);
        user.setVerityCode(verityCode);
        User user1 = userService.getUserByUsername(user.getUsername());
        if (user1 != null && user1.getVerityCode().equals(encode(user.getVerityCode()))) {
            user.setId(user1.getId());
            user.setPassword(encode(user.getPassword()));  //密码加密
            user.setVerityCode(encode(user.getVerityCode()));  //验证码加密
            userService.saveOrUpdate(user);
            return new Reponse(true, "修改密码成功");
        } else {
            return new Reponse(false, "修改失败，验证码错误");
        }
    }

    @RequestMapping("/user_s")
    @ResponseBody//如何果上面的注解是@ontroller不是@RestController，那@ResponseBody的作用就是让return能返回字符串，而不是页面。这里可以不加，即忽略
    @PreAuthorize("hasRole('ROLE_USER')")
    public String printUser() {
        return "如果你看见这句话，说明你有ROLE_USER角色";
    }

    //关于登录异常处理
//    @RequestMapping("/login/error")
//    public Object loginError() {
////        public Object loginError(HttpServletRequest request, HttpServletResponse response) {
////        response.setContentType("text/html;charset=utf-8");
////        AuthenticationException exception =
////                (AuthenticationException)request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
////        try {
////            response.getWriter().write(exception.toString());
////        }catch (IOException e) {
////            e.printStackTrace();
////        }
////        response.reset();
//        return new Reponse(false, "登录失败",null);
//    }

    /**
     * http请求,(与第三方登录配合使用)
     * Author: zj
     * @return
     */
    public RestTemplate getRestTemplate() {// 手动添加
        SimpleClientHttpRequestFactory requestFactory=new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(120000);
        List<HttpMessageConverter<?>> messageConverters = new LinkedList<>();
        messageConverters.add(new ByteArrayHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter( StandardCharsets.UTF_8));
        messageConverters.add(new ResourceHttpMessageConverter());
        messageConverters.add(new SourceHttpMessageConverter<Source>());
        messageConverters.add(new AllEncompassingFormHttpMessageConverter());
        messageConverters.add(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate=new RestTemplate(messageConverters);
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }

    /**
     * github第三方登录
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/auth/login/github", method = {RequestMethod.GET})
    public void githubLoginS(HttpServletResponse response) {
        try {
            String url = String.format( client1.getClient().getUserAuthorizationUri() +
                    "?client_id=" + client1.getClient().getClientId() +
                    "&redirect_uri=" + client1.getClient().getPreEstablishedRedirectUri() );//组装url
            response.sendRedirect(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取github用户信息，并自动创建本项目账号与之关联（前端不需要调用此接口）
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/login/github", method = {RequestMethod.GET})
    public Object githubCallback(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        String code = request.getParameter("code");
        System.out.println(code);
        String url =  String.format(client1.getClient().getAccessTokenUri()+
                "?client_id="+client1.getClient().getClientId()+
                "&client_secret="+client1.getClient().getClientSecret()+
                "&grant_type=authorization_code"+
                "&redirect_uri="+client1.getClient().getPreEstablishedRedirectUri()+
                "&code="+code);//组装url

        Map<String, Object> params = new HashMap<>();
        params.put("client_id", client1.getClient().getClientId());
        params.put("client_secret", client1.getClient().getClientSecret());
        params.put("code", code);

        RestTemplate restTemplate = new RestTemplate();
        String result=restTemplate.postForObject(url,params,String.class);   //POST请求 token（授权成功，会自动发送这个请求）

        String[] temp = result.split("&");
        String access_token = temp[0].split("=")[1];
        System.out.println(access_token);

        String url1 = "https://api.github.com/user"+"?access_token="+access_token;  //访问 Github API请求资源
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url1 );
        URI uri = builder.build().encode().toUri();
        String result1=restTemplate.getForObject(uri,String.class);
        JSONObject data = new JSONObject(result1 );

        String username="github#"+String.valueOf(data.getLong("id"));
        String avater=data.getString("avatar_url");
        String nickname=data.getString("login");

        if(userService.getUserByUsername(username) == null) {
            User user=new User();
            user.setUsername(username);
            user.setAvater(avater);
            user.setNickname(nickname);
            user.setLastPasswordResetDate(new Date());
            user.setPassword(encode(username+encode("github")));
            SysRole sysRole =roleservice.getOneByName("ROLE_USER");//通过权限名查找表中权限
            List<SysRole> roles = new ArrayList<>(); //因为我在关联表时候定义的是L..类型
            roles.add(sysRole);//所以只能通过add来添加权限
            user.setRoles(roles); //user信息添加完毕，即通过user表来更新关系表
            userService.saveOrUpdate(user);
            Thread.sleep(1000);        //1000 毫秒，也就是1秒.要不然第一次验证时,token无效
                                              // （原因：数据库插入记录需要时间，导致token生成的时间快于该记录创建的时间）
        }

        User user=userService.getUserByUsername(username);
        String token = authService.login(user.getUsername(),username+encode("github"));

        return new Reponse(true,"github第三方登录成功,返回token",new JwtAuthenticationResponse(token));
    }

    /**
     * weibo第三方登录
     * @param response
     * @throws IOException
     */
    @GetMapping("/auth/login/weibo")
    public void loginAndWeibo(HttpServletResponse response) throws IOException {
        String urlAuthorize = String.format( client2.getClient().getUserAuthorizationUri() +
                "?client_id=" + client2.getClient().getClientId() +
                "&redirect_uri=" + client2.getClient().getPreEstablishedRedirectUri() );//组装url
        System.out.println( urlAuthorize );
        response.sendRedirect( urlAuthorize );
    }

    /**
     * 获取weibo的用户信息，并自动创建本项目账号与之关联（前端不需要调用此接口），前端不需要调用此接口
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @GetMapping("/login/weibo")//回调地址
    public Object loginAndWeiBoAndAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        String code = request.getParameter( "code" );
        System.out.println( code );

        String GetTokenUrl = String.format(client2.getClient().getAccessTokenUri()+
                "?client_id="+client2.getClient().getClientId()+
                "&client_secret="+client2.getClient().getClientSecret()+
                "&grant_type=authorization_code"+
                "&redirect_uri="+client2.getClient().getPreEstablishedRedirectUri()+
                "&code="+code);
        System.out.println( GetTokenUrl );
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl( GetTokenUrl  );
        URI uri = builder.build().encode().toUri();
        String resp = getRestTemplate().postForObject( uri,null,String.class );//post请求，url获取token
        System.out.println(resp);//resp 是json格式
        JSONObject data = new JSONObject( resp );
        String access_token = data.getString( "access_token" );//获得token
        String uid = data.getString( "uid" );
        System.out.println( access_token);
        System.out.println( uid);
        Long uid2 = Long.parseLong(uid);
        System.out.println( uid2 );

        String getUseInfoUrl = String.format( client2.getResource().getUserInfoUri()+
                "?access_token="+access_token+
                "&uid="+uid2);
        System.out.println( getUseInfoUrl );
        UriComponentsBuilder builder2 = UriComponentsBuilder.fromHttpUrl( getUseInfoUrl );//get请求，获取用户信息
        URI uri2 = builder2.build().encode().toUri();
        String resp2 = getRestTemplate().getForObject( uri2,String.class );
        JSONObject data2 = new JSONObject( resp2 );
        System.out.println( data2 );

        String username="weibo#"+String.valueOf(data2.getLong("id"));
        String avater=data2.getString("avatar_hd");
        String nickname=data2.getString("name");

        if(userService.getUserByUsername(username) == null) {
            User user=new User();
            user.setUsername(username);
            user.setAvater(avater);
            user.setNickname(nickname);
            user.setLastPasswordResetDate(new Date());
            user.setPassword(encode(username+encode("weibo")));
            SysRole sysRole =roleservice.getOneByName("ROLE_USER");//通过权限名查找表中权限
            List<SysRole> roles = new ArrayList<>(); //因为我在关联表时候定义的是L..类型
            roles.add(sysRole);//所以只能通过add来添加权限
            user.setRoles(roles); //user信息添加完毕，即通过user表来更新关系表
            userService.saveOrUpdate(user);
            Thread.sleep(1000);        //1000 毫秒，也就是1秒.要不然第一次验证时,token无效
            // （原因：数据库插入记录需要时间，导致token生成的时间快于该记录创建的时间）
        }

        User user=userService.getUserByUsername(username);
        String token = authService.login(user.getUsername(),username+encode("weibo"));

        return new Reponse(true,"weibo第三方登录成功,返回token",new JwtAuthenticationResponse(token));
    }

    /**
     * qq第三方登录
     * @param response
     * @throws IOException
     */
    @GetMapping("/auth/login/qq")
    public void loginAndQq(HttpServletResponse response) throws IOException {
        String urlAuthorize = String.format( client3.getClient().getUserAuthorizationUri()+
                "?response_type=code"+
                "&client_id="+client3.getClient().getClientId()+
                "&redirect_uri="+client3.getClient().getPreEstablishedRedirectUri()+
                "&state=ZjQAQ");
        System.out.println( urlAuthorize);
        response.sendRedirect( urlAuthorize );
    }

    /**
     * 获取用户qq个人信息，并自动创建本项目账号与之关联（前端不需要调用此接口）
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @GetMapping("/login/qq")//回调地址
    public Object loginAndQqAndAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        String code = request.getParameter( "code" );
        System.out.println( code );

        String GetTokenUrl = String.format(client3.getClient().getAccessTokenUri()+
                "?client_id="+client3.getClient().getClientId()+
                "&client_secret="+client3.getClient().getClientSecret()+
                "&grant_type=authorization_code"+
                "&redirect_uri="+client3.getClient().getPreEstablishedRedirectUri()+
                "&code="+code);
        System.out.println( GetTokenUrl );
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl( GetTokenUrl  );
        URI uri = builder.build().encode().toUri();
        String resp = getRestTemplate().getForObject( uri,String.class );//get请求，url获取token
        System.out.println(resp);//resp 是String型
        String access_token = resp.split( "&")[0];//获得token
        System.out.println(access_token);

        String GetOpenIdUrl = String.format( getOpenIdUrl+
                "?"+access_token);
        System.out.println( GetOpenIdUrl );
        UriComponentsBuilder builder2 = UriComponentsBuilder.fromHttpUrl( GetOpenIdUrl);
        URI uri2 = builder2.build().encode().toUri();
        String resp2 = getRestTemplate().getForObject( uri2,String.class );//get请求，url获取openid
        System.out.println(resp2);
        String str1 =resp2.split("\\(")[1];
        System.out.println(str1);
        String str2 =str1.split("\\)")[0];
        System.out.println(str2);
        JSONObject data = new JSONObject( str2 );
        String openid = data.getString( "openid" );//获得openid
        System.out.println( openid );

        String getUseInfoUrl = String.format( client3.getResource().getUserInfoUri()+
                "?"+access_token+
                "&oauth_consumer_key="+client3.getClient().getClientId()+
                "&openid="+openid);
        System.out.println( getUseInfoUrl );
        UriComponentsBuilder builder3 = UriComponentsBuilder.fromHttpUrl( getUseInfoUrl);
        URI uri3 = builder3.build().encode().toUri();
        String resp3 = getRestTemplate().getForObject( uri3,String.class );//get请求，url获取openid
        JSONObject data3 = new JSONObject( resp3 );

        String username="QQ#"+openid;
        String avater=data3.getString("figureurl_qq_2");
        String nickname=data3.getString("nickname");

        if(userService.getUserByUsername(username) == null) {
            User user=new User();
            user.setUsername(username);
            user.setAvater(avater);
            user.setNickname(nickname);
            user.setLastPasswordResetDate(new Date());
            user.setPassword(encode(username+encode("QQ")));
            SysRole sysRole =roleservice.getOneByName("ROLE_USER");//通过权限名查找表中权限
            List<SysRole> roles = new ArrayList<>(); //因为我在关联表时候定义的是L..类型
            roles.add(sysRole);//所以只能通过add来添加权限
            user.setRoles(roles); //user信息添加完毕，即通过user表来更新关系表
            userService.saveOrUpdate(user);
            Thread.sleep(1000);        //1000 毫秒，也就是1秒.要不然第一次验证时,token无效
            // （原因：数据库插入记录需要时间，导致token生成的时间快于该记录创建的时间）
        }

        User user=userService.getUserByUsername(username);
        String token = authService.login(user.getUsername(),username+encode("QQ"));

        return new Reponse(true,"QQ第三方登录成功,返回token",new JwtAuthenticationResponse(token));
    }

    /**
     * 获取荧石token
     * @return
     */
    @GetMapping("/auth/camera/token")
    public Object cameraAndGetToken() {
        String GetTokenUrl = String.format(client4.getClient().getAccessTokenUri()+
                "?appKey="+client4.getClient().getClientId()+
                "&appSecret="+client4.getClient().getClientSecret());
        System.out.println( GetTokenUrl );
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl( GetTokenUrl);
        URI uri = builder.build().encode().toUri();
        String resp = getRestTemplate().postForObject( uri,null,String.class );//post请求，url获取token
        System.out.println( resp );
        JSONObject data =new JSONObject( resp );
        System.out.println( data );
        JSONObject token=data.getJSONObject( "data" );
        String accessToken=token.getString("accessToken");
        System.out.println( accessToken );
        return new Reponse( true,"获取荧石平台token成功！",accessToken );
    }

//    /**
//     * 定时器,采集数据
//     */
//
//
//    @Scheduled(cron = "0/60 * * * * *")  //每60秒获取一次，测试用
//    public void timer() throws UnsupportedEncodingException {//获取当前时间
//        String cityId = "CHJS000000";
//
//        LocalDateTime localDateTime =LocalDateTime.now();
//        System.out.println("当前时间为:" + localDateTime.format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//
//        String weatherUrl = String.format( "http://tj.nineton.cn/Heart/index/all" +
//                        "?city="+cityId+
//        "&language=zh-chs&unit=c&aqi=city&alarm=1&key=78928e706123c1a8f1766f062bc8676b");
//
//        System.out.println( weatherUrl);
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl( weatherUrl);
//        URI uri = builder.build().encode().toUri();
//        String resp = getRestTemplate().getForObject( uri,String.class );//get请求，url获取天气信息
//        String str = unicodeToUtf8( resp );
//        System.out.println(str);
//        JSONObject data =new JSONObject( str );
//        System.out.println( data );
//
//
//
//    }

    /**
     * unicode格式 To Utf8
     * @param theString
     * @return
     */
    public static String unicodeToUtf8(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);

        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    /**
     * 定时器,采集数据&&并插入数据库
     */

    @Scheduled(cron = "0 0 */2 * * ?")  //每2小时采集一次数据
    public Object getWeather() throws InterruptedException {
        String GetTokenUrl = String.format("http://210.28.188.98:8088/Aqua/user/checklogin"+
                "?userName=zhangsir"+ "&password=12121212"+"&power=0");
        System.out.println( GetTokenUrl );

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, httpHeaders);
        ResponseEntity<String> resp = getRestTemplate().exchange(GetTokenUrl, HttpMethod.POST, requestEntity, String.class);
        HttpHeaders header = resp.getHeaders();
        System.out.println( header );
        String set_cookie = resp.getHeaders().getFirst(resp.getHeaders().SET_COOKIE);//获取cookie
        System.out.println( set_cookie );


        LocalDateTime localDateTime =LocalDateTime.now();

        String startTime = LocalDateTime.now().minusMinutes(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));//时间退10min
        System.out.println( "##" +startTime );
        String nowTime = localDateTime.format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("当前时间为:" +nowTime);

        String getWeatherUrl = String.format( "http://210.28.188.98:8088/Aqua/dataDisplay/queryHistoryData"+
                "?gwid="+"2"+
                "&pondID="+"172"+
                "&startTime="+startTime+
                "&endTime="+ nowTime);
        System.out.println( getWeatherUrl );

        HttpHeaders httpHeaders1 = new HttpHeaders();
        httpHeaders1.add( "Cookie",set_cookie );//添加cookie
        HttpEntity<String> requestEntity1 = new HttpEntity<String>(null, httpHeaders1);
        ResponseEntity<String> resp1 = getRestTemplate().exchange(getWeatherUrl, HttpMethod.POST, requestEntity1, String.class);
        String result = resp1.getBody();
        System.out.println( result );

        //对数据源进行提取
        JSONObject data = new JSONObject(result);// String转化成JSONObject
        JSONObject res =data.getJSONObject( "res" );
        System.out.println( "res:"+res );
//        JSONArray sensorKinds = res.getJSONArray( "sensorKinds" );//JSONObject转化成JSONArray
//        System.out.println( "sensorKinds:"+sensorKinds );

        JSONArray ph9 = res.getJSONArray( "ph9" );
        System.out.println( "ph9:"+ph9 );
        Double ph9_new = ph9.getDouble(0);//JSONArray转化成Double
        System.out.println( "ph9 first:"+ph9_new );

        JSONArray ph8 = res.getJSONArray( "ph8" );
        System.out.println( "ph8:"+ph8 );
        Double ph8_new = ph8.getDouble(0);//JSONArray转化成Double
        System.out.println( "ph8 first:"+ph8_new );

        JSONArray do1 = res.getJSONArray( "do1" );
        System.out.println( "do1:"+do1 );
        Double do1_new = do1.getDouble(0);//JSONArray转化成Double
        System.out.println( "do1 first:"+do1_new );

        JSONArray do4 = res.getJSONArray( "do4" );
        System.out.println( "do4:"+do4 );
        Double do4_new = do4.getDouble(0);//JSONArray转化成Double
        System.out.println( "do4 first:"+do4_new );

        JSONArray pha = res.getJSONArray( "pha" );
        System.out.println( "pha:"+pha );
        Double pha_new = pha.getDouble(0);//JSONArray转化成Double
        System.out.println( "pha first:"+pha_new );

        JSONArray do3 = res.getJSONArray( "do3" );
        System.out.println( "do3:"+do3 );
        Double do3_new = do3.getDouble(0);//JSONArray转化成Double
        System.out.println( "do3 first:"+do3_new );

        JSONArray do6 = res.getJSONArray( "do6" );
        System.out.println( "do6:"+do6 );
        Double do6_new = do6.getDouble(0);//JSONArray转化成Double
        System.out.println( "do6 first:"+do6_new );

        JSONArray do5 = res.getJSONArray( "do5" );
        System.out.println( "do5:"+do5 );
        Double do5_new = do5.getDouble(0);//JSONArray转化成Double
        System.out.println( "do5 first:"+do5_new );

        JSONArray temp2 = res.getJSONArray( "temp2" );
        System.out.println( "temp2:"+temp2 );
        Double temp2_new = temp2.getDouble(0);//JSONArray转化成Double
        System.out.println( "temp2 first:"+temp2_new );

        JSONArray temp3 = res.getJSONArray( "temp3" );
        System.out.println( "temp3:"+temp3 );
        Double temp3_new = temp3.getDouble(0);//JSONArray转化成Double
        System.out.println( "temp3 first:"+temp3_new );

        JSONArray temp6 = res.getJSONArray( "temp6" );
        System.out.println( "temp6:"+temp6 );
        Double temp6_new = temp6.getDouble(0);//JSONArray转化成Double
        System.out.println( "temp6 first:"+temp6_new );

        JSONArray temp4 = res.getJSONArray( "temp4" );
        System.out.println( "temp4:"+temp4 );
        Double temp4_new = temp4.getDouble(0);//JSONArray转化成Double
        System.out.println( "temp4 first:"+temp4_new );

        JSONArray temp5 = res.getJSONArray( "temp5" );
        System.out.println( "temp5:"+temp5 );
        Double temp5_new = temp5.getDouble(0);//JSONArray转化成Double
        System.out.println( "temp5 first:"+temp5_new );

        JSONArray ph2 = res.getJSONArray( "ph2" );
        System.out.println( "ph2:"+ph2 );
        Double ph2_new = ph2.getDouble(0);//JSONArray转化成Double
        System.out.println( "ph2 first:"+ph2_new );

        JSONArray ph7 = res.getJSONArray( "ph7" );
        System.out.println( "ph7:"+ph7 );
        Double ph7_new = ph7.getDouble(0);//JSONArray转化成Double
        System.out.println( "ph7 first:"+ph7_new );

        JSONArray time = res.getJSONArray( "time" );
        System.out.println( "time:"+time );
        String time_new = time.getString(0);//JSONArray转化成String
        System.out.println( "time first:"+time_new );


        //插入数据库
        Weather weather = new Weather();
        weather.setPh5(ph9_new);
        weather.setPh4( ph8_new );
        weather.setDo1( do1_new );
        weather.setDo3( do4_new );
        weather.setPh1( pha_new );
        weather.setDo2( do3_new );
        weather.setDo5( do6_new );
        weather.setDo4( do5_new );
        weather.setTemp1( temp2_new );
        weather.setTemp2( temp3_new );
        weather.setTemp5( temp6_new );
        weather.setTemp3( temp4_new );
        weather.setTemp4( temp5_new );
        weather.setPh2(ph2_new);
        weather.setPh3(ph7_new);
        weather.setUpTime( time_new);
        weatherRepository.save( weather );
        return null;

    }

    /**
     * 前端获取数据源的具体信息，以便绘图
     * @return
     */
    @GetMapping("/auth/getFarmInformation")
    public Object getFarmInformation() {
        List<Weather> weatherList = weatherRepository.findAll();

        return new Reponse(true,"获取数据成功",weatherList);

    }

    /**
     * 分页，返回数据
     * @return
     */
    @GetMapping("/auth/getFarmInformationPage")
    public Object getFarmInformationPage(@RequestParam(value = "pageIndex",required = false,defaultValue = "0") int pageIndex) {
        Pageable pageable=new PageRequest(pageIndex,12);
        List<Weather> weatherList = weatherRepository.findAll(pageable).getContent();//getContent()获取page实体中的内容

        if(weatherList.size()==0) {
            return new Reponse(false,"没有更多数据了");
        } else {
            return new Reponse( true, "分页，获取数据列表成功！", weatherList );
        }
    }

    /**
     * 分页，按日期获取当天数据
     * @param date
     * @return
     */
    @GetMapping("/auth/getFarmInformationByDate")
    public Object getFarmInformationByDate(@RequestParam("date") String date) {
        Pageable pageable = new PageRequest( 0,12 );
        List<Weather> weatherList = weatherRepository.findDistinctByUpTimeContaining( date,pageable ).getContent();
        if(weatherList.size()==0) {
            return new Reponse(false,"没有更多数据了");
        } else {
            return new Reponse( true, "分页，按日期获取当天数据列表成功！", weatherList );
        }
    }

    /**
     * 仅用于测试
     * @return
     */
    @GetMapping("/auth/toutf8")
    public Object toutf8() {
        String str = "# \\u5546\\u6237UID\n" +
                "alipay.uid=\n" +
                "# \\u5E94\\u7528ID,\\u60A8\\u7684APPID\\uFF0C\\u6536\\u6B3E\\u8D26\\u53F7\\u65E2\\u662F\\u60A8\\u7684APPID\\u5BF9\\u5E94\\u652F\\u4ED8\\u5B9D\\u8D26\\u53F7\n" +
                "alipay.app_id=\n" +
                "# \\u5546\\u6237\\u79C1\\u94A5\\uFF0C\\u60A8\\u7684PKCS8\\u683C\\u5F0FRSA2\\u79C1\\u94A5\n" +
                "alipay.merchant_private_key=\n" +
                "# \\u652F\\u4ED8\\u5B9D\\u516C\\u94A5,\\u67E5\\u770B\\u5730\\u5740\\uFF1Ahttps://openhome.alipay.com/platform/keyManage.htm \\u5BF9\\u5E94APPID\\u4E0B\\u7684\\u652F\\u4ED8\\u5B9D\\u516C\\u94A5\\u3002\n" +
                "alipay.alipay_public_key=\n" +
                "# \\u670D\\u52A1\\u5668\\u5F02\\u6B65\\u901A\\u77E5\\u9875\\u9762\\u8DEF\\u5F84  \\u9700http://\\u683C\\u5F0F\\u7684\\u5B8C\\u6574\\u8DEF\\u5F84\\uFF0C\\u4E0D\\u80FD\\u52A0?id=123\\u8FD9\\u7C7B\\u81EA\\u5B9A\\u4E49\\u53C2\\u6570\\uFF0C\\u5FC5\\u987B\\u5916\\u7F51\\u53EF\\u4EE5\\u6B63\\u5E38\\u8BBF\\u95EE\\uFF01\\uFF01\n" +
                "alipay.notify_url=http://ppsqzi.natappfree.cc/alipay/notify\n" +
                "# \\u9875\\u9762\\u8DF3\\u8F6C\\u540C\\u6B65\\u901A\\u77E5\\u9875\\u9762\\u8DEF\\u5F84 \\u9700http://\\u683C\\u5F0F\\u7684\\u5B8C\\u6574\\u8DEF\\u5F84\\uFF0C\\u4E0D\\u80FD\\u52A0?id=123\\u8FD9\\u7C7B\\u81EA\\u5B9A\\u4E49\\u53C2\\u6570\n" +
                "alipay.return_url=http://ppsqzi.natappfree.cc/alipay/return\n" +
                "# \\u7B7E\\u540D\\u65B9\\u5F0F\n" +
                "alipay.sign_type=RSA2\n" +
                "# \\u652F\\u4ED8\\u5B9D\\u7F51\\u5173\n" +
                "# \\u6C99\\u7BB1\\uFF1Ahttps://openapi.alipaydev.com/gateway.do\n" +
                "# \\u6B63\\u5F0F\\uFF1Ahttps://openapi.alipay.com/gateway.do\n" +
                "alipay.gateway_url=https://openapi.alipaydev.com/gateway.do";

        return new Reponse( true,"转码成功",unicodeToUtf8(str) );
    }



}
