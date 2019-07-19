/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RabbitEntityModel<T extends RabbitEntity>
extends EntityModel<T> {
    private final ModelPart field_3525 = new ModelPart(this, 26, 24);
    private final ModelPart field_3532;
    private final ModelPart field_3526;
    private final ModelPart field_3522;
    private final ModelPart field_3528;
    private final ModelPart field_3527;
    private final ModelPart field_3521;
    private final ModelPart field_3529;
    private final ModelPart field_3523;
    private final ModelPart field_3520;
    private final ModelPart field_3524;
    private final ModelPart field_3530;
    private float field_3531;

    public RabbitEntityModel() {
        this.field_3525.addCuboid(-1.0f, 5.5f, -3.7f, 2, 1, 7);
        this.field_3525.setPivot(3.0f, 17.5f, 3.7f);
        this.field_3525.mirror = true;
        this.method_2827(this.field_3525, 0.0f, 0.0f, 0.0f);
        this.field_3532 = new ModelPart(this, 8, 24);
        this.field_3532.addCuboid(-1.0f, 5.5f, -3.7f, 2, 1, 7);
        this.field_3532.setPivot(-3.0f, 17.5f, 3.7f);
        this.field_3532.mirror = true;
        this.method_2827(this.field_3532, 0.0f, 0.0f, 0.0f);
        this.field_3526 = new ModelPart(this, 30, 15);
        this.field_3526.addCuboid(-1.0f, 0.0f, 0.0f, 2, 4, 5);
        this.field_3526.setPivot(3.0f, 17.5f, 3.7f);
        this.field_3526.mirror = true;
        this.method_2827(this.field_3526, -0.34906584f, 0.0f, 0.0f);
        this.field_3522 = new ModelPart(this, 16, 15);
        this.field_3522.addCuboid(-1.0f, 0.0f, 0.0f, 2, 4, 5);
        this.field_3522.setPivot(-3.0f, 17.5f, 3.7f);
        this.field_3522.mirror = true;
        this.method_2827(this.field_3522, -0.34906584f, 0.0f, 0.0f);
        this.field_3528 = new ModelPart(this, 0, 0);
        this.field_3528.addCuboid(-3.0f, -2.0f, -10.0f, 6, 5, 10);
        this.field_3528.setPivot(0.0f, 19.0f, 8.0f);
        this.field_3528.mirror = true;
        this.method_2827(this.field_3528, -0.34906584f, 0.0f, 0.0f);
        this.field_3527 = new ModelPart(this, 8, 15);
        this.field_3527.addCuboid(-1.0f, 0.0f, -1.0f, 2, 7, 2);
        this.field_3527.setPivot(3.0f, 17.0f, -1.0f);
        this.field_3527.mirror = true;
        this.method_2827(this.field_3527, -0.17453292f, 0.0f, 0.0f);
        this.field_3521 = new ModelPart(this, 0, 15);
        this.field_3521.addCuboid(-1.0f, 0.0f, -1.0f, 2, 7, 2);
        this.field_3521.setPivot(-3.0f, 17.0f, -1.0f);
        this.field_3521.mirror = true;
        this.method_2827(this.field_3521, -0.17453292f, 0.0f, 0.0f);
        this.field_3529 = new ModelPart(this, 32, 0);
        this.field_3529.addCuboid(-2.5f, -4.0f, -5.0f, 5, 4, 5);
        this.field_3529.setPivot(0.0f, 16.0f, -1.0f);
        this.field_3529.mirror = true;
        this.method_2827(this.field_3529, 0.0f, 0.0f, 0.0f);
        this.field_3523 = new ModelPart(this, 52, 0);
        this.field_3523.addCuboid(-2.5f, -9.0f, -1.0f, 2, 5, 1);
        this.field_3523.setPivot(0.0f, 16.0f, -1.0f);
        this.field_3523.mirror = true;
        this.method_2827(this.field_3523, 0.0f, -0.2617994f, 0.0f);
        this.field_3520 = new ModelPart(this, 58, 0);
        this.field_3520.addCuboid(0.5f, -9.0f, -1.0f, 2, 5, 1);
        this.field_3520.setPivot(0.0f, 16.0f, -1.0f);
        this.field_3520.mirror = true;
        this.method_2827(this.field_3520, 0.0f, 0.2617994f, 0.0f);
        this.field_3524 = new ModelPart(this, 52, 6);
        this.field_3524.addCuboid(-1.5f, -1.5f, 0.0f, 3, 3, 2);
        this.field_3524.setPivot(0.0f, 20.0f, 7.0f);
        this.field_3524.mirror = true;
        this.method_2827(this.field_3524, -0.3490659f, 0.0f, 0.0f);
        this.field_3530 = new ModelPart(this, 32, 9);
        this.field_3530.addCuboid(-0.5f, -2.5f, -5.5f, 1, 1, 1);
        this.field_3530.setPivot(0.0f, 16.0f, -1.0f);
        this.field_3530.mirror = true;
        this.method_2827(this.field_3530, 0.0f, 0.0f, 0.0f);
    }

    private void method_2827(ModelPart modelPart, float f, float g, float h) {
        modelPart.pitch = f;
        modelPart.yaw = g;
        modelPart.roll = h;
    }

    @Override
    public void render(T rabbitEntity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(rabbitEntity, f, g, h, i, j, k);
        if (this.child) {
            float l = 1.5f;
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.56666666f, 0.56666666f, 0.56666666f);
            GlStateManager.translatef(0.0f, 22.0f * k, 2.0f * k);
            this.field_3529.render(k);
            this.field_3520.render(k);
            this.field_3523.render(k);
            this.field_3530.render(k);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.4f, 0.4f, 0.4f);
            GlStateManager.translatef(0.0f, 36.0f * k, 0.0f);
            this.field_3525.render(k);
            this.field_3532.render(k);
            this.field_3526.render(k);
            this.field_3522.render(k);
            this.field_3528.render(k);
            this.field_3527.render(k);
            this.field_3521.render(k);
            this.field_3524.render(k);
            GlStateManager.popMatrix();
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.6f, 0.6f, 0.6f);
            GlStateManager.translatef(0.0f, 16.0f * k, 0.0f);
            this.field_3525.render(k);
            this.field_3532.render(k);
            this.field_3526.render(k);
            this.field_3522.render(k);
            this.field_3528.render(k);
            this.field_3527.render(k);
            this.field_3521.render(k);
            this.field_3529.render(k);
            this.field_3523.render(k);
            this.field_3520.render(k);
            this.field_3524.render(k);
            this.field_3530.render(k);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void setAngles(T rabbitEntity, float f, float g, float h, float i, float j, float k) {
        float l = h - (float)((RabbitEntity)rabbitEntity).age;
        this.field_3530.pitch = j * ((float)Math.PI / 180);
        this.field_3529.pitch = j * ((float)Math.PI / 180);
        this.field_3523.pitch = j * ((float)Math.PI / 180);
        this.field_3520.pitch = j * ((float)Math.PI / 180);
        this.field_3530.yaw = i * ((float)Math.PI / 180);
        this.field_3529.yaw = i * ((float)Math.PI / 180);
        this.field_3523.yaw = this.field_3530.yaw - 0.2617994f;
        this.field_3520.yaw = this.field_3530.yaw + 0.2617994f;
        this.field_3531 = MathHelper.sin(((RabbitEntity)rabbitEntity).method_6605(l) * (float)Math.PI);
        this.field_3526.pitch = (this.field_3531 * 50.0f - 21.0f) * ((float)Math.PI / 180);
        this.field_3522.pitch = (this.field_3531 * 50.0f - 21.0f) * ((float)Math.PI / 180);
        this.field_3525.pitch = this.field_3531 * 50.0f * ((float)Math.PI / 180);
        this.field_3532.pitch = this.field_3531 * 50.0f * ((float)Math.PI / 180);
        this.field_3527.pitch = (this.field_3531 * -40.0f - 11.0f) * ((float)Math.PI / 180);
        this.field_3521.pitch = (this.field_3531 * -40.0f - 11.0f) * ((float)Math.PI / 180);
    }

    @Override
    public void animateModel(T rabbitEntity, float f, float g, float h) {
        super.animateModel(rabbitEntity, f, g, h);
        this.field_3531 = MathHelper.sin(((RabbitEntity)rabbitEntity).method_6605(h) * (float)Math.PI);
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles((T)((RabbitEntity)entity), f, g, h, i, j, k);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.render((T)((RabbitEntity)entity), f, g, h, i, j, k);
    }
}

