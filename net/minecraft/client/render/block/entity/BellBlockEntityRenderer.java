/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BellBlockEntityRenderer
extends BlockEntityRenderer<BellBlockEntity> {
    public static final Identifier BELL_BODY_TEXTURE = new Identifier("entity/bell/bell_body");
    private final ModelPart field_20816 = new ModelPart(32, 32, 0, 0);

    public BellBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
        this.field_20816.addCuboid(-3.0f, -6.0f, -3.0f, 6.0f, 7.0f, 6.0f);
        this.field_20816.setRotationPoint(8.0f, 12.0f, 8.0f);
        ModelPart modelPart = new ModelPart(32, 32, 0, 13);
        modelPart.addCuboid(4.0f, 4.0f, 4.0f, 8.0f, 2.0f, 8.0f);
        modelPart.setRotationPoint(-8.0f, -12.0f, -8.0f);
        this.field_20816.addChild(modelPart);
    }

    public void method_17139(BellBlockEntity bellBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
        float h = (float)bellBlockEntity.ringTicks + g;
        float j = 0.0f;
        float k = 0.0f;
        if (bellBlockEntity.isRinging) {
            float l = MathHelper.sin(h / (float)Math.PI) / (4.0f + h / 3.0f);
            if (bellBlockEntity.lastSideHit == Direction.NORTH) {
                j = -l;
            } else if (bellBlockEntity.lastSideHit == Direction.SOUTH) {
                j = l;
            } else if (bellBlockEntity.lastSideHit == Direction.EAST) {
                k = -l;
            } else if (bellBlockEntity.lastSideHit == Direction.WEST) {
                k = l;
            }
        }
        this.field_20816.pitch = j;
        this.field_20816.roll = k;
        class_4588 lv = arg2.getBuffer(BlockRenderLayer.SOLID);
        this.field_20816.method_22698(arg, lv, 0.0625f, i, this.method_23082(BELL_BODY_TEXTURE));
    }
}

