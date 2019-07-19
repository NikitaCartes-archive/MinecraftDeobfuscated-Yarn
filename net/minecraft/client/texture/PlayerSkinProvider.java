/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.ImageFilter;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.SkinRemappingImageFilter;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinProvider {
    private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
    private final TextureManager textureManager;
    private final File skinCacheDir;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCache;

    public PlayerSkinProvider(TextureManager textureManager, File file, MinecraftSessionService minecraftSessionService) {
        this.textureManager = textureManager;
        this.skinCacheDir = file;
        this.sessionService = minecraftSessionService;
        this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>(){

            @Override
            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(GameProfile gameProfile) throws Exception {
                try {
                    return MinecraftClient.getInstance().getSessionService().getTextures(gameProfile, false);
                } catch (Throwable throwable) {
                    return Maps.newHashMap();
                }
            }

            @Override
            public /* synthetic */ Object load(Object object) throws Exception {
                return this.load((GameProfile)object);
            }
        });
    }

    public Identifier loadSkin(MinecraftProfileTexture minecraftProfileTexture, MinecraftProfileTexture.Type type) {
        return this.loadSkin(minecraftProfileTexture, type, null);
    }

    public Identifier loadSkin(final MinecraftProfileTexture minecraftProfileTexture, final MinecraftProfileTexture.Type type, final @Nullable SkinTextureAvailableCallback skinTextureAvailableCallback) {
        String string = Hashing.sha1().hashUnencodedChars(minecraftProfileTexture.getHash()).toString();
        final Identifier identifier = new Identifier("skins/" + string);
        Texture texture = this.textureManager.getTexture(identifier);
        if (texture != null) {
            if (skinTextureAvailableCallback != null) {
                skinTextureAvailableCallback.onSkinTextureAvailable(type, identifier, minecraftProfileTexture);
            }
        } else {
            File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
            File file2 = new File(file, string);
            final SkinRemappingImageFilter imageFilter = type == MinecraftProfileTexture.Type.SKIN ? new SkinRemappingImageFilter() : null;
            PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(file2, minecraftProfileTexture.getUrl(), DefaultSkinHelper.getTexture(), new ImageFilter(){

                @Override
                public NativeImage filterImage(NativeImage nativeImage) {
                    if (imageFilter != null) {
                        return imageFilter.filterImage(nativeImage);
                    }
                    return nativeImage;
                }

                @Override
                public void method_3238() {
                    if (imageFilter != null) {
                        imageFilter.method_3238();
                    }
                    if (skinTextureAvailableCallback != null) {
                        skinTextureAvailableCallback.onSkinTextureAvailable(type, identifier, minecraftProfileTexture);
                    }
                }
            });
            this.textureManager.registerTexture(identifier, playerSkinTexture);
        }
        return identifier;
    }

    public void loadSkin(GameProfile gameProfile, SkinTextureAvailableCallback skinTextureAvailableCallback, boolean bl) {
        EXECUTOR_SERVICE.submit(() -> {
            HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Maps.newHashMap();
            try {
                map.putAll(this.sessionService.getTextures(gameProfile, bl));
            } catch (InsecureTextureException insecureTextureException) {
                // empty catch block
            }
            if (map.isEmpty()) {
                gameProfile.getProperties().clear();
                if (gameProfile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {
                    gameProfile.getProperties().putAll(MinecraftClient.getInstance().getSessionProperties());
                    map.putAll(this.sessionService.getTextures(gameProfile, false));
                } else {
                    this.sessionService.fillProfileProperties(gameProfile, bl);
                    try {
                        map.putAll(this.sessionService.getTextures(gameProfile, bl));
                    } catch (InsecureTextureException insecureTextureException) {
                        // empty catch block
                    }
                }
            }
            MinecraftClient.getInstance().execute(() -> {
                if (map.containsKey((Object)MinecraftProfileTexture.Type.SKIN)) {
                    this.loadSkin((MinecraftProfileTexture)map.get((Object)MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN, skinTextureAvailableCallback);
                }
                if (map.containsKey((Object)MinecraftProfileTexture.Type.CAPE)) {
                    this.loadSkin((MinecraftProfileTexture)map.get((Object)MinecraftProfileTexture.Type.CAPE), MinecraftProfileTexture.Type.CAPE, skinTextureAvailableCallback);
                }
            });
        });
    }

    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile gameProfile) {
        return this.skinCache.getUnchecked(gameProfile);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface SkinTextureAvailableCallback {
        public void onSkinTextureAvailable(MinecraftProfileTexture.Type var1, Identifier var2, MinecraftProfileTexture var3);
    }
}

