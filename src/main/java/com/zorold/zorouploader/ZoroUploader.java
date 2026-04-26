package com.zorold.zorouploader;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;

@Mod(ZoroUploader.MODID)
public class ZoroUploader {
    public static final String MODID = "zorouploader";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ZoroUploader(ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(new ServerEvents());
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        LOGGER.info("ZoroUploader Mod Initialized");
    }
}
