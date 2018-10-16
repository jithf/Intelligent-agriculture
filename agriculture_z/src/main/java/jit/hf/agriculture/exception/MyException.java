package jit.hf.agriculture.exception;

/**
 * @Auther: zj
 * @Date: 2018/6/14 10:22
 * @Description:自定义异常类，用来实验捕获该异常，并返回json
 */
public class MyException extends Exception {
    public MyException(String message) {
        super(message);
    }
}
