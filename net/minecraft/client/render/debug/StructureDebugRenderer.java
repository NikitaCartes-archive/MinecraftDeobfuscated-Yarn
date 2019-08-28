/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.dimension.DimensionType;

@Environment(value=EnvType.CLIENT)
public class StructureDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient field_4624;
    private final Map<DimensionType, Map<String, MutableIntBoundingBox>> field_4626 = Maps.newIdentityHashMap();
    private final Map<DimensionType, Map<String, MutableIntBoundingBox>> field_4627 = Maps.newIdentityHashMap();
    private final Map<DimensionType, Map<String, Boolean>> field_4625 = Maps.newIdentityHashMap();

    public StructureDebugRenderer(MinecraftClient minecraftClient) {
        this.field_4624 = minecraftClient;
    }

    @Override
    public void render(long l) {
        Camera camera = this.field_4624.gameRenderer.getCamera();
        ClientWorld iWorld = this.field_4624.world;
        DimensionType dimensionType = iWorld.getDimension().getType();
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double f = camera.getPos().z;
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO);
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        BlockPos blockPos = new BlockPos(camera.getPos().x, 0.0, camera.getPos().z);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
        RenderSystem.lineWidth(1.0f);
        if (this.field_4626.containsKey(dimensionType)) {
            for (MutableIntBoundingBox mutableIntBoundingBox : this.field_4626.get(dimensionType).values()) {
                if (!blockPos.isWithinDistance(mutableIntBoundingBox.method_19635(), 500.0)) continue;
                WorldRenderer.buildBoxOutline(bufferBuilder, (double)mutableIntBoundingBox.minX - d, (double)mutableIntBoundingBox.minY - e, (double)mutableIntBoundingBox.minZ - f, (double)(mutableIntBoundingBox.maxX + 1) - d, (double)(mutableIntBoundingBox.maxY + 1) - e, (double)(mutableIntBoundingBox.maxZ + 1) - f, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        if (this.field_4627.containsKey(dimensionType)) {
            for (Map.Entry entry : this.field_4627.get(dimensionType).entrySet()) {
                String string = (String)entry.getKey();
                MutableIntBoundingBox mutableIntBoundingBox2 = (MutableIntBoundingBox)entry.getValue();
                Boolean boolean_ = this.field_4625.get(dimensionType).get(string);
                if (!blockPos.isWithinDistance(mutableIntBoundingBox2.method_19635(), 500.0)) continue;
                if (boolean_.booleanValue()) {
                    WorldRenderer.buildBoxOutline(bufferBuilder, (double)mutableIntBoundingBox2.minX - d, (double)mutableIntBoundingBox2.minY - e, (double)mutableIntBoundingBox2.minZ - f, (double)(mutableIntBoundingBox2.maxX + 1) - d, (double)(mutableIntBoundingBox2.maxY + 1) - e, (double)(mutableIntBoundingBox2.maxZ + 1) - f, 0.0f, 1.0f, 0.0f, 1.0f);
                    continue;
                }
                WorldRenderer.buildBoxOutline(bufferBuilder, (double)mutableIntBoundingBox2.minX - d, (double)mutableIntBoundingBox2.minY - e, (double)mutableIntBoundingBox2.minZ - f, (double)(mutableIntBoundingBox2.maxX + 1) - d, (double)(mutableIntBoundingBox2.maxY + 1) - e, (double)(mutableIntBoundingBox2.maxZ + 1) - f, 0.0f, 0.0f, 1.0f, 1.0f);
            }
        }
        tessellator.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }

    public void method_3871(MutableIntBoundingBox mutableIntBoundingBox, List<MutableIntBoundingBox> list, List<Boolean> list2, DimensionType dimensionType) {
        if (!this.field_4626.containsKey(dimensionType)) {
            this.field_4626.put(dimensionType, Maps.newHashMap());
        }
        if (!this.field_4627.containsKey(dimensionType)) {
            this.field_4627.put(dimensionType, Maps.newHashMap());
            this.field_4625.put(dimensionType, Maps.newHashMap());
        }
        this.field_4626.get(dimensionType).put(mutableIntBoundingBox.toString(), mutableIntBoundingBox);
        for (int i = 0; i < list.size(); ++i) {
            MutableIntBoundingBox mutableIntBoundingBox2 = list.get(i);
            Boolean boolean_ = list2.get(i);
            this.field_4627.get(dimensionType).put(mutableIntBoundingBox2.toString(), mutableIntBoundingBox2);
            this.field_4625.get(dimensionType).put(mutableIntBoundingBox2.toString(), boolean_);
        }
    }

    @Override
    public void clear() {
        this.field_4626.clear();
        this.field_4627.clear();
        this.field_4625.clear();
    }
}

