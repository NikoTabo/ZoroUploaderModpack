package com.zorold.zorouploader;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

public class ServerEvents {

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        ServerManager.get().start();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        ServerManager.get().stop();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ZoroUploader.LOGGER.info("Registering commands for " + ZoroUploader.MODID);
        ModCommands.register(event.getDispatcher()); // Используем ваш класс ModCommands
    }
}
