/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import java.util.Random;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.ReusableStream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public abstract class Particle {
    private static final Box EMPTY_BOUNDING_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    protected final World world;
    protected double prevPosX;
    protected double prevPosY;
    protected double prevPosZ;
    protected double x;
    protected double y;
    protected double z;
    protected double velocityX;
    protected double velocityY;
    protected double velocityZ;
    private Box boundingBox = EMPTY_BOUNDING_BOX;
    protected boolean onGround;
    protected boolean collidesWithWorld = true;
    private boolean field_21507;
    protected boolean dead;
    protected float spacingXZ = 0.6f;
    protected float spacingY = 1.8f;
    protected final Random random = new Random();
    protected int age;
    protected int maxAge;
    protected float gravityStrength;
    protected float colorRed = 1.0f;
    protected float colorGreen = 1.0f;
    protected float colorBlue = 1.0f;
    protected float colorAlpha = 1.0f;
    protected float angle;
    protected float prevAngle;
    public static double cameraX;
    public static double cameraY;
    public static double cameraZ;

    protected Particle(World world, double d, double e, double f) {
        this.world = world;
        this.setBoundingBoxSpacing(0.2f, 0.2f);
        this.setPos(d, e, f);
        this.prevPosX = d;
        this.prevPosY = e;
        this.prevPosZ = f;
        this.maxAge = (int)(4.0f / (this.random.nextFloat() * 0.9f + 0.1f));
    }

    public Particle(World world, double d, double e, double f, double g, double h, double i) {
        this(world, d, e, f);
        this.velocityX = g + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        this.velocityY = h + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        this.velocityZ = i + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        float j = (float)(Math.random() + Math.random() + 1.0) * 0.15f;
        float k = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
        this.velocityX = this.velocityX / (double)k * (double)j * (double)0.4f;
        this.velocityY = this.velocityY / (double)k * (double)j * (double)0.4f + (double)0.1f;
        this.velocityZ = this.velocityZ / (double)k * (double)j * (double)0.4f;
    }

    public Particle move(float f) {
        this.velocityX *= (double)f;
        this.velocityY = (this.velocityY - (double)0.1f) * (double)f + (double)0.1f;
        this.velocityZ *= (double)f;
        return this;
    }

    public Particle scale(float f) {
        this.setBoundingBoxSpacing(0.2f * f, 0.2f * f);
        return this;
    }

    public void setColor(float f, float g, float h) {
        this.colorRed = f;
        this.colorGreen = g;
        this.colorBlue = h;
    }

    protected void setColorAlpha(float f) {
        this.colorAlpha = f;
    }

    public void setMaxAge(int i) {
        this.maxAge = i;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.velocityY -= 0.04 * (double)this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= (double)0.98f;
        this.velocityY *= (double)0.98f;
        this.velocityZ *= (double)0.98f;
        if (this.onGround) {
            this.velocityX *= (double)0.7f;
            this.velocityZ *= (double)0.7f;
        }
    }

    public abstract void buildGeometry(VertexConsumer var1, Camera var2, float var3, float var4, float var5, float var6, float var7, float var8);

    public abstract ParticleTextureSheet getType();

    public String toString() {
        return this.getClass().getSimpleName() + ", Pos (" + this.x + "," + this.y + "," + this.z + "), RGBA (" + this.colorRed + "," + this.colorGreen + "," + this.colorBlue + "," + this.colorAlpha + "), Age " + this.age;
    }

    public void markDead() {
        this.dead = true;
    }

    protected void setBoundingBoxSpacing(float f, float g) {
        if (f != this.spacingXZ || g != this.spacingY) {
            this.spacingXZ = f;
            this.spacingY = g;
            Box box = this.getBoundingBox();
            double d = (box.x1 + box.x2 - (double)f) / 2.0;
            double e = (box.z1 + box.z2 - (double)f) / 2.0;
            this.setBoundingBox(new Box(d, box.y1, e, d + (double)this.spacingXZ, box.y1 + (double)this.spacingY, e + (double)this.spacingXZ));
        }
    }

    public void setPos(double d, double e, double f) {
        this.x = d;
        this.y = e;
        this.z = f;
        float g = this.spacingXZ / 2.0f;
        float h = this.spacingY;
        this.setBoundingBox(new Box(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g));
    }

    public void move(double d, double e, double f) {
        if (this.field_21507) {
            e = 0.0;
        }
        double g = d;
        double h = e;
        double i = f;
        if (this.collidesWithWorld && (d != 0.0 || e != 0.0 || f != 0.0) && !this.field_21507) {
            Vec3d vec3d = Entity.adjustMovementForCollisions(null, new Vec3d(d, e, f), this.getBoundingBox(), this.world, EntityContext.absent(), new ReusableStream<VoxelShape>(Stream.empty()));
            d = vec3d.x;
            e = vec3d.y;
            f = vec3d.z;
        }
        if (d != 0.0 || e != 0.0 || f != 0.0) {
            this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
            this.repositionFromBoundingBox();
        }
        if (Math.abs(e) < (double)1.0E-5f) {
            this.field_21507 = true;
        }
        boolean bl = this.onGround = h != e && h < 0.0;
        if (g != d) {
            this.velocityX = 0.0;
        }
        if (i != f) {
            this.velocityZ = 0.0;
        }
    }

    protected void repositionFromBoundingBox() {
        Box box = this.getBoundingBox();
        this.x = (box.x1 + box.x2) / 2.0;
        this.y = box.y1;
        this.z = (box.z1 + box.z2) / 2.0;
    }

    protected int getColorMultiplier(float f) {
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        if (this.world.isChunkLoaded(blockPos)) {
            return this.world.getLightmapCoordinates(blockPos);
        }
        return 0;
    }

    public boolean isAlive() {
        return !this.dead;
    }

    public Box getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(Box box) {
        this.boundingBox = box;
    }
}

