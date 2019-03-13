package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_1414;
import net.minecraft.class_4051;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.AmphibiousPathNodeMaker;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class TurtleEntity extends AnimalEntity {
	private static final TrackedData<BlockPos> field_6920 = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
	private static final TrackedData<Boolean> field_6919 = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_6923 = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<BlockPos> field_6922 = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
	private static final TrackedData<Boolean> field_6924 = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_6925 = DataTracker.registerData(TurtleEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int field_6918;
	public static final Predicate<LivingEntity> BABY_TURTLE_ON_LAND_FILTER = livingEntity -> livingEntity.isChild() && !livingEntity.isInsideWater();

	public TurtleEntity(EntityType<? extends TurtleEntity> entityType, World world) {
		super(entityType, world);
		this.field_6207 = new TurtleEntity.TurtleMoveControl(this);
		this.field_6746 = Blocks.field_10102;
		this.stepHeight = 1.0F;
	}

	public void method_6683(BlockPos blockPos) {
		this.field_6011.set(field_6920, blockPos);
	}

	private BlockPos method_6693() {
		return this.field_6011.get(field_6920);
	}

	private void method_6699(BlockPos blockPos) {
		this.field_6011.set(field_6922, blockPos);
	}

	private BlockPos method_6687() {
		return this.field_6011.get(field_6922);
	}

	public boolean getHasEgg() {
		return this.field_6011.get(field_6919);
	}

	private void setHasEgg(boolean bl) {
		this.field_6011.set(field_6919, bl);
	}

	public boolean method_6695() {
		return this.field_6011.get(field_6923);
	}

	private void method_6676(boolean bl) {
		this.field_6918 = bl ? 1 : 0;
		this.field_6011.set(field_6923, bl);
	}

	private boolean method_6684() {
		return this.field_6011.get(field_6924);
	}

	private void method_6697(boolean bl) {
		this.field_6011.set(field_6924, bl);
	}

	private boolean method_6691() {
		return this.field_6011.get(field_6925);
	}

	private void method_6696(boolean bl) {
		this.field_6011.set(field_6925, bl);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_6920, BlockPos.ORIGIN);
		this.field_6011.startTracking(field_6919, false);
		this.field_6011.startTracking(field_6922, BlockPos.ORIGIN);
		this.field_6011.startTracking(field_6924, false);
		this.field_6011.startTracking(field_6925, false);
		this.field_6011.startTracking(field_6923, false);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("HomePosX", this.method_6693().getX());
		compoundTag.putInt("HomePosY", this.method_6693().getY());
		compoundTag.putInt("HomePosZ", this.method_6693().getZ());
		compoundTag.putBoolean("HasEgg", this.getHasEgg());
		compoundTag.putInt("TravelPosX", this.method_6687().getX());
		compoundTag.putInt("TravelPosY", this.method_6687().getY());
		compoundTag.putInt("TravelPosZ", this.method_6687().getZ());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		int i = compoundTag.getInt("HomePosX");
		int j = compoundTag.getInt("HomePosY");
		int k = compoundTag.getInt("HomePosZ");
		this.method_6683(new BlockPos(i, j, k));
		super.method_5749(compoundTag);
		this.setHasEgg(compoundTag.getBoolean("HasEgg"));
		int l = compoundTag.getInt("TravelPosX");
		int m = compoundTag.getInt("TravelPosY");
		int n = compoundTag.getInt("TravelPosZ");
		this.method_6699(new BlockPos(l, m, n));
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.method_6683(new BlockPos(this));
		this.method_6699(BlockPos.ORIGIN);
		return super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Override
	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		BlockPos blockPos = new BlockPos(this.x, this.method_5829().minY, this.z);
		return blockPos.getY() < iWorld.getSeaLevel() + 4 && super.method_5979(iWorld, spawnType);
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(0, new TurtleEntity.class_1487(this, 1.2));
		this.field_6201.add(1, new TurtleEntity.TurtleMateGoal(this, 1.0));
		this.field_6201.add(1, new TurtleEntity.class_1485(this, 1.0));
		this.field_6201.add(2, new TurtleEntity.class_1490(this, 1.1, Blocks.field_10376.getItem()));
		this.field_6201.add(3, new TurtleEntity.class_1484(this, 1.0));
		this.field_6201.add(4, new TurtleEntity.class_1483(this, 1.0));
		this.field_6201.add(7, new TurtleEntity.class_1491(this, 1.0));
		this.field_6201.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.field_6201.add(9, new TurtleEntity.class_1489(this, 1.0, 100));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	public boolean canFly() {
		return false;
	}

	@Override
	public boolean canBreatheInWater() {
		return true;
	}

	@Override
	public EntityGroup method_6046() {
		return EntityGroup.AQUATIC;
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 200;
	}

	@Nullable
	@Override
	protected SoundEvent method_5994() {
		return !this.isInsideWater() && this.onGround && !this.isChild() ? SoundEvents.field_14722 : super.method_5994();
	}

	@Override
	protected void method_5734(float f) {
		super.method_5734(f * 1.5F);
	}

	@Override
	protected SoundEvent method_5737() {
		return SoundEvents.field_14764;
	}

	@Nullable
	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return this.isChild() ? SoundEvents.field_15070 : SoundEvents.field_15183;
	}

	@Nullable
	@Override
	protected SoundEvent method_6002() {
		return this.isChild() ? SoundEvents.field_14618 : SoundEvents.field_14856;
	}

	@Override
	protected void method_5712(BlockPos blockPos, BlockState blockState) {
		SoundEvent soundEvent = this.isChild() ? SoundEvents.field_14864 : SoundEvents.field_14549;
		this.method_5783(soundEvent, 0.15F, 1.0F);
	}

	@Override
	public boolean method_6482() {
		return super.method_6482() && !this.getHasEgg();
	}

	@Override
	protected float method_5867() {
		return this.field_5994 + 0.15F;
	}

	@Override
	public float method_17825() {
		return this.isChild() ? 0.3F : 1.0F;
	}

	@Override
	protected EntityNavigation method_5965(World world) {
		return new TurtleEntity.TurtleSwimNavigation(this, world);
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		return EntityType.TURTLE.method_5883(this.field_6002);
	}

	@Override
	public boolean method_6481(ItemStack itemStack) {
		return itemStack.getItem() == Blocks.field_10376.getItem();
	}

	@Override
	public float method_6144(BlockPos blockPos, ViewableWorld viewableWorld) {
		return !this.method_6684() && viewableWorld.method_8316(blockPos).method_15767(FluidTags.field_15517) ? 10.0F : super.method_6144(blockPos, viewableWorld);
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		if (this.isValid() && this.method_6695() && this.field_6918 >= 1 && this.field_6918 % 5 == 0) {
			BlockPos blockPos = new BlockPos(this);
			if (this.field_6002.method_8320(blockPos.down()).getBlock() == Blocks.field_10102) {
				this.field_6002.method_8535(2001, blockPos, Block.method_9507(Blocks.field_10102.method_9564()));
			}
		}
	}

	@Override
	protected void method_5619() {
		super.method_5619();
		if (!this.isChild() && this.field_6002.getGameRules().getBoolean("doMobLoot")) {
			this.method_5870(Items.field_8161, 1);
		}
	}

	@Override
	public void method_6091(Vec3d vec3d) {
		if (this.method_6034() && this.isInsideWater()) {
			this.method_5724(0.1F, vec3d);
			this.method_5784(MovementType.field_6308, this.method_18798());
			this.method_18799(this.method_18798().multiply(0.9));
			if (this.getTarget() == null && (!this.method_6684() || !(this.method_5831(this.method_6693()) < 400.0))) {
				this.method_18799(this.method_18798().add(0.0, -0.005, 0.0));
			}
		} else {
			super.method_6091(vec3d);
		}
	}

	@Override
	public boolean method_5931(PlayerEntity playerEntity) {
		return false;
	}

	@Override
	public void method_5800(LightningEntity lightningEntity) {
		this.damage(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
	}

	static class TurtleMateGoal extends AnimalMateGoal {
		private final TurtleEntity field_6926;

		TurtleMateGoal(TurtleEntity turtleEntity, double d) {
			super(turtleEntity, d);
			this.field_6926 = turtleEntity;
		}

		@Override
		public boolean canStart() {
			return super.canStart() && !this.field_6926.getHasEgg();
		}

		@Override
		protected void method_6249() {
			ServerPlayerEntity serverPlayerEntity = this.field_6404.method_6478();
			if (serverPlayerEntity == null && this.field_6406.method_6478() != null) {
				serverPlayerEntity = this.field_6406.method_6478();
			}

			if (serverPlayerEntity != null) {
				serverPlayerEntity.method_7281(Stats.field_15410);
				Criterions.BRED_ANIMALS.method_855(serverPlayerEntity, this.field_6404, this.field_6406, null);
			}

			this.field_6926.setHasEgg(true);
			this.field_6404.resetLoveTicks();
			this.field_6406.resetLoveTicks();
			Random random = this.field_6404.getRand();
			if (this.field_6405.getGameRules().getBoolean("doMobLoot")) {
				this.field_6405.spawnEntity(new ExperienceOrbEntity(this.field_6405, this.field_6404.x, this.field_6404.y, this.field_6404.z, random.nextInt(7) + 1));
			}
		}
	}

	static class TurtleMoveControl extends MoveControl {
		private final TurtleEntity turtle;

		TurtleMoveControl(TurtleEntity turtleEntity) {
			super(turtleEntity);
			this.turtle = turtleEntity;
		}

		private void method_6700() {
			if (this.turtle.isInsideWater()) {
				this.turtle.method_18799(this.turtle.method_18798().add(0.0, 0.005, 0.0));
				if (this.turtle.method_5831(this.turtle.method_6693()) > 256.0) {
					this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 2.0F, 0.08F));
				}

				if (this.turtle.isChild()) {
					this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 3.0F, 0.06F));
				}
			} else if (this.turtle.onGround) {
				this.turtle.setMovementSpeed(Math.max(this.turtle.getMovementSpeed() / 2.0F, 0.06F));
			}
		}

		@Override
		public void tick() {
			this.method_6700();
			if (this.state == MoveControl.State.field_6378 && !this.turtle.method_5942().isIdle()) {
				double d = this.targetX - this.turtle.x;
				double e = this.targetY - this.turtle.y;
				double f = this.targetZ - this.turtle.z;
				double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
				e /= g;
				float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.turtle.yaw = this.method_6238(this.turtle.yaw, h, 90.0F);
				this.turtle.field_6283 = this.turtle.yaw;
				float i = (float)(this.speed * this.turtle.method_5996(EntityAttributes.MOVEMENT_SPEED).getValue());
				this.turtle.setMovementSpeed(MathHelper.lerp(0.125F, this.turtle.getMovementSpeed(), i));
				this.turtle.method_18799(this.turtle.method_18798().add(0.0, (double)this.turtle.getMovementSpeed() * e * 0.1, 0.0));
			} else {
				this.turtle.setMovementSpeed(0.0F);
			}
		}
	}

	static class TurtleSwimNavigation extends SwimNavigation {
		TurtleSwimNavigation(TurtleEntity turtleEntity, World world) {
			super(turtleEntity, world);
		}

		@Override
		protected boolean isAtValidPosition() {
			return true;
		}

		@Override
		protected PathNodeNavigator method_6336(int i) {
			return new PathNodeNavigator(new AmphibiousPathNodeMaker(), i);
		}

		@Override
		public boolean method_6333(BlockPos blockPos) {
			if (this.entity instanceof TurtleEntity) {
				TurtleEntity turtleEntity = (TurtleEntity)this.entity;
				if (turtleEntity.method_6691()) {
					return this.field_6677.method_8320(blockPos).getBlock() == Blocks.field_10382;
				}
			}

			return !this.field_6677.method_8320(blockPos.down()).isAir();
		}
	}

	static class class_1483 extends Goal {
		private final TurtleEntity field_6930;
		private final double field_6927;
		private boolean field_6929;
		private int field_6928;

		class_1483(TurtleEntity turtleEntity, double d) {
			this.field_6930 = turtleEntity;
			this.field_6927 = d;
		}

		@Override
		public boolean canStart() {
			if (this.field_6930.isChild()) {
				return false;
			} else if (this.field_6930.getHasEgg()) {
				return true;
			} else {
				return this.field_6930.getRand().nextInt(700) != 0 ? false : this.field_6930.method_5831(this.field_6930.method_6693()) >= 4096.0;
			}
		}

		@Override
		public void start() {
			this.field_6930.method_6697(true);
			this.field_6929 = false;
			this.field_6928 = 0;
		}

		@Override
		public void onRemove() {
			this.field_6930.method_6697(false);
		}

		@Override
		public boolean shouldContinue() {
			return this.field_6930.method_5831(this.field_6930.method_6693()) >= 49.0 && !this.field_6929 && this.field_6928 <= 600;
		}

		@Override
		public void tick() {
			BlockPos blockPos = this.field_6930.method_6693();
			boolean bl = this.field_6930.method_5831(blockPos) <= 256.0;
			if (bl) {
				this.field_6928++;
			}

			if (this.field_6930.method_5942().isIdle()) {
				Vec3d vec3d = class_1414.method_6377(
					this.field_6930, 16, 3, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), (float) (Math.PI / 10)
				);
				if (vec3d == null) {
					vec3d = class_1414.method_6373(this.field_6930, 8, 7, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
				}

				if (vec3d != null && !bl && this.field_6930.field_6002.method_8320(new BlockPos(vec3d)).getBlock() != Blocks.field_10382) {
					vec3d = class_1414.method_6373(this.field_6930, 16, 5, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
				}

				if (vec3d == null) {
					this.field_6929 = true;
					return;
				}

				this.field_6930.method_5942().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.field_6927);
			}
		}
	}

	static class class_1484 extends MoveToTargetPosGoal {
		private final TurtleEntity field_6931;

		private class_1484(TurtleEntity turtleEntity, double d) {
			super(turtleEntity, turtleEntity.isChild() ? 2.0 : d, 24);
			this.field_6931 = turtleEntity;
			this.field_6515 = -1;
		}

		@Override
		public boolean shouldContinue() {
			return !this.field_6931.isInsideWater() && this.tryingTime <= 1200 && this.method_6296(this.field_6931.field_6002, this.field_6512);
		}

		@Override
		public boolean canStart() {
			if (this.field_6931.isChild() && !this.field_6931.isInsideWater()) {
				return super.canStart();
			} else {
				return !this.field_6931.method_6684() && !this.field_6931.isInsideWater() && !this.field_6931.getHasEgg() ? super.canStart() : false;
			}
		}

		@Override
		public boolean shouldResetPath() {
			return this.tryingTime % 160 == 0;
		}

		@Override
		protected boolean method_6296(ViewableWorld viewableWorld, BlockPos blockPos) {
			Block block = viewableWorld.method_8320(blockPos).getBlock();
			return block == Blocks.field_10382;
		}
	}

	static class class_1485 extends MoveToTargetPosGoal {
		private final TurtleEntity field_6932;

		class_1485(TurtleEntity turtleEntity, double d) {
			super(turtleEntity, d, 16);
			this.field_6932 = turtleEntity;
		}

		@Override
		public boolean canStart() {
			return this.field_6932.getHasEgg() && this.field_6932.method_5831(this.field_6932.method_6693()) < 81.0 ? super.canStart() : false;
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && this.field_6932.getHasEgg() && this.field_6932.method_5831(this.field_6932.method_6693()) < 81.0;
		}

		@Override
		public void tick() {
			super.tick();
			BlockPos blockPos = new BlockPos(this.field_6932);
			if (!this.field_6932.isInsideWater() && this.hasReached()) {
				if (this.field_6932.field_6918 < 1) {
					this.field_6932.method_6676(true);
				} else if (this.field_6932.field_6918 > 200) {
					World world = this.field_6932.field_6002;
					world.method_8396(null, blockPos, SoundEvents.field_14634, SoundCategory.field_15245, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
					world.method_8652(
						this.field_6512.up(),
						Blocks.field_10195.method_9564().method_11657(TurtleEggBlock.field_11710, Integer.valueOf(this.field_6932.random.nextInt(4) + 1)),
						3
					);
					this.field_6932.setHasEgg(false);
					this.field_6932.method_6676(false);
					this.field_6932.setLoveTicks(600);
				}

				if (this.field_6932.method_6695()) {
					this.field_6932.field_6918++;
				}
			}
		}

		@Override
		protected boolean method_6296(ViewableWorld viewableWorld, BlockPos blockPos) {
			if (!viewableWorld.method_8623(blockPos.up())) {
				return false;
			} else {
				Block block = viewableWorld.method_8320(blockPos).getBlock();
				return block == Blocks.field_10102;
			}
		}
	}

	static class class_1487 extends EscapeDangerGoal {
		class_1487(TurtleEntity turtleEntity, double d) {
			super(turtleEntity, d);
		}

		@Override
		public boolean canStart() {
			if (this.owner.getAttacker() == null && !this.owner.isOnFire()) {
				return false;
			} else {
				BlockPos blockPos = this.method_6300(this.owner.field_6002, this.owner, 7, 4);
				if (blockPos != null) {
					this.targetX = (double)blockPos.getX();
					this.targetY = (double)blockPos.getY();
					this.targetZ = (double)blockPos.getZ();
					return true;
				} else {
					return this.method_6301();
				}
			}
		}
	}

	static class class_1489 extends WanderAroundGoal {
		private final TurtleEntity field_6934;

		private class_1489(TurtleEntity turtleEntity, double d, int i) {
			super(turtleEntity, d, i);
			this.field_6934 = turtleEntity;
		}

		@Override
		public boolean canStart() {
			return !this.owner.isInsideWater() && !this.field_6934.method_6684() && !this.field_6934.getHasEgg() ? super.canStart() : false;
		}
	}

	static class class_1490 extends Goal {
		private static final class_4051 field_18117 = new class_4051().method_18418(10.0).method_18421().method_18417();
		private final TurtleEntity field_6938;
		private final double field_6935;
		private PlayerEntity field_6939;
		private int field_6936;
		private final Set<Item> field_6937;

		class_1490(TurtleEntity turtleEntity, double d, Item item) {
			this.field_6938 = turtleEntity;
			this.field_6935 = d;
			this.field_6937 = Sets.<Item>newHashSet(item);
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
		}

		@Override
		public boolean canStart() {
			if (this.field_6936 > 0) {
				this.field_6936--;
				return false;
			} else {
				this.field_6939 = this.field_6938.field_6002.method_18462(field_18117, this.field_6938);
				return this.field_6939 == null ? false : this.method_6701(this.field_6939.method_6047()) || this.method_6701(this.field_6939.method_6079());
			}
		}

		private boolean method_6701(ItemStack itemStack) {
			return this.field_6937.contains(itemStack.getItem());
		}

		@Override
		public boolean shouldContinue() {
			return this.canStart();
		}

		@Override
		public void onRemove() {
			this.field_6939 = null;
			this.field_6938.method_5942().stop();
			this.field_6936 = 100;
		}

		@Override
		public void tick() {
			this.field_6938.method_5988().lookAt(this.field_6939, (float)(this.field_6938.method_5986() + 20), (float)this.field_6938.method_5978());
			if (this.field_6938.squaredDistanceTo(this.field_6939) < 6.25) {
				this.field_6938.method_5942().stop();
			} else {
				this.field_6938.method_5942().startMovingTo(this.field_6939, this.field_6935);
			}
		}
	}

	static class class_1491 extends Goal {
		private final TurtleEntity field_6942;
		private final double field_6940;
		private boolean field_6941;

		class_1491(TurtleEntity turtleEntity, double d) {
			this.field_6942 = turtleEntity;
			this.field_6940 = d;
		}

		@Override
		public boolean canStart() {
			return !this.field_6942.method_6684() && !this.field_6942.getHasEgg() && this.field_6942.isInsideWater();
		}

		@Override
		public void start() {
			int i = 512;
			int j = 4;
			Random random = this.field_6942.random;
			int k = random.nextInt(1025) - 512;
			int l = random.nextInt(9) - 4;
			int m = random.nextInt(1025) - 512;
			if ((double)l + this.field_6942.y > (double)(this.field_6942.field_6002.getSeaLevel() - 1)) {
				l = 0;
			}

			BlockPos blockPos = new BlockPos((double)k + this.field_6942.x, (double)l + this.field_6942.y, (double)m + this.field_6942.z);
			this.field_6942.method_6699(blockPos);
			this.field_6942.method_6696(true);
			this.field_6941 = false;
		}

		@Override
		public void tick() {
			if (this.field_6942.method_5942().isIdle()) {
				BlockPos blockPos = this.field_6942.method_6687();
				Vec3d vec3d = class_1414.method_6377(
					this.field_6942, 16, 3, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()), (float) (Math.PI / 10)
				);
				if (vec3d == null) {
					vec3d = class_1414.method_6373(this.field_6942, 8, 7, new Vec3d((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
				}

				if (vec3d != null) {
					int i = MathHelper.floor(vec3d.x);
					int j = MathHelper.floor(vec3d.z);
					int k = 34;
					if (!this.field_6942.field_6002.isAreaLoaded(i - 34, 0, j - 34, i + 34, 0, j + 34)) {
						vec3d = null;
					}
				}

				if (vec3d == null) {
					this.field_6941 = true;
					return;
				}

				this.field_6942.method_5942().startMovingTo(vec3d.x, vec3d.y, vec3d.z, this.field_6940);
			}
		}

		@Override
		public boolean shouldContinue() {
			return !this.field_6942.method_5942().isIdle()
				&& !this.field_6941
				&& !this.field_6942.method_6684()
				&& !this.field_6942.isInLove()
				&& !this.field_6942.getHasEgg();
		}

		@Override
		public void onRemove() {
			this.field_6942.method_6696(false);
			super.onRemove();
		}
	}
}
