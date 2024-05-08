package edu.cvtc.ttt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Random;

public class Main {
    public static final String DATABASE_NAME = "ttt";
    public static final String DATABASE_PATH = DATABASE_NAME + ".db";
    private static final int TIMEOUT = 5;
    private static PreparedStatement sqlInsertGame;
    private static PreparedStatement sqlFetchGames;
    private static PreparedStatement sqlFetchPlayerWins;
    private static PreparedStatement sqlFetchComputerWins;
    private static PreparedStatement sqlResetGames;
    private static PreparedStatement sqlDeletePlayer;
    private static PreparedStatement sqlGetPlayerId;
    private static PreparedStatement sqlInsertPlayer;
    private static PreparedStatement sqlGetPlayerRecords;
    private static PreparedStatement sqlGetLeaderBoard;
    private static Random rnd;
    private static boolean usrTurn = true;
    static String playerName;
    static JLabel txtfield;
    static JLabel computer;
    static JLabel player;
    private static int score;
    private static int computerScore;
    static JButton[] buttons;
    static JButton record;
    public static Timer timer;
    private static Handler gameRecords;
    private static Handler handler;
    private static ComputerHandler comHandler;
    public static String playerGame;
    public static String computerGame;
    static boolean win;
    static boolean test = false;

    public static class gameRecords implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            JButton records = (JButton) evt.getSource();
            if (records.getText().equals("Records")) {
                try {
                    displayRecords();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public static class Handler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            JButton clickedBtn = (JButton) evt.getSource();
            for (JButton button : buttons) {
                if (clickedBtn == button) {
                    if (usrTurn) {
                        if (button.getText().isEmpty()) {
                            button.setText("X");
                            usrTurn = false;
                            txtfield.setFont(new Font("Apple Casual", Font.BOLD, 50));
                            txtfield.setText("Computer's turn.");
                            checkStatus();
                            triggerComputer();
                        }
                    }
                }
            }
        }
    }

    public static void triggerComputer() {
        Timer timer = new Timer(1300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comHandler == null) {
                    comHandler = new ComputerHandler();
                }
                comHandler.actionPerformed(null);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    public static class ComputerHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {

            rnd = new Random();
            int choice = rnd.nextInt(9);

            for (int j = 0; j < buttons.length; j++) {
                if (buttons[choice].getText().isEmpty() && !usrTurn) {
                    buttons[choice].setText("O");
                    txtfield.setFont(new Font("Apple Casual", Font.BOLD, 50));
                    txtfield.setText(playerName + "'s turn.");
                    usrTurn = true;
                    buttons[choice].doClick();
                    checkStatus();
                } else if(!buttons[choice].getText().isEmpty()){
                    choice = rnd.nextInt(9);
                }
            }
        }
    }
    public static void main(String[] args) {
        getPlayerName();
        gameRecords gameRecords = new gameRecords();

        buttons = new JButton[9];
        if (handler == null) {
            handler = new Handler();
        }

        // Create GUI
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(800, 600));
        frame.getContentPane().setBackground(Color.white);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        txtfield = new JLabel("Tic Tac Toe");
        txtfield.setBackground(Color.BLACK);
        txtfield.setForeground(Color.WHITE);
        txtfield.setFont(new Font("Apple Chancery",Font.BOLD,60));
        txtfield.setHorizontalAlignment(JLabel.CENTER);
        txtfield.setOpaque(true);

        JPanel title = new JPanel();
        title.setLayout(new BorderLayout());
        title.setBounds(0,0,800,80);
        title.add(txtfield);
        frame.add(title,BorderLayout.NORTH);

        JLabel instructions = new JLabel();
        instructions.setBackground(Color.BLACK);
        instructions.setForeground(Color.WHITE);
        instructions.setFont(new Font("Apple Casual",Font.BOLD,25));
        instructions.setHorizontalAlignment(JLabel.CENTER);
        instructions.setText("In order to win, you need to get three in a row. You can go first.");
        instructions.setOpaque(true);

        JPanel directions = new JPanel();
        directions.setLayout(new BorderLayout());
        directions.setBounds(0,510,800,60);
        directions.add(instructions);
        frame.add(directions,BorderLayout.SOUTH);

        JPanel buttonPnl = new JPanel();
        buttonPnl.setLayout(new GridLayout(3,3));
        buttonPnl.setBounds(200, 80, 400, 430);
        buttonPnl.setBackground(Color.white);
        frame.add(buttonPnl,BorderLayout.CENTER);

        // Add Game Board
        for(int i =0; i < buttons.length; i++) {
            buttons[i] = new JButton();
            buttonPnl.add(buttons[i]);
            buttons[i].setFont(new Font("American Typewriter",Font.BOLD,100));
            buttons[i].setBackground(Color.WHITE);
            buttons[i].setForeground(Color.BLACK);
            buttons[i].setText("");
            buttons[i].setFocusable(false);
            buttons[i].addActionListener(handler);
        }

        player = new JLabel(playerName + "'s score: " + score);
        player.setBackground(Color.lightGray);
        player.setForeground(Color.BLACK);
        player.setFont(new Font("Dialog",Font.BOLD,15));
        player.setHorizontalAlignment(JLabel.CENTER);
        player.setOpaque(true);

        JButton records = new JButton("Records");
        records.setFocusable(false);
        records.addActionListener(gameRecords);

        JPanel leftPnl = new JPanel();
        leftPnl.setLayout(new BorderLayout());
        leftPnl.setBounds(0, 80, 200, 430);
        leftPnl.setBackground(Color.lightGray);
        leftPnl.add(records, BorderLayout.SOUTH);
        leftPnl.add(player);
        frame.add(leftPnl, BorderLayout.WEST);

        computer = new JLabel("Computer's score: " + computerScore);
        computer.setBackground(Color.lightGray);
        computer.setForeground(Color.BLACK);
        computer.setFont(new Font("Dialog",Font.BOLD,15));
        computer.setHorizontalAlignment(JLabel.CENTER);
        computer.setOpaque(true);

        JPanel rightPnl = new JPanel();
        rightPnl.setLayout(new BorderLayout());
        rightPnl.setBounds(600, 80, 200, 430);
        rightPnl.setBackground(Color.lightGray);
        rightPnl.add(computer);
        frame.add(rightPnl, BorderLayout.EAST);
    }
    public static Connection createConnection () {
        Connection result = null;
        try {
            result = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH);
            Statement command = result.createStatement();
            command.setQueryTimeout(TIMEOUT);

            // Create the tables.
            command.executeUpdate("CREATE TABLE IF NOT EXISTS players (id INTEGER PRIMARY KEY, name TEXT NOT NULL)");
            command.executeUpdate("CREATE TABLE IF NOT EXISTS games (id INTEGER PRIMARY KEY, players_id INTEGER, score INTEGER, computer_score INTEGER, FOREIGN KEY (players_id) REFERENCES players(id))");

            sqlInsertGame = result.prepareStatement("INSERT INTO games (players_id, score, computer_score) VALUES (?, ?, ?)");
            sqlFetchGames = result.prepareStatement("SELECT COUNT(players.name) AS total_games FROM games JOIN players ON games.players_id = players.id WHERE players.name = ?");
            sqlFetchPlayerWins = result.prepareStatement("SELECT SUM(score) AS total_score FROM games JOIN players ON games.players_id = players.id WHERE players.name = ?");
            sqlFetchComputerWins = result.prepareStatement("SELECT SUM(computer_score) AS computer_score FROM games JOIN players ON games.players_id = players.id WHERE players.name = ?");
            sqlResetGames = result.prepareStatement("DELETE FROM games WHERE players_id IN (SELECT id FROM players WHERE name = ?)");
            sqlDeletePlayer = result.prepareStatement("DELETE FROM players WHERE name = ?");
            sqlGetPlayerId = result.prepareStatement(("SELECT id FROM players WHERE name = ?"));
            sqlInsertPlayer = result.prepareStatement("INSERT INTO players (name) VALUES (?)");
            sqlGetPlayerRecords = result.prepareStatement("SELECT score AS player_score, computer_score FROM games JOIN players ON games.players_id = players.id WHERE games.players_id = ?");
            sqlGetLeaderBoard = result.prepareStatement("SELECT players.id AS player_id, players.name AS name, SUM(score) AS player_total_score, SUM(computer_score) AS computer_total_score FROM games JOIN players ON games.players_id = players.id GROUP BY players.id, players.name ORDER BY player_total_score DESC LIMIT 20");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public static void displayRecords () throws SQLException {
        JPanel prompt = new JPanel();
        prompt.add(new JLabel("Would you like to view the leaderboard or your last twenty records?"));

        String[] options = {"LeaderBoard", "Last Twenty Records"};
        int result = JOptionPane.showOptionDialog(null, prompt, "Game Records", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        try (Connection db = Main.createConnection()){
            if (result == 0) {
                StringBuilder recordList = new StringBuilder();

                ResultSet rows = sqlGetLeaderBoard.executeQuery();

                int count = 1;
                while (rows.next() && count < 11) {
                    String player = rows.getString("name");
                    int playerScore = rows.getInt("player_total_score");
                    int comScore = rows.getInt("computer_total_score");

                    recordList.append("Rank: ").append(count).append("  Name: ").append(player).append("  Player score: ").append(playerScore).append("  Computer Score: ").append(comScore).append("\n\n");

                    count++;
                }
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), recordList.toString());

            } else if (result == 1) {
                StringBuilder recordsList = new StringBuilder();

                int id = getPlayerId(playerName);
                if (id == -1) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), playerName + " does not have any games to show.");
                } else {
                    sqlGetPlayerRecords.setInt(1, id);
                    ResultSet rows = sqlGetPlayerRecords.executeQuery();

                    int count = 1;
                    while (rows.next() && count < 21) {
                        int playerScore = rows.getInt("player_score");
                        int comScore = rows.getInt("computer_score");

                        recordsList.append("Game: ").append(count).append("  Player score: ").append(playerScore).append("  Computer Score: ").append(comScore).append("\n");

                        count++;
                    }
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), recordsList.toString());
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static ResultSet sqlQuery (Connection db, String sql){
        ResultSet result = null;
        try {
            Statement statement = db.createStatement();
            result = statement.executeQuery(sql);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public static void playAgainPrompt () {
        JPanel prompt = new JPanel();
        prompt.add(new JLabel("Would you like to play again?"));

        playerGame = "games";
        computerGame = "games";

        if (score == 1) {
            playerGame = "game";
        }
        if (computerScore == 1) {
                computerGame = "game";
        }

        int result = JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(), prompt, "Continue?",JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Great! Select your square!");

            usrTurn = true;

            for (JButton button : buttons) {
                button.setFont(new Font("American Typewriter", Font.BOLD, 100));
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
                button.setText("");
                button.removeActionListener(handler);
                button.addActionListener(handler);
                button.setEnabled(true);
            }
        } else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), playerName + " won " + score + " " + playerGame + " and the computer won " + computerScore + " " + computerGame + ".  Goodbye.");
            System.exit(0);
        }
    }

    public static boolean deletePlayer(Connection db, String playerName) throws SQLException {
        sqlResetGames.setString(1, playerName);
        int gameDeleted = sqlResetGames.executeUpdate();
        sqlDeletePlayer.setString(1,playerName);
        int playerDeleted = sqlDeletePlayer.executeUpdate();

        if (gameDeleted > 0 && playerDeleted > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static int getGames(Connection db, String playerName) throws SQLException {
        sqlFetchGames.setString(1,playerName);
        ResultSet rows = sqlFetchGames.executeQuery();
        int gameCount = 0;

        while (rows.next()) {
            gameCount = rows.getInt("total_games");
        }
        return gameCount;
    }

    public static int getPlayerScore(Connection db, String playerName) throws SQLException {
        sqlFetchPlayerWins.setString(1,playerName);
        ResultSet rows = sqlFetchPlayerWins.executeQuery();
        int totalScore = 0;

        while (rows.next()) {
            totalScore = rows.getInt("total_score");
        }

        return totalScore;
    }

    public static int getComputerScore(Connection db, String playerName) throws SQLException {
        sqlFetchComputerWins.setString(1,playerName);
        ResultSet rows = sqlFetchComputerWins.executeQuery();
        int computerTotal = 0;

        while (rows.next()) {
            computerTotal = rows.getInt("computer_score");
        }

        return computerTotal;
    }

    public static void recordGame(Connection db, String playerName, int score, int computerScore) throws SQLException {
        int playerId = getPlayerId(playerName);

        // Insert or update player info
        if (playerId == -1) {
            insertPlayer(playerName, score, computerScore);
        } else {
            //System.out.println("Player found in the database. Updating player information...");
            insertGame(playerId, score, computerScore);
        }
    }

    public static void insertPlayer(String playerName, int score, int computerScore) throws SQLException {
        sqlInsertPlayer.setString(1, playerName);
        sqlInsertPlayer.executeUpdate();

        // Get playerId for inserted player
        int playerId = getPlayerId(playerName);
        // Insert the game for the inserted player
        insertGame(playerId, score, computerScore);
    }

    public static int getPlayerId(String playerName) throws SQLException {
        sqlGetPlayerId.setString(1, playerName);
        ResultSet playerIdResult = sqlGetPlayerId.executeQuery();

        int playerId = -1;

         if (playerIdResult.next()) {
             playerId = playerIdResult.getInt("id");
         }
         return playerId;
    }

    public static void insertGame(int playerId, int score, int computerScore) throws SQLException {
        sqlInsertGame.setInt(1, playerId);
        sqlInsertGame.setInt(2, score);
        sqlInsertGame.setInt(3, computerScore);
        sqlInsertGame.executeUpdate();
    }

    public static String getPlayerName() {
        JTextField usrName = new JTextField(20);
        JPanel pnl = new JPanel();
        pnl.add(new JLabel("Hello, what is your name?"));
        pnl.add(usrName);

        String playerWins = "wins";
        String computerWins = "games";
        String games = "games";
        int gameCount = 0;

        int result = JOptionPane.showConfirmDialog(null, pnl, "Greetings",JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            if (usrName.getText().isBlank() || usrName.getText().isEmpty()) {
                playerName = "Player";

                try(Connection db = Main.createConnection()) {
                    gameCount = getGames(db, playerName);
                    score = getPlayerScore(db, playerName);
                    computerScore = getComputerScore(db, playerName);
                    if(gameCount == 1) {
                        games = "game";
                    }
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }

                if (score == 1) {
                    playerWins = "win";
                }
                if (computerScore == 1) {
                    computerWins = "game";
                }

                JOptionPane.showMessageDialog(null, "Hello " + playerName + ", you have played " + gameCount + " " + games + " and currently have " + score + " " + playerWins + " and lost " + computerScore + " " + computerWins + ".");
            } else {
                playerName = usrName.getText();
                int registeredUser = 0;

                try(Connection db = Main.createConnection()) {
                    registeredUser = getPlayerId(playerName);
                     if (registeredUser == -1){
                        JOptionPane.showMessageDialog(null, "Hello " + playerName + ", let's play.");
                    } else {
                         gameCount = getGames(db, playerName);
                         Main.score = getPlayerScore(db, playerName);
                         Main.computerScore = getComputerScore(db, playerName);
                         registeredUser = 0;
                     }

                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }

                if (score == 1) {
                    playerWins = "win";
                }
                if (computerScore == 1) {
                    computerWins = "game";
                }

                if (registeredUser == 0) {
                    JOptionPane.showMessageDialog(null, "Welcome back  " + playerName + ", you have played " + gameCount + " " + games + " and currently have " + score + " " + playerWins + " and lost " + computerScore + " " + computerWins + ".");
                }
            }
        } else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION){
            System.exit(0);
        }
        return playerName;
    }

    public static int[] checkStatus() {
        int winner = 0;
        int[] winningButtons = null;
        boolean noSpacesLeft = false;
        win = false;

        // Check if the user won
        if ((buttons[0].getText().equals("X")) && (buttons[1].getText().equals("X")) && (buttons[2].getText().equals("X"))) {
            win = true;
            winner = 1;
            winningButtons = new int[]{winner, 0, 1, 2};
            callWinner(winningButtons);
        } else if ((buttons[3].getText().equals("X")) && (buttons[4].getText().equals("X")) && (buttons[5].getText().equals("X"))) {
            win = true;
            winner = 1;
            winningButtons = new int[]{winner, 3, 4, 5};
            callWinner(winningButtons);
        } else if ((buttons[6].getText().equals("X")) && (buttons[7].getText().equals("X")) && (buttons[8].getText().equals("X"))) {
            win = true;
            winner = 1;
            winningButtons = new int[]{winner, 6, 7, 8};
            callWinner(winningButtons);
        } else if ((buttons[0].getText().equals("X")) && (buttons[3].getText().equals("X")) && (buttons[6].getText().equals("X"))) {
            win = true;
            winner = 1;
            winningButtons = new int[]{winner, 0, 3, 6};
            callWinner(winningButtons);
        } else if ((buttons[1].getText().equals("X")) && (buttons[4].getText().equals("X")) && (buttons[7].getText().equals("X"))) {
            win = true;
            winner = 1;
            winningButtons = new int[]{winner, 1, 4, 7};;
            callWinner(winningButtons);
        } else if ((buttons[2].getText().equals("X")) && (buttons[5].getText().equals("X")) && (buttons[8].getText().equals("X"))) {
            win = true;
            winner = 1;
            winningButtons = new int[]{winner, 2, 5, 8};
            callWinner(winningButtons);
        } else if ((buttons[0].getText().equals("X")) && (buttons[4].getText().equals("X")) && (buttons[8].getText().equals("X"))) {
            win = true;
            winner = 1;
            winningButtons = new int[]{winner, 0, 4, 8};
            callWinner(winningButtons);
        }else if ((buttons[2].getText().equals("X")) && (buttons[4].getText().equals("X")) && (buttons[6].getText().equals("X"))) {
            win = true;
            winner = 1;
            winningButtons = new int[]{winner, 2, 4, 6};
            callWinner(winningButtons);
            // Check if computer won
        } else if ((buttons[0].getText().equals("O")) && (buttons[1].getText().equals("O")) && (buttons[2].getText().equals("O"))) {
            win = true;
            winner = 2;
            winningButtons = new int[]{winner, 0, 1, 2};
            callWinner(winningButtons);
        } else if ((buttons[3].getText().equals("O")) && (buttons[4].getText().equals("O")) && (buttons[5].getText().equals("O"))) {
            win = true;
            winner = 2;
            winningButtons = new int[]{winner, 3, 4, 5};
            callWinner(winningButtons);
        } else if ((buttons[6].getText().equals("O")) && (buttons[7].getText().equals("O")) && (buttons[8].getText().equals("O"))) {
            win = true;
            winner = 2;
            winningButtons = new int[]{winner, 6, 7, 8};
            callWinner(winningButtons);
        } else if ((buttons[0].getText().equals("O")) && (buttons[3].getText().equals("O")) && (buttons[6].getText().equals("O"))) {
            win = true;
            winner = 2;
            winningButtons = new int[] {winner, 0, 3, 6};
            callWinner(winningButtons);
        } else if ((buttons[1].getText().equals("O")) && (buttons[4].getText().equals("O")) && (buttons[7].getText().equals("O"))) {
            win = true;
            winner = 2;
            winningButtons = new int[] {winner, 1, 4, 7};
            callWinner(winningButtons);
        } else if ((buttons[2].getText().equals("O")) && (buttons[5].getText().equals("O")) && (buttons[8].getText().equals("O"))) {
            win = true;
            winner = 2;
            winningButtons = new int[] {winner, 2, 5, 8};
            callWinner(winningButtons);
        } else if ((buttons[0].getText().equals("O")) && (buttons[4].getText().equals("O")) && (buttons[8].getText().equals("O"))) {
            win = true;
            winner = 2;
            winningButtons = new int[] {winner, 0, 4, 8};
            callWinner(winningButtons);
        } else if ((buttons[2].getText().equals("O")) && (buttons[4].getText().equals("O")) && (buttons[6].getText().equals("O"))) {
            win = true;
            winner = 2;
            winningButtons = new int[] {winner, 2, 4, 6};
            callWinner(winningButtons);
        } else {
            for (int i =0; i < buttons.length; i++) {
                if (buttons[i].getText().isEmpty() || buttons[i].getText().isBlank()) {
                    noSpacesLeft = true;
                    break;
                }
            }

            if(!noSpacesLeft) {
                win = true;
                winner = 3;
                winningButtons = new int[]{winner, 0, 0, 0};
                callWinner(winningButtons);
            }
        }
        return winningButtons;
    }

    public static void callWinner(int[] result) {
        int winner = result[0];

        int a = result[1];
        int b = result[2];
        int c = result[3];

        if (winner == 1) {
            usrWins(a, b, c);
        } else if (winner == 2) {
            comWins(a, b, c);
        } else if (winner == 3) {
            tie();
        }
    }


    public static void usrWins(int a, int b, int c) {
        buttons[a].setBackground(Color.green);
        buttons[b].setBackground(Color.green);
        buttons[c].setBackground(Color.green);
        for (JButton button : buttons) {
            button.setEnabled(false);
        }
        txtfield.setText(playerName + " wins!");

        score += 1;

        player.setText(playerName + "'s score: " + score);

        if (test == false) {
            try (Connection db = Main.createConnection()) {
                recordGame(db, playerName, 1, 0);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
            playAgainPrompt();
        }
    }

    public static void comWins(int a, int b, int c) {
        buttons[a].setBackground(Color.green);
        buttons[b].setBackground(Color.green);
        buttons[c].setBackground(Color.green);
        for (JButton button : buttons) {
            button.setEnabled(false);
        }
        txtfield.setText("Computer wins!");

        computerScore += 1;
        computer.setText("Computer's score: " + computerScore);

        if (test == false) {
            try (Connection db = Main.createConnection()) {
                recordGame(db, playerName, 0, 1);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
            playAgainPrompt();
        }
    }

    public static void tie() {
        for (JButton button : buttons) {
            button.setEnabled(false);
            button.setBackground(Color.green);
        }
        txtfield.setText("It's a tie!");
        score += 1;
        computerScore += 1;

        computer.setText("Computer's score: " + computerScore);
        player.setText(playerName + "'s score: " + score);

        if (test == false) {
            try (Connection db = Main.createConnection()) {
                recordGame(db, playerName, 1, 1);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
            playAgainPrompt();
        }
    }
}