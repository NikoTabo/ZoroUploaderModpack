package com.zorold.zorouploader;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("zu")
                .then(Commands.literal("reload")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> reloadServer(context.getSource()))
                )
        );
    }

    private static int reloadServer(CommandSourceStack source) {
        ServerManager.get().reconnect();

        source.sendSystemMessage(Component.literal(ZoroUploader.MODID + " server successfully reloaded."));

        return 1;
    }
}