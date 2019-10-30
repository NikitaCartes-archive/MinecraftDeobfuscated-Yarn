/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureStitcher;
import net.minecraft.client.texture.TextureStitcherCannotFitException;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.client.util.PngFile;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SpriteAtlasTexture
extends AbstractTexture
implements TextureTickListener {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Identifier BLOCK_ATLAS_TEX = new Identifier("textures/atlas/blocks.png");
    public static final Identifier PARTICLE_ATLAS_TEX = new Identifier("textures/atlas/particles.png");
    public static final Identifier PAINTING_ATLAS_TEX = new Identifier("textures/atlas/paintings.png");
    public static final Identifier STATUS_EFFECT_ATLAS_TEX = new Identifier("textures/atlas/mob_effects.png");
    private final List<Sprite> animatedSprites = Lists.newArrayList();
    private final Set<Identifier> spritesToLoad = Sets.newHashSet();
    private final Map<Identifier, Sprite> sprites = Maps.newHashMap();
    private final String atlasPath;
    private final int maxTextureSize;
    private int mipLevel;
    private final Sprite missingSprite = MissingSprite.getMissingSprite();

    public SpriteAtlasTexture(String string) {
        this.atlasPath = string;
        this.maxTextureSize = RenderSystem.maxSupportedTextureSize();
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
    }

    public void upload(Data data) {
        this.spritesToLoad.clear();
        this.spritesToLoad.addAll(data.spriteIds);
        LOGGER.info("Created: {}x{} {}-atlas", (Object)data.width, (Object)data.height, (Object)this.atlasPath);
        TextureUtil.prepareImage(this.getGlId(), this.mipLevel, data.width, data.height);
        this.clear();
        for (Sprite sprite : data.sprites) {
            this.sprites.put(sprite.getId(), sprite);
            try {
                sprite.upload();
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Stitching texture atlas");
                CrashReportSection crashReportSection = crashReport.addElement("Texture being stitched together");
                crashReportSection.add("Atlas path", this.atlasPath);
                crashReportSection.add("Sprite", sprite);
                throw new CrashException(crashReport);
            }
            if (!sprite.isAnimated()) continue;
            this.animatedSprites.add(sprite);
        }
    }

    public Data stitch(ResourceManager resourceManager, Iterable<Identifier> iterable, Profiler profiler) {
        HashSet<Identifier> set = Sets.newHashSet();
        profiler.push("preparing");
        iterable.forEach(identifier -> {
            if (identifier == null) {
                throw new IllegalArgumentException("Location cannot be null!");
            }
            set.add((Identifier)identifier);
        });
        int i = this.maxTextureSize;
        TextureStitcher textureStitcher = new TextureStitcher(i, i, this.mipLevel);
        int j = Integer.MAX_VALUE;
        int k = 1 << this.mipLevel;
        profiler.swap("extracting_frames");
        for (Sprite sprite2 : this.loadSprites(resourceManager, set)) {
            j = Math.min(j, Math.min(sprite2.getWidth(), sprite2.getHeight()));
            int l = Math.min(Integer.lowestOneBit(sprite2.getWidth()), Integer.lowestOneBit(sprite2.getHeight()));
            if (l < k) {
                LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", (Object)sprite2.getId(), (Object)sprite2.getWidth(), (Object)sprite2.getHeight(), (Object)MathHelper.log2(k), (Object)MathHelper.log2(l));
                k = l;
            }
            textureStitcher.add(sprite2);
        }
        int m = Math.min(j, k);
        int n = MathHelper.log2(m);
        if (n < this.mipLevel) {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", (Object)this.atlasPath, (Object)this.mipLevel, (Object)n, (Object)m);
            this.mipLevel = n;
        }
        profiler.swap("mipmapping");
        this.missingSprite.generateMipmaps(this.mipLevel);
        profiler.swap("register");
        textureStitcher.add(this.missingSprite);
        profiler.swap("stitching");
        try {
            textureStitcher.stitch();
        } catch (TextureStitcherCannotFitException textureStitcherCannotFitException) {
            CrashReport crashReport = CrashReport.create(textureStitcherCannotFitException, "Stitching");
            CrashReportSection crashReportSection = crashReport.addElement("Stitcher");
            crashReportSection.add("Sprites", textureStitcherCannotFitException.getSprites().stream().map(sprite -> String.format("%s[%dx%d]", sprite.getId(), sprite.getWidth(), sprite.getHeight())).collect(Collectors.joining(",")));
            crashReportSection.add("Max Texture Size", i);
            throw new CrashException(crashReport);
        }
        profiler.swap("loading");
        List<Sprite> list = this.loadSprites(resourceManager, textureStitcher);
        profiler.pop();
        return new Data(set, textureStitcher.getWidth(), textureStitcher.getHeight(), list);
    }

    private Collection<Sprite> loadSprites(ResourceManager resourceManager, Set<Identifier> set) {
        ArrayList<CompletableFuture<Void>> list = Lists.newArrayList();
        ConcurrentLinkedQueue<Sprite> concurrentLinkedQueue = new ConcurrentLinkedQueue<Sprite>();
        for (Identifier identifier : set) {
            if (this.missingSprite.getId().equals(identifier)) continue;
            list.add(CompletableFuture.runAsync(() -> {
                Sprite sprite;
                Identifier identifier2 = this.getTexturePath(identifier);
                try (Resource resource = resourceManager.getResource(identifier2);){
                    PngFile pngFile = new PngFile(resource.toString(), resource.getInputStream());
                    AnimationResourceMetadata animationResourceMetadata = resource.getMetadata(AnimationResourceMetadata.READER);
                    sprite = new Sprite(identifier, pngFile, animationResourceMetadata);
                } catch (RuntimeException runtimeException) {
                    LOGGER.error("Unable to parse metadata from {} : {}", (Object)identifier2, (Object)runtimeException);
                    return;
                } catch (IOException iOException) {
                    LOGGER.error("Using missing texture, unable to load {} : {}", (Object)identifier2, (Object)iOException);
                    return;
                }
                concurrentLinkedQueue.add(sprite);
            }, Util.getServerWorkerExecutor()));
        }
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return concurrentLinkedQueue;
    }

    private List<Sprite> loadSprites(ResourceManager resourceManager, TextureStitcher textureStitcher) {
        ConcurrentLinkedQueue<Sprite> concurrentLinkedQueue = new ConcurrentLinkedQueue<Sprite>();
        ArrayList<CompletableFuture<Void>> list = Lists.newArrayList();
        for (Sprite sprite : textureStitcher.getStitchedSprites()) {
            if (sprite == this.missingSprite) {
                concurrentLinkedQueue.add(sprite);
                continue;
            }
            list.add(CompletableFuture.runAsync(() -> {
                if (this.loadSprite(resourceManager, sprite)) {
                    concurrentLinkedQueue.add(sprite);
                }
            }, Util.getServerWorkerExecutor()));
        }
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return Lists.newArrayList(concurrentLinkedQueue);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean loadSprite(ResourceManager resourceManager, Sprite sprite) {
        Identifier identifier = this.getTexturePath(sprite.getId());
        Resource resource = null;
        try {
            resource = resourceManager.getResource(identifier);
            sprite.load(resource, this.mipLevel + 1);
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Unable to parse metadata from {}", (Object)identifier, (Object)runtimeException);
            boolean bl = false;
            return bl;
        } catch (IOException iOException) {
            LOGGER.error("Using missing texture, unable to load {}", (Object)identifier, (Object)iOException);
            boolean bl = false;
            return bl;
        } finally {
            IOUtils.closeQuietly((Closeable)resource);
        }
        try {
            sprite.generateMipmaps(this.mipLevel);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Applying mipmap");
            CrashReportSection crashReportSection = crashReport.addElement("Sprite being mipmapped");
            crashReportSection.add("Sprite name", () -> sprite.getId().toString());
            crashReportSection.add("Sprite size", () -> sprite.getWidth() + " x " + sprite.getHeight());
            crashReportSection.add("Sprite frames", () -> sprite.getFrameCount() + " frames");
            crashReportSection.add("Mipmap levels", this.mipLevel);
            throw new CrashException(crashReport);
        }
        return true;
    }

    private Identifier getTexturePath(Identifier identifier) {
        return new Identifier(identifier.getNamespace(), String.format("%s/%s%s", this.atlasPath, identifier.getPath(), ".png"));
    }

    public Sprite getSprite(String string) {
        return this.getSprite(new Identifier(string));
    }

    public void tickAnimatedSprites() {
        this.bindTexture();
        for (Sprite sprite : this.animatedSprites) {
            sprite.tickAnimation();
        }
    }

    @Override
    public void tick() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::tickAnimatedSprites);
        } else {
            this.tickAnimatedSprites();
        }
    }

    public void setMipLevel(int i) {
        this.mipLevel = i;
    }

    public Sprite getSprite(Identifier identifier) {
        Sprite sprite = this.sprites.get(identifier);
        if (sprite == null) {
            return this.missingSprite;
        }
        return sprite;
    }

    public void clear() {
        for (Sprite sprite : this.sprites.values()) {
            sprite.destroy();
        }
        this.sprites.clear();
        this.animatedSprites.clear();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Data {
        final Set<Identifier> spriteIds;
        final int width;
        final int height;
        final List<Sprite> sprites;

        public Data(Set<Identifier> set, int i, int j, List<Sprite> list) {
            this.spriteIds = set;
            this.width = i;
            this.height = j;
            this.sprites = list;
        }
    }
}

