/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4836;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class class_4840<T extends class_4836>
extends BipedEntityModel<T> {
    public final ModelPart field_22404;
    public final ModelPart field_22405;

    public class_4840(float f, int i, int j) {
        super(f, 0.0f, i, j);
        this.torso = new ModelPart(this, 16, 16);
        this.torso.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, f);
        this.head = new ModelPart(this);
        this.head.setTextureOffset(0, 0).addCuboid(-5.0f, -8.0f, -4.0f, 10.0f, 8.0f, 8.0f, f);
        this.head.setTextureOffset(31, 1).addCuboid(-2.0f, -4.0f, -5.0f, 4.0f, 4.0f, 1.0f, f);
        this.head.setTextureOffset(2, 4).addCuboid(2.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, f);
        this.head.setTextureOffset(2, 0).addCuboid(-3.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, f);
        this.field_22404 = new ModelPart(this);
        this.field_22404.setPivot(4.5f, -6.0f, 0.0f);
        this.field_22404.setTextureOffset(57, 38).addCuboid(0.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, f);
        this.head.addChild(this.field_22404);
        this.field_22405 = new ModelPart(this);
        this.field_22405.setPivot(-4.5f, -6.0f, 0.0f);
        this.head.addChild(this.field_22405);
        this.field_22405.setTextureOffset(57, 22).addCuboid(-1.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, f);
        this.helmet = new ModelPart(this);
        this.helmet.setPivot(0.0f, 0.0f, 0.0f);
        this.rightArm = new ModelPart(this);
        this.rightArm.setPivot(-5.0f, 2.0f, 0.0f);
        this.rightArm.setTextureOffset(40, 16).addCuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.leftArm = new ModelPart(this);
        this.leftArm.setPivot(5.0f, 2.0f, 0.0f);
        this.leftArm.setTextureOffset(40, 16).addCuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.rightLeg = new ModelPart(this);
        this.rightLeg.setPivot(-1.9f, 12.0f, 0.0f);
        this.rightLeg.setTextureOffset(0, 16).addCuboid(-2.1f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.leftLeg = new ModelPart(this);
        this.leftLeg.setPivot(1.9f, 12.0f, 0.0f);
        this.leftLeg.setTextureOffset(0, 16).addCuboid(-1.9f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
    }

    @Override
    public void setAngles(T arg, float f, float g, float h, float i, float j) {
        super.setAngles(arg, f, g, h, i, j);
        float k = 0.5235988f;
        float l = h * 0.1f + f * 0.5f;
        float m = 0.08f + g * 0.4f;
        this.field_22404.roll = -0.5235988f - MathHelper.cos(l * 1.2f) * m;
        this.field_22405.roll = 0.5235988f + MathHelper.cos(l) * m;
        class_4836.class_4837 lv = ((class_4836)arg).method_24705();
        if (lv == class_4836.class_4837.field_22383) {
            this.rightArm.yaw = -0.3f + this.head.yaw;
            this.leftArm.yaw = 0.6f + this.head.yaw;
            this.rightArm.pitch = -1.5707964f + this.head.pitch + 0.1f;
            this.leftArm.pitch = -1.5f + this.head.pitch;
        } else if (lv == class_4836.class_4837.field_22384) {
            this.rightArm.yaw = -0.8f;
            this.rightArm.pitch = -0.97079635f;
            this.leftArm.pitch = -0.97079635f;
            float n = MathHelper.clamp(((LivingEntity)arg).getItemUseTime(), 0, 25);
            float o = n / 25.0f;
            this.leftArm.yaw = MathHelper.lerp(o, 0.4f, 0.85f);
            this.leftArm.pitch = MathHelper.lerp(o, this.leftArm.pitch, -1.5707964f);
        } else if (lv == class_4836.class_4837.field_22385) {
            this.leftArm.yaw = 0.5f;
            this.leftArm.pitch = -0.9f;
            this.head.pitch = 0.5f;
            this.head.yaw = 0.0f;
        }
    }
}

