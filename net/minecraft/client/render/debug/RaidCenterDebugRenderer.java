/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class RaidCenterDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private Collection<BlockPos> raidCenters = Lists.newArrayList();

    public RaidCenterDebugRenderer(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    public void setRaidCenters(Collection<BlockPos> collection) {
        this.raidCenters = collection;
    }

    @Override
    public void render(long l) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        this.method_23124();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void method_23124() {
        BlockPos blockPos = this.method_23125().getBlockPos();
        for (BlockPos blockPos2 : this.raidCenters) {
            if (!blockPos.isWithinDistance(blockPos2, 160.0)) continue;
            RaidCenterDebugRenderer.method_23122(blockPos2);
        }
    }

    private static void method_23122(BlockPos blockPos) {
        DebugRenderer.drawBox(blockPos.add(-0.5, -0.5, -0.5), blockPos.add(1.5, 1.5, 1.5), 1.0f, 0.0f, 0.0f, 0.15f);
        int i = -65536;
        RaidCenterDebugRenderer.method_23123("Raid center", blockPos, -65536);
    }

    private static void method_23123(String string, BlockPos blockPos, int i) {
        double d = (double)blockPos.getX() + 0.5;
        double e = (double)blockPos.getY() + 1.3;
        double f = (double)blockPos.getZ() + 0.5;
        DebugRenderer.drawString(string, d, e, f, i, 0.04f, true, 0.0f, true);
    }

    private Camera method_23125() {
        return this.client.gameRenderer.getCamera();
    }
}

