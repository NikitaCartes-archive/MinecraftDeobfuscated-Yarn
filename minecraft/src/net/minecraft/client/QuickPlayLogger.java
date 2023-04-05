package net.minecraft.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class QuickPlayLogger {
	private static final QuickPlayLogger NOOP = new QuickPlayLogger("") {
		@Override
		public void save(MinecraftClient client) {
		}

		@Override
		public void setWorld(QuickPlayLogger.WorldType worldType, String id, String name) {
		}
	};
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Gson GSON = new GsonBuilder().create();
	private final Path path;
	@Nullable
	private QuickPlayLogger.QuickPlayWorld world;

	QuickPlayLogger(String relativePath) {
		this.path = MinecraftClient.getInstance().runDirectory.toPath().resolve(relativePath);
	}

	public static QuickPlayLogger create(@Nullable String relativePath) {
		return relativePath == null ? NOOP : new QuickPlayLogger(relativePath);
	}

	public void setWorld(QuickPlayLogger.WorldType worldType, String id, String name) {
		this.world = new QuickPlayLogger.QuickPlayWorld(worldType, id, name);
	}

	public void save(MinecraftClient client) {
		if (client.interactionManager != null && this.world != null) {
			Util.getIoWorkerExecutor()
				.execute(
					() -> {
						try {
							Files.deleteIfExists(this.path);
						} catch (IOException var3) {
							LOGGER.error("Failed to delete quickplay log file {}", this.path, var3);
						}

						QuickPlayLogger.Log log = new QuickPlayLogger.Log(this.world, Instant.now(), client.interactionManager.getCurrentGameMode());
						Codec.list(QuickPlayLogger.Log.CODEC)
							.encodeStart(JsonOps.INSTANCE, List.of(log))
							.resultOrPartial(Util.addPrefix("Quick Play: ", LOGGER::error))
							.ifPresent(json -> {
								try {
									Files.createDirectories(this.path.getParent());
									Files.writeString(this.path, GSON.toJson(json));
								} catch (IOException var3x) {
									LOGGER.error("Failed to write to quickplay log file {}", this.path, var3x);
								}
							});
					}
				);
		} else {
			LOGGER.error("Failed to log session for quickplay. Missing world data or gamemode");
		}
	}

	@Environment(EnvType.CLIENT)
	static record Log(QuickPlayLogger.QuickPlayWorld quickPlayWorld, Instant lastPlayedTime, GameMode gameMode) {
		public static final Codec<QuickPlayLogger.Log> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						QuickPlayLogger.QuickPlayWorld.CODEC.forGetter(QuickPlayLogger.Log::quickPlayWorld),
						Codecs.INSTANT.fieldOf("lastPlayedTime").forGetter(QuickPlayLogger.Log::lastPlayedTime),
						GameMode.CODEC.fieldOf("gamemode").forGetter(QuickPlayLogger.Log::gameMode)
					)
					.apply(instance, QuickPlayLogger.Log::new)
		);
	}

	@Environment(EnvType.CLIENT)
	static record QuickPlayWorld(QuickPlayLogger.WorldType type, String id, String name) {
		public static final MapCodec<QuickPlayLogger.QuickPlayWorld> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						QuickPlayLogger.WorldType.CODEC.fieldOf("type").forGetter(QuickPlayLogger.QuickPlayWorld::type),
						Codec.STRING.fieldOf("id").forGetter(QuickPlayLogger.QuickPlayWorld::id),
						Codec.STRING.fieldOf("name").forGetter(QuickPlayLogger.QuickPlayWorld::name)
					)
					.apply(instance, QuickPlayLogger.QuickPlayWorld::new)
		);
	}

	@Environment(EnvType.CLIENT)
	public static enum WorldType implements StringIdentifiable {
		SINGLEPLAYER("singleplayer"),
		MULTIPLAYER("multiplayer"),
		REALMS("realms");

		static final com.mojang.serialization.Codec<QuickPlayLogger.WorldType> CODEC = StringIdentifiable.createCodec(QuickPlayLogger.WorldType::values);
		private final String id;

		private WorldType(String id) {
			this.id = id;
		}

		@Override
		public String asString() {
			return this.id;
		}
	}
}
