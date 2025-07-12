package com.sushkomihail.llmagent.requests;

public abstract class LlmAgentWithFileRequest extends LlmAgentRequest {
    private final MimeType mimeType;
    private final String filePath;

    public LlmAgentWithFileRequest(MimeType mimeType, String request, String filePath) {
        super(request);
        this.mimeType = mimeType;
        this.filePath = filePath;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public String getFilePath() {
        return filePath;
    }
}
