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
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PhantomEntityModel<T extends PhantomEntity>
extends SinglePartEntityModel<T> {
    private static final String TAIL_BASE = "tail_base";
    private static final String TAIL_TIP = "tail_tip";
    private final ModelPart root;
    private final ModelPart leftWingBase;
    private final ModelPart leftWingTip;
    private final ModelPart rightWingBase;
    private final ModelPart rightWingTip;
    private final ModelPart tailBase;
    private final ModelPart tailTip;

    public PhantomEntityModel(ModelPart root) {
        this.root = root;
        ModelPart modelPart = root.getChild(EntityModelPartNames.BODY);
        this.tailBase = modelPart.getChild(TAIL_BASE);
        this.tailTip = this.tailBase.getChild(TAIL_TIP);
        this.leftWingBase = modelPart.getChild(EntityModelPartNames.LEFT_WING_BASE);
        this.leftWingTip = this.leftWingBase.getChild(EntityModelPartNames.LEFT_WING_TIP);
        this.rightWingBase = modelPart.getChild(EntityModelPartNames.RIGHT_WING_BASE);
        this.rightWingTip = this.rightWingBase.getChild(EntityModelPartNames.RIGHT_WING_TIP);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 8).cuboid(-3.0f, -2.0f, -8.0f, 5.0f, 3.0f, 9.0f), ModelTransform.rotation(-0.1f, 0.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild(TAIL_BASE, ModelPartBuilder.create().uv(3, 20).cuboid(-2.0f, 0.0f, 0.0f, 3.0f, 2.0f, 6.0f), ModelTransform.pivot(0.0f, -2.0f, 1.0f));
        modelPartData3.addChild(TAIL_TIP, ModelPartBuilder.create().uv(4, 29).cuboid(-1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 6.0f), ModelTransform.pivot(0.0f, 0.5f, 6.0f));
        ModelPartData modelPartData4 = modelPartData2.addChild(EntityModelPartNames.LEFT_WING_BASE, ModelPartBuilder.create().uv(23, 12).cuboid(0.0f, 0.0f, 0.0f, 6.0f, 2.0f, 9.0f), ModelTransform.of(2.0f, -2.0f, -8.0f, 0.0f, 0.0f, 0.1f));
        modelPartData4.addChild(EntityModelPartNames.LEFT_WING_TIP, ModelPartBuilder.create().uv(16, 24).cuboid(0.0f, 0.0f, 0.0f, 13.0f, 1.0f, 9.0f), ModelTransform.of(6.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.1f));
        ModelPartData modelPartData5 = modelPartData2.addChild(EntityModelPartNames.RIGHT_WING_BASE, ModelPartBuilder.create().uv(23, 12).mirrored().cuboid(-6.0f, 0.0f, 0.0f, 6.0f, 2.0f, 9.0f), ModelTransform.of(-3.0f, -2.0f, -8.0f, 0.0f, 0.0f, -0.1f));
        modelPartData5.addChild(EntityModelPartNames.RIGHT_WING_TIP, ModelPartBuilder.create().uv(16, 24).mirrored().cuboid(-13.0f, 0.0f, 0.0f, 13.0f, 1.0f, 9.0f), ModelTransform.of(-6.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.1f));
        modelPartData2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -2.0f, -5.0f, 7.0f, 3.0f, 5.0f), ModelTransform.of(0.0f, 1.0f, -7.0f, 0.2f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(T phantomEntity, float f, float g, float h, float i, float j) {
        float k = ((float)((PhantomEntity)phantomEntity).method_33588() + h) * 7.448451f * ((float)Math.PI / 180);
        float l = 16.0f;
        this.leftWingBase.roll = MathHelper.cos(k) * 16.0f * ((float)Math.PI / 180);
        this.leftWingTip.roll = MathHelper.cos(k) * 16.0f * ((float)Math.PI / 180);
        this.rightWingBase.roll = -this.leftWingBase.roll;
        this.rightWingTip.roll = -this.leftWingTip.roll;
        this.tailBase.pitch = -(5.0f + MathHelper.cos(k * 2.0f) * 5.0f) * ((float)Math.PI / 180);
        this.tailTip.pitch = -(5.0f + MathHelper.cos(k * 2.0f) * 5.0f) * ((float)Math.PI / 180);
    }
}

