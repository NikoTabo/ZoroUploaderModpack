package com.zorold.zorouploader;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("zu")
                .then(Commands.literal("reload")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> reloadServer())
                )
        );
    }

    private static int reloadServer() {
        ServerManager.get().reconnect();
        return 1;
    }
}