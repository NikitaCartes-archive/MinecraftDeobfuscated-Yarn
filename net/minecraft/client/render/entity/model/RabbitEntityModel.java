/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RabbitEntityModel<T extends RabbitEntity>
extends EntityModel<T> {
    private final ModelPart leftFoot = new ModelPart(this, 26, 24);
    private final ModelPart rightFoot;
    private final ModelPart leftBackLeg;
    private final ModelPart rightBackLeg;
    private final ModelPart torso;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart tail;
    private final ModelPart nose;
    private float field_3531;

    public RabbitEntityModel() {
        this.leftFoot.addCuboid(-1.0f, 5.5f, -3.7f, 2.0f, 1.0f, 7.0f);
        this.leftFoot.setPivot(3.0f, 17.5f, 3.7f);
        this.leftFoot.mirror = true;
        this.method_2827(this.leftFoot, 0.0f, 0.0f, 0.0f);
        this.rightFoot = new ModelPart(this, 8, 24);
        this.rightFoot.addCuboid(-1.0f, 5.5f, -3.7f, 2.0f, 1.0f, 7.0f);
        this.rightFoot.setPivot(-3.0f, 17.5f, 3.7f);
        this.rightFoot.mirror = true;
        this.method_2827(this.rightFoot, 0.0f, 0.0f, 0.0f);
        this.leftBackLeg = new ModelPart(this, 30, 15);
        this.leftBackLeg.addCuboid(-1.0f, 0.0f, 0.0f, 2.0f, 4.0f, 5.0f);
        this.leftBackLeg.setPivot(3.0f, 17.5f, 3.7f);
        this.leftBackLeg.mirror = true;
        this.method_2827(this.leftBackLeg, -0.34906584f, 0.0f, 0.0f);
        this.rightBackLeg = new ModelPart(this, 16, 15);
        this.rightBackLeg.addCuboid(-1.0f, 0.0f, 0.0f, 2.0f, 4.0f, 5.0f);
        this.rightBackLeg.setPivot(-3.0f, 17.5f, 3.7f);
        this.rightBackLeg.mirror = true;
        this.method_2827(this.rightBackLeg, -0.34906584f, 0.0f, 0.0f);
        this.torso = new ModelPart(this, 0, 0);
        this.torso.addCuboid(-3.0f, -2.0f, -10.0f, 6.0f, 5.0f, 10.0f);
        this.torso.setPivot(0.0f, 19.0f, 8.0f);
        this.torso.mirror = true;
        this.method_2827(this.torso, -0.34906584f, 0.0f, 0.0f);
        this.leftFrontLeg = new ModelPart(this, 8, 15);
        this.leftFrontLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f);
        this.leftFrontLeg.setPivot(3.0f, 17.0f, -1.0f);
        this.leftFrontLeg.mirror = true;
        this.method_2827(this.leftFrontLeg, -0.17453292f, 0.0f, 0.0f);
        this.rightFrontLeg = new ModelPart(this, 0, 15);
        this.rightFrontLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f);
        this.rightFrontLeg.setPivot(-3.0f, 17.0f, -1.0f);
        this.rightFrontLeg.mirror = true;
        this.method_2827(this.rightFrontLeg, -0.17453292f, 0.0f, 0.0f);
        this.head = new ModelPart(this, 32, 0);
        this.head.addCuboid(-2.5f, -4.0f, -5.0f, 5.0f, 4.0f, 5.0f);
        this.head.setPivot(0.0f, 16.0f, -1.0f);
        this.head.mirror = true;
        this.method_2827(this.head, 0.0f, 0.0f, 0.0f);
        this.rightEar = new ModelPart(this, 52, 0);
        this.rightEar.addCuboid(-2.5f, -9.0f, -1.0f, 2.0f, 5.0f, 1.0f);
        this.rightEar.setPivot(0.0f, 16.0f, -1.0f);
        this.rightEar.mirror = true;
        this.method_2827(this.rightEar, 0.0f, -0.2617994f, 0.0f);
        this.leftEar = new ModelPart(this, 58, 0);
        this.leftEar.addCuboid(0.5f, -9.0f, -1.0f, 2.0f, 5.0f, 1.0f);
        this.leftEar.setPivot(0.0f, 16.0f, -1.0f);
        this.leftEar.mirror = true;
        this.method_2827(this.leftEar, 0.0f, 0.2617994f, 0.0f);
        this.tail = new ModelPart(this, 52, 6);
        this.tail.addCuboid(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 2.0f);
        this.tail.setPivot(0.0f, 20.0f, 7.0f);
        this.tail.mirror = true;
        this.method_2827(this.tail, -0.3490659f, 0.0f, 0.0f);
        this.nose = new ModelPart(this, 32, 9);
        this.nose.addCuboid(-0.5f, -2.5f, -5.5f, 1.0f, 1.0f, 1.0f);
        this.nose.setPivot(0.0f, 16.0f, -1.0f);
        this.nose.mirror = true;
        this.method_2827(this.nose, 0.0f, 0.0f, 0.0f);
    }

    private void method_2827(ModelPart modelPart, float f, float g, float h) {
        modelPart.pitch = f;
        modelPart.yaw = g;
        modelPart.roll = h;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (this.child) {
            float f = 1.5f;
            matrices.push();
            matrices.scale(0.56666666f, 0.56666666f, 0.56666666f);
            matrices.translate(0.0, 1.375, 0.125);
            ImmutableList.of(this.head, this.leftEar, this.rightEar, this.nose).forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
            matrices.pop();
            matrices.push();
            matrices.scale(0.4f, 0.4f, 0.4f);
            matrices.translate(0.0, 2.25, 0.0);
            ImmutableList.of(this.leftFoot, this.rightFoot, this.leftBackLeg, this.rightBackLeg, this.torso, this.leftFrontLeg, this.rightFrontLeg, this.tail).forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
            matrices.pop();
        } else {
            matrices.push();
            matrices.scale(0.6f, 0.6f, 0.6f);
            matrices.translate(0.0, 1.0, 0.0);
            ImmutableList.of(this.leftFoot, this.rightFoot, this.leftBackLeg, this.rightBackLeg, this.torso, this.leftFrontLeg, this.rightFrontLeg, this.head, this.rightEar, this.leftEar, this.tail, this.nose, new ModelPart[0]).forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
            matrices.pop();
        }
    }

    @Override
    public void setAngles(T rabbitEntity, float f, float g, float h, float i, float j) {
        float k = h - (float)((RabbitEntity)rabbitEntity).age;
        this.nose.pitch = j * ((float)Math.PI / 180);
        this.head.pitch = j * ((float)Math.PI / 180);
        this.rightEar.pitch = j * ((float)Math.PI / 180);
        this.leftEar.pitch = j * ((float)Math.PI / 180);
        this.nose.yaw = i * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.rightEar.yaw = this.nose.yaw - 0.2617994f;
        this.leftEar.yaw = this.nose.yaw + 0.2617994f;
        this.field_3531 = MathHelper.sin(((RabbitEntity)rabbitEntity).getJumpProgress(k) * (float)Math.PI);
        this.leftBackLeg.pitch = (this.field_3531 * 50.0f - 21.0f) * ((float)Math.PI / 180);
        this.rightBackLeg.pitch = (this.field_3531 * 50.0f - 21.0f) * ((float)Math.PI / 180);
        this.leftFoot.pitch = this.field_3531 * 50.0f * ((float)Math.PI / 180);
        this.rightFoot.pitch = this.field_3531 * 50.0f * ((float)Math.PI / 180);
        this.leftFrontLeg.pitch = (this.field_3531 * -40.0f - 11.0f) * ((float)Math.PI / 180);
        this.rightFrontLeg.pitch = (this.field_3531 * -40.0f - 11.0f) * ((float)Math.PI / 180);
    }

    @Override
    public void animateModel(T rabbitEntity, float f, float g, float h) {
        super.animateModel(rabbitEntity, f, g, h);
        this.field_3531 = MathHelper.sin(((RabbitEntity)rabbitEntity).getJumpProgress(h) * (float)Math.PI);
    }
}

