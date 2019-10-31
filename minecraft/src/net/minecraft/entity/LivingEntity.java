package net.minecraft.entity;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.network.packet.EntityAnimationS2CPacket;
import net.minecraft.client.network.packet.EntityEquipmentUpdateS2CPacket;
import net.minecraft.client.network.packet.ItemPickupAnimationS2CPacket;
import net.minecraft.client.network.packet.MobSpawnS2CPacket;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.AbstractEntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Arm;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextTypes;
import org.apache.commons.lang3.tuple.Pair;

public abstract class LivingEntity extends Entity {
	private static final UUID ATTR_SPRINTING_SPEED_BOOST_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
	private static final EntityAttributeModifier ATTR_SPRINTING_SPEED_BOOST = new EntityAttributeModifier(
			ATTR_SPRINTING_SPEED_BOOST_ID, "Sprinting speed boost", 0.3F, EntityAttributeModifier.Operation.field_6331
		)
		.setSerialize(false);
	protected static final TrackedData<Byte> LIVING_FLAGS = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Float> HEALTH = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> POTION_SWIRLS_COLOR = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> POTION_SWIRLS_AMBIENT = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> STUCK_ARROWS = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Optional<BlockPos>> SLEEPING_POSITION = DataTracker.registerData(
		LivingEntity.class, TrackedDataHandlerRegistry.OPTIONA_BLOCK_POS
	);
	protected static final EntityDimensions SLEEPING_DIMENSIONS = EntityDimensions.fixed(0.2F, 0.2F);
	private AbstractEntityAttributeContainer attributeContainer;
	private final DamageTracker damageTracker = new DamageTracker(this);
	private final Map<StatusEffect, StatusEffectInstance> activeStatusEffects = Maps.<StatusEffect, StatusEffectInstance>newHashMap();
	private final DefaultedList<ItemStack> equippedHand = DefaultedList.ofSize(2, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> equippedArmor = DefaultedList.ofSize(4, ItemStack.EMPTY);
	public boolean isHandSwinging;
	public Hand preferredHand;
	public int handSwingTicks;
	public int stuckArrowTimer;
	public int hurtTime;
	public int field_6254;
	public float field_6271;
	public int deathTime;
	public float lastHandSwingProgress;
	public float handSwingProgress;
	protected int lastAttackedTicks;
	public float lastLimbDistance;
	public float limbDistance;
	public float limbAngle;
	public final int field_6269 = 20;
	public final float field_6244;
	public final float field_6262;
	public float field_6283;
	public float field_6220;
	public float headYaw;
	public float prevHeadYaw;
	public float field_6281 = 0.02F;
	protected PlayerEntity attackingPlayer;
	protected int playerHitTimer;
	protected boolean dead;
	protected int despawnCounter;
	protected float field_6217;
	protected float field_6233;
	protected float field_6255;
	protected float field_6275;
	protected float field_6215;
	protected int field_6232;
	protected float field_6253;
	protected boolean jumping;
	public float sidewaysSpeed;
	public float upwardSpeed;
	public float forwardSpeed;
	public float field_6267;
	protected int field_6210;
	protected double field_6224;
	protected double field_6245;
	protected double field_6263;
	protected double field_6284;
	protected double field_6221;
	protected double field_6242;
	protected int field_6265;
	private boolean field_6285 = true;
	@Nullable
	private LivingEntity attacker;
	private int lastAttackedTime;
	private LivingEntity attacking;
	private int lastAttackTime;
	private float movementSpeed;
	private int field_6228;
	private float absorptionAmount;
	protected ItemStack activeItemStack = ItemStack.EMPTY;
	protected int itemUseTimeLeft;
	protected int field_6239;
	private BlockPos lastBlockPos;
	private DamageSource field_6276;
	private long field_6226;
	protected int field_6261;
	private float field_6243;
	private float field_6264;
	protected Brain<?> brain;

	protected LivingEntity(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
		this.initAttributes();
		this.setHealth(this.getHealthMaximum());
		this.inanimate = true;
		this.field_6262 = (float)((Math.random() + 1.0) * 0.01F);
		this.setPosition(this.x, this.y, this.z);
		this.field_6244 = (float)Math.random() * 12398.0F;
		this.yaw = (float)(Math.random() * (float) (Math.PI * 2));
		this.headYaw = this.yaw;
		this.stepHeight = 0.6F;
		this.brain = this.deserializeBrain(new Dynamic<>(NbtOps.INSTANCE, new CompoundTag()));
	}

	public Brain<?> getBrain() {
		return this.brain;
	}

	protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
		return new Brain<>(ImmutableList.<MemoryModuleType<?>>of(), ImmutableList.of(), dynamic);
	}

	@Override
	public void kill() {
		this.damage(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
	}

	public boolean canTarget(EntityType<?> entityType) {
		return true;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(LIVING_FLAGS, (byte)0);
		this.dataTracker.startTracking(POTION_SWIRLS_COLOR, 0);
		this.dataTracker.startTracking(POTION_SWIRLS_AMBIENT, false);
		this.dataTracker.startTracking(STUCK_ARROWS, 0);
		this.dataTracker.startTracking(HEALTH, 1.0F);
		this.dataTracker.startTracking(SLEEPING_POSITION, Optional.empty());
	}

	protected void initAttributes() {
		this.getAttributeContainer().register(EntityAttributes.MAX_HEALTH);
		this.getAttributeContainer().register(EntityAttributes.KNOCKBACK_RESISTANCE);
		this.getAttributeContainer().register(EntityAttributes.MOVEMENT_SPEED);
		this.getAttributeContainer().register(EntityAttributes.ARMOR);
		this.getAttributeContainer().register(EntityAttributes.ARMOR_TOUGHNESS);
	}

	@Override
	protected void fall(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
		if (!this.isInsideWater()) {
			this.checkWaterState();
		}

		if (!this.world.isClient && this.fallDistance > 3.0F && bl) {
			float f = (float)MathHelper.ceil(this.fallDistance - 3.0F);
			if (!blockState.isAir()) {
				double e = Math.min((double)(0.2F + f / 15.0F), 2.5);
				int i = (int)(150.0 * e);
				((ServerWorld)this.world)
					.spawnParticles(new BlockStateParticleEffect(ParticleTypes.field_11217, blockState), this.x, this.y, this.z, i, 0.0, 0.0, 0.0, 0.15F);
			}
		}

		super.fall(d, bl, blockState, blockPos);
	}

	public boolean canBreatheInWater() {
		return this.getGroup() == EntityGroup.UNDEAD;
	}

	@Environment(EnvType.CLIENT)
	public float method_6024(float f) {
		return MathHelper.lerp(f, this.field_6264, this.field_6243);
	}

	@Override
	public void baseTick() {
		this.lastHandSwingProgress = this.handSwingProgress;
		if (this.firstUpdate) {
			this.getSleepingPosition().ifPresent(this::setPositionInBed);
		}

		super.baseTick();
		this.world.getProfiler().push("livingEntityBaseTick");
		boolean bl = this instanceof PlayerEntity;
		if (this.isAlive()) {
			if (this.isInsideWall()) {
				this.damage(DamageSource.IN_WALL, 1.0F);
			} else if (bl && !this.world.getWorldBorder().contains(this.getBoundingBox())) {
				double d = this.world.getWorldBorder().contains(this) + this.world.getWorldBorder().getBuffer();
				if (d < 0.0) {
					double e = this.world.getWorldBorder().getDamagePerBlock();
					if (e > 0.0) {
						this.damage(DamageSource.IN_WALL, (float)Math.max(1, MathHelper.floor(-d * e)));
					}
				}
			}
		}

		if (this.isFireImmune() || this.world.isClient) {
			this.extinguish();
		}

		boolean bl2 = bl && ((PlayerEntity)this).abilities.invulnerable;
		if (this.isAlive()) {
			if (this.isInFluid(FluidTags.field_15517)
				&& this.world.getBlockState(new BlockPos(this.x, this.y + (double)this.getStandingEyeHeight(), this.z)).getBlock() != Blocks.field_10422) {
				if (!this.canBreatheInWater() && !StatusEffectUtil.hasWaterBreathing(this) && !bl2) {
					this.setBreath(this.getNextBreathInWater(this.getBreath()));
					if (this.getBreath() == -20) {
						this.setBreath(0);
						Vec3d vec3d = this.getVelocity();

						for (int i = 0; i < 8; i++) {
							float f = this.random.nextFloat() - this.random.nextFloat();
							float g = this.random.nextFloat() - this.random.nextFloat();
							float h = this.random.nextFloat() - this.random.nextFloat();
							this.world.addParticle(ParticleTypes.field_11247, this.x + (double)f, this.y + (double)g, this.z + (double)h, vec3d.x, vec3d.y, vec3d.z);
						}

						this.damage(DamageSource.DROWN, 2.0F);
					}
				}

				if (!this.world.isClient && this.hasVehicle() && this.getVehicle() != null && !this.getVehicle().canBeRiddenInWater()) {
					this.stopRiding();
				}
			} else if (this.getBreath() < this.getMaxBreath()) {
				this.setBreath(this.getNextBreathInAir(this.getBreath()));
			}

			if (!this.world.isClient) {
				BlockPos blockPos = new BlockPos(this);
				if (!Objects.equal(this.lastBlockPos, blockPos)) {
					this.lastBlockPos = blockPos;
					this.applyFrostWalker(blockPos);
				}
			}
		}

		if (this.isAlive() && this.isTouchingWater()) {
			this.extinguish();
		}

		if (this.hurtTime > 0) {
			this.hurtTime--;
		}

		if (this.timeUntilRegen > 0 && !(this instanceof ServerPlayerEntity)) {
			this.timeUntilRegen--;
		}

		if (this.getHealth() <= 0.0F) {
			this.updatePostDeath();
		}

		if (this.playerHitTimer > 0) {
			this.playerHitTimer--;
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

		this.spawnPotionParticles();
		this.field_6275 = this.field_6255;
		this.field_6220 = this.field_6283;
		this.prevHeadYaw = this.headYaw;
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
		this.world.getProfiler().pop();
	}

	protected void applyFrostWalker(BlockPos blockPos) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.field_9122, this);
		if (i > 0) {
			FrostWalkerEnchantment.freezeWater(this, this.world, blockPos, i);
		}
	}

	public boolean isBaby() {
		return false;
	}

	public float getScaleFactor() {
		return this.isBaby() ? 0.5F : 1.0F;
	}

	@Override
	public boolean canBeRiddenInWater() {
		return false;
	}

	protected void updatePostDeath() {
		this.deathTime++;
		if (this.deathTime == 20) {
			if (!this.world.isClient
				&& (this.shouldAlwaysDropXp() || this.playerHitTimer > 0 && this.canDropLootAndXp() && this.world.getGameRules().getBoolean(GameRules.field_19391))) {
				int i = this.getCurrentExperience(this.attackingPlayer);

				while (i > 0) {
					int j = ExperienceOrbEntity.roundToOrbSize(i);
					i -= j;
					this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y, this.z, j));
				}
			}

			this.remove();

			for (int i = 0; i < 20; i++) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.world
					.addParticle(
						ParticleTypes.field_11203,
						this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
						this.y + (double)(this.random.nextFloat() * this.getHeight()),
						this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
						d,
						e,
						f
					);
			}
		}
	}

	protected boolean canDropLootAndXp() {
		return !this.isBaby();
	}

	protected int getNextBreathInWater(int i) {
		int j = EnchantmentHelper.getRespiration(this);
		return j > 0 && this.random.nextInt(j + 1) > 0 ? i : i - 1;
	}

	protected int getNextBreathInAir(int i) {
		return Math.min(i + 4, this.getMaxBreath());
	}

	protected int getCurrentExperience(PlayerEntity playerEntity) {
		return 0;
	}

	protected boolean shouldAlwaysDropXp() {
		return false;
	}

	public Random getRand() {
		return this.random;
	}

	@Nullable
	public LivingEntity getAttacker() {
		return this.attacker;
	}

	public int getLastAttackedTime() {
		return this.lastAttackedTime;
	}

	public void setAttacker(@Nullable LivingEntity livingEntity) {
		this.attacker = livingEntity;
		this.lastAttackedTime = this.age;
	}

	@Nullable
	public LivingEntity getAttacking() {
		return this.attacking;
	}

	public int getLastAttackTime() {
		return this.lastAttackTime;
	}

	public void onAttacking(Entity entity) {
		if (entity instanceof LivingEntity) {
			this.attacking = (LivingEntity)entity;
		} else {
			this.attacking = null;
		}

		this.lastAttackTime = this.age;
	}

	public int getDespawnCounter() {
		return this.despawnCounter;
	}

	public void setDespawnCounter(int i) {
		this.despawnCounter = i;
	}

	protected void onEquipStack(ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			SoundEvent soundEvent = SoundEvents.field_14883;
			Item item = itemStack.getItem();
			if (item instanceof ArmorItem) {
				soundEvent = ((ArmorItem)item).getMaterial().getEquipSound();
			} else if (item == Items.field_8833) {
				soundEvent = SoundEvents.field_14966;
			}

			this.playSound(soundEvent, 1.0F, 1.0F);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.putFloat("Health", this.getHealth());
		compoundTag.putShort("HurtTime", (short)this.hurtTime);
		compoundTag.putInt("HurtByTimestamp", this.lastAttackedTime);
		compoundTag.putShort("DeathTime", (short)this.deathTime);
		compoundTag.putFloat("AbsorptionAmount", this.getAbsorptionAmount());

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			ItemStack itemStack = this.getEquippedStack(equipmentSlot);
			if (!itemStack.isEmpty()) {
				this.getAttributeContainer().removeAll(itemStack.getAttributeModifiers(equipmentSlot));
			}
		}

		compoundTag.put("Attributes", EntityAttributes.toTag(this.getAttributeContainer()));

		for (EquipmentSlot equipmentSlotx : EquipmentSlot.values()) {
			ItemStack itemStack = this.getEquippedStack(equipmentSlotx);
			if (!itemStack.isEmpty()) {
				this.getAttributeContainer().replaceAll(itemStack.getAttributeModifiers(equipmentSlotx));
			}
		}

		if (!this.activeStatusEffects.isEmpty()) {
			ListTag listTag = new ListTag();

			for (StatusEffectInstance statusEffectInstance : this.activeStatusEffects.values()) {
				listTag.add(statusEffectInstance.serialize(new CompoundTag()));
			}

			compoundTag.put("ActiveEffects", listTag);
		}

		compoundTag.putBoolean("FallFlying", this.isFallFlying());
		this.getSleepingPosition().ifPresent(blockPos -> {
			compoundTag.putInt("SleepingX", blockPos.getX());
			compoundTag.putInt("SleepingY", blockPos.getY());
			compoundTag.putInt("SleepingZ", blockPos.getZ());
		});
		compoundTag.put("Brain", this.brain.serialize(NbtOps.INSTANCE));
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		this.setAbsorptionAmount(compoundTag.getFloat("AbsorptionAmount"));
		if (compoundTag.containsKey("Attributes", 9) && this.world != null && !this.world.isClient) {
			EntityAttributes.fromTag(this.getAttributeContainer(), compoundTag.getList("Attributes", 10));
		}

		if (compoundTag.containsKey("ActiveEffects", 9)) {
			ListTag listTag = compoundTag.getList("ActiveEffects", 10);

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag2 = listTag.getCompoundTag(i);
				StatusEffectInstance statusEffectInstance = StatusEffectInstance.deserialize(compoundTag2);
				if (statusEffectInstance != null) {
					this.activeStatusEffects.put(statusEffectInstance.getEffectType(), statusEffectInstance);
				}
			}
		}

		if (compoundTag.containsKey("Health", 99)) {
			this.setHealth(compoundTag.getFloat("Health"));
		}

		this.hurtTime = compoundTag.getShort("HurtTime");
		this.deathTime = compoundTag.getShort("DeathTime");
		this.lastAttackedTime = compoundTag.getInt("HurtByTimestamp");
		if (compoundTag.containsKey("Team", 8)) {
			String string = compoundTag.getString("Team");
			Team team = this.world.getScoreboard().getTeam(string);
			boolean bl = team != null && this.world.getScoreboard().addPlayerToTeam(this.getUuidAsString(), team);
			if (!bl) {
				LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", string);
			}
		}

		if (compoundTag.getBoolean("FallFlying")) {
			this.setFlag(7, true);
		}

		if (compoundTag.containsKey("SleepingX", 99) && compoundTag.containsKey("SleepingY", 99) && compoundTag.containsKey("SleepingZ", 99)) {
			BlockPos blockPos = new BlockPos(compoundTag.getInt("SleepingX"), compoundTag.getInt("SleepingY"), compoundTag.getInt("SleepingZ"));
			this.setSleepingPosition(blockPos);
			this.dataTracker.set(POSE, EntityPose.field_18078);
			if (!this.firstUpdate) {
				this.setPositionInBed(blockPos);
			}
		}

		if (compoundTag.containsKey("Brain", 10)) {
			this.brain = this.deserializeBrain(new Dynamic<>(NbtOps.INSTANCE, compoundTag.getTag("Brain")));
		}
	}

	protected void spawnPotionParticles() {
		Iterator<StatusEffect> iterator = this.activeStatusEffects.keySet().iterator();

		try {
			while (iterator.hasNext()) {
				StatusEffect statusEffect = (StatusEffect)iterator.next();
				StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.activeStatusEffects.get(statusEffect);
				if (!statusEffectInstance.update(this)) {
					if (!this.world.isClient) {
						iterator.remove();
						this.method_6129(statusEffectInstance);
					}
				} else if (statusEffectInstance.getDuration() % 600 == 0) {
					this.method_6009(statusEffectInstance, false);
				}
			}
		} catch (ConcurrentModificationException var11) {
		}

		if (this.field_6285) {
			if (!this.world.isClient) {
				this.updatePotionVisibility();
			}

			this.field_6285 = false;
		}

		int i = this.dataTracker.get(POTION_SWIRLS_COLOR);
		boolean bl = this.dataTracker.get(POTION_SWIRLS_AMBIENT);
		if (i > 0) {
			boolean bl2;
			if (this.isInvisible()) {
				bl2 = this.random.nextInt(15) == 0;
			} else {
				bl2 = this.random.nextBoolean();
			}

			if (bl) {
				bl2 &= this.random.nextInt(5) == 0;
			}

			if (bl2 && i > 0) {
				double d = (double)(i >> 16 & 0xFF) / 255.0;
				double e = (double)(i >> 8 & 0xFF) / 255.0;
				double f = (double)(i >> 0 & 0xFF) / 255.0;
				this.world
					.addParticle(
						bl ? ParticleTypes.field_11225 : ParticleTypes.field_11226,
						this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
						this.y + this.random.nextDouble() * (double)this.getHeight(),
						this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
						d,
						e,
						f
					);
			}
		}
	}

	protected void updatePotionVisibility() {
		if (this.activeStatusEffects.isEmpty()) {
			this.clearPotionSwirls();
			this.setInvisible(false);
		} else {
			Collection<StatusEffectInstance> collection = this.activeStatusEffects.values();
			this.dataTracker.set(POTION_SWIRLS_AMBIENT, containsOnlyAmbientEffects(collection));
			this.dataTracker.set(POTION_SWIRLS_COLOR, PotionUtil.getColor(collection));
			this.setInvisible(this.hasStatusEffect(StatusEffects.field_5905));
		}
	}

	public double getAttackDistanceScalingFactor(@Nullable Entity entity) {
		double d = 1.0;
		if (this.isSneaking()) {
			d *= 0.8;
		}

		if (this.isInvisible()) {
			float f = this.method_18396();
			if (f < 0.1F) {
				f = 0.1F;
			}

			d *= 0.7 * (double)f;
		}

		if (entity != null) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.field_6169);
			Item item = itemStack.getItem();
			EntityType<?> entityType = entity.getType();
			if (entityType == EntityType.field_6137 && item == Items.SKELETON_SKULL
				|| entityType == EntityType.field_6051 && item == Items.ZOMBIE_HEAD
				|| entityType == EntityType.field_6046 && item == Items.CREEPER_HEAD) {
				d *= 0.5;
			}
		}

		return d;
	}

	public boolean canTarget(LivingEntity livingEntity) {
		return true;
	}

	public boolean isTarget(LivingEntity livingEntity, TargetPredicate targetPredicate) {
		return targetPredicate.test(this, livingEntity);
	}

	public static boolean containsOnlyAmbientEffects(Collection<StatusEffectInstance> collection) {
		for (StatusEffectInstance statusEffectInstance : collection) {
			if (!statusEffectInstance.isAmbient()) {
				return false;
			}
		}

		return true;
	}

	protected void clearPotionSwirls() {
		this.dataTracker.set(POTION_SWIRLS_AMBIENT, false);
		this.dataTracker.set(POTION_SWIRLS_COLOR, 0);
	}

	public boolean clearPotionEffects() {
		if (this.world.isClient) {
			return false;
		} else {
			Iterator<StatusEffectInstance> iterator = this.activeStatusEffects.values().iterator();

			boolean bl;
			for (bl = false; iterator.hasNext(); bl = true) {
				this.method_6129((StatusEffectInstance)iterator.next());
				iterator.remove();
			}

			return bl;
		}
	}

	public Collection<StatusEffectInstance> getStatusEffects() {
		return this.activeStatusEffects.values();
	}

	public Map<StatusEffect, StatusEffectInstance> getActiveStatusEffects() {
		return this.activeStatusEffects;
	}

	public boolean hasStatusEffect(StatusEffect statusEffect) {
		return this.activeStatusEffects.containsKey(statusEffect);
	}

	@Nullable
	public StatusEffectInstance getStatusEffect(StatusEffect statusEffect) {
		return (StatusEffectInstance)this.activeStatusEffects.get(statusEffect);
	}

	public boolean addPotionEffect(StatusEffectInstance statusEffectInstance) {
		if (!this.isPotionEffective(statusEffectInstance)) {
			return false;
		} else {
			StatusEffectInstance statusEffectInstance2 = (StatusEffectInstance)this.activeStatusEffects.get(statusEffectInstance.getEffectType());
			if (statusEffectInstance2 == null) {
				this.activeStatusEffects.put(statusEffectInstance.getEffectType(), statusEffectInstance);
				this.method_6020(statusEffectInstance);
				return true;
			} else if (statusEffectInstance2.upgrade(statusEffectInstance)) {
				this.method_6009(statusEffectInstance2, true);
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean isPotionEffective(StatusEffectInstance statusEffectInstance) {
		if (this.getGroup() == EntityGroup.UNDEAD) {
			StatusEffect statusEffect = statusEffectInstance.getEffectType();
			if (statusEffect == StatusEffects.field_5924 || statusEffect == StatusEffects.field_5899) {
				return false;
			}
		}

		return true;
	}

	public boolean isUndead() {
		return this.getGroup() == EntityGroup.UNDEAD;
	}

	@Nullable
	public StatusEffectInstance removePotionEffect(@Nullable StatusEffect statusEffect) {
		return (StatusEffectInstance)this.activeStatusEffects.remove(statusEffect);
	}

	public boolean removeStatusEffect(StatusEffect statusEffect) {
		StatusEffectInstance statusEffectInstance = this.removePotionEffect(statusEffect);
		if (statusEffectInstance != null) {
			this.method_6129(statusEffectInstance);
			return true;
		} else {
			return false;
		}
	}

	protected void method_6020(StatusEffectInstance statusEffectInstance) {
		this.field_6285 = true;
		if (!this.world.isClient) {
			statusEffectInstance.getEffectType().method_5555(this, this.getAttributeContainer(), statusEffectInstance.getAmplifier());
		}
	}

	protected void method_6009(StatusEffectInstance statusEffectInstance, boolean bl) {
		this.field_6285 = true;
		if (bl && !this.world.isClient) {
			StatusEffect statusEffect = statusEffectInstance.getEffectType();
			statusEffect.method_5562(this, this.getAttributeContainer(), statusEffectInstance.getAmplifier());
			statusEffect.method_5555(this, this.getAttributeContainer(), statusEffectInstance.getAmplifier());
		}
	}

	protected void method_6129(StatusEffectInstance statusEffectInstance) {
		this.field_6285 = true;
		if (!this.world.isClient) {
			statusEffectInstance.getEffectType().method_5562(this, this.getAttributeContainer(), statusEffectInstance.getAmplifier());
		}
	}

	public void heal(float f) {
		float g = this.getHealth();
		if (g > 0.0F) {
			this.setHealth(g + f);
		}
	}

	public float getHealth() {
		return this.dataTracker.get(HEALTH);
	}

	public void setHealth(float f) {
		this.dataTracker.set(HEALTH, MathHelper.clamp(f, 0.0F, this.getHealthMaximum()));
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (this.world.isClient) {
			return false;
		} else if (this.getHealth() <= 0.0F) {
			return false;
		} else if (damageSource.isFire() && this.hasStatusEffect(StatusEffects.field_5918)) {
			return false;
		} else {
			if (this.isSleeping() && !this.world.isClient) {
				this.wakeUp();
			}

			this.despawnCounter = 0;
			float g = f;
			if ((damageSource == DamageSource.ANVIL || damageSource == DamageSource.FALLING_BLOCK) && !this.getEquippedStack(EquipmentSlot.field_6169).isEmpty()) {
				this.getEquippedStack(EquipmentSlot.field_6169)
					.damage((int)(f * 4.0F + this.random.nextFloat() * f * 2.0F), this, livingEntityx -> livingEntityx.sendEquipmentBreakStatus(EquipmentSlot.field_6169));
				f *= 0.75F;
			}

			boolean bl = false;
			float h = 0.0F;
			if (f > 0.0F && this.method_6061(damageSource)) {
				this.damageShield(f);
				h = f;
				f = 0.0F;
				if (!damageSource.isProjectile()) {
					Entity entity = damageSource.getSource();
					if (entity instanceof LivingEntity) {
						this.takeShieldHit((LivingEntity)entity);
					}
				}

				bl = true;
			}

			this.limbDistance = 1.5F;
			Entity entity = damageSource.getAttacker();
			int i = 10;
			if (entity != null && entity instanceof PlayerEntity) {
				i = Math.min((int)((PlayerEntity)entity).method_21752(), i);
			}

			if (damageSource.isProjectile()) {
				i = 0;
			}

			boolean bl2 = true;
			if (this.timeUntilRegen > 0) {
				if (f <= this.field_6253) {
					return false;
				}

				this.applyDamage(damageSource, f - this.field_6253);
				this.field_6253 = f;
				bl2 = false;
			} else {
				this.field_6253 = f;
				this.timeUntilRegen = i;
				this.applyDamage(damageSource, f);
				this.field_6254 = 10;
				this.hurtTime = this.field_6254;
			}

			this.field_6271 = 0.0F;
			if (entity != null) {
				if (entity instanceof LivingEntity) {
					this.setAttacker((LivingEntity)entity);
				}

				if (entity instanceof PlayerEntity) {
					this.playerHitTimer = 100;
					this.attackingPlayer = (PlayerEntity)entity;
				} else if (entity instanceof WolfEntity) {
					WolfEntity wolfEntity = (WolfEntity)entity;
					if (wolfEntity.isTamed()) {
						this.playerHitTimer = 100;
						LivingEntity livingEntity = wolfEntity.getOwner();
						if (livingEntity != null && livingEntity.getType() == EntityType.field_6097) {
							this.attackingPlayer = (PlayerEntity)livingEntity;
						} else {
							this.attackingPlayer = null;
						}
					}
				}
			}

			if (bl2) {
				if (bl) {
					this.world.sendEntityStatus(this, (byte)29);
				} else if (damageSource instanceof EntityDamageSource && ((EntityDamageSource)damageSource).method_5549()) {
					this.world.sendEntityStatus(this, (byte)33);
				} else {
					byte b;
					if (damageSource == DamageSource.DROWN) {
						b = 36;
					} else if (damageSource.isFire()) {
						b = 37;
					} else if (damageSource == DamageSource.SWEET_BERRY_BUSH) {
						b = 44;
					} else {
						b = 2;
					}

					this.world.sendEntityStatus(this, b);
				}

				if (damageSource != DamageSource.DROWN && (!bl || f > 0.0F)) {
					this.scheduleVelocityUpdate();
				}

				if (entity != null) {
					double d = entity.x - this.x;

					double e;
					for (e = entity.z - this.z; d * d + e * e < 1.0E-4; e = (Math.random() - Math.random()) * 0.01) {
						d = (Math.random() - Math.random()) * 0.01;
					}

					this.field_6271 = (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI - (double)this.yaw);
					this.takeKnockback(entity, 0.4F, d, e);
				} else {
					this.field_6271 = (float)((int)(Math.random() * 2.0) * 180);
				}
			}

			if (this.getHealth() <= 0.0F) {
				if (!this.method_6095(damageSource)) {
					SoundEvent soundEvent = this.getDeathSound();
					if (bl2 && soundEvent != null) {
						this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
					}

					this.onDeath(damageSource);
				}
			} else if (bl2) {
				this.playHurtSound(damageSource);
			}

			boolean bl3 = !bl || f > 0.0F;
			if (bl3) {
				this.field_6276 = damageSource;
				this.field_6226 = this.world.getTime();
			}

			if (this instanceof ServerPlayerEntity) {
				Criterions.ENTITY_HURT_PLAYER.handle((ServerPlayerEntity)this, damageSource, g, f, bl);
				if (h > 0.0F && h < 3.4028235E37F) {
					((ServerPlayerEntity)this).increaseStat(Stats.field_15380, Math.round(h * 10.0F));
				}
			}

			if (entity instanceof ServerPlayerEntity) {
				Criterions.PLAYER_HURT_ENTITY.handle((ServerPlayerEntity)entity, this, damageSource, g, f, bl);
			}

			return bl3;
		}
	}

	protected void takeShieldHit(LivingEntity livingEntity) {
		livingEntity.knockback(this);
	}

	protected void knockback(LivingEntity livingEntity) {
		livingEntity.takeKnockback(this, 0.5F, livingEntity.x - this.x, livingEntity.z - this.z);
		if (this.getMainHandStack().getItem() instanceof AxeItem) {
			float f = 1.6F + (float)EnchantmentHelper.getChoppingLevel(this) * 0.5F;
			livingEntity.method_21748(f);
		}
	}

	public boolean method_21748(float f) {
		return false;
	}

	private boolean method_6095(DamageSource damageSource) {
		if (damageSource.doesDamageToCreative()) {
			return false;
		} else {
			ItemStack itemStack = null;

			for (Hand hand : Hand.values()) {
				ItemStack itemStack2 = this.getStackInHand(hand);
				if (itemStack2.getItem() == Items.field_8288) {
					itemStack = itemStack2.copy();
					itemStack2.decrement(1);
					break;
				}
			}

			if (itemStack != null) {
				if (this instanceof ServerPlayerEntity) {
					ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this;
					serverPlayerEntity.incrementStat(Stats.field_15372.getOrCreateStat(Items.field_8288));
					Criterions.USED_TOTEM.handle(serverPlayerEntity, itemStack);
				}

				this.setHealth(1.0F);
				this.clearPotionEffects();
				this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5924, 900, 1));
				this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5898, 100, 1));
				this.world.sendEntityStatus(this, (byte)35);
			}

			return itemStack != null;
		}
	}

	@Nullable
	public DamageSource getRecentDamageSource() {
		if (this.world.getTime() - this.field_6226 > 40L) {
			this.field_6276 = null;
		}

		return this.field_6276;
	}

	protected void playHurtSound(DamageSource damageSource) {
		SoundEvent soundEvent = this.getHurtSound(damageSource);
		if (soundEvent != null) {
			this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	private boolean method_6061(DamageSource damageSource) {
		Entity entity = damageSource.getSource();
		boolean bl = false;
		if (entity instanceof ProjectileEntity) {
			ProjectileEntity projectileEntity = (ProjectileEntity)entity;
			if (projectileEntity.getPierceLevel() > 0) {
				bl = true;
			}
		}

		if (!damageSource.bypassesArmor() && this.method_6039() && !bl) {
			Vec3d vec3d = damageSource.method_5510();
			if (vec3d != null) {
				Vec3d vec3d2 = this.getRotationVec(1.0F);
				Vec3d vec3d3 = vec3d.reverseSubtract(new Vec3d(this.x, this.y, this.z)).normalize();
				vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
				if (vec3d3.dotProduct(vec3d2) * (float) Math.PI < 0.87266463F) {
					return true;
				}
			}
		}

		return false;
	}

	@Environment(EnvType.CLIENT)
	private void playEquipmentBreakEffects(ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			if (!this.isSilent()) {
				this.world.playSound(this.x, this.y, this.z, SoundEvents.field_15075, this.getSoundCategory(), 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F, false);
			}

			this.spawnItemParticles(itemStack, 5);
		}
	}

	public void onDeath(DamageSource damageSource) {
		if (!this.dead) {
			Entity entity = damageSource.getAttacker();
			LivingEntity livingEntity = this.method_6124();
			if (this.field_6232 >= 0 && livingEntity != null) {
				livingEntity.updateKilledAdvancementCriterion(this, this.field_6232, damageSource);
			}

			if (entity != null) {
				entity.onKilledOther(this);
			}

			if (this.isSleeping()) {
				this.wakeUp();
			}

			this.dead = true;
			this.getDamageTracker().update();
			if (!this.world.isClient) {
				this.drop(damageSource);
				boolean bl = false;
				if (livingEntity instanceof WitherEntity) {
					if (this.world.getGameRules().getBoolean(GameRules.field_19388)) {
						BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
						BlockState blockState = Blocks.field_10606.getDefaultState();
						if (this.world.getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.world, blockPos)) {
							this.world.setBlockState(blockPos, blockState, 3);
							bl = true;
						}
					}

					if (!bl) {
						ItemEntity itemEntity = new ItemEntity(this.world, this.x, this.y, this.z, new ItemStack(Items.WITHER_ROSE));
						this.world.spawnEntity(itemEntity);
					}
				}
			}

			this.world.sendEntityStatus(this, (byte)3);
			this.setPose(EntityPose.field_18082);
		}
	}

	protected void drop(DamageSource damageSource) {
		Entity entity = damageSource.getAttacker();
		int i;
		if (entity instanceof PlayerEntity) {
			i = EnchantmentHelper.getLooting((LivingEntity)entity);
		} else {
			i = 0;
		}

		boolean bl = this.playerHitTimer > 0;
		if (this.canDropLootAndXp() && this.world.getGameRules().getBoolean(GameRules.field_19391)) {
			this.dropLoot(damageSource, bl);
			this.dropEquipment(damageSource, i, bl);
		}

		this.dropInventory();
	}

	protected void dropInventory() {
	}

	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
	}

	public Identifier getLootTable() {
		return this.getType().getLootTableId();
	}

	protected void dropLoot(DamageSource damageSource, boolean bl) {
		Identifier identifier = this.getLootTable();
		LootSupplier lootSupplier = this.world.getServer().getLootManager().getSupplier(identifier);
		LootContext.Builder builder = this.getLootContextBuilder(bl, damageSource);
		lootSupplier.dropLimited(builder.build(LootContextTypes.field_1173), this::dropStack);
	}

	protected LootContext.Builder getLootContextBuilder(boolean bl, DamageSource damageSource) {
		LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world)
			.setRandom(this.random)
			.put(LootContextParameters.field_1226, this)
			.put(LootContextParameters.field_1232, new BlockPos(this))
			.put(LootContextParameters.field_1231, damageSource)
			.putNullable(LootContextParameters.field_1230, damageSource.getAttacker())
			.putNullable(LootContextParameters.field_1227, damageSource.getSource());
		if (bl && this.attackingPlayer != null) {
			builder = builder.put(LootContextParameters.field_1233, this.attackingPlayer).setLuck(this.attackingPlayer.getLuck());
		}

		return builder;
	}

	public void takeKnockback(Entity entity, float f, double d, double e) {
		f = (float)((double)f * (1.0 - this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).getValue()));
		if (!(f <= 0.0F)) {
			this.velocityDirty = true;
			Vec3d vec3d = this.getVelocity();
			Vec3d vec3d2 = new Vec3d(d, 0.0, e).normalize().multiply((double)f);
			this.setVelocity(vec3d.x / 2.0 - vec3d2.x, this.onGround ? Math.min(0.4, vec3d.y / 2.0 + (double)f) : vec3d.y, vec3d.z / 2.0 - vec3d2.z);
		}
	}

	@Nullable
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14940;
	}

	@Nullable
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14732;
	}

	protected SoundEvent getFallSound(int i) {
		return i > 4 ? SoundEvents.field_14928 : SoundEvents.field_15018;
	}

	protected SoundEvent getDrinkSound(ItemStack itemStack) {
		return SoundEvents.field_14643;
	}

	public SoundEvent getEatSound(ItemStack itemStack) {
		return SoundEvents.field_14544;
	}

	public boolean isClimbing() {
		if (this.isSpectator()) {
			return false;
		} else {
			BlockState blockState = this.method_16212();
			Block block = blockState.getBlock();
			return block != Blocks.field_9983 && block != Blocks.field_10597 && block != Blocks.field_16492
				? block instanceof TrapdoorBlock && this.method_6077(new BlockPos(this), blockState)
				: true;
		}
	}

	public BlockState method_16212() {
		return this.world.getBlockState(new BlockPos(this));
	}

	private boolean method_6077(BlockPos blockPos, BlockState blockState) {
		if ((Boolean)blockState.get(TrapdoorBlock.OPEN)) {
			BlockState blockState2 = this.world.getBlockState(blockPos.down());
			if (blockState2.getBlock() == Blocks.field_9983 && blockState2.get(LadderBlock.FACING) == blockState.get(TrapdoorBlock.FACING)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isAlive() {
		return !this.removed && this.getHealth() > 0.0F;
	}

	@Override
	public void handleFallDamage(float f, float g) {
		super.handleFallDamage(f, g);
		StatusEffectInstance statusEffectInstance = this.getStatusEffect(StatusEffects.field_5913);
		float h = statusEffectInstance == null ? 0.0F : (float)(statusEffectInstance.getAmplifier() + 1);
		int i = MathHelper.ceil((f - 3.0F - h) * g);
		if (i > 0) {
			this.playSound(this.getFallSound(i), 1.0F, 1.0F);
			this.damage(DamageSource.FALL, (float)i);
			int j = MathHelper.floor(this.x);
			int k = MathHelper.floor(this.y - 0.2F);
			int l = MathHelper.floor(this.z);
			BlockState blockState = this.world.getBlockState(new BlockPos(j, k, l));
			if (!blockState.isAir()) {
				BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
				this.playSound(blockSoundGroup.getFallSound(), blockSoundGroup.getVolume() * 0.5F, blockSoundGroup.getPitch() * 0.75F);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void animateDamage() {
		this.field_6254 = 10;
		this.hurtTime = this.field_6254;
		this.field_6271 = 0.0F;
	}

	public int getArmor() {
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.ARMOR);
		return MathHelper.floor(entityAttributeInstance.getValue());
	}

	protected void damageArmor(float f) {
	}

	protected void damageShield(float f) {
	}

	protected float applyArmorToDamage(DamageSource damageSource, float f) {
		if (!damageSource.bypassesArmor()) {
			this.damageArmor(f);
			f = DamageUtil.getDamageLeft(f, (float)this.getArmor(), (float)this.getAttributeInstance(EntityAttributes.ARMOR_TOUGHNESS).getValue());
		}

		return f;
	}

	protected float applyEnchantmentsToDamage(DamageSource damageSource, float f) {
		if (damageSource.isUnblockable()) {
			return f;
		} else {
			if (this.hasStatusEffect(StatusEffects.field_5907) && damageSource != DamageSource.OUT_OF_WORLD) {
				int i = (this.getStatusEffect(StatusEffects.field_5907).getAmplifier() + 1) * 5;
				int j = 25 - i;
				float g = f * (float)j;
				float h = f;
				f = Math.max(g / 25.0F, 0.0F);
				float k = h - f;
				if (k > 0.0F && k < 3.4028235E37F) {
					if (this instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)this).increaseStat(Stats.field_15425, Math.round(k * 10.0F));
					} else if (damageSource.getAttacker() instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)damageSource.getAttacker()).increaseStat(Stats.field_15397, Math.round(k * 10.0F));
					}
				}
			}

			if (f <= 0.0F) {
				return 0.0F;
			} else {
				int i = EnchantmentHelper.getProtectionAmount(this.getArmorItems(), damageSource);
				if (i > 0) {
					f = DamageUtil.getInflictedDamage(f, (float)i);
				}

				return f;
			}
		}
	}

	protected void applyDamage(DamageSource damageSource, float f) {
		if (!this.isInvulnerableTo(damageSource)) {
			f = this.applyArmorToDamage(damageSource, f);
			f = this.applyEnchantmentsToDamage(damageSource, f);
			float var8 = Math.max(f - this.getAbsorptionAmount(), 0.0F);
			this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - var8));
			float h = f - var8;
			if (h > 0.0F && h < 3.4028235E37F && damageSource.getAttacker() instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)damageSource.getAttacker()).increaseStat(Stats.field_15408, Math.round(h * 10.0F));
			}

			if (var8 != 0.0F) {
				float i = this.getHealth();
				this.setHealth(i - var8);
				this.getDamageTracker().onDamage(damageSource, i, var8);
				this.setAbsorptionAmount(this.getAbsorptionAmount() - var8);
			}
		}
	}

	public DamageTracker getDamageTracker() {
		return this.damageTracker;
	}

	@Nullable
	public LivingEntity method_6124() {
		if (this.damageTracker.getBiggestAttacker() != null) {
			return this.damageTracker.getBiggestAttacker();
		} else if (this.attackingPlayer != null) {
			return this.attackingPlayer;
		} else {
			return this.attacker != null ? this.attacker : null;
		}
	}

	public final float getHealthMaximum() {
		return (float)this.getAttributeInstance(EntityAttributes.MAX_HEALTH).getValue();
	}

	public final int getStuckArrows() {
		return this.dataTracker.get(STUCK_ARROWS);
	}

	public final void setStuckArrows(int i) {
		this.dataTracker.set(STUCK_ARROWS, i);
	}

	private int getHandSwingDuration() {
		if (StatusEffectUtil.hasHaste(this)) {
			return 6 - (1 + StatusEffectUtil.getHasteAmplifier(this));
		} else {
			return this.hasStatusEffect(StatusEffects.field_5901) ? 6 + (1 + this.getStatusEffect(StatusEffects.field_5901).getAmplifier()) * 2 : 6;
		}
	}

	public void swingHand(Hand hand) {
		if (!this.isHandSwinging || this.handSwingTicks >= this.getHandSwingDuration() / 2 || this.handSwingTicks < 0) {
			this.handSwingTicks = -1;
			this.isHandSwinging = true;
			this.preferredHand = hand;
			if (this.world instanceof ServerWorld) {
				((ServerWorld)this.world).method_14178().sendToOtherNearbyPlayers(this, new EntityAnimationS2CPacket(this, hand == Hand.field_5808 ? 0 : 3));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		switch (b) {
			case 2:
			case 33:
			case 36:
			case 37:
			case 44:
				boolean bl = b == 33;
				boolean bl2 = b == 36;
				boolean bl3 = b == 37;
				boolean bl4 = b == 44;
				this.limbDistance = 1.5F;
				this.timeUntilRegen = 10;
				this.field_6254 = 10;
				this.hurtTime = this.field_6254;
				this.field_6271 = 0.0F;
				if (bl) {
					this.playSound(SoundEvents.field_14663, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				}

				DamageSource damageSource;
				if (bl3) {
					damageSource = DamageSource.ON_FIRE;
				} else if (bl2) {
					damageSource = DamageSource.DROWN;
				} else if (bl4) {
					damageSource = DamageSource.SWEET_BERRY_BUSH;
				} else {
					damageSource = DamageSource.GENERIC;
				}

				SoundEvent soundEvent = this.getHurtSound(damageSource);
				if (soundEvent != null) {
					this.playSound(soundEvent, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				}

				this.damage(DamageSource.GENERIC, 0.0F);
				break;
			case 3:
				SoundEvent soundEvent2 = this.getDeathSound();
				if (soundEvent2 != null) {
					this.playSound(soundEvent2, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				}

				this.setHealth(0.0F);
				this.onDeath(DamageSource.GENERIC);
				break;
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
			case 27:
			case 28:
			case 31:
			case 32:
			case 34:
			case 35:
			case 38:
			case 39:
			case 40:
			case 41:
			case 42:
			case 43:
			case 45:
			default:
				super.handleStatus(b);
				break;
			case 29:
				this.playSound(SoundEvents.field_15150, 1.0F, 0.8F + this.world.random.nextFloat() * 0.4F);
				break;
			case 30:
				this.playSound(SoundEvents.field_15239, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
				break;
			case 46:
				int i = 128;

				for (int j = 0; j < 128; j++) {
					double d = (double)j / 127.0;
					float f = (this.random.nextFloat() - 0.5F) * 0.2F;
					float g = (this.random.nextFloat() - 0.5F) * 0.2F;
					float h = (this.random.nextFloat() - 0.5F) * 0.2F;
					double e = MathHelper.lerp(d, this.prevX, this.x) + (this.random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
					double k = MathHelper.lerp(d, this.prevY, this.y) + this.random.nextDouble() * (double)this.getHeight();
					double l = MathHelper.lerp(d, this.prevZ, this.z) + (this.random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
					this.world.addParticle(ParticleTypes.field_11214, e, k, l, (double)f, (double)g, (double)h);
				}
				break;
			case 47:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.field_6173));
				break;
			case 48:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.field_6171));
				break;
			case 49:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.field_6169));
				break;
			case 50:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.field_6174));
				break;
			case 51:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.field_6172));
				break;
			case 52:
				this.playEquipmentBreakEffects(this.getEquippedStack(EquipmentSlot.field_6166));
		}
	}

	@Override
	protected void destroy() {
		this.damage(DamageSource.OUT_OF_WORLD, 4.0F);
	}

	protected void tickHandSwing() {
		int i = this.getHandSwingDuration();
		if (this.isHandSwinging) {
			this.handSwingTicks++;
			if (this.handSwingTicks >= i) {
				this.handSwingTicks = 0;
				this.isHandSwinging = false;
			}
		} else {
			this.handSwingTicks = 0;
		}

		this.handSwingProgress = (float)this.handSwingTicks / (float)i;
	}

	public EntityAttributeInstance getAttributeInstance(EntityAttribute entityAttribute) {
		return this.getAttributeContainer().get(entityAttribute);
	}

	public AbstractEntityAttributeContainer getAttributeContainer() {
		if (this.attributeContainer == null) {
			this.attributeContainer = new EntityAttributeContainer();
		}

		return this.attributeContainer;
	}

	public EntityGroup getGroup() {
		return EntityGroup.DEFAULT;
	}

	public ItemStack getMainHandStack() {
		return this.getEquippedStack(EquipmentSlot.field_6173);
	}

	public ItemStack getOffHandStack() {
		return this.getEquippedStack(EquipmentSlot.field_6171);
	}

	public ItemStack getStackInHand(Hand hand) {
		if (hand == Hand.field_5808) {
			return this.getEquippedStack(EquipmentSlot.field_6173);
		} else if (hand == Hand.field_5810) {
			return this.getEquippedStack(EquipmentSlot.field_6171);
		} else {
			throw new IllegalArgumentException("Invalid hand " + hand);
		}
	}

	public void setStackInHand(Hand hand, ItemStack itemStack) {
		if (hand == Hand.field_5808) {
			this.setEquippedStack(EquipmentSlot.field_6173, itemStack);
		} else {
			if (hand != Hand.field_5810) {
				throw new IllegalArgumentException("Invalid hand " + hand);
			}

			this.setEquippedStack(EquipmentSlot.field_6171, itemStack);
		}
	}

	public boolean isEquippedStackValid(EquipmentSlot equipmentSlot) {
		return !this.getEquippedStack(equipmentSlot).isEmpty();
	}

	@Override
	public abstract Iterable<ItemStack> getArmorItems();

	public abstract ItemStack getEquippedStack(EquipmentSlot equipmentSlot);

	@Override
	public abstract void setEquippedStack(EquipmentSlot equipmentSlot, ItemStack itemStack);

	public float method_18396() {
		Iterable<ItemStack> iterable = this.getArmorItems();
		int i = 0;
		int j = 0;

		for (ItemStack itemStack : iterable) {
			if (!itemStack.isEmpty()) {
				j++;
			}

			i++;
		}

		return i > 0 ? (float)j / (float)i : 0.0F;
	}

	@Override
	public void setSprinting(boolean bl) {
		super.setSprinting(bl);
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
		if (entityAttributeInstance.getModifier(ATTR_SPRINTING_SPEED_BOOST_ID) != null) {
			entityAttributeInstance.removeModifier(ATTR_SPRINTING_SPEED_BOOST);
		}

		if (bl) {
			entityAttributeInstance.addModifier(ATTR_SPRINTING_SPEED_BOOST);
		}
	}

	protected float getSoundVolume() {
		return 1.0F;
	}

	protected float getSoundPitch() {
		return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
	}

	protected boolean cannotMove() {
		return this.getHealth() <= 0.0F;
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (!this.isSleeping()) {
			super.pushAwayFrom(entity);
		}
	}

	public void method_6038(Entity entity) {
		if (!(entity instanceof BoatEntity) && !(entity instanceof HorseBaseEntity)) {
			double k = entity.x;
			double l = entity.getBoundingBox().minY + (double)entity.getHeight();
			double e = entity.z;
			Direction direction = entity.getMovementDirection();
			if (direction != null) {
				Direction direction2 = direction.rotateYClockwise();
				int[][] is = new int[][]{{0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0}, {0, 1}};
				double m = Math.floor(this.x) + 0.5;
				double n = Math.floor(this.z) + 0.5;
				double o = this.getBoundingBox().maxX - this.getBoundingBox().minX;
				double p = this.getBoundingBox().maxZ - this.getBoundingBox().minZ;
				Box box = new Box(
					m - o / 2.0, entity.getBoundingBox().minY, n - p / 2.0, m + o / 2.0, Math.floor(entity.getBoundingBox().minY) + (double)this.getHeight(), n + p / 2.0
				);

				for (int[] js : is) {
					double q = (double)(direction.getOffsetX() * js[0] + direction2.getOffsetX() * js[1]);
					double r = (double)(direction.getOffsetZ() * js[0] + direction2.getOffsetZ() * js[1]);
					double s = m + q;
					double t = n + r;
					Box box2 = box.offset(q, 0.0, r);
					if (this.world.doesNotCollide(this, box2)) {
						BlockPos blockPos = new BlockPos(s, this.y, t);
						if (this.world.getBlockState(blockPos).hasSolidTopSurface(this.world, blockPos, this)) {
							this.requestTeleport(s, this.y + 1.0, t);
							return;
						}

						BlockPos blockPos2 = new BlockPos(s, this.y - 1.0, t);
						if (this.world.getBlockState(blockPos2).hasSolidTopSurface(this.world, blockPos2, this)
							|| this.world.getFluidState(blockPos2).matches(FluidTags.field_15517)) {
							k = s;
							l = this.y + 1.0;
							e = t;
						}
					} else {
						BlockPos blockPosx = new BlockPos(s, this.y + 1.0, t);
						if (this.world.doesNotCollide(this, box2.offset(0.0, 1.0, 0.0)) && this.world.getBlockState(blockPosx).hasSolidTopSurface(this.world, blockPosx, this)) {
							k = s;
							l = this.y + 2.0;
							e = t;
						}
					}
				}
			}

			this.requestTeleport(k, l, e);
		} else {
			double d = (double)(this.getWidth() / 2.0F + entity.getWidth() / 2.0F) + 0.4;
			float f;
			if (entity instanceof BoatEntity) {
				f = 0.0F;
			} else {
				f = (float) (Math.PI / 2) * (float)(this.getMainArm() == Arm.field_6183 ? -1 : 1);
			}

			float g = -MathHelper.sin(-this.yaw * (float) (Math.PI / 180.0) - (float) Math.PI + f);
			float h = -MathHelper.cos(-this.yaw * (float) (Math.PI / 180.0) - (float) Math.PI + f);
			double e = Math.abs(g) > Math.abs(h) ? d / (double)Math.abs(g) : d / (double)Math.abs(h);
			double i = this.x + (double)g * e;
			double j = this.z + (double)h * e;
			this.setPosition(i, entity.y + (double)entity.getHeight() + 0.001, j);
			if (!this.world.doesNotCollide(this, this.getBoundingBox().union(entity.getBoundingBox()))) {
				this.setPosition(i, entity.y + (double)entity.getHeight() + 1.001, j);
				if (!this.world.doesNotCollide(this, this.getBoundingBox().union(entity.getBoundingBox()))) {
					this.setPosition(entity.x, entity.y + (double)this.getHeight() + 0.001, entity.z);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderName() {
		return this.isCustomNameVisible();
	}

	protected float getJumpVelocity() {
		return 0.42F;
	}

	protected void jump() {
		float f;
		if (this.hasStatusEffect(StatusEffects.field_5913)) {
			f = this.getJumpVelocity() + 0.1F * (float)(this.getStatusEffect(StatusEffects.field_5913).getAmplifier() + 1);
		} else {
			f = this.getJumpVelocity();
		}

		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x, (double)f, vec3d.z);
		if (this.isSprinting()) {
			float g = this.yaw * (float) (Math.PI / 180.0);
			this.setVelocity(this.getVelocity().add((double)(-MathHelper.sin(g) * 0.2F), 0.0, (double)(MathHelper.cos(g) * 0.2F)));
		}

		this.velocityDirty = true;
	}

	@Environment(EnvType.CLIENT)
	protected void method_6093() {
		this.setVelocity(this.getVelocity().add(0.0, -0.04F, 0.0));
	}

	protected void swimUpward(Tag<Fluid> tag) {
		this.setVelocity(this.getVelocity().add(0.0, 0.04F, 0.0));
	}

	protected float getBaseMovementSpeedMultiplier() {
		return 0.8F;
	}

	public void travel(Vec3d vec3d) {
		if (this.canMoveVoluntarily() || this.isLogicalSideForUpdatingMovement()) {
			double d = 0.08;
			boolean bl = this.getVelocity().y <= 0.0;
			if (bl && this.hasStatusEffect(StatusEffects.field_5906)) {
				d = 0.01;
				this.fallDistance = 0.0F;
			}

			if (!this.isInsideWater() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying) {
				if (!this.isInLava() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying) {
					if (this.isFallFlying()) {
						Vec3d vec3d5 = this.getVelocity();
						if (vec3d5.y > -0.5) {
							this.fallDistance = 1.0F;
						}

						Vec3d vec3d6 = this.getRotationVector();
						float f = this.pitch * (float) (Math.PI / 180.0);
						double j = Math.sqrt(vec3d6.x * vec3d6.x + vec3d6.z * vec3d6.z);
						double k = Math.sqrt(squaredHorizontalLength(vec3d5));
						double i = vec3d6.length();
						float l = MathHelper.cos(f);
						l = (float)((double)l * (double)l * Math.min(1.0, i / 0.4));
						vec3d5 = this.getVelocity().add(0.0, d * (-1.0 + (double)l * 0.75), 0.0);
						if (vec3d5.y < 0.0 && j > 0.0) {
							double m = vec3d5.y * -0.1 * (double)l;
							vec3d5 = vec3d5.add(vec3d6.x * m / j, m, vec3d6.z * m / j);
						}

						if (f < 0.0F && j > 0.0) {
							double m = k * (double)(-MathHelper.sin(f)) * 0.04;
							vec3d5 = vec3d5.add(-vec3d6.x * m / j, m * 3.2, -vec3d6.z * m / j);
						}

						if (j > 0.0) {
							vec3d5 = vec3d5.add((vec3d6.x / j * k - vec3d5.x) * 0.1, 0.0, (vec3d6.z / j * k - vec3d5.z) * 0.1);
						}

						this.setVelocity(vec3d5.multiply(0.99F, 0.98F, 0.99F));
						this.move(MovementType.field_6308, this.getVelocity());
						if (this.horizontalCollision && !this.world.isClient) {
							double m = Math.sqrt(squaredHorizontalLength(this.getVelocity()));
							double n = k - m;
							float o = (float)(n * 10.0 - 3.0);
							if (o > 0.0F) {
								this.playSound(this.getFallSound((int)o), 1.0F, 1.0F);
								this.damage(DamageSource.FLY_INTO_WALL, o);
							}
						}

						if (this.onGround && !this.world.isClient) {
							this.setFlag(7, false);
						}
					} else {
						BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z);
						float p = this.world.getBlockState(blockPos).getBlock().getSlipperiness();
						float fx = this.onGround ? p * 0.91F : 0.91F;
						this.updateVelocity(this.method_18802(p), vec3d);
						this.setVelocity(this.method_18801(this.getVelocity()));
						this.move(MovementType.field_6308, this.getVelocity());
						Vec3d vec3d7 = this.getVelocity();
						if ((this.horizontalCollision || this.jumping) && this.isClimbing()) {
							vec3d7 = new Vec3d(vec3d7.x, 0.2, vec3d7.z);
						}

						double q = vec3d7.y;
						if (this.hasStatusEffect(StatusEffects.field_5902)) {
							q += (0.05 * (double)(this.getStatusEffect(StatusEffects.field_5902).getAmplifier() + 1) - vec3d7.y) * 0.2;
							this.fallDistance = 0.0F;
						} else if (this.world.isClient && !this.world.isBlockLoaded(blockPos)) {
							if (this.y > 0.0) {
								q = -0.1;
							} else {
								q = 0.0;
							}
						} else if (!this.hasNoGravity()) {
							q -= d;
						}

						this.setVelocity(vec3d7.x * (double)fx, q * 0.98F, vec3d7.z * (double)fx);
					}
				} else {
					double e = this.y;
					this.updateVelocity(0.02F, vec3d);
					this.move(MovementType.field_6308, this.getVelocity());
					this.setVelocity(this.getVelocity().multiply(0.5));
					if (!this.hasNoGravity()) {
						this.setVelocity(this.getVelocity().add(0.0, -d / 4.0, 0.0));
					}

					Vec3d vec3d4 = this.getVelocity();
					if (this.horizontalCollision && this.doesNotCollide(vec3d4.x, vec3d4.y + 0.6F - this.y + e, vec3d4.z)) {
						this.setVelocity(vec3d4.x, 0.3F, vec3d4.z);
					}
				}
			} else {
				double ex = this.y;
				float fxx = this.isSprinting() ? 0.9F : this.getBaseMovementSpeedMultiplier();
				float g = 0.02F;
				float h = (float)EnchantmentHelper.getDepthStrider(this);
				if (h > 3.0F) {
					h = 3.0F;
				}

				if (!this.onGround) {
					h *= 0.5F;
				}

				if (h > 0.0F) {
					fxx += (0.54600006F - fxx) * h / 3.0F;
					g += (this.getMovementSpeed() - g) * h / 3.0F;
				}

				if (this.hasStatusEffect(StatusEffects.field_5900)) {
					fxx = 0.96F;
				}

				this.updateVelocity(g, vec3d);
				this.move(MovementType.field_6308, this.getVelocity());
				Vec3d vec3d2 = this.getVelocity();
				if (this.horizontalCollision && this.isClimbing()) {
					vec3d2 = new Vec3d(vec3d2.x, 0.2, vec3d2.z);
				}

				this.setVelocity(vec3d2.multiply((double)fxx, 0.8F, (double)fxx));
				if (!this.hasNoGravity() && !this.isSprinting()) {
					Vec3d vec3d3 = this.getVelocity();
					double ix;
					if (bl && Math.abs(vec3d3.y - 0.005) >= 0.003 && Math.abs(vec3d3.y - d / 16.0) < 0.003) {
						ix = -0.003;
					} else {
						ix = vec3d3.y - d / 16.0;
					}

					this.setVelocity(vec3d3.x, ix, vec3d3.z);
				}

				Vec3d vec3d3 = this.getVelocity();
				if (this.horizontalCollision && this.doesNotCollide(vec3d3.x, vec3d3.y + 0.6F - this.y + ex, vec3d3.z)) {
					this.setVelocity(vec3d3.x, 0.3F, vec3d3.z);
				}
			}
		}

		this.lastLimbDistance = this.limbDistance;
		double dx = this.x - this.prevX;
		double r = this.z - this.prevZ;
		double s = this instanceof Bird ? this.y - this.prevY : 0.0;
		float gx = MathHelper.sqrt(dx * dx + s * s + r * r) * 4.0F;
		if (gx > 1.0F) {
			gx = 1.0F;
		}

		this.limbDistance = this.limbDistance + (gx - this.limbDistance) * 0.4F;
		this.limbAngle = this.limbAngle + this.limbDistance;
	}

	private Vec3d method_18801(Vec3d vec3d) {
		if (this.isClimbing()) {
			this.fallDistance = 0.0F;
			float f = 0.15F;
			double d = MathHelper.clamp(vec3d.x, -0.15F, 0.15F);
			double e = MathHelper.clamp(vec3d.z, -0.15F, 0.15F);
			double g = Math.max(vec3d.y, -0.15F);
			if (g < 0.0 && this.method_16212().getBlock() != Blocks.field_16492 && this.isSneaking() && this instanceof PlayerEntity) {
				g = 0.0;
			}

			vec3d = new Vec3d(d, g, e);
		}

		return vec3d;
	}

	private float method_18802(float f) {
		return this.onGround ? this.getMovementSpeed() * (0.21600002F / (f * f * f)) : this.field_6281;
	}

	public float getMovementSpeed() {
		return this.movementSpeed;
	}

	public void setMovementSpeed(float f) {
		this.movementSpeed = f;
	}

	public boolean tryAttack(Entity entity) {
		this.onAttacking(entity);
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		this.method_6076();
		this.method_6072();
		if (!this.world.isClient) {
			int i = this.getStuckArrows();
			if (i > 0) {
				if (this.stuckArrowTimer <= 0) {
					this.stuckArrowTimer = 20 * (30 - i);
				}

				this.stuckArrowTimer--;
				if (this.stuckArrowTimer <= 0) {
					this.setStuckArrows(i - 1);
				}
			}

			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				ItemStack itemStack;
				switch (equipmentSlot.getType()) {
					case field_6177:
						itemStack = this.equippedHand.get(equipmentSlot.getEntitySlotId());
						break;
					case field_6178:
						itemStack = this.equippedArmor.get(equipmentSlot.getEntitySlotId());
						break;
					default:
						continue;
				}

				ItemStack itemStack2 = this.getEquippedStack(equipmentSlot);
				if (!ItemStack.areEqualIgnoreDamage(itemStack2, itemStack)) {
					((ServerWorld)this.world).method_14178().sendToOtherNearbyPlayers(this, new EntityEquipmentUpdateS2CPacket(this.getEntityId(), equipmentSlot, itemStack2));
					if (!itemStack.isEmpty()) {
						this.getAttributeContainer().removeAll(itemStack.getAttributeModifiers(equipmentSlot));
					}

					if (!itemStack2.isEmpty()) {
						this.getAttributeContainer().replaceAll(itemStack2.getAttributeModifiers(equipmentSlot));
					}

					switch (equipmentSlot.getType()) {
						case field_6177:
							this.equippedHand.set(equipmentSlot.getEntitySlotId(), itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2.copy());
							break;
						case field_6178:
							this.equippedArmor.set(equipmentSlot.getEntitySlotId(), itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2.copy());
					}
				}
			}

			if (this.age % 20 == 0) {
				this.getDamageTracker().update();
			}

			if (!this.glowing) {
				boolean bl = this.hasStatusEffect(StatusEffects.field_5912);
				if (this.getFlag(6) != bl) {
					this.setFlag(6, bl);
				}
			}

			if (this.isSleeping() && !this.isSleepingInBed()) {
				this.wakeUp();
			}
		}

		this.tickMovement();
		double d = this.x - this.prevX;
		double e = this.z - this.prevZ;
		float f = (float)(d * d + e * e);
		float g = this.field_6283;
		float h = 0.0F;
		this.field_6217 = this.field_6233;
		float j = 0.0F;
		if (f > 0.0025000002F) {
			j = 1.0F;
			h = (float)Math.sqrt((double)f) * 3.0F;
			float k = (float)MathHelper.atan2(e, d) * (180.0F / (float)Math.PI) - 90.0F;
			float l = MathHelper.abs(MathHelper.wrapDegrees(this.yaw) - k);
			if (95.0F < l && l < 265.0F) {
				g = k - 180.0F;
			} else {
				g = k;
			}
		}

		if (this.handSwingProgress > 0.0F) {
			g = this.yaw;
		}

		if (!this.onGround) {
			j = 0.0F;
		}

		this.field_6233 = this.field_6233 + (j - this.field_6233) * 0.3F;
		this.world.getProfiler().push("headTurn");
		h = this.method_6031(g, h);
		this.world.getProfiler().pop();
		this.world.getProfiler().push("rangeChecks");

		while (this.yaw - this.prevYaw < -180.0F) {
			this.prevYaw -= 360.0F;
		}

		while (this.yaw - this.prevYaw >= 180.0F) {
			this.prevYaw += 360.0F;
		}

		while (this.field_6283 - this.field_6220 < -180.0F) {
			this.field_6220 -= 360.0F;
		}

		while (this.field_6283 - this.field_6220 >= 180.0F) {
			this.field_6220 += 360.0F;
		}

		while (this.pitch - this.prevPitch < -180.0F) {
			this.prevPitch -= 360.0F;
		}

		while (this.pitch - this.prevPitch >= 180.0F) {
			this.prevPitch += 360.0F;
		}

		while (this.headYaw - this.prevHeadYaw < -180.0F) {
			this.prevHeadYaw -= 360.0F;
		}

		while (this.headYaw - this.prevHeadYaw >= 180.0F) {
			this.prevHeadYaw += 360.0F;
		}

		this.world.getProfiler().pop();
		this.field_6255 += h;
		if (this.isFallFlying()) {
			this.field_6239++;
		} else {
			this.field_6239 = 0;
		}

		if (this.isSleeping()) {
			this.pitch = 0.0F;
		}
	}

	protected float method_6031(float f, float g) {
		float h = MathHelper.wrapDegrees(f - this.field_6283);
		this.field_6283 += h * 0.3F;
		float i = MathHelper.wrapDegrees(this.yaw - this.field_6283);
		boolean bl = i < -90.0F || i >= 90.0F;
		if (i < -75.0F) {
			i = -75.0F;
		}

		if (i >= 75.0F) {
			i = 75.0F;
		}

		this.field_6283 = this.yaw - i;
		if (i * i > 2500.0F) {
			this.field_6283 += i * 0.2F;
		}

		if (bl) {
			g *= -1.0F;
		}

		return g;
	}

	public void tickMovement() {
		if (this.field_6228 > 0) {
			this.field_6228--;
		}

		if (this.field_6210 > 0 && !this.isLogicalSideForUpdatingMovement()) {
			double d = this.x + (this.field_6224 - this.x) / (double)this.field_6210;
			double e = this.y + (this.field_6245 - this.y) / (double)this.field_6210;
			double f = this.z + (this.field_6263 - this.z) / (double)this.field_6210;
			double g = MathHelper.wrapDegrees(this.field_6284 - (double)this.yaw);
			this.yaw = (float)((double)this.yaw + g / (double)this.field_6210);
			this.pitch = (float)((double)this.pitch + (this.field_6221 - (double)this.pitch) / (double)this.field_6210);
			this.field_6210--;
			this.setPosition(d, e, f);
			this.setRotation(this.yaw, this.pitch);
		} else if (!this.canMoveVoluntarily()) {
			this.setVelocity(this.getVelocity().multiply(0.98));
		}

		if (this.field_6265 > 0) {
			this.headYaw = (float)((double)this.headYaw + MathHelper.wrapDegrees(this.field_6242 - (double)this.headYaw) / (double)this.field_6265);
			this.field_6265--;
		}

		Vec3d vec3d = this.getVelocity();
		double h = vec3d.x;
		double i = vec3d.y;
		double j = vec3d.z;
		if (Math.abs(vec3d.x) < 0.003) {
			h = 0.0;
		}

		if (Math.abs(vec3d.y) < 0.003) {
			i = 0.0;
		}

		if (Math.abs(vec3d.z) < 0.003) {
			j = 0.0;
		}

		this.setVelocity(h, i, j);
		this.world.getProfiler().push("ai");
		if (this.cannotMove()) {
			this.jumping = false;
			this.sidewaysSpeed = 0.0F;
			this.forwardSpeed = 0.0F;
			this.field_6267 = 0.0F;
		} else if (this.canMoveVoluntarily()) {
			this.world.getProfiler().push("newAi");
			this.tickNewAi();
			this.world.getProfiler().pop();
		}

		this.world.getProfiler().pop();
		this.world.getProfiler().push("jump");
		if (this.jumping) {
			if (!(this.waterHeight > 0.0) || this.onGround && !(this.waterHeight > 0.4)) {
				if (this.isInLava()) {
					this.swimUpward(FluidTags.field_15518);
				} else if ((this.onGround || this.waterHeight > 0.0 && this.waterHeight <= 0.4) && this.field_6228 == 0) {
					this.jump();
					this.field_6228 = 10;
				}
			} else {
				this.swimUpward(FluidTags.field_15517);
			}
		} else {
			this.field_6228 = 0;
		}

		this.world.getProfiler().pop();
		this.world.getProfiler().push("travel");
		this.sidewaysSpeed *= 0.98F;
		this.forwardSpeed *= 0.98F;
		this.field_6267 *= 0.9F;
		this.initAi();
		Box box = this.getBoundingBox();
		this.travel(new Vec3d((double)this.sidewaysSpeed, (double)this.upwardSpeed, (double)this.forwardSpeed));
		this.world.getProfiler().pop();
		this.world.getProfiler().push("push");
		if (this.field_6261 > 0) {
			this.field_6261--;
			this.method_6035(box, this.getBoundingBox());
		}

		this.tickPushing();
		this.world.getProfiler().pop();
	}

	private void initAi() {
		boolean bl = this.getFlag(7);
		if (bl && !this.onGround && !this.hasVehicle()) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.field_6174);
			if (itemStack.getItem() == Items.field_8833 && ElytraItem.isUsable(itemStack)) {
				bl = true;
				if (!this.world.isClient && (this.field_6239 + 1) % 20 == 0) {
					itemStack.damage(1, this, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.field_6174));
				}
			} else {
				bl = false;
			}
		} else {
			bl = false;
		}

		if (!this.world.isClient) {
			this.setFlag(7, bl);
		}
	}

	protected void tickNewAi() {
	}

	protected void tickPushing() {
		List<Entity> list = this.world.getEntities(this, this.getBoundingBox(), EntityPredicates.canBePushedBy(this));
		if (!list.isEmpty()) {
			int i = this.world.getGameRules().getInt(GameRules.field_19405);
			if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
				int j = 0;

				for (int k = 0; k < list.size(); k++) {
					if (!((Entity)list.get(k)).hasVehicle()) {
						j++;
					}
				}

				if (j > i - 1) {
					this.damage(DamageSource.CRAMMING, 6.0F);
				}
			}

			for (int j = 0; j < list.size(); j++) {
				Entity entity = (Entity)list.get(j);
				this.pushAway(entity);
			}
		}
	}

	protected void method_6035(Box box, Box box2) {
		Box box3 = box.union(box2);
		List<Entity> list = this.world.getEntities(this, box3);
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity)list.get(i);
				if (entity instanceof LivingEntity) {
					this.attackLivingEntity((LivingEntity)entity);
					this.field_6261 = 0;
					this.setVelocity(this.getVelocity().multiply(-0.2));
					break;
				}
			}
		} else if (this.horizontalCollision) {
			this.field_6261 = 0;
		}

		if (!this.world.isClient && this.field_6261 <= 0) {
			this.setLivingFlag(4, false);
		}
	}

	protected void pushAway(Entity entity) {
		entity.pushAwayFrom(this);
	}

	protected void attackLivingEntity(LivingEntity livingEntity) {
	}

	public void method_6018(int i) {
		this.field_6261 = i;
		if (!this.world.isClient) {
			this.setLivingFlag(4, true);
		}
	}

	public boolean isUsingRiptide() {
		return (this.dataTracker.get(LIVING_FLAGS) & 4) != 0;
	}

	@Override
	public void stopRiding() {
		Entity entity = this.getVehicle();
		super.stopRiding();
		if (entity != null && entity != this.getVehicle() && !this.world.isClient) {
			this.method_6038(entity);
		}
	}

	@Override
	public void tickRiding() {
		super.tickRiding();
		this.field_6217 = this.field_6233;
		this.field_6233 = 0.0F;
		this.fallDistance = 0.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.field_6224 = d;
		this.field_6245 = e;
		this.field_6263 = f;
		this.field_6284 = (double)g;
		this.field_6221 = (double)h;
		this.field_6210 = i;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void updateTrackedHeadRotation(float f, int i) {
		this.field_6242 = (double)f;
		this.field_6265 = i;
	}

	public void setJumping(boolean bl) {
		this.jumping = bl;
	}

	public void sendPickup(Entity entity, int i) {
		if (!entity.removed && !this.world.isClient && (entity instanceof ItemEntity || entity instanceof ProjectileEntity || entity instanceof ExperienceOrbEntity)) {
			((ServerWorld)this.world).method_14178().sendToOtherNearbyPlayers(entity, new ItemPickupAnimationS2CPacket(entity.getEntityId(), this.getEntityId(), i));
		}
	}

	public boolean canSee(Entity entity) {
		Vec3d vec3d = new Vec3d(this.x, this.y + (double)this.getStandingEyeHeight(), this.z);
		Vec3d vec3d2 = new Vec3d(entity.x, entity.y + (double)entity.getStandingEyeHeight(), entity.z);
		return this.world
				.rayTrace(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.field_1348, this))
				.getType()
			== HitResult.Type.field_1333;
	}

	@Override
	public float getYaw(float f) {
		return f == 1.0F ? this.headYaw : MathHelper.lerp(f, this.prevHeadYaw, this.headYaw);
	}

	@Environment(EnvType.CLIENT)
	public float getHandSwingProgress(float f) {
		float g = this.handSwingProgress - this.lastHandSwingProgress;
		if (g < 0.0F) {
			g++;
		}

		return this.lastHandSwingProgress + g * f;
	}

	public boolean canMoveVoluntarily() {
		return !this.world.isClient;
	}

	@Override
	public boolean collides() {
		return !this.removed;
	}

	@Override
	public boolean isPushable() {
		return this.isAlive() && !this.isClimbing();
	}

	@Override
	protected void scheduleVelocityUpdate() {
		this.velocityModified = this.random.nextDouble() >= this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).getValue();
	}

	@Override
	public float getHeadYaw() {
		return this.headYaw;
	}

	@Override
	public void setHeadYaw(float f) {
		this.headYaw = f;
	}

	@Override
	public void setYaw(float f) {
		this.field_6283 = f;
	}

	public float getAbsorptionAmount() {
		return this.absorptionAmount;
	}

	public void setAbsorptionAmount(float f) {
		if (f < 0.0F) {
			f = 0.0F;
		}

		this.absorptionAmount = f;
	}

	public void method_6000() {
	}

	public void method_6044() {
	}

	protected void method_6008() {
		this.field_6285 = true;
	}

	public abstract Arm getMainArm();

	public boolean isUsingItem() {
		return (this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
	}

	public Hand getActiveHand() {
		return (this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.field_5810 : Hand.field_5808;
	}

	private void method_6076() {
		if (this.isUsingItem()) {
			if (ItemStack.areItemsEqual(this.getStackInHand(this.getActiveHand()), this.activeItemStack)) {
				this.activeItemStack.usageTick(this.world, this, this.getItemUseTimeLeft());
				if (this.getItemUseTimeLeft() <= 25 && this.getItemUseTimeLeft() % 4 == 0) {
					this.spawnConsumptionEffects(this.activeItemStack, 5);
				}

				if (--this.itemUseTimeLeft == 0 && !this.world.isClient && !this.activeItemStack.isUsedOnRelease()) {
					this.method_6040();
				}
			} else {
				this.clearActiveItem();
			}
		}
	}

	private void method_6072() {
		this.field_6264 = this.field_6243;
		if (this.isInSwimmingPose()) {
			this.field_6243 = Math.min(1.0F, this.field_6243 + 0.09F);
		} else {
			this.field_6243 = Math.max(0.0F, this.field_6243 - 0.09F);
		}
	}

	protected void setLivingFlag(int i, boolean bl) {
		int j = this.dataTracker.get(LIVING_FLAGS);
		if (bl) {
			j |= i;
		} else {
			j &= ~i;
		}

		this.dataTracker.set(LIVING_FLAGS, (byte)j);
	}

	public void setCurrentHand(Hand hand) {
		ItemStack itemStack = this.getStackInHand(hand);
		if (!itemStack.isEmpty() && !this.isUsingItem()) {
			this.activeItemStack = itemStack;
			this.itemUseTimeLeft = itemStack.getMaxUseTime();
			if (!this.world.isClient) {
				this.setLivingFlag(1, true);
				this.setLivingFlag(2, hand == Hand.field_5810);
			}
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		super.onTrackedDataSet(trackedData);
		if (SLEEPING_POSITION.equals(trackedData)) {
			if (this.world.isClient) {
				this.getSleepingPosition().ifPresent(this::setPositionInBed);
			}
		} else if (LIVING_FLAGS.equals(trackedData) && this.world.isClient) {
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
	public void lookAt(EntityAnchorArgumentType.EntityAnchor entityAnchor, Vec3d vec3d) {
		super.lookAt(entityAnchor, vec3d);
		this.prevHeadYaw = this.headYaw;
		this.field_6283 = this.headYaw;
		this.field_6220 = this.field_6283;
	}

	protected void spawnConsumptionEffects(ItemStack itemStack, int i) {
		if (!itemStack.isEmpty() && this.isUsingItem()) {
			if (itemStack.getUseAction() == UseAction.field_8946) {
				this.playSound(this.getDrinkSound(itemStack), 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
			}

			if (itemStack.getUseAction() == UseAction.field_8950) {
				this.spawnItemParticles(itemStack, i);
				this.playSound(this.getEatSound(itemStack), 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}
		}
	}

	private void spawnItemParticles(ItemStack itemStack, int i) {
		for (int j = 0; j < i; j++) {
			Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
			vec3d = vec3d.rotateX(-this.pitch * (float) (Math.PI / 180.0));
			vec3d = vec3d.rotateY(-this.yaw * (float) (Math.PI / 180.0));
			double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
			Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
			vec3d2 = vec3d2.rotateX(-this.pitch * (float) (Math.PI / 180.0));
			vec3d2 = vec3d2.rotateY(-this.yaw * (float) (Math.PI / 180.0));
			vec3d2 = vec3d2.add(this.x, this.y + (double)this.getStandingEyeHeight(), this.z);
			this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.field_11218, itemStack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
		}
	}

	protected void method_6040() {
		if (!this.activeItemStack.isEmpty() && this.isUsingItem()) {
			this.spawnConsumptionEffects(this.activeItemStack, 16);
			this.setStackInHand(this.getActiveHand(), this.activeItemStack.finishUsing(this.world, this));
			this.clearActiveItem();
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
			this.activeItemStack.onStoppedUsing(this.world, this, this.getItemUseTimeLeft());
			if (this.activeItemStack.isUsedOnRelease()) {
				this.method_6076();
			}
		}

		this.clearActiveItem();
	}

	public void clearActiveItem() {
		if (!this.world.isClient) {
			this.setLivingFlag(1, false);
		}

		this.activeItemStack = ItemStack.EMPTY;
		this.itemUseTimeLeft = 0;
	}

	public float getAttackCooldownProgress(float f) {
		return 2.0F;
	}

	public boolean method_6039() {
		if (this.isUsingItem() && !this.activeItemStack.isEmpty()) {
			Item item = this.activeItemStack.getItem();
			if (item.getUseAction(this.activeItemStack) == UseAction.field_8949) {
				return true;
			}
		}

		if ((this.isSneaking() || this.hasVehicle()) && !this.isUsingItem() && this.getAttackCooldownProgress(1.0F) > 1.95F) {
			ItemStack itemStack = this.getStackInHand(Hand.field_5810);
			if (!itemStack.isEmpty() && itemStack.getItem().getUseAction(itemStack) == UseAction.field_8949 && !this.method_21747(itemStack)) {
				return true;
			}
		}

		return false;
	}

	public boolean method_21747(ItemStack itemStack) {
		return false;
	}

	public boolean isFallFlying() {
		return this.getFlag(7);
	}

	@Override
	public boolean isInSwimmingPose() {
		return super.isInSwimmingPose() || !this.isFallFlying() && this.getPose() == EntityPose.field_18077;
	}

	@Environment(EnvType.CLIENT)
	public int method_6003() {
		return this.field_6239;
	}

	public boolean teleport(double d, double e, double f, boolean bl) {
		double g = this.x;
		double h = this.y;
		double i = this.z;
		this.x = d;
		this.y = e;
		this.z = f;
		boolean bl2 = false;
		BlockPos blockPos = new BlockPos(this);
		World world = this.world;
		if (world.isBlockLoaded(blockPos)) {
			boolean bl3 = false;

			while (!bl3 && blockPos.getY() > 0) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState = world.getBlockState(blockPos2);
				if (blockState.getMaterial().blocksMovement()) {
					bl3 = true;
				} else {
					this.y--;
					blockPos = blockPos2;
				}
			}

			if (bl3) {
				this.requestTeleport(this.x, this.y, this.z);
				if (world.doesNotCollide(this) && !world.intersectsFluid(this.getBoundingBox())) {
					bl2 = true;
				}
			}
		}

		if (!bl2) {
			this.requestTeleport(g, h, i);
			return false;
		} else {
			if (bl) {
				world.sendEntityStatus(this, (byte)46);
			}

			if (this instanceof MobEntityWithAi) {
				((MobEntityWithAi)this).getNavigation().stop();
			}

			return true;
		}
	}

	public boolean isAffectedBySplashPotions() {
		return true;
	}

	public boolean method_6102() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public void setNearbySongPlaying(BlockPos blockPos, boolean bl) {
	}

	public boolean canPickUp(ItemStack itemStack) {
		return false;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new MobSpawnS2CPacket(this);
	}

	@Override
	public EntityDimensions getDimensions(EntityPose entityPose) {
		return entityPose == EntityPose.field_18078 ? SLEEPING_DIMENSIONS : super.getDimensions(entityPose).scaled(this.getScaleFactor());
	}

	public Optional<BlockPos> getSleepingPosition() {
		return this.dataTracker.get(SLEEPING_POSITION);
	}

	public void setSleepingPosition(BlockPos blockPos) {
		this.dataTracker.set(SLEEPING_POSITION, Optional.of(blockPos));
	}

	public void clearSleepingPosition() {
		this.dataTracker.set(SLEEPING_POSITION, Optional.empty());
	}

	public boolean isSleeping() {
		return this.getSleepingPosition().isPresent();
	}

	public void sleep(BlockPos blockPos) {
		if (this.hasVehicle()) {
			this.stopRiding();
		}

		BlockState blockState = this.world.getBlockState(blockPos);
		if (blockState.getBlock() instanceof BedBlock) {
			this.world.setBlockState(blockPos, blockState.with(BedBlock.OCCUPIED, Boolean.valueOf(true)), 3);
		}

		this.setPose(EntityPose.field_18078);
		this.setPositionInBed(blockPos);
		this.setSleepingPosition(blockPos);
		this.setVelocity(Vec3d.ZERO);
		this.velocityDirty = true;
	}

	private void setPositionInBed(BlockPos blockPos) {
		this.setPosition((double)blockPos.getX() + 0.5, (double)((float)blockPos.getY() + 0.6875F), (double)blockPos.getZ() + 0.5);
	}

	private boolean isSleepingInBed() {
		return (Boolean)this.getSleepingPosition().map(blockPos -> this.world.getBlockState(blockPos).getBlock() instanceof BedBlock).orElse(false);
	}

	public void wakeUp() {
		this.getSleepingPosition().filter(this.world::isBlockLoaded).ifPresent(blockPos -> {
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.getBlock() instanceof BedBlock) {
				this.world.setBlockState(blockPos, blockState.with(BedBlock.OCCUPIED, Boolean.valueOf(false)), 3);
				Vec3d vec3d = (Vec3d)BedBlock.findWakeUpPosition(this.getType(), this.world, blockPos, 0).orElseGet(() -> {
					BlockPos blockPos2 = blockPos.up();
					return new Vec3d((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.1, (double)blockPos2.getZ() + 0.5);
				});
				this.setPosition(vec3d.x, vec3d.y, vec3d.z);
			}
		});
		this.setPose(EntityPose.field_18076);
		this.clearSleepingPosition();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Direction getSleepingDirection() {
		BlockPos blockPos = (BlockPos)this.getSleepingPosition().orElse(null);
		return blockPos != null ? BedBlock.getDirection(this.world, blockPos) : null;
	}

	@Override
	public boolean isInsideWall() {
		return !this.isSleeping() && super.isInsideWall();
	}

	@Override
	protected final float getEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return entityPose == EntityPose.field_18078 ? 0.2F : this.getActiveEyeHeight(entityPose, entityDimensions);
	}

	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return super.getEyeHeight(entityPose, entityDimensions);
	}

	public ItemStack getArrowType(ItemStack itemStack) {
		return ItemStack.EMPTY;
	}

	public ItemStack eatFood(World world, ItemStack itemStack) {
		if (itemStack.isFood()) {
			world.playSound(
				null,
				this.x,
				this.y,
				this.z,
				this.getEatSound(itemStack),
				SoundCategory.field_15254,
				1.0F,
				1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F
			);
			this.applyFoodEffects(itemStack, world, this);
			itemStack.decrement(1);
		}

		return itemStack;
	}

	private void applyFoodEffects(ItemStack itemStack, World world, LivingEntity livingEntity) {
		Item item = itemStack.getItem();
		if (item.isFood()) {
			for (Pair<StatusEffectInstance, Float> pair : item.getFoodComponent().getStatusEffects()) {
				if (!world.isClient && pair.getLeft() != null && world.random.nextFloat() < pair.getRight()) {
					livingEntity.addPotionEffect(new StatusEffectInstance(pair.getLeft()));
				}
			}
		}
	}

	private static byte getEquipmentBreakStatus(EquipmentSlot equipmentSlot) {
		switch (equipmentSlot) {
			case field_6173:
				return 47;
			case field_6171:
				return 48;
			case field_6169:
				return 49;
			case field_6174:
				return 50;
			case field_6166:
				return 52;
			case field_6172:
				return 51;
			default:
				return 47;
		}
	}

	public void sendEquipmentBreakStatus(EquipmentSlot equipmentSlot) {
		this.world.sendEntityStatus(this, getEquipmentBreakStatus(equipmentSlot));
	}

	public void sendToolBreakStatus(Hand hand) {
		this.sendEquipmentBreakStatus(hand == Hand.field_5808 ? EquipmentSlot.field_6173 : EquipmentSlot.field_6171);
	}
}
