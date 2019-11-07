/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class ItemPickupParticle
extends Particle {
    private final BufferBuilderStorage field_20944;
    private final Entity field_3823;
    private final Entity field_3821;
    private int field_3826;
    private final EntityRenderDispatcher field_3824;

    public ItemPickupParticle(EntityRenderDispatcher entityRenderDispatcher, BufferBuilderStorage bufferBuilderStorage, World world, Entity entity, Entity entity2) {
        this(entityRenderDispatcher, bufferBuilderStorage, world, entity, entity2, entity.getVelocity());
    }

    private ItemPickupParticle(EntityRenderDispatcher entityRenderDispatcher, BufferBuilderStorage bufferBuilderStorage, World world, Entity entity, Entity entity2, Vec3d vec3d) {
        super(world, entity.getX(), entity.getY(), entity.getZ(), vec3d.x, vec3d.y, vec3d.z);
        this.field_20944 = bufferBuilderStorage;
        this.field_3823 = entity;
        this.field_3821 = entity2;
        this.field_3824 = entityRenderDispatcher;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float f) {
        float g = ((float)this.field_3826 + f) / 3.0f;
        g *= g;
        double d = MathHelper.lerp((double)f, this.field_3821.prevRenderX, this.field_3821.getX());
        double e = MathHelper.lerp((double)f, this.field_3821.prevRenderY, this.field_3821.getY()) + 0.5;
        double h = MathHelper.lerp((double)f, this.field_3821.prevRenderZ, this.field_3821.getZ());
        double i = MathHelper.lerp((double)g, this.field_3823.getX(), d);
        double j = MathHelper.lerp((double)g, this.field_3823.getY(), e);
        double k = MathHelper.lerp((double)g, this.field_3823.getZ(), h);
        VertexConsumerProvider.Immediate immediate = this.field_20944.getEntityVertexConsumers();
        Vec3d vec3d = camera.getPos();
        this.field_3824.render(this.field_3823, i - vec3d.getX(), j - vec3d.getY(), k - vec3d.getZ(), this.field_3823.yaw, f, new MatrixStack(), immediate, EntityRenderDispatcher.method_23839(this.field_3823));
        immediate.draw();
    }

    @Override
    public void tick() {
        ++this.field_3826;
        if (this.field_3826 == 3) {
            this.markDead();
        }
    }
}

