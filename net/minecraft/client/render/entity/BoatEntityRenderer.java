/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BoatEntityRenderer
extends EntityRenderer<BoatEntity> {
    private static final Identifier[] SKIN = new Identifier[]{new Identifier("textures/entity/boat/oak.png"), new Identifier("textures/entity/boat/spruce.png"), new Identifier("textures/entity/boat/birch.png"), new Identifier("textures/entity/boat/jungle.png"), new Identifier("textures/entity/boat/acacia.png"), new Identifier("textures/entity/boat/dark_oak.png")};
    protected final BoatEntityModel model = new BoatEntityModel();

    public BoatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.8f;
    }

    public void method_3888(BoatEntity boatEntity, double d, double e, double f, float g, float h) {
        GlStateManager.pushMatrix();
        this.method_3890(d, e, f);
        this.method_3889(boatEntity, g, h);
        this.bindEntityTexture(boatEntity);
        if (this.field_4674) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(boatEntity));
        }
        this.model.method_17071(boatEntity, h, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        if (this.field_4674) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.render(boatEntity, d, e, f, g, h);
    }

    public void method_3889(BoatEntity boatEntity, float f, float g) {
        float j;
        GlStateManager.rotatef(180.0f - f, 0.0f, 1.0f, 0.0f);
        float h = (float)boatEntity.method_7533() - g;
        float i = boatEntity.method_7554() - g;
        if (i < 0.0f) {
            i = 0.0f;
        }
        if (h > 0.0f) {
            GlStateManager.rotatef(MathHelper.sin(h) * h * i / 10.0f * (float)boatEntity.method_7543(), 1.0f, 0.0f, 0.0f);
        }
        if (!MathHelper.equalsApproximate(j = boatEntity.method_7547(g), 0.0f)) {
            GlStateManager.rotatef(boatEntity.method_7547(g), 1.0f, 0.0f, 1.0f);
        }
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
    }

    public void method_3890(double d, double e, double f) {
        GlStateManager.translatef((float)d, (float)e + 0.375f, (float)f);
    }

    protected Identifier method_3891(BoatEntity boatEntity) {
        return SKIN[boatEntity.getBoatType().ordinal()];
    }

    @Override
    public boolean hasSecondPass() {
        return true;
    }

    public void method_3887(BoatEntity boatEntity, double d, double e, double f, float g, float h) {
        GlStateManager.pushMatrix();
        this.method_3890(d, e, f);
        this.method_3889(boatEntity, g, h);
        this.bindEntityTexture(boatEntity);
        this.model.renderPass(boatEntity, h, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
    }
}

