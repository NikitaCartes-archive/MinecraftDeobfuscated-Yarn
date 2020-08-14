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

@Environment(EnvType.CLIENT)
public class PlayerSkinProvider {
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
		Identifier identifier = new Identifier("skins/" + string);
		AbstractTexture abstractTexture = this.textureManager.getTexture(identifier);
		if (abstractTexture != null) {
			if (callback != null) {
				callback.onSkinTextureAvailable(type, identifier, profileTexture);
			}
		} else {
			File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
			File file2 = new File(file, string);
			PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(file2, profileTexture.getUrl(), DefaultSkinHelper.getTexture(), type == Type.SKIN, () -> {
				if (callback != null) {
					callback.onSkinTextureAvailable(type, identifier, profileTexture);
				}
			});
			this.textureManager.registerTexture(identifier, playerSkinTexture);
		}

		return identifier;
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

			MinecraftClient.getInstance().execute(() -> RenderSystem.recordRenderCall(() -> ImmutableList.of(Type.SKIN, Type.CAPE).forEach(type -> {
						if (map.containsKey(type)) {
							this.loadSkin((MinecraftProfileTexture)map.get(type), type, callback);
						}
					})));
		};
		Util.getMainWorkerExecutor().execute(runnable);
	}

	public Map<Type, MinecraftProfileTexture> getTextures(GameProfile gameProfile) {
		Property property = Iterables.getFirst(gameProfile.getProperties().get("textures"), null);
		return (Map<Type, MinecraftProfileTexture>)(property == null ? ImmutableMap.of() : this.skinCache.getUnchecked(property.getValue()));
	}

	@Environment(EnvType.CLIENT)
	public interface SkinTextureAvailableCallback {
		void onSkinTextureAvailable(Type type, Identifier identifier, MinecraftProfileTexture texture);
	}
}
