package com.example.pract22;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Bot extends AppCompatActivity {

    private Button resetbtn, exitbtn;
    private int[][] board = new int[3][3];
    private int currentPlayer = 1; // 1 - крестик, 2 - нолик
    private boolean gameOver = false;
    SharedPreferences sharedPreferences;

    private Button[] cells;
    private TextView table1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);
        exitbtn = findViewById(R.id.exit);
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Bot.this, MainActivity.class);
                startActivity(intent);
            }
        });
        cells = new Button[]{
                findViewById(R.id.game1),
                findViewById(R.id.game2),
                findViewById(R.id.game3),
                findViewById(R.id.game4),
                findViewById(R.id.game5),
                findViewById(R.id.game6),
                findViewById(R.id.game7),
                findViewById(R.id.game8),
                findViewById(R.id.game9)
        };
        table1 = findViewById(R.id.table);

        // Инициализация SharedPreferences
        sharedPreferences = getSharedPreferences("TicTacToePrefs", Context.MODE_PRIVATE);
        loadStatistics();
        // Обработчики событий для клеток
        for (int i = 0; i < cells.length; i++) {
            final int index = i;
            cells[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleCellClick(index);
                }
            });
        }

        resetbtn = findViewById(R.id.reset);
        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetGame();
            }
        });

    }
    private void handleCellClick(int index) {
        if (gameOver) return;

        int row = index / 3;
        int col = index % 3;

        if (board[row][col] == 0) {
            board[row][col] = currentPlayer;
            cells[index].setText(currentPlayer == 1 ? "X" : "O");
            currentPlayer = currentPlayer == 1 ? 2 : 1;
            if (checkWin()) {
                gameOver = true;
                table1.setText("Игрок " + (currentPlayer == 1 ? "O" : "X") + " победил!");
                updateStatistics(true, false); // Обновление статистики
            } else if (checkDraw()) {
                gameOver = true;
                table1.setText("Ничья!");
                updateStatistics(false, true); // Обновление статистики
            } else {
                table1.setText("Ход игрока " + (currentPlayer == 1 ? "X" : "O"));
                if (currentPlayer == 2) {
                    // Если ход бота, выполняем его ход
                    botMove();
                }
            }
        }
    }
    private void botMove() {
        // Простой алгоритм для бота (например, случайный ход)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = 2; // Бот ставит "O"
                    cells[i * 3 + j].setText("O");
                    if (checkWin()) {
                        table1.setText("Бот победил!");
                        gameOver = true;
                        updateStatistics(false, false); // Обновление статистики
                    } else if (checkDraw()) {
                        table1.setText("Ничья!");
                        gameOver = true;
                        updateStatistics(false, true); // Обновление статистики
                    } else {
                        currentPlayer = 1; // возвращаем ход игроку
                        table1.setText("Ход игрока X");
                    }
                    return; // Завершаем выполнение после хода бота
                }
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
        for (Button cell : cells) {
            cell.setText("");
        }
    }


}