/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class EndCrystalEntityModel<T extends Entity>
extends EntityModel<T> {
    private final Cuboid cube;
    private final Cuboid glass = new Cuboid(this, "glass");
    private final Cuboid base;

    public EndCrystalEntityModel(float f, boolean bl) {
        this.glass.setTextureOffset(0, 0).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        this.cube = new Cuboid(this, "cube");
        this.cube.setTextureOffset(32, 0).addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8);
        if (bl) {
            this.base = new Cuboid(this, "base");
            this.base.setTextureOffset(0, 16).addBox(-6.0f, 0.0f, -6.0f, 12, 4, 12);
        } else {
            this.base = null;
        }
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        GlStateManager.pushMatrix();
        GlStateManager.scalef(2.0f, 2.0f, 2.0f);
        GlStateManager.translatef(0.0f, -0.5f, 0.0f);
        if (this.base != null) {
            this.base.render(k);
        }
        GlStateManager.rotatef(g, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, 0.8f + h, 0.0f);
        GlStateManager.rotatef(60.0f, 0.7071f, 0.0f, 0.7071f);
        this.glass.render(k);
        float l = 0.875f;
        GlStateManager.scalef(0.875f, 0.875f, 0.875f);
        GlStateManager.rotatef(60.0f, 0.7071f, 0.0f, 0.7071f);
        GlStateManager.rotatef(g, 0.0f, 1.0f, 0.0f);
        this.glass.render(k);
        GlStateManager.scalef(0.875f, 0.875f, 0.875f);
        GlStateManager.rotatef(60.0f, 0.7071f, 0.0f, 0.7071f);
        GlStateManager.rotatef(g, 0.0f, 1.0f, 0.0f);
        this.cube.render(k);
        GlStateManager.popMatrix();
    }
}

