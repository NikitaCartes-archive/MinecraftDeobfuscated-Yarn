package net.minecraft.entity.passive;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5425;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
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
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
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
		this.goalSelector.add(1, new RabbitEntity.EscapeDangerGoal(this, 2.2));
		this.goalSelector.add(2, new AnimalMateGoal(this, 0.8));
		this.goalSelector.add(3, new TemptGoal(this, 1.0, Ingredient.ofItems(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
		this.goalSelector.add(4, new RabbitEntity.FleeGoal(this, PlayerEntity.class, 8.0F, 2.2, 2.2));
		this.goalSelector.add(4, new RabbitEntity.FleeGoal(this, WolfEntity.class, 10.0F, 2.2, 2.2));
		this.goalSelector.add(4, new RabbitEntity.FleeGoal(this, HostileEntity.class, 4.0F, 2.2, 2.2));
		this.goalSelector.add(5, new RabbitEntity.EatCarrotCropGoal(this));
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.6));
		this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
	}

	@Override
	protected float getJumpVelocity() {
		if (!this.horizontalCollision && (!this.moveControl.isMoving() || !(this.moveControl.getTargetY() > this.getY() + 0.5))) {
			Path path = this.navigation.getCurrentPath();
			if (path != null && path.getCurrentNodeIndex() < path.getLength()) {
				Vec3d vec3d = path.getNodePosition(this);
				if (vec3d.y > this.getY() + 0.5) {
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
	public float getJumpProgress(float delta) {
		return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + delta) / (float)this.jumpDuration;
	}

	public void setSpeed(double speed) {
		this.getNavigation().setSpeed(speed);
		this.moveControl.moveTo(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ(), speed);
	}

	@Override
	public void setJumping(boolean jumping) {
		super.setJumping(jumping);
		if (jumping) {
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
				this.scheduleJump();
			}

			if (this.getRabbitType() == 99 && this.ticksUntilJump == 0) {
				LivingEntity livingEntity = this.getTarget();
				if (livingEntity != null && this.squaredDistanceTo(livingEntity) < 16.0) {
					this.lookTowards(livingEntity.getX(), livingEntity.getZ());
					this.moveControl.moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), this.moveControl.getSpeed());
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
			} else if (!rabbitJumpControl.method_27313()) {
				this.method_6611();
			}
		}

		this.lastOnGround = this.onGround;
	}

	@Override
	public boolean shouldSpawnSprintingParticles() {
		return false;
	}

	private void lookTowards(double x, double z) {
		this.yaw = (float)(MathHelper.atan2(z - this.getZ(), x - this.getX()) * 180.0F / (float)Math.PI) - 90.0F;
	}

	private void method_6611() {
		((RabbitEntity.RabbitJumpControl)this.jumpControl).method_27311(true);
	}

	private void method_6621() {
		((RabbitEntity.RabbitJumpControl)this.jumpControl).method_27311(false);
	}

	private void doScheduleJump() {
		if (this.moveControl.getSpeed() < 2.2) {
			this.ticksUntilJump = 10;
		} else {
			this.ticksUntilJump = 1;
		}
	}

	private void scheduleJump() {
		this.doScheduleJump();
		this.method_6621();
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.jumpTicks != this.jumpDuration) {
			this.jumpTicks++;
		} else if (this.jumpDuration != 0) {
			this.jumpTicks = 0;
			this.jumpDuration = 0;
			this.setJumping(false);
		}
	}

	public static DefaultAttributeContainer.Builder createRabbitAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 3.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("RabbitType", this.getRabbitType());
		tag.putInt("MoreCarrotTicks", this.moreCarrotTicks);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setRabbitType(tag.getInt("RabbitType"));
		this.moreCarrotTicks = tag.getInt("MoreCarrotTicks");
	}

	protected SoundEvent getJumpSound() {
		return SoundEvents.ENTITY_RABBIT_JUMP;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_RABBIT_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_RABBIT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_RABBIT_DEATH;
	}

	@Override
	public boolean tryAttack(Entity target) {
		if (this.getRabbitType() == 99) {
			this.playSound(SoundEvents.ENTITY_RABBIT_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			return target.damage(DamageSource.mob(this), 8.0F);
		} else {
			return target.damage(DamageSource.mob(this), 3.0F);
		}
	}

	@Override
	public SoundCategory getSoundCategory() {
		return this.getRabbitType() == 99 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return this.isInvulnerableTo(source) ? false : super.damage(source, amount);
	}

	private boolean isBreedingItem(Item item) {
		return item == Items.CARROT || item == Items.GOLDEN_CARROT || item == Blocks.DANDELION.asItem();
	}

	public RabbitEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		RabbitEntity rabbitEntity = EntityType.RABBIT.create(serverWorld);
		int i = this.chooseType(serverWorld);
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
	public boolean isBreedingItem(ItemStack stack) {
		return this.isBreedingItem(stack.getItem());
	}

	public int getRabbitType() {
		return this.dataTracker.get(RABBIT_TYPE);
	}

	public void setRabbitType(int rabbitType) {
		if (rabbitType == 99) {
			this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(8.0);
			this.goalSelector.add(4, new RabbitEntity.RabbitAttackGoal(this));
			this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge());
			this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
			this.targetSelector.add(2, new FollowTargetGoal(this, WolfEntity.class, true));
			if (!this.hasCustomName()) {
				this.setCustomName(new TranslatableText(Util.createTranslationKey("entity", KILLER_BUNNY)));
			}
		}

		this.dataTracker.set(RABBIT_TYPE, rabbitType);
	}

	@Nullable
	@Override
	public EntityData initialize(
		class_5425 arg, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		int i = this.chooseType(arg);
		if (entityData instanceof RabbitEntity.RabbitData) {
			i = ((RabbitEntity.RabbitData)entityData).type;
		} else {
			entityData = new RabbitEntity.RabbitData(i);
		}

		this.setRabbitType(i);
		return super.initialize(arg, difficulty, spawnReason, entityData, entityTag);
	}

	private int chooseType(WorldAccess world) {
		Biome biome = world.getBiome(this.getBlockPos());
		int i = this.random.nextInt(100);
		if (biome.getPrecipitation() == Biome.Precipitation.SNOW) {
			return i < 80 ? 1 : 3;
		} else if (biome.getCategory() == Biome.Category.DESERT) {
			return 4;
		} else {
			return i < 50 ? 0 : (i < 90 ? 5 : 2);
		}
	}

	public static boolean canSpawn(EntityType<RabbitEntity> entity, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		BlockState blockState = world.getBlockState(pos.down());
		return (blockState.isOf(Blocks.GRASS_BLOCK) || blockState.isOf(Blocks.SNOW) || blockState.isOf(Blocks.SAND)) && world.getBaseLightLevel(pos, 0) > 8;
	}

	private boolean wantsCarrots() {
		return this.moreCarrotTicks == 0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 1) {
			this.spawnSprintingParticles();
			this.jumpDuration = 10;
			this.jumpTicks = 0;
		} else {
			super.handleStatus(status);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Vec3d method_29919() {
		return new Vec3d(0.0, (double)(0.6F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
	}

	static class EatCarrotCropGoal extends MoveToTargetPosGoal {
		private final RabbitEntity rabbit;
		private boolean wantsCarrots;
		private boolean hasTarget;

		public EatCarrotCropGoal(RabbitEntity rabbit) {
			super(rabbit, 0.7F, 16);
			this.rabbit = rabbit;
		}

		@Override
		public boolean canStart() {
			if (this.cooldown <= 0) {
				if (!this.rabbit.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
					return false;
				}

				this.hasTarget = false;
				this.wantsCarrots = this.rabbit.wantsCarrots();
				this.wantsCarrots = true;
			}

			return super.canStart();
		}

		@Override
		public boolean shouldContinue() {
			return this.hasTarget && super.shouldContinue();
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
				if (this.hasTarget && block instanceof CarrotsBlock) {
					Integer integer = blockState.get(CarrotsBlock.AGE);
					if (integer == 0) {
						world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
						world.breakBlock(blockPos, true, this.rabbit);
					} else {
						world.setBlockState(blockPos, blockState.with(CarrotsBlock.AGE, Integer.valueOf(integer - 1)), 2);
						world.syncWorldEvent(2001, blockPos, Block.getRawIdFromState(blockState));
					}

					this.rabbit.moreCarrotTicks = 40;
				}

				this.hasTarget = false;
				this.cooldown = 10;
			}
		}

		@Override
		protected boolean isTargetPos(WorldView world, BlockPos pos) {
			Block block = world.getBlockState(pos).getBlock();
			if (block == Blocks.FARMLAND && this.wantsCarrots && !this.hasTarget) {
				pos = pos.up();
				BlockState blockState = world.getBlockState(pos);
				block = blockState.getBlock();
				if (block instanceof CarrotsBlock && ((CarrotsBlock)block).isMature(blockState)) {
					this.hasTarget = true;
					return true;
				}
			}

			return false;
		}
	}

	static class EscapeDangerGoal extends net.minecraft.entity.ai.goal.EscapeDangerGoal {
		private final RabbitEntity rabbit;

		public EscapeDangerGoal(RabbitEntity rabbit, double speed) {
			super(rabbit, speed);
			this.rabbit = rabbit;
		}

		@Override
		public void tick() {
			super.tick();
			this.rabbit.setSpeed(this.speed);
		}
	}

	static class FleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final RabbitEntity rabbit;

		public FleeGoal(RabbitEntity rabbit, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed) {
			super(rabbit, fleeFromType, distance, slowSpeed, fastSpeed);
			this.rabbit = rabbit;
		}

		@Override
		public boolean canStart() {
			return this.rabbit.getRabbitType() != 99 && super.canStart();
		}
	}

	static class RabbitAttackGoal extends MeleeAttackGoal {
		public RabbitAttackGoal(RabbitEntity rabbit) {
			super(rabbit, 1.4, true);
		}

		@Override
		protected double getSquaredMaxAttackDistance(LivingEntity entity) {
			return (double)(4.0F + entity.getWidth());
		}
	}

	public static class RabbitData extends PassiveEntity.PassiveData {
		public final int type;

		public RabbitData(int type) {
			super(1.0F);
			this.type = type;
		}
	}

	public class RabbitJumpControl extends JumpControl {
		private final RabbitEntity rabbit;
		private boolean field_24091;

		public RabbitJumpControl(RabbitEntity rabbit) {
			super(rabbit);
			this.rabbit = rabbit;
		}

		public boolean isActive() {
			return this.active;
		}

		public boolean method_27313() {
			return this.field_24091;
		}

		public void method_27311(boolean bl) {
			this.field_24091 = bl;
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
		private double rabbitSpeed;

		public RabbitMoveControl(RabbitEntity owner) {
			super(owner);
			this.rabbit = owner;
		}

		@Override
		public void tick() {
			if (this.rabbit.onGround && !this.rabbit.jumping && !((RabbitEntity.RabbitJumpControl)this.rabbit.jumpControl).isActive()) {
				this.rabbit.setSpeed(0.0);
			} else if (this.isMoving()) {
				this.rabbit.setSpeed(this.rabbitSpeed);
			}

			super.tick();
		}

		@Override
		public void moveTo(double x, double y, double z, double speed) {
			if (this.rabbit.isTouchingWater()) {
				speed = 1.5;
			}

			super.moveTo(x, y, z, speed);
			if (speed > 0.0) {
				this.rabbitSpeed = speed;
			}
		}
	}
}
