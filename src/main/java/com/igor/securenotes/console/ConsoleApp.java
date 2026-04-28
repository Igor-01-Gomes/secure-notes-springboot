package com.igor.securenotes.console;

import com.igor.securenotes.model.NoteEntity;
import com.igor.securenotes.model.Role;
import com.igor.securenotes.model.UserEntity;
import com.igor.securenotes.service.AuthService;
import com.igor.securenotes.service.NoteService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleApp implements CommandLineRunner {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AuthService authService;
    private final NoteService noteService;
    private final Scanner scanner = new Scanner(System.in);

    @Value("${ADMIN_USERNAME}")
    private String adminUsername;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    public ConsoleApp(AuthService authService, NoteService noteService) {
        this.authService = authService;
        this.noteService = noteService;
    }

    @PostConstruct
    public void seedAdmin() {
        authService.createAdminIfMissing(adminUsername, adminPassword);
    }

    @Override
    public void run(String... args) {
        boolean running = true;

        while (running) {
            printStartMenu();
            String choice = readLine("Välj: ");

            switch (choice) {
                case "1" -> handleRegister();
                case "2" -> handleLogin();
                case "0" -> {
                    System.out.println("Avslutar programmet. Hej då!");
                    running = false;
                }
                default -> System.out.println("Ogiltigt val. Försök igen.");
            }
        }
    }

    private void printStartMenu() {
        System.out.println("\n=== Secure Notes ===");
        System.out.println("1. Registrera användare");
        System.out.println("2. Logga in");
        System.out.println("0. Avsluta");
    }

    private void handleRegister() {
        try {
            String username = readLine("Användarnamn: ");
            String password = readLine("Lösenord: ");
            authService.registerUser(username, password);
            System.out.println("Användare skapad.");
        } catch (IllegalArgumentException ex) {
            System.out.println("Fel: " + ex.getMessage());
        }
    }

    private void handleLogin() {
        String username = readLine("Användarnamn: ");
        String password = readLine("Lösenord: ");

        authService.login(username, password).ifPresentOrElse(
                this::handleUserSession,
                () -> System.out.println("Fel användarnamn eller lösenord.")
        );
    }

    private void handleUserSession(UserEntity currentUser) {
        System.out.printf("\nInloggad som %s (%s)%n", currentUser.getUsername(), currentUser.getRole());
        boolean loggedIn = true;

        while (loggedIn) {
            printUserMenu(currentUser);
            String choice = readLine("Välj: ");

            try {
                switch (choice) {
                    case "1" -> createNote(currentUser);
                    case "2" -> showOwnNotes(currentUser);
                    case "3" -> updateOwnNote(currentUser);
                    case "4" -> deleteOwnNote(currentUser);
                    case "5" -> changePassword(currentUser);
                    case "6" -> {
                        if (currentUser.getRole() == Role.ADMIN) {
                            showAllNotes();
                        } else {
                            System.out.println("Ogiltigt val.");
                        }
                    }
                    case "7" -> {
                        if (currentUser.getRole() == Role.ADMIN) {
                            adminDeleteAnyNote(currentUser);
                        } else {
                            System.out.println("Ogiltigt val.");
                        }
                    }
                    case "0" -> {
                        System.out.println("Du är nu utloggad.");
                        loggedIn = false;
                    }
                    default -> System.out.println("Ogiltigt val.");
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Fel: " + ex.getMessage());
            }
        }
    }

    private void printUserMenu(UserEntity user) {
        System.out.println("\n=== Meny ===");
        System.out.println("1. Skapa note");
        System.out.println("2. Visa mina notes");
        System.out.println("3. Ändra min note");
        System.out.println("4. Radera min note");
        System.out.println("5. Byt lösenord");
        if (user.getRole() == Role.ADMIN) {
            System.out.println("6. Visa alla notes (admin)");
            System.out.println("7. Radera valfri note (admin)");
        }
        System.out.println("0. Logga ut");
    }

    private void createNote(UserEntity user) {
        String content = readLine("Skriv din note: ");
        noteService.createNote(user, content);
        System.out.println("Note sparad.");
    }

    private void showOwnNotes(UserEntity user) {
        List<NoteEntity> notes = noteService.getOwnNotes(user);
        printNotes(notes, false);
    }

    private void updateOwnNote(UserEntity user) {
        showOwnNotes(user);
        Long noteId = readLong("Ange ID på note att ändra: ");
        String newContent = readLine("Ny text: ");
        noteService.updateOwnNote(user, noteId, newContent);
        System.out.println("Note uppdaterad.");
    }

    private void deleteOwnNote(UserEntity user) {
        showOwnNotes(user);
        Long noteId = readLong("Ange ID på note att radera: ");
        noteService.deleteOwnNote(user, noteId);
        System.out.println("Note raderad.");
    }

    private void changePassword(UserEntity user) {
        String currentPassword = readLine("Nuvarande lösenord: ");
        String newPassword = readLine("Nytt lösenord: ");
        authService.changePassword(user, currentPassword, newPassword);
        System.out.println("Lösenord uppdaterat.");
    }

    private void showAllNotes() {
        List<NoteEntity> notes = noteService.getAllNotes();
        printNotes(notes, true);
    }

    private void adminDeleteAnyNote(UserEntity admin) {
        showAllNotes();
        Long noteId = readLong("Ange ID på note att radera: ");
        noteService.adminDeleteAnyNote(admin, noteId);
        System.out.println("Note raderad av admin.");
    }

    private void printNotes(List<NoteEntity> notes, boolean includeOwner) {
        if (notes.isEmpty()) {
            System.out.println("Inga notes hittades.");
            return;
        }

        System.out.println("\n--- Notes ---");
        for (NoteEntity note : notes) {
            String ownerPart = includeOwner ? " | Ägare: " + note.getOwner().getUsername() : "";
            System.out.printf("ID: %d | Skapad: %s | Uppdaterad: %s%s%n",
                    note.getId(),
                    note.getCreatedAt().format(FORMATTER),
                    note.getUpdatedAt().format(FORMATTER),
                    ownerPart);
            System.out.println("Text: " + note.getContent());
            System.out.println("------------------------------");
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private Long readLong(String prompt) {
        while (true) {
            try {
                return Long.parseLong(readLine(prompt));
            } catch (NumberFormatException ex) {
                System.out.println("Du måste ange ett giltigt nummer.");
            }
        }
    }
}