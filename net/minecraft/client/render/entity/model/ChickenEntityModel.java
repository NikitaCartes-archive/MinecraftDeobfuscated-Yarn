/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ChickenEntityModel<T extends Entity>
extends AnimalModel<T> {
    public static final String RED_THING = "red_thing";
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart beak;
    private final ModelPart wattle;

    public ChickenEntityModel(ModelPart root) {
        this.head = root.getChild(EntityModelPartNames.HEAD);
        this.beak = root.getChild(EntityModelPartNames.BEAK);
        this.wattle = root.getChild(RED_THING);
        this.body = root.getChild(EntityModelPartNames.BODY);
        this.rightLeg = root.getChild(EntityModelPartNames.RIGHT_LEG);
        this.leftLeg = root.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightWing = root.getChild(EntityModelPartNames.RIGHT_WING);
        this.leftWing = root.getChild(EntityModelPartNames.LEFT_WING);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 16;
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 3.0f), ModelTransform.pivot(0.0f, 15.0f, -4.0f));
        modelPartData.addChild(EntityModelPartNames.BEAK, ModelPartBuilder.create().uv(14, 0).cuboid(-2.0f, -4.0f, -4.0f, 4.0f, 2.0f, 2.0f), ModelTransform.pivot(0.0f, 15.0f, -4.0f));
        modelPartData.addChild(RED_THING, ModelPartBuilder.create().uv(14, 4).cuboid(-1.0f, -2.0f, -3.0f, 2.0f, 2.0f, 2.0f), ModelTransform.pivot(0.0f, 15.0f, -4.0f));
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 9).cuboid(-3.0f, -4.0f, -3.0f, 6.0f, 8.0f, 6.0f), ModelTransform.of(0.0f, 16.0f, 0.0f, 1.5707964f, 0.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(26, 0).cuboid(-1.0f, 0.0f, -3.0f, 3.0f, 5.0f, 3.0f);
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, modelPartBuilder, ModelTransform.pivot(-2.0f, 19.0f, 1.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, modelPartBuilder, ModelTransform.pivot(1.0f, 19.0f, 1.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(24, 13).cuboid(0.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), ModelTransform.pivot(-4.0f, 13.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(24, 13).cuboid(-1.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), ModelTransform.pivot(4.0f, 13.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head, this.beak, this.wattle);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body, this.rightLeg, this.leftLeg, this.rightWing, this.leftWing);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.beak.pitch = this.head.pitch;
        this.beak.yaw = this.head.yaw;
        this.wattle.pitch = this.head.pitch;
        this.wattle.yaw = this.head.yaw;
        this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.rightWing.roll = animationProgress;
        this.leftWing.roll = -animationProgress;
    }
}

