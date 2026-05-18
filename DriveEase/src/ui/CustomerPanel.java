package ui;

import model.Customer;
import service.CustomerService;
import util.UITheme;
import util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * CustomerPanel - Full CRUD UI for customer management.
 */
public class CustomerPanel extends JPanel {

    private final CustomerService service = new CustomerService();

    private JTextField idField, nameField, phoneField, emailField, licenseField;
    private JTextArea  addressArea;
    private JTable     table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public CustomerPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        loadTable("");
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.PAGE_BG);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        JLabel title = new JLabel("👤  Customer Management");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);
        p.add(title, BorderLayout.WEST);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchRow.setBackground(UITheme.PAGE_BG);
        searchField = UITheme.styledField(18);
        JButton searchBtn = UITheme.primaryButton("Search");
        JButton clearBtn  = UITheme.warningButton("Clear");
        searchBtn.addActionListener(e -> loadTable(searchField.getText().trim()));
        searchField.addActionListener(e -> loadTable(searchField.getText().trim()));
        clearBtn.addActionListener(e -> { searchField.setText(""); loadTable(""); });
        searchRow.add(new JLabel("Search: ")); searchRow.add(searchField);
        searchRow.add(searchBtn); searchRow.add(clearBtn);
        p.add(searchRow, BorderLayout.EAST);
        return p;
    }

    private JSplitPane buildCenter() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildForm(), buildTablePanel());
        split.setDividerLocation(320);
        split.setDividerSize(6);
        split.setBorder(null);
        return split;
    }

    private JPanel buildForm() {
        JPanel p = UITheme.cardPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setPreferredSize(new Dimension(300, 0));

        JLabel title = new JLabel("Customer Details");
        title.setFont(UITheme.FONT_HEADING);
        title.setForeground(UITheme.PRIMARY);
        title.setAlignmentX(LEFT_ALIGNMENT);
        p.add(title); p.add(Box.createVerticalStrut(14));

        idField      = hiddenField();
        nameField    = row(p, "Full Name *",            UITheme.styledField(20));
        phoneField   = row(p, "Phone Number *",         UITheme.styledField(20));
        emailField   = row(p, "Email",                  UITheme.styledField(20));
        licenseField = row(p, "Driving License No. *",  UITheme.styledField(20));

        JLabel addrLabel = new JLabel("Address");
        addrLabel.setFont(UITheme.FONT_SMALL);
        addrLabel.setAlignmentX(LEFT_ALIGNMENT);
        addressArea = new JTextArea(3, 20);
        addressArea.setFont(UITheme.FONT_BODY);
        addressArea.setLineWrap(true);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189,195,199)),
            BorderFactory.createEmptyBorder(4,6,4,6)));
        JScrollPane addrScroll = new JScrollPane(addressArea);
        addrScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        addrScroll.setAlignmentX(LEFT_ALIGNMENT);
        p.add(addrLabel); p.add(Box.createVerticalStrut(2));
        p.add(addrScroll); p.add(Box.createVerticalStrut(14));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(Color.WHITE); btnRow.setAlignmentX(LEFT_ALIGNMENT);
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
        p.add(btnRow);
        return p;
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.PAGE_BG);
        p.setBorder(BorderFactory.createEmptyBorder(0,12,0,0));

        String[] cols = {"ID","Full Name","Phone","Email","License No.","Address"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) populateForm();
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private void handleAdd() {
        Customer c = buildFromForm(); if (c == null) return;
        String result = service.addCustomer(c);
        if ("SUCCESS".equals(result)) {
            ValidationUtil.showSuccess(this, "Customer added successfully!");
            clearForm(); loadTable("");
        } else ValidationUtil.showError(this, result);
    }

    private void handleUpdate() {
        if (idField.getText().isEmpty()) {
            ValidationUtil.showError(this, "Select a customer from the table."); return;
        }
        Customer c = buildFromForm(); if (c == null) return;
        c.setCustomerId(Integer.parseInt(idField.getText()));
        String result = service.updateCustomer(c);
        if ("SUCCESS".equals(result)) {
            ValidationUtil.showSuccess(this, "Customer updated."); clearForm(); loadTable(""); }
        else ValidationUtil.showError(this, result);
    }

    private void handleDelete() {
        if (idField.getText().isEmpty()) {
            ValidationUtil.showError(this, "Select a customer to delete."); return; }
        int ch = JOptionPane.showConfirmDialog(this,"Delete this customer?","Confirm",JOptionPane.YES_NO_OPTION);
        if (ch == JOptionPane.YES_OPTION) {
            String r = service.deleteCustomer(Integer.parseInt(idField.getText()));
            if ("SUCCESS".equals(r)) { ValidationUtil.showSuccess(this,"Customer deleted."); clearForm(); loadTable(""); }
            else ValidationUtil.showError(this, r);
        }
    }

    private Customer buildFromForm() {
        String name    = nameField.getText().trim();
        String phone   = phoneField.getText().trim();
        String email   = emailField.getText().trim();
        String license = licenseField.getText().trim();
        String address = addressArea.getText().trim();

        if (ValidationUtil.isNullOrEmpty(name))    { ValidationUtil.showError(this,"Name required."); return null; }
        if (!ValidationUtil.isValidPhone(phone))   { ValidationUtil.showError(this,"Invalid phone (10 digits)."); return null; }
        if (!email.isEmpty() && !ValidationUtil.isValidEmail(email)) { ValidationUtil.showError(this,"Invalid email."); return null; }
        if (ValidationUtil.isNullOrEmpty(license)) { ValidationUtil.showError(this,"License number required."); return null; }
        return new Customer(name, phone, email, license, address);
    }

    private void populateForm() {
        int row = table.getSelectedRow(); if (row < 0) return;
        idField.setText(tableModel.getValueAt(row,0).toString());
        nameField.setText(tableModel.getValueAt(row,1).toString());
        phoneField.setText(tableModel.getValueAt(row,2).toString());
        emailField.setText(tableModel.getValueAt(row,3).toString());
        licenseField.setText(tableModel.getValueAt(row,4).toString());
        addressArea.setText(tableModel.getValueAt(row,5).toString());
    }

    private void clearForm() {
        idField.setText(""); nameField.setText(""); phoneField.setText("");
        emailField.setText(""); licenseField.setText(""); addressArea.setText("");
        table.clearSelection();
    }

    private void loadTable(String kw) {
        tableModel.setRowCount(0);
        List<Customer> list = kw.isEmpty() ? service.getAllCustomers() : service.searchCustomers(kw);
        for (Customer c : list) {
            tableModel.addRow(new Object[]{
                c.getCustomerId(), c.getFullName(), c.getPhone(),
                c.getEmail(), c.getDrivingLicense(), c.getAddress()
            });
        }
    }

    private <T extends JTextField> T row(JPanel p, String lbl, T f) {
        JLabel l = new JLabel(lbl); l.setFont(UITheme.FONT_SMALL);
        l.setAlignmentX(LEFT_ALIGNMENT);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); f.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(2)); p.add(f); p.add(Box.createVerticalStrut(10));
        return f;
    }
    private JTextField hiddenField() { JTextField f = new JTextField(); f.setVisible(false); return f; }
}
