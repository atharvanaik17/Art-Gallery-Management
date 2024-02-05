
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ArtGalleryManagementSystem1 {
    private static final String DB_URL = "jdbc:mysql://localhost/art_gallery";
    private static final String DB_USER = "enter your userid";
    private static final String DB_PASSWORD = "enter your password";

    private static Connection dbConnection;

    public static void main(String[] args) {
        try {
            dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            SwingUtilities.invokeLater(() -> createAndShowLoginGUI());
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void createAndShowLoginGUI() {
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 150);
        loginFrame.setLayout(new BorderLayout());
        loginFrame.setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("username:");
        JLabel passwordLabel = new JLabel("Password:");
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);

                if (authenticateUser(username, password)) {
                    loginFrame.dispose();
                    createAndShowGUI();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password");
                }
            }
        });

        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel()); // Empty label for spacing
        loginPanel.add(loginButton);

        loginFrame.add(loginPanel, BorderLayout.CENTER);
        loginFrame.setVisible(true);
    }

   private static boolean authenticateUser(String username, String password) {
     
     try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)){
        return true;

    } 
    
    catch (SQLException e) {
        System.err.println("SQL Error: " + e.getMessage()); // Add this line for error details
        e.printStackTrace();
        return false;
    }
 
}


    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Art Gallery Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Add Artwork", createAddArtworkPanel());
        tabbedPane.addTab("View Artworks", createViewArtworksPanel());
        tabbedPane.addTab("Delete Artwork", createDeleteArtworkPanel());
        tabbedPane.addTab("Update Artwork", createUpdateArtworkPanel());

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static JPanel createAddArtworkPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        JTextField titleField = new JTextField(20);
        JTextField artistNameField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JButton addButton = new JButton("Add Artwork");

        addButton.addActionListener(e -> {
            String title = titleField.getText();
            String artistName = artistNameField.getText();
            double price = Double.parseDouble(priceField.getText());
            addArtwork(title, artistName, price);
        });

        panel.add(new JLabel("Artwork Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Artist Name:"));
        panel.add(artistNameField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(addButton);

        return panel;
    }

   private static JPanel createViewArtworksPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    JTextArea artworksTextArea = new JTextArea();
    artworksTextArea.setEditable(false);

    JButton viewButton = new JButton("REFRESH and View Artworks");
    viewButton.addActionListener(e -> {
        displayArtworks(artworksTextArea);
    });

    panel.add(new JScrollPane(artworksTextArea), BorderLayout.CENTER);
    panel.add(viewButton, BorderLayout.NORTH);

    return panel;
}

private static void displayArtworks(JTextArea artworksTextArea) {
    artworksTextArea.setText(""); // Clear the text area

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
        String selectSQL = "SELECT a.artwork_title, a.artist_id, b.artist_name, a.price FROM artworks a JOIN artists b ON a.artist_id = b.artist_id";
        try (PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
            ResultSet rs = stmt.executeQuery();
            artworksTextArea.append("Artworks:\n");
            artworksTextArea.append("Title\t\tArtist_ID\t\tArtist\t\tPrice\n");
            while (rs.next()) {
                String title = rs.getString("artwork_title");
                String artist = rs.getString("artist_name");
                double price = rs.getDouble("price");
                double Id = rs.getDouble("artist_id");
                artworksTextArea.append(title + "\t\t" + Id + "\t\t" + artist + "\t\t" + price + "\n");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }
}


    private static JPanel createDeleteArtworkPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JTextField titleField = new JTextField(20);
        JButton deleteButton = new JButton("Delete Artwork");

        deleteButton.addActionListener(e -> {
            String title = titleField.getText();
            deleteArtwork(title);
        });

        panel.add(new JLabel("Artwork Title:"));
        panel.add(titleField);
        panel.add(deleteButton);

        return panel;
    }

    private static JPanel createUpdateArtworkPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2));
        JTextField titleField = new JTextField(20);
        JTextField newTitleField = new JTextField(20);
        JTextField newArtistNameField = new JTextField(20);
        JTextField newPriceField = new JTextField(20);
        JButton updateButton = new JButton("Update Artwork");

        updateButton.addActionListener(e -> {
            String title = titleField.getText();
            String newTitle = newTitleField.getText();
            String newArtistName = newArtistNameField.getText();
            double newPrice = Double.parseDouble(newPriceField.getText());
            updateArtwork(title, newTitle, newArtistName, newPrice);
        });

        panel.add(new JLabel("Artwork Title to Update:"));
        panel.add(titleField);
        panel.add(new JLabel("New Artwork Title :"));
        panel.add(newTitleField);
        panel.add(new JLabel("New Artist Name :"));
        panel.add(newArtistNameField);
        panel.add(new JLabel("New Price:"));
        panel.add(newPriceField);
        panel.add(updateButton);

        return panel;
    }

    private static void addArtwork(String title, String artistName, double price) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            int artistId = getOrCreateArtist(conn, artistName);
            String insertSQL = "INSERT INTO artworks (artwork_title, artist_id, price) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                stmt.setString(1, title);
                stmt.setInt(2, artistId);
                stmt.setDouble(3, price);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Artwork added successfully");
                } else {
                    JOptionPane.showMessageDialog(null, "Artwork addition failed");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private static void deleteArtwork(String title) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String deleteSQL = "DELETE FROM artworks WHERE artwork_title = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
                stmt.setString(1, title);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Artwork deleted successfully");
                } else {
                    JOptionPane.showMessageDialog(null, "Artwork deletion failed. Artwork not found");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private static void updateArtwork(String title, String newTitle, String newArtistName, double newPrice) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            if (!artworkExists(conn, title)) {
                JOptionPane.showMessageDialog(null, "Artwork not found.");
                return;
            }
            int newArtistId = getOrCreateArtist(conn, newArtistName);
            String updateSQL = "UPDATE artworks SET artwork_title = ?, artist_id = ?, price = ? WHERE artwork_title = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
                stmt.setString(1, newTitle);
                stmt.setInt(2, newArtistId);
                stmt.setDouble(3, newPrice);
                stmt.setString(4, title);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Artwork updated successfully");
                } else {
                    JOptionPane.showMessageDialog(null, "Artwork update failed. Artwork not found");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    private static boolean artworkExists(Connection conn, String title) {
        String selectSQL = "SELECT 1 FROM artworks WHERE artwork_title = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
            stmt.setString(1, title);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static int getOrCreateArtist(Connection conn, String artistName) {
        String selectSQL = "SELECT artist_id FROM artists WHERE artist_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(selectSQL)) {
            stmt.setString(1, artistName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("artist_id");
            } else {
                String insertSQL = "INSERT INTO artists (artist_name) VALUES (?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setString(1, artistName);
                    int rowsAffected = insertStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1);
                        }
                    }
                }
                throw new SQLException("Failed to create artist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}


//java -cp /Users/atharva/Downloads/mysql-connector-j-8.1.0/mysql-connector-j-8.1.0.jar ArtGalleryManagementSystem1.java