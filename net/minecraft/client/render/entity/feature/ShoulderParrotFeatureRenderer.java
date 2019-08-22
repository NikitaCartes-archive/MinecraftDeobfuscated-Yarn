/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;

@Environment(value=EnvType.CLIENT)
public class ShoulderParrotFeatureRenderer<T extends PlayerEntity>
extends FeatureRenderer<T, PlayerEntityModel<T>> {
    private final ParrotEntityModel model = new ParrotEntityModel();

    public ShoulderParrotFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4185(T playerEntity, float f, float g, float h, float i, float j, float k, float l) {
        RenderSystem.enableRescaleNormal();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.renderShoulderParrot(playerEntity, f, g, h, j, k, l, true);
        this.renderShoulderParrot(playerEntity, f, g, h, j, k, l, false);
        RenderSystem.disableRescaleNormal();
    }

    private void renderShoulderParrot(T playerEntity, float f, float g, float h, float i, float j, float k, boolean bl) {
        CompoundTag compoundTag = bl ? ((PlayerEntity)playerEntity).getShoulderEntityLeft() : ((PlayerEntity)playerEntity).getShoulderEntityRight();
        EntityType.get(compoundTag.getString("id")).filter(entityType -> entityType == EntityType.PARROT).ifPresent(entityType -> {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(bl ? 0.4f : -0.4f, playerEntity.isInSneakingPose() ? -1.3f : -1.5f, 0.0f);
            this.bindTexture(ParrotEntityRenderer.SKINS[compoundTag.getInt("Variant")]);
            this.model.method_17106(f, g, i, j, k, playerEntity.age);
            RenderSystem.popMatrix();
        });
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

