/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
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

@Environment(value=EnvType.CLIENT)
public class StructureDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient field_4624;
    private final Map<Integer, Map<String, MutableIntBoundingBox>> field_4626 = Maps.newHashMap();
    private final Map<Integer, Map<String, MutableIntBoundingBox>> field_4627 = Maps.newHashMap();
    private final Map<Integer, Map<String, Boolean>> field_4625 = Maps.newHashMap();

    public StructureDebugRenderer(MinecraftClient minecraftClient) {
        this.field_4624 = minecraftClient;
    }

    @Override
    public void render(long l) {
        Camera camera = this.field_4624.gameRenderer.getCamera();
        ClientWorld iWorld = this.field_4624.world;
        int i = iWorld.getLevelProperties().getDimension();
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double f = camera.getPos().z;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        GlStateManager.disableDepthTest();
        BlockPos blockPos = new BlockPos(camera.getPos().x, 0.0, camera.getPos().z);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
        GlStateManager.lineWidth(1.0f);
        if (this.field_4626.containsKey(i)) {
            for (MutableIntBoundingBox mutableIntBoundingBox : this.field_4626.get(i).values()) {
                if (!blockPos.isWithinDistance(mutableIntBoundingBox.method_19635(), 500.0)) continue;
                WorldRenderer.buildBoxOutline(bufferBuilder, (double)mutableIntBoundingBox.minX - d, (double)mutableIntBoundingBox.minY - e, (double)mutableIntBoundingBox.minZ - f, (double)(mutableIntBoundingBox.maxX + 1) - d, (double)(mutableIntBoundingBox.maxY + 1) - e, (double)(mutableIntBoundingBox.maxZ + 1) - f, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        if (this.field_4627.containsKey(i)) {
            for (Map.Entry entry : this.field_4627.get(i).entrySet()) {
                String string = (String)entry.getKey();
                MutableIntBoundingBox mutableIntBoundingBox2 = (MutableIntBoundingBox)entry.getValue();
                Boolean boolean_ = this.field_4625.get(i).get(string);
                if (!blockPos.isWithinDistance(mutableIntBoundingBox2.method_19635(), 500.0)) continue;
                if (boolean_.booleanValue()) {
                    WorldRenderer.buildBoxOutline(bufferBuilder, (double)mutableIntBoundingBox2.minX - d, (double)mutableIntBoundingBox2.minY - e, (double)mutableIntBoundingBox2.minZ - f, (double)(mutableIntBoundingBox2.maxX + 1) - d, (double)(mutableIntBoundingBox2.maxY + 1) - e, (double)(mutableIntBoundingBox2.maxZ + 1) - f, 0.0f, 1.0f, 0.0f, 1.0f);
                    continue;
                }
                WorldRenderer.buildBoxOutline(bufferBuilder, (double)mutableIntBoundingBox2.minX - d, (double)mutableIntBoundingBox2.minY - e, (double)mutableIntBoundingBox2.minZ - f, (double)(mutableIntBoundingBox2.maxX + 1) - d, (double)(mutableIntBoundingBox2.maxY + 1) - e, (double)(mutableIntBoundingBox2.maxZ + 1) - f, 0.0f, 0.0f, 1.0f, 1.0f);
            }
        }
        tessellator.draw();
        GlStateManager.enableDepthTest();
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }

    public void method_3871(MutableIntBoundingBox mutableIntBoundingBox, List<MutableIntBoundingBox> list, List<Boolean> list2, int i) {
        if (!this.field_4626.containsKey(i)) {
            this.field_4626.put(i, Maps.newHashMap());
        }
        if (!this.field_4627.containsKey(i)) {
            this.field_4627.put(i, Maps.newHashMap());
            this.field_4625.put(i, Maps.newHashMap());
        }
        this.field_4626.get(i).put(mutableIntBoundingBox.toString(), mutableIntBoundingBox);
        for (int j = 0; j < list.size(); ++j) {
            MutableIntBoundingBox mutableIntBoundingBox2 = list.get(j);
            Boolean boolean_ = list2.get(j);
            this.field_4627.get(i).put(mutableIntBoundingBox2.toString(), mutableIntBoundingBox2);
            this.field_4625.get(i).put(mutableIntBoundingBox2.toString(), boolean_);
        }
    }
}

