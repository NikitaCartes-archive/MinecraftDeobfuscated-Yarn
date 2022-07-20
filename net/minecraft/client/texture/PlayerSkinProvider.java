/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinProvider {
    public static final String TEXTURES = "textures";
    private final TextureManager textureManager;
    private final File skinCacheDir;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCache;

    public PlayerSkinProvider(TextureManager textureManager, File skinCacheDir, final MinecraftSessionService sessionService) {
        this.textureManager = textureManager;
        this.skinCacheDir = skinCacheDir;
        this.sessionService = sessionService;
        this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>(){

            @Override
            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(String string) {
                GameProfile gameProfile = new GameProfile(null, "dummy_mcdummyface");
                gameProfile.getProperties().put(PlayerSkinProvider.TEXTURES, new Property(PlayerSkinProvider.TEXTURES, string, ""));
                try {
                    return sessionService.getTextures(gameProfile, false);
                } catch (Throwable throwable) {
                    return ImmutableMap.of();
                }
            }

            @Override
            public /* synthetic */ Object load(Object value) throws Exception {
                return this.load((String)value);
            }
        });
    }

    public Identifier loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type type) {
        return this.loadSkin(profileTexture, type, null);
    }

    private Identifier loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type type, @Nullable SkinTextureAvailableCallback callback) {
        String string = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
        Identifier identifier = PlayerSkinProvider.method_45033(type, string);
        AbstractTexture abstractTexture = this.textureManager.getOrDefault(identifier, MissingSprite.getMissingSpriteTexture());
        if (abstractTexture == MissingSprite.getMissingSpriteTexture()) {
            File file = new File(this.skinCacheDir, string.length() > 2 ? string.substring(0, 2) : "xx");
            File file2 = new File(file, string);
            PlayerSkinTexture playerSkinTexture = new PlayerSkinTexture(file2, profileTexture.getUrl(), DefaultSkinHelper.getTexture(), type == MinecraftProfileTexture.Type.SKIN, () -> {
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

    private static Identifier method_45033(MinecraftProfileTexture.Type type, String string) {
        String string2 = switch (type) {
            default -> throw new IncompatibleClassChangeError();
            case MinecraftProfileTexture.Type.SKIN -> "skins";
            case MinecraftProfileTexture.Type.CAPE -> "capes";
            case MinecraftProfileTexture.Type.ELYTRA -> "elytra";
        };
        return new Identifier(string2 + "/" + string);
    }

    public void loadSkin(GameProfile profile, SkinTextureAvailableCallback callback, boolean requireSecure) {
        Runnable runnable = () -> {
            HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Maps.newHashMap();
            try {
                map.putAll(this.sessionService.getTextures(profile, requireSecure));
            } catch (InsecureTextureException insecureTextureException) {
                // empty catch block
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
                    } catch (InsecureTextureException insecureTextureException) {
                        // empty catch block
                    }
                }
            }
            MinecraftClient.getInstance().execute(() -> RenderSystem.recordRenderCall(() -> ImmutableList.of(MinecraftProfileTexture.Type.SKIN, MinecraftProfileTexture.Type.CAPE).forEach(textureType -> {
                if (map.containsKey(textureType)) {
                    this.loadSkin((MinecraftProfileTexture)map.get(textureType), (MinecraftProfileTexture.Type)((Object)((Object)((Object)((Object)textureType)))), callback);
                }
            })));
        };
        Util.getMainWorkerExecutor().execute(runnable);
    }

    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile) {
        Property property = Iterables.getFirst(profile.getProperties().get(TEXTURES), null);
        if (property == null) {
            return ImmutableMap.of();
        }
        return this.skinCache.getUnchecked(property.getValue());
    }

    /**
     * {@return the ID of {@code profile}'s skin, or the default skin for the profile's
     * UUID if the skin is missing}
     */
    public Identifier loadSkin(GameProfile profile) {
        MinecraftProfileTexture minecraftProfileTexture = this.getTextures(profile).get((Object)MinecraftProfileTexture.Type.SKIN);
        if (minecraftProfileTexture != null) {
            return this.loadSkin(minecraftProfileTexture, MinecraftProfileTexture.Type.SKIN);
        }
        return DefaultSkinHelper.getTexture(DynamicSerializableUuid.getUuidFromProfile(profile));
    }

    @Environment(value=EnvType.CLIENT)
    public static interface SkinTextureAvailableCallback {
        public void onSkinTextureAvailable(MinecraftProfileTexture.Type var1, Identifier var2, MinecraftProfileTexture var3);
    }
}

