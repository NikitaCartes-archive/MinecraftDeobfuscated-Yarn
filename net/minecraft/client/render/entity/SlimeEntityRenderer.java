/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SlimeEntityRenderer
extends MobEntityRenderer<SlimeEntity, SlimeEntityModel<SlimeEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/slime/slime.png");

    public SlimeEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new SlimeEntityModel(16), 0.25f);
        this.addFeature(new SlimeOverlayFeatureRenderer<SlimeEntity>(this));
    }

    public void method_4117(SlimeEntity slimeEntity, double d, double e, double f, float g, float h) {
        this.field_4673 = 0.25f * (float)slimeEntity.getSize();
        super.method_4072(slimeEntity, d, e, f, g, h);
    }

    protected void method_4118(SlimeEntity slimeEntity, float f) {
        float g = 0.999f;
        RenderSystem.scalef(0.999f, 0.999f, 0.999f);
        float h = slimeEntity.getSize();
        float i = MathHelper.lerp(f, slimeEntity.lastStretch, slimeEntity.stretch) / (h * 0.5f + 1.0f);
        float j = 1.0f / (i + 1.0f);
        RenderSystem.scalef(j * h, 1.0f / j * h, j * h);
    }

    protected Identifier method_4116(SlimeEntity slimeEntity) {
        return SKIN;
    }
}

