package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1361;
import net.minecraft.class_1374;
import net.minecraft.class_1376;
import net.minecraft.class_1379;
import net.minecraft.class_1399;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class PolarBearEntity extends AnimalEntity {
	private static final TrackedData<Boolean> field_6840 = DataTracker.registerData(PolarBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private float field_6838;
	private float field_6837;
	private int field_6839;

	public PolarBearEntity(World world) {
		super(EntityType.POLAR_BEAR, world);
		this.setSize(1.3F, 1.4F);
	}

	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		return new PolarBearEntity(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return false;
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new PolarBearEntity.class_1460());
		this.goalSelector.add(1, new PolarBearEntity.class_1461());
		this.goalSelector.add(4, new FollowParentGoal(this, 1.25));
		this.goalSelector.add(5, new class_1379(this, 1.0));
		this.goalSelector.add(6, new class_1361(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(7, new class_1376(this));
		this.targetSelector.add(1, new PolarBearEntity.class_1459());
		this.targetSelector.add(2, new PolarBearEntity.class_1457());
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(20.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
		this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.getBoundingBox().minY);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		Biome biome = iWorld.getBiome(blockPos);
		return biome != Biomes.field_9435 && biome != Biomes.field_9418
			? super.canSpawn(iWorld, spawnType)
			: iWorld.getLightLevel(blockPos, 0) > 8 && iWorld.getBlockState(blockPos.down()).getBlock() == Blocks.field_10295;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isChild() ? SoundEvents.field_14605 : SoundEvents.field_15078;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15107;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15209;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSoundAtEntity(SoundEvents.field_15036, 0.15F, 1.0F);
	}

	protected void method_6602() {
		if (this.field_6839 <= 0) {
			this.playSoundAtEntity(SoundEvents.field_14937, 1.0F, 1.0F);
			this.field_6839 = 40;
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_6840, false);
	}

	@Override
	public void update() {
		super.update();
		if (this.world.isClient) {
			this.field_6838 = this.field_6837;
			if (this.method_6600()) {
				this.field_6837 = MathHelper.clamp(this.field_6837 + 1.0F, 0.0F, 6.0F);
			} else {
				this.field_6837 = MathHelper.clamp(this.field_6837 - 1.0F, 0.0F, 6.0F);
			}
		}

		if (this.field_6839 > 0) {
			this.field_6839--;
		}
	}

	@Override
	public boolean method_6121(Entity entity) {
		boolean bl = entity.damage(DamageSource.mob(this), (float)((int)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue()));
		if (bl) {
			this.method_5723(this, entity);
		}

		return bl;
	}

	public boolean method_6600() {
		return this.dataTracker.get(field_6840);
	}

	public void method_6603(boolean bl) {
		this.dataTracker.set(field_6840, bl);
	}

	@Environment(EnvType.CLIENT)
	public float method_6601(float f) {
		return MathHelper.lerp(f, this.field_6838, this.field_6837) / 6.0F;
	}

	@Override
	protected float method_6120() {
		return 0.98F;
	}

	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		if (entityData instanceof PolarBearEntity.class_1458) {
			if (((PolarBearEntity.class_1458)entityData).field_6842) {
				this.setBreedingAge(-24000);
			}
		} else {
			PolarBearEntity.class_1458 lv = new PolarBearEntity.class_1458();
			lv.field_6842 = true;
			entityData = lv;
		}

		return entityData;
	}

	class class_1457 extends FollowTargetGoal<PlayerEntity> {
		public class_1457() {
			super(PolarBearEntity.this, PlayerEntity.class, 20, true, true, null);
		}

		@Override
		public boolean canStart() {
			if (PolarBearEntity.this.isChild()) {
				return false;
			} else {
				if (super.canStart()) {
					for (PolarBearEntity polarBearEntity : PolarBearEntity.this.world
						.getVisibleEntities(PolarBearEntity.class, PolarBearEntity.this.getBoundingBox().expand(8.0, 4.0, 8.0))) {
						if (polarBearEntity.isChild()) {
							return true;
						}
					}
				}

				PolarBearEntity.this.setTarget(null);
				return false;
			}
		}

		@Override
		protected double getFollowRange() {
			return super.getFollowRange() * 0.5;
		}
	}

	static class class_1458 implements EntityData {
		public boolean field_6842;

		private class_1458() {
		}
	}

	class class_1459 extends class_1399 {
		public class_1459() {
			super(PolarBearEntity.this);
		}

		@Override
		public void start() {
			super.start();
			if (PolarBearEntity.this.isChild()) {
				this.method_6317();
				this.onRemove();
			}
		}

		@Override
		protected void method_6319(MobEntityWithAi mobEntityWithAi, LivingEntity livingEntity) {
			if (mobEntityWithAi instanceof PolarBearEntity && !mobEntityWithAi.isChild()) {
				super.method_6319(mobEntityWithAi, livingEntity);
			}
		}
	}

	class class_1460 extends MeleeAttackGoal {
		public class_1460() {
			super(PolarBearEntity.this, 1.25, true);
		}

		@Override
		protected void method_6288(LivingEntity livingEntity, double d) {
			double e = this.method_6289(livingEntity);
			if (d <= e && this.field_6505 <= 0) {
				this.field_6505 = 20;
				this.entity.method_6121(livingEntity);
				PolarBearEntity.this.method_6603(false);
			} else if (d <= e * 2.0) {
				if (this.field_6505 <= 0) {
					PolarBearEntity.this.method_6603(false);
					this.field_6505 = 20;
				}

				if (this.field_6505 <= 10) {
					PolarBearEntity.this.method_6603(true);
					PolarBearEntity.this.method_6602();
				}
			} else {
				this.field_6505 = 20;
				PolarBearEntity.this.method_6603(false);
			}
		}

		@Override
		public void onRemove() {
			PolarBearEntity.this.method_6603(false);
			super.onRemove();
		}

		@Override
		protected double method_6289(LivingEntity livingEntity) {
			return (double)(4.0F + livingEntity.width);
		}
	}

	class class_1461 extends class_1374 {
		public class_1461() {
			super(PolarBearEntity.this, 2.0);
		}

		@Override
		public boolean canStart() {
			return !PolarBearEntity.this.isChild() && !PolarBearEntity.this.isOnFire() ? false : super.canStart();
		}
	}
}
