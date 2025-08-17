package com.example.transcript_summarizer.Sevices;



import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class FileService {
    private final Tika tika = new Tika();

    public String extractText(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) return "";
        try (InputStream in = file.getInputStream()) {
            String text = tika.parseToString(in);
            return text == null ? "" : text.trim();
        }
    }
}
