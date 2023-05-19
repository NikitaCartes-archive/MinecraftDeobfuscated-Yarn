package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
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
public record ServerMetadata(
	Text description,
	Optional<ServerMetadata.Players> players,
	Optional<ServerMetadata.Version> version,
	Optional<ServerMetadata.Favicon> favicon,
	boolean secureChatEnforced
) {
	public static final Codec<ServerMetadata> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.TEXT.optionalFieldOf("description", ScreenTexts.EMPTY).forGetter(ServerMetadata::description),
					ServerMetadata.Players.CODEC.optionalFieldOf("players").forGetter(ServerMetadata::players),
					ServerMetadata.Version.CODEC.optionalFieldOf("version").forGetter(ServerMetadata::version),
					ServerMetadata.Favicon.CODEC.optionalFieldOf("favicon").forGetter(ServerMetadata::favicon),
					Codec.BOOL.optionalFieldOf("enforcesSecureChat", Boolean.valueOf(false)).forGetter(ServerMetadata::secureChatEnforced)
				)
				.apply(instance, ServerMetadata::new)
	);

	public static record Favicon(byte[] iconBytes) {
		private static final String DATA_URI_PREFIX = "data:image/png;base64,";
		public static final Codec<ServerMetadata.Favicon> CODEC = Codec.STRING.comapFlatMap(uri -> {
			if (!uri.startsWith("data:image/png;base64,")) {
				return DataResult.error(() -> "Unknown format");
			} else {
				try {
					String string = uri.substring("data:image/png;base64,".length()).replaceAll("\n", "");
					byte[] bs = Base64.getDecoder().decode(string.getBytes(StandardCharsets.UTF_8));
					return DataResult.success(new ServerMetadata.Favicon(bs));
				} catch (IllegalArgumentException var3) {
					return DataResult.error(() -> "Malformed base64 server icon");
				}
			}
		}, iconBytes -> "data:image/png;base64," + new String(Base64.getEncoder().encode(iconBytes.iconBytes), StandardCharsets.UTF_8));
	}

	public static record Players(int max, int online, List<GameProfile> sample) {
		private static final Codec<GameProfile> GAME_PROFILE_CODEC = RecordCodecBuilder.create(
			instance -> instance.group(Uuids.STRING_CODEC.fieldOf("id").forGetter(GameProfile::getId), Codec.STRING.fieldOf("name").forGetter(GameProfile::getName))
					.apply(instance, GameProfile::new)
		);
		public static final Codec<ServerMetadata.Players> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("max").forGetter(ServerMetadata.Players::max),
						Codec.INT.fieldOf("online").forGetter(ServerMetadata.Players::online),
						GAME_PROFILE_CODEC.listOf().optionalFieldOf("sample", List.of()).forGetter(ServerMetadata.Players::sample)
					)
					.apply(instance, ServerMetadata.Players::new)
		);
	}

	public static record Version(String gameVersion, int protocolVersion) {
		public static final Codec<ServerMetadata.Version> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.STRING.fieldOf("name").forGetter(ServerMetadata.Version::gameVersion),
						Codec.INT.fieldOf("protocol").forGetter(ServerMetadata.Version::protocolVersion)
					)
					.apply(instance, ServerMetadata.Version::new)
		);

		public static ServerMetadata.Version create() {
			GameVersion gameVersion = SharedConstants.getGameVersion();
			return new ServerMetadata.Version(gameVersion.getName(), gameVersion.getProtocolVersion());
		}
	}
}
