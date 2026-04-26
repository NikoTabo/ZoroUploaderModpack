package com.zorold.zorouploader;

import java.util.List;

public class Manifest {
    // Версия сборки
    public String packVersion;
    // Версия лаунчера/лоадера
    public String loaderVersion;
    public String forgeVersionId;
    public String vanillaVersionId;
    // Управляемые лаунчером пути
    public List<String> managedPaths;
    // Список файлов сборки
    public List<ManifestFile> files;

    public static class ManifestFile {
        public String path;
        public long size;
        public String sha256;

        public ManifestFile(String path, long size, String sha256) {
            this.path = path;
            this.size = size;
            this.sha256 = sha256;
        }

        public ManifestFile(){}
    }
}
