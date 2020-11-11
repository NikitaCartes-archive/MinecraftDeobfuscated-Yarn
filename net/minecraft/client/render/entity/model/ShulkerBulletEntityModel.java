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
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class ShulkerBulletEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    private final ModelPart field_27496;
    private final ModelPart bullet;

    public ShulkerBulletEntityModel(ModelPart modelPart) {
        this.field_27496 = modelPart;
        this.bullet = modelPart.getChild("main");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -1.0f, 8.0f, 8.0f, 2.0f).uv(0, 10).cuboid(-1.0f, -4.0f, -4.0f, 2.0f, 8.0f, 8.0f).uv(20, 0).cuboid(-4.0f, -1.0f, -4.0f, 8.0f, 2.0f, 8.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.field_27496;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.bullet.yaw = headYaw * ((float)Math.PI / 180);
        this.bullet.pitch = headPitch * ((float)Math.PI / 180);
    }
}

