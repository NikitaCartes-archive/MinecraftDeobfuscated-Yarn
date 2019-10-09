/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.thrown;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThrownExperienceBottleEntity
extends ThrownItemEntity {
    public ThrownExperienceBottleEntity(EntityType<? extends ThrownExperienceBottleEntity> entityType, World world) {
        super((EntityType<? extends ThrownItemEntity>)entityType, world);
    }

    public ThrownExperienceBottleEntity(World world, LivingEntity livingEntity) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.EXPERIENCE_BOTTLE, livingEntity, world);
    }

    public ThrownExperienceBottleEntity(World world, double d, double e, double f) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.EXPERIENCE_BOTTLE, d, e, f, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.EXPERIENCE_BOTTLE;
    }

    @Override
    protected float getGravity() {
        return 0.07f;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (!this.world.isClient) {
            int j;
            this.world.playLevelEvent(2002, new BlockPos(this), PotionUtil.getColor(Potions.WATER));
            for (int i = 3 + this.world.random.nextInt(5) + this.world.random.nextInt(5); i > 0; i -= j) {
                j = ExperienceOrbEntity.roundToOrbSize(i);
                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY(), this.getZ(), j));
            }
            this.remove();
        }
    }
}

