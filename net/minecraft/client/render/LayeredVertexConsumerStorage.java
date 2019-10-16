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
public interface LayeredVertexConsumerStorage {
    public static Drawer makeDrawer(BufferBuilder bufferBuilder) {
        return LayeredVertexConsumerStorage.makeDrawer(ImmutableMap.of(), bufferBuilder);
    }

    public static Drawer makeDrawer(Map<RenderLayer, BufferBuilder> map, BufferBuilder bufferBuilder) {
        return new Drawer(bufferBuilder, map);
    }

    public VertexConsumer getBuffer(RenderLayer var1);

    @Environment(value=EnvType.CLIENT)
    public static class Drawer
    implements LayeredVertexConsumerStorage {
        protected final BufferBuilder sharedConsumer;
        protected final Map<RenderLayer, BufferBuilder> layerSpecificConsumers;
        protected Optional<RenderLayer> currentLayer = Optional.empty();
        protected final Set<BufferBuilder> activeConsumers = Sets.newHashSet();

        protected Drawer(BufferBuilder bufferBuilder, Map<RenderLayer, BufferBuilder> map) {
            this.sharedConsumer = bufferBuilder;
            this.layerSpecificConsumers = map;
        }

        @Override
        public VertexConsumer getBuffer(RenderLayer renderLayer) {
            Optional<RenderLayer> optional = Optional.of(renderLayer);
            BufferBuilder bufferBuilder = this.getConsumer(renderLayer);
            if (!Objects.equals(this.currentLayer, optional)) {
                RenderLayer renderLayer2;
                if (this.currentLayer.isPresent() && !this.layerSpecificConsumers.containsKey(renderLayer2 = this.currentLayer.get())) {
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
            return this.layerSpecificConsumers.getOrDefault(renderLayer, this.sharedConsumer);
        }

        public void draw() {
            this.currentLayer.ifPresent(renderLayer -> {
                VertexConsumer vertexConsumer = this.getBuffer((RenderLayer)renderLayer);
                if (vertexConsumer == this.sharedConsumer) {
                    this.draw((RenderLayer)renderLayer);
                }
            });
            for (RenderLayer renderLayer2 : this.layerSpecificConsumers.keySet()) {
                this.draw(renderLayer2);
            }
        }

        public void draw(RenderLayer renderLayer) {
            BufferBuilder bufferBuilder = this.getConsumer(renderLayer);
            boolean bl = Objects.equals(this.currentLayer, Optional.of(renderLayer));
            if (!bl && bufferBuilder == this.sharedConsumer) {
                return;
            }
            if (!this.activeConsumers.remove(bufferBuilder)) {
                return;
            }
            renderLayer.draw(bufferBuilder);
            if (bl) {
                this.currentLayer = Optional.empty();
            }
        }
    }
}

