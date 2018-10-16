package jit.hf.agriculture.controller;


import jit.hf.agriculture.Repository.DocRepository;
import jit.hf.agriculture.Service.DocService;
import jit.hf.agriculture.Service.UserDataService;
import jit.hf.agriculture.Service.UserService;
import jit.hf.agriculture.domain.Doc;
import jit.hf.agriculture.domain.User;
import jit.hf.agriculture.vo.Reponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Author: zj
 * Description:文件上传下载模块
 */

@RestController
public class DocController {
    @Autowired
    private UserService userService;
    @Autowired
    private DocService docService;
    @Autowired
    private DocRepository docRepository;
    @Autowired
    UserDataService dataService;

    @Value( "${upload.filepath}" )
    private String filepath;

    //String filepath = "./src/main/webapp/";//   ！！！&&还要修改fileName[]的参数！！！！！！！！！！

    @PostMapping("/docUpload")
    public Object docUpload(@RequestParam("title") String title,
                            @RequestParam("description") String description,
                            @RequestParam("file") MultipartFile file ) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
        User user = userService.getUserByUsername( author );
        //System.out.println( user );
        String fileName = file.getOriginalFilename().toString();//获取文件名
        //System.out.println( fileName );
        if(fileName.indexOf('?')!=fileName.length()-1)
            fileName=title+fileName.substring(fileName.lastIndexOf("."));

        final SimpleDateFormat sDateFormate = new SimpleDateFormat("yyyymmddHHmmss");  //设置时间格式
        String nowTimeStr = sDateFormate.format(new Date()); // 当前时间
        fileName=fileName.substring(0,fileName.indexOf("."))+nowTimeStr+fileName.substring(fileName.lastIndexOf("."));

        Doc doc = new Doc();
        if (!file.isEmpty()) {
            try {
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(new File(filepath + fileName)));//保存图片到目录下,建立保存文件的输入流
                out.write(file.getBytes());
                out.flush();
                out.close();
                String filename = filepath+fileName;
                Long fileSize = file.getSize();
                System.out.println( file.getSize());

                doc.setTitle( title );
                doc.setAvatar( filename );
                doc.setAuthor( author );
                doc.setAuthor_picture(user.getAvater());
                doc.setUptime( new Date() );
                doc.setDescription( description );
                doc.setFileSize( fileSize );
                docService.saveOrUpdateDoc( doc );

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return new Reponse(false,"上传文件失败," + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                return new Reponse(false,"上传文件失败," + e.getMessage());
            }
            return new Reponse(true,"上传文件成功",doc);//返回文件信息

        }
        else {
            return new Reponse(false,"上传失败，因为文件是空的");
        }
    }

    /**
     * Author: zj
     *
     * 文件下载（仅用于windows,且只能用浏览器运行）
     *
     * 如果不设置，则默认下载到C:\\users\\downloads，即本机的默认下载的目录
     * @param id
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/download",method = RequestMethod.GET)
    public Object downloadFile(@RequestParam("id") Long id, org.apache.catalina.servlet4preview.http.HttpServletRequest request, HttpServletResponse response){
        Doc doc = docService.getDocById( id );
        String fileName = doc.getAvatar();//获取文件路径
        System.out.println( fileName );
        String filename = fileName.split( "/")[4];//获取文件名，该数字要具体情况修改
        System.out.println( filename );

        //String downPath = "C:\\Users\\zj\\Desktop\\"+filename;
        if (fileName != null) {
            File file = new File(fileName);

            System.out.println( file );
            if (file.exists()) {
                response.setContentType("application/force-download");// 设置强制下载不打开
                response.addHeader("Content-Disposition",
                        "attachment;fileName=" +  filename);// 设置文件名
//                response.addHeader("Content-Disposition",
//                        "attachment;filePath=" +  downPath);// 设置文件路径



                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);//下载的源文件流
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    //System.out.println("success");
                    Integer downloads = doc.getDownloads();
                    doc.setDownloads( downloads+1 );
                    docService.saveOrUpdateDoc( doc );
                    dataService.editDocData();
                    return new Reponse( true,"文件下载成功");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return new Reponse( false,"未知错误，请联系管理员" );
    }

    /**
     * 安卓端的下载通用接口（本项目，安卓可以忽略）
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/auth/download_android_disable",method = RequestMethod.GET)
    public Object  downLoadFromUrl(@RequestParam("id") Long id) throws IOException{
            Doc doc = docService.getDocById( id );
            String fileName1 = doc.getAvatar();//获取文件路径
            System.out.println( fileName1 );
            String filename = fileName1.split( "/")[4];//获取文件名，该数字要具体情况修改
            String savePath = "C:\\Users\\zj\\Desktop\\"+filename;//保存地址,请自己修改


                String urlStr = "http://127.0.0.1:8088/"+filename;
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                        //设置超时间为3秒
                conn.setConnectTimeout(3*1000);
                //防止屏蔽程序抓取而返回403错误
                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

                //得到输入流
                InputStream inputStream = conn.getInputStream();
                //获取自己数组
                byte[] getData = readInputStream(inputStream);

                File file = new File(savePath);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(getData);
                if(fos!=null){
                       fos.close();
                    }
                if(inputStream!=null){
                        inputStream.close();
                    }

                System.out.println("info:"+url+" download success");

                Integer downloads = doc.getDownloads();
                doc.setDownloads( downloads+1 );
                docService.saveOrUpdateDoc( doc );
                return null;
            }

    /**
     * 获取android文件下载url,仅适应于服务器测试
     * @param id
     * @return
     */
    @RequestMapping("/auth/download/android_url")
    public Object getDownloadAndroidUrl(@RequestParam("id") Long id) {
        Doc doc = docService.getDocById( id );
        String fileName1 = doc.getAvatar();//获取文件路径
        System.out.println( fileName1 );
        String filename = fileName1.split( "/")[4];//获取文件名，该数字要具体情况修改
        String url ="http://112.74.53.186/"+filename;
        System.out.println( url );

        Integer downloads = doc.getDownloads();
        doc.setDownloads( downloads+1 );
        docService.saveOrUpdateDoc( doc );
        return new Reponse( true,"获取android文件下载url成功！！！" ,url);
    }


    /**
     * 获取文件列表
     * @return
     */
    @RequestMapping(value = "/getFileList",method = RequestMethod.GET)
    public Object getFileList() {
        List<Doc> list = docRepository.findAll();
        return new Reponse( true,"获取文件列表成功",list );
    }

    /**
     * 在线预览（请在服务器上调用此接口）
     * @param docId
     * @param response
     * @throws IOException
     */
    @GetMapping(value = "/auth/previewOnline")
    public void previewOnline(@RequestParam(value="docId") Long docId,HttpServletResponse response)throws IOException {
        String key = "367400494";//永中云域名key

        Doc doc = docRepository.findOneById( docId );
        String filename = doc.getAvatar().split( "/" )[4];
        String url = String.format( "http://dcsapi.com/"+
                "?k="+key+
                "&url=http://112.74.53.186/"+filename);
        System.out.println( url );
        response.sendRedirect( url );
    }

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

}
