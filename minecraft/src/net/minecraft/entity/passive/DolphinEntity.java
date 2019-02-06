package net.minecraft.entity.passive;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1378;
import net.minecraft.class_1399;
import net.minecraft.class_1414;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.WaterCreatureEntity;
import net.minecraft.entity.ai.control.DolphinLookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.BreatheAirGoal;
import net.minecraft.entity.ai.goal.ChaseBoatGoal;
import net.minecraft.entity.ai.goal.DolphinJumpGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveIntoWaterGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;

public class DolphinEntity extends WaterCreatureEntity {
	private static final TrackedData<BlockPos> field_6747 = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
	private static final TrackedData<Boolean> field_6750 = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_6749 = DataTracker.registerData(DolphinEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final Predicate<ItemEntity> field_6748 = itemEntity -> !itemEntity.cannotPickup() && itemEntity.isValid() && itemEntity.isInsideWater();

	public DolphinEntity(World world) {
		super(EntityType.DOLPHIN, world);
		this.moveControl = new DolphinEntity.DolphinMoveControl(this);
		this.lookControl = new DolphinLookControl(this, 10);
		this.setCanPickUpLoot(true);
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.setBreath(this.getMaxBreath());
		this.pitch = 0.0F;
		return super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Override
	public boolean canBreatheInWater() {
		return false;
	}

	@Override
	protected void method_6673(int i) {
	}

	public void method_6493(BlockPos blockPos) {
		this.dataTracker.set(field_6747, blockPos);
	}

	public BlockPos method_6494() {
		return this.dataTracker.get(field_6747);
	}

	public boolean method_6487() {
		return this.dataTracker.get(field_6750);
	}

	public void method_6486(boolean bl) {
		this.dataTracker.set(field_6750, bl);
	}

	public int method_6491() {
		return this.dataTracker.get(field_6749);
	}

	public void method_6489(int i) {
		this.dataTracker.set(field_6749, i);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_6747, BlockPos.ORIGIN);
		this.dataTracker.startTracking(field_6750, false);
		this.dataTracker.startTracking(field_6749, 2400);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("TreasurePosX", this.method_6494().getX());
		compoundTag.putInt("TreasurePosY", this.method_6494().getY());
		compoundTag.putInt("TreasurePosZ", this.method_6494().getZ());
		compoundTag.putBoolean("GotFish", this.method_6487());
		compoundTag.putInt("Moistness", this.method_6491());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		int i = compoundTag.getInt("TreasurePosX");
		int j = compoundTag.getInt("TreasurePosY");
		int k = compoundTag.getInt("TreasurePosZ");
		this.method_6493(new BlockPos(i, j, k));
		super.readCustomDataFromTag(compoundTag);
		this.method_6486(compoundTag.getBoolean("GotFish"));
		this.method_6489(compoundTag.getInt("Moistness"));
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new BreatheAirGoal(this));
		this.goalSelector.add(0, new MoveIntoWaterGoal(this));
		this.goalSelector.add(1, new DolphinEntity.class_1435(this));
		this.goalSelector.add(2, new DolphinEntity.class_1436(this, 4.0));
		this.goalSelector.add(4, new class_1378(this, 1.0, 10));
		this.goalSelector.add(4, new LookAroundGoal(this));
		this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(5, new DolphinJumpGoal(this, 10));
		this.goalSelector.add(6, new MeleeAttackGoal(this, 1.2F, true));
		this.goalSelector.add(8, new DolphinEntity.class_1437());
		this.goalSelector.add(8, new ChaseBoatGoal(this));
		this.goalSelector.add(9, new FleeEntityGoal(this, GuardianEntity.class, 8.0F, 1.0, 1.0));
		this.targetSelector.add(1, new class_1399(this, GuardianEntity.class).method_6318());
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(1.2F);
		this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SwimNavigation(this, world);
	}

	@Override
	public boolean method_6121(Entity entity) {
		boolean bl = entity.damage(DamageSource.mob(this), (float)((int)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue()));
		if (bl) {
			this.method_5723(this, entity);
			this.playSound(SoundEvents.field_14992, 1.0F, 1.0F);
		}

		return bl;
	}

	@Override
	public int getMaxBreath() {
		return 4800;
	}

	@Override
	protected int method_6064(int i) {
		return this.getMaxBreath();
	}

	@Override
	public float getEyeHeight() {
		return 0.3F;
	}

	@Override
	public int method_5978() {
		return 1;
	}

	@Override
	public int method_5986() {
		return 1;
	}

	@Override
	protected boolean canStartRiding(Entity entity) {
		return true;
	}

	@Override
	protected void pickupItem(ItemEntity itemEntity) {
		if (this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty()) {
			ItemStack itemStack = itemEntity.getStack();
			if (this.method_5939(itemStack)) {
				this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack);
				this.handDropChances[EquipmentSlot.HAND_MAIN.getEntitySlotId()] = 2.0F;
				this.method_6103(itemEntity, itemStack.getAmount());
				itemEntity.invalidate();
			}
		}
	}

	@Override
	public void update() {
		super.update();
		if (!this.isAiDisabled()) {
			if (this.isTouchingWater()) {
				this.method_6489(2400);
			} else {
				this.method_6489(this.method_6491() - 1);
				if (this.method_6491() <= 0) {
					this.damage(DamageSource.DRYOUT, 1.0F);
				}

				if (this.onGround) {
					this.velocityY += 0.5;
					this.velocityX = this.velocityX + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F);
					this.velocityZ = this.velocityZ + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F);
					this.yaw = this.random.nextFloat() * 360.0F;
					this.onGround = false;
					this.velocityDirty = true;
				}
			}

			if (this.world.isClient
				&& this.isInsideWater()
				&& this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ > 0.03) {
				Vec3d vec3d = this.getRotationVec(0.0F);
				float f = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * 0.3F;
				float g = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * 0.3F;
				float h = 1.2F - this.random.nextFloat() * 0.7F;

				for (int i = 0; i < 2; i++) {
					this.world
						.addParticle(
							ParticleTypes.field_11222, this.x - vec3d.x * (double)h + (double)f, this.y - vec3d.y, this.z - vec3d.z * (double)h + (double)g, 0.0, 0.0, 0.0
						);
					this.world
						.addParticle(
							ParticleTypes.field_11222, this.x - vec3d.x * (double)h - (double)f, this.y - vec3d.y, this.z - vec3d.z * (double)h - (double)g, 0.0, 0.0, 0.0
						);
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 38) {
			this.method_6492(ParticleTypes.field_11211);
		} else {
			super.method_5711(b);
		}
	}

	@Environment(EnvType.CLIENT)
	private void method_6492(ParticleParameters particleParameters) {
		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.01;
			double e = this.random.nextGaussian() * 0.01;
			double f = this.random.nextGaussian() * 0.01;
			this.world
				.addParticle(
					particleParameters,
					this.x + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					this.y + 0.2F + (double)(this.random.nextFloat() * this.getHeight()),
					this.z + (double)(this.random.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(),
					d,
					e,
					f
				);
		}
	}

	@Override
	protected boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (!itemStack.isEmpty() && itemStack.getItem().matches(ItemTags.field_15527)) {
			if (!this.world.isClient) {
				this.playSound(SoundEvents.field_14590, 1.0F, 1.0F);
			}

			this.method_6486(true);
			if (!playerEntity.abilities.creativeMode) {
				itemStack.subtractAmount(1);
			}

			return true;
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		return this.y > 45.0 && this.y < (double)iWorld.getSeaLevel() && iWorld.getBiome(new BlockPos(this)) != Biomes.field_9423
			|| iWorld.getBiome(new BlockPos(this)) != Biomes.field_9446 && super.canSpawn(iWorld, spawnType);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15216;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15101;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return this.isInsideWater() ? SoundEvents.field_14881 : SoundEvents.field_14799;
	}

	@Override
	protected SoundEvent getSoundSplash() {
		return SoundEvents.field_14887;
	}

	@Override
	protected SoundEvent getSoundSwim() {
		return SoundEvents.field_15172;
	}

	protected boolean method_6484() {
		BlockPos blockPos = this.getNavigation().getTargetPos();
		return blockPos != null ? this.squaredDistanceTo(blockPos) < 144.0 : false;
	}

	@Override
	public void method_6091(float f, float g, float h) {
		if (this.method_6034() && this.isInsideWater()) {
			this.method_5724(f, g, h, this.method_6029());
			this.move(MovementType.field_6308, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.9F;
			this.velocityY *= 0.9F;
			this.velocityZ *= 0.9F;
			if (this.getTarget() == null) {
				this.velocityY -= 0.005;
			}
		} else {
			super.method_6091(f, g, h);
		}
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return true;
	}

	static class DolphinMoveControl extends MoveControl {
		private final DolphinEntity dolphin;

		public DolphinMoveControl(DolphinEntity dolphinEntity) {
			super(dolphinEntity);
			this.dolphin = dolphinEntity;
		}

		@Override
		public void tick() {
			if (this.dolphin.isInsideWater()) {
				this.dolphin.velocityY += 0.005;
			}

			if (this.state == MoveControl.State.field_6378 && !this.dolphin.getNavigation().isIdle()) {
				double d = this.targetX - this.dolphin.x;
				double e = this.targetY - this.dolphin.y;
				double f = this.targetZ - this.dolphin.z;
				double g = d * d + e * e + f * f;
				if (g < 2.5000003E-7F) {
					this.entity.method_5930(0.0F);
				} else {
					float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
					this.dolphin.yaw = this.method_6238(this.dolphin.yaw, h, 10.0F);
					this.dolphin.field_6283 = this.dolphin.yaw;
					this.dolphin.headYaw = this.dolphin.yaw;
					float i = (float)(this.speed * this.dolphin.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
					if (this.dolphin.isInsideWater()) {
						this.dolphin.method_6125(i * 0.02F);
						float j = -((float)(MathHelper.atan2(e, (double)MathHelper.sqrt(d * d + f * f)) * 180.0F / (float)Math.PI));
						j = MathHelper.clamp(MathHelper.wrapDegrees(j), -85.0F, 85.0F);
						this.dolphin.pitch = this.method_6238(this.dolphin.pitch, j, 5.0F);
						float k = MathHelper.cos(this.dolphin.pitch * (float) (Math.PI / 180.0));
						float l = MathHelper.sin(this.dolphin.pitch * (float) (Math.PI / 180.0));
						this.dolphin.field_6250 = k * i;
						this.dolphin.field_6227 = -l * i;
					} else {
						this.dolphin.method_6125(i * 0.1F);
					}
				}
			} else {
				this.dolphin.method_6125(0.0F);
				this.dolphin.method_5938(0.0F);
				this.dolphin.method_5976(0.0F);
				this.dolphin.method_5930(0.0F);
			}
		}
	}

	static class class_1435 extends Goal {
		private final DolphinEntity field_6752;
		private boolean field_6753;

		class_1435(DolphinEntity dolphinEntity) {
			this.field_6752 = dolphinEntity;
			this.setControlBits(3);
		}

		@Override
		public boolean canStop() {
			return false;
		}

		@Override
		public boolean canStart() {
			return this.field_6752.method_6487() && this.field_6752.getBreath() >= 100;
		}

		@Override
		public boolean shouldContinue() {
			BlockPos blockPos = this.field_6752.method_6494();
			return this.field_6752.squaredDistanceTo(new BlockPos((double)blockPos.getX(), this.field_6752.y, (double)blockPos.getZ())) > 16.0
				&& !this.field_6753
				&& this.field_6752.getBreath() >= 100;
		}

		@Override
		public void start() {
			this.field_6753 = false;
			this.field_6752.getNavigation().stop();
			World world = this.field_6752.world;
			BlockPos blockPos = new BlockPos(this.field_6752);
			String string = (double)world.random.nextFloat() >= 0.5 ? "Ocean_Ruin" : "Shipwreck";
			BlockPos blockPos2 = world.locateStructure(string, blockPos, 50, false);
			if (blockPos2 == null) {
				BlockPos blockPos3 = world.locateStructure(string.equals("Ocean_Ruin") ? "Shipwreck" : "Ocean_Ruin", blockPos, 50, false);
				if (blockPos3 == null) {
					this.field_6753 = true;
					return;
				}

				this.field_6752.method_6493(blockPos3);
			} else {
				this.field_6752.method_6493(blockPos2);
			}

			world.summonParticle(this.field_6752, (byte)38);
		}

		@Override
		public void onRemove() {
			BlockPos blockPos = this.field_6752.method_6494();
			if (this.field_6752.squaredDistanceTo(new BlockPos((double)blockPos.getX(), this.field_6752.y, (double)blockPos.getZ())) <= 16.0 || this.field_6753) {
				this.field_6752.method_6486(false);
			}
		}

		@Override
		public void tick() {
			BlockPos blockPos = this.field_6752.method_6494();
			World world = this.field_6752.world;
			if (this.field_6752.method_6484() || this.field_6752.getNavigation().isIdle()) {
				Vec3d vec3d = class_1414.method_6377(
					this.field_6752, 16, 1, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), (float) (Math.PI / 8)
				);
				if (vec3d == null) {
					vec3d = class_1414.method_6373(this.field_6752, 8, 4, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
				}

				if (vec3d != null) {
					BlockPos blockPos2 = new BlockPos(vec3d);
					if (!world.getFluidState(blockPos2).matches(FluidTags.field_15517)
						|| !world.getBlockState(blockPos2).canPlaceAtSide(world, blockPos2, BlockPlacementEnvironment.field_48)) {
						vec3d = class_1414.method_6373(this.field_6752, 8, 5, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
					}
				}

				if (vec3d == null) {
					this.field_6753 = true;
					return;
				}

				this.field_6752.getLookControl().lookAt(vec3d.x, vec3d.y, vec3d.z, (float)(this.field_6752.method_5986() + 20), (float)this.field_6752.method_5978());
				this.field_6752.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 1.3);
				if (world.random.nextInt(80) == 0) {
					world.summonParticle(this.field_6752, (byte)38);
				}
			}
		}
	}

	static class class_1436 extends Goal {
		private final DolphinEntity field_6755;
		private final double field_6754;
		private PlayerEntity field_6756;

		class_1436(DolphinEntity dolphinEntity, double d) {
			this.field_6755 = dolphinEntity;
			this.field_6754 = d;
			this.setControlBits(3);
		}

		@Override
		public boolean canStart() {
			this.field_6756 = this.field_6755.world.getClosestPlayer(this.field_6755, 10.0);
			return this.field_6756 == null ? false : this.field_6756.isSwimming();
		}

		@Override
		public boolean shouldContinue() {
			return this.field_6756 != null && this.field_6756.isSwimming() && this.field_6755.squaredDistanceTo(this.field_6756) < 256.0;
		}

		@Override
		public void start() {
			this.field_6756.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5900, 100));
		}

		@Override
		public void onRemove() {
			this.field_6756 = null;
			this.field_6755.getNavigation().stop();
		}

		@Override
		public void tick() {
			this.field_6755.getLookControl().lookAt(this.field_6756, (float)(this.field_6755.method_5986() + 20), (float)this.field_6755.method_5978());
			if (this.field_6755.squaredDistanceTo(this.field_6756) < 6.25) {
				this.field_6755.getNavigation().stop();
			} else {
				this.field_6755.getNavigation().startMovingTo(this.field_6756, this.field_6754);
			}

			if (this.field_6756.isSwimming() && this.field_6756.world.random.nextInt(6) == 0) {
				this.field_6756.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5900, 100));
			}
		}
	}

	class class_1437 extends Goal {
		private int field_6758;

		private class_1437() {
		}

		@Override
		public boolean canStart() {
			if (this.field_6758 > DolphinEntity.this.age) {
				return false;
			} else {
				List<ItemEntity> list = DolphinEntity.this.world
					.getEntities(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.field_6748);
				return !list.isEmpty() || !DolphinEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN).isEmpty();
			}
		}

		@Override
		public void start() {
			List<ItemEntity> list = DolphinEntity.this.world
				.getEntities(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.field_6748);
			if (!list.isEmpty()) {
				DolphinEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
				DolphinEntity.this.playSound(SoundEvents.field_14972, 1.0F, 1.0F);
			}

			this.field_6758 = 0;
		}

		@Override
		public void onRemove() {
			ItemStack itemStack = DolphinEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN);
			if (!itemStack.isEmpty()) {
				this.method_18056(itemStack);
				DolphinEntity.this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
				this.field_6758 = DolphinEntity.this.age + DolphinEntity.this.random.nextInt(100);
			}
		}

		@Override
		public void tick() {
			List<ItemEntity> list = DolphinEntity.this.world
				.getEntities(ItemEntity.class, DolphinEntity.this.getBoundingBox().expand(8.0, 8.0, 8.0), DolphinEntity.field_6748);
			ItemStack itemStack = DolphinEntity.this.getEquippedStack(EquipmentSlot.HAND_MAIN);
			if (!itemStack.isEmpty()) {
				this.method_18056(itemStack);
				DolphinEntity.this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
			} else if (!list.isEmpty()) {
				DolphinEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2F);
			}
		}

		private void method_18056(ItemStack itemStack) {
			if (!itemStack.isEmpty()) {
				double d = DolphinEntity.this.y - 0.3F + (double)DolphinEntity.this.getEyeHeight();
				ItemEntity itemEntity = new ItemEntity(DolphinEntity.this.world, DolphinEntity.this.x, d, DolphinEntity.this.z, itemStack);
				itemEntity.setPickupDelay(40);
				itemEntity.setThrower(DolphinEntity.this.getUuid());
				float f = 0.3F;
				itemEntity.velocityX = (double)(
					-MathHelper.sin(DolphinEntity.this.yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(DolphinEntity.this.pitch * (float) (Math.PI / 180.0)) * f
				);
				itemEntity.velocityY = (double)(MathHelper.sin(DolphinEntity.this.pitch * (float) (Math.PI / 180.0)) * f * 1.5F);
				itemEntity.velocityZ = (double)(
					MathHelper.cos(DolphinEntity.this.yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(DolphinEntity.this.pitch * (float) (Math.PI / 180.0)) * f
				);
				float g = DolphinEntity.this.random.nextFloat() * (float) (Math.PI * 2);
				f = 0.02F * DolphinEntity.this.random.nextFloat();
				itemEntity.velocityX = itemEntity.velocityX + (double)(MathHelper.cos(g) * f);
				itemEntity.velocityZ = itemEntity.velocityZ + (double)(MathHelper.sin(g) * f);
				DolphinEntity.this.world.spawnEntity(itemEntity);
			}
		}
	}
}
