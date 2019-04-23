/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BookModel
extends Model {
    private final Cuboid leftCover = new Cuboid(this).setTextureOffset(0, 0).addBox(-6.0f, -5.0f, 0.0f, 6, 10, 0);
    private final Cuboid rightCover = new Cuboid(this).setTextureOffset(16, 0).addBox(0.0f, -5.0f, 0.0f, 6, 10, 0);
    private final Cuboid leftBlock;
    private final Cuboid rightBlock;
    private final Cuboid leftPage;
    private final Cuboid rightPage;
    private final Cuboid spine = new Cuboid(this).setTextureOffset(12, 0).addBox(-1.0f, -5.0f, 0.0f, 2, 10, 0);

    public BookModel() {
        this.leftBlock = new Cuboid(this).setTextureOffset(0, 10).addBox(0.0f, -4.0f, -0.99f, 5, 8, 1);
        this.rightBlock = new Cuboid(this).setTextureOffset(12, 10).addBox(0.0f, -4.0f, -0.01f, 5, 8, 1);
        this.leftPage = new Cuboid(this).setTextureOffset(24, 10).addBox(0.0f, -4.0f, 0.0f, 5, 8, 0);
        this.rightPage = new Cuboid(this).setTextureOffset(24, 10).addBox(0.0f, -4.0f, 0.0f, 5, 8, 0);
        this.leftCover.setRotationPoint(0.0f, 0.0f, -1.0f);
        this.rightCover.setRotationPoint(0.0f, 0.0f, 1.0f);
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
        this.leftBlock.rotationPointX = MathHelper.sin(l);
        this.rightBlock.rotationPointX = MathHelper.sin(l);
        this.leftPage.rotationPointX = MathHelper.sin(l);
        this.rightPage.rotationPointX = MathHelper.sin(l);
    }
}

