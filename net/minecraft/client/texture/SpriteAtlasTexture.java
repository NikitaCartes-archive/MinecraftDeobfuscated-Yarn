/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureStitcher;
import net.minecraft.client.texture.TextureStitcherCannotFitException;
import net.minecraft.client.texture.TextureTickListener;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.client.util.PngFile;
import net.minecraft.container.PlayerContainer;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SpriteAtlasTexture
extends AbstractTexture
implements TextureTickListener {
    private static final Logger LOGGER = LogManager.getLogger();
    @Deprecated
    public static final Identifier BLOCK_ATLAS_TEX = PlayerContainer.field_21668;
    @Deprecated
    public static final Identifier PARTICLE_ATLAS_TEX = new Identifier("textures/atlas/particles.png");
    private final List<Sprite> animatedSprites = Lists.newArrayList();
    private final Set<Identifier> spritesToLoad = Sets.newHashSet();
    private final Map<Identifier, Sprite> sprites = Maps.newHashMap();
    private final Identifier id;
    private final int maxTextureSize;

    public SpriteAtlasTexture(Identifier identifier) {
        this.id = identifier;
        this.maxTextureSize = RenderSystem.maxSupportedTextureSize();
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
    }

    public void upload(Data data) {
        this.spritesToLoad.clear();
        this.spritesToLoad.addAll(data.spriteIds);
        LOGGER.info("Created: {}x{}x{} {}-atlas", (Object)data.width, (Object)data.height, (Object)data.field_21795, (Object)this.id);
        TextureUtil.prepareImage(this.getGlId(), data.field_21795, data.width, data.height);
        this.clear();
        for (Sprite sprite : data.sprites) {
            this.sprites.put(sprite.getId(), sprite);
            try {
                sprite.upload();
            } catch (Throwable throwable) {
                CrashReport crashReport = CrashReport.create(throwable, "Stitching texture atlas");
                CrashReportSection crashReportSection = crashReport.addElement("Texture being stitched together");
                crashReportSection.add("Atlas path", this.id);
                crashReportSection.add("Sprite", sprite);
                throw new CrashException(crashReport);
            }
            if (!sprite.isAnimated()) continue;
            this.animatedSprites.add(sprite);
        }
    }

    public Data stitch(ResourceManager resourceManager, Stream<Identifier> stream, Profiler profiler, int i) {
        int m;
        profiler.push("preparing");
        Set<Identifier> set = stream.peek(identifier -> {
            if (identifier == null) {
                throw new IllegalArgumentException("Location cannot be null!");
            }
        }).collect(Collectors.toSet());
        int j = this.maxTextureSize;
        TextureStitcher textureStitcher = new TextureStitcher(j, j, i);
        int k = Integer.MAX_VALUE;
        int l = 1 << i;
        profiler.swap("extracting_frames");
        for (Sprite.Info info2 : this.loadSprites(resourceManager, set)) {
            k = Math.min(k, Math.min(info2.getWidth(), info2.getHeight()));
            m = Math.min(Integer.lowestOneBit(info2.getWidth()), Integer.lowestOneBit(info2.getHeight()));
            if (m < l) {
                LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", (Object)info2.getId(), (Object)info2.getWidth(), (Object)info2.getHeight(), (Object)MathHelper.log2(l), (Object)MathHelper.log2(m));
                l = m;
            }
            textureStitcher.add(info2);
        }
        int n = Math.min(k, l);
        int o = MathHelper.log2(n);
        if (o < i) {
            LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", (Object)this.id, (Object)i, (Object)o, (Object)n);
            m = o;
        } else {
            m = i;
        }
        profiler.swap("register");
        textureStitcher.add(MissingSprite.getMissingInfo());
        profiler.swap("stitching");
        try {
            textureStitcher.stitch();
        } catch (TextureStitcherCannotFitException textureStitcherCannotFitException) {
            CrashReport crashReport = CrashReport.create(textureStitcherCannotFitException, "Stitching");
            CrashReportSection crashReportSection = crashReport.addElement("Stitcher");
            crashReportSection.add("Sprites", textureStitcherCannotFitException.getSprites().stream().map(info -> String.format("%s[%dx%d]", info.getId(), info.getWidth(), info.getHeight())).collect(Collectors.joining(",")));
            crashReportSection.add("Max Texture Size", j);
            throw new CrashException(crashReport);
        }
        profiler.swap("loading");
        List<Sprite> list = this.method_18161(resourceManager, textureStitcher, m);
        profiler.pop();
        return new Data(set, textureStitcher.getWidth(), textureStitcher.getHeight(), m, list);
    }

    private Collection<Sprite.Info> loadSprites(ResourceManager resourceManager, Set<Identifier> set) {
        ArrayList<CompletableFuture<Void>> list = Lists.newArrayList();
        ConcurrentLinkedQueue<Sprite.Info> concurrentLinkedQueue = new ConcurrentLinkedQueue<Sprite.Info>();
        for (Identifier identifier : set) {
            if (MissingSprite.getMissingSpriteId().equals(identifier)) continue;
            list.add(CompletableFuture.runAsync(() -> {
                Sprite.Info info;
                Identifier identifier2 = this.getTexturePath(identifier);
                try (Resource resource = resourceManager.getResource(identifier2);){
                    PngFile pngFile = new PngFile(resource.toString(), resource.getInputStream());
                    AnimationResourceMetadata animationResourceMetadata = resource.getMetadata(AnimationResourceMetadata.READER);
                    if (animationResourceMetadata == null) {
                        animationResourceMetadata = AnimationResourceMetadata.EMPTY;
                    }
                    Pair<Integer, Integer> pair = animationResourceMetadata.method_24141(pngFile.width, pngFile.height);
                    info = new Sprite.Info(identifier, pair.getFirst(), pair.getSecond(), animationResourceMetadata);
                } catch (RuntimeException runtimeException) {
                    LOGGER.error("Unable to parse metadata from {} : {}", (Object)identifier2, (Object)runtimeException);
                    return;
                } catch (IOException iOException) {
                    LOGGER.error("Using missing texture, unable to load {} : {}", (Object)identifier2, (Object)iOException);
                    return;
                }
                concurrentLinkedQueue.add(info);
            }, Util.getServerWorkerExecutor()));
        }
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return concurrentLinkedQueue;
    }

    private List<Sprite> method_18161(ResourceManager resourceManager, TextureStitcher textureStitcher, int i) {
        ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();
        ArrayList list = Lists.newArrayList();
        textureStitcher.getStitchedSprites((info, j, k, l, m) -> {
            if (info == MissingSprite.getMissingInfo()) {
                MissingSprite missingSprite = MissingSprite.getMissingSprite(this, i, j, k, l, m);
                concurrentLinkedQueue.add(missingSprite);
            } else {
                list.add(CompletableFuture.runAsync(() -> {
                    Sprite sprite = this.loadSprite(resourceManager, info, j, k, i, l, m);
                    if (sprite != null) {
                        concurrentLinkedQueue.add(sprite);
                    }
                }, Util.getServerWorkerExecutor()));
            }
        });
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
        return Lists.newArrayList(concurrentLinkedQueue);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    private Sprite loadSprite(ResourceManager resourceManager, Sprite.Info info, int i, int j, int k, int l, int m) {
        Identifier identifier = this.getTexturePath(info.getId());
        try (Resource resource = resourceManager.getResource(identifier);){
            NativeImage nativeImage = NativeImage.read(resource.getInputStream());
            Sprite sprite = new Sprite(this, info, k, i, j, l, m, nativeImage);
            return sprite;
        } catch (RuntimeException runtimeException) {
            LOGGER.error("Unable to parse metadata from {}", (Object)identifier, (Object)runtimeException);
            return null;
        } catch (IOException iOException) {
            LOGGER.error("Using missing texture, unable to load {}", (Object)identifier, (Object)iOException);
            return null;
        }
    }

    private Identifier getTexturePath(Identifier identifier) {
        return new Identifier(identifier.getNamespace(), String.format("textures/%s%s", identifier.getPath(), ".png"));
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

    public Sprite getSprite(Identifier identifier) {
        Sprite sprite = this.sprites.get(identifier);
        if (sprite == null) {
            return this.sprites.get(MissingSprite.getMissingSpriteId());
        }
        return sprite;
    }

    public void clear() {
        for (Sprite sprite : this.sprites.values()) {
            sprite.close();
        }
        this.sprites.clear();
        this.animatedSprites.clear();
    }

    public Identifier getId() {
        return this.id;
    }

    public void method_24198(Data data) {
        this.setFilter(false, data.field_21795 > 0);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Data {
        final Set<Identifier> spriteIds;
        final int width;
        final int height;
        final int field_21795;
        final List<Sprite> sprites;

        public Data(Set<Identifier> set, int i, int j, int k, List<Sprite> list) {
            this.spriteIds = set;
            this.width = i;
            this.height = j;
            this.field_21795 = k;
            this.sprites = list;
        }
    }
}

