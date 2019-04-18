package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
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
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class PandaEntity extends AnimalEntity {
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
		return (item == Blocks.field_10211.getItem() || item == Blocks.field_10183.getItem()) && itemEntity.isAlive() && !itemEntity.cannotPickup();
	};

	public PandaEntity(EntityType<? extends PandaEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new PandaEntity.PandaMoveControl(this);
		if (!this.isChild()) {
			this.setCanPickUpLoot(true);
		}
	}

	@Override
	public boolean canPickUp(ItemStack itemStack) {
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
		return !this.getEquippedStack(equipmentSlot).isEmpty() ? false : equipmentSlot == EquipmentSlot.HAND_MAIN && super.canPickUp(itemStack);
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

	public PandaEntity.Gene getMainGene() {
		return PandaEntity.Gene.byId(this.dataTracker.get(MAIN_GENE));
	}

	public void setMainGene(PandaEntity.Gene gene) {
		if (gene.getId() > 6) {
			gene = PandaEntity.Gene.createRandom(this.random);
		}

		this.dataTracker.set(MAIN_GENE, (byte)gene.getId());
	}

	public PandaEntity.Gene getHiddenGene() {
		return PandaEntity.Gene.byId(this.dataTracker.get(HIDDEN_GENE));
	}

	public void setHiddenGene(PandaEntity.Gene gene) {
		if (gene.getId() > 6) {
			gene = PandaEntity.Gene.createRandom(this.random);
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
		this.setMainGene(PandaEntity.Gene.byName(compoundTag.getString("MainGene")));
		this.setHiddenGene(PandaEntity.Gene.byName(compoundTag.getString("HiddenGene")));
	}

	@Nullable
	@Override
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
		this.goalSelector.add(2, new PandaEntity.ExtinguishFireGoal(this, 2.0));
		this.goalSelector.add(2, new PandaEntity.PandaMateGoal(this, 1.0));
		this.goalSelector.add(3, new PandaEntity.AttackGoal(this, 1.2F, true));
		this.goalSelector.add(4, new TemptGoal(this, 1.0, Ingredient.ofItems(Blocks.field_10211.getItem()), false));
		this.goalSelector.add(6, new PandaEntity.PandaFleeGoal(this, PlayerEntity.class, 8.0F, 2.0, 2.0));
		this.goalSelector.add(6, new PandaEntity.PandaFleeGoal(this, HostileEntity.class, 4.0F, 2.0, 2.0));
		this.goalSelector.add(7, new PandaEntity.PickUpFoodGoal());
		this.goalSelector.add(8, new PandaEntity.LieOnBackGoal(this));
		this.goalSelector.add(8, new PandaEntity.SneezeGoal(this));
		this.goalSelector.add(9, new PandaEntity.PandaLookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(10, new LookAroundGoal(this));
		this.goalSelector.add(12, new PandaEntity.PlayGoal(this));
		this.goalSelector.add(13, new FollowParentGoal(this, 1.25));
		this.goalSelector.add(14, new WanderAroundFarGoal(this, 1.0));
		this.targetSelector.add(1, new PandaEntity.PandaRevengeGoal(this).setGroupRevenge(new Class[0]));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.15F);
		this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
	}

	public PandaEntity.Gene getProductGene() {
		return PandaEntity.Gene.getProductGene(this.getMainGene(), this.getHiddenGene());
	}

	public boolean isLazy() {
		return this.getProductGene() == PandaEntity.Gene.field_6794;
	}

	public boolean isWorried() {
		return this.getProductGene() == PandaEntity.Gene.field_6795;
	}

	public boolean isPlayful() {
		return this.getProductGene() == PandaEntity.Gene.field_6791;
	}

	public boolean isWeak() {
		return this.getProductGene() == PandaEntity.Gene.field_6793;
	}

	@Override
	public boolean isAttacking() {
		return this.getProductGene() == PandaEntity.Gene.field_6789;
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return false;
	}

	@Override
	public boolean tryAttack(Entity entity) {
		this.playSound(SoundEvents.field_14552, 1.0F, 1.0F);
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
				this.lookAtEntity(this.getTarget(), 90.0F, 90.0F);
			}

			if (this.getAskForBambooTicks() == 29 || this.getAskForBambooTicks() == 14) {
				this.playSound(SoundEvents.field_14936, 1.0F, 1.0F);
			}

			this.setAskForBambooTicks(this.getAskForBambooTicks() - 1);
		}

		if (this.isSneezing()) {
			this.setSneezeProgress(this.getSneezeProgress() + 1);
			if (this.getSneezeProgress() > 20) {
				this.setSneezing(false);
				this.sneeze();
			} else if (this.getSneezeProgress() == 1) {
				this.playSound(SoundEvents.field_14997, 1.0F, 1.0F);
			}
		}

		if (this.isPlaying()) {
			this.updatePlaying();
		} else {
			this.playingTicks = 0;
		}

		if (this.isScared()) {
			this.pitch = 0.0F;
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
		if (!this.isEating() && this.isScared() && !this.method_6524() && !this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty() && this.random.nextInt(80) == 1) {
			this.setEating(true);
		} else if (this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty() || !this.isScared()) {
			this.setEating(false);
		}

		if (this.isEating()) {
			this.playEatingAnimation();
			if (!this.world.isClient && this.getEatingTicks() > 80 && this.random.nextInt(20) == 1) {
				if (this.getEatingTicks() > 100 && this.canEat(this.getEquippedStack(EquipmentSlot.HAND_MAIN))) {
					if (!this.world.isClient) {
						this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
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
			this.playSound(SoundEvents.field_15106, 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

			for (int i = 0; i < 6; i++) {
				Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, ((double)this.random.nextFloat() - 0.5) * 0.1);
				vec3d = vec3d.rotateX(-this.pitch * (float) (Math.PI / 180.0));
				vec3d = vec3d.rotateY(-this.yaw * (float) (Math.PI / 180.0));
				double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
				Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.8, d, 1.0 + ((double)this.random.nextFloat() - 0.5) * 0.4);
				vec3d2 = vec3d2.rotateY(-this.field_6283 * (float) (Math.PI / 180.0));
				vec3d2 = vec3d2.add(this.x, this.y + (double)this.getStandingEyeHeight() + 1.0, this.z);
				this.world
					.addParticle(
						new ItemStackParticleParameters(ParticleTypes.field_11218, this.getEquippedStack(EquipmentSlot.HAND_MAIN)),
						vec3d2.x,
						vec3d2.y,
						vec3d2.z,
						vec3d.x,
						vec3d.y + 0.05,
						vec3d.z
					);
			}
		}
	}

	private void updateScaredAnimation() {
		this.lastScaredAnimationProgress = this.scaredAnimationProgress;
		if (this.isScared()) {
			this.scaredAnimationProgress = Math.min(1.0F, this.scaredAnimationProgress + 0.15F);
		} else {
			this.scaredAnimationProgress = Math.max(0.0F, this.scaredAnimationProgress - 0.19F);
		}
	}

	private void updateLieOnBackAnimation() {
		this.lastLieOnBackAnimationProgress = this.lieOnBackAnimationProgress;
		if (this.isLyingOnBack()) {
			this.lieOnBackAnimationProgress = Math.min(1.0F, this.lieOnBackAnimationProgress + 0.15F);
		} else {
			this.lieOnBackAnimationProgress = Math.max(0.0F, this.lieOnBackAnimationProgress - 0.19F);
		}
	}

	private void updateRollOverAnimation() {
		this.lastRollOverAnimationProgress = this.rollOverAnimationProgress;
		if (this.isPlaying()) {
			this.rollOverAnimationProgress = Math.min(1.0F, this.rollOverAnimationProgress + 0.15F);
		} else {
			this.rollOverAnimationProgress = Math.max(0.0F, this.rollOverAnimationProgress - 0.19F);
		}
	}

	@Environment(EnvType.CLIENT)
	public float getScaredAnimationProgress(float f) {
		return MathHelper.lerp(f, this.lastScaredAnimationProgress, this.scaredAnimationProgress);
	}

	@Environment(EnvType.CLIENT)
	public float getLieOnBackAnimationProgress(float f) {
		return MathHelper.lerp(f, this.lastLieOnBackAnimationProgress, this.lieOnBackAnimationProgress);
	}

	@Environment(EnvType.CLIENT)
	public float getRollOverAnimationProgress(float f) {
		return MathHelper.lerp(f, this.lastRollOverAnimationProgress, this.rollOverAnimationProgress);
	}

	private void updatePlaying() {
		this.playingTicks++;
		if (this.playingTicks > 32) {
			this.setPlaying(false);
		} else {
			if (!this.world.isClient) {
				Vec3d vec3d = this.getVelocity();
				if (this.playingTicks == 1) {
					float f = this.yaw * (float) (Math.PI / 180.0);
					float g = this.isChild() ? 0.1F : 0.2F;
					this.playingJump = new Vec3d(vec3d.x + (double)(-MathHelper.sin(f) * g), 0.0, vec3d.z + (double)(MathHelper.cos(f) * g));
					this.setVelocity(this.playingJump.add(0.0, 0.27, 0.0));
				} else if ((float)this.playingTicks != 7.0F && (float)this.playingTicks != 15.0F && (float)this.playingTicks != 23.0F) {
					this.setVelocity(this.playingJump.x, vec3d.y, this.playingJump.z);
				} else {
					this.setVelocity(0.0, this.onGround ? 0.27 : vec3d.y, 0.0);
				}
			}
		}
	}

	private void sneeze() {
		Vec3d vec3d = this.getVelocity();
		this.world
			.addParticle(
				ParticleTypes.field_11234,
				this.x - (double)(this.getWidth() + 1.0F) * 0.5 * (double)MathHelper.sin(this.field_6283 * (float) (Math.PI / 180.0)),
				this.y + (double)this.getStandingEyeHeight() - 0.1F,
				this.z + (double)(this.getWidth() + 1.0F) * 0.5 * (double)MathHelper.cos(this.field_6283 * (float) (Math.PI / 180.0)),
				vec3d.x,
				0.0,
				vec3d.z
			);
		this.playSound(SoundEvents.field_15076, 1.0F, 1.0F);

		for (PandaEntity pandaEntity : this.world.getEntities(PandaEntity.class, this.getBoundingBox().expand(10.0))) {
			if (!pandaEntity.isChild() && pandaEntity.onGround && !pandaEntity.isInsideWater() && pandaEntity.method_18442()) {
				pandaEntity.jump();
			}
		}

		if (!this.world.isClient() && this.random.nextInt(700) == 0 && this.world.getGameRules().getBoolean("doMobLoot")) {
			this.dropItem(Items.field_8777);
		}
	}

	@Override
	protected void loot(ItemEntity itemEntity) {
		if (this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty() && IS_FOOD.test(itemEntity)) {
			ItemStack itemStack = itemEntity.getStack();
			this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack);
			this.handDropChances[EquipmentSlot.HAND_MAIN.getEntitySlotId()] = 2.0F;
			this.sendPickup(itemEntity, itemStack.getAmount());
			itemEntity.remove();
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		this.setScared(false);
		return super.damage(damageSource, f);
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		this.setMainGene(PandaEntity.Gene.createRandom(this.random));
		this.setHiddenGene(PandaEntity.Gene.createRandom(this.random));
		this.resetAttributes();
		if (entityData instanceof PandaEntity.SpawnData) {
			if (this.random.nextInt(5) == 0) {
				this.setBreedingAge(-24000);
			}
		} else {
			entityData = new PandaEntity.SpawnData();
		}

		return entityData;
	}

	public void initGenes(PandaEntity pandaEntity, @Nullable PandaEntity pandaEntity2) {
		if (pandaEntity2 == null) {
			if (this.random.nextBoolean()) {
				this.setMainGene(pandaEntity.getRandomGene());
				this.setHiddenGene(PandaEntity.Gene.createRandom(this.random));
			} else {
				this.setMainGene(PandaEntity.Gene.createRandom(this.random));
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
			this.setMainGene(PandaEntity.Gene.createRandom(this.random));
		}

		if (this.random.nextInt(32) == 0) {
			this.setHiddenGene(PandaEntity.Gene.createRandom(this.random));
		}
	}

	private PandaEntity.Gene getRandomGene() {
		return this.random.nextBoolean() ? this.getMainGene() : this.getHiddenGene();
	}

	public void resetAttributes() {
		if (this.isWeak()) {
			this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		}

		if (this.isLazy()) {
			this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.07F);
		}
	}

	private void stop() {
		if (!this.isInsideWater()) {
			this.setForwardSpeed(0.0F);
			this.getNavigation().stop();
			this.setScared(true);
		}
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() instanceof SpawnEggItem) {
			return super.interactMob(playerEntity, hand);
		} else if (this.method_6524()) {
			return false;
		} else if (this.isLyingOnBack()) {
			this.setLyingOnBack(false);
			return true;
		} else if (this.isBreedingItem(itemStack)) {
			if (this.getTarget() != null) {
				this.shouldGetRevenge = true;
			}

			if (this.isChild()) {
				this.eat(playerEntity, itemStack);
				this.growUp((int)((float)(-this.getBreedingAge() / 20) * 0.1F), true);
			} else if (!this.world.isClient && this.getBreedingAge() == 0 && this.canEat()) {
				this.eat(playerEntity, itemStack);
				this.lovePlayer(playerEntity);
			} else {
				if (this.world.isClient || this.isScared() || this.isInsideWater()) {
					return false;
				}

				this.stop();
				this.setEating(true);
				ItemStack itemStack2 = this.getEquippedStack(EquipmentSlot.HAND_MAIN);
				if (!itemStack2.isEmpty() && !playerEntity.abilities.creativeMode) {
					this.dropStack(itemStack2);
				}

				this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(itemStack.getItem(), 1));
				this.eat(playerEntity, itemStack);
			}

			return true;
		} else {
			return false;
		}
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (this.isAttacking()) {
			return SoundEvents.field_14801;
		} else {
			return this.isWorried() ? SoundEvents.field_14715 : SoundEvents.field_14604;
		}
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.field_15035, 0.15F, 1.0F);
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return itemStack.getItem() == Blocks.field_10211.getItem();
	}

	private boolean canEat(ItemStack itemStack) {
		return this.isBreedingItem(itemStack) || itemStack.getItem() == Blocks.field_10183.getItem();
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15208;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14668;
	}

	public boolean method_18442() {
		return !this.isLyingOnBack() && !this.method_6524() && !this.isEating() && !this.isPlaying() && !this.isScared();
	}

	static class AttackGoal extends MeleeAttackGoal {
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

	static class ExtinguishFireGoal extends EscapeDangerGoal {
		private final PandaEntity panda;

		public ExtinguishFireGoal(PandaEntity pandaEntity, double d) {
			super(pandaEntity, d);
			this.panda = pandaEntity;
		}

		@Override
		public boolean canStart() {
			if (!this.panda.isOnFire()) {
				return false;
			} else {
				BlockPos blockPos = this.locateClosestWater(this.owner.world, this.owner, 5, 4);
				if (blockPos != null) {
					this.targetX = (double)blockPos.getX();
					this.targetY = (double)blockPos.getY();
					this.targetZ = (double)blockPos.getZ();
					return true;
				} else {
					return this.findTarget();
				}
			}
		}

		@Override
		public boolean shouldContinue() {
			if (this.panda.isScared()) {
				this.panda.getNavigation().stop();
				return false;
			} else {
				return super.shouldContinue();
			}
		}
	}

	public static enum Gene {
		field_6788(0, "normal", false),
		field_6794(1, "lazy", false),
		field_6795(2, "worried", false),
		field_6791(3, "playful", false),
		field_6792(4, "brown", true),
		field_6793(5, "weak", true),
		field_6789(6, "aggressive", false);

		private static final PandaEntity.Gene[] VALUES = (PandaEntity.Gene[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(PandaEntity.Gene::getId))
			.toArray(PandaEntity.Gene[]::new);
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

		private static PandaEntity.Gene getProductGene(PandaEntity.Gene gene, PandaEntity.Gene gene2) {
			if (gene.isRecessive()) {
				return gene == gene2 ? gene : field_6788;
			} else {
				return gene;
			}
		}

		public static PandaEntity.Gene byId(int i) {
			if (i < 0 || i >= VALUES.length) {
				i = 0;
			}

			return VALUES[i];
		}

		public static PandaEntity.Gene byName(String string) {
			for (PandaEntity.Gene gene : values()) {
				if (gene.name.equals(string)) {
					return gene;
				}
			}

			return field_6788;
		}

		public static PandaEntity.Gene createRandom(Random random) {
			int i = random.nextInt(16);
			if (i == 0) {
				return field_6794;
			} else if (i == 1) {
				return field_6795;
			} else if (i == 2) {
				return field_6791;
			} else if (i == 4) {
				return field_6789;
			} else if (i < 9) {
				return field_6793;
			} else {
				return i < 11 ? field_6792 : field_6788;
			}
		}
	}

	static class LieOnBackGoal extends Goal {
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
			return !this.panda.isInsideWater() && (this.panda.isLazy() || this.panda.random.nextInt(600) != 1) ? this.panda.random.nextInt(2000) != 1 : false;
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

	static class PandaFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final PandaEntity owner;

		public PandaFleeGoal(PandaEntity pandaEntity, Class<T> class_, float f, double d, double e) {
			super(pandaEntity, class_, f, d, e, EntityPredicates.EXCEPT_SPECTATOR::test);
			this.owner = pandaEntity;
		}

		@Override
		public boolean canStart() {
			return this.owner.isWorried() && this.owner.method_18442() && super.canStart();
		}
	}

	static class PandaLookAtEntityGoal extends LookAtEntityGoal {
		private final PandaEntity panda;

		public PandaLookAtEntityGoal(PandaEntity pandaEntity, Class<? extends LivingEntity> class_, float f) {
			super(pandaEntity, class_, f);
			this.panda = pandaEntity;
		}

		@Override
		public boolean canStart() {
			return this.panda.method_18442() && super.canStart();
		}
	}

	static class PandaMateGoal extends AnimalMateGoal {
		private static final TargetPredicate CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(8.0).includeTeammates().includeInvulnerable();
		private final PandaEntity panda;
		private int nextAskPlayerForBambooAge;

		public PandaMateGoal(PandaEntity pandaEntity, double d) {
			super(pandaEntity, d);
			this.panda = pandaEntity;
		}

		@Override
		public boolean canStart() {
			if (!super.canStart() || this.panda.getAskForBambooTicks() != 0) {
				return false;
			} else if (!this.isBambooClose()) {
				if (this.nextAskPlayerForBambooAge <= this.panda.age) {
					this.panda.setAskForBambooTicks(32);
					this.nextAskPlayerForBambooAge = this.panda.age + 600;
					if (this.panda.canMoveVoluntarily()) {
						PlayerEntity playerEntity = this.world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, this.panda);
						this.panda.setTarget(playerEntity);
					}
				}

				return false;
			} else {
				return true;
			}
		}

		private boolean isBambooClose() {
			BlockPos blockPos = new BlockPos(this.panda);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 8; j++) {
					for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
						for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
							mutable.set(blockPos).setOffset(k, i, l);
							if (this.world.getBlockState(mutable).getBlock() == Blocks.field_10211) {
								return true;
							}
						}
					}
				}
			}

			return false;
		}
	}

	static class PandaMoveControl extends MoveControl {
		private final PandaEntity panda;

		public PandaMoveControl(PandaEntity pandaEntity) {
			super(pandaEntity);
			this.panda = pandaEntity;
		}

		@Override
		public void tick() {
			if (this.panda.method_18442()) {
				super.tick();
			}
		}
	}

	static class PandaRevengeGoal extends RevengeGoal {
		private final PandaEntity panda;

		public PandaRevengeGoal(PandaEntity pandaEntity, Class<?>... classs) {
			super(pandaEntity, classs);
			this.panda = pandaEntity;
		}

		@Override
		public boolean shouldContinue() {
			if (!this.panda.shouldGetRevenge && !this.panda.shouldAttack) {
				return super.shouldContinue();
			} else {
				this.panda.setTarget(null);
				return false;
			}
		}

		@Override
		protected void setMobEntityTarget(MobEntity mobEntity, LivingEntity livingEntity) {
			if (mobEntity instanceof PandaEntity && ((PandaEntity)mobEntity).isAttacking()) {
				mobEntity.setTarget(livingEntity);
			}
		}
	}

	class PickUpFoodGoal extends Goal {
		private int startAge;

		public PickUpFoodGoal() {
			this.setControls(EnumSet.of(Goal.Control.field_18405));
		}

		@Override
		public boolean canStart() {
			if (this.startAge <= PandaEntity.this.age
				&& !PandaEntity.this.isChild()
				&& !PandaEntity.this.isInsideWater()
				&& PandaEntity.this.method_18442()
				&& PandaEntity.this.getAskForBambooTicks() <= 0) {
				List<ItemEntity> list = PandaEntity.this.world.getEntities(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(6.0, 6.0, 6.0), PandaEntity.IS_FOOD);
				return !list.isEmpty() || !PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty();
			} else {
				return false;
			}
		}

		@Override
		public boolean shouldContinue() {
			return !PandaEntity.this.isInsideWater() && (PandaEntity.this.isLazy() || PandaEntity.this.random.nextInt(600) != 1)
				? PandaEntity.this.random.nextInt(2000) != 1
				: false;
		}

		@Override
		public void tick() {
			if (!PandaEntity.this.isScared() && !PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
				PandaEntity.this.stop();
			}
		}

		@Override
		public void start() {
			List<ItemEntity> list = PandaEntity.this.world.getEntities(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PandaEntity.IS_FOOD);
			if (!list.isEmpty() && PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
				PandaEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
			} else if (!PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
				PandaEntity.this.stop();
			}

			this.startAge = 0;
		}

		@Override
		public void stop() {
			ItemStack itemStack = PandaEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN);
			if (!itemStack.isEmpty()) {
				PandaEntity.this.dropStack(itemStack);
				PandaEntity.this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
				int i = PandaEntity.this.isLazy() ? PandaEntity.this.random.nextInt(50) + 10 : PandaEntity.this.random.nextInt(150) + 10;
				this.startAge = PandaEntity.this.age + i * 20;
			}

			PandaEntity.this.setScared(false);
		}
	}

	static class PlayGoal extends Goal {
		private final PandaEntity panda;

		public PlayGoal(PandaEntity pandaEntity) {
			this.panda = pandaEntity;
			this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406, Goal.Control.field_18407));
		}

		@Override
		public boolean canStart() {
			if ((this.panda.isChild() || this.panda.isPlayful()) && this.panda.onGround) {
				if (!this.panda.method_18442()) {
					return false;
				} else {
					float f = this.panda.yaw * (float) (Math.PI / 180.0);
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
					} else {
						return this.panda.isPlayful() && this.panda.random.nextInt(60) == 1 ? true : this.panda.random.nextInt(500) == 1;
					}
				}
			} else {
				return false;
			}
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

	static class SneezeGoal extends Goal {
		private final PandaEntity panda;

		public SneezeGoal(PandaEntity pandaEntity) {
			this.panda = pandaEntity;
		}

		@Override
		public boolean canStart() {
			if (this.panda.isChild() && this.panda.method_18442()) {
				return this.panda.isWeak() && this.panda.random.nextInt(500) == 1 ? true : this.panda.random.nextInt(6000) == 1;
			} else {
				return false;
			}
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

	static class SpawnData implements EntityData {
		private SpawnData() {
		}
	}
}
