package net.minecraft.data.report;

import com.mojang.brigadier.CommandDispatcher;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CommandSyntaxProvider implements DataProvider {
	private final DataOutput output;
	private final CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture;

	public CommandSyntaxProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		this.output = output;
		this.registriesFuture = registriesFuture;
	}

	@Override
	public CompletableFuture<?> run(DataWriter writer) {
		Path path = this.output.resolvePath(DataOutput.OutputType.REPORTS).resolve("commands.json");
		return this.registriesFuture
			.thenCompose(
				registries -> {
					CommandDispatcher<ServerCommandSource> commandDispatcher = new CommandManager(
							CommandManager.RegistrationEnvironment.ALL, CommandManager.createRegistryAccess(registries)
						)
						.getDispatcher();
					return DataProvider.writeToPath(writer, ArgumentHelper.toJson(commandDispatcher, commandDispatcher.getRoot()), path);
				}
			);
	}

	@Override
	public final String getName() {
		return "Command Syntax";
	}
}
