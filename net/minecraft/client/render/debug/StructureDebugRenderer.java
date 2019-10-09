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
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

@Environment(value=EnvType.CLIENT)
public class StructureDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient field_4624;
    private final Map<DimensionType, Map<String, BlockBox>> field_4626 = Maps.newIdentityHashMap();
    private final Map<DimensionType, Map<String, BlockBox>> field_4627 = Maps.newIdentityHashMap();
    private final Map<DimensionType, Map<String, Boolean>> field_4625 = Maps.newIdentityHashMap();

    public StructureDebugRenderer(MinecraftClient minecraftClient) {
        this.field_4624 = minecraftClient;
    }

    @Override
    public void method_23109(long l) {
        Camera camera = this.field_4624.gameRenderer.getCamera();
        ClientWorld iWorld = this.field_4624.world;
        DimensionType dimensionType = iWorld.getDimension().getType();
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double f = camera.getPos().z;
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        BlockPos blockPos = new BlockPos(camera.getPos().x, 0.0, camera.getPos().z);
        LayeredVertexConsumerStorage.class_4598 lv = LayeredVertexConsumerStorage.method_22991(Tessellator.getInstance().getBufferBuilder());
        VertexConsumer vertexConsumer = lv.getBuffer(RenderLayer.getLines());
        if (this.field_4626.containsKey(dimensionType)) {
            for (BlockBox blockBox : this.field_4626.get(dimensionType).values()) {
                if (!blockPos.isWithinDistance(blockBox.method_22874(), 500.0)) continue;
                WorldRenderer.drawBoxOutline(vertexConsumer, (double)blockBox.minX - d, (double)blockBox.minY - e, (double)blockBox.minZ - f, (double)(blockBox.maxX + 1) - d, (double)(blockBox.maxY + 1) - e, (double)(blockBox.maxZ + 1) - f, 1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
        if (this.field_4627.containsKey(dimensionType)) {
            for (Map.Entry entry : this.field_4627.get(dimensionType).entrySet()) {
                String string = (String)entry.getKey();
                BlockBox blockBox2 = (BlockBox)entry.getValue();
                Boolean boolean_ = this.field_4625.get(dimensionType).get(string);
                if (!blockPos.isWithinDistance(blockBox2.method_22874(), 500.0)) continue;
                if (boolean_.booleanValue()) {
                    WorldRenderer.drawBoxOutline(vertexConsumer, (double)blockBox2.minX - d, (double)blockBox2.minY - e, (double)blockBox2.minZ - f, (double)(blockBox2.maxX + 1) - d, (double)(blockBox2.maxY + 1) - e, (double)(blockBox2.maxZ + 1) - f, 0.0f, 1.0f, 0.0f, 1.0f);
                    continue;
                }
                WorldRenderer.drawBoxOutline(vertexConsumer, (double)blockBox2.minX - d, (double)blockBox2.minY - e, (double)blockBox2.minZ - f, (double)(blockBox2.maxX + 1) - d, (double)(blockBox2.maxY + 1) - e, (double)(blockBox2.maxZ + 1) - f, 0.0f, 0.0f, 1.0f, 1.0f);
            }
        }
        lv.method_22993();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }

    public void method_3871(BlockBox blockBox, List<BlockBox> list, List<Boolean> list2, DimensionType dimensionType) {
        if (!this.field_4626.containsKey(dimensionType)) {
            this.field_4626.put(dimensionType, Maps.newHashMap());
        }
        if (!this.field_4627.containsKey(dimensionType)) {
            this.field_4627.put(dimensionType, Maps.newHashMap());
            this.field_4625.put(dimensionType, Maps.newHashMap());
        }
        this.field_4626.get(dimensionType).put(blockBox.toString(), blockBox);
        for (int i = 0; i < list.size(); ++i) {
            BlockBox blockBox2 = list.get(i);
            Boolean boolean_ = list2.get(i);
            this.field_4627.get(dimensionType).put(blockBox2.toString(), blockBox2);
            this.field_4625.get(dimensionType).put(blockBox2.toString(), boolean_);
        }
    }

    @Override
    public void clear() {
        this.field_4626.clear();
        this.field_4627.clear();
        this.field_4625.clear();
    }
}

