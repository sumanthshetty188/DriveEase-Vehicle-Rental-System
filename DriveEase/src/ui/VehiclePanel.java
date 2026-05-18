package ui;

import model.Vehicle;
import service.VehicleService;
import util.UITheme;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * VehiclePanel - Full CRUD UI for vehicle management.
 */
public class VehiclePanel extends JPanel {

    private final VehicleService service = new VehicleService();

    // Form fields
    private JTextField idField, nameField, brandField, regNoField, priceField;
    private JComboBox<String> typeCombo, statusCombo;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;

    // Search
    private JTextField searchField;

    public VehiclePanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        loadTable("");
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.PAGE_BG);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("🚗  Vehicle Management");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        p.add(title, BorderLayout.WEST);

        // Search bar on the right
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchRow.setBackground(UITheme.PAGE_BG);
        searchField = UITheme.styledField(18);
        searchField.putClientProperty("JTextField.placeholderText", "Search vehicles...");

        JButton searchBtn = UITheme.primaryButton("Search");
        searchBtn.addActionListener(e -> loadTable(searchField.getText().trim()));
        searchField.addActionListener(e -> loadTable(searchField.getText().trim()));

        JButton clearBtn = UITheme.warningButton("Clear");
        clearBtn.addActionListener(e -> { searchField.setText(""); loadTable(""); });

        searchRow.add(new JLabel("Search: "));
        searchRow.add(searchField);
        searchRow.add(searchBtn);
        searchRow.add(clearBtn);
        p.add(searchRow, BorderLayout.EAST);
        return p;
    }

    private JSplitPane buildCenter() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildFormPanel(), buildTablePanel());
        split.setDividerLocation(340);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setBackground(UITheme.PAGE_BG);
        return split;
    }

    private JPanel buildFormPanel() {
        JPanel panel = UITheme.cardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(320, 0));

        addSectionLabel(panel, "Vehicle Details");

        idField   = hiddenIdField();
        nameField = addFormRow(panel, "Vehicle Name *",   UITheme.styledField(20));
        brandField= addFormRow(panel, "Brand *",          UITheme.styledField(20));

        typeCombo = UITheme.styledCombo(new String[]{"Car","Bike","SUV","Van","Truck"});
        addLabeledCombo(panel, "Vehicle Type *", typeCombo);

        regNoField = addFormRow(panel, "Registration No. *", UITheme.styledField(20));
        priceField = addFormRow(panel, "Price Per Day (₹) *", UITheme.styledField(20));

        statusCombo = UITheme.styledCombo(new String[]{"Available","Rented","Maintenance"});
        addLabeledCombo(panel, "Status *", statusCombo);

        panel.add(Box.createVerticalStrut(18));

        // Button row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        JButton addBtn    = UITheme.successButton("Add");
        JButton updateBtn = UITheme.primaryButton("Update");
        JButton deleteBtn = UITheme.dangerButton("Delete");
        JButton clearBtn  = UITheme.warningButton("Clear");

        addBtn.addActionListener(e    -> handleAdd());
        updateBtn.addActionListener(e -> handleUpdate());
        deleteBtn.addActionListener(e -> handleDelete());
        clearBtn.addActionListener(e  -> clearForm());

        btnRow.add(addBtn); btnRow.add(updateBtn);
        btnRow.add(deleteBtn); btnRow.add(clearBtn);
        panel.add(btnRow);

        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.PAGE_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));

        String[] cols = {"ID","Name","Brand","Type","Reg. Number","₹/Day","Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(5).setMaxWidth(80);
        table.getColumnModel().getColumn(6).setMaxWidth(100);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0)
                populateForm();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ── CRUD Handlers ──────────────────────────────────
    private void handleAdd() {
        Vehicle v = buildVehicleFromForm();
        if (v == null) return;
        String result = service.addVehicle(v);
        if ("SUCCESS".equals(result)) {
            ValidationUtil.showSuccess(this, "Vehicle added successfully!");
            clearForm(); loadTable("");
        } else {
            ValidationUtil.showError(this, result);
        }
    }

    private void handleUpdate() {
        if (idField.getText().isEmpty()) {
            ValidationUtil.showError(this, "Select a vehicle from the table first."); return;
        }
        Vehicle v = buildVehicleFromForm();
        if (v == null) return;
        v.setVehicleId(Integer.parseInt(idField.getText()));
        String result = service.updateVehicle(v);
        if ("SUCCESS".equals(result)) {
            ValidationUtil.showSuccess(this, "Vehicle updated successfully!");
            clearForm(); loadTable("");
        } else {
            ValidationUtil.showError(this, result);
        }
    }

    private void handleDelete() {
        if (idField.getText().isEmpty()) {
            ValidationUtil.showError(this, "Select a vehicle to delete."); return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
            "Delete this vehicle?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            String result = service.deleteVehicle(Integer.parseInt(idField.getText()));
            if ("SUCCESS".equals(result)) {
                ValidationUtil.showSuccess(this, "Vehicle deleted.");
                clearForm(); loadTable("");
            } else {
                ValidationUtil.showError(this, result);
            }
        }
    }

    // ── Helpers ─────────────────────────────────────────
    private Vehicle buildVehicleFromForm() {
        String name   = nameField.getText().trim();
        String brand  = brandField.getText().trim();
        String regNo  = regNoField.getText().trim();
        String price  = priceField.getText().trim();
        String type   = (String) typeCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();

        if (ValidationUtil.isNullOrEmpty(name)) {
            ValidationUtil.showError(this, "Vehicle name is required."); return null; }
        if (ValidationUtil.isNullOrEmpty(brand)) {
            ValidationUtil.showError(this, "Brand is required."); return null; }
        if (ValidationUtil.isNullOrEmpty(regNo)) {
            ValidationUtil.showError(this, "Registration number is required."); return null; }
        if (!ValidationUtil.isPositiveDecimal(price)) {
            ValidationUtil.showError(this, "Enter a valid price per day."); return null; }

        return new Vehicle(name, brand, type, regNo, Double.parseDouble(price), status);
    }

    private void populateForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        idField.setText(tableModel.getValueAt(row, 0).toString());
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        brandField.setText(tableModel.getValueAt(row, 2).toString());
        typeCombo.setSelectedItem(tableModel.getValueAt(row, 3));
        regNoField.setText(tableModel.getValueAt(row, 4).toString());
        priceField.setText(tableModel.getValueAt(row, 5).toString());
        statusCombo.setSelectedItem(tableModel.getValueAt(row, 6));
    }

    private void clearForm() {
        idField.setText(""); nameField.setText(""); brandField.setText("");
        regNoField.setText(""); priceField.setText("");
        typeCombo.setSelectedIndex(0); statusCombo.setSelectedIndex(0);
        table.clearSelection();
    }

    private void loadTable(String keyword) {
        tableModel.setRowCount(0);
        List<Vehicle> list = keyword.isEmpty()
            ? service.getAllVehicles() : service.searchVehicles(keyword);
        for (Vehicle v : list) {
            tableModel.addRow(new Object[]{
                v.getVehicleId(), v.getVehicleName(), v.getBrand(),
                v.getVehicleType(), v.getRegistrationNumber(),
                String.format("%.2f", v.getPricePerDay()), v.getAvailabilityStatus()
            });
        }
    }

    // ── Utility form builders ────────────────────────────
    private <T extends JTextField> T addFormRow(JPanel p, String label, T field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lbl); p.add(Box.createVerticalStrut(2));
        p.add(field); p.add(Box.createVerticalStrut(10));
        return field;
    }

    private void addLabeledCombo(JPanel p, String label, JComboBox<?> combo) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_PRIMARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        combo.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lbl); p.add(Box.createVerticalStrut(2));
        p.add(combo); p.add(Box.createVerticalStrut(10));
    }

    private void addSectionLabel(JPanel p, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_HEADING);
        lbl.setForeground(UITheme.PRIMARY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        p.add(lbl);
    }

    private JTextField hiddenIdField() {
        JTextField f = new JTextField();
        f.setVisible(false); return f;
    }
}
