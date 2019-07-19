/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import java.util.Random;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CollisionView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public class MooshroomEntity
extends CowEntity {
    private static final TrackedData<String> TYPE = DataTracker.registerData(MooshroomEntity.class, TrackedDataHandlerRegistry.STRING);
    private StatusEffect stewEffect;
    private int stewEffectDuration;
    private UUID lightningId;

    public MooshroomEntity(EntityType<? extends MooshroomEntity> entityType, World world) {
        super((EntityType<? extends CowEntity>)entityType, world);
    }

    @Override
    public float getPathfindingFavor(BlockPos blockPos, CollisionView collisionView) {
        if (collisionView.getBlockState(blockPos.down()).getBlock() == Blocks.MYCELIUM) {
            return 10.0f;
        }
        return collisionView.getBrightness(blockPos) - 0.5f;
    }

    public static boolean method_20665(EntityType<MooshroomEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
        return iWorld.getBlockState(blockPos.down()).getBlock() == Blocks.MYCELIUM && iWorld.getLightLevel(blockPos, 0) > 8;
    }

    @Override
    public void onStruckByLightning(LightningEntity lightningEntity) {
        UUID uUID = lightningEntity.getUuid();
        if (!uUID.equals(this.lightningId)) {
            this.setType(this.getMooshroomType() == Type.RED ? Type.BROWN : Type.RED);
            this.lightningId = uUID;
            this.playSound(SoundEvents.ENTITY_MOOSHROOM_CONVERT, 2.0f, 1.0f);
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TYPE, Type.RED.name);
    }

    @Override
    public boolean interactMob(PlayerEntity playerEntity2, Hand hand) {
        ItemStack itemStack = playerEntity2.getStackInHand(hand);
        if (itemStack.getItem() == Items.BOWL && this.getBreedingAge() >= 0 && !playerEntity2.abilities.creativeMode) {
            ItemStack itemStack2;
            itemStack.decrement(1);
            boolean bl = false;
            if (this.stewEffect != null) {
                bl = true;
                itemStack2 = new ItemStack(Items.SUSPICIOUS_STEW);
                SuspiciousStewItem.addEffectToStew(itemStack2, this.stewEffect, this.stewEffectDuration);
                this.stewEffect = null;
                this.stewEffectDuration = 0;
            } else {
                itemStack2 = new ItemStack(Items.MUSHROOM_STEW);
            }
            if (itemStack.isEmpty()) {
                playerEntity2.setStackInHand(hand, itemStack2);
            } else if (!playerEntity2.inventory.insertStack(itemStack2)) {
                playerEntity2.dropItem(itemStack2, false);
            }
            SoundEvent soundEvent = bl ? SoundEvents.ENTITY_MOOSHROOM_SUSPICIOUS_MILK : SoundEvents.ENTITY_MOOSHROOM_MILK;
            this.playSound(soundEvent, 1.0f, 1.0f);
            return true;
        }
        if (itemStack.getItem() == Items.SHEARS && this.getBreedingAge() >= 0) {
            this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y + (double)(this.getHeight() / 2.0f), this.z, 0.0, 0.0, 0.0);
            if (!this.world.isClient) {
                this.remove();
                CowEntity cowEntity = EntityType.COW.create(this.world);
                cowEntity.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
                cowEntity.setHealth(this.getHealth());
                cowEntity.field_6283 = this.field_6283;
                if (this.hasCustomName()) {
                    cowEntity.setCustomName(this.getCustomName());
                }
                this.world.spawnEntity(cowEntity);
                for (int i = 0; i < 5; ++i) {
                    this.world.spawnEntity(new ItemEntity(this.world, this.x, this.y + (double)this.getHeight(), this.z, new ItemStack(this.getMooshroomType().mushroom.getBlock())));
                }
                itemStack.damage(1, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(hand));
                this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0f, 1.0f);
            }
            return true;
        }
        if (this.getMooshroomType() == Type.BROWN && itemStack.getItem().isIn(ItemTags.SMALL_FLOWERS)) {
            if (this.stewEffect != null) {
                for (int j = 0; j < 2; ++j) {
                    this.world.addParticle(ParticleTypes.SMOKE, this.x + (double)(this.random.nextFloat() / 2.0f), this.y + (double)(this.getHeight() / 2.0f), this.z + (double)(this.random.nextFloat() / 2.0f), 0.0, this.random.nextFloat() / 5.0f, 0.0);
                }
            } else {
                Pair<StatusEffect, Integer> pair = this.getStewEffectFrom(itemStack);
                if (!playerEntity2.abilities.creativeMode) {
                    itemStack.decrement(1);
                }
                for (int i = 0; i < 4; ++i) {
                    this.world.addParticle(ParticleTypes.EFFECT, this.x + (double)(this.random.nextFloat() / 2.0f), this.y + (double)(this.getHeight() / 2.0f), this.z + (double)(this.random.nextFloat() / 2.0f), 0.0, this.random.nextFloat() / 5.0f, 0.0);
                }
                this.stewEffect = pair.getLeft();
                this.stewEffectDuration = pair.getRight();
                this.playSound(SoundEvents.ENTITY_MOOSHROOM_EAT, 2.0f, 1.0f);
            }
        }
        return super.interactMob(playerEntity2, hand);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        compoundTag.putString("Type", this.getMooshroomType().name);
        if (this.stewEffect != null) {
            compoundTag.putByte("EffectId", (byte)StatusEffect.getRawId(this.stewEffect));
            compoundTag.putInt("EffectDuration", this.stewEffectDuration);
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        super.readCustomDataFromTag(compoundTag);
        this.setType(Type.fromName(compoundTag.getString("Type")));
        if (compoundTag.contains("EffectId", 1)) {
            this.stewEffect = StatusEffect.byRawId(compoundTag.getByte("EffectId"));
        }
        if (compoundTag.contains("EffectDuration", 3)) {
            this.stewEffectDuration = compoundTag.getInt("EffectDuration");
        }
    }

    private Pair<StatusEffect, Integer> getStewEffectFrom(ItemStack itemStack) {
        FlowerBlock flowerBlock = (FlowerBlock)((BlockItem)itemStack.getItem()).getBlock();
        return Pair.of(flowerBlock.getEffectInStew(), flowerBlock.getEffectInStewDuration());
    }

    private void setType(Type type) {
        this.dataTracker.set(TYPE, type.name);
    }

    public Type getMooshroomType() {
        return Type.fromName(this.dataTracker.get(MooshroomEntity.TYPE));
    }

    @Override
    public MooshroomEntity createChild(PassiveEntity passiveEntity) {
        MooshroomEntity mooshroomEntity = EntityType.MOOSHROOM.create(this.world);
        mooshroomEntity.setType(this.chooseBabyType((MooshroomEntity)passiveEntity));
        return mooshroomEntity;
    }

    private Type chooseBabyType(MooshroomEntity mooshroomEntity) {
        Type type2;
        Type type = this.getMooshroomType();
        Type type3 = type == (type2 = mooshroomEntity.getMooshroomType()) && this.random.nextInt(1024) == 0 ? (type == Type.BROWN ? Type.RED : Type.BROWN) : (this.random.nextBoolean() ? type : type2);
        return type3;
    }

    @Override
    public /* synthetic */ CowEntity createChild(PassiveEntity passiveEntity) {
        return this.createChild(passiveEntity);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity passiveEntity) {
        return this.createChild(passiveEntity);
    }

    public static enum Type {
        RED("red", Blocks.RED_MUSHROOM.getDefaultState()),
        BROWN("brown", Blocks.BROWN_MUSHROOM.getDefaultState());

        private final String name;
        private final BlockState mushroom;

        private Type(String string2, BlockState blockState) {
            this.name = string2;
            this.mushroom = blockState;
        }

        @Environment(value=EnvType.CLIENT)
        public BlockState getMushroomState() {
            return this.mushroom;
        }

        private static Type fromName(String string) {
            for (Type type : Type.values()) {
                if (!type.name.equals(string)) continue;
                return type;
            }
            return RED;
        }
    }
}

