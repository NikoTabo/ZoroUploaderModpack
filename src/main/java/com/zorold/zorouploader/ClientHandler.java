package com.zorold.zorouploader;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends Thread {
    private Socket socket;
    private boolean running = true;

    // Объявляем потоки здесь, чтобы не пересоздавать их постоянно
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            // Оборачиваем в буфер для скорости!
            this.inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            shutdown();
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Читаем команду
                String message = inputStream.readUTF();

                if (message.equals("~get_modpack")) {
                    Path modpackPath = Paths.get(Config.PATH_UPLOAD.get()).resolve("modpack");
                    sendFiles(modpackPath.toString());
                } else if (message.equals("~ping")) {
                    outputStream.writeUTF("~pong");
                    outputStream.flush(); // Обязательно проталкиваем буфер
                } else {
                    shutdown();
                }
            } catch (IOException e) {
                shutdown();
                ZoroUploader.LOGGER.info("Client disconnected: " + socket.getInetAddress());
            }
        }
    }

    public void shutdown() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }

    public void sendFiles(String path) {
        try {
            File dir = new File(path);

            // Ждем от клиента список ТОЛЬКО тех файлов, которые ему нужны
            int filesNeededCount = inputStream.readInt();
            List<String> filesToSend = new ArrayList<>();
            for (int i = 0; i < filesNeededCount; i++) {
                filesToSend.add(inputStream.readUTF());
            }

            // Отправляем запрошенные файлы сплошным потоком (без ожидания ~yep)
            byte[] buffer = new byte[8192]; // Буфер 8 КБ
            for (String relativePath : filesToSend) {
                File file = new File(dir, relativePath);
                if (file.exists()) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        int count;
                        while ((count = fis.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, count);
                        }
                    }
                }
            }
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
            shutdown();
        }
    }
}