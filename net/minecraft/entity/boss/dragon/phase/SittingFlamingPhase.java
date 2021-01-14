/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.AbstractSittingPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SittingFlamingPhase
extends AbstractSittingPhase {
    private int ticks;
    private int field_7052;
    private AreaEffectCloudEntity field_7051;

    public SittingFlamingPhase(EnderDragonEntity enderDragonEntity) {
        super(enderDragonEntity);
    }

    @Override
    public void clientTick() {
        ++this.ticks;
        if (this.ticks % 2 == 0 && this.ticks < 10) {
            Vec3d vec3d = this.dragon.getRotationVectorFromPhase(1.0f).normalize();
            vec3d.rotateY(-0.7853982f);
            double d = this.dragon.head.getX();
            double e = this.dragon.head.getBodyY(0.5);
            double f = this.dragon.head.getZ();
            for (int i = 0; i < 8; ++i) {
                double g = d + this.dragon.getRandom().nextGaussian() / 2.0;
                double h = e + this.dragon.getRandom().nextGaussian() / 2.0;
                double j = f + this.dragon.getRandom().nextGaussian() / 2.0;
                for (int k = 0; k < 6; ++k) {
                    this.dragon.world.addParticle(ParticleTypes.DRAGON_BREATH, g, h, j, -vec3d.x * (double)0.08f * (double)k, -vec3d.y * (double)0.6f, -vec3d.z * (double)0.08f * (double)k);
                }
                vec3d.rotateY(0.19634955f);
            }
        }
    }

    @Override
    public void serverTick() {
        ++this.ticks;
        if (this.ticks >= 200) {
            if (this.field_7052 >= 4) {
                this.dragon.getPhaseManager().setPhase(PhaseType.TAKEOFF);
            } else {
                this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
            }
        } else if (this.ticks == 10) {
            double g;
            Vec3d vec3d = new Vec3d(this.dragon.head.getX() - this.dragon.getX(), 0.0, this.dragon.head.getZ() - this.dragon.getZ()).normalize();
            float f = 5.0f;
            double d = this.dragon.head.getX() + vec3d.x * 5.0 / 2.0;
            double e = this.dragon.head.getZ() + vec3d.z * 5.0 / 2.0;
            double h = g = this.dragon.head.getBodyY(0.5);
            BlockPos.Mutable mutable = new BlockPos.Mutable(d, h, e);
            while (this.dragon.world.isAir(mutable)) {
                if ((h -= 1.0) < 0.0) {
                    h = g;
                    break;
                }
                mutable.set(d, h, e);
            }
            h = MathHelper.floor(h) + 1;
            this.field_7051 = new AreaEffectCloudEntity(this.dragon.world, d, h, e);
            this.field_7051.setOwner(this.dragon);
            this.field_7051.setRadius(5.0f);
            this.field_7051.setDuration(200);
            this.field_7051.setParticleType(ParticleTypes.DRAGON_BREATH);
            this.field_7051.addEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE));
            this.dragon.world.spawnEntity(this.field_7051);
        }
    }

    @Override
    public void beginPhase() {
        this.ticks = 0;
        ++this.field_7052;
    }

    @Override
    public void endPhase() {
        if (this.field_7051 != null) {
            this.field_7051.remove();
            this.field_7051 = null;
        }
    }

    public PhaseType<SittingFlamingPhase> getType() {
        return PhaseType.SITTING_FLAMING;
    }

    public void method_6857() {
        this.field_7052 = 0;
    }
}

