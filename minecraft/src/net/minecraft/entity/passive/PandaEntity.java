package net.minecraft.entity.passive;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PandaEntity extends AnimalEntity {
	private static final TrackedData<Integer> ASK_FOR_BAMBOO_TICKS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> SNEEZE_PROGRESS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> EATING_TICKS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Byte> MAIN_GENE = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Byte> HIDDEN_GENE = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Byte> PANDA_FLAGS = DataTracker.registerData(PandaEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TargetPredicate ASK_FOR_BAMBOO_TARGET = new TargetPredicate().setBaseMaxDistance(8.0).includeTeammates().includeInvulnerable();
	private static final int field_30344 = 2;
	private static final int field_30345 = 4;
	private static final int field_30346 = 8;
	private static final int field_30347 = 16;
	private static final int field_30348 = 5;
	public static final int field_30343 = 32;
	private static final int field_30349 = 32;
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
	private PandaEntity.LookAtEntityGoal lookAtPlayerGoal;
	private static final Predicate<ItemEntity> IS_FOOD = item -> {
		ItemStack itemStack = item.getStack();
		return (itemStack.isOf(Blocks.BAMBOO.asItem()) || itemStack.isOf(Blocks.CAKE.asItem())) && item.isAlive() && !item.cannotPickup();
	};

	public PandaEntity(EntityType<? extends PandaEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new PandaEntity.PandaMoveControl(this);
		if (!this.isBaby()) {
			this.setCanPickUpLoot(true);
		}
	}

	@Override
	public boolean canEquip(ItemStack stack) {
		EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(stack);
		return !this.getEquippedStack(equipmentSlot).isEmpty() ? false : equipmentSlot == EquipmentSlot.MAINHAND && super.canEquip(stack);
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
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putString("MainGene", this.getMainGene().getName());
		nbt.putString("HiddenGene", this.getHiddenGene().getName());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setMainGene(PandaEntity.Gene.byName(nbt.getString("MainGene")));
		this.setHiddenGene(PandaEntity.Gene.byName(nbt.getString("HiddenGene")));
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		PandaEntity pandaEntity = EntityType.PANDA.create(world);
		if (entity instanceof PandaEntity) {
			pandaEntity.initGenes(this, (PandaEntity)entity);
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
		this.goalSelector.add(4, new TemptGoal(this, 1.0, Ingredient.ofItems(Blocks.BAMBOO.asItem()), false));
		this.goalSelector.add(6, new PandaEntity.PandaFleeGoal(this, PlayerEntity.class, 8.0F, 2.0, 2.0));
		this.goalSelector.add(6, new PandaEntity.PandaFleeGoal(this, HostileEntity.class, 4.0F, 2.0, 2.0));
		this.goalSelector.add(7, new PandaEntity.PickUpFoodGoal());
		this.goalSelector.add(8, new PandaEntity.LieOnBackGoal(this));
		this.goalSelector.add(8, new PandaEntity.SneezeGoal(this));
		this.lookAtPlayerGoal = new PandaEntity.LookAtEntityGoal(this, PlayerEntity.class, 6.0F);
		this.goalSelector.add(9, this.lookAtPlayerGoal);
		this.goalSelector.add(10, new LookAroundGoal(this));
		this.goalSelector.add(12, new PandaEntity.PlayGoal(this));
		this.goalSelector.add(13, new FollowParentGoal(this, 1.25));
		this.goalSelector.add(14, new WanderAroundFarGoal(this, 1.0));
		this.targetSelector.add(1, new PandaEntity.PandaRevengeGoal(this).setGroupRevenge(new Class[0]));
	}

	public static DefaultAttributeContainer.Builder createPandaAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15F).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
	}

	public PandaEntity.Gene getProductGene() {
		return PandaEntity.Gene.getProductGene(this.getMainGene(), this.getHiddenGene());
	}

	public boolean isLazy() {
		return this.getProductGene() == PandaEntity.Gene.LAZY;
	}

	public boolean isWorried() {
		return this.getProductGene() == PandaEntity.Gene.WORRIED;
	}

	public boolean isPlayful() {
		return this.getProductGene() == PandaEntity.Gene.PLAYFUL;
	}

	public boolean isBrown() {
		return this.getProductGene() == PandaEntity.Gene.BROWN;
	}

	public boolean isWeak() {
		return this.getProductGene() == PandaEntity.Gene.WEAK;
	}

	@Override
	public boolean isAttacking() {
		return this.getProductGene() == PandaEntity.Gene.AGGRESSIVE;
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity player) {
		return false;
	}

	@Override
	public boolean tryAttack(Entity target) {
		this.playSound(SoundEvents.ENTITY_PANDA_BITE, 1.0F, 1.0F);
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
				this.lookAtEntity(this.getTarget(), 90.0F, 90.0F);
			}

			if (this.getAskForBambooTicks() == 29 || this.getAskForBambooTicks() == 14) {
				this.playSound(SoundEvents.ENTITY_PANDA_CANT_BREED, 1.0F, 1.0F);
			}

			this.setAskForBambooTicks(this.getAskForBambooTicks() - 1);
		}

		if (this.isSneezing()) {
			this.setSneezeProgress(this.getSneezeProgress() + 1);
			if (this.getSneezeProgress() > 20) {
				this.setSneezing(false);
				this.sneeze();
			} else if (this.getSneezeProgress() == 1) {
				this.playSound(SoundEvents.ENTITY_PANDA_PRE_SNEEZE, 1.0F, 1.0F);
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

	public boolean isScaredByThunderstorm() {
		return this.isWorried() && this.world.isThundering();
	}

	private void updateEatingAnimation() {
		if (!this.isEating()
			&& this.isScared()
			&& !this.isScaredByThunderstorm()
			&& !this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()
			&& this.random.nextInt(80) == 1) {
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
						this.emitGameEvent(GameEvent.EAT, this.getCameraBlockPos());
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
			this.playSound(SoundEvents.ENTITY_PANDA_EAT, 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

			for (int i = 0; i < 6; i++) {
				Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, ((double)this.random.nextFloat() - 0.5) * 0.1);
				vec3d = vec3d.rotateX(-this.pitch * (float) (Math.PI / 180.0));
				vec3d = vec3d.rotateY(-this.yaw * (float) (Math.PI / 180.0));
				double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
				Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.8, d, 1.0 + ((double)this.random.nextFloat() - 0.5) * 0.4);
				vec3d2 = vec3d2.rotateY(-this.bodyYaw * (float) (Math.PI / 180.0));
				vec3d2 = vec3d2.add(this.getX(), this.getEyeY() + 1.0, this.getZ());
				this.world
					.addParticle(
						new ItemStackParticleEffect(ParticleTypes.ITEM, this.getEquippedStack(EquipmentSlot.MAINHAND)),
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

	public float getScaredAnimationProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastScaredAnimationProgress, this.scaredAnimationProgress);
	}

	public float getLieOnBackAnimationProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastLieOnBackAnimationProgress, this.lieOnBackAnimationProgress);
	}

	public float getRollOverAnimationProgress(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastRollOverAnimationProgress, this.rollOverAnimationProgress);
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
					float g = this.isBaby() ? 0.1F : 0.2F;
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
				ParticleTypes.SNEEZE,
				this.getX() - (double)(this.getWidth() + 1.0F) * 0.5 * (double)MathHelper.sin(this.bodyYaw * (float) (Math.PI / 180.0)),
				this.getEyeY() - 0.1F,
				this.getZ() + (double)(this.getWidth() + 1.0F) * 0.5 * (double)MathHelper.cos(this.bodyYaw * (float) (Math.PI / 180.0)),
				vec3d.x,
				0.0,
				vec3d.z
			);
		this.playSound(SoundEvents.ENTITY_PANDA_SNEEZE, 1.0F, 1.0F);

		for (PandaEntity pandaEntity : this.world.getNonSpectatingEntities(PandaEntity.class, this.getBoundingBox().expand(10.0))) {
			if (!pandaEntity.isBaby() && pandaEntity.onGround && !pandaEntity.isTouchingWater() && pandaEntity.isIdle()) {
				pandaEntity.jump();
			}
		}

		if (!this.world.isClient() && this.random.nextInt(700) == 0 && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
			this.dropItem(Items.SLIME_BALL);
		}
	}

	@Override
	protected void loot(ItemEntity item) {
		if (this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && IS_FOOD.test(item)) {
			this.triggerItemPickedUpByEntityCriteria(item);
			ItemStack itemStack = item.getStack();
			this.equipStack(EquipmentSlot.MAINHAND, itemStack);
			this.handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 2.0F;
			this.sendPickup(item, itemStack.getCount());
			item.discard();
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		this.setScared(false);
		return super.damage(source, amount);
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		this.setMainGene(PandaEntity.Gene.createRandom(this.random));
		this.setHiddenGene(PandaEntity.Gene.createRandom(this.random));
		this.resetAttributes();
		if (entityData == null) {
			entityData = new PassiveEntity.PassiveData(0.2F);
		}

		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	public void initGenes(PandaEntity mother, @Nullable PandaEntity father) {
		if (father == null) {
			if (this.random.nextBoolean()) {
				this.setMainGene(mother.getRandomGene());
				this.setHiddenGene(PandaEntity.Gene.createRandom(this.random));
			} else {
				this.setMainGene(PandaEntity.Gene.createRandom(this.random));
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
			this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(10.0);
		}

		if (this.isLazy()) {
			this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.07F);
		}
	}

	private void stop() {
		if (!this.isTouchingWater()) {
			this.setForwardSpeed(0.0F);
			this.getNavigation().stop();
			this.setScared(true);
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (this.isScaredByThunderstorm()) {
			return ActionResult.PASS;
		} else if (this.isLyingOnBack()) {
			this.setLyingOnBack(false);
			return ActionResult.success(this.world.isClient);
		} else if (this.isBreedingItem(itemStack)) {
			if (this.getTarget() != null) {
				this.shouldGetRevenge = true;
			}

			if (this.isBaby()) {
				this.eat(player, hand, itemStack);
				this.growUp((int)((float)(-this.getBreedingAge() / 20) * 0.1F), true);
				this.emitGameEvent(GameEvent.MOB_INTERACT, this.getCameraBlockPos());
			} else if (!this.world.isClient && this.getBreedingAge() == 0 && this.canEat()) {
				this.eat(player, hand, itemStack);
				this.lovePlayer(player);
				this.emitGameEvent(GameEvent.MOB_INTERACT, this.getCameraBlockPos());
			} else {
				if (this.world.isClient || this.isScared() || this.isTouchingWater()) {
					return ActionResult.PASS;
				}

				this.stop();
				this.setEating(true);
				ItemStack itemStack2 = this.getEquippedStack(EquipmentSlot.MAINHAND);
				if (!itemStack2.isEmpty() && !player.getAbilities().creativeMode) {
					this.dropStack(itemStack2);
				}

				this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(itemStack.getItem(), 1));
				this.eat(player, hand, itemStack);
			}

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (this.isAttacking()) {
			return SoundEvents.ENTITY_PANDA_AGGRESSIVE_AMBIENT;
		} else {
			return this.isWorried() ? SoundEvents.ENTITY_PANDA_WORRIED_AMBIENT : SoundEvents.ENTITY_PANDA_AMBIENT;
		}
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_PANDA_STEP, 0.15F, 1.0F);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.isOf(Blocks.BAMBOO.asItem());
	}

	private boolean canEat(ItemStack stack) {
		return this.isBreedingItem(stack) || stack.isOf(Blocks.CAKE.asItem());
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PANDA_DEATH;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_PANDA_HURT;
	}

	public boolean isIdle() {
		return !this.isLyingOnBack() && !this.isScaredByThunderstorm() && !this.isEating() && !this.isPlaying() && !this.isScared();
	}

	static class AttackGoal extends MeleeAttackGoal {
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

	static class ExtinguishFireGoal extends EscapeDangerGoal {
		private final PandaEntity panda;

		public ExtinguishFireGoal(PandaEntity panda, double speed) {
			super(panda, speed);
			this.panda = panda;
		}

		@Override
		public boolean canStart() {
			if (!this.panda.isOnFire()) {
				return false;
			} else {
				BlockPos blockPos = this.locateClosestWater(this.mob.world, this.mob, 5, 4);
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
		NORMAL(0, "normal", false),
		LAZY(1, "lazy", false),
		WORRIED(2, "worried", false),
		PLAYFUL(3, "playful", false),
		BROWN(4, "brown", true),
		WEAK(5, "weak", true),
		AGGRESSIVE(6, "aggressive", false);

		private static final PandaEntity.Gene[] VALUES = (PandaEntity.Gene[])Arrays.stream(values())
			.sorted(Comparator.comparingInt(PandaEntity.Gene::getId))
			.toArray(PandaEntity.Gene[]::new);
		private static final int field_30350 = 6;
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

		private static PandaEntity.Gene getProductGene(PandaEntity.Gene mainGene, PandaEntity.Gene hiddenGene) {
			if (mainGene.isRecessive()) {
				return mainGene == hiddenGene ? mainGene : NORMAL;
			} else {
				return mainGene;
			}
		}

		public static PandaEntity.Gene byId(int id) {
			if (id < 0 || id >= VALUES.length) {
				id = 0;
			}

			return VALUES[id];
		}

		public static PandaEntity.Gene byName(String name) {
			for (PandaEntity.Gene gene : values()) {
				if (gene.name.equals(name)) {
					return gene;
				}
			}

			return NORMAL;
		}

		public static PandaEntity.Gene createRandom(Random random) {
			int i = random.nextInt(16);
			if (i == 0) {
				return LAZY;
			} else if (i == 1) {
				return WORRIED;
			} else if (i == 2) {
				return PLAYFUL;
			} else if (i == 4) {
				return AGGRESSIVE;
			} else if (i < 9) {
				return WEAK;
			} else {
				return i < 11 ? BROWN : NORMAL;
			}
		}
	}

	static class LieOnBackGoal extends Goal {
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
			return !this.panda.isTouchingWater() && (this.panda.isLazy() || this.panda.random.nextInt(600) != 1) ? this.panda.random.nextInt(2000) != 1 : false;
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

	static class LookAtEntityGoal extends net.minecraft.entity.ai.goal.LookAtEntityGoal {
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
			} else {
				if (this.target == null) {
					if (this.targetType == PlayerEntity.class) {
						this.target = this.mob.world.getClosestPlayer(this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
					} else {
						this.target = this.mob
							.world
							.getClosestEntity(
								this.mob.world.getEntitiesByClass(this.targetType, this.mob.getBoundingBox().expand((double)this.range, 3.0, (double)this.range), livingEntity -> true),
								this.targetPredicate,
								this.mob,
								this.mob.getX(),
								this.mob.getEyeY(),
								this.mob.getZ()
							);
					}
				}

				return this.panda.isIdle() && this.target != null;
			}
		}

		@Override
		public void tick() {
			if (this.target != null) {
				super.tick();
			}
		}
	}

	static class PandaFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
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

	class PandaMateGoal extends AnimalMateGoal {
		private final PandaEntity panda;
		private int nextAskPlayerForBambooAge;

		public PandaMateGoal(PandaEntity panda, double chance) {
			super(panda, chance);
			this.panda = panda;
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
						PlayerEntity playerEntity = this.world.getClosestPlayer(PandaEntity.ASK_FOR_BAMBOO_TARGET, this.panda);
						this.panda.lookAtPlayerGoal.setTarget(playerEntity);
					}
				}

				return false;
			} else {
				return true;
			}
		}

		private boolean isBambooClose() {
			BlockPos blockPos = this.panda.getBlockPos();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 8; j++) {
					for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
						for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
							mutable.set(blockPos, k, i, l);
							if (this.world.getBlockState(mutable).isOf(Blocks.BAMBOO)) {
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

		public PandaMoveControl(PandaEntity panda) {
			super(panda);
			this.panda = panda;
		}

		@Override
		public void tick() {
			if (this.panda.isIdle()) {
				super.tick();
			}
		}
	}

	static class PandaRevengeGoal extends RevengeGoal {
		private final PandaEntity panda;

		public PandaRevengeGoal(PandaEntity panda, Class<?>... noRevengeTypes) {
			super(panda, noRevengeTypes);
			this.panda = panda;
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
		protected void setMobEntityTarget(MobEntity mob, LivingEntity target) {
			if (mob instanceof PandaEntity && ((PandaEntity)mob).isAttacking()) {
				mob.setTarget(target);
			}
		}
	}

	class PickUpFoodGoal extends Goal {
		private int startAge;

		public PickUpFoodGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			if (this.startAge <= PandaEntity.this.age
				&& !PandaEntity.this.isBaby()
				&& !PandaEntity.this.isTouchingWater()
				&& PandaEntity.this.isIdle()
				&& PandaEntity.this.getAskForBambooTicks() <= 0) {
				List<ItemEntity> list = PandaEntity.this.world
					.getEntitiesByClass(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(6.0, 6.0, 6.0), PandaEntity.IS_FOOD);
				return !list.isEmpty() || !PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
			} else {
				return false;
			}
		}

		@Override
		public boolean shouldContinue() {
			return !PandaEntity.this.isTouchingWater() && (PandaEntity.this.isLazy() || PandaEntity.this.random.nextInt(600) != 1)
				? PandaEntity.this.random.nextInt(2000) != 1
				: false;
		}

		@Override
		public void tick() {
			if (!PandaEntity.this.isScared() && !PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
				PandaEntity.this.stop();
			}
		}

		@Override
		public void start() {
			List<ItemEntity> list = PandaEntity.this.world
				.getEntitiesByClass(ItemEntity.class, PandaEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), PandaEntity.IS_FOOD);
			if (!list.isEmpty() && PandaEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
				PandaEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
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

	static class PlayGoal extends Goal {
		private final PandaEntity panda;

		public PlayGoal(PandaEntity panda) {
			this.panda = panda;
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
		}

		@Override
		public boolean canStart() {
			if ((this.panda.isBaby() || this.panda.isPlayful()) && this.panda.onGround) {
				if (!this.panda.isIdle()) {
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

					if (this.panda.world.getBlockState(this.panda.getBlockPos().add(i, -1, j)).isAir()) {
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

		public SneezeGoal(PandaEntity panda) {
			this.panda = panda;
		}

		@Override
		public boolean canStart() {
			if (this.panda.isBaby() && this.panda.isIdle()) {
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
}
