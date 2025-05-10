package ru.rtln.userservice.model;

import lombok.Getter;

@Getter
public class FileModel {

    private final String url;

    public FileModel(String url) {
        this.url = url;
    }
}
