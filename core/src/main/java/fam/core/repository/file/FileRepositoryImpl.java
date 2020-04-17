package fam.core.repository.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileRepositoryImpl<E> implements FileRepository<E> {
    private static final Logger LOG = LoggerFactory.getLogger(FileRepositoryImpl.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File dataDirectory;

    private File entityFile;
    private Set<E> entities;

    public FileRepositoryImpl(File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @Override
    public List<E> findAll() {
        getEntities();
        return new ArrayList<>(entities);
    }

    @Override
    public E save(E entity) {
        getEntities().remove(entity);
        getEntities().add(entity);
        writeEntitiesToFile();
        return entity;
    }

    private E deserializeEntity(String entityString) {
        try {
            return objectMapper.readValue(entityString, getEntityClass());
        } catch (Exception e) {
            LOG.error(String.format("Exception occurred while attempting to deserialize entity string (%s) - skipping",entityString), e);
            return null;
        }
    }

    private File getDataDirectory() {
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }

        return dataDirectory;
    }

    private Set<E> getEntities() {
        if (entities == null) {
            readEntitiesFromFile();
        }

        return entities;
    }

    private String getRepositoryName() throws ClassNotFoundException {
        return String.format("%s.dat",getEntityName());
    }


    private File getEntityFile() throws ClassNotFoundException, IOException {
        if (entityFile == null) {
            entityFile = new File(getDataDirectory(), getRepositoryName());
            entityFile.createNewFile();
        }

        return entityFile;
    }

    private void readEntitiesFromFile() {
        try (Stream<String> lines = Files.lines(getEntityFile().toPath())) {
            entities = lines
                .map(this::deserializeEntity)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            LOG.error("Exception occured while reading entities from file", e);
            entities = new HashSet<>();
        }
    }

    private String serializeEntity(E entity) {
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (Exception e) {
            LOG.error(String.format("Exception occurred while attempting to serialize entity string (%s) - skipping",entity), e);
            return null;
        }
    }

    private void writeEntitiesToFile() {
        try (Stream<E> entityStream = entities.stream()) {
            String entitiesFileContent = entityStream.map(this::serializeEntity)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(System.lineSeparator()));
            Files.write(getEntityFile().toPath(), entitiesFileContent.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch(Exception e) {
            LOG.error("Exception occured while writing entities to file", e);
        }
    }
}
