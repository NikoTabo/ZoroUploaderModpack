package com.zorold.zorouploader;

import com.google.gson.Gson;
import com.zorold.zorouploader.utils.ManifestUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TcpServer extends Thread{
    private ServerSocket socket;
    private final int port;
    private volatile boolean running = true;
    private final ArrayList<ClientHandler> clients = new ArrayList<>();
    private final Manifest manifest;

    public TcpServer(int port) {
        this.port = port;
        Path path = Paths.get(Config.PATH_UPLOAD.get());
        manifest = manifestGetter(path);
        ZoroUploader.LOGGER.info("Files reading successful!!!");
    }

    @Override
    public void run(){
        try {
            socket = new ServerSocket(port);
            ZoroUploader.LOGGER.info("Сервер стартанул!");
            while (running) {
                try {
                    Socket client = socket.accept();
                    DataOutputStream dos = new DataOutputStream(client.getOutputStream());

                    // Сериализуем манифест
                    Gson gson = new Gson();
                    byte[] manifestBytes =
                            gson.toJson(manifest).getBytes(StandardCharsets.UTF_8);

                    // Отправляем длину
                    dos.writeInt(manifestBytes.length);

                    // Отправляем сам JSON
                    dos.write(manifestBytes);
                    dos.flush();

                    ClientHandler handler = new ClientHandler(client);
                    clients.add(handler);
                    handler.start();
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        running = false;
        for (ClientHandler handler : clients) {
            handler.shutdown();
        }
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Manifest manifestGetter(Path path){
        Manifest manifest = null;
        try {
            manifest = ManifestUtils.updateManifest(path);
        } catch (RuntimeException e) {
            ZoroUploader.LOGGER.error(e.getMessage());
        } catch (IOException e) {
            ZoroUploader.LOGGER.warn(e.getMessage());
            manifest = ManifestUtils.createManifest(path, "2.2(neo)");
        }
        return manifest;
    }
}
