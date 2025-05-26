package tk.project.goodsstorage.services.minio;

import java.util.UUID;

public interface MinioFile {
    UUID getName();

    String getOriginalName();
}
