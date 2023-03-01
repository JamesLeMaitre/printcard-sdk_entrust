package com.example.demoprint;

import com.example.demoprint.services.PrintService;
import com.example.demoprint.services.impls.PrintServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
@AllArgsConstructor
class DemoprintApplicationTests {

    @Test
    void contextLoads() throws Exception {
        PrintService service = new PrintServiceImpl();
//        service.print();
    }

}
