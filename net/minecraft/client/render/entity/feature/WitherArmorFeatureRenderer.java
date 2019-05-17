/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherArmorFeatureRenderer
extends FeatureRenderer<WitherEntity, WitherEntityModel<WitherEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/wither/wither_armor.png");
    private final WitherEntityModel<WitherEntity> model = new WitherEntityModel(0.5f);

    public WitherArmorFeatureRenderer(FeatureRendererContext<WitherEntity, WitherEntityModel<WitherEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4207(WitherEntity witherEntity, float f, float g, float h, float i, float j, float k, float l) {
        if (!witherEntity.isAtHalfHealth()) {
            return;
        }
        GlStateManager.depthMask(!witherEntity.isInvisible());
        this.bindTexture(SKIN);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float m = (float)witherEntity.age + h;
        float n = MathHelper.cos(m * 0.02f) * 3.0f;
        float o = m * 0.01f;
        GlStateManager.translatef(n, o, 0.0f);
        GlStateManager.matrixMode(5888);
        GlStateManager.enableBlend();
        float p = 0.5f;
        GlStateManager.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        this.model.method_17128(witherEntity, f, g, h);
        ((WitherEntityModel)this.getModel()).copyStateTo(this.model);
        GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
        gameRenderer.setFogBlack(true);
        this.model.method_17129(witherEntity, f, g, i, j, k, l);
        gameRenderer.setFogBlack(false);
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

