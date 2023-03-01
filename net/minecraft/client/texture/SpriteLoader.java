/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.TextureStitcher;
import net.minecraft.client.texture.TextureStitcherCannotFitException;
import net.minecraft.client.texture.atlas.AtlasLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SpriteLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Identifier id;
    private final int maxTextureSize;
    private final int width;
    private final int height;

    public SpriteLoader(Identifier id, int maxTextureSize, int width, int height) {
        this.id = id;
        this.maxTextureSize = maxTextureSize;
        this.width = width;
        this.height = height;
    }

    public static SpriteLoader fromAtlas(SpriteAtlasTexture atlasTexture) {
        return new SpriteLoader(atlasTexture.getId(), atlasTexture.getMaxTextureSize(), atlasTexture.getWidth(), atlasTexture.getHeight());
    }

    public StitchResult stitch(List<SpriteContents> sprites, int mipLevel, Executor executor) {
        int l;
        int i = this.maxTextureSize;
        TextureStitcher<SpriteContents> textureStitcher = new TextureStitcher<SpriteContents>(i, i, mipLevel);
        int j = Integer.MAX_VALUE;
        int k = 1 << mipLevel;
        for (SpriteContents spriteContents : sprites) {
            j = Math.min(j, Math.min(spriteContents.getWidth(), spriteContents.getHeight()));
            l = Math.min(Integer.lowestOneBit(spriteContents.getWidth()), Integer.lowestOneBit(spriteContents.getHeight()));
            if (l < k) {
                LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", spriteContents.getId(), spriteContents.getWidth(), spriteContents.getHeight(), MathHelper.floorLog2(k), MathHelper.floorLog2(l));
                k = l;
            }
            textureStitcher.add(spriteContents);
        }
        int m = Math.min(j, k);
        int n = MathHelper.floorLog2(m);
        if (n < mipLevel) {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.id, mipLevel, n, m);
            l = n;
        } else {
            l = mipLevel;
        }
        try {
            textureStitcher.stitch();
        } catch (TextureStitcherCannotFitException textureStitcherCannotFitException) {
            CrashReport crashReport = CrashReport.create(textureStitcherCannotFitException, "Stitching");
            CrashReportSection crashReportSection = crashReport.addElement("Stitcher");
            crashReportSection.add("Sprites", textureStitcherCannotFitException.getSprites().stream().map(sprite -> String.format(Locale.ROOT, "%s[%dx%d]", sprite.getId(), sprite.getWidth(), sprite.getHeight())).collect(Collectors.joining(",")));
            crashReportSection.add("Max Texture Size", i);
            throw new CrashException(crashReport);
        }
        int o = Math.max(textureStitcher.getWidth(), this.width);
        int p = Math.max(textureStitcher.getHeight(), this.height);
        Map<Identifier, Sprite> map = this.collectStitchedSprites(textureStitcher, o, p);
        Sprite sprite2 = map.get(MissingSprite.getMissingSpriteId());
        CompletableFuture<Object> completableFuture = l > 0 ? CompletableFuture.runAsync(() -> map.values().forEach(sprite -> sprite.getContents().generateMipmaps(l)), executor) : CompletableFuture.completedFuture(null);
        return new StitchResult(o, p, l, sprite2, map, completableFuture);
    }

    public static CompletableFuture<List<SpriteContents>> loadAll(List<Supplier<SpriteContents>> sources, Executor executor) {
        List<CompletableFuture> list = sources.stream().map(source -> CompletableFuture.supplyAsync(source, executor)).toList();
        return Util.combineSafe(list).thenApply(sprites -> sprites.stream().filter(Objects::nonNull).toList());
    }

    public CompletableFuture<StitchResult> load(ResourceManager resourceManager, Identifier path, int mipLevel, Executor executor) {
        return ((CompletableFuture)CompletableFuture.supplyAsync(() -> AtlasLoader.of(resourceManager, path).loadSources(resourceManager), executor).thenCompose(sources -> SpriteLoader.loadAll(sources, executor))).thenApply(sprites -> this.stitch((List<SpriteContents>)sprites, mipLevel, executor));
    }

    @Nullable
    public static SpriteContents load(Identifier id, Resource resource) {
        NativeImage nativeImage;
        AnimationResourceMetadata animationResourceMetadata;
        try {
            animationResourceMetadata = resource.getMetadata().decode(AnimationResourceMetadata.READER).orElse(AnimationResourceMetadata.EMPTY);
        } catch (Exception exception) {
            LOGGER.error("Unable to parse metadata from {}", (Object)id, (Object)exception);
            return null;
        }
        try (InputStream inputStream = resource.getInputStream();){
            nativeImage = NativeImage.read(inputStream);
        } catch (IOException iOException) {
            LOGGER.error("Using missing texture, unable to load {}", (Object)id, (Object)iOException);
            return null;
        }
        SpriteDimensions spriteDimensions = animationResourceMetadata.getSize(nativeImage.getWidth(), nativeImage.getHeight());
        if (MathHelper.isMultipleOf(nativeImage.getWidth(), spriteDimensions.width()) && MathHelper.isMultipleOf(nativeImage.getHeight(), spriteDimensions.height())) {
            return new SpriteContents(id, spriteDimensions, nativeImage, animationResourceMetadata);
        }
        LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}", id, nativeImage.getWidth(), nativeImage.getHeight(), spriteDimensions.width(), spriteDimensions.height());
        nativeImage.close();
        return null;
    }

    private Map<Identifier, Sprite> collectStitchedSprites(TextureStitcher<SpriteContents> stitcher, int atlasWidth, int atlasHeight) {
        HashMap<Identifier, Sprite> map = new HashMap<Identifier, Sprite>();
        stitcher.getStitchedSprites((info, x, y) -> map.put(info.getId(), new Sprite(this.id, (SpriteContents)info, atlasWidth, atlasHeight, x, y)));
        return map;
    }

    @Environment(value=EnvType.CLIENT)
    public record StitchResult(int width, int height, int mipLevel, Sprite missing, Map<Identifier, Sprite> regions, CompletableFuture<Void> readyForUpload) {
        public CompletableFuture<StitchResult> whenComplete() {
            return this.readyForUpload.thenApply(void_ -> this);
        }
    }
}

