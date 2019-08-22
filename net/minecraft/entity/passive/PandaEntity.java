/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PandaEntity
extends AnimalEntity {
    private static final TrackedData<Integer> ASK_FOR_BAMBOO_TICKS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SNEEZE_PROGRESS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> EATING_TICKS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> MAIN_GENE = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Byte> HIDDEN_GENE = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Byte> PANDA_FLAGS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
    private boolean shouldGetRevenge;
    private boolean shouldAttack;
    public int playingTicks;
    private Vec3d playingJump;
    private float scaredAnimationProgress;
    private float lastScaredAnimationProgress;
    private float lieOnBackAnimationProgress;
    private float lastLieOnBackAnimationProgress;
    private float rollOverAnimationProgress;
    private float lastRollOverAnimationProgress;
    private static final Predicate<ItemEntity> IS_FOOD = itemEntity -> {
        Item item = itemEntity.getStack().getItem();
        return (item == Blocks.BAMBOO.asItem() || item == Blocks.CAKE.asItem()) && itemEntity.isAlive() && !itemEntity.cannotPickup();
    };

    public PandaEntity(EntityType<? extends PandaEntity> entityType, World world) {
        super((EntityType<? extends AnimalEntity>)entityType, world);
        this.moveControl = new PandaMoveControl(this);
        if (!this.isBaby()) {
            this.setCanPickUpLoot(true);
        }
    }

    @Override
    public boolean canPickUp(ItemStack itemStack) {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
        if (!this.getEquippedStack(equipmentSlot).isEmpty()) {
            return false;
        }
        return equipmentSlot == EquipmentSlot.MAINHAND && super.canPickUp(itemStack);
    }

    public int getAskForBambooTicks() {
        return this.dataTracker.get(ASK_FOR_BAMBOO_TICKS);
    }

    public void setAskForBambooTicks(int i) {
        this.dataTracker.set(ASK_FOR_BAMBOO_TICKS, i);
    }

    public boolean isSneezing() {
        return this.hasPandaFlag(2);
    }

    public boolean isScared() {
        return this.hasPandaFlag(8);
    }

    public void setScared(boolean bl) {
        this.setPandaFlag(8, bl);
    }

    public boolean isLyingOnBack() {
        return this.hasPandaFlag(16);
    }

    public void setLyingOnBack(boolean bl) {
        this.setPandaFlag(16, bl);
    }

    public boolean isEating() {
        return this.dataTracker.get(EATING_TICKS) > 0;
    }

    public void setEating(boolean bl) {
        this.dataTracker.set(EATING_TICKS, bl ? 1 : 0);
    }

    private int getEatingTicks() {
        return this.dataTracker.get(EATING_TICKS);
    }

    private void setEatingTicks(int i) {
        this.dataTracker.set(EATING_TICKS, i);
    }

    public void setSneezing(boolean bl) {
        this.setPandaFlag(2, bl);
        if (!bl) {
            this.setSneezeProgress(0);
        }
    }

    public int getSneezeProgress() {
        return this.dataTracker.get(SNEEZE_PROGRESS);
    }

    public void setSneezeProgress(int i) {
        this.dataTracker.set(SNEEZE_PROGRESS, i);
    }

    public Gene getMainGene() {
        return Gene.byId(this.dataTracker.get(MAIN_GENE).byteValue());
    }

    public void setMainGene(Gene gene) {
        if (gene.getId() > 6) {
            gene = Gene.createRandom(this.random);
        }
        this.dataTracker.set(MAIN_GENE, (byte)gene.getId());
    }

    public Gene getHiddenGene() {
        return Gene.byId(this.dataTracker.get(HIDDEN_GENE).byteValue());
    }

    public void setHiddenGene(Gene gene) {
        if (gene.getId() > 6) {
            gene = Gene.createRandom(this.random);
        }
        this.dataTracker.set(HIDDEN_GENE, (byte)gene.getId());
    }

    public boolean isPlaying() {
        return this.hasPandaFlag(4);
    }

    public void setPlaying(boolean bl) {
        this.setPandaFlag(4, bl);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ASK_FOR_BAMBOO_TICKS, 0);
        this.dataTracker.startTracking(SNEEZE_PROGRESS, 0);
        this.dataTracker.startTracking(MAIN_GENE, (byte)0);
        this.dataTracker.startTracking(HIDDEN_GENE, (byte)0);
        this.dataTracker.startTracking(PANDA_FLAGS, (byte)0);
        this.dataTracker.startTracking(EATING_TICKS, 0);
    }

    private boolean hasPandaFlag(int i) {
        return (this.dataTracker.get(PANDA_FLAGS) & i) != 0;
    }

    private void setPandaFlag(int i, boolean bl) {
        byte b = this.dataTracker.get(PANDA_FLAGS);
        if (bl) {
            this.dataTracker.set(PANDA_FLAGS, (byte)(b | i));
        } else {
            this.dataTracker.set(PANDA_FLAGS, (byte)(b & ~i));
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        compoundTag.putString("MainGene", this.getMainGene().getName());
        compoundTag.putString("HiddenGene", this.getHiddenGene().getName());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        super.readCustomDataFromTag(compoundTag);
        this.setMainGene(Gene.byName(compoundTag.getString("MainGene")));
        this.setHiddenGene(Gene.byName(compoundTag.getString("HiddenGene")));
    }

    @Override
    @Nullable
    public PassiveEntity createChild(PassiveEntity passiveEntity) {
        PandaEntity pandaEntity = EntityType.PANDA.create(this.world);
        if (passiveEntity instanceof PandaEntity) {
            pandaEntity.initGenes(this, (PandaEntity)passiveEntity);
        }
        pandaEntity.resetAttributes();
        return pandaEntity;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new ExtinguishFireGoal(this, 2.0));
        this.goalSelector.add(2, new PandaMateGoal(this, 1.0));
        this.goalSelector.add(3, new AttackGoal(this, (double)1.2f, true));
        this.goalSelector.add(4, new TemptGoal((MobEntityWithAi)this, 1.0, Ingredient.ofItems(Blocks.BAMBOO.asItem()), false));
        this.goalSelector.add(6, new PandaFleeGoal<PlayerEntity>(this, PlayerEntity.class, 8.0f, 2.0, 2.0));
        this.goalSelector.add(6, new PandaFleeGoal<HostileEntity>(this, HostileEntity.class, 4.0f, 2.0, 2.0));
        this.goalSelector.add(7, new PickUpFoodGoal());
        this.goalSelector.add(8, new LieOnBackGoal(this));
        this.goalSelector.add(8, new SneezeGoal(this));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.goalSelector.add(12, new PlayGoal(this));
        this.goalSelector.add(13, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(14, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new PandaRevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.15f);
        this.getAttributes().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
    }

    public Gene getProductGene() {
        return Gene.getProductGene(this.getMainGene(), this.getHiddenGene());
    }

    public boolean isLazy() {
        return this.getProductGene() == Gene.LAZY;
    }

    public boolean isWorried() {
        return this.getProductGene() == Gene.WORRIED;
    }

    public boolean isPlayful() {
        return this.getProductGene() == Gene.PLAYFUL;
    }

    public boolean isWeak() {
        return this.getProductGene() == Gene.WEAK;
    }

    @Override
    public boolean isAttacking() {
        return this.getProductGene() == Gene.AGGRESSIVE;
    }

    @Override
    public boolean canBeLeashedBy(PlayerEntity playerEntity) {
        return false;
    }

    @Override
    public boolean tryAttack(Entity entity) {
        this.playSound(SoundEvents.ENTITY_PANDA_BITE, 1.0f, 1.0f);
        if (!this.isAttacking()) {
            this.shouldAttack = true;
        }
        return super.tryAttack(entity);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isWorried()) {
            if (this.world.isThundering() && !this.isInsideWater()) {
                this.setScared(true);
                this.setEating(false);
            } else if (!this.isEating()) {
                this.setScared(false);
            }
        }
        if (this.getTarget() == null) {
            this.shouldGetRevenge = false;
            this.shouldAttack = false;
        }
        if (this.getAskForBambooTicks() > 0) {
            if (this.getTarget() != null) {
                this.lookAtEntity(this.getTarget(), 90.0f, 90.0f);
            }
            if (this.getAskForBambooTicks() == 29 || this.getAskForBambooTicks() == 14) {
                this.playSound(SoundEvents.ENTITY_PANDA_CANT_BREED, 1.0f, 1.0f);
            }
            this.setAskForBambooTicks(this.getAskForBambooTicks() - 1);
        }
        if (this.isSneezing()) {
            this.setSneezeProgress(this.getSneezeProgress() + 1);
            if (this.getSneezeProgress() > 20) {
                this.setSneezing(false);
                this.sneeze();
            } else if (this.getSneezeProgress() == 1) {
                this.playSound(SoundEvents.ENTITY_PANDA_PRE_SNEEZE, 1.0f, 1.0f);
            }
        }
        if (this.isPlaying()) {
            this.updatePlaying();
        } else {
            this.playingTicks = 0;
        }
        if (this.isScared()) {
            this.pitch = 0.0f;
        }
        this.updateScaredAnimation();
        this.updateEatingAnimation();
        this.updateLieOnBackAnimation();
        this.updateRollOverAnimation();
    }

    public boolean method_6524() {
        return this.isWorried() && this.world.isThundering();
    }

    private void updateEatingAnimation() {
        if (!this.isEating() && this.isScared() && !this.method_6524() && !this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && this.random.nextInt(80) == 1) {
            this.setEating(true);
        } else if (this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() || !this.isScared()) {
            this.setEating(false);
        }
        if (this.isEating()) {
            this.playEatingAnimation();
            if (!this.world.isClient && this.getEatingTicks() > 80 && this.random.nextInt(20) == 1) {
                if (this.getEatingTicks() > 100 && this.canEat(this.getEquippedStack(EquipmentSlot.MAINHAND))) {
                    if (!this.world.isClient) {
                        this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    }
                    this.setScared(false);
                }
                this.setEating(false);
                return;
            }
            this.setEatingTicks(this.getEatingTicks() + 1);
        }
    }

    private void playEatingAnimation() {
        if (this.getEatingTicks() % 5 == 0) {
            this.playSound(SoundEvents.ENTITY_PANDA_EAT, 0.5f + 0.5f * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            for (int i = 0; i < 6; ++i) {
                Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, ((double)this.random.nextFloat() - 0.5) * 0.1);
                vec3d = vec3d.rotateX(-this.pitch * ((float)Math.PI / 180));
                vec3d = vec3d.rotateY(-this.yaw * ((float)Math.PI / 180));
                double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
                Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.8, d, 1.0 + ((double)this.random.nextFloat() - 0.5) * 0.4);
                vec3d2 = vec3d2.rotateY(-this.bodyYaw * ((float)Math.PI / 180));
                vec3d2 = vec3d2.add(this.x, this.y + (double)this.getStandingEyeHeight() + 1.0, this.z);
                this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getEquippedStack(EquipmentSlot.MAINHAND)), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
            }
        }
    }

    private void updateScaredAnimation() {
        this.lastScaredAnimationProgress = this.scaredAnimationProgress;
        this.scaredAnimationProgress = this.isScared() ? Math.min(1.0f, this.scaredAnimationProgress + 0.15f) : Math.max(0.0f, this.scaredAnimationProgress - 0.19f);
    }

    private void updateLieOnBackAnimation() {
        this.lastLieOnBackAnimationProgress = this.lieOnBackAnimationProgress;
        this.lieOnBackAnimationProgress = this.isLyingOnBack() ? Math.min(1.0f, this.lieOnBackAnimationProgress + 0.15f) : Math.max(0.0f, this.lieOnBackAnimationProgress - 0.19f);
    }

    private void updateRollOverAnimation() {
        this.lastRollOverAnimationProgress = this.rollOverAnimationProgress;
        this.rollOverAnimationProgress = this.isPlaying() ? Math.min(1.0f, this.rollOverAnimationProgress + 0.15f) : Math.max(0.0f, this.rollOverAnimationProgress - 0.19f);
    }

    @Environment(value=EnvType.CLIENT)
    public float getScaredAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastScaredAnimationProgress, this.scaredAnimationProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getLieOnBackAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastLieOnBackAnimationProgress, this.lieOnBackAnimationProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getRollOverAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastRollOverAnimationProgress, this.rollOverAnimationProgress);
    }

    private void updatePlaying() {
        ++this.playingTicks;
        if (this.playingTicks > 32) {
            this.setPlaying(false);
            return;
        }
        if (!this.world.isClient) {
            Vec3d vec3d = this.getVelocity();
            if (this.playingTicks == 1) {
                float f = this.yaw * ((float)Math.PI / 180);
                float g = this.isBaby() ? 0.1f : 0.2f;
                this.playingJump = new Vec3d(vec3d.x + (double)(-MathHelper.sin(f) * g), 0.0, vec3d.z + (double)(MathHelper.cos(f) * g));
                this.setVelocity(this.playingJump.add(0.0, 0.27, 0.0));
            } else if ((float)this.playingTicks == 7.0f || (float)this.playingTicks == 15.0f || (float)this.playingTicks == 23.0f) {
                this.setVelocity(0.0, this.onGround ? 0.27 : vec3d.y, 0.0);
            } else {
                this.setVelocity(this.playingJump.x, vec3d.y, this.playingJump.z);
            }
        }
    }

    private void sneeze() {
        Vec3d vec3d = this.getVelocity();
        this.world.addParticle(ParticleTypes.SNEEZE, this.x - (double)(this.getWidth() + 1.0f) * 0.5 * (double)MathHelper.sin(this.bodyYaw * ((float)Math.PI / 180)), this.y + (double)this.getStandingEyeHeight() - (double)0.1f, this.z + (double)(this.getWidth() + 1.0f) * 0.5 * (double)MathHelper.cos(this.bodyYaw * ((float)Math.PI / 180)), vec3d.x, 0.0, vec3d.z);
        this.playSound(SoundEvents.ENTITY_PANDA_SNEEZE, 1.0f, 1.0f);
        List<PandaEntity> list = this.world.getEntities(PandaEntity.class, this.getBoundingBox().expand(10.0));
        for (PandaEntity pandaEntity : list) {
            if (pandaEntity.isBaby() || !pandaEntity.onGround || pandaEntity.isInsideWater() || !pandaEntity.method_18442()) continue;
            pandaEntity.jump();
        }
        if (!this.world.isClient() && this.random.nextInt(700) == 0 && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.dropItem(Items.SLIME_BALL);
        }
    }

    @Override
    protected void loot(ItemEntity itemEntity) {
        if (this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && IS_FOOD.test(itemEntity)) {
            ItemStack itemStack = itemEntity.getStack();
            this.equipStack(EquipmentSlot.MAINHAND, itemStack);
            this.handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 2.0f;
            this.sendPickup(itemEntity, itemStack.getCount());
            itemEntity.remove();
        }
    }

    @Override
    public boolean damage(DamageSource damageSource, float f) {
        this.setScared(false);
        return super.damage(damageSource, f);
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag) {
        entityData = super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
        this.setMainGene(Gene.createRandom(this.random));
        this.setHiddenGene(Gene.createRandom(this.random));
        this.resetAttributes();
        if (entityData instanceof SpawnData) {
            if (this.random.nextInt(5) == 0) {
                this.setBreedingAge(-24000);
            }
        } else {
            entityData = new SpawnData();
        }
        return entityData;
    }

    public void initGenes(PandaEntity pandaEntity, @Nullable PandaEntity pandaEntity2) {
        if (pandaEntity2 == null) {
            if (this.random.nextBoolean()) {
                this.setMainGene(pandaEntity.getRandomGene());
                this.setHiddenGene(Gene.createRandom(this.random));
            } else {
                this.setMainGene(Gene.createRandom(this.random));
                this.setHiddenGene(pandaEntity.getRandomGene());
            }
        } else if (this.random.nextBoolean()) {
            this.setMainGene(pandaEntity.getRandomGene());
            this.setHiddenGene(pandaEntity2.getRandomGene());
        } else {
            this.setMainGene(pandaEntity2.getRandomGene());
            this.setHiddenGene(pandaEntity.getRandomGene());
        }
        if (this.random.nextInt(32) == 0) {
            this.setMainGene(Gene.createRandom(this.random));
        }
        if (this.random.nextInt(32) == 0) {
            this.setHiddenGene(Gene.createRandom(this.random));
        }
    }

    private Gene getRandomGene() {
        if (this.random.nextBoolean()) {
            return this.getMainGene();
        }
        return this.getHiddenGene();
    }

    public void resetAttributes() {
        if (this.isWeak()) {
            this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
        }
        if (this.isLazy()) {
            this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.07f);
        }
    }

    private void stop() {
        if (!this.isInsideWater()) {
            this.setForwardSpeed(0.0f);
            this.getNavigation().stop();
            this.setScared(true);
        }
    }

    @Override
    public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (itemStack.getItem() instanceof SpawnEggItem) {
            return super.interactMob(playerEntity, hand);
        }
        if (this.method_6524()) {
            return false;
        }
        if (this.isLyingOnBack()) {
            this.setLyingOnBack(false);
            return true;
        }
        if (this.isBreedingItem(itemStack)) {
            if (this.getTarget() != null) {
                this.shouldGetRevenge = true;
            }
            if (this.isBaby()) {
                this.eat(playerEntity, itemStack);
                this.growUp((int)((float)(-this.getBreedingAge() / 20) * 0.1f), true);
            } else if (!this.world.isClient && this.getBreedingAge() == 0 && this.canEat()) {
                this.eat(playerEntity, itemStack);
                this.lovePlayer(playerEntity);
            } else if (!(this.world.isClient || this.isScared() || this.isInsideWater())) {
                this.stop();
                this.setEating(true);
                ItemStack itemStack2 = this.getEquippedStack(EquipmentSlot.MAINHAND);
                if (!itemStack2.isEmpty() && !playerEntity.abilities.creativeMode) {
                    this.dropStack(itemStack2);
                }
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(itemStack.getItem(), 1));
                this.eat(playerEntity, itemStack);
            } else {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isAttacking()) {
            return SoundEvents.ENTITY_PANDA_AGGRESSIVE_AMBIENT;
        }
        if (this.isWorried()) {
            return SoundEvents.ENTITY_PANDA_WORRIED_AMBIENT;
        }
        return SoundEvents.ENTITY_PANDA_AMBIENT;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        this.playSound(SoundEvents.ENTITY_PANDA_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean isBreedingItem(ItemStack itemStack) {
        return itemStack.getItem() == Blocks.BAMBOO.asItem();
    }

    private boolean canEat(ItemStack itemStack) {
        return this.isBreedingItem(itemStack) || itemStack.getItem() == Blocks.CAKE.asItem();
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PANDA_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_PANDA_HURT;
    }

    public boolean method_18442() {
        return !this.isLyingOnBack() && !this.method_6524() && !this.isEating() && !this.isPlaying() && !this.isScared();
    }

    static class ExtinguishFireGoal
    extends EscapeDangerGoal {
        private final PandaEntity panda;

        public ExtinguishFireGoal(PandaEntity pandaEntity, double d) {
            super(pandaEntity, d);
            this.panda = pandaEntity;
        }

        @Override
        public boolean canStart() {
            if (!this.panda.isOnFire()) {
                return false;
            }
            BlockPos blockPos = this.locateClosestWater(this.mob.world, this.mob, 5, 4);
            if (blockPos != null) {
                this.targetX = blockPos.getX();
                this.targetY = blockPos.getY();
                this.targetZ = blockPos.getZ();
                return true;
            }
            return this.findTarget();
        }

        @Override
        public boolean shouldContinue() {
            if (this.panda.isScared()) {
                this.panda.getNavigation().stop();
                return false;
            }
            return super.shouldContinue();
        }
    }

    static class PandaRevengeGoal
    extends RevengeGoal {
        private final PandaEntity panda;

        public PandaRevengeGoal(PandaEntity pandaEntity, Class<?> ... classs) {
            super(pandaEntity, classs);
            this.panda = pandaEntity;
        }

        @Override
        public boolean shouldContinue() {
            if (this.panda.shouldGetRevenge || this.panda.shouldAttack) {
                this.panda.setTarget(null);
                return false;
            }
            return super.shouldContinue();
        }

        @Override
        protected void setMobEntityTarget(MobEntity mobEntity, LivingEntity livingEntity) {
            if (mobEntity instanceof PandaEntity && ((PandaEntity)mobEntity).isAttacking()) {
                mobEntity.setTarget(livingEntity);
            }
        }
    }

    static class LieOnBackGoal
    extends Goal {
        private final PandaEntity panda;
        private int nextLieOnBackAge;

        public LieOnBackGoal(PandaEntity pandaEntity) {
            this.panda = pandaEntity;
        }

        @Override
        public boolean canStart() {
            return this.nextLieOnBackAge < this.panda.age && this.panda.isLazy() && this.panda.method_18442() && this.panda.random.nextInt(400) == 1;
        }

        @Override
        public boolean shouldContinue() {
            if (this.panda.isInsideWater() || !this.panda.isLazy() && this.panda.random.nextInt(600) == 1) {
                return false;
            }
            return this.panda.random.nextInt(2000) != 1;
        }

        @Override
        public void start() {
            this.panda.setLyingOnBack(true);
            this.nextLieOnBackAge = 0;
        }

        @Override
        public void stop() {
            this.panda.setLyingOnBack(false);
            this.nextLieOnBackAge = this.panda.age + 200;
        }
    }

    class PickUpFoodGoal
    extends Goal {
        private int startAge;

        public PickUpFoodGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (this.startAge > PandaEntity.this.age || PandaEntity.this.isBaby() || PandaEntity.this.isInsideWater() || !PandaEntity.this.method_18442() || PandaEntity.this.getAskForBambooTicks() > 0) {
                return false;
            }
            List<ItemEntity> list = PandaEntity.this.world.getEntities(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(6.0, 6.0, 6.0), IS_FOOD);
            return !list.isEmpty() || !PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
        }

        @Override
        public boolean shouldContinue() {
            if (PandaEntity.this.isInsideWater() || !PandaEntity.this.isLazy() && PandaEntity.this.random.nextInt(600) == 1) {
                return false;
            }
            return PandaEntity.this.random.nextInt(2000) != 1;
        }

        @Override
        public void tick() {
            if (!PandaEntity.this.isScared() && !PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
                PandaEntity.this.stop();
            }
        }

        @Override
        public void start() {
            List<ItemEntity> list = PandaEntity.this.world.getEntities(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), IS_FOOD);
            if (!list.isEmpty() && PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
                PandaEntity.this.getNavigation().startMovingTo(list.get(0), 1.2f);
            } else if (!PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
                PandaEntity.this.stop();
            }
            this.startAge = 0;
        }

        @Override
        public void stop() {
            ItemStack itemStack = PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (!itemStack.isEmpty()) {
                PandaEntity.this.dropStack(itemStack);
                PandaEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                int i = PandaEntity.this.isLazy() ? PandaEntity.this.random.nextInt(50) + 10 : PandaEntity.this.random.nextInt(150) + 10;
                this.startAge = PandaEntity.this.age + i * 20;
            }
            PandaEntity.this.setScared(false);
        }
    }

    static class PandaFleeGoal<T extends LivingEntity>
    extends FleeEntityGoal<T> {
        private final PandaEntity panda;

        public PandaFleeGoal(PandaEntity pandaEntity, Class<T> class_, float f, double d, double e) {
            super(pandaEntity, class_, f, d, e, EntityPredicates.EXCEPT_SPECTATOR::test);
            this.panda = pandaEntity;
        }

        @Override
        public boolean canStart() {
            return this.panda.isWorried() && this.panda.method_18442() && super.canStart();
        }
    }

    static class PandaMateGoal
    extends AnimalMateGoal {
        private static final TargetPredicate CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(8.0).includeTeammates().includeInvulnerable();
        private final PandaEntity panda;
        private int nextAskPlayerForBambooAge;

        public PandaMateGoal(PandaEntity pandaEntity, double d) {
            super(pandaEntity, d);
            this.panda = pandaEntity;
        }

        @Override
        public boolean canStart() {
            if (super.canStart() && this.panda.getAskForBambooTicks() == 0) {
                if (!this.isBambooClose()) {
                    if (this.nextAskPlayerForBambooAge <= this.panda.age) {
                        this.panda.setAskForBambooTicks(32);
                        this.nextAskPlayerForBambooAge = this.panda.age + 600;
                        if (this.panda.canMoveVoluntarily()) {
                            PlayerEntity playerEntity = this.world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, this.panda);
                            this.panda.setTarget(playerEntity);
                        }
                    }
                    return false;
                }
                return true;
            }
            return false;
        }

        private boolean isBambooClose() {
            BlockPos blockPos = new BlockPos(this.panda);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 8; ++j) {
                    int k = 0;
                    while (k <= j) {
                        int l;
                        int n = l = k < j && k > -j ? j : 0;
                        while (l <= j) {
                            mutable.set(blockPos).setOffset(k, i, l);
                            if (this.world.getBlockState(mutable).getBlock() == Blocks.BAMBOO) {
                                return true;
                            }
                            l = l > 0 ? -l : 1 - l;
                        }
                        k = k > 0 ? -k : 1 - k;
                    }
                }
            }
            return false;
        }
    }

    static class SneezeGoal
    extends Goal {
        private final PandaEntity panda;

        public SneezeGoal(PandaEntity pandaEntity) {
            this.panda = pandaEntity;
        }

        @Override
        public boolean canStart() {
            if (!this.panda.isBaby() || !this.panda.method_18442()) {
                return false;
            }
            if (this.panda.isWeak() && this.panda.random.nextInt(500) == 1) {
                return true;
            }
            return this.panda.random.nextInt(6000) == 1;
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void start() {
            this.panda.setSneezing(true);
        }
    }

    static class PlayGoal
    extends Goal {
        private final PandaEntity panda;

        public PlayGoal(PandaEntity pandaEntity) {
            this.panda = pandaEntity;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
        }

        @Override
        public boolean canStart() {
            if (!this.panda.isBaby() && !this.panda.isPlayful() || !this.panda.onGround) {
                return false;
            }
            if (!this.panda.method_18442()) {
                return false;
            }
            float f = this.panda.yaw * ((float)Math.PI / 180);
            int i = 0;
            int j = 0;
            float g = -MathHelper.sin(f);
            float h = MathHelper.cos(f);
            if ((double)Math.abs(g) > 0.5) {
                i = (int)((float)i + g / Math.abs(g));
            }
            if ((double)Math.abs(h) > 0.5) {
                j = (int)((float)j + h / Math.abs(h));
            }
            if (this.panda.world.getBlockState(new BlockPos(this.panda).add(i, -1, j)).isAir()) {
                return true;
            }
            if (this.panda.isPlayful() && this.panda.random.nextInt(60) == 1) {
                return true;
            }
            return this.panda.random.nextInt(500) == 1;
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void start() {
            this.panda.setPlaying(true);
        }

        @Override
        public boolean canStop() {
            return false;
        }
    }

    static class LookAtEntityGoal
    extends net.minecraft.entity.ai.goal.LookAtEntityGoal {
        private final PandaEntity panda;

        public LookAtEntityGoal(PandaEntity pandaEntity, Class<? extends LivingEntity> class_, float f) {
            super(pandaEntity, class_, f);
            this.panda = pandaEntity;
        }

        @Override
        public boolean canStart() {
            return this.panda.method_18442() && super.canStart();
        }
    }

    static class AttackGoal
    extends MeleeAttackGoal {
        private final PandaEntity panda;

        public AttackGoal(PandaEntity pandaEntity, double d, boolean bl) {
            super(pandaEntity, d, bl);
            this.panda = pandaEntity;
        }

        @Override
        public boolean canStart() {
            return this.panda.method_18442() && super.canStart();
        }
    }

    static class SpawnData
    implements EntityData {
        private SpawnData() {
        }
    }

    static class PandaMoveControl
    extends MoveControl {
        private final PandaEntity panda;

        public PandaMoveControl(PandaEntity pandaEntity) {
            super(pandaEntity);
            this.panda = pandaEntity;
        }

        @Override
        public void tick() {
            if (!this.panda.method_18442()) {
                return;
            }
            super.tick();
        }
    }

    public static enum Gene {
        NORMAL(0, "normal", false),
        LAZY(1, "lazy", false),
        WORRIED(2, "worried", false),
        PLAYFUL(3, "playful", false),
        BROWN(4, "brown", true),
        WEAK(5, "weak", true),
        AGGRESSIVE(6, "aggressive", false);

        private static final Gene[] VALUES;
        private final int id;
        private final String name;
        private final boolean recessive;

        private Gene(int j, String string2, boolean bl) {
            this.id = j;
            this.name = string2;
            this.recessive = bl;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public boolean isRecessive() {
            return this.recessive;
        }

        private static Gene getProductGene(Gene gene, Gene gene2) {
            if (gene.isRecessive()) {
                if (gene == gene2) {
                    return gene;
                }
                return NORMAL;
            }
            return gene;
        }

        public static Gene byId(int i) {
            if (i < 0 || i >= VALUES.length) {
                i = 0;
            }
            return VALUES[i];
        }

        public static Gene byName(String string) {
            for (Gene gene : Gene.values()) {
                if (!gene.name.equals(string)) continue;
                return gene;
            }
            return NORMAL;
        }

        public static Gene createRandom(Random random) {
            int i = random.nextInt(16);
            if (i == 0) {
                return LAZY;
            }
            if (i == 1) {
                return WORRIED;
            }
            if (i == 2) {
                return PLAYFUL;
            }
            if (i == 4) {
                return AGGRESSIVE;
            }
            if (i < 9) {
                return WEAK;
            }
            if (i < 11) {
                return BROWN;
            }
            return NORMAL;
        }

        static {
            VALUES = (Gene[])Arrays.stream(Gene.values()).sorted(Comparator.comparingInt(Gene::getId)).toArray(Gene[]::new);
        }
    }
}

