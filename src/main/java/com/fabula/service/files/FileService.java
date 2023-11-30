package com.fabula.service.files;

import com.fabula.model.domain.Domain;
import com.fabula.model.file.File;
import com.fabula.model.file.ImageThumbnail;
import com.fabula.repository.files.FileRepository;
import com.fabula.repository.files.ThumbnailRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * @author caio
 */
@Component
@Service
public class FileService {

    @Autowired
    FileRepository fileRepository;
    @Autowired
    ImageService imageService;
    @Autowired
    ThumbnailRepository thumbnailRepository;

    @Transactional
    public Optional<File> create(String fileName, byte[] data, String mimeType, Domain domain) {
        File file = new File(fileName, data, mimeType, domain);
        Optional<File> optionalFile = Optional.of(fileRepository.save(file));
        if (optionalFile.isPresent()) {
            Optional<ImageThumbnail> optionalThumbnail = this.createThumbnail(optionalFile.get());
            if(optionalThumbnail.isPresent()){
                thumbnailRepository.save(optionalThumbnail.get());
            }
        }
        return optionalFile;
    }

    public Optional<File> get(UUID fileId) {
        return fileRepository.findById(fileId);
    }

    public Set<File> getAll(Domain domain) {
        return this.fileRepository.findByDomain(domain);
    }

    public Optional<ImageThumbnail> createThumbnail(File file) {
        return imageService.makeThumbnail(file);
    }

    public File save(File file) {
        return fileRepository.save(file);
    }

}
