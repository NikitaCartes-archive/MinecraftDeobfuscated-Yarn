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

@Environment(value=EnvType.CLIENT)
public interface VertexConsumerProvider {
    public static Immediate immediate(BufferBuilder buffer) {
        return VertexConsumerProvider.immediate(ImmutableMap.of(), buffer);
    }

    public static Immediate immediate(Map<RenderLayer, BufferBuilder> layerBuffers, BufferBuilder fallbackBuffer) {
        return new Immediate(fallbackBuffer, layerBuffers);
    }

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
            Optional<RenderLayer> optional = Optional.of(renderLayer);
            BufferBuilder bufferBuilder = this.getBufferInternal(renderLayer);
            if (!Objects.equals(this.currentLayer, optional)) {
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

        public void draw() {
            this.currentLayer.ifPresent(renderLayer -> {
                VertexConsumer vertexConsumer = this.getBuffer((RenderLayer)renderLayer);
                if (vertexConsumer == this.fallbackBuffer) {
                    this.draw((RenderLayer)renderLayer);
                }
            });
            for (RenderLayer renderLayer2 : this.layerBuffers.keySet()) {
                this.draw(renderLayer2);
            }
        }

        public void draw(RenderLayer layer) {
            BufferBuilder bufferBuilder = this.getBufferInternal(layer);
            boolean bl = Objects.equals(this.currentLayer, Optional.of(layer));
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

