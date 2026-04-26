package com.zorold.zorouploader.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zorold.zorouploader.Manifest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ManifestUtils {
    public static Manifest getManifest(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("Манифест не найден: " + path);
        }

        // Читаем весь файл в строку
        String json = Files.readString(Path.of(path + "/manifest.json"));

        // Создаём Gson
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Десериализуем в объект Manifest
        Manifest manifest = gson.fromJson(json, Manifest.class);

        if (manifest == null) {
            throw new RuntimeException("Не удалось десериализовать манифест: " + path);
        }

        return manifest;
    }

    public static void setManifest(Path path, Manifest manifest) throws IOException {
        // Гарантируем, что папка существует
        Files.createDirectories(path);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        String json = gson.toJson(manifest);

        Files.writeString(Path.of(path + "/manifest.json"), json);
    }

    public static Manifest updateManifest(Path rootDir) throws IOException {
        ArrayList<Manifest.ManifestFile> files = generateFilesInfo(rootDir);
        Manifest manifest = getManifest(rootDir);
        manifest.files = files;
        setManifest(rootDir, manifest);
        return manifest;
    }

    public static ArrayList<Manifest.ManifestFile> generateFilesInfo(Path rootDir) throws IOException {
        ArrayList<Manifest.ManifestFile> files = new ArrayList<>();
        String modpackDir = rootDir + "/modpack";
        Files.walk(Path.of(modpackDir))
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try {
                        Manifest.ManifestFile file = new Manifest.ManifestFile();

                        // относительный путь
                        file.path = Path.of(modpackDir).relativize(path)
                                .toString()
                                .replace('\\', '/');

                        // размер
                        file.size = Files.size(path);

                        // хэш
                        file.sha256 = HashUtils.sha256(path);

                        files.add(file);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        return files;
    }

    public static Manifest createManifest(Path rootDir, String launcherVersion) {
        Manifest manifest = new Manifest();
        manifest.packVersion = "Скорее установите версию)";
        manifest.forgeVersionId = "Укажите forge";
        manifest.vanillaVersionId = "Укажите версию майнкрафта";
        manifest.managedPaths = new ArrayList<>();
        manifest.loaderVersion = launcherVersion;
        manifest.files = new ArrayList<>();

        try {
            manifest.files = generateFilesInfo(rootDir);
            setManifest(rootDir, manifest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return manifest;
    }
}
