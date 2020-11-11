/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BlazeEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    private final ModelPart field_27394;
    private final ModelPart[] rods;
    private final ModelPart field_27395;

    public BlazeEntityModel(ModelPart modelPart) {
        this.field_27394 = modelPart;
        this.field_27395 = modelPart.getChild("head");
        this.rods = new ModelPart[12];
        Arrays.setAll(this.rods, i -> modelPart.getChild(BlazeEntityModel.method_31983(i)));
    }

    private static String method_31983(int i) {
        return "part" + i;
    }

    public static TexturedModelData getTexturedModelData() {
        float j;
        float h;
        float g;
        int i;
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE);
        float f = 0.0f;
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 8.0f, 2.0f);
        for (i = 0; i < 4; ++i) {
            g = MathHelper.cos(f) * 9.0f;
            h = -2.0f + MathHelper.cos((float)(i * 2) * 0.25f);
            j = MathHelper.sin(f) * 9.0f;
            modelPartData.addChild(BlazeEntityModel.method_31983(i), modelPartBuilder, ModelTransform.pivot(g, h, j));
            f += 1.5707964f;
        }
        f = 0.7853982f;
        for (i = 4; i < 8; ++i) {
            g = MathHelper.cos(f) * 7.0f;
            h = 2.0f + MathHelper.cos((float)(i * 2) * 0.25f);
            j = MathHelper.sin(f) * 7.0f;
            modelPartData.addChild(BlazeEntityModel.method_31983(i), modelPartBuilder, ModelTransform.pivot(g, h, j));
            f += 1.5707964f;
        }
        f = 0.47123894f;
        for (i = 8; i < 12; ++i) {
            g = MathHelper.cos(f) * 5.0f;
            h = 11.0f + MathHelper.cos((float)i * 1.5f * 0.5f);
            j = MathHelper.sin(f) * 5.0f;
            modelPartData.addChild(BlazeEntityModel.method_31983(i), modelPartBuilder, ModelTransform.pivot(g, h, j));
            f += 1.5707964f;
        }
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.field_27394;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        int i;
        float f = animationProgress * (float)Math.PI * -0.1f;
        for (i = 0; i < 4; ++i) {
            this.rods[i].pivotY = -2.0f + MathHelper.cos(((float)(i * 2) + animationProgress) * 0.25f);
            this.rods[i].pivotX = MathHelper.cos(f) * 9.0f;
            this.rods[i].pivotZ = MathHelper.sin(f) * 9.0f;
            f += 1.5707964f;
        }
        f = 0.7853982f + animationProgress * (float)Math.PI * 0.03f;
        for (i = 4; i < 8; ++i) {
            this.rods[i].pivotY = 2.0f + MathHelper.cos(((float)(i * 2) + animationProgress) * 0.25f);
            this.rods[i].pivotX = MathHelper.cos(f) * 7.0f;
            this.rods[i].pivotZ = MathHelper.sin(f) * 7.0f;
            f += 1.5707964f;
        }
        f = 0.47123894f + animationProgress * (float)Math.PI * -0.05f;
        for (i = 8; i < 12; ++i) {
            this.rods[i].pivotY = 11.0f + MathHelper.cos(((float)i * 1.5f + animationProgress) * 0.5f);
            this.rods[i].pivotX = MathHelper.cos(f) * 5.0f;
            this.rods[i].pivotZ = MathHelper.sin(f) * 5.0f;
            f += 1.5707964f;
        }
        this.field_27395.yaw = headYaw * ((float)Math.PI / 180);
        this.field_27395.pitch = headPitch * ((float)Math.PI / 180);
    }
}

