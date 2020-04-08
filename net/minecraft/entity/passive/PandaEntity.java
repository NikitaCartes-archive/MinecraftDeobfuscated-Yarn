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
import net.minecraft.entity.attribute.DefaultAttributeContainer;
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
    private static final TargetPredicate field_21803 = new TargetPredicate().setBaseMaxDistance(8.0).includeTeammates().includeInvulnerable();
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
    private LookAtEntityGoal lookAtPlayerGoal;
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
    public boolean canPickUp(ItemStack stack) {
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
        if (!this.getEquippedStack(equipmentSlot).isEmpty()) {
            return false;
        }
        return equipmentSlot == EquipmentSlot.MAINHAND && super.canPickUp(stack);
    }

    public int getAskForBambooTicks() {
        return this.dataTracker.get(ASK_FOR_BAMBOO_TICKS);
    }

    public void setAskForBambooTicks(int askForBambooTicks) {
        this.dataTracker.set(ASK_FOR_BAMBOO_TICKS, askForBambooTicks);
    }

    public boolean isSneezing() {
        return this.hasPandaFlag(2);
    }

    public boolean isScared() {
        return this.hasPandaFlag(8);
    }

    public void setScared(boolean scared) {
        this.setPandaFlag(8, scared);
    }

    public boolean isLyingOnBack() {
        return this.hasPandaFlag(16);
    }

    public void setLyingOnBack(boolean lyingOnBack) {
        this.setPandaFlag(16, lyingOnBack);
    }

    public boolean isEating() {
        return this.dataTracker.get(EATING_TICKS) > 0;
    }

    public void setEating(boolean eating) {
        this.dataTracker.set(EATING_TICKS, eating ? 1 : 0);
    }

    private int getEatingTicks() {
        return this.dataTracker.get(EATING_TICKS);
    }

    private void setEatingTicks(int eatingTicks) {
        this.dataTracker.set(EATING_TICKS, eatingTicks);
    }

    public void setSneezing(boolean sneezing) {
        this.setPandaFlag(2, sneezing);
        if (!sneezing) {
            this.setSneezeProgress(0);
        }
    }

    public int getSneezeProgress() {
        return this.dataTracker.get(SNEEZE_PROGRESS);
    }

    public void setSneezeProgress(int sneezeProgress) {
        this.dataTracker.set(SNEEZE_PROGRESS, sneezeProgress);
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

    public void setPlaying(boolean playing) {
        this.setPandaFlag(4, playing);
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

    private boolean hasPandaFlag(int bitmask) {
        return (this.dataTracker.get(PANDA_FLAGS) & bitmask) != 0;
    }

    private void setPandaFlag(int mask, boolean value) {
        byte b = this.dataTracker.get(PANDA_FLAGS);
        if (value) {
            this.dataTracker.set(PANDA_FLAGS, (byte)(b | mask));
        } else {
            this.dataTracker.set(PANDA_FLAGS, (byte)(b & ~mask));
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putString("MainGene", this.getMainGene().getName());
        tag.putString("HiddenGene", this.getHiddenGene().getName());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setMainGene(Gene.byName(tag.getString("MainGene")));
        this.setHiddenGene(Gene.byName(tag.getString("HiddenGene")));
    }

    @Override
    @Nullable
    public PassiveEntity createChild(PassiveEntity mate) {
        PandaEntity pandaEntity = EntityType.PANDA.create(this.world);
        if (mate instanceof PandaEntity) {
            pandaEntity.initGenes(this, (PandaEntity)mate);
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
        this.lookAtPlayerGoal = new LookAtEntityGoal(this, PlayerEntity.class, 6.0f);
        this.goalSelector.add(9, this.lookAtPlayerGoal);
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.goalSelector.add(12, new PlayGoal(this));
        this.goalSelector.add(13, new FollowParentGoal(this, 1.25));
        this.goalSelector.add(14, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new PandaRevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
    }

    public static DefaultAttributeContainer.Builder createPandaAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
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
    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    @Override
    public boolean tryAttack(Entity target) {
        this.playSound(SoundEvents.ENTITY_PANDA_BITE, 1.0f, 1.0f);
        if (!this.isAttacking()) {
            this.shouldAttack = true;
        }
        return super.tryAttack(target);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isWorried()) {
            if (this.world.isThundering() && !this.isTouchingWater()) {
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

    public boolean isScaredByThunderstorm() {
        return this.isWorried() && this.world.isThundering();
    }

    private void updateEatingAnimation() {
        if (!this.isEating() && this.isScared() && !this.isScaredByThunderstorm() && !this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && this.random.nextInt(80) == 1) {
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
                vec3d2 = vec3d2.add(this.getX(), this.getEyeY() + 1.0, this.getZ());
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
    public float getScaredAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastScaredAnimationProgress, this.scaredAnimationProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getLieOnBackAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastLieOnBackAnimationProgress, this.lieOnBackAnimationProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getRollOverAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastRollOverAnimationProgress, this.rollOverAnimationProgress);
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
        this.world.addParticle(ParticleTypes.SNEEZE, this.getX() - (double)(this.getWidth() + 1.0f) * 0.5 * (double)MathHelper.sin(this.bodyYaw * ((float)Math.PI / 180)), this.getEyeY() - (double)0.1f, this.getZ() + (double)(this.getWidth() + 1.0f) * 0.5 * (double)MathHelper.cos(this.bodyYaw * ((float)Math.PI / 180)), vec3d.x, 0.0, vec3d.z);
        this.playSound(SoundEvents.ENTITY_PANDA_SNEEZE, 1.0f, 1.0f);
        List<PandaEntity> list = this.world.getNonSpectatingEntities(PandaEntity.class, this.getBoundingBox().expand(10.0));
        for (PandaEntity pandaEntity : list) {
            if (pandaEntity.isBaby() || !pandaEntity.onGround || pandaEntity.isTouchingWater() || !pandaEntity.isIdle()) continue;
            pandaEntity.jump();
        }
        if (!this.world.isClient() && this.random.nextInt(700) == 0 && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.dropItem(Items.SLIME_BALL);
        }
    }

    @Override
    protected void loot(ItemEntity item) {
        if (this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && IS_FOOD.test(item)) {
            ItemStack itemStack = item.getStack();
            this.equipStack(EquipmentSlot.MAINHAND, itemStack);
            this.handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 2.0f;
            this.sendPickup(item, itemStack.getCount());
            item.remove();
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        this.setScared(false);
        return super.damage(source, amount);
    }

    @Override
    @Nullable
    public EntityData initialize(IWorld world, LocalDifficulty difficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        this.setMainGene(Gene.createRandom(this.random));
        this.setHiddenGene(Gene.createRandom(this.random));
        this.resetAttributes();
        if (entityData == null) {
            entityData = new PassiveEntity.PassiveData();
            ((PassiveEntity.PassiveData)entityData).setBabyChance(0.2f);
        }
        return super.initialize(world, difficulty, spawnType, entityData, entityTag);
    }

    public void initGenes(PandaEntity mother, @Nullable PandaEntity father) {
        if (father == null) {
            if (this.random.nextBoolean()) {
                this.setMainGene(mother.getRandomGene());
                this.setHiddenGene(Gene.createRandom(this.random));
            } else {
                this.setMainGene(Gene.createRandom(this.random));
                this.setHiddenGene(mother.getRandomGene());
            }
        } else if (this.random.nextBoolean()) {
            this.setMainGene(mother.getRandomGene());
            this.setHiddenGene(father.getRandomGene());
        } else {
            this.setMainGene(father.getRandomGene());
            this.setHiddenGene(mother.getRandomGene());
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
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
        }
        if (this.isLazy()) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.07f);
        }
    }

    private void stop() {
        if (!this.isTouchingWater()) {
            this.setForwardSpeed(0.0f);
            this.getNavigation().stop();
            this.setScared(true);
        }
    }

    @Override
    public boolean interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() instanceof SpawnEggItem) {
            return super.interactMob(player, hand);
        }
        if (this.isScaredByThunderstorm()) {
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
                this.eat(player, itemStack);
                this.growUp((int)((float)(-this.getBreedingAge() / 20) * 0.1f), true);
            } else if (!this.world.isClient && this.getBreedingAge() == 0 && this.canEat()) {
                this.eat(player, itemStack);
                this.lovePlayer(player);
            } else if (!(this.world.isClient || this.isScared() || this.isTouchingWater())) {
                this.stop();
                this.setEating(true);
                ItemStack itemStack2 = this.getEquippedStack(EquipmentSlot.MAINHAND);
                if (!itemStack2.isEmpty() && !player.abilities.creativeMode) {
                    this.dropStack(itemStack2);
                }
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(itemStack.getItem(), 1));
                this.eat(player, itemStack);
            } else {
                return false;
            }
            player.swingHand(hand, true);
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
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_PANDA_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Blocks.BAMBOO.asItem();
    }

    private boolean canEat(ItemStack stack) {
        return this.isBreedingItem(stack) || stack.getItem() == Blocks.CAKE.asItem();
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PANDA_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PANDA_HURT;
    }

    public boolean isIdle() {
        return !this.isLyingOnBack() && !this.isScaredByThunderstorm() && !this.isEating() && !this.isPlaying() && !this.isScared();
    }

    static class ExtinguishFireGoal
    extends EscapeDangerGoal {
        private final PandaEntity panda;

        public ExtinguishFireGoal(PandaEntity panda, double speed) {
            super(panda, speed);
            this.panda = panda;
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

        public PandaRevengeGoal(PandaEntity panda, Class<?> ... noRevengeTypes) {
            super(panda, noRevengeTypes);
            this.panda = panda;
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
        protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
            if (mob instanceof PandaEntity && ((PandaEntity)mob).isAttacking()) {
                mob.setTarget(target);
            }
        }
    }

    static class LieOnBackGoal
    extends Goal {
        private final PandaEntity panda;
        private int nextLieOnBackAge;

        public LieOnBackGoal(PandaEntity panda) {
            this.panda = panda;
        }

        @Override
        public boolean canStart() {
            return this.nextLieOnBackAge < this.panda.age && this.panda.isLazy() && this.panda.isIdle() && this.panda.random.nextInt(400) == 1;
        }

        @Override
        public boolean shouldContinue() {
            if (this.panda.isTouchingWater() || !this.panda.isLazy() && this.panda.random.nextInt(600) == 1) {
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
            if (this.startAge > PandaEntity.this.age || PandaEntity.this.isBaby() || PandaEntity.this.isTouchingWater() || !PandaEntity.this.isIdle() || PandaEntity.this.getAskForBambooTicks() > 0) {
                return false;
            }
            List<ItemEntity> list = PandaEntity.this.world.getEntities(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(6.0, 6.0, 6.0), IS_FOOD);
            return !list.isEmpty() || !PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
        }

        @Override
        public boolean shouldContinue() {
            if (PandaEntity.this.isTouchingWater() || !PandaEntity.this.isLazy() && PandaEntity.this.random.nextInt(600) == 1) {
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

        public PandaFleeGoal(PandaEntity panda, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
            super(panda, fleeFromType, distance, slowSpeed, fastSpeed, EntityPredicates.EXCEPT_SPECTATOR::test);
            this.panda = panda;
        }

        @Override
        public boolean canStart() {
            return this.panda.isWorried() && this.panda.isIdle() && super.canStart();
        }
    }

    class PandaMateGoal
    extends AnimalMateGoal {
        private final PandaEntity panda;
        private int nextAskPlayerForBambooAge;

        public PandaMateGoal(PandaEntity pandaEntity2, double d) {
            super(pandaEntity2, d);
            this.panda = pandaEntity2;
        }

        @Override
        public boolean canStart() {
            if (super.canStart() && this.panda.getAskForBambooTicks() == 0) {
                if (!this.isBambooClose()) {
                    if (this.nextAskPlayerForBambooAge <= this.panda.age) {
                        this.panda.setAskForBambooTicks(32);
                        this.nextAskPlayerForBambooAge = this.panda.age + 600;
                        if (this.panda.canMoveVoluntarily()) {
                            PlayerEntity playerEntity = this.world.getClosestPlayer(field_21803, this.panda);
                            this.panda.lookAtPlayerGoal.setTarget(playerEntity);
                        }
                    }
                    return false;
                }
                return true;
            }
            return false;
        }

        private boolean isBambooClose() {
            BlockPos blockPos = this.panda.getBlockPos();
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 8; ++j) {
                    int k = 0;
                    while (k <= j) {
                        int l;
                        int n = l = k < j && k > -j ? j : 0;
                        while (l <= j) {
                            mutable.set(blockPos, k, i, l);
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

        public SneezeGoal(PandaEntity panda) {
            this.panda = panda;
        }

        @Override
        public boolean canStart() {
            if (!this.panda.isBaby() || !this.panda.isIdle()) {
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

        public PlayGoal(PandaEntity panda) {
            this.panda = panda;
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
        }

        @Override
        public boolean canStart() {
            if (!this.panda.isBaby() && !this.panda.isPlayful() || !this.panda.onGround) {
                return false;
            }
            if (!this.panda.isIdle()) {
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
            if (this.panda.world.getBlockState(this.panda.getBlockPos().add(i, -1, j)).isAir()) {
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

        public LookAtEntityGoal(PandaEntity panda, Class<? extends LivingEntity> targetType, float range) {
            super(panda, targetType, range);
            this.panda = panda;
        }

        public void setTarget(LivingEntity target) {
            this.target = target;
        }

        @Override
        public boolean shouldContinue() {
            return this.target != null && super.shouldContinue();
        }

        @Override
        public boolean canStart() {
            if (this.mob.getRandom().nextFloat() >= this.chance) {
                return false;
            }
            if (this.target == null) {
                this.target = this.targetType == PlayerEntity.class ? this.mob.world.getClosestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ()) : this.mob.world.getClosestEntityIncludingUngeneratedChunks(this.targetType, this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), this.mob.getBoundingBox().expand(this.range, 3.0, this.range));
            }
            return this.panda.isIdle() && this.target != null;
        }

        @Override
        public void tick() {
            if (this.target != null) {
                super.tick();
            }
        }
    }

    static class AttackGoal
    extends MeleeAttackGoal {
        private final PandaEntity panda;

        public AttackGoal(PandaEntity panda, double speed, boolean pauseWhenMobIdle) {
            super(panda, speed, pauseWhenMobIdle);
            this.panda = panda;
        }

        @Override
        public boolean canStart() {
            return this.panda.isIdle() && super.canStart();
        }
    }

    static class PandaMoveControl
    extends MoveControl {
        private final PandaEntity panda;

        public PandaMoveControl(PandaEntity panda) {
            super(panda);
            this.panda = panda;
        }

        @Override
        public void tick() {
            if (!this.panda.isIdle()) {
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

        private Gene(int id, String name, boolean recessive) {
            this.id = id;
            this.name = name;
            this.recessive = recessive;
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

        private static Gene getProductGene(Gene mainGene, Gene hiddenGene) {
            if (mainGene.isRecessive()) {
                if (mainGene == hiddenGene) {
                    return mainGene;
                }
                return NORMAL;
            }
            return mainGene;
        }

        public static Gene byId(int id) {
            if (id < 0 || id >= VALUES.length) {
                id = 0;
            }
            return VALUES[id];
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

