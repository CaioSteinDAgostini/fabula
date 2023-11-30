package com.fabula.model.file;

import com.fabula.model.authorization.IResource;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import java.util.UUID;

/**
 *
 * @author caio
 */
@Entity
public class ImageThumbnail implements IResource{

    @Id
    UUID id;
    @OneToOne
    @JoinColumn(name = "id")
    @MapsId
    File file;
    @Lob
    private byte[] data;

    public ImageThumbnail() {
    }

    public ImageThumbnail(File file, byte[] data) {
//        this.id = file.getId();
        this.file = file;
        this.data = data;
    }
    
    public UUID getId(){
        return this.id;
    }
    
    public String getImage(){
        return this.file.getFilename();
    }
    
    public String getMediaType(){
        return this.file.getMediaType();
    }
    
    public byte[] getData(){
        return this.data;
    }

}
