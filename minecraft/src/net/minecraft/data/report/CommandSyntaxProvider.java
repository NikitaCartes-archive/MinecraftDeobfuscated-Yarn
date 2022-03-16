package net.minecraft.data.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.registry.DynamicRegistryManager;

public class CommandSyntaxProvider implements DataProvider {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator generator;

	public CommandSyntaxProvider(DataGenerator generator) {
		this.generator = generator;
	}

	@Override
	public void run(DataCache cache) throws IOException {
		Path path = this.generator.getOutput().resolve("reports/commands.json");
		CommandDispatcher<ServerCommandSource> commandDispatcher = new CommandManager(
				CommandManager.RegistrationEnvironment.ALL, new CommandRegistryAccess((DynamicRegistryManager)DynamicRegistryManager.BUILTIN.get())
			)
			.getDispatcher();
		DataProvider.writeToPath(GSON, cache, ArgumentHelper.toJson(commandDispatcher, commandDispatcher.getRoot()), path);
	}

	@Override
	public String getName() {
		return "Command Syntax";
	}
}
