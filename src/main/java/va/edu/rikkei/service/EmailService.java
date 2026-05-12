package va.edu.rikkei.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import va.edu.rikkei.model.entity.Booking;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // Annotation @Async giúp hàm này chạy ngầm độc lập với giao diện
    @Async
    public void sendTicketEmail(Booking booking) {
        try {
            // 1. Dữ liệu nhúng vào mã QR (Ví dụ: ID vé và tên khách)
            String qrContent = "TICKET_ID: " + booking.getId() + " | KH: " + booking.getUser().getFullName();

            // 2. Sinh ảnh QR Code (kích thước 250x250)
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 250, 250);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] qrCodeImage = pngOutputStream.toByteArray();

            // 3. Chuẩn bị nội dung Email HTML
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(booking.getUser().getEmail());
            helper.setSubject("🎫 Vé xem phim của bạn tại Smart Cinema");

            String showTimeStr = booking.getShowtime().getStartTime().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"));

            // Dùng thẻ <img src='cid:qrImage'> để chèn trực tiếp ảnh QR vào thân email
            String htmlBody = "<div style='font-family: Arial; border: 2px solid #e50914; padding: 20px; border-radius: 10px;'>"
                    + "<h2 style='color: #e50914;'>Cảm ơn bạn đã đặt vé!</h2>"
                    + "<p><b>Phim:</b> " + booking.getShowtime().getMovie().getTitle() + "</p>"
                    + "<p><b>Suất chiếu:</b> " + showTimeStr + "</p>"
                    + "<p><b>Phòng:</b> " + booking.getShowtime().getRoom().getName() + " | <b>Ghế:</b> " + booking.getSeat().getName() + "</p>"
                    + "<hr/>"
                    + "<p>Vui lòng đưa mã QR này cho nhân viên soát vé:</p>"
                    + "<img src='cid:qrImage' style='border: 1px solid #ccc; padding: 5px;'/>"
                    + "</div>";

            helper.setText(htmlBody, true);

            // 4. Đính kèm biến byte[] mã QR vào email với ID "qrImage"
            helper.addInline("qrImage", new ByteArrayResource(qrCodeImage), "image/png");

            // 5. Gửi đi
            mailSender.send(message);
            System.out.println("Đã gửi email ngầm thành công cho vé #" + booking.getId());

        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email chạy ngầm: " + e.getMessage());
        }
    }
}