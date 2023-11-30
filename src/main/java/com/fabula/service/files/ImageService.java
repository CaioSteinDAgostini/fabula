/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fabula.service.files;

import com.fabula.model.file.File;
import com.fabula.model.file.ImageThumbnail;
import com.fabula.repository.files.ThumbnailRepository;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author caio
 */
@Service
public class ImageService {

    @Autowired
    ThumbnailRepository thumbnailRepository;

    float THUMBNAIL_MAX_WIDTH = 300;
    float THUMBNAIL_MAX_HEIGHT = 300;

    public byte[] resize(byte[] data, String format, int targetWidth, int targetHeight) throws IOException {

        InputStream input = new ByteArrayInputStream(data);
        BufferedImage originalImage = ImageIO.read(input);

        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage resizedBuffer = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        resizedBuffer.getGraphics().drawImage(resultingImage, 0, 0, null);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(resizedBuffer, format, output);
        return output.toByteArray();
    }

    public Optional<ImageThumbnail> makeThumbnail(File file) {
        //this method does not save into the ThumbnailRepository because it would generate a Detached entity problem. Thus it is the FileService which calls the repository, inside a Transactional method
        try {
            if (file.isImage()) {
                InputStream input = new ByteArrayInputStream(file.getData());
                BufferedImage originalImage = ImageIO.read(input);
                int width = originalImage.getWidth();
                int height = originalImage.getHeight();

                float ratio;
                if (width > height) {
                    ratio = width / THUMBNAIL_MAX_WIDTH;
                } else {
                    ratio = height / THUMBNAIL_MAX_HEIGHT;
                }
                byte[] resizedData = this.resize(file.getData(), file.getFormat(), (int) (width / ratio), (int) (height / ratio));

                ImageThumbnail thumbnail = new ImageThumbnail(file, resizedData);
                return Optional.of(thumbnail);
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            return Optional.empty();
        }

    }
    
    public Optional<ImageThumbnail> getThumbnail(UUID id){
        return thumbnailRepository.findById(id);
    }
    
    public Optional<ImageThumbnail> getThumbnail(File file){
        return thumbnailRepository.findById(file.getId());
    }

    public Iterable<ImageThumbnail> findAllTumbnails(){
        return thumbnailRepository.findAll();
    }
}
