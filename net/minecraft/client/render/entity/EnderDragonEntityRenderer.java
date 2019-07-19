/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.EnderDragonDeathFeatureRenderer;
import net.minecraft.client.render.entity.feature.EnderDragonEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.DragonEntityModel;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EnderDragonEntityRenderer
extends MobEntityRenderer<EnderDragonEntity, DragonEntityModel> {
    public static final Identifier CRYSTAL_BEAM_TEX = new Identifier("textures/entity/end_crystal/end_crystal_beam.png");
    private static final Identifier EXPLOSION_TEX = new Identifier("textures/entity/enderdragon/dragon_exploding.png");
    private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon.png");

    public EnderDragonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new DragonEntityModel(0.0f), 0.5f);
        this.addFeature(new EnderDragonEyesFeatureRenderer(this));
        this.addFeature(new EnderDragonDeathFeatureRenderer(this));
    }

    @Override
    protected void setupTransforms(EnderDragonEntity enderDragonEntity, float f, float g, float h) {
        float i = (float)enderDragonEntity.method_6817(7, h)[0];
        float j = (float)(enderDragonEntity.method_6817(5, h)[1] - enderDragonEntity.method_6817(10, h)[1]);
        GlStateManager.rotatef(-i, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(j * 10.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translatef(0.0f, 0.0f, 1.0f);
        if (enderDragonEntity.deathTime > 0) {
            float k = ((float)enderDragonEntity.deathTime + h - 1.0f) / 20.0f * 1.6f;
            if ((k = MathHelper.sqrt(k)) > 1.0f) {
                k = 1.0f;
            }
            GlStateManager.rotatef(k * this.getLyingAngle(enderDragonEntity), 0.0f, 0.0f, 1.0f);
        }
    }

    @Override
    protected void render(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k) {
        if (enderDragonEntity.field_7031 > 0) {
            float l = (float)enderDragonEntity.field_7031 / 200.0f;
            GlStateManager.depthFunc(515);
            GlStateManager.enableAlphaTest();
            GlStateManager.alphaFunc(516, l);
            this.bindTexture(EXPLOSION_TEX);
            ((DragonEntityModel)this.model).render(enderDragonEntity, f, g, h, i, j, k);
            GlStateManager.alphaFunc(516, 0.1f);
            GlStateManager.depthFunc(514);
        }
        this.bindEntityTexture(enderDragonEntity);
        ((DragonEntityModel)this.model).render(enderDragonEntity, f, g, h, i, j, k);
        if (enderDragonEntity.hurtTime > 0) {
            GlStateManager.depthFunc(514);
            GlStateManager.disableTexture();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color4f(1.0f, 0.0f, 0.0f, 0.5f);
            ((DragonEntityModel)this.model).render(enderDragonEntity, f, g, h, i, j, k);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
            GlStateManager.depthFunc(515);
        }
    }

    @Override
    public void render(EnderDragonEntity enderDragonEntity, double d, double e, double f, float g, float h) {
        super.render(enderDragonEntity, d, e, f, g, h);
        if (enderDragonEntity.connectedCrystal != null) {
            this.bindTexture(CRYSTAL_BEAM_TEX);
            float i = MathHelper.sin(((float)enderDragonEntity.connectedCrystal.age + h) * 0.2f) / 2.0f + 0.5f;
            i = (i * i + i) * 0.2f;
            EnderDragonEntityRenderer.renderCrystalBeam(d, e, f, h, MathHelper.lerp((double)(1.0f - h), enderDragonEntity.x, enderDragonEntity.prevX), MathHelper.lerp((double)(1.0f - h), enderDragonEntity.y, enderDragonEntity.prevY), MathHelper.lerp((double)(1.0f - h), enderDragonEntity.z, enderDragonEntity.prevZ), enderDragonEntity.age, enderDragonEntity.connectedCrystal.x, (double)i + enderDragonEntity.connectedCrystal.y, enderDragonEntity.connectedCrystal.z);
        }
    }

    public static void renderCrystalBeam(double d, double e, double f, float g, double h, double i, double j, int k, double l, double m, double n) {
        float o = (float)(l - h);
        float p = (float)(m - 1.0 - i);
        float q = (float)(n - j);
        float r = MathHelper.sqrt(o * o + q * q);
        float s = MathHelper.sqrt(o * o + p * p + q * q);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)d, (float)e + 2.0f, (float)f);
        GlStateManager.rotatef((float)(-Math.atan2(q, o)) * 57.295776f - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef((float)(-Math.atan2(r, p)) * 57.295776f - 90.0f, 1.0f, 0.0f, 0.0f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        DiffuseLighting.disable();
        GlStateManager.disableCull();
        GlStateManager.shadeModel(7425);
        float t = 0.0f - ((float)k + g) * 0.01f;
        float u = MathHelper.sqrt(o * o + p * p + q * q) / 32.0f - ((float)k + g) * 0.01f;
        bufferBuilder.begin(5, VertexFormats.POSITION_TEXTURE_COLOR);
        int v = 8;
        for (int w = 0; w <= 8; ++w) {
            float x = MathHelper.sin((float)(w % 8) * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float y = MathHelper.cos((float)(w % 8) * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float z = (float)(w % 8) / 8.0f;
            bufferBuilder.vertex(x * 0.2f, y * 0.2f, 0.0).texture(z, t).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(x, y, s).texture(z, u).color(255, 255, 255, 255).next();
        }
        tessellator.draw();
        GlStateManager.enableCull();
        GlStateManager.shadeModel(7424);
        DiffuseLighting.enable();
        GlStateManager.popMatrix();
    }

    @Override
    protected Identifier getTexture(EnderDragonEntity enderDragonEntity) {
        return SKIN;
    }
}

