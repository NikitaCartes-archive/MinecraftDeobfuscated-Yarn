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
    public static Immediate immediate(BufferBuilder bufferBuilder) {
        return VertexConsumerProvider.immediate(ImmutableMap.of(), bufferBuilder);
    }

    public static Immediate immediate(Map<RenderLayer, BufferBuilder> map, BufferBuilder bufferBuilder) {
        return new Immediate(bufferBuilder, map);
    }

    public VertexConsumer getBuffer(RenderLayer var1);

    @Environment(value=EnvType.CLIENT)
    public static class Immediate
    implements VertexConsumerProvider {
        protected final BufferBuilder defaultBuilder;
        protected final Map<RenderLayer, BufferBuilder> layerBuilders;
        protected Optional<RenderLayer> currentLayer = Optional.empty();
        protected final Set<BufferBuilder> activeConsumers = Sets.newHashSet();

        protected Immediate(BufferBuilder bufferBuilder, Map<RenderLayer, BufferBuilder> map) {
            this.defaultBuilder = bufferBuilder;
            this.layerBuilders = map;
        }

        @Override
        public VertexConsumer getBuffer(RenderLayer renderLayer) {
            Optional<RenderLayer> optional = Optional.of(renderLayer);
            BufferBuilder bufferBuilder = this.getConsumer(renderLayer);
            if (!Objects.equals(this.currentLayer, optional)) {
                RenderLayer renderLayer2;
                if (this.currentLayer.isPresent() && !this.layerBuilders.containsKey(renderLayer2 = this.currentLayer.get())) {
                    this.draw(renderLayer2);
                }
                if (this.activeConsumers.add(bufferBuilder)) {
                    bufferBuilder.begin(renderLayer.getDrawMode(), renderLayer.getVertexFormat());
                }
                this.currentLayer = optional;
            }
            return bufferBuilder;
        }

        private BufferBuilder getConsumer(RenderLayer renderLayer) {
            return this.layerBuilders.getOrDefault(renderLayer, this.defaultBuilder);
        }

        public void draw() {
            this.method_23796(0, 0, 0);
        }

        public void method_23796(int i, int j, int k) {
            this.currentLayer.ifPresent(renderLayer -> {
                VertexConsumer vertexConsumer = this.getBuffer((RenderLayer)renderLayer);
                if (vertexConsumer == this.defaultBuilder) {
                    this.method_23797((RenderLayer)renderLayer, i, j, k);
                }
            });
            for (RenderLayer renderLayer2 : this.layerBuilders.keySet()) {
                this.method_23797(renderLayer2, i, j, k);
            }
        }

        public void draw(RenderLayer renderLayer) {
            this.method_23797(renderLayer, 0, 0, 0);
        }

        public void method_23797(RenderLayer renderLayer, int i, int j, int k) {
            BufferBuilder bufferBuilder = this.getConsumer(renderLayer);
            boolean bl = Objects.equals(this.currentLayer, Optional.of(renderLayer));
            if (!bl && bufferBuilder == this.defaultBuilder) {
                return;
            }
            if (!this.activeConsumers.remove(bufferBuilder)) {
                return;
            }
            renderLayer.draw(bufferBuilder, i, j, k);
            if (bl) {
                this.currentLayer = Optional.empty();
            }
        }
    }
}

