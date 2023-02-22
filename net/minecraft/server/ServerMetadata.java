/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

/**
 * Represents metadata sent to the client. This describes the server's message of the day, online players and the protocol version.
 */
public record ServerMetadata(Text description, Optional<Players> players, Optional<Version> version, Optional<Favicon> favicon, boolean secureChatEnforced) {
    public static final Codec<ServerMetadata> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codecs.TEXT.optionalFieldOf("description", ScreenTexts.EMPTY).forGetter(ServerMetadata::description), Players.CODEC.optionalFieldOf("players").forGetter(ServerMetadata::players), Version.CODEC.optionalFieldOf("version").forGetter(ServerMetadata::version), Favicon.CODEC.optionalFieldOf("favicon").forGetter(ServerMetadata::favicon), Codec.BOOL.optionalFieldOf("enforcesSecureChat", false).forGetter(ServerMetadata::secureChatEnforced)).apply((Applicative<ServerMetadata, ?>)instance, ServerMetadata::new));

    public record Players(int max, int online, List<GameProfile> sample) {
        private static final Codec<GameProfile> GAME_PROFILE_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Uuids.STRING_CODEC.fieldOf("id")).forGetter(GameProfile::getId), ((MapCodec)Codec.STRING.fieldOf("name")).forGetter(GameProfile::getName)).apply((Applicative<GameProfile, ?>)instance, GameProfile::new));
        public static final Codec<Players> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.INT.fieldOf("max")).forGetter(Players::max), ((MapCodec)Codec.INT.fieldOf("online")).forGetter(Players::online), GAME_PROFILE_CODEC.listOf().optionalFieldOf("sample", List.of()).forGetter(Players::sample)).apply((Applicative<Players, ?>)instance, Players::new));
    }

    public record Version(String gameVersion, int protocolVersion) {
        public static final Codec<Version> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.STRING.fieldOf("name")).forGetter(Version::gameVersion), ((MapCodec)Codec.INT.fieldOf("protocol")).forGetter(Version::protocolVersion)).apply((Applicative<Version, ?>)instance, Version::new));

        public static Version create() {
            GameVersion gameVersion = SharedConstants.getGameVersion();
            return new Version(gameVersion.getName(), gameVersion.getProtocolVersion());
        }
    }

    public record Favicon(byte[] iconBytes) {
        public static final int HEIGHT = 64;
        public static final int WIDTH = 64;
        private static final String DATA_URI_PREFIX = "data:image/png;base64,";
        public static final Codec<Favicon> CODEC = Codec.STRING.comapFlatMap(uri -> {
            if (!uri.startsWith(DATA_URI_PREFIX)) {
                return DataResult.error(() -> "Unknown format");
            }
            try {
                String string = uri.substring(DATA_URI_PREFIX.length()).replaceAll("\n", "");
                byte[] bs = Base64.getDecoder().decode(string.getBytes(StandardCharsets.UTF_8));
                return DataResult.success(new Favicon(bs));
            } catch (IllegalArgumentException illegalArgumentException) {
                return DataResult.error(() -> "Malformed base64 server icon");
            }
        }, iconBytes -> DATA_URI_PREFIX + new String(Base64.getEncoder().encode(iconBytes.iconBytes), StandardCharsets.UTF_8));
    }
}

