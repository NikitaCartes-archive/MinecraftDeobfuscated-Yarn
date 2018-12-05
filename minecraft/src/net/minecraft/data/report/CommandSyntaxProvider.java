package net.minecraft.data.report;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Paths;
import java.util.UUID;
import net.minecraft.class_3807;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.datafixers.Schemas;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.UserCache;

public class CommandSyntaxProvider implements DataProvider {
	private final DataGenerator root;

	public CommandSyntaxProvider(DataGenerator dataGenerator) {
		this.root = dataGenerator;
	}

	@Override
	public void run(DataCache dataCache) {
		YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
		MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
		GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
		File file = new File(this.root.getOutput().toFile(), "tmp");
		UserCache userCache = new UserCache(gameProfileRepository, new File(file, MinecraftServer.USER_CACHE_FILE.getName()));
		class_3807 lv = new class_3807(Paths.get("server.properties"));
		MinecraftServer minecraftServer = new MinecraftDedicatedServer(
			file, lv, Schemas.getFixer(), yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, userCache
		);
		minecraftServer.getCommandManager().writeCommandTree(this.root.getOutput().resolve("reports/commands.json").toFile());
	}

	@Override
	public String getName() {
		return "Command Syntax";
	}
}
