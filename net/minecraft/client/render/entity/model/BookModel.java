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
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of the enchanting table's book.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_LID}</td><td>{@linkplain #root Root part}</td><td>{@link #leftCover}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_LID}</td><td>{@linkplain #root Root part}</td><td>{@link #rightCover}</td>
 * </tr>
 * <tr>
 *   <td>{@code seam}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value #LEFT_PAGES}</td><td>{@linkplain #root Root part}</td><td>{@link #leftPages}</td>
 * </tr>
 * <tr>
 *   <td>{@value #RIGHT_PAGES}</td><td>{@linkplain #root Root part}</td><td>{@link #rightPages}</td>
 * </tr>
 * <tr>
 *   <td>{@value #FLIP_PAGE1}</td><td>{@linkplain #root Root part}</td><td>{@link #leftFlippingPage}</td>
 * </tr>
 * <tr>
 *   <td>{@value #FLIP_PAGE2}</td><td>{@linkplain #root Root part}</td><td>{@link #rightFlippingPage}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(value=EnvType.CLIENT)
public class BookModel
extends Model {
    /**
     * The key of the left pages model part, whose value is {@value}.
     */
    private static final String LEFT_PAGES = "left_pages";
    /**
     * The key of the right pages model part, whose value is {@value}.
     */
    private static final String RIGHT_PAGES = "right_pages";
    /**
     * The key of the left flipping page model part, whose value is {@value}.
     */
    private static final String FLIP_PAGE1 = "flip_page1";
    /**
     * The key of the right flipping page model part, whose value is {@value}.
     */
    private static final String FLIP_PAGE2 = "flip_page2";
    private final ModelPart root;
    private final ModelPart leftCover;
    private final ModelPart rightCover;
    private final ModelPart leftPages;
    private final ModelPart rightPages;
    private final ModelPart leftFlippingPage;
    private final ModelPart rightFlippingPage;

    public BookModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.root = root;
        this.leftCover = root.getChild(EntityModelPartNames.LEFT_LID);
        this.rightCover = root.getChild(EntityModelPartNames.RIGHT_LID);
        this.leftPages = root.getChild(LEFT_PAGES);
        this.rightPages = root.getChild(RIGHT_PAGES);
        this.leftFlippingPage = root.getChild(FLIP_PAGE1);
        this.rightFlippingPage = root.getChild(FLIP_PAGE2);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.LEFT_LID, ModelPartBuilder.create().uv(0, 0).cuboid(-6.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f), ModelTransform.pivot(0.0f, 0.0f, -1.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_LID, ModelPartBuilder.create().uv(16, 0).cuboid(0.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f), ModelTransform.pivot(0.0f, 0.0f, 1.0f));
        modelPartData.addChild("seam", ModelPartBuilder.create().uv(12, 0).cuboid(-1.0f, -5.0f, 0.0f, 2.0f, 10.0f, 0.005f), ModelTransform.rotation(0.0f, 1.5707964f, 0.0f));
        modelPartData.addChild(LEFT_PAGES, ModelPartBuilder.create().uv(0, 10).cuboid(0.0f, -4.0f, -0.99f, 5.0f, 8.0f, 1.0f), ModelTransform.NONE);
        modelPartData.addChild(RIGHT_PAGES, ModelPartBuilder.create().uv(12, 10).cuboid(0.0f, -4.0f, -0.01f, 5.0f, 8.0f, 1.0f), ModelTransform.NONE);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(24, 10).cuboid(0.0f, -4.0f, 0.0f, 5.0f, 8.0f, 0.005f);
        modelPartData.addChild(FLIP_PAGE1, modelPartBuilder, ModelTransform.NONE);
        modelPartData.addChild(FLIP_PAGE2, modelPartBuilder, ModelTransform.NONE);
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
        this.leftFlippingPage.yaw = f - f * 2.0f * leftFlipAmount;
        this.rightFlippingPage.yaw = f - f * 2.0f * rightFlipAmount;
        this.leftPages.pivotX = MathHelper.sin(f);
        this.rightPages.pivotX = MathHelper.sin(f);
        this.leftFlippingPage.pivotX = MathHelper.sin(f);
        this.rightFlippingPage.pivotX = MathHelper.sin(f);
    }
}

