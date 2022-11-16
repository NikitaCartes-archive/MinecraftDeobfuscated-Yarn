/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture.atlas;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class UnstitchAtlasSource
implements AtlasSource {
    static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceFinder FINDER = new ResourceFinder("textures", ".png");
    public static final Codec<UnstitchAtlasSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Identifier.CODEC.fieldOf("resource")).forGetter(unstitchAtlasSource -> unstitchAtlasSource.resource), ((MapCodec)Codecs.nonEmptyList(Region.CODEC.listOf()).fieldOf("regions")).forGetter(unstitchAtlasSource -> unstitchAtlasSource.regions), Codec.DOUBLE.optionalFieldOf("divisor_x", 1.0).forGetter(unstitchAtlasSource -> unstitchAtlasSource.divisorX), Codec.DOUBLE.optionalFieldOf("divisor_y", 1.0).forGetter(unstitchAtlasSource -> unstitchAtlasSource.divisorY)).apply((Applicative<UnstitchAtlasSource, ?>)instance, UnstitchAtlasSource::new));
    private final Identifier resource;
    private final List<Region> regions;
    private final double divisorX;
    private final double divisorY;

    public UnstitchAtlasSource(Identifier resource, List<Region> regions, double divisorX, double divisorY) {
        this.resource = resource;
        this.regions = regions;
        this.divisorX = divisorX;
        this.divisorY = divisorY;
    }

    @Override
    public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
        Identifier identifier = this.FINDER.toResourcePath(this.resource);
        Optional<Resource> optional = resourceManager.getResource(identifier);
        if (optional.isPresent()) {
            Sprite sprite = new Sprite(identifier, optional.get(), this.regions.size());
            for (Region region : this.regions) {
                regions.add(region.sprite, new SpriteRegion(sprite, region, this.divisorX, this.divisorY));
            }
        } else {
            LOGGER.warn("Missing sprite: {}", (Object)identifier);
        }
    }

    @Override
    public AtlasSourceType getType() {
        return AtlasSourceManager.UNSTITCH;
    }

    @Environment(value=EnvType.CLIENT)
    static class Sprite {
        private final Identifier id;
        private final Resource resource;
        private final AtomicReference<NativeImage> image = new AtomicReference();
        private final AtomicInteger regionCount;

        Sprite(Identifier id, Resource resource, int regionCount) {
            this.id = id;
            this.resource = resource;
            this.regionCount = new AtomicInteger(regionCount);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public NativeImage read() throws IOException {
            NativeImage nativeImage = this.image.get();
            if (nativeImage == null) {
                Sprite sprite = this;
                synchronized (sprite) {
                    nativeImage = this.image.get();
                    if (nativeImage == null) {
                        try (InputStream inputStream = this.resource.getInputStream();){
                            nativeImage = NativeImage.read(inputStream);
                            this.image.set(nativeImage);
                        } catch (IOException iOException) {
                            throw new IOException("Failed to load image " + this.id, iOException);
                        }
                    }
                }
            }
            return nativeImage;
        }

        public void close() {
            NativeImage nativeImage;
            int i = this.regionCount.decrementAndGet();
            if (i <= 0 && (nativeImage = (NativeImage)this.image.getAndSet(null)) != null) {
                nativeImage.close();
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    record Region(Identifier sprite, double x, double y, double width, double height) {
        public static final Codec<Region> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Identifier.CODEC.fieldOf("sprite")).forGetter(Region::sprite), ((MapCodec)Codec.DOUBLE.fieldOf("x")).forGetter(Region::x), ((MapCodec)Codec.DOUBLE.fieldOf("y")).forGetter(Region::y), ((MapCodec)Codec.DOUBLE.fieldOf("width")).forGetter(Region::width), ((MapCodec)Codec.DOUBLE.fieldOf("height")).forGetter(Region::height)).apply((Applicative<Region, ?>)instance, Region::new));
    }

    @Environment(value=EnvType.CLIENT)
    static class SpriteRegion
    implements AtlasSource.SpriteRegion {
        private final Sprite sprite;
        private final Region region;
        private final double divisorX;
        private final double divisorY;

        SpriteRegion(Sprite sprite, Region region, double divisorX, double divisorY) {
            this.sprite = sprite;
            this.region = region;
            this.divisorX = divisorX;
            this.divisorY = divisorY;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public SpriteContents get() {
            try {
                NativeImage nativeImage = this.sprite.read();
                double d = (double)nativeImage.getWidth() / this.divisorX;
                double e = (double)nativeImage.getHeight() / this.divisorY;
                int i = MathHelper.floor(this.region.x * d);
                int j = MathHelper.floor(this.region.y * e);
                int k = MathHelper.floor(this.region.width * d);
                int l = MathHelper.floor(this.region.height * e);
                NativeImage nativeImage2 = new NativeImage(NativeImage.Format.RGBA, k, l, false);
                nativeImage.copyRect(nativeImage2, i, j, 0, 0, k, l, false, false);
                SpriteContents spriteContents = new SpriteContents(this.region.sprite, new SpriteDimensions(k, l), nativeImage2, AnimationResourceMetadata.EMPTY);
                return spriteContents;
            } catch (Exception exception) {
                LOGGER.error("Failed to unstitch region {}", (Object)this.region.sprite, (Object)exception);
            } finally {
                this.sprite.close();
            }
            return MissingSprite.createSpriteContents();
        }

        @Override
        public void close() {
            this.sprite.close();
        }

        @Override
        public /* synthetic */ Object get() {
            return this.get();
        }
    }
}

