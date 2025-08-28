package org.example.log_tailer;

import org.example.message_sender.MessageSender;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LogTailer {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final MessageSender messageSender;
    private String filePath;
    private int delay;
    private long offset;

    public LogTailer(MessageSender messageSender, String filepath, int delay, long offset) {
        this.messageSender = messageSender;
        this.filePath = filepath;
        this.delay = delay;
        this.offset = offset;
    }

    public void start() {
        scheduler.scheduleWithFixedDelay(() -> {
            try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
                raf.seek(offset);

                String line;
                while ((line = raf.readLine()) != null) {
                    messageSender.sendMessage(line);
                }

                offset = raf.getFilePointer();
                DataOutputStream dos = new DataOutputStream(new FileOutputStream("src/main/resources/offset.txt"));
                dos.writeLong(offset);
            } catch (IOException err) {
                throw new RuntimeException("Failed to run log tailer: " + err.getMessage());
            }
        }, 0, delay, TimeUnit.MILLISECONDS);
    }
}
