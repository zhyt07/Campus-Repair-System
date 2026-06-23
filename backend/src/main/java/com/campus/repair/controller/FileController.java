package com.campus.repair.controller;

import com.campus.repair.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 上传图片
     * POST /api/files/upload
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }

        // 生成文件名
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        // 创建目录
        File dir = new File(uploadPath + dateDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 保存文件
        File dest = new File(dir, fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            return Result.error("文件上传失败");
        }

        // 返回访问URL
        String url = "/uploads/" + dateDir + "/" + fileName;
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        data.put("name", originalName);
        return Result.success("上传成功", data);
    }
}
