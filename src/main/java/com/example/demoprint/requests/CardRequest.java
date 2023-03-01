package com.example.demoprint.requests;

import lombok.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRequest {

    @NotNull
    public FilePart recto;

    @NotNull
    public FilePart verso;
}
