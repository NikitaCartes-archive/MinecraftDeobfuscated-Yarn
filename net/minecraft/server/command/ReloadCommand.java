/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.SaveProperties;
import org.slf4j.Logger;

public class ReloadCommand {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void tryReloadDataPacks(Collection<String> dataPacks, ServerCommandSource source) {
        source.getServer().reloadResources(dataPacks).exceptionally(throwable -> {
            LOGGER.warn("Failed to execute reload", (Throwable)throwable);
            source.sendError(new TranslatableText("commands.reload.failure"));
            return null;
        });
    }

    private static Collection<String> findNewDataPacks(ResourcePackManager dataPackManager, SaveProperties saveProperties, Collection<String> enabledDataPacks) {
        dataPackManager.scanPacks();
        ArrayList<String> collection = Lists.newArrayList(enabledDataPacks);
        List<String> collection2 = saveProperties.getDataPackSettings().getDisabled();
        for (String string : dataPackManager.getNames()) {
            if (collection2.contains(string) || collection.contains(string)) continue;
            collection.add(string);
        }
        return collection;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("reload").requires(source -> source.hasPermissionLevel(2))).executes(context -> {
            ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
            MinecraftServer minecraftServer = serverCommandSource.getServer();
            ResourcePackManager resourcePackManager = minecraftServer.getDataPackManager();
            SaveProperties saveProperties = minecraftServer.getSaveProperties();
            Collection<String> collection = resourcePackManager.getEnabledNames();
            Collection<String> collection2 = ReloadCommand.findNewDataPacks(resourcePackManager, saveProperties, collection);
            serverCommandSource.sendFeedback(new TranslatableText("commands.reload.success"), true);
            ReloadCommand.tryReloadDataPacks(collection2, serverCommandSource);
            return 0;
        }));
    }
}

