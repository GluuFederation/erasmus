package org.xdi.oxd.badgemanager.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    public static final String ORGANIZATIONS = "organizations";
    public static final String BADGES = "badges";
    public static final String ISSUERS = "issuers";

    void init();

    String store(MultipartFile file);

    String store(MultipartFile file, String filename);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void setsubdir(String subdir);

    void deleteAll();

}
