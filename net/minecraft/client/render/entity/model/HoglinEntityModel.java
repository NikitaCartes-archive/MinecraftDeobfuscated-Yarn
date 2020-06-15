/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class HoglinEntityModel<T extends MobEntity>
extends AnimalModel<T> {
    private final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart torso;
    private final ModelPart field_22231;
    private final ModelPart field_22232;
    private final ModelPart field_22233;
    private final ModelPart field_22234;
    private final ModelPart field_25484;

    public HoglinEntityModel() {
        super(true, 8.0f, 6.0f, 1.9f, 2.0f, 24.0f);
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.torso = new ModelPart(this);
        this.torso.setPivot(0.0f, 7.0f, 0.0f);
        this.torso.setTextureOffset(1, 1).addCuboid(-8.0f, -7.0f, -13.0f, 16.0f, 14.0f, 26.0f);
        this.field_25484 = new ModelPart(this);
        this.field_25484.setPivot(0.0f, -14.0f, -5.0f);
        this.field_25484.setTextureOffset(90, 33).addCuboid(0.0f, 0.0f, -9.0f, 0.0f, 10.0f, 19.0f, 0.001f);
        this.torso.addChild(this.field_25484);
        this.head = new ModelPart(this);
        this.head.setPivot(0.0f, 2.0f, -12.0f);
        this.head.setTextureOffset(61, 1).addCuboid(-7.0f, -3.0f, -19.0f, 14.0f, 6.0f, 19.0f);
        this.rightEar = new ModelPart(this);
        this.rightEar.setPivot(-6.0f, -2.0f, -3.0f);
        this.rightEar.setTextureOffset(1, 1).addCuboid(-6.0f, -1.0f, -2.0f, 6.0f, 1.0f, 4.0f);
        this.rightEar.roll = -0.6981317f;
        this.head.addChild(this.rightEar);
        this.leftEar = new ModelPart(this);
        this.leftEar.setPivot(6.0f, -2.0f, -3.0f);
        this.leftEar.setTextureOffset(1, 6).addCuboid(0.0f, -1.0f, -2.0f, 6.0f, 1.0f, 4.0f);
        this.leftEar.roll = 0.6981317f;
        this.head.addChild(this.leftEar);
        ModelPart modelPart = new ModelPart(this);
        modelPart.setPivot(-7.0f, 2.0f, -12.0f);
        modelPart.setTextureOffset(10, 13).addCuboid(-1.0f, -11.0f, -1.0f, 2.0f, 11.0f, 2.0f);
        this.head.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(this);
        modelPart2.setPivot(7.0f, 2.0f, -12.0f);
        modelPart2.setTextureOffset(1, 13).addCuboid(-1.0f, -11.0f, -1.0f, 2.0f, 11.0f, 2.0f);
        this.head.addChild(modelPart2);
        this.head.pitch = 0.87266463f;
        int i = 14;
        int j = 11;
        this.field_22231 = new ModelPart(this);
        this.field_22231.setPivot(-4.0f, 10.0f, -8.5f);
        this.field_22231.setTextureOffset(66, 42).addCuboid(-3.0f, 0.0f, -3.0f, 6.0f, 14.0f, 6.0f);
        this.field_22232 = new ModelPart(this);
        this.field_22232.setPivot(4.0f, 10.0f, -8.5f);
        this.field_22232.setTextureOffset(41, 42).addCuboid(-3.0f, 0.0f, -3.0f, 6.0f, 14.0f, 6.0f);
        this.field_22233 = new ModelPart(this);
        this.field_22233.setPivot(-5.0f, 13.0f, 10.0f);
        this.field_22233.setTextureOffset(21, 45).addCuboid(-2.5f, 0.0f, -2.5f, 5.0f, 11.0f, 5.0f);
        this.field_22234 = new ModelPart(this);
        this.field_22234.setPivot(5.0f, 13.0f, 10.0f);
        this.field_22234.setTextureOffset(0, 45).addCuboid(-2.5f, 0.0f, -2.5f, 5.0f, 11.0f, 5.0f);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.field_22231, this.field_22232, this.field_22233, this.field_22234);
    }

    @Override
    public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
        this.rightEar.roll = -0.6981317f - g * MathHelper.sin(f);
        this.leftEar.roll = 0.6981317f + g * MathHelper.sin(f);
        this.head.yaw = i * ((float)Math.PI / 180);
        int k = ((Hoglin)mobEntity).getMovementCooldownTicks();
        float l = 1.0f - (float)MathHelper.abs(10 - 2 * k) / 10.0f;
        this.head.pitch = MathHelper.lerp(l, 0.87266463f, -0.34906584f);
        if (((LivingEntity)mobEntity).isBaby()) {
            this.head.pivotY = MathHelper.lerp(l, 2.0f, 5.0f);
            this.field_25484.pivotZ = -3.0f;
        } else {
            this.head.pivotY = 2.0f;
            this.field_25484.pivotZ = -7.0f;
        }
        float m = 1.2f;
        this.field_22231.pitch = MathHelper.cos(f) * 1.2f * g;
        this.field_22233.pitch = this.field_22232.pitch = MathHelper.cos(f + (float)Math.PI) * 1.2f * g;
        this.field_22234.pitch = this.field_22231.pitch;
    }
}

