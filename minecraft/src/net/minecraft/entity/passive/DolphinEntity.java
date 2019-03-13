package net.minecraft.entity.passive;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1378;
import net.minecraft.class_1399;
import net.minecraft.class_1414;
import net.minecraft.class_4051;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
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
import net.minecraft.entity.mob.MobEntity;
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
	private static final class_4051 field_18101 = new class_4051().method_18418(10.0).method_18421().method_18417();
	public static final Predicate<ItemEntity> field_6748 = itemEntity -> !itemEntity.cannotPickup() && itemEntity.isValid() && itemEntity.isInsideWater();

	public DolphinEntity(EntityType<? extends DolphinEntity> entityType, World world) {
		super(entityType, world);
		this.field_6207 = new DolphinEntity.DolphinMoveControl(this);
		this.field_6206 = new DolphinLookControl(this, 10);
		this.setCanPickUpLoot(true);
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.setBreath(this.getMaxBreath());
		this.pitch = 0.0F;
		return super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Override
	public boolean canBreatheInWater() {
		return false;
	}

	@Override
	protected void method_6673(int i) {
	}

	public void method_6493(BlockPos blockPos) {
		this.field_6011.set(field_6747, blockPos);
	}

	public BlockPos method_6494() {
		return this.field_6011.get(field_6747);
	}

	public boolean method_6487() {
		return this.field_6011.get(field_6750);
	}

	public void method_6486(boolean bl) {
		this.field_6011.set(field_6750, bl);
	}

	public int method_6491() {
		return this.field_6011.get(field_6749);
	}

	public void method_6489(int i) {
		this.field_6011.set(field_6749, i);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_6747, BlockPos.ORIGIN);
		this.field_6011.startTracking(field_6750, false);
		this.field_6011.startTracking(field_6749, 2400);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("TreasurePosX", this.method_6494().getX());
		compoundTag.putInt("TreasurePosY", this.method_6494().getY());
		compoundTag.putInt("TreasurePosZ", this.method_6494().getZ());
		compoundTag.putBoolean("GotFish", this.method_6487());
		compoundTag.putInt("Moistness", this.method_6491());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		int i = compoundTag.getInt("TreasurePosX");
		int j = compoundTag.getInt("TreasurePosY");
		int k = compoundTag.getInt("TreasurePosZ");
		this.method_6493(new BlockPos(i, j, k));
		super.method_5749(compoundTag);
		this.method_6486(compoundTag.getBoolean("GotFish"));
		this.method_6489(compoundTag.getInt("Moistness"));
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(0, new BreatheAirGoal(this));
		this.field_6201.add(0, new MoveIntoWaterGoal(this));
		this.field_6201.add(1, new DolphinEntity.class_1435(this));
		this.field_6201.add(2, new DolphinEntity.class_1436(this, 4.0));
		this.field_6201.add(4, new class_1378(this, 1.0, 10));
		this.field_6201.add(4, new LookAroundGoal(this));
		this.field_6201.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.field_6201.add(5, new DolphinJumpGoal(this, 10));
		this.field_6201.add(6, new MeleeAttackGoal(this, 1.2F, true));
		this.field_6201.add(8, new DolphinEntity.class_1437());
		this.field_6201.add(8, new ChaseBoatGoal(this));
		this.field_6201.add(9, new FleeEntityGoal(this, GuardianEntity.class, 8.0F, 1.0, 1.0));
		this.field_6185.add(1, new class_1399(this, GuardianEntity.class).method_6318());
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(1.2F);
		this.method_6127().register(EntityAttributes.ATTACK_DAMAGE);
		this.method_5996(EntityAttributes.ATTACK_DAMAGE).setBaseValue(3.0);
	}

	@Override
	protected EntityNavigation method_5965(World world) {
		return new SwimNavigation(this, world);
	}

	@Override
	public boolean attack(Entity entity) {
		boolean bl = entity.damage(DamageSource.method_5511(this), (float)((int)this.method_5996(EntityAttributes.ATTACK_DAMAGE).getValue()));
		if (bl) {
			this.method_5723(this, entity);
			this.method_5783(SoundEvents.field_14992, 1.0F, 1.0F);
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
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
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
	public boolean method_18397(ItemStack itemStack) {
		EquipmentSlot equipmentSlot = MobEntity.method_5953(itemStack);
		return !this.method_6118(equipmentSlot).isEmpty() ? false : equipmentSlot == EquipmentSlot.HAND_MAIN && super.method_18397(itemStack);
	}

	@Override
	protected void method_5949(ItemEntity itemEntity) {
		if (this.method_6118(EquipmentSlot.HAND_MAIN).isEmpty()) {
			ItemStack itemStack = itemEntity.method_6983();
			if (this.method_5939(itemStack)) {
				this.method_5673(EquipmentSlot.HAND_MAIN, itemStack);
				this.handDropChances[EquipmentSlot.HAND_MAIN.getEntitySlotId()] = 2.0F;
				this.pickUpEntity(itemEntity, itemStack.getAmount());
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
					this.method_18799(
						this.method_18798().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F))
					);
					this.yaw = this.random.nextFloat() * 360.0F;
					this.onGround = false;
					this.velocityDirty = true;
				}
			}

			if (this.field_6002.isClient && this.isInsideWater() && this.method_18798().lengthSquared() > 0.03) {
				Vec3d vec3d = this.method_5828(0.0F);
				float f = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * 0.3F;
				float g = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * 0.3F;
				float h = 1.2F - this.random.nextFloat() * 0.7F;

				for (int i = 0; i < 2; i++) {
					this.field_6002
						.method_8406(
							ParticleTypes.field_11222, this.x - vec3d.x * (double)h + (double)f, this.y - vec3d.y, this.z - vec3d.z * (double)h + (double)g, 0.0, 0.0, 0.0
						);
					this.field_6002
						.method_8406(
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
			this.field_6002
				.method_8406(
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
	protected boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (!itemStack.isEmpty() && itemStack.getItem().method_7855(ItemTags.field_15527)) {
			if (!this.field_6002.isClient) {
				this.method_5783(SoundEvents.field_14590, 1.0F, 1.0F);
			}

			this.method_6486(true);
			if (!playerEntity.abilities.creativeMode) {
				itemStack.subtractAmount(1);
			}

			return true;
		} else {
			return super.method_5992(playerEntity, hand);
		}
	}

	@Override
	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		return this.y > 45.0 && this.y < (double)iWorld.getSeaLevel() && iWorld.method_8310(new BlockPos(this)) != Biomes.field_9423
			|| iWorld.method_8310(new BlockPos(this)) != Biomes.field_9446 && super.method_5979(iWorld, spawnType);
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_15216;
	}

	@Nullable
	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_15101;
	}

	@Nullable
	@Override
	protected SoundEvent method_5994() {
		return this.isInsideWater() ? SoundEvents.field_14881 : SoundEvents.field_14799;
	}

	@Override
	protected SoundEvent method_5625() {
		return SoundEvents.field_14887;
	}

	@Override
	protected SoundEvent method_5737() {
		return SoundEvents.field_15172;
	}

	protected boolean method_6484() {
		BlockPos blockPos = this.method_5942().method_6355();
		return blockPos != null ? this.method_5831(blockPos) < 144.0 : false;
	}

	@Override
	public void method_6091(Vec3d vec3d) {
		if (this.method_6034() && this.isInsideWater()) {
			this.method_5724(this.getMovementSpeed(), vec3d);
			this.method_5784(MovementType.field_6308, this.method_18798());
			this.method_18799(this.method_18798().multiply(0.9));
			if (this.getTarget() == null) {
				this.method_18799(this.method_18798().add(0.0, -0.005, 0.0));
			}
		} else {
			super.method_6091(vec3d);
		}
	}

	@Override
	public boolean method_5931(PlayerEntity playerEntity) {
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
				this.dolphin.method_18799(this.dolphin.method_18798().add(0.0, 0.005, 0.0));
			}

			if (this.state == MoveControl.State.field_6378 && !this.dolphin.method_5942().isIdle()) {
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
					float i = (float)(this.speed * this.dolphin.method_5996(EntityAttributes.MOVEMENT_SPEED).getValue());
					if (this.dolphin.isInsideWater()) {
						this.dolphin.setMovementSpeed(i * 0.02F);
						float j = -((float)(MathHelper.atan2(e, (double)MathHelper.sqrt(d * d + f * f)) * 180.0F / (float)Math.PI));
						j = MathHelper.clamp(MathHelper.wrapDegrees(j), -85.0F, 85.0F);
						this.dolphin.pitch = this.method_6238(this.dolphin.pitch, j, 5.0F);
						float k = MathHelper.cos(this.dolphin.pitch * (float) (Math.PI / 180.0));
						float l = MathHelper.sin(this.dolphin.pitch * (float) (Math.PI / 180.0));
						this.dolphin.movementInputForward = k * i;
						this.dolphin.movementInputUp = -l * i;
					} else {
						this.dolphin.setMovementSpeed(i * 0.1F);
					}
				}
			} else {
				this.dolphin.setMovementSpeed(0.0F);
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
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
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
			return this.field_6752.method_5831(new BlockPos((double)blockPos.getX(), this.field_6752.y, (double)blockPos.getZ())) > 16.0
				&& !this.field_6753
				&& this.field_6752.getBreath() >= 100;
		}

		@Override
		public void start() {
			this.field_6753 = false;
			this.field_6752.method_5942().stop();
			World world = this.field_6752.field_6002;
			BlockPos blockPos = new BlockPos(this.field_6752);
			String string = (double)world.random.nextFloat() >= 0.5 ? "Ocean_Ruin" : "Shipwreck";
			BlockPos blockPos2 = world.method_8487(string, blockPos, 50, false);
			if (blockPos2 == null) {
				BlockPos blockPos3 = world.method_8487(string.equals("Ocean_Ruin") ? "Shipwreck" : "Ocean_Ruin", blockPos, 50, false);
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
			if (this.field_6752.method_5831(new BlockPos((double)blockPos.getX(), this.field_6752.y, (double)blockPos.getZ())) <= 16.0 || this.field_6753) {
				this.field_6752.method_6486(false);
			}
		}

		@Override
		public void tick() {
			BlockPos blockPos = this.field_6752.method_6494();
			World world = this.field_6752.field_6002;
			if (this.field_6752.method_6484() || this.field_6752.method_5942().isIdle()) {
				Vec3d vec3d = class_1414.method_6377(
					this.field_6752, 16, 1, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), (float) (Math.PI / 8)
				);
				if (vec3d == null) {
					vec3d = class_1414.method_6373(this.field_6752, 8, 4, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
				}

				if (vec3d != null) {
					BlockPos blockPos2 = new BlockPos(vec3d);
					if (!world.method_8316(blockPos2).method_15767(FluidTags.field_15517)
						|| !world.method_8320(blockPos2).method_11609(world, blockPos2, BlockPlacementEnvironment.field_48)) {
						vec3d = class_1414.method_6373(this.field_6752, 8, 5, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
					}
				}

				if (vec3d == null) {
					this.field_6753 = true;
					return;
				}

				this.field_6752.method_5988().lookAt(vec3d.x, vec3d.y, vec3d.z, (float)(this.field_6752.method_5986() + 20), (float)this.field_6752.method_5978());
				this.field_6752.method_5942().startMovingTo(vec3d.x, vec3d.y, vec3d.z, 1.3);
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
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
		}

		@Override
		public boolean canStart() {
			this.field_6756 = this.field_6755.field_6002.method_18462(DolphinEntity.field_18101, this.field_6755);
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
			this.field_6755.method_5942().stop();
		}

		@Override
		public void tick() {
			this.field_6755.method_5988().lookAt(this.field_6756, (float)(this.field_6755.method_5986() + 20), (float)this.field_6755.method_5978());
			if (this.field_6755.squaredDistanceTo(this.field_6756) < 6.25) {
				this.field_6755.method_5942().stop();
			} else {
				this.field_6755.method_5942().startMovingTo(this.field_6756, this.field_6754);
			}

			if (this.field_6756.isSwimming() && this.field_6756.field_6002.random.nextInt(6) == 0) {
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
				List<ItemEntity> list = DolphinEntity.this.field_6002
					.method_8390(ItemEntity.class, DolphinEntity.this.method_5829().expand(8.0, 8.0, 8.0), DolphinEntity.field_6748);
				return !list.isEmpty() || !DolphinEntity.this.method_6118(EquipmentSlot.HAND_MAIN).isEmpty();
			}
		}

		@Override
		public void start() {
			List<ItemEntity> list = DolphinEntity.this.field_6002
				.method_8390(ItemEntity.class, DolphinEntity.this.method_5829().expand(8.0, 8.0, 8.0), DolphinEntity.field_6748);
			if (!list.isEmpty()) {
				DolphinEntity.this.method_5942().startMovingTo((Entity)list.get(0), 1.2F);
				DolphinEntity.this.method_5783(SoundEvents.field_14972, 1.0F, 1.0F);
			}

			this.field_6758 = 0;
		}

		@Override
		public void onRemove() {
			ItemStack itemStack = DolphinEntity.this.method_6118(EquipmentSlot.HAND_MAIN);
			if (!itemStack.isEmpty()) {
				this.method_18056(itemStack);
				DolphinEntity.this.method_5673(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
				this.field_6758 = DolphinEntity.this.age + DolphinEntity.this.random.nextInt(100);
			}
		}

		@Override
		public void tick() {
			List<ItemEntity> list = DolphinEntity.this.field_6002
				.method_8390(ItemEntity.class, DolphinEntity.this.method_5829().expand(8.0, 8.0, 8.0), DolphinEntity.field_6748);
			ItemStack itemStack = DolphinEntity.this.method_6118(EquipmentSlot.HAND_MAIN);
			if (!itemStack.isEmpty()) {
				this.method_18056(itemStack);
				DolphinEntity.this.method_5673(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
			} else if (!list.isEmpty()) {
				DolphinEntity.this.method_5942().startMovingTo((Entity)list.get(0), 1.2F);
			}
		}

		private void method_18056(ItemStack itemStack) {
			if (!itemStack.isEmpty()) {
				double d = DolphinEntity.this.y - 0.3F + (double)DolphinEntity.this.getStandingEyeHeight();
				ItemEntity itemEntity = new ItemEntity(DolphinEntity.this.field_6002, DolphinEntity.this.x, d, DolphinEntity.this.z, itemStack);
				itemEntity.setPickupDelay(40);
				itemEntity.setThrower(DolphinEntity.this.getUuid());
				float f = 0.3F;
				float g = DolphinEntity.this.random.nextFloat() * (float) (Math.PI * 2);
				float h = 0.02F * DolphinEntity.this.random.nextFloat();
				itemEntity.setVelocity(
					(double)(
						0.3F * -MathHelper.sin(DolphinEntity.this.yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(DolphinEntity.this.pitch * (float) (Math.PI / 180.0))
							+ MathHelper.cos(g) * h
					),
					(double)(0.3F * MathHelper.sin(DolphinEntity.this.pitch * (float) (Math.PI / 180.0)) * 1.5F),
					(double)(
						0.3F * MathHelper.cos(DolphinEntity.this.yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(DolphinEntity.this.pitch * (float) (Math.PI / 180.0))
							+ MathHelper.sin(g) * h
					)
				);
				DolphinEntity.this.field_6002.spawnEntity(itemEntity);
			}
		}
	}
}
