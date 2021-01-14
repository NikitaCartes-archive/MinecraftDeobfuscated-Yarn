/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BookModel
extends Model {
    private final ModelPart leftCover = new ModelPart(64, 32, 0, 0).addCuboid(-6.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f);
    private final ModelPart rightCover = new ModelPart(64, 32, 16, 0).addCuboid(0.0f, -5.0f, -0.005f, 6.0f, 10.0f, 0.005f);
    private final ModelPart leftPages;
    private final ModelPart rightPages;
    private final ModelPart leftPage;
    private final ModelPart rightPage;
    private final ModelPart spine = new ModelPart(64, 32, 12, 0).addCuboid(-1.0f, -5.0f, 0.0f, 2.0f, 10.0f, 0.005f);
    private final List<ModelPart> parts;

    public BookModel() {
        super(RenderLayer::getEntitySolid);
        this.leftPages = new ModelPart(64, 32, 0, 10).addCuboid(0.0f, -4.0f, -0.99f, 5.0f, 8.0f, 1.0f);
        this.rightPages = new ModelPart(64, 32, 12, 10).addCuboid(0.0f, -4.0f, -0.01f, 5.0f, 8.0f, 1.0f);
        this.leftPage = new ModelPart(64, 32, 24, 10).addCuboid(0.0f, -4.0f, 0.0f, 5.0f, 8.0f, 0.005f);
        this.rightPage = new ModelPart(64, 32, 24, 10).addCuboid(0.0f, -4.0f, 0.0f, 5.0f, 8.0f, 0.005f);
        this.parts = ImmutableList.of(this.leftCover, this.rightCover, this.spine, this.leftPages, this.rightPages, this.leftPage, this.rightPage);
        this.leftCover.setPivot(0.0f, 0.0f, -1.0f);
        this.rightCover.setPivot(0.0f, 0.0f, 1.0f);
        this.spine.yaw = 1.5707964f;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.renderBook(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    public void renderBook(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.parts.forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
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

