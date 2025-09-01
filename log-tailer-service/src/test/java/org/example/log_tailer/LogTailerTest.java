package org.example.log_tailer;

import org.example.message_sender.MessageSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class LogTailerTest {

    private Path logFilePath;
    private Path offsetFilePath;
    private MessageSender messageSender;
    private LogTailer tailer;

    @BeforeEach
    void setup() throws IOException {
        logFilePath = Files.createTempFile("minecraft-server", ".log");
        offsetFilePath = Files.createTempFile("offset", ".txt");
        messageSender = Mockito.mock(MessageSender.class);
        tailer = new LogTailer(messageSender, logFilePath.toString(), 500, offsetFilePath.toString());
    }

    @Test
    void shouldWriteOffsetToFile() throws IOException {
        Files.write(logFilePath, List.of("Test 1", "Test 2"));

        tailer.readNewLines();
        Files.write(logFilePath, List.of("Test 3"), StandardOpenOption.APPEND);
        tailer.readNewLines();

        verify(messageSender, times(1)).sendMessage("Test 1");
        verify(messageSender, times(1)).sendMessage("Test 2");
        verify(messageSender, times(1)).sendMessage("Test 3");

        int offsetValue = Files.readAllBytes(offsetFilePath).length;
        assertNotEquals(0, offsetValue);
    }

    @Test
    void shouldHandleEmptyLogFile() {
        tailer.readNewLines();

        verify(messageSender, never()).sendMessage(anyString());
    }

    @Test
    void shouldHandleNonExistentLogFile() throws IOException {
        tailer = new LogTailer(messageSender, "non-existent-file.log", 500, offsetFilePath.toString());

        assertThrows(RuntimeException.class, tailer::readNewLines);
    }
}
