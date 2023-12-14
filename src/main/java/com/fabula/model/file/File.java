package com.fabula.model.file;

import com.fabula.model.authorization.IResource;
import com.fabula.model.domain.Domain;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.http.MediaType;

/**
 *
 * @author caio
 */
@Entity
public class File implements IResource {

    @Lob
    private byte[] data;
    private String mediaType;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true)
    private String filename;
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime creationDateTime;

//    @OneToOne(mappedBy="file")
//    ImageThumbnail thumbnail;
    @ManyToOne
    @JoinColumn(name = "domain")
    Domain domain;

    public File() {
    }

    public File(String filename, byte[] data, String mediaType, Domain domain) {
        this.mediaType = mediaType;
        this.filename = filename;
        this.domain = domain;
        this.data = data;
        this.creationDateTime = LocalDateTime.now();
    }

    public File(UUID id){
        this.id = id;
    }
    
    public byte[] getData() {
        return this.data;
    }

    public UUID getId() {
        return this.id;
    }

    public String getFilename() {
        return this.filename;
    }

    public boolean isImage() {
        return this.getMediaType().startsWith("image");
    }

    public String getFormat() {
        String[] parts = this.getMediaType().split("/");
        if (parts.length == 2) {
            return parts[1];
        }
        return null;
    }

    public String getMediaType() {
        return mediaType;
    }
}
