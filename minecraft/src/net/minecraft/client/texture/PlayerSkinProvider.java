package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.DynamicSerializableUuid;

@Environment(EnvType.CLIENT)
public class PlayerSkinProvider {
	public static final String TEXTURES = "textures";
	private final TextureManager textureManager;
	private final File skinCacheDir;
	private final MinecraftSessionService sessionService;
	private final LoadingCache<String, Map<Type, MinecraftProfileTexture>> skinCache;

	public PlayerSkinProvider(TextureManager textureManager, File skinCacheDir, MinecraftSessionService sessionService) {
		this.textureManager = textureManager;
		this.skinCacheDir = skinCacheDir;
		this.sessionService = sessionService;
		this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<String, Map<Type, MinecraftProfileTexture>>() {
			public Map<Type, MinecraftProfileTexture> load(String string) {
				GameProfile gameProfile = new GameProfile(null, "dummy_mcdummyface");
				gameProfile.getProperties().put("textures", new Property("textures", string, ""));

				try {
					return sessionService.getTextures(gameProfile, false);
				} catch (Throwable var4) {
					return ImmutableMap.of();
				}
			}
		});
	}

	public Identifier loadSkin(MinecraftProfileTexture profileTexture, Type type) {
		return this.loadSkin(profileTexture, type, null);
	}

	private Identifier loadSkin(MinecraftProfileTexture profileTexture, Type type, @Nullable PlayerSkinProvider.SkinTextureAvailableCallback callback) {
		String string = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
		Identifier identifier = method_45033(type, string);
		AbstractTexture abstractTexture = this.textureManager.getOrDefault(identifier, MissingSprite.getMissingSpriteTexture());
		if (abstractTexture == MissingSprite.getMissingSpriteTexture()) {
			File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
			File file2 = new File(file, string);
			PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(file2, profileTexture.getUrl(), DefaultSkinHelper.getTexture(), type == Type.SKIN, () -> {
				if (callback != null) {
					callback.onSkinTextureAvailable(type, identifier, profileTexture);
				}
			});
			this.textureManager.registerTexture(identifier, playerSkinTexture);
		} else if (callback != null) {
			callback.onSkinTextureAvailable(type, identifier, profileTexture);
		}

		return identifier;
	}

	private static Identifier method_45033(Type type, String string) {
		String string2 = switch (type) {
			case SKIN -> "skins";
			case CAPE -> "capes";
			case ELYTRA -> "elytra";
		};
		return new Identifier(string2 + "/" + string);
	}

	public void loadSkin(GameProfile profile, PlayerSkinProvider.SkinTextureAvailableCallback callback, boolean requireSecure) {
		Runnable runnable = () -> {
			Map<Type, MinecraftProfileTexture> map = Maps.<Type, MinecraftProfileTexture>newHashMap();

			try {
				map.putAll(this.sessionService.getTextures(profile, requireSecure));
			} catch (InsecureTextureException var7) {
			}

			if (map.isEmpty()) {
				profile.getProperties().clear();
				if (profile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {
					profile.getProperties().putAll(MinecraftClient.getInstance().getSessionProperties());
					map.putAll(this.sessionService.getTextures(profile, false));
				} else {
					this.sessionService.fillProfileProperties(profile, requireSecure);

					try {
						map.putAll(this.sessionService.getTextures(profile, requireSecure));
					} catch (InsecureTextureException var6) {
					}
				}
			}

			MinecraftClient.getInstance().execute(() -> RenderSystem.recordRenderCall(() -> ImmutableList.of(Type.SKIN, Type.CAPE).forEach(textureType -> {
						if (map.containsKey(textureType)) {
							this.loadSkin((MinecraftProfileTexture)map.get(textureType), textureType, callback);
						}
					})));
		};
		Util.getMainWorkerExecutor().execute(runnable);
	}

	public Map<Type, MinecraftProfileTexture> getTextures(GameProfile profile) {
		Property property = Iterables.getFirst(profile.getProperties().get("textures"), null);
		return (Map<Type, MinecraftProfileTexture>)(property == null ? ImmutableMap.of() : this.skinCache.getUnchecked(property.getValue()));
	}

	/**
	 * {@return the ID of {@code profile}'s skin, or the default skin for the profile's
	 * UUID if the skin is missing}
	 */
	public Identifier loadSkin(GameProfile profile) {
		MinecraftProfileTexture minecraftProfileTexture = (MinecraftProfileTexture)this.getTextures(profile).get(Type.SKIN);
		return minecraftProfileTexture != null
			? this.loadSkin(minecraftProfileTexture, Type.SKIN)
			: DefaultSkinHelper.getTexture(DynamicSerializableUuid.getUuidFromProfile(profile));
	}

	@Environment(EnvType.CLIENT)
	public interface SkinTextureAvailableCallback {
		void onSkinTextureAvailable(Type type, Identifier id, MinecraftProfileTexture texture);
	}
}
