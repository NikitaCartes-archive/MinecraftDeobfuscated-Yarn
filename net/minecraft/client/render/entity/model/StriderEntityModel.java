/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class StriderEntityModel<T extends StriderEntity>
extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart body;
    private final ModelPart rightBottomBristle;
    private final ModelPart rightMiddleBristle;
    private final ModelPart rightTopBristle;
    private final ModelPart leftTopBristle;
    private final ModelPart leftMiddleBristle;
    private final ModelPart leftBottomBristle;

    public StriderEntityModel(ModelPart root) {
        this.root = root;
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.body = root.getChild("body");
        this.rightBottomBristle = this.body.getChild("right_bottom_bristle");
        this.rightMiddleBristle = this.body.getChild("right_middle_bristle");
        this.rightTopBristle = this.body.getChild("right_top_bristle");
        this.leftTopBristle = this.body.getChild("left_top_bristle");
        this.leftMiddleBristle = this.body.getChild("left_middle_bristle");
        this.leftBottomBristle = this.body.getChild("left_bottom_bristle");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 32).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f), ModelTransform.pivot(-4.0f, 8.0f, 0.0f));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 55).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 16.0f, 4.0f), ModelTransform.pivot(4.0f, 8.0f, 0.0f));
        ModelPartData modelPartData2 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -6.0f, -8.0f, 16.0f, 14.0f, 16.0f), ModelTransform.pivot(0.0f, 1.0f, 0.0f));
        modelPartData2.addChild("right_bottom_bristle", ModelPartBuilder.create().uv(16, 65).cuboid(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, true), ModelTransform.of(-8.0f, 4.0f, -8.0f, 0.0f, 0.0f, -1.2217305f));
        modelPartData2.addChild("right_middle_bristle", ModelPartBuilder.create().uv(16, 49).cuboid(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, true), ModelTransform.of(-8.0f, -1.0f, -8.0f, 0.0f, 0.0f, -1.134464f));
        modelPartData2.addChild("right_top_bristle", ModelPartBuilder.create().uv(16, 33).cuboid(-12.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f, true), ModelTransform.of(-8.0f, -5.0f, -8.0f, 0.0f, 0.0f, -0.87266463f));
        modelPartData2.addChild("left_top_bristle", ModelPartBuilder.create().uv(16, 33).cuboid(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f), ModelTransform.of(8.0f, -6.0f, -8.0f, 0.0f, 0.0f, 0.87266463f));
        modelPartData2.addChild("left_middle_bristle", ModelPartBuilder.create().uv(16, 49).cuboid(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f), ModelTransform.of(8.0f, -2.0f, -8.0f, 0.0f, 0.0f, 1.134464f));
        modelPartData2.addChild("left_bottom_bristle", ModelPartBuilder.create().uv(16, 65).cuboid(0.0f, 0.0f, 0.0f, 12.0f, 0.0f, 16.0f), ModelTransform.of(8.0f, 3.0f, -8.0f, 0.0f, 0.0f, 1.2217305f));
        return TexturedModelData.of(modelData, 64, 128);
    }

    @Override
    public void setAngles(StriderEntity striderEntity, float f, float g, float h, float i, float j) {
        g = Math.min(0.25f, g);
        if (!striderEntity.hasPassengers()) {
            this.body.pitch = j * ((float)Math.PI / 180);
            this.body.yaw = i * ((float)Math.PI / 180);
        } else {
            this.body.pitch = 0.0f;
            this.body.yaw = 0.0f;
        }
        float k = 1.5f;
        this.body.roll = 0.1f * MathHelper.sin(f * 1.5f) * 4.0f * g;
        this.body.pivotY = 2.0f;
        this.body.pivotY -= 2.0f * MathHelper.cos(f * 1.5f) * 2.0f * g;
        this.leftLeg.pitch = MathHelper.sin(f * 1.5f * 0.5f) * 2.0f * g;
        this.rightLeg.pitch = MathHelper.sin(f * 1.5f * 0.5f + (float)Math.PI) * 2.0f * g;
        this.leftLeg.roll = 0.17453292f * MathHelper.cos(f * 1.5f * 0.5f) * g;
        this.rightLeg.roll = 0.17453292f * MathHelper.cos(f * 1.5f * 0.5f + (float)Math.PI) * g;
        this.leftLeg.pivotY = 8.0f + 2.0f * MathHelper.sin(f * 1.5f * 0.5f + (float)Math.PI) * 2.0f * g;
        this.rightLeg.pivotY = 8.0f + 2.0f * MathHelper.sin(f * 1.5f * 0.5f) * 2.0f * g;
        this.rightBottomBristle.roll = -1.2217305f;
        this.rightMiddleBristle.roll = -1.134464f;
        this.rightTopBristle.roll = -0.87266463f;
        this.leftTopBristle.roll = 0.87266463f;
        this.leftMiddleBristle.roll = 1.134464f;
        this.leftBottomBristle.roll = 1.2217305f;
        float l = MathHelper.cos(f * 1.5f + (float)Math.PI) * g;
        this.rightBottomBristle.roll += l * 1.3f;
        this.rightMiddleBristle.roll += l * 1.2f;
        this.rightTopBristle.roll += l * 0.6f;
        this.leftTopBristle.roll += l * 0.6f;
        this.leftMiddleBristle.roll += l * 1.2f;
        this.leftBottomBristle.roll += l * 1.3f;
        float m = 1.0f;
        float n = 1.0f;
        this.rightBottomBristle.roll += 0.05f * MathHelper.sin(h * 1.0f * -0.4f);
        this.rightMiddleBristle.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.2f);
        this.rightTopBristle.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.4f);
        this.leftTopBristle.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.4f);
        this.leftMiddleBristle.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.2f);
        this.leftBottomBristle.roll += 0.05f * MathHelper.sin(h * 1.0f * -0.4f);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}

