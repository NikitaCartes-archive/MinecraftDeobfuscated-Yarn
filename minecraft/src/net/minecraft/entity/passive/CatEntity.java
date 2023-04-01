package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.GoToBedAndSleepGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.UntamedActiveTargetGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.CatVariantTags;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

/**
 * Meow.
 */
public class CatEntity extends TameableEntity implements VariantHolder<CatVariant> {
	public static final double CROUCHING_SPEED = 0.6;
	public static final double NORMAL_SPEED = 0.8;
	public static final double SPRINTING_SPEED = 1.33;
	private static final Ingredient TAMING_INGREDIENT = Ingredient.ofItems(Items.COD, Items.SALMON);
	private static final TrackedData<CatVariant> CAT_VARIANT = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.CAT_VARIANT);
	private static final TrackedData<Boolean> IN_SLEEPING_POSE = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> HEAD_DOWN = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> COLLAR_COLOR = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private CatEntity.CatFleeGoal<PlayerEntity> fleeGoal;
	@Nullable
	private net.minecraft.entity.ai.goal.TemptGoal temptGoal;
	private float sleepAnimation;
	private float prevSleepAnimation;
	private float tailCurlAnimation;
	private float prevTailCurlAnimation;
	private float headDownAnimation;
	private float prevHeadDownAnimation;

	public CatEntity(EntityType<? extends CatEntity> entityType, World world) {
		super(entityType, world);
	}

	public Identifier getTexture() {
		return this.getVariant().texture();
	}

	@Override
	protected void initGoals() {
		this.temptGoal = new CatEntity.TemptGoal(this, 0.6, TAMING_INGREDIENT, true);
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.5));
		this.goalSelector.add(2, new SitGoal(this));
		this.goalSelector.add(3, new CatEntity.SleepWithOwnerGoal(this));
		this.goalSelector.add(4, this.temptGoal);
		this.goalSelector.add(5, new GoToBedAndSleepGoal(this, 1.1, 8));
		this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0F, 5.0F, false));
		this.goalSelector.add(7, new CatSitOnBlockGoal(this, 0.8));
		this.goalSelector.add(8, new PounceAtTargetGoal(this, 0.3F));
		this.goalSelector.add(9, new AttackGoal(this));
		this.goalSelector.add(10, new AnimalMateGoal(this, 0.8));
		this.goalSelector.add(11, new WanderAroundFarGoal(this, 0.8, 1.0000001E-5F));
		this.goalSelector.add(12, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
		this.targetSelector.add(1, new UntamedActiveTargetGoal(this, RabbitEntity.class, false, null));
		this.targetSelector.add(1, new UntamedActiveTargetGoal(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}

	public CatVariant getVariant() {
		return this.dataTracker.get(CAT_VARIANT);
	}

	public void setVariant(CatVariant catVariant) {
		this.dataTracker.set(CAT_VARIANT, catVariant);
	}

	/**
	 * Sets whether this cat is in a sleeping pose or not.
	 * 
	 * @param sleeping {@code true} if this cat is in a sleeping pose, otherwise {@code false}
	 */
	public void setInSleepingPose(boolean sleeping) {
		this.dataTracker.set(IN_SLEEPING_POSE, sleeping);
	}

	/**
	 * {@return whether this cat is in a sleeping pose}
	 */
	public boolean isInSleepingPose() {
		return this.dataTracker.get(IN_SLEEPING_POSE);
	}

	public void setHeadDown(boolean headDown) {
		this.dataTracker.set(HEAD_DOWN, headDown);
	}

	public boolean isHeadDown() {
		return this.dataTracker.get(HEAD_DOWN);
	}

	public DyeColor getCollarColor() {
		return DyeColor.byId(this.dataTracker.get(COLLAR_COLOR));
	}

	public void setCollarColor(DyeColor color) {
		this.dataTracker.set(COLLAR_COLOR, color.getId());
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CAT_VARIANT, Registries.CAT_VARIANT.getOrThrow(CatVariant.BLACK));
		this.dataTracker.startTracking(IN_SLEEPING_POSE, false);
		this.dataTracker.startTracking(HEAD_DOWN, false);
		this.dataTracker.startTracking(COLLAR_COLOR, DyeColor.RED.getId());
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putString("variant", Registries.CAT_VARIANT.getId(this.getVariant()).toString());
		nbt.putByte("CollarColor", (byte)this.getCollarColor().getId());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		CatVariant catVariant = Registries.CAT_VARIANT.get(Identifier.tryParse(nbt.getString("variant")));
		if (catVariant != null) {
			this.setVariant(catVariant);
		}

		if (nbt.contains("CollarColor", NbtElement.NUMBER_TYPE)) {
			this.setCollarColor(DyeColor.byId(nbt.getInt("CollarColor")));
		}
	}

	@Override
	public void mobTick() {
		if (this.getMoveControl().isMoving()) {
			double d = this.getMoveControl().getSpeed();
			if (d == 0.6) {
				this.setPose(EntityPose.CROUCHING);
				this.setSprinting(false);
			} else if (d == 1.33) {
				this.setPose(EntityPose.STANDING);
				this.setSprinting(true);
			} else {
				this.setPose(EntityPose.STANDING);
				this.setSprinting(false);
			}
		} else {
			this.setPose(EntityPose.STANDING);
			this.setSprinting(false);
		}
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (this.isTamed()) {
			if (this.isInLove()) {
				return SoundEvents.ENTITY_CAT_PURR;
			} else {
				return this.random.nextInt(4) == 0 ? SoundEvents.ENTITY_CAT_PURREOW : SoundEvents.ENTITY_CAT_AMBIENT;
			}
		} else {
			return SoundEvents.ENTITY_CAT_STRAY_AMBIENT;
		}
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	public void hiss() {
		this.playSound(SoundEvents.ENTITY_CAT_HISS, this.getSoundVolume(), this.getSoundPitch());
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_CAT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_CAT_DEATH;
	}

	public static DefaultAttributeContainer.Builder createCatAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
	}

	@Override
	protected void eat(PlayerEntity player, Hand hand, ItemStack stack) {
		if (this.isBreedingItem(stack)) {
			this.playSound(SoundEvents.ENTITY_CAT_EAT, 1.0F, 1.0F);
		}

		super.eat(player, hand, stack);
	}

	private float getAttackDamage() {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
	}

	@Override
	public boolean tryAttack(Entity target) {
		return target.damageWithModifier(this.getDamageSources().mobAttack(this), this.getAttackDamage());
	}

	@Override
	public void tick() {
		super.tick();
		if (this.temptGoal != null && this.temptGoal.isActive() && !this.isTamed() && this.age % 100 == 0) {
			this.playSound(SoundEvents.ENTITY_CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
		}

		this.updateAnimations();
	}

	private void updateAnimations() {
		if ((this.isInSleepingPose() || this.isHeadDown()) && this.age % 5 == 0) {
			this.playSound(SoundEvents.ENTITY_CAT_PURR, 0.6F + 0.4F * (this.random.nextFloat() - this.random.nextFloat()), 1.0F);
		}

		this.updateSleepAnimation();
		this.updateHeadDownAnimation();
	}

	private void updateSleepAnimation() {
		this.prevSleepAnimation = this.sleepAnimation;
		this.prevTailCurlAnimation = this.tailCurlAnimation;
		if (this.isInSleepingPose()) {
			this.sleepAnimation = Math.min(1.0F, this.sleepAnimation + 0.15F);
			this.tailCurlAnimation = Math.min(1.0F, this.tailCurlAnimation + 0.08F);
		} else {
			this.sleepAnimation = Math.max(0.0F, this.sleepAnimation - 0.22F);
			this.tailCurlAnimation = Math.max(0.0F, this.tailCurlAnimation - 0.13F);
		}
	}

	private void updateHeadDownAnimation() {
		this.prevHeadDownAnimation = this.headDownAnimation;
		if (this.isHeadDown()) {
			this.headDownAnimation = Math.min(1.0F, this.headDownAnimation + 0.1F);
		} else {
			this.headDownAnimation = Math.max(0.0F, this.headDownAnimation - 0.13F);
		}
	}

	public float getSleepAnimation(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.prevSleepAnimation, this.sleepAnimation);
	}

	public float getTailCurlAnimation(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.prevTailCurlAnimation, this.tailCurlAnimation);
	}

	public float getHeadDownAnimation(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.prevHeadDownAnimation, this.headDownAnimation);
	}

	@Nullable
	public CatEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		CatEntity catEntity = EntityType.CAT.create(serverWorld);
		if (catEntity != null && passiveEntity instanceof CatEntity catEntity2) {
			if (this.random.nextBoolean()) {
				catEntity.setVariant(this.getVariant());
			} else {
				catEntity.setVariant(catEntity2.getVariant());
			}

			if (this.isTamed()) {
				catEntity.setOwnerUuid(this.getOwnerUuid());
				catEntity.setTamed(true);
				if (this.random.nextBoolean()) {
					catEntity.setCollarColor(this.getCollarColor());
				} else {
					catEntity.setCollarColor(catEntity2.getCollarColor());
				}
			}
		}

		return catEntity;
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		if (!this.isTamed()) {
			return false;
		} else {
			return !(other instanceof CatEntity catEntity) ? false : catEntity.isTamed() && super.canBreedWith(other);
		}
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		entityData = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
		boolean bl = world.getMoonSize() > 0.9F;
		TagKey<CatVariant> tagKey = bl ? CatVariantTags.FULL_MOON_SPAWNS : CatVariantTags.DEFAULT_SPAWNS;
		Registries.CAT_VARIANT
			.getEntryList(tagKey)
			.flatMap(list -> list.getRandom(world.getRandom()))
			.ifPresent(variant -> this.setVariant((CatVariant)variant.value()));
		ServerWorld serverWorld = world.toServerWorld();
		if (serverWorld.getStructureAccessor().getStructureContaining(this.getBlockPos(), StructureTags.CATS_SPAWN_AS_BLACK).hasChildren()) {
			this.setVariant(Registries.CAT_VARIANT.getOrThrow(CatVariant.ALL_BLACK));
			this.setPersistent();
		}

		return entityData;
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (this.world.isClient) {
			if (this.isTamed() && this.isOwner(player)) {
				return ActionResult.SUCCESS;
			} else {
				return !this.isBreedingItem(itemStack) || !(this.getHealth() < this.getMaxHealth()) && this.isTamed() ? ActionResult.PASS : ActionResult.SUCCESS;
			}
		} else {
			if (this.isTamed()) {
				if (this.isOwner(player)) {
					if (!(item instanceof DyeItem)) {
						if (item.isFood() && this.isBreedingItem(itemStack) && this.getHealth() < this.getMaxHealth()) {
							this.eat(player, hand, itemStack);
							this.heal((float)item.getFoodComponent().getHunger());
							return ActionResult.CONSUME;
						}

						ActionResult actionResult = super.interactMob(player, hand);
						if (!actionResult.isAccepted() || this.isBaby()) {
							this.setSitting(!this.isSitting());
						}

						return actionResult;
					}

					DyeColor dyeColor = ((DyeItem)item).getColor();
					if (dyeColor != this.getCollarColor()) {
						this.setCollarColor(dyeColor);
						if (!player.getAbilities().creativeMode) {
							itemStack.decrement(1);
						}

						this.setPersistent();
						return ActionResult.CONSUME;
					}
				}
			} else if (this.isBreedingItem(itemStack)) {
				this.eat(player, hand, itemStack);
				if (this.random.nextInt(3) == 0) {
					this.setOwner(player);
					this.setSitting(true);
					this.world.sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
				} else {
					this.world.sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
				}

				this.setPersistent();
				return ActionResult.CONSUME;
			}

			ActionResult actionResult = super.interactMob(player, hand);
			if (actionResult.isAccepted()) {
				this.setPersistent();
			}

			return actionResult;
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return TAMING_INGREDIENT.test(stack);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.5F;
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return !this.isTamed() && this.age > 2400;
	}

	@Override
	protected void onTamedChanged() {
		if (this.fleeGoal == null) {
			this.fleeGoal = new CatEntity.CatFleeGoal<>(this, PlayerEntity.class, 16.0F, 0.8, 1.33);
		}

		this.goalSelector.remove(this.fleeGoal);
		if (!this.isTamed()) {
			this.goalSelector.add(4, this.fleeGoal);
		}
	}

	@Override
	public boolean bypassesSteppingEffects() {
		return this.isInSneakingPose() || super.bypassesSteppingEffects();
	}

	static class CatFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final CatEntity cat;

		public CatFleeGoal(CatEntity cat, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
			super(cat, fleeFromType, distance, slowSpeed, fastSpeed, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
			this.cat = cat;
		}

		@Override
		public boolean canStart() {
			return !this.cat.isTamed() && super.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return !this.cat.isTamed() && super.shouldContinue();
		}
	}

	static class SleepWithOwnerGoal extends Goal {
		private final CatEntity cat;
		@Nullable
		private PlayerEntity owner;
		@Nullable
		private BlockPos bedPos;
		private int ticksOnBed;

		public SleepWithOwnerGoal(CatEntity cat) {
			this.cat = cat;
		}

		@Override
		public boolean canStart() {
			if (!this.cat.isTamed()) {
				return false;
			} else if (this.cat.isSitting()) {
				return false;
			} else {
				LivingEntity livingEntity = this.cat.getOwner();
				if (livingEntity instanceof PlayerEntity) {
					this.owner = (PlayerEntity)livingEntity;
					if (!livingEntity.isSleeping()) {
						return false;
					}

					if (this.cat.squaredDistanceTo(this.owner) > 100.0) {
						return false;
					}

					BlockPos blockPos = this.owner.getBlockPos();
					BlockState blockState = this.cat.world.getBlockState(blockPos);
					if (blockState.isIn(BlockTags.BEDS)) {
						this.bedPos = (BlockPos)blockState.getOrEmpty(BedBlock.FACING)
							.map(direction -> blockPos.offset(direction.getOpposite()))
							.orElseGet(() -> new BlockPos(blockPos));
						return !this.cannotSleep();
					}
				}

				return false;
			}
		}

		private boolean cannotSleep() {
			for (CatEntity catEntity : this.cat.world.getNonSpectatingEntities(CatEntity.class, new Box(this.bedPos).expand(2.0))) {
				if (catEntity != this.cat && (catEntity.isInSleepingPose() || catEntity.isHeadDown())) {
					return true;
				}
			}

			return false;
		}

		@Override
		public boolean shouldContinue() {
			return this.cat.isTamed() && !this.cat.isSitting() && this.owner != null && this.owner.isSleeping() && this.bedPos != null && !this.cannotSleep();
		}

		@Override
		public void start() {
			if (this.bedPos != null) {
				this.cat.setInSittingPose(false);
				this.cat.getNavigation().startMovingTo((double)this.bedPos.getX(), (double)this.bedPos.getY(), (double)this.bedPos.getZ(), 1.1F);
			}
		}

		@Override
		public void stop() {
			this.cat.setInSleepingPose(false);
			float f = this.cat.world.getSkyAngle(1.0F);
			if (this.owner.getSleepTimer() >= 100 && (double)f > 0.77 && (double)f < 0.8 && (double)this.cat.world.getRandom().nextFloat() < 0.7) {
				this.dropMorningGifts();
			}

			this.ticksOnBed = 0;
			this.cat.setHeadDown(false);
			this.cat.getNavigation().stop();
		}

		private void dropMorningGifts() {
			Random random = this.cat.getRandom();
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			mutable.set(this.cat.isLeashed() ? this.cat.getHoldingEntity().getBlockPos() : this.cat.getBlockPos());
			this.cat
				.teleport(
					(double)(mutable.getX() + random.nextInt(11) - 5),
					(double)(mutable.getY() + random.nextInt(5) - 2),
					(double)(mutable.getZ() + random.nextInt(11) - 5),
					false
				);
			mutable.set(this.cat.getBlockPos());
			LootTable lootTable = this.cat.world.getServer().getLootManager().getTable(LootTables.CAT_MORNING_GIFT_GAMEPLAY);
			LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.cat.world)
				.parameter(LootContextParameters.ORIGIN, this.cat.getPos())
				.parameter(LootContextParameters.THIS_ENTITY, this.cat)
				.random(random);

			for (ItemStack itemStack : lootTable.generateLoot(builder.build(LootContextTypes.GIFT))) {
				this.cat
					.world
					.spawnEntity(
						new ItemEntity(
							this.cat.world,
							(double)mutable.getX() - (double)MathHelper.sin(this.cat.bodyYaw * (float) (Math.PI / 180.0)),
							(double)mutable.getY(),
							(double)mutable.getZ() + (double)MathHelper.cos(this.cat.bodyYaw * (float) (Math.PI / 180.0)),
							itemStack
						)
					);
			}
		}

		@Override
		public void tick() {
			if (this.owner != null && this.bedPos != null) {
				this.cat.setInSittingPose(false);
				this.cat.getNavigation().startMovingTo((double)this.bedPos.getX(), (double)this.bedPos.getY(), (double)this.bedPos.getZ(), 1.1F);
				if (this.cat.squaredDistanceTo(this.owner) < 2.5) {
					this.ticksOnBed++;
					if (this.ticksOnBed > this.getTickCount(16)) {
						this.cat.setInSleepingPose(true);
						this.cat.setHeadDown(false);
					} else {
						this.cat.lookAtEntity(this.owner, 45.0F, 45.0F);
						this.cat.setHeadDown(true);
					}
				} else {
					this.cat.setInSleepingPose(false);
				}
			}
		}
	}

	static class TemptGoal extends net.minecraft.entity.ai.goal.TemptGoal {
		@Nullable
		private PlayerEntity player;
		private final CatEntity cat;

		public TemptGoal(CatEntity cat, double speed, Ingredient food, boolean canBeScared) {
			super(cat, speed, food, canBeScared);
			this.cat = cat;
		}

		@Override
		public void tick() {
			super.tick();
			if (this.player == null && this.mob.getRandom().nextInt(this.getTickCount(600)) == 0) {
				this.player = this.closestPlayer;
			} else if (this.mob.getRandom().nextInt(this.getTickCount(500)) == 0) {
				this.player = null;
			}
		}

		@Override
		protected boolean canBeScared() {
			return this.player != null && this.player.equals(this.closestPlayer) ? false : super.canBeScared();
		}

		@Override
		public boolean canStart() {
			return super.canStart() && !this.cat.isTamed();
		}
	}
}
