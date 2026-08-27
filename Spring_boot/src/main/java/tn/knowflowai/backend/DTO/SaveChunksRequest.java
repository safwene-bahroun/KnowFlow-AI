package tn.knowflowai.backend.DTO;

import java.util.List;

public class SaveChunksRequest {

    private List<ChunkRequest> chunks;

    public SaveChunksRequest() {
    }

    public List<ChunkRequest> getChunks() {
        return chunks;
    }

    public void setChunks(List<ChunkRequest> chunks) {
        this.chunks = chunks;
    }
}