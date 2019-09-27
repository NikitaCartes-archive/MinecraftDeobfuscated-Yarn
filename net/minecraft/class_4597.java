/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.class_4588;
import net.minecraft.client.render.BufferBuilder;

@Environment(value=EnvType.CLIENT)
public interface class_4597 {
    public static class_4598 method_22991(BufferBuilder bufferBuilder) {
        return class_4597.method_22992(ImmutableMap.of(), bufferBuilder);
    }

    public static class_4598 method_22992(Map<BlockRenderLayer, BufferBuilder> map, BufferBuilder bufferBuilder) {
        return new class_4598(bufferBuilder, map);
    }

    public class_4588 getBuffer(BlockRenderLayer var1);

    @Environment(value=EnvType.CLIENT)
    public static class class_4598
    implements class_4597 {
        protected final BufferBuilder field_20952;
        protected final Map<BlockRenderLayer, BufferBuilder> field_20953;
        protected Optional<BlockRenderLayer> field_20954 = Optional.empty();
        protected final Set<BufferBuilder> field_20955 = Sets.newHashSet();

        protected class_4598(BufferBuilder bufferBuilder, Map<BlockRenderLayer, BufferBuilder> map) {
            this.field_20952 = bufferBuilder;
            this.field_20953 = map;
        }

        @Override
        public class_4588 getBuffer(BlockRenderLayer blockRenderLayer) {
            Optional<BlockRenderLayer> optional = Optional.of(blockRenderLayer);
            BufferBuilder bufferBuilder = this.method_22995(blockRenderLayer);
            if (!Objects.equals(this.field_20954, optional)) {
                BlockRenderLayer blockRenderLayer2;
                if (this.field_20954.isPresent() && !this.field_20953.containsKey(blockRenderLayer2 = this.field_20954.get())) {
                    this.method_22994(blockRenderLayer2);
                }
                if (this.field_20955.add(bufferBuilder)) {
                    bufferBuilder.begin(blockRenderLayer.method_23033(), blockRenderLayer.method_23031());
                }
                this.field_20954 = optional;
            }
            return bufferBuilder;
        }

        private BufferBuilder method_22995(BlockRenderLayer blockRenderLayer) {
            return this.field_20953.getOrDefault(blockRenderLayer, this.field_20952);
        }

        public void method_22993() {
            this.field_20954.ifPresent(blockRenderLayer -> {
                class_4588 lv = this.getBuffer((BlockRenderLayer)blockRenderLayer);
                if (lv == this.field_20952) {
                    this.method_22994((BlockRenderLayer)blockRenderLayer);
                }
            });
            for (BlockRenderLayer blockRenderLayer2 : this.field_20953.keySet()) {
                this.method_22994(blockRenderLayer2);
            }
        }

        public void method_22994(BlockRenderLayer blockRenderLayer) {
            BufferBuilder bufferBuilder = this.method_22995(blockRenderLayer);
            if (!this.field_20955.remove(bufferBuilder)) {
                return;
            }
            blockRenderLayer.method_23012(bufferBuilder);
            if (Objects.equals(this.field_20954, Optional.of(blockRenderLayer))) {
                this.field_20954 = Optional.empty();
            }
        }
    }
}

