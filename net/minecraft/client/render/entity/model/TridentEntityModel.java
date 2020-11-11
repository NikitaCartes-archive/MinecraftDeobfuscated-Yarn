/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TridentEntityModel
extends Model {
    public static final Identifier TEXTURE = new Identifier("textures/entity/trident.png");
    private final ModelPart field_27521;

    public TridentEntityModel(ModelPart modelPart) {
        super(RenderLayer::getEntitySolid);
        this.field_27521 = modelPart;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("pole", ModelPartBuilder.create().uv(0, 6).cuboid(-0.5f, 2.0f, -0.5f, 1.0f, 25.0f, 1.0f), ModelTransform.NONE);
        modelPartData2.addChild("base", ModelPartBuilder.create().uv(4, 0).cuboid(-1.5f, 0.0f, -0.5f, 3.0f, 2.0f, 1.0f), ModelTransform.NONE);
        modelPartData2.addChild("left_spike", ModelPartBuilder.create().uv(4, 3).cuboid(-2.5f, -3.0f, -0.5f, 1.0f, 4.0f, 1.0f), ModelTransform.NONE);
        modelPartData2.addChild("middle_spike", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5f, -4.0f, -0.5f, 1.0f, 4.0f, 1.0f), ModelTransform.NONE);
        modelPartData2.addChild("right_spike", ModelPartBuilder.create().uv(4, 3).mirrored().cuboid(1.5f, -3.0f, -0.5f, 1.0f, 4.0f, 1.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.field_27521.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}

