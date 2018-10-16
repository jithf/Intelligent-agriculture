package jit.hf.agriculture.controller;

import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import jit.hf.agriculture.Repository.OrderInfoRepository;
import jit.hf.agriculture.Service.OrderInfoService;
import jit.hf.agriculture.Service.UserService;
import jit.hf.agriculture.domain.User;
import jit.hf.agriculture.exception.MyException;
import jit.hf.agriculture.domain.OrderInfo;
import jit.hf.agriculture.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Auther: zj
 * @Date: 2018/6/7 10:09
 * @Description:在老师要求之外，自己额外拓展的功能模块
 */
@RestController
public class ExtraController {

    @Value("${alipay.uid}")
    private String SELLER_ID;
    @Value("${alipay.notify_url}")
    private String NOTIFY_URL;
    @Value("${alipay.return_url}")
    private String RETURN_URL;


    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private UserService userService;

    private final Logger logger = LoggerFactory.getLogger(ExtraController.class);

    /**
     * 支付宝支付
     * 该方法无返回值，执行成功后response回写结果即可
     * @param subject 订单名称
     * @param body 订单描述
     * @param money 支付金额
     * @author jitwxs
     * @since 2018/6/4 14:00
     */
    @PostMapping("/auth/alipay/payment")
    public void payment(String subject, String body, float money, HttpServletResponse response) {
        // 金额保留两位
        money = (float) (Math.round(money * 100)) / 100;

        // 生成订单
        OrderInfo orderInfo = orderInfoService.createOrder(subject, body, money, SELLER_ID);

        // 1、设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        // 页面跳转同步通知页面路径
        alipayRequest.setReturnUrl(RETURN_URL);
        // 服务器异步通知页面路径
        alipayRequest.setNotifyUrl(NOTIFY_URL);

        // 2、SDK已经封装掉了公共参数，这里只需要传入业务参数，请求参数查阅开头Wiki
        Map<String,String> map = new HashMap<>(16);
        map.put("out_trade_no", orderInfo.getOrderId());
        map.put("total_amount", String.valueOf(money));
        map.put("subject", subject);
        map.put("body",body);
        // 销售产品码
        map.put("product_code","FAST_INSTANT_TRADE_PAY");

        alipayRequest.setBizContent(JsonUtils.objectToJson(map));

        response.setContentType("text/html;charset=utf-8");
        try{
            // 3、生成支付表单
            AlipayTradePagePayResponse alipayResponse = alipayClient.pageExecute(alipayRequest);
            if(alipayResponse.isSuccess()) {
                String result = alipayResponse.getBody();
                response.getWriter().write(result);
            } else {
                String result = "支付表单生成失败：" + alipayResponse.getMsg() + ":" + alipayResponse.getSubMsg();
                logger.error(result);
                response.getWriter().write(result);
            }
        } catch (Exception e) {
            logger.error("支付表单方法出现异常，错误信息：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 该方式仅仅在买家付款完成以后进行自动跳转，因此只会进行一次
     * 支付宝服务器同步通知页面，获取支付宝GET过来反馈信息
     * 该方法执行完毕后跳转到成功页即可
     * （1）该方式不是支付宝主动去调用商户页面，而是支付宝的程序利用页面自动跳转的函数，使用户的当前页面自动跳转；
     * （2）返回URL只有一分钟的有效期，超过一分钟该链接地址会失效，验证则会失败
     * （3）可在本机而不是只能在服务器上进行调试
     * @author jitwxs
     * @since 2018/6/4 15:06
     */
    @GetMapping("/alipay/return")
    public void alipayReturn(HttpServletRequest request,  HttpServletResponse response) {
        // 获取参数
        Map<String,String> params = getPayParams(request);
        try {
            // 验证订单
            boolean flag = orderInfoService.validOrder(params);
            if(flag) {
                // 验证成功后，修改订单状态为已支付
                String orderId = params.get("out_trade_no");
                /*
                 * 订单状态（与官方统一）
                 * WAIT_BUYER_PAY：交易创建，等待买家付款；
                 * TRADE_CLOSED：未付款交易超时关闭，或支付完成后全额退款；
                 * TRADE_SUCCESS：交易支付成功；
                 * TRADE_FINISHED：交易结束，不可退款
                 */
                // 获取支付宝订单号
                String tradeNo = params.get("trade_no");
                // 更新状态
                orderInfoService.changeStatus(orderId, "TRADE_SUCCESS", tradeNo);

                /**
                 * 与本项目积分挂钩，10元=100积分(最后整合项目时加上去)
                 */
                String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
                User user = userService.getUserByUsername( username );
                Integer values = user.getUserTalkValues()+Integer.valueOf(params.get("total_amount"))*10;
                user.setUserTalkValues( values );
                userService.saveOrUpdate( user );

                response.setContentType("text/html;charset=utf-8");
                response.getWriter().write("<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>支付成功</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "\n" +
                        "<div class=\"container\">\n" +
                        "    <div class=\"row\">\n" +
                        "        <p>订单号："+orderId+"</p>\n" +
                        "        <p>支付宝交易号："+tradeNo+"</p>\n" +
                        "        <a href=\"/\">返回首页</a>\n" +
                        "    </div>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
            } else {
                logger.error("支付同步方法验证失败");
                response.getWriter().write("支付验证失败");
            }
        } catch (Exception e) {
            logger.error("祝福同步方法出现异常，错误信息：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 服务器异步通知，获取支付宝POST过来反馈信息
     * 该方法无返回值，静默处理
     * 订单的状态已该方法为主，其他的状态修改方法为辅 *
     * （1）程序执行完后必须打印输出“success”（不包含引号）。
     * 如果商户反馈给支付宝的字符不是success这7个字符，支付宝服务器会不断重发通知，直到超过24小时22分钟。
     * （2）程序执行完成后，该页面不能执行页面跳转。
     * 如果执行页面跳转，支付宝会收不到success字符，会被支付宝服务器判定为该页面程序运行出现异常，而重发处理结果通知
     * （3）cookies、session等在此页面会失效，即无法获取这些数据
     * （4）该方式的调试与运行必须在服务器上，即互联网上能访问 *
     * @author jitwxs
     * @since 2018/6/4 14:45
     */
    @PostMapping("/alipay/notify")
    public void alipayNotify(HttpServletRequest request,  HttpServletResponse response){
        /*
         默认只有TRADE_SUCCESS会触发通知，如果需要开通其他通知，请联系客服申请
         触发条件名 	    触发条件描述 	触发条件默认值
        TRADE_FINISHED 	交易完成 	false（不触发通知）
        TRADE_SUCCESS 	支付成功 	true（触发通知）
        WAIT_BUYER_PAY 	交易创建 	false（不触发通知）
        TRADE_CLOSED 	交易关闭 	false（不触发通知）
        来源：https://docs.open.alipay.com/270/105902/#s2
         */
        // 获取参数
        Map<String,String> params = getPayParams(request);
        try{
            // 验证订单
            boolean flag = orderInfoService.validOrder(params);
            if(flag) {
                //商户订单号
                String orderId = params.get("out_trade_no");
                //支付宝交易号
                String tradeNo = params.get("trade_no");
                //交易状态
                String tradeStatus = params.get("trade_status");

                switch (tradeStatus) {
                    case "WAIT_BUYER_PAY":
                        orderInfoService.changeStatus(orderId, tradeStatus);
                        break;
                    /*
                     * 关闭订单
                     * （1)订单已创建，但用户未付款，调用关闭交易接口
                     * （2）付款成功后，订单金额已全部退款【如果没有全部退完，仍是TRADE_SUCCESS状态】
                     */
                    case "TRADE_CLOSED":
                        orderInfoService.changeStatus(orderId, tradeStatus);
                        break;
                    /*
                     * 订单完成
                     * （1）退款日期超过可退款期限后
                     */
                    case "TRADE_FINISHED" :
                        orderInfoService.changeStatus(orderId, tradeStatus);
                        break;
                    /*
                     * 订单Success
                     * （1）用户付款成功
                     */
                    case "TRADE_SUCCESS" :
                        orderInfoService.changeStatus(orderId, tradeStatus, tradeNo);
                        break;
                    default:break;
                }
                response.getWriter().write("success");
            }else {//验证失败
                //调试用，写文本函数记录程序运行情况是否正常
                String sWord = AlipaySignature.getSignCheckContentV1(params);
                logger.error("支付异步方法验证失败：" + sWord);
                response.getWriter().write("fail");
            }
        } catch (Exception e){
            logger.error("支付异步方法出现异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取支付参数
     * @author jitwxs
     * @since 2018/6/4 16:39
     */
    private Map<String,String> getPayParams(HttpServletRequest request) {
        Map<String,String> params = new HashMap<>(16);
        Map<String,String[]> requestParams = request.getParameterMap();

        Iterator<String> iter = requestParams.keySet().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        return params;
    }

    /**
     * 抛出自定义异常，（测试版）
     * @return
     */
    @GetMapping("/errorTest")
    public void errorTest() throws MyException { //抛出自定义MyException异常
        throw new MyException("发生错误2");
    }

}
