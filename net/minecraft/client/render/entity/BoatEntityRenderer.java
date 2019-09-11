/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
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
        RenderSystem.pushMatrix();
        this.translateToBoat(d, e, f);
        this.rotateToBoat(boatEntity, g, h);
        this.bindEntityTexture(boatEntity);
        if (this.renderOutlines) {
            RenderSystem.enableColorMaterial();
            RenderSystem.setupSolidRenderingTextureCombine(this.getOutlineColor(boatEntity));
        }
        this.model.method_17071(boatEntity, h, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        if (this.renderOutlines) {
            RenderSystem.tearDownSolidRenderingTextureCombine();
            RenderSystem.disableColorMaterial();
        }
        RenderSystem.popMatrix();
        super.render(boatEntity, d, e, f, g, h);
    }

    public void rotateToBoat(BoatEntity boatEntity, float f, float g) {
        float j;
        RenderSystem.rotatef(180.0f - f, 0.0f, 1.0f, 0.0f);
        float h = (float)boatEntity.getDamageWobbleTicks() - g;
        float i = boatEntity.getDamageWobbleStrength() - g;
        if (i < 0.0f) {
            i = 0.0f;
        }
        if (h > 0.0f) {
            RenderSystem.rotatef(MathHelper.sin(h) * h * i / 10.0f * (float)boatEntity.getDamageWobbleSide(), 1.0f, 0.0f, 0.0f);
        }
        if (!MathHelper.approximatelyEquals(j = boatEntity.interpolateBubbleWobble(g), 0.0f)) {
            RenderSystem.rotatef(boatEntity.interpolateBubbleWobble(g), 1.0f, 0.0f, 1.0f);
        }
        RenderSystem.scalef(-1.0f, -1.0f, 1.0f);
    }

    public void translateToBoat(double d, double e, double f) {
        RenderSystem.translatef((float)d, (float)e + 0.375f, (float)f);
    }

    protected Identifier method_3891(BoatEntity boatEntity) {
        return SKIN[boatEntity.getBoatType().ordinal()];
    }

    @Override
    public boolean hasSecondPass() {
        return true;
    }

    public void method_3887(BoatEntity boatEntity, double d, double e, double f, float g, float h) {
        RenderSystem.pushMatrix();
        this.translateToBoat(d, e, f);
        this.rotateToBoat(boatEntity, g, h);
        this.bindEntityTexture(boatEntity);
        this.model.renderPass(boatEntity, h, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        RenderSystem.popMatrix();
    }
}

