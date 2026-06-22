package io.ivanandreev.ai.model;

public record FileEntry(
        String name,
        String type
) {
    public boolean isDirectory() {
        return "DIRECTORY".equals(type);
    }
}
