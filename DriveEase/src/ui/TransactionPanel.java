package ui;

import model.Payment;
import model.Rental;
import service.RentalService;
import util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * TransactionPanel - Rental history + payment records with search/filter.
 */
public class TransactionPanel extends JPanel {

    private final RentalService service = new RentalService();

    private JTable            rentalTable, paymentTable;
    private DefaultTableModel rentalModel, paymentModel;
    private JTextField        rentalSearch, paymentSearch;

    public TransactionPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel header = new JLabel("📜  Transaction History");
        header.setFont(UITheme.FONT_TITLE);
        header.setForeground(UITheme.TEXT_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(0,0,16,0));
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_BODY);
        tabs.addTab("  Rental Records  ", buildRentalTab());
        tabs.addTab("  Payment Records  ", buildPaymentTab());
        add(tabs, BorderLayout.CENTER);

        loadRentals(""); loadPayments("");
    }

    private JPanel buildRentalTab() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(UITheme.PAGE_BG);
        p.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        // Search bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setBackground(UITheme.PAGE_BG);
        rentalSearch = UITheme.styledField(22);
        JButton searchBtn = UITheme.primaryButton("Search");
        JButton clearBtn  = UITheme.warningButton("Clear");
        searchBtn.addActionListener(e -> loadRentals(rentalSearch.getText().trim()));
        clearBtn.addActionListener(e  -> { rentalSearch.setText(""); loadRentals(""); });
        top.add(new JLabel("Search: ")); top.add(rentalSearch);
        top.add(searchBtn); top.add(clearBtn);
        p.add(top, BorderLayout.NORTH);

        String[] cols = {"ID","Customer","Vehicle","Reg No","Start","End","Return Date","Amount","Fine","Status"};
        rentalModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r,int c){return false;}};
        rentalTable = new JTable(rentalModel);
        UITheme.styleTable(rentalTable);
        rentalTable.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane scroll = new JScrollPane(rentalTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildPaymentTab() {
        JPanel p = new JPanel(new BorderLayout(0,10));
        p.setBackground(UITheme.PAGE_BG);
        p.setBorder(BorderFactory.createEmptyBorder(12,0,0,0));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setBackground(UITheme.PAGE_BG);
        paymentSearch = UITheme.styledField(22);
        JButton searchBtn = UITheme.primaryButton("Search");
        JButton clearBtn  = UITheme.warningButton("Clear");
        searchBtn.addActionListener(e -> loadPayments(paymentSearch.getText().trim()));
        clearBtn.addActionListener(e  -> { paymentSearch.setText(""); loadPayments(""); });
        top.add(new JLabel("Search: ")); top.add(paymentSearch);
        top.add(searchBtn); top.add(clearBtn);
        p.add(top, BorderLayout.NORTH);

        String[] cols = {"Pay ID","Rental ID","Customer","Vehicle","Amount","Method","Date"};
        paymentModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r,int c){return false;}};
        paymentTable = new JTable(paymentModel);
        UITheme.styleTable(paymentTable);
        paymentTable.getColumnModel().getColumn(0).setMaxWidth(60);
        paymentTable.getColumnModel().getColumn(1).setMaxWidth(80);

        // Revenue summary label
        double rev = service.totalRevenue();
        JLabel revLabel = new JLabel(String.format("Total Revenue Collected: ₹ %.2f", rev));
        revLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        revLabel.setForeground(UITheme.ACCENT);
        revLabel.setBorder(BorderFactory.createEmptyBorder(8,0,0,0));

        JScrollPane scroll = new JScrollPane(paymentTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220,220,220)));
        p.add(scroll, BorderLayout.CENTER);
        p.add(revLabel, BorderLayout.SOUTH);
        return p;
    }

    private void loadRentals(String kw) {
        rentalModel.setRowCount(0);
        List<Rental> list = kw.isEmpty() ? service.getAllRentals() : service.searchRentals(kw);
        for (Rental r : list) {
            rentalModel.addRow(new Object[]{
                r.getRentalId(), r.getCustomerName(), r.getVehicleName(),
                r.getVehicleRegNo(), r.getStartDate(), r.getEndDate(),
                r.getActualReturnDate(),
                String.format("₹ %.2f", r.getTotalAmount()),
                String.format("₹ %.2f", r.getFineAmount()),
                r.getStatus()
            });
        }
    }

    private void loadPayments(String kw) {
        paymentModel.setRowCount(0);
        List<Payment> list = kw.isEmpty() ? service.getAllPayments() : service.searchPayments(kw);
        for (Payment p : list) {
            paymentModel.addRow(new Object[]{
                p.getPaymentId(), p.getRentalId(),
                p.getCustomerName(), p.getVehicleName(),
                String.format("₹ %.2f", p.getAmountPaid()),
                p.getPaymentMethod(), p.getPaymentDate()
            });
        }
    }
}
