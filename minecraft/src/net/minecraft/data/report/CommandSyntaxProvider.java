package net.minecraft.data.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.brigadier.CommandDispatcher;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.datafixers.Schemas;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import net.minecraft.util.UserCache;

public class CommandSyntaxProvider implements DataProvider {
	private static final Gson field_17169 = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator root;

	public CommandSyntaxProvider(DataGenerator dataGenerator) {
		this.root = dataGenerator;
	}

	@Override
	public void run(DataCache dataCache) throws IOException {
		YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
		MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
		GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
		File file = new File(this.root.getOutput().toFile(), "tmp");
		UserCache userCache = new UserCache(gameProfileRepository, new File(file, MinecraftServer.USER_CACHE_FILE.getName()));
		ServerPropertiesLoader serverPropertiesLoader = new ServerPropertiesLoader(Paths.get("server.properties"));
		MinecraftServer minecraftServer = new MinecraftDedicatedServer(
			file,
			serverPropertiesLoader,
			Schemas.getFixer(),
			yggdrasilAuthenticationService,
			minecraftSessionService,
			gameProfileRepository,
			userCache,
			WorldGenerationProgressLogger::new,
			serverPropertiesLoader.getPropertiesHandler().levelName
		);
		Path path = this.root.getOutput().resolve("reports/commands.json");
		CommandDispatcher<ServerCommandSource> commandDispatcher = minecraftServer.getCommandManager().getDispatcher();
		DataProvider.writeToPath(field_17169, dataCache, ArgumentTypes.toJson(commandDispatcher, commandDispatcher.getRoot()), path);
	}

	@Override
	public String getName() {
		return "Command Syntax";
	}
}
