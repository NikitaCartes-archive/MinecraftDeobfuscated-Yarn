/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WolfEntityRenderer
extends MobEntityRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
    private static final Identifier WILD_SKIN = new Identifier("textures/entity/wolf/wolf.png");
    private static final Identifier TAMED_SKIN = new Identifier("textures/entity/wolf/wolf_tame.png");
    private static final Identifier ANGRY_SKIN = new Identifier("textures/entity/wolf/wolf_angry.png");

    public WolfEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new WolfEntityModel(), 0.5f);
        this.addFeature(new WolfCollarFeatureRenderer(this));
    }

    protected float method_4167(WolfEntity wolfEntity, float f) {
        return wolfEntity.method_6714();
    }

    public void method_4166(WolfEntity wolfEntity, double d, double e, double f, float g, float h) {
        if (wolfEntity.isWet()) {
            float i = wolfEntity.getBrightnessAtEyes() * wolfEntity.getWetBrightnessMultiplier(h);
            RenderSystem.color3f(i, i, i);
        }
        super.method_4072(wolfEntity, d, e, f, g, h);
    }

    protected Identifier method_4165(WolfEntity wolfEntity) {
        if (wolfEntity.isTamed()) {
            return TAMED_SKIN;
        }
        if (wolfEntity.isAngry()) {
            return ANGRY_SKIN;
        }
        return WILD_SKIN;
    }

    @Override
    protected /* synthetic */ float getAge(LivingEntity livingEntity, float f) {
        return this.method_4167((WolfEntity)livingEntity, f);
    }
}

