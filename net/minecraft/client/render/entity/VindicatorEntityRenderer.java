/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class VindicatorEntityRenderer
extends IllagerEntityRenderer<VindicatorEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/illager/vindicator.png");

    public VindicatorEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new IllagerEntityModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<VindicatorEntity, IllagerEntityModel<VindicatorEntity>>((FeatureRendererContext)this){

            @Override
            public void render(VindicatorEntity vindicatorEntity, float f, float g, float h, float i, float j, float k, float l) {
                if (vindicatorEntity.isAttacking()) {
                    super.render(vindicatorEntity, f, g, h, i, j, k, l);
                }
            }
        });
    }

    @Override
    protected Identifier getTexture(VindicatorEntity vindicatorEntity) {
        return SKIN;
    }
}

