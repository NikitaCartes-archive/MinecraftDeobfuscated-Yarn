package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class DrownedEntity extends ZombieEntity implements RangedAttackMob {
	private boolean targetingUnderwater;
	protected final SwimNavigation waterNavigation;
	protected final MobNavigation landNavigation;

	public DrownedEntity(EntityType<? extends DrownedEntity> entityType, World world) {
		super(entityType, world);
		this.stepHeight = 1.0F;
		this.moveControl = new DrownedEntity.DrownedMoveControl(this);
		this.setPathNodeTypeWeight(PathNodeType.field_18, 0.0F);
		this.waterNavigation = new SwimNavigation(this, world);
		this.landNavigation = new MobNavigation(this, world);
	}

	@Override
	protected void initCustomGoals() {
		this.goalSelector.add(1, new DrownedEntity.WanderAroundOnSurfaceGoal(this, 1.0));
		this.goalSelector.add(2, new DrownedEntity.TridentAttackGoal(this, 1.0, 40, 10.0F));
		this.goalSelector.add(2, new DrownedEntity.DrownedAttackGoal(this, 1.0, false));
		this.goalSelector.add(5, new DrownedEntity.LeaveWaterGoal(this, 1.0));
		this.goalSelector.add(6, new DrownedEntity.class_1557(this, 1.0, this.world.getSeaLevel()));
		this.goalSelector.add(7, new WanderAroundGoal(this, 1.0));
		this.targetSelector.add(1, new RevengeGoal(this, DrownedEntity.class).setGroupRevenge(ZombiePigmanEntity.class));
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, this::method_7012));
		this.targetSelector.add(3, new FollowTargetGoal(this, AbstractTraderEntity.class, false));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.targetSelector.add(5, new FollowTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}

	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		if (this.getEquippedStack(EquipmentSlot.field_6171).isEmpty() && this.random.nextFloat() < 0.03F) {
			this.setEquippedStack(EquipmentSlot.field_6171, new ItemStack(Items.field_8864));
			this.handDropChances[EquipmentSlot.field_6171.getEntitySlotId()] = 2.0F;
		}

		return entityData;
	}

	public static boolean method_20673(EntityType<DrownedEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		Biome biome = iWorld.getBiome(blockPos);
		boolean bl = iWorld.getDifficulty() != Difficulty.field_5801
			&& method_20679(iWorld, blockPos, random)
			&& (spawnType == SpawnType.field_16469 || iWorld.getFluidState(blockPos).matches(FluidTags.field_15517));
		return biome != Biomes.field_9438 && biome != Biomes.field_9463
			? random.nextInt(40) == 0 && method_20672(iWorld, blockPos) && bl
			: random.nextInt(15) == 0 && bl;
	}

	private static boolean method_20672(IWorld iWorld, BlockPos blockPos) {
		return blockPos.getY() < iWorld.getSeaLevel() - 5;
	}

	@Override
	protected boolean shouldBreakDoors() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isInsideWater() ? SoundEvents.field_14980 : SoundEvents.field_15030;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return this.isInsideWater() ? SoundEvents.field_14651 : SoundEvents.field_14571;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isInsideWater() ? SoundEvents.field_15162 : SoundEvents.field_15066;
	}

	@Override
	protected SoundEvent getStepSound() {
		return SoundEvents.field_14835;
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.field_14913;
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		if ((double)this.random.nextFloat() > 0.9) {
			int i = this.random.nextInt(16);
			if (i < 10) {
				this.setEquippedStack(EquipmentSlot.field_6173, new ItemStack(Items.field_8547));
			} else {
				this.setEquippedStack(EquipmentSlot.field_6173, new ItemStack(Items.field_8378));
			}
		}
	}

	@Override
	protected boolean isBetterItemFor(ItemStack itemStack, ItemStack itemStack2, EquipmentSlot equipmentSlot) {
		if (itemStack2.getItem() == Items.field_8864) {
			return false;
		} else if (itemStack2.getItem() == Items.field_8547) {
			return itemStack.getItem() == Items.field_8547 ? itemStack.getDamage() < itemStack2.getDamage() : false;
		} else {
			return itemStack.getItem() == Items.field_8547 ? true : super.isBetterItemFor(itemStack, itemStack2, equipmentSlot);
		}
	}

	@Override
	protected boolean canConvertInWater() {
		return false;
	}

	@Override
	public boolean canSpawn(ViewableWorld viewableWorld) {
		return viewableWorld.intersectsEntities(this);
	}

	public boolean method_7012(@Nullable LivingEntity livingEntity) {
		return livingEntity != null ? !this.world.isDaylight() || livingEntity.isInsideWater() : false;
	}

	@Override
	public boolean canFly() {
		return !this.isSwimming();
	}

	private boolean isTargetingUnderwater() {
		if (this.targetingUnderwater) {
			return true;
		} else {
			LivingEntity livingEntity = this.getTarget();
			return livingEntity != null && livingEntity.isInsideWater();
		}
	}

	@Override
	public void travel(Vec3d vec3d) {
		if (this.canMoveVoluntarily() && this.isInsideWater() && this.isTargetingUnderwater()) {
			this.updateVelocity(0.01F, vec3d);
			this.move(MovementType.field_6308, this.getVelocity());
			this.setVelocity(this.getVelocity().multiply(0.9));
		} else {
			super.travel(vec3d);
		}
	}

	@Override
	public void updateSwimming() {
		if (!this.world.isClient) {
			if (this.canMoveVoluntarily() && this.isInsideWater() && this.isTargetingUnderwater()) {
				this.navigation = this.waterNavigation;
				this.setSwimming(true);
			} else {
				this.navigation = this.landNavigation;
				this.setSwimming(false);
			}
		}
	}

	protected boolean method_7016() {
		Path path = this.getNavigation().getCurrentPath();
		if (path != null) {
			BlockPos blockPos = path.method_48();
			if (blockPos != null) {
				double d = this.squaredDistanceTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
				if (d < 4.0) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void attack(LivingEntity livingEntity, float f) {
		TridentEntity tridentEntity = new TridentEntity(this.world, this, new ItemStack(Items.field_8547));
		double d = livingEntity.x - this.x;
		double e = livingEntity.getBoundingBox().minY + (double)(livingEntity.getHeight() / 3.0F) - tridentEntity.y;
		double g = livingEntity.z - this.z;
		double h = (double)MathHelper.sqrt(d * d + g * g);
		tridentEntity.setVelocity(d, e + h * 0.2F, g, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
		this.playSound(SoundEvents.field_14753, 1.0F, 1.0F / (this.getRand().nextFloat() * 0.4F + 0.8F));
		this.world.spawnEntity(tridentEntity);
	}

	public void setTargetingUnderwater(boolean bl) {
		this.targetingUnderwater = bl;
	}

	static class DrownedAttackGoal extends ZombieAttackGoal {
		private final DrownedEntity drowned;

		public DrownedAttackGoal(DrownedEntity drownedEntity, double d, boolean bl) {
			super(drownedEntity, d, bl);
			this.drowned = drownedEntity;
		}

		@Override
		public boolean canStart() {
			return super.canStart() && this.drowned.method_7012(this.drowned.getTarget());
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && this.drowned.method_7012(this.drowned.getTarget());
		}
	}

	static class DrownedMoveControl extends MoveControl {
		private final DrownedEntity drowned;

		public DrownedMoveControl(DrownedEntity drownedEntity) {
			super(drownedEntity);
			this.drowned = drownedEntity;
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = this.drowned.getTarget();
			if (this.drowned.isTargetingUnderwater() && this.drowned.isInsideWater()) {
				if (livingEntity != null && livingEntity.y > this.drowned.y || this.drowned.targetingUnderwater) {
					this.drowned.setVelocity(this.drowned.getVelocity().add(0.0, 0.002, 0.0));
				}

				if (this.state != MoveControl.State.field_6378 || this.drowned.getNavigation().isIdle()) {
					this.drowned.setMovementSpeed(0.0F);
					return;
				}

				double d = this.targetX - this.drowned.x;
				double e = this.targetY - this.drowned.y;
				double f = this.targetZ - this.drowned.z;
				double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
				e /= g;
				float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.drowned.yaw = this.changeAngle(this.drowned.yaw, h, 90.0F);
				this.drowned.field_6283 = this.drowned.yaw;
				float i = (float)(this.speed * this.drowned.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
				float j = MathHelper.lerp(0.125F, this.drowned.getMovementSpeed(), i);
				this.drowned.setMovementSpeed(j);
				this.drowned.setVelocity(this.drowned.getVelocity().add((double)j * d * 0.005, (double)j * e * 0.1, (double)j * f * 0.005));
			} else {
				if (!this.drowned.onGround) {
					this.drowned.setVelocity(this.drowned.getVelocity().add(0.0, -0.008, 0.0));
				}

				super.tick();
			}
		}
	}

	static class LeaveWaterGoal extends MoveToTargetPosGoal {
		private final DrownedEntity drowned;

		public LeaveWaterGoal(DrownedEntity drownedEntity, double d) {
			super(drownedEntity, d, 8, 2);
			this.drowned = drownedEntity;
		}

		@Override
		public boolean canStart() {
			return super.canStart()
				&& !this.drowned.world.isDaylight()
				&& this.drowned.isInsideWater()
				&& this.drowned.y >= (double)(this.drowned.world.getSeaLevel() - 3);
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue();
		}

		@Override
		protected boolean isTargetPos(ViewableWorld viewableWorld, BlockPos blockPos) {
			BlockPos blockPos2 = blockPos.up();
			return viewableWorld.isAir(blockPos2) && viewableWorld.isAir(blockPos2.up())
				? viewableWorld.getBlockState(blockPos).hasSolidTopSurface(viewableWorld, blockPos, this.drowned)
				: false;
		}

		@Override
		public void start() {
			this.drowned.setTargetingUnderwater(false);
			this.drowned.navigation = this.drowned.landNavigation;
			super.start();
		}

		@Override
		public void stop() {
			super.stop();
		}
	}

	static class TridentAttackGoal extends ProjectileAttackGoal {
		private final DrownedEntity drowned;

		public TridentAttackGoal(RangedAttackMob rangedAttackMob, double d, int i, float f) {
			super(rangedAttackMob, d, i, f);
			this.drowned = (DrownedEntity)rangedAttackMob;
		}

		@Override
		public boolean canStart() {
			return super.canStart() && this.drowned.getMainHandStack().getItem() == Items.field_8547;
		}

		@Override
		public void start() {
			super.start();
			this.drowned.setAttacking(true);
			this.drowned.setCurrentHand(Hand.field_5808);
		}

		@Override
		public void stop() {
			super.stop();
			this.drowned.clearActiveItem();
			this.drowned.setAttacking(false);
		}
	}

	static class WanderAroundOnSurfaceGoal extends Goal {
		private final MobEntityWithAi mob;
		private double x;
		private double y;
		private double z;
		private final double speed;
		private final World world;

		public WanderAroundOnSurfaceGoal(MobEntityWithAi mobEntityWithAi, double d) {
			this.mob = mobEntityWithAi;
			this.speed = d;
			this.world = mobEntityWithAi.world;
			this.setControls(EnumSet.of(Goal.Control.field_18405));
		}

		@Override
		public boolean canStart() {
			if (!this.world.isDaylight()) {
				return false;
			} else if (this.mob.isInsideWater()) {
				return false;
			} else {
				Vec3d vec3d = this.getWanderTarget();
				if (vec3d == null) {
					return false;
				} else {
					this.x = vec3d.x;
					this.y = vec3d.y;
					this.z = vec3d.z;
					return true;
				}
			}
		}

		@Override
		public boolean shouldContinue() {
			return !this.mob.getNavigation().isIdle();
		}

		@Override
		public void start() {
			this.mob.getNavigation().startMovingTo(this.x, this.y, this.z, this.speed);
		}

		@Nullable
		private Vec3d getWanderTarget() {
			Random random = this.mob.getRand();
			BlockPos blockPos = new BlockPos(this.mob.x, this.mob.getBoundingBox().minY, this.mob.z);

			for (int i = 0; i < 10; i++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
				if (this.world.getBlockState(blockPos2).getBlock() == Blocks.field_10382) {
					return new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
				}
			}

			return null;
		}
	}

	static class class_1557 extends Goal {
		private final DrownedEntity drowned;
		private final double speed;
		private final int minY;
		private boolean field_7248;

		public class_1557(DrownedEntity drownedEntity, double d, int i) {
			this.drowned = drownedEntity;
			this.speed = d;
			this.minY = i;
		}

		@Override
		public boolean canStart() {
			return !this.drowned.world.isDaylight() && this.drowned.isInsideWater() && this.drowned.y < (double)(this.minY - 2);
		}

		@Override
		public boolean shouldContinue() {
			return this.canStart() && !this.field_7248;
		}

		@Override
		public void tick() {
			if (this.drowned.y < (double)(this.minY - 1) && (this.drowned.getNavigation().isIdle() || this.drowned.method_7016())) {
				Vec3d vec3d = PathfindingUtil.method_6373(this.drowned, 4, 8, new Vec3d(this.drowned.x, (double)(this.minY - 1), this.drowned.z));
				if (vec3d == null) {
					this.field_7248 = true;
					return;
				}

				this.drowned.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.speed);
			}
		}

		@Override
		public void start() {
			this.drowned.setTargetingUnderwater(true);
			this.field_7248 = false;
		}

		@Override
		public void stop() {
			this.drowned.setTargetingUnderwater(false);
		}
	}
}
