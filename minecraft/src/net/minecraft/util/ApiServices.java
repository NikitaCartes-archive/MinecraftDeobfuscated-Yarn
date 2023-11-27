package net.minecraft.util;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.network.encryption.SignatureVerifier;

/**
 * A record holding session services used by the server.
 * 
 * @apiNote Individual services can be accessed using the getters in
 * {@link net.minecraft.server.MinecraftServer}.
 */
public record ApiServices(MinecraftSessionService sessionService, ServicesKeySet servicesKeySet, GameProfileRepository profileRepository, UserCache userCache) {
	private static final String USER_CACHE_FILE_NAME = "usercache.json";

	/**
	 * {@return a new API service instance}
	 * 
	 * <p>This is usually not needed; call getters on {@link
	 * net.minecraft.server.MinecraftServer} instead.
	 */
	public static ApiServices create(YggdrasilAuthenticationService authenticationService, File rootDirectory) {
		MinecraftSessionService minecraftSessionService = authenticationService.createMinecraftSessionService();
		GameProfileRepository gameProfileRepository = authenticationService.createProfileRepository();
		UserCache userCache = new UserCache(gameProfileRepository, new File(rootDirectory, "usercache.json"));
		return new ApiServices(minecraftSessionService, authenticationService.getServicesKeySet(), gameProfileRepository, userCache);
	}

	@Nullable
	public SignatureVerifier serviceSignatureVerifier() {
		return SignatureVerifier.create(this.servicesKeySet, ServicesKeyType.PROFILE_KEY);
	}

	public boolean providesProfileKeys() {
		return !this.servicesKeySet.keys(ServicesKeyType.PROFILE_KEY).isEmpty();
	}
}
