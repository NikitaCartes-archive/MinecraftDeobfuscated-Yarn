/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class RaidCenterDebugRenderer
implements DebugRenderer.Renderer {
    private static final int RANGE = 160;
    private static final float DRAWN_STRING_SIZE = 0.04f;
    private final MinecraftClient client;
    private Collection<BlockPos> raidCenters = Lists.newArrayList();

    public RaidCenterDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void setRaidCenters(Collection<BlockPos> centers) {
        this.raidCenters = centers;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        BlockPos blockPos = this.getCamera().getBlockPos();
        for (BlockPos blockPos2 : this.raidCenters) {
            if (!blockPos.isWithinDistance(blockPos2, 160.0)) continue;
            RaidCenterDebugRenderer.drawRaidCenter(blockPos2);
        }
    }

    private static void drawRaidCenter(BlockPos pos) {
        DebugRenderer.drawBox(pos.add(-0.5, -0.5, -0.5), pos.add(1.5, 1.5, 1.5), 1.0f, 0.0f, 0.0f, 0.15f);
        int i = -65536;
        RaidCenterDebugRenderer.drawString("Raid center", pos, -65536);
    }

    private static void drawString(String string, BlockPos pos, int color) {
        double d = (double)pos.getX() + 0.5;
        double e = (double)pos.getY() + 1.3;
        double f = (double)pos.getZ() + 0.5;
        DebugRenderer.drawString(string, d, e, f, color, 0.04f, true, 0.0f, true);
    }

    private Camera getCamera() {
        return this.client.gameRenderer.getCamera();
    }
}

