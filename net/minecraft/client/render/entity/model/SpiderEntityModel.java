/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SpiderEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart head;
    private final ModelPart thorax;
    private final ModelPart abdomen;
    private final ModelPart rightBackLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightBackMiddleLeg;
    private final ModelPart leftBackMiddleLeg;
    private final ModelPart rightFrontMiddleLeg;
    private final ModelPart leftFrontMiddleLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    public SpiderEntityModel() {
        float f = 0.0f;
        int i = 15;
        this.head = new ModelPart(this, 32, 4);
        this.head.addCuboid(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, 0.0f);
        this.head.setPivot(0.0f, 15.0f, -3.0f);
        this.thorax = new ModelPart(this, 0, 0);
        this.thorax.addCuboid(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f, 0.0f);
        this.thorax.setPivot(0.0f, 15.0f, 0.0f);
        this.abdomen = new ModelPart(this, 0, 12);
        this.abdomen.addCuboid(-5.0f, -4.0f, -6.0f, 10.0f, 8.0f, 12.0f, 0.0f);
        this.abdomen.setPivot(0.0f, 15.0f, 9.0f);
        this.rightBackLeg = new ModelPart(this, 18, 0);
        this.rightBackLeg.addCuboid(-15.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.rightBackLeg.setPivot(-4.0f, 15.0f, 2.0f);
        this.leftBackLeg = new ModelPart(this, 18, 0);
        this.leftBackLeg.addCuboid(-1.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.leftBackLeg.setPivot(4.0f, 15.0f, 2.0f);
        this.rightBackMiddleLeg = new ModelPart(this, 18, 0);
        this.rightBackMiddleLeg.addCuboid(-15.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.rightBackMiddleLeg.setPivot(-4.0f, 15.0f, 1.0f);
        this.leftBackMiddleLeg = new ModelPart(this, 18, 0);
        this.leftBackMiddleLeg.addCuboid(-1.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.leftBackMiddleLeg.setPivot(4.0f, 15.0f, 1.0f);
        this.rightFrontMiddleLeg = new ModelPart(this, 18, 0);
        this.rightFrontMiddleLeg.addCuboid(-15.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.rightFrontMiddleLeg.setPivot(-4.0f, 15.0f, 0.0f);
        this.leftFrontMiddleLeg = new ModelPart(this, 18, 0);
        this.leftFrontMiddleLeg.addCuboid(-1.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.leftFrontMiddleLeg.setPivot(4.0f, 15.0f, 0.0f);
        this.rightFrontLeg = new ModelPart(this, 18, 0);
        this.rightFrontLeg.addCuboid(-15.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.rightFrontLeg.setPivot(-4.0f, 15.0f, -1.0f);
        this.leftFrontLeg = new ModelPart(this, 18, 0);
        this.leftFrontLeg.addCuboid(-1.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f, 0.0f);
        this.leftFrontLeg.setPivot(4.0f, 15.0f, -1.0f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.head, this.thorax, this.abdomen, this.rightBackLeg, this.leftBackLeg, this.rightBackMiddleLeg, this.leftBackMiddleLeg, this.rightFrontMiddleLeg, this.leftFrontMiddleLeg, this.rightFrontLeg, this.leftFrontLeg);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j) {
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.pitch = j * ((float)Math.PI / 180);
        float k = 0.7853982f;
        this.rightBackLeg.roll = -0.7853982f;
        this.leftBackLeg.roll = 0.7853982f;
        this.rightBackMiddleLeg.roll = -0.58119464f;
        this.leftBackMiddleLeg.roll = 0.58119464f;
        this.rightFrontMiddleLeg.roll = -0.58119464f;
        this.leftFrontMiddleLeg.roll = 0.58119464f;
        this.rightFrontLeg.roll = -0.7853982f;
        this.leftFrontLeg.roll = 0.7853982f;
        float l = -0.0f;
        float m = 0.3926991f;
        this.rightBackLeg.yaw = 0.7853982f;
        this.leftBackLeg.yaw = -0.7853982f;
        this.rightBackMiddleLeg.yaw = 0.3926991f;
        this.leftBackMiddleLeg.yaw = -0.3926991f;
        this.rightFrontMiddleLeg.yaw = -0.3926991f;
        this.leftFrontMiddleLeg.yaw = 0.3926991f;
        this.rightFrontLeg.yaw = -0.7853982f;
        this.leftFrontLeg.yaw = 0.7853982f;
        float n = -(MathHelper.cos(f * 0.6662f * 2.0f + 0.0f) * 0.4f) * g;
        float o = -(MathHelper.cos(f * 0.6662f * 2.0f + (float)Math.PI) * 0.4f) * g;
        float p = -(MathHelper.cos(f * 0.6662f * 2.0f + 1.5707964f) * 0.4f) * g;
        float q = -(MathHelper.cos(f * 0.6662f * 2.0f + 4.712389f) * 0.4f) * g;
        float r = Math.abs(MathHelper.sin(f * 0.6662f + 0.0f) * 0.4f) * g;
        float s = Math.abs(MathHelper.sin(f * 0.6662f + (float)Math.PI) * 0.4f) * g;
        float t = Math.abs(MathHelper.sin(f * 0.6662f + 1.5707964f) * 0.4f) * g;
        float u = Math.abs(MathHelper.sin(f * 0.6662f + 4.712389f) * 0.4f) * g;
        this.rightBackLeg.yaw += n;
        this.leftBackLeg.yaw += -n;
        this.rightBackMiddleLeg.yaw += o;
        this.leftBackMiddleLeg.yaw += -o;
        this.rightFrontMiddleLeg.yaw += p;
        this.leftFrontMiddleLeg.yaw += -p;
        this.rightFrontLeg.yaw += q;
        this.leftFrontLeg.yaw += -q;
        this.rightBackLeg.roll += r;
        this.leftBackLeg.roll += -r;
        this.rightBackMiddleLeg.roll += s;
        this.leftBackMiddleLeg.roll += -s;
        this.rightFrontMiddleLeg.roll += t;
        this.leftFrontMiddleLeg.roll += -t;
        this.rightFrontLeg.roll += u;
        this.leftFrontLeg.roll += -u;
    }
}

