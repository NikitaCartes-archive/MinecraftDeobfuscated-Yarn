/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class VexEntityModel
extends BipedEntityModel<VexEntity> {
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public VexEntityModel() {
        super(0.0f, 0.0f, 64, 64);
        this.leftLeg.visible = false;
        this.hat.visible = false;
        this.rightLeg = new ModelPart(this, 32, 0);
        this.rightLeg.addCuboid(-1.0f, -1.0f, -2.0f, 6.0f, 10.0f, 4.0f, 0.0f);
        this.rightLeg.setPivot(-1.9f, 12.0f, 0.0f);
        this.rightWing = new ModelPart(this, 0, 32);
        this.rightWing.addCuboid(-20.0f, 0.0f, 0.0f, 20.0f, 12.0f, 1.0f);
        this.leftWing = new ModelPart(this, 0, 32);
        this.leftWing.mirror = true;
        this.leftWing.addCuboid(0.0f, 0.0f, 0.0f, 20.0f, 12.0f, 1.0f);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.rightWing, this.leftWing));
    }

    @Override
    public void setAngles(VexEntity vexEntity, float f, float g, float h, float i, float j) {
        super.setAngles(vexEntity, f, g, h, i, j);
        if (vexEntity.isCharging()) {
            if (vexEntity.getMainHandStack().isEmpty()) {
                this.rightArm.pitch = 4.712389f;
                this.leftArm.pitch = 4.712389f;
            } else if (vexEntity.getMainArm() == Arm.RIGHT) {
                this.rightArm.pitch = 3.7699115f;
            } else {
                this.leftArm.pitch = 3.7699115f;
            }
        }
        this.rightLeg.pitch += 0.62831855f;
        this.rightWing.pivotZ = 2.0f;
        this.leftWing.pivotZ = 2.0f;
        this.rightWing.pivotY = 1.0f;
        this.leftWing.pivotY = 1.0f;
        this.rightWing.yaw = 0.47123894f + MathHelper.cos(h * 0.8f) * (float)Math.PI * 0.05f;
        this.leftWing.yaw = -this.rightWing.yaw;
        this.leftWing.roll = -0.47123894f;
        this.leftWing.pitch = 0.47123894f;
        this.rightWing.pitch = 0.47123894f;
        this.rightWing.roll = 0.47123894f;
    }
}

