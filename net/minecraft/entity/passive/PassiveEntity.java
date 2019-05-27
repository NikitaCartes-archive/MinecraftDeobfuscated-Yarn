/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class PassiveEntity
extends MobEntityWithAi {
    private static final TrackedData<Boolean> CHILD = DataTracker.registerData(PassiveEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected int breedingAge;
    protected int forcedAge;
    protected int happyTicksRemaining;

    protected PassiveEntity(EntityType<? extends PassiveEntity> entityType, World world) {
        super((EntityType<? extends MobEntityWithAi>)entityType, world);
    }

    @Nullable
    public abstract PassiveEntity createChild(PassiveEntity var1);

    protected void onPlayerSpawnedChild(PlayerEntity playerEntity, PassiveEntity passiveEntity) {
    }

    @Override
    public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (item instanceof SpawnEggItem && ((SpawnEggItem)item).isOfSameEntityType(itemStack.getTag(), this.getType())) {
            PassiveEntity passiveEntity;
            if (!this.world.isClient && (passiveEntity = this.createChild(this)) != null) {
                passiveEntity.setBreedingAge(-24000);
                passiveEntity.setPositionAndAngles(this.x, this.y, this.z, 0.0f, 0.0f);
                this.world.spawnEntity(passiveEntity);
                if (itemStack.hasCustomName()) {
                    passiveEntity.setCustomName(itemStack.getCustomName());
                }
                this.onPlayerSpawnedChild(playerEntity, passiveEntity);
                if (!playerEntity.abilities.creativeMode) {
                    itemStack.decrement(1);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CHILD, false);
    }

    public int getBreedingAge() {
        if (this.world.isClient) {
            return this.dataTracker.get(CHILD) != false ? -1 : 1;
        }
        return this.breedingAge;
    }

    public void growUp(int i, boolean bl) {
        int j;
        int k = j = this.getBreedingAge();
        if ((j += i * 20) > 0) {
            j = 0;
        }
        int l = j - k;
        this.setBreedingAge(j);
        if (bl) {
            this.forcedAge += l;
            if (this.happyTicksRemaining == 0) {
                this.happyTicksRemaining = 40;
            }
        }
        if (this.getBreedingAge() == 0) {
            this.setBreedingAge(this.forcedAge);
        }
    }

    public void growUp(int i) {
        this.growUp(i, false);
    }

    public void setBreedingAge(int i) {
        int j = this.breedingAge;
        this.breedingAge = i;
        if (j < 0 && i >= 0 || j >= 0 && i < 0) {
            this.dataTracker.set(CHILD, i < 0);
            this.onGrowUp();
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        compoundTag.putInt("Age", this.getBreedingAge());
        compoundTag.putInt("ForcedAge", this.forcedAge);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        super.readCustomDataFromTag(compoundTag);
        this.setBreedingAge(compoundTag.getInt("Age"));
        this.forcedAge = compoundTag.getInt("ForcedAge");
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> trackedData) {
        if (CHILD.equals(trackedData)) {
            this.refreshSize();
        }
        super.onTrackedDataSet(trackedData);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.world.isClient) {
            if (this.happyTicksRemaining > 0) {
                if (this.happyTicksRemaining % 4 == 0) {
                    this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()), this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), 0.0, 0.0, 0.0);
                }
                --this.happyTicksRemaining;
            }
        } else if (this.isAlive()) {
            int i = this.getBreedingAge();
            if (i < 0) {
                this.setBreedingAge(++i);
            } else if (i > 0) {
                this.setBreedingAge(--i);
            }
        }
    }

    protected void onGrowUp() {
    }

    @Override
    public boolean isBaby() {
        return this.getBreedingAge() < 0;
    }
}

