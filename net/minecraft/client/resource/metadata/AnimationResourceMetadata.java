/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource.metadata;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import net.minecraft.client.resource.metadata.AnimationResourceMetadataReader;
import net.minecraft.client.texture.SpriteDimensions;

@Environment(value=EnvType.CLIENT)
public class AnimationResourceMetadata {
    public static final AnimationResourceMetadataReader READER = new AnimationResourceMetadataReader();
    public static final String KEY = "animation";
    public static final int EMPTY_FRAME_TIME = 1;
    public static final int UNDEFINED = -1;
    public static final AnimationResourceMetadata EMPTY = new AnimationResourceMetadata((List)Lists.newArrayList(), -1, -1, 1, false){

        @Override
        public SpriteDimensions getSize(int defaultWidth, int defaultHeight) {
            return new SpriteDimensions(defaultWidth, defaultHeight);
        }
    };
    private final List<AnimationFrameResourceMetadata> frames;
    private final int width;
    private final int height;
    private final int defaultFrameTime;
    private final boolean interpolate;

    public AnimationResourceMetadata(List<AnimationFrameResourceMetadata> frames, int width, int height, int defaultFrameTime, boolean interpolate) {
        this.frames = frames;
        this.width = width;
        this.height = height;
        this.defaultFrameTime = defaultFrameTime;
        this.interpolate = interpolate;
    }

    public SpriteDimensions getSize(int defaultWidth, int defaultHeight) {
        if (this.width != -1) {
            if (this.height != -1) {
                return new SpriteDimensions(this.width, this.height);
            }
            return new SpriteDimensions(this.width, defaultHeight);
        }
        if (this.height != -1) {
            return new SpriteDimensions(defaultWidth, this.height);
        }
        int i = Math.min(defaultWidth, defaultHeight);
        return new SpriteDimensions(i, i);
    }

    public int getDefaultFrameTime() {
        return this.defaultFrameTime;
    }

    public boolean shouldInterpolate() {
        return this.interpolate;
    }

    public void forEachFrame(FrameConsumer consumer) {
        for (AnimationFrameResourceMetadata animationFrameResourceMetadata : this.frames) {
            consumer.accept(animationFrameResourceMetadata.getIndex(), animationFrameResourceMetadata.getTime(this.defaultFrameTime));
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface FrameConsumer {
        public void accept(int var1, int var2);
    }
}

