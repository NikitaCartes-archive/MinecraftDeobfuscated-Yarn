package net.minecraft.entity.mob;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_1361;
import net.minecraft.class_1376;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.TagHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class EndermanEntity extends HostileEntity {
	private static final UUID field_7256 = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
	private static final EntityAttributeModifier field_7252 = new EntityAttributeModifier(
			field_7256, "Attacking speed boost", 0.15F, EntityAttributeModifier.Operation.field_6328
		)
		.setSerialize(false);
	private static final TrackedData<Optional<BlockState>> CARRIED_BLOCK = DataTracker.registerData(
		EndermanEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE
	);
	private static final TrackedData<Boolean> ANGRY = DataTracker.registerData(EndermanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int field_7253;
	private int ageWhenTargetSet;

	public EndermanEntity(World world) {
		super(EntityType.ENDERMAN, world);
		this.stepHeight = 1.0F;
		this.setPathNodeTypeWeight(PathNodeType.WATER, -1.0F);
	}

	@Override
	protected void method_5959() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
		this.goalSelector.add(7, new class_1394(this, 1.0, 0.0F));
		this.goalSelector.add(8, new class_1361(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(8, new class_1376(this));
		this.goalSelector.add(10, new EndermanEntity.PlaceBlockGoal(this));
		this.goalSelector.add(11, new EndermanEntity.PickUpBlockGoal(this));
		this.targetSelector.add(1, new EndermanEntity.class_1562(this));
		this.targetSelector.add(2, new class_1399(this));
		this.targetSelector.add(3, new FollowTargetGoal(this, EndermiteEntity.class, 10, true, false, EndermiteEntity::method_7023));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(40.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(7.0);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(64.0);
	}

	@Override
	public void setTarget(@Nullable LivingEntity livingEntity) {
		super.setTarget(livingEntity);
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
		if (livingEntity == null) {
			this.ageWhenTargetSet = 0;
			this.dataTracker.set(ANGRY, false);
			entityAttributeInstance.removeModifier(field_7252);
		} else {
			this.ageWhenTargetSet = this.age;
			this.dataTracker.set(ANGRY, true);
			if (!entityAttributeInstance.hasModifier(field_7252)) {
				entityAttributeInstance.addModifier(field_7252);
			}
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(CARRIED_BLOCK, Optional.empty());
		this.dataTracker.startTracking(ANGRY, false);
	}

	public void method_7030() {
		if (this.age >= this.field_7253 + 400) {
			this.field_7253 = this.age;
			if (!this.isSilent()) {
				this.world.playSound(this.x, this.y + (double)this.getEyeHeight(), this.z, SoundEvents.field_14967, this.getSoundCategory(), 2.5F, 1.0F, false);
			}
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (ANGRY.equals(trackedData) && this.isAngry() && this.world.isClient) {
			this.method_7030();
		}

		super.onTrackedDataSet(trackedData);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		BlockState blockState = this.getCarriedBlock();
		if (blockState != null) {
			compoundTag.put("carriedBlockState", TagHelper.serializeBlockState(blockState));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		BlockState blockState = null;
		if (compoundTag.containsKey("carriedBlockState", 10)) {
			blockState = TagHelper.deserializeBlockState(compoundTag.getCompound("carriedBlockState"));
			if (blockState.isAir()) {
				blockState = null;
			}
		}

		this.setCarriedBlock(blockState);
	}

	private boolean method_7026(PlayerEntity playerEntity) {
		ItemStack itemStack = playerEntity.inventory.armor.get(3);
		if (itemStack.getItem() == Blocks.field_10147.getItem()) {
			return false;
		} else {
			Vec3d vec3d = playerEntity.getRotationVec(1.0F).normalize();
			Vec3d vec3d2 = new Vec3d(
				this.x - playerEntity.x,
				this.getBoundingBox().minY + (double)this.getEyeHeight() - (playerEntity.y + (double)playerEntity.getEyeHeight()),
				this.z - playerEntity.z
			);
			double d = vec3d2.length();
			vec3d2 = vec3d2.normalize();
			double e = vec3d.dotProduct(vec3d2);
			return e > 1.0 - 0.025 / d ? playerEntity.canSee(this) : false;
		}
	}

	@Override
	public float getEyeHeight() {
		return 2.55F;
	}

	@Override
	public void updateMovement() {
		if (this.world.isClient) {
			for (int i = 0; i < 2; i++) {
				this.world
					.addParticle(
						ParticleTypes.field_11214,
						this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
						this.y + this.random.nextDouble() * (double)this.getHeight() - 0.25,
						this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
						(this.random.nextDouble() - 0.5) * 2.0,
						-this.random.nextDouble(),
						(this.random.nextDouble() - 0.5) * 2.0
					);
			}
		}

		this.field_6282 = false;
		super.updateMovement();
	}

	@Override
	protected void mobTick() {
		if (this.isTouchingWater()) {
			this.damage(DamageSource.DROWN, 1.0F);
		}

		if (this.world.isDaylight() && this.age >= this.ageWhenTargetSet + 600) {
			float f = this.method_5718();
			if (f > 0.5F && this.world.isSkyVisible(new BlockPos(this)) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
				this.setTarget(null);
				this.method_7029();
			}
		}

		super.mobTick();
	}

	protected boolean method_7029() {
		double d = this.x + (this.random.nextDouble() - 0.5) * 64.0;
		double e = this.y + (double)(this.random.nextInt(64) - 32);
		double f = this.z + (this.random.nextDouble() - 0.5) * 64.0;
		return this.method_7024(d, e, f);
	}

	protected boolean method_7025(Entity entity) {
		Vec3d vec3d = new Vec3d(
			this.x - entity.x, this.getBoundingBox().minY + (double)(this.getHeight() / 2.0F) - entity.y + (double)entity.getEyeHeight(), this.z - entity.z
		);
		vec3d = vec3d.normalize();
		double d = 16.0;
		double e = this.x + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.x * 16.0;
		double f = this.y + (double)(this.random.nextInt(16) - 8) - vec3d.y * 16.0;
		double g = this.z + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.z * 16.0;
		return this.method_7024(e, f, g);
	}

	private boolean method_7024(double d, double e, double f) {
		boolean bl = this.method_6082(d, e, f, true);
		if (bl) {
			this.world.playSound(null, this.prevX, this.prevY, this.prevZ, SoundEvents.field_14879, this.getSoundCategory(), 1.0F, 1.0F);
			this.playSound(SoundEvents.field_14879, 1.0F, 1.0F);
		}

		return bl;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isAngry() ? SoundEvents.field_14713 : SoundEvents.field_14696;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14797;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14608;
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);
		BlockState blockState = this.getCarriedBlock();
		if (blockState != null) {
			this.dropItem(blockState.getBlock());
		}
	}

	public void setCarriedBlock(@Nullable BlockState blockState) {
		this.dataTracker.set(CARRIED_BLOCK, Optional.ofNullable(blockState));
	}

	@Nullable
	public BlockState getCarriedBlock() {
		return (BlockState)this.dataTracker.get(CARRIED_BLOCK).orElse(null);
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (damageSource instanceof ProjectileDamageSource) {
			for (int i = 0; i < 64; i++) {
				if (this.method_7029()) {
					return true;
				}
			}

			return false;
		} else {
			boolean bl = super.damage(damageSource, f);
			if (damageSource.doesBypassArmor() && this.random.nextInt(10) != 0) {
				this.method_7029();
			}

			return bl;
		}
	}

	public boolean isAngry() {
		return this.dataTracker.get(ANGRY);
	}

	static class PickUpBlockGoal extends Goal {
		private final EndermanEntity owner;

		public PickUpBlockGoal(EndermanEntity endermanEntity) {
			this.owner = endermanEntity;
		}

		@Override
		public boolean canStart() {
			if (this.owner.getCarriedBlock() != null) {
				return false;
			} else {
				return !this.owner.world.getGameRules().getBoolean("mobGriefing") ? false : this.owner.getRand().nextInt(20) == 0;
			}
		}

		@Override
		public void tick() {
			Random random = this.owner.getRand();
			World world = this.owner.world;
			int i = MathHelper.floor(this.owner.x - 2.0 + random.nextDouble() * 4.0);
			int j = MathHelper.floor(this.owner.y + random.nextDouble() * 3.0);
			int k = MathHelper.floor(this.owner.z - 2.0 + random.nextDouble() * 4.0);
			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			Vec3d vec3d = new Vec3d((double)MathHelper.floor(this.owner.x) + 0.5, (double)j + 0.5, (double)MathHelper.floor(this.owner.z) + 0.5);
			Vec3d vec3d2 = new Vec3d((double)i + 0.5, (double)j + 0.5, (double)k + 0.5);
			BlockHitResult blockHitResult = world.method_17742(
				new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.NONE, this.owner)
			);
			boolean bl = blockHitResult.getType() != HitResult.Type.NONE && blockHitResult.getBlockPos().equals(blockPos);
			if (block.matches(BlockTags.field_15460) && bl) {
				this.owner.setCarriedBlock(blockState);
				world.clearBlockState(blockPos);
			}
		}
	}

	static class PlaceBlockGoal extends Goal {
		private final EndermanEntity owner;

		public PlaceBlockGoal(EndermanEntity endermanEntity) {
			this.owner = endermanEntity;
		}

		@Override
		public boolean canStart() {
			if (this.owner.getCarriedBlock() == null) {
				return false;
			} else {
				return !this.owner.world.getGameRules().getBoolean("mobGriefing") ? false : this.owner.getRand().nextInt(2000) == 0;
			}
		}

		@Override
		public void tick() {
			Random random = this.owner.getRand();
			IWorld iWorld = this.owner.world;
			int i = MathHelper.floor(this.owner.x - 1.0 + random.nextDouble() * 2.0);
			int j = MathHelper.floor(this.owner.y + random.nextDouble() * 2.0);
			int k = MathHelper.floor(this.owner.z - 1.0 + random.nextDouble() * 2.0);
			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = iWorld.getBlockState(blockPos);
			BlockPos blockPos2 = blockPos.down();
			BlockState blockState2 = iWorld.getBlockState(blockPos2);
			BlockState blockState3 = this.owner.getCarriedBlock();
			if (blockState3 != null && this.method_7033(iWorld, blockPos, blockState3, blockState, blockState2, blockPos2)) {
				iWorld.setBlockState(blockPos, blockState3, 3);
				this.owner.setCarriedBlock(null);
			}
		}

		private boolean method_7033(
			ViewableWorld viewableWorld, BlockPos blockPos, BlockState blockState, BlockState blockState2, BlockState blockState3, BlockPos blockPos2
		) {
			return blockState2.isAir() && !blockState3.isAir() && blockState3.method_11604(viewableWorld, blockPos2) && blockState.canPlaceAt(viewableWorld, blockPos);
		}
	}

	static class class_1562 extends FollowTargetGoal<PlayerEntity> {
		private final EndermanEntity field_7260;
		private PlayerEntity field_7259;
		private int field_7262;
		private int field_7261;

		public class_1562(EndermanEntity endermanEntity) {
			super(endermanEntity, PlayerEntity.class, false);
			this.field_7260 = endermanEntity;
		}

		@Override
		public boolean canStart() {
			double d = this.getFollowRange();
			this.field_7259 = this.field_7260
				.world
				.method_8439(
					this.field_7260.x, this.field_7260.y, this.field_7260.z, d, d, null, playerEntity -> playerEntity != null && this.field_7260.method_7026(playerEntity)
				);
			return this.field_7259 != null;
		}

		@Override
		public void start() {
			this.field_7262 = 5;
			this.field_7261 = 0;
		}

		@Override
		public void onRemove() {
			this.field_7259 = null;
			super.onRemove();
		}

		@Override
		public boolean shouldContinue() {
			if (this.field_7259 != null) {
				if (!this.field_7260.method_7026(this.field_7259)) {
					return false;
				} else {
					this.field_7260.method_5951(this.field_7259, 10.0F, 10.0F);
					return true;
				}
			} else {
				return this.field_6644 != null && this.field_6644.isValid() ? true : super.shouldContinue();
			}
		}

		@Override
		public void tick() {
			if (this.field_7259 != null) {
				if (--this.field_7262 <= 0) {
					this.field_6644 = this.field_7259;
					this.field_7259 = null;
					super.start();
				}
			} else {
				if (this.field_6644 != null) {
					if (this.field_7260.method_7026(this.field_6644)) {
						if (this.field_6644.squaredDistanceTo(this.field_7260) < 16.0) {
							this.field_7260.method_7029();
						}

						this.field_7261 = 0;
					} else if (this.field_6644.squaredDistanceTo(this.field_7260) > 256.0 && this.field_7261++ >= 30 && this.field_7260.method_7025(this.field_6644)) {
						this.field_7261 = 0;
					}
				}

				super.tick();
			}
		}
	}
}
