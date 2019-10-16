/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EnderCrystalEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EnderDragonEntityRenderer
extends EntityRenderer<EnderDragonEntity> {
    public static final Identifier CRYSTAL_BEAM_TEX = new Identifier("textures/entity/end_crystal/end_crystal_beam.png");
    private static final Identifier EXPLOSION_TEX = new Identifier("textures/entity/enderdragon/dragon_exploding.png");
    private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon.png");
    private static final Identifier EYES_TEX = new Identifier("textures/entity/enderdragon/dragon_eyes.png");
    private static final float field_21007 = (float)(Math.sqrt(3.0) / 2.0);
    private final DragonEntityModel field_21008 = new DragonEntityModel(0.0f);

    public EnderDragonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.5f;
    }

    public void method_3918(EnderDragonEntity enderDragonEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        matrixStack.push();
        float i = (float)enderDragonEntity.method_6817(7, h)[0];
        float j = (float)(enderDragonEntity.method_6817(5, h)[1] - enderDragonEntity.method_6817(10, h)[1]);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-i));
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(j * 10.0f));
        matrixStack.translate(0.0, 0.0, 1.0);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        float k = 0.0625f;
        matrixStack.translate(0.0, -1.501f, 0.0);
        boolean bl = enderDragonEntity.hurtTime > 0;
        int l = enderDragonEntity.getLightmapCoordinates();
        this.field_21008.method_23620(enderDragonEntity, 0.0f, 0.0f, h);
        if (enderDragonEntity.field_7031 > 0) {
            float m = (float)enderDragonEntity.field_7031 / 200.0f;
            VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityAlpha(EXPLOSION_TEX, m));
            this.field_21008.render(matrixStack, vertexConsumer, l, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f);
            VertexConsumer vertexConsumer2 = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityDecal(SKIN));
            this.field_21008.render(matrixStack, vertexConsumer2, l, OverlayTexture.packUv(0.0f, bl), 1.0f, 1.0f, 1.0f);
        } else {
            VertexConsumer vertexConsumer3 = layeredVertexConsumerStorage.getBuffer(this.field_21008.getLayer(SKIN));
            this.field_21008.render(matrixStack, vertexConsumer3, l, OverlayTexture.packUv(0.0f, bl), 1.0f, 1.0f, 1.0f);
        }
        VertexConsumer vertexConsumer3 = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEyes(EYES_TEX));
        this.field_21008.render(matrixStack, vertexConsumer3, l, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f);
        if (enderDragonEntity.field_7031 > 0) {
            float n = ((float)enderDragonEntity.field_7031 + h) / 200.0f;
            float o = 0.0f;
            if (n > 0.8f) {
                o = (n - 0.8f) / 0.2f;
            }
            Random random = new Random(432L);
            VertexConsumer vertexConsumer4 = layeredVertexConsumerStorage.getBuffer(RenderLayer.getLightning());
            matrixStack.push();
            matrixStack.translate(0.0, -1.0, -2.0);
            int p = 0;
            while ((float)p < (n + n * n) / 2.0f * 60.0f) {
                matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(random.nextFloat() * 360.0f));
                matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(random.nextFloat() * 360.0f));
                matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(random.nextFloat() * 360.0f));
                matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(random.nextFloat() * 360.0f));
                matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(random.nextFloat() * 360.0f));
                matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(random.nextFloat() * 360.0f + n * 90.0f));
                float q = random.nextFloat() * 20.0f + 5.0f + o * 10.0f;
                float r = random.nextFloat() * 2.0f + 1.0f + o * 2.0f;
                Matrix4f matrix4f = matrixStack.peek();
                int s = (int)(255.0f * (1.0f - o));
                EnderDragonEntityRenderer.method_23157(vertexConsumer4, matrix4f, s);
                EnderDragonEntityRenderer.method_23156(vertexConsumer4, matrix4f, q, r);
                EnderDragonEntityRenderer.method_23158(vertexConsumer4, matrix4f, q, r);
                EnderDragonEntityRenderer.method_23157(vertexConsumer4, matrix4f, s);
                EnderDragonEntityRenderer.method_23158(vertexConsumer4, matrix4f, q, r);
                EnderDragonEntityRenderer.method_23159(vertexConsumer4, matrix4f, q, r);
                EnderDragonEntityRenderer.method_23157(vertexConsumer4, matrix4f, s);
                EnderDragonEntityRenderer.method_23159(vertexConsumer4, matrix4f, q, r);
                EnderDragonEntityRenderer.method_23156(vertexConsumer4, matrix4f, q, r);
                ++p;
            }
            matrixStack.pop();
        }
        matrixStack.pop();
        if (enderDragonEntity.connectedCrystal != null) {
            matrixStack.push();
            float n = (float)(enderDragonEntity.connectedCrystal.getX() - MathHelper.lerp((double)h, enderDragonEntity.prevX, enderDragonEntity.getX()));
            float o = (float)(enderDragonEntity.connectedCrystal.getY() - MathHelper.lerp((double)h, enderDragonEntity.prevY, enderDragonEntity.getY()));
            float t = (float)(enderDragonEntity.connectedCrystal.getZ() - MathHelper.lerp((double)h, enderDragonEntity.prevZ, enderDragonEntity.getZ()));
            EnderDragonEntityRenderer.renderCrystalBeam(n, o + EnderCrystalEntityRenderer.method_23155(enderDragonEntity.connectedCrystal, h), t, h, enderDragonEntity.age, matrixStack, layeredVertexConsumerStorage, l);
            matrixStack.pop();
        }
        super.render(enderDragonEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
    }

    private static void method_23157(VertexConsumer vertexConsumer, Matrix4f matrix4f, int i) {
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(255, 255, 255, i).next();
        vertexConsumer.vertex(matrix4f, 0.0f, 0.0f, 0.0f).color(255, 255, 255, i).next();
    }

    private static void method_23156(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g) {
        vertexConsumer.vertex(matrix4f, -field_21007 * g, f, -0.5f * g).color(255, 0, 255, 0).next();
    }

    private static void method_23158(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g) {
        vertexConsumer.vertex(matrix4f, field_21007 * g, f, -0.5f * g).color(255, 0, 255, 0).next();
    }

    private static void method_23159(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g) {
        vertexConsumer.vertex(matrix4f, 0.0f, f, 1.0f * g).color(255, 0, 255, 0).next();
    }

    public static void renderCrystalBeam(float f, float g, float h, float i, int j, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int k) {
        float l = MathHelper.sqrt(f * f + h * h);
        float m = MathHelper.sqrt(f * f + g * g + h * h);
        matrixStack.push();
        matrixStack.translate(0.0, 2.0, 0.0);
        matrixStack.multiply(Vector3f.POSITIVE_Y.method_23626((float)(-Math.atan2(h, f)) - 1.5707964f));
        matrixStack.multiply(Vector3f.POSITIVE_X.method_23626((float)(-Math.atan2(l, g)) - 1.5707964f));
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntitySmoothCutout(CRYSTAL_BEAM_TEX));
        float n = 0.0f - ((float)j + i) * 0.01f;
        float o = MathHelper.sqrt(f * f + g * g + h * h) / 32.0f - ((float)j + i) * 0.01f;
        int p = 8;
        float q = 0.0f;
        float r = 0.75f;
        float s = 0.0f;
        Matrix4f matrix4f = matrixStack.peek();
        for (int t = 1; t <= 8; ++t) {
            float u = MathHelper.sin((float)(t % 8) * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float v = MathHelper.cos((float)(t % 8) * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float w = (float)(t % 8) / 8.0f;
            vertexConsumer.vertex(matrix4f, q * 0.2f, r * 0.2f, 0.0f).color(0, 0, 0, 255).texture(s, n).defaultOverlay(OverlayTexture.DEFAULT_UV).light(k).normal(0.0f, 1.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, q, r, m).color(255, 255, 255, 255).texture(s, o).defaultOverlay(OverlayTexture.DEFAULT_UV).light(k).normal(0.0f, 1.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, u, v, m).color(255, 255, 255, 255).texture(w, o).defaultOverlay(OverlayTexture.DEFAULT_UV).light(k).normal(0.0f, 1.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, u * 0.2f, v * 0.2f, 0.0f).color(0, 0, 0, 255).texture(w, n).defaultOverlay(OverlayTexture.DEFAULT_UV).light(k).normal(0.0f, 1.0f, 0.0f).next();
            q = u;
            r = v;
            s = w;
        }
        matrixStack.pop();
    }

    public Identifier method_3914(EnderDragonEntity enderDragonEntity) {
        return SKIN;
    }

    @Environment(value=EnvType.CLIENT)
    public static class DragonEntityModel
    extends EntityModel<EnderDragonEntity> {
        private final ModelPart head;
        private final ModelPart neck;
        private final ModelPart jaw;
        private final ModelPart body;
        private final ModelPart rearLeg;
        private final ModelPart frontLeg;
        private final ModelPart rearLegTip;
        private final ModelPart frontLegTip;
        private final ModelPart rearFoot;
        private final ModelPart frontFoot;
        private final ModelPart wing;
        private final ModelPart wingTip;
        @Nullable
        private EnderDragonEntity dragon;
        private float field_21442;

        public DragonEntityModel(float f) {
            this.textureWidth = 256;
            this.textureHeight = 256;
            float g = -16.0f;
            this.head = new ModelPart(this);
            this.head.addCuboid("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, f, 176, 44);
            this.head.addCuboid("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, f, 112, 30);
            this.head.mirror = true;
            this.head.addCuboid("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, f, 0, 0);
            this.head.addCuboid("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, f, 112, 0);
            this.head.mirror = false;
            this.head.addCuboid("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, f, 0, 0);
            this.head.addCuboid("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, f, 112, 0);
            this.jaw = new ModelPart(this);
            this.jaw.setPivot(0.0f, 4.0f, -8.0f);
            this.jaw.addCuboid("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, f, 176, 65);
            this.head.addChild(this.jaw);
            this.neck = new ModelPart(this);
            this.neck.addCuboid("box", -5.0f, -5.0f, -5.0f, 10, 10, 10, f, 192, 104);
            this.neck.addCuboid("scale", -1.0f, -9.0f, -3.0f, 2, 4, 6, f, 48, 0);
            this.body = new ModelPart(this);
            this.body.setPivot(0.0f, 4.0f, 8.0f);
            this.body.addCuboid("body", -12.0f, 0.0f, -16.0f, 24, 24, 64, f, 0, 0);
            this.body.addCuboid("scale", -1.0f, -6.0f, -10.0f, 2, 6, 12, f, 220, 53);
            this.body.addCuboid("scale", -1.0f, -6.0f, 10.0f, 2, 6, 12, f, 220, 53);
            this.body.addCuboid("scale", -1.0f, -6.0f, 30.0f, 2, 6, 12, f, 220, 53);
            this.wing = new ModelPart(this);
            this.wing.setPivot(-12.0f, 5.0f, 2.0f);
            this.wing.addCuboid("bone", -56.0f, -4.0f, -4.0f, 56, 8, 8, f, 112, 88);
            this.wing.addCuboid("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, f, -56, 88);
            this.wingTip = new ModelPart(this);
            this.wingTip.setPivot(-56.0f, 0.0f, 0.0f);
            this.wingTip.addCuboid("bone", -56.0f, -2.0f, -2.0f, 56, 4, 4, f, 112, 136);
            this.wingTip.addCuboid("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, f, -56, 144);
            this.wing.addChild(this.wingTip);
            this.frontLeg = new ModelPart(this);
            this.frontLeg.setPivot(-12.0f, 20.0f, 2.0f);
            this.frontLeg.addCuboid("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, f, 112, 104);
            this.frontLegTip = new ModelPart(this);
            this.frontLegTip.setPivot(0.0f, 20.0f, -1.0f);
            this.frontLegTip.addCuboid("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, f, 226, 138);
            this.frontLeg.addChild(this.frontLegTip);
            this.frontFoot = new ModelPart(this);
            this.frontFoot.setPivot(0.0f, 23.0f, 0.0f);
            this.frontFoot.addCuboid("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, f, 144, 104);
            this.frontLegTip.addChild(this.frontFoot);
            this.rearLeg = new ModelPart(this);
            this.rearLeg.setPivot(-16.0f, 16.0f, 42.0f);
            this.rearLeg.addCuboid("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, f, 0, 0);
            this.rearLegTip = new ModelPart(this);
            this.rearLegTip.setPivot(0.0f, 32.0f, -4.0f);
            this.rearLegTip.addCuboid("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, f, 196, 0);
            this.rearLeg.addChild(this.rearLegTip);
            this.rearFoot = new ModelPart(this);
            this.rearFoot.setPivot(0.0f, 31.0f, 4.0f);
            this.rearFoot.addCuboid("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, f, 112, 0);
            this.rearLegTip.addChild(this.rearFoot);
        }

        public void method_23620(EnderDragonEntity enderDragonEntity, float f, float g, float h) {
            this.dragon = enderDragonEntity;
            this.field_21442 = h;
        }

        public void method_23621(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k) {
        }

        @Override
        public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h) {
            float v;
            float k = 0.0625f;
            matrixStack.push();
            float l = MathHelper.lerp(this.field_21442, this.dragon.field_7019, this.dragon.field_7030);
            this.jaw.pitch = (float)(Math.sin(l * ((float)Math.PI * 2)) + 1.0) * 0.2f;
            float m = (float)(Math.sin(l * ((float)Math.PI * 2) - 1.0f) + 1.0);
            m = (m * m + m * 2.0f) * 0.05f;
            matrixStack.translate(0.0, m - 2.0f, -3.0);
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(m * 2.0f));
            float n = 0.0f;
            float o = 20.0f;
            float p = -12.0f;
            float q = 1.5f;
            double[] ds = this.dragon.method_6817(6, this.field_21442);
            float r = MathHelper.method_22860(this.dragon.method_6817(5, this.field_21442)[0] - this.dragon.method_6817(10, this.field_21442)[0]);
            float s = MathHelper.method_22860(this.dragon.method_6817(5, this.field_21442)[0] + (double)(r / 2.0f));
            float t = l * ((float)Math.PI * 2);
            for (int u = 0; u < 5; ++u) {
                double[] es = this.dragon.method_6817(5 - u, this.field_21442);
                v = (float)Math.cos((float)u * 0.45f + t) * 0.15f;
                this.neck.yaw = MathHelper.method_22860(es[0] - ds[0]) * ((float)Math.PI / 180) * 1.5f;
                this.neck.pitch = v + this.dragon.method_6823(u, ds, es) * ((float)Math.PI / 180) * 1.5f * 5.0f;
                this.neck.roll = -MathHelper.method_22860(es[0] - (double)s) * ((float)Math.PI / 180) * 1.5f;
                this.neck.pivotY = o;
                this.neck.pivotZ = p;
                this.neck.pivotX = n;
                o = (float)((double)o + Math.sin(this.neck.pitch) * 10.0);
                p = (float)((double)p - Math.cos(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                n = (float)((double)n - Math.sin(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                this.neck.render(matrixStack, vertexConsumer, 0.0625f, i, j, null);
            }
            this.head.pivotY = o;
            this.head.pivotZ = p;
            this.head.pivotX = n;
            double[] fs = this.dragon.method_6817(0, this.field_21442);
            this.head.yaw = MathHelper.method_22860(fs[0] - ds[0]) * ((float)Math.PI / 180);
            this.head.pitch = MathHelper.method_22860(this.dragon.method_6823(6, ds, fs)) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            this.head.roll = -MathHelper.method_22860(fs[0] - (double)s) * ((float)Math.PI / 180);
            this.head.render(matrixStack, vertexConsumer, 0.0625f, i, j, null);
            matrixStack.push();
            matrixStack.translate(0.0, 1.0, 0.0);
            matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(-r * 1.5f));
            matrixStack.translate(0.0, -1.0, 0.0);
            this.body.roll = 0.0f;
            this.body.render(matrixStack, vertexConsumer, 0.0625f, i, j, null);
            for (int w = 0; w < 2; ++w) {
                v = l * ((float)Math.PI * 2);
                this.wing.pitch = 0.125f - (float)Math.cos(v) * 0.2f;
                this.wing.yaw = 0.25f;
                this.wing.roll = (float)(Math.sin(v) + 0.125) * 0.8f;
                this.wingTip.roll = -((float)(Math.sin(v + 2.0f) + 0.5)) * 0.75f;
                this.rearLeg.pitch = 1.0f + m * 0.1f;
                this.rearLegTip.pitch = 0.5f + m * 0.1f;
                this.rearFoot.pitch = 0.75f + m * 0.1f;
                this.frontLeg.pitch = 1.3f + m * 0.1f;
                this.frontLegTip.pitch = -0.5f - m * 0.1f;
                this.frontFoot.pitch = 0.75f + m * 0.1f;
                this.wing.render(matrixStack, vertexConsumer, 0.0625f, i, j, null);
                this.frontLeg.render(matrixStack, vertexConsumer, 0.0625f, i, j, null);
                this.rearLeg.render(matrixStack, vertexConsumer, 0.0625f, i, j, null);
                matrixStack.scale(-1.0f, 1.0f, 1.0f);
            }
            matrixStack.pop();
            float x = -((float)Math.sin(l * ((float)Math.PI * 2))) * 0.0f;
            t = l * ((float)Math.PI * 2);
            o = 10.0f;
            p = 60.0f;
            n = 0.0f;
            ds = this.dragon.method_6817(11, this.field_21442);
            for (int y = 0; y < 12; ++y) {
                fs = this.dragon.method_6817(12 + y, this.field_21442);
                x = (float)((double)x + Math.sin((float)y * 0.45f + t) * (double)0.05f);
                this.neck.yaw = (MathHelper.method_22860(fs[0] - ds[0]) * 1.5f + 180.0f) * ((float)Math.PI / 180);
                this.neck.pitch = x + (float)(fs[1] - ds[1]) * ((float)Math.PI / 180) * 1.5f * 5.0f;
                this.neck.roll = MathHelper.method_22860(fs[0] - (double)s) * ((float)Math.PI / 180) * 1.5f;
                this.neck.pivotY = o;
                this.neck.pivotZ = p;
                this.neck.pivotX = n;
                o = (float)((double)o + Math.sin(this.neck.pitch) * 10.0);
                p = (float)((double)p - Math.cos(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                n = (float)((double)n - Math.sin(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                this.neck.render(matrixStack, vertexConsumer, 0.0625f, i, j, null);
            }
            matrixStack.pop();
        }
    }
}

