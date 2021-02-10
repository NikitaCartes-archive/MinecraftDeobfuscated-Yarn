/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class HorseEntityModel<T extends HorseBaseEntity>
extends AnimalModel<T> {
    protected final ModelPart body;
    protected final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightHindBabyLeg;
    private final ModelPart leftHindBabyLeg;
    private final ModelPart rightFrontBabyLeg;
    private final ModelPart leftFrontBabyLeg;
    private final ModelPart tail;
    private final ModelPart[] saddle;
    private final ModelPart[] straps;

    public HorseEntityModel(ModelPart root) {
        super(true, 16.2f, 1.36f, 2.7272f, 2.0f, 20.0f);
        this.body = root.getChild("body");
        this.head = root.getChild("head_parts");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.rightHindBabyLeg = root.getChild("right_hind_baby_leg");
        this.leftHindBabyLeg = root.getChild("left_hind_baby_leg");
        this.rightFrontBabyLeg = root.getChild("right_front_baby_leg");
        this.leftFrontBabyLeg = root.getChild("left_front_baby_leg");
        this.tail = this.body.getChild("tail");
        ModelPart modelPart = this.body.getChild("saddle");
        ModelPart modelPart2 = this.head.getChild("left_saddle_mouth");
        ModelPart modelPart3 = this.head.getChild("right_saddle_mouth");
        ModelPart modelPart4 = this.head.getChild("left_saddle_line");
        ModelPart modelPart5 = this.head.getChild("right_saddle_line");
        ModelPart modelPart6 = this.head.getChild("head_saddle");
        ModelPart modelPart7 = this.head.getChild("mouth_saddle_wrap");
        this.saddle = new ModelPart[]{modelPart, modelPart2, modelPart3, modelPart6, modelPart7};
        this.straps = new ModelPart[]{modelPart4, modelPart5};
    }

    public static ModelData getModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 32).cuboid(-5.0f, -8.0f, -17.0f, 10.0f, 10.0f, 22.0f, new Dilation(0.05f)), ModelTransform.pivot(0.0f, 11.0f, 5.0f));
        ModelPartData modelPartData3 = modelPartData.addChild("head_parts", ModelPartBuilder.create().uv(0, 35).cuboid(-2.05f, -6.0f, -2.0f, 4.0f, 12.0f, 7.0f), ModelTransform.of(0.0f, 4.0f, -12.0f, 0.5235988f, 0.0f, 0.0f));
        ModelPartData modelPartData4 = modelPartData3.addChild("head", ModelPartBuilder.create().uv(0, 13).cuboid(-3.0f, -11.0f, -2.0f, 6.0f, 5.0f, 7.0f, dilation), ModelTransform.NONE);
        modelPartData3.addChild("mane", ModelPartBuilder.create().uv(56, 36).cuboid(-1.0f, -11.0f, 5.01f, 2.0f, 16.0f, 2.0f, dilation), ModelTransform.NONE);
        modelPartData3.addChild("upper_mouth", ModelPartBuilder.create().uv(0, 25).cuboid(-2.0f, -11.0f, -7.0f, 4.0f, 5.0f, 5.0f, dilation), ModelTransform.NONE);
        modelPartData.addChild("left_hind_leg", ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, dilation), ModelTransform.pivot(4.0f, 14.0f, 7.0f));
        modelPartData.addChild("right_hind_leg", ModelPartBuilder.create().uv(48, 21).cuboid(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, dilation), ModelTransform.pivot(-4.0f, 14.0f, 7.0f));
        modelPartData.addChild("left_front_leg", ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, dilation), ModelTransform.pivot(4.0f, 14.0f, -12.0f));
        modelPartData.addChild("right_front_leg", ModelPartBuilder.create().uv(48, 21).cuboid(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, dilation), ModelTransform.pivot(-4.0f, 14.0f, -12.0f));
        Dilation dilation2 = dilation.add(0.0f, 5.5f, 0.0f);
        modelPartData.addChild("left_hind_baby_leg", ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, dilation2), ModelTransform.pivot(4.0f, 14.0f, 7.0f));
        modelPartData.addChild("right_hind_baby_leg", ModelPartBuilder.create().uv(48, 21).cuboid(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, dilation2), ModelTransform.pivot(-4.0f, 14.0f, 7.0f));
        modelPartData.addChild("left_front_baby_leg", ModelPartBuilder.create().uv(48, 21).mirrored().cuboid(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, dilation2), ModelTransform.pivot(4.0f, 14.0f, -12.0f));
        modelPartData.addChild("right_front_baby_leg", ModelPartBuilder.create().uv(48, 21).cuboid(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, dilation2), ModelTransform.pivot(-4.0f, 14.0f, -12.0f));
        modelPartData2.addChild("tail", ModelPartBuilder.create().uv(42, 36).cuboid(-1.5f, 0.0f, 0.0f, 3.0f, 14.0f, 4.0f, dilation), ModelTransform.of(0.0f, -5.0f, 2.0f, 0.5235988f, 0.0f, 0.0f));
        modelPartData2.addChild("saddle", ModelPartBuilder.create().uv(26, 0).cuboid(-5.0f, -8.0f, -9.0f, 10.0f, 9.0f, 9.0f, new Dilation(0.5f)), ModelTransform.NONE);
        modelPartData3.addChild("left_saddle_mouth", ModelPartBuilder.create().uv(29, 5).cuboid(2.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f, dilation), ModelTransform.NONE);
        modelPartData3.addChild("right_saddle_mouth", ModelPartBuilder.create().uv(29, 5).cuboid(-3.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f, dilation), ModelTransform.NONE);
        modelPartData3.addChild("left_saddle_line", ModelPartBuilder.create().uv(32, 2).cuboid(3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f, dilation), ModelTransform.rotation(-0.5235988f, 0.0f, 0.0f));
        modelPartData3.addChild("right_saddle_line", ModelPartBuilder.create().uv(32, 2).cuboid(-3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f, dilation), ModelTransform.rotation(-0.5235988f, 0.0f, 0.0f));
        modelPartData3.addChild("head_saddle", ModelPartBuilder.create().uv(1, 1).cuboid(-3.0f, -11.0f, -1.9f, 6.0f, 5.0f, 6.0f, new Dilation(0.2f)), ModelTransform.NONE);
        modelPartData3.addChild("mouth_saddle_wrap", ModelPartBuilder.create().uv(19, 0).cuboid(-2.0f, -11.0f, -4.0f, 4.0f, 5.0f, 2.0f, new Dilation(0.2f)), ModelTransform.NONE);
        modelPartData4.addChild("left_ear", ModelPartBuilder.create().uv(19, 16).cuboid(0.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, new Dilation(-0.001f)), ModelTransform.NONE);
        modelPartData4.addChild("right_ear", ModelPartBuilder.create().uv(19, 16).cuboid(-2.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, new Dilation(-0.001f)), ModelTransform.NONE);
        return modelData;
    }

    @Override
    public void setAngles(T horseBaseEntity, float f, float g, float h, float i, float j) {
        boolean bl = ((HorseBaseEntity)horseBaseEntity).isSaddled();
        boolean bl2 = ((Entity)horseBaseEntity).hasPassengers();
        for (ModelPart modelPart : this.saddle) {
            modelPart.visible = bl;
        }
        for (ModelPart modelPart : this.straps) {
            modelPart.visible = bl2 && bl;
        }
        this.body.pivotY = 11.0f;
    }

    @Override
    public Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightHindBabyLeg, this.leftHindBabyLeg, this.rightFrontBabyLeg, this.leftFrontBabyLeg);
    }

    @Override
    public void animateModel(T horseBaseEntity, float f, float g, float h) {
        super.animateModel(horseBaseEntity, f, g, h);
        float i = MathHelper.lerpAngle(((HorseBaseEntity)horseBaseEntity).prevBodyYaw, ((HorseBaseEntity)horseBaseEntity).bodyYaw, h);
        float j = MathHelper.lerpAngle(((HorseBaseEntity)horseBaseEntity).prevHeadYaw, ((HorseBaseEntity)horseBaseEntity).headYaw, h);
        float k = MathHelper.lerp(h, ((HorseBaseEntity)horseBaseEntity).prevPitch, ((HorseBaseEntity)horseBaseEntity).pitch);
        float l = j - i;
        float m = k * ((float)Math.PI / 180);
        if (l > 20.0f) {
            l = 20.0f;
        }
        if (l < -20.0f) {
            l = -20.0f;
        }
        if (g > 0.2f) {
            m += MathHelper.cos(f * 0.4f) * 0.15f * g;
        }
        float n = ((HorseBaseEntity)horseBaseEntity).getEatingGrassAnimationProgress(h);
        float o = ((HorseBaseEntity)horseBaseEntity).getAngryAnimationProgress(h);
        float p = 1.0f - o;
        float q = ((HorseBaseEntity)horseBaseEntity).getEatingAnimationProgress(h);
        boolean bl = ((HorseBaseEntity)horseBaseEntity).tailWagTicks != 0;
        float r = (float)((HorseBaseEntity)horseBaseEntity).age + h;
        this.head.pivotY = 4.0f;
        this.head.pivotZ = -12.0f;
        this.body.pitch = 0.0f;
        this.head.pitch = 0.5235988f + m;
        this.head.yaw = l * ((float)Math.PI / 180);
        float s = ((Entity)horseBaseEntity).isTouchingWater() ? 0.2f : 1.0f;
        float t = MathHelper.cos(s * f * 0.6662f + (float)Math.PI);
        float u = t * 0.8f * g;
        float v = (1.0f - Math.max(o, n)) * (0.5235988f + m + q * MathHelper.sin(r) * 0.05f);
        this.head.pitch = o * (0.2617994f + m) + n * (2.1816616f + MathHelper.sin(r) * 0.05f) + v;
        this.head.yaw = o * l * ((float)Math.PI / 180) + (1.0f - Math.max(o, n)) * this.head.yaw;
        this.head.pivotY = o * -4.0f + n * 11.0f + (1.0f - Math.max(o, n)) * this.head.pivotY;
        this.head.pivotZ = o * -4.0f + n * -12.0f + (1.0f - Math.max(o, n)) * this.head.pivotZ;
        this.body.pitch = o * -0.7853982f + p * this.body.pitch;
        float w = 0.2617994f * o;
        float x = MathHelper.cos(r * 0.6f + (float)Math.PI);
        this.leftFrontLeg.pivotY = 2.0f * o + 14.0f * p;
        this.leftFrontLeg.pivotZ = -6.0f * o - 10.0f * p;
        this.rightFrontLeg.pivotY = this.leftFrontLeg.pivotY;
        this.rightFrontLeg.pivotZ = this.leftFrontLeg.pivotZ;
        float y = (-1.0471976f + x) * o + u * p;
        float z = (-1.0471976f - x) * o - u * p;
        this.leftHindLeg.pitch = w - t * 0.5f * g * p;
        this.rightHindLeg.pitch = w + t * 0.5f * g * p;
        this.leftFrontLeg.pitch = y;
        this.rightFrontLeg.pitch = z;
        this.tail.pitch = 0.5235988f + g * 0.75f;
        this.tail.pivotY = -5.0f + g;
        this.tail.pivotZ = 2.0f + g * 2.0f;
        this.tail.yaw = bl ? MathHelper.cos(r * 0.7f) : 0.0f;
        this.rightHindBabyLeg.pivotY = this.rightHindLeg.pivotY;
        this.rightHindBabyLeg.pivotZ = this.rightHindLeg.pivotZ;
        this.rightHindBabyLeg.pitch = this.rightHindLeg.pitch;
        this.leftHindBabyLeg.pivotY = this.leftHindLeg.pivotY;
        this.leftHindBabyLeg.pivotZ = this.leftHindLeg.pivotZ;
        this.leftHindBabyLeg.pitch = this.leftHindLeg.pitch;
        this.rightFrontBabyLeg.pivotY = this.rightFrontLeg.pivotY;
        this.rightFrontBabyLeg.pivotZ = this.rightFrontLeg.pivotZ;
        this.rightFrontBabyLeg.pitch = this.rightFrontLeg.pitch;
        this.leftFrontBabyLeg.pivotY = this.leftFrontLeg.pivotY;
        this.leftFrontBabyLeg.pivotZ = this.leftFrontLeg.pivotZ;
        this.leftFrontBabyLeg.pitch = this.leftFrontLeg.pitch;
        boolean bl2 = ((PassiveEntity)horseBaseEntity).isBaby();
        this.rightHindLeg.visible = !bl2;
        this.leftHindLeg.visible = !bl2;
        this.rightFrontLeg.visible = !bl2;
        this.leftFrontLeg.visible = !bl2;
        this.rightHindBabyLeg.visible = bl2;
        this.leftHindBabyLeg.visible = bl2;
        this.rightFrontBabyLeg.visible = bl2;
        this.leftFrontBabyLeg.visible = bl2;
        this.body.pivotY = bl2 ? 10.8f : 0.0f;
    }
}

