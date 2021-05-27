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
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a hoglin-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>Root part</td><td>{@link #body}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#MANE}</td><td>{@value EntityModelPartNames#BODY}</td><td>{@link #mane}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>Root part</td><td>{@link #head}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_EAR}</td><td>{@value EntityModelPartNames#HEAD}</td><td>{@link #rightEar}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_EAR}</td><td>{@value EntityModelPartNames#HEAD}</td><td>{@link #leftEar}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_HORN}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_HORN}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_FRONT_LEG}</td><td>Root part</td><td>{@link #rightFrontLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_FRONT_LEG}</td><td>Root part</td><td>{@link #leftFrontLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_HIND_LEG}</td><td>Root part</td><td>{@link #rightHindLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_HIND_LEG}</td><td>Root part</td><td>{@link #leftHindLeg}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(value=EnvType.CLIENT)
public class HoglinEntityModel<T extends MobEntity>
extends AnimalModel<T> {
    private static final float field_32484 = 0.87266463f;
    private static final float field_32485 = -0.34906584f;
    private final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart body;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart mane;

    public HoglinEntityModel(ModelPart root) {
        super(true, 8.0f, 6.0f, 1.9f, 2.0f, 24.0f);
        this.body = root.getChild(EntityModelPartNames.BODY);
        this.mane = this.body.getChild(EntityModelPartNames.MANE);
        this.head = root.getChild(EntityModelPartNames.HEAD);
        this.rightEar = this.head.getChild(EntityModelPartNames.RIGHT_EAR);
        this.leftEar = this.head.getChild(EntityModelPartNames.LEFT_EAR);
        this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(1, 1).cuboid(-8.0f, -7.0f, -13.0f, 16.0f, 14.0f, 26.0f), ModelTransform.pivot(0.0f, 7.0f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.MANE, ModelPartBuilder.create().uv(90, 33).cuboid(0.0f, 0.0f, -9.0f, 0.0f, 10.0f, 19.0f, new Dilation(0.001f)), ModelTransform.pivot(0.0f, -14.0f, -5.0f));
        ModelPartData modelPartData3 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(61, 1).cuboid(-7.0f, -3.0f, -19.0f, 14.0f, 6.0f, 19.0f), ModelTransform.of(0.0f, 2.0f, -12.0f, 0.87266463f, 0.0f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(1, 1).cuboid(-6.0f, -1.0f, -2.0f, 6.0f, 1.0f, 4.0f), ModelTransform.of(-6.0f, -2.0f, -3.0f, 0.0f, 0.0f, -0.6981317f));
        modelPartData3.addChild(EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(1, 6).cuboid(0.0f, -1.0f, -2.0f, 6.0f, 1.0f, 4.0f), ModelTransform.of(6.0f, -2.0f, -3.0f, 0.0f, 0.0f, 0.6981317f));
        modelPartData3.addChild(EntityModelPartNames.RIGHT_HORN, ModelPartBuilder.create().uv(10, 13).cuboid(-1.0f, -11.0f, -1.0f, 2.0f, 11.0f, 2.0f), ModelTransform.pivot(-7.0f, 2.0f, -12.0f));
        modelPartData3.addChild(EntityModelPartNames.LEFT_HORN, ModelPartBuilder.create().uv(1, 13).cuboid(-1.0f, -11.0f, -1.0f, 2.0f, 11.0f, 2.0f), ModelTransform.pivot(7.0f, 2.0f, -12.0f));
        int i = 14;
        int j = 11;
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, ModelPartBuilder.create().uv(66, 42).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 14.0f, 6.0f), ModelTransform.pivot(-4.0f, 10.0f, -8.5f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, ModelPartBuilder.create().uv(41, 42).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 14.0f, 6.0f), ModelTransform.pivot(4.0f, 10.0f, -8.5f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, ModelPartBuilder.create().uv(21, 45).cuboid(-2.5f, 0.0f, -2.5f, 5.0f, 11.0f, 5.0f), ModelTransform.pivot(-5.0f, 13.0f, 10.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, ModelPartBuilder.create().uv(0, 45).cuboid(-2.5f, 0.0f, -2.5f, 5.0f, 11.0f, 5.0f), ModelTransform.pivot(5.0f, 13.0f, 10.0f));
        return TexturedModelData.of(modelData, 128, 64);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body, this.rightFrontLeg, this.leftFrontLeg, this.rightHindLeg, this.leftHindLeg);
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
            this.mane.pivotZ = -3.0f;
        } else {
            this.head.pivotY = 2.0f;
            this.mane.pivotZ = -7.0f;
        }
        float m = 1.2f;
        this.rightFrontLeg.pitch = MathHelper.cos(f) * 1.2f * g;
        this.rightHindLeg.pitch = this.leftFrontLeg.pitch = MathHelper.cos(f + (float)Math.PI) * 1.2f * g;
        this.leftHindLeg.pitch = this.rightFrontLeg.pitch;
    }
}

