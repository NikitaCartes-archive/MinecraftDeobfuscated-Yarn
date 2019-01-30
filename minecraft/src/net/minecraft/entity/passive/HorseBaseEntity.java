package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1394;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.config.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class HorseBaseEntity extends AnimalEntity implements InventoryListener, JumpingMount {
	private static final Predicate<Entity> field_6956 = entity -> entity instanceof HorseBaseEntity && ((HorseBaseEntity)entity).isBred();
	protected static final EntityAttribute ATTR_JUMP_STRENGTH = new ClampedEntityAttribute(null, "horse.jumpStrength", 0.7, 0.0, 2.0)
		.setName("Jump Strength")
		.method_6212(true);
	private static final TrackedData<Byte> HORSE_FLAGS = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(HorseBaseEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private int field_6971;
	private int field_6973;
	private int field_6970;
	public int field_6957;
	public int field_6958;
	protected boolean field_6968;
	protected BasicInventory decorationItem;
	protected int temper;
	protected float field_6976;
	private boolean field_6960;
	private float field_6969;
	private float field_6966;
	private float field_6967;
	private float field_6963;
	private float field_6965;
	private float field_6961;
	protected boolean field_6964 = true;
	protected int field_6975;

	protected HorseBaseEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.stepHeight = 1.0F;
		this.method_6721();
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
		this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.0, HorseBaseEntity.class));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.0));
		this.goalSelector.add(6, new class_1394(this, 0.7));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.method_6764();
	}

	protected void method_6764() {
		this.goalSelector.add(0, new SwimGoal(this));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(HORSE_FLAGS, (byte)0);
		this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
	}

	protected boolean getHorseFlag(int i) {
		return (this.dataTracker.get(HORSE_FLAGS) & i) != 0;
	}

	protected void setHorseFlag(int i, boolean bl) {
		byte b = this.dataTracker.get(HORSE_FLAGS);
		if (bl) {
			this.dataTracker.set(HORSE_FLAGS, (byte)(b | i));
		} else {
			this.dataTracker.set(HORSE_FLAGS, (byte)(b & ~i));
		}
	}

	public boolean isTame() {
		return this.getHorseFlag(2);
	}

	@Nullable
	public UUID getOwnerUuid() {
		return (UUID)this.dataTracker.get(OWNER_UUID).orElse(null);
	}

	public void setOwnerUuid(@Nullable UUID uUID) {
		this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uUID));
	}

	public boolean method_6763() {
		return this.field_6968;
	}

	public void setTame(boolean bl) {
		this.setHorseFlag(2, bl);
	}

	public void method_6758(boolean bl) {
		this.field_6968 = bl;
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return super.canBeLeashedBy(playerEntity) && this.getGroup() != EntityGroup.UNDEAD;
	}

	@Override
	protected void method_6142(float f) {
		if (f > 6.0F && this.isEating()) {
			this.setEating(false);
		}
	}

	public boolean isEating() {
		return this.getHorseFlag(16);
	}

	public boolean method_6736() {
		return this.getHorseFlag(32);
	}

	public boolean isBred() {
		return this.getHorseFlag(8);
	}

	public void setBred(boolean bl) {
		this.setHorseFlag(8, bl);
	}

	public void setSaddled(boolean bl) {
		this.setHorseFlag(4, bl);
	}

	public int getTemper() {
		return this.temper;
	}

	public void setTemper(int i) {
		this.temper = i;
	}

	public int method_6745(int i) {
		int j = MathHelper.clamp(this.getTemper() + i, 0, this.method_6755());
		this.setTemper(j);
		return j;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		Entity entity = damageSource.getAttacker();
		return this.hasPassengers() && entity != null && this.method_5821(entity) ? false : super.damage(damageSource, f);
	}

	@Override
	public boolean isPushable() {
		return !this.hasPassengers();
	}

	private void method_6733() {
		this.method_6738();
		if (!this.isSilent()) {
			this.world
				.playSound(
					null, this.x, this.y, this.z, SoundEvents.field_15099, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
				);
		}
	}

	@Override
	public void handleFallDamage(float f, float g) {
		if (f > 1.0F) {
			this.playSound(SoundEvents.field_14783, 0.4F, 1.0F);
		}

		int i = MathHelper.ceil((f * 0.5F - 3.0F) * g);
		if (i > 0) {
			this.damage(DamageSource.FALL, (float)i);
			if (this.hasPassengers()) {
				for (Entity entity : this.method_5736()) {
					entity.damage(DamageSource.FALL, (float)i);
				}
			}

			BlockState blockState = this.world.getBlockState(new BlockPos(this.x, this.y - 0.2 - (double)this.prevYaw, this.z));
			if (!blockState.isAir() && !this.isSilent()) {
				BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
				this.world
					.playSound(
						null,
						this.x,
						this.y,
						this.z,
						blockSoundGroup.getStepSound(),
						this.getSoundCategory(),
						blockSoundGroup.getVolume() * 0.5F,
						blockSoundGroup.getPitch() * 0.75F
					);
			}
		}
	}

	protected int getInventorySize() {
		return 2;
	}

	protected void method_6721() {
		BasicInventory basicInventory = this.decorationItem;
		this.decorationItem = new BasicInventory(this.getInventorySize());
		if (basicInventory != null) {
			basicInventory.removeListener(this);
			int i = Math.min(basicInventory.getInvSize(), this.decorationItem.getInvSize());

			for (int j = 0; j < i; j++) {
				ItemStack itemStack = basicInventory.getInvStack(j);
				if (!itemStack.isEmpty()) {
					this.decorationItem.setInvStack(j, itemStack.copy());
				}
			}
		}

		this.decorationItem.addListener(this);
		this.method_6731();
	}

	protected void method_6731() {
		if (!this.world.isClient) {
			this.setSaddled(!this.decorationItem.getInvStack(0).isEmpty() && this.method_6765());
		}
	}

	@Override
	public void onInvChange(Inventory inventory) {
		boolean bl = this.isSaddled();
		this.method_6731();
		if (this.age > 20 && !bl && this.isSaddled()) {
			this.playSound(SoundEvents.field_14704, 0.5F, 1.0F);
		}
	}

	@Nullable
	protected HorseBaseEntity method_6756(Entity entity, double d) {
		double e = Double.MAX_VALUE;
		Entity entity2 = null;

		for (Entity entity3 : this.world.getEntities(entity, entity.getBoundingBox().stretch(d, d, d), field_6956)) {
			double f = entity3.squaredDistanceTo(entity.x, entity.y, entity.z);
			if (f < e) {
				entity2 = entity3;
				e = f;
			}
		}

		return (HorseBaseEntity)entity2;
	}

	public double method_6771() {
		return this.getAttributeInstance(ATTR_JUMP_STRENGTH).getValue();
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return null;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		if (this.random.nextInt(3) == 0) {
			this.method_6748();
		}

		return null;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (this.random.nextInt(10) == 0 && !this.method_6062()) {
			this.method_6748();
		}

		return null;
	}

	public boolean method_6765() {
		return true;
	}

	public boolean isSaddled() {
		return this.getHorseFlag(4);
	}

	@Nullable
	protected SoundEvent method_6747() {
		this.method_6748();
		return null;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		if (!blockState.getMaterial().isLiquid()) {
			BlockState blockState2 = this.world.getBlockState(blockPos.up());
			BlockSoundGroup blockSoundGroup = blockState.getSoundGroup();
			if (blockState2.getBlock() == Blocks.field_10477) {
				blockSoundGroup = blockState2.getSoundGroup();
			}

			if (this.hasPassengers() && this.field_6964) {
				this.field_6975++;
				if (this.field_6975 > 5 && this.field_6975 % 3 == 0) {
					this.method_6761(blockSoundGroup);
				} else if (this.field_6975 <= 5) {
					this.playSound(SoundEvents.field_15061, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
				}
			} else if (blockSoundGroup == BlockSoundGroup.WOOD) {
				this.playSound(SoundEvents.field_15061, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
			} else {
				this.playSound(SoundEvents.field_14613, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
			}
		}
	}

	protected void method_6761(BlockSoundGroup blockSoundGroup) {
		this.playSound(SoundEvents.field_14987, blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeContainer().register(ATTR_JUMP_STRENGTH);
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(53.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.225F);
	}

	@Override
	public int getLimitPerChunk() {
		return 6;
	}

	public int method_6755() {
		return 100;
	}

	@Override
	protected float getSoundVolume() {
		return 0.8F;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 400;
	}

	public void method_6722(PlayerEntity playerEntity) {
		if (!this.world.isClient && (!this.hasPassengers() || this.hasPassenger(playerEntity)) && this.isTame()) {
			playerEntity.openHorseInventory(this, this.decorationItem);
		}
	}

	protected boolean method_6742(PlayerEntity playerEntity, ItemStack itemStack) {
		boolean bl = false;
		float f = 0.0F;
		int i = 0;
		int j = 0;
		Item item = itemStack.getItem();
		if (item == Items.field_8861) {
			f = 2.0F;
			i = 20;
			j = 3;
		} else if (item == Items.field_8479) {
			f = 1.0F;
			i = 30;
			j = 3;
		} else if (item == Blocks.field_10359.getItem()) {
			f = 20.0F;
			i = 180;
		} else if (item == Items.field_8279) {
			f = 3.0F;
			i = 60;
			j = 3;
		} else if (item == Items.field_8071) {
			f = 4.0F;
			i = 60;
			j = 5;
			if (this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
				bl = true;
				this.method_6480(playerEntity);
			}
		} else if (item == Items.field_8463 || item == Items.field_8367) {
			f = 10.0F;
			i = 240;
			j = 10;
			if (this.isTame() && this.getBreedingAge() == 0 && !this.isInLove()) {
				bl = true;
				this.method_6480(playerEntity);
			}
		}

		if (this.getHealth() < this.getHealthMaximum() && f > 0.0F) {
			this.heal(f);
			bl = true;
		}

		if (this.isChild() && i > 0) {
			this.world
				.addParticle(
					ParticleTypes.field_11211,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					0.0,
					0.0,
					0.0
				);
			if (!this.world.isClient) {
				this.method_5615(i);
			}

			bl = true;
		}

		if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.method_6755()) {
			bl = true;
			if (!this.world.isClient) {
				this.method_6745(j);
			}
		}

		if (bl) {
			this.method_6733();
		}

		return bl;
	}

	protected void method_6726(PlayerEntity playerEntity) {
		this.setEating(false);
		this.method_6737(false);
		if (!this.world.isClient) {
			playerEntity.yaw = this.yaw;
			playerEntity.pitch = this.pitch;
			playerEntity.startRiding(this);
		}
	}

	@Override
	protected boolean method_6062() {
		return super.method_6062() && this.hasPassengers() && this.isSaddled() || this.isEating() || this.method_6736();
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return false;
	}

	private void method_6759() {
		this.field_6957 = 1;
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (this.decorationItem != null) {
			for (int i = 0; i < this.decorationItem.getInvSize(); i++) {
				ItemStack itemStack = this.decorationItem.getInvStack(i);
				if (!itemStack.isEmpty()) {
					this.dropStack(itemStack);
				}
			}
		}
	}

	@Override
	public void updateMovement() {
		if (this.random.nextInt(200) == 0) {
			this.method_6759();
		}

		super.updateMovement();
		if (!this.world.isClient) {
			if (this.random.nextInt(900) == 0 && this.deathCounter == 0) {
				this.heal(1.0F);
			}

			if (this.method_6762()) {
				if (!this.isEating()
					&& !this.hasPassengers()
					&& this.random.nextInt(300) == 0
					&& this.world.getBlockState(new BlockPos(this).down()).getBlock() == Blocks.field_10219) {
					this.setEating(true);
				}

				if (this.isEating() && ++this.field_6971 > 50) {
					this.field_6971 = 0;
					this.setEating(false);
				}
			}

			this.method_6746();
		}
	}

	protected void method_6746() {
		if (this.isBred() && this.isChild() && !this.isEating()) {
			HorseBaseEntity horseBaseEntity = this.method_6756(this, 16.0);
			if (horseBaseEntity != null && this.squaredDistanceTo(horseBaseEntity) > 4.0) {
				this.navigation.findPathTo(horseBaseEntity);
			}
		}
	}

	public boolean method_6762() {
		return true;
	}

	@Override
	public void update() {
		super.update();
		if (this.field_6973 > 0 && ++this.field_6973 > 30) {
			this.field_6973 = 0;
			this.setHorseFlag(64, false);
		}

		if ((this.method_5787() || this.method_6034()) && this.field_6970 > 0 && ++this.field_6970 > 20) {
			this.field_6970 = 0;
			this.method_6737(false);
		}

		if (this.field_6957 > 0 && ++this.field_6957 > 8) {
			this.field_6957 = 0;
		}

		if (this.field_6958 > 0) {
			this.field_6958++;
			if (this.field_6958 > 300) {
				this.field_6958 = 0;
			}
		}

		this.field_6966 = this.field_6969;
		if (this.isEating()) {
			this.field_6969 = this.field_6969 + (1.0F - this.field_6969) * 0.4F + 0.05F;
			if (this.field_6969 > 1.0F) {
				this.field_6969 = 1.0F;
			}
		} else {
			this.field_6969 = this.field_6969 + ((0.0F - this.field_6969) * 0.4F - 0.05F);
			if (this.field_6969 < 0.0F) {
				this.field_6969 = 0.0F;
			}
		}

		this.field_6963 = this.field_6967;
		if (this.method_6736()) {
			this.field_6969 = 0.0F;
			this.field_6966 = this.field_6969;
			this.field_6967 = this.field_6967 + (1.0F - this.field_6967) * 0.4F + 0.05F;
			if (this.field_6967 > 1.0F) {
				this.field_6967 = 1.0F;
			}
		} else {
			this.field_6960 = false;
			this.field_6967 = this.field_6967 + ((0.8F * this.field_6967 * this.field_6967 * this.field_6967 - this.field_6967) * 0.6F - 0.05F);
			if (this.field_6967 < 0.0F) {
				this.field_6967 = 0.0F;
			}
		}

		this.field_6961 = this.field_6965;
		if (this.getHorseFlag(64)) {
			this.field_6965 = this.field_6965 + (1.0F - this.field_6965) * 0.7F + 0.05F;
			if (this.field_6965 > 1.0F) {
				this.field_6965 = 1.0F;
			}
		} else {
			this.field_6965 = this.field_6965 + ((0.0F - this.field_6965) * 0.7F - 0.05F);
			if (this.field_6965 < 0.0F) {
				this.field_6965 = 0.0F;
			}
		}
	}

	private void method_6738() {
		if (!this.world.isClient) {
			this.field_6973 = 1;
			this.setHorseFlag(64, true);
		}
	}

	public void setEating(boolean bl) {
		this.setHorseFlag(16, bl);
	}

	public void method_6737(boolean bl) {
		if (bl) {
			this.setEating(false);
		}

		this.setHorseFlag(32, bl);
	}

	private void method_6748() {
		if (this.method_5787() || this.method_6034()) {
			this.field_6970 = 1;
			this.method_6737(true);
		}
	}

	public void method_6757() {
		this.method_6748();
		SoundEvent soundEvent = this.method_6747();
		if (soundEvent != null) {
			this.playSound(soundEvent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	public boolean method_6752(PlayerEntity playerEntity) {
		this.setOwnerUuid(playerEntity.getUuid());
		this.setTame(true);
		if (playerEntity instanceof ServerPlayerEntity) {
			Criterions.TAME_ANIMAL.handle((ServerPlayerEntity)playerEntity, this);
		}

		this.world.summonParticle(this, (byte)7);
		return true;
	}

	@Override
	public void method_6091(float f, float g, float h) {
		if (this.hasPassengers() && this.method_5956() && this.isSaddled()) {
			LivingEntity livingEntity = (LivingEntity)this.getPrimaryPassenger();
			this.yaw = livingEntity.yaw;
			this.prevYaw = this.yaw;
			this.pitch = livingEntity.pitch * 0.5F;
			this.setRotation(this.yaw, this.pitch);
			this.field_6283 = this.yaw;
			this.headYaw = this.field_6283;
			f = livingEntity.field_6212 * 0.5F;
			h = livingEntity.field_6250;
			if (h <= 0.0F) {
				h *= 0.25F;
				this.field_6975 = 0;
			}

			if (this.onGround && this.field_6976 == 0.0F && this.method_6736() && !this.field_6960) {
				f = 0.0F;
				h = 0.0F;
			}

			if (this.field_6976 > 0.0F && !this.method_6763() && this.onGround) {
				this.velocityY = this.method_6771() * (double)this.field_6976;
				if (this.hasPotionEffect(StatusEffects.field_5913)) {
					this.velocityY = this.velocityY + (double)((float)(this.getPotionEffect(StatusEffects.field_5913).getAmplifier() + 1) * 0.1F);
				}

				this.method_6758(true);
				this.velocityDirty = true;
				if (h > 0.0F) {
					float i = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0));
					float j = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
					this.velocityX = this.velocityX + (double)(-0.4F * i * this.field_6976);
					this.velocityZ = this.velocityZ + (double)(0.4F * j * this.field_6976);
					this.method_6723();
				}

				this.field_6976 = 0.0F;
			}

			this.field_6281 = this.method_6029() * 0.1F;
			if (this.method_5787()) {
				this.method_6125((float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
				super.method_6091(f, g, h);
			} else if (livingEntity instanceof PlayerEntity) {
				this.velocityX = 0.0;
				this.velocityY = 0.0;
				this.velocityZ = 0.0;
			}

			if (this.onGround) {
				this.field_6976 = 0.0F;
				this.method_6758(false);
			}

			this.field_6211 = this.field_6225;
			double d = this.x - this.prevX;
			double e = this.z - this.prevZ;
			float k = MathHelper.sqrt(d * d + e * e) * 4.0F;
			if (k > 1.0F) {
				k = 1.0F;
			}

			this.field_6225 = this.field_6225 + (k - this.field_6225) * 0.4F;
			this.field_6249 = this.field_6249 + this.field_6225;
		} else {
			this.field_6281 = 0.02F;
			super.method_6091(f, g, h);
		}
	}

	protected void method_6723() {
		this.playSound(SoundEvents.field_14831, 0.4F, 1.0F);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("EatingHaystack", this.isEating());
		compoundTag.putBoolean("Bred", this.isBred());
		compoundTag.putInt("Temper", this.getTemper());
		compoundTag.putBoolean("Tame", this.isTame());
		if (this.getOwnerUuid() != null) {
			compoundTag.putString("OwnerUUID", this.getOwnerUuid().toString());
		}

		if (!this.decorationItem.getInvStack(0).isEmpty()) {
			compoundTag.put("SaddleItem", this.decorationItem.getInvStack(0).toTag(new CompoundTag()));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setEating(compoundTag.getBoolean("EatingHaystack"));
		this.setBred(compoundTag.getBoolean("Bred"));
		this.setTemper(compoundTag.getInt("Temper"));
		this.setTame(compoundTag.getBoolean("Tame"));
		String string;
		if (compoundTag.containsKey("OwnerUUID", 8)) {
			string = compoundTag.getString("OwnerUUID");
		} else {
			String string2 = compoundTag.getString("Owner");
			string = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string2);
		}

		if (!string.isEmpty()) {
			this.setOwnerUuid(UUID.fromString(string));
		}

		EntityAttributeInstance entityAttributeInstance = this.getAttributeContainer().get("Speed");
		if (entityAttributeInstance != null) {
			this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(entityAttributeInstance.getBaseValue() * 0.25);
		}

		if (compoundTag.containsKey("SaddleItem", 10)) {
			ItemStack itemStack = ItemStack.fromTag(compoundTag.getCompound("SaddleItem"));
			if (itemStack.getItem() == Items.field_8175) {
				this.decorationItem.setInvStack(0, itemStack);
			}
		}

		this.method_6731();
	}

	@Override
	public boolean canBreedWith(AnimalEntity animalEntity) {
		return false;
	}

	protected boolean method_6734() {
		return !this.hasPassengers() && !this.hasVehicle() && this.isTame() && !this.isChild() && this.getHealth() >= this.getHealthMaximum() && this.isInLove();
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		return null;
	}

	protected void method_6743(PassiveEntity passiveEntity, HorseBaseEntity horseBaseEntity) {
		double d = this.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue()
			+ passiveEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH).getBaseValue()
			+ (double)this.method_6754();
		horseBaseEntity.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(d / 3.0);
		double e = this.getAttributeInstance(ATTR_JUMP_STRENGTH).getBaseValue()
			+ passiveEntity.getAttributeInstance(ATTR_JUMP_STRENGTH).getBaseValue()
			+ this.method_6774();
		horseBaseEntity.getAttributeInstance(ATTR_JUMP_STRENGTH).setBaseValue(e / 3.0);
		double f = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue()
			+ passiveEntity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue()
			+ this.method_6728();
		horseBaseEntity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(f / 3.0);
	}

	@Override
	public boolean method_5956() {
		return this.getPrimaryPassenger() instanceof LivingEntity;
	}

	@Environment(EnvType.CLIENT)
	public float method_6739(float f) {
		return MathHelper.lerp(f, this.field_6966, this.field_6969);
	}

	@Environment(EnvType.CLIENT)
	public float method_6767(float f) {
		return MathHelper.lerp(f, this.field_6963, this.field_6967);
	}

	@Environment(EnvType.CLIENT)
	public float method_6772(float f) {
		return MathHelper.lerp(f, this.field_6961, this.field_6965);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setJumpStrength(int i) {
		if (this.isSaddled()) {
			if (i < 0) {
				i = 0;
			} else {
				this.field_6960 = true;
				this.method_6748();
			}

			if (i >= 90) {
				this.field_6976 = 1.0F;
			} else {
				this.field_6976 = 0.4F + 0.4F * (float)i / 90.0F;
			}
		}
	}

	@Override
	public boolean canJump() {
		return this.isSaddled();
	}

	@Override
	public void startJumping(int i) {
		this.field_6960 = true;
		this.method_6748();
	}

	@Override
	public void stopJumping() {
	}

	@Environment(EnvType.CLIENT)
	protected void method_6760(boolean bl) {
		ParticleParameters particleParameters = bl ? ParticleTypes.field_11201 : ParticleTypes.field_11251;

		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world
				.addParticle(
					particleParameters,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 0.5 + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					d,
					e,
					f
				);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 7) {
			this.method_6760(true);
		} else if (b == 6) {
			this.method_6760(false);
		} else {
			super.method_5711(b);
		}
	}

	@Override
	public void method_5865(Entity entity) {
		super.method_5865(entity);
		if (entity instanceof MobEntity) {
			MobEntity mobEntity = (MobEntity)entity;
			this.field_6283 = mobEntity.field_6283;
		}

		if (this.field_6963 > 0.0F) {
			float f = MathHelper.sin(this.field_6283 * (float) (Math.PI / 180.0));
			float g = MathHelper.cos(this.field_6283 * (float) (Math.PI / 180.0));
			float h = 0.7F * this.field_6963;
			float i = 0.15F * this.field_6963;
			entity.setPosition(this.x + (double)(h * f), this.y + this.getMountedHeightOffset() + entity.getHeightOffset() + (double)i, this.z - (double)(h * g));
			if (entity instanceof LivingEntity) {
				((LivingEntity)entity).field_6283 = this.field_6283;
			}
		}
	}

	protected float method_6754() {
		return 15.0F + (float)this.random.nextInt(8) + (float)this.random.nextInt(9);
	}

	protected double method_6774() {
		return 0.4F + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2 + this.random.nextDouble() * 0.2;
	}

	protected double method_6728() {
		return (0.45F + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3 + this.random.nextDouble() * 0.3) * 0.25;
	}

	@Override
	public boolean canClimb() {
		return false;
	}

	@Override
	public float getEyeHeight() {
		return this.getHeight();
	}

	public boolean method_6735() {
		return false;
	}

	public boolean method_6773(ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean method_5758(int i, ItemStack itemStack) {
		int j = i - 400;
		if (j >= 0 && j < 2 && j < this.decorationItem.getInvSize()) {
			if (j == 0 && itemStack.getItem() != Items.field_8175) {
				return false;
			} else if (j != 1 || this.method_6735() && this.method_6773(itemStack)) {
				this.decorationItem.setInvStack(j, itemStack);
				this.method_6731();
				return true;
			} else {
				return false;
			}
		} else {
			int k = i - 500 + 2;
			if (k >= 2 && k < this.decorationItem.getInvSize()) {
				this.decorationItem.setInvStack(k, itemStack);
				return true;
			} else {
				return false;
			}
		}
	}

	@Nullable
	@Override
	public Entity getPrimaryPassenger() {
		return this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		if (this.random.nextInt(5) == 0) {
			this.setBreedingAge(-24000);
		}

		return entityData;
	}
}
