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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BookModel
extends Model {
    private final ModelPart root;
    private final ModelPart leftCover;
    private final ModelPart rightCover;
    private final ModelPart leftPages;
    private final ModelPart rightPages;
    private final ModelPart leftPage;
    private final ModelPart rightPage;

    public BookModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.root = root;
        this.leftCover = root.getChild("left_lid");
        this.rightCover = root.getChild("right_lid");
        this.leftPages = root.getChild("left_pages");
        this.rightPages = root.getChild("right_pages");
        this.leftPage = root.getChild("flip_page1");
        this.rightPage = root.getChild("flip_page2");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("left_lid", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f), ModelTransform.pivot(0.0f, 0.0f, -1.0f));
        modelPartData.addChild("right_lid", ModelPartBuilder.create().uv(16, 0).cuboid(0.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f), ModelTransform.pivot(0.0f, 0.0f, 1.0f));
        modelPartData.addChild("seam", ModelPartBuilder.create().uv(12, 0).cuboid(-1.0f, -5.0f, 0.0f, 2.0f, 10.0f, 0.005f), ModelTransform.rotation(0.0f, 1.5707964f, 0.0f));
        modelPartData.addChild("left_pages", ModelPartBuilder.create().uv(0, 10).cuboid(0.0f, -4.0f, -0.99f, 5.0f, 8.0f, 1.0f), ModelTransform.NONE);
        modelPartData.addChild("right_pages", ModelPartBuilder.create().uv(12, 10).cuboid(0.0f, -4.0f, -0.01f, 5.0f, 8.0f, 1.0f), ModelTransform.NONE);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(24, 10).cuboid(0.0f, -4.0f, 0.0f, 5.0f, 8.0f, 0.005f);
        modelPartData.addChild("flip_page1", modelPartBuilder, ModelTransform.NONE);
        modelPartData.addChild("flip_page2", modelPartBuilder, ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.renderBook(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    public void renderBook(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    public void setPageAngles(float pageTurnAmount, float leftFlipAmount, float rightFlipAmount, float pageTurnSpeed) {
        float f = (MathHelper.sin(pageTurnAmount * 0.02f) * 0.1f + 1.25f) * pageTurnSpeed;
        this.leftCover.yaw = (float)Math.PI + f;
        this.rightCover.yaw = -f;
        this.leftPages.yaw = f;
        this.rightPages.yaw = -f;
        this.leftPage.yaw = f - f * 2.0f * leftFlipAmount;
        this.rightPage.yaw = f - f * 2.0f * rightFlipAmount;
        this.leftPages.pivotX = MathHelper.sin(f);
        this.rightPages.pivotX = MathHelper.sin(f);
        this.leftPage.pivotX = MathHelper.sin(f);
        this.rightPage.pivotX = MathHelper.sin(f);
    }
}

