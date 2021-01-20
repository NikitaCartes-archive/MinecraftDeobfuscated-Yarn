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
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AxolotlEntityModel<T extends AxolotlEntity>
extends AnimalModel<T> {
    private final ModelPart tail;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart topGills;
    private final ModelPart leftGills;
    private final ModelPart rightGills;

    public AxolotlEntityModel(ModelPart root) {
        super(true, 8.0f, 3.35f);
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.rightHindLeg = this.body.getChild("right_hind_leg");
        this.leftHindLeg = this.body.getChild("left_hind_leg");
        this.rightFrontLeg = this.body.getChild("right_front_leg");
        this.leftFrontLeg = this.body.getChild("left_front_leg");
        this.tail = this.body.getChild("tail");
        this.topGills = this.head.getChild("top_gills");
        this.leftGills = this.head.getChild("left_gills");
        this.rightGills = this.head.getChild("right_gills");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 11).cuboid(-4.0f, -2.0f, -9.0f, 8.0f, 4.0f, 10.0f).uv(2, 17).cuboid(0.0f, -3.0f, -8.0f, 0.0f, 5.0f, 9.0f), ModelTransform.pivot(0.0f, 20.0f, 5.0f));
        Dilation dilation = new Dilation(0.001f);
        ModelPartData modelPartData3 = modelPartData2.addChild("head", ModelPartBuilder.create().uv(0, 1).cuboid(-4.0f, -3.0f, -5.0f, 8.0f, 5.0f, 5.0f, dilation), ModelTransform.pivot(0.0f, 0.0f, -9.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(3, 37).cuboid(-4.0f, -3.0f, 0.0f, 8.0f, 3.0f, 0.0f, dilation);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(0, 40).cuboid(-3.0f, -5.0f, 0.0f, 3.0f, 7.0f, 0.0f, dilation);
        ModelPartBuilder modelPartBuilder3 = ModelPartBuilder.create().uv(11, 40).cuboid(0.0f, -5.0f, 0.0f, 3.0f, 7.0f, 0.0f, dilation);
        modelPartData3.addChild("top_gills", modelPartBuilder, ModelTransform.pivot(0.0f, -3.0f, -1.0f));
        modelPartData3.addChild("left_gills", modelPartBuilder2, ModelTransform.pivot(-4.0f, 0.0f, -1.0f));
        modelPartData3.addChild("right_gills", modelPartBuilder3, ModelTransform.pivot(4.0f, 0.0f, -1.0f));
        ModelPartBuilder modelPartBuilder4 = ModelPartBuilder.create().uv(2, 13).cuboid(-1.0f, 0.0f, 0.0f, 3.0f, 5.0f, 0.0f, dilation);
        ModelPartBuilder modelPartBuilder5 = ModelPartBuilder.create().uv(2, 13).cuboid(-2.0f, 0.0f, 0.0f, 3.0f, 5.0f, 0.0f, dilation);
        modelPartData2.addChild("right_hind_leg", modelPartBuilder5, ModelTransform.pivot(-3.5f, 1.0f, -1.0f));
        modelPartData2.addChild("left_hind_leg", modelPartBuilder4, ModelTransform.pivot(3.5f, 1.0f, -1.0f));
        modelPartData2.addChild("right_front_leg", modelPartBuilder5, ModelTransform.pivot(-3.5f, 1.0f, -8.0f));
        modelPartData2.addChild("left_front_leg", modelPartBuilder4, ModelTransform.pivot(3.5f, 1.0f, -8.0f));
        modelPartData2.addChild("tail", ModelPartBuilder.create().uv(2, 19).cuboid(0.0f, -3.0f, 0.0f, 0.0f, 5.0f, 12.0f), ModelTransform.pivot(0.0f, 0.0f, 1.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void setAngles(T axolotlEntity, float f, float g, float h, float i, float j) {
        boolean bl;
        this.method_33292(i, j);
        if (((AxolotlEntity)axolotlEntity).isPlayingDead()) {
            this.method_33298();
            return;
        }
        boolean bl2 = bl = Entity.squaredHorizontalLength(((Entity)axolotlEntity).getVelocity()) > 1.0E-7;
        if (((Entity)axolotlEntity).isInsideWaterOrBubbleColumn()) {
            if (bl) {
                this.method_33295(h, j);
            } else {
                this.method_33297(h);
            }
            return;
        }
        if (((Entity)axolotlEntity).isOnGround()) {
            if (bl) {
                this.method_33294(h);
            } else {
                this.method_33291(h);
            }
        }
    }

    private void method_33292(float f, float g) {
        this.body.pivotX = 0.0f;
        this.head.pivotY = 0.0f;
        this.body.pivotY = 20.0f;
        this.body.method_33425(g * ((float)Math.PI / 180), f * ((float)Math.PI / 180), 0.0f);
        this.head.method_33425(0.0f, 0.0f, 0.0f);
        this.leftHindLeg.method_33425(0.0f, 0.0f, 0.0f);
        this.rightHindLeg.method_33425(0.0f, 0.0f, 0.0f);
        this.leftFrontLeg.method_33425(0.0f, 0.0f, 0.0f);
        this.rightFrontLeg.method_33425(0.0f, 0.0f, 0.0f);
        this.leftGills.method_33425(0.0f, 0.0f, 0.0f);
        this.rightGills.method_33425(0.0f, 0.0f, 0.0f);
        this.topGills.method_33425(0.0f, 0.0f, 0.0f);
        this.tail.method_33425(0.0f, 0.0f, 0.0f);
    }

    private void method_33291(float f) {
        float g = f * 0.09f;
        float h = MathHelper.sin(g);
        float i = MathHelper.cos(g);
        float j = h * h - 2.0f * h;
        float k = i * i - 3.0f * h;
        this.head.pitch = -0.09f * j;
        this.head.roll = -0.2f;
        this.tail.yaw = -0.1f + 0.1f * j;
        this.topGills.pitch = 0.6f + 0.05f * k;
        this.leftGills.yaw = -this.topGills.pitch;
        this.rightGills.yaw = -this.leftGills.yaw;
        this.leftHindLeg.method_33425(1.1f, 1.0f, 0.0f);
        this.leftFrontLeg.method_33425(0.8f, 2.3f, -0.5f);
        this.method_33299();
    }

    private void method_33294(float f) {
        float g = f * 0.11f;
        float h = MathHelper.cos(g);
        float i = (h * h - 2.0f * h) / 5.0f;
        float j = 0.7f * h;
        this.tail.yaw = this.head.yaw = 0.09f * h;
        this.topGills.pitch = 0.6f - 0.08f * (h * h + 2.0f * MathHelper.sin(g));
        this.leftGills.yaw = -this.topGills.pitch;
        this.rightGills.yaw = -this.leftGills.yaw;
        this.leftHindLeg.method_33425(0.9424779f, 1.5f - i, -0.1f);
        this.leftFrontLeg.method_33425(1.0995574f, 1.5707964f - j, 0.0f);
        this.rightHindLeg.method_33425(this.leftHindLeg.pitch, -1.0f - i, 0.0f);
        this.rightFrontLeg.method_33425(this.leftFrontLeg.pitch, -1.5707964f - j, 0.0f);
    }

    private void method_33297(float f) {
        float g = f * 0.075f;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g) * 0.15f;
        this.body.pitch = -0.15f + 0.075f * h;
        this.body.pivotY -= i;
        this.head.pitch = -this.body.pitch;
        this.topGills.pitch = 0.2f * h;
        this.leftGills.yaw = -0.3f * h - 0.19f;
        this.rightGills.yaw = -this.leftGills.yaw;
        this.leftHindLeg.method_33425(2.3561945f - h * 0.11f, 0.47123894f, 1.7278761f);
        this.leftFrontLeg.method_33425(0.7853982f - h * 0.2f, 2.042035f, 0.0f);
        this.method_33299();
        this.tail.yaw = 0.5f * h;
    }

    private void method_33295(float f, float g) {
        float h = f * 0.33f;
        float i = MathHelper.sin(h);
        float j = MathHelper.cos(h);
        float k = 0.13f * i;
        this.body.pitch = g * ((float)Math.PI / 180) + k;
        this.head.pitch = -k * 1.8f;
        this.body.pivotY -= 0.45f * j;
        this.topGills.pitch = -0.5f * i - 0.8f;
        this.leftGills.yaw = 0.3f * i + 0.9f;
        this.rightGills.yaw = -this.leftGills.yaw;
        this.tail.yaw = 0.3f * MathHelper.cos(h * 0.9f);
        this.leftHindLeg.method_33425(1.8849558f, -0.4f * i, 1.5707964f);
        this.leftFrontLeg.method_33425(1.8849558f, -0.2f * j - 0.1f, 1.5707964f);
        this.method_33299();
    }

    private void method_33298() {
        this.leftHindLeg.method_33425(1.4137167f, 1.0995574f, 0.7853982f);
        this.leftFrontLeg.method_33425(0.7853982f, 2.042035f, 0.0f);
        this.body.pitch = -0.15f;
        this.body.roll = 0.35f;
        this.method_33299();
    }

    private void method_33299() {
        this.rightHindLeg.method_33425(this.leftHindLeg.pitch, -this.leftHindLeg.yaw, -this.leftHindLeg.roll);
        this.rightFrontLeg.method_33425(this.leftFrontLeg.pitch, -this.leftFrontLeg.yaw, -this.leftFrontLeg.roll);
    }
}

