package client;

import java.sql.*;
import java.util.Scanner;

import util.DatabaseConnection;
import custom_exceptions.UserNotFoundException;
import custom_exceptions.InvalidEmailException;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static int currentIndex = -1;

    private static void registerUser(Connection connection) {
        try {
            System.out.println("--------------Register User--------------");
            String username = "", email = "", password = "", confirmPassword = "";
            System.out.println("Enter your email id:");
            email = sc.next();
            System.out.println("Enter a username:");
            username = sc.next();
            System.out.println("Enter a password:");
            password = sc.next();
            System.out.println("Confirm password:");
            confirmPassword = sc.next();
            boolean subscriptionType;
            System.out.println("Do you want premium subscription ? Enter Yes or No.");
            String str = "";
            str = sc.next();
            if (str.toLowerCase().equals("yes") || str.toLowerCase().equals("y")) {
                subscriptionType = true;
            } else {
                subscriptionType = false;
            }
            if (email.isEmpty()) {
                System.out.println("Please enter an email id");
                return;
            }
            if (!email.contains("@")) {
                // Throwing custom exception
                throw new InvalidEmailException(email + "is not a valid email ID.");
            }
            if (username.isEmpty()) {
                System.out.println("Please enter a username");
                return;
            }
            if (password.isEmpty()) {
                System.out.println("Please enter a password");
                return;
            }
            if (confirmPassword.isEmpty()) {
                System.out.println("Please confirm password by re-entering your password");
                return;
            }
            if (!password.equals(confirmPassword)) {
                System.out.println("Password and confirm password does not match");
                return;
            }
            String insertUserQuery = "insert into User (username, email, password_hash, subscription_type) values (?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertUserQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.setBoolean(4, subscriptionType);
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("User registered successfully!");
            }
        } catch (InvalidEmailException e) { // Catching custom exception
            System.err.println("ERROR: " + e.getMessage());
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000") || e.getErrorCode() == 1062) {
                System.out.println("User registration failed: Email already exists.");
            } else {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void viewAccount(Connection connection) {
        try {
            System.out.println("--------------View Account Details--------------");
            String email = "", password = "";
            System.out.println("Enter your registered email id:");
            email = sc.next();
            System.out.println("Enter your password:");
            password = sc.next();
            if (email.isEmpty()) {
                System.out.println("Please enter the email id");
                return;
            }
            if (password.isEmpty()) {
                System.out.println("Please enter the password");
                return;
            }
            String selectUserQuery = "select * from User where email = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserQuery);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password_hash");
                if (hashedPassword.equals(password)) {
                    String username = resultSet.getString("username");
                    String emailId = resultSet.getString("email");
                    boolean subscriptionType = resultSet.getBoolean("subscription_type");
                    System.out.println("----------------ACCOUNT DETAILS----------------");
                    System.out.println("Username: " + username);
                    System.out.println("Email: " + emailId);
                    if (subscriptionType) {
                        System.out.println("Subscription Type: Premium");
                    } else {
                        System.out.println("Subscription Type: Normal");
                    }
                } else {
                    System.out.println("Password is wrong.");
                }
            } else {
                // Throwing Custom Exception
                throw new UserNotFoundException("user with email id: " + email + " not found.");
            }
        } catch (UserNotFoundException e) // Catching Custom Exception
        {
            System.err.println("ERROR: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateAccount(Connection connection) {
        try {
            System.out.println("--------------Update Account Details--------------");
            String email = "", password = "";
            System.out.println("Enter your registered email id:");
            email = sc.next();
            System.out.println("Enter your password:");
            password = sc.next();
            if (email.isEmpty()) {
                System.out.println("Please enter the email id");
                return;
            }
            if (password.isEmpty()) {
                System.out.println("Please enter the password");
                return;
            }
            String selectUserQuery = "select * from User where email = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserQuery);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password_hash");
                if (hashedPassword.equals(password)) {
                    int id = resultSet.getInt("user_id");
                    String username = resultSet.getString("username");
                    String emailId = resultSet.getString("email");
                    boolean subscriptionType = resultSet.getBoolean("subscription_type");
                    System.out.println("----------------CURRENT ACCOUNT DETAILS----------------");
                    System.out.println("Username: " + username);
                    System.out.println("Email: " + emailId);
                    if (subscriptionType) {
                        System.out.println("Subscription Type: Premium");
                    } else {
                        System.out.println("Subscription Type: Normal");
                    }
                    System.out.println("-------------------------------------------------------");
                    String newUsername = "", newPassword = "";
                    boolean newSubscriptionType = false, subscriptionTypeChanged = false;
                    System.out.println("Enter your new username:");
                    newUsername = sc.next();
                    System.out.println("Enter your new password:");
                    newPassword = sc.next();
                    System.out.println("Change subscription type. For Premium Subscription enter \"Yes\", for Normal Subscription enter \"No\":");
                    String s = sc.next();
                    if (s.toLowerCase().equals("yes") || s.toLowerCase().equals("y")) {
                        newSubscriptionType = true;
                        subscriptionTypeChanged = true;
                    } else if (s.toLowerCase().equals("no") || s.toLowerCase().equals("n")) {
                        newSubscriptionType = false;
                        subscriptionTypeChanged = true;
                    }
                    String updateUserQuery = "update User set username = ?, password_hash = ?, subscription_type = ? where user_id = ?;";
                    preparedStatement = connection.prepareStatement(updateUserQuery);
                    if (newUsername.equals("")) {
                        preparedStatement.setString(1, username);
                    } else {
                        preparedStatement.setString(1, newUsername);
                    }
                    if (newPassword.equals("")) {
                        preparedStatement.setString(2, password);
                    } else {
                        preparedStatement.setString(2, newPassword);
                    }
                    if (subscriptionTypeChanged) {
                        preparedStatement.setBoolean(3, newSubscriptionType);
                    } else {
                        preparedStatement.setBoolean(1, subscriptionType);
                    }
                    preparedStatement.setInt(4, id);
                    int rowsUpdated = preparedStatement.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Account details updated successfully.");
                    } else {
                        System.out.println("Failed to update account details.");
                    }
                } else {
                    System.out.println("Password is wrong.");
                }
            } else {
                // Throwing Custom Exception
                throw new UserNotFoundException("user with email id: " + email + " not found.");
            }
        } catch (UserNotFoundException e) // Catching Custom Exception
        {
            System.err.println("ERROR: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteAccount(Connection connection) {
        try {
            System.out.println("--------------Delete Account--------------");
            String email = "", password = "";
            System.out.println("Enter your registered email id:");
            email = sc.next();
            System.out.println("Enter your password:");
            password = sc.next();
            if (email.isEmpty()) {
                System.out.println("Please enter the email id");
                return;
            }
            if (password.isEmpty()) {
                System.out.println("Please enter the password");
                return;
            }
            String selectUserQuery = "select user_id, password_hash from User where email = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserQuery);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password_hash");
                if (hashedPassword.equals(password)) {
                    int id = resultSet.getInt("user_id");
                    String deleteQuery = "delete from User where user_id = ?;";
                    preparedStatement = connection.prepareStatement(deleteQuery);
                    preparedStatement.setInt(1, id);
                    int rowsDeleted = preparedStatement.executeUpdate();
                    if (rowsDeleted > 0) {
                        System.out.println("Account deleted.");
                    } else {
                        System.out.println("Failed to delete the account.");
                    }
                } else {
                    System.out.println("Password is wrong.");
                }
            } else {
                System.out.println("No user found with email id: " + email);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addSong(Connection connection) {
        try {
            System.out.println("--------------Add A New Song--------------");
            String title = "", artist = "", genre = "";
            int duration = 0;
            System.out.println("Enter song duration:");
            duration = sc.nextInt();
            sc.nextLine();
            System.out.println("Enter song title:");
            title = sc.nextLine();
            System.out.println("Enter artist name:");
            artist = sc.nextLine();
            System.out.println("Enter song genre:");
            genre = sc.nextLine();
            if (title.isEmpty()) {
                System.out.println("Please enter song title");
                return;
            }
            if (artist.isEmpty()) {
                System.out.println("Please enter artist name");
                return;
            }
            if (duration == 0) {
                System.out.println("Please enter song duration");
                return;
            }
            if (genre.isEmpty()) {
                System.out.println("Please enter genre of the song");
                return;
            }
            String insertSongQuery = "insert into Song (title, artist, duration, genre) values (?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(insertSongQuery);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, artist);
            preparedStatement.setInt(3, duration);
            preparedStatement.setString(4, genre);
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Song added successfully!");
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000") || e.getErrorCode() == 1062) {
                System.out.println("Song addition failed: Song already exists.");
            } else {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void viewSongDetails(Connection connection) {
        try {
            System.out.println("--------------View Song Details--------------");
            // Clear the scanner buffer
            if (sc.hasNextLine()) {
                sc.nextLine();
            }
            String title = "";
            System.out.println("Enter song title:");
            title = sc.nextLine();
            if (title.isEmpty()) {
                System.out.println("Please enter song title");
                return;
            }
            String selectSongQuery = "select * from Song where title = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(selectSongQuery);
            preparedStatement.setString(1, title);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String songTitle = resultSet.getString("title");
                String songArtist = resultSet.getString("artist");
                int songDuration = resultSet.getInt("duration");
                String songGenre = resultSet.getString("genre");
                System.out.println("--------------SONG DETAILS--------------");
                System.out.println("----------------------------------------");
                System.out.println("Title: " + songTitle);
                System.out.println("Artist: " + songArtist);
                System.out.println("Duration: " + songDuration + " minutes");
                System.out.println("Genre: " + songGenre);
                System.out.println("----------------------------------------");
            } else {
                System.out.println("No song found with these details.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateSongInformation(Connection connection) {
        try {
            System.out.println("--------------Update Song Information--------------");
            // Clear the scanner buffer
            if (sc.hasNextLine()) {
                sc.nextLine();
            }
            String title = "", artist = "", genre = "";
            int duration = 0;
            System.out.println("Enter song details to search the song.");
            System.out.println("Enter song duration:");
            duration = sc.nextInt();
            sc.nextLine();
            System.out.println("Enter song title:");
            title = sc.nextLine();
            System.out.println("Enter artist name:");
            artist = sc.nextLine();
            System.out.println("Enter song genre:");
            genre = sc.nextLine();
            if (title.isEmpty()) {
                System.out.println("Please enter song title");
                return;
            }
            if (artist.isEmpty()) {
                System.out.println("Please enter artist name");
                return;
            }
            if (duration == 0) {
                System.out.println("Please enter song duration");
                return;
            }
            if (genre.isEmpty()) {
                System.out.println("Please enter genre of the song");
                return;
            }
            String selectUserQuery = "select * from Song where title = ? and artist = ? and duration = ? and genre = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserQuery);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, artist);
            preparedStatement.setInt(3, duration);
            preparedStatement.setString(4, genre);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("song_id");
                String songTitle = resultSet.getString("title");
                String songArtist = resultSet.getString("artist");
                int songDuration = resultSet.getInt("duration");
                String songGenre = resultSet.getString("genre");
                System.out.println("----------------CURRENT SONG DETAILS----------------");
                System.out.println("Title: " + songTitle);
                System.out.println("Artist: " + songArtist);
                System.out.println("Duration: " + songDuration + " minutes");
                System.out.println("Genre: " + songGenre);
                System.out.println("-------------------------------------------------------");
                String newTitle = "", newArtist = "", newGenre = "";
                int newDuration = 0;
                System.out.println("Enter Duration:");
                newDuration = sc.nextInt();
                sc.nextLine();
                System.out.println("Enter title:");
                newTitle = sc.nextLine();
                System.out.println("Enter artist:");
                newArtist = sc.nextLine();
                System.out.println("Enter genre:");
                newGenre = sc.nextLine();
                String updateUserQuery = "update Song set title = ?, artist = ?, duration = ?, genre = ? where song_id = ?;";
                preparedStatement = connection.prepareStatement(updateUserQuery);
                if (newTitle.equals("")) {
                    preparedStatement.setString(1, title);
                } else {
                    preparedStatement.setString(1, newTitle);
                }
                if (newArtist.equals("")) {
                    preparedStatement.setString(2, artist);
                } else {
                    preparedStatement.setString(2, newArtist);
                }
                if (newDuration == 0) {
                    preparedStatement.setInt(3, duration);
                } else {
                    preparedStatement.setInt(3, newDuration);
                }
                if (newGenre.equals("")) {
                    preparedStatement.setString(4, genre);
                } else {
                    preparedStatement.setString(4, newGenre);
                }
                preparedStatement.setInt(5, id);
                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Song information updated successfully.");
                } else {
                    System.out.println("Failed to update song information.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteSong(Connection connection) {
        try {
            System.out.println("--------------Delete A Song--------------");
            // Clear the scanner buffer
            if (sc.hasNextLine()) {
                sc.nextLine();
            }
            String title = "", artist = "", genre = "";
            int duration = 0;
            System.out.println("Enter song details to search the song.");
            System.out.println("Enter song duration:");
            duration = sc.nextInt();
            sc.nextLine();
            System.out.println("Enter song title:");
            title = sc.nextLine();
            System.out.println("Enter artist name:");
            artist = sc.nextLine();
            System.out.println("Enter song genre:");
            genre = sc.nextLine();
            if (title.isEmpty()) {
                System.out.println("Please enter song title");
                return;
            }
            if (artist.isEmpty()) {
                System.out.println("Please enter artist name");
                return;
            }
            if (duration == 0) {
                System.out.println("Please enter song duration");
                return;
            }
            if (genre.isEmpty()) {
                System.out.println("Please enter genre of the song");
                return;
            }
            String selectUserQuery = "select * from Song where title = ? and artist = ? and duration = ? and genre = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserQuery);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, artist);
            preparedStatement.setInt(3, duration);
            preparedStatement.setString(4, genre);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("song_id");
                String deleteQuery = "delete from Song where song_id = ?;";
                preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.setInt(1, id);
                int rowsDeleted = preparedStatement.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Song deleted.");
                } else {
                    System.out.println("Failed to delete the song.");
                }
            } else {
                System.out.println("No song found with these details.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createPlaylist(Connection connection) {
        try {
            System.out.println("--------------Create Playlist--------------");
            String email = "", password = "";
            System.out.println("Enter your email id:");
            email = sc.next();
            System.out.println("Enter your password:");
            password = sc.next();
            if (email.isEmpty()) {
                System.out.println("Please enter the email id");
                return;
            }
            if (password.isEmpty()) {
                System.out.println("Please enter the password");
                return;
            }
            String selectUserQuery = "select * from User where email = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserQuery);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password_hash");
                if (hashedPassword.equals(password)) {
                    int creatorId = resultSet.getInt("user_id");
                    // Clear the scanner buffer
                    if (sc.hasNextLine()) {
                        sc.nextLine();
                    }
                    String name = "";
                    System.out.println("Enter playlist name:");
                    name = sc.nextLine();
                    if (name.isEmpty()) {
                        System.out.println("Please enter a playlist name");
                        return;
                    }
                    String insertPlaylistQuery = "insert into Playlist (name, creator_id, creation_date) values (?, ?, now());";
                    preparedStatement = connection.prepareStatement(insertPlaylistQuery);
                    preparedStatement.setString(1, name);
                    preparedStatement.setInt(2, creatorId);
                    int rowsInserted = preparedStatement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("Playlist created successfully!");
                    }
                } else {
                    System.out.println("Password is wrong.");
                }
            } else {
                System.out.println("No user found with email id: " + email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void viewPlaylist(Connection connection) {
        try {
            System.out.println("--------------View Playlist--------------");
            String email = "", password = "";
            System.out.println("Enter your email id:");
            email = sc.next();
            System.out.println("Enter your password:");
            password = sc.next();
            if (email.isEmpty()) {
                System.out.println("Please enter the email id");
                return;
            }
            if (password.isEmpty()) {
                System.out.println("Please enter the password");
                return;
            }
            String selectUserQuery = "select * from User where email = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserQuery);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password_hash");
                if (hashedPassword.equals(password)) {
                    int creatorId = resultSet.getInt("user_id");
                    // Clear the scanner buffer
                    if (sc.hasNextLine()) {
                        sc.nextLine();
                    }
                    String name = "";
                    System.out.println("Enter playlist name:");
                    name = sc.nextLine();
                    if (name.isEmpty()) {
                        System.out.println("Please enter a playlist name");
                        return;
                    }
                    String insertPlaylistQuery = "select * from Playlist where name = ? and creator_id = ?;";
                    preparedStatement = connection.prepareStatement(insertPlaylistQuery);
                    preparedStatement.setString(1, name);
                    preparedStatement.setInt(2, creatorId);
                    resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        String playlistName = resultSet.getString("name");
                        String playlistCreationDate = resultSet.getString("creation_date");
                        System.out.println("----------------PLAYLIST DETAILS----------------");
                        System.out.println("Name: " + playlistName);
                        System.out.println("Creation Date: " + playlistCreationDate);
                    } else {
                        System.out.println("No playlist found with the above details.");
                    }
                } else {
                    System.out.println("Password is wrong.");
                }
            } else {
                System.out.println("No user found with email id: " + email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void updatePlaylistInformation(Connection connection) {
        try {
            System.out.println("--------------Update Playlist Information--------------");

            System.out.println("Enter your email id:");
            String email = sc.next();
            System.out.println("Enter your password:");
            String password = sc.next();

            if (email.isEmpty()) {
                System.out.println("Please enter the email id");
                return;
            }
            if (password.isEmpty()) {
                System.out.println("Please enter the password");
                return;
            }

            String selectUserQuery = "SELECT * FROM User WHERE email = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserQuery);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password_hash");
                int uid = resultSet.getInt("user_id");

                if (hashedPassword.equals(password)) {
                    sc.nextLine(); // Clear the scanner buffer after using `next()`
                    System.out.println("Enter the playlist name:");
                    String name = sc.nextLine();

                    if (name.isEmpty()) {
                        System.out.println("Please enter the playlist name");
                        return;
                    }

                    String getPlaylistQuery = "SELECT * FROM Playlist WHERE creator_id = ? AND name = ?;";
                    preparedStatement = connection.prepareStatement(getPlaylistQuery);
                    preparedStatement.setInt(1, uid);
                    preparedStatement.setString(2, name);
                    resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        // Retrieve playlist details
                        String playlistName = resultSet.getString("name");
                        String playlistCreationDate = resultSet.getString("creation_date");
                        int pid = resultSet.getInt("playlist_id");

                        System.out.println("----------------CURRENT PLAYLIST DETAILS----------------");
                        System.out.println("Name: " + playlistName);
                        System.out.println("Creation Date: " + playlistCreationDate);
                        System.out.println("-------------------------------------------------------");

                        System.out.println("Enter new playlist name:");
                        String newPlaylistName = sc.nextLine();

                        if (newPlaylistName.isEmpty()) {
                            System.out.println("Please enter the playlist name");
                            return;
                        }

                        String updatePlaylistQuery = "UPDATE Playlist SET name = ? WHERE playlist_id = ?;";
                        preparedStatement = connection.prepareStatement(updatePlaylistQuery);
                        preparedStatement.setString(1, newPlaylistName);
                        preparedStatement.setInt(2, pid);
                        int rowsUpdated = preparedStatement.executeUpdate();

                        if (rowsUpdated > 0) {
                            System.out.println("Playlist updated successfully!");
                        } else {
                            System.out.println("Failed to update playlist information.");
                        }
                    } else {
                        System.out.println("Playlist not found.");
                    }
                } else {
                    System.out.println("Password is incorrect.");
                }
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void deletePlaylist(Connection connection) {
        try {
            System.out.println("--------------Delete Playlist--------------");
            String email = "", password = "";
            System.out.println("Enter your email id:");
            email = sc.next();
            System.out.println("Enter your password:");
            password = sc.next();
            if (email.isEmpty()) {
                System.out.println("Please enter the email id");
                return;
            }
            if (password.isEmpty()) {
                System.out.println("Please enter the password");
                return;
            }
            String selectUserQuery = "select * from User where email = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserQuery);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password_hash");
                if (hashedPassword.equals(password)) {
                    int creatorId = resultSet.getInt("user_id");
                    // Clear the scanner buffer
                    if (sc.hasNextLine()) {
                        sc.nextLine();
                    }
                    String name = "";
                    System.out.println("Enter playlist name:");
                    name = sc.nextLine();
                    if (name.isEmpty()) {
                        System.out.println("Please enter a playlist name");
                        return;
                    }
                    String deletePlaylistQuery = "delete from Playlist where name = ? and creator_id = ?;";
                    preparedStatement = connection.prepareStatement(deletePlaylistQuery);
                    preparedStatement.setString(1, name);
                    preparedStatement.setInt(2, creatorId);
                    int rowsDeleted = preparedStatement.executeUpdate();
                    if (rowsDeleted > 0) {
                        System.out.println("Playlist deleted.");
                    } else {
                        System.out.println("Failed to delete the playlist.");
                    }
                } else {
                    System.out.println("Password is wrong.");
                }
            } else {
                System.out.println("No user found with email id: " + email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void performTask(Connection connection) {
        switch (currentIndex) {
            case 0:
                System.out.println("Exit");
                break;
            case 1:
                registerUser(connection);
                break;
            case 2:
                viewAccount(connection);
                break;
            case 3:
                updateAccount(connection);
                break;
            case 4:
                deleteAccount(connection);
                break;
            case 5:
                addSong(connection);
                break;
            case 6:
                viewSongDetails(connection);
                break;
            case 7:
                updateSongInformation(connection);
                break;
            case 8:
                deleteSong(connection);
                break;
            case 9:
                createPlaylist(connection);
                break;
            case 10:
                viewPlaylist(connection);
                break;
            case 11:
                updatePlaylistInformation(connection);
                break;
            case 12:
                deletePlaylist(connection);
                break;
            default:
                System.out.println("Please enter between 1 to 13");
        }
    }

    private static void printMenu() {
        System.out.println("---------------MENU---------------\n----------------------------------\n0 --> Exit\n1 --> Register\n2 --> View Account Details\n3 --> Update Account Details\n4 --> Delete Account\n----------------------------------\n5 --> Add New Song\n6 --> View Song Details\n7 --> Update Song Information\n8 --> Delete A Song\n----------------------------------\n9 --> Create A New Playlist\n10 --> View Playlist Details\n11 --> Update Playlist Information\n12 --> Delete A Playlist\n----------------------------------\nEnter the number according to which service you want:");
        currentIndex = sc.nextInt();
    }

    public static void main(String[] args) {
        try {
            Connection connection = DatabaseConnection.getConnection();
            Statement statement = connection.createStatement();
            String createUserTableQuery = "create table if not exists User (user_id int primary key auto_increment, username varchar(30) not null, email varchar(30) not null, password_hash varchar(255) not null, subscription_type bool not null, unique(email));";
            boolean userTableCreated = statement.execute(createUserTableQuery);
            if (userTableCreated) {
                System.out.println("User table is created.");
            }
            String createSongTableQuery = "create table if not exists Song (song_id int primary key auto_increment, title varchar(255) not null, artist varchar(255) not null, duration int not null, genre varchar(255) not null, unique(title, artist, duration, genre));";
            boolean songTableCreated = statement.execute(createSongTableQuery);
            if (songTableCreated) {
                System.out.println("Song table is created.");
            }
            String createPlaylistTableQuery = "create table if not exists Playlist (playlist_id int primary key auto_increment, name varchar(255) not null, creator_id int not null, creation_date date not null, foreign key (creator_id) references User(user_id), unique(name, creator_id));";
            boolean playlistTableCreated = statement.execute(createPlaylistTableQuery);
            if (playlistTableCreated) {
                System.out.println("Playlist table is created.");
            }
            String createPlaylist_songTableQuery = "create table if not exists Playlist_Song (playlist_id int not null, song_id int not null, PRIMARY KEY (playlist_id, song_id), foreign key (playlist_id) references Playlist(playlist_id) on delete cascade, foreign key (song_id) references Song(song_id) on delete cascade, unique(playlist_id, song_id));";
            boolean playlist_songTableCreated = statement.execute(createPlaylist_songTableQuery);
            if (playlist_songTableCreated) {
                System.out.println("Playlist_Song table is created.");
            }

            System.out.println("***************--Welcome to Music Streaming Service--***************");

            while (true) {
                printMenu();
                if (currentIndex == 0) {
                    break;
                }
                performTask(connection);
            }
        } catch (Exception e) {
            // Handling exception
            e.printStackTrace();
        }
    }
}
