/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
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
    protected final ModelPart field_38449;
    protected final ModelPart rightArm;
    protected final ModelPart rightLeg;
    protected final ModelPart field_38450;
    private final List<ModelPart> field_38451;
    private final List<ModelPart> field_38452;
    private final List<ModelPart> field_38453;
    private final List<ModelPart> field_38454;

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
        this.field_38450 = this.body.getChild("right_ribcage");
        this.field_38449 = this.body.getChild("left_ribcage");
        this.field_38451 = ImmutableList.of(this.leftTendril, this.rightTendril);
        this.field_38452 = ImmutableList.of(this.body);
        this.field_38453 = ImmutableList.of(this.head, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg);
        this.field_38454 = ImmutableList.of(this.body, this.head, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-9.0f, -13.0f, -4.0f, 18.0f, 21.0f, 11.0f), ModelTransform.pivot(0.0f, -21.0f, 0.0f));
        modelPartData3.addChild("right_ribcage", ModelPartBuilder.create().uv(79, 11).cuboid(-2.0f, -11.0f, -0.1f, 9.0f, 21.0f, 0.0f), ModelTransform.pivot(-7.0f, -2.0f, -4.0f));
        modelPartData3.addChild("left_ribcage", ModelPartBuilder.create().uv(79, 11).mirrored().cuboid(-7.0f, -11.0f, -0.1f, 9.0f, 21.0f, 0.0f).mirrored(false), ModelTransform.pivot(7.0f, -2.0f, -4.0f));
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
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        float k = h - (float)((WardenEntity)wardenEntity).age;
        long l = Util.getMeasuringTimeMs();
        this.method_42735(i, j);
        this.method_42737(f, g);
        this.method_42734(h);
        this.method_42736(wardenEntity, h, k);
        this.runAnimation(((WardenEntity)wardenEntity).attackingAnimationState, WardenAnimations.ATTACKING, l);
        this.runAnimation(((WardenEntity)wardenEntity).diggingAnimationState, WardenAnimations.DIGGING, l);
        this.runAnimation(((WardenEntity)wardenEntity).emergingAnimationState, WardenAnimations.EMERGING, l);
        this.runAnimation(((WardenEntity)wardenEntity).roaringAnimationState, WardenAnimations.ROARING, l);
        this.runAnimation(((WardenEntity)wardenEntity).sniffingAnimationState, WardenAnimations.SNIFFING, l);
    }

    private void method_42735(float f, float g) {
        this.head.pitch = g * ((float)Math.PI / 180);
        this.head.yaw = f * ((float)Math.PI / 180);
    }

    private void method_42734(float f) {
        float g = f * 0.1f;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        this.head.roll += 0.06f * h;
        this.head.pitch += 0.06f * i;
        this.body.roll += 0.025f * i;
        this.body.pitch += 0.025f * h;
    }

    private void method_42737(float f, float g) {
        float h = Math.min(0.5f, 3.0f * g);
        float i = f * 0.8662f;
        float j = MathHelper.cos(i);
        float k = MathHelper.sin(i);
        float l = Math.min(0.35f, h);
        this.head.roll += 0.3f * k * h;
        this.head.pitch += 1.2f * MathHelper.cos(i + 1.5707964f) * l;
        this.body.roll = 0.1f * k * h;
        this.body.pitch = 1.0f * j * l;
        this.leftLeg.pitch = 1.0f * j * h;
        this.rightLeg.pitch = 1.0f * MathHelper.cos(i + (float)Math.PI) * h;
        this.leftArm.pitch = -(0.8f * j * h);
        this.leftArm.roll = 0.0f;
        this.rightArm.pitch = -(0.8f * k * h);
        this.rightArm.roll = 0.0f;
        this.method_42742();
    }

    private void method_42742() {
        this.leftArm.yaw = 0.0f;
        this.leftArm.pivotZ = 1.0f;
        this.leftArm.pivotX = 13.0f;
        this.leftArm.pivotY = -13.0f;
        this.rightArm.yaw = 0.0f;
        this.rightArm.pivotZ = 1.0f;
        this.rightArm.pivotX = -13.0f;
        this.rightArm.pivotY = -13.0f;
    }

    private void method_42736(T wardenEntity, float f, float g) {
        float h;
        this.leftTendril.pitch = h = ((WardenEntity)wardenEntity).getEarPitch(g) * (float)(Math.cos((double)f * 2.25) * Math.PI * (double)0.1f);
        this.rightTendril.pitch = -h;
    }

    public void runAnimation(AnimationState animationState, Animation animation, long time) {
        animationState.run(state -> AnimationHelper.animate(this, animation, time - state.getStartTime(), 1.0f, field_38326));
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    public List<ModelPart> method_42738() {
        return this.field_38451;
    }

    public List<ModelPart> method_42739() {
        return this.field_38452;
    }

    public List<ModelPart> method_42740() {
        return this.field_38453;
    }

    public List<ModelPart> method_42741() {
        return this.field_38454;
    }
}

