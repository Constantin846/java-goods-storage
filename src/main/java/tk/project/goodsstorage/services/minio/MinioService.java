package tk.project.goodsstorage.services.minio;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.zip.ZipOutputStream;

public interface MinioService {
    void uploadFile(final MultipartFile file, final String fileName);

    void downloadFiles(final List<? extends MinioFile> files, final ZipOutputStream zipOutputStream);
}
