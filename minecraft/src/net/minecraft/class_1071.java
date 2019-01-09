package net.minecraft;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1071 {
	private static final ExecutorService field_5307 = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue());
	private final class_1060 field_5304;
	private final File field_5305;
	private final MinecraftSessionService field_5308;
	private final LoadingCache<GameProfile, Map<Type, MinecraftProfileTexture>> field_5306;

	public class_1071(class_1060 arg, File file, MinecraftSessionService minecraftSessionService) {
		this.field_5304 = arg;
		this.field_5305 = file;
		this.field_5308 = minecraftSessionService;
		this.field_5306 = CacheBuilder.newBuilder()
			.expireAfterAccess(15L, TimeUnit.SECONDS)
			.build(new CacheLoader<GameProfile, Map<Type, MinecraftProfileTexture>>() {
				public Map<Type, MinecraftProfileTexture> method_4657(GameProfile gameProfile) throws Exception {
					try {
						return class_310.method_1551().method_1495().getTextures(gameProfile, false);
					} catch (Throwable var3) {
						return Maps.<Type, MinecraftProfileTexture>newHashMap();
					}
				}
			});
	}

	public class_2960 method_4656(MinecraftProfileTexture minecraftProfileTexture, Type type) {
		return this.method_4651(minecraftProfileTexture, type, null);
	}

	public class_2960 method_4651(MinecraftProfileTexture minecraftProfileTexture, Type type, @Nullable class_1071.class_1072 arg) {
		String string = Hashing.sha1().hashUnencodedChars(minecraftProfileTexture.getHash()).toString();
		final class_2960 lv = new class_2960("skins/" + string);
		class_1062 lv2 = this.field_5304.method_4619(lv);
		if (lv2 != null) {
			if (arg != null) {
				arg.onSkinTextureAvailable(type, lv, minecraftProfileTexture);
			}
		} else {
			File file = new File(this.field_5305, string.length() > 2 ? string.substring(0, 2) : "xx");
			File file2 = new File(file, string);
			final class_760 lv3 = type == Type.SKIN ? new class_764() : null;
			class_1046 lv4 = new class_1046(file2, minecraftProfileTexture.getUrl(), class_1068.method_4649(), new class_760() {
				@Override
				public class_1011 method_3237(class_1011 arg) {
					return lv3 != null ? lv3.method_3237(arg) : arg;
				}

				@Override
				public void method_3238() {
					if (lv3 != null) {
						lv3.method_3238();
					}

					if (arg != null) {
						arg.onSkinTextureAvailable(type, lv, minecraftProfileTexture);
					}
				}
			});
			this.field_5304.method_4616(lv, lv4);
		}

		return lv;
	}

	public void method_4652(GameProfile gameProfile, class_1071.class_1072 arg, boolean bl) {
		field_5307.submit(() -> {
			Map<Type, MinecraftProfileTexture> map = Maps.<Type, MinecraftProfileTexture>newHashMap();

			try {
				map.putAll(this.field_5308.getTextures(gameProfile, bl));
			} catch (InsecureTextureException var7) {
			}

			if (map.isEmpty()) {
				gameProfile.getProperties().clear();
				if (gameProfile.getId().equals(class_310.method_1551().method_1548().method_1677().getId())) {
					gameProfile.getProperties().putAll(class_310.method_1551().method_1539());
					map.putAll(this.field_5308.getTextures(gameProfile, false));
				} else {
					this.field_5308.fillProfileProperties(gameProfile, bl);

					try {
						map.putAll(this.field_5308.getTextures(gameProfile, bl));
					} catch (InsecureTextureException var6) {
					}
				}
			}

			class_310.method_1551().execute(() -> {
				if (map.containsKey(Type.SKIN)) {
					this.method_4651((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN, arg);
				}

				if (map.containsKey(Type.CAPE)) {
					this.method_4651((MinecraftProfileTexture)map.get(Type.CAPE), Type.CAPE, arg);
				}
			});
		});
	}

	public Map<Type, MinecraftProfileTexture> method_4654(GameProfile gameProfile) {
		return this.field_5306.getUnchecked(gameProfile);
	}

	@Environment(EnvType.CLIENT)
	public interface class_1072 {
		void onSkinTextureAvailable(Type type, class_2960 arg, MinecraftProfileTexture minecraftProfileTexture);
	}
}
