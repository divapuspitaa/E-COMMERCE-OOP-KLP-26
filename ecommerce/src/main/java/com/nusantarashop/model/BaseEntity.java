package com.nusantarashop.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract base class untuk semua entitas dalam aplikasi.
 * Menerapkan pilar ABSTRACTION - mendefinisikan kontrak umum.
 */
public abstract class BaseEntity {

    // Encapsulation: field private
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    protected BaseEntity(String id) {
        this.id = id;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Abstract method - wajib diimplementasi subclass (ABSTRACTION)
    public abstract String getDisplayName();
    public abstract boolean isValid();

    // Template method pattern
    public final String getSummary() {
        return String.format("[%s] %s (Created: %s)",
                getClass().getSimpleName(), getDisplayName(), createdAt.toLocalDate());
    }

    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters & Setters (ENCAPSULATION)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
