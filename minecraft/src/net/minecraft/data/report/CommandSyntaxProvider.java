package net.minecraft.data.report;

import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.registry.BuiltinRegistries;

public class CommandSyntaxProvider implements DataProvider {
	private final DataOutput field_40600;

	public CommandSyntaxProvider(DataOutput generator) {
		this.field_40600 = generator;
	}

	@Override
	public void run(DataWriter writer) throws IOException {
		Path path = this.field_40600.resolvePath(DataOutput.OutputType.REPORTS).resolve("commands.json");
		CommandDispatcher<ServerCommandSource> commandDispatcher = new CommandManager(
				CommandManager.RegistrationEnvironment.ALL,
				new CommandRegistryAccess(BuiltinRegistries.createBuiltinRegistryManager(), FeatureFlags.FEATURE_MANAGER.getFeatureSet())
			)
			.getDispatcher();
		DataProvider.writeToPath(writer, ArgumentHelper.toJson(commandDispatcher, commandDispatcher.getRoot()), path);
	}

	@Override
	public String getName() {
		return "Command Syntax";
	}
}
