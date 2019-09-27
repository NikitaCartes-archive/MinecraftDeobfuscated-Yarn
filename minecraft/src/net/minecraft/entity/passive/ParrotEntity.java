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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LogBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FlyAroundGoal;
import net.minecraft.entity.ai.goal.FollowMobGoal;
import net.minecraft.entity.ai.goal.FollowOwnerFlyingGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SitOnOwnerShoulder;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ParrotEntity extends TameableShoulderEntity implements Flutterer {
	private static final TrackedData<Integer> ATTR_VARIANT = DataTracker.registerData(ParrotEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Predicate<MobEntity> CAN_IMITATE = new Predicate<MobEntity>() {
		public boolean method_6590(@Nullable MobEntity mobEntity) {
			return mobEntity != null && ParrotEntity.MOB_SOUNDS.containsKey(mobEntity.getType());
		}
	};
	private static final Item COOKIE = Items.COOKIE;
	private static final Set<Item> TAMING_INGREDIENTS = Sets.<Item>newHashSet(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
	private static final Map<EntityType<?>, SoundEvent> MOB_SOUNDS = SystemUtil.consume(Maps.<EntityType<?>, SoundEvent>newHashMap(), hashMap -> {
		hashMap.put(EntityType.BLAZE, SoundEvents.ENTITY_PARROT_IMITATE_BLAZE);
		hashMap.put(EntityType.CAVE_SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
		hashMap.put(EntityType.CREEPER, SoundEvents.ENTITY_PARROT_IMITATE_CREEPER);
		hashMap.put(EntityType.DROWNED, SoundEvents.ENTITY_PARROT_IMITATE_DROWNED);
		hashMap.put(EntityType.ELDER_GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_ELDER_GUARDIAN);
		hashMap.put(EntityType.ENDER_DRAGON, SoundEvents.ENTITY_PARROT_IMITATE_ENDER_DRAGON);
		hashMap.put(EntityType.ENDERMAN, SoundEvents.ENTITY_PARROT_IMITATE_ENDERMAN);
		hashMap.put(EntityType.ENDERMITE, SoundEvents.ENTITY_PARROT_IMITATE_ENDERMITE);
		hashMap.put(EntityType.EVOKER, SoundEvents.ENTITY_PARROT_IMITATE_EVOKER);
		hashMap.put(EntityType.GHAST, SoundEvents.ENTITY_PARROT_IMITATE_GHAST);
		hashMap.put(EntityType.GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_GUARDIAN);
		hashMap.put(EntityType.HUSK, SoundEvents.ENTITY_PARROT_IMITATE_HUSK);
		hashMap.put(EntityType.ILLUSIONER, SoundEvents.ENTITY_PARROT_IMITATE_ILLUSIONER);
		hashMap.put(EntityType.MAGMA_CUBE, SoundEvents.ENTITY_PARROT_IMITATE_MAGMA_CUBE);
		hashMap.put(EntityType.ZOMBIE_PIGMAN, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE_PIGMAN);
		hashMap.put(EntityType.PANDA, SoundEvents.ENTITY_PARROT_IMITATE_PANDA);
		hashMap.put(EntityType.PHANTOM, SoundEvents.ENTITY_PARROT_IMITATE_PHANTOM);
		hashMap.put(EntityType.PILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_PILLAGER);
		hashMap.put(EntityType.POLAR_BEAR, SoundEvents.ENTITY_PARROT_IMITATE_POLAR_BEAR);
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
		hashMap.put(EntityType.WOLF, SoundEvents.ENTITY_PARROT_IMITATE_WOLF);
		hashMap.put(EntityType.ZOMBIE, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE);
		hashMap.put(EntityType.ZOMBIE_VILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE_VILLAGER);
	});
	public float field_6818;
	public float field_6819;
	public float field_6827;
	public float field_6829;
	public float field_6824 = 1.0F;
	private boolean songPlaying;
	private BlockPos songSource;

	public ParrotEntity(EntityType<? extends ParrotEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new FlightMoveControl(this, 10, false);
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.setVariant(this.random.nextInt(5));
		if (entityData == null) {
			entityData = new PassiveEntity$1();
			((PassiveEntity$1)entityData).method_22434(false);
		}

		return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Override
	protected void initGoals() {
		this.sitGoal = new SitGoal(this);
		this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25));
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(2, this.sitGoal);
		this.goalSelector.add(2, new FollowOwnerFlyingGoal(this, 1.0, 5.0F, 1.0F));
		this.goalSelector.add(2, new FlyAroundGoal(this, 1.0));
		this.goalSelector.add(3, new SitOnOwnerShoulder(this));
		this.goalSelector.add(3, new FollowMobGoal(this, 1.0, 3.0F, 7.0F));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributes().register(EntityAttributes.FLYING_SPEED);
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(6.0);
		this.getAttributeInstance(EntityAttributes.FLYING_SPEED).setBaseValue(0.4F);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2F);
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
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return entityDimensions.height * 0.6F;
	}

	@Override
	public void tickMovement() {
		imitateNearbyMob(this.world, this);
		if (this.songSource == null
			|| !this.songSource.isWithinDistance(this.getPos(), 3.46)
			|| this.world.getBlockState(this.songSource).getBlock() != Blocks.JUKEBOX) {
			this.songPlaying = false;
			this.songSource = null;
		}

		super.tickMovement();
		this.method_6578();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setNearbySongPlaying(BlockPos blockPos, boolean bl) {
		this.songSource = blockPos;
		this.songPlaying = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean getSongPlaying() {
		return this.songPlaying;
	}

	private void method_6578() {
		this.field_6829 = this.field_6818;
		this.field_6827 = this.field_6819;
		this.field_6819 = (float)((double)this.field_6819 + (double)(!this.onGround && !this.hasVehicle() ? 4 : -1) * 0.3);
		this.field_6819 = MathHelper.clamp(this.field_6819, 0.0F, 1.0F);
		if (!this.onGround && this.field_6824 < 1.0F) {
			this.field_6824 = 1.0F;
		}

		this.field_6824 = (float)((double)this.field_6824 * 0.9);
		Vec3d vec3d = this.getVelocity();
		if (!this.onGround && vec3d.y < 0.0) {
			this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
		}

		this.field_6818 = this.field_6818 + this.field_6824 * 2.0F;
	}

	private static boolean imitateNearbyMob(World world, Entity entity) {
		if (entity.isAlive() && !entity.isSilent() && world.random.nextInt(50) == 0) {
			List<MobEntity> list = world.getEntities(MobEntity.class, entity.getBoundingBox().expand(20.0), CAN_IMITATE);
			if (!list.isEmpty()) {
				MobEntity mobEntity = (MobEntity)list.get(world.random.nextInt(list.size()));
				if (!mobEntity.isSilent()) {
					SoundEvent soundEvent = getSound(mobEntity.getType());
					world.playSound(null, entity.x, entity.y, entity.z, soundEvent, entity.getSoundCategory(), 0.7F, getSoundPitch(world.random));
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (!this.isTamed() && TAMING_INGREDIENTS.contains(itemStack.getItem())) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.decrement(1);
			}

			if (!this.isSilent()) {
				this.world
					.playSound(
						null,
						this.x,
						this.y,
						this.z,
						SoundEvents.ENTITY_PARROT_EAT,
						this.getSoundCategory(),
						1.0F,
						1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
					);
			}

			if (!this.world.isClient) {
				if (this.random.nextInt(10) == 0) {
					this.setOwner(playerEntity);
					this.showEmoteParticle(true);
					this.world.sendEntityStatus(this, (byte)7);
				} else {
					this.showEmoteParticle(false);
					this.world.sendEntityStatus(this, (byte)6);
				}
			}

			return true;
		} else if (itemStack.getItem() == COOKIE) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.decrement(1);
			}

			this.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 900));
			if (playerEntity.isCreative() || !this.isInvulnerable()) {
				this.damage(DamageSource.player(playerEntity), Float.MAX_VALUE);
			}

			return true;
		} else {
			if (!this.world.isClient && !this.isInAir() && this.isTamed() && this.isOwner(playerEntity)) {
				this.sitGoal.setEnabledWithOwner(!this.isSitting());
			}

			return super.interactMob(playerEntity, hand);
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return false;
	}

	public static boolean canSpawn(EntityType<ParrotEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		Block block = iWorld.getBlockState(blockPos.method_10074()).getBlock();
		return (block.matches(BlockTags.LEAVES) || block == Blocks.GRASS_BLOCK || block instanceof LogBlock || block == Blocks.AIR)
			&& iWorld.getBaseLightLevel(blockPos, 0) > 8;
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Override
	protected void fall(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
	}

	@Override
	public boolean canBreedWith(AnimalEntity animalEntity) {
		return false;
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		return null;
	}

	public static void playMobSound(World world, Entity entity) {
		if (!entity.isSilent() && !imitateNearbyMob(world, entity) && world.random.nextInt(200) == 0) {
			world.playSound(null, entity.x, entity.y, entity.z, getRandomSound(world.random), entity.getSoundCategory(), 1.0F, getSoundPitch(world.random));
		}
	}

	@Override
	public boolean tryAttack(Entity entity) {
		return entity.damage(DamageSource.mob(this), 3.0F);
	}

	@Nullable
	@Override
	public SoundEvent getAmbientSound() {
		return getRandomSound(this.random);
	}

	private static SoundEvent getRandomSound(Random random) {
		if (random.nextInt(1000) == 0) {
			List<EntityType<?>> list = Lists.<EntityType<?>>newArrayList(MOB_SOUNDS.keySet());
			return getSound((EntityType<?>)list.get(random.nextInt(list.size())));
		} else {
			return SoundEvents.ENTITY_PARROT_AMBIENT;
		}
	}

	public static SoundEvent getSound(EntityType<?> entityType) {
		return (SoundEvent)MOB_SOUNDS.getOrDefault(entityType, SoundEvents.ENTITY_PARROT_AMBIENT);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.ENTITY_PARROT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PARROT_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.ENTITY_PARROT_STEP, 0.15F, 1.0F);
	}

	@Override
	protected float playFlySound(float f) {
		this.playSound(SoundEvents.ENTITY_PARROT_FLY, 0.15F, 1.0F);
		return f + this.field_6819 / 2.0F;
	}

	@Override
	protected boolean hasWings() {
		return true;
	}

	@Override
	protected float getSoundPitch() {
		return getSoundPitch(this.random);
	}

	private static float getSoundPitch(Random random) {
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
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			if (this.sitGoal != null) {
				this.sitGoal.setEnabledWithOwner(false);
			}

			return super.damage(damageSource, f);
		}
	}

	public int getVariant() {
		return MathHelper.clamp(this.dataTracker.get(ATTR_VARIANT), 0, 4);
	}

	public void setVariant(int i) {
		this.dataTracker.set(ATTR_VARIANT, i);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(ATTR_VARIANT, 0);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Variant", this.getVariant());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setVariant(compoundTag.getInt("Variant"));
	}

	public boolean isInAir() {
		return !this.onGround;
	}
}
