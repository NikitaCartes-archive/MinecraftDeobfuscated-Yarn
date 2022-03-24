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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.WardenAnimations;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class WardenEntityModel<T extends WardenEntity>
extends SinglePartEntityModel<T> {
    private static final float field_38324 = 13.0f;
    private static final float field_38325 = 1.0f;
    private static final Vec3f field_38326 = new Vec3f();
    private final ModelPart root;
    protected final ModelPart bone;
    protected final ModelPart body;
    protected final ModelPart head;
    protected final ModelPart rightTendril;
    protected final ModelPart leftTendril;
    protected final ModelPart leftLeg;
    protected final ModelPart leftArm;
    protected final ModelPart rightArm;
    protected final ModelPart rightLeg;

    public WardenEntityModel(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCull);
        this.root = root;
        this.bone = root.getChild("bone");
        this.body = this.bone.getChild(EntityModelPartNames.BODY);
        this.head = this.body.getChild(EntityModelPartNames.HEAD);
        this.rightLeg = this.bone.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftLeg = this.bone.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightArm = this.body.getChild(EntityModelPartNames.RIGHT_ARM);
        this.leftArm = this.body.getChild(EntityModelPartNames.LEFT_ARM);
        this.rightTendril = this.head.getChild("right_tendril");
        this.leftTendril = this.head.getChild("left_tendril");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-9.0f, -13.0f, -4.0f, 18.0f, 21.0f, 11.0f), ModelTransform.pivot(0.0f, -21.0f, 0.0f));
        ModelPartData modelPartData4 = modelPartData3.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 32).cuboid(-8.0f, -16.0f, -5.0f, 16.0f, 16.0f, 10.0f), ModelTransform.pivot(0.0f, -13.0f, 0.0f));
        modelPartData4.addChild("right_tendril", ModelPartBuilder.create().uv(52, 32).cuboid(-16.0f, -13.0f, 0.0f, 16.0f, 16.0f, 0.0f), ModelTransform.pivot(-8.0f, -12.0f, 0.0f));
        modelPartData4.addChild("left_tendril", ModelPartBuilder.create().uv(58, 0).cuboid(0.0f, -13.0f, 0.0f, 16.0f, 16.0f, 0.0f), ModelTransform.pivot(8.0f, -12.0f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(44, 50).cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 28.0f, 8.0f), ModelTransform.pivot(-13.0f, -13.0f, 1.0f));
        modelPartData3.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(0, 58).cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 28.0f, 8.0f), ModelTransform.pivot(13.0f, -13.0f, 1.0f));
        modelPartData2.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(76, 48).cuboid(-3.1f, 0.0f, -3.0f, 6.0f, 13.0f, 6.0f), ModelTransform.pivot(-5.9f, -13.0f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(76, 76).cuboid(-2.9f, 0.0f, -3.0f, 6.0f, 13.0f, 6.0f), ModelTransform.pivot(5.9f, -13.0f, 0.0f));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(T wardenEntity, float f, float g, float h, float i, float j) {
        float t;
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        float k = h - (float)((WardenEntity)wardenEntity).age;
        float l = Math.min(0.5f, 3.0f * g);
        float m = h * 0.1f;
        float n = f * 0.8662f;
        float o = MathHelper.cos(n);
        float p = MathHelper.sin(n);
        float q = MathHelper.cos(m);
        float r = MathHelper.sin(m);
        float s = Math.min(0.35f, l);
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.roll += 0.3f * p * l;
        this.head.roll += 0.06f * q;
        this.head.pitch += 1.2f * MathHelper.cos(n + 1.5707964f) * s;
        this.head.pitch += 0.06f * r;
        this.body.roll = 0.1f * p * l;
        this.body.roll += 0.025f * r;
        this.body.pitch = 1.0f * o * s;
        this.body.pitch += 0.025f * q;
        this.leftLeg.pitch = 1.0f * o * l;
        this.rightLeg.pitch = 1.0f * MathHelper.cos(n + (float)Math.PI) * l;
        this.leftArm.pitch = -(0.8f * o * l);
        this.leftArm.roll = 0.0f;
        this.rightArm.pitch = -(0.8f * p * l);
        this.rightArm.roll = 0.0f;
        this.leftArm.yaw = 0.0f;
        this.leftArm.pivotZ = 1.0f;
        this.leftArm.pivotX = 13.0f;
        this.leftArm.pivotY = -13.0f;
        this.rightArm.yaw = 0.0f;
        this.rightArm.pivotZ = 1.0f;
        this.rightArm.pivotX = -13.0f;
        this.rightArm.pivotY = -13.0f;
        this.leftTendril.pitch = t = ((WardenEntity)wardenEntity).getEarPitch(k) * (float)(Math.cos((double)h * 2.25) * Math.PI * (double)0.1f);
        this.rightTendril.pitch = -t;
        long u = Util.getMeasuringTimeMs();
        this.runAnimation(((WardenEntity)wardenEntity).attackingAnimationState, WardenAnimations.ATTACKING, u);
        this.runAnimation(((WardenEntity)wardenEntity).diggingAnimationState, WardenAnimations.DIGGING, u);
        this.runAnimation(((WardenEntity)wardenEntity).emergingAnimationState, WardenAnimations.EMERGING, u);
        this.runAnimation(((WardenEntity)wardenEntity).roaringAnimationState, WardenAnimations.ROARING, u);
        this.runAnimation(((WardenEntity)wardenEntity).sniffingAnimationState, WardenAnimations.SNIFFING, u);
    }

    public void runAnimation(AnimationState animationState, Animation animation, long time) {
        animationState.run(state -> AnimationHelper.animate(this, animation, time - state.getStartTime(), 1.0f, field_38326));
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}

