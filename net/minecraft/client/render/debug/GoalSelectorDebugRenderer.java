/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class GoalSelectorDebugRenderer
implements DebugRenderer.Renderer {
    private static final int field_32902 = 160;
    private final MinecraftClient client;
    private final Map<Integer, List<GoalSelector>> goalSelectors = Maps.newHashMap();

    @Override
    public void clear() {
        this.goalSelectors.clear();
    }

    public void setGoalSelectorList(int index, List<GoalSelector> list) {
        this.goalSelectors.put(index, list);
    }

    public void removeGoalSelectorList(int index) {
        this.goalSelectors.remove(index);
    }

    public GoalSelectorDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        Camera camera = this.client.gameRenderer.getCamera();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        BlockPos blockPos = new BlockPos(camera.getPos().x, 0.0, camera.getPos().z);
        this.goalSelectors.forEach((integer, list) -> {
            for (int i = 0; i < list.size(); ++i) {
                GoalSelector goalSelector = (GoalSelector)list.get(i);
                if (!blockPos.isWithinDistance(goalSelector.pos, 160.0)) continue;
                double d = (double)goalSelector.pos.getX() + 0.5;
                double e = (double)goalSelector.pos.getY() + 2.0 + (double)i * 0.25;
                double f = (double)goalSelector.pos.getZ() + 0.5;
                int j = goalSelector.field_18785 ? -16711936 : -3355444;
                DebugRenderer.drawString(goalSelector.name, d, e, f, j);
            }
        });
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
    }

    @Environment(value=EnvType.CLIENT)
    public static class GoalSelector {
        public final BlockPos pos;
        public final int field_18783;
        public final String name;
        public final boolean field_18785;

        public GoalSelector(BlockPos pos, int i, String name, boolean bl) {
            this.pos = pos;
            this.field_18783 = i;
            this.name = name;
            this.field_18785 = bl;
        }
    }
}

