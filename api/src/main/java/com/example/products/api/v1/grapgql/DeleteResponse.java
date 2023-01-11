package com.example.products.api.v1.grapgql;

public class DeleteResponse {

    private boolean deleted;

    public DeleteResponse(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
