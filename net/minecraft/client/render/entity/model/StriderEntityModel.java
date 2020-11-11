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
    private final ModelPart field_27514;
    private final ModelPart field_23353;
    private final ModelPart field_23354;
    private final ModelPart field_23355;
    private final ModelPart field_27515;
    private final ModelPart field_27516;
    private final ModelPart field_27517;
    private final ModelPart field_27518;
    private final ModelPart field_27519;
    private final ModelPart field_27520;

    public StriderEntityModel(ModelPart modelPart) {
        this.field_27514 = modelPart;
        this.field_23353 = modelPart.getChild("right_leg");
        this.field_23354 = modelPart.getChild("left_leg");
        this.field_23355 = modelPart.getChild("body");
        this.field_27515 = this.field_23355.getChild("right_bottom_bristle");
        this.field_27516 = this.field_23355.getChild("right_middle_bristle");
        this.field_27517 = this.field_23355.getChild("right_top_bristle");
        this.field_27518 = this.field_23355.getChild("left_top_bristle");
        this.field_27519 = this.field_23355.getChild("left_middle_bristle");
        this.field_27520 = this.field_23355.getChild("left_bottom_bristle");
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
            this.field_23355.pitch = j * ((float)Math.PI / 180);
            this.field_23355.yaw = i * ((float)Math.PI / 180);
        } else {
            this.field_23355.pitch = 0.0f;
            this.field_23355.yaw = 0.0f;
        }
        float k = 1.5f;
        this.field_23355.roll = 0.1f * MathHelper.sin(f * 1.5f) * 4.0f * g;
        this.field_23355.pivotY = 2.0f;
        this.field_23355.pivotY -= 2.0f * MathHelper.cos(f * 1.5f) * 2.0f * g;
        this.field_23354.pitch = MathHelper.sin(f * 1.5f * 0.5f) * 2.0f * g;
        this.field_23353.pitch = MathHelper.sin(f * 1.5f * 0.5f + (float)Math.PI) * 2.0f * g;
        this.field_23354.roll = 0.17453292f * MathHelper.cos(f * 1.5f * 0.5f) * g;
        this.field_23353.roll = 0.17453292f * MathHelper.cos(f * 1.5f * 0.5f + (float)Math.PI) * g;
        this.field_23354.pivotY = 8.0f + 2.0f * MathHelper.sin(f * 1.5f * 0.5f + (float)Math.PI) * 2.0f * g;
        this.field_23353.pivotY = 8.0f + 2.0f * MathHelper.sin(f * 1.5f * 0.5f) * 2.0f * g;
        this.field_27515.roll = -1.2217305f;
        this.field_27516.roll = -1.134464f;
        this.field_27517.roll = -0.87266463f;
        this.field_27518.roll = 0.87266463f;
        this.field_27519.roll = 1.134464f;
        this.field_27520.roll = 1.2217305f;
        float l = MathHelper.cos(f * 1.5f + (float)Math.PI) * g;
        this.field_27515.roll += l * 1.3f;
        this.field_27516.roll += l * 1.2f;
        this.field_27517.roll += l * 0.6f;
        this.field_27518.roll += l * 0.6f;
        this.field_27519.roll += l * 1.2f;
        this.field_27520.roll += l * 1.3f;
        float m = 1.0f;
        float n = 1.0f;
        this.field_27515.roll += 0.05f * MathHelper.sin(h * 1.0f * -0.4f);
        this.field_27516.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.2f);
        this.field_27517.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.4f);
        this.field_27518.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.4f);
        this.field_27519.roll += 0.1f * MathHelper.sin(h * 1.0f * 0.2f);
        this.field_27520.roll += 0.05f * MathHelper.sin(h * 1.0f * -0.4f);
    }

    @Override
    public ModelPart getPart() {
        return this.field_27514;
    }
}

