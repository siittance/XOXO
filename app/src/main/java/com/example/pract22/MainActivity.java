package com.example.pract22;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    SharedPreferences themeSettings;
    SharedPreferences.Editor settingsEditor;
    ImageButton imageTheme;

    private int[][] board = new int[3][3];
    private int currentPlayer = 1; // 1 - крестик, 2 - нолик
    private boolean gameOver = false;
    SharedPreferences sharedPreferences;

    private Button[] btns;
    private TextView table1;
    private Button btnbot, reset;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получаем SharedPreferences
        themeSettings = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        // Проверяем, есть ли уже сохраненные настройки
        if (!themeSettings.contains("MODE_NIGHT_ON")) {
            settingsEditor = themeSettings.edit();
            settingsEditor.putBoolean("MODE_NIGHT_ON", false);
            settingsEditor.apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            setCurrentTheme();
        }

        setContentView(R.layout.activity_main);

        // Находим кнопка для изменения темы
        imageTheme = findViewById(R.id.imgbtn);
        updateImageButton();

        // Устанавливаем слушатель клика
        imageTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверяем текущее состояние и переключаем тему
                if (themeSettings.getBoolean("MODE_NIGHT_ON", false)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    settingsEditor = themeSettings.edit();
                    settingsEditor.putBoolean("MODE_NIGHT_ON", false);
                    settingsEditor.apply();

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    settingsEditor = themeSettings.edit();
                    settingsEditor.putBoolean("MODE_NIGHT_ON", true);
                    settingsEditor.apply();
                }
                // Обновляем изображение кнопки
                updateImageButton();
            }
        });

        //кусок говна ебаный я ебал всю семью андроида
        //а если без рофлов
        //инициализация кнопок и массива, крч полный фарш


        //кнопка перехода на другую активити
        btnbot = findViewById(R.id.bot);
        btnbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Bot.class);
                startActivity(intent);
            }
        });

        //массив
        btns = new Button[]{
                findViewById(R.id.game1),
                findViewById(R.id.game2),
                findViewById(R.id.game3),
                findViewById(R.id.game4),
                findViewById(R.id.game5),
                findViewById(R.id.game6),
                findViewById(R.id.game7),
                findViewById(R.id.game8),
                findViewById(R.id.game9),
        };
        //инициализация текста сверху
        table1 = findViewById(R.id.table);

        // Инициализация SharedPreferences
        sharedPreferences = getSharedPreferences("TicTacToePrefs", Context.MODE_PRIVATE);
        loadStatistics();
        // Обработчики событий для клеток
        for (int i = 0; i < btns.length; i++) {
            final int index = i;
            btns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleCellClick(index);
                }
            });
        }

        reset = findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetGame();
            }
        });



    }

    // Метод для обновления изображения в зависимости от темы
    private void updateImageButton() {
        if (themeSettings.getBoolean("MODE_NIGHT_ON", false)) {
            imageTheme.setImageResource(R.drawable.sun); // здесь укажим иконку для светлой темы
        } else {
            imageTheme.setImageResource(R.drawable.moon); // здесь укажим иконку для темной темы
        }
    }

    private void setCurrentTheme() {
        if (themeSettings.getBoolean("MODE_NIGHT_ON", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void handleCellClick(int index) {
        if (gameOver) return;

        int row = index / 3;
        int col = index % 3;

        if (board[row][col] == 0) {
            board[row][col] = currentPlayer;
            btns[index].setText(currentPlayer == 1 ? "X" : "O");
            currentPlayer = currentPlayer == 1 ? 2 : 1;
            if (checkWin()) {
                gameOver = true;
                table1.setText("Игрок " + (currentPlayer == 1 ? "O" : "X") + " победил!");
                updateStatistics(true, false);
            } else if (checkDraw()) {
                gameOver = true;
                table1.setText("Ничья!");
                updateStatistics(false, true);
            } else {
                table1.setText("Ход игрока " + (currentPlayer == 2 ? "X" : "O"));
            }
        }
    }

    private boolean checkWin() {
        // Проверка строк, столбцов и диагоналей
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != 0)
                return true;
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != 0)
                return true;
        }
        if ((board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != 0) ||
                (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != 0))
            return true;
        return false;
    }

    private boolean checkDraw() {
        // Проверка на заполненность всех клеток
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) return false;
            }
        }
        return true;
    }

    private void updateStatistics(boolean isWin, boolean isDraw) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isWin) {
            editor.putInt("wins", sharedPreferences.getInt("wins", 0) + 1);
        }
        else if (isDraw) {
            editor.putInt("draws", sharedPreferences.getInt("draws", 0) + 1);
        }
        else {
            editor.putInt("losses", sharedPreferences.getInt("losses", 0) + 1);
        }
        editor.apply();
        loadStatistics();
    }

    private void loadStatistics() {
        int wins = sharedPreferences.getInt("wins", 0);
        int losses = sharedPreferences.getInt("losses", 0);
        int draws = sharedPreferences.getInt("draws", 0);
        table1.setText("Победы: " + wins + ", Поражения: " + losses + ", Ничьи: " + draws);
    }
    private void ResetGame(){
        gameOver = false;
        currentPlayer = 1;
        table1.setText("Ход игрока X");
        board = new int[3][3];
        for (Button cell : btns) {
            cell.setText("");
        }
    }
}





