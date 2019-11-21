/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class GuardianEntityModel
extends CompositeEntityModel<GuardianEntity> {
    private static final float[] field_17131 = new float[]{1.75f, 0.25f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 1.25f, 0.75f, 0.0f, 0.0f};
    private static final float[] field_17132 = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.25f, 1.75f, 1.25f, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f};
    private static final float[] field_17133 = new float[]{0.0f, 0.0f, 0.25f, 1.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.75f, 1.25f};
    private static final float[] field_17134 = new float[]{0.0f, 0.0f, 8.0f, -8.0f, -8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f, 8.0f, -8.0f};
    private static final float[] field_17135 = new float[]{-8.0f, -8.0f, -8.0f, -8.0f, 0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 8.0f, 8.0f, 8.0f};
    private static final float[] field_17136 = new float[]{8.0f, -8.0f, 0.0f, 0.0f, -8.0f, -8.0f, 8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f};
    private final ModelPart body;
    private final ModelPart eye;
    private final ModelPart[] field_3380;
    private final ModelPart[] field_3378;

    public GuardianEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_3380 = new ModelPart[12];
        this.body = new ModelPart(this);
        this.body.setTextureOffset(0, 0).addCuboid(-6.0f, 10.0f, -8.0f, 12.0f, 12.0f, 16.0f);
        this.body.setTextureOffset(0, 28).addCuboid(-8.0f, 10.0f, -6.0f, 2.0f, 12.0f, 12.0f);
        this.body.setTextureOffset(0, 28).addCuboid(6.0f, 10.0f, -6.0f, 2.0f, 12.0f, 12.0f, true);
        this.body.setTextureOffset(16, 40).addCuboid(-6.0f, 8.0f, -6.0f, 12.0f, 2.0f, 12.0f);
        this.body.setTextureOffset(16, 40).addCuboid(-6.0f, 22.0f, -6.0f, 12.0f, 2.0f, 12.0f);
        for (int i = 0; i < this.field_3380.length; ++i) {
            this.field_3380[i] = new ModelPart(this, 0, 0);
            this.field_3380[i].addCuboid(-1.0f, -4.5f, -1.0f, 2.0f, 9.0f, 2.0f);
            this.body.addChild(this.field_3380[i]);
        }
        this.eye = new ModelPart(this, 8, 0);
        this.eye.addCuboid(-1.0f, 15.0f, 0.0f, 2.0f, 2.0f, 1.0f);
        this.body.addChild(this.eye);
        this.field_3378 = new ModelPart[3];
        this.field_3378[0] = new ModelPart(this, 40, 0);
        this.field_3378[0].addCuboid(-2.0f, 14.0f, 7.0f, 4.0f, 4.0f, 8.0f);
        this.field_3378[1] = new ModelPart(this, 0, 54);
        this.field_3378[1].addCuboid(0.0f, 14.0f, 0.0f, 3.0f, 3.0f, 7.0f);
        this.field_3378[2] = new ModelPart(this);
        this.field_3378[2].setTextureOffset(41, 32).addCuboid(0.0f, 14.0f, 0.0f, 2.0f, 2.0f, 6.0f);
        this.field_3378[2].setTextureOffset(25, 19).addCuboid(1.0f, 10.5f, 3.0f, 1.0f, 9.0f, 9.0f);
        this.body.addChild(this.field_3378[0]);
        this.field_3378[0].addChild(this.field_3378[1]);
        this.field_3378[1].addChild(this.field_3378[2]);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void setAngles(GuardianEntity guardianEntity, float f, float g, float h, float i, float j) {
        float k = h - (float)guardianEntity.age;
        this.body.yaw = i * ((float)Math.PI / 180);
        this.body.pitch = j * ((float)Math.PI / 180);
        float l = (1.0f - guardianEntity.getTailAngle(k)) * 0.55f;
        for (int m = 0; m < 12; ++m) {
            this.field_3380[m].pitch = (float)Math.PI * field_17131[m];
            this.field_3380[m].yaw = (float)Math.PI * field_17132[m];
            this.field_3380[m].roll = (float)Math.PI * field_17133[m];
            this.field_3380[m].pivotX = field_17134[m] * (1.0f + MathHelper.cos(h * 1.5f + (float)m) * 0.01f - l);
            this.field_3380[m].pivotY = 16.0f + field_17135[m] * (1.0f + MathHelper.cos(h * 1.5f + (float)m) * 0.01f - l);
            this.field_3380[m].pivotZ = field_17136[m] * (1.0f + MathHelper.cos(h * 1.5f + (float)m) * 0.01f - l);
        }
        this.eye.pivotZ = -8.25f;
        Entity entity = MinecraftClient.getInstance().getCameraEntity();
        if (guardianEntity.hasBeamTarget()) {
            entity = guardianEntity.getBeamTarget();
        }
        if (entity != null) {
            Vec3d vec3d = entity.getCameraPosVec(0.0f);
            Vec3d vec3d2 = guardianEntity.getCameraPosVec(0.0f);
            double d = vec3d.y - vec3d2.y;
            this.eye.pivotY = d > 0.0 ? 0.0f : 1.0f;
            Vec3d vec3d3 = guardianEntity.getRotationVec(0.0f);
            vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
            Vec3d vec3d4 = new Vec3d(vec3d2.x - vec3d.x, 0.0, vec3d2.z - vec3d.z).normalize().rotateY(1.5707964f);
            double e = vec3d3.dotProduct(vec3d4);
            this.eye.pivotX = MathHelper.sqrt((float)Math.abs(e)) * 2.0f * (float)Math.signum(e);
        }
        this.eye.visible = true;
        float n = guardianEntity.getSpikesExtension(k);
        this.field_3378[0].yaw = MathHelper.sin(n) * (float)Math.PI * 0.05f;
        this.field_3378[1].yaw = MathHelper.sin(n) * (float)Math.PI * 0.1f;
        this.field_3378[1].pivotX = -1.5f;
        this.field_3378[1].pivotY = 0.5f;
        this.field_3378[1].pivotZ = 14.0f;
        this.field_3378[2].yaw = MathHelper.sin(n) * (float)Math.PI * 0.15f;
        this.field_3378[2].pivotX = 0.5f;
        this.field_3378[2].pivotY = 0.5f;
        this.field_3378[2].pivotZ = 6.0f;
    }
}

