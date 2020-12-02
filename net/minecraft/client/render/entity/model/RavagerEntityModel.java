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
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RavagerEntityModel
extends SinglePartEntityModel<RavagerEntity> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart neck;

    public RavagerEntityModel(ModelPart root) {
        this.root = root;
        this.neck = root.getChild("neck");
        this.head = this.neck.getChild("head");
        this.jaw = this.head.getChild("mouth");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 16;
        ModelPartData modelPartData2 = modelPartData.addChild("neck", ModelPartBuilder.create().uv(68, 73).cuboid(-5.0f, -1.0f, -18.0f, 10.0f, 10.0f, 18.0f), ModelTransform.pivot(0.0f, -7.0f, 5.5f));
        ModelPartData modelPartData3 = modelPartData2.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -20.0f, -14.0f, 16.0f, 20.0f, 16.0f).uv(0, 0).cuboid(-2.0f, -6.0f, -18.0f, 4.0f, 8.0f, 4.0f), ModelTransform.pivot(0.0f, 16.0f, -17.0f));
        modelPartData3.addChild("right_horn", ModelPartBuilder.create().uv(74, 55).cuboid(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f), ModelTransform.of(-10.0f, -14.0f, -8.0f, 1.0995574f, 0.0f, 0.0f));
        modelPartData3.addChild("left_horn", ModelPartBuilder.create().uv(74, 55).mirrored().cuboid(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f), ModelTransform.of(8.0f, -14.0f, -8.0f, 1.0995574f, 0.0f, 0.0f));
        modelPartData3.addChild("mouth", ModelPartBuilder.create().uv(0, 36).cuboid(-8.0f, 0.0f, -16.0f, 16.0f, 3.0f, 16.0f), ModelTransform.pivot(0.0f, -2.0f, 2.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 55).cuboid(-7.0f, -10.0f, -7.0f, 14.0f, 16.0f, 20.0f).uv(0, 91).cuboid(-6.0f, 6.0f, -7.0f, 12.0f, 13.0f, 18.0f), ModelTransform.of(0.0f, 1.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        modelPartData.addChild("right_hind_leg", ModelPartBuilder.create().uv(96, 0).cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), ModelTransform.pivot(-8.0f, -13.0f, 18.0f));
        modelPartData.addChild("left_hind_leg", ModelPartBuilder.create().uv(96, 0).mirrored().cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), ModelTransform.pivot(8.0f, -13.0f, 18.0f));
        modelPartData.addChild("right_front_leg", ModelPartBuilder.create().uv(64, 0).cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), ModelTransform.pivot(-8.0f, -13.0f, -5.0f));
        modelPartData.addChild("left_front_leg", ModelPartBuilder.create().uv(64, 0).mirrored().cuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f), ModelTransform.pivot(8.0f, -13.0f, -5.0f));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(RavagerEntity ravagerEntity, float f, float g, float h, float i, float j) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        float k = 0.4f * g;
        this.rightHindLeg.pitch = MathHelper.cos(f * 0.6662f) * k;
        this.leftHindLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * k;
        this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * k;
        this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662f) * k;
    }

    @Override
    public void animateModel(RavagerEntity ravagerEntity, float f, float g, float h) {
        super.animateModel(ravagerEntity, f, g, h);
        int i = ravagerEntity.getStunTick();
        int j = ravagerEntity.getRoarTick();
        int k = 20;
        int l = ravagerEntity.getAttackTick();
        int m = 10;
        if (l > 0) {
            float n = MathHelper.wrap((float)l - h, 10.0f);
            float o = (1.0f + n) * 0.5f;
            float p = o * o * o * 12.0f;
            float q = p * MathHelper.sin(this.neck.pitch);
            this.neck.pivotZ = -6.5f + p;
            this.neck.pivotY = -7.0f - q;
            float r = MathHelper.sin(((float)l - h) / 10.0f * (float)Math.PI * 0.25f);
            this.jaw.pitch = 1.5707964f * r;
            this.jaw.pitch = l > 5 ? MathHelper.sin(((float)(-4 + l) - h) / 4.0f) * (float)Math.PI * 0.4f : 0.15707964f * MathHelper.sin((float)Math.PI * ((float)l - h) / 10.0f);
        } else {
            float n = -1.0f;
            float o = -1.0f * MathHelper.sin(this.neck.pitch);
            this.neck.pivotX = 0.0f;
            this.neck.pivotY = -7.0f - o;
            this.neck.pivotZ = 5.5f;
            boolean bl = i > 0;
            this.neck.pitch = bl ? 0.21991149f : 0.0f;
            this.jaw.pitch = (float)Math.PI * (bl ? 0.05f : 0.01f);
            if (bl) {
                double d = (double)i / 40.0;
                this.neck.pivotX = (float)Math.sin(d * 10.0) * 3.0f;
            } else if (j > 0) {
                float q = MathHelper.sin(((float)(20 - j) - h) / 20.0f * (float)Math.PI * 0.25f);
                this.jaw.pitch = 1.5707964f * q;
            }
        }
    }
}

