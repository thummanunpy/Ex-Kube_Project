package io.learnk8s.knotejava.repositories;

import io.learnk8s.knotejava.dto.Note;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface NotesRepository extends MongoRepository<Note, String> {
}
