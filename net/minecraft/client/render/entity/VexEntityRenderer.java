/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class VexEntityRenderer
extends BipedEntityRenderer<VexEntity, VexEntityModel> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/illager/vex.png");
    private static final Identifier CHARGING_TEXTURE = new Identifier("textures/entity/illager/vex_charging.png");

    public VexEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new VexEntityModel(), 0.3f);
    }

    @Override
    protected int getBlockLight(VexEntity vexEntity, float f) {
        return 15;
    }

    @Override
    public Identifier getTexture(VexEntity vexEntity) {
        if (vexEntity.isCharging()) {
            return CHARGING_TEXTURE;
        }
        return TEXTURE;
    }

    @Override
    protected void scale(VexEntity vexEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(0.4f, 0.4f, 0.4f);
    }
}

