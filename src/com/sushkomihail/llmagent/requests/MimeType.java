package com.sushkomihail.llmagent.requests;

public enum MimeType {
    TXT("text/plain"),
    DOC("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    DOCX("application/msword"),
    PDF("application/pdf"),
    EPUB("application/epub"),
    PPT("application/ppt"),
    PPTX("application/pptx");

    private final String title;

    MimeType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
