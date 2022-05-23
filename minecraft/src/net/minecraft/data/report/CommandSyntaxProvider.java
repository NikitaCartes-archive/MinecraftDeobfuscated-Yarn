package net.minecraft.data.report;

import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.registry.DynamicRegistryManager;

public class CommandSyntaxProvider implements DataProvider {
	private final DataGenerator generator;

	public CommandSyntaxProvider(DataGenerator generator) {
		this.generator = generator;
	}

	@Override
	public void run(DataWriter writer) throws IOException {
		Path path = this.generator.resolveRootDirectoryPath(DataGenerator.OutputType.REPORTS).resolve("commands.json");
		CommandDispatcher<ServerCommandSource> commandDispatcher = new CommandManager(
				CommandManager.RegistrationEnvironment.ALL, new CommandRegistryAccess((DynamicRegistryManager)DynamicRegistryManager.BUILTIN.get())
			)
			.getDispatcher();
		DataProvider.writeToPath(writer, ArgumentHelper.toJson(commandDispatcher, commandDispatcher.getRoot()), path);
	}

	@Override
	public String getName() {
		return "Command Syntax";
	}
}
