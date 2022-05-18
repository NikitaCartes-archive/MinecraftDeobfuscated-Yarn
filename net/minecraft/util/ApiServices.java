/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.util.UserCache;

public record ApiServices(MinecraftSessionService sessionService, SignatureVerifier serviceSignatureVerifier, GameProfileRepository profileRepository, UserCache userCache) {
    private static final String USER_CACHE_FILE_NAME = "usercache.json";

    public static ApiServices create(YggdrasilAuthenticationService authenticationService, File rootDirectory) {
        MinecraftSessionService minecraftSessionService = authenticationService.createMinecraftSessionService();
        GameProfileRepository gameProfileRepository = authenticationService.createProfileRepository();
        UserCache userCache = new UserCache(gameProfileRepository, new File(rootDirectory, USER_CACHE_FILE_NAME));
        SignatureVerifier signatureVerifier = SignatureVerifier.create(authenticationService.getServicesKey());
        return new ApiServices(minecraftSessionService, signatureVerifier, gameProfileRepository, userCache);
    }
}

