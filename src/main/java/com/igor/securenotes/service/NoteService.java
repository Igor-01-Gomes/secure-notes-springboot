package com.igor.securenotes.service;

import com.igor.securenotes.model.NoteEntity;
import com.igor.securenotes.model.Role;
import com.igor.securenotes.model.UserEntity;
import com.igor.securenotes.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public NoteEntity createNote(UserEntity owner, String content) {
        validateContent(content);

        NoteEntity note = new NoteEntity();
        note.setOwner(owner);
        note.setContent(content.trim());
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        return noteRepository.save(note);
    }

    public List<NoteEntity> getOwnNotes(UserEntity owner) {
        return noteRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    public List<NoteEntity> getAllNotes() {
        return noteRepository.findAllWithOwner();
    }

    public NoteEntity updateOwnNote(UserEntity owner, Long noteId, String newContent) {
        validateContent(newContent);

        NoteEntity note = noteRepository.findByIdAndOwner(noteId, owner)
                .orElseThrow(() -> new IllegalArgumentException("Note hittades inte eller tillhör inte användaren."));

        note.setContent(newContent.trim());
        note.setUpdatedAt(LocalDateTime.now());
        return noteRepository.save(note);
    }

    public void deleteOwnNote(UserEntity owner, Long noteId) {
        NoteEntity note = noteRepository.findByIdAndOwner(noteId, owner)
                .orElseThrow(() -> new IllegalArgumentException("Note hittades inte eller tillhör inte användaren."));
        noteRepository.delete(note);
    }

    public void adminDeleteAnyNote(UserEntity admin, Long noteId) {
        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Endast admin får radera andra användares notes.");
        }

        NoteEntity note = noteRepository.findById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Note hittades inte."));
        noteRepository.delete(note);
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Note-innehåll får inte vara tomt.");
        }
        if (content.trim().length() > 1000) {
            throw new IllegalArgumentException("Note-innehåll får vara max 1000 tecken.");
        }
    }
}
