package BankaSistemi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    static final String DB_URL = "jdbc:mysql://localhost:3306/";
    static final String USER = "root";
    static final String PASS = "123456";
    static final String DB_NAME = "banka_db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
    }

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            
            String sql = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
            stmt.executeUpdate(sql);
            System.out.println("Veritabanı oluşturuldu veya zaten var.");

            try (Connection dbConn = DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
                 Statement dbStmt = dbConn.createStatement()) {

                String createKullaniciTable = "CREATE TABLE IF NOT EXISTS kullanicilar (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "type VARCHAR(20), " +
                        "adSoyad VARCHAR(100), " +
                        "sifre INT, " +
                        "tcno INT UNIQUE, " +
                        "bakiye FLOAT, " +
                        "gelir FLOAT, " +
                        "kredipuani INT, " +
                        "cinsiyet VARCHAR(10), " +
                        "davetkod INT, " +
                        "musterino INT UNIQUE" +
                        ")";
                dbStmt.executeUpdate(createKullaniciTable);

                String createKrediTable = "CREATE TABLE IF NOT EXISTS krediler (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "tcno INT, " +
                        "miktar FLOAT, " +
                        "kredituru VARCHAR(50), " +
                        "vadesayi INT, " +
                        "FOREIGN KEY (tcno) REFERENCES kullanicilar(tcno) ON DELETE CASCADE" +
                        ")";
                dbStmt.executeUpdate(createKrediTable);

                String checkCalisan = "SELECT COUNT(*) FROM kullanicilar WHERE type = 'Calisan'";
                try (java.sql.ResultSet rs = dbStmt.executeQuery(checkCalisan)) {
                    if (rs.next() && rs.getInt(1) == 0) {
                         String insertCalisan = "INSERT INTO kullanicilar (type, adSoyad, sifre, tcno, bakiye, gelir, kredipuani) VALUES " +
                                 "('Calisan', 'Varsayilan Bankaci', 123, 11111111, 0, 5000, 0)";
                         dbStmt.executeUpdate(insertCalisan);
                         System.out.println("Varsayılan çalışan eklendi.");
                    }
                }
                String checkAdmin = "SELECT COUNT(*) FROM kullanicilar WHERE type = 'Admin'";
                try (java.sql.ResultSet rs = dbStmt.executeQuery(checkAdmin)) {
                    if (rs.next() && rs.getInt(1) == 0) {

                        String insertAdmin = "INSERT INTO kullanicilar (type, adSoyad, sifre, tcno, bakiye, gelir, kredipuani) VALUES " +
                                "('Admin', 'PATRON', 12345, 12345, 0, 0, 0)";
                        dbStmt.executeUpdate(insertAdmin);
                         System.out.println("Varsayılan Admin eklendi.");
                    }
                }
                
                System.out.println("Tablolar oluşturuldu.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addMusteri(Musteri m) {
        String sql = "INSERT INTO kullanicilar (type, adSoyad, sifre, tcno, bakiye, gelir, kredipuani, cinsiyet, davetkod, musterino) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "Musteri");
            pstmt.setString(2, m.getAdSoyad());
            pstmt.setInt(3, m.getSifre());
            pstmt.setInt(4, m.tcno);
            pstmt.setFloat(5, m.bakiye);
            pstmt.setFloat(6, m.gelir);
            pstmt.setInt(7, m.kredipuani);
            pstmt.setString(8, m.cinsiyet);
            pstmt.setInt(9, m.davetkod);
            pstmt.setInt(10, m.musterino);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Kullanici login(int idOrTc, int password) {
        String sql = "SELECT * FROM kullanicilar WHERE (tcno = ? OR musterino = ?) AND sifre = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idOrTc);
            pstmt.setInt(2, idOrTc);
            pstmt.setInt(3, password);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("type");
                    String adSoyad = rs.getString("adSoyad");
                    int tcno = rs.getInt("tcno");
                    float bakiye = rs.getFloat("bakiye");
                    float gelir = rs.getFloat("gelir");
                    int kredipuani = rs.getInt("kredipuani");
                    
                    if ("Musteri".equals(type)) {
                        String cinsiyet = rs.getString("cinsiyet");
                        int davetkod = rs.getInt("davetkod");
                        int musterino = rs.getInt("musterino");
                        java.util.HashSet<Kredi> krediler = getKrediler(tcno);
                        return new Musteri(adSoyad, password, tcno, bakiye, cinsiyet, gelir, davetkod, musterino, krediler, kredipuani);
                    } else if ("Calisan".equals(type)) {
                         return new Calisan(adSoyad, password, tcno, bakiye, gelir, new java.util.HashSet<>(), kredipuani);
                    } else if ("Admin".equals(type)) {
                         return new Admin(adSoyad, password, tcno, bakiye, gelir, new java.util.HashSet<>(), kredipuani);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }
    
    public static void updateBakiye(int tcno, float amount) {
         String sql = "UPDATE kullanicilar SET bakiye = bakiye + ? WHERE tcno = ?";
         try (Connection conn = getConnection();
              java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setFloat(1, amount);
             pstmt.setInt(2, tcno);
             pstmt.executeUpdate();
         } catch (SQLException e) {
             e.printStackTrace();
         }
    }

    public static java.util.HashSet<Kredi> getKrediler(int tcno) {
        java.util.HashSet<Kredi> list = new java.util.HashSet<>();
        String sql = "SELECT * FROM krediler WHERE tcno = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tcno);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    float miktar = rs.getFloat("miktar");
                    String tur = rs.getString("kredituru");
                    int vade = rs.getInt("vadesayi");
                    list.add(new Kredi(miktar, tur, vade));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addKredi(Kredi k, int musteriTc) {
        String sql = "INSERT INTO krediler (tcno, miktar, kredituru, vadesayi) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, musteriTc);
            pstmt.setFloat(2, k.miktar);
            pstmt.setString(3, k.kredituru);
            pstmt.setInt(4, k.vadesayi);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getRandomCalisanTc() {
        java.util.List<Integer> tcs = new java.util.ArrayList<>();
        String sql = "SELECT tcno FROM kullanicilar WHERE type = 'Calisan'";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tcs.add(rs.getInt("tcno"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (tcs.isEmpty()) return -1;
        
        java.util.Random rand = new java.util.Random();
        return tcs.get(rand.nextInt(tcs.size()));
    }

    public static void updateCalisanBonus(int calisanTc, int puanBonus, float gelirBonus) {
        String sql = "UPDATE kullanicilar SET kredipuani = kredipuani + ?, gelir = gelir + ? WHERE tcno = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, puanBonus);
            pstmt.setFloat(2, gelirBonus);
            pstmt.setInt(3, calisanTc);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String transferMoney(int senderTc, int receiverMusteriNo, float amount) {
        if (amount <= 0) return "Geçersiz miktar.";
        
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); 
            float senderBalance = 0;
            String checkSender = "SELECT bakiye FROM kullanicilar WHERE tcno = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(checkSender)) {
                ps.setInt(1, senderTc);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) senderBalance = rs.getFloat("bakiye");
                    else return "Gönderen hesap bulunamadı.";
                }
            }
            
            if (senderBalance < amount) return "Yetersiz bakiye.";

            int receiverTc = -1;
            String checkReceiver = "SELECT tcno FROM kullanicilar WHERE musterino = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(checkReceiver)) {
                ps.setInt(1, receiverMusteriNo);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) receiverTc = rs.getInt("tcno");
                    else return "Alıcı müşteri numarası bulunamadı.";
                }
            }
            
            if (receiverTc == senderTc) return "Kendinize para gönderemezsiniz.";

            String deduct = "UPDATE kullanicilar SET bakiye = bakiye - ? WHERE tcno = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(deduct)) {
                ps.setFloat(1, amount);
                ps.setInt(2, senderTc);
                ps.executeUpdate();
            }
            
            String add = "UPDATE kullanicilar SET bakiye = bakiye + ? WHERE tcno = ?";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(add)) {
                ps.setFloat(1, amount);
                ps.setInt(2, receiverTc);
                ps.executeUpdate();
            }
            
            conn.commit(); 
            return "SUCCESS";
            
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return "İşlem sırasında veritabanı hatası: " + e.getMessage();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public static boolean deleteUser(int tcno) {
        String sql = "DELETE FROM kullanicilar WHERE tcno = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tcno);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addCalisan(String adSoyad, int tcno, float maas) {
        String sql = "INSERT INTO kullanicilar (type, adSoyad, sifre, tcno, bakiye, gelir, kredipuani) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "Calisan");
            pstmt.setString(2, adSoyad);
            pstmt.setInt(3, 123); 
            pstmt.setInt(4, tcno);
            pstmt.setFloat(5, 0);
            pstmt.setFloat(6, maas);
            pstmt.setInt(7, 0); 
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static java.util.List<Kullanici> getAllUsers(String userType) {
        java.util.List<Kullanici> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM kullanicilar WHERE type = ?";
        try (Connection conn = getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userType);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String adSoyad = rs.getString("adSoyad");
                    int tcno = rs.getInt("tcno");
                    float bakiye = rs.getFloat("bakiye");
                    float gelir = rs.getFloat("gelir");
                    int sifre = rs.getInt("sifre");
                    int kredipuani = rs.getInt("kredipuani");
                    
                    if ("Musteri".equals(userType)) {
                         String cinsiyet = rs.getString("cinsiyet");
                         int davetkod = rs.getInt("davetkod");
                         int musterino = rs.getInt("musterino");
                         list.add(new Musteri(adSoyad, sifre, tcno, bakiye, cinsiyet, gelir, davetkod, musterino, new java.util.HashSet<>(), kredipuani));
                    } else if ("Calisan".equals(userType)) {
                         list.add(new Calisan(adSoyad, sifre, tcno, bakiye, gelir, new java.util.HashSet<>(), kredipuani));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static float getTotalBakiye() {
        float total = 0;
        String sql = "SELECT SUM(bakiye) FROM kullanicilar WHERE type = 'Musteri'";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                total = rs.getFloat(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
}
