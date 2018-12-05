package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1359;
import net.minecraft.class_1361;
import net.minecraft.class_1371;
import net.minecraft.class_1394;
import net.minecraft.class_3730;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.Particle;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class OcelotEntity extends AnimalEntity {
	private static final Ingredient field_16299 = Ingredient.ofItems(Items.field_8429, Items.field_8209);
	private static final TrackedData<Boolean> field_16301 = DataTracker.registerData(OcelotEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private OcelotEntity.OcelotFleeGoal<PlayerEntity> field_16300;
	private OcelotEntity.class_3703 field_16302;

	public OcelotEntity(World world) {
		super(EntityType.OCELOT, world);
		this.setSize(0.6F, 0.7F);
		this.method_16103();
	}

	private boolean method_16099() {
		return this.dataTracker.get(field_16301);
	}

	private void method_16102(boolean bl) {
		this.dataTracker.set(field_16301, bl);
		this.method_16103();
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("Trusting", this.method_16099());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.method_16102(compoundTag.getBoolean("Trusting"));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_16301, false);
	}

	@Override
	protected void method_5959() {
		this.field_16302 = new OcelotEntity.class_3703(this, 0.6, field_16299, true);
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(3, this.field_16302);
		this.goalSelector.add(7, new class_1359(this, 0.3F));
		this.goalSelector.add(8, new class_1371(this));
		this.goalSelector.add(9, new AnimalMateGoal(this, 0.8));
		this.goalSelector.add(10, new class_1394(this, 0.8, 1.0000001E-5F));
		this.goalSelector.add(11, new class_1361(this, PlayerEntity.class, 10.0F));
		this.targetSelector.add(1, new FollowTargetGoal(this, ChickenEntity.class, false));
		this.targetSelector.add(1, new FollowTargetGoal(this, TurtleEntity.class, 10, false, false, TurtleEntity.field_6921));
	}

	@Override
	public void mobTick() {
		if (this.getMoveControl().method_6241()) {
			double d = this.getMoveControl().method_6242();
			if (d == 0.6) {
				this.setSneaking(true);
				this.setSprinting(false);
			} else if (d == 1.33) {
				this.setSneaking(false);
				this.setSprinting(true);
			} else {
				this.setSneaking(false);
				this.setSprinting(false);
			}
		} else {
			this.setSneaking(false);
			this.setSprinting(false);
		}
	}

	@Override
	public boolean method_5974(double d) {
		return !this.method_16099() && this.age > 2400;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_16437;
	}

	@Override
	public int method_5970() {
		return 900;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_16441;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_16442;
	}

	@Override
	public boolean method_6121(Entity entity) {
		return entity.damage(DamageSource.mob(this), 3.0F);
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return this.isInvulnerableTo(damageSource) ? false : super.damage(damageSource, f);
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if ((this.field_16302 == null || this.field_16302.method_6313())
			&& !this.method_16099()
			&& this.method_6481(itemStack)
			&& playerEntity.squaredDistanceTo(this) < 9.0) {
			this.method_6475(playerEntity, itemStack);
			if (!this.world.isRemote) {
				if (this.random.nextInt(3) == 0) {
					this.method_16102(true);
					this.method_16100(true);
					this.world.method_8421(this, (byte)41);
				} else {
					this.method_16100(false);
					this.world.method_8421(this, (byte)40);
				}
			}

			return true;
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 41) {
			this.method_16100(true);
		} else if (b == 40) {
			this.method_16100(false);
		} else {
			super.method_5711(b);
		}
	}

	private void method_16100(boolean bl) {
		Particle particle = ParticleTypes.field_11201;
		if (!bl) {
			particle = ParticleTypes.field_11251;
		}

		for (int i = 0; i < 7; i++) {
			double d = this.random.nextGaussian() * 0.02;
			double e = this.random.nextGaussian() * 0.02;
			double f = this.random.nextGaussian() * 0.02;
			this.world
				.method_8406(
					particle,
					this.x + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
					this.y + 0.5 + (double)(this.random.nextFloat() * this.height),
					this.z + (double)(this.random.nextFloat() * this.width * 2.0F) - (double)this.width,
					d,
					e,
					f
				);
		}
	}

	protected void method_16103() {
		if (this.field_16300 == null) {
			this.field_16300 = new OcelotEntity.OcelotFleeGoal<>(this, PlayerEntity.class, 16.0F, 0.8, 1.33);
		}

		this.goalSelector.remove(this.field_16300);
		if (!this.method_16099()) {
			this.goalSelector.add(4, this.field_16300);
		}
	}

	public OcelotEntity method_16104(PassiveEntity passiveEntity) {
		return new OcelotEntity(this.world);
	}

	@Override
	public boolean method_6481(ItemStack itemStack) {
		return field_16299.matches(itemStack);
	}

	@Override
	public boolean method_5979(IWorld iWorld, class_3730 arg) {
		return this.random.nextInt(3) != 0;
	}

	@Override
	public boolean method_5957(ViewableWorld viewableWorld) {
		if (viewableWorld.method_8606(this, this.getBoundingBox())
			&& viewableWorld.method_8587(this, this.getBoundingBox())
			&& !viewableWorld.method_8599(this.getBoundingBox())) {
			BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
			if (blockPos.getY() < viewableWorld.getSeaLevel()) {
				return false;
			}

			BlockState blockState = viewableWorld.getBlockState(blockPos.down());
			Block block = blockState.getBlock();
			if (block == Blocks.field_10219 || blockState.matches(BlockTags.field_15503)) {
				return true;
			}
		}

		return false;
	}

	protected void method_16105() {
		for (int i = 0; i < 2; i++) {
			OcelotEntity ocelotEntity = new OcelotEntity(this.world);
			ocelotEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
			ocelotEntity.setBreedingAge(-24000);
			this.world.spawnEntity(ocelotEntity);
		}
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, class_3730 arg, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.method_5943(iWorld, localDifficulty, arg, entityData, compoundTag);
		if (iWorld.getRandom().nextInt(7) == 0) {
			this.method_16105();
		}

		return entityData;
	}

	static class OcelotFleeGoal<T extends Entity> extends FleeEntityGoal<T> {
		private final OcelotEntity field_16303;

		public OcelotFleeGoal(OcelotEntity ocelotEntity, Class<T> class_, float f, double d, double e) {
			super(ocelotEntity, class_, f, d, e, EntityPredicates.EXCEPT_SPECTATOR);
			this.field_16303 = ocelotEntity;
		}

		@Override
		public boolean canStart() {
			return !this.field_16303.method_16099() && super.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return !this.field_16303.method_16099() && super.shouldContinue();
		}
	}

	static class class_3703 extends TemptGoal {
		private final OcelotEntity field_16304;

		public class_3703(OcelotEntity ocelotEntity, double d, Ingredient ingredient, boolean bl) {
			super(ocelotEntity, d, ingredient, bl);
			this.field_16304 = ocelotEntity;
		}

		@Override
		protected boolean method_16081() {
			return super.method_16081() && !this.field_16304.method_16099();
		}
	}
}
