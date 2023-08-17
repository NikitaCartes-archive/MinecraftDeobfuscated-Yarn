package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nullables;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class PlayerSkinProvider {
	private static final String TEXTURES = "textures";
	private final LoadingCache<PlayerSkinProvider.Key, CompletableFuture<SkinTextures>> cache;
	private final PlayerSkinProvider.FileCache skinCache;
	private final PlayerSkinProvider.FileCache capeCache;
	private final PlayerSkinProvider.FileCache elytraCache;

	public PlayerSkinProvider(TextureManager textureManager, Path directory, MinecraftSessionService sessionService, Executor executor) {
		this.skinCache = new PlayerSkinProvider.FileCache(textureManager, directory, Type.SKIN);
		this.capeCache = new PlayerSkinProvider.FileCache(textureManager, directory, Type.CAPE);
		this.elytraCache = new PlayerSkinProvider.FileCache(textureManager, directory, Type.ELYTRA);
		this.cache = CacheBuilder.newBuilder()
			.expireAfterAccess(Duration.ofSeconds(15L))
			.build(new CacheLoader<PlayerSkinProvider.Key, CompletableFuture<SkinTextures>>() {
				public CompletableFuture<SkinTextures> load(PlayerSkinProvider.Key key) {
					GameProfile gameProfile = key.profile();
					return CompletableFuture.supplyAsync(() -> {
						try {
							try {
								return PlayerSkinProvider.Textures.fromMap(sessionService.getTextures(gameProfile, true), true);
							} catch (InsecurePublicKeyException var3) {
								return PlayerSkinProvider.Textures.fromMap(sessionService.getTextures(gameProfile, false), false);
							}
						} catch (Throwable var4) {
							return PlayerSkinProvider.Textures.MISSING;
						}
					}, Util.getMainWorkerExecutor()).thenComposeAsync(textures -> PlayerSkinProvider.this.fetchSkinTextures(gameProfile, textures), executor);
				}
			});
	}

	public Supplier<SkinTextures> getSkinTexturesSupplier(GameProfile profile) {
		CompletableFuture<SkinTextures> completableFuture = this.fetchSkinTextures(profile);
		SkinTextures skinTextures = DefaultSkinHelper.getTexture(profile);
		return () -> (SkinTextures)completableFuture.getNow(skinTextures);
	}

	public SkinTextures getSkinTextures(GameProfile profile) {
		SkinTextures skinTextures = (SkinTextures)this.fetchSkinTextures(profile).getNow(null);
		return skinTextures != null ? skinTextures : DefaultSkinHelper.getTexture(profile);
	}

	public CompletableFuture<SkinTextures> fetchSkinTextures(GameProfile profile) {
		return this.cache.getUnchecked(new PlayerSkinProvider.Key(profile));
	}

	CompletableFuture<SkinTextures> fetchSkinTextures(GameProfile profile, PlayerSkinProvider.Textures textures) {
		MinecraftProfileTexture minecraftProfileTexture = textures.skin();
		CompletableFuture<Identifier> completableFuture;
		SkinTextures.Model model;
		if (minecraftProfileTexture != null) {
			completableFuture = this.skinCache.get(minecraftProfileTexture);
			model = SkinTextures.Model.fromName(minecraftProfileTexture.getMetadata("model"));
		} else {
			SkinTextures skinTextures = DefaultSkinHelper.getTexture(profile);
			completableFuture = CompletableFuture.completedFuture(skinTextures.texture());
			model = skinTextures.model();
		}

		String string = Nullables.map(minecraftProfileTexture, MinecraftProfileTexture::getUrl);
		MinecraftProfileTexture minecraftProfileTexture2 = textures.cape();
		CompletableFuture<Identifier> completableFuture2 = minecraftProfileTexture2 != null
			? this.capeCache.get(minecraftProfileTexture2)
			: CompletableFuture.completedFuture(null);
		MinecraftProfileTexture minecraftProfileTexture3 = textures.elytra();
		CompletableFuture<Identifier> completableFuture3 = minecraftProfileTexture3 != null
			? this.elytraCache.get(minecraftProfileTexture3)
			: CompletableFuture.completedFuture(null);
		return CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3)
			.thenApply(
				void_ -> new SkinTextures(
						(Identifier)completableFuture.join(), string, (Identifier)completableFuture2.join(), (Identifier)completableFuture3.join(), model, textures.secure()
					)
			);
	}

	@Nullable
	static Property getTextureEntry(GameProfile profile) {
		return Iterables.getFirst(profile.getProperties().get("textures"), null);
	}

	@Environment(EnvType.CLIENT)
	static class FileCache {
		private final TextureManager textureManager;
		private final Path directory;
		private final Type type;
		private final Map<String, CompletableFuture<Identifier>> hashToTexture = new Object2ObjectOpenHashMap<>();

		FileCache(TextureManager textureManager, Path directory, Type type) {
			this.textureManager = textureManager;
			this.directory = directory;
			this.type = type;
		}

		public CompletableFuture<Identifier> get(MinecraftProfileTexture texture) {
			String string = texture.getHash();
			CompletableFuture<Identifier> completableFuture = (CompletableFuture<Identifier>)this.hashToTexture.get(string);
			if (completableFuture == null) {
				completableFuture = this.store(texture);
				this.hashToTexture.put(string, completableFuture);
			}

			return completableFuture;
		}

		private CompletableFuture<Identifier> store(MinecraftProfileTexture texture) {
			String string = Hashing.sha1().hashUnencodedChars(texture.getHash()).toString();
			Identifier identifier = this.getTexturePath(string);
			Path path = this.directory.resolve(string.length() > 2 ? string.substring(0, 2) : "xx").resolve(string);
			CompletableFuture<Identifier> completableFuture = new CompletableFuture();
			PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(
				path.toFile(), texture.getUrl(), DefaultSkinHelper.getTexture(), this.type == Type.SKIN, () -> completableFuture.complete(identifier)
			);
			this.textureManager.registerTexture(identifier, playerSkinTexture);
			return completableFuture;
		}

		private Identifier getTexturePath(String hash) {
			String string = switch (this.type) {
				case SKIN -> "skins";
				case CAPE -> "capes";
				case ELYTRA -> "elytra";
			};
			return new Identifier(string + "/" + hash);
		}
	}

	@Environment(EnvType.CLIENT)
	static record Key(GameProfile profile) {
		public boolean equals(Object o) {
			return !(o instanceof PlayerSkinProvider.Key key)
				? false
				: this.profile.getId().equals(key.profile.getId()) && Objects.equals(this.getTextureEntry(), key.getTextureEntry());
		}

		public int hashCode() {
			return this.profile.getId().hashCode() + Objects.hashCode(this.getTextureEntry()) * 31;
		}

		@Nullable
		private String getTextureEntry() {
			Property property = PlayerSkinProvider.getTextureEntry(this.profile);
			return property != null ? property.value() : null;
		}
	}

	@Environment(EnvType.CLIENT)
	static record Textures(
		@Nullable MinecraftProfileTexture skin, @Nullable MinecraftProfileTexture cape, @Nullable MinecraftProfileTexture elytra, boolean secure
	) {
		public static final PlayerSkinProvider.Textures MISSING = new PlayerSkinProvider.Textures(null, null, null, true);

		public static PlayerSkinProvider.Textures fromMap(Map<Type, MinecraftProfileTexture> textures, boolean secure) {
			return textures.isEmpty()
				? MISSING
				: new PlayerSkinProvider.Textures(
					(MinecraftProfileTexture)textures.get(Type.SKIN),
					(MinecraftProfileTexture)textures.get(Type.CAPE),
					(MinecraftProfileTexture)textures.get(Type.ELYTRA),
					secure
				);
		}
	}
}
