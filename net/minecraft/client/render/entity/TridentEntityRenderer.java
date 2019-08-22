/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TridentEntityRenderer
extends EntityRenderer<TridentEntity> {
    public static final Identifier SKIN = new Identifier("textures/entity/trident.png");
    private final TridentEntityModel model = new TridentEntityModel();

    public TridentEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public void method_4133(TridentEntity tridentEntity, double d, double e, double f, float g, float h) {
        this.bindEntityTexture(tridentEntity);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.pushMatrix();
        RenderSystem.disableLighting();
        RenderSystem.translatef((float)d, (float)e, (float)f);
        RenderSystem.rotatef(MathHelper.lerp(h, tridentEntity.prevYaw, tridentEntity.yaw) - 90.0f, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(MathHelper.lerp(h, tridentEntity.prevPitch, tridentEntity.pitch) + 90.0f, 0.0f, 0.0f, 1.0f);
        this.model.renderItem();
        RenderSystem.popMatrix();
        this.method_4131(tridentEntity, d, e, f, g, h);
        super.render(tridentEntity, d, e, f, g, h);
        RenderSystem.enableLighting();
    }

    protected Identifier method_4134(TridentEntity tridentEntity) {
        return SKIN;
    }

    protected void method_4131(TridentEntity tridentEntity, double d, double e, double f, float g, float h) {
        float am;
        float al;
        float ak;
        double aj;
        double ai;
        double ah;
        double ag;
        float af;
        double ae;
        int ad;
        Entity entity = tridentEntity.getOwner();
        if (entity == null || !tridentEntity.isNoClip()) {
            return;
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        double i = MathHelper.lerp(h * 0.5f, entity.yaw, entity.prevYaw) * ((float)Math.PI / 180);
        double j = Math.cos(i);
        double k = Math.sin(i);
        double l = MathHelper.lerp((double)h, entity.prevX, entity.x);
        double m = MathHelper.lerp((double)h, entity.prevY + (double)entity.getStandingEyeHeight() * 0.8, entity.y + (double)entity.getStandingEyeHeight() * 0.8);
        double n = MathHelper.lerp((double)h, entity.prevZ, entity.z);
        double o = j - k;
        double p = k + j;
        double q = MathHelper.lerp((double)h, tridentEntity.prevX, tridentEntity.x);
        double r = MathHelper.lerp((double)h, tridentEntity.prevY, tridentEntity.y);
        double s = MathHelper.lerp((double)h, tridentEntity.prevZ, tridentEntity.z);
        double t = (float)(l - q);
        double u = (float)(m - r);
        double v = (float)(n - s);
        double w = Math.sqrt(t * t + u * u + v * v);
        int x = tridentEntity.getEntityId() + tridentEntity.age;
        double y = (double)((float)x + h) * -0.1;
        double z = Math.min(0.5, w / 30.0);
        RenderSystem.disableTexture();
        RenderSystem.disableLighting();
        RenderSystem.disableCull();
        RenderSystem.glMultiTexCoord2f(33985, 255.0f, 255.0f);
        bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
        int aa = 37;
        int ab = 7 - x % 7;
        double ac = 0.1;
        for (ad = 0; ad <= 37; ++ad) {
            ae = (double)ad / 37.0;
            af = 1.0f - (float)((ad + ab) % 7) / 7.0f;
            ag = ae * 2.0 - 1.0;
            ag = (1.0 - ag * ag) * z;
            ah = d + t * ae + Math.sin(ae * Math.PI * 8.0 + y) * o * ag;
            ai = e + u * ae + Math.cos(ae * Math.PI * 8.0 + y) * 0.02 + (0.1 + ag) * 1.0;
            aj = f + v * ae + Math.sin(ae * Math.PI * 8.0 + y) * p * ag;
            ak = 0.87f * af + 0.3f * (1.0f - af);
            al = 0.91f * af + 0.6f * (1.0f - af);
            am = 0.85f * af + 0.5f * (1.0f - af);
            bufferBuilder.vertex(ah, ai, aj).color(ak, al, am, 1.0f).next();
            bufferBuilder.vertex(ah + 0.1 * ag, ai + 0.1 * ag, aj).color(ak, al, am, 1.0f).next();
            if (ad > tridentEntity.field_7649 * 2) break;
        }
        tessellator.draw();
        bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
        for (ad = 0; ad <= 37; ++ad) {
            ae = (double)ad / 37.0;
            af = 1.0f - (float)((ad + ab) % 7) / 7.0f;
            ag = ae * 2.0 - 1.0;
            ag = (1.0 - ag * ag) * z;
            ah = d + t * ae + Math.sin(ae * Math.PI * 8.0 + y) * o * ag;
            ai = e + u * ae + Math.cos(ae * Math.PI * 8.0 + y) * 0.01 + (0.1 + ag) * 1.0;
            aj = f + v * ae + Math.sin(ae * Math.PI * 8.0 + y) * p * ag;
            ak = 0.87f * af + 0.3f * (1.0f - af);
            al = 0.91f * af + 0.6f * (1.0f - af);
            am = 0.85f * af + 0.5f * (1.0f - af);
            bufferBuilder.vertex(ah, ai, aj).color(ak, al, am, 1.0f).next();
            bufferBuilder.vertex(ah + 0.1 * ag, ai, aj + 0.1 * ag).color(ak, al, am, 1.0f).next();
            if (ad > tridentEntity.field_7649 * 2) break;
        }
        tessellator.draw();
        RenderSystem.enableLighting();
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
    }
}

