package jit.hf.agriculture.controller;


import jit.hf.agriculture.domain.PublicMsg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

/**
 * Author: zj
 * Description:ueditor富文本实现
 */

@RestController
public class UEditorController {

    @Value( "${ueditor.filepath}" )
    private String filepath;

    @Value( "${nginx.url}" )
    private String url;

    //映射配置文件
    @RequestMapping(value="/auth/ueditor")
    public String ueditor(HttpServletRequest request) {
        System.out.println( PublicMsg.UEDITOR_CONFIG );
        return PublicMsg.UEDITOR_CONFIG;
    }

    //上传图片
    @RequestMapping(value="/auth/imgUpload")
    public Map<String,Object> imgUpload(MultipartFile upfile) throws IOException {
        //Ueditor ueditor = new Ueditor();
        Map<String,Object> result = new HashMap<String, Object>();
        if(!upfile.isEmpty()){
            //设置文件的保存路径
            String filePath = filepath + upfile.getOriginalFilename();
            //转存文件
            try {
                upfile.transferTo(new File(filePath));
                System.out.println( filePath );
                result.put( "state","SUCCESS" );
                result.put( "url",url+ upfile.getOriginalFilename());
                result.put( "title",upfile.getOriginalFilename() );
                result.put( "original",upfile.getOriginalFilename() );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


}
