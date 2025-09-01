package org.example.log_tailer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class OffsetParser {
    public final static Logger logger = LoggerFactory.getLogger(OffsetParser.class);

    // Метод для получения текущей позиции для считывания логов
    public static long parseOffset(String offsetFilePath) {
        File offsetFile = new File(offsetFilePath);

        if (!offsetFile.exists() || offsetFile.length() < 8) {
            logger.warn("Offset file not found or empty, starting from 0");
            return 0;
        } else {
            try (DataInputStream dis = new DataInputStream(
                    new FileInputStream(offsetFile))) {
                return dis.readLong();
            } catch (IOException err) {
                throw new RuntimeException("Failed to open offset.txt file: " + err);
            }
        }
    }

    // Метод для сохранения текущей позиции в файл
    public static void saveOffset(long offset, String offsetFilePath) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(offsetFilePath))) {
            dos.writeLong(offset);
        } catch (IOException err) {
            throw new RuntimeException("Failed to save offset to a file: " + err.getMessage());
        }
    }
}
