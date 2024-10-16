package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.FlyGoal;
import net.minecraft.entity.ai.goal.FollowMobGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SitOnOwnerShoulderGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ParrotEntity extends TameableShoulderEntity implements VariantHolder<ParrotEntity.Variant>, Flutterer {
	private static final TrackedData<Integer> VARIANT = DataTracker.registerData(ParrotEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Predicate<MobEntity> CAN_IMITATE = new Predicate<MobEntity>() {
		public boolean test(@Nullable MobEntity mobEntity) {
			return mobEntity != null && ParrotEntity.MOB_SOUNDS.containsKey(mobEntity.getType());
		}
	};
	static final Map<EntityType<?>, SoundEvent> MOB_SOUNDS = Util.make(Maps.<EntityType<?>, SoundEvent>newHashMap(), map -> {
		map.put(EntityType.BLAZE, SoundEvents.ENTITY_PARROT_IMITATE_BLAZE);
		map.put(EntityType.BOGGED, SoundEvents.ENTITY_PARROT_IMITATE_BOGGED);
		map.put(EntityType.BREEZE, SoundEvents.ENTITY_PARROT_IMITATE_BREEZE);
		map.put(EntityType.CAVE_SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
		map.put(EntityType.CREEPER, SoundEvents.ENTITY_PARROT_IMITATE_CREEPER);
		map.put(EntityType.DROWNED, SoundEvents.ENTITY_PARROT_IMITATE_DROWNED);
		map.put(EntityType.ELDER_GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_ELDER_GUARDIAN);
		map.put(EntityType.ENDER_DRAGON, SoundEvents.ENTITY_PARROT_IMITATE_ENDER_DRAGON);
		map.put(EntityType.ENDERMITE, SoundEvents.ENTITY_PARROT_IMITATE_ENDERMITE);
		map.put(EntityType.EVOKER, SoundEvents.ENTITY_PARROT_IMITATE_EVOKER);
		map.put(EntityType.GHAST, SoundEvents.ENTITY_PARROT_IMITATE_GHAST);
		map.put(EntityType.GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_GUARDIAN);
		map.put(EntityType.HOGLIN, SoundEvents.ENTITY_PARROT_IMITATE_HOGLIN);
		map.put(EntityType.HUSK, SoundEvents.ENTITY_PARROT_IMITATE_HUSK);
		map.put(EntityType.ILLUSIONER, SoundEvents.ENTITY_PARROT_IMITATE_ILLUSIONER);
		map.put(EntityType.MAGMA_CUBE, SoundEvents.ENTITY_PARROT_IMITATE_MAGMA_CUBE);
		map.put(EntityType.PHANTOM, SoundEvents.ENTITY_PARROT_IMITATE_PHANTOM);
		map.put(EntityType.PIGLIN, SoundEvents.ENTITY_PARROT_IMITATE_PIGLIN);
		map.put(EntityType.PIGLIN_BRUTE, SoundEvents.ENTITY_PARROT_IMITATE_PIGLIN_BRUTE);
		map.put(EntityType.PILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_PILLAGER);
		map.put(EntityType.RAVAGER, SoundEvents.ENTITY_PARROT_IMITATE_RAVAGER);
		map.put(EntityType.SHULKER, SoundEvents.ENTITY_PARROT_IMITATE_SHULKER);
		map.put(EntityType.SILVERFISH, SoundEvents.ENTITY_PARROT_IMITATE_SILVERFISH);
		map.put(EntityType.SKELETON, SoundEvents.ENTITY_PARROT_IMITATE_SKELETON);
		map.put(EntityType.SLIME, SoundEvents.ENTITY_PARROT_IMITATE_SLIME);
		map.put(EntityType.SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
		map.put(EntityType.STRAY, SoundEvents.ENTITY_PARROT_IMITATE_STRAY);
		map.put(EntityType.VEX, SoundEvents.ENTITY_PARROT_IMITATE_VEX);
		map.put(EntityType.VINDICATOR, SoundEvents.ENTITY_PARROT_IMITATE_VINDICATOR);
		map.put(EntityType.WARDEN, SoundEvents.ENTITY_PARROT_IMITATE_WARDEN);
		map.put(EntityType.WITCH, SoundEvents.ENTITY_PARROT_IMITATE_WITCH);
		map.put(EntityType.WITHER, SoundEvents.ENTITY_PARROT_IMITATE_WITHER);
		map.put(EntityType.WITHER_SKELETON, SoundEvents.ENTITY_PARROT_IMITATE_WITHER_SKELETON);
		map.put(EntityType.ZOGLIN, SoundEvents.ENTITY_PARROT_IMITATE_ZOGLIN);
		map.put(EntityType.CREAKING, SoundEvents.ENTITY_PARROT_IMITATE_CREAKING);
		map.put(EntityType.CREAKING_TRANSIENT, SoundEvents.ENTITY_PARROT_IMITATE_CREAKING);
		map.put(EntityType.ZOMBIE, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE);
		map.put(EntityType.ZOMBIE_VILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE_VILLAGER);
	});
	public float flapProgress;
	public float maxWingDeviation;
	public float prevMaxWingDeviation;
	public float prevFlapProgress;
	private float flapSpeed = 1.0F;
	private float field_28640 = 1.0F;
	private boolean songPlaying;
	@Nullable
	private BlockPos songSource;

	public ParrotEntity(EntityType<? extends ParrotEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new FlightMoveControl(this, 10, false);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
		this.setPathfindingPenalty(PathNodeType.COCOA, -1.0F);
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		this.setVariant(Util.getRandom(ParrotEntity.Variant.values(), world.getRandom()));
		if (entityData == null) {
			entityData = new PassiveEntity.PassiveData(false);
		}

		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	@Override
	public boolean isBaby() {
		return false;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new TameableEntity.TameableEscapeDangerGoal(1.25));
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(2, new SitGoal(this));
		this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0, 5.0F, 1.0F));
		this.goalSelector.add(2, new ParrotEntity.FlyOntoTreeGoal(this, 1.0));
		this.goalSelector.add(3, new SitOnOwnerShoulderGoal(this));
		this.goalSelector.add(3, new FollowMobGoal(this, 1.0, 3.0F, 7.0F));
	}

	public static DefaultAttributeContainer.Builder createParrotAttributes() {
		return AnimalEntity.createAnimalAttributes()
			.add(EntityAttributes.MAX_HEALTH, 6.0)
			.add(EntityAttributes.FLYING_SPEED, 0.4F)
			.add(EntityAttributes.MOVEMENT_SPEED, 0.2F)
			.add(EntityAttributes.ATTACK_DAMAGE, 3.0);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		BirdNavigation birdNavigation = new BirdNavigation(this, world);
		birdNavigation.setCanPathThroughDoors(false);
		birdNavigation.setCanSwim(true);
		birdNavigation.setCanEnterOpenDoors(true);
		return birdNavigation;
	}

	@Override
	public void tickMovement() {
		if (this.songSource == null || !this.songSource.isWithinDistance(this.getPos(), 3.46) || !this.getWorld().getBlockState(this.songSource).isOf(Blocks.JUKEBOX)
			)
		 {
			this.songPlaying = false;
			this.songSource = null;
		}

		if (this.getWorld().random.nextInt(400) == 0) {
			imitateNearbyMob(this.getWorld(), this);
		}

		super.tickMovement();
		this.flapWings();
	}

	@Override
	public void setNearbySongPlaying(BlockPos songPosition, boolean playing) {
		this.songSource = songPosition;
		this.songPlaying = playing;
	}

	public boolean isSongPlaying() {
		return this.songPlaying;
	}

	private void flapWings() {
		this.prevFlapProgress = this.flapProgress;
		this.prevMaxWingDeviation = this.maxWingDeviation;
		this.maxWingDeviation = this.maxWingDeviation + (float)(!this.isOnGround() && !this.hasVehicle() ? 4 : -1) * 0.3F;
		this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0F, 1.0F);
		if (!this.isOnGround() && this.flapSpeed < 1.0F) {
			this.flapSpeed = 1.0F;
		}

		this.flapSpeed *= 0.9F;
		Vec3d vec3d = this.getVelocity();
		if (!this.isOnGround() && vec3d.y < 0.0) {
			this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
		}

		this.flapProgress = this.flapProgress + this.flapSpeed * 2.0F;
	}

	public static boolean imitateNearbyMob(World world, Entity parrot) {
		if (parrot.isAlive() && !parrot.isSilent() && world.random.nextInt(2) == 0) {
			List<MobEntity> list = world.getEntitiesByClass(MobEntity.class, parrot.getBoundingBox().expand(20.0), CAN_IMITATE);
			if (!list.isEmpty()) {
				MobEntity mobEntity = (MobEntity)list.get(world.random.nextInt(list.size()));
				if (!mobEntity.isSilent()) {
					SoundEvent soundEvent = getSound(mobEntity.getType());
					world.playSound(null, parrot.getX(), parrot.getY(), parrot.getZ(), soundEvent, parrot.getSoundCategory(), 0.7F, getSoundPitch(world.random));
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!this.isTamed() && itemStack.isIn(ItemTags.PARROT_FOOD)) {
			this.eat(player, hand, itemStack);
			if (!this.isSilent()) {
				this.getWorld()
					.playSound(
						null,
						this.getX(),
						this.getY(),
						this.getZ(),
						SoundEvents.ENTITY_PARROT_EAT,
						this.getSoundCategory(),
						1.0F,
						1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
					);
			}

			if (!this.getWorld().isClient) {
				if (this.random.nextInt(10) == 0) {
					this.setOwner(player);
					this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
				} else {
					this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_NEGATIVE_PLAYER_REACTION_PARTICLES);
				}
			}

			return ActionResult.SUCCESS;
		} else if (!itemStack.isIn(ItemTags.PARROT_POISONOUS_FOOD)) {
			if (!this.isInAir() && this.isTamed() && this.isOwner(player)) {
				if (!this.getWorld().isClient) {
					this.setSitting(!this.isSitting());
				}

				return ActionResult.SUCCESS;
			} else {
				return super.interactMob(player, hand);
			}
		} else {
			this.eat(player, hand, itemStack);
			this.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 900));
			if (player.isCreative() || !this.isInvulnerable()) {
				this.serverDamage(this.getDamageSources().playerAttack(player), Float.MAX_VALUE);
			}

			return ActionResult.SUCCESS;
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	public static boolean canSpawn(EntityType<ParrotEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return world.getBlockState(pos.down()).isIn(BlockTags.PARROTS_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
	}

	@Override
	public boolean canBreedWith(AnimalEntity other) {
		return false;
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return null;
	}

	@Nullable
	@Override
	public SoundEvent getAmbientSound() {
		return getRandomSound(this.getWorld(), this.getWorld().random);
	}

	public static SoundEvent getRandomSound(World world, Random random) {
		if (world.getDifficulty() != Difficulty.PEACEFUL && random.nextInt(1000) == 0) {
			List<EntityType<?>> list = Lists.<EntityType<?>>newArrayList(MOB_SOUNDS.keySet());
			return getSound((EntityType<?>)list.get(random.nextInt(list.size())));
		} else {
			return SoundEvents.ENTITY_PARROT_AMBIENT;
		}
	}

	private static SoundEvent getSound(EntityType<?> imitate) {
		return (SoundEvent)MOB_SOUNDS.getOrDefault(imitate, SoundEvents.ENTITY_PARROT_AMBIENT);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_PARROT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PARROT_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_PARROT_STEP, 0.15F, 1.0F);
	}

	@Override
	protected boolean isFlappingWings() {
		return this.speed > this.field_28640;
	}

	@Override
	protected void addFlapEffects() {
		this.playSound(SoundEvents.ENTITY_PARROT_FLY, 0.15F, 1.0F);
		this.field_28640 = this.speed + this.maxWingDeviation / 2.0F;
	}

	@Override
	public float getSoundPitch() {
		return getSoundPitch(this.random);
	}

	public static float getSoundPitch(Random random) {
		return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.NEUTRAL;
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	protected void pushAway(Entity entity) {
		if (!(entity instanceof PlayerEntity)) {
			super.pushAway(entity);
		}
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		if (this.isInvulnerableTo(world, source)) {
			return false;
		} else {
			this.setSitting(false);
			return super.damage(world, source, amount);
		}
	}

	public ParrotEntity.Variant getVariant() {
		return ParrotEntity.Variant.byIndex(this.dataTracker.get(VARIANT));
	}

	public void setVariant(ParrotEntity.Variant variant) {
		this.dataTracker.set(VARIANT, variant.id);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(VARIANT, 0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Variant", this.getVariant().id);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setVariant(ParrotEntity.Variant.byIndex(nbt.getInt("Variant")));
	}

	@Override
	public boolean isInAir() {
		return !this.isOnGround();
	}

	@Override
	protected boolean canTeleportOntoLeaves() {
		return true;
	}

	@Override
	public Vec3d getLeashOffset() {
		return new Vec3d(0.0, (double)(0.5F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
	}

	static class FlyOntoTreeGoal extends FlyGoal {
		public FlyOntoTreeGoal(PathAwareEntity pathAwareEntity, double d) {
			super(pathAwareEntity, d);
		}

		@Nullable
		@Override
		protected Vec3d getWanderTarget() {
			Vec3d vec3d = null;
			if (this.mob.isTouchingWater()) {
				vec3d = FuzzyTargeting.find(this.mob, 15, 15);
			}

			if (this.mob.getRandom().nextFloat() >= this.probability) {
				vec3d = this.locateTree();
			}

			return vec3d == null ? super.getWanderTarget() : vec3d;
		}

		@Nullable
		private Vec3d locateTree() {
			BlockPos blockPos = this.mob.getBlockPos();
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			BlockPos.Mutable mutable2 = new BlockPos.Mutable();

			for (BlockPos blockPos2 : BlockPos.iterate(
				MathHelper.floor(this.mob.getX() - 3.0),
				MathHelper.floor(this.mob.getY() - 6.0),
				MathHelper.floor(this.mob.getZ() - 3.0),
				MathHelper.floor(this.mob.getX() + 3.0),
				MathHelper.floor(this.mob.getY() + 6.0),
				MathHelper.floor(this.mob.getZ() + 3.0)
			)) {
				if (!blockPos.equals(blockPos2)) {
					BlockState blockState = this.mob.getWorld().getBlockState(mutable2.set(blockPos2, Direction.DOWN));
					boolean bl = blockState.getBlock() instanceof LeavesBlock || blockState.isIn(BlockTags.LOGS);
					if (bl && this.mob.getWorld().isAir(blockPos2) && this.mob.getWorld().isAir(mutable.set(blockPos2, Direction.UP))) {
						return Vec3d.ofBottomCenter(blockPos2);
					}
				}
			}

			return null;
		}
	}

	public static enum Variant implements StringIdentifiable {
		RED_BLUE(0, "red_blue"),
		BLUE(1, "blue"),
		GREEN(2, "green"),
		YELLOW_BLUE(3, "yellow_blue"),
		GRAY(4, "gray");

		public static final Codec<ParrotEntity.Variant> CODEC = StringIdentifiable.createCodec(ParrotEntity.Variant::values);
		private static final IntFunction<ParrotEntity.Variant> BY_ID = ValueLists.createIdToValueFunction(
			ParrotEntity.Variant::getId, values(), ValueLists.OutOfBoundsHandling.CLAMP
		);
		final int id;
		private final String name;

		private Variant(final int id, final String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return this.id;
		}

		public static ParrotEntity.Variant byIndex(int index) {
			return (ParrotEntity.Variant)BY_ID.apply(index);
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
