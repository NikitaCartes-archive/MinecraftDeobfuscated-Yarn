/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;

/**
 * Manages rendering with multiple {@linkplain RenderLayer render layers}.
 */
@Environment(value=EnvType.CLIENT)
public interface VertexConsumerProvider {
    /**
     * {@return a vertex consumer provider that immediately draws the current
     * buffer builder when a different render layer is requested}.
     */
    public static Immediate immediate(BufferBuilder buffer) {
        return VertexConsumerProvider.immediate(ImmutableMap.of(), buffer);
    }

    /**
     * {@return a vertex consumer provider that immediately draws the current
     * buffer builder when a different render layer is requested except {@code
     * layerBuffers}}.
     * 
     * <p>{@code layerBuffers} will not be drawn immediately after switching
     * the current render layer. The caller of this method can control when to
     * draw these render layers. For example, {@linkplain
     * RenderLayer#TRANSLUCENT the translucent render layer} should be drawn in
     * a later stage so the other things behind translucent objects are
     * visible.
     */
    public static Immediate immediate(Map<RenderLayer, BufferBuilder> layerBuffers, BufferBuilder fallbackBuffer) {
        return new Immediate(fallbackBuffer, layerBuffers);
    }

    /**
     * Starts building a buffer that will be drawn with {@code layer}.
     * 
     * <p>The returned vertex consumer can only be safely used until this
     * method is called with a different render layer.
     * 
     * <p>Note that the user of this method cannot modify states controlled
     * by the render layer. Those states will be overridden by the render layer
     * before drawing them. If you need to set states yourself, consider using
     * {@link Tessellator} or making a custom render layer.
     */
    public VertexConsumer getBuffer(RenderLayer var1);

    @Environment(value=EnvType.CLIENT)
    public static class Immediate
    implements VertexConsumerProvider {
        protected final BufferBuilder fallbackBuffer;
        protected final Map<RenderLayer, BufferBuilder> layerBuffers;
        protected Optional<RenderLayer> currentLayer = Optional.empty();
        protected final Set<BufferBuilder> activeConsumers = Sets.newHashSet();

        protected Immediate(BufferBuilder fallbackBuffer, Map<RenderLayer, BufferBuilder> layerBuffers) {
            this.fallbackBuffer = fallbackBuffer;
            this.layerBuffers = layerBuffers;
        }

        @Override
        public VertexConsumer getBuffer(RenderLayer renderLayer) {
            Optional<RenderLayer> optional = renderLayer.asOptional();
            BufferBuilder bufferBuilder = this.getBufferInternal(renderLayer);
            if (!Objects.equals(this.currentLayer, optional) || !renderLayer.areVerticesNotShared()) {
                RenderLayer renderLayer2;
                if (this.currentLayer.isPresent() && !this.layerBuffers.containsKey(renderLayer2 = this.currentLayer.get())) {
                    this.draw(renderLayer2);
                }
                if (this.activeConsumers.add(bufferBuilder)) {
                    bufferBuilder.begin(renderLayer.getDrawMode(), renderLayer.getVertexFormat());
                }
                this.currentLayer = optional;
            }
            return bufferBuilder;
        }

        private BufferBuilder getBufferInternal(RenderLayer layer) {
            return this.layerBuffers.getOrDefault(layer, this.fallbackBuffer);
        }

        public void drawCurrentLayer() {
            if (this.currentLayer.isPresent()) {
                RenderLayer renderLayer = this.currentLayer.get();
                if (!this.layerBuffers.containsKey(renderLayer)) {
                    this.draw(renderLayer);
                }
                this.currentLayer = Optional.empty();
            }
        }

        public void draw() {
            this.currentLayer.ifPresent(layer -> {
                VertexConsumer vertexConsumer = this.getBuffer((RenderLayer)layer);
                if (vertexConsumer == this.fallbackBuffer) {
                    this.draw((RenderLayer)layer);
                }
            });
            for (RenderLayer renderLayer : this.layerBuffers.keySet()) {
                this.draw(renderLayer);
            }
        }

        public void draw(RenderLayer layer) {
            BufferBuilder bufferBuilder = this.getBufferInternal(layer);
            boolean bl = Objects.equals(this.currentLayer, layer.asOptional());
            if (!bl && bufferBuilder == this.fallbackBuffer) {
                return;
            }
            if (!this.activeConsumers.remove(bufferBuilder)) {
                return;
            }
            layer.draw(bufferBuilder, 0, 0, 0);
            if (bl) {
                this.currentLayer = Optional.empty();
            }
        }
    }
}

