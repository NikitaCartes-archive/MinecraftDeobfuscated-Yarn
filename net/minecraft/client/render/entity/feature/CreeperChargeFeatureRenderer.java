/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CreeperChargeFeatureRenderer
extends FeatureRenderer<CreeperEntity, CreeperEntityModel<CreeperEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
    private final CreeperEntityModel<CreeperEntity> model = new CreeperEntityModel(2.0f);

    public CreeperChargeFeatureRenderer(FeatureRendererContext<CreeperEntity, CreeperEntityModel<CreeperEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4178(CreeperEntity creeperEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (!creeperEntity.isCharged()) {
            return;
        }
        boolean bl = creeperEntity.isInvisible();
        RenderSystem.depthMask(!bl);
        this.bindTexture(SKIN);
        RenderSystem.matrixMode(5890);
        RenderSystem.loadIdentity();
        float m = (float)creeperEntity.age + h;
        RenderSystem.translatef(m * 0.01f, m * 0.01f, 0.0f);
        RenderSystem.matrixMode(5888);
        RenderSystem.enableBlend();
        float n = 0.5f;
        RenderSystem.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        RenderSystem.disableLighting();
        RenderSystem.blendFunc(GlStateManager.class_4535.ONE, GlStateManager.class_4534.ONE);
        ((CreeperEntityModel)this.getModel()).copyStateTo(this.model);
        BackgroundRenderer.setFogBlack(true);
        this.model.render(creeperEntity, f, g, i, j, k, l);
        BackgroundRenderer.setFogBlack(false);
        RenderSystem.matrixMode(5890);
        RenderSystem.loadIdentity();
        RenderSystem.matrixMode(5888);
        RenderSystem.enableLighting();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

