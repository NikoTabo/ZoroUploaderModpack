package com.zorold.zorouploader;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue PORT = BUILDER
            .comment("Порт TCP-сервера")
            .defineInRange("port", 25571, 1, 65535);

    public static final ModConfigSpec.ConfigValue<String> PATH_UPLOAD = BUILDER
            .comment("Путь к директории файлов загрузки")
            .define("uploadFilePath", "");

    static final ModConfigSpec SPEC = BUILDER.build();
}
