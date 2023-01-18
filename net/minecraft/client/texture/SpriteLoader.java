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

    public SpriteLoader(Identifier id, int maxTextureSize) {
        this.id = id;
        this.maxTextureSize = maxTextureSize;
    }

    public static SpriteLoader fromAtlas(SpriteAtlasTexture atlasTexture) {
        return new SpriteLoader(atlasTexture.getId(), atlasTexture.getMaxTextureSize());
    }

    public StitchResult method_47663(List<SpriteContents> list, int i, Executor executor) {
        int m;
        int j = this.maxTextureSize;
        TextureStitcher<SpriteContents> textureStitcher = new TextureStitcher<SpriteContents>(j, j, i);
        int k = Integer.MAX_VALUE;
        int l = 1 << i;
        for (SpriteContents spriteContents : list) {
            k = Math.min(k, Math.min(spriteContents.getWidth(), spriteContents.getHeight()));
            m = Math.min(Integer.lowestOneBit(spriteContents.getWidth()), Integer.lowestOneBit(spriteContents.getHeight()));
            if (m < l) {
                LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", spriteContents.getId(), spriteContents.getWidth(), spriteContents.getHeight(), MathHelper.floorLog2(l), MathHelper.floorLog2(m));
                l = m;
            }
            textureStitcher.add(spriteContents);
        }
        int n = Math.min(k, l);
        int o = MathHelper.floorLog2(n);
        if (o < i) {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.id, i, o, n);
            m = o;
        } else {
            m = i;
        }
        try {
            textureStitcher.stitch();
        } catch (TextureStitcherCannotFitException textureStitcherCannotFitException) {
            CrashReport crashReport = CrashReport.create(textureStitcherCannotFitException, "Stitching");
            CrashReportSection crashReportSection = crashReport.addElement("Stitcher");
            crashReportSection.add("Sprites", textureStitcherCannotFitException.getSprites().stream().map(sprite -> String.format(Locale.ROOT, "%s[%dx%d]", sprite.getId(), sprite.getWidth(), sprite.getHeight())).collect(Collectors.joining(",")));
            crashReportSection.add("Max Texture Size", j);
            throw new CrashException(crashReport);
        }
        Map<Identifier, Sprite> map = this.collectStitchedSprites(textureStitcher);
        Sprite sprite2 = map.get(MissingSprite.getMissingSpriteId());
        CompletableFuture<Object> completableFuture = m > 0 ? CompletableFuture.runAsync(() -> map.values().forEach(sprite -> sprite.getContents().generateMipmaps(m)), executor) : CompletableFuture.completedFuture(null);
        return new StitchResult(textureStitcher.getWidth(), textureStitcher.getHeight(), m, sprite2, map, completableFuture);
    }

    public static CompletableFuture<List<SpriteContents>> method_47664(List<Supplier<SpriteContents>> list2, Executor executor) {
        List<CompletableFuture> list22 = list2.stream().map(supplier -> CompletableFuture.supplyAsync(supplier, executor)).toList();
        return Util.combineSafe(list22).thenApply(list -> list.stream().filter(Objects::nonNull).toList());
    }

    public CompletableFuture<StitchResult> method_47661(ResourceManager resourceManager, Identifier identifier, int i, Executor executor) {
        return ((CompletableFuture)CompletableFuture.supplyAsync(() -> AtlasLoader.of(resourceManager, identifier).loadSources(resourceManager), executor).thenCompose(list -> SpriteLoader.method_47664(list, executor))).thenApply(list -> this.method_47663((List<SpriteContents>)list, i, executor));
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

    private Map<Identifier, Sprite> collectStitchedSprites(TextureStitcher<SpriteContents> stitcher) {
        HashMap<Identifier, Sprite> map = new HashMap<Identifier, Sprite>();
        int i = stitcher.getWidth();
        int j = stitcher.getHeight();
        stitcher.getStitchedSprites((info, width, height) -> map.put(info.getId(), new Sprite(this.id, (SpriteContents)info, i, j, width, height)));
        return map;
    }

    @Environment(value=EnvType.CLIENT)
    public record StitchResult(int width, int height, int mipLevel, Sprite missing, Map<Identifier, Sprite> regions, CompletableFuture<Void> readyForUpload) {
        public CompletableFuture<StitchResult> whenComplete() {
            return this.readyForUpload.thenApply(void_ -> this);
        }
    }
}

