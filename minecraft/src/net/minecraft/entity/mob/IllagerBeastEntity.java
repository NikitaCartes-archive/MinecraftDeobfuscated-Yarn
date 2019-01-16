package net.minecraft.entity.mob;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1361;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class IllagerBeastEntity extends RaiderEntity {
	private static final Predicate<Entity> field_7301 = entity -> entity.isValid() && !(entity instanceof IllagerBeastEntity);
	private int attackTick;
	private int stunTick;
	private int roarTick;

	public IllagerBeastEntity(World world) {
		super(EntityType.ILLAGER_BEAST, world);
		this.stepHeight = 1.0F;
		this.experiencePoints = 20;
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(4, new IllagerBeastEntity.class_1585());
		this.goalSelector.add(5, new class_1394(this, 0.4));
		this.goalSelector.add(6, new class_1361(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(10, new class_1361(this, MobEntity.class, 8.0F));
		this.targetSelector.add(2, new class_1399(this, IllagerEntity.class).method_6318());
		this.targetSelector.add(3, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(4, new FollowTargetGoal(this, VillagerEntity.class, true));
		this.targetSelector.add(4, new FollowTargetGoal(this, IronGolemEntity.class, true));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(100.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
		this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(12.0);
		this.getAttributeInstance(EntityAttributes.ATTACK_KNOCKBACK).setBaseValue(1.5);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(32.0);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("AttackTick", this.attackTick);
		compoundTag.putInt("StunTick", this.stunTick);
		compoundTag.putInt("RoarTick", this.roarTick);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.attackTick = compoundTag.getInt("AttackTick");
		this.stunTick = compoundTag.getInt("StunTick");
		this.roarTick = compoundTag.getInt("RoarTick");
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new IllagerBeastEntity.class_1586(this, world);
	}

	@Override
	public int method_5986() {
		return 45;
	}

	@Override
	public double getMountedHeightOffset() {
		return 2.1;
	}

	@Override
	public boolean method_5956() {
		return true;
	}

	@Nullable
	@Override
	public Entity getPrimaryPassenger() {
		return this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		if (this.method_6062()) {
			this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.0);
		} else {
			double d = this.getTarget() != null ? 0.35 : 0.3;
			double e = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getBaseValue();
			this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(MathHelper.lerp(0.1, e, d));
		}

		if (this.horizontalCollision && this.world.getGameRules().getBoolean("mobGriefing")) {
			boolean bl = false;
			BoundingBox boundingBox = this.getBoundingBox().expand(0.2);

			for (BlockPos blockPos : BlockPos.iterateBoxPositions(
				MathHelper.floor(boundingBox.minX),
				MathHelper.floor(boundingBox.minY),
				MathHelper.floor(boundingBox.minZ),
				MathHelper.floor(boundingBox.maxX),
				MathHelper.floor(boundingBox.maxY),
				MathHelper.floor(boundingBox.maxZ)
			)) {
				BlockState blockState = this.world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (block instanceof LeavesBlock) {
					bl = this.world.breakBlock(blockPos, true) || bl;
				}
			}

			if (!bl && this.onGround) {
				this.method_6043();
			}
		}

		if (this.roarTick > 0) {
			this.roarTick--;
			if (this.roarTick == 10) {
				this.method_7071();
			}
		}

		if (this.attackTick > 0) {
			this.attackTick--;
		}

		if (this.stunTick > 0) {
			this.stunTick--;
			this.method_7073();
			if (this.stunTick == 0) {
				this.playSound(SoundEvents.field_14733, 1.0F, 1.0F);
				this.roarTick = 20;
			}
		}
	}

	private void method_7073() {
		if (this.random.nextInt(6) == 0) {
			double d = this.x - (double)this.getWidth() * Math.sin((double)(this.field_6283 * (float) (Math.PI / 180.0))) + (this.random.nextDouble() * 0.6 - 0.3);
			double e = this.y + (double)this.getHeight() - 0.3;
			double f = this.z + (double)this.getWidth() * Math.cos((double)(this.field_6283 * (float) (Math.PI / 180.0))) + (this.random.nextDouble() * 0.6 - 0.3);
			this.world.addParticle(ParticleTypes.field_11226, d, e, f, 0.4980392156862745, 0.5137254901960784, 0.5725490196078431);
		}
	}

	@Override
	protected boolean method_6062() {
		return super.method_6062() || this.attackTick > 0 || this.stunTick > 0 || this.roarTick > 0;
	}

	@Override
	public boolean canSee(Entity entity) {
		return this.stunTick <= 0 && this.roarTick <= 0 ? super.canSee(entity) : false;
	}

	@Override
	protected void method_6060(LivingEntity livingEntity) {
		if (this.roarTick == 0) {
			if (this.random.nextDouble() < 0.5) {
				this.stunTick = 40;
				this.playSound(SoundEvents.field_14822, 1.0F, 1.0F);
				this.world.summonParticle(this, (byte)39);
				livingEntity.pushAwayFrom(this);
			} else {
				this.method_7068(livingEntity);
			}

			livingEntity.velocityModified = true;
		}
	}

	private void method_7071() {
		if (this.isValid()) {
			for (Entity entity : this.world.getEntities(LivingEntity.class, this.getBoundingBox().expand(4.0), field_7301)) {
				if (!(entity instanceof IllagerEntity)) {
					entity.damage(DamageSource.mob(this), 6.0F);
				}

				this.method_7068(entity);
			}

			Vec3d vec3d = this.getBoundingBox().getCenter();

			for (int i = 0; i < 40; i++) {
				double d = this.random.nextGaussian() * 0.2;
				double e = this.random.nextGaussian() * 0.2;
				double f = this.random.nextGaussian() * 0.2;
				this.world.addParticle(ParticleTypes.field_11203, vec3d.x, vec3d.y, vec3d.z, d, e, f);
			}
		}
	}

	private void method_7068(Entity entity) {
		double d = entity.x - this.x;
		double e = entity.z - this.z;
		double f = d * d + e * e;
		entity.addVelocity(d / f * 4.0, 0.2, e / f * 4.0);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 4) {
			this.attackTick = 10;
			this.playSound(SoundEvents.field_15240, 1.0F, 1.0F);
		} else if (b == 39) {
			this.stunTick = 40;
		}

		super.method_5711(b);
	}

	@Environment(EnvType.CLIENT)
	public int method_7070() {
		return this.attackTick;
	}

	@Environment(EnvType.CLIENT)
	public int method_7074() {
		return this.stunTick;
	}

	@Environment(EnvType.CLIENT)
	public int method_7072() {
		return this.roarTick;
	}

	@Override
	public boolean method_6121(Entity entity) {
		this.attackTick = 10;
		this.world.summonParticle(this, (byte)4);
		this.playSound(SoundEvents.field_15240, 1.0F, 1.0F);
		return super.method_6121(entity);
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14639;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15007;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15146;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.field_14929, 0.15F, 1.0F);
	}

	@Override
	public boolean method_5957(ViewableWorld viewableWorld) {
		return !viewableWorld.method_8599(this.getBoundingBox());
	}

	@Override
	public void addBonusForWave(int i, boolean bl) {
	}

	@Override
	public boolean canLead() {
		return false;
	}

	class class_1585 extends MeleeAttackGoal {
		public class_1585() {
			super(IllagerBeastEntity.this, 1.0, true);
		}

		@Override
		protected double method_6289(LivingEntity livingEntity) {
			float f = IllagerBeastEntity.this.getWidth() - 0.1F;
			return (double)(f * 2.0F * f * 2.0F + livingEntity.getWidth());
		}
	}

	static class class_1586 extends EntityMobNavigation {
		public class_1586(MobEntity mobEntity, World world) {
			super(mobEntity, world);
		}

		@Override
		protected PathNodeNavigator createPathNodeNavigator() {
			this.field_6678 = new IllagerBeastEntity.class_1587();
			return new PathNodeNavigator(this.field_6678);
		}
	}

	static class class_1587 extends LandPathNodeMaker {
		private class_1587() {
		}

		@Override
		protected PathNodeType method_61(BlockView blockView, boolean bl, boolean bl2, BlockPos blockPos, PathNodeType pathNodeType) {
			return pathNodeType == PathNodeType.field_6 ? PathNodeType.AIR : super.method_61(blockView, bl, bl2, blockPos, pathNodeType);
		}
	}
}
