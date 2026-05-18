package ui;

import model.User;
import service.*;
import util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * DashboardFrame - Main application window.
 * Left: modern sidebar. Right: content panel (card layout for each module).
 */
public class DashboardFrame extends JFrame {

    private final User            currentUser;
    private final VehicleService  vehicleService  = new VehicleService();
    private final CustomerService customerService = new CustomerService();
    private final RentalService   rentalService   = new RentalService();

    private JPanel    contentArea;
    private CardLayout cardLayout;

    // Sidebar buttons array for active-state tracking
    private JButton[] sidebarButtons;
    private JButton   activeSidebarButton;

    public DashboardFrame(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("DriveEase – Vehicle Rental System | " + currentUser.getFullName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 650));

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContentArea(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // ──────────────────────────────────────────────────────────────────
    // SIDEBAR
    // ──────────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BorderLayout());

        // App logo area
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(UITheme.SIDEBAR_BG);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel logo = new JLabel("🚗  DriveEase");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        logoPanel.add(logo);

        // Nav menu
        JPanel navPanel = new JPanel();
        navPanel.setBackground(UITheme.SIDEBAR_BG);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        String[][] menuItems = {
            {"🏠  Dashboard",          "DASHBOARD"},
            {"🚗  Vehicles",           "VEHICLES"},
            {"👤  Customers",          "CUSTOMERS"},
            {"📋  Rental Booking",     "RENTALS"},
            {"📜  Transactions",       "TRANSACTIONS"},
            {"📊  Reports & Bills",    "REPORTS"},
        };

        sidebarButtons = new JButton[menuItems.length];
        for (int i = 0; i < menuItems.length; i++) {
            final String card = menuItems[i][1];
            JButton btn = createSidebarButton(menuItems[i][0]);
            btn.addActionListener(e -> switchCard(card, btn));
            navPanel.add(btn);
            sidebarButtons[i] = btn;
        }

        // User info footer
        JPanel footer = new JPanel();
        footer.setBackground(new Color(21, 32, 43));
        footer.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));

        JLabel userLabel = new JLabel("👤  " + currentUser.getFullName());
        userLabel.setFont(UITheme.FONT_SMALL);
        userLabel.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel("    " + currentUser.getRole());
        roleLabel.setFont(UITheme.FONT_SMALL);
        roleLabel.setForeground(new Color(127, 140, 141));

        JButton logoutBtn = new JButton("⏻  Logout");
        logoutBtn.setFont(UITheme.FONT_SMALL);
        logoutBtn.setForeground(UITheme.DANGER);
        logoutBtn.setBackground(new Color(21, 32, 43));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setAlignmentX(LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> handleLogout());

        footer.add(userLabel);
        footer.add(Box.createVerticalStrut(2));
        footer.add(roleLabel);
        footer.add(Box.createVerticalStrut(8));
        footer.add(logoutBtn);

        sidebar.add(logoPanel, BorderLayout.NORTH);
        sidebar.add(navPanel,  BorderLayout.CENTER);
        sidebar.add(footer,    BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BODY);
        btn.setForeground(new Color(189, 195, 199));
        btn.setBackground(UITheme.SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeSidebarButton)
                    btn.setBackground(UITheme.SIDEBAR_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                if (btn != activeSidebarButton)
                    btn.setBackground(UITheme.SIDEBAR_BG);
            }
        });
        return btn;
    }

    // ──────────────────────────────────────────────────────────────────
    // CONTENT AREA (CardLayout)
    // ──────────────────────────────────────────────────────────────────
    private JPanel buildContentArea() {
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(UITheme.PAGE_BG);

        contentArea.add(buildHomePanel(),          "DASHBOARD");
        contentArea.add(new VehiclePanel(),        "VEHICLES");
        contentArea.add(new CustomerPanel(),       "CUSTOMERS");
        contentArea.add(new RentalPanel(currentUser), "RENTALS");
        contentArea.add(new TransactionPanel(),    "TRANSACTIONS");
        contentArea.add(new ReportPanel(currentUser), "REPORTS");

        // Show dashboard by default
        SwingUtilities.invokeLater(() -> {
            if (sidebarButtons.length > 0)
                switchCard("DASHBOARD", sidebarButtons[0]);
        });

        return contentArea;
    }

    private void switchCard(String name, JButton sourceBtn) {
        cardLayout.show(contentArea, name);
        // Update active sidebar state
        if (activeSidebarButton != null) {
            activeSidebarButton.setBackground(UITheme.SIDEBAR_BG);
            activeSidebarButton.setForeground(new Color(189, 195, 199));
        }
        sourceBtn.setBackground(UITheme.PRIMARY);
        sourceBtn.setForeground(Color.WHITE);
        activeSidebarButton = sourceBtn;
    }

    // ──────────────────────────────────────────────────────────────────
    // HOME / DASHBOARD PANEL
    // ──────────────────────────────────────────────────────────────────
    private JPanel buildHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.PAGE_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        // Header
        JLabel header = new JLabel("Dashboard Overview");
        header.setFont(UITheme.FONT_TITLE);
        header.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(header, BorderLayout.NORTH);

        // Stats grid
        JPanel statsGrid = new JPanel(new GridLayout(2, 3, 18, 18));
        statsGrid.setBackground(UITheme.PAGE_BG);
        statsGrid.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        int totalVehicles     = vehicleService.countTotal();
        int availableVehicles = vehicleService.countByStatus("Available");
        int rentedVehicles    = vehicleService.countByStatus("Rented");
        int maintenanceVeh    = vehicleService.countByStatus("Maintenance");
        int totalCustomers    = customerService.countTotal();
        int activeRentals     = rentalService.countActive();
        double totalRevenue   = rentalService.totalRevenue();

        statsGrid.add(statCard("🚗  Total Vehicles",     String.valueOf(totalVehicles),     UITheme.PRIMARY));
        statsGrid.add(statCard("✅  Available",           String.valueOf(availableVehicles), UITheme.ACCENT));
        statsGrid.add(statCard("🔑  Rented",             String.valueOf(rentedVehicles),    UITheme.WARNING));
        statsGrid.add(statCard("👤  Total Customers",    String.valueOf(totalCustomers),    new Color(142, 68, 173)));
        statsGrid.add(statCard("📋  Active Rentals",     String.valueOf(activeRentals),     new Color(52, 152, 219)));
        statsGrid.add(statCard("💰  Total Revenue",
            "₹ " + NumberFormat.getNumberInstance(new Locale("en","IN"))
                              .format(totalRevenue), UITheme.DANGER));

        panel.add(statsGrid, BorderLayout.CENTER);

        // Quick info footer
        JLabel welcome = new JLabel("Welcome, " + currentUser.getFullName() +
            "  |  Role: " + currentUser.getRole());
        welcome.setFont(UITheme.FONT_SMALL);
        welcome.setForeground(UITheme.TEXT_SECONDARY);
        welcome.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        panel.add(welcome, BorderLayout.SOUTH);

        return panel;
    }

    /** Creates a coloured statistics card. */
    private JPanel statCard(String title, String value, Color accent) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        // Coloured left border strip
        JPanel strip = new JPanel();
        strip.setBackground(accent);
        strip.setPreferredSize(new Dimension(6, 0));
        card.add(strip, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setBackground(Color.WHITE);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL);
        titleLbl.setForeground(UITheme.TEXT_SECONDARY);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLbl.setForeground(accent);

        text.add(titleLbl);
        text.add(Box.createVerticalStrut(6));
        text.add(valueLbl);
        card.add(text, BorderLayout.CENTER);

        return card;
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?", "Logout",
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
