package BankaSistemi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BankaGUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Kullanici currentUser;

    public BankaGUI() {
        setTitle("Banka Sistemi");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panels
        mainPanel.add(createLoginPanel(), "Login");
        
        add(mainPanel);
    }

    private ImageIcon loadLogo() {
        try {
            java.io.File imgFile = new java.io.File("logo.png");
            if (imgFile.exists()) {
                 ImageIcon icon = new ImageIcon("logo.png");
                 Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                 return new ImageIcon(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JPanel createLoginPanel() {
        JPanel container = new JPanel(new BorderLayout());
        
        ImageIcon logo = loadLogo();
        if (logo != null) {
            JLabel logoLabel = new JLabel(logo);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            container.add(logoLabel, BorderLayout.NORTH);
            setIconImage(logo.getImage()); // Set window icon too
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

        JLabel userLabel = new JLabel("Müşteri No / TC:");
        JTextField userField = new JTextField();

        JLabel passLabel = new JLabel("Şifre:");
        JPasswordField passField = new JPasswordField();

        JButton loginButton = new JButton("Giriş Yap");
        JButton registerButton = new JButton("Kayıt Ol");

        loginButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(userField.getText());
                int pass = Integer.parseInt(new String(passField.getPassword()));
                
                Kullanici user = DatabaseManager.login(id, pass);
                if (user != null) {
                    currentUser = user;
                    JOptionPane.showMessageDialog(this, "Hoşgeldiniz " + user.getAdSoyad());
                    
                    if (user instanceof Admin) {
                        mainPanel.add(createAdminDashboardPanel(), "AdminDashboard");
                        cardLayout.show(mainPanel, "AdminDashboard");
                    } else if (user instanceof Calisan) {
                        mainPanel.add(createCalisanDashboardPanel(), "CalisanDashboard");
                        cardLayout.show(mainPanel, "CalisanDashboard");
                    } else {
                        mainPanel.add(createDashboardPanel(), "Dashboard");
                        cardLayout.show(mainPanel, "Dashboard");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Hatalı Giriş!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lütfen sayısal değerler giriniz.");
            }
        });

        registerButton.addActionListener(e -> {
             mainPanel.add(createRegisterPanel(), "Register");
             cardLayout.show(mainPanel, "Register");
        });

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(new JLabel(""));
        panel.add(loginButton);
        panel.add(new JLabel("")); 
        panel.add(registerButton);

        container.add(panel, BorderLayout.CENTER);
        return container;
    }
    
    private JPanel createAdminDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("ADMİN PANELİ - Hoşgeldiniz PATRON");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(welcomeLabel, BorderLayout.NORTH);
        
        JPanel grid = new JPanel(new GridLayout(4, 1, 10, 10));
        grid.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JButton listMusteriBtn = new JButton("Müşterileri Listele");
        JButton listCalisanBtn = new JButton("Çalışanları Listele / Finans");
        JButton hireFireBtn = new JButton("Personel İşlemleri");
        JButton logoutBtn = new JButton("Çıkış Yap");
        
        listMusteriBtn.addActionListener(e -> {
             // Fetch all Musteri
             StringBuilder sb = new StringBuilder("--- MÜŞTERİLER ---\n");
             java.util.List<Kullanici> users = DatabaseManager.getAllUsers("Musteri");
             for(Kullanici u : users) {
                 if(u instanceof Musteri) {
                     Musteri m = (Musteri) u;
                     sb.append("Ad: ").append(m.getAdSoyad())
                       .append(" | TC: ").append(m.tcno)
                       .append(" | Bakiye: ").append(m.bakiye)
                       .append(" | Müşteri No: ").append(m.musterino).append("\n");
                 }
             }
             JTextArea area = new JTextArea(sb.toString());
             area.setEditable(false);
             JOptionPane.showMessageDialog(this, new JScrollPane(area), "Müşteri Listesi", JOptionPane.INFORMATION_MESSAGE);
        });
        
        listCalisanBtn.addActionListener(e -> {
             StringBuilder sb = new StringBuilder("--- ÇALIŞANLAR & FİNANS ---\n");
             java.util.List<Kullanici> users = DatabaseManager.getAllUsers("Calisan");
             float totalGelir = 0;
             for(Kullanici u : users) {
                 sb.append("Ad: ").append(u.getAdSoyad())
                   .append(" | TC: ").append(u.tcno)
                   .append(" | Maaş: ").append(u.gelir).append("\n");
                 totalGelir += u.gelir;
             }
             float bankaSermaye = DatabaseManager.getTotalBakiye();
             
             sb.append("\n\n--- BANKA DURUMU ---\n");
             sb.append("Toplam Müşteri Mevduatı (Sermaye): ").append(bankaSermaye).append("\n");
             sb.append("Toplam Personel Maaş Gideri: ").append(totalGelir).append("\n");
             
             JTextArea area = new JTextArea(sb.toString());
             area.setEditable(false);
             JOptionPane.showMessageDialog(this, new JScrollPane(area), "Çalışanlar & Finans", JOptionPane.INFORMATION_MESSAGE);
        });
        
        hireFireBtn.addActionListener(e -> {
            String[] options = {"Personel İşe Al", "Personel Çıkar"};
            int choice = JOptionPane.showOptionDialog(this, "İşlem Seçiniz", "Personel Yönetimi", 
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            
            if (choice == 0) {
                // Hire
                JTextField adField = new JTextField();
                JTextField tcField = new JTextField();
                JTextField maasField = new JTextField();
                Object[] mess = {
                    "Ad Soyad:", adField,
                    "TC No:", tcField,
                    "Maaş:", maasField
                };
                int opt = JOptionPane.showConfirmDialog(this, mess, "Personel Ekle", JOptionPane.OK_CANCEL_OPTION);
                if (opt == JOptionPane.OK_OPTION) {
                    try {
                        String ad = adField.getText();
                        int tc = Integer.parseInt(tcField.getText());
                        float maas = Float.parseFloat(maasField.getText());
                        if (DatabaseManager.addCalisan(ad, tc, maas)) {
                            JOptionPane.showMessageDialog(this, "Personel eklendi.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Hata (TC Çakışması vb).");
                        }
                    } catch(Exception ex) {
                         JOptionPane.showMessageDialog(this, "Hatalı girdi.");
                    }
                }
            } else if (choice == 1) {
                // Fire
                String tcStr = JOptionPane.showInputDialog(this, "Çıkarılacak Personel TC:");
                if (tcStr != null) {
                    try {
                        int tc = Integer.parseInt(tcStr);
                        if(DatabaseManager.deleteUser(tc)) {
                             JOptionPane.showMessageDialog(this, "Personel silindi.");
                        } else {
                             JOptionPane.showMessageDialog(this, "Kullanıcı bulunamadı.");
                        }
                    } catch(Exception ex) {
                        JOptionPane.showMessageDialog(this, "Geçersiz TC.");
                    }
                }
            }
        });
        
        logoutBtn.addActionListener(e -> {
             currentUser = null;
             cardLayout.show(mainPanel, "Login");
        });
        
        grid.add(listMusteriBtn);
        grid.add(listCalisanBtn);
        grid.add(hireFireBtn);
        grid.add(logoutBtn);
        
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField adSoyadField = new JTextField();
        JTextField tcField = new JTextField();
        JTextField passField = new JTextField();
        JTextField gelirField = new JTextField();
        JTextField cinsiyetField = new JTextField(); // Sadece musteri

        panel.add(new JLabel("Ad Soyad:")); panel.add(adSoyadField);
        panel.add(new JLabel("TC No:")); panel.add(tcField);
        panel.add(new JLabel("Şifre:")); panel.add(passField);
        panel.add(new JLabel("Aylık Gelir:")); panel.add(gelirField);
        panel.add(new JLabel("Cinsiyet:")); panel.add(cinsiyetField);
        
        JButton submitButton = new JButton("Kaydol");
        JButton backButton = new JButton("Geri");
        
        submitButton.addActionListener(e -> {
            try {
                String adSoyad = adSoyadField.getText();
                int tc = Integer.parseInt(tcField.getText());
                int pass = Integer.parseInt(passField.getText());
                float gelir = Float.parseFloat(gelirField.getText());
                String cinsiyet = cinsiyetField.getText();
                
                // Random mus no
                int musNo = 1000 + (int)(Math.random() * 8999);
                int davetKod = 1000 + (int)(Math.random() * 8999);
                
                Musteri m = new Musteri(adSoyad, pass, tc, 0, cinsiyet, gelir, davetKod, musNo, new java.util.HashSet<>(), 0);
                if (DatabaseManager.addMusteri(m)) {
                     JOptionPane.showMessageDialog(this, "Kayıt Başarılı! Müşteri No: " + musNo);
                     cardLayout.show(mainPanel, "Login");
                } else {
                     JOptionPane.showMessageDialog(this, "Kayıt Hatası (TC çakışması olabilir).");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                 JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        });
        
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Login"));
        
        panel.add(submitButton);
        panel.add(backButton);
        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Header
        JLabel welcomeLabel = new JLabel("Hoşgeldiniz, " + currentUser.getAdSoyad() + " | Bakiye: " + currentUser.bakiye);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(welcomeLabel, BorderLayout.NORTH);
        
        // Actions
        JPanel actionsPanel = new JPanel();
        JButton depositBtn = new JButton("Para Yatır");
        JButton withdrawBtn = new JButton("Para Çek");
        JButton transferBtn = new JButton("Para Gönder");
        JButton krediBtn = new JButton("Kredi Çek");
        JButton refreshBtn = new JButton("Yenile"); // Basit bir reload icin
        JButton logoutBtn = new JButton("Çıkış");

        depositBtn.addActionListener(e -> {
             String val = JOptionPane.showInputDialog(this, "Miktar:");
             if(val != null) {
                 try {
                     float amount = Float.parseFloat(val);
                     DatabaseManager.updateBakiye(currentUser.tcno, amount);
                     currentUser.bakiye += amount; // Local update
                     welcomeLabel.setText("Hoşgeldiniz, " + currentUser.getAdSoyad() + " | Bakiye: " + currentUser.bakiye);
                     JOptionPane.showMessageDialog(this, "İşlem Başarılı");
                 } catch(Exception ex) {
                     JOptionPane.showMessageDialog(this, "Hata");
                 }
             }
        });
        
        withdrawBtn.addActionListener(e -> {
             String val = JOptionPane.showInputDialog(this, "Miktar:");
             if(val != null) {
                 try {
                     float amount = Float.parseFloat(val);
                     if(currentUser.bakiye >= amount) {
                        DatabaseManager.updateBakiye(currentUser.tcno, -amount);
                        currentUser.bakiye -= amount;
                        welcomeLabel.setText("Hoşgeldiniz, " + currentUser.getAdSoyad() + " | Bakiye: " + currentUser.bakiye);
                        JOptionPane.showMessageDialog(this, "İşlem Başarılı");
                     } else {
                         JOptionPane.showMessageDialog(this, "Yetersiz Bakiye");
                     }
                 } catch(Exception ex) {
                     JOptionPane.showMessageDialog(this, "Hata");
                 }
             }
        });

        transferBtn.addActionListener(e -> {
             try {
                String targetStr = JOptionPane.showInputDialog(this, "Alıcı Müşteri Numarası:");
                if (targetStr == null) return;
                int targetNo = Integer.parseInt(targetStr);
                
                String amountStr = JOptionPane.showInputDialog(this, "Gönderilecek Miktar:");
                if (amountStr == null) return;
                float amount = Float.parseFloat(amountStr);
                
                String result = DatabaseManager.transferMoney(currentUser.tcno, targetNo, amount);
                if ("SUCCESS".equals(result)) {
                     currentUser.bakiye -= amount;
                     welcomeLabel.setText("Hoşgeldiniz, " + currentUser.getAdSoyad() + " | Bakiye: " + currentUser.bakiye);
                     JOptionPane.showMessageDialog(this, "Para başarıyla gönderildi.");
                } else {
                     JOptionPane.showMessageDialog(this, result);
                }
             } catch (NumberFormatException ex) {
                 JOptionPane.showMessageDialog(this, "Lütfen geçerli sayılar giriniz.");
             }
        });

        krediBtn.addActionListener(e -> {
             mainPanel.add(createKrediPanel(), "Kredi");
             cardLayout.show(mainPanel, "Kredi");
        });
        
        refreshBtn.addActionListener(e -> {
             Kullanici reloaded = DatabaseManager.login(currentUser.tcno, currentUser.getSifre());
             if(reloaded != null) {
                 currentUser = reloaded;
                 welcomeLabel.setText("Hoşgeldiniz, " + currentUser.getAdSoyad() + " | Bakiye: " + currentUser.bakiye);
             }
        });

        logoutBtn.addActionListener(e -> {
             currentUser = null;
             cardLayout.show(mainPanel, "Login");
        });

        actionsPanel.add(depositBtn);
        actionsPanel.add(withdrawBtn);
        actionsPanel.add(transferBtn);
        actionsPanel.add(krediBtn);
        actionsPanel.add(refreshBtn);
        actionsPanel.add(logoutBtn);
        
        panel.add(actionsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createKrediPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel centerPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        
        JLabel infoLabel = new JLabel("<html><b>Mevcut Geliriniz: " + currentUser.gelir + "</b><br>Kredi Seçenekleri:</html>");
        centerPanel.add(infoLabel);

        JRadioButton r1 = new JRadioButton("Ev Kredisi (100.000 TL, 18 Ay) - Min Gelir: 20.000");
        JRadioButton r2 = new JRadioButton("Araba Kredisi (250.000 TL, 24 Ay) - Min Gelir: 15.000");
        JRadioButton r3 = new JRadioButton("İş Kredisi (300.000 TL, 32 Ay) - Min Gelir: 18.000");
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(r1); bg.add(r2); bg.add(r3);
        
        centerPanel.add(r1);
        centerPanel.add(r2);
        centerPanel.add(r3);
        
        JCheckBox teminatCheck = new JCheckBox("Teminatım var (Ev, Araba vb.) - ZORUNLU");
        centerPanel.add(teminatCheck);

        JPanel btnPanel = new JPanel();
        JButton applyBtn = new JButton("Başvur");
        JButton backBtn = new JButton("Geri Dön");
        
        applyBtn.addActionListener(e -> {
            if (!teminatCheck.isSelected()) {
                JOptionPane.showMessageDialog(this, "Teminat olmadan kredi çekemezsiniz!");
                return;
            }
            
            float amount = 0;
            String type = "";
            int term = 0;
            float minIncome = 0;
            int calisanPuan = 0;
            float calisanGelir = 0;

            if (r1.isSelected()) {
                amount = 100000; type = "Ev Kredisi"; term = 18; minIncome = 20000;
                calisanPuan = 15; calisanGelir = 1500;
            } else if (r2.isSelected()) {
                amount = 250000; type = "Araba Kredisi"; term = 24; minIncome = 15000;
                calisanPuan = 10; calisanGelir = 1000;
            } else if (r3.isSelected()) {
                amount = 300000; type = "Is Kredisi"; term = 32; minIncome = 18000;
                calisanPuan = 25; calisanGelir = 2500;
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir kredi türü seçin.");
                return;
            }

            if (currentUser.gelir < minIncome) {
                JOptionPane.showMessageDialog(this, "Bu kredi için geliriniz yetersiz.");
                return;
            }
           
            boolean hasCredit = false;
            for(Kredi k : currentUser.krediler) {
                if (k.vadesayi == term) {
                    hasCredit = true; break;
                }
            }
            if (hasCredit) {
                JOptionPane.showMessageDialog(this, "Bu krediyi zaten çektiniz!");
                return;
            }
           int calisanTc = DatabaseManager.getRandomCalisanTc();
            if (calisanTc == -1) {
                JOptionPane.showMessageDialog(this, "Müsait çalışan bulunamadı."); 
                return; 
            }

            Kredi newKredi = new Kredi(amount, type, term);
            if(DatabaseManager.addKredi(newKredi, currentUser.tcno)) {
                DatabaseManager.updateBakiye(currentUser.tcno, amount);
                DatabaseManager.updateCalisanBonus(calisanTc, calisanPuan, calisanGelir);
                
                currentUser.bakiye += amount;
                currentUser.krediler.add(newKredi);
                
                JOptionPane.showMessageDialog(this, "Kredi Başarıyla Çekildi! Hesabınıza " + amount + " TL yüklendi.");
                cardLayout.show(mainPanel, "Dashboard");
            } else {
                JOptionPane.showMessageDialog(this, "İşlem sırasında hata oluştu.");
            }
        });
        
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        
        btnPanel.add(applyBtn);
        btnPanel.add(backBtn);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createCalisanDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("ÇALIŞAN PANELİ - Hoşgeldiniz " + currentUser.getAdSoyad());
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(welcomeLabel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel maasLabel = new JLabel("Maaş: " + currentUser.gelir + " TL");
        maasLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        maasLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel puanLabel = new JLabel("Performans Puanı: " + currentUser.kredipuani);
        puanLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        puanLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        centerPanel.add(maasLabel);
        centerPanel.add(puanLabel);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        JButton logoutBtn = new JButton("Çıkış Yap");
        logoutBtn.addActionListener(e -> {
             currentUser = null;
             cardLayout.show(mainPanel, "Login");
        });
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(logoutBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        SwingUtilities.invokeLater(() -> {
            new BankaGUI().setVisible(true);
        });
    }
}
