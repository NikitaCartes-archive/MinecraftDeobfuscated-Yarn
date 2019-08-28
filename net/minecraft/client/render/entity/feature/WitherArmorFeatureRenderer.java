/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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
        RenderSystem.depthMask(!witherEntity.isInvisible());
        this.bindTexture(SKIN);
        RenderSystem.matrixMode(5890);
        RenderSystem.loadIdentity();
        float m = (float)witherEntity.age + h;
        float n = MathHelper.cos(m * 0.02f) * 3.0f;
        float o = m * 0.01f;
        RenderSystem.translatef(n, o, 0.0f);
        RenderSystem.matrixMode(5888);
        RenderSystem.enableBlend();
        float p = 0.5f;
        RenderSystem.color4f(0.5f, 0.5f, 0.5f, 1.0f);
        RenderSystem.disableLighting();
        RenderSystem.blendFunc(GlStateManager.class_4535.ONE, GlStateManager.class_4534.ONE);
        this.model.method_17128(witherEntity, f, g, h);
        ((WitherEntityModel)this.getModel()).copyStateTo(this.model);
        GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
        gameRenderer.setFogBlack(true);
        this.model.method_17129(witherEntity, f, g, i, j, k, l);
        gameRenderer.setFogBlack(false);
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

