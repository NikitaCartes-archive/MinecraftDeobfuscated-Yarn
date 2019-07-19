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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class IllusionerEntityRenderer
extends IllagerEntityRenderer<IllusionerEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/illager/illusioner.png");

    public IllusionerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new IllagerEntityModel(0.0f, 0.0f, 64, 64), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<IllusionerEntity, IllagerEntityModel<IllusionerEntity>>((FeatureRendererContext)this){

            @Override
            public void render(IllusionerEntity illusionerEntity, float f, float g, float h, float i, float j, float k, float l) {
                if (illusionerEntity.isSpellcasting() || illusionerEntity.isAttacking()) {
                    super.render(illusionerEntity, f, g, h, i, j, k, l);
                }
            }
        });
        ((IllagerEntityModel)this.model).method_2812().visible = true;
    }

    @Override
    protected Identifier getTexture(IllusionerEntity illusionerEntity) {
        return SKIN;
    }

    @Override
    public void render(IllusionerEntity illusionerEntity, double d, double e, double f, float g, float h) {
        if (illusionerEntity.isInvisible()) {
            Vec3d[] vec3ds = illusionerEntity.method_7065(h);
            float i = this.getAnimationProgress(illusionerEntity, h);
            for (int j = 0; j < vec3ds.length; ++j) {
                super.render(illusionerEntity, d + vec3ds[j].x + (double)MathHelper.cos((float)j + i * 0.5f) * 0.025, e + vec3ds[j].y + (double)MathHelper.cos((float)j + i * 0.75f) * 0.0125, f + vec3ds[j].z + (double)MathHelper.cos((float)j + i * 0.7f) * 0.025, g, h);
            }
        } else {
            super.render(illusionerEntity, d, e, f, g, h);
        }
    }

    @Override
    protected boolean method_4056(IllusionerEntity illusionerEntity) {
        return true;
    }

    @Override
    protected /* synthetic */ boolean method_4056(LivingEntity livingEntity) {
        return this.method_4056((IllusionerEntity)livingEntity);
    }
}

