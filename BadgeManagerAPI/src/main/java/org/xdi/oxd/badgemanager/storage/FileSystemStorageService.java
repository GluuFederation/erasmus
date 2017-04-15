package org.xdi.oxd.badgemanager.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class FileSystemStorageService implements StorageService {

    private Path rootLocation;
    private String subDir = "";
    private String location = "";

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.location = properties.getLocation();
    }

    @Override
    public void setsubdir(String s) {
        this.subDir = s;
        this.rootLocation = Paths.get(location + "/" + subDir);

    }


    @Override
    public String store(MultipartFile file) {
        try {
            if (!Files.exists(this.rootLocation)) {
                Files.createDirectory(this.rootLocation);
            }
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            if (Files.exists(this.rootLocation.resolve(file.getOriginalFilename()))) {

            }
            Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()), REPLACE_EXISTING);
            return (load(file.getOriginalFilename()).toString()).replace("src/main/resources", "");
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }


    @Override
    public String store(MultipartFile file, String filename) {
        try {
            //filename = filename + file.getOriginalFilename().toString().substring(file.getOriginalFilename().lastIndexOf("."));
            filename += ".png";
            if (!Files.exists(this.rootLocation)) {
                Files.createDirectory(this.rootLocation);
            }
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            if (Files.exists(this.rootLocation.resolve(filename))) {

            }
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename), REPLACE_EXISTING);
            return (load(filename).toString()).replace("src/main/resources", "");
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            if (!Files.exists(rootLocation)) {
                Files.createDirectory(rootLocation);
            }
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
