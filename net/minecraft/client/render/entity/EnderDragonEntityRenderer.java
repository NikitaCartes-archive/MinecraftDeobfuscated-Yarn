/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EnderCrystalEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EnderDragonEntityRenderer
extends EntityRenderer<EnderDragonEntity> {
    public static final Identifier CRYSTAL_BEAM = new Identifier("textures/entity/end_crystal/end_crystal_beam.png");
    private static final Identifier EXPLOSION_TEX = new Identifier("textures/entity/enderdragon/dragon_exploding.png");
    private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon.png");
    private static final Identifier field_21006 = new Identifier("textures/entity/enderdragon/dragon_eyes.png");
    private static final float field_21007 = (float)(Math.sqrt(3.0) / 2.0);
    private final DragonEntityModel field_21008 = new DragonEntityModel(0.0f);

    public EnderDragonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.5f;
    }

    public void method_3918(EnderDragonEntity enderDragonEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
        arg.method_22903();
        float i = (float)enderDragonEntity.method_6817(7, h)[0];
        float j = (float)(enderDragonEntity.method_6817(5, h)[1] - enderDragonEntity.method_6817(10, h)[1]);
        arg.method_22907(Vector3f.field_20705.method_23214(-i, true));
        arg.method_22907(Vector3f.field_20703.method_23214(j * 10.0f, true));
        arg.method_22904(0.0, 0.0, 1.0);
        arg.method_22905(-1.0f, -1.0f, 1.0f);
        float k = 0.0625f;
        arg.method_22904(0.0, -1.501f, 0.0);
        boolean bl = enderDragonEntity.hurtTime > 0;
        int l = enderDragonEntity.getLightmapCoordinates();
        if (enderDragonEntity.field_7031 > 0) {
            float m = (float)enderDragonEntity.field_7031 / 200.0f;
            class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23020(EXPLOSION_TEX, false, true, true, m, false));
            class_4608.method_23211(lv);
            this.field_21008.method_17137(arg, lv, enderDragonEntity, 0.0625f, h, l);
            lv.method_22923();
            class_4588 lv2 = arg2.getBuffer(BlockRenderLayer.method_23020(SKIN, false, true, true, 0.1f, true));
            lv2.method_22922(class_4608.method_23210(0.0f), class_4608.method_23212(bl));
            this.field_21008.method_17137(arg, lv2, enderDragonEntity, 0.0625f, h, l);
            lv2.method_22923();
        } else {
            class_4588 lv3 = arg2.getBuffer(BlockRenderLayer.method_23019(SKIN, false, true, true));
            lv3.method_22922(class_4608.method_23210(0.0f), class_4608.method_23212(bl));
            this.field_21008.method_17137(arg, lv3, enderDragonEntity, 0.0625f, h, l);
            lv3.method_22923();
        }
        class_4588 lv3 = arg2.getBuffer(BlockRenderLayer.method_23026(field_21006));
        class_4608.method_23211(lv3);
        this.field_21008.method_17137(arg, lv3, enderDragonEntity, 0.0625f, h, l);
        lv3.method_22923();
        if (enderDragonEntity.field_7031 > 0) {
            float n = ((float)enderDragonEntity.field_7031 + h) / 200.0f;
            float o = 0.0f;
            if (n > 0.8f) {
                o = (n - 0.8f) / 0.2f;
            }
            Random random = new Random(432L);
            class_4588 lv4 = arg2.getBuffer(BlockRenderLayer.LIGHTNING);
            arg.method_22903();
            arg.method_22904(0.0, -1.0, -2.0);
            int p = 0;
            while ((float)p < (n + n * n) / 2.0f * 60.0f) {
                arg.method_22907(Vector3f.field_20703.method_23214(random.nextFloat() * 360.0f, true));
                arg.method_22907(Vector3f.field_20705.method_23214(random.nextFloat() * 360.0f, true));
                arg.method_22907(Vector3f.field_20707.method_23214(random.nextFloat() * 360.0f, true));
                arg.method_22907(Vector3f.field_20703.method_23214(random.nextFloat() * 360.0f, true));
                arg.method_22907(Vector3f.field_20705.method_23214(random.nextFloat() * 360.0f, true));
                arg.method_22907(Vector3f.field_20707.method_23214(random.nextFloat() * 360.0f + n * 90.0f, true));
                float q = random.nextFloat() * 20.0f + 5.0f + o * 10.0f;
                float r = random.nextFloat() * 2.0f + 1.0f + o * 2.0f;
                Matrix4f matrix4f = arg.method_22910();
                int s = (int)(255.0f * (1.0f - o));
                EnderDragonEntityRenderer.method_23157(lv4, matrix4f, s);
                EnderDragonEntityRenderer.method_23156(lv4, matrix4f, q, r);
                EnderDragonEntityRenderer.method_23158(lv4, matrix4f, q, r);
                EnderDragonEntityRenderer.method_23157(lv4, matrix4f, s);
                EnderDragonEntityRenderer.method_23158(lv4, matrix4f, q, r);
                EnderDragonEntityRenderer.method_23159(lv4, matrix4f, q, r);
                EnderDragonEntityRenderer.method_23157(lv4, matrix4f, s);
                EnderDragonEntityRenderer.method_23159(lv4, matrix4f, q, r);
                EnderDragonEntityRenderer.method_23156(lv4, matrix4f, q, r);
                ++p;
            }
            arg.method_22909();
        }
        arg.method_22909();
        if (enderDragonEntity.connectedCrystal != null) {
            arg.method_22903();
            float n = (float)(enderDragonEntity.connectedCrystal.x - MathHelper.lerp((double)h, enderDragonEntity.prevX, enderDragonEntity.x));
            float o = (float)(enderDragonEntity.connectedCrystal.y - MathHelper.lerp((double)h, enderDragonEntity.prevY, enderDragonEntity.y));
            float t = (float)(enderDragonEntity.connectedCrystal.z - MathHelper.lerp((double)h, enderDragonEntity.prevZ, enderDragonEntity.z));
            EnderDragonEntityRenderer.renderCrystalBeam(n, o + EnderCrystalEntityRenderer.method_23155(enderDragonEntity.connectedCrystal, h), t, h, enderDragonEntity.age, arg, arg2, l);
            arg.method_22909();
        }
        super.render(enderDragonEntity, d, e, f, g, h, arg, arg2);
    }

    private static void method_23157(class_4588 arg, Matrix4f matrix4f, int i) {
        arg.method_22918(matrix4f, 0.0f, 0.0f, 0.0f).color(255, 255, 255, i).next();
        arg.method_22918(matrix4f, 0.0f, 0.0f, 0.0f).color(255, 255, 255, i).next();
    }

    private static void method_23156(class_4588 arg, Matrix4f matrix4f, float f, float g) {
        arg.method_22918(matrix4f, -field_21007 * g, f, -0.5f * g).color(255, 0, 255, 0).next();
    }

    private static void method_23158(class_4588 arg, Matrix4f matrix4f, float f, float g) {
        arg.method_22918(matrix4f, field_21007 * g, f, -0.5f * g).color(255, 0, 255, 0).next();
    }

    private static void method_23159(class_4588 arg, Matrix4f matrix4f, float f, float g) {
        arg.method_22918(matrix4f, 0.0f, f, 1.0f * g).color(255, 0, 255, 0).next();
    }

    public static void renderCrystalBeam(float f, float g, float h, float i, int j, class_4587 arg, class_4597 arg2, int k) {
        float l = MathHelper.sqrt(f * f + h * h);
        float m = MathHelper.sqrt(f * f + g * g + h * h);
        arg.method_22903();
        arg.method_22904(0.0, 2.0, 0.0);
        arg.method_22907(Vector3f.field_20705.method_23214((float)(-Math.atan2(h, f)) - 1.5707964f, false));
        arg.method_22907(Vector3f.field_20703.method_23214((float)(-Math.atan2(l, g)) - 1.5707964f, false));
        class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23019(CRYSTAL_BEAM, false, true, true));
        class_4608.method_23211(lv);
        float n = 0.0f - ((float)j + i) * 0.01f;
        float o = MathHelper.sqrt(f * f + g * g + h * h) / 32.0f - ((float)j + i) * 0.01f;
        int p = 8;
        float q = 0.0f;
        float r = 0.75f;
        float s = 0.0f;
        Matrix4f matrix4f = arg.method_22910();
        for (int t = 1; t <= 8; ++t) {
            float u = MathHelper.sin((float)(t % 8) * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float v = MathHelper.cos((float)(t % 8) * ((float)Math.PI * 2) / 8.0f) * 0.75f;
            float w = (float)(t % 8) / 8.0f;
            lv.method_22918(matrix4f, q * 0.2f, r * 0.2f, 0.0f).color(0, 0, 0, 255).texture(s, n).method_22916(k).method_22914(0.0f, 1.0f, 0.0f).next();
            lv.method_22918(matrix4f, q, r, m).color(255, 255, 255, 255).texture(s, o).method_22916(k).method_22914(0.0f, 1.0f, 0.0f).next();
            lv.method_22918(matrix4f, u, v, m).color(255, 255, 255, 255).texture(w, o).method_22916(k).method_22914(0.0f, 1.0f, 0.0f).next();
            lv.method_22918(matrix4f, u * 0.2f, v * 0.2f, 0.0f).color(0, 0, 0, 255).texture(w, n).method_22916(k).method_22914(0.0f, 1.0f, 0.0f).next();
            q = u;
            r = v;
            s = w;
        }
        arg.method_22909();
        lv.method_22923();
    }

    public Identifier method_3914(EnderDragonEntity enderDragonEntity) {
        return SKIN;
    }

    @Environment(value=EnvType.CLIENT)
    public static class DragonEntityModel
    extends Model {
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
            this.jaw.setRotationPoint(0.0f, 4.0f, -8.0f);
            this.jaw.addCuboid("jaw", -6.0f, 0.0f, -16.0f, 12, 4, 16, f, 176, 65);
            this.head.addChild(this.jaw);
            this.neck = new ModelPart(this);
            this.neck.addCuboid("box", -5.0f, -5.0f, -5.0f, 10, 10, 10, f, 192, 104);
            this.neck.addCuboid("scale", -1.0f, -9.0f, -3.0f, 2, 4, 6, f, 48, 0);
            this.body = new ModelPart(this);
            this.body.setRotationPoint(0.0f, 4.0f, 8.0f);
            this.body.addCuboid("body", -12.0f, 0.0f, -16.0f, 24, 24, 64, f, 0, 0);
            this.body.addCuboid("scale", -1.0f, -6.0f, -10.0f, 2, 6, 12, f, 220, 53);
            this.body.addCuboid("scale", -1.0f, -6.0f, 10.0f, 2, 6, 12, f, 220, 53);
            this.body.addCuboid("scale", -1.0f, -6.0f, 30.0f, 2, 6, 12, f, 220, 53);
            this.wing = new ModelPart(this);
            this.wing.setRotationPoint(-12.0f, 5.0f, 2.0f);
            this.wing.addCuboid("bone", -56.0f, -4.0f, -4.0f, 56, 8, 8, f, 112, 88);
            this.wing.addCuboid("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, f, -56, 88);
            this.wingTip = new ModelPart(this);
            this.wingTip.setRotationPoint(-56.0f, 0.0f, 0.0f);
            this.wingTip.addCuboid("bone", -56.0f, -2.0f, -2.0f, 56, 4, 4, f, 112, 136);
            this.wingTip.addCuboid("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56, f, -56, 144);
            this.wing.addChild(this.wingTip);
            this.frontLeg = new ModelPart(this);
            this.frontLeg.setRotationPoint(-12.0f, 20.0f, 2.0f);
            this.frontLeg.addCuboid("main", -4.0f, -4.0f, -4.0f, 8, 24, 8, f, 112, 104);
            this.frontLegTip = new ModelPart(this);
            this.frontLegTip.setRotationPoint(0.0f, 20.0f, -1.0f);
            this.frontLegTip.addCuboid("main", -3.0f, -1.0f, -3.0f, 6, 24, 6, f, 226, 138);
            this.frontLeg.addChild(this.frontLegTip);
            this.frontFoot = new ModelPart(this);
            this.frontFoot.setRotationPoint(0.0f, 23.0f, 0.0f);
            this.frontFoot.addCuboid("main", -4.0f, 0.0f, -12.0f, 8, 4, 16, f, 144, 104);
            this.frontLegTip.addChild(this.frontFoot);
            this.rearLeg = new ModelPart(this);
            this.rearLeg.setRotationPoint(-16.0f, 16.0f, 42.0f);
            this.rearLeg.addCuboid("main", -8.0f, -4.0f, -8.0f, 16, 32, 16, f, 0, 0);
            this.rearLegTip = new ModelPart(this);
            this.rearLegTip.setRotationPoint(0.0f, 32.0f, -4.0f);
            this.rearLegTip.addCuboid("main", -6.0f, -2.0f, 0.0f, 12, 32, 12, f, 196, 0);
            this.rearLeg.addChild(this.rearLegTip);
            this.rearFoot = new ModelPart(this);
            this.rearFoot.setRotationPoint(0.0f, 31.0f, 4.0f);
            this.rearFoot.addCuboid("main", -9.0f, 0.0f, -20.0f, 18, 6, 24, f, 112, 0);
            this.rearLegTip.addChild(this.rearFoot);
        }

        public void method_17137(class_4587 arg, class_4588 arg2, EnderDragonEntity enderDragonEntity, float f, float g, int i) {
            float s;
            arg.method_22903();
            float h = MathHelper.lerp(g, enderDragonEntity.field_7019, enderDragonEntity.field_7030);
            this.jaw.pitch = (float)(Math.sin(h * ((float)Math.PI * 2)) + 1.0) * 0.2f;
            float j = (float)(Math.sin(h * ((float)Math.PI * 2) - 1.0f) + 1.0);
            j = (j * j + j * 2.0f) * 0.05f;
            arg.method_22904(0.0, j - 2.0f, -3.0);
            arg.method_22907(Vector3f.field_20703.method_23214(j * 2.0f, true));
            float k = 0.0f;
            float l = 20.0f;
            float m = -12.0f;
            float n = 1.5f;
            double[] ds = enderDragonEntity.method_6817(6, g);
            float o = MathHelper.method_22860(enderDragonEntity.method_6817(5, g)[0] - enderDragonEntity.method_6817(10, g)[0]);
            float p = MathHelper.method_22860(enderDragonEntity.method_6817(5, g)[0] + (double)(o / 2.0f));
            float q = h * ((float)Math.PI * 2);
            for (int r = 0; r < 5; ++r) {
                double[] es = enderDragonEntity.method_6817(5 - r, g);
                s = (float)Math.cos((float)r * 0.45f + q) * 0.15f;
                this.neck.yaw = MathHelper.method_22860(es[0] - ds[0]) * ((float)Math.PI / 180) * 1.5f;
                this.neck.pitch = s + enderDragonEntity.method_6823(r, ds, es) * ((float)Math.PI / 180) * 1.5f * 5.0f;
                this.neck.roll = -MathHelper.method_22860(es[0] - (double)p) * ((float)Math.PI / 180) * 1.5f;
                this.neck.rotationPointY = l;
                this.neck.rotationPointZ = m;
                this.neck.rotationPointX = k;
                l = (float)((double)l + Math.sin(this.neck.pitch) * 10.0);
                m = (float)((double)m - Math.cos(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                k = (float)((double)k - Math.sin(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                this.neck.method_22698(arg, arg2, f, i, null);
            }
            this.head.rotationPointY = l;
            this.head.rotationPointZ = m;
            this.head.rotationPointX = k;
            double[] fs = enderDragonEntity.method_6817(0, g);
            this.head.yaw = MathHelper.method_22860(fs[0] - ds[0]) * ((float)Math.PI / 180);
            this.head.pitch = MathHelper.method_22860(enderDragonEntity.method_6823(6, ds, fs)) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            this.head.roll = -MathHelper.method_22860(fs[0] - (double)p) * ((float)Math.PI / 180);
            this.head.method_22698(arg, arg2, f, i, null);
            arg.method_22903();
            arg.method_22904(0.0, 1.0, 0.0);
            arg.method_22907(Vector3f.field_20707.method_23214(-o * 1.5f, true));
            arg.method_22904(0.0, -1.0, 0.0);
            this.body.roll = 0.0f;
            this.body.method_22698(arg, arg2, f, i, null);
            for (int t = 0; t < 2; ++t) {
                s = h * ((float)Math.PI * 2);
                this.wing.pitch = 0.125f - (float)Math.cos(s) * 0.2f;
                this.wing.yaw = 0.25f;
                this.wing.roll = (float)(Math.sin(s) + 0.125) * 0.8f;
                this.wingTip.roll = -((float)(Math.sin(s + 2.0f) + 0.5)) * 0.75f;
                this.rearLeg.pitch = 1.0f + j * 0.1f;
                this.rearLegTip.pitch = 0.5f + j * 0.1f;
                this.rearFoot.pitch = 0.75f + j * 0.1f;
                this.frontLeg.pitch = 1.3f + j * 0.1f;
                this.frontLegTip.pitch = -0.5f - j * 0.1f;
                this.frontFoot.pitch = 0.75f + j * 0.1f;
                this.wing.method_22698(arg, arg2, f, i, null);
                this.frontLeg.method_22698(arg, arg2, f, i, null);
                this.rearLeg.method_22698(arg, arg2, f, i, null);
                arg.method_22905(-1.0f, 1.0f, 1.0f);
            }
            arg.method_22909();
            float u = -((float)Math.sin(h * ((float)Math.PI * 2))) * 0.0f;
            q = h * ((float)Math.PI * 2);
            l = 10.0f;
            m = 60.0f;
            k = 0.0f;
            ds = enderDragonEntity.method_6817(11, g);
            for (int v = 0; v < 12; ++v) {
                fs = enderDragonEntity.method_6817(12 + v, g);
                u = (float)((double)u + Math.sin((float)v * 0.45f + q) * (double)0.05f);
                this.neck.yaw = (MathHelper.method_22860(fs[0] - ds[0]) * 1.5f + 180.0f) * ((float)Math.PI / 180);
                this.neck.pitch = u + (float)(fs[1] - ds[1]) * ((float)Math.PI / 180) * 1.5f * 5.0f;
                this.neck.roll = MathHelper.method_22860(fs[0] - (double)p) * ((float)Math.PI / 180) * 1.5f;
                this.neck.rotationPointY = l;
                this.neck.rotationPointZ = m;
                this.neck.rotationPointX = k;
                l = (float)((double)l + Math.sin(this.neck.pitch) * 10.0);
                m = (float)((double)m - Math.cos(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                k = (float)((double)k - Math.sin(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
                this.neck.method_22698(arg, arg2, f, i, null);
            }
            arg.method_22909();
        }
    }
}

