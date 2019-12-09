/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EnderCrystalEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.Matrix3f;
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
    private static final RenderLayer field_21737 = RenderLayer.getEntityCutoutNoCull(SKIN);
    private static final RenderLayer field_21738 = RenderLayer.getEntityDecal(SKIN);
    private static final RenderLayer field_21739 = RenderLayer.getEyes(EYES_TEX);
    private static final RenderLayer field_21740 = RenderLayer.getEntitySmoothCutout(CRYSTAL_BEAM_TEX);
    private static final float field_21007 = (float)(Math.sqrt(3.0) / 2.0);
    private final DragonEntityModel field_21008 = new DragonEntityModel();

    public EnderDragonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.shadowSize = 0.5f;
    }

    @Override
    public void render(EnderDragonEntity enderDragonEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        float h = (float)enderDragonEntity.method_6817(7, g)[0];
        float j = (float)(enderDragonEntity.method_6817(5, g)[1] - enderDragonEntity.method_6817(10, g)[1]);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-h));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(j * 10.0f));
        matrixStack.translate(0.0, 0.0, 1.0);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(0.0, -1.501f, 0.0);
        boolean bl = enderDragonEntity.hurtTime > 0;
        this.field_21008.animateModel(enderDragonEntity, 0.0f, 0.0f, g);
        if (enderDragonEntity.field_7031 > 0) {
            float k = (float)enderDragonEntity.field_7031 / 200.0f;
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityAlpha(EXPLOSION_TEX, k));
            this.field_21008.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
            VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(field_21738);
            this.field_21008.render(matrixStack, vertexConsumer2, i, OverlayTexture.getUv(0.0f, bl), 1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            VertexConsumer vertexConsumer3 = vertexConsumerProvider.getBuffer(field_21737);
            this.field_21008.render(matrixStack, vertexConsumer3, i, OverlayTexture.getUv(0.0f, bl), 1.0f, 1.0f, 1.0f, 1.0f);
        }
        VertexConsumer vertexConsumer3 = vertexConsumerProvider.getBuffer(field_21739);
        this.field_21008.render(matrixStack, vertexConsumer3, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        if (enderDragonEntity.field_7031 > 0) {
            float l = ((float)enderDragonEntity.field_7031 + g) / 200.0f;
            float m = 0.0f;
            if (l > 0.8f) {
                m = (l - 0.8f) / 0.2f;
            }
            Random random = new Random(432L);
            VertexConsumer vertexConsumer4 = vertexConsumerProvider.getBuffer(RenderLayer.getLightning());
            matrixStack.push();
            matrixStack.translate(0.0, -1.0, -2.0);
            int n = 0;
            while ((float)n < (l + l * l) / 2.0f * 60.0f) {
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(random.nextFloat() * 360.0f));
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(random.nextFloat() * 360.0f));
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(random.nextFloat() * 360.0f));
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(random.nextFloat() * 360.0f));
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(random.nextFloat() * 360.0f));
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(random.nextFloat() * 360.0f + l * 90.0f));
                float o = random.nextFloat() * 20.0f + 5.0f + m * 10.0f;
                float p = random.nextFloat() * 2.0f + 1.0f + m * 2.0f;
                Matrix4f matrix4f = matrixStack.peek().getModel();
                int q = (int)(255.0f * (1.0f - m));
                EnderDragonEntityRenderer.method_23157(vertexConsumer4, matrix4f, q);
                EnderDragonEntityRenderer.method_23156(vertexConsumer4, matrix4f, o, p);
                EnderDragonEntityRenderer.method_23158(vertexConsumer4, matrix4f, o, p);
                EnderDragonEntityRenderer.method_23157(vertexConsumer4, matrix4f, q);
                EnderDragonEntityRenderer.method_23158(vertexConsumer4, matrix4f, o, p);
                EnderDragonEntityRenderer.method_23159(vertexConsumer4, matrix4f, o, p);
                EnderDragonEntityRenderer.method_23157(vertexConsumer4, matrix4f, q);
                EnderDragonEntityRenderer.method_23159(vertexConsumer4, matrix4f, o, p);
                EnderDragonEntityRenderer.method_23156(vertexConsumer4, matrix4f, o, p);
                ++n;
            }
            matrixStack.pop();
        }
        matrixStack.pop();
        if (enderDragonEntity.connectedCrystal != null) {
            matrixStack.push();
            float l = (float)(enderDragonEntity.connectedCrystal.getX() - MathHelper.lerp((double)g, enderDragonEntity.prevX, enderDragonEntity.getX()));
            float m = (float)(enderDragonEntity.connectedCrystal.getY() - MathHelper.lerp((double)g, enderDragonEntity.prevY, enderDragonEntity.getY()));
            float r = (float)(enderDragonEntity.connectedCrystal.getZ() - MathHelper.lerp((double)g, enderDragonEntity.prevZ, enderDragonEntity.getZ()));
            EnderDragonEntityRenderer.renderCrystalBeam(l, m + EnderCrystalEntityRenderer.method_23155(enderDragonEntity.connectedCrystal, g), r, g, enderDragonEntity.age, matrixStack, vertexConsumerProvider, i);
            matrixStack.pop();
        }
        super.render(enderDragonEntity, f, g, matrixStack, vertexConsumerProvider, i);
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

    public static void renderCrystalBeam(float f, float g, float h, float i, int j, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int k) {
        float l = MathHelper.sqrt(f * f + h * h);
        float m = MathHelper.sqrt(f * f + g * g + h * h);
        matrixStack.push();
        matrixStack.translate(0.0, 2.0, 0.0);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion((float)(-Math.atan2(h, f)) - 1.5707964f));
        matrixStack.multiply(Vector3f.POSITIVE_X.getRadialQuaternion((float)(-Math.atan2(l, g)) - 1.5707964f));
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(field_21740);
        float n = 0.0f - ((float)j + i) * 0.01f;
        float o = MathHelper.sqrt(f * f + g * g + h * h) / 32.0f - ((float)j + i) * 0.01f;
        int p = 8;
        float q = 0.0f;
        float r = 0.75f;
        float s = 0.0f;
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getModel();
        Matrix3f matrix3f = entry.getNormal();
        for (int t = 1; t <= 8; ++t) {
            float u = MathHelper.sin((float)(t % 8) * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float v = MathHelper.cos((float)(t % 8) * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float w = (float)(t % 8) / 8.0f;
            vertexConsumer.vertex(matrix4f, q * 0.2f, r * 0.2f, 0.0f).color(0, 0, 0, 255).texture(s, n).overlay(OverlayTexture.DEFAULT_UV).light(k).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, q, r, m).color(255, 255, 255, 255).texture(s, o).overlay(OverlayTexture.DEFAULT_UV).light(k).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, u, v, m).color(255, 255, 255, 255).texture(w, o).overlay(OverlayTexture.DEFAULT_UV).light(k).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
            vertexConsumer.vertex(matrix4f, u * 0.2f, v * 0.2f, 0.0f).color(0, 0, 0, 255).texture(w, n).overlay(OverlayTexture.DEFAULT_UV).light(k).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
            q = u;
            r = v;
            s = w;
        }
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(EnderDragonEntity enderDragonEntity) {
        return SKIN;
    }

    @Environment(value=EnvType.CLIENT)
    public static class DragonEntityModel
    extends EntityModel<EnderDragonEntity> {
        private final ModelPart head;
        private final ModelPart neck;
        private final ModelPart jaw;
        private final ModelPart body;
        private ModelPart wing;
        private ModelPart field_21548;
        private ModelPart field_21549;
        private ModelPart field_21550;
        private ModelPart field_21551;
        private ModelPart field_21552;
        private ModelPart field_21553;
        private ModelPart field_21554;
        private ModelPart field_21555;
        private ModelPart wingTip;
        private ModelPart frontLeg;
        private ModelPart frontLegTip;
        private ModelPart frontFoot;
        private ModelPart rearLeg;
        private ModelPart rearLegTip;
        private ModelPart rearFoot;
        @Nullable
        private EnderDragonEntity dragon;
        private float field_21442;

        public DragonEntityModel() {
            this.textureWidth = 256;
            this.textureHeight = 256;
            float f = -16.0f;
            this.head = new ModelPart(this);
            this.head.addCuboid("upperlip", -6.0f, -1.0f, -24.0f, 12, 5, 16, 0.0f, 176, 44);
            this.head.addCuboid("upperhead", -8.0f, -8.0f, -10.0f, 16, 16, 16, 0.0f, 112, 30);
            this.head.mirror = true;
            this.head.addCuboid("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, 0.0f, 0, 0);
            this.head.addCuboid("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, 0.0f, 112, 0);
            this.head.mirror = false;
            this.head.addCuboid("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, 0.0f, 0, 0);
            this.head.addCuboid("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, 0.0f, 112, 0);
            this.jaw = new ModelPart(this);
            this.jaw.setPivot(0.0f, 4.0f, -8.0f);
            this.jaw.addCuboid("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, 0.0f, 176, 65);
            this.head.addChild(this.jaw);
            this.neck = new ModelPart(this);
            this.neck.addCuboid("box", -5.0f, -5.0f, -5.0f, 10, 10, 10, 0.0f, 192, 104);
            this.neck.addCuboid("scale", -1.0f, -9.0f, -3.0f, 2, 4, 6, 0.0f, 48, 0);
            this.body = new ModelPart(this);
            this.body.setPivot(0.0f, 4.0f, 8.0f);
            this.body.addCuboid("body", -12.0f, 0.0f, -16.0f, 24, 24, 64, 0.0f, 0, 0);
            this.body.addCuboid("scale", -1.0f, -6.0f, -10.0f, 2, 6, 12, 0.0f, 220, 53);
            this.body.addCuboid("scale", -1.0f, -6.0f, 10.0f, 2, 6, 12, 0.0f, 220, 53);
            this.body.addCuboid("scale", -1.0f, -6.0f, 30.0f, 2, 6, 12, 0.0f, 220, 53);
            this.wing = new ModelPart(this);
            this.wing.mirror = true;
            this.wing.setPivot(12.0f, 5.0f, 2.0f);
            this.wing.addCuboid("bone", 0.0f, -4.0f, -4.0f, 56, 8, 8, 0.0f, 112, 88);
            this.wing.addCuboid("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 88);
            this.field_21548 = new ModelPart(this);
            this.field_21548.mirror = true;
            this.field_21548.setPivot(56.0f, 0.0f, 0.0f);
            this.field_21548.addCuboid("bone", 0.0f, -2.0f, -2.0f, 56, 4, 4, 0.0f, 112, 136);
            this.field_21548.addCuboid("skin", 0.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 144);
            this.wing.addChild(this.field_21548);
            this.field_21549 = new ModelPart(this);
            this.field_21549.setPivot(12.0f, 20.0f, 2.0f);
            this.field_21549.addCuboid("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 0.0f, 112, 104);
            this.field_21550 = new ModelPart(this);
            this.field_21550.setPivot(0.0f, 20.0f, -1.0f);
            this.field_21550.addCuboid("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 0.0f, 226, 138);
            this.field_21549.addChild(this.field_21550);
            this.field_21551 = new ModelPart(this);
            this.field_21551.setPivot(0.0f, 23.0f, 0.0f);
            this.field_21551.addCuboid("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 0.0f, 144, 104);
            this.field_21550.addChild(this.field_21551);
            this.field_21552 = new ModelPart(this);
            this.field_21552.setPivot(16.0f, 16.0f, 42.0f);
            this.field_21552.addCuboid("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0.0f, 0, 0);
            this.field_21553 = new ModelPart(this);
            this.field_21553.setPivot(0.0f, 32.0f, -4.0f);
            this.field_21553.addCuboid("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 0.0f, 196, 0);
            this.field_21552.addChild(this.field_21553);
            this.field_21554 = new ModelPart(this);
            this.field_21554.setPivot(0.0f, 31.0f, 4.0f);
            this.field_21554.addCuboid("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 0.0f, 112, 0);
            this.field_21553.addChild(this.field_21554);
            this.field_21555 = new ModelPart(this);
            this.field_21555.setPivot(-12.0f, 5.0f, 2.0f);
            this.field_21555.addCuboid("bone", -56.0f, -4.0f, -4.0f, 56, 8, 8, 0.0f, 112, 88);
            this.field_21555.addCuboid("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 88);
            this.wingTip = new ModelPart(this);
            this.wingTip.setPivot(-56.0f, 0.0f, 0.0f);
            this.wingTip.addCuboid("bone", -56.0f, -2.0f, -2.0f, 56, 4, 4, 0.0f, 112, 136);
            this.wingTip.addCuboid("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, 0.0f, -56, 144);
            this.field_21555.addChild(this.wingTip);
            this.frontLeg = new ModelPart(this);
            this.frontLeg.setPivot(-12.0f, 20.0f, 2.0f);
            this.frontLeg.addCuboid("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, 0.0f, 112, 104);
            this.frontLegTip = new ModelPart(this);
            this.frontLegTip.setPivot(0.0f, 20.0f, -1.0f);
            this.frontLegTip.addCuboid("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, 0.0f, 226, 138);
            this.frontLeg.addChild(this.frontLegTip);
            this.frontFoot = new ModelPart(this);
            this.frontFoot.setPivot(0.0f, 23.0f, 0.0f);
            this.frontFoot.addCuboid("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, 0.0f, 144, 104);
            this.frontLegTip.addChild(this.frontFoot);
            this.rearLeg = new ModelPart(this);
            this.rearLeg.setPivot(-16.0f, 16.0f, 42.0f);
            this.rearLeg.addCuboid("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, 0.0f, 0, 0);
            this.rearLegTip = new ModelPart(this);
            this.rearLegTip.setPivot(0.0f, 32.0f, -4.0f);
            this.rearLegTip.addCuboid("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, 0.0f, 196, 0);
            this.rearLeg.addChild(this.rearLegTip);
            this.rearFoot = new ModelPart(this);
            this.rearFoot.setPivot(0.0f, 31.0f, 4.0f);
            this.rearFoot.addCuboid("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, 0.0f, 112, 0);
            this.rearLegTip.addChild(this.rearFoot);
        }

        @Override
        public void animateModel(EnderDragonEntity enderDragonEntity, float f, float g, float h) {
            this.dragon = enderDragonEntity;
            this.field_21442 = h;
        }

        @Override
        public void setAngles(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j) {
        }

        @Override
        public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
            float p;
            matrices.push();
            float f = MathHelper.lerp(this.field_21442, this.dragon.field_7019, this.dragon.field_7030);
            this.jaw.pitch = (float)(Math.sin(f * ((float)Math.PI * 2)) + 1.0) * 0.2f;
            float g = (float)(Math.sin(f * ((float)Math.PI * 2) - 1.0f) + 1.0);
            g = (g * g + g * 2.0f) * 0.05f;
            matrices.translate(0.0, g - 2.0f, -3.0);
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(g * 2.0f));
            float h = 0.0f;
            float i = 20.0f;
            float j = -12.0f;
            float k = 1.5f;
            double[] ds = this.dragon.method_6817(6, this.field_21442);
            float l = MathHelper.method_22860(this.dragon.method_6817(5, this.field_21442)[0] - this.dragon.method_6817(10, this.field_21442)[0]);
            float m = MathHelper.method_22860(this.dragon.method_6817(5, this.field_21442)[0] + (double)(l / 2.0f));
            float n = f * ((float)Math.PI * 2);
            for (int o = 0; o < 5; ++o) {
                double[] es = this.dragon.method_6817(5 - o, this.field_21442);
                p = (float)Math.cos((float)o * 0.45f + n) * 0.15f;
                this.neck.yaw = MathHelper.method_22860(es[0] - ds[0]) * ((float)Math.PI / 180) * 1.5f;
                this.neck.pitch = p + this.dragon.method_6823(o, ds, es) * ((float)Math.PI / 180) * 1.5f * 5.0f;
                this.neck.roll = -MathHelper.method_22860(es[0] - (double)m) * ((float)Math.PI / 180) * 1.5f;
                this.neck.pivotY = i;
                this.neck.pivotZ = j;
                this.neck.pivotX = h;
                i = (float)((double)i + Math.sin(this.neck.pitch) * 10.0);
                j = (float)((double)j - Math.cos(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                h = (float)((double)h - Math.sin(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                this.neck.render(matrices, vertexConsumer, light, overlay);
            }
            this.head.pivotY = i;
            this.head.pivotZ = j;
            this.head.pivotX = h;
            double[] fs = this.dragon.method_6817(0, this.field_21442);
            this.head.yaw = MathHelper.method_22860(fs[0] - ds[0]) * ((float)Math.PI / 180);
            this.head.pitch = MathHelper.method_22860(this.dragon.method_6823(6, ds, fs)) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            this.head.roll = -MathHelper.method_22860(fs[0] - (double)m) * ((float)Math.PI / 180);
            this.head.render(matrices, vertexConsumer, light, overlay);
            matrices.push();
            matrices.translate(0.0, 1.0, 0.0);
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-l * 1.5f));
            matrices.translate(0.0, -1.0, 0.0);
            this.body.roll = 0.0f;
            this.body.render(matrices, vertexConsumer, light, overlay);
            float q = f * ((float)Math.PI * 2);
            this.wing.pitch = 0.125f - (float)Math.cos(q) * 0.2f;
            this.wing.yaw = -0.25f;
            this.wing.roll = -((float)(Math.sin(q) + 0.125)) * 0.8f;
            this.field_21548.roll = (float)(Math.sin(q + 2.0f) + 0.5) * 0.75f;
            this.field_21555.pitch = this.wing.pitch;
            this.field_21555.yaw = -this.wing.yaw;
            this.field_21555.roll = -this.wing.roll;
            this.wingTip.roll = -this.field_21548.roll;
            this.method_23838(matrices, vertexConsumer, light, overlay, g, this.wing, this.field_21549, this.field_21550, this.field_21551, this.field_21552, this.field_21553, this.field_21554);
            this.method_23838(matrices, vertexConsumer, light, overlay, g, this.field_21555, this.frontLeg, this.frontLegTip, this.frontFoot, this.rearLeg, this.rearLegTip, this.rearFoot);
            matrices.pop();
            p = -((float)Math.sin(f * ((float)Math.PI * 2))) * 0.0f;
            n = f * ((float)Math.PI * 2);
            i = 10.0f;
            j = 60.0f;
            h = 0.0f;
            ds = this.dragon.method_6817(11, this.field_21442);
            for (int r = 0; r < 12; ++r) {
                fs = this.dragon.method_6817(12 + r, this.field_21442);
                p = (float)((double)p + Math.sin((float)r * 0.45f + n) * (double)0.05f);
                this.neck.yaw = (MathHelper.method_22860(fs[0] - ds[0]) * 1.5f + 180.0f) * ((float)Math.PI / 180);
                this.neck.pitch = p + (float)(fs[1] - ds[1]) * ((float)Math.PI / 180) * 1.5f * 5.0f;
                this.neck.roll = MathHelper.method_22860(fs[0] - (double)m) * ((float)Math.PI / 180) * 1.5f;
                this.neck.pivotY = i;
                this.neck.pivotZ = j;
                this.neck.pivotX = h;
                i = (float)((double)i + Math.sin(this.neck.pitch) * 10.0);
                j = (float)((double)j - Math.cos(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                h = (float)((double)h - Math.sin(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                this.neck.render(matrices, vertexConsumer, light, overlay);
            }
            matrices.pop();
        }

        private void method_23838(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, ModelPart modelPart, ModelPart modelPart2, ModelPart modelPart3, ModelPart modelPart4, ModelPart modelPart5, ModelPart modelPart6, ModelPart modelPart7) {
            modelPart5.pitch = 1.0f + f * 0.1f;
            modelPart6.pitch = 0.5f + f * 0.1f;
            modelPart7.pitch = 0.75f + f * 0.1f;
            modelPart2.pitch = 1.3f + f * 0.1f;
            modelPart3.pitch = -0.5f - f * 0.1f;
            modelPart4.pitch = 0.75f + f * 0.1f;
            modelPart.render(matrixStack, vertexConsumer, i, j);
            modelPart2.render(matrixStack, vertexConsumer, i, j);
            modelPart5.render(matrixStack, vertexConsumer, i, j);
        }
    }
}

