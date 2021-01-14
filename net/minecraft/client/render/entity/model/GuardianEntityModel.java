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
    private static final float[] SPIKE_PITCHES = new float[]{1.75f, 0.25f, 0.0f, 0.0f, 0.5f, 0.5f, 0.5f, 0.5f, 1.25f, 0.75f, 0.0f, 0.0f};
    private static final float[] SPIKE_YAWS = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.25f, 1.75f, 1.25f, 0.75f, 0.0f, 0.0f, 0.0f, 0.0f};
    private static final float[] SPIKE_ROLLS = new float[]{0.0f, 0.0f, 0.25f, 1.75f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.75f, 1.25f};
    private static final float[] SPIKE_PIVOTS_X = new float[]{0.0f, 0.0f, 8.0f, -8.0f, -8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f, 8.0f, -8.0f};
    private static final float[] SPIKE_PIVOTS_Y = new float[]{-8.0f, -8.0f, -8.0f, -8.0f, 0.0f, 0.0f, 0.0f, 0.0f, 8.0f, 8.0f, 8.0f, 8.0f};
    private static final float[] SPIKE_PIVOTS_Z = new float[]{8.0f, -8.0f, 0.0f, 0.0f, -8.0f, -8.0f, 8.0f, 8.0f, 8.0f, -8.0f, 0.0f, 0.0f};
    private final ModelPart head;
    private final ModelPart eye;
    private final ModelPart[] spikes;
    private final ModelPart[] tail;

    public GuardianEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.spikes = new ModelPart[12];
        this.head = new ModelPart(this);
        this.head.setTextureOffset(0, 0).addCuboid(-6.0f, 10.0f, -8.0f, 12.0f, 12.0f, 16.0f);
        this.head.setTextureOffset(0, 28).addCuboid(-8.0f, 10.0f, -6.0f, 2.0f, 12.0f, 12.0f);
        this.head.setTextureOffset(0, 28).addCuboid(6.0f, 10.0f, -6.0f, 2.0f, 12.0f, 12.0f, true);
        this.head.setTextureOffset(16, 40).addCuboid(-6.0f, 8.0f, -6.0f, 12.0f, 2.0f, 12.0f);
        this.head.setTextureOffset(16, 40).addCuboid(-6.0f, 22.0f, -6.0f, 12.0f, 2.0f, 12.0f);
        for (int i = 0; i < this.spikes.length; ++i) {
            this.spikes[i] = new ModelPart(this, 0, 0);
            this.spikes[i].addCuboid(-1.0f, -4.5f, -1.0f, 2.0f, 9.0f, 2.0f);
            this.head.addChild(this.spikes[i]);
        }
        this.eye = new ModelPart(this, 8, 0);
        this.eye.addCuboid(-1.0f, 15.0f, 0.0f, 2.0f, 2.0f, 1.0f);
        this.head.addChild(this.eye);
        this.tail = new ModelPart[3];
        this.tail[0] = new ModelPart(this, 40, 0);
        this.tail[0].addCuboid(-2.0f, 14.0f, 7.0f, 4.0f, 4.0f, 8.0f);
        this.tail[1] = new ModelPart(this, 0, 54);
        this.tail[1].addCuboid(0.0f, 14.0f, 0.0f, 3.0f, 3.0f, 7.0f);
        this.tail[2] = new ModelPart(this);
        this.tail[2].setTextureOffset(41, 32).addCuboid(0.0f, 14.0f, 0.0f, 2.0f, 2.0f, 6.0f);
        this.tail[2].setTextureOffset(25, 19).addCuboid(1.0f, 10.5f, 3.0f, 1.0f, 9.0f, 9.0f);
        this.head.addChild(this.tail[0]);
        this.tail[0].addChild(this.tail[1]);
        this.tail[1].addChild(this.tail[2]);
        this.updateSpikeExtensions(0.0f, 0.0f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    public void setAngles(GuardianEntity guardianEntity, float f, float g, float h, float i, float j) {
        float k = h - (float)guardianEntity.age;
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.pitch = j * ((float)Math.PI / 180);
        float l = (1.0f - guardianEntity.getSpikesExtension(k)) * 0.55f;
        this.updateSpikeExtensions(h, l);
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
        float m = guardianEntity.getTailAngle(k);
        this.tail[0].yaw = MathHelper.sin(m) * (float)Math.PI * 0.05f;
        this.tail[1].yaw = MathHelper.sin(m) * (float)Math.PI * 0.1f;
        this.tail[1].pivotX = -1.5f;
        this.tail[1].pivotY = 0.5f;
        this.tail[1].pivotZ = 14.0f;
        this.tail[2].yaw = MathHelper.sin(m) * (float)Math.PI * 0.15f;
        this.tail[2].pivotX = 0.5f;
        this.tail[2].pivotY = 0.5f;
        this.tail[2].pivotZ = 6.0f;
    }

    private void updateSpikeExtensions(float animationProgress, float extension) {
        for (int i = 0; i < 12; ++i) {
            this.spikes[i].pitch = (float)Math.PI * SPIKE_PITCHES[i];
            this.spikes[i].yaw = (float)Math.PI * SPIKE_YAWS[i];
            this.spikes[i].roll = (float)Math.PI * SPIKE_ROLLS[i];
            this.spikes[i].pivotX = SPIKE_PIVOTS_X[i] * (1.0f + MathHelper.cos(animationProgress * 1.5f + (float)i) * 0.01f - extension);
            this.spikes[i].pivotY = 16.0f + SPIKE_PIVOTS_Y[i] * (1.0f + MathHelper.cos(animationProgress * 1.5f + (float)i) * 0.01f - extension);
            this.spikes[i].pivotZ = SPIKE_PIVOTS_Z[i] * (1.0f + MathHelper.cos(animationProgress * 1.5f + (float)i) * 0.01f - extension);
        }
    }
}

