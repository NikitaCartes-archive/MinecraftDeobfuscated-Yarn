/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.FormCaravanGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LlamaEntity
extends AbstractDonkeyEntity
implements RangedAttackMob {
    private static final TrackedData<Integer> ATTR_STRENGTH = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CARPET_COLOR = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ATTR_VARIANT = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private boolean field_6999;
    @Nullable
    private LlamaEntity field_7000;
    @Nullable
    private LlamaEntity field_6997;

    public LlamaEntity(EntityType<? extends LlamaEntity> entityType, World world) {
        super((EntityType<? extends AbstractDonkeyEntity>)entityType, world);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isTrader() {
        return false;
    }

    private void setStrength(int i) {
        this.dataTracker.set(ATTR_STRENGTH, Math.max(1, Math.min(5, i)));
    }

    private void initializeStrength() {
        int i = this.random.nextFloat() < 0.04f ? 5 : 3;
        this.setStrength(1 + this.random.nextInt(i));
    }

    public int getStrength() {
        return this.dataTracker.get(ATTR_STRENGTH);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        compoundTag.putInt("Variant", this.getVariant());
        compoundTag.putInt("Strength", this.getStrength());
        if (!this.items.getInvStack(1).isEmpty()) {
            compoundTag.put("DecorItem", this.items.getInvStack(1).toTag(new CompoundTag()));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        this.setStrength(compoundTag.getInt("Strength"));
        super.readCustomDataFromTag(compoundTag);
        this.setVariant(compoundTag.getInt("Variant"));
        if (compoundTag.containsKey("DecorItem", 10)) {
            this.items.setInvStack(1, ItemStack.fromTag(compoundTag.getCompound("DecorItem")));
        }
        this.updateSaddle();
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
        this.goalSelector.add(2, new FormCaravanGoal(this, 2.1f));
        this.goalSelector.add(3, new ProjectileAttackGoal(this, 1.25, 40, 20.0f));
        this.goalSelector.add(3, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(4, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(5, new FollowParentGoal(this, 1.0));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.7));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new class_1504(this));
        this.targetSelector.add(2, new ChaseWolvesGoal(this));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(40.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTR_STRENGTH, 0);
        this.dataTracker.startTracking(CARPET_COLOR, -1);
        this.dataTracker.startTracking(ATTR_VARIANT, 0);
    }

    public int getVariant() {
        return MathHelper.clamp(this.dataTracker.get(ATTR_VARIANT), 0, 3);
    }

    public void setVariant(int i) {
        this.dataTracker.set(ATTR_VARIANT, i);
    }

    @Override
    protected int getInventorySize() {
        if (this.hasChest()) {
            return 2 + 3 * this.method_6702();
        }
        return super.getInventorySize();
    }

    @Override
    public void updatePassengerPosition(Entity entity) {
        if (!this.hasPassenger(entity)) {
            return;
        }
        float f = MathHelper.cos(this.bodyYaw * ((float)Math.PI / 180));
        float g = MathHelper.sin(this.bodyYaw * ((float)Math.PI / 180));
        float h = 0.3f;
        entity.setPosition(this.x + (double)(0.3f * g), this.y + this.getMountedHeightOffset() + entity.getHeightOffset(), this.z - (double)(0.3f * f));
    }

    @Override
    public double getMountedHeightOffset() {
        return (double)this.getHeight() * 0.67;
    }

    @Override
    public boolean canBeControlledByRider() {
        return false;
    }

    @Override
    protected boolean receiveFood(PlayerEntity playerEntity, ItemStack itemStack) {
        int i = 0;
        int j = 0;
        float f = 0.0f;
        boolean bl = false;
        Item item = itemStack.getItem();
        if (item == Items.WHEAT) {
            i = 10;
            j = 3;
            f = 2.0f;
        } else if (item == Blocks.HAY_BLOCK.asItem()) {
            i = 90;
            j = 6;
            f = 10.0f;
            if (this.isTame() && this.getBreedingAge() == 0 && this.canEat()) {
                bl = true;
                this.lovePlayer(playerEntity);
            }
        }
        if (this.getHealth() < this.getMaximumHealth() && f > 0.0f) {
            this.heal(f);
            bl = true;
        }
        if (this.isBaby() && i > 0) {
            this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()), this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0f) - (double)this.getWidth(), 0.0, 0.0, 0.0);
            if (!this.world.isClient) {
                this.growUp(i);
            }
            bl = true;
        }
        if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
            bl = true;
            if (!this.world.isClient) {
                this.addTemper(j);
            }
        }
        if (bl && !this.isSilent()) {
            this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_LLAMA_EAT, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
        return bl;
    }

    @Override
    protected boolean isImmobile() {
        return this.getHealth() <= 0.0f || this.isEatingGrass();
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag) {
        int i;
        this.initializeStrength();
        if (entityData instanceof class_1503) {
            i = ((class_1503)entityData).field_7001;
        } else {
            i = this.random.nextInt(4);
            entityData = new class_1503(i);
        }
        this.setVariant(i);
        return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
    }

    @Override
    protected SoundEvent getAngrySound() {
        return SoundEvents.ENTITY_LLAMA_ANGRY;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_LLAMA_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_LLAMA_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_LLAMA_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.ENTITY_LLAMA_STEP, 0.15f, 1.0f);
    }

    @Override
    protected void playAddChestSound() {
        this.playSound(SoundEvents.ENTITY_LLAMA_CHEST, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }

    @Override
    public void playAngrySound() {
        SoundEvent soundEvent = this.getAngrySound();
        if (soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public int method_6702() {
        return this.getStrength();
    }

    @Override
    public boolean canEquip() {
        return true;
    }

    @Override
    public boolean canEquip(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return ItemTags.CARPETS.contains(item);
    }

    @Override
    public boolean canBeSaddled() {
        return false;
    }

    @Override
    public void onInvChange(Inventory inventory) {
        DyeColor dyeColor = this.getCarpetColor();
        super.onInvChange(inventory);
        DyeColor dyeColor2 = this.getCarpetColor();
        if (this.age > 20 && dyeColor2 != null && dyeColor2 != dyeColor) {
            this.playSound(SoundEvents.ENTITY_LLAMA_SWAG, 0.5f, 1.0f);
        }
    }

    @Override
    protected void updateSaddle() {
        if (this.world.isClient) {
            return;
        }
        super.updateSaddle();
        this.setCarpetColor(LlamaEntity.getColorFromCarpet(this.items.getInvStack(1)));
    }

    private void setCarpetColor(@Nullable DyeColor dyeColor) {
        this.dataTracker.set(CARPET_COLOR, dyeColor == null ? -1 : dyeColor.getId());
    }

    @Nullable
    private static DyeColor getColorFromCarpet(ItemStack itemStack) {
        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block instanceof CarpetBlock) {
            return ((CarpetBlock)block).getColor();
        }
        return null;
    }

    @Nullable
    public DyeColor getCarpetColor() {
        int i = this.dataTracker.get(CARPET_COLOR);
        return i == -1 ? null : DyeColor.byId(i);
    }

    @Override
    public int getMaxTemper() {
        return 30;
    }

    @Override
    public boolean canBreedWith(AnimalEntity animalEntity) {
        return animalEntity != this && animalEntity instanceof LlamaEntity && this.canBreed() && ((LlamaEntity)animalEntity).canBreed();
    }

    public LlamaEntity method_6804(PassiveEntity passiveEntity) {
        LlamaEntity llamaEntity = this.createChild();
        this.setChildAttributes(passiveEntity, llamaEntity);
        LlamaEntity llamaEntity2 = (LlamaEntity)passiveEntity;
        int i = this.random.nextInt(Math.max(this.getStrength(), llamaEntity2.getStrength())) + 1;
        if (this.random.nextFloat() < 0.03f) {
            ++i;
        }
        llamaEntity.setStrength(i);
        llamaEntity.setVariant(this.random.nextBoolean() ? this.getVariant() : llamaEntity2.getVariant());
        return llamaEntity;
    }

    protected LlamaEntity createChild() {
        return EntityType.LLAMA.create(this.world);
    }

    private void spitAt(LivingEntity livingEntity) {
        LlamaSpitEntity llamaSpitEntity = new LlamaSpitEntity(this.world, this);
        double d = livingEntity.x - this.x;
        double e = livingEntity.getBoundingBox().minY + (double)(livingEntity.getHeight() / 3.0f) - llamaSpitEntity.y;
        double f = livingEntity.z - this.z;
        float g = MathHelper.sqrt(d * d + f * f) * 0.2f;
        llamaSpitEntity.setVelocity(d, e + (double)g, f, 1.5f, 10.0f);
        this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_LLAMA_SPIT, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        this.world.spawnEntity(llamaSpitEntity);
        this.field_6999 = true;
    }

    private void method_6808(boolean bl) {
        this.field_6999 = bl;
    }

    @Override
    public void handleFallDamage(float f, float g) {
        BlockState blockState;
        int i = MathHelper.ceil((f * 0.5f - 3.0f) * g);
        if (i <= 0) {
            return;
        }
        if (f >= 6.0f) {
            this.damage(DamageSource.FALL, i);
            if (this.hasPassengers()) {
                for (Entity entity : this.getPassengersDeep()) {
                    entity.damage(DamageSource.FALL, i);
                }
            }
        }
        if (!(blockState = this.world.getBlockState(new BlockPos(this.x, this.y - 0.2 - (double)this.prevYaw, this.z))).isAir() && !this.isSilent()) {
            BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
            this.world.playSound(null, this.x, this.y, this.z, blockSoundGroup.getStepSound(), this.getSoundCategory(), blockSoundGroup.getVolume() * 0.5f, blockSoundGroup.getPitch() * 0.75f);
        }
    }

    public void method_6797() {
        if (this.field_7000 != null) {
            this.field_7000.field_6997 = null;
        }
        this.field_7000 = null;
    }

    public void method_6791(LlamaEntity llamaEntity) {
        this.field_7000 = llamaEntity;
        this.field_7000.field_6997 = this;
    }

    public boolean method_6793() {
        return this.field_6997 != null;
    }

    public boolean isFollowing() {
        return this.field_7000 != null;
    }

    @Nullable
    public LlamaEntity getFollowing() {
        return this.field_7000;
    }

    @Override
    protected double getRunFromLeashSpeed() {
        return 2.0;
    }

    @Override
    protected void walkToParent() {
        if (!this.isFollowing() && this.isBaby()) {
            super.walkToParent();
        }
    }

    @Override
    public boolean eatsGrass() {
        return false;
    }

    @Override
    public void attack(LivingEntity livingEntity, float f) {
        this.spitAt(livingEntity);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity passiveEntity) {
        return this.method_6804(passiveEntity);
    }

    static class ChaseWolvesGoal
    extends FollowTargetGoal<WolfEntity> {
        public ChaseWolvesGoal(LlamaEntity llamaEntity) {
            super(llamaEntity, WolfEntity.class, 16, false, true, livingEntity -> !((WolfEntity)livingEntity).isTamed());
        }

        @Override
        protected double getFollowRange() {
            return super.getFollowRange() * 0.25;
        }
    }

    static class class_1504
    extends RevengeGoal {
        public class_1504(LlamaEntity llamaEntity) {
            super(llamaEntity, new Class[0]);
        }

        @Override
        public boolean shouldContinue() {
            LlamaEntity llamaEntity;
            if (this.mob instanceof LlamaEntity && (llamaEntity = (LlamaEntity)this.mob).field_6999) {
                llamaEntity.method_6808(false);
                return false;
            }
            return super.shouldContinue();
        }
    }

    static class class_1503
    extends PassiveEntity._1 {
        public final int field_7001;

        private class_1503(int i) {
            this.field_7001 = i;
        }
    }
}

