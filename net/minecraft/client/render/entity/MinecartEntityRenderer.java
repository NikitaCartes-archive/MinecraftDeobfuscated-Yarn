/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class MinecartEntityRenderer<T extends AbstractMinecartEntity>
extends EntityRenderer<T> {
    private static final Identifier SKIN = new Identifier("textures/entity/minecart.png");
    protected final EntityModel<T> model = new MinecartEntityModel();

    public MinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.7f;
    }

    public void method_4063(T abstractMinecartEntity, double d, double e, double f, float g, float h) {
        BlockState blockState;
        GlStateManager.pushMatrix();
        this.bindEntityTexture(abstractMinecartEntity);
        long l = (long)((Entity)abstractMinecartEntity).getEntityId() * 493286711L;
        l = l * l * 4392167121L + l * 98761L;
        float i = (((float)(l >> 16 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float j = (((float)(l >> 20 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float k = (((float)(l >> 24 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        GlStateManager.translatef(i, j, k);
        double m = MathHelper.lerp((double)h, ((AbstractMinecartEntity)abstractMinecartEntity).prevRenderX, ((AbstractMinecartEntity)abstractMinecartEntity).x);
        double n = MathHelper.lerp((double)h, ((AbstractMinecartEntity)abstractMinecartEntity).prevRenderY, ((AbstractMinecartEntity)abstractMinecartEntity).y);
        double o = MathHelper.lerp((double)h, ((AbstractMinecartEntity)abstractMinecartEntity).prevRenderZ, ((AbstractMinecartEntity)abstractMinecartEntity).z);
        double p = 0.3f;
        Vec3d vec3d = ((AbstractMinecartEntity)abstractMinecartEntity).method_7508(m, n, o);
        float q = MathHelper.lerp(h, ((AbstractMinecartEntity)abstractMinecartEntity).prevPitch, ((AbstractMinecartEntity)abstractMinecartEntity).pitch);
        if (vec3d != null) {
            Vec3d vec3d2 = ((AbstractMinecartEntity)abstractMinecartEntity).method_7505(m, n, o, 0.3f);
            Vec3d vec3d3 = ((AbstractMinecartEntity)abstractMinecartEntity).method_7505(m, n, o, -0.3f);
            if (vec3d2 == null) {
                vec3d2 = vec3d;
            }
            if (vec3d3 == null) {
                vec3d3 = vec3d;
            }
            d += vec3d.x - m;
            e += (vec3d2.y + vec3d3.y) / 2.0 - n;
            f += vec3d.z - o;
            Vec3d vec3d4 = vec3d3.add(-vec3d2.x, -vec3d2.y, -vec3d2.z);
            if (vec3d4.length() != 0.0) {
                vec3d4 = vec3d4.normalize();
                g = (float)(Math.atan2(vec3d4.z, vec3d4.x) * 180.0 / Math.PI);
                q = (float)(Math.atan(vec3d4.y) * 73.0);
            }
        }
        GlStateManager.translatef((float)d, (float)e + 0.375f, (float)f);
        GlStateManager.rotatef(180.0f - g, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(-q, 0.0f, 0.0f, 1.0f);
        float r = (float)((AbstractMinecartEntity)abstractMinecartEntity).method_7507() - h;
        float s = ((AbstractMinecartEntity)abstractMinecartEntity).method_7521() - h;
        if (s < 0.0f) {
            s = 0.0f;
        }
        if (r > 0.0f) {
            GlStateManager.rotatef(MathHelper.sin(r) * r * s / 10.0f * (float)((AbstractMinecartEntity)abstractMinecartEntity).method_7522(), 1.0f, 0.0f, 0.0f);
        }
        int t = ((AbstractMinecartEntity)abstractMinecartEntity).getBlockOffset();
        if (this.field_4674) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(abstractMinecartEntity));
        }
        if ((blockState = ((AbstractMinecartEntity)abstractMinecartEntity).getContainedBlock()).getRenderType() != BlockRenderType.INVISIBLE) {
            GlStateManager.pushMatrix();
            this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            float u = 0.75f;
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.translatef(-0.5f, (float)(t - 8) / 16.0f, 0.5f);
            this.renderBlock(abstractMinecartEntity, h, blockState);
            GlStateManager.popMatrix();
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.bindEntityTexture(abstractMinecartEntity);
        }
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        this.model.render(abstractMinecartEntity, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
        if (this.field_4674) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        super.render(abstractMinecartEntity, d, e, f, g, h);
    }

    protected Identifier method_4065(T abstractMinecartEntity) {
        return SKIN;
    }

    protected void renderBlock(T abstractMinecartEntity, float f, BlockState blockState) {
        GlStateManager.pushMatrix();
        MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(blockState, ((Entity)abstractMinecartEntity).getBrightnessAtEyes());
        GlStateManager.popMatrix();
    }
}

