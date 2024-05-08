package edu.cvtc.ttt;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static edu.cvtc.ttt.Main.buttons;
import static java.awt.SystemColor.window;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;

class MainTest{
    @Test
    void createConnection(){
        assertDoesNotThrow(
            () -> {
                Connection db = Main.createConnection();
                assertNotNull(db);
                assertFalse(db.isClosed());
                assertTrue(db.isValid(5));
                db.close();
                assertTrue(db.isClosed());
            }
        );
    }

    @Test
    void sqlQuery() {
        assertDoesNotThrow(
            () -> {
                try (Connection db = Main.createConnection()) {
                    ResultSet rows = Main.sqlQuery(db,  "SELECT 6 AS test");
                    assertNotNull(rows);
                    assertTrue(rows.next());
                    int result = rows.getInt("test");
                    assertEquals(6, result);
                    db.close();
                }
            }
        );
    }

    @Test
    void deletePlayer() {
        assertDoesNotThrow(
                () -> {
                    try (Connection db = Main.createConnection()) {
                        String usrName = "Player";
                        int score = 5;
                        int comScore = 7;
                        Main.insertPlayer(usrName, score, comScore);
                        assertTrue(usrName.equals("Player"));
                        assertTrue(score == 5);
                        assertTrue(comScore == 7);

                        assertTrue(Main.deletePlayer(db, usrName));
                        assertTrue(Main.getPlayerId(usrName) == -1);
                        assertTrue(Main.getPlayerScore(db,usrName) == 0);
                        assertTrue(Main.getComputerScore(db,usrName) == 0);
                        assertTrue(Main.getGames(db,usrName) == 0);
                        db.close();
                    }
                }
        );
    }

    @Test
    void recordGame() {
        assertDoesNotThrow(
            () -> {
               try (Connection db = Main.createConnection()) {
                   assertTrue(db.isValid(5));
                   String pName = "p";
                   int plyrScore = 1;
                   int comScore = 0;
                   Main.recordGame(db, pName, plyrScore, comScore);
                   int numGames = Main.getGames(db, pName);
                   System.out.println(numGames);
                   assertTrue(numGames != 0);

                   pName = "k";
                   plyrScore = 5;
                   comScore = 1;
                   Main.recordGame(db, pName, plyrScore, comScore);
                   int Id = Main.getPlayerId(pName);
                   assertTrue(Id != -1);
                   numGames = Main.getGames(db, pName);
                   assertTrue(numGames != -1);
                   db.close();
                   assertFalse(db.isValid(5));
               }
            }
        );
    }
    @Test
    void insertGame() {
        assertDoesNotThrow(
                () -> {
                    try(Connection db = Main.createConnection()) {
                        String pName = "Invalid";
                        int id = Main.getPlayerId(pName);
                        int score = Main.getPlayerScore(db,pName);
                        int comScore = 10;
                        int total = -1;

                        assertTrue(id == -1);
                        assertTrue(score == 0);
                        assertTrue(comScore == 10);
                        assertTrue(total == -1);

                        Main.insertGame(id,score,comScore);

                        id = Main.getPlayerId(pName);
                        score = Main.getPlayerScore(db, pName);
                        comScore = Main.getComputerScore(db, pName);
                        total = Main.getGames(db, pName);

                        assertTrue(total == 0);
                        assertTrue(id == -1);
                        assertTrue(score == 0);
                        assertTrue(comScore == 0);
                        db.close();
                    }
                }
        );
    }

    @Test
    void checkStatus() {
        assertDoesNotThrow(
                () -> {
                    try(Connection db = Main.createConnection()) {
                        Main.test = true;
                        Main.txtfield = new JLabel();
                        Main.computer = new JLabel();
                        Main.player = new JLabel();

                        buttons = new JButton[9];

                        buttons[0] = new JButton();
                        buttons[1] = new JButton();
                        buttons[2] = new JButton();
                        buttons[3] = new JButton();
                        buttons[4] = new JButton();
                        buttons[5] = new JButton();
                        buttons[6] = new JButton();
                        buttons[7] = new JButton();
                        buttons[8] = new JButton();

                        assertThrowsExactly(ArrayIndexOutOfBoundsException.class,
                                () -> {
                                    buttons[10] = new JButton();
                                    buttons[10].setText("1");
                                }
                        );


                        Main.txtfield.setText("");
                        for (int i = 0; i < buttons.length; i++) {
                            buttons[i].setText("");
                            assertTrue(buttons[i].getText().equals(""));
                        }

                        Main.checkStatus();
                        assertFalse(Main.win, "No winner");
                        assertFalse(Main.txtfield.getText().equals("It's a tie!"));

                        for (int i = 0; i < buttons.length; i++) {
                            buttons[i].setText(" ");
                            assertTrue(buttons[i].getText().equals(" "));
                        }

                        Main.checkStatus();
                        assertFalse(Main.win, "No winner");
                        assertFalse(Main.txtfield.getText().equals("It's a tie!"));

                        for (int i = 0; i < buttons.length; i++) {
                            buttons[i].setText("X");
                            assertTrue(buttons[i].getText().equals("X"));
                        }

                        Main.checkStatus();
                        assertTrue(Main.win, "Player wins");
                        assertTrue(Main.txtfield.getText().equals(Main.playerName + " wins!"));

                        for (int i = 0; i < buttons.length; i++) {
                            buttons[i].setText("O");
                            assertTrue(buttons[i].getText().equals("O"));
                        }

                        Main.checkStatus();
                        assertTrue(Main.win, "Computer Wins");
                        assertTrue(Main.txtfield.getText().equals("Computer wins!"));


                        for (int i = 0; i < buttons.length; i++) {

                            if (i == 0 || i == 2 || i == 5 || i == 6 || i == 7) {
                                buttons[i].setText("X");
                                assertTrue(buttons[i].getText().equals("X"));
                            } else if (i == 1 || i == 3 || i == 4 || i == 8) {
                                buttons[i].setText("O");
                                assertTrue(buttons[i].getText().equals("O"));
                            }
                        }
                        Main.checkStatus();
                        assertTrue(Main.win, "tie");
                        assertTrue(Main.txtfield.getText().equals("It's a tie!"));
                        db.close();
                    }
                }
        );
    }
}