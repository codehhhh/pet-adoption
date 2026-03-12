package com.kmbeast.controller;

import com.kmbeast.utils.PathUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件控制器
 * 提供文件上传和下载功能
 * 企业级优化：删除冗余逻辑、添加日志/接口文档、异常处理优化
 */
@Api(tags = "文件上传下载接口")
@RestController
@RequestMapping("/file")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private static final String UPLOAD_DIR = "/file";
    private static final String FILE_PARAM = "file";
    private static final String FILE_NAME_PARAM = "fileName";

    @Value("${my-server.api-context-path}")
    private String apiContextPath;

    /**
     * 文件上传
     */
    @ApiOperation("文件上传（支持图片/文档等）")
    @PostMapping("/upload")
    public Map<String, Object> uploadFile(
            @ApiParam(value = "上传文件", required = true)
            @RequestParam(FILE_PARAM) MultipartFile multipartFile) {
        log.info("文件上传请求：文件名={}，大小={}KB",
                multipartFile.getOriginalFilename(),
                multipartFile.getSize() / 1024);
        return handleFileUpload(multipartFile);
    }

    /**
     * 查看文件资源
     */
    @ApiOperation("根据文件名获取文件资源")
    @GetMapping("/getFile")
    public void getFile(
            @ApiParam(value = "文件名", required = true)
            @RequestParam(FILE_NAME_PARAM) String fileName,
            HttpServletResponse response) throws IOException {
        if (fileName == null || fileName.trim().isEmpty()) {
            log.warn("获取文件参数非法：fileName为空");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        File file = getUploadedFile(fileName);
        if (!file.exists()) {
            log.warn("文件不存在：{}", fileName);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        // 设置响应头，优化文件下载体验
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

        try (FileInputStream fileInputStream = new FileInputStream(file);
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[4096]; // 增大缓冲区，提升性能
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            log.info("文件{}获取成功", fileName);
        } catch (IOException e) {
            log.error("获取文件失败：{}", fileName, e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * 处理文件上传逻辑
     */
    private Map<String, Object> handleFileUpload(MultipartFile multipartFile) {
        Map<String, Object> response = new HashMap<>();

        if (multipartFile.isEmpty()) {
            log.warn("上传文件为空");
            response.put("code", HttpStatus.BAD_REQUEST.value());
            response.put("msg", "文件不能为空");
            return response;
        }

        // 优化文件名生成：时间戳 + 原文件名（避免特殊字符）
        String originalFileName = multipartFile.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" +
                originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "");

        try {
            if (saveUploadedFile(multipartFile, fileName)) {
                String fileUrl = apiContextPath + "/file/getFile?" + FILE_NAME_PARAM + "=" + fileName;
                log.info("文件上传成功：{}，访问地址：{}", fileName, fileUrl);

                response.put("code", HttpStatus.OK.value());
                response.put("msg", "上传成功");
                response.put("data", fileUrl);
                return response;
            }
        } catch (IOException e) {
            log.error("文件上传失败：{}", originalFileName, e);
            response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("msg", "文件上传失败：" + e.getMessage());
            return response;
        }

        response.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("msg", "文件上传失败");
        return response;
    }

    /**
     * 保存上传的文件
     */
    private boolean saveUploadedFile(MultipartFile multipartFile, String fileName) throws IOException {
        File uploadDir = new File(PathUtils.getClassLoadRootPath() + UPLOAD_DIR);

        // 确保目录存在（优化创建逻辑）
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            log.error("创建上传目录失败：{}", uploadDir.getAbsolutePath());
            return false;
        }

        File destFile = new File(uploadDir, fileName);

        // 安全检查：防止路径穿越
        if (!destFile.getAbsolutePath().startsWith(uploadDir.getAbsolutePath())) {
            log.warn("文件路径非法：{}", destFile.getAbsolutePath());
            return false;
        }

        // 上传文件（覆盖已存在文件）
        multipartFile.transferTo(destFile);
        return true;
    }

    /**
     * 获取已上传的文件
     */
    private File getUploadedFile(String fileName) {
        File uploadDir = new File(PathUtils.getClassLoadRootPath() + UPLOAD_DIR);
        return new File(uploadDir, fileName);
    }
}