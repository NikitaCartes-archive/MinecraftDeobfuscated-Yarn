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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class DragonHeadEntityModel
extends SkullBlockEntityModel {
    private final ModelPart head;
    private final ModelPart jaw;

    public DragonHeadEntityModel(ModelPart modelPart) {
        this.head = modelPart.getChild("head");
        this.jaw = this.head.getChild("jaw");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = -16.0f;
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().cuboid("upper_lip", -6.0f, -1.0f, -24.0f, 12, 5, 16, 176, 44).cuboid("upper_head", -8.0f, -8.0f, -10.0f, 16, 16, 16, 112, 30).mirrored(true).cuboid("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, 0, 0).cuboid("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, 112, 0).mirrored(false).cuboid("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, 0, 0).cuboid("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, 112, 0), ModelTransform.NONE);
        modelPartData2.addChild("jaw", ModelPartBuilder.create().uv(176, 65).cuboid("jaw", -6.0f, 0.0f, -16.0f, 12.0f, 4.0f, 16.0f), ModelTransform.pivot(0.0f, 4.0f, -8.0f));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void method_2821(float f, float g, float h) {
        this.jaw.pitch = (float)(Math.sin(f * (float)Math.PI * 0.2f) + 1.0) * 0.2f;
        this.head.yaw = g * ((float)Math.PI / 180);
        this.head.pitch = h * ((float)Math.PI / 180);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.translate(0.0, -0.374375f, 0.0);
        matrices.scale(0.75f, 0.75f, 0.75f);
        this.head.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}

