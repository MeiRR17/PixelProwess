# 🎮 PixelProwess – Final Project Guide

Welcome to **PixelProwess**, a simple yet fun 2D platformer game developed as a final project.  
This guide will help any user – even with no prior experience – to set up, run, and use the game from GitHub.

---

## 📦 1. Requirements

To run this game, you need:

- A Java IDE (e.g. **IntelliJ IDEA**, **Eclipse**, etc.)
- **Java JDK** installed
- Internet connection to access the GitHub repository

---

## ⬇️ 2. Download the Project

Open this link:  
👉 [https://github.com/MeiRR17/PixelProwess](https://github.com/MeiRR17/PixelProwess)

Then either:
- Click **Code > Download ZIP**, extract it,  
**or**
- Use Git:
  ```bash
  git clone https://github.com/MeiRR17/PixelProwess.git
🛠️ 3. Set Up the Project in IDE
Open your IDE (for example: IntelliJ IDEA).

Select Open > PixelProwess folder.

Wait for the project to load and index.

🧭 Set the Resources Folder
Go to:

css
Copy
Edit
src/main/res
Right-click the res folder →
Choose “Mark Directory as > Resources Root”
(especially important in IntelliJ – otherwise the game will not find its files).

▶️ 4. Running the Game
In the IDE, navigate to the src/main/java folder.

Find the main class (the one with public static void main(String[] args)).

Right-click it and choose Run.

🕹️ 5. Using the Game
🧵 Start Screen
When the game opens, you'll see a “Press any key to continue” message.

Press any key → a short transition video plays.

Then, you enter the Main Menu.

📋 Main Menu Options
Option	Description
PLAY	Go to character selection and start the game.
OPTIONS	Configure game settings.
LEADERBOARD	View top players and their high scores.
EXIT	Quit the game.

🎭 Play Menu
In the PLAY menu:

Choose one of 4 characters, or select a Random Character.

Then click PLAY again to start the game.

🌍 Inside the Game
You start on a central platform with 4 paths.

If you walk to the right → you teleport to the left side of a random map, where the real game begins.

The controls and instructions are shown on screen in the PLAY MENU.

🏆 Leaderboard
Shows the 7 highest scores of all users who played the game.
These are fetched from Firebase (players collection).

⚙️ Options
Opens the game settings screen (may include sound, controls, etc.).

❌ Exit
Closes the game.

💾 6. Database (Firebase)
This project uses Firebase for storing user data:

users: Stores usernames and hashed passwords.

players: Stores usernames and their high scores for leaderboard display.

📌 Notes
Make sure the res folder is marked correctly.

If something is missing (like an image or sound), check your IDE logs.

All assets (graphics/audio) are located inside src/main/res.

✅ Summary
Clone or download the project.

Open it in an IDE.

Mark the res folder as resource root.

Run the main class.

Use the menu to play and enjoy the game.

Thanks for checking out PixelProwess!
Feel free to ⭐ the repo or suggest improvements.
