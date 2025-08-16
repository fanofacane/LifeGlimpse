package com.sky.skybackend.utils;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

@Component
@Slf4j
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;
    /**
     * 上传文件到minio，并返回访问该文件的url
     * @param bucket 桶名称
     * @param multipartFile MultipartFile对象
     * @return
     */
    public String uploadFile(String bucket, MultipartFile multipartFile){
        String localFileAbsolutePath = null;
        String minioPath = null;
        String httpPath = "http://47.96.156.182:9000/";
        File localFile = null;
        String mimeType = null;
        boolean isExist = false;
        try {
            // 获取文件扩展名
            String extension = getExtension(multipartFile);
            // 获取mimeType
            mimeType = getMimeType(extension);
            // 获取本地文件的路径（源路径）
            localFile = File.createTempFile("minio", ".temp");
            multipartFile.transferTo(localFile);
            localFileAbsolutePath = localFile.getAbsolutePath();
            // 构造minio中的路径（目标路径）=年+月+日+md5值+扩展名
            String folder = "sky/";
            String fileName;
            try (FileInputStream fis = new FileInputStream(localFile)) {
                fileName = DigestUtils.md5Hex(fis); // 计算MD5
            }
            minioPath = folder + fileName + extension;
            // 检查MinIO中是否已存在该文件
            // 通过statObject方法获取文件信息，如果不存在会抛出异常
            minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(minioPath).build());
            log.info("文件已存在，返回已存在的路径");
            return httpPath + bucket + "/" + minioPath; // 直接返回已存在的路径
        } catch (Exception e) {
            try {
                UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                        .bucket(bucket)
                        .filename(localFileAbsolutePath)// 本地文件的路径（源路径）
                        .object(minioPath)// minio中的路径（目标路径）
                        .contentType(mimeType)
                        .build();
                minioClient.uploadObject(uploadObjectArgs);
                log.info("上传成功");
            } catch (Exception ex) {
                log.info("上传失败"+ex);
        }
        }finally {
            // 确保临时文件被删除
            if (localFile != null && localFile.exists()) {
                boolean deleted = localFile.delete();
                if (!deleted) {
                    log.warn("临时文件删除失败: {}", localFile.getAbsolutePath());
                }
            }
        }
        // 返回访问该文件的url
        return httpPath + bucket + "/" + minioPath;
    }

    /**
     * 根据MultipartFile对象获取其文件扩展名
     * @param multipartFile MultipartFile对象
     * @return
     */
    private static String getExtension(MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        return extension;
    }

    /**
     * 根据扩展名获取文件类型（mimetype）
     * @param extension 文件的扩展名，如：“.png”
     * @return
     */
    public static String getMimeType(String extension) {
        if (extension == null){
            extension = "";
        }
        // 根据文件扩展名获取文件类型
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        if (extensionMatch != null){
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

}
