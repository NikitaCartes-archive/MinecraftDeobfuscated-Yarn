package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FlyOntoTreeGoal;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ParrotEntity extends TameableShoulderEntity implements Flutterer {
	private static final TrackedData<Integer> VARIANT = DataTracker.registerData(ParrotEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Predicate<MobEntity> CAN_IMITATE = new Predicate<MobEntity>() {
		public boolean test(@Nullable MobEntity mobEntity) {
			return mobEntity != null && ParrotEntity.MOB_SOUNDS.containsKey(mobEntity.getType());
		}
	};
	private static final Item COOKIE = Items.COOKIE;
	private static final Set<Item> TAMING_INGREDIENTS = Sets.<Item>newHashSet(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
	private static final Map<EntityType<?>, SoundEvent> MOB_SOUNDS = Util.make(Maps.<EntityType<?>, SoundEvent>newHashMap(), hashMap -> {
		hashMap.put(EntityType.BLAZE, SoundEvents.ENTITY_PARROT_IMITATE_BLAZE);
		hashMap.put(EntityType.CAVE_SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
		hashMap.put(EntityType.CREEPER, SoundEvents.ENTITY_PARROT_IMITATE_CREEPER);
		hashMap.put(EntityType.DROWNED, SoundEvents.ENTITY_PARROT_IMITATE_DROWNED);
		hashMap.put(EntityType.ELDER_GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_ELDER_GUARDIAN);
		hashMap.put(EntityType.ENDER_DRAGON, SoundEvents.ENTITY_PARROT_IMITATE_ENDER_DRAGON);
		hashMap.put(EntityType.ENDERMITE, SoundEvents.ENTITY_PARROT_IMITATE_ENDERMITE);
		hashMap.put(EntityType.EVOKER, SoundEvents.ENTITY_PARROT_IMITATE_EVOKER);
		hashMap.put(EntityType.GHAST, SoundEvents.ENTITY_PARROT_IMITATE_GHAST);
		hashMap.put(EntityType.GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_GUARDIAN);
		hashMap.put(EntityType.HOGLIN, SoundEvents.ENTITY_PARROT_IMITATE_HOGLIN);
		hashMap.put(EntityType.HUSK, SoundEvents.ENTITY_PARROT_IMITATE_HUSK);
		hashMap.put(EntityType.ILLUSIONER, SoundEvents.ENTITY_PARROT_IMITATE_ILLUSIONER);
		hashMap.put(EntityType.MAGMA_CUBE, SoundEvents.ENTITY_PARROT_IMITATE_MAGMA_CUBE);
		hashMap.put(EntityType.PHANTOM, SoundEvents.ENTITY_PARROT_IMITATE_PHANTOM);
		hashMap.put(EntityType.PIGLIN, SoundEvents.ENTITY_PARROT_IMITATE_PIGLIN);
		hashMap.put(EntityType.PIGLIN_BRUTE, SoundEvents.ENTITY_PARROT_IMITATE_PIGLIN_BRUTE);
		hashMap.put(EntityType.PILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_PILLAGER);
		hashMap.put(EntityType.RAVAGER, SoundEvents.ENTITY_PARROT_IMITATE_RAVAGER);
		hashMap.put(EntityType.SHULKER, SoundEvents.ENTITY_PARROT_IMITATE_SHULKER);
		hashMap.put(EntityType.SILVERFISH, SoundEvents.ENTITY_PARROT_IMITATE_SILVERFISH);
		hashMap.put(EntityType.SKELETON, SoundEvents.ENTITY_PARROT_IMITATE_SKELETON);
		hashMap.put(EntityType.SLIME, SoundEvents.ENTITY_PARROT_IMITATE_SLIME);
		hashMap.put(EntityType.SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
		hashMap.put(EntityType.STRAY, SoundEvents.ENTITY_PARROT_IMITATE_STRAY);
		hashMap.put(EntityType.VEX, SoundEvents.ENTITY_PARROT_IMITATE_VEX);
		hashMap.put(EntityType.VINDICATOR, SoundEvents.ENTITY_PARROT_IMITATE_VINDICATOR);
		hashMap.put(EntityType.WITCH, SoundEvents.ENTITY_PARROT_IMITATE_WITCH);
		hashMap.put(EntityType.WITHER, SoundEvents.ENTITY_PARROT_IMITATE_WITHER);
		hashMap.put(EntityType.WITHER_SKELETON, SoundEvents.ENTITY_PARROT_IMITATE_WITHER_SKELETON);
		hashMap.put(EntityType.ZOGLIN, SoundEvents.ENTITY_PARROT_IMITATE_ZOGLIN);
		hashMap.put(EntityType.ZOMBIE, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE);
		hashMap.put(EntityType.ZOMBIE_VILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE_VILLAGER);
	});
	public float flapProgress;
	public float maxWingDeviation;
	public float prevMaxWingDeviation;
	public float prevFlapProgress;
	private float flapSpeed = 1.0F;
	private float field_28640 = 1.0F;
	private boolean songPlaying;
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
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		this.setVariant(this.random.nextInt(5));
		if (entityData == null) {
			entityData = new PassiveEntity.PassiveData(false);
		}

		return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
	}

	@Override
	public boolean isBaby() {
		return false;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25));
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(2, new SitGoal(this));
		this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0, 5.0F, 1.0F, true));
		this.goalSelector.add(2, new FlyOntoTreeGoal(this, 1.0));
		this.goalSelector.add(3, new SitOnOwnerShoulderGoal(this));
		this.goalSelector.add(3, new FollowMobGoal(this, 1.0, 3.0F, 7.0F));
	}

	public static DefaultAttributeContainer.Builder createParrotAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0)
			.add(EntityAttributes.GENERIC_FLYING_SPEED, 0.4F)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F);
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
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.6F;
	}

	@Override
	public void tickMovement() {
		if (this.songSource == null || !this.songSource.isWithinDistance(this.getPos(), 3.46) || !this.world.getBlockState(this.songSource).isOf(Blocks.JUKEBOX)) {
			this.songPlaying = false;
			this.songSource = null;
		}

		if (this.world.random.nextInt(400) == 0) {
			imitateNearbyMob(this.world, this);
		}

		super.tickMovement();
		this.flapWings();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setNearbySongPlaying(BlockPos songPosition, boolean playing) {
		this.songSource = songPosition;
		this.songPlaying = playing;
	}

	@Environment(EnvType.CLIENT)
	public boolean isSongPlaying() {
		return this.songPlaying;
	}

	private void flapWings() {
		this.prevFlapProgress = this.flapProgress;
		this.prevMaxWingDeviation = this.maxWingDeviation;
		this.maxWingDeviation = (float)((double)this.maxWingDeviation + (double)(!this.onGround && !this.hasVehicle() ? 4 : -1) * 0.3);
		this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0F, 1.0F);
		if (!this.onGround && this.flapSpeed < 1.0F) {
			this.flapSpeed = 1.0F;
		}

		this.flapSpeed = (float)((double)this.flapSpeed * 0.9);
		Vec3d vec3d = this.getVelocity();
		if (!this.onGround && vec3d.y < 0.0) {
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
		if (!this.isTamed() && TAMING_INGREDIENTS.contains(itemStack.getItem())) {
			if (!player.getAbilities().creativeMode) {
				itemStack.decrement(1);
			}

			if (!this.isSilent()) {
				this.world
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

			if (!this.world.isClient) {
				if (this.random.nextInt(10) == 0) {
					this.setOwner(player);
					this.world.sendEntityStatus(this, (byte)7);
				} else {
					this.world.sendEntityStatus(this, (byte)6);
				}
			}

			return ActionResult.success(this.world.isClient);
		} else if (itemStack.isOf(COOKIE)) {
			if (!player.getAbilities().creativeMode) {
				itemStack.decrement(1);
			}

			this.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 900));
			if (player.isCreative() || !this.isInvulnerable()) {
				this.damage(DamageSource.player(player), Float.MAX_VALUE);
			}

			return ActionResult.success(this.world.isClient);
		} else if (!this.isInAir() && this.isTamed() && this.isOwner(player)) {
			if (!this.world.isClient) {
				this.setSitting(!this.isSitting());
			}

			return ActionResult.success(this.world.isClient);
		} else {
			return super.interactMob(player, hand);
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	public static boolean canSpawn(EntityType<ParrotEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		BlockState blockState = world.getBlockState(pos.down());
		return (blockState.isIn(BlockTags.LEAVES) || blockState.isOf(Blocks.GRASS_BLOCK) || blockState.isIn(BlockTags.LOGS) || blockState.isOf(Blocks.AIR))
			&& world.getBaseLightLevel(pos, 0) > 8;
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
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

	@Override
	public boolean tryAttack(Entity target) {
		return target.damage(DamageSource.mob(this), 3.0F);
	}

	@Nullable
	@Override
	public SoundEvent getAmbientSound() {
		return getRandomSound(this.world, this.world.random);
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
	protected boolean hasWings() {
		return this.field_28627 > this.field_28640;
	}

	@Override
	protected void addFlapEffects() {
		this.playSound(SoundEvents.ENTITY_PARROT_FLY, 0.15F, 1.0F);
		this.field_28640 = this.field_28627 + this.maxWingDeviation / 2.0F;
	}

	@Override
	protected float getSoundPitch() {
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
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.setSitting(false);
			return super.damage(source, amount);
		}
	}

	public int getVariant() {
		return MathHelper.clamp(this.dataTracker.get(VARIANT), 0, 4);
	}

	public void setVariant(int variant) {
		this.dataTracker.set(VARIANT, variant);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(VARIANT, 0);
	}

	@Override
	public void writeCustomDataToNbt(CompoundTag tag) {
		super.writeCustomDataToNbt(tag);
		tag.putInt("Variant", this.getVariant());
	}

	@Override
	public void readCustomDataFromNbt(CompoundTag tag) {
		super.readCustomDataFromNbt(tag);
		this.setVariant(tag.getInt("Variant"));
	}

	public boolean isInAir() {
		return !this.onGround;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d method_29919() {
		return new Vec3d(0.0, (double)(0.5F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
	}
}
