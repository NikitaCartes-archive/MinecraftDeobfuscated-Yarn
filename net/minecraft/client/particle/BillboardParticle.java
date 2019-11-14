/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public abstract class BillboardParticle
extends Particle {
    protected float scale;

    protected BillboardParticle(World world, double d, double e, double f) {
        super(world, d, e, f);
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    protected BillboardParticle(World world, double d, double e, double f, double g, double h, double i) {
        super(world, d, e, f, g, h, i);
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float f) {
        Quaternion quaternion;
        Vec3d vec3d = camera.getPos();
        float g = (float)(MathHelper.lerp((double)f, this.prevPosX, this.x) - vec3d.getX());
        float h = (float)(MathHelper.lerp((double)f, this.prevPosY, this.y) - vec3d.getY());
        float i = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.z) - vec3d.getZ());
        if (this.angle == 0.0f) {
            quaternion = camera.method_23767();
        } else {
            quaternion = new Quaternion(camera.method_23767());
            float j = MathHelper.lerp(f, this.prevAngle, this.angle);
            quaternion.hamiltonProduct(Vector3f.POSITIVE_Z.getRadialQuaternion(j));
        }
        Vector3f vector3f = new Vector3f(-1.0f, -1.0f, 0.0f);
        vector3f.method_19262(quaternion);
        Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
        float k = this.getSize(f);
        for (int l = 0; l < 4; ++l) {
            Vector3f vector3f2 = vector3fs[l];
            vector3f2.method_19262(quaternion);
            vector3f2.scale(k);
            vector3f2.add(g, h, i);
        }
        float m = this.getMinU();
        float n = this.getMaxU();
        float o = this.getMinV();
        float p = this.getMaxV();
        int q = this.getColorMultiplier(f);
        vertexConsumer.vertex(vector3fs[0].getX(), vector3fs[0].getY(), vector3fs[0].getZ()).texture(n, p).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(q).next();
        vertexConsumer.vertex(vector3fs[1].getX(), vector3fs[1].getY(), vector3fs[1].getZ()).texture(n, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(q).next();
        vertexConsumer.vertex(vector3fs[2].getX(), vector3fs[2].getY(), vector3fs[2].getZ()).texture(m, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(q).next();
        vertexConsumer.vertex(vector3fs[3].getX(), vector3fs[3].getY(), vector3fs[3].getZ()).texture(m, p).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(q).next();
    }

    public float getSize(float f) {
        return this.scale;
    }

    @Override
    public Particle scale(float f) {
        this.scale *= f;
        return super.scale(f);
    }

    protected abstract float getMinU();

    protected abstract float getMaxU();

    protected abstract float getMinV();

    protected abstract float getMaxV();
}

