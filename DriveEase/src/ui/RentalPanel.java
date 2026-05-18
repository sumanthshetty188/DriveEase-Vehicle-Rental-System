package ui;

import model.*;
import service.*;
import util.UITheme;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * RentalPanel - Book vehicles, view active rentals, process returns.
 */
public class RentalPanel extends JPanel {

    private final User            currentUser;
    private final RentalService   rentalService   = new RentalService();
    private final VehicleService  vehicleService  = new VehicleService();
    private final CustomerService customerService = new CustomerService();

    // Booking form
    private JComboBox<String> customerCombo, vehicleCombo, paymentCombo;
    private JTextField startDateField, endDateField, totalAmountField;

    // Return form
    private JTextField returnRentalIdField, returnDateField;
    private JLabel     fineLabel;

    // Active rentals table
    private JTable            activeTable;
    private DefaultTableModel activeModel;

    public RentalPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(UITheme.PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel header = new JLabel("📋  Rental Booking");
        header.setFont(UITheme.FONT_TITLE);
        header.setForeground(UITheme.TEXT_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        add(header, BorderLayout.NORTH);

        JSplitPane center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildBookingForm(), buildActiveRentals());
        center.setDividerLocation(380);
        center.setDividerSize(6);
        center.setBorder(null);
        add(center, BorderLayout.CENTER);

        loadActiveRentals();
        refreshCombos();
    }

    // ── Booking Form ────────────────────────────────────
    private JPanel buildBookingForm() {
        JPanel p = UITheme.cardPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(360, 0));

        // ── Book New Section ────
        sectionTitle(p, "🔑  Book a Vehicle");

        customerCombo = dropdownRow(p, "Customer *",
            customerService.getAllCustomers().stream()
                .map(Customer::toString).toArray(String[]::new));
        vehicleCombo = dropdownRow(p, "Vehicle *",
            vehicleService.getAvailableVehicles().stream()
                .map(Vehicle::toString).toArray(String[]::new));

        startDateField = inputRow(p, "Start Date (YYYY-MM-DD) *", LocalDate.now().toString());
        endDateField   = inputRow(p, "End Date (YYYY-MM-DD) *",   LocalDate.now().plusDays(1).toString());
        totalAmountField = inputRow(p, "Total Amount (₹)", "0.00");
        totalAmountField.setEditable(false);
        totalAmountField.setBackground(new Color(245, 245, 245));

        paymentCombo = dropdownRow(p, "Payment Method *",
            new String[]{"Cash", "Card", "Online"});

        // Calculate button
        JButton calcBtn = UITheme.warningButton("Calculate");
        calcBtn.setAlignmentX(LEFT_ALIGNMENT);
        calcBtn.addActionListener(e -> calculateTotal());
        p.add(calcBtn);
        p.add(Box.createVerticalStrut(10));

        JButton bookBtn = UITheme.successButton("Confirm Booking");
        bookBtn.setAlignmentX(LEFT_ALIGNMENT);
        bookBtn.addActionListener(e -> handleBook());
        p.add(bookBtn);

        // Separator
        p.add(Box.createVerticalStrut(20));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        p.add(sep);
        p.add(Box.createVerticalStrut(16));

        // ── Return Vehicle Section ────
        sectionTitle(p, "↩  Return a Vehicle");

        returnRentalIdField = inputRow(p, "Rental ID *", "");
        returnDateField     = inputRow(p, "Return Date (YYYY-MM-DD) *", LocalDate.now().toString());

        fineLabel = new JLabel("Fine: ₹ 0.00");
        fineLabel.setFont(UITheme.FONT_BODY);
        fineLabel.setForeground(UITheme.DANGER);
        fineLabel.setAlignmentX(LEFT_ALIGNMENT);
        p.add(fineLabel);
        p.add(Box.createVerticalStrut(10));

        JButton returnBtn = UITheme.dangerButton("Process Return");
        returnBtn.setAlignmentX(LEFT_ALIGNMENT);
        returnBtn.addActionListener(e -> handleReturn());
        p.add(returnBtn);

        return p;
    }

    // ── Active Rentals Table ────────────────────────────
    private JPanel buildActiveRentals() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.PAGE_BG);
        p.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        JLabel title = new JLabel("Active Rentals");
        title.setFont(UITheme.FONT_HEADING);
        title.setForeground(UITheme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        p.add(title, BorderLayout.NORTH);

        String[] cols = {"ID","Customer","Vehicle","Reg. No","Start","End","Amount","Status"};
        activeModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        activeTable = new JTable(activeModel);
        UITheme.styleTable(activeTable);
        activeTable.getColumnModel().getColumn(0).setMaxWidth(50);

        // Clicking a row fills rental ID in return form
        activeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && activeTable.getSelectedRow() >= 0) {
                returnRentalIdField.setText(
                    activeModel.getValueAt(activeTable.getSelectedRow(), 0).toString());
            }
        });

        JScrollPane scroll = new JScrollPane(activeTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        p.add(scroll, BorderLayout.CENTER);

        JButton refreshBtn = UITheme.primaryButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> { loadActiveRentals(); refreshCombos(); });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(UITheme.PAGE_BG);
        bottom.add(refreshBtn);
        p.add(bottom, BorderLayout.SOUTH);
        return p;
    }

    // ── Handlers ────────────────────────────────────────
    private void calculateTotal() {
        try {
            LocalDate start = LocalDate.parse(startDateField.getText().trim());
            LocalDate end   = LocalDate.parse(endDateField.getText().trim());
            if (!end.isAfter(start)) {
                ValidationUtil.showError(this, "End date must be after start date."); return; }
            long days = ChronoUnit.DAYS.between(start, end);
            // Get selected vehicle price
            String selected = (String) vehicleCombo.getSelectedItem();
            if (selected == null) return;
            int vid = Integer.parseInt(selected.split(" – ")[0].trim());
            Vehicle v = vehicleService.getById(vid);
            if (v == null) return;
            double total = days * v.getPricePerDay();
            totalAmountField.setText(String.format("%.2f", total));
        } catch (Exception ex) {
            ValidationUtil.showError(this, "Invalid date format. Use YYYY-MM-DD.");
        }
    }

    private void handleBook() {
        try {
            String custStr = (String) customerCombo.getSelectedItem();
            String vehStr  = (String) vehicleCombo.getSelectedItem();
            if (custStr == null || vehStr == null) {
                ValidationUtil.showError(this, "Please select customer and vehicle."); return; }

            int customerId = Integer.parseInt(custStr.split(" – ")[0].trim());
            int vehicleId  = Integer.parseInt(vehStr.split(" – ")[0].trim());
            Date start = Date.valueOf(startDateField.getText().trim());
            Date end   = Date.valueOf(endDateField.getText().trim());
            String method  = (String) paymentCombo.getSelectedItem();

            Rental r = new Rental();
            r.setCustomerId(customerId);
            r.setVehicleId(vehicleId);
            r.setStartDate(start);
            r.setEndDate(end);
            r.setCreatedBy(currentUser.getUserId());

            String result = rentalService.bookVehicle(r, method);
            if ("SUCCESS".equals(result)) {
                ValidationUtil.showSuccess(this,
                    "Vehicle booked successfully!\nTotal: ₹ " + r.getTotalAmount());
                loadActiveRentals(); refreshCombos();
            } else {
                ValidationUtil.showError(this, result);
            }
        } catch (Exception ex) {
            ValidationUtil.showError(this, "Invalid input: " + ex.getMessage());
        }
    }

    private void handleReturn() {
        String ridStr = returnRentalIdField.getText().trim();
        String rdStr  = returnDateField.getText().trim();
        if (ridStr.isEmpty()) { ValidationUtil.showError(this,"Enter Rental ID."); return; }
        try {
            int  rentalId = Integer.parseInt(ridStr);
            Date retDate  = Date.valueOf(rdStr);
            double fine   = rentalService.returnVehicle(rentalId, retDate);
            if (fine < 0) { ValidationUtil.showError(this,"Rental not found."); return; }
            fineLabel.setText("Fine: ₹ " + String.format("%.2f", fine));
            String msg = "Vehicle returned successfully!";
            if (fine > 0) msg += "\nLate return fine: ₹ " + String.format("%.2f", fine);
            ValidationUtil.showSuccess(this, msg);
            loadActiveRentals(); refreshCombos();
            returnRentalIdField.setText("");
        } catch (Exception ex) {
            ValidationUtil.showError(this, "Invalid input: " + ex.getMessage());
        }
    }

    private void loadActiveRentals() {
        activeModel.setRowCount(0);
        for (Rental r : rentalService.getActiveRentals()) {
            activeModel.addRow(new Object[]{
                r.getRentalId(), r.getCustomerName(), r.getVehicleName(),
                r.getVehicleRegNo(), r.getStartDate(), r.getEndDate(),
                String.format("₹ %.2f", r.getTotalAmount()), r.getStatus()
            });
        }
    }

    private void refreshCombos() {
        customerCombo.removeAllItems();
        for (Customer c : customerService.getAllCustomers()) customerCombo.addItem(c.toString());
        vehicleCombo.removeAllItems();
        for (Vehicle v : vehicleService.getAvailableVehicles()) vehicleCombo.addItem(v.toString());
    }

    // ── Form Helpers ─────────────────────────────────────
    private JTextField inputRow(JPanel p, String label, String defaultVal) {
        JLabel lbl = new JLabel(label); lbl.setFont(UITheme.FONT_SMALL);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        JTextField tf = UITheme.styledField(20);
        tf.setText(defaultVal);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        tf.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lbl); p.add(Box.createVerticalStrut(2));
        p.add(tf);  p.add(Box.createVerticalStrut(10));
        return tf;
    }

    private JComboBox<String> dropdownRow(JPanel p, String label, String[] items) {
        JLabel lbl = new JLabel(label); lbl.setFont(UITheme.FONT_SMALL);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(UITheme.FONT_BODY);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cb.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lbl); p.add(Box.createVerticalStrut(2));
        p.add(cb);  p.add(Box.createVerticalStrut(10));
        return cb;
    }

    private void sectionTitle(JPanel p, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_HEADING); lbl.setForeground(UITheme.PRIMARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        p.add(lbl);
    }
}
