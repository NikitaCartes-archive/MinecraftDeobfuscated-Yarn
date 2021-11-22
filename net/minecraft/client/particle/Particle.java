/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public abstract class Particle {
    private static final Box EMPTY_BOUNDING_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private static final double MAX_SQUARED_COLLISION_CHECK_DISTANCE = MathHelper.square(100.0);
    protected final ClientWorld world;
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
    protected float velocityMultiplier = 0.98f;
    protected boolean field_28787 = false;

    protected Particle(ClientWorld world, double x, double y, double z) {
        this.world = world;
        this.setBoundingBoxSpacing(0.2f, 0.2f);
        this.setPos(x, y, z);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.maxAge = (int)(4.0f / (this.random.nextFloat() * 0.9f + 0.1f));
    }

    public Particle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this(world, x, y, z);
        this.velocityX = velocityX + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        this.velocityY = velocityY + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        this.velocityZ = velocityZ + (Math.random() * 2.0 - 1.0) * (double)0.4f;
        double d = (Math.random() + Math.random() + 1.0) * (double)0.15f;
        double e = Math.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
        this.velocityX = this.velocityX / e * d * (double)0.4f;
        this.velocityY = this.velocityY / e * d * (double)0.4f + (double)0.1f;
        this.velocityZ = this.velocityZ / e * d * (double)0.4f;
    }

    public Particle move(float speed) {
        this.velocityX *= (double)speed;
        this.velocityY = (this.velocityY - (double)0.1f) * (double)speed + (double)0.1f;
        this.velocityZ *= (double)speed;
        return this;
    }

    public void setVelocity(double velocityX, double velocityY, double velocityZ) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }

    public Particle scale(float scale) {
        this.setBoundingBoxSpacing(0.2f * scale, 0.2f * scale);
        return this;
    }

    public void setColor(float red, float green, float blue) {
        this.colorRed = red;
        this.colorGreen = green;
        this.colorBlue = blue;
    }

    protected void setColorAlpha(float alpha) {
        this.colorAlpha = alpha;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
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
        if (this.field_28787 && this.y == this.prevPosY) {
            this.velocityX *= 1.1;
            this.velocityZ *= 1.1;
        }
        this.velocityX *= (double)this.velocityMultiplier;
        this.velocityY *= (double)this.velocityMultiplier;
        this.velocityZ *= (double)this.velocityMultiplier;
        if (this.onGround) {
            this.velocityX *= (double)0.7f;
            this.velocityZ *= (double)0.7f;
        }
    }

    public abstract void buildGeometry(VertexConsumer var1, Camera var2, float var3);

    public abstract ParticleTextureSheet getType();

    public String toString() {
        return this.getClass().getSimpleName() + ", Pos (" + this.x + "," + this.y + "," + this.z + "), RGBA (" + this.colorRed + "," + this.colorGreen + "," + this.colorBlue + "," + this.colorAlpha + "), Age " + this.age;
    }

    public void markDead() {
        this.dead = true;
    }

    protected void setBoundingBoxSpacing(float spacingXZ, float spacingY) {
        if (spacingXZ != this.spacingXZ || spacingY != this.spacingY) {
            this.spacingXZ = spacingXZ;
            this.spacingY = spacingY;
            Box box = this.getBoundingBox();
            double d = (box.minX + box.maxX - (double)spacingXZ) / 2.0;
            double e = (box.minZ + box.maxZ - (double)spacingXZ) / 2.0;
            this.setBoundingBox(new Box(d, box.minY, e, d + (double)this.spacingXZ, box.minY + (double)this.spacingY, e + (double)this.spacingXZ));
        }
    }

    public void setPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        float f = this.spacingXZ / 2.0f;
        float g = this.spacingY;
        this.setBoundingBox(new Box(x - (double)f, y, z - (double)f, x + (double)f, y + (double)g, z + (double)f));
    }

    public void move(double dx, double dy, double dz) {
        if (this.field_21507) {
            return;
        }
        double d = dx;
        double e = dy;
        double f = dz;
        if (this.collidesWithWorld && (dx != 0.0 || dy != 0.0 || dz != 0.0) && dx * dx + dy * dy + dz * dz < MAX_SQUARED_COLLISION_CHECK_DISTANCE) {
            Vec3d vec3d = Entity.adjustMovementForCollisions(null, new Vec3d(dx, dy, dz), this.getBoundingBox(), this.world, List.of());
            dx = vec3d.x;
            dy = vec3d.y;
            dz = vec3d.z;
        }
        if (dx != 0.0 || dy != 0.0 || dz != 0.0) {
            this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
            this.repositionFromBoundingBox();
        }
        if (Math.abs(e) >= (double)1.0E-5f && Math.abs(dy) < (double)1.0E-5f) {
            this.field_21507 = true;
        }
        boolean bl = this.onGround = e != dy && e < 0.0;
        if (d != dx) {
            this.velocityX = 0.0;
        }
        if (f != dz) {
            this.velocityZ = 0.0;
        }
    }

    protected void repositionFromBoundingBox() {
        Box box = this.getBoundingBox();
        this.x = (box.minX + box.maxX) / 2.0;
        this.y = box.minY;
        this.z = (box.minZ + box.maxZ) / 2.0;
    }

    protected int getBrightness(float tint) {
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        if (this.world.isChunkLoaded(blockPos)) {
            return WorldRenderer.getLightmapCoordinates(this.world, blockPos);
        }
        return 0;
    }

    public boolean isAlive() {
        return !this.dead;
    }

    public Box getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(Box boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * {@return the optional group that this particle belongs to}
     * 
     * <p>A particle group restricts the number of particles from the group that
     * can be rendered in a client world. If the particle does not have a group,
     * it is not restricted.
     */
    public Optional<ParticleGroup> getGroup() {
        return Optional.empty();
    }
}

