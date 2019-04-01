package net.minecraft;

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
import net.minecraft.server.MinecraftServer;

public class class_2425 implements class_2405 {
	private static final Gson field_17169 = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private final class_2403 field_11321;

	public class_2425(class_2403 arg) {
		this.field_11321 = arg;
	}

	@Override
	public void method_10319(class_2408 arg) throws IOException {
		YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
		MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
		GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
		File file = new File(this.field_11321.method_10313().toFile(), "tmp");
		class_3312 lv = new class_3312(gameProfileRepository, new File(file, MinecraftServer.field_4588.getName()));
		class_3807 lv2 = new class_3807(Paths.get("server.properties"));
		MinecraftServer minecraftServer = new class_3176(
			file,
			lv2,
			class_3551.method_15450(),
			yggdrasilAuthenticationService,
			minecraftSessionService,
			gameProfileRepository,
			lv,
			class_3951::new,
			lv2.method_16717().field_16820
		);
		Path path = this.field_11321.method_10313().resolve("reports/commands.json");
		CommandDispatcher<class_2168> commandDispatcher = minecraftServer.method_3734().method_9235();
		class_2405.method_10320(field_17169, arg, class_2316.method_10016(commandDispatcher, commandDispatcher.getRoot()), path);
	}

	@Override
	public String method_10321() {
		return "Command Syntax";
	}
}
