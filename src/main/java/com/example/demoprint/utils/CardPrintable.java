package com.example.demoprint.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterGraphics;
import java.io.File;
import java.net.URL;
import java.util.Objects;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardPrintable implements Printable {

    private String rectoFilePath;

    private String versoFilePath;

    /**
     * Prints the page at the specified index into the specified
     * {@link Graphics} context in the specified
     * format.  A {@code PrinterJob} calls the
     * {@code Printable} interface to request that a page be
     * rendered into the context specified by
     * {@code graphics}.  The format of the page to be drawn is
     * specified by {@code pageFormat}.  The zero based index
     * of the requested page is specified by {@code pageIndex}.
     * If the requested page does not exist then this method returns
     * NO_SUCH_PAGE; otherwise PAGE_EXISTS is returned.
     * The {@code Graphics} class or subclass implements the
     * {@link PrinterGraphics} interface to provide additional
     * information.  If the {@code Printable} object
     * aborts the print job then it throws a {@link PrinterException}.
     *
     * @param graphics   the context into which the page is drawn
     * @param pageFormat the size and orientation of the page being drawn
     * @param pageIndex  the zero based index of the page to be drawn
     * @return PAGE_EXISTS if the page is rendered successfully
     * or NO_SUCH_PAGE if {@code pageIndex} specifies a
     * non-existent page.
     * @throws PrinterException thrown when the print job is terminated.
     */
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        int imgStartX = 0;
        int imgStartY = 0;
        if (pageIndex > 1) {
            log.info(" Print job has been sent to spooler.");
            return 1;
        } else {
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setFont(new Font((String) null, 0, 8));
//            int printerDPI = true;
            graphics2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            double pageWidth = pageFormat.getWidth() * 300.0 / 72.0;
            double pageHeight = pageFormat.getHeight() * 300.0 / 72.0;
            if (pageIndex == 0) {
                graphics2D.setColor(Color.black);
                log.info("recto");
                this.drawImage(graphics2D, pageFormat, rectoFilePath, imgStartX, imgStartY, (int) pageWidth, (int) pageHeight, pageIndex);
                return 0;
            } else {
                if (pageIndex == 1) {
                    graphics2D.setColor(Color.black);
                    log.info("verso");
                    this.drawImage(graphics2D, pageFormat, versoFilePath, imgStartX, imgStartY, (int) pageWidth, (int) pageHeight, pageIndex);
                    return 0;
                }
                return 1;
            }
        }
    }

    private void drawImage(Graphics2D graphics2D, PageFormat pageFormat, String fileName, int x, int y, int pageWidth, int pageHeight, int pageNumber) {
        BufferedImage img = this.getSystemImage(fileName);
        if (img != null) {
            if (pageNumber == 1) {
                img = rotateImage180(img);
            }

            int w = img.getWidth();
            int h = img.getHeight();
            int destW = (int) ((double) w * 0.3) + x;
            int destH = (int) ((double) h * 0.3) + y;
            if (w > pageWidth) {
                destW = pageWidth;
            }

            if (h > pageHeight) {
                destH = pageHeight;
            }

            graphics2D.drawImage(img, x, y, destW, destH, 0, 0, w, h, (ImageObserver) null);
        }
    }

    private BufferedImage getSystemImage(String filename) {
        if (filename != null && filename.length() != 0) {
            File file = new File(filename);
            URL url = ClassLoader.getSystemResource(filename);
            if (!file.exists()) {
                log.info("  Unable to locate file: " + filename);
                return null;
            } else {
                try {
                    BufferedImage bufImage = ImageIO.read(file);
                    if (bufImage.getWidth() != 0 && bufImage.getHeight() != 0) {
                        return bufImage;
                    } else {
                        log.info("  image width / Height = 0");
                        return null;
                    }
                } catch (Exception var4) {
                    log.info("{}", var4.getMessage());
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    private static BufferedImage rotateImage180(BufferedImage image) {
        if (image == null) {
            return null;
        } else {
            float x = (float) image.getWidth() / 2.0F;
            float y = (float) image.getHeight() / 2.0F;
            return rotateImage(image, 180, x, y);
        }
    }

    private static BufferedImage rotateImage(BufferedImage image, int angle, float x, float y) {
        int newWidth = angle == 180 ? image.getWidth() : image.getHeight();
        int newHeight = angle == 180 ? image.getHeight() : image.getWidth();
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, 2);
        double radians = 6.283185307179586 * (double) angle / 360.0;
        AffineTransform tx = new AffineTransform();
        tx.rotate(radians, (double) x, (double) y);
        return applyTransform(image, newImage, tx);
    }

    private static BufferedImage applyTransform(BufferedImage sourceImage, BufferedImage destImage, AffineTransform tx) {
        Graphics2D g2 = destImage.createGraphics();
        g2.transform(tx);
        g2.drawImage(sourceImage, (BufferedImageOp) null, 0, 0);
        g2.dispose();
        return destImage;
    }
}
