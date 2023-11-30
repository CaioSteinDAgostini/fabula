package com.fabula.repository.files;

import com.fabula.model.domain.Domain;
import com.fabula.model.file.File;
import com.fabula.model.file.ImageThumbnail;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author caio
 */
@Transactional
public interface ThumbnailRepository extends CrudRepository<ImageThumbnail, UUID> {

}
