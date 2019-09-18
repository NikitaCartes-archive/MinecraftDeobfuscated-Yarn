/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DragonEntityModel
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
    private float delta;

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

    public void method_17136(EnderDragonEntity enderDragonEntity, float f, float g, float h) {
        this.delta = h;
    }

    public void method_17137(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k) {
        float v;
        RenderSystem.pushMatrix();
        float l = MathHelper.lerp(this.delta, enderDragonEntity.field_7019, enderDragonEntity.field_7030);
        this.jaw.pitch = (float)(Math.sin(l * ((float)Math.PI * 2)) + 1.0) * 0.2f;
        float m = (float)(Math.sin(l * ((float)Math.PI * 2) - 1.0f) + 1.0);
        m = (m * m + m * 2.0f) * 0.05f;
        RenderSystem.translatef(0.0f, m - 2.0f, -3.0f);
        RenderSystem.rotatef(m * 2.0f, 1.0f, 0.0f, 0.0f);
        float n = 0.0f;
        float o = 20.0f;
        float p = -12.0f;
        float q = 1.5f;
        double[] ds = enderDragonEntity.method_6817(6, this.delta);
        float r = this.updateRotations(enderDragonEntity.method_6817(5, this.delta)[0] - enderDragonEntity.method_6817(10, this.delta)[0]);
        float s = this.updateRotations(enderDragonEntity.method_6817(5, this.delta)[0] + (double)(r / 2.0f));
        float t = l * ((float)Math.PI * 2);
        for (int u = 0; u < 5; ++u) {
            double[] es = enderDragonEntity.method_6817(5 - u, this.delta);
            v = (float)Math.cos((float)u * 0.45f + t) * 0.15f;
            this.neck.yaw = this.updateRotations(es[0] - ds[0]) * ((float)Math.PI / 180) * 1.5f;
            this.neck.pitch = v + enderDragonEntity.method_6823(u, ds, es) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            this.neck.roll = -this.updateRotations(es[0] - (double)s) * ((float)Math.PI / 180) * 1.5f;
            this.neck.rotationPointY = o;
            this.neck.rotationPointZ = p;
            this.neck.rotationPointX = n;
            o = (float)((double)o + Math.sin(this.neck.pitch) * 10.0);
            p = (float)((double)p - Math.cos(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
            n = (float)((double)n - Math.sin(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
            this.neck.render(k);
        }
        this.head.rotationPointY = o;
        this.head.rotationPointZ = p;
        this.head.rotationPointX = n;
        double[] fs = enderDragonEntity.method_6817(0, this.delta);
        this.head.yaw = this.updateRotations(fs[0] - ds[0]) * ((float)Math.PI / 180);
        this.head.pitch = this.updateRotations(enderDragonEntity.method_6823(6, ds, fs)) * ((float)Math.PI / 180) * 1.5f * 5.0f;
        this.head.roll = -this.updateRotations(fs[0] - (double)s) * ((float)Math.PI / 180);
        this.head.render(k);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(-r * 1.5f, 0.0f, 0.0f, 1.0f);
        RenderSystem.translatef(0.0f, -1.0f, 0.0f);
        this.body.roll = 0.0f;
        this.body.render(k);
        for (int w = 0; w < 2; ++w) {
            RenderSystem.enableCull();
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
            this.wing.render(k);
            this.frontLeg.render(k);
            this.rearLeg.render(k);
            RenderSystem.scalef(-1.0f, 1.0f, 1.0f);
            if (w != 0) continue;
            RenderSystem.cullFace(GlStateManager.FaceSides.FRONT);
        }
        RenderSystem.popMatrix();
        RenderSystem.cullFace(GlStateManager.FaceSides.BACK);
        RenderSystem.disableCull();
        float x = -((float)Math.sin(l * ((float)Math.PI * 2))) * 0.0f;
        t = l * ((float)Math.PI * 2);
        o = 10.0f;
        p = 60.0f;
        n = 0.0f;
        ds = enderDragonEntity.method_6817(11, this.delta);
        for (int y = 0; y < 12; ++y) {
            fs = enderDragonEntity.method_6817(12 + y, this.delta);
            x = (float)((double)x + Math.sin((float)y * 0.45f + t) * (double)0.05f);
            this.neck.yaw = (this.updateRotations(fs[0] - ds[0]) * 1.5f + 180.0f) * ((float)Math.PI / 180);
            this.neck.pitch = x + (float)(fs[1] - ds[1]) * ((float)Math.PI / 180) * 1.5f * 5.0f;
            this.neck.roll = this.updateRotations(fs[0] - (double)s) * ((float)Math.PI / 180) * 1.5f;
            this.neck.rotationPointY = o;
            this.neck.rotationPointZ = p;
            this.neck.rotationPointX = n;
            o = (float)((double)o + Math.sin(this.neck.pitch) * 10.0);
            p = (float)((double)p - Math.cos(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
            n = (float)((double)n - Math.sin(this.neck.yaw) * Math.cos(this.neck.pitch) * 10.0);
            this.neck.render(k);
        }
        RenderSystem.popMatrix();
    }

    private float updateRotations(double d) {
        while (d >= 180.0) {
            d -= 360.0;
        }
        while (d < -180.0) {
            d += 360.0;
        }
        return (float)d;
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17137((EnderDragonEntity)entity, f, g, h, i, j, k);
    }
}

