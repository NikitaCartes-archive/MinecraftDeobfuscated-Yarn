/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BookModel
extends Model {
    private final ModelPart leftCover = new ModelPart(this).setTextureOffset(0, 0).addCuboid(-6.0f, -5.0f, 0.0f, 6, 10, 0);
    private final ModelPart rightCover = new ModelPart(this).setTextureOffset(16, 0).addCuboid(0.0f, -5.0f, 0.0f, 6, 10, 0);
    private final ModelPart leftBlock;
    private final ModelPart rightBlock;
    private final ModelPart leftPage;
    private final ModelPart rightPage;
    private final ModelPart spine = new ModelPart(this).setTextureOffset(12, 0).addCuboid(-1.0f, -5.0f, 0.0f, 2, 10, 0);

    public BookModel() {
        this.leftBlock = new ModelPart(this).setTextureOffset(0, 10).addCuboid(0.0f, -4.0f, -0.99f, 5, 8, 1);
        this.rightBlock = new ModelPart(this).setTextureOffset(12, 10).addCuboid(0.0f, -4.0f, -0.01f, 5, 8, 1);
        this.leftPage = new ModelPart(this).setTextureOffset(24, 10).addCuboid(0.0f, -4.0f, 0.0f, 5, 8, 0);
        this.rightPage = new ModelPart(this).setTextureOffset(24, 10).addCuboid(0.0f, -4.0f, 0.0f, 5, 8, 0);
        this.leftCover.setPivot(0.0f, 0.0f, -1.0f);
        this.rightCover.setPivot(0.0f, 0.0f, 1.0f);
        this.spine.yaw = 1.5707964f;
    }

    public void render(float f, float g, float h, float i, float j, float k) {
        this.setPageAngles(f, g, h, i, j, k);
        this.leftCover.render(k);
        this.rightCover.render(k);
        this.spine.render(k);
        this.leftBlock.render(k);
        this.rightBlock.render(k);
        this.leftPage.render(k);
        this.rightPage.render(k);
    }

    private void setPageAngles(float f, float g, float h, float i, float j, float k) {
        float l = (MathHelper.sin(f * 0.02f) * 0.1f + 1.25f) * i;
        this.leftCover.yaw = (float)Math.PI + l;
        this.rightCover.yaw = -l;
        this.leftBlock.yaw = l;
        this.rightBlock.yaw = -l;
        this.leftPage.yaw = l - l * 2.0f * g;
        this.rightPage.yaw = l - l * 2.0f * h;
        this.leftBlock.pivotX = MathHelper.sin(l);
        this.rightBlock.pivotX = MathHelper.sin(l);
        this.leftPage.pivotX = MathHelper.sin(l);
        this.rightPage.pivotX = MathHelper.sin(l);
    }
}

