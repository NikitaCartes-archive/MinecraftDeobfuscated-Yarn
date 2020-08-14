package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import java.util.Collection;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.SaveProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReloadCommand {
	private static final Logger field_25343 = LogManager.getLogger();

	public static void method_29480(Collection<String> collection, ServerCommandSource serverCommandSource) {
		serverCommandSource.getMinecraftServer().reloadResources(collection).exceptionally(throwable -> {
			field_25343.warn("Failed to execute reload", throwable);
			serverCommandSource.sendError(new TranslatableText("commands.reload.failure"));
			return null;
		});
	}

	private static Collection<String> method_29478(ResourcePackManager resourcePackManager, SaveProperties saveProperties, Collection<String> collection) {
		resourcePackManager.scanPacks();
		Collection<String> collection2 = Lists.<String>newArrayList(collection);
		Collection<String> collection3 = saveProperties.getDataPackSettings().getDisabled();

		for (String string : resourcePackManager.getNames()) {
			if (!collection3.contains(string) && !collection2.contains(string)) {
				collection2.add(string);
			}
		}

		return collection2;
	}

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("reload").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).executes(commandContext -> {
			ServerCommandSource serverCommandSource = commandContext.getSource();
			MinecraftServer minecraftServer = serverCommandSource.getMinecraftServer();
			ResourcePackManager resourcePackManager = minecraftServer.getDataPackManager();
			SaveProperties saveProperties = minecraftServer.getSaveProperties();
			Collection<String> collection = resourcePackManager.getEnabledNames();
			Collection<String> collection2 = method_29478(resourcePackManager, saveProperties, collection);
			serverCommandSource.sendFeedback(new TranslatableText("commands.reload.success"), true);
			method_29480(collection2, serverCommandSource);
			return 0;
		}));
	}
}
