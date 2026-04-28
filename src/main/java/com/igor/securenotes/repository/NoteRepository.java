package com.igor.securenotes.repository;

import com.igor.securenotes.model.NoteEntity;
import com.igor.securenotes.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<NoteEntity, Long> {

    List<NoteEntity> findByOwnerOrderByCreatedAtDesc(UserEntity owner);

    Optional<NoteEntity> findByIdAndOwner(Long id, UserEntity owner);

    @Query("select n from NoteEntity n join fetch n.owner order by n.createdAt desc")
    List<NoteEntity> findAllWithOwner();
}