/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class EmitterParticle
extends NoRenderParticle {
    private final Entity entity;
    private int emitterAge;
    private final int maxEmitterAge;
    private final ParticleEffect parameters;

    public EmitterParticle(World world, Entity entity, ParticleEffect particleEffect) {
        this(world, entity, particleEffect, 3);
    }

    public EmitterParticle(World world, Entity entity, ParticleEffect particleEffect, int i) {
        this(world, entity, particleEffect, i, entity.getVelocity());
    }

    private EmitterParticle(World world, Entity entity, ParticleEffect particleEffect, int i, Vec3d vec3d) {
        super(world, entity.getX(), entity.getBodyY(0.5), entity.getZ(), vec3d.x, vec3d.y, vec3d.z);
        this.entity = entity;
        this.maxEmitterAge = i;
        this.parameters = particleEffect;
        this.tick();
    }

    @Override
    public void tick() {
        for (int i = 0; i < 16; ++i) {
            double f;
            double e;
            double d = this.random.nextFloat() * 2.0f - 1.0f;
            if (d * d + (e = (double)(this.random.nextFloat() * 2.0f - 1.0f)) * e + (f = (double)(this.random.nextFloat() * 2.0f - 1.0f)) * f > 1.0) continue;
            double g = this.entity.offsetX(d / 4.0);
            double h = this.entity.getBodyY(0.5 + e / 4.0);
            double j = this.entity.offsetZ(f / 4.0);
            this.world.addParticle(this.parameters, false, g, h, j, d, e + 0.2, f);
        }
        ++this.emitterAge;
        if (this.emitterAge >= this.maxEmitterAge) {
            this.markDead();
        }
    }
}

