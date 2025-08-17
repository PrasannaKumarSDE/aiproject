package com.example.transcript_summarizer.dto;

import jakarta.validation.constraints.NotBlank;

public class SummarizeRequest {

    @NotBlank(message = "transcript cannot be blank")
    private String transcript;

    private String instruction; // optional

    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }

    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }
}
