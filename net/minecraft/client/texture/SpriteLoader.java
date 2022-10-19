/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
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
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
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
    private static final ResourceFinder FINDER = new ResourceFinder("textures", ".png");

    public SpriteLoader(Identifier id, int maxTextureSize) {
        this.id = id;
        this.maxTextureSize = maxTextureSize;
    }

    public static SpriteLoader fromAtlas(SpriteAtlasTexture atlasTexture) {
        return new SpriteLoader(atlasTexture.getId(), atlasTexture.getMaxTextureSize());
    }

    public CompletableFuture<StitchResult> stitch(Map<Identifier, Resource> sprites, int mipmapLevels, Executor executor) {
        return this.loadAll(sprites, executor).thenApplyAsync(spriteContents -> {
            int m;
            int j = this.maxTextureSize;
            TextureStitcher<SpriteContents> textureStitcher = new TextureStitcher<SpriteContents>(j, j, mipmapLevels);
            int k = Integer.MAX_VALUE;
            int l = 1 << mipmapLevels;
            for (SpriteContents spriteContents2 : spriteContents) {
                k = Math.min(k, Math.min(spriteContents2.getWidth(), spriteContents2.getHeight()));
                m = Math.min(Integer.lowestOneBit(spriteContents2.getWidth()), Integer.lowestOneBit(spriteContents2.getHeight()));
                if (m < l) {
                    LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", spriteContents2.getId(), spriteContents2.getWidth(), spriteContents2.getHeight(), MathHelper.floorLog2(l), MathHelper.floorLog2(m));
                    l = m;
                }
                textureStitcher.add(spriteContents2);
            }
            int n = Math.min(k, l);
            int o = MathHelper.floorLog2(n);
            if (o < mipmapLevels) {
                LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.id, mipmapLevels, o, n);
                m = o;
            } else {
                m = mipmapLevels;
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
        }, executor);
    }

    private CompletableFuture<List<SpriteContents>> loadAll(Map<Identifier, Resource> sprites, Executor executor) {
        ArrayList<CompletableFuture<SpriteContents>> list = new ArrayList<CompletableFuture<SpriteContents>>();
        list.add(CompletableFuture.supplyAsync(MissingSprite::createSpriteContents, executor));
        sprites.forEach((id, resource) -> list.add(CompletableFuture.supplyAsync(() -> this.load((Identifier)id, (Resource)resource), executor)));
        return Util.combineSafe(list).thenApply(spriteContents -> spriteContents.stream().filter(Objects::nonNull).toList());
    }

    @Nullable
    private SpriteContents load(Identifier id, Resource resource) {
        NativeImage nativeImage;
        AnimationResourceMetadata animationResourceMetadata;
        try {
            animationResourceMetadata = resource.getMetadata().decode(AnimationResourceMetadata.READER).orElse(AnimationResourceMetadata.EMPTY);
        } catch (Exception exception) {
            LOGGER.error("Unable to parse metadata from {} : {}", (Object)this.id, (Object)exception);
            return null;
        }
        try (InputStream inputStream = resource.getInputStream();){
            nativeImage = NativeImage.read(inputStream);
        } catch (IOException iOException) {
            LOGGER.error("Using missing texture, unable to load {} : {}", (Object)this.id, (Object)iOException);
            return null;
        }
        SpriteDimensions spriteDimensions = animationResourceMetadata.getSize(nativeImage.getWidth(), nativeImage.getHeight());
        if (!MathHelper.isMultipleOf(nativeImage.getWidth(), spriteDimensions.width()) || !MathHelper.isMultipleOf(nativeImage.getHeight(), spriteDimensions.height())) {
            LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}", this.id, nativeImage.getWidth(), nativeImage.getHeight(), spriteDimensions.width(), spriteDimensions.height());
            nativeImage.close();
            return null;
        }
        return new SpriteContents(id, spriteDimensions, nativeImage, animationResourceMetadata);
    }

    private Map<Identifier, Sprite> collectStitchedSprites(TextureStitcher<SpriteContents> stitcher) {
        HashMap<Identifier, Sprite> map = new HashMap<Identifier, Sprite>();
        int i = stitcher.getWidth();
        int j = stitcher.getHeight();
        stitcher.getStitchedSprites((info, width, height) -> map.put(info.getId(), new Sprite(this.id, (SpriteContents)info, i, j, width, height)));
        return map;
    }

    public static void addResource(ResourceManager resourceManager, Identifier id, BiConsumer<Identifier, Resource> adder) {
        Identifier identifier = FINDER.toResourcePath(id);
        Optional<Resource> optional = resourceManager.getResource(identifier);
        if (optional.isPresent()) {
            adder.accept(id, optional.get());
        } else {
            LOGGER.warn("Missing sprite: {}", (Object)identifier);
        }
    }

    public static void addResources(ResourceManager resourceManager, String textureId, BiConsumer<Identifier, Resource> adder) {
        SpriteLoader.addResources(resourceManager, "textures/" + textureId, textureId + "/", adder);
    }

    public static void addResources(ResourceManager resourceManager, String textureId, String prefix, BiConsumer<Identifier, Resource> adder) {
        ResourceFinder resourceFinder = new ResourceFinder(textureId, ".png");
        resourceFinder.findResources(resourceManager).forEach((id, resource) -> {
            Identifier identifier = resourceFinder.toResourceId((Identifier)id).withPrefixedPath(prefix);
            adder.accept(identifier, (Resource)resource);
        });
    }

    public static Map<Identifier, Resource> findAllResources(ResourceManager resourceManager, String textureId) {
        return SpriteLoader.findAllResources(resourceManager, "textures/" + textureId, textureId + "/");
    }

    public static Map<Identifier, Resource> findAllResources(ResourceManager resourceManager, String textureId, String prefix) {
        HashMap<Identifier, Resource> map = new HashMap<Identifier, Resource>();
        SpriteLoader.addResources(resourceManager, textureId, prefix, map::put);
        return map;
    }

    @Environment(value=EnvType.CLIENT)
    public record StitchResult(int width, int height, int mipLevel, Sprite missing, Map<Identifier, Sprite> regions, CompletableFuture<Void> readyForUpload) {
        public CompletableFuture<StitchResult> whenComplete() {
            return this.readyForUpload.thenApply(void_ -> this);
        }
    }
}

