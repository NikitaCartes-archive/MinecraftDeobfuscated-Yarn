/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.projectile.thrown;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

public class PotionEntity
extends ThrownItemEntity
implements FlyingItemEntity {
    public static final double field_30667 = 4.0;
    private static final double field_30668 = 16.0;
    public static final Predicate<LivingEntity> AFFECTED_BY_WATER = entity -> entity.hurtByWater() || entity.isOnFire();

    public PotionEntity(EntityType<? extends PotionEntity> entityType, World world) {
        super((EntityType<? extends ThrownItemEntity>)entityType, world);
    }

    public PotionEntity(World world, LivingEntity owner) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.POTION, owner, world);
    }

    public PotionEntity(World world, double x, double y, double z) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.POTION, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SPLASH_POTION;
    }

    @Override
    protected float getGravity() {
        return 0.05f;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (this.world.isClient) {
            return;
        }
        ItemStack itemStack = this.getStack();
        Potion potion = PotionUtil.getPotion(itemStack);
        List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
        boolean bl = potion == Potions.WATER && list.isEmpty();
        Direction direction = blockHitResult.getSide();
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockPos blockPos2 = blockPos.offset(direction);
        if (bl) {
            this.extinguishFire(blockPos2);
            this.extinguishFire(blockPos2.offset(direction.getOpposite()));
            for (Direction direction2 : Direction.Type.HORIZONTAL) {
                this.extinguishFire(blockPos2.offset(direction2));
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        boolean bl;
        super.onCollision(hitResult);
        if (this.world.isClient) {
            return;
        }
        ItemStack itemStack = this.getStack();
        Potion potion = PotionUtil.getPotion(itemStack);
        List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
        boolean bl2 = bl = potion == Potions.WATER && list.isEmpty();
        if (bl) {
            this.applyWater();
        } else if (!list.isEmpty()) {
            if (this.isLingering()) {
                this.applyLingeringPotion(itemStack, potion);
            } else {
                this.applySplashPotion(list, hitResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)hitResult).getEntity() : null);
            }
        }
        int i = potion.hasInstantEffect() ? WorldEvents.INSTANT_SPLASH_POTION_SPLASHED : WorldEvents.SPLASH_POTION_SPLASHED;
        this.world.syncWorldEvent(i, this.getBlockPos(), PotionUtil.getColor(itemStack));
        this.discard();
    }

    private void applyWater() {
        Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
        List<LivingEntity> list = this.world.getEntitiesByClass(LivingEntity.class, box, AFFECTED_BY_WATER);
        for (LivingEntity livingEntity : list) {
            double d = this.squaredDistanceTo(livingEntity);
            if (!(d < 16.0)) continue;
            if (livingEntity.hurtByWater()) {
                livingEntity.damage(this.getDamageSources().indirectMagic(this, this.getOwner()), 1.0f);
            }
            if (!livingEntity.isOnFire() || !livingEntity.isAlive()) continue;
            livingEntity.extinguishWithSound();
        }
        List<AxolotlEntity> list2 = this.world.getNonSpectatingEntities(AxolotlEntity.class, box);
        for (AxolotlEntity axolotlEntity : list2) {
            axolotlEntity.hydrateFromPotion();
        }
    }

    private void applySplashPotion(List<StatusEffectInstance> statusEffects, @Nullable Entity entity) {
        Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
        List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, box);
        if (!list.isEmpty()) {
            Entity entity2 = this.getEffectCause();
            for (LivingEntity livingEntity : list) {
                double d;
                if (!livingEntity.isAffectedBySplashPotions() || !((d = this.squaredDistanceTo(livingEntity)) < 16.0)) continue;
                double e = livingEntity == entity ? 1.0 : 1.0 - Math.sqrt(d) / 4.0;
                for (StatusEffectInstance statusEffectInstance : statusEffects) {
                    StatusEffect statusEffect = statusEffectInstance.getEffectType();
                    if (statusEffect.isInstant()) {
                        statusEffect.applyInstantEffect(this, this.getOwner(), livingEntity, statusEffectInstance.getAmplifier(), e);
                        continue;
                    }
                    int i2 = statusEffectInstance.mapDuration(i -> (int)(e * (double)i + 0.5));
                    StatusEffectInstance statusEffectInstance2 = new StatusEffectInstance(statusEffect, i2, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles());
                    if (statusEffectInstance2.isDurationBelow(20)) continue;
                    livingEntity.addStatusEffect(statusEffectInstance2, entity2);
                }
            }
        }
    }

    private void applyLingeringPotion(ItemStack stack, Potion potion) {
        AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, this.getX(), this.getY(), this.getZ());
        Entity entity = this.getOwner();
        if (entity instanceof LivingEntity) {
            areaEffectCloudEntity.setOwner((LivingEntity)entity);
        }
        areaEffectCloudEntity.setRadius(3.0f);
        areaEffectCloudEntity.setRadiusOnUse(-0.5f);
        areaEffectCloudEntity.setWaitTime(10);
        areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
        areaEffectCloudEntity.setPotion(potion);
        for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(stack)) {
            areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
        }
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null && nbtCompound.contains("CustomPotionColor", NbtElement.NUMBER_TYPE)) {
            areaEffectCloudEntity.setColor(nbtCompound.getInt("CustomPotionColor"));
        }
        this.world.spawnEntity(areaEffectCloudEntity);
    }

    private boolean isLingering() {
        return this.getStack().isOf(Items.LINGERING_POTION);
    }

    private void extinguishFire(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);
        if (blockState.isIn(BlockTags.FIRE)) {
            this.world.removeBlock(pos, false);
        } else if (AbstractCandleBlock.isLitCandle(blockState)) {
            AbstractCandleBlock.extinguish(null, blockState, this.world, pos);
        } else if (CampfireBlock.isLitCampfire(blockState)) {
            this.world.syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISHED, pos, 0);
            CampfireBlock.extinguish(this.getOwner(), this.world, pos, blockState);
            this.world.setBlockState(pos, (BlockState)blockState.with(CampfireBlock.LIT, false));
        }
    }
}

