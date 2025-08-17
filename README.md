AI Meeting Summary & Email Sharing App
Overview

This application allows users to upload meeting transcripts (.txt, .docx, .pdf), provide custom instructions for summarization, generate AI-based structured summaries, edit them, and share via email. The focus is on functionality and backend integration with AI services.

Features

Upload transcripts in .txt, .docx, .pdf formats

Input custom instructions/prompts (e.g., “Summarize in bullet points”)

AI-powered summarization

Editable summary directly in the frontend

Send summary via email to multiple recipients

Basic, user-friendly interface

Tech Stack

Backend: Java Spring Boot

Frontend: HTML, CSS, JavaScript

AI Service: OpenAI GPT / Groq / any AI API

Email: JavaMailSender (SMTP)

Libraries: Apache POI (.docx), PDFBox (.pdf)

Deployment: Render / Railway / Localhost

Project Structure
backend/
├── src/main/java/com/example/meeting/
│   ├── controller/
│   ├── service/
│   └── model/
├── resources/
│   └── application.properties
frontend/
├── index.html
├── script.js
├── style.css

Flowchart
flowchart TD
    A[Frontend Upload Transcript] --> B[Spring Boot Upload API]
    B --> C[Extract text (txt/docx/pdf)]
    C --> D[Send transcript + prompt to AI Service]
    D --> E[AI Generates Summary]
    E --> F[Editable Summary in Frontend]
    F --> G[User clicks Send Email]
    G --> H[Spring Boot Email Service -> SMTP -> Recipient]

APIs
1. Upload File
POST /api/upload
Body: form-data
  - file: (.txt/.docx/.pdf)
  - prompt: string
Response: Extracted text

2. Generate Summary
POST /api/summarize
Body: JSON
  - text: extracted text
Response: AI-generated summary

3. Send Email
POST /api/send-email
Body: JSON
  - summary: string
  - recipients: [email1, email2]
Response: Success / Failure

Example Requests (Postman)

Upload File:

POST http://localhost:8080/api/upload
Body → form-data:
  - file: meeting_notes.pdf


Generate Summary:

POST http://localhost:8080/api/summarize
Body → raw JSON:
{
  "text": "Extracted meeting transcript here...",
  "prompt": "Summarize in bullet points for executives"
}


Send Email:

POST http://localhost:8080/api/send-email
Body → raw JSON:
{
  "summary": "Generated AI summary here...",
  "recipients": ["example1@gmail.com", "example2@gmail.com"]
}

Frontend

Open frontend/index.html in browser

Postman Testing

Test /api/upload, /api/summarize, /api/send-email endpoints
