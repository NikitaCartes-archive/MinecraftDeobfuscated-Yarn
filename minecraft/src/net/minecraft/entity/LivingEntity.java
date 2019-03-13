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
import net.minecraft.class_1280;
import net.minecraft.class_1432;
import net.minecraft.class_4051;
import net.minecraft.class_4095;
import net.minecraft.class_4140;
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
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.loot.LootSupplier;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameters;
import net.minecraft.world.loot.context.LootContextTypes;
import org.apache.commons.lang3.tuple.Pair;

public abstract class LivingEntity extends Entity {
	private static final UUID ATTR_SPRINTING_SPEED_BOOST_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
	private static final EntityAttributeModifier field_6231 = new EntityAttributeModifier(
			ATTR_SPRINTING_SPEED_BOOST_ID, "Sprinting speed boost", 0.3F, EntityAttributeModifier.Operation.field_6331
		)
		.setSerialize(false);
	protected static final TrackedData<Byte> field_6257 = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Float> field_6247 = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> field_6240 = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> field_6214 = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_6219 = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Optional<BlockPos>> field_18073 = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.OPTIONA_BLOCK_POS);
	protected static final EntitySize SLEEPING_SIZE = EntitySize.constant(0.2F, 0.2F);
	private AbstractEntityAttributeContainer field_6260;
	private final DamageTracker damageTracker = new DamageTracker(this);
	private final Map<StatusEffect, StatusEffectInstance> activePotionEffects = Maps.<StatusEffect, StatusEffectInstance>newHashMap();
	private final DefaultedList<ItemStack> field_6234 = DefaultedList.create(2, ItemStack.EMPTY);
	private final DefaultedList<ItemStack> field_6248 = DefaultedList.create(4, ItemStack.EMPTY);
	public boolean field_6252;
	public Hand preferredHand;
	public int field_6279;
	public int stuckArrowTimer;
	public int hurtTime;
	public int field_6254;
	public float field_6271;
	public int deathCounter;
	public float field_6229;
	public float field_6251;
	protected int field_6273;
	public float field_6211;
	public float field_6225;
	public float field_6249;
	public final int field_6269 = 20;
	public float field_6286;
	public float field_6223;
	public final float field_6244;
	public final float field_6262;
	public float field_6283;
	public float field_6220;
	public float headYaw;
	public float prevHeadYaw;
	public float field_6281 = 0.02F;
	protected PlayerEntity field_6258;
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
	protected boolean field_6282;
	public float movementInputSideways;
	public float movementInputUp;
	public float movementInputForward;
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
	protected ItemStack field_6277 = ItemStack.EMPTY;
	protected int field_6222;
	protected int field_6239;
	private BlockPos field_6268;
	private DamageSource field_6276;
	private long field_6226;
	protected int field_6261;
	private float field_6243;
	private float field_6264;
	protected class_4095<?> field_18321;

	protected LivingEntity(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
		this.initAttributes();
		this.setHealth(this.getHealthMaximum());
		this.field_6033 = true;
		this.field_6262 = (float)((Math.random() + 1.0) * 0.01F);
		this.setPosition(this.x, this.y, this.z);
		this.field_6244 = (float)Math.random() * 12398.0F;
		this.yaw = (float)(Math.random() * (float) (Math.PI * 2));
		this.headYaw = this.yaw;
		this.stepHeight = 0.6F;
		this.field_18321 = this.method_18867(new Dynamic<>(NbtOps.INSTANCE, new CompoundTag()));
	}

	public class_4095<?> method_18868() {
		return this.field_18321;
	}

	protected class_4095<?> method_18867(Dynamic<?> dynamic) {
		return new class_4095<>(ImmutableList.<class_4140<?>>of(), ImmutableList.of(), dynamic);
	}

	@Override
	public void kill() {
		this.damage(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
	}

	public boolean method_5973(EntityType<?> entityType) {
		return true;
	}

	@Override
	protected void initDataTracker() {
		this.field_6011.startTracking(field_6257, (byte)0);
		this.field_6011.startTracking(field_6240, 0);
		this.field_6011.startTracking(field_6214, false);
		this.field_6011.startTracking(field_6219, 0);
		this.field_6011.startTracking(field_6247, 1.0F);
		this.field_6011.startTracking(field_18073, Optional.empty());
	}

	protected void initAttributes() {
		this.method_6127().register(EntityAttributes.MAX_HEALTH);
		this.method_6127().register(EntityAttributes.KNOCKBACK_RESISTANCE);
		this.method_6127().register(EntityAttributes.MOVEMENT_SPEED);
		this.method_6127().register(EntityAttributes.ARMOR);
		this.method_6127().register(EntityAttributes.ARMOR_TOUGHNESS);
	}

	@Override
	protected void method_5623(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
		if (!this.isInsideWater()) {
			this.method_5713();
		}

		if (!this.field_6002.isClient && this.fallDistance > 3.0F && bl) {
			float f = (float)MathHelper.ceil(this.fallDistance - 3.0F);
			if (!blockState.isAir()) {
				double e = Math.min((double)(0.2F + f / 15.0F), 2.5);
				int i = (int)(150.0 * e);
				((ServerWorld)this.field_6002)
					.method_14199(new BlockStateParticleParameters(ParticleTypes.field_11217, blockState), this.x, this.y, this.z, i, 0.0, 0.0, 0.0, 0.15F);
			}
		}

		super.method_5623(d, bl, blockState, blockPos);
	}

	public boolean canBreatheInWater() {
		return this.method_6046() == EntityGroup.UNDEAD;
	}

	@Environment(EnvType.CLIENT)
	public float method_6024(float f) {
		return MathHelper.lerp(f, this.field_6264, this.field_6243);
	}

	@Override
	public void updateLogic() {
		this.field_6229 = this.field_6251;
		super.updateLogic();
		this.field_6002.getProfiler().push("livingEntityBaseTick");
		boolean bl = this instanceof PlayerEntity;
		if (this.isValid()) {
			if (this.isInsideWall()) {
				this.damage(DamageSource.IN_WALL, 1.0F);
			} else if (bl && !this.field_6002.method_8621().method_11966(this.method_5829())) {
				double d = this.field_6002.method_8621().contains(this) + this.field_6002.method_8621().getSafeZone();
				if (d < 0.0) {
					double e = this.field_6002.method_8621().getDamagePerBlock();
					if (e > 0.0) {
						this.damage(DamageSource.IN_WALL, (float)Math.max(1, MathHelper.floor(-d * e)));
					}
				}
			}
		}

		if (this.isFireImmune() || this.field_6002.isClient) {
			this.extinguish();
		}

		boolean bl2 = bl && ((PlayerEntity)this).abilities.invulnerable;
		if (this.isValid()) {
			if (this.method_5777(FluidTags.field_15517)
				&& this.field_6002.method_8320(new BlockPos(this.x, this.y + (double)this.getStandingEyeHeight(), this.z)).getBlock() != Blocks.field_10422) {
				if (!this.canBreatheInWater() && !StatusEffectUtil.method_5574(this) && !bl2) {
					this.setBreath(this.method_6130(this.getBreath()));
					if (this.getBreath() == -20) {
						this.setBreath(0);
						Vec3d vec3d = this.method_18798();

						for (int i = 0; i < 8; i++) {
							float f = this.random.nextFloat() - this.random.nextFloat();
							float g = this.random.nextFloat() - this.random.nextFloat();
							float h = this.random.nextFloat() - this.random.nextFloat();
							this.field_6002.method_8406(ParticleTypes.field_11247, this.x + (double)f, this.y + (double)g, this.z + (double)h, vec3d.x, vec3d.y, vec3d.z);
						}

						this.damage(DamageSource.DROWN, 2.0F);
					}
				}

				if (!this.field_6002.isClient && this.hasVehicle() && this.getRiddenEntity() != null && !this.getRiddenEntity().method_5788()) {
					this.stopRiding();
				}
			} else if (this.getBreath() < this.getMaxBreath()) {
				this.setBreath(this.method_6064(this.getBreath()));
			}

			if (!this.field_6002.isClient) {
				BlockPos blockPos = new BlockPos(this);
				if (!Objects.equal(this.field_6268, blockPos)) {
					this.field_6268 = blockPos;
					this.method_6126(blockPos);
				}
			}
		}

		if (this.isValid() && this.isTouchingWater()) {
			this.extinguish();
		}

		this.field_6286 = this.field_6223;
		if (this.hurtTime > 0) {
			this.hurtTime--;
		}

		if (this.field_6008 > 0 && !(this instanceof ServerPlayerEntity)) {
			this.field_6008--;
		}

		if (this.getHealth() <= 0.0F) {
			this.updatePostDeath();
		}

		if (this.playerHitTimer > 0) {
			this.playerHitTimer--;
		} else {
			this.field_6258 = null;
		}

		if (this.attacking != null && !this.attacking.isValid()) {
			this.attacking = null;
		}

		if (this.attacker != null) {
			if (!this.attacker.isValid()) {
				this.setAttacker(null);
			} else if (this.age - this.lastAttackedTime > 100) {
				this.setAttacker(null);
			}
		}

		this.method_6050();
		this.field_6275 = this.field_6255;
		this.field_6220 = this.field_6283;
		this.prevHeadYaw = this.headYaw;
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
		this.field_6002.getProfiler().pop();
	}

	protected void method_6126(BlockPos blockPos) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.field_9122, this);
		if (i > 0) {
			FrostWalkerEnchantment.method_8236(this, this.field_6002, blockPos, i);
		}
	}

	public boolean isChild() {
		return false;
	}

	public float method_17825() {
		return this.isChild() ? 0.5F : 1.0F;
	}

	@Override
	public boolean method_5788() {
		return false;
	}

	protected void updatePostDeath() {
		this.deathCounter++;
		if (this.deathCounter == 20) {
			if (!this.field_6002.isClient
				&& (this.method_6071() || this.playerHitTimer > 0 && this.canDropLootAndXp() && this.field_6002.getGameRules().getBoolean("doMobLoot"))) {
				int i = this.method_6110(this.field_6258);

				while (i > 0) {
					int j = ExperienceOrbEntity.roundToOrbSize(i);
					i -= j;
					this.field_6002.spawnEntity(new ExperienceOrbEntity(this.field_6002, this.x, this.y, this.z, j));
				}
			}

			this.invalidate();

			for (int i = 0; i < 20; i++) {
				double d = this.random.nextGaussian() * 0.02;
				double e = this.random.nextGaussian() * 0.02;
				double f = this.random.nextGaussian() * 0.02;
				this.field_6002
					.method_8406(
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
		return !this.isChild();
	}

	protected int method_6130(int i) {
		int j = EnchantmentHelper.getRespiration(this);
		return j > 0 && this.random.nextInt(j + 1) > 0 ? i : i - 1;
	}

	protected int method_6064(int i) {
		return Math.min(i + 4, this.getMaxBreath());
	}

	protected int method_6110(PlayerEntity playerEntity) {
		return 0;
	}

	protected boolean method_6071() {
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

	protected void method_6116(ItemStack itemStack) {
		if (!itemStack.isEmpty()) {
			SoundEvent soundEvent = SoundEvents.field_14883;
			Item item = itemStack.getItem();
			if (item instanceof ArmorItem) {
				soundEvent = ((ArmorItem)item).method_7686().method_7698();
			} else if (item == Items.field_8833) {
				soundEvent = SoundEvents.field_14966;
			}

			this.method_5783(soundEvent, 1.0F, 1.0F);
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		compoundTag.putFloat("Health", this.getHealth());
		compoundTag.putShort("HurtTime", (short)this.hurtTime);
		compoundTag.putInt("HurtByTimestamp", this.lastAttackedTime);
		compoundTag.putShort("DeathTime", (short)this.deathCounter);
		compoundTag.putFloat("AbsorptionAmount", this.getAbsorptionAmount());

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			ItemStack itemStack = this.method_6118(equipmentSlot);
			if (!itemStack.isEmpty()) {
				this.method_6127().method_6209(itemStack.getAttributeModifiers(equipmentSlot));
			}
		}

		compoundTag.method_10566("Attributes", EntityAttributes.method_7134(this.method_6127()));

		for (EquipmentSlot equipmentSlotx : EquipmentSlot.values()) {
			ItemStack itemStack = this.method_6118(equipmentSlotx);
			if (!itemStack.isEmpty()) {
				this.method_6127().method_6210(itemStack.getAttributeModifiers(equipmentSlotx));
			}
		}

		if (!this.activePotionEffects.isEmpty()) {
			ListTag listTag = new ListTag();

			for (StatusEffectInstance statusEffectInstance : this.activePotionEffects.values()) {
				listTag.add(statusEffectInstance.method_5582(new CompoundTag()));
			}

			compoundTag.method_10566("ActiveEffects", listTag);
		}

		compoundTag.putBoolean("FallFlying", this.isFallFlying());
		this.getSleepingPosition().ifPresent(blockPos -> {
			compoundTag.putInt("SleepingX", blockPos.getX());
			compoundTag.putInt("SleepingY", blockPos.getY());
			compoundTag.putInt("SleepingZ", blockPos.getZ());
		});
		compoundTag.method_10566("Brain", this.field_18321.method_19508(NbtOps.INSTANCE));
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		this.setAbsorptionAmount(compoundTag.getFloat("AbsorptionAmount"));
		if (compoundTag.containsKey("Attributes", 9) && this.field_6002 != null && !this.field_6002.isClient) {
			EntityAttributes.method_7131(this.method_6127(), compoundTag.method_10554("Attributes", 10));
		}

		if (compoundTag.containsKey("ActiveEffects", 9)) {
			ListTag listTag = compoundTag.method_10554("ActiveEffects", 10);

			for (int i = 0; i < listTag.size(); i++) {
				CompoundTag compoundTag2 = listTag.getCompoundTag(i);
				StatusEffectInstance statusEffectInstance = StatusEffectInstance.method_5583(compoundTag2);
				if (statusEffectInstance != null) {
					this.activePotionEffects.put(statusEffectInstance.getEffectType(), statusEffectInstance);
				}
			}
		}

		if (compoundTag.containsKey("Health", 99)) {
			this.setHealth(compoundTag.getFloat("Health"));
		}

		this.hurtTime = compoundTag.getShort("HurtTime");
		this.deathCounter = compoundTag.getShort("DeathTime");
		this.lastAttackedTime = compoundTag.getInt("HurtByTimestamp");
		if (compoundTag.containsKey("Team", 8)) {
			String string = compoundTag.getString("Team");
			ScoreboardTeam scoreboardTeam = this.field_6002.method_8428().getTeam(string);
			boolean bl = scoreboardTeam != null && this.field_6002.method_8428().addPlayerToTeam(this.getUuidAsString(), scoreboardTeam);
			if (!bl) {
				LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", string);
			}
		}

		if (compoundTag.getBoolean("FallFlying")) {
			this.setEntityFlag(7, true);
		}

		if (compoundTag.containsKey("SleepingX", 99) && compoundTag.containsKey("SleepingY", 99) && compoundTag.containsKey("SleepingZ", 99)) {
			BlockPos blockPos = new BlockPos(compoundTag.getInt("SleepingX"), compoundTag.getInt("SleepingY"), compoundTag.getInt("SleepingZ"));
			this.method_18402(blockPos);
			this.method_18392(blockPos);
			this.method_18380(EntityPose.field_18078);
		}

		if (compoundTag.containsKey("Brain", 10)) {
			this.field_18321 = this.method_18867(new Dynamic<>(NbtOps.INSTANCE, compoundTag.method_10580("Brain")));
		}
	}

	protected void method_6050() {
		Iterator<StatusEffect> iterator = this.activePotionEffects.keySet().iterator();

		try {
			while (iterator.hasNext()) {
				StatusEffect statusEffect = (StatusEffect)iterator.next();
				StatusEffectInstance statusEffectInstance = (StatusEffectInstance)this.activePotionEffects.get(statusEffect);
				if (!statusEffectInstance.method_5585(this)) {
					if (!this.field_6002.isClient) {
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
			if (!this.field_6002.isClient) {
				this.updatePotionVisibility();
			}

			this.field_6285 = false;
		}

		int i = this.field_6011.get(field_6240);
		boolean bl = this.field_6011.get(field_6214);
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
				this.field_6002
					.method_8406(
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
		if (this.activePotionEffects.isEmpty()) {
			this.clearPotionSwirls();
			this.setInvisible(false);
		} else {
			Collection<StatusEffectInstance> collection = this.activePotionEffects.values();
			this.field_6011.set(field_6214, containsOnlyAmbientEffects(collection));
			this.field_6011.set(field_6240, PotionUtil.getColor(collection));
			this.setInvisible(this.hasPotionEffect(StatusEffects.field_5905));
		}
	}

	public double method_18390(@Nullable Entity entity) {
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
			ItemStack itemStack = this.method_6118(EquipmentSlot.HEAD);
			Item item = itemStack.getItem();
			EntityType<?> entityType = entity.method_5864();
			if (entityType == EntityType.SKELETON && item == Items.SKELETON_SKULL
				|| entityType == EntityType.ZOMBIE && item == Items.ZOMBIE_HEAD
				|| entityType == EntityType.CREEPER && item == Items.CREEPER_HEAD) {
				d *= 0.5;
			}
		}

		return d;
	}

	public boolean method_18395(LivingEntity livingEntity) {
		return true;
	}

	public boolean method_18391(LivingEntity livingEntity, class_4051 arg) {
		return arg.method_18419(this, livingEntity);
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
		this.field_6011.set(field_6214, false);
		this.field_6011.set(field_6240, 0);
	}

	public boolean clearPotionEffects() {
		if (this.field_6002.isClient) {
			return false;
		} else {
			Iterator<StatusEffectInstance> iterator = this.activePotionEffects.values().iterator();

			boolean bl;
			for (bl = false; iterator.hasNext(); bl = true) {
				this.method_6129((StatusEffectInstance)iterator.next());
				iterator.remove();
			}

			return bl;
		}
	}

	public Collection<StatusEffectInstance> getPotionEffects() {
		return this.activePotionEffects.values();
	}

	public Map<StatusEffect, StatusEffectInstance> method_6088() {
		return this.activePotionEffects;
	}

	public boolean hasPotionEffect(StatusEffect statusEffect) {
		return this.activePotionEffects.containsKey(statusEffect);
	}

	@Nullable
	public StatusEffectInstance getPotionEffect(StatusEffect statusEffect) {
		return (StatusEffectInstance)this.activePotionEffects.get(statusEffect);
	}

	public boolean addPotionEffect(StatusEffectInstance statusEffectInstance) {
		if (!this.isPotionEffective(statusEffectInstance)) {
			return false;
		} else {
			StatusEffectInstance statusEffectInstance2 = (StatusEffectInstance)this.activePotionEffects.get(statusEffectInstance.getEffectType());
			if (statusEffectInstance2 == null) {
				this.activePotionEffects.put(statusEffectInstance.getEffectType(), statusEffectInstance);
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
		if (this.method_6046() == EntityGroup.UNDEAD) {
			StatusEffect statusEffect = statusEffectInstance.getEffectType();
			if (statusEffect == StatusEffects.field_5924 || statusEffect == StatusEffects.field_5899) {
				return false;
			}
		}

		return true;
	}

	public boolean isUndead() {
		return this.method_6046() == EntityGroup.UNDEAD;
	}

	@Nullable
	public StatusEffectInstance removePotionEffect(@Nullable StatusEffect statusEffect) {
		return (StatusEffectInstance)this.activePotionEffects.remove(statusEffect);
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
		if (!this.field_6002.isClient) {
			statusEffectInstance.getEffectType().method_5555(this, this.method_6127(), statusEffectInstance.getAmplifier());
		}
	}

	protected void method_6009(StatusEffectInstance statusEffectInstance, boolean bl) {
		this.field_6285 = true;
		if (bl && !this.field_6002.isClient) {
			StatusEffect statusEffect = statusEffectInstance.getEffectType();
			statusEffect.method_5562(this, this.method_6127(), statusEffectInstance.getAmplifier());
			statusEffect.method_5555(this, this.method_6127(), statusEffectInstance.getAmplifier());
		}
	}

	protected void method_6129(StatusEffectInstance statusEffectInstance) {
		this.field_6285 = true;
		if (!this.field_6002.isClient) {
			statusEffectInstance.getEffectType().method_5562(this, this.method_6127(), statusEffectInstance.getAmplifier());
		}
	}

	public void heal(float f) {
		float g = this.getHealth();
		if (g > 0.0F) {
			this.setHealth(g + f);
		}
	}

	public float getHealth() {
		return this.field_6011.get(field_6247);
	}

	public void setHealth(float f) {
		this.field_6011.set(field_6247, MathHelper.clamp(f, 0.0F, this.getHealthMaximum()));
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (this.field_6002.isClient) {
			return false;
		} else if (this.getHealth() <= 0.0F) {
			return false;
		} else if (damageSource.isFire() && this.hasPotionEffect(StatusEffects.field_5918)) {
			return false;
		} else {
			if (this.isSleeping() && !this.field_6002.isClient) {
				this.wakeUp();
			}

			this.despawnCounter = 0;
			float g = f;
			if ((damageSource == DamageSource.ANVIL || damageSource == DamageSource.FALLING_BLOCK) && !this.method_6118(EquipmentSlot.HEAD).isEmpty()) {
				this.method_6118(EquipmentSlot.HEAD).applyDamage((int)(f * 4.0F + this.random.nextFloat() * f * 2.0F), this);
				f *= 0.75F;
			}

			boolean bl = false;
			float h = 0.0F;
			if (f > 0.0F && this.method_6061(damageSource)) {
				this.damageShield(f);
				h = f;
				f = 0.0F;
				if (!damageSource.isProjectile()) {
					Entity entity = damageSource.method_5526();
					if (entity instanceof LivingEntity) {
						this.method_6090((LivingEntity)entity);
					}
				}

				bl = true;
			}

			this.field_6225 = 1.5F;
			boolean bl2 = true;
			if ((float)this.field_6008 > 10.0F) {
				if (f <= this.field_6253) {
					return false;
				}

				this.applyDamage(damageSource, f - this.field_6253);
				this.field_6253 = f;
				bl2 = false;
			} else {
				this.field_6253 = f;
				this.field_6008 = 20;
				this.applyDamage(damageSource, f);
				this.field_6254 = 10;
				this.hurtTime = this.field_6254;
			}

			this.field_6271 = 0.0F;
			Entity entity2 = damageSource.method_5529();
			if (entity2 != null) {
				if (entity2 instanceof LivingEntity) {
					this.setAttacker((LivingEntity)entity2);
				}

				if (entity2 instanceof PlayerEntity) {
					this.playerHitTimer = 100;
					this.field_6258 = (PlayerEntity)entity2;
				} else if (entity2 instanceof WolfEntity) {
					WolfEntity wolfEntity = (WolfEntity)entity2;
					if (wolfEntity.isTamed()) {
						this.playerHitTimer = 100;
						LivingEntity livingEntity = wolfEntity.getOwner();
						if (livingEntity != null && livingEntity.method_5864() == EntityType.PLAYER) {
							this.field_6258 = (PlayerEntity)livingEntity;
						} else {
							this.field_6258 = null;
						}
					}
				}
			}

			if (bl2) {
				if (bl) {
					this.field_6002.summonParticle(this, (byte)29);
				} else if (damageSource instanceof EntityDamageSource && ((EntityDamageSource)damageSource).method_5549()) {
					this.field_6002.summonParticle(this, (byte)33);
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

					this.field_6002.summonParticle(this, b);
				}

				if (damageSource != DamageSource.DROWN && (!bl || f > 0.0F)) {
					this.scheduleVelocityUpdate();
				}

				if (entity2 != null) {
					double d = entity2.x - this.x;

					double e;
					for (e = entity2.z - this.z; d * d + e * e < 1.0E-4; e = (Math.random() - Math.random()) * 0.01) {
						d = (Math.random() - Math.random()) * 0.01;
					}

					this.field_6271 = (float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI - (double)this.yaw);
					this.method_6005(entity2, 0.4F, d, e);
				} else {
					this.field_6271 = (float)((int)(Math.random() * 2.0) * 180);
				}
			}

			if (this.getHealth() <= 0.0F) {
				if (!this.method_6095(damageSource)) {
					SoundEvent soundEvent = this.method_6002();
					if (bl2 && soundEvent != null) {
						this.method_5783(soundEvent, this.getSoundVolume(), this.getSoundPitch());
					}

					this.onDeath(damageSource);
				}
			} else if (bl2) {
				this.method_6013(damageSource);
			}

			boolean bl3 = !bl || f > 0.0F;
			if (bl3) {
				this.field_6276 = damageSource;
				this.field_6226 = this.field_6002.getTime();
			}

			if (this instanceof ServerPlayerEntity) {
				Criterions.ENTITY_HURT_PLAYER.method_8901((ServerPlayerEntity)this, damageSource, g, f, bl);
				if (h > 0.0F && h < 3.4028235E37F) {
					((ServerPlayerEntity)this).method_7339(Stats.field_15380, Math.round(h * 10.0F));
				}
			}

			if (entity2 instanceof ServerPlayerEntity) {
				Criterions.PLAYER_HURT_ENTITY.method_9097((ServerPlayerEntity)entity2, this, damageSource, g, f, bl);
			}

			return bl3;
		}
	}

	protected void method_6090(LivingEntity livingEntity) {
		livingEntity.method_6060(this);
	}

	protected void method_6060(LivingEntity livingEntity) {
		livingEntity.method_6005(this, 0.5F, livingEntity.x - this.x, livingEntity.z - this.z);
	}

	private boolean method_6095(DamageSource damageSource) {
		if (damageSource.doesDamageToCreative()) {
			return false;
		} else {
			ItemStack itemStack = null;

			for (Hand hand : Hand.values()) {
				ItemStack itemStack2 = this.method_5998(hand);
				if (itemStack2.getItem() == Items.field_8288) {
					itemStack = itemStack2.copy();
					itemStack2.subtractAmount(1);
					break;
				}
			}

			if (itemStack != null) {
				if (this instanceof ServerPlayerEntity) {
					ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this;
					serverPlayerEntity.method_7259(Stats.field_15372.getOrCreateStat(Items.field_8288));
					Criterions.USED_TOTEM.method_9165(serverPlayerEntity, itemStack);
				}

				this.setHealth(1.0F);
				this.clearPotionEffects();
				this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5924, 900, 1));
				this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5898, 100, 1));
				this.field_6002.summonParticle(this, (byte)35);
			}

			return itemStack != null;
		}
	}

	@Nullable
	public DamageSource method_6081() {
		if (this.field_6002.getTime() - this.field_6226 > 40L) {
			this.field_6276 = null;
		}

		return this.field_6276;
	}

	protected void method_6013(DamageSource damageSource) {
		SoundEvent soundEvent = this.method_6011(damageSource);
		if (soundEvent != null) {
			this.method_5783(soundEvent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	private boolean method_6061(DamageSource damageSource) {
		Entity entity = damageSource.method_5526();
		boolean bl = false;
		if (entity instanceof ProjectileEntity) {
			ProjectileEntity projectileEntity = (ProjectileEntity)entity;
			if (projectileEntity.getPierceLevel() > 0) {
				bl = true;
			}
		}

		if (!damageSource.doesBypassArmor() && this.method_6039() && !bl) {
			Vec3d vec3d = damageSource.method_5510();
			if (vec3d != null) {
				Vec3d vec3d2 = this.method_5828(1.0F);
				Vec3d vec3d3 = vec3d.reverseSubtract(new Vec3d(this.x, this.y, this.z)).normalize();
				vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
				if (vec3d3.dotProduct(vec3d2) < 0.0) {
					return true;
				}
			}
		}

		return false;
	}

	public void method_6045(ItemStack itemStack) {
		super.method_5783(SoundEvents.field_15075, 0.8F, 0.8F + this.field_6002.random.nextFloat() * 0.4F);
		this.method_6037(itemStack, 5);
	}

	public void onDeath(DamageSource damageSource) {
		if (!this.dead) {
			Entity entity = damageSource.method_5529();
			LivingEntity livingEntity = this.method_6124();
			if (this.field_6232 >= 0 && livingEntity != null) {
				livingEntity.method_5716(this, this.field_6232, damageSource);
			}

			if (entity != null) {
				entity.method_5874(this);
			}

			this.dead = true;
			this.getDamageTracker().update();
			if (!this.field_6002.isClient) {
				this.method_16080(damageSource);
				boolean bl = false;
				if (livingEntity instanceof WitherEntity) {
					if (this.field_6002.getGameRules().getBoolean("mobGriefing")) {
						BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
						BlockState blockState = Blocks.field_10606.method_9564();
						if (this.field_6002.method_8320(blockPos).isAir() && blockState.method_11591(this.field_6002, blockPos)) {
							this.field_6002.method_8652(blockPos, blockState, 3);
							bl = true;
						}
					}

					if (!bl) {
						ItemEntity itemEntity = new ItemEntity(this.field_6002, this.x, this.y, this.z, new ItemStack(Items.WITHER_ROSE));
						this.field_6002.spawnEntity(itemEntity);
					}
				}
			}

			this.field_6002.summonParticle(this, (byte)3);
			this.method_18380(EntityPose.field_18082);
		}
	}

	protected void method_16080(DamageSource damageSource) {
		Entity entity = damageSource.method_5529();
		int i;
		if (entity instanceof PlayerEntity) {
			i = EnchantmentHelper.getLooting((LivingEntity)entity);
		} else {
			i = 0;
		}

		boolean bl = this.playerHitTimer > 0;
		if (this.canDropLootAndXp() && this.field_6002.getGameRules().getBoolean("doMobLoot")) {
			this.dropLoot(damageSource, bl);
			this.dropEquipment(damageSource, i, bl);
		}

		this.dropInventory();
	}

	protected void dropInventory() {
	}

	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
	}

	public Identifier method_5989() {
		return this.method_5864().method_16351();
	}

	protected void dropLoot(DamageSource damageSource, boolean bl) {
		Identifier identifier = this.method_5989();
		LootSupplier lootSupplier = this.field_6002.getServer().getLootManager().method_367(identifier);
		LootContext.Builder builder = this.method_16079(bl, damageSource);
		lootSupplier.dropLimited(builder.method_309(LootContextTypes.ENTITY), this::method_5775);
	}

	protected LootContext.Builder method_16079(boolean bl, DamageSource damageSource) {
		LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.field_6002)
			.setRandom(this.random)
			.method_312(LootContextParameters.field_1226, this)
			.method_312(LootContextParameters.field_1232, new BlockPos(this))
			.method_312(LootContextParameters.field_1231, damageSource)
			.method_306(LootContextParameters.field_1230, damageSource.method_5529())
			.method_306(LootContextParameters.field_1227, damageSource.method_5526());
		if (bl && this.field_6258 != null) {
			builder = builder.method_312(LootContextParameters.field_1233, this.field_6258).setLuck(this.field_6258.getLuck());
		}

		return builder;
	}

	public void method_6005(Entity entity, float f, double d, double e) {
		if (!(this.random.nextDouble() < this.method_5996(EntityAttributes.KNOCKBACK_RESISTANCE).getValue())) {
			this.velocityDirty = true;
			Vec3d vec3d = this.method_18798();
			Vec3d vec3d2 = new Vec3d(d, 0.0, e).normalize().multiply((double)f);
			this.setVelocity(vec3d.x / 2.0 - vec3d2.x, this.onGround ? Math.min(0.4, vec3d.y / 2.0 + (double)f) : vec3d.y, vec3d.z / 2.0 - vec3d2.z);
		}
	}

	@Nullable
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14940;
	}

	@Nullable
	protected SoundEvent method_6002() {
		return SoundEvents.field_14732;
	}

	protected SoundEvent method_6041(int i) {
		return i > 4 ? SoundEvents.field_14928 : SoundEvents.field_15018;
	}

	protected SoundEvent method_18807(ItemStack itemStack) {
		return SoundEvents.field_14643;
	}

	public SoundEvent method_18869(ItemStack itemStack) {
		return SoundEvents.field_14544;
	}

	public boolean canClimb() {
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
		return this.field_6002.method_8320(new BlockPos(this));
	}

	private boolean method_6077(BlockPos blockPos, BlockState blockState) {
		if ((Boolean)blockState.method_11654(TrapdoorBlock.field_11631)) {
			BlockState blockState2 = this.field_6002.method_8320(blockPos.down());
			if (blockState2.getBlock() == Blocks.field_9983 && blockState2.method_11654(LadderBlock.field_11253) == blockState.method_11654(TrapdoorBlock.field_11177)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isValid() {
		return !this.invalid && this.getHealth() > 0.0F;
	}

	@Override
	public void handleFallDamage(float f, float g) {
		super.handleFallDamage(f, g);
		StatusEffectInstance statusEffectInstance = this.getPotionEffect(StatusEffects.field_5913);
		float h = statusEffectInstance == null ? 0.0F : (float)(statusEffectInstance.getAmplifier() + 1);
		int i = MathHelper.ceil((f - 3.0F - h) * g);
		if (i > 0) {
			this.method_5783(this.method_6041(i), 1.0F, 1.0F);
			this.damage(DamageSource.FALL, (float)i);
			int j = MathHelper.floor(this.x);
			int k = MathHelper.floor(this.y - 0.2F);
			int l = MathHelper.floor(this.z);
			BlockState blockState = this.field_6002.method_8320(new BlockPos(j, k, l));
			if (!blockState.isAir()) {
				BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
				this.method_5783(blockSoundGroup.method_10593(), blockSoundGroup.getVolume() * 0.5F, blockSoundGroup.getPitch() * 0.75F);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5879() {
		this.field_6254 = 10;
		this.hurtTime = this.field_6254;
		this.field_6271 = 0.0F;
	}

	public int method_6096() {
		EntityAttributeInstance entityAttributeInstance = this.method_5996(EntityAttributes.ARMOR);
		return MathHelper.floor(entityAttributeInstance.getValue());
	}

	protected void method_6105(float f) {
	}

	protected void damageShield(float f) {
	}

	protected float method_6132(DamageSource damageSource, float f) {
		if (!damageSource.doesBypassArmor()) {
			this.method_6105(f);
			f = class_1280.method_5496(f, (float)this.method_6096(), (float)this.method_5996(EntityAttributes.ARMOR_TOUGHNESS).getValue());
		}

		return f;
	}

	protected float method_6036(DamageSource damageSource, float f) {
		if (damageSource.isUnblockable()) {
			return f;
		} else {
			if (this.hasPotionEffect(StatusEffects.field_5907) && damageSource != DamageSource.OUT_OF_WORLD) {
				int i = (this.getPotionEffect(StatusEffects.field_5907).getAmplifier() + 1) * 5;
				int j = 25 - i;
				float g = f * (float)j;
				float h = f;
				f = Math.max(g / 25.0F, 0.0F);
				float k = h - f;
				if (k > 0.0F && k < 3.4028235E37F) {
					if (this instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)this).method_7339(Stats.field_15425, Math.round(k * 10.0F));
					} else if (damageSource.method_5529() instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)damageSource.method_5529()).method_7339(Stats.field_15397, Math.round(k * 10.0F));
					}
				}
			}

			if (f <= 0.0F) {
				return 0.0F;
			} else {
				int i = EnchantmentHelper.getProtectionAmount(this.getItemsArmor(), damageSource);
				if (i > 0) {
					f = class_1280.method_5497(f, (float)i);
				}

				return f;
			}
		}
	}

	protected void applyDamage(DamageSource damageSource, float f) {
		if (!this.isInvulnerableTo(damageSource)) {
			f = this.method_6132(damageSource, f);
			f = this.method_6036(damageSource, f);
			float var8 = Math.max(f - this.getAbsorptionAmount(), 0.0F);
			this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - var8));
			float h = f - var8;
			if (h > 0.0F && h < 3.4028235E37F && damageSource.method_5529() instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)damageSource.method_5529()).method_7339(Stats.field_15408, Math.round(h * 10.0F));
			}

			if (var8 != 0.0F) {
				float i = this.getHealth();
				this.setHealth(i - var8);
				this.getDamageTracker().method_5547(damageSource, i, var8);
				this.setAbsorptionAmount(this.getAbsorptionAmount() - var8);
			}
		}
	}

	public DamageTracker getDamageTracker() {
		return this.damageTracker;
	}

	@Nullable
	public LivingEntity method_6124() {
		if (this.damageTracker.method_5541() != null) {
			return this.damageTracker.method_5541();
		} else if (this.field_6258 != null) {
			return this.field_6258;
		} else {
			return this.attacker != null ? this.attacker : null;
		}
	}

	public final float getHealthMaximum() {
		return (float)this.method_5996(EntityAttributes.MAX_HEALTH).getValue();
	}

	public final int getStuckArrows() {
		return this.field_6011.get(field_6219);
	}

	public final void setStuckArrows(int i) {
		this.field_6011.set(field_6219, i);
	}

	private int method_6028() {
		if (StatusEffectUtil.method_5576(this)) {
			return 6 - (1 + StatusEffectUtil.method_5575(this));
		} else {
			return this.hasPotionEffect(StatusEffects.field_5901) ? 6 + (1 + this.getPotionEffect(StatusEffects.field_5901).getAmplifier()) * 2 : 6;
		}
	}

	public void swingHand(Hand hand) {
		if (!this.field_6252 || this.field_6279 >= this.method_6028() / 2 || this.field_6279 < 0) {
			this.field_6279 = -1;
			this.field_6252 = true;
			this.preferredHand = hand;
			if (this.field_6002 instanceof ServerWorld) {
				((ServerWorld)this.field_6002).method_14178().method_18754(this, new EntityAnimationS2CPacket(this, hand == Hand.MAIN ? 0 : 3));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		boolean bl = b == 33;
		boolean bl2 = b == 36;
		boolean bl3 = b == 37;
		boolean bl4 = b == 44;
		if (b == 2 || bl || bl2 || bl3 || bl4) {
			this.field_6225 = 1.5F;
			this.field_6008 = 20;
			this.field_6254 = 10;
			this.hurtTime = this.field_6254;
			this.field_6271 = 0.0F;
			if (bl) {
				this.method_5783(SoundEvents.field_14663, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
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

			SoundEvent soundEvent = this.method_6011(damageSource);
			if (soundEvent != null) {
				this.method_5783(soundEvent, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}

			this.damage(DamageSource.GENERIC, 0.0F);
		} else if (b == 3) {
			SoundEvent soundEvent2 = this.method_6002();
			if (soundEvent2 != null) {
				this.method_5783(soundEvent2, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			}

			this.setHealth(0.0F);
			this.onDeath(DamageSource.GENERIC);
		} else if (b == 30) {
			this.method_5783(SoundEvents.field_15239, 0.8F, 0.8F + this.field_6002.random.nextFloat() * 0.4F);
		} else if (b == 29) {
			this.method_5783(SoundEvents.field_15150, 1.0F, 0.8F + this.field_6002.random.nextFloat() * 0.4F);
		} else {
			super.method_5711(b);
		}
	}

	@Override
	protected void destroy() {
		this.damage(DamageSource.OUT_OF_WORLD, 4.0F);
	}

	protected void method_6119() {
		int i = this.method_6028();
		if (this.field_6252) {
			this.field_6279++;
			if (this.field_6279 >= i) {
				this.field_6279 = 0;
				this.field_6252 = false;
			}
		} else {
			this.field_6279 = 0;
		}

		this.field_6251 = (float)this.field_6279 / (float)i;
	}

	public EntityAttributeInstance method_5996(EntityAttribute entityAttribute) {
		return this.method_6127().get(entityAttribute);
	}

	public AbstractEntityAttributeContainer method_6127() {
		if (this.field_6260 == null) {
			this.field_6260 = new EntityAttributeContainer();
		}

		return this.field_6260;
	}

	public EntityGroup method_6046() {
		return EntityGroup.DEFAULT;
	}

	public ItemStack method_6047() {
		return this.method_6118(EquipmentSlot.HAND_MAIN);
	}

	public ItemStack method_6079() {
		return this.method_6118(EquipmentSlot.HAND_OFF);
	}

	public ItemStack method_5998(Hand hand) {
		if (hand == Hand.MAIN) {
			return this.method_6118(EquipmentSlot.HAND_MAIN);
		} else if (hand == Hand.OFF) {
			return this.method_6118(EquipmentSlot.HAND_OFF);
		} else {
			throw new IllegalArgumentException("Invalid hand " + hand);
		}
	}

	public void method_6122(Hand hand, ItemStack itemStack) {
		if (hand == Hand.MAIN) {
			this.method_5673(EquipmentSlot.HAND_MAIN, itemStack);
		} else {
			if (hand != Hand.OFF) {
				throw new IllegalArgumentException("Invalid hand " + hand);
			}

			this.method_5673(EquipmentSlot.HAND_OFF, itemStack);
		}
	}

	public boolean isEquippedStackValid(EquipmentSlot equipmentSlot) {
		return !this.method_6118(equipmentSlot).isEmpty();
	}

	@Override
	public abstract Iterable<ItemStack> getItemsArmor();

	public abstract ItemStack method_6118(EquipmentSlot equipmentSlot);

	@Override
	public abstract void method_5673(EquipmentSlot equipmentSlot, ItemStack itemStack);

	public float method_18396() {
		Iterable<ItemStack> iterable = this.getItemsArmor();
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
		EntityAttributeInstance entityAttributeInstance = this.method_5996(EntityAttributes.MOVEMENT_SPEED);
		if (entityAttributeInstance.method_6199(ATTR_SPRINTING_SPEED_BOOST_ID) != null) {
			entityAttributeInstance.method_6202(field_6231);
		}

		if (bl) {
			entityAttributeInstance.method_6197(field_6231);
		}
	}

	protected float getSoundVolume() {
		return 1.0F;
	}

	protected float getSoundPitch() {
		return this.isChild() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
	}

	protected boolean method_6062() {
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
			double l = entity.method_5829().minY + (double)entity.getHeight();
			double e = entity.z;
			Direction direction = entity.method_5755();
			if (direction != null) {
				Direction direction2 = direction.rotateYClockwise();
				int[][] is = new int[][]{{0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0}, {0, 1}};
				double m = Math.floor(this.x) + 0.5;
				double n = Math.floor(this.z) + 0.5;
				double o = this.method_5829().maxX - this.method_5829().minX;
				double p = this.method_5829().maxZ - this.method_5829().minZ;
				BoundingBox boundingBox = new BoundingBox(
					m - o / 2.0, entity.method_5829().minY, n - p / 2.0, m + o / 2.0, Math.floor(entity.method_5829().minY) + (double)this.getHeight(), n + p / 2.0
				);

				for (int[] js : is) {
					double q = (double)(direction.getOffsetX() * js[0] + direction2.getOffsetX() * js[1]);
					double r = (double)(direction.getOffsetZ() * js[0] + direction2.getOffsetZ() * js[1]);
					double s = m + q;
					double t = n + r;
					BoundingBox boundingBox2 = boundingBox.offset(q, 0.0, r);
					if (this.field_6002.method_8587(this, boundingBox2)) {
						BlockPos blockPos = new BlockPos(s, this.y, t);
						if (this.field_6002.method_8320(blockPos).method_11631(this.field_6002, blockPos)) {
							this.method_5859(s, this.y + 1.0, t);
							return;
						}

						BlockPos blockPos2 = new BlockPos(s, this.y - 1.0, t);
						if (this.field_6002.method_8320(blockPos2).method_11631(this.field_6002, blockPos2)
							|| this.field_6002.method_8316(blockPos2).method_15767(FluidTags.field_15517)) {
							k = s;
							l = this.y + 1.0;
							e = t;
						}
					} else {
						BlockPos blockPosx = new BlockPos(s, this.y + 1.0, t);
						if (this.field_6002.method_8587(this, boundingBox2.offset(0.0, 1.0, 0.0))
							&& this.field_6002.method_8320(blockPosx).method_11631(this.field_6002, blockPosx)) {
							k = s;
							l = this.y + 2.0;
							e = t;
						}
					}
				}
			}

			this.method_5859(k, l, e);
		} else {
			double d = (double)(this.getWidth() / 2.0F + entity.getWidth() / 2.0F) + 0.4;
			float f;
			if (entity instanceof BoatEntity) {
				f = 0.0F;
			} else {
				f = (float) (Math.PI / 2) * (float)(this.getMainHand() == OptionMainHand.field_6183 ? -1 : 1);
			}

			float g = -MathHelper.sin(-this.yaw * (float) (Math.PI / 180.0) - (float) Math.PI + f);
			float h = -MathHelper.cos(-this.yaw * (float) (Math.PI / 180.0) - (float) Math.PI + f);
			double e = Math.abs(g) > Math.abs(h) ? d / (double)Math.abs(g) : d / (double)Math.abs(h);
			double i = this.x + (double)g * e;
			double j = this.z + (double)h * e;
			this.setPosition(i, entity.y + (double)entity.getHeight() + 0.001, j);
			if (!this.field_6002.method_8587(this, this.method_5829().union(entity.method_5829()))) {
				this.setPosition(i, entity.y + (double)entity.getHeight() + 1.001, j);
				if (!this.field_6002.method_8587(this, this.method_5829().union(entity.method_5829()))) {
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
		if (this.hasPotionEffect(StatusEffects.field_5913)) {
			f = this.getJumpVelocity() + 0.1F * (float)(this.getPotionEffect(StatusEffects.field_5913).getAmplifier() + 1);
		} else {
			f = this.getJumpVelocity();
		}

		Vec3d vec3d = this.method_18798();
		this.setVelocity(vec3d.x, (double)f, vec3d.z);
		if (this.isSprinting()) {
			float g = this.yaw * (float) (Math.PI / 180.0);
			this.method_18799(this.method_18798().add((double)(-MathHelper.sin(g) * 0.2F), 0.0, (double)(MathHelper.cos(g) * 0.2F)));
		}

		this.velocityDirty = true;
	}

	@Environment(EnvType.CLIENT)
	protected void method_6093() {
		this.method_18799(this.method_18798().add(0.0, -0.04F, 0.0));
	}

	protected void method_6010(Tag<Fluid> tag) {
		this.method_18799(this.method_18798().add(0.0, 0.04F, 0.0));
	}

	protected float method_6120() {
		return 0.8F;
	}

	public void method_6091(Vec3d vec3d) {
		if (this.method_6034() || this.method_5787()) {
			double d = 0.08;
			boolean bl = this.method_18798().y <= 0.0;
			if (bl && this.hasPotionEffect(StatusEffects.field_5906)) {
				d = 0.01;
				this.fallDistance = 0.0F;
			}

			if (!this.isInsideWater() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying) {
				if (!this.isTouchingLava() || this instanceof PlayerEntity && ((PlayerEntity)this).abilities.flying) {
					if (this.isFallFlying()) {
						Vec3d vec3d5 = this.method_18798();
						if (vec3d5.y > -0.5) {
							this.fallDistance = 1.0F;
						}

						Vec3d vec3d6 = this.method_5720();
						float f = this.pitch * (float) (Math.PI / 180.0);
						double j = Math.sqrt(vec3d6.x * vec3d6.x + vec3d6.z * vec3d6.z);
						double k = Math.sqrt(method_17996(vec3d5));
						double i = vec3d6.length();
						float l = MathHelper.cos(f);
						l = (float)((double)l * (double)l * Math.min(1.0, i / 0.4));
						vec3d5 = this.method_18798().add(0.0, d * (-1.0 + (double)l * 0.75), 0.0);
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

						this.method_18799(vec3d5.multiply(0.99F, 0.98F, 0.99F));
						this.method_5784(MovementType.field_6308, this.method_18798());
						if (this.horizontalCollision && !this.field_6002.isClient) {
							double m = Math.sqrt(method_17996(this.method_18798()));
							double n = k - m;
							float o = (float)(n * 10.0 - 3.0);
							if (o > 0.0F) {
								this.method_5783(this.method_6041((int)o), 1.0F, 1.0F);
								this.damage(DamageSource.FLY_INTO_WALL, o);
							}
						}

						if (this.onGround && !this.field_6002.isClient) {
							this.setEntityFlag(7, false);
						}
					} else {
						BlockPos blockPos = new BlockPos(this.x, this.method_5829().minY - 1.0, this.z);
						float p = this.field_6002.method_8320(blockPos).getBlock().getFrictionCoefficient();
						float fx = this.onGround ? p * 0.91F : 0.91F;
						this.method_5724(this.method_18802(p), vec3d);
						this.method_18799(this.method_18801(this.method_18798()));
						this.method_5784(MovementType.field_6308, this.method_18798());
						Vec3d vec3d7 = this.method_18798();
						if ((this.horizontalCollision || this.field_6282) && this.canClimb()) {
							vec3d7 = new Vec3d(vec3d7.x, 0.2, vec3d7.z);
						}

						double q = vec3d7.y;
						if (this.hasPotionEffect(StatusEffects.field_5902)) {
							q += (0.05 * (double)(this.getPotionEffect(StatusEffects.field_5902).getAmplifier() + 1) - vec3d7.y) * 0.2;
							this.fallDistance = 0.0F;
						} else if (this.field_6002.isClient && !this.field_6002.method_8591(blockPos)) {
							if (this.y > 0.0) {
								q = -0.1;
							} else {
								q = 0.0;
							}
						} else if (!this.isUnaffectedByGravity()) {
							q -= d;
						}

						this.setVelocity(vec3d7.x * (double)fx, q * 0.98F, vec3d7.z * (double)fx);
					}
				} else {
					double e = this.y;
					this.method_5724(0.02F, vec3d);
					this.method_5784(MovementType.field_6308, this.method_18798());
					this.method_18799(this.method_18798().multiply(0.5));
					if (!this.isUnaffectedByGravity()) {
						this.method_18799(this.method_18798().add(0.0, -d / 4.0, 0.0));
					}

					Vec3d vec3d4 = this.method_18798();
					if (this.horizontalCollision && this.method_5654(vec3d4.x, vec3d4.y + 0.6F - this.y + e, vec3d4.z)) {
						this.setVelocity(vec3d4.x, 0.3F, vec3d4.z);
					}
				}
			} else {
				double ex = this.y;
				float fxx = this.isSprinting() ? 0.9F : this.method_6120();
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

				if (this.hasPotionEffect(StatusEffects.field_5900)) {
					fxx = 0.96F;
				}

				this.method_5724(g, vec3d);
				this.method_5784(MovementType.field_6308, this.method_18798());
				Vec3d vec3d2 = this.method_18798();
				if (this.horizontalCollision && this.canClimb()) {
					vec3d2 = new Vec3d(vec3d2.x, 0.2, vec3d2.z);
				}

				this.method_18799(vec3d2.multiply((double)fxx, 0.8F, (double)fxx));
				if (!this.isUnaffectedByGravity() && !this.isSprinting()) {
					Vec3d vec3d3 = this.method_18798();
					double ix;
					if (bl && Math.abs(vec3d3.y - 0.005) >= 0.003 && Math.abs(vec3d3.y - d / 16.0) < 0.003) {
						ix = -0.003;
					} else {
						ix = vec3d3.y - d / 16.0;
					}

					this.setVelocity(vec3d3.x, ix, vec3d3.z);
				}

				Vec3d vec3d3 = this.method_18798();
				if (this.horizontalCollision && this.method_5654(vec3d3.x, vec3d3.y + 0.6F - this.y + ex, vec3d3.z)) {
					this.setVelocity(vec3d3.x, 0.3F, vec3d3.z);
				}
			}
		}

		this.field_6211 = this.field_6225;
		double dx = this.x - this.prevX;
		double r = this.z - this.prevZ;
		double s = this instanceof class_1432 ? this.y - this.prevY : 0.0;
		float gx = MathHelper.sqrt(dx * dx + s * s + r * r) * 4.0F;
		if (gx > 1.0F) {
			gx = 1.0F;
		}

		this.field_6225 = this.field_6225 + (gx - this.field_6225) * 0.4F;
		this.field_6249 = this.field_6249 + this.field_6225;
	}

	private Vec3d method_18801(Vec3d vec3d) {
		if (this.canClimb()) {
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

	public boolean attack(Entity entity) {
		this.onAttacking(entity);
		return false;
	}

	@Override
	public void update() {
		super.update();
		this.method_6076();
		this.method_6072();
		if (!this.field_6002.isClient) {
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
					case HAND:
						itemStack = this.field_6234.get(equipmentSlot.getEntitySlotId());
						break;
					case ARMOR:
						itemStack = this.field_6248.get(equipmentSlot.getEntitySlotId());
						break;
					default:
						continue;
				}

				ItemStack itemStack2 = this.method_6118(equipmentSlot);
				if (!ItemStack.areEqual(itemStack2, itemStack)) {
					((ServerWorld)this.field_6002).method_14178().method_18754(this, new EntityEquipmentUpdateS2CPacket(this.getEntityId(), equipmentSlot, itemStack2));
					if (!itemStack.isEmpty()) {
						this.method_6127().method_6209(itemStack.getAttributeModifiers(equipmentSlot));
					}

					if (!itemStack2.isEmpty()) {
						this.method_6127().method_6210(itemStack2.getAttributeModifiers(equipmentSlot));
					}

					switch (equipmentSlot.getType()) {
						case HAND:
							this.field_6234.set(equipmentSlot.getEntitySlotId(), itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2.copy());
							break;
						case ARMOR:
							this.field_6248.set(equipmentSlot.getEntitySlotId(), itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2.copy());
					}
				}
			}

			if (this.age % 20 == 0) {
				this.getDamageTracker().update();
			}

			if (!this.glowing) {
				boolean bl = this.hasPotionEffect(StatusEffects.field_5912);
				if (this.getEntityFlag(6) != bl) {
					this.setEntityFlag(6, bl);
				}
			}

			if (this.isSleeping() && !this.isSleepingInBed()) {
				this.wakeUp();
			}
		}

		this.updateMovement();
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

		if (this.field_6251 > 0.0F) {
			g = this.yaw;
		}

		if (!this.onGround) {
			j = 0.0F;
		}

		this.field_6233 = this.field_6233 + (j - this.field_6233) * 0.3F;
		this.field_6002.getProfiler().push("headTurn");
		h = this.method_6031(g, h);
		this.field_6002.getProfiler().pop();
		this.field_6002.getProfiler().push("rangeChecks");

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

		this.field_6002.getProfiler().pop();
		this.field_6255 += h;
		if (this.isFallFlying()) {
			this.field_6239++;
		} else {
			this.field_6239 = 0;
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

	public void updateMovement() {
		if (this.field_6228 > 0) {
			this.field_6228--;
		}

		if (this.field_6210 > 0 && !this.method_5787()) {
			double d = this.x + (this.field_6224 - this.x) / (double)this.field_6210;
			double e = this.y + (this.field_6245 - this.y) / (double)this.field_6210;
			double f = this.z + (this.field_6263 - this.z) / (double)this.field_6210;
			double g = MathHelper.wrapDegrees(this.field_6284 - (double)this.yaw);
			this.yaw = (float)((double)this.yaw + g / (double)this.field_6210);
			this.pitch = (float)((double)this.pitch + (this.field_6221 - (double)this.pitch) / (double)this.field_6210);
			this.field_6210--;
			this.setPosition(d, e, f);
			this.setRotation(this.yaw, this.pitch);
		} else if (!this.method_6034()) {
			this.method_18799(this.method_18798().multiply(0.98));
		}

		if (this.field_6265 > 0) {
			this.headYaw = (float)((double)this.headYaw + MathHelper.wrapDegrees(this.field_6242 - (double)this.headYaw) / (double)this.field_6265);
			this.field_6265--;
		}

		Vec3d vec3d = this.method_18798();
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
		this.field_6002.getProfiler().push("ai");
		if (this.method_6062()) {
			this.field_6282 = false;
			this.movementInputSideways = 0.0F;
			this.movementInputForward = 0.0F;
			this.field_6267 = 0.0F;
		} else if (this.method_6034()) {
			this.field_6002.getProfiler().push("newAi");
			this.method_6023();
			this.field_6002.getProfiler().pop();
		}

		this.field_6002.getProfiler().pop();
		this.field_6002.getProfiler().push("jump");
		if (this.field_6282) {
			if (!(this.field_5964 > 0.0) || this.onGround && !(this.field_5964 > 0.4)) {
				if (this.isTouchingLava()) {
					this.method_6010(FluidTags.field_15518);
				} else if ((this.onGround || this.field_5964 > 0.0 && this.field_5964 <= 0.4) && this.field_6228 == 0) {
					this.jump();
					this.field_6228 = 10;
				}
			} else {
				this.method_6010(FluidTags.field_15517);
			}
		} else {
			this.field_6228 = 0;
		}

		this.field_6002.getProfiler().pop();
		this.field_6002.getProfiler().push("travel");
		this.movementInputSideways *= 0.98F;
		this.movementInputForward *= 0.98F;
		this.field_6267 *= 0.9F;
		this.initAi();
		BoundingBox boundingBox = this.method_5829();
		this.method_6091(new Vec3d((double)this.movementInputSideways, (double)this.movementInputUp, (double)this.movementInputForward));
		this.field_6002.getProfiler().pop();
		this.field_6002.getProfiler().push("push");
		if (this.field_6261 > 0) {
			this.field_6261--;
			this.method_6035(boundingBox, this.method_5829());
		}

		this.doPushLogic();
		this.field_6002.getProfiler().pop();
	}

	private void initAi() {
		boolean bl = this.getEntityFlag(7);
		if (bl && !this.onGround && !this.hasVehicle()) {
			ItemStack itemStack = this.method_6118(EquipmentSlot.CHEST);
			if (itemStack.getItem() == Items.field_8833 && ElytraItem.method_7804(itemStack)) {
				bl = true;
				if (!this.field_6002.isClient && (this.field_6239 + 1) % 20 == 0) {
					itemStack.applyDamage(1, this);
				}
			} else {
				bl = false;
			}
		} else {
			bl = false;
		}

		if (!this.field_6002.isClient) {
			this.setEntityFlag(7, bl);
		}
	}

	protected void method_6023() {
	}

	protected void doPushLogic() {
		List<Entity> list = this.field_6002.method_8333(this, this.method_5829(), EntityPredicates.method_5911(this));
		if (!list.isEmpty()) {
			int i = this.field_6002.getGameRules().getInteger("maxEntityCramming");
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

	protected void method_6035(BoundingBox boundingBox, BoundingBox boundingBox2) {
		BoundingBox boundingBox3 = boundingBox.union(boundingBox2);
		List<Entity> list = this.field_6002.method_8335(this, boundingBox3);
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				Entity entity = (Entity)list.get(i);
				if (entity instanceof LivingEntity) {
					this.attackLivingEntity((LivingEntity)entity);
					this.field_6261 = 0;
					this.method_18799(this.method_18798().multiply(-0.2));
					break;
				}
			}
		} else if (this.horizontalCollision) {
			this.field_6261 = 0;
		}

		if (!this.field_6002.isClient && this.field_6261 <= 0) {
			this.method_6085(4, false);
		}
	}

	protected void pushAway(Entity entity) {
		entity.pushAwayFrom(this);
	}

	protected void attackLivingEntity(LivingEntity livingEntity) {
	}

	public void method_6018(int i) {
		this.field_6261 = i;
		if (!this.field_6002.isClient) {
			this.method_6085(4, true);
		}
	}

	public boolean isUsingRiptide() {
		return (this.field_6011.get(field_6257) & 4) != 0;
	}

	@Override
	public void stopRiding() {
		Entity entity = this.getRiddenEntity();
		super.stopRiding();
		if (entity != null && entity != this.getRiddenEntity() && !this.field_6002.isClient) {
			this.method_6038(entity);
		}
	}

	@Override
	public void updateRiding() {
		super.updateRiding();
		this.field_6217 = this.field_6233;
		this.field_6233 = 0.0F;
		this.fallDistance = 0.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setPositionAndRotations(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.field_6224 = d;
		this.field_6245 = e;
		this.field_6263 = f;
		this.field_6284 = (double)g;
		this.field_6221 = (double)h;
		this.field_6210 = i;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5683(float f, int i) {
		this.field_6242 = (double)f;
		this.field_6265 = i;
	}

	public void doJump(boolean bl) {
		this.field_6282 = bl;
	}

	public void pickUpEntity(Entity entity, int i) {
		if (!entity.invalid
			&& !this.field_6002.isClient
			&& (entity instanceof ItemEntity || entity instanceof ProjectileEntity || entity instanceof ExperienceOrbEntity)) {
			((ServerWorld)this.field_6002).method_14178().method_18754(entity, new ItemPickupAnimationS2CPacket(entity.getEntityId(), this.getEntityId(), i));
		}
	}

	public boolean canSee(Entity entity) {
		Vec3d vec3d = new Vec3d(this.x, this.y + (double)this.getStandingEyeHeight(), this.z);
		Vec3d vec3d2 = new Vec3d(entity.x, entity.y + (double)entity.getStandingEyeHeight(), entity.z);
		return this.field_6002
				.method_17742(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.NONE, this))
				.getType()
			== HitResult.Type.NONE;
	}

	@Override
	public float getYaw(float f) {
		return f == 1.0F ? this.headYaw : MathHelper.lerp(f, this.prevHeadYaw, this.headYaw);
	}

	@Environment(EnvType.CLIENT)
	public float method_6055(float f) {
		float g = this.field_6251 - this.field_6229;
		if (g < 0.0F) {
			g++;
		}

		return this.field_6229 + g * f;
	}

	public boolean method_6034() {
		return !this.field_6002.isClient;
	}

	@Override
	public boolean doesCollide() {
		return !this.invalid;
	}

	@Override
	public boolean isPushable() {
		return this.isValid() && !this.canClimb();
	}

	@Override
	protected void scheduleVelocityUpdate() {
		this.velocityModified = this.random.nextDouble() >= this.method_5996(EntityAttributes.KNOCKBACK_RESISTANCE).getValue();
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

	public abstract OptionMainHand getMainHand();

	public boolean isUsingItem() {
		return (this.field_6011.get(field_6257) & 1) > 0;
	}

	public Hand getActiveHand() {
		return (this.field_6011.get(field_6257) & 2) > 0 ? Hand.OFF : Hand.MAIN;
	}

	protected void method_6076() {
		if (this.isUsingItem()) {
			if (this.method_5998(this.getActiveHand()) == this.field_6277) {
				this.field_6277.method_7949(this.field_6002, this, this.method_6014());
				if (this.method_6014() <= 25 && this.method_6014() % 4 == 0) {
					this.method_6098(this.field_6277, 5);
				}

				if (--this.field_6222 == 0 && !this.field_6002.isClient && !this.field_6277.method_7967()) {
					this.method_6040();
				}
			} else {
				this.method_6021();
			}
		}
	}

	private void method_6072() {
		this.field_6264 = this.field_6243;
		if (this.isSwimming()) {
			this.field_6243 = Math.min(1.0F, this.field_6243 + 0.09F);
		} else {
			this.field_6243 = Math.max(0.0F, this.field_6243 - 0.09F);
		}
	}

	protected void method_6085(int i, boolean bl) {
		int j = this.field_6011.get(field_6257);
		if (bl) {
			j |= i;
		} else {
			j &= ~i;
		}

		this.field_6011.set(field_6257, (byte)j);
	}

	public void setCurrentHand(Hand hand) {
		ItemStack itemStack = this.method_5998(hand);
		if (!itemStack.isEmpty() && !this.isUsingItem()) {
			this.field_6277 = itemStack;
			this.field_6222 = itemStack.getMaxUseTime();
			if (!this.field_6002.isClient) {
				this.method_6085(1, true);
				this.method_6085(2, hand == Hand.OFF);
			}
		}
	}

	@Override
	public void method_5674(TrackedData<?> trackedData) {
		super.method_5674(trackedData);
		if (field_18073.equals(trackedData)) {
			if (this.field_6002.isClient) {
				this.getSleepingPosition().ifPresent(this::method_18392);
			}
		} else if (field_6257.equals(trackedData) && this.field_6002.isClient) {
			if (this.isUsingItem() && this.field_6277.isEmpty()) {
				this.field_6277 = this.method_5998(this.getActiveHand());
				if (!this.field_6277.isEmpty()) {
					this.field_6222 = this.field_6277.getMaxUseTime();
				}
			} else if (!this.isUsingItem() && !this.field_6277.isEmpty()) {
				this.field_6277 = ItemStack.EMPTY;
				this.field_6222 = 0;
			}
		}
	}

	@Override
	public void method_5702(EntityAnchorArgumentType.EntityAnchor entityAnchor, Vec3d vec3d) {
		super.method_5702(entityAnchor, vec3d);
		this.prevHeadYaw = this.headYaw;
		this.field_6283 = this.headYaw;
		this.field_6220 = this.field_6283;
	}

	protected void method_6098(ItemStack itemStack, int i) {
		if (!itemStack.isEmpty() && this.isUsingItem()) {
			if (itemStack.method_7976() == UseAction.field_8946) {
				this.method_5783(this.method_18807(itemStack), 0.5F, this.field_6002.random.nextFloat() * 0.1F + 0.9F);
			}

			if (itemStack.method_7976() == UseAction.field_8950) {
				this.method_6037(itemStack, i);
				this.method_5783(
					this.method_18869(itemStack), 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F
				);
			}
		}
	}

	private void method_6037(ItemStack itemStack, int i) {
		for (int j = 0; j < i; j++) {
			Vec3d vec3d = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
			vec3d = vec3d.rotateX(-this.pitch * (float) (Math.PI / 180.0));
			vec3d = vec3d.rotateY(-this.yaw * (float) (Math.PI / 180.0));
			double d = (double)(-this.random.nextFloat()) * 0.6 - 0.3;
			Vec3d vec3d2 = new Vec3d(((double)this.random.nextFloat() - 0.5) * 0.3, d, 0.6);
			vec3d2 = vec3d2.rotateX(-this.pitch * (float) (Math.PI / 180.0));
			vec3d2 = vec3d2.rotateY(-this.yaw * (float) (Math.PI / 180.0));
			vec3d2 = vec3d2.add(this.x, this.y + (double)this.getStandingEyeHeight(), this.z);
			this.field_6002
				.method_8406(new ItemStackParticleParameters(ParticleTypes.field_11218, itemStack), vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y + 0.05, vec3d.z);
		}
	}

	protected void method_6040() {
		if (!this.field_6277.isEmpty() && this.isUsingItem()) {
			this.method_6098(this.field_6277, 16);
			this.method_6122(this.getActiveHand(), this.field_6277.method_7910(this.field_6002, this));
			this.method_6021();
		}
	}

	public ItemStack method_6030() {
		return this.field_6277;
	}

	public int method_6014() {
		return this.field_6222;
	}

	public int method_6048() {
		return this.isUsingItem() ? this.field_6277.getMaxUseTime() - this.method_6014() : 0;
	}

	public void method_6075() {
		if (!this.field_6277.isEmpty()) {
			this.field_6277.method_7930(this.field_6002, this, this.method_6014());
			if (this.field_6277.method_7967()) {
				this.method_6076();
			}
		}

		this.method_6021();
	}

	public void method_6021() {
		if (!this.field_6002.isClient) {
			this.method_6085(1, false);
		}

		this.field_6277 = ItemStack.EMPTY;
		this.field_6222 = 0;
	}

	public boolean method_6039() {
		if (this.isUsingItem() && !this.field_6277.isEmpty()) {
			Item item = this.field_6277.getItem();
			return item.method_7853(this.field_6277) != UseAction.field_8949 ? false : item.method_7881(this.field_6277) - this.field_6222 >= 5;
		} else {
			return false;
		}
	}

	public boolean isFallFlying() {
		return this.getEntityFlag(7);
	}

	@Environment(EnvType.CLIENT)
	public int method_6003() {
		return this.field_6239;
	}

	public boolean method_6082(double d, double e, double f, boolean bl) {
		double g = this.x;
		double h = this.y;
		double i = this.z;
		this.x = d;
		this.y = e;
		this.z = f;
		boolean bl2 = false;
		BlockPos blockPos = new BlockPos(this);
		IWorld iWorld = this.field_6002;
		Random random = this.getRand();
		if (iWorld.method_8591(blockPos)) {
			boolean bl3 = false;

			while (!bl3 && blockPos.getY() > 0) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState = iWorld.method_8320(blockPos2);
				if (blockState.method_11620().suffocates()) {
					bl3 = true;
				} else {
					this.y--;
					blockPos = blockPos2;
				}
			}

			if (bl3) {
				this.method_5859(this.x, this.y, this.z);
				if (iWorld.method_17892(this) && !iWorld.method_8599(this.method_5829())) {
					bl2 = true;
				}
			}
		}

		if (!bl2) {
			this.method_5859(g, h, i);
			return false;
		} else {
			if (bl) {
				int j = 128;

				for (int k = 0; k < 128; k++) {
					double l = (double)k / 127.0;
					float m = (random.nextFloat() - 0.5F) * 0.2F;
					float n = (random.nextFloat() - 0.5F) * 0.2F;
					float o = (random.nextFloat() - 0.5F) * 0.2F;
					double p = MathHelper.lerp(l, g, this.x) + (random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
					double q = MathHelper.lerp(l, h, this.y) + random.nextDouble() * (double)this.getHeight();
					double r = MathHelper.lerp(l, i, this.z) + (random.nextDouble() - 0.5) * (double)this.getWidth() * 2.0;
					iWorld.method_8406(ParticleTypes.field_11214, p, q, r, (double)m, (double)n, (double)o);
				}
			}

			if (this instanceof MobEntityWithAi) {
				((MobEntityWithAi)this).method_5942().stop();
			}

			return true;
		}
	}

	public boolean method_6086() {
		return true;
	}

	public boolean method_6102() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public void method_6006(BlockPos blockPos, boolean bl) {
	}

	public boolean method_18397(ItemStack itemStack) {
		return false;
	}

	@Override
	public Packet<?> method_18002() {
		return new MobSpawnS2CPacket(this);
	}

	@Override
	public EntitySize method_18377(EntityPose entityPose) {
		return entityPose == EntityPose.field_18078 ? SLEEPING_SIZE : super.method_18377(entityPose).scaled(this.method_17825());
	}

	public Optional<BlockPos> getSleepingPosition() {
		return this.field_6011.get(field_18073);
	}

	public void method_18402(BlockPos blockPos) {
		this.field_6011.set(field_18073, Optional.of(blockPos));
	}

	public void clearSleepingPosition() {
		this.field_6011.set(field_18073, Optional.empty());
	}

	public boolean isSleeping() {
		return this.getSleepingPosition().isPresent();
	}

	public void method_18403(BlockPos blockPos) {
		if (this.hasVehicle()) {
			this.stopRiding();
		}

		BlockState blockState = this.field_6002.method_8320(blockPos);
		if (blockState.getBlock() instanceof BedBlock) {
			this.field_6002.method_8652(blockPos, blockState.method_11657(BedBlock.field_9968, Boolean.valueOf(true)), 3);
		}

		this.method_18380(EntityPose.field_18078);
		this.method_18392(blockPos);
		this.method_18402(blockPos);
		this.method_18799(Vec3d.ZERO);
		this.velocityDirty = true;
	}

	private void method_18392(BlockPos blockPos) {
		this.setPosition((double)blockPos.getX() + 0.5, (double)((float)blockPos.getY() + 0.6875F), (double)blockPos.getZ() + 0.5);
	}

	private boolean isSleepingInBed() {
		return (Boolean)this.getSleepingPosition().map(blockPos -> this.field_6002.method_8320(blockPos).getBlock() instanceof BedBlock).orElse(false);
	}

	public void wakeUp() {
		this.getSleepingPosition().filter(this.field_6002::method_8591).ifPresent(blockPos -> {
			BlockState blockState = this.field_6002.method_8320(blockPos);
			if (blockState.getBlock() instanceof BedBlock) {
				this.field_6002.method_8652(blockPos, blockState.method_11657(BedBlock.field_9968, Boolean.valueOf(false)), 4);
				BlockPos blockPos2 = BedBlock.method_9484(this.field_6002, blockPos, 0);
				if (blockPos2 == null) {
					blockPos2 = blockPos.up();
				}

				this.setPosition((double)((float)blockPos2.getX() + 0.5F), (double)((float)blockPos2.getY() + 0.1F), (double)((float)blockPos2.getZ() + 0.5F));
			}
		});
		this.method_18380(EntityPose.field_18076);
		this.clearSleepingPosition();
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Direction method_18401() {
		BlockPos blockPos = (BlockPos)this.getSleepingPosition().orElse(null);
		return blockPos != null ? BedBlock.method_18476(this.field_6002, blockPos) : null;
	}

	@Override
	public boolean isInsideWall() {
		return !this.isSleeping() && super.isInsideWall();
	}

	@Override
	protected final float method_18378(EntityPose entityPose, EntitySize entitySize) {
		return entityPose == EntityPose.field_18078 ? 0.2F : this.method_18394(entityPose, entitySize);
	}

	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return super.method_18378(entityPose, entitySize);
	}

	public ItemStack method_18808(ItemStack itemStack) {
		return ItemStack.EMPTY;
	}

	public ItemStack method_18866(World world, ItemStack itemStack) {
		if (itemStack.method_19267()) {
			world.method_8465(
				null,
				this.x,
				this.y,
				this.z,
				this.method_18869(itemStack),
				SoundCategory.field_15254,
				1.0F,
				1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F
			);
			this.method_18865(itemStack, world, this);
			itemStack.subtractAmount(1);
		}

		return itemStack;
	}

	private void method_18865(ItemStack itemStack, World world, LivingEntity livingEntity) {
		Item item = itemStack.getItem();
		if (item.method_19263()) {
			for (Pair<StatusEffectInstance, Float> pair : item.method_19264().method_19235()) {
				if (!world.isClient && pair.getLeft() != null && world.random.nextFloat() < pair.getRight()) {
					livingEntity.addPotionEffect(new StatusEffectInstance(pair.getLeft()));
				}
			}
		}
	}
}
