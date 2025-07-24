package main.data;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.mindrot.jbcrypt.BCrypt;

import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseManager {
        private final Firestore db;
        private String currentUser = null;

        public FirebaseManager() throws IOException {
            // Initialize Firebase with your service account
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
            if (serviceAccount == null) {
                throw new IOException("serviceAccountKey.json file not found.");
            }

            // Check if FirebaseApp already exists
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
            }

            db = FirestoreClient.getFirestore();
        }

        public boolean signUp(String username, String password) {
            try {
                if (isUsernameTaken(username)) {
                    System.err.println("Sign up failed: Username is already taken.");
                    return false;
                }

                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                Map<String, Object> userData = new HashMap<>();
                userData.put("username", username);
                userData.put("passwordHash", hashedPassword);

                db.collection("users").document(username).set(userData).get();
                currentUser = username;
                System.out.println("User signed up with username: " + currentUser);

                DocumentReference playerRef = db.collection("players").document(currentUser);
                Map<String, Object> playerData = new HashMap<>();
                playerData.put("username", username);
                playerData.put("highScore", 0);
                playerRef.set(playerData).get();

                return true;
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Sign up failed: " + e.getMessage());
                return false;
            }
        }

        public boolean signIn(String username, String password) {
            try {
                DocumentSnapshot document = db.collection("users").document(username).get().get();
                if (!document.exists()) {
                    System.err.println("Sign in failed: Username does not exist.");
                    return false;
                }

                String storedHash = document.getString("passwordHash");
                if (BCrypt.checkpw(password, storedHash)) {
                    currentUser = username;
                    System.out.println("User signed in with username: " + currentUser);
                    return true;
                } else {
                    System.err.println("Sign in failed: Incorrect password.");
                    return false;
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Sign in failed: " + e.getMessage());
                return false;
            }
        }

        public Firestore getDb() {
            return db;
        }

        public String getCurrentUserId() {
            System.out.println("Retrieving current user ID: " + currentUser);
            return currentUser;
        }

    public void updateScore(int newScore) {
        if (currentUser == null) {
            System.err.println("Error: currentUser is null.");
            return;
        }

        DocumentReference docRef = db.collection("players").document(currentUser);

        // Fetch the current high score from the database
        try {
            DocumentSnapshot document = docRef.get().get();
            if (document.exists()) {
                int currentHighScore = document.getLong("highScore").intValue();
                if (newScore > currentHighScore) {
                    // Update high score only if the new score is higher
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("highScore", newScore);

                    docRef.set(updates, SetOptions.merge()).get();
                    System.out.println("New high score updated to: " + newScore + " for user: " + currentUser);
                } else {
                    System.out.println("Score not updated. Current high score: " + currentHighScore + ", New score: " + newScore);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error updating score: " + e.getMessage());
        }
    }


    private boolean isUsernameTaken(String username) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = db.collection("users").document(username).get().get();
        return document.exists();
    }
}
