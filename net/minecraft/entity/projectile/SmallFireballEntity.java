/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.projectile;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SmallFireballEntity
extends AbstractFireballEntity {
    public SmallFireballEntity(EntityType<? extends SmallFireballEntity> entityType, World world) {
        super((EntityType<? extends AbstractFireballEntity>)entityType, world);
    }

    public SmallFireballEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
        super((EntityType<? extends AbstractFireballEntity>)EntityType.SMALL_FIREBALL, livingEntity, d, e, f, world);
    }

    public SmallFireballEntity(World world, double d, double e, double f, double g, double h, double i) {
        super((EntityType<? extends AbstractFireballEntity>)EntityType.SMALL_FIREBALL, d, e, f, g, h, i, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (!this.world.isClient) {
            BlockHitResult blockHitResult;
            BlockPos blockPos;
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult)hitResult).getEntity();
                if (!entity.isFireImmune()) {
                    entity.setOnFireFor(5);
                    boolean bl = entity.damage(DamageSource.explosiveProjectile(this, this.owner), 5.0f);
                    if (bl) {
                        this.dealDamage(this.owner, entity);
                    }
                }
            } else if ((this.owner == null || !(this.owner instanceof MobEntity) || this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) && this.world.isAir(blockPos = (blockHitResult = (BlockHitResult)hitResult).getBlockPos().offset(blockHitResult.getSide()))) {
                this.world.setBlockState(blockPos, Blocks.FIRE.getDefaultState());
            }
            this.remove();
        }
    }

    @Override
    public boolean collides() {
        return false;
    }

    @Override
    public boolean damage(DamageSource damageSource, float f) {
        return false;
    }
}

