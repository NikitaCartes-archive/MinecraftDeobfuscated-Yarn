package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class RabbitEntity extends AnimalEntity {
	private static final TrackedData<Integer> RABBIT_TYPE = DataTracker.registerData(RabbitEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Identifier KILLER_BUNNY = new Identifier("killer_bunny");
	private int jumpTicks;
	private int jumpDuration;
	private boolean lastOnGround;
	private int ticksUntilJump;
	private int moreCarrotTicks;

	public RabbitEntity(EntityType<? extends RabbitEntity> entityType, World world) {
		super(entityType, world);
		this.jumpControl = new RabbitEntity.RabbitJumpControl(this);
		this.moveControl = new RabbitEntity.RabbitMoveControl(this);
		this.setSpeed(0.0);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(1, new RabbitEntity.RabbitEscapeDangerGoal(this, 2.2));
		this.goalSelector.add(2, new AnimalMateGoal(this, 0.8));
		this.goalSelector.add(3, new TemptGoal(this, 1.0, Ingredient.ofItems(Items.field_8179, Items.field_8071, Blocks.field_10182), false));
		this.goalSelector.add(4, new RabbitEntity.RabbitFleeGoal(this, PlayerEntity.class, 8.0F, 2.2, 2.2));
		this.goalSelector.add(4, new RabbitEntity.RabbitFleeGoal(this, WolfEntity.class, 10.0F, 2.2, 2.2));
		this.goalSelector.add(4, new RabbitEntity.RabbitFleeGoal(this, HostileEntity.class, 4.0F, 2.2, 2.2));
		this.goalSelector.add(5, new RabbitEntity.class_1470(this));
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6));
		this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
	}

	@Override
	protected float getJumpVelocity() {
		if (!this.horizontalCollision && (!this.moveControl.isMoving() || !(this.moveControl.getTargetY() > this.y + 0.5))) {
			Path path = this.navigation.getCurrentPath();
			if (path != null && path.getCurrentNodeIndex() < path.getLength()) {
				Vec3d vec3d = path.getNodePosition(this);
				if (vec3d.y > this.y + 0.5) {
					return 0.5F;
				}
			}

			return this.moveControl.getSpeed() <= 0.6 ? 0.2F : 0.3F;
		} else {
			return 0.5F;
		}
	}

	@Override
	protected void jump() {
		super.jump();
		double d = this.moveControl.getSpeed();
		if (d > 0.0) {
			double e = squaredHorizontalLength(this.getVelocity());
			if (e < 0.01) {
				this.updateVelocity(0.1F, new Vec3d(0.0, 0.0, 1.0));
			}
		}

		if (!this.world.isClient) {
			this.world.sendEntityStatus(this, (byte)1);
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_6605(float f) {
		return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + f) / (float)this.jumpDuration;
	}

	public void setSpeed(double d) {
		this.getNavigation().setSpeed(d);
		this.moveControl.moveTo(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ(), d);
	}

	@Override
	public void setJumping(boolean bl) {
		super.setJumping(bl);
		if (bl) {
			this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
		}
	}

	public void startJump() {
		this.setJumping(true);
		this.jumpDuration = 10;
		this.jumpTicks = 0;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(RABBIT_TYPE, 0);
	}

	@Override
	public void mobTick() {
		if (this.ticksUntilJump > 0) {
			this.ticksUntilJump--;
		}

		if (this.moreCarrotTicks > 0) {
			this.moreCarrotTicks = this.moreCarrotTicks - this.random.nextInt(3);
			if (this.moreCarrotTicks < 0) {
				this.moreCarrotTicks = 0;
			}
		}

		if (this.onGround) {
			if (!this.lastOnGround) {
				this.setJumping(false);
				this.method_6619();
			}

			if (this.getRabbitType() == 99 && this.ticksUntilJump == 0) {
				LivingEntity livingEntity = this.getTarget();
				if (livingEntity != null && this.squaredDistanceTo(livingEntity) < 16.0) {
					this.lookTowards(livingEntity.x, livingEntity.z);
					this.moveControl.moveTo(livingEntity.x, livingEntity.y, livingEntity.z, this.moveControl.getSpeed());
					this.startJump();
					this.lastOnGround = true;
				}
			}

			RabbitEntity.RabbitJumpControl rabbitJumpControl = (RabbitEntity.RabbitJumpControl)this.jumpControl;
			if (!rabbitJumpControl.isActive()) {
				if (this.moveControl.isMoving() && this.ticksUntilJump == 0) {
					Path path = this.navigation.getCurrentPath();
					Vec3d vec3d = new Vec3d(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ());
					if (path != null && path.getCurrentNodeIndex() < path.getLength()) {
						vec3d = path.getNodePosition(this);
					}

					this.lookTowards(vec3d.x, vec3d.z);
					this.startJump();
				}
			} else if (!rabbitJumpControl.method_6625()) {
				this.method_6611();
			}
		}

		this.lastOnGround = this.onGround;
	}

	@Override
	public void attemptSprintingParticles() {
	}

	private void lookTowards(double d, double e) {
		this.yaw = (float)(MathHelper.atan2(e - this.z, d - this.x) * 180.0F / (float)Math.PI) - 90.0F;
	}

	private void method_6611() {
		((RabbitEntity.RabbitJumpControl)this.jumpControl).method_6623(true);
	}

	private void method_6621() {
		((RabbitEntity.RabbitJumpControl)this.jumpControl).method_6623(false);
	}

	private void scheduleJump() {
		if (this.moveControl.getSpeed() < 2.2) {
			this.ticksUntilJump = 10;
		} else {
			this.ticksUntilJump = 1;
		}
	}

	private void method_6619() {
		this.scheduleJump();
		this.method_6621();
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		if (this.jumpTicks != this.jumpDuration) {
			this.jumpTicks++;
		} else if (this.jumpDuration != 0) {
			this.jumpTicks = 0;
			this.jumpDuration = 0;
			this.setJumping(false);
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(3.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("RabbitType", this.getRabbitType());
		compoundTag.putInt("MoreCarrotTicks", this.moreCarrotTicks);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setRabbitType(compoundTag.getInt("RabbitType"));
		this.moreCarrotTicks = compoundTag.getInt("MoreCarrotTicks");
	}

	protected SoundEvent getJumpSound() {
		return SoundEvents.field_15091;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14693;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15164;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14872;
	}

	@Override
	public boolean attack(Entity entity) {
		if (this.getRabbitType() == 99) {
			this.playSound(SoundEvents.field_15147, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			return entity.damage(DamageSource.mob(this), 8.0F);
		} else {
			return entity.damage(DamageSource.mob(this), 3.0F);
		}
	}

	@Override
	public SoundCategory getSoundCategory() {
		return this.getRabbitType() == 99 ? SoundCategory.field_15251 : SoundCategory.field_15254;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return this.isInvulnerableTo(damageSource) ? false : super.damage(damageSource, f);
	}

	private boolean isBreedingItem(Item item) {
		return item == Items.field_8179 || item == Items.field_8071 || item == Blocks.field_10182.getItem();
	}

	public RabbitEntity method_6620(PassiveEntity passiveEntity) {
		RabbitEntity rabbitEntity = EntityType.RABBIT.create(this.world);
		int i = this.chooseType(this.world);
		if (this.random.nextInt(20) != 0) {
			if (passiveEntity instanceof RabbitEntity && this.random.nextBoolean()) {
				i = ((RabbitEntity)passiveEntity).getRabbitType();
			} else {
				i = this.getRabbitType();
			}
		}

		rabbitEntity.setRabbitType(i);
		return rabbitEntity;
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return this.isBreedingItem(itemStack.getItem());
	}

	public int getRabbitType() {
		return this.dataTracker.get(RABBIT_TYPE);
	}

	public void setRabbitType(int i) {
		if (i == 99) {
			this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(8.0);
			this.goalSelector.add(4, new RabbitEntity.RabbitAttackGoal(this));
			this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge());
			this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
			this.targetSelector.add(2, new FollowTargetGoal(this, WolfEntity.class, true));
			if (!this.hasCustomName()) {
				this.setCustomName(new TranslatableTextComponent(SystemUtil.createTranslationKey("entity", KILLER_BUNNY)));
			}
		}

		this.dataTracker.set(RABBIT_TYPE, i);
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		int i = this.chooseType(iWorld);
		boolean bl = false;
		if (entityData instanceof RabbitEntity.RabbitEntityData) {
			i = ((RabbitEntity.RabbitEntityData)entityData).type;
			bl = true;
		} else {
			entityData = new RabbitEntity.RabbitEntityData(i);
		}

		this.setRabbitType(i);
		if (bl) {
			this.setBreedingAge(-24000);
		}

		return entityData;
	}

	private int chooseType(IWorld iWorld) {
		Biome biome = iWorld.getBiome(new BlockPos(this));
		int i = this.random.nextInt(100);
		if (biome.getPrecipitation() == Biome.Precipitation.SNOW) {
			return i < 80 ? 1 : 3;
		} else if (biome.getCategory() == Biome.Category.DESERT) {
			return 4;
		} else {
			return i < 50 ? 0 : (i < 90 ? 5 : 2);
		}
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.getBoundingBox().minY);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		Block block = iWorld.getBlockState(blockPos.down()).getBlock();
		return block != Blocks.field_10479 && block != Blocks.field_10477 && block != Blocks.field_10102 ? super.canSpawn(iWorld, spawnType) : true;
	}

	private boolean wantsCarrots() {
		return this.moreCarrotTicks == 0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 1) {
			this.spawnSprintingParticles();
			this.jumpDuration = 10;
			this.jumpTicks = 0;
		} else {
			super.handleStatus(b);
		}
	}

	static class RabbitAttackGoal extends MeleeAttackGoal {
		public RabbitAttackGoal(RabbitEntity rabbitEntity) {
			super(rabbitEntity, 1.4, true);
		}

		@Override
		protected double getSquaredMaxAttackDistance(LivingEntity livingEntity) {
			return (double)(4.0F + livingEntity.getWidth());
		}
	}

	public static class RabbitEntityData implements EntityData {
		public final int type;

		public RabbitEntityData(int i) {
			this.type = i;
		}
	}

	static class RabbitEscapeDangerGoal extends EscapeDangerGoal {
		private final RabbitEntity owner;

		public RabbitEscapeDangerGoal(RabbitEntity rabbitEntity, double d) {
			super(rabbitEntity, d);
			this.owner = rabbitEntity;
		}

		@Override
		public void tick() {
			super.tick();
			this.owner.setSpeed(this.speed);
		}
	}

	static class RabbitFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final RabbitEntity rabbit;

		public RabbitFleeGoal(RabbitEntity rabbitEntity, Class<T> class_, float f, double d, double e) {
			super(rabbitEntity, class_, f, d, e);
			this.rabbit = rabbitEntity;
		}

		@Override
		public boolean canStart() {
			return this.rabbit.getRabbitType() != 99 && super.canStart();
		}
	}

	public class RabbitJumpControl extends JumpControl {
		private final RabbitEntity rabbit;
		private boolean field_6856;

		public RabbitJumpControl(RabbitEntity rabbitEntity2) {
			super(rabbitEntity2);
			this.rabbit = rabbitEntity2;
		}

		public boolean isActive() {
			return this.active;
		}

		public boolean method_6625() {
			return this.field_6856;
		}

		public void method_6623(boolean bl) {
			this.field_6856 = bl;
		}

		@Override
		public void tick() {
			if (this.active) {
				this.rabbit.startJump();
				this.active = false;
			}
		}
	}

	static class RabbitMoveControl extends MoveControl {
		private final RabbitEntity rabbit;
		private double field_6858;

		public RabbitMoveControl(RabbitEntity rabbitEntity) {
			super(rabbitEntity);
			this.rabbit = rabbitEntity;
		}

		@Override
		public void tick() {
			if (this.rabbit.onGround && !this.rabbit.jumping && !((RabbitEntity.RabbitJumpControl)this.rabbit.jumpControl).isActive()) {
				this.rabbit.setSpeed(0.0);
			} else if (this.isMoving()) {
				this.rabbit.setSpeed(this.field_6858);
			}

			super.tick();
		}

		@Override
		public void moveTo(double d, double e, double f, double g) {
			if (this.rabbit.isInsideWater()) {
				g = 1.5;
			}

			super.moveTo(d, e, f, g);
			if (g > 0.0) {
				this.field_6858 = g;
			}
		}
	}

	static class class_1470 extends MoveToTargetPosGoal {
		private final RabbitEntity rabbit;
		private boolean field_6862;
		private boolean field_6861;

		public class_1470(RabbitEntity rabbitEntity) {
			super(rabbitEntity, 0.7F, 16);
			this.rabbit = rabbitEntity;
		}

		@Override
		public boolean canStart() {
			if (this.cooldown <= 0) {
				if (!this.rabbit.world.getGameRules().getBoolean("mobGriefing")) {
					return false;
				}

				this.field_6861 = false;
				this.field_6862 = this.rabbit.wantsCarrots();
				this.field_6862 = true;
			}

			return super.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return this.field_6861 && super.shouldContinue();
		}

		@Override
		public void tick() {
			super.tick();
			this.rabbit
				.getLookControl()
				.lookAt(
					(double)this.targetPos.getX() + 0.5,
					(double)(this.targetPos.getY() + 1),
					(double)this.targetPos.getZ() + 0.5,
					10.0F,
					(float)this.rabbit.getLookPitchSpeed()
				);
			if (this.hasReached()) {
				World world = this.rabbit.world;
				BlockPos blockPos = this.targetPos.up();
				BlockState blockState = world.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (this.field_6861 && block instanceof CarrotsBlock) {
					Integer integer = blockState.get(CarrotsBlock.AGE);
					if (integer == 0) {
						world.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 2);
						world.breakBlock(blockPos, true);
					} else {
						world.setBlockState(blockPos, blockState.with(CarrotsBlock.AGE, Integer.valueOf(integer - 1)), 2);
						world.method_20290(2001, blockPos, Block.getRawIdFromState(blockState));
					}

					this.rabbit.moreCarrotTicks = 40;
				}

				this.field_6861 = false;
				this.cooldown = 10;
			}
		}

		@Override
		protected boolean isTargetPos(ViewableWorld viewableWorld, BlockPos blockPos) {
			Block block = viewableWorld.getBlockState(blockPos).getBlock();
			if (block == Blocks.field_10362 && this.field_6862 && !this.field_6861) {
				blockPos = blockPos.up();
				BlockState blockState = viewableWorld.getBlockState(blockPos);
				block = blockState.getBlock();
				if (block instanceof CarrotsBlock && ((CarrotsBlock)block).isValidState(blockState)) {
					this.field_6861 = true;
					return true;
				}
			}

			return false;
		}
	}
}
