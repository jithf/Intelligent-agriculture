package jit.hf.agriculture.vo;

/**
 * Author: jit.hf
 * Description:对返回对象进行封装
 * Date: Created in 上午12:34 18-3-26
 **/
public class Reponse {
    private boolean success;  //返回处理是否成功
    private String message;   //反回提示信息
    private Object body;   //返回数据

    public Reponse(boolean success,String message) {
        this.success=success;
        this.message=message;
    }

    public Reponse(boolean success,String message,Object body) {
        this.success=success;
        this.message=message;
        this.body=body;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

}
