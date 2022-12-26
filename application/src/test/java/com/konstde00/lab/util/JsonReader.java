package com.konstde00.lab.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class JsonReader {

    private final Path workingDir = Path.of("", "src/test/resources/");

    public String read(String pathToFile) throws IOException {

        Path file = this.workingDir.resolve(pathToFile);

        return Files.readString(file);
    }
}
