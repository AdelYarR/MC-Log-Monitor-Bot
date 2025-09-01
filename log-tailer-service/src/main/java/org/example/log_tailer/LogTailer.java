package org.example.log_tailer;

import org.example.log_tailer.utils.OffsetParser;
import org.example.message_sender.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LogTailer {
    private final static Logger logger = LoggerFactory.getLogger(LogTailer.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final MessageSender messageSender;
    private String logFilePath;
    private int delay;
    private long offset;
    private String offsetFilePath;

    public LogTailer(MessageSender messageSender, String logFilePath, int delay, String offsetFilePath) {
        this.messageSender = messageSender;
        this.logFilePath = logFilePath;
        this.delay = delay;
        this.offsetFilePath = offsetFilePath;
        this.offset = OffsetParser.parseOffset(offsetFilePath);
    }

    public void start() {
        logger.info("Log tailer started sending messages...");
        scheduler.scheduleWithFixedDelay(this::readNewLines, 0, delay, TimeUnit.MILLISECONDS);
    }

    public void readNewLines() {
        try (RandomAccessFile raf = new RandomAccessFile(logFilePath, "r")) {
            raf.seek(offset);

            String line;
            while ((line = raf.readLine()) != null) {
                messageSender.sendMessage(line);
            }

            offset = raf.getFilePointer();
            OffsetParser.saveOffset(offset, offsetFilePath);
        } catch (IOException err) {
            throw new RuntimeException("Failed to run log tailer: " + err.getMessage());
        }
    }
}
