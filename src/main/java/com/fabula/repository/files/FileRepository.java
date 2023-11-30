package com.fabula.repository.files;

import com.fabula.model.domain.Domain;
import com.fabula.model.file.File;
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
public interface FileRepository extends CrudRepository<File, UUID> {

   Optional<File> findByFilename(String filename);

    Set<File> findByDomain(Domain domain);

}
