/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.class_4576;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BellBlockEntityRenderer
extends class_4576<BellBlockEntity> {
    public static final Identifier BELL_BODY_TEXTURE = new Identifier("entity/bell/bell_body");
    private final ModelPart field_20816 = new ModelPart(32, 32, 0, 0);

    public BellBlockEntityRenderer() {
        this.field_20816.addCuboid(-3.0f, -6.0f, -3.0f, 6.0f, 7.0f, 6.0f);
        this.field_20816.setRotationPoint(8.0f, 12.0f, 8.0f);
        ModelPart modelPart = new ModelPart(32, 32, 0, 13);
        modelPart.addCuboid(4.0f, 4.0f, 4.0f, 8.0f, 2.0f, 8.0f);
        modelPart.setRotationPoint(-8.0f, -12.0f, -8.0f);
        this.field_20816.addChild(modelPart);
    }

    protected void method_17139(BellBlockEntity bellBlockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer, BufferBuilder bufferBuilder, int j, int k) {
        float h = (float)bellBlockEntity.ringTicks + g;
        float l = 0.0f;
        float m = 0.0f;
        if (bellBlockEntity.isRinging) {
            float n = MathHelper.sin(h / (float)Math.PI) / (4.0f + h / 3.0f);
            if (bellBlockEntity.lastSideHit == Direction.NORTH) {
                l = -n;
            } else if (bellBlockEntity.lastSideHit == Direction.SOUTH) {
                l = n;
            } else if (bellBlockEntity.lastSideHit == Direction.EAST) {
                m = -n;
            } else if (bellBlockEntity.lastSideHit == Direction.WEST) {
                m = n;
            }
        }
        this.field_20816.pitch = l;
        this.field_20816.roll = m;
        bufferBuilder.method_22629();
        bufferBuilder.method_22631().method_22668();
        this.field_20816.method_22698(bufferBuilder, 0.0625f, j, k, this.method_22739(BELL_BODY_TEXTURE));
        bufferBuilder.method_22630();
    }
}

