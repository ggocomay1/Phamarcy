package service;

import java.awt.*;
import java.awt.print.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Logger;

import common.AppLogger;
import service.dto.PrintableInvoice;
import service.dto.PrintableInvoice.PrintLine;

/**
 * InvoicePrintService – In hóa đơn thermal / A4.
 *
 * Cách dùng:
 *   var invoice = invoicePrintService.buildPrintableInvoice(maHoaDon);
 *   invoicePrintService.print(invoice);       // mở dialog máy in
 *   invoicePrintService.preview(invoice);     // trả String text preview
 *
 * @version 1.0
 */
public class InvoicePrintService {

    private static final Logger LOG = AppLogger.get(InvoicePrintService.class);
    private static final NumberFormat VND = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final HoaDonService hoaDonSvc = new HoaDonService();

    // ================================================================
    //  BUILD PRINTABLE DTO
    // ================================================================

    /**
     * Xây dựng PrintableInvoice từ mã hóa đơn.
     */
    public PrintableInvoice buildPrintableInvoice(int maHoaDon) {
        var inv = new PrintableInvoice();
        inv.maHoaDon = "HD" + String.format("%04d", maHoaDon);

        // Lấy header từ search (1 row)
        var filter = new HoaDonService.InvoiceFilter();
        // Tìm chính xác hóa đơn
        var allRows = hoaDonSvc.searchInvoices(null);
        for (var row : allRows) {
            if (row.maHoaDon == maHoaDon) {
                inv.ngayBan = row.ngayBan != null ? row.ngayBan.format(DTF) : "";
                inv.nhanVien = row.tenNhanVien;
                inv.khachHang = row.tenKhachHang;
                inv.soDienThoai = row.sdtKhachHang != null ? row.sdtKhachHang : "";
                inv.phuongThucTT = row.phuongThucTT;
                inv.tongTien = row.tongTien != null ? row.tongTien : BigDecimal.ZERO;
                inv.soMatHang = row.soMatHang;
                break;
            }
        }

        // Lấy chi tiết
        var details = hoaDonSvc.getInvoiceDetails(maHoaDon);
        int stt = 1;
        BigDecimal subtotal = BigDecimal.ZERO;
        for (var d : details) {
            inv.lines.add(new PrintLine(stt++, d.tenSanPham, d.donViTinh, d.soLuong, d.giaBan, d.thanhTien));
            if (d.thanhTien != null) subtotal = subtotal.add(d.thanhTien);
        }
        inv.tamTinh = subtotal;
        if (inv.tongTien.compareTo(BigDecimal.ZERO) == 0) inv.tongTien = subtotal;

        return inv;
    }

    // ================================================================
    //  TEXT PREVIEW (cho màn hình / debug)
    // ================================================================

    /**
     * Tạo text preview kiểu hóa đơn thermal 58mm.
     */
    public String preview(PrintableInvoice inv) {
        var sb = new StringBuilder();
        String line = "─".repeat(40);

        sb.append("        ").append(inv.tenCuaHang).append("\n");
        if (!inv.diaChi.isEmpty()) sb.append("  ").append(inv.diaChi).append("\n");
        sb.append(line).append("\n");
        sb.append("HÓA ĐƠN BÁN HÀNG\n");
        sb.append("Số: ").append(inv.maHoaDon).append("\n");
        sb.append("Ngày: ").append(inv.ngayBan).append("\n");
        sb.append("NV: ").append(inv.nhanVien).append("\n");
        sb.append("KH: ").append(inv.khachHang != null ? inv.khachHang : "Khách lẻ").append("\n");
        sb.append(line).append("\n");

        sb.append(String.format("%-20s %5s %14s\n", "Sản phẩm", "SL", "Thành tiền"));
        sb.append(line).append("\n");

        for (var l : inv.lines) {
            String name = l.tenSanPham;
            if (name.length() > 20) name = name.substring(0, 18) + "..";
            sb.append(String.format("%-20s %5d %14s\n", name, l.soLuong, VND.format(l.thanhTien)));
        }

        sb.append(line).append("\n");
        sb.append(String.format("%-25s %14s\n", "Tạm tính:", VND.format(inv.tamTinh)));
        sb.append(String.format("%-25s %14s\n", "TỔNG CỘNG:", VND.format(inv.tongTien)));
        if (inv.tienKhachDua.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(String.format("%-25s %14s\n", "Tiền khách đưa:", VND.format(inv.tienKhachDua)));
            sb.append(String.format("%-25s %14s\n", "Tiền thừa:", VND.format(inv.tienThua)));
        }
        sb.append("PTTT: ").append(inv.phuongThucTT).append("\n");
        sb.append(line).append("\n");
        sb.append("      Cảm ơn quý khách!\n");

        return sb.toString();
    }

    // ================================================================
    //  PRINT (mở dialog máy in)
    // ================================================================

    /**
     * In hóa đơn qua Java Print API.
     */
    public boolean print(PrintableInvoice inv) {
        String text = preview(inv);
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("MEPHAR - " + inv.maHoaDon);

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2.setFont(new Font("Monospaced", Font.PLAIN, 9));

            String[] lines2 = text.split("\n");
            int y = 12;
            for (String l : lines2) {
                g2.drawString(l, 0, y);
                y += 13;
            }
            return Printable.PAGE_EXISTS;
        });

        if (job.printDialog()) {
            try {
                job.print();
                LOG.info("[PRINT] Printing invoice: " + inv.maHoaDon);
                return true;
            } catch (PrinterException e) {
                LOG.severe("[PRINT] Print failed: " + e.getMessage());
            }
        }
        return false;
    }
}
