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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ShulkerEntityModel<T extends ShulkerEntity>
extends CompositeEntityModel<T> {
    private static final String LID = "lid";
    private static final String BASE = "base";
    private final ModelPart base;
    private final ModelPart lid;
    private final ModelPart head;

    public ShulkerEntityModel(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCullZOffset);
        this.lid = root.getChild(LID);
        this.base = root.getChild(BASE);
        this.head = root.getChild(EntityModelPartNames.HEAD);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(LID, ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -16.0f, -8.0f, 16.0f, 12.0f, 16.0f), ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        modelPartData.addChild(BASE, ModelPartBuilder.create().uv(0, 28).cuboid(-8.0f, -8.0f, -8.0f, 16.0f, 8.0f, 16.0f), ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 52).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 6.0f, 6.0f), ModelTransform.pivot(0.0f, 12.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(T shulkerEntity, float f, float g, float h, float i, float j) {
        float k = h - (float)((ShulkerEntity)shulkerEntity).age;
        float l = (0.5f + ((ShulkerEntity)shulkerEntity).getOpenProgress(k)) * (float)Math.PI;
        float m = -1.0f + MathHelper.sin(l);
        float n = 0.0f;
        if (l > (float)Math.PI) {
            n = MathHelper.sin(h * 0.1f) * 0.7f;
        }
        this.lid.setPivot(0.0f, 16.0f + MathHelper.sin(l) * 8.0f + n, 0.0f);
        this.lid.yaw = ((ShulkerEntity)shulkerEntity).getOpenProgress(k) > 0.3f ? m * m * m * m * (float)Math.PI * 0.125f : 0.0f;
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = (((ShulkerEntity)shulkerEntity).headYaw - 180.0f - ((ShulkerEntity)shulkerEntity).bodyYaw) * ((float)Math.PI / 180);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.base, this.lid);
    }

    public ModelPart getTopShell() {
        return this.lid;
    }

    public ModelPart getHead() {
        return this.head;
    }
}

