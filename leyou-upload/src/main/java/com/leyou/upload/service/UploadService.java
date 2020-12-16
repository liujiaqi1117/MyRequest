package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.upload.controller.UploadController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {


    @Autowired
    private FastFileStorageClient storageClient;

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    // 支持的文件类型
    private static final List<String> suffixes = Arrays.asList("image/png", "image/jpeg","image/jpg","image/gif");

    public String upload(MultipartFile file) {
        // 1、图片信息校验
        // 1)校验文件类型
        String originalFilename = file.getOriginalFilename();
        String type = file.getContentType();
        if (!suffixes.contains(type)) {
            logger.info("文件类型不合法：{}", originalFilename);
            return null;
        }
        try {

            // 2)校验图片内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                logger.info("文件类型不合法：{}", originalFilename);
                return null;
            }
            String substringAfterLast = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), substringAfterLast, null);
             return  "http://image.leyou.com/"+storePath.getFullPath();

            /*if (image == null) {
                logger.info("上传失败，文件内容不符合要求");
                return null;
            }
            // 2、保存图片
            // 2.1、生成保存目录
            File dir = new File("E:\\img");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 2.2、保存图片
            file.transferTo(new File(dir, file.getOriginalFilename()));

            // 2.3、拼接图片地址
            String url = "http://image.leyou.com/upload/" + file.getOriginalFilename();

            System.out.println("url--"+url);
            return url;*/
        } catch (Exception e) {
            logger.info("服务器内部错误，请稍后重试 {}"+originalFilename);
           e.printStackTrace();
        }
        return null;
    }
}
