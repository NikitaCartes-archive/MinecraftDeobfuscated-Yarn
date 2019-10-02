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
    public static class_4598 method_22991(BufferBuilder bufferBuilder) {
        return LayeredVertexConsumerStorage.method_22992(ImmutableMap.of(), bufferBuilder);
    }

    public static class_4598 method_22992(Map<RenderLayer, BufferBuilder> map, BufferBuilder bufferBuilder) {
        return new class_4598(bufferBuilder, map);
    }

    public VertexConsumer getBuffer(RenderLayer var1);

    @Environment(value=EnvType.CLIENT)
    public static class class_4598
    implements LayeredVertexConsumerStorage {
        protected final BufferBuilder field_20952;
        protected final Map<RenderLayer, BufferBuilder> field_20953;
        protected Optional<RenderLayer> field_20954 = Optional.empty();
        protected final Set<BufferBuilder> field_20955 = Sets.newHashSet();

        protected class_4598(BufferBuilder bufferBuilder, Map<RenderLayer, BufferBuilder> map) {
            this.field_20952 = bufferBuilder;
            this.field_20953 = map;
        }

        @Override
        public VertexConsumer getBuffer(RenderLayer renderLayer) {
            Optional<RenderLayer> optional = Optional.of(renderLayer);
            BufferBuilder bufferBuilder = this.method_22995(renderLayer);
            if (!Objects.equals(this.field_20954, optional)) {
                RenderLayer renderLayer2;
                if (this.field_20954.isPresent() && !this.field_20953.containsKey(renderLayer2 = this.field_20954.get())) {
                    this.method_22994(renderLayer2);
                }
                if (this.field_20955.add(bufferBuilder)) {
                    bufferBuilder.begin(renderLayer.getDrawMode(), renderLayer.getVertexFormat());
                }
                this.field_20954 = optional;
            }
            return bufferBuilder;
        }

        private BufferBuilder method_22995(RenderLayer renderLayer) {
            return this.field_20953.getOrDefault(renderLayer, this.field_20952);
        }

        public void method_22993() {
            this.field_20954.ifPresent(renderLayer -> {
                VertexConsumer vertexConsumer = this.getBuffer((RenderLayer)renderLayer);
                if (vertexConsumer == this.field_20952) {
                    this.method_22994((RenderLayer)renderLayer);
                }
            });
            for (RenderLayer renderLayer2 : this.field_20953.keySet()) {
                this.method_22994(renderLayer2);
            }
        }

        public void method_22994(RenderLayer renderLayer) {
            BufferBuilder bufferBuilder = this.method_22995(renderLayer);
            if (!this.field_20955.remove(bufferBuilder)) {
                return;
            }
            renderLayer.method_23012(bufferBuilder);
            if (Objects.equals(this.field_20954, Optional.of(renderLayer))) {
                this.field_20954 = Optional.empty();
            }
        }
    }
}

