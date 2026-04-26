package com.zorold.zorouploader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerManager {
    private static final ServerManager INSTANCE = new ServerManager();
    private TcpServer server;

    public static ServerManager get() {
        return INSTANCE;
    }

    public void start() {
        if (canStartServer()) {
            server = new TcpServer(Config.PORT.get());
            server.start();
        }
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void reconnect() {
        stop();
        start();
    }

    private boolean canStartServer() {
        String raw = Config.PATH_UPLOAD.get();

        Path path = Paths.get(raw);

        if (!Files.exists(path) || raw.isBlank()) {
            ZoroUploader.LOGGER.error("Upload папка не найдена: {}", raw);
            return false;
        }

        return true;
    }
}
