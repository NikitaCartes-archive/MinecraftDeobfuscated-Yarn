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
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class VillagerResemblingModel<T extends Entity>
extends SinglePartEntityModel<T>
implements ModelWithHead,
ModelWithHat {
    private final ModelPart field_27526;
    private final ModelPart field_27527;
    private final ModelPart field_27528;
    private final ModelPart field_27529;
    private final ModelPart field_27530;
    private final ModelPart field_27531;
    protected final ModelPart field_27525;

    public VillagerResemblingModel(ModelPart modelPart) {
        this.field_27526 = modelPart;
        this.field_27527 = modelPart.getChild("head");
        this.field_27528 = this.field_27527.getChild("hat");
        this.field_27529 = this.field_27528.getChild("hat_rim");
        this.field_27525 = this.field_27527.getChild("nose");
        this.field_27530 = modelPart.getChild("right_leg");
        this.field_27531 = modelPart.getChild("left_leg");
    }

    public static ModelData getModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 0.5f;
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f), ModelTransform.NONE);
        ModelPartData modelPartData3 = modelPartData2.addChild("hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, new Dilation(0.5f)), ModelTransform.NONE);
        modelPartData3.addChild("hat_rim", ModelPartBuilder.create().uv(30, 47).cuboid(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f), ModelTransform.rotation(-1.5707964f, 0.0f, 0.0f));
        modelPartData2.addChild("nose", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f), ModelTransform.pivot(0.0f, -2.0f, 0.0f));
        ModelPartData modelPartData4 = modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 20).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f), ModelTransform.NONE);
        modelPartData4.addChild("jacket", ModelPartBuilder.create().uv(0, 38).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, new Dilation(0.5f)), ModelTransform.NONE);
        modelPartData.addChild("arms", ModelPartBuilder.create().uv(44, 22).cuboid(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f).uv(44, 22).cuboid(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, true).uv(40, 38).cuboid(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f), ModelTransform.of(0.0f, 3.0f, -1.0f, -0.75f, 0.0f, 0.0f));
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 22).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.pivot(-2.0f, 12.0f, 0.0f));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.pivot(2.0f, 12.0f, 0.0f));
        return modelData;
    }

    @Override
    public ModelPart getPart() {
        return this.field_27526;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        boolean bl = false;
        if (entity instanceof MerchantEntity) {
            bl = ((MerchantEntity)entity).getHeadRollingTimeLeft() > 0;
        }
        this.field_27527.yaw = headYaw * ((float)Math.PI / 180);
        this.field_27527.pitch = headPitch * ((float)Math.PI / 180);
        if (bl) {
            this.field_27527.roll = 0.3f * MathHelper.sin(0.45f * animationProgress);
            this.field_27527.pitch = 0.4f;
        } else {
            this.field_27527.roll = 0.0f;
        }
        this.field_27530.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance * 0.5f;
        this.field_27531.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance * 0.5f;
        this.field_27530.yaw = 0.0f;
        this.field_27531.yaw = 0.0f;
    }

    @Override
    public ModelPart getHead() {
        return this.field_27527;
    }

    @Override
    public void setHatVisible(boolean visible) {
        this.field_27527.visible = visible;
        this.field_27528.visible = visible;
        this.field_27529.visible = visible;
    }
}

