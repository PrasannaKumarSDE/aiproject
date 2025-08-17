package com.example.transcript_summarizer.Controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.transcript_summarizer.Sevices.AIService;
import com.example.transcript_summarizer.Sevices.EmailService;
import com.example.transcript_summarizer.Sevices.FileService;
import com.example.transcript_summarizer.dto.EmailRequest;
import com.example.transcript_summarizer.dto.SummarizeRequest;
import com.example.transcript_summarizer.dto.UploadResponse;

@RestController
@RequestMapping("/api")
public class TranscriptController {

    private static final Logger log = LoggerFactory.getLogger(TranscriptController.class);

    private final FileService fileService;
    private final AIService aiService;
    private final EmailService emailService;

    public TranscriptController(FileService fileService, AIService aiService, EmailService emailService) {
        this.fileService = fileService;
        this.aiService = aiService;
        this.emailService = emailService;
    }

    /** Upload a file OR paste text. */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestPart(value = "file", required = false) MultipartFile file,
                                    @RequestPart(value = "text", required = false) String pastedText) {
        try {
            String extracted = null;
            if (file != null && !file.isEmpty()) {
                extracted = fileService.extractText(file);
                log.info("Uploaded file: {}", file.getOriginalFilename());
            } else if (pastedText != null && !pastedText.trim().isEmpty()) {
                extracted = pastedText.trim();
                log.info("Used pasted text input");
            } else {
                return ResponseEntity.badRequest().body("Either 'file' or 'text' must be provided.");
            }
            return ResponseEntity.ok(new UploadResponse(extracted));
        } catch (Exception e) {
            log.error("Upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error extracting text: " + e.getMessage());
        }
    }

    /** Generate AI summary from transcript + instruction. */
    @PostMapping(value = "/summarize", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> summarize(@Valid @RequestBody SummarizeRequest req) {
        try {
            String summary = aiService.summarize(req.getTranscript(), req.getInstruction());
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            log.error("Summarization failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate summary: " + e.getMessage());
        }
    }

    /** Send the (edited) summary to recipients via email. */
    @PostMapping(value = "/send-email", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendEmail(@Valid @RequestBody EmailRequest req) {
        try {
            emailService.sendPlainText(req.getTo(), req.getSubject(), req.getBody());
            return ResponseEntity.ok("Email sent to: " + req.getTo());
        } catch (Exception e) {
            log.error("Email sending failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }

    /** Simple health check */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
