/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Sets;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

@Environment(value=EnvType.CLIENT)
public class VillageSectionsDebugRenderer
implements DebugRenderer.Renderer {
    private static final int RANGE = 60;
    private final Set<ChunkSectionPos> sections = Sets.newHashSet();

    VillageSectionsDebugRenderer() {
    }

    @Override
    public void clear() {
        this.sections.clear();
    }

    public void addSection(ChunkSectionPos pos) {
        this.sections.add(pos);
    }

    public void removeSection(ChunkSectionPos pos) {
        this.sections.remove(pos);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        BlockPos blockPos = BlockPos.ofFloored(cameraX, cameraY, cameraZ);
        this.sections.forEach(section -> {
            if (blockPos.isWithinDistance(section.getCenterPos(), 60.0)) {
                VillageSectionsDebugRenderer.drawBoxAtCenterOf(matrices, vertexConsumers, section);
            }
        });
    }

    private static void drawBoxAtCenterOf(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ChunkSectionPos sectionPos) {
        boolean i = true;
        BlockPos blockPos = sectionPos.getCenterPos();
        BlockPos blockPos2 = blockPos.add(-1, -1, -1);
        BlockPos blockPos3 = blockPos.add(1, 1, 1);
        DebugRenderer.drawBox(matrices, vertexConsumers, blockPos2, blockPos3, 0.2f, 1.0f, 0.2f, 0.15f);
    }
}

