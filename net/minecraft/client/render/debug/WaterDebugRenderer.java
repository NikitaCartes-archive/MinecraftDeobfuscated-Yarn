/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
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
        World viewableWorld = this.client.player.world;
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        GlStateManager.disableTexture();
        GlStateManager.lineWidth(6.0f);
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
            fluidState = viewableWorld.getFluidState(blockPos2);
            if (!fluidState.matches(FluidTags.WATER)) continue;
            double g = (float)blockPos2.getY() + fluidState.getHeight(viewableWorld, blockPos2);
            DebugRenderer.method_19695(new Box((float)blockPos2.getX() + 0.01f, (float)blockPos2.getY() + 0.01f, (float)blockPos2.getZ() + 0.01f, (float)blockPos2.getX() + 0.99f, g, (float)blockPos2.getZ() + 0.99f).offset(-d, -e, -f), 1.0f, 1.0f, 1.0f, 0.2f);
        }
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
            fluidState = viewableWorld.getFluidState(blockPos2);
            if (!fluidState.matches(FluidTags.WATER)) continue;
            DebugRenderer.method_3714(String.valueOf(fluidState.getLevel()), (double)blockPos2.getX() + 0.5, (float)blockPos2.getY() + fluidState.getHeight(viewableWorld, blockPos2), (double)blockPos2.getZ() + 0.5, -16777216);
        }
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
}

