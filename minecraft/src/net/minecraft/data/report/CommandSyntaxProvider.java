package net.minecraft.data.report;

import com.mojang.brigadier.CommandDispatcher;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
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
	private final DataOutput output;

	public CommandSyntaxProvider(DataOutput output) {
		this.output = output;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		Path path = this.output.resolvePath(DataOutput.OutputType.REPORTS).resolve("commands.json");
		CommandDispatcher<ServerCommandSource> commandDispatcher = new CommandManager(
				CommandManager.RegistrationEnvironment.ALL,
				new CommandRegistryAccess(BuiltinRegistries.createBuiltinRegistryManager(), FeatureFlags.FEATURE_MANAGER.getFeatureSet())
			)
			.getDispatcher();
		return DataProvider.writeToPath(writer, ArgumentHelper.toJson(commandDispatcher, commandDispatcher.getRoot()), path);
	}

	@Override
	public final String getName() {
		return "Command Syntax";
	}
}
