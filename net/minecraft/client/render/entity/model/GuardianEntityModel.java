/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class GuardianEntityModel
extends EntityModel<GuardianEntity> {
    private static final float[] field_17131 = new float[]{1.75f, 0.25f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 1.25f, 0.75f, 0.0f, 0.0f};
    private static final float[] field_17132 = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.25f, 1.75f, 1.25f, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f};
    private static final float[] field_17133 = new float[]{0.0f, 0.0f, 0.25f, 1.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.75f, 1.25f};
    private static final float[] field_17134 = new float[]{0.0f, 0.0f, 8.0f, -8.0f, -8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f, 8.0f, -8.0f};
    private static final float[] field_17135 = new float[]{-8.0f, -8.0f, -8.0f, -8.0f, 0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 8.0f, 8.0f, 8.0f};
    private static final float[] field_17136 = new float[]{8.0f, -8.0f, 0.0f, 0.0f, -8.0f, -8.0f, 8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f};
    private final Cuboid field_3379;
    private final Cuboid field_3381;
    private final Cuboid[] field_3380;
    private final Cuboid[] field_3378;

    public GuardianEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_3380 = new Cuboid[12];
        this.field_3379 = new Cuboid(this);
        this.field_3379.setTextureOffset(0, 0).addBox(-6.0f, 10.0f, -8.0f, 12, 12, 16);
        this.field_3379.setTextureOffset(0, 28).addBox(-8.0f, 10.0f, -6.0f, 2, 12, 12);
        this.field_3379.setTextureOffset(0, 28).addBox(6.0f, 10.0f, -6.0f, 2, 12, 12, true);
        this.field_3379.setTextureOffset(16, 40).addBox(-6.0f, 8.0f, -6.0f, 12, 2, 12);
        this.field_3379.setTextureOffset(16, 40).addBox(-6.0f, 22.0f, -6.0f, 12, 2, 12);
        for (int i = 0; i < this.field_3380.length; ++i) {
            this.field_3380[i] = new Cuboid(this, 0, 0);
            this.field_3380[i].addBox(-1.0f, -4.5f, -1.0f, 2, 9, 2);
            this.field_3379.addChild(this.field_3380[i]);
        }
        this.field_3381 = new Cuboid(this, 8, 0);
        this.field_3381.addBox(-1.0f, 15.0f, 0.0f, 2, 2, 1);
        this.field_3379.addChild(this.field_3381);
        this.field_3378 = new Cuboid[3];
        this.field_3378[0] = new Cuboid(this, 40, 0);
        this.field_3378[0].addBox(-2.0f, 14.0f, 7.0f, 4, 4, 8);
        this.field_3378[1] = new Cuboid(this, 0, 54);
        this.field_3378[1].addBox(0.0f, 14.0f, 0.0f, 3, 3, 7);
        this.field_3378[2] = new Cuboid(this);
        this.field_3378[2].setTextureOffset(41, 32).addBox(0.0f, 14.0f, 0.0f, 2, 2, 6);
        this.field_3378[2].setTextureOffset(25, 19).addBox(1.0f, 10.5f, 3.0f, 1, 9, 9);
        this.field_3379.addChild(this.field_3378[0]);
        this.field_3378[0].addChild(this.field_3378[1]);
        this.field_3378[1].addChild(this.field_3378[2]);
    }

    public void method_17082(GuardianEntity guardianEntity, float f, float g, float h, float i, float j, float k) {
        this.method_17083(guardianEntity, f, g, h, i, j, k);
        this.field_3379.render(k);
    }

    public void method_17083(GuardianEntity guardianEntity, float f, float g, float h, float i, float j, float k) {
        float l = h - (float)guardianEntity.age;
        this.field_3379.yaw = i * ((float)Math.PI / 180);
        this.field_3379.pitch = j * ((float)Math.PI / 180);
        float m = (1.0f - guardianEntity.getTailAngle(l)) * 0.55f;
        for (int n = 0; n < 12; ++n) {
            this.field_3380[n].pitch = (float)Math.PI * field_17131[n];
            this.field_3380[n].yaw = (float)Math.PI * field_17132[n];
            this.field_3380[n].roll = (float)Math.PI * field_17133[n];
            this.field_3380[n].rotationPointX = field_17134[n] * (1.0f + MathHelper.cos(h * 1.5f + (float)n) * 0.01f - m);
            this.field_3380[n].rotationPointY = 16.0f + field_17135[n] * (1.0f + MathHelper.cos(h * 1.5f + (float)n) * 0.01f - m);
            this.field_3380[n].rotationPointZ = field_17136[n] * (1.0f + MathHelper.cos(h * 1.5f + (float)n) * 0.01f - m);
        }
        this.field_3381.rotationPointZ = -8.25f;
        Entity entity = MinecraftClient.getInstance().getCameraEntity();
        if (guardianEntity.hasBeamTarget()) {
            entity = guardianEntity.getBeamTarget();
        }
        if (entity != null) {
            Vec3d vec3d = entity.getCameraPosVec(0.0f);
            Vec3d vec3d2 = guardianEntity.getCameraPosVec(0.0f);
            double d = vec3d.y - vec3d2.y;
            this.field_3381.rotationPointY = d > 0.0 ? 0.0f : 1.0f;
            Vec3d vec3d3 = guardianEntity.getRotationVec(0.0f);
            vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
            Vec3d vec3d4 = new Vec3d(vec3d2.x - vec3d.x, 0.0, vec3d2.z - vec3d.z).normalize().rotateY(1.5707964f);
            double e = vec3d3.dotProduct(vec3d4);
            this.field_3381.rotationPointX = MathHelper.sqrt((float)Math.abs(e)) * 2.0f * (float)Math.signum(e);
        }
        this.field_3381.visible = true;
        float o = guardianEntity.getSpikesExtension(l);
        this.field_3378[0].yaw = MathHelper.sin(o) * (float)Math.PI * 0.05f;
        this.field_3378[1].yaw = MathHelper.sin(o) * (float)Math.PI * 0.1f;
        this.field_3378[1].rotationPointX = -1.5f;
        this.field_3378[1].rotationPointY = 0.5f;
        this.field_3378[1].rotationPointZ = 14.0f;
        this.field_3378[2].yaw = MathHelper.sin(o) * (float)Math.PI * 0.15f;
        this.field_3378[2].rotationPointX = 0.5f;
        this.field_3378[2].rotationPointY = 0.5f;
        this.field_3378[2].rotationPointZ = 6.0f;
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17083((GuardianEntity)entity, f, g, h, i, j, k);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17082((GuardianEntity)entity, f, g, h, i, j, k);
    }
}

