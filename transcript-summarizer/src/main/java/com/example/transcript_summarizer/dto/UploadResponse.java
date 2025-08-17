package com.example.transcript_summarizer.dto;

public class UploadResponse {
    private String transcript;

    public UploadResponse() {}
    public UploadResponse(String transcript) { this.transcript = transcript; }

    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }
}
