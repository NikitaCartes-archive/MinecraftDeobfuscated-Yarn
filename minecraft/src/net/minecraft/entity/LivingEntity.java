package net.minecraft.entity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.UseAction;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;

/**
 * Represents an entity which has a health value and can receive damage.
 */
public abstract class LivingEntity extends Entity implements Attackable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String ACTIVE_EFFECTS_NBT_KEY = "active_effects";
	private static final UUID SOUL_SPEED_BOOST_ID = UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038");
	private static final UUID POWDER_SNOW_SLOW_ID = UUID.fromString("1eaf83ff-7207-4596-b37a-d7a07b3ec4ce");
	private static final EntityAttributeModifier SPRINTING_SPEED_BOOST = new EntityAttributeModifier(
		UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D"), "Sprinting speed boost", 0.3F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
	);
	public static final int field_30069 = 2;
	public static final int field_30070 = 4;
	public static final int EQUIPMENT_SLOT_ID = 98;
	public static final int field_30072 = 100;
	public static final int field_48827 = 105;
	public static final int GLOWING_FLAG = 6;
	public static final int field_30074 = 100;
	private static final int field_30078 = 40;
	public static final double field_30075 = 0.003;
	public static final double GRAVITY = 0.08;
	public static final int DEATH_TICKS = 20;
	private static final int field_30080 = 10;
	private static final int field_30081 = 2;
	public static final int field_30063 = 4;
	public static final float field_44874 = 0.42F;
	private static final double MAX_ENTITY_VIEWING_DISTANCE = 128.0;
	protected static final int USING_ITEM_FLAG = 1;
	protected static final int OFF_HAND_ACTIVE_FLAG = 2;
	protected static final int USING_RIPTIDE_FLAG = 4;
	protected static final TrackedData<Byte> LIVING_FLAGS = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Float> HEALTH = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<List<ParticleEffect>> POTION_SWIRLS = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.PARTICLE_LIST);
	private static final TrackedData<Boolean> POTION_SWIRLS_AMBIENT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> STUCK_ARROW_COUNT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> STINGER_COUNT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Optional<BlockPos>> SLEEPING_POSITION = DataTracker.registerData(
		LivingEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS
	);
	private static final int field_49793 = 15;
	protected static final EntityDimensions SLEEPING_DIMENSIONS = EntityDimensions.fixed(0.2F, 0.2F).withEyeHeight(0.2F);
	public static final float BABY_SCALE_FACTOR = 0.5F;
	public static final float field_47756 = 0.5F;
	private static final float field_49972 = 0.21875F;
	private final AttributeContainer attributes;
	private final DamageTracker damageTracker = new DamageTracker(this);
	private final Map<RegistryEntry<StatusEffect>, StatusEffectInstance> activeStatusEffects = Maps.<RegistryEntry<StatusEffect>, StatusEffectInstance>newHashMap();
	private final DefaultedList<ItemStack> syncedHandStacks = DefaultedList.ofSize(2, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> syncedArmorStacks = DefaultedList.ofSize(4, ItemStack.EMPTY);
	private ItemStack syncedBodyArmorStack = ItemStack.EMPTY;
	public boolean handSwinging;
	private boolean noDrag = false;
	public Hand preferredHand;
	public int handSwingTicks;
	public int stuckArrowTimer;
	public int stuckStingerTimer;
	public int hurtTime;
	public int maxHurtTime;
	public int deathTime;
	public float lastHandSwingProgress;
	public float handSwingProgress;
	protected int lastAttackedTicks;
	public final LimbAnimator limbAnimator = new LimbAnimator();
	public final int defaultMaxHealth = 20;
	public final float randomLargeSeed;
	public final float randomSmallSeed;
	public float bodyYaw;
	public float prevBodyYaw;
	public float headYaw;
	public float prevHeadYaw;
	@Nullable
	protected PlayerEntity attackingPlayer;
	protected int playerHitTimer;
	protected boolean dead;
	protected int despawnCounter;
	protected float prevStepBobbingAmount;
	protected float stepBobbingAmount;
	protected float lookDirection;
	protected float prevLookDirection;
	protected float field_6215;
	protected int scoreAmount;
	protected float lastDamageTaken;
	protected boolean jumping;
	public float sidewaysSpeed;
	public float upwardSpeed;
	public float forwardSpeed;
	protected int bodyTrackingIncrements;
	protected double serverX;
	protected double serverY;
	protected double serverZ;
	protected double serverYaw;
	protected double serverPitch;
	protected double serverHeadYaw;
	protected int headTrackingIncrements;
	private boolean effectsChanged = true;
	@Nullable
	private LivingEntity attacker;
	private int lastAttackedTime;
	private LivingEntity attacking;
	private int lastAttackTime;
	private float movementSpeed;
	private int jumpingCooldown;
	private float absorptionAmount;
	protected ItemStack activeItemStack = ItemStack.EMPTY;
	protected int itemUseTimeLeft;
	protected int fallFlyingTicks;
	private BlockPos lastBlockPos;
	private Optional<BlockPos> climbingPos = Optional.empty();
	@Nullable
	private DamageSource lastDamageSource;
	private long lastDamageTime;
	protected int riptideTicks;
	private float leaningPitch;
	private float lastLeaningPitch;
	protected Brain<?> brain;
	private boolean experienceDroppingDisabled;
	protected float prevScale = 1.0F;

	protected LivingEntity(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
		this.attributes = new AttributeContainer(DefaultAttributeRegistry.get(entityType));
		this.setHealth(this.getMaxHealth());
		this.intersectionChecked = true;
		this.randomSmallSeed = (float)((Math.random() + 1.0) * 0.01F);
		this.refreshPosition();
		this.randomLargeSeed = (float)Math.random() * 12398.0F;
		this.setYaw((float)(Math.random() * (float) (Math.PI * 2)));
		this.headYaw = this.getYaw();
		NbtOps nbtOps = NbtOps.INSTANCE;
		this.brain = this.deserializeBrain(new Dynamic<>(nbtOps, nbtOps.createMap(ImmutableMap.of(nbtOps.createString("memories"), nbtOps.emptyMap()))));
	}

	public Brain<?> getBrain() {
		return this.brain;
	}

	protected Brain.Profile<?> createBrainProfile() {
		return Brain.createProfile(ImmutableList.of(), ImmutableList.of());
	}

	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return this.createBrainProfile().deserialize(dynamic);
	}

	@Override
	public void kill() {
		this.damage(this.getDamageSources().genericKill(), Float.MAX_VALUE);
	}

	public boolean canTarget(EntityType<?> type) {
		return true;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(LIVING_FLAGS, (byte)0);
		builder.add(POTION_SWIRLS, List.of());
		builder.add(POTION_SWIRLS_AMBIENT, false);
		builder.add(STUCK_ARROW_COUNT, 0);
		builder.add(STINGER_COUNT, 0);
		builder.add(HEALTH, 1.0F);
		builder.add(SLEEPING_POSITION, Optional.empty());
	}

	public static DefaultAttributeContainer.Builder createLivingAttributes() {
		return DefaultAttributeContainer.builder()
			.add(EntityAttributes.GENERIC_MAX_HEALTH)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED)
			.add(EntityAttributes.GENERIC_ARMOR)
			.add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)
			.add(EntityAttributes.GENERIC_MAX_ABSORPTION)
			.add(EntityAttributes.GENERIC_STEP_HEIGHT)
			.add(EntityAttributes.GENERIC_SCALE)
			.add(EntityAttributes.GENERIC_GRAVITY)
			.add(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE)
			.add(EntityAttributes.GENERIC_FALL_DAMAGE_MULTIPLIER)
			.add(EntityAttributes.GENERIC_JUMP_STRENGTH);
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
		if (!this.isTouchingWater()) {
			this.checkWaterState();
		}

		if (!this.getWorld().isClient && onGround && this.fallDistance > 0.0F) {
			this.removeSoulSpeedBoost();
			this.addSoulSpeedBoostIfNeeded();
			double d = this.getAttributeValue(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE);
			if ((double)this.fallDistance > d && !state.isAir()) {
				double e = this.getX();
				double f = this.getY();
				double g = this.getZ();
				BlockPos blockPos = this.getBlockPos();
				if (landedPosition.getX() != blockPos.getX() || landedPosition.getZ() != blockPos.getZ()) {
					double h = e - (double)landedPosition.getX() - 0.5;
					double i = g - (double)landedPosition.getZ() - 0.5;
					double j = Math.max(Math.abs(h), Math.abs(i));
					e = (double)landedPosition.getX() + 0.5 + h / j * 0.5;
					g = (double)landedPosition.getZ() + 0.5 + i / j * 0.5;
				}

				float k = (float)MathHelper.ceil((double)this.fallDistance - d);
				double l = Math.min((double)(0.2F + k / 15.0F), 2.5);
				int m = (int)(150.0 * l);
				((ServerWorld)this.getWorld()).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), e, f, g, m, 0.0, 0.0, 0.0, 0.15F);
			}
		}

		super.fall(heightDifference, onGround, state, landedPosition);
		if (onGround) {
			this.climbingPos = Optional.empty();
		}
	}

	public final boolean canBreatheInWater() {
		return this.getType().isIn(EntityTypeTags.CAN_BREATHE_UNDER_WATER);
	}

	public float getLeaningPitch(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastLeaningPitch, this.leaningPitch);
	}

	@Override
	public void baseTick() {
		this.lastHandSwingProgress = this.handSwingProgress;
		if (this.firstUpdate) {
			this.getSleepingPosition().ifPresent(this::setPositionInBed);
		}

		if (this.shouldDisplaySoulSpeedEffects()) {
			this.displaySoulSpeedEffects();
		}

		super.baseTick();
		this.getWorld().getProfiler().push("livingEntityBaseTick");
		if (this.isFireImmune() || this.getWorld().isClient) {
			this.extinguish();
		}

		if (this.isAlive()) {
			boolean bl = this instanceof PlayerEntity;
			if (!this.getWorld().isClient) {
				if (this.isInsideWall()) {
					this.damage(this.getDamageSources().inWall(), 1.0F);
				} else if (bl && !this.getWorld().getWorldBorder().contains(this.getBoundingBox())) {
					double d = this.getWorld().getWorldBorder().getDistanceInsideBorder(this) + this.getWorld().getWorldBorder().getSafeZone();
					if (d < 0.0) {
						double e = this.getWorld().getWorldBorder().getDamagePerBlock();
						if (e > 0.0) {
							this.damage(this.getDamageSources().outsideBorder(), (float)Math.max(1, MathHelper.floor(-d * e)));
						}
					}
				}
			}

			if (this.isSubmergedIn(FluidTags.WATER)
				&& !this.getWorld().getBlockState(BlockPos.ofFloored(this.getX(), this.getEyeY(), this.getZ())).isOf(Blocks.BUBBLE_COLUMN)) {
				boolean bl2 = !this.canBreatheInWater() && !StatusEffectUtil.hasWaterBreathing(this) && (!bl || !((PlayerEntity)this).getAbilities().invulnerable);
				if (bl2) {
					this.setAir(this.getNextAirUnderwater(this.getAir()));
					if (this.getAir() == -20) {
						this.setAir(0);
						Vec3d vec3d = this.getVelocity();

						for(int i = 0; i < 8; ++i) {
							double f = this.random.nextDouble() - this.random.nextDouble();
							double g = this.random.nextDouble() - this.random.nextDouble();
							double h = this.random.nextDouble() - this.random.nextDouble();
							this.getWorld().addParticle(ParticleTypes.BUBBLE, this.getX() + f, this.getY() + g, this.getZ() + h, vec3d.x, vec3d.y, vec3d.z);
						}

						this.damage(this.getDamageSources().drown(), 2.0F);
					}
				}

				if (!this.getWorld().isClient && this.hasVehicle() && this.getVehicle() != null && this.getVehicle().shouldDismountUnderwater()) {
					this.stopRiding();
				}
			} else if (this.getAir() < this.getMaxAir()) {
				this.setAir(this.getNextAirOnLand(this.getAir()));
			}

			if (!this.getWorld().isClient) {
				BlockPos blockPos = this.getBlockPos();
				if (!Objects.equal(this.lastBlockPos, blockPos)) {
					this.lastBlockPos = blockPos;
					this.applyMovementEffects(blockPos);
				}
			}
		}

		if (this.isAlive() && (this.isWet() || this.inPowderSnow)) {
			this.extinguishWithSound();
		}

		if (this.hurtTime > 0) {
			--this.hurtTime;
		}

		if (this.timeUntilRegen > 0 && !(this instanceof ServerPlayerEntity)) {
			--this.timeUntilRegen;
		}

		if (this.isDead() && this.getWorld().shouldUpdatePostDeath(this)) {
			this.updatePostDeath();
		}

		if (this.playerHitTimer > 0) {
			--this.playerHitTimer;
		} else {
			this.attackingPlayer = null;
		}

		if (this.attacking != null && !this.attacking.isAlive()) {
			this.attacking = null;
		}

		if (this.attacker != null) {
			if (!this.attacker.isAlive()) {
				this.setAttacker(null);
			} else if (this.age - this.lastAttackedTime > 100) {
				this.setAttacker(null);
			}
		}

		this.tickStatusEffects();
		this.prevLookDirection = this.lookDirection;
		this.prevBodyYaw = this.bodyYaw;
		this.prevHeadYaw = this.headYaw;
		this.prevYaw = this.getYaw();
		this.prevPitch = this.getPitch();
		this.getWorld().getProfiler().pop();
	}

	public boolean shouldDisplaySoulSpeedEffects() {
		return this.age % 5 == 0
			&& this.getVelocity().x != 0.0
			&& this.getVelocity().z != 0.0
			&& !this.isSpectator()
			&& EnchantmentHelper.hasSoulSpeed(this)
			&& this.isOnSoulSpeedBlock();
	}

	protected void displaySoulSpeedEffects() {
		Vec3d vec3d = this.getVelocity();
		this.getWorld()
			.addParticle(
				ParticleTypes.SOUL,
				this.getX() + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
				this.getY() + 0.1,
				this.getZ() + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
				vec3d.x * -0.2,
				0.1,
				vec3d.z * -0.2
			);
		float f = this.random.nextFloat() * 0.4F + this.random.nextFloat() > 0.9F ? 0.6F : 0.0F;
		this.playSound(SoundEvents.PARTICLE_SOUL_ESCAPE, f, 0.6F + this.random.nextFloat() * 0.4F);
	}

	protected boolean isOnSoulSpeedBlock() {
		return this.getWorld().getBlockState(this.getVelocityAffectingPos()).isIn(BlockTags.SOUL_SPEED_BLOCKS);
	}

	@Override
	protected float getVelocityMultiplier() {
		return this.isOnSoulSpeedBlock() && EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, this) > 0 ? 1.0F : super.getVelocityMultiplier();
	}

	protected boolean shouldRemoveSoulSpeedBoost(BlockState landingState) {
		return !landingState.isAir() || this.isFallFlying();
	}

	protected void removeSoulSpeedBoost() {
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (entityAttributeInstance != null) {
			if (entityAttributeInstance.getModifier(SOUL_SPEED_BOOST_ID) != null) {
				entityAttributeInstance.removeModifier(SOUL_SPEED_BOOST_ID);
			}
		}
	}

	protected void addSoulSpeedBoostIfNeeded() {
		if (!this.getLandingBlockState().isAir()) {
			int i = EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, this);
			if (i > 0 && this.isOnSoulSpeedBlock()) {
				EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
				if (entityAttributeInstance == null) {
					return;
				}

				entityAttributeInstance.addTemporaryModifier(
					new EntityAttributeModifier(
						SOUL_SPEED_BOOST_ID, "Soul speed boost", (double)(0.03F * (1.0F + (float)i * 0.35F)), EntityAttributeModifier.Operation.ADD_VALUE
					)
				);
				if (this.getRandom().nextFloat() < 0.04F) {
					ItemStack itemStack = this.getEquippedStack(EquipmentSlot.FEET);
					itemStack.damage(1, this, EquipmentSlot.FEET);
				}
			}
		}
	}

	protected void removePowderSnowSlow() {
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (entityAttributeInstance != null) {
			if (entityAttributeInstance.getModifier(POWDER_SNOW_SLOW_ID) != null) {
				entityAttributeInstance.removeModifier(POWDER_SNOW_SLOW_ID);
			}
		}
	}

	protected void addPowderSnowSlowIfNeeded() {
		if (!this.getLandingBlockState().isAir()) {
			int i = this.getFrozenTicks();
			if (i > 0) {
				EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
				if (entityAttributeInstance == null) {
					return;
				}

				float f = -0.05F * this.getFreezingScale();
				entityAttributeInstance.addTemporaryModifier(
					new EntityAttributeModifier(POWDER_SNOW_SLOW_ID, "Powder snow slow", (double)f, EntityAttributeModifier.Operation.ADD_VALUE)
				);
			}
		}
	}

	protected void applyMovementEffects(BlockPos pos) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.FROST_WALKER, this);
		if (i > 0) {
			FrostWalkerEnchantment.freezeWater(this, this.getWorld(), pos, i);
		}

		if (this.shouldRemoveSoulSpeedBoost(this.getLandingBlockState())) {
			this.removeSoulSpeedBoost();
		}

		this.addSoulSpeedBoostIfNeeded();
	}

	public boolean isBaby() {
		return false;
	}

	public float getScaleFactor() {
		return this.isBaby() ? 0.5F : 1.0F;
	}

	public float getScale() {
		AttributeContainer attributeContainer = this.getAttributes();
		return attributeContainer == null ? 1.0F : this.clampScale((float)attributeContainer.getValue(EntityAttributes.GENERIC_SCALE));
	}

	protected float clampScale(float scale) {
		return scale;
	}

	protected boolean shouldSwimInFluids() {
		return true;
	}

	protected void updatePostDeath() {
		++this.deathTime;
		if (this.deathTime >= 20 && !this.getWorld().isClient() && !this.isRemoved()) {
			this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_DEATH_PARTICLES);
			this.remove(Entity.RemovalReason.KILLED);
		}
	}

	/**
	 * Returns if this entity should drop experience on death when the {@linkplain
	 * net.minecraft.world.GameRules#DO_MOB_LOOT doMobLoot} game rule is
	 * enabled and has been attacked by a player.
	 * 
	 * <p>If {@link #shouldAlwaysDropXp() shouldAlwaysDropXp()} returns {@code
	 * true}, this check is disregarded.
	 * 
	 * @see #dropXp()
	 * @see #shouldAlwaysDropXp()
	 * @see #getXpToDrop()
	 */
	public boolean shouldDropXp() {
		return !this.isBaby();
	}

	protected boolean shouldDropLoot() {
		return !this.isBaby();
	}

	protected int getNextAirUnderwater(int air) {
		int i = EnchantmentHelper.getRespiration(this);
		return i > 0 && this.random.nextInt(i + 1) > 0 ? air : air - 1;
	}

	protected int getNextAirOnLand(int air) {
		return Math.min(air + 4, this.getMaxAir());
	}

	/**
	 * Called when this entity is killed and returns the amount of experience
	 * to drop.
	 * 
	 * @see #dropXp()
	 * @see #shouldAlwaysDropXp()
	 * @see #shouldDropXp()
	 */
	public int getXpToDrop() {
		return 0;
	}

	/**
	 * Returns if this entity may always drop experience, skipping any
	 * other checks.
	 * 
	 * @see #dropXp()
	 * @see #getXpToDrop()
	 */
	protected boolean shouldAlwaysDropXp() {
		return false;
	}

	public Random getRandom() {
		return this.random;
	}

	@Nullable
	public LivingEntity getAttacker() {
		return this.attacker;
	}

	@Override
	public LivingEntity getLastAttacker() {
		return this.getAttacker();
	}

	public int getLastAttackedTime() {
		return this.lastAttackedTime;
	}

	public void setAttacking(@Nullable PlayerEntity attacking) {
		this.attackingPlayer = attacking;
		this.playerHitTimer = this.age;
	}

	public void setAttacker(@Nullable LivingEntity attacker) {
		this.attacker = attacker;
		this.lastAttackedTime = this.age;
	}

	@Nullable
	public LivingEntity getAttacking() {
		return this.attacking;
	}

	public int getLastAttackTime() {
		return this.lastAttackTime;
	}

	public void onAttacking(Entity target) {
		if (target instanceof LivingEntity) {
			this.attacking = (LivingEntity)target;
		} else {
			this.attacking = null;
		}

		this.lastAttackTime = this.age;
	}

	public int getDespawnCounter() {
		return this.despawnCounter;
	}

	public void setDespawnCounter(int despawnCounter) {
		this.despawnCounter = despawnCounter;
	}

	public boolean hasNoDrag() {
		return this.noDrag;
	}

	public void setNoDrag(boolean noDrag) {
		this.noDrag = noDrag;
	}

	protected boolean isArmorSlot(EquipmentSlot slot) {
		return true;
	}

	public void onEquipStack(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack) {
		boolean bl = newStack.isEmpty() && oldStack.isEmpty();
		if (!bl && !ItemStack.areItemsAndComponentsEqual(oldStack, newStack) && !this.firstUpdate) {
			Equipment equipment = Equipment.fromStack(newStack);
			if (!this.getWorld().isClient() && !this.isSpectator()) {
				if (!this.isSilent() && equipment != null && equipment.getSlotType() == slot) {
					this.getWorld()
						.playSound(null, this.getX(), this.getY(), this.getZ(), equipment.getEquipSound(), this.getSoundCategory(), 1.0F, 1.0F, this.random.nextLong());
				}

				if (this.isArmorSlot(slot)) {
					this.emitGameEvent(equipment != null ? GameEvent.EQUIP : GameEvent.UNEQUIP);
				}
			}
		}
	}

	@Override
	public void remove(Entity.RemovalReason reason) {
		super.remove(reason);
		this.brain.forgetAll();
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putFloat("Health", this.getHealth());
		nbt.putShort("HurtTime", (short)this.hurtTime);
		nbt.putInt("HurtByTimestamp", this.lastAttackedTime);
		nbt.putShort("DeathTime", (short)this.deathTime);
		nbt.putFloat("AbsorptionAmount", this.getAbsorptionAmount());
		nbt.put("Attributes", this.getAttributes().toNbt());
		if (!this.activeStatusEffects.isEmpty()) {
			NbtList nbtList = new NbtList();

			for(StatusEffectInstance statusEffectInstance : this.activeStatusEffects.values()) {
				nbtList.add(statusEffectInstance.writeNbt());
			}

			nbt.put("active_effects", nbtList);
		}

		nbt.putBoolean("FallFlying", this.isFallFlying());
		this.getSleepingPosition().ifPresent(pos -> {
			nbt.putInt("SleepingX", pos.getX());
			nbt.putInt("SleepingY", pos.getY());
			nbt.putInt("SleepingZ", pos.getZ());
		});
		DataResult<NbtElement> dataResult = this.brain.encode(NbtOps.INSTANCE);
		dataResult.resultOrPartial(LOGGER::error).ifPresent(brain -> nbt.put("Brain", brain));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.setAbsorptionAmountUnclamped(nbt.getFloat("AbsorptionAmount"));
		if (nbt.contains("Attributes", NbtElement.LIST_TYPE) && this.getWorld() != null && !this.getWorld().isClient) {
			this.getAttributes().readNbt(nbt.getList("Attributes", NbtElement.COMPOUND_TYPE));
		}

		if (nbt.contains("active_effects", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("active_effects", NbtElement.COMPOUND_TYPE);

			for(int i = 0; i < nbtList.size(); ++i) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtCompound);
				if (statusEffectInstance != null) {
					this.activeStatusEffects.put(statusEffectInstance.getEffectType(), statusEffectInstance);
				}
			}
		}

		if (nbt.contains("Health", NbtElement.NUMBER_TYPE)) {
			this.setHealth(nbt.getFloat("Health"));
		}

		this.hurtTime = nbt.getShort("HurtTime");
		this.deathTime = nbt.getShort("DeathTime");
		this.lastAttackedTime = nbt.getInt("HurtByTimestamp");
		if (nbt.contains("Team", NbtElement.STRING_TYPE)) {
			String string = nbt.getString("Team");
			Scoreboard scoreboard = this.getWorld().getScoreboard();
			Team team = scoreboard.getTeam(string);
			boolean bl = team != null && scoreboard.addScoreHolderToTeam(this.getUuidAsString(), team);
			if (!bl) {
				LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", string);
			}
		}

		if (nbt.getBoolean("FallFlying")) {
			this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, true);
		}

		if (nbt.contains("SleepingX", NbtElement.NUMBER_TYPE)
			&& nbt.contains("SleepingY", NbtElement.NUMBER_TYPE)
			&& nbt.contains("SleepingZ", NbtElement.NUMBER_TYPE)) {
			BlockPos blockPos = new BlockPos(nbt.getInt("SleepingX"), nbt.getInt("SleepingY"), nbt.getInt("SleepingZ"));
			this.setSleepingPosition(blockPos);
			this.dataTracker.set(POSE, EntityPose.SLEEPING);
			if (!this.firstUpdate) {
				this.setPositionInBed(blockPos);
			}
		}

		if (nbt.contains("Brain", NbtElement.COMPOUND_TYPE)) {
			this.brain = this.deserializeBrain(new Dynamic<>(NbtOps.INSTANCE, nbt.get("Brain")));
		}
	}

	protected void tickStatusEffects() {
		Iterator<RegistryEntry<StatusEffect>> iterator = this.activeStatusEffects.keySet().iterator();

		try {
			while(iterator.hasNext()) {
				RegistryEntry<StatusEffect> registryEntry = (RegistryEntry)iterator.next();
				StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.activeStatusEffects.get(registryEntry);
				if (!statusEffectInstance.update(this, () -> this.onStatusEffectUpgraded(statusEffectInstance, true, null))) {
					if (!this.getWorld().isClient) {
						iterator.remove();
						this.onStatusEffectRemoved(statusEffectInstance);
					}
				} else if (statusEffectInstance.getDuration() % 600 == 0) {
					this.onStatusEffectUpgraded(statusEffectInstance, false, null);
				}
			}
		} catch (ConcurrentModificationException var6) {
		}

		if (this.effectsChanged) {
			if (!this.getWorld().isClient) {
				this.updatePotionVisibility();
				this.updateGlowing();
			}

			this.effectsChanged = false;
		}

		List<ParticleEffect> list = this.dataTracker.get(POTION_SWIRLS);
		if (!list.isEmpty()) {
			boolean bl = this.dataTracker.get(POTION_SWIRLS_AMBIENT);
			int i = this.isInvisible() ? 15 : 4;
			int j = bl ? 5 : 1;
			if (this.random.nextInt(i * j) == 0) {
				this.getWorld().addParticle(Util.getRandom(list, this.random), this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 1.0, 1.0, 1.0);
			}
		}
	}

	protected void updatePotionVisibility() {
		if (this.activeStatusEffects.isEmpty()) {
			this.clearPotionSwirls();
			this.setInvisible(false);
		} else {
			this.setInvisible(this.hasStatusEffect(StatusEffects.INVISIBILITY));
			this.updatePotionSwirls();
		}
	}

	private void updatePotionSwirls() {
		List<ParticleEffect> list = this.activeStatusEffects
			.values()
			.stream()
			.filter(StatusEffectInstance::shouldShowParticles)
			.map(StatusEffectInstance::createParticle)
			.toList();
		this.dataTracker.set(POTION_SWIRLS, list);
		this.dataTracker.set(POTION_SWIRLS_AMBIENT, containsOnlyAmbientEffects(this.activeStatusEffects.values()));
	}

	private void updateGlowing() {
		boolean bl = this.isGlowing();
		if (this.getFlag(Entity.GLOWING_FLAG_INDEX) != bl) {
			this.setFlag(Entity.GLOWING_FLAG_INDEX, bl);
		}
	}

	public double getAttackDistanceScalingFactor(@Nullable Entity entity) {
		double d = 1.0;
		if (this.isSneaky()) {
			d *= 0.8;
		}

		if (this.isInvisible()) {
			float f = this.getArmorVisibility();
			if (f < 0.1F) {
				f = 0.1F;
			}

			d *= 0.7 * (double)f;
		}

		if (entity != null) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
			EntityType<?> entityType = entity.getType();
			if (entityType == EntityType.SKELETON && itemStack.isOf(Items.SKELETON_SKULL)
				|| entityType == EntityType.ZOMBIE && itemStack.isOf(Items.ZOMBIE_HEAD)
				|| entityType == EntityType.PIGLIN && itemStack.isOf(Items.PIGLIN_HEAD)
				|| entityType == EntityType.PIGLIN_BRUTE && itemStack.isOf(Items.PIGLIN_HEAD)
				|| entityType == EntityType.CREEPER && itemStack.isOf(Items.CREEPER_HEAD)) {
				d *= 0.5;
			}
		}

		return d;
	}

	public boolean canTarget(LivingEntity target) {
		return target instanceof PlayerEntity && this.getWorld().getDifficulty() == Difficulty.PEACEFUL ? false : target.canTakeDamage();
	}

	public boolean isTarget(LivingEntity entity, TargetPredicate predicate) {
		return predicate.test(this, entity);
	}

	public boolean canTakeDamage() {
		return !this.isInvulnerable() && this.isPartOfGame();
	}

	public boolean isPartOfGame() {
		return !this.isSpectator() && this.isAlive();
	}

	public static boolean containsOnlyAmbientEffects(Collection<StatusEffectInstance> effects) {
		for(StatusEffectInstance statusEffectInstance : effects) {
			if (statusEffectInstance.shouldShowParticles() && !statusEffectInstance.isAmbient()) {
				return false;
			}
		}

		return true;
	}

	protected void clearPotionSwirls() {
		this.dataTracker.set(POTION_SWIRLS, List.of());
	}

	public boolean clearStatusEffects() {
		if (this.getWorld().isClient) {
			return false;
		} else {
			Iterator<StatusEffectInstance> iterator = this.activeStatusEffects.values().iterator();

			boolean bl;
			for(bl = false; iterator.hasNext(); bl = true) {
				this.onStatusEffectRemoved((StatusEffectInstance)iterator.next());
				iterator.remove();
			}

			return bl;
		}
	}

	public Collection<StatusEffectInstance> getStatusEffects() {
		return this.activeStatusEffects.values();
	}

	public Map<RegistryEntry<StatusEffect>, StatusEffectInstance> getActiveStatusEffects() {
		return this.activeStatusEffects;
	}

	public boolean hasStatusEffect(RegistryEntry<StatusEffect> effect) {
		return this.activeStatusEffects.containsKey(effect);
	}

	@Nullable
	public StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect) {
		return (StatusEffectInstance)this.activeStatusEffects.get(effect);
	}

	/**
	 * Adds a status effect to this entity without specifying a source entity.
	 * 
	 * <p>Consider calling {@link #addStatusEffect(StatusEffectInstance, Entity)}
	 * if the {@code effect} is caused by or from an entity.
	 * 
	 * @return whether the active status effects of this entity has been modified
	 * @see #addStatusEffect(StatusEffectInstance, Entity)
	 * 
	 * @param effect the effect to add
	 */
	public final boolean addStatusEffect(StatusEffectInstance effect) {
		return this.addStatusEffect(effect, null);
	}

	/**
	 * Adds a status effect to this entity.
	 * 
	 * @implNote A status effect may fail to be added due to getting overridden by
	 * existing effects or the effect being incompatible with this entity.
	 * 
	 * @return whether the active status effects of this entity has been modified
	 * 
	 * @param effect the effect to add
	 * @param source the source entity or {@code null} for non-entity sources
	 */
	public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
		if (!this.canHaveStatusEffect(effect)) {
			return false;
		} else {
			StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.activeStatusEffects.get(effect.getEffectType());
			boolean bl = false;
			if (statusEffectInstance == null) {
				this.activeStatusEffects.put(effect.getEffectType(), effect);
				this.onStatusEffectApplied(effect, source);
				bl = true;
			} else if (statusEffectInstance.upgrade(effect)) {
				this.onStatusEffectUpgraded(statusEffectInstance, true, source);
				bl = true;
			}

			effect.onApplied(this);
			return bl;
		}
	}

	public boolean canHaveStatusEffect(StatusEffectInstance effect) {
		if (!this.getType().isIn(EntityTypeTags.IGNORES_POISON_AND_REGEN)) {
			return true;
		} else {
			return !effect.equals(StatusEffects.REGENERATION) && !effect.equals(StatusEffects.POISON);
		}
	}

	/**
	 * Sets a status effect in this entity.
	 * 
	 * <p>The preexistent status effect of the same type on this entity, if there is one, is cleared.
	 * To actually add a status effect and undergo effect combination logic, call
	 * {@link #addStatusEffect(StatusEffectInstance, Entity)}.
	 * 
	 * @apiNote In vanilla, this is exclusively used by the client to set a status
	 * effect on the player upon {@linkplain
	 * net.minecraft.client.network.ClientPlayNetworkHandler#onEntityStatusEffect
	 * reception} of the status effect packet.
	 * 
	 * @param source the source entity or {@code null} for non-entity sources
	 * @param effect the effect to set
	 */
	public void setStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
		if (this.canHaveStatusEffect(effect)) {
			StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.activeStatusEffects.put(effect.getEffectType(), effect);
			if (statusEffectInstance == null) {
				this.onStatusEffectApplied(effect, source);
			} else {
				effect.copyFadingFrom(statusEffectInstance);
				this.onStatusEffectUpgraded(effect, true, source);
			}
		}
	}

	public boolean hasInvertedHealingAndHarm() {
		return this.getType().isIn(EntityTypeTags.INVERTED_HEALING_AND_HARM);
	}

	/**
	 * Removes a status effect from this entity without calling any listener.
	 * 
	 * <p>This method does not perform any cleanup or synchronization operation.
	 * Under most circumstances, calling {@link #removeStatusEffect(RegistryEntry)} is highly preferable.
	 * 
	 * @return the status effect removed
	 */
	@Nullable
	public StatusEffectInstance removeStatusEffectInternal(RegistryEntry<StatusEffect> effect) {
		return (StatusEffectInstance)this.activeStatusEffects.remove(effect);
	}

	/**
	 * Removes a status effect from this entity.
	 * 
	 * <p>Calling this method will call cleanup methods on the status effect and trigger synchronization of effect particles with watching clients. If this entity is a player,
	 * the change in the list of effects will also be synchronized with the corresponding client.
	 * 
	 * @return whether the active status effects on this entity has been changed by
	 * this call
	 */
	public boolean removeStatusEffect(RegistryEntry<StatusEffect> effect) {
		StatusEffectInstance statusEffectInstance = this.removeStatusEffectInternal(effect);
		if (statusEffectInstance != null) {
			this.onStatusEffectRemoved(statusEffectInstance);
			return true;
		} else {
			return false;
		}
	}

	protected void onStatusEffectApplied(StatusEffectInstance effect, @Nullable Entity source) {
		this.effectsChanged = true;
		if (!this.getWorld().isClient) {
			effect.getEffectType().value().onApplied(this.getAttributes(), effect.getAmplifier());
			this.sendEffectToControllingPlayer(effect);
		}
	}

	public void sendEffectToControllingPlayer(StatusEffectInstance effect) {
		for(Entity entity : this.getPassengerList()) {
			if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
				serverPlayerEntity.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(this.getId(), effect, false));
			}
		}
	}

	protected void onStatusEffectUpgraded(StatusEffectInstance effect, boolean reapplyEffect, @Nullable Entity source) {
		this.effectsChanged = true;
		if (reapplyEffect && !this.getWorld().isClient) {
			StatusEffect statusEffect = effect.getEffectType().value();
			statusEffect.onRemoved(this.getAttributes());
			statusEffect.onApplied(this.getAttributes(), effect.getAmplifier());
			this.updateAttributes();
		}

		if (!this.getWorld().isClient) {
			this.sendEffectToControllingPlayer(effect);
		}
	}

	protected void onStatusEffectRemoved(StatusEffectInstance effect) {
		this.effectsChanged = true;
		if (!this.getWorld().isClient) {
			effect.getEffectType().value().onRemoved(this.getAttributes());
			this.updateAttributes();

			for(Entity entity : this.getPassengerList()) {
				if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
					serverPlayerEntity.networkHandler.sendPacket(new RemoveEntityStatusEffectS2CPacket(this.getId(), effect.getEffectType()));
				}
			}
		}
	}

	private void updateAttributes() {
		for(EntityAttributeInstance entityAttributeInstance : this.getAttributes().getTracked()) {
			this.updateAttribute(entityAttributeInstance.getAttribute());
		}
	}

	private void updateAttribute(RegistryEntry<EntityAttribute> attribute) {
		if (attribute.matches(EntityAttributes.GENERIC_MAX_HEALTH)) {
			float f = this.getMaxHealth();
			if (this.getHealth() > f) {
				this.setHealth(f);
			}
		} else if (attribute.matches(EntityAttributes.GENERIC_MAX_ABSORPTION)) {
			float f = this.getMaxAbsorption();
			if (this.getAbsorptionAmount() > f) {
				this.setAbsorptionAmount(f);
			}
		}
	}

	/**
	 * Heals this entity by the given {@code amount} of half-hearts.
	 * 
	 * <p>A dead entity cannot be healed.
	 * 
	 * @see #isDead()
	 */
	public void heal(float amount) {
		float f = this.getHealth();
		if (f > 0.0F) {
			this.setHealth(f + amount);
		}
	}

	public float getHealth() {
		return this.dataTracker.get(HEALTH);
	}

	public void setHealth(float health) {
		this.dataTracker.set(HEALTH, MathHelper.clamp(health, 0.0F, this.getMaxHealth()));
	}

	public boolean isDead() {
		return this.getHealth() <= 0.0F;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (this.getWorld().isClient) {
			return false;
		} else if (this.isDead()) {
			return false;
		} else if (source.isIn(DamageTypeTags.IS_FIRE) && this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
			return false;
		} else {
			if (this.isSleeping() && !this.getWorld().isClient) {
				this.wakeUp();
			}

			this.despawnCounter = 0;
			float f = amount;
			boolean bl = false;
			float g = 0.0F;
			if (amount > 0.0F && this.blockedByShield(source)) {
				this.damageShield(amount);
				g = amount;
				amount = 0.0F;
				if (!source.isIn(DamageTypeTags.IS_PROJECTILE)) {
					Entity entity = source.getSource();
					if (entity instanceof LivingEntity livingEntity) {
						this.takeShieldHit(livingEntity);
					}
				}

				bl = true;
			}

			if (source.isIn(DamageTypeTags.IS_FREEZING) && this.getType().isIn(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
				amount *= 5.0F;
			}

			if (source.isIn(DamageTypeTags.DAMAGES_HELMET) && !this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
				this.damageHelmet(source, amount);
				amount *= 0.75F;
			}

			this.limbAnimator.setSpeed(1.5F);
			boolean bl2 = true;
			if ((float)this.timeUntilRegen > 10.0F && !source.isIn(DamageTypeTags.BYPASSES_COOLDOWN)) {
				if (amount <= this.lastDamageTaken) {
					return false;
				}

				this.applyDamage(source, amount - this.lastDamageTaken);
				this.lastDamageTaken = amount;
				bl2 = false;
			} else {
				this.lastDamageTaken = amount;
				this.timeUntilRegen = 20;
				this.applyDamage(source, amount);
				this.maxHurtTime = 10;
				this.hurtTime = this.maxHurtTime;
			}

			Entity entity2 = source.getAttacker();
			if (entity2 != null) {
				if (entity2 instanceof LivingEntity livingEntity2
					&& !source.isIn(DamageTypeTags.NO_ANGER)
					&& (!source.isOf(DamageTypes.WIND_CHARGE) || !this.getType().isIn(EntityTypeTags.NO_ANGER_FROM_WIND_CHARGE))) {
					this.setAttacker(livingEntity2);
				}

				if (entity2 instanceof PlayerEntity playerEntity) {
					this.playerHitTimer = 100;
					this.attackingPlayer = playerEntity;
				} else if (entity2 instanceof WolfEntity wolfEntity && wolfEntity.isTamed()) {
					this.playerHitTimer = 100;
					LivingEntity var11 = wolfEntity.getOwner();
					if (var11 instanceof PlayerEntity playerEntity2) {
						this.attackingPlayer = playerEntity2;
					} else {
						this.attackingPlayer = null;
					}
				}
			}

			if (bl2) {
				if (bl) {
					this.getWorld().sendEntityStatus(this, EntityStatuses.BLOCK_WITH_SHIELD);
				} else {
					this.getWorld().sendEntityDamage(this, source);
				}

				if (!source.isIn(DamageTypeTags.NO_IMPACT) && (!bl || amount > 0.0F)) {
					this.scheduleVelocityUpdate();
				}

				if (entity2 != null && !source.isIn(DamageTypeTags.NO_KNOCKBACK)) {
					double d = entity2.getX() - this.getX();

					double e;
					for(e = entity2.getZ() - this.getZ(); d * d + e * e < 1.0E-4; e = (Math.random() - Math.random()) * 0.01) {
						d = (Math.random() - Math.random()) * 0.01;
					}

					this.takeKnockback(0.4F, d, e);
					if (!bl) {
						this.tiltScreen(d, e);
					}
				}
			}

			if (this.isDead()) {
				if (!this.tryUseTotem(source)) {
					if (bl2) {
						this.playSound(this.getDeathSound());
					}

					this.onDeath(source);
				}
			} else if (bl2) {
				this.playHurtSound(source);
			}

			boolean bl3 = !bl || amount > 0.0F;
			if (bl3) {
				this.lastDamageSource = source;
				this.lastDamageTime = this.getWorld().getTime();
			}

			if (this instanceof ServerPlayerEntity) {
				Criteria.ENTITY_HURT_PLAYER.trigger((ServerPlayerEntity)this, source, f, amount, bl);
				if (g > 0.0F && g < 3.4028235E37F) {
					((ServerPlayerEntity)this).increaseStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(g * 10.0F));
				}
			}

			if (entity2 instanceof ServerPlayerEntity) {
				Criteria.PLAYER_HURT_ENTITY.trigger((ServerPlayerEntity)entity2, this, source, f, amount, bl);
			}

			return bl3;
		}
	}

	protected void takeShieldHit(LivingEntity attacker) {
		attacker.knockback(this);
	}

	protected void knockback(LivingEntity target) {
		target.takeKnockback(0.5, target.getX() - this.getX(), target.getZ() - this.getZ());
	}

	private boolean tryUseTotem(DamageSource source) {
		if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return false;
		} else {
			ItemStack itemStack = null;

			for(Hand hand : Hand.values()) {
				ItemStack itemStack2 = this.getStackInHand(hand);
				if (itemStack2.isOf(Items.TOTEM_OF_UNDYING)) {
					itemStack = itemStack2.copy();
					itemStack2.decrement(1);
					break;
				}
			}

			if (itemStack != null) {
				if (this instanceof ServerPlayerEntity serverPlayerEntity) {
					serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(Items.TOTEM_OF_UNDYING));
					Criteria.USED_TOTEM.trigger(serverPlayerEntity, itemStack);
					this.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
				}

				this.setHealth(1.0F);
				this.clearStatusEffects();
				this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
				this.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
				this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
				this.getWorld().sendEntityStatus(this, EntityStatuses.USE_TOTEM_OF_UNDYING);
			}

			return itemStack != null;
		}
	}

	@Nullable
	public DamageSource getRecentDamageSource() {
		if (this.getWorld().getTime() - this.lastDamageTime > 40L) {
			this.lastDamageSource = null;
		}

		return this.lastDamageSource;
	}

	protected void playHurtSound(DamageSource damageSource) {
		this.playSound(this.getHurtSound(damageSource));
	}

	public void playSound(@Nullable SoundEvent sound) {
		if (sound != null) {
			this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	public boolean blockedByShield(DamageSource source) {
		Entity entity = source.getSource();
		boolean bl = false;
		if (entity instanceof PersistentProjectileEntity persistentProjectileEntity && persistentProjectileEntity.getPierceLevel() > 0) {
			bl = true;
		}

		if (!source.isIn(DamageTypeTags.BYPASSES_SHIELD) && this.isBlocking() && !bl) {
			Vec3d vec3d = source.getPosition();
			if (vec3d != null) {
				Vec3d vec3d2 = this.getRotationVector(0.0F, this.getHeadYaw());
				Vec3d vec3d3 = vec3d.relativize(this.getPos());
				vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z).normalize();
				return vec3d3.dotProduct(vec3d2) < 0.0;
			}
		}

		return false;
	}

	private void playEquipmentBreakEffects(ItemStack stack) {
		if (!stack.isEmpty()) {
			if (!this.isSilent()) {
				this.getWorld()
					.playSound(
						this.getX(), this.getY(), this.getZ(), stack.getBreakSound(), this.getSoundCategory(), 0.8F, 0.8F + this.getWorld().random.nextFloat() * 0.4F, false
					);
			}

			this.spawnItemParticles(stack, 5);
		}
	}

	public void onDeath(DamageSource damageSource) {
		if (!this.isRemoved() && !this.dead) {
			Entity entity = damageSource.getAttacker();
			LivingEntity livingEntity = this.getPrimeAdversary();
			if (this.scoreAmount >= 0 && livingEntity != null) {
				livingEntity.updateKilledAdvancementCriterion(this, this.scoreAmount, damageSource);
			}

			if (this.isSleeping()) {
				this.wakeUp();
			}

			if (!this.getWorld().isClient && this.hasCustomName()) {
				LOGGER.info("Named entity {} died: {}", this, this.getDamageTracker().getDeathMessage().getString());
			}

			this.dead = true;
			this.getDamageTracker().update();
			World var5 = this.getWorld();
			if (var5 instanceof ServerWorld serverWorld) {
				if (entity == null || entity.onKilledOther(serverWorld, this)) {
					this.emitGameEvent(GameEvent.ENTITY_DIE);
					this.drop(damageSource);
					this.onKilledBy(livingEntity);
				}

				this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
			}

			this.setPose(EntityPose.DYING);
		}
	}

	/**
	 * Performs secondary effects after this mob has been killed.
	 * 
	 * <p> The default behavior spawns a wither rose if {@code adversary} is a {@code WitherEntity}.
	 * 
	 * @param adversary the main adversary responsible for this entity's death
	 */
	protected void onKilledBy(@Nullable LivingEntity adversary) {
		if (!this.getWorld().isClient) {
			boolean bl = false;
			if (adversary instanceof WitherEntity) {
				if (this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
					BlockPos blockPos = this.getBlockPos();
					BlockState blockState = Blocks.WITHER_ROSE.getDefaultState();
					if (this.getWorld().getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.getWorld(), blockPos)) {
						this.getWorld().setBlockState(blockPos, blockState, Block.NOTIFY_ALL);
						bl = true;
					}
				}

				if (!bl) {
					ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), new ItemStack(Items.WITHER_ROSE));
					this.getWorld().spawnEntity(itemEntity);
				}
			}
		}
	}

	protected void drop(DamageSource source) {
		Entity entity = source.getAttacker();
		int i;
		if (entity instanceof PlayerEntity) {
			i = EnchantmentHelper.getLooting((LivingEntity)entity);
		} else {
			i = 0;
		}

		boolean bl = this.playerHitTimer > 0;
		if (this.shouldDropLoot() && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
			this.dropLoot(source, bl);
			this.dropEquipment(source, i, bl);
		}

		this.dropInventory();
		this.dropXp();
	}

	protected void dropInventory() {
	}

	/**
	 * Drops experience when this entity is killed.
	 * 
	 * <p>To control the details of experience dropping, consider overriding
	 * {@link #shouldAlwaysDropXp()}, {@link #shouldDropXp()}, and
	 * {@link #getXpToDrop()}.
	 */
	protected void dropXp() {
		if (this.getWorld() instanceof ServerWorld
			&& !this.isExperienceDroppingDisabled()
			&& (this.shouldAlwaysDropXp() || this.playerHitTimer > 0 && this.shouldDropXp() && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_LOOT))) {
			ExperienceOrbEntity.spawn((ServerWorld)this.getWorld(), this.getPos(), this.getXpToDrop());
		}
	}

	protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
	}

	public RegistryKey<LootTable> getLootTable() {
		return this.getType().getLootTableId();
	}

	public long getLootTableSeed() {
		return 0L;
	}

	protected void dropLoot(DamageSource damageSource, boolean causedByPlayer) {
		RegistryKey<LootTable> registryKey = this.getLootTable();
		LootTable lootTable = this.getWorld().getServer().getReloadableRegistries().getLootTable(registryKey);
		LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld)this.getWorld())
			.add(LootContextParameters.THIS_ENTITY, this)
			.add(LootContextParameters.ORIGIN, this.getPos())
			.add(LootContextParameters.DAMAGE_SOURCE, damageSource)
			.addOptional(LootContextParameters.KILLER_ENTITY, damageSource.getAttacker())
			.addOptional(LootContextParameters.DIRECT_KILLER_ENTITY, damageSource.getSource());
		if (causedByPlayer && this.attackingPlayer != null) {
			builder = builder.add(LootContextParameters.LAST_DAMAGE_PLAYER, this.attackingPlayer).luck(this.attackingPlayer.getLuck());
		}

		LootContextParameterSet lootContextParameterSet = builder.build(LootContextTypes.ENTITY);
		lootTable.generateLoot(lootContextParameterSet, this.getLootTableSeed(), this::dropStack);
	}

	public void takeKnockback(double strength, double x, double z) {
		strength *= 1.0 - this.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
		if (!(strength <= 0.0)) {
			this.velocityDirty = true;
			Vec3d vec3d = this.getVelocity();
			Vec3d vec3d2 = new Vec3d(x, 0.0, z).normalize().multiply(strength);
			this.setVelocity(vec3d.x / 2.0 - vec3d2.x, this.isOnGround() ? Math.min(0.4, vec3d.y / 2.0 + strength) : vec3d.y, vec3d.z / 2.0 - vec3d2.z);
		}
	}

	public void tiltScreen(double deltaX, double deltaZ) {
	}

	@Nullable
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_GENERIC_HURT;
	}

	@Nullable
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_GENERIC_DEATH;
	}

	private SoundEvent getFallSound(int distance) {
		return distance > 4 ? this.getFallSounds().big() : this.getFallSounds().small();
	}

	public void disableExperienceDropping() {
		this.experienceDroppingDisabled = true;
	}

	public boolean isExperienceDroppingDisabled() {
		return this.experienceDroppingDisabled;
	}

	public float getDamageTiltYaw() {
		return 0.0F;
	}

	/**
	 * Gets the area in which this entity can be attacked by mobs whose attack box overlaps it.
	 * 
	 * @see net.minecraft.entity.mob.MobEntity#getAttackBox
	 */
	protected Box getHitbox() {
		Box box = this.getBoundingBox();
		Entity entity = this.getVehicle();
		if (entity != null) {
			Vec3d vec3d = entity.getPassengerRidingPos(this);
			return box.withMinY(Math.max(vec3d.y, box.minY));
		} else {
			return box;
		}
	}

	public LivingEntity.FallSounds getFallSounds() {
		return new LivingEntity.FallSounds(SoundEvents.ENTITY_GENERIC_SMALL_FALL, SoundEvents.ENTITY_GENERIC_BIG_FALL);
	}

	protected SoundEvent getDrinkSound(ItemStack stack) {
		return stack.getDrinkSound();
	}

	public SoundEvent getEatSound(ItemStack stack) {
		return stack.getEatSound();
	}

	public Optional<BlockPos> getClimbingPos() {
		return this.climbingPos;
	}

	public boolean isClimbing() {
		if (this.isSpectator()) {
			return false;
		} else {
			BlockPos blockPos = this.getBlockPos();
			BlockState blockState = this.getBlockStateAtPos();
			if (blockState.isIn(BlockTags.CLIMBABLE)) {
				this.climbingPos = Optional.of(blockPos);
				return true;
			} else if (blockState.getBlock() instanceof TrapdoorBlock && this.canEnterTrapdoor(blockPos, blockState)) {
				this.climbingPos = Optional.of(blockPos);
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean canEnterTrapdoor(BlockPos pos, BlockState state) {
		if (state.get(TrapdoorBlock.OPEN)) {
			BlockState blockState = this.getWorld().getBlockState(pos.down());
			if (blockState.isOf(Blocks.LADDER) && blockState.get(LadderBlock.FACING) == state.get(TrapdoorBlock.FACING)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isAlive() {
		return !this.isRemoved() && this.getHealth() > 0.0F;
	}

	@Override
	public int getSafeFallDistance() {
		return this.getSafeFallDistance(0.0F);
	}

	protected final int getSafeFallDistance(float health) {
		return MathHelper.floor(health + 3.0F);
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		boolean bl = super.handleFallDamage(fallDistance, damageMultiplier, damageSource);
		int i = this.computeFallDamage(fallDistance, damageMultiplier);
		if (i > 0) {
			this.playSound(this.getFallSound(i), 1.0F, 1.0F);
			this.playBlockFallSound();
			this.damage(damageSource, (float)i);
			return true;
		} else {
			return bl;
		}
	}

	protected int computeFallDamage(float fallDistance, float damageMultiplier) {
		if (this.getType().isIn(EntityTypeTags.FALL_DAMAGE_IMMUNE)) {
			return 0;
		} else {
			float f = (float)this.getAttributeValue(EntityAttributes.GENERIC_SAFE_FALL_DISTANCE);
			float g = fallDistance - f;
			return MathHelper.ceil((double)(g * damageMultiplier) * this.getAttributeValue(EntityAttributes.GENERIC_FALL_DAMAGE_MULTIPLIER));
		}
	}

	protected void playBlockFallSound() {
		if (!this.isSilent()) {
			int i = MathHelper.floor(this.getX());
			int j = MathHelper.floor(this.getY() - 0.2F);
			int k = MathHelper.floor(this.getZ());
			BlockState blockState = this.getWorld().getBlockState(new BlockPos(i, j, k));
			if (!blockState.isAir()) {
				BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
				this.playSound(blockSoundGroup.getFallSound(), blockSoundGroup.getVolume() * 0.5F, blockSoundGroup.getPitch() * 0.75F);
			}
		}
	}

	@Override
	public void animateDamage(float yaw) {
		this.maxHurtTime = 10;
		this.hurtTime = this.maxHurtTime;
	}

	public int getArmor() {
		return MathHelper.floor(this.getAttributeValue(EntityAttributes.GENERIC_ARMOR));
	}

	protected void damageArmor(DamageSource source, float amount) {
	}

	protected void damageHelmet(DamageSource source, float amount) {
	}

	protected void damageShield(float amount) {
	}

	protected void damageEquipment(DamageSource source, float amount, EquipmentSlot... slots) {
		if (!(amount <= 0.0F)) {
			int i = (int)Math.max(1.0F, amount / 4.0F);

			for(EquipmentSlot equipmentSlot : slots) {
				ItemStack itemStack = this.getEquippedStack(equipmentSlot);
				if (itemStack.getItem() instanceof ArmorItem && itemStack.takesDamageFrom(source)) {
					itemStack.damage(i, this, equipmentSlot);
				}
			}
		}
	}

	protected float applyArmorToDamage(DamageSource source, float amount) {
		if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
			this.damageArmor(source, amount);
			amount = DamageUtil.getDamageLeft(amount, (float)this.getArmor(), (float)this.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
		}

		return amount;
	}

	/**
	 * {@return the modified damage value for the applied {@code damage}}
	 * 
	 * @apiNote Subclasses should override this to make the entity take reduced damage.
	 * 
	 * @implNote This applies various {@linkplain net.minecraft.enchantment.ProtectionEnchantment
	 * protection enchantments} and the resistance effect. {@link
	 * net.minecraft.entity.mob.WitchEntity} uses this to negate their own damage and reduce the
	 * applied status effect damage.
	 */
	protected float modifyAppliedDamage(DamageSource source, float amount) {
		if (source.isIn(DamageTypeTags.BYPASSES_EFFECTS)) {
			return amount;
		} else {
			if (this.hasStatusEffect(StatusEffects.RESISTANCE) && !source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)) {
				int i = (this.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
				int j = 25 - i;
				float f = amount * (float)j;
				float g = amount;
				amount = Math.max(f / 25.0F, 0.0F);
				float h = g - amount;
				if (h > 0.0F && h < 3.4028235E37F) {
					if (this instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)this).increaseStat(Stats.DAMAGE_RESISTED, Math.round(h * 10.0F));
					} else if (source.getAttacker() instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)source.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(h * 10.0F));
					}
				}
			}

			if (amount <= 0.0F) {
				return 0.0F;
			} else if (source.isIn(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
				return amount;
			} else {
				int i = EnchantmentHelper.getProtectionAmount(this.getAllArmorItems(), source);
				if (i > 0) {
					amount = DamageUtil.getInflictedDamage(amount, (float)i);
				}

				return amount;
			}
		}
	}

	protected void applyDamage(DamageSource source, float amount) {
		if (!this.isInvulnerableTo(source)) {
			amount = this.applyArmorToDamage(source, amount);
			amount = this.modifyAppliedDamage(source, amount);
			float var9 = Math.max(amount - this.getAbsorptionAmount(), 0.0F);
			this.setAbsorptionAmount(this.getAbsorptionAmount() - (amount - var9));
			float g = amount - var9;
			if (g > 0.0F && g < 3.4028235E37F) {
				Entity var6 = source.getAttacker();
				if (var6 instanceof ServerPlayerEntity serverPlayerEntity) {
					serverPlayerEntity.increaseStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(g * 10.0F));
				}
			}

			if (var9 != 0.0F) {
				this.getDamageTracker().onDamage(source, var9);
				this.setHealth(this.getHealth() - var9);
				this.setAbsorptionAmount(this.getAbsorptionAmount() - var9);
				this.emitGameEvent(GameEvent.ENTITY_DAMAGE);
			}
		}
	}

	public DamageTracker getDamageTracker() {
		return this.damageTracker;
	}

	@Nullable
	public LivingEntity getPrimeAdversary() {
		if (this.attackingPlayer != null) {
			return this.attackingPlayer;
		} else {
			return this.attacker != null ? this.attacker : null;
		}
	}

	public final float getMaxHealth() {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
	}

	public final float getMaxAbsorption() {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_MAX_ABSORPTION);
	}

	public final int getStuckArrowCount() {
		return this.dataTracker.get(STUCK_ARROW_COUNT);
	}

	public final void setStuckArrowCount(int stuckArrowCount) {
		this.dataTracker.set(STUCK_ARROW_COUNT, stuckArrowCount);
	}

	public final int getStingerCount() {
		return this.dataTracker.get(STINGER_COUNT);
	}

	public final void setStingerCount(int stingerCount) {
		this.dataTracker.set(STINGER_COUNT, stingerCount);
	}

	private int getHandSwingDuration() {
		if (StatusEffectUtil.hasHaste(this)) {
			return 6 - (1 + StatusEffectUtil.getHasteAmplifier(this));
		} else {
			return this.hasStatusEffect(StatusEffects.MINING_FATIGUE) ? 6 + (1 + this.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
		}
	}

	public void swingHand(Hand hand) {
		this.swingHand(hand, false);
	}

	public void swingHand(Hand hand, boolean fromServerPlayer) {
		if (!this.handSwinging || this.handSwingTicks >= this.getHandSwingDuration() / 2 || this.handSwingTicks < 0) {
			this.handSwingTicks = -1;
			this.handSwinging = true;
			this.preferredHand = hand;
			if (this.getWorld() instanceof ServerWorld) {
				EntityAnimationS2CPacket entityAnimationS2CPacket = new EntityAnimationS2CPacket(
					this, hand == Hand.MAIN_HAND ? EntityAnimationS2CPacket.SWING_MAIN_HAND : EntityAnimationS2CPacket.SWING_OFF_HAND
				);
				ServerChunkManager serverChunkManager = ((ServerWorld)this.getWorld()).getChunkManager();
				if (fromServerPlayer) {
					serverChunkManager.sendToNearbyPlayers(this, entityAnimationS2CPacket);
				} else {
					serverChunkManager.sendToOtherNearbyPlayers(this, entityAnimationS2CPacket);
				}
			}
		}
	}

	@Override
	public void onDamaged(DamageSource damageSource) {
		this.limbAnimator.setSpeed(1.5F);
		this.timeUntilRegen = 20;
		this.maxHurtTime = 10;
		this.hurtTime = this.maxHurtTime;
		SoundEvent soundEvent = this.getHurtSound(damageSource);
		if (soundEvent != null) {
			this.playSound(soundEvent, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
		}

		this.damage(this.getDamageSources().generic(), 0.0F);
		this.lastDamageSource = damageSource;
		this.lastDamageTime = this.getWorld().getTime();
	}

	@Override
	public void handleStatus(byte status) {
		switch(status) {
			case 3:
				SoundEvent soundEvent = this.getDeathSound();
				if (soundEvent != null) {
					this.playSound(soundEvent, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				}

				if (!(this instanceof PlayerEntity)) {
					this.setHealth(0.0F);
					this.onDeath(this.getDamageSources().generic());
				}
				break;
			case 29:
				this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + this.getWorld().random.nextFloat() * 0.4F);
				break;
			case 30:
				this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.getWorld().random.nextFloat() * 0.4F);
				break;
			case 46:
				int i = 128;

				for(int j = 0; j < 128; ++j) {
					double d = (double)j / 127.0;
					float f = (this.random.nextFloat() - 0.5F) * 0.2F;
					float g = (this.random.nextFloat() - 0.5F) * 0.2F;
					float h = (this.random.nextFloat() - 0.5F) * 0.2F;
					double e = MathHelper.lerp(d, this.prevX, this.getX()) + (this.random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
					double k = MathHelper.lerp(d, this.prevY, this.getY()) + this.random.nextDouble() * (double)this.getHeight();
					double l = MathHelper.lerp(d, this.prevZ, this.getZ()) + (this.random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
					this.getWorld().addParticle(ParticleTypes.PORTAL, e, k, l, (double)f, (double)g, (double)h);
				}
				break;
			case 47:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.MAINHAND));
				break;
			case 48:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.OFFHAND));
				break;
			case 49:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.HEAD));
				break;
			case 50:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.CHEST));
				break;
			case 51:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.LEGS));
				break;
			case 52:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.FEET));
				break;
			case 54:
				HoneyBlock.addRichParticles(this);
				break;
			case 55:
				this.swapHandStacks();
				break;
			case 60:
				this.addDeathParticles();
				break;
			case 65:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.BODY));
				break;
			default:
				super.handleStatus(status);
		}
	}

	private void addDeathParticles() {
		for(int i = 0; i < 20; ++i) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.getWorld().addParticle(ParticleTypes.POOF, this.getParticleX(1.0), this.getRandomBodyY(), this.getParticleZ(1.0), d, e, f);
		}
	}

	private void swapHandStacks() {
		ItemStack itemStack = this.getEquippedStack(EquipmentSlot.OFFHAND);
		this.equipStack(EquipmentSlot.OFFHAND, this.getEquippedStack(EquipmentSlot.MAINHAND));
		this.equipStack(EquipmentSlot.MAINHAND, itemStack);
	}

	@Override
	protected void tickInVoid() {
		this.damage(this.getDamageSources().outOfWorld(), 4.0F);
	}

	protected void tickHandSwing() {
		int i = this.getHandSwingDuration();
		if (this.handSwinging) {
			++this.handSwingTicks;
			if (this.handSwingTicks >= i) {
				this.handSwingTicks = 0;
				this.handSwinging = false;
			}
		} else {
			this.handSwingTicks = 0;
		}

		this.handSwingProgress = (float)this.handSwingTicks / (float)i;
	}

	@Nullable
	public EntityAttributeInstance getAttributeInstance(RegistryEntry<EntityAttribute> attribute) {
		return this.getAttributes().getCustomInstance(attribute);
	}

	public double getAttributeValue(RegistryEntry<EntityAttribute> attribute) {
		return this.getAttributes().getValue(attribute);
	}

	public double getAttributeBaseValue(RegistryEntry<EntityAttribute> attribute) {
		return this.getAttributes().getBaseValue(attribute);
	}

	public AttributeContainer getAttributes() {
		return this.attributes;
	}

	public ItemStack getMainHandStack() {
		return this.getEquippedStack(EquipmentSlot.MAINHAND);
	}

	public ItemStack getOffHandStack() {
		return this.getEquippedStack(EquipmentSlot.OFFHAND);
	}

	/**
	 * Checks if this entity is holding a certain item.
	 * 
	 * <p>This checks both the entity's main and off hand.
	 */
	public boolean isHolding(Item item) {
		return this.isHolding(stack -> stack.isOf(item));
	}

	/**
	 * Checks if this entity is holding a certain item.
	 * 
	 * <p>This checks both the entity's main and off hand.
	 */
	public boolean isHolding(Predicate<ItemStack> predicate) {
		return predicate.test(this.getMainHandStack()) || predicate.test(this.getOffHandStack());
	}

	public ItemStack getStackInHand(Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			return this.getEquippedStack(EquipmentSlot.MAINHAND);
		} else if (hand == Hand.OFF_HAND) {
			return this.getEquippedStack(EquipmentSlot.OFFHAND);
		} else {
			throw new IllegalArgumentException("Invalid hand " + hand);
		}
	}

	public void setStackInHand(Hand hand, ItemStack stack) {
		if (hand == Hand.MAIN_HAND) {
			this.equipStack(EquipmentSlot.MAINHAND, stack);
		} else {
			if (hand != Hand.OFF_HAND) {
				throw new IllegalArgumentException("Invalid hand " + hand);
			}

			this.equipStack(EquipmentSlot.OFFHAND, stack);
		}
	}

	public boolean hasStackEquipped(EquipmentSlot slot) {
		return !this.getEquippedStack(slot).isEmpty();
	}

	public boolean canUseSlot(EquipmentSlot slot) {
		return false;
	}

	public abstract Iterable<ItemStack> getArmorItems();

	public abstract ItemStack getEquippedStack(EquipmentSlot slot);

	public abstract void equipStack(EquipmentSlot slot, ItemStack stack);

	public Iterable<ItemStack> getHandItems() {
		return List.of();
	}

	public Iterable<ItemStack> getAllArmorItems() {
		return this.getArmorItems();
	}

	public Iterable<ItemStack> getEquippedItems() {
		return Iterables.concat(this.getHandItems(), this.getAllArmorItems());
	}

	protected void processEquippedStack(ItemStack stack) {
		stack.getItem().postProcessComponents(stack);
	}

	public float getArmorVisibility() {
		Iterable<ItemStack> iterable = this.getArmorItems();
		int i = 0;
		int j = 0;

		for(ItemStack itemStack : iterable) {
			if (!itemStack.isEmpty()) {
				++j;
			}

			++i;
		}

		return i > 0 ? (float)j / (float)i : 0.0F;
	}

	@Override
	public void setSprinting(boolean sprinting) {
		super.setSprinting(sprinting);
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		entityAttributeInstance.removeModifier(SPRINTING_SPEED_BOOST.uuid());
		if (sprinting) {
			entityAttributeInstance.addTemporaryModifier(SPRINTING_SPEED_BOOST);
		}
	}

	protected float getSoundVolume() {
		return 1.0F;
	}

	public float getSoundPitch() {
		return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
	}

	protected boolean isImmobile() {
		return this.isDead();
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (!this.isSleeping()) {
			super.pushAwayFrom(entity);
		}
	}

	private void onDismounted(Entity vehicle) {
		Vec3d vec3d;
		if (this.isRemoved()) {
			vec3d = this.getPos();
		} else if (!vehicle.isRemoved() && !this.getWorld().getBlockState(vehicle.getBlockPos()).isIn(BlockTags.PORTALS)) {
			vec3d = vehicle.updatePassengerForDismount(this);
		} else {
			double d = Math.max(this.getY(), vehicle.getY());
			vec3d = new Vec3d(this.getX(), d, this.getZ());
		}

		this.requestTeleportAndDismount(vec3d.x, vec3d.y, vec3d.z);
	}

	@Override
	public boolean shouldRenderName() {
		return this.isCustomNameVisible();
	}

	protected float getJumpVelocity() {
		return this.getJumpVelocity(1.0F);
	}

	protected float getJumpVelocity(float strength) {
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_JUMP_STRENGTH) * strength * this.getJumpVelocityMultiplier()
			+ this.getJumpBoostVelocityModifier();
	}

	public float getJumpBoostVelocityModifier() {
		return this.hasStatusEffect(StatusEffects.JUMP_BOOST) ? 0.1F * ((float)this.getStatusEffect(StatusEffects.JUMP_BOOST).getAmplifier() + 1.0F) : 0.0F;
	}

	protected void jump() {
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x, (double)this.getJumpVelocity(), vec3d.z);
		if (this.isSprinting()) {
			float f = this.getYaw() * (float) (Math.PI / 180.0);
			this.setVelocity(this.getVelocity().add((double)(-MathHelper.sin(f) * 0.2F), 0.0, (double)(MathHelper.cos(f) * 0.2F)));
		}

		this.velocityDirty = true;
	}

	protected void knockDownwards() {
		this.setVelocity(this.getVelocity().add(0.0, -0.04F, 0.0));
	}

	protected void swimUpward(TagKey<Fluid> fluid) {
		this.setVelocity(this.getVelocity().add(0.0, 0.04F, 0.0));
	}

	protected float getBaseMovementSpeedMultiplier() {
		return 0.8F;
	}

	public boolean canWalkOnFluid(FluidState state) {
		return false;
	}

	@Override
	protected double getGravity() {
		return this.getAttributeValue(EntityAttributes.GENERIC_GRAVITY);
	}

	/**
	 * Allows you to do certain speed and velocity calculations. This is useful for custom vehicle behavior, or custom entity movement. This is not to be confused with AI.
	 * 
	 * <p>See vanilla examples of {@linkplain net.minecraft.entity.passive.AbstractHorseEntity#travel
	 * custom horse vehicle} and {@linkplain net.minecraft.entity.mob.FlyingEntity#travel
	 * flying entities}.
	 * 
	 * @param movementInput represents the sidewaysSpeed, upwardSpeed, and forwardSpeed of the entity in that order
	 */
	public void travel(Vec3d movementInput) {
		if (this.isLogicalSideForUpdatingMovement()) {
			double d = this.getFinalGravity();
			boolean bl = this.getVelocity().y <= 0.0;
			if (bl && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
				d = Math.min(d, 0.01);
			}

			FluidState fluidState = this.getWorld().getFluidState(this.getBlockPos());
			if (this.isTouchingWater() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState)) {
				double e = this.getY();
				float f = this.isSprinting() ? 0.9F : this.getBaseMovementSpeedMultiplier();
				float g = 0.02F;
				float h = (float)EnchantmentHelper.getDepthStrider(this);
				if (h > 3.0F) {
					h = 3.0F;
				}

				if (!this.isOnGround()) {
					h *= 0.5F;
				}

				if (h > 0.0F) {
					f += (0.54600006F - f) * h / 3.0F;
					g += (this.getMovementSpeed() - g) * h / 3.0F;
				}

				if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
					f = 0.96F;
				}

				this.updateVelocity(g, movementInput);
				this.move(MovementType.SELF, this.getVelocity());
				Vec3d vec3d = this.getVelocity();
				if (this.horizontalCollision && this.isClimbing()) {
					vec3d = new Vec3d(vec3d.x, 0.2, vec3d.z);
				}

				this.setVelocity(vec3d.multiply((double)f, 0.8F, (double)f));
				Vec3d vec3d2 = this.applyFluidMovingSpeed(d, bl, this.getVelocity());
				this.setVelocity(vec3d2);
				if (this.horizontalCollision && this.doesNotCollide(vec3d2.x, vec3d2.y + 0.6F - this.getY() + e, vec3d2.z)) {
					this.setVelocity(vec3d2.x, 0.3F, vec3d2.z);
				}
			} else if (this.isInLava() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState)) {
				double e = this.getY();
				this.updateVelocity(0.02F, movementInput);
				this.move(MovementType.SELF, this.getVelocity());
				if (this.getFluidHeight(FluidTags.LAVA) <= this.getSwimHeight()) {
					this.setVelocity(this.getVelocity().multiply(0.5, 0.8F, 0.5));
					Vec3d vec3d3 = this.applyFluidMovingSpeed(d, bl, this.getVelocity());
					this.setVelocity(vec3d3);
				} else {
					this.setVelocity(this.getVelocity().multiply(0.5));
				}

				if (d != 0.0) {
					this.setVelocity(this.getVelocity().add(0.0, -d / 4.0, 0.0));
				}

				Vec3d vec3d3 = this.getVelocity();
				if (this.horizontalCollision && this.doesNotCollide(vec3d3.x, vec3d3.y + 0.6F - this.getY() + e, vec3d3.z)) {
					this.setVelocity(vec3d3.x, 0.3F, vec3d3.z);
				}
			} else if (this.isFallFlying()) {
				this.limitFallDistance();
				Vec3d vec3d4 = this.getVelocity();
				Vec3d vec3d5 = this.getRotationVector();
				float f = this.getPitch() * (float) (Math.PI / 180.0);
				double i = Math.sqrt(vec3d5.x * vec3d5.x + vec3d5.z * vec3d5.z);
				double j = vec3d4.horizontalLength();
				double k = vec3d5.length();
				double l = Math.cos((double)f);
				l = l * l * Math.min(1.0, k / 0.4);
				vec3d4 = this.getVelocity().add(0.0, d * (-1.0 + l * 0.75), 0.0);
				if (vec3d4.y < 0.0 && i > 0.0) {
					double m = vec3d4.y * -0.1 * l;
					vec3d4 = vec3d4.add(vec3d5.x * m / i, m, vec3d5.z * m / i);
				}

				if (f < 0.0F && i > 0.0) {
					double m = j * (double)(-MathHelper.sin(f)) * 0.04;
					vec3d4 = vec3d4.add(-vec3d5.x * m / i, m * 3.2, -vec3d5.z * m / i);
				}

				if (i > 0.0) {
					vec3d4 = vec3d4.add((vec3d5.x / i * j - vec3d4.x) * 0.1, 0.0, (vec3d5.z / i * j - vec3d4.z) * 0.1);
				}

				this.setVelocity(vec3d4.multiply(0.99F, 0.98F, 0.99F));
				this.move(MovementType.SELF, this.getVelocity());
				if (this.horizontalCollision && !this.getWorld().isClient) {
					double m = this.getVelocity().horizontalLength();
					double n = j - m;
					float o = (float)(n * 10.0 - 3.0);
					if (o > 0.0F) {
						this.playSound(this.getFallSound((int)o), 1.0F, 1.0F);
						this.damage(this.getDamageSources().flyIntoWall(), o);
					}
				}

				if (this.isOnGround() && !this.getWorld().isClient) {
					this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, false);
				}
			} else {
				BlockPos blockPos = this.getVelocityAffectingPos();
				float p = this.getWorld().getBlockState(blockPos).getBlock().getSlipperiness();
				float f = this.isOnGround() ? p * 0.91F : 0.91F;
				Vec3d vec3d6 = this.applyMovementInput(movementInput, p);
				double q = vec3d6.y;
				if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
					q += (0.05 * (double)(this.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1) - vec3d6.y) * 0.2;
				} else if (!this.getWorld().isClient || this.getWorld().isChunkLoaded(blockPos)) {
					q -= d;
				} else if (this.getY() > (double)this.getWorld().getBottomY()) {
					q = -0.1;
				} else {
					q = 0.0;
				}

				if (this.hasNoDrag()) {
					this.setVelocity(vec3d6.x, q, vec3d6.z);
				} else {
					this.setVelocity(vec3d6.x * (double)f, this instanceof Flutterer ? q * (double)f : q * 0.98F, vec3d6.z * (double)f);
				}
			}
		}

		this.updateLimbs(this instanceof Flutterer);
	}

	private void travelControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
		Vec3d vec3d = this.getControlledMovementInput(controllingPlayer, movementInput);
		this.tickControlled(controllingPlayer, vec3d);
		if (this.isLogicalSideForUpdatingMovement()) {
			this.setMovementSpeed(this.getSaddledSpeed(controllingPlayer));
			this.travel(vec3d);
		} else {
			this.updateLimbs(false);
			this.setVelocity(Vec3d.ZERO);
			this.tryCheckBlockCollision();
		}
	}

	protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
	}

	protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
		return movementInput;
	}

	protected float getSaddledSpeed(PlayerEntity controllingPlayer) {
		return this.getMovementSpeed();
	}

	public void updateLimbs(boolean flutter) {
		float f = (float)MathHelper.magnitude(this.getX() - this.prevX, flutter ? this.getY() - this.prevY : 0.0, this.getZ() - this.prevZ);
		this.updateLimbs(f);
	}

	protected void updateLimbs(float posDelta) {
		float f = Math.min(posDelta * 4.0F, 1.0F);
		this.limbAnimator.updateLimbs(f, 0.4F);
	}

	public Vec3d applyMovementInput(Vec3d movementInput, float slipperiness) {
		this.updateVelocity(this.getMovementSpeed(slipperiness), movementInput);
		this.setVelocity(this.applyClimbingSpeed(this.getVelocity()));
		this.move(MovementType.SELF, this.getVelocity());
		Vec3d vec3d = this.getVelocity();
		if ((this.horizontalCollision || this.jumping)
			&& (this.isClimbing() || this.getBlockStateAtPos().isOf(Blocks.POWDER_SNOW) && PowderSnowBlock.canWalkOnPowderSnow(this))) {
			vec3d = new Vec3d(vec3d.x, 0.2, vec3d.z);
		}

		return vec3d;
	}

	public Vec3d applyFluidMovingSpeed(double gravity, boolean falling, Vec3d motion) {
		if (gravity != 0.0 && !this.isSprinting()) {
			double d;
			if (falling && Math.abs(motion.y - 0.005) >= 0.003 && Math.abs(motion.y - gravity / 16.0) < 0.003) {
				d = -0.003;
			} else {
				d = motion.y - gravity / 16.0;
			}

			return new Vec3d(motion.x, d, motion.z);
		} else {
			return motion;
		}
	}

	private Vec3d applyClimbingSpeed(Vec3d motion) {
		if (this.isClimbing()) {
			this.onLanding();
			float f = 0.15F;
			double d = MathHelper.clamp(motion.x, -0.15F, 0.15F);
			double e = MathHelper.clamp(motion.z, -0.15F, 0.15F);
			double g = Math.max(motion.y, -0.15F);
			if (g < 0.0 && !this.getBlockStateAtPos().isOf(Blocks.SCAFFOLDING) && this.isHoldingOntoLadder() && this instanceof PlayerEntity) {
				g = 0.0;
			}

			motion = new Vec3d(d, g, e);
		}

		return motion;
	}

	private float getMovementSpeed(float slipperiness) {
		return this.isOnGround() ? this.getMovementSpeed() * (0.21600002F / (slipperiness * slipperiness * slipperiness)) : this.getOffGroundSpeed();
	}

	protected float getOffGroundSpeed() {
		return this.getControllingPassenger() instanceof PlayerEntity ? this.getMovementSpeed() * 0.1F : 0.02F;
	}

	public float getMovementSpeed() {
		return this.movementSpeed;
	}

	public void setMovementSpeed(float movementSpeed) {
		this.movementSpeed = movementSpeed;
	}

	public boolean tryAttack(Entity target) {
		this.onAttacking(target);
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		this.tickActiveItemStack();
		this.updateLeaningPitch();
		if (!this.getWorld().isClient) {
			int i = this.getStuckArrowCount();
			if (i > 0) {
				if (this.stuckArrowTimer <= 0) {
					this.stuckArrowTimer = 20 * (30 - i);
				}

				--this.stuckArrowTimer;
				if (this.stuckArrowTimer <= 0) {
					this.setStuckArrowCount(i - 1);
				}
			}

			int j = this.getStingerCount();
			if (j > 0) {
				if (this.stuckStingerTimer <= 0) {
					this.stuckStingerTimer = 20 * (30 - j);
				}

				--this.stuckStingerTimer;
				if (this.stuckStingerTimer <= 0) {
					this.setStingerCount(j - 1);
				}
			}

			this.sendEquipmentChanges();
			if (this.age % 20 == 0) {
				this.getDamageTracker().update();
			}

			if (this.isSleeping() && !this.isSleepingInBed()) {
				this.wakeUp();
			}
		}

		if (!this.isRemoved()) {
			this.tickMovement();
		}

		double d = this.getX() - this.prevX;
		double e = this.getZ() - this.prevZ;
		float f = (float)(d * d + e * e);
		float g = this.bodyYaw;
		float h = 0.0F;
		this.prevStepBobbingAmount = this.stepBobbingAmount;
		float k = 0.0F;
		if (f > 0.0025000002F) {
			k = 1.0F;
			h = (float)Math.sqrt((double)f) * 3.0F;
			float l = (float)MathHelper.atan2(e, d) * (180.0F / (float)Math.PI) - 90.0F;
			float m = MathHelper.abs(MathHelper.wrapDegrees(this.getYaw()) - l);
			if (95.0F < m && m < 265.0F) {
				g = l - 180.0F;
			} else {
				g = l;
			}
		}

		if (this.handSwingProgress > 0.0F) {
			g = this.getYaw();
		}

		if (!this.isOnGround()) {
			k = 0.0F;
		}

		this.stepBobbingAmount += (k - this.stepBobbingAmount) * 0.3F;
		this.getWorld().getProfiler().push("headTurn");
		h = this.turnHead(g, h);
		this.getWorld().getProfiler().pop();
		this.getWorld().getProfiler().push("rangeChecks");

		while(this.getYaw() - this.prevYaw < -180.0F) {
			this.prevYaw -= 360.0F;
		}

		while(this.getYaw() - this.prevYaw >= 180.0F) {
			this.prevYaw += 360.0F;
		}

		while(this.bodyYaw - this.prevBodyYaw < -180.0F) {
			this.prevBodyYaw -= 360.0F;
		}

		while(this.bodyYaw - this.prevBodyYaw >= 180.0F) {
			this.prevBodyYaw += 360.0F;
		}

		while(this.getPitch() - this.prevPitch < -180.0F) {
			this.prevPitch -= 360.0F;
		}

		while(this.getPitch() - this.prevPitch >= 180.0F) {
			this.prevPitch += 360.0F;
		}

		while(this.headYaw - this.prevHeadYaw < -180.0F) {
			this.prevHeadYaw -= 360.0F;
		}

		while(this.headYaw - this.prevHeadYaw >= 180.0F) {
			this.prevHeadYaw += 360.0F;
		}

		this.getWorld().getProfiler().pop();
		this.lookDirection += h;
		if (this.isFallFlying()) {
			++this.fallFlyingTicks;
		} else {
			this.fallFlyingTicks = 0;
		}

		if (this.isSleeping()) {
			this.setPitch(0.0F);
		}

		this.updateAttributes();
		float l = this.getScale();
		if (l != this.prevScale) {
			this.prevScale = l;
			this.calculateDimensions();
		}
	}

	/**
	 * Sends equipment changes to nearby players.
	 */
	private void sendEquipmentChanges() {
		Map<EquipmentSlot, ItemStack> map = this.getEquipmentChanges();
		if (map != null) {
			this.checkHandStackSwap(map);
			if (!map.isEmpty()) {
				this.sendEquipmentChanges(map);
			}
		}
	}

	/**
	 * {@return the difference between the last sent equipment set and the
	 * current one}
	 */
	@Nullable
	private Map<EquipmentSlot, ItemStack> getEquipmentChanges() {
		Map<EquipmentSlot, ItemStack> map = null;

		for(EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			ItemStack itemStack = switch(equipmentSlot.getType()) {
				case HAND -> this.getSyncedHandStack(equipmentSlot);
				case ARMOR -> this.getSyncedArmorStack(equipmentSlot);
				case BODY -> this.syncedBodyArmorStack;
			};
			ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
			if (this.areItemsDifferent(itemStack, itemStack2)) {
				if (map == null) {
					map = Maps.newEnumMap(EquipmentSlot.class);
				}

				map.put(equipmentSlot, itemStack2);
				AttributeContainer attributeContainer = this.getAttributes();
				if (!itemStack.isEmpty()) {
					itemStack.applyAttributeModifiers(equipmentSlot, (attribute, modifier) -> {
						EntityAttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance(attribute);
						if (entityAttributeInstance != null) {
							entityAttributeInstance.removeModifier(modifier);
						}
					});
				}

				if (!itemStack2.isEmpty()) {
					itemStack2.applyAttributeModifiers(equipmentSlot, (attribute, modifier) -> {
						EntityAttributeInstance entityAttributeInstance = attributeContainer.getCustomInstance(attribute);
						if (entityAttributeInstance != null) {
							entityAttributeInstance.removeModifier(modifier.uuid());
							entityAttributeInstance.addTemporaryModifier(modifier);
						}
					});
				}
			}
		}

		return map;
	}

	public boolean areItemsDifferent(ItemStack stack, ItemStack stack2) {
		return !ItemStack.areEqual(stack2, stack);
	}

	/**
	 * Notifies nearby players if the stacks in the hands have been swapped.
	 */
	private void checkHandStackSwap(Map<EquipmentSlot, ItemStack> equipmentChanges) {
		ItemStack itemStack = (ItemStack)equipmentChanges.get(EquipmentSlot.MAINHAND);
		ItemStack itemStack2 = (ItemStack)equipmentChanges.get(EquipmentSlot.OFFHAND);
		if (itemStack != null
			&& itemStack2 != null
			&& ItemStack.areEqual(itemStack, this.getSyncedHandStack(EquipmentSlot.OFFHAND))
			&& ItemStack.areEqual(itemStack2, this.getSyncedHandStack(EquipmentSlot.MAINHAND))) {
			((ServerWorld)this.getWorld()).getChunkManager().sendToOtherNearbyPlayers(this, new EntityStatusS2CPacket(this, EntityStatuses.SWAP_HANDS));
			equipmentChanges.remove(EquipmentSlot.MAINHAND);
			equipmentChanges.remove(EquipmentSlot.OFFHAND);
			this.setSyncedHandStack(EquipmentSlot.MAINHAND, itemStack.copy());
			this.setSyncedHandStack(EquipmentSlot.OFFHAND, itemStack2.copy());
		}
	}

	/**
	 * Sends equipment changes to nearby players.
	 * 
	 * @see #sendEquipmentChanges()
	 */
	private void sendEquipmentChanges(Map<EquipmentSlot, ItemStack> equipmentChanges) {
		List<Pair<EquipmentSlot, ItemStack>> list = Lists.<Pair<EquipmentSlot, ItemStack>>newArrayListWithCapacity(equipmentChanges.size());
		equipmentChanges.forEach((slot, stack) -> {
			ItemStack itemStack = stack.copy();
			list.add(Pair.of(slot, itemStack));
			switch(slot.getType()) {
				case HAND:
					this.setSyncedHandStack(slot, itemStack);
					break;
				case ARMOR:
					this.setSyncedArmorStack(slot, itemStack);
					break;
				case BODY:
					this.syncedBodyArmorStack = itemStack;
			}
		});
		((ServerWorld)this.getWorld()).getChunkManager().sendToOtherNearbyPlayers(this, new EntityEquipmentUpdateS2CPacket(this.getId(), list));
	}

	private ItemStack getSyncedArmorStack(EquipmentSlot slot) {
		return this.syncedArmorStacks.get(slot.getEntitySlotId());
	}

	private void setSyncedArmorStack(EquipmentSlot slot, ItemStack armor) {
		this.syncedArmorStacks.set(slot.getEntitySlotId(), armor);
	}

	private ItemStack getSyncedHandStack(EquipmentSlot slot) {
		return this.syncedHandStacks.get(slot.getEntitySlotId());
	}

	private void setSyncedHandStack(EquipmentSlot slot, ItemStack stack) {
		this.syncedHandStacks.set(slot.getEntitySlotId(), stack);
	}

	protected float turnHead(float bodyRotation, float headRotation) {
		float f = MathHelper.wrapDegrees(bodyRotation - this.bodyYaw);
		this.bodyYaw += f * 0.3F;
		float g = MathHelper.wrapDegrees(this.getYaw() - this.bodyYaw);
		float h = this.getMaxRelativeHeadRotation();
		if (Math.abs(g) > h) {
			this.bodyYaw += g - (float)MathHelper.sign((double)g) * h;
		}

		boolean bl = g < -90.0F || g >= 90.0F;
		if (bl) {
			headRotation *= -1.0F;
		}

		return headRotation;
	}

	/**
	 * {@return the maximum rotation of the head relative to the body in degrees}
	 */
	protected float getMaxRelativeHeadRotation() {
		return 50.0F;
	}

	public void tickMovement() {
		if (this.jumpingCooldown > 0) {
			--this.jumpingCooldown;
		}

		if (this.isLogicalSideForUpdatingMovement()) {
			this.bodyTrackingIncrements = 0;
			this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
		}

		if (this.bodyTrackingIncrements > 0) {
			this.lerpPosAndRotation(this.bodyTrackingIncrements, this.serverX, this.serverY, this.serverZ, this.serverYaw, this.serverPitch);
			--this.bodyTrackingIncrements;
		} else if (!this.canMoveVoluntarily()) {
			this.setVelocity(this.getVelocity().multiply(0.98));
		}

		if (this.headTrackingIncrements > 0) {
			this.lerpHeadYaw(this.headTrackingIncrements, this.serverHeadYaw);
			--this.headTrackingIncrements;
		}

		Vec3d vec3d = this.getVelocity();
		double d = vec3d.x;
		double e = vec3d.y;
		double f = vec3d.z;
		if (Math.abs(vec3d.x) < 0.003) {
			d = 0.0;
		}

		if (Math.abs(vec3d.y) < 0.003) {
			e = 0.0;
		}

		if (Math.abs(vec3d.z) < 0.003) {
			f = 0.0;
		}

		this.setVelocity(d, e, f);
		this.getWorld().getProfiler().push("ai");
		if (this.isImmobile()) {
			this.jumping = false;
			this.sidewaysSpeed = 0.0F;
			this.forwardSpeed = 0.0F;
		} else if (this.canMoveVoluntarily()) {
			this.getWorld().getProfiler().push("newAi");
			this.tickNewAi();
			this.getWorld().getProfiler().pop();
		}

		this.getWorld().getProfiler().pop();
		this.getWorld().getProfiler().push("jump");
		if (this.jumping && this.shouldSwimInFluids()) {
			double g;
			if (this.isInLava()) {
				g = this.getFluidHeight(FluidTags.LAVA);
			} else {
				g = this.getFluidHeight(FluidTags.WATER);
			}

			boolean bl = this.isTouchingWater() && g > 0.0;
			double h = this.getSwimHeight();
			if (!bl || this.isOnGround() && !(g > h)) {
				if (!this.isInLava() || this.isOnGround() && !(g > h)) {
					if ((this.isOnGround() || bl && g <= h) && this.jumpingCooldown == 0) {
						this.jump();
						this.jumpingCooldown = 10;
					}
				} else {
					this.swimUpward(FluidTags.LAVA);
				}
			} else {
				this.swimUpward(FluidTags.WATER);
			}
		} else {
			this.jumpingCooldown = 0;
		}

		this.getWorld().getProfiler().pop();
		this.getWorld().getProfiler().push("travel");
		this.sidewaysSpeed *= 0.98F;
		this.forwardSpeed *= 0.98F;
		this.tickFallFlying();
		Box box = this.getBoundingBox();
		Vec3d vec3d2 = new Vec3d((double)this.sidewaysSpeed, (double)this.upwardSpeed, (double)this.forwardSpeed);
		if (this.hasStatusEffect(StatusEffects.SLOW_FALLING) || this.hasStatusEffect(StatusEffects.LEVITATION)) {
			this.onLanding();
		}

		label104: {
			LivingEntity var16 = this.getControllingPassenger();
			if (var16 instanceof PlayerEntity playerEntity && this.isAlive()) {
				this.travelControlled(playerEntity, vec3d2);
				break label104;
			}

			this.travel(vec3d2);
		}

		this.getWorld().getProfiler().pop();
		this.getWorld().getProfiler().push("freezing");
		if (!this.getWorld().isClient && !this.isDead()) {
			int i = this.getFrozenTicks();
			if (this.inPowderSnow && this.canFreeze()) {
				this.setFrozenTicks(Math.min(this.getMinFreezeDamageTicks(), i + 1));
			} else {
				this.setFrozenTicks(Math.max(0, i - 2));
			}
		}

		this.removePowderSnowSlow();
		this.addPowderSnowSlowIfNeeded();
		if (!this.getWorld().isClient && this.age % 40 == 0 && this.isFrozen() && this.canFreeze()) {
			this.damage(this.getDamageSources().freeze(), 1.0F);
		}

		this.getWorld().getProfiler().pop();
		this.getWorld().getProfiler().push("push");
		if (this.riptideTicks > 0) {
			--this.riptideTicks;
			this.tickRiptide(box, this.getBoundingBox());
		}

		this.tickCramming();
		this.getWorld().getProfiler().pop();
		if (!this.getWorld().isClient && this.hurtByWater() && this.isWet()) {
			this.damage(this.getDamageSources().drown(), 1.0F);
		}
	}

	public boolean hurtByWater() {
		return false;
	}

	private void tickFallFlying() {
		boolean bl = this.getFlag(Entity.FALL_FLYING_FLAG_INDEX);
		if (bl && !this.isOnGround() && !this.hasVehicle() && !this.hasStatusEffect(StatusEffects.LEVITATION)) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.CHEST);
			if (itemStack.isOf(Items.ELYTRA) && ElytraItem.isUsable(itemStack)) {
				bl = true;
				int i = this.fallFlyingTicks + 1;
				if (!this.getWorld().isClient && i % 10 == 0) {
					int j = i / 10;
					if (j % 2 == 0) {
						itemStack.damage(1, this, EquipmentSlot.CHEST);
					}

					this.emitGameEvent(GameEvent.ELYTRA_GLIDE);
				}
			} else {
				bl = false;
			}
		} else {
			bl = false;
		}

		if (!this.getWorld().isClient) {
			this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, bl);
		}
	}

	protected void tickNewAi() {
	}

	protected void tickCramming() {
		if (this.getWorld().isClient()) {
			this.getWorld()
				.getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), this.getBoundingBox(), EntityPredicates.canBePushedBy(this))
				.forEach(this::pushAway);
		} else {
			List<Entity> list = this.getWorld().getOtherEntities(this, this.getBoundingBox(), EntityPredicates.canBePushedBy(this));
			if (!list.isEmpty()) {
				int i = this.getWorld().getGameRules().getInt(GameRules.MAX_ENTITY_CRAMMING);
				if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
					int j = 0;

					for(Entity entity : list) {
						if (!entity.hasVehicle()) {
							++j;
						}
					}

					if (j > i - 1) {
						this.damage(this.getDamageSources().cramming(), 6.0F);
					}
				}

				for(Entity entity2 : list) {
					this.pushAway(entity2);
				}
			}
		}
	}

	protected void tickRiptide(Box a, Box b) {
		Box box = a.union(b);
		List<Entity> list = this.getWorld().getOtherEntities(this, box);
		if (!list.isEmpty()) {
			for(Entity entity : list) {
				if (entity instanceof LivingEntity) {
					this.attackLivingEntity((LivingEntity)entity);
					this.riptideTicks = 0;
					this.setVelocity(this.getVelocity().multiply(-0.2));
					break;
				}
			}
		} else if (this.horizontalCollision) {
			this.riptideTicks = 0;
		}

		if (!this.getWorld().isClient && this.riptideTicks <= 0) {
			this.setLivingFlag(USING_RIPTIDE_FLAG, false);
		}
	}

	protected void pushAway(Entity entity) {
		entity.pushAwayFrom(this);
	}

	protected void attackLivingEntity(LivingEntity target) {
	}

	public boolean isUsingRiptide() {
		return (this.dataTracker.get(LIVING_FLAGS) & 4) != 0;
	}

	@Override
	public void stopRiding() {
		Entity entity = this.getVehicle();
		super.stopRiding();
		if (entity != null && entity != this.getVehicle() && !this.getWorld().isClient) {
			this.onDismounted(entity);
		}
	}

	@Override
	public void tickRiding() {
		super.tickRiding();
		this.prevStepBobbingAmount = this.stepBobbingAmount;
		this.stepBobbingAmount = 0.0F;
		this.onLanding();
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
		this.serverX = x;
		this.serverY = y;
		this.serverZ = z;
		this.serverYaw = (double)yaw;
		this.serverPitch = (double)pitch;
		this.bodyTrackingIncrements = interpolationSteps;
	}

	@Override
	public double getLerpTargetX() {
		return this.bodyTrackingIncrements > 0 ? this.serverX : this.getX();
	}

	@Override
	public double getLerpTargetY() {
		return this.bodyTrackingIncrements > 0 ? this.serverY : this.getY();
	}

	@Override
	public double getLerpTargetZ() {
		return this.bodyTrackingIncrements > 0 ? this.serverZ : this.getZ();
	}

	@Override
	public float getLerpTargetPitch() {
		return this.bodyTrackingIncrements > 0 ? (float)this.serverPitch : this.getPitch();
	}

	@Override
	public float getLerpTargetYaw() {
		return this.bodyTrackingIncrements > 0 ? (float)this.serverYaw : this.getYaw();
	}

	@Override
	public void updateTrackedHeadRotation(float yaw, int interpolationSteps) {
		this.serverHeadYaw = (double)yaw;
		this.headTrackingIncrements = interpolationSteps;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	/**
	 * Called to trigger advancement criteria when an entity picks up an item
	 * thrown by a player.
	 */
	public void triggerItemPickedUpByEntityCriteria(ItemEntity item) {
		Entity entity = item.getOwner();
		if (entity instanceof ServerPlayerEntity) {
			Criteria.THROWN_ITEM_PICKED_UP_BY_ENTITY.trigger((ServerPlayerEntity)entity, item.getStack(), this);
		}
	}

	public void sendPickup(Entity item, int count) {
		if (!item.isRemoved()
			&& !this.getWorld().isClient
			&& (item instanceof ItemEntity || item instanceof PersistentProjectileEntity || item instanceof ExperienceOrbEntity)) {
			((ServerWorld)this.getWorld()).getChunkManager().sendToOtherNearbyPlayers(item, new ItemPickupAnimationS2CPacket(item.getId(), this.getId(), count));
		}
	}

	public boolean canSee(Entity entity) {
		if (entity.getWorld() != this.getWorld()) {
			return false;
		} else {
			Vec3d vec3d = new Vec3d(this.getX(), this.getEyeY(), this.getZ());
			Vec3d vec3d2 = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
			if (vec3d2.distanceTo(vec3d) > 128.0) {
				return false;
			} else {
				return this.getWorld().raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this)).getType()
					== HitResult.Type.MISS;
			}
		}
	}

	@Override
	public float getYaw(float tickDelta) {
		return tickDelta == 1.0F ? this.headYaw : MathHelper.lerp(tickDelta, this.prevHeadYaw, this.headYaw);
	}

	public float getHandSwingProgress(float tickDelta) {
		float f = this.handSwingProgress - this.lastHandSwingProgress;
		if (f < 0.0F) {
			++f;
		}

		return this.lastHandSwingProgress + f * tickDelta;
	}

	@Override
	public boolean canHit() {
		return !this.isRemoved();
	}

	@Override
	public boolean isPushable() {
		return this.isAlive() && !this.isSpectator() && !this.isClimbing();
	}

	@Override
	public float getHeadYaw() {
		return this.headYaw;
	}

	@Override
	public void setHeadYaw(float headYaw) {
		this.headYaw = headYaw;
	}

	@Override
	public void setBodyYaw(float bodyYaw) {
		this.bodyYaw = bodyYaw;
	}

	@Override
	protected Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect) {
		return positionInPortal(super.positionInPortal(portalAxis, portalRect));
	}

	public static Vec3d positionInPortal(Vec3d pos) {
		return new Vec3d(pos.x, pos.y, 0.0);
	}

	public float getAbsorptionAmount() {
		return this.absorptionAmount;
	}

	public final void setAbsorptionAmount(float absorptionAmount) {
		this.setAbsorptionAmountUnclamped(MathHelper.clamp(absorptionAmount, 0.0F, this.getMaxAbsorption()));
	}

	protected void setAbsorptionAmountUnclamped(float absorptionAmount) {
		this.absorptionAmount = absorptionAmount;
	}

	public void enterCombat() {
	}

	public void endCombat() {
	}

	protected void markEffectsDirty() {
		this.effectsChanged = true;
	}

	public abstract Arm getMainArm();

	public boolean isUsingItem() {
		return (this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
	}

	public Hand getActiveHand() {
		return (this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
	}

	private void tickActiveItemStack() {
		if (this.isUsingItem()) {
			if (ItemStack.areItemsEqual(this.getStackInHand(this.getActiveHand()), this.activeItemStack)) {
				this.activeItemStack = this.getStackInHand(this.getActiveHand());
				this.tickItemStackUsage(this.activeItemStack);
			} else {
				this.clearActiveItem();
			}
		}
	}

	protected void tickItemStackUsage(ItemStack stack) {
		stack.usageTick(this.getWorld(), this, this.getItemUseTimeLeft());
		if (this.shouldSpawnConsumptionEffects()) {
			this.spawnConsumptionEffects(stack, 5);
		}

		if (--this.itemUseTimeLeft == 0 && !this.getWorld().isClient && !stack.isUsedOnRelease()) {
			this.consumeItem();
		}
	}

	private boolean shouldSpawnConsumptionEffects() {
		int i = this.activeItemStack.getMaxUseTime() - this.getItemUseTimeLeft();
		int j = (int)((float)this.activeItemStack.getMaxUseTime() * 0.21875F);
		boolean bl = i > j;
		return bl && this.getItemUseTimeLeft() % 4 == 0;
	}

	private void updateLeaningPitch() {
		this.lastLeaningPitch = this.leaningPitch;
		if (this.isInSwimmingPose()) {
			this.leaningPitch = Math.min(1.0F, this.leaningPitch + 0.09F);
		} else {
			this.leaningPitch = Math.max(0.0F, this.leaningPitch - 0.09F);
		}
	}

	protected void setLivingFlag(int mask, boolean value) {
		int i = this.dataTracker.get(LIVING_FLAGS);
		if (value) {
			i |= mask;
		} else {
			i &= ~mask;
		}

		this.dataTracker.set(LIVING_FLAGS, (byte)i);
	}

	public void setCurrentHand(Hand hand) {
		ItemStack itemStack = this.getStackInHand(hand);
		if (!itemStack.isEmpty() && !this.isUsingItem()) {
			this.activeItemStack = itemStack;
			this.itemUseTimeLeft = itemStack.getMaxUseTime();
			if (!this.getWorld().isClient) {
				this.setLivingFlag(USING_ITEM_FLAG, true);
				this.setLivingFlag(OFF_HAND_ACTIVE_FLAG, hand == Hand.OFF_HAND);
				this.emitGameEvent(GameEvent.ITEM_INTERACT_START);
			}
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (SLEEPING_POSITION.equals(data)) {
			if (this.getWorld().isClient) {
				this.getSleepingPosition().ifPresent(this::setPositionInBed);
			}
		} else if (LIVING_FLAGS.equals(data) && this.getWorld().isClient) {
			if (this.isUsingItem() && this.activeItemStack.isEmpty()) {
				this.activeItemStack = this.getStackInHand(this.getActiveHand());
				if (!this.activeItemStack.isEmpty()) {
					this.itemUseTimeLeft = this.activeItemStack.getMaxUseTime();
				}
			} else if (!this.isUsingItem() && !this.activeItemStack.isEmpty()) {
				this.activeItemStack = ItemStack.EMPTY;
				this.itemUseTimeLeft = 0;
			}
		}
	}

	@Override
	public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
		super.lookAt(anchorPoint, target);
		this.prevHeadYaw = this.headYaw;
		this.bodyYaw = this.headYaw;
		this.prevBodyYaw = this.bodyYaw;
	}

	protected void spawnConsumptionEffects(ItemStack stack, int particleCount) {
		if (!stack.isEmpty() && this.isUsingItem()) {
			if (stack.getUseAction() == UseAction.DRINK) {
				this.playSound(this.getDrinkSound(stack), 0.5F, this.getWorld().random.nextFloat() * 0.1F + 0.9F);
			}

			if (stack.getUseAction() == UseAction.EAT) {
				this.spawnItemParticles(stack, particleCount);
				this.playSound(this.getEatSound(stack), 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}
		}
	}

	private void spawnItemParticles(ItemStack stack, int count) {
		for(int i = 0; i < count; ++i) {
			Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
			vec3d = vec3d.rotateX(-this.getPitch() * (float) (Math.PI / 180.0));
			vec3d = vec3d.rotateY(-this.getYaw() * (float) (Math.PI / 180.0));
			double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
			Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
			vec3d2 = vec3d2.rotateX(-this.getPitch() * (float) (Math.PI / 180.0));
			vec3d2 = vec3d2.rotateY(-this.getYaw() * (float) (Math.PI / 180.0));
			vec3d2 = vec3d2.add(this.getX(), this.getEyeY(), this.getZ());
			this.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
		}
	}

	protected void consumeItem() {
		if (!this.getWorld().isClient || this.isUsingItem()) {
			Hand hand = this.getActiveHand();
			if (!this.activeItemStack.equals(this.getStackInHand(hand))) {
				this.stopUsingItem();
			} else {
				if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
					this.spawnConsumptionEffects(this.activeItemStack, 16);
					ItemStack itemStack = this.activeItemStack.finishUsing(this.getWorld(), this);
					if (itemStack != this.activeItemStack) {
						this.setStackInHand(hand, itemStack);
					}

					this.clearActiveItem();
				}
			}
		}
	}

	public ItemStack getActiveItem() {
		return this.activeItemStack;
	}

	public int getItemUseTimeLeft() {
		return this.itemUseTimeLeft;
	}

	public int getItemUseTime() {
		return this.isUsingItem() ? this.activeItemStack.getMaxUseTime() - this.getItemUseTimeLeft() : 0;
	}

	public void stopUsingItem() {
		if (!this.activeItemStack.isEmpty()) {
			this.activeItemStack.onStoppedUsing(this.getWorld(), this, this.getItemUseTimeLeft());
			if (this.activeItemStack.isUsedOnRelease()) {
				this.tickActiveItemStack();
			}
		}

		this.clearActiveItem();
	}

	public void clearActiveItem() {
		if (!this.getWorld().isClient) {
			boolean bl = this.isUsingItem();
			this.setLivingFlag(USING_ITEM_FLAG, false);
			if (bl) {
				this.emitGameEvent(GameEvent.ITEM_INTERACT_FINISH);
			}
		}

		this.activeItemStack = ItemStack.EMPTY;
		this.itemUseTimeLeft = 0;
	}

	public boolean isBlocking() {
		if (this.isUsingItem() && !this.activeItemStack.isEmpty()) {
			Item item = this.activeItemStack.getItem();
			if (item.getUseAction(this.activeItemStack) != UseAction.BLOCK) {
				return false;
			} else {
				return item.getMaxUseTime(this.activeItemStack) - this.itemUseTimeLeft >= 5;
			}
		} else {
			return false;
		}
	}

	/**
	 * @return {@code true} if this entity should not lose height while in a climbing state
	 * @see net.minecraft.entity.LivingEntity
	 */
	public boolean isHoldingOntoLadder() {
		return this.isSneaking();
	}

	public boolean isFallFlying() {
		return this.getFlag(Entity.FALL_FLYING_FLAG_INDEX);
	}

	@Override
	public boolean isInSwimmingPose() {
		return super.isInSwimmingPose() || !this.isFallFlying() && this.isInPose(EntityPose.FALL_FLYING);
	}

	public int getFallFlyingTicks() {
		return this.fallFlyingTicks;
	}

	public boolean teleport(double x, double y, double z, boolean particleEffects) {
		double d = this.getX();
		double e = this.getY();
		double f = this.getZ();
		double g = y;
		boolean bl = false;
		BlockPos blockPos = BlockPos.ofFloored(x, y, z);
		World world = this.getWorld();
		if (world.isChunkLoaded(blockPos)) {
			boolean bl2 = false;

			while(!bl2 && blockPos.getY() > world.getBottomY()) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState = world.getBlockState(blockPos2);
				if (blockState.blocksMovement()) {
					bl2 = true;
				} else {
					--g;
					blockPos = blockPos2;
				}
			}

			if (bl2) {
				this.requestTeleport(x, g, z);
				if (world.isSpaceEmpty(this) && !world.containsFluid(this.getBoundingBox())) {
					bl = true;
				}
			}
		}

		if (!bl) {
			this.requestTeleport(d, e, f);
			return false;
		} else {
			if (particleEffects) {
				world.sendEntityStatus(this, EntityStatuses.ADD_PORTAL_PARTICLES);
			}

			if (this instanceof PathAwareEntity pathAwareEntity) {
				pathAwareEntity.getNavigation().stop();
			}

			return true;
		}
	}

	public boolean isAffectedBySplashPotions() {
		return !this.isDead();
	}

	public boolean isMobOrPlayer() {
		return true;
	}

	public void setNearbySongPlaying(BlockPos songPosition, boolean playing) {
	}

	public boolean canEquip(ItemStack stack) {
		return false;
	}

	@Override
	public final EntityDimensions getDimensions(EntityPose pose) {
		return pose == EntityPose.SLEEPING ? SLEEPING_DIMENSIONS : this.getBaseDimensions(pose).scaled(this.getScale());
	}

	protected EntityDimensions getBaseDimensions(EntityPose pose) {
		return this.getType().getDimensions().scaled(this.getScaleFactor());
	}

	public ImmutableList<EntityPose> getPoses() {
		return ImmutableList.of(EntityPose.STANDING);
	}

	public Box getBoundingBox(EntityPose pose) {
		EntityDimensions entityDimensions = this.getDimensions(pose);
		return new Box(
			(double)(-entityDimensions.width() / 2.0F),
			0.0,
			(double)(-entityDimensions.width() / 2.0F),
			(double)(entityDimensions.width() / 2.0F),
			(double)entityDimensions.height(),
			(double)(entityDimensions.width() / 2.0F)
		);
	}

	protected boolean wouldNotSuffocateInPose(EntityPose pose) {
		Box box = this.getDimensions(pose).getBoxAt(this.getPos());
		return this.getWorld().isBlockSpaceEmpty(this, box);
	}

	@Override
	public boolean canUsePortals() {
		return super.canUsePortals() && !this.isSleeping();
	}

	public Optional<BlockPos> getSleepingPosition() {
		return this.dataTracker.get(SLEEPING_POSITION);
	}

	public void setSleepingPosition(BlockPos pos) {
		this.dataTracker.set(SLEEPING_POSITION, Optional.of(pos));
	}

	public void clearSleepingPosition() {
		this.dataTracker.set(SLEEPING_POSITION, Optional.empty());
	}

	public boolean isSleeping() {
		return this.getSleepingPosition().isPresent();
	}

	public void sleep(BlockPos pos) {
		if (this.hasVehicle()) {
			this.stopRiding();
		}

		BlockState blockState = this.getWorld().getBlockState(pos);
		if (blockState.getBlock() instanceof BedBlock) {
			this.getWorld().setBlockState(pos, blockState.with(BedBlock.OCCUPIED, Boolean.valueOf(true)), Block.NOTIFY_ALL);
		}

		this.setPose(EntityPose.SLEEPING);
		this.setPositionInBed(pos);
		this.setSleepingPosition(pos);
		this.setVelocity(Vec3d.ZERO);
		this.velocityDirty = true;
	}

	private void setPositionInBed(BlockPos pos) {
		this.setPosition((double)pos.getX() + 0.5, (double)pos.getY() + 0.6875, (double)pos.getZ() + 0.5);
	}

	private boolean isSleepingInBed() {
		return this.getSleepingPosition().map(pos -> this.getWorld().getBlockState(pos).getBlock() instanceof BedBlock).orElse(false);
	}

	/**
	 * Wakes this entity up.
	 * 
	 * @see net.minecraft.entity.player.PlayerEntity#wakeUp(boolean, boolean) a more specific overload for players
	 */
	public void wakeUp() {
		this.getSleepingPosition().filter(this.getWorld()::isChunkLoaded).ifPresent(pos -> {
			BlockState blockState = this.getWorld().getBlockState(pos);
			if (blockState.getBlock() instanceof BedBlock) {
				Direction direction = blockState.get(BedBlock.FACING);
				this.getWorld().setBlockState(pos, blockState.with(BedBlock.OCCUPIED, Boolean.valueOf(false)), Block.NOTIFY_ALL);
				Vec3d vec3dxx = (Vec3d)BedBlock.findWakeUpPosition(this.getType(), this.getWorld(), pos, direction, this.getYaw()).orElseGet(() -> {
					BlockPos blockPos2 = pos.up();
					return new Vec3d((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.1, (double)blockPos2.getZ() + 0.5);
				});
				Vec3d vec3d2 = Vec3d.ofBottomCenter(pos).subtract(vec3dxx).normalize();
				float f = (float)MathHelper.wrapDegrees(MathHelper.atan2(vec3d2.z, vec3d2.x) * 180.0F / (float)Math.PI - 90.0);
				this.setPosition(vec3dxx.x, vec3dxx.y, vec3dxx.z);
				this.setYaw(f);
				this.setPitch(0.0F);
			}
		});
		Vec3d vec3d = this.getPos();
		this.setPose(EntityPose.STANDING);
		this.setPosition(vec3d.x, vec3d.y, vec3d.z);
		this.clearSleepingPosition();
	}

	@Nullable
	public Direction getSleepingDirection() {
		BlockPos blockPos = (BlockPos)this.getSleepingPosition().orElse(null);
		return blockPos != null ? BedBlock.getDirection(this.getWorld(), blockPos) : null;
	}

	@Override
	public boolean isInsideWall() {
		return !this.isSleeping() && super.isInsideWall();
	}

	public ItemStack getProjectileType(ItemStack stack) {
		return ItemStack.EMPTY;
	}

	public ItemStack eatFood(World world, ItemStack stack) {
		FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);
		if (foodComponent != null) {
			world.playSound(
				null,
				this.getX(),
				this.getY(),
				this.getZ(),
				this.getEatSound(stack),
				SoundCategory.NEUTRAL,
				1.0F,
				1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F
			);
			this.applyFoodEffects(foodComponent);
			stack.decrementUnlessCreative(1, this);
			this.emitGameEvent(GameEvent.EAT);
		}

		return stack;
	}

	private void applyFoodEffects(FoodComponent component) {
		if (!this.getWorld().isClient()) {
			for(FoodComponent.StatusEffectEntry statusEffectEntry : component.effects()) {
				if (this.random.nextFloat() < statusEffectEntry.probability()) {
					this.addStatusEffect(statusEffectEntry.effect());
				}
			}
		}
	}

	private static byte getEquipmentBreakStatus(EquipmentSlot slot) {
		return switch(slot) {
			case MAINHAND -> 47;
			case OFFHAND -> 48;
			case HEAD -> 49;
			case CHEST -> 50;
			case FEET -> 52;
			case LEGS -> 51;
			case BODY -> 65;
		};
	}

	public void sendEquipmentBreakStatus(EquipmentSlot slot) {
		this.getWorld().sendEntityStatus(this, getEquipmentBreakStatus(slot));
	}

	public static EquipmentSlot getSlotForHand(Hand hand) {
		return hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
	}

	@Override
	public Box getVisibilityBoundingBox() {
		if (this.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.DRAGON_HEAD)) {
			float f = 0.5F;
			return this.getBoundingBox().expand(0.5, 0.5, 0.5);
		} else {
			return super.getVisibilityBoundingBox();
		}
	}

	public static EquipmentSlot getPreferredEquipmentSlot(ItemStack stack) {
		Equipment equipment = Equipment.fromStack(stack);
		return equipment != null ? equipment.getSlotType() : EquipmentSlot.MAINHAND;
	}

	private static StackReference getStackReference(LivingEntity entity, EquipmentSlot slot) {
		return slot != EquipmentSlot.HEAD && slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND
			? StackReference.of(entity, slot, stack -> stack.isEmpty() || MobEntity.getPreferredEquipmentSlot(stack) == slot)
			: StackReference.of(entity, slot);
	}

	@Nullable
	private static EquipmentSlot getEquipmentSlot(int slotId) {
		if (slotId == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
			return EquipmentSlot.HEAD;
		} else if (slotId == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
			return EquipmentSlot.CHEST;
		} else if (slotId == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
			return EquipmentSlot.LEGS;
		} else if (slotId == 100 + EquipmentSlot.FEET.getEntitySlotId()) {
			return EquipmentSlot.FEET;
		} else if (slotId == 98) {
			return EquipmentSlot.MAINHAND;
		} else if (slotId == 99) {
			return EquipmentSlot.OFFHAND;
		} else {
			return slotId == 105 ? EquipmentSlot.BODY : null;
		}
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		EquipmentSlot equipmentSlot = getEquipmentSlot(mappedIndex);
		return equipmentSlot != null ? getStackReference(this, equipmentSlot) : super.getStackReference(mappedIndex);
	}

	@Override
	public boolean canFreeze() {
		if (this.isSpectator()) {
			return false;
		} else {
			boolean bl = !this.getEquippedStack(EquipmentSlot.HEAD).isIn(ItemTags.FREEZE_IMMUNE_WEARABLES)
				&& !this.getEquippedStack(EquipmentSlot.CHEST).isIn(ItemTags.FREEZE_IMMUNE_WEARABLES)
				&& !this.getEquippedStack(EquipmentSlot.LEGS).isIn(ItemTags.FREEZE_IMMUNE_WEARABLES)
				&& !this.getEquippedStack(EquipmentSlot.FEET).isIn(ItemTags.FREEZE_IMMUNE_WEARABLES);
			return bl && super.canFreeze();
		}
	}

	@Override
	public boolean isGlowing() {
		return !this.getWorld().isClient() && this.hasStatusEffect(StatusEffects.GLOWING) || super.isGlowing();
	}

	@Override
	public float getBodyYaw() {
		return this.bodyYaw;
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		float g = packet.getYaw();
		float h = packet.getPitch();
		this.updateTrackedPosition(d, e, f);
		this.bodyYaw = packet.getHeadYaw();
		this.headYaw = packet.getHeadYaw();
		this.prevBodyYaw = this.bodyYaw;
		this.prevHeadYaw = this.headYaw;
		this.setId(packet.getId());
		this.setUuid(packet.getUuid());
		this.updatePositionAndAngles(d, e, f, g, h);
		this.setVelocity(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ());
	}

	public boolean disablesShield() {
		return this.getMainHandStack().getItem() instanceof AxeItem;
	}

	@Override
	public float getStepHeight() {
		float f = (float)this.getAttributeValue(EntityAttributes.GENERIC_STEP_HEIGHT);
		return this.getControllingPassenger() instanceof PlayerEntity ? Math.max(f, 1.0F) : f;
	}

	@Override
	public Vec3d getPassengerRidingPos(Entity passenger) {
		return this.getPos().add(this.getPassengerAttachmentPos(passenger, this.getDimensions(this.getPose()), this.getScale() * this.getScaleFactor()));
	}

	protected void lerpHeadYaw(int headTrackingIncrements, double serverHeadYaw) {
		this.headYaw = (float)MathHelper.lerpAngleDegrees(1.0 / (double)headTrackingIncrements, (double)this.headYaw, serverHeadYaw);
	}

	@Override
	public void setOnFireForTicks(int ticks) {
		super.setOnFireForTicks(ProtectionEnchantment.transformFireDuration(this, ticks));
	}

	public boolean isInCreativeMode() {
		return false;
	}

	public static record FallSounds(SoundEvent small, SoundEvent big) {
	}
}
