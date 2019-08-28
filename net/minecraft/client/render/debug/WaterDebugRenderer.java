/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class WaterDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public WaterDebugRenderer(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    @Override
    public void render(long l) {
        FluidState fluidState;
        Camera camera = this.client.gameRenderer.getCamera();
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double f = camera.getPos().z;
        BlockPos blockPos = this.client.player.getBlockPos();
        World lv = this.client.player.world;
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO);
        RenderSystem.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(6.0f);
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
            fluidState = lv.getFluidState(blockPos2);
            if (!fluidState.matches(FluidTags.WATER)) continue;
            double g = (float)blockPos2.getY() + fluidState.getHeight(lv, blockPos2);
            DebugRenderer.method_19695(new Box((float)blockPos2.getX() + 0.01f, (float)blockPos2.getY() + 0.01f, (float)blockPos2.getZ() + 0.01f, (float)blockPos2.getX() + 0.99f, g, (float)blockPos2.getZ() + 0.99f).offset(-d, -e, -f), 1.0f, 1.0f, 1.0f, 0.2f);
        }
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
            fluidState = lv.getFluidState(blockPos2);
            if (!fluidState.matches(FluidTags.WATER)) continue;
            DebugRenderer.method_3714(String.valueOf(fluidState.getLevel()), (double)blockPos2.getX() + 0.5, (float)blockPos2.getY() + fluidState.getHeight(lv, blockPos2), (double)blockPos2.getZ() + 0.5, -16777216);
        }
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}

