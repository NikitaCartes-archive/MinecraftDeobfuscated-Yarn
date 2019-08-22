/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DragonEntityModel;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class EnderDragonEyesFeatureRenderer
extends FeatureRenderer<EnderDragonEntity, DragonEntityModel> {
    private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon_eyes.png");

    public EnderDragonEyesFeatureRenderer(FeatureRendererContext<EnderDragonEntity, DragonEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_4183(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k, float l) {
        this.bindTexture(SKIN);
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFunc(class_4493.class_4535.ONE, class_4493.class_4534.ONE);
        RenderSystem.disableLighting();
        RenderSystem.depthFunc(514);
        int m = 61680;
        int n = 61680;
        boolean o = false;
        RenderSystem.glMultiTexCoord2f(33985, 61680.0f, 0.0f);
        RenderSystem.enableLighting();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
        gameRenderer.setFogBlack(true);
        ((DragonEntityModel)this.getModel()).method_17137(enderDragonEntity, f, g, i, j, k, l);
        gameRenderer.setFogBlack(false);
        this.applyLightmapCoordinates(enderDragonEntity);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.depthFunc(515);
    }

    @Override
    public boolean hasHurtOverlay() {
        return false;
    }
}

