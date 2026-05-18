package util;

import model.Rental;
import java.awt.*;
import java.awt.print.*;
import javax.swing.*;

/**
 * PDFGenerator - Generates a printable rental invoice using Java's PrinterJob API.
 * In a real deployment, swap the body for an iText/Apache PDFBox call.
 */
public class PDFGenerator implements Printable {

    private final String[] invoiceLines;

    public PDFGenerator(String[] lines) {
        this.invoiceLines = lines;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        if (pageIndex > 0) return NO_SUCH_PAGE;

        Graphics2D g2 = (Graphics2D) g;
        g2.translate(pf.getImageableX(), pf.getImageableY());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int y = 40;
        g2.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2.setColor(new Color(41, 128, 185));
        g2.drawString("DriveEase – Vehicle Rental System", 60, y);
        y += 10;
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g2.drawString("─────────────────────────────────────────────", 60, y += 20);

        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2.setColor(Color.BLACK);
        for (String line : invoiceLines) {
            g2.drawString(line, 60, y += 20);
        }

        g2.setColor(Color.GRAY);
        g2.drawString("─────────────────────────────────────────────", 60, y += 20);
        g2.drawString("Thank you for choosing DriveEase!", 100, y += 20);
        return PAGE_EXISTS;
    }

    /**
     * Opens the system print dialog with the invoice content.
     * @param lines array of invoice text lines
     */
    public static void printInvoice(String[] lines) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new PDFGenerator(lines));
        if (job.printDialog()) {
            try {
                job.print();
                JOptionPane.showMessageDialog(null,
                    "Invoice sent to printer successfully!",
                    "Print Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(null,
                    "Printing failed: " + e.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
