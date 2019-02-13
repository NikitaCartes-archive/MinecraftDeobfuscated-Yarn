package net.minecraft.entity.mob;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_1396;
import net.minecraft.class_1399;
import net.minecraft.class_1414;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
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
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class DrownedEntity extends ZombieEntity implements RangedAttacker {
	private boolean field_7233;
	protected final SwimNavigation field_7234;
	protected final EntityMobNavigation field_7232;

	public DrownedEntity(World world) {
		super(EntityType.DROWNED, world);
		this.stepHeight = 1.0F;
		this.moveControl = new DrownedEntity.DrownedMoveControl(this);
		this.setPathNodeTypeWeight(PathNodeType.field_18, 0.0F);
		this.field_7234 = new SwimNavigation(this, world);
		this.field_7232 = new EntityMobNavigation(this, world);
	}

	@Override
	protected void method_7208() {
		this.goalSelector.add(1, new DrownedEntity.class_1555(this, 1.0));
		this.goalSelector.add(2, new DrownedEntity.class_1558(this, 1.0, 40, 10.0F));
		this.goalSelector.add(2, new DrownedEntity.class_1552(this, 1.0, false));
		this.goalSelector.add(5, new DrownedEntity.class_1554(this, 1.0));
		this.goalSelector.add(6, new DrownedEntity.class_1557(this, 1.0, this.world.getSeaLevel()));
		this.goalSelector.add(7, new WanderAroundGoal(this, 1.0));
		this.targetSelector.add(1, new class_1399(this, DrownedEntity.class).method_6318(PigZombieEntity.class));
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, new DrownedEntity.class_1553(this)));
		this.targetSelector.add(3, new FollowTargetGoal(this, AbstractTraderEntity.class, false));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.targetSelector.add(5, new FollowTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return super.createNavigation(world);
	}

	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		if (this.getEquippedStack(EquipmentSlot.HAND_OFF).isEmpty() && this.random.nextFloat() < 0.03F) {
			this.setEquippedStack(EquipmentSlot.HAND_OFF, new ItemStack(Items.field_8864));
			this.handDropChances[EquipmentSlot.HAND_OFF.getEntitySlotId()] = 2.0F;
		}

		return entityData;
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		Biome biome = iWorld.getBiome(new BlockPos(this.x, this.y, this.z));
		return biome != Biomes.field_9438 && biome != Biomes.field_9463
			? this.random.nextInt(40) == 0 && this.method_7015() && super.canSpawn(iWorld, spawnType)
			: this.random.nextInt(15) == 0 && super.canSpawn(iWorld, spawnType);
	}

	private boolean method_7015() {
		return this.getBoundingBox().minY < (double)(this.world.getSeaLevel() - 5);
	}

	@Override
	protected boolean method_7212() {
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
	protected SoundEvent getSoundStep() {
		return SoundEvents.field_14835;
	}

	@Override
	protected SoundEvent getSoundSwim() {
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
				this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8547));
			} else {
				this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8378));
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
	protected boolean method_7209() {
		return false;
	}

	@Override
	public boolean method_5957(ViewableWorld viewableWorld) {
		return viewableWorld.method_8606(this);
	}

	public boolean method_7012(@Nullable LivingEntity livingEntity) {
		return livingEntity != null ? !this.world.isDaylight() || livingEntity.isInsideWater() : false;
	}

	@Override
	public boolean canFly() {
		return !this.isSwimming();
	}

	private boolean method_7018() {
		if (this.field_7233) {
			return true;
		} else {
			LivingEntity livingEntity = this.getTarget();
			return livingEntity != null && livingEntity.isInsideWater();
		}
	}

	@Override
	public void method_6091(float f, float g, float h) {
		if (this.method_6034() && this.isInsideWater() && this.method_7018()) {
			this.method_5724(f, g, h, 0.01F);
			this.move(MovementType.field_6308, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.9F;
			this.velocityY *= 0.9F;
			this.velocityZ *= 0.9F;
		} else {
			super.method_6091(f, g, h);
		}
	}

	@Override
	public void method_5790() {
		if (!this.world.isClient) {
			if (this.method_6034() && this.isInsideWater() && this.method_7018()) {
				this.navigation = this.field_7234;
				this.method_5796(true);
			} else {
				this.navigation = this.field_7232;
				this.method_5796(false);
			}
		}
	}

	protected boolean method_7016() {
		Path path = this.getNavigation().getCurrentPath();
		if (path != null) {
			PathNode pathNode = path.method_48();
			if (pathNode != null) {
				double d = this.squaredDistanceTo((double)pathNode.x, (double)pathNode.y, (double)pathNode.z);
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

	public void method_7013(boolean bl) {
		this.field_7233 = bl;
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
			if (this.drowned.method_7018() && this.drowned.isInsideWater()) {
				if (livingEntity != null && livingEntity.y > this.drowned.y || this.drowned.field_7233) {
					this.drowned.velocityY += 0.002;
				}

				if (this.state != MoveControl.State.field_6378 || this.drowned.getNavigation().isIdle()) {
					this.drowned.method_6125(0.0F);
					return;
				}

				double d = this.targetX - this.drowned.x;
				double e = this.targetY - this.drowned.y;
				double f = this.targetZ - this.drowned.z;
				double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
				e /= g;
				float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.drowned.yaw = this.method_6238(this.drowned.yaw, h, 90.0F);
				this.drowned.field_6283 = this.drowned.yaw;
				float i = (float)(this.speed * this.drowned.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
				this.drowned.method_6125(MathHelper.lerp(0.125F, this.drowned.method_6029(), i));
				this.drowned.velocityY = this.drowned.velocityY + (double)this.drowned.method_6029() * e * 0.1;
				this.drowned.velocityX = this.drowned.velocityX + (double)this.drowned.method_6029() * d * 0.005;
				this.drowned.velocityZ = this.drowned.velocityZ + (double)this.drowned.method_6029() * f * 0.005;
			} else {
				if (!this.drowned.onGround) {
					this.drowned.velocityY -= 0.008;
				}

				super.tick();
			}
		}
	}

	static class class_1552 extends class_1396 {
		private final DrownedEntity field_7235;

		public class_1552(DrownedEntity drownedEntity, double d, boolean bl) {
			super(drownedEntity, d, bl);
			this.field_7235 = drownedEntity;
		}

		@Override
		public boolean canStart() {
			return super.canStart() && this.field_7235.method_7012(this.field_7235.getTarget());
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && this.field_7235.method_7012(this.field_7235.getTarget());
		}
	}

	static class class_1553 implements Predicate<PlayerEntity> {
		private final DrownedEntity field_7236;

		public class_1553(DrownedEntity drownedEntity) {
			this.field_7236 = drownedEntity;
		}

		public boolean method_7020(@Nullable PlayerEntity playerEntity) {
			return this.field_7236.method_7012(playerEntity);
		}
	}

	static class class_1554 extends MoveToTargetPosGoal {
		private final DrownedEntity field_7237;

		public class_1554(DrownedEntity drownedEntity, double d) {
			super(drownedEntity, d, 8, 2);
			this.field_7237 = drownedEntity;
		}

		@Override
		public boolean canStart() {
			return super.canStart()
				&& !this.field_7237.world.isDaylight()
				&& this.field_7237.isInsideWater()
				&& this.field_7237.y >= (double)(this.field_7237.world.getSeaLevel() - 3);
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue();
		}

		@Override
		protected boolean isTargetPos(ViewableWorld viewableWorld, BlockPos blockPos) {
			BlockPos blockPos2 = blockPos.up();
			return viewableWorld.isAir(blockPos2) && viewableWorld.isAir(blockPos2.up())
				? viewableWorld.getBlockState(blockPos).hasSolidTopSurface(viewableWorld, blockPos)
				: false;
		}

		@Override
		public void start() {
			this.field_7237.method_7013(false);
			this.field_7237.navigation = this.field_7237.field_7232;
			super.start();
		}

		@Override
		public void onRemove() {
			super.onRemove();
		}
	}

	static class class_1555 extends Goal {
		private final MobEntityWithAi field_7242;
		private double field_7240;
		private double field_7239;
		private double field_7238;
		private final double field_7243;
		private final World field_7241;

		public class_1555(MobEntityWithAi mobEntityWithAi, double d) {
			this.field_7242 = mobEntityWithAi;
			this.field_7243 = d;
			this.field_7241 = mobEntityWithAi.world;
			this.setControlBits(1);
		}

		@Override
		public boolean canStart() {
			if (!this.field_7241.isDaylight()) {
				return false;
			} else if (this.field_7242.isInsideWater()) {
				return false;
			} else {
				Vec3d vec3d = this.method_7021();
				if (vec3d == null) {
					return false;
				} else {
					this.field_7240 = vec3d.x;
					this.field_7239 = vec3d.y;
					this.field_7238 = vec3d.z;
					return true;
				}
			}
		}

		@Override
		public boolean shouldContinue() {
			return !this.field_7242.getNavigation().isIdle();
		}

		@Override
		public void start() {
			this.field_7242.getNavigation().startMovingTo(this.field_7240, this.field_7239, this.field_7238, this.field_7243);
		}

		@Nullable
		private Vec3d method_7021() {
			Random random = this.field_7242.getRand();
			BlockPos blockPos = new BlockPos(this.field_7242.x, this.field_7242.getBoundingBox().minY, this.field_7242.z);

			for (int i = 0; i < 10; i++) {
				BlockPos blockPos2 = blockPos.add(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
				if (this.field_7241.getBlockState(blockPos2).getBlock() == Blocks.field_10382) {
					return new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
				}
			}

			return null;
		}
	}

	static class class_1557 extends Goal {
		private final DrownedEntity field_7246;
		private final double field_7245;
		private final int field_7247;
		private boolean field_7248;

		public class_1557(DrownedEntity drownedEntity, double d, int i) {
			this.field_7246 = drownedEntity;
			this.field_7245 = d;
			this.field_7247 = i;
		}

		@Override
		public boolean canStart() {
			return !this.field_7246.world.isDaylight() && this.field_7246.isInsideWater() && this.field_7246.y < (double)(this.field_7247 - 2);
		}

		@Override
		public boolean shouldContinue() {
			return this.canStart() && !this.field_7248;
		}

		@Override
		public void tick() {
			if (this.field_7246.y < (double)(this.field_7247 - 1) && (this.field_7246.getNavigation().isIdle() || this.field_7246.method_7016())) {
				Vec3d vec3d = class_1414.method_6373(this.field_7246, 4, 8, new Vec3d(this.field_7246.x, (double)(this.field_7247 - 1), this.field_7246.z));
				if (vec3d == null) {
					this.field_7248 = true;
					return;
				}

				this.field_7246.getNavigation().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.field_7245);
			}
		}

		@Override
		public void start() {
			this.field_7246.method_7013(true);
			this.field_7248 = false;
		}

		@Override
		public void onRemove() {
			this.field_7246.method_7013(false);
		}
	}

	static class class_1558 extends ProjectileAttackGoal {
		private final DrownedEntity field_7249;

		public class_1558(RangedAttacker rangedAttacker, double d, int i, float f) {
			super(rangedAttacker, d, i, f);
			this.field_7249 = (DrownedEntity)rangedAttacker;
		}

		@Override
		public boolean canStart() {
			return super.canStart() && this.field_7249.getMainHandStack().getItem() == Items.field_8547;
		}

		@Override
		public void start() {
			super.start();
			this.field_7249.setArmsRaised(true);
			this.field_7249.setCurrentHand(Hand.MAIN);
		}

		@Override
		public void onRemove() {
			super.onRemove();
			this.field_7249.method_6021();
			this.field_7249.setArmsRaised(false);
		}
	}
}
