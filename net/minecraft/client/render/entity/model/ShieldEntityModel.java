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

@Environment(value=EnvType.CLIENT)
public class ShieldEntityModel
extends Model {
    private static final String PLATE = "plate";
    private static final String HANDLE = "handle";
    private static final int field_32551 = 10;
    private static final int field_32552 = 20;
    private final ModelPart root;
    private final ModelPart plate;
    private final ModelPart handle;

    public ShieldEntityModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.root = root;
        this.plate = root.getChild(PLATE);
        this.handle = root.getChild(HANDLE);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(PLATE, ModelPartBuilder.create().uv(0, 0).cuboid(-6.0f, -11.0f, -2.0f, 12.0f, 22.0f, 1.0f), ModelTransform.NONE);
        modelPartData.addChild(HANDLE, ModelPartBuilder.create().uv(26, 0).cuboid(-1.0f, -3.0f, -1.0f, 2.0f, 6.0f, 6.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 64);
    }

    public ModelPart getPlate() {
        return this.plate;
    }

    public ModelPart getHandle() {
        return this.handle;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}

