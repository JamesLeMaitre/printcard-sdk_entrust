package com.example.demoprint.services.impls;

import com.example.demoprint.requests.CardRequest;
import com.example.demoprint.services.PrintService;
import com.example.demoprint.utils.CardPrintable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterResolution;
import javax.print.attribute.standard.Sides;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

import static com.example.demoprint.utils.JavaUtils.USER_PATH;

@Slf4j
@Service
@AllArgsConstructor
public class PrintServiceImpl implements PrintService {


    /**
     * @param requestMono
     * @return
     * @throws Exception
     */
    @Override
    public Mono<String> print(Mono<CardRequest> requestMono) {
        return requestMono.publishOn(Schedulers.boundedElastic())
                .map(request -> {
            Path userPath = Paths.get(USER_PATH + "user/images").toAbsolutePath().normalize();
            String rectoFileName = UUID.randomUUID() + "_" + request.recto.filename();
            String versoFileName = UUID.randomUUID() + "_" + request.verso.filename();
            new File(USER_PATH + "user/images").mkdirs();
            File rectoFile = new File(userPath.toString(), rectoFileName);
            File versoFile = new File(userPath.toString(), versoFileName);
            request.recto.transferTo(rectoFile).subscribe();
            request.verso.transferTo(versoFile).subscribe();
            HashMap<String, File> map = new HashMap<>();
            map.put("recto", rectoFile);
            map.put("verso", versoFile);
            return map;
        }).doOnNext(map -> {
            CardPrintable printable = new CardPrintable(map.get("recto").getPath(), map.get("verso").getPath());
            log.info("recto {}, verso {}", map.get("recto"), map.get("verso"));
            try {
                PrinterJob printJob = PrinterJob.getPrinterJob();
                PrinterResolution pr = new PrinterResolution(300, 300, 100);
                PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
                aset.add(pr);
                PageFormat pf = printJob.getPageFormat((PrintRequestAttributeSet) null);
                Paper paper = pf.getPaper();
                double paperWidth = paper.getWidth() / 72.0;
                double paperHeight = paper.getHeight() / 72.0;
                if (paperWidth < 2.0 || paperWidth > 3.5 || paperHeight < 2.0 || paperHeight > 3.5) {
                    log.info("original paper width = {}, height = {} (in inches)", paperWidth, paperHeight);
                }
                paper.setSize(153.0, 243.0);
                paper.setImageableArea(0.0, 0.0, paper.getWidth(), paper.getHeight());
                pf.setOrientation(0);

                pf.setPaper(paper);
                aset.add(new Copies(1));
                aset.add(Sides.DUPLEX);
                printJob.setPrintable(printable, pf);
                printJob.print(aset);
            } catch (PrinterException e) {
                throw new RuntimeException(e);
            }
            log.info("I'm launch");
        }).publishOn(Schedulers.boundedElastic()).map(map -> {
            try {
                Path rectoPath = Paths.get(map.get("recto").getPath());
                Path versoPath = Paths.get(map.get("verso").getPath());
                Files.deleteIfExists(rectoPath);
                Files.deleteIfExists(versoPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return "Success";
        });
    }
}
