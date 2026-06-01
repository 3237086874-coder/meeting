package com.enterprise.meeting.service;

import com.enterprise.meeting.dto.FileDto;
import com.enterprise.meeting.entity.FileEntity;
import com.enterprise.meeting.entity.User;
import com.enterprise.meeting.exception.BusinessException;
import com.enterprise.meeting.repository.FileRepository;
import com.enterprise.meeting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    public FileDto uploadFile(MultipartFile file, Long meetingId, Long taskId, String fileType, Long uploadedBy) {
        User uploader = userRepository.findById(uploadedBy)
                .orElseThrow(() -> new BusinessException(400, "上传者不存在"));

        try {
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String storedName = UUID.randomUUID().toString().replace("-", "") + ext;
            Path targetDir = Paths.get(uploadDir, datePath);
            Files.createDirectories(targetDir);
            Path targetPath = targetDir.resolve(storedName);
            file.transferTo(targetPath.toFile());

            String ossKey = datePath + "/" + storedName;

            FileEntity fileEntity = FileEntity.builder()
                    .fileName(originalName != null ? originalName : "unknown")
                    .fileSize(file.getSize())
                    .mimeType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .ossKey(ossKey)
                    .fileType(fileType != null ? fileType : "ATTACHMENT")
                    .uploadedBy(uploader)
                    .meetingId(meetingId)
                    .taskId(taskId)
                    .build();

            fileEntity = fileRepository.save(fileEntity);
            return toDto(fileEntity);
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new BusinessException(500, "文件上传失败");
        }
    }

    public List<FileDto> getFilesByMeeting(Long meetingId) {
        return fileRepository.findByMeetingIdOrderByCreatedAtDesc(meetingId)
                .stream().map(this::toDto).toList();
    }

    public List<FileDto> getFilesByTask(Long taskId) {
        return fileRepository.findByTaskIdOrderByCreatedAtDesc(taskId)
                .stream().map(this::toDto).toList();
    }

    private FileDto toDto(FileEntity entity) {
        return new FileDto(
                entity.getId(),
                entity.getFileName(),
                entity.getFileSize(),
                entity.getMimeType(),
                entity.getOssKey(),
                entity.getFileType(),
                entity.getUploadedBy().getId(),
                entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null
        );
    }
}
