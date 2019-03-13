package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
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
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
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
	private static final TrackedData<Integer> field_6852 = DataTracker.registerData(RabbitEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Identifier field_6846 = new Identifier("killer_bunny");
	private int field_6851;
	private int field_6849;
	private boolean field_6850;
	private int field_6848;
	private int field_6847;

	public RabbitEntity(EntityType<? extends RabbitEntity> entityType, World world) {
		super(entityType, world);
		this.field_6204 = new RabbitEntity.RabbitJumpControl(this);
		this.field_6207 = new RabbitEntity.RabbitMoveControl(this);
		this.setSpeed(0.0);
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(1, new SwimGoal(this));
		this.field_6201.add(1, new RabbitEntity.class_1469(this, 2.2));
		this.field_6201.add(2, new AnimalMateGoal(this, 0.8));
		this.field_6201.add(3, new TemptGoal(this, 1.0, Ingredient.method_8091(Items.field_8179, Items.field_8071, Blocks.field_10182), false));
		this.field_6201.add(4, new RabbitEntity.RabbitFleeGoal(this, PlayerEntity.class, 8.0F, 2.2, 2.2));
		this.field_6201.add(4, new RabbitEntity.RabbitFleeGoal(this, WolfEntity.class, 10.0F, 2.2, 2.2));
		this.field_6201.add(4, new RabbitEntity.RabbitFleeGoal(this, HostileEntity.class, 4.0F, 2.2, 2.2));
		this.field_6201.add(5, new RabbitEntity.class_1470(this));
		this.field_6201.add(6, new class_1394(this, 0.6));
		this.field_6201.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
	}

	@Override
	protected float getJumpVelocity() {
		if (!this.horizontalCollision && (!this.field_6207.isMoving() || !(this.field_6207.getTargetY() > this.y + 0.5))) {
			Path path = this.field_6189.method_6345();
			if (path != null && path.getCurrentNodeIndex() < path.getLength()) {
				Vec3d vec3d = path.method_49(this);
				if (vec3d.y > this.y + 0.5) {
					return 0.5F;
				}
			}

			return this.field_6207.getSpeed() <= 0.6 ? 0.2F : 0.3F;
		} else {
			return 0.5F;
		}
	}

	@Override
	protected void jump() {
		super.jump();
		double d = this.field_6207.getSpeed();
		if (d > 0.0) {
			double e = method_17996(this.method_18798());
			if (e < 0.01) {
				this.method_5724(0.1F, new Vec3d(0.0, 0.0, 1.0));
			}
		}

		if (!this.field_6002.isClient) {
			this.field_6002.summonParticle(this, (byte)1);
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_6605(float f) {
		return this.field_6849 == 0 ? 0.0F : ((float)this.field_6851 + f) / (float)this.field_6849;
	}

	public void setSpeed(double d) {
		this.method_5942().setSpeed(d);
		this.field_6207.moveTo(this.field_6207.getTargetX(), this.field_6207.getTargetY(), this.field_6207.getTargetZ(), d);
	}

	@Override
	public void doJump(boolean bl) {
		super.doJump(bl);
		if (bl) {
			this.method_5783(this.method_6615(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
		}
	}

	public void method_6618() {
		this.doJump(true);
		this.field_6849 = 10;
		this.field_6851 = 0;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_6852, 0);
	}

	@Override
	public void mobTick() {
		if (this.field_6848 > 0) {
			this.field_6848--;
		}

		if (this.field_6847 > 0) {
			this.field_6847 = this.field_6847 - this.random.nextInt(3);
			if (this.field_6847 < 0) {
				this.field_6847 = 0;
			}
		}

		if (this.onGround) {
			if (!this.field_6850) {
				this.doJump(false);
				this.method_6619();
			}

			if (this.getRabbitType() == 99 && this.field_6848 == 0) {
				LivingEntity livingEntity = this.getTarget();
				if (livingEntity != null && this.squaredDistanceTo(livingEntity) < 16.0) {
					this.method_6616(livingEntity.x, livingEntity.z);
					this.field_6207.moveTo(livingEntity.x, livingEntity.y, livingEntity.z, this.field_6207.getSpeed());
					this.method_6618();
					this.field_6850 = true;
				}
			}

			RabbitEntity.RabbitJumpControl rabbitJumpControl = (RabbitEntity.RabbitJumpControl)this.field_6204;
			if (!rabbitJumpControl.isActive()) {
				if (this.field_6207.isMoving() && this.field_6848 == 0) {
					Path path = this.field_6189.method_6345();
					Vec3d vec3d = new Vec3d(this.field_6207.getTargetX(), this.field_6207.getTargetY(), this.field_6207.getTargetZ());
					if (path != null && path.getCurrentNodeIndex() < path.getLength()) {
						vec3d = path.method_49(this);
					}

					this.method_6616(vec3d.x, vec3d.z);
					this.method_6618();
				}
			} else if (!rabbitJumpControl.method_6625()) {
				this.method_6611();
			}
		}

		this.field_6850 = this.onGround;
	}

	@Override
	public void attemptSprintingParticles() {
	}

	private void method_6616(double d, double e) {
		this.yaw = (float)(MathHelper.atan2(e - this.z, d - this.x) * 180.0F / (float)Math.PI) - 90.0F;
	}

	private void method_6611() {
		((RabbitEntity.RabbitJumpControl)this.field_6204).method_6623(true);
	}

	private void method_6621() {
		((RabbitEntity.RabbitJumpControl)this.field_6204).method_6623(false);
	}

	private void method_6608() {
		if (this.field_6207.getSpeed() < 2.2) {
			this.field_6848 = 10;
		} else {
			this.field_6848 = 1;
		}
	}

	private void method_6619() {
		this.method_6608();
		this.method_6621();
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		if (this.field_6851 != this.field_6849) {
			this.field_6851++;
		} else if (this.field_6849 != 0) {
			this.field_6851 = 0;
			this.field_6849 = 0;
			this.doJump(false);
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(3.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("RabbitType", this.getRabbitType());
		compoundTag.putInt("MoreCarrotTicks", this.field_6847);
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.setRabbitType(compoundTag.getInt("RabbitType"));
		this.field_6847 = compoundTag.getInt("MoreCarrotTicks");
	}

	protected SoundEvent method_6615() {
		return SoundEvents.field_15091;
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_14693;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_15164;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14872;
	}

	@Override
	public boolean attack(Entity entity) {
		if (this.getRabbitType() == 99) {
			this.method_5783(SoundEvents.field_15147, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			return entity.damage(DamageSource.method_5511(this), 8.0F);
		} else {
			return entity.damage(DamageSource.method_5511(this), 3.0F);
		}
	}

	@Override
	public SoundCategory method_5634() {
		return this.getRabbitType() == 99 ? SoundCategory.field_15251 : SoundCategory.field_15254;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return this.isInvulnerableTo(damageSource) ? false : super.damage(damageSource, f);
	}

	private boolean method_6614(Item item) {
		return item == Items.field_8179 || item == Items.field_8071 || item == Blocks.field_10182.getItem();
	}

	public RabbitEntity method_6620(PassiveEntity passiveEntity) {
		RabbitEntity rabbitEntity = EntityType.RABBIT.method_5883(this.field_6002);
		int i = this.method_6622(this.field_6002);
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
	public boolean method_6481(ItemStack itemStack) {
		return this.method_6614(itemStack.getItem());
	}

	public int getRabbitType() {
		return this.field_6011.get(field_6852);
	}

	public void setRabbitType(int i) {
		if (i == 99) {
			this.method_5996(EntityAttributes.ARMOR).setBaseValue(8.0);
			this.field_6201.add(4, new RabbitEntity.class_1464(this));
			this.field_6185.add(1, new class_1399(this).method_6318());
			this.field_6185.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
			this.field_6185.add(2, new FollowTargetGoal(this, WolfEntity.class, true));
			if (!this.hasCustomName()) {
				this.method_5665(new TranslatableTextComponent(SystemUtil.method_646("entity", field_6846)));
			}
		}

		this.field_6011.set(field_6852, i);
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		int i = this.method_6622(iWorld);
		boolean bl = false;
		if (entityData instanceof RabbitEntity.class_1466) {
			i = ((RabbitEntity.class_1466)entityData).field_6854;
			bl = true;
		} else {
			entityData = new RabbitEntity.class_1466(i);
		}

		this.setRabbitType(i);
		if (bl) {
			this.setBreedingAge(-24000);
		}

		return entityData;
	}

	private int method_6622(IWorld iWorld) {
		Biome biome = iWorld.method_8310(new BlockPos(this));
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
	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.method_5829().minY);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		Block block = iWorld.method_8320(blockPos.down()).getBlock();
		return block != Blocks.field_10479 && block != Blocks.field_10477 && block != Blocks.field_10102 ? super.method_5979(iWorld, spawnType) : true;
	}

	private boolean method_6607() {
		return this.field_6847 == 0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 1) {
			this.spawnSprintingParticles();
			this.field_6849 = 10;
			this.field_6851 = 0;
		} else {
			super.method_5711(b);
		}
	}

	static class RabbitFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final RabbitEntity field_6853;

		public RabbitFleeGoal(RabbitEntity rabbitEntity, Class<T> class_, float f, double d, double e) {
			super(rabbitEntity, class_, f, d, e);
			this.field_6853 = rabbitEntity;
		}

		@Override
		public boolean canStart() {
			return this.field_6853.getRabbitType() != 99 && super.canStart();
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
				this.rabbit.method_6618();
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
			if (this.rabbit.onGround && !this.rabbit.field_6282 && !((RabbitEntity.RabbitJumpControl)this.rabbit.field_6204).isActive()) {
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

	static class class_1464 extends MeleeAttackGoal {
		public class_1464(RabbitEntity rabbitEntity) {
			super(rabbitEntity, 1.4, true);
		}

		@Override
		protected double method_6289(LivingEntity livingEntity) {
			return (double)(4.0F + livingEntity.getWidth());
		}
	}

	public static class class_1466 implements EntityData {
		public final int field_6854;

		public class_1466(int i) {
			this.field_6854 = i;
		}
	}

	static class class_1469 extends EscapeDangerGoal {
		private final RabbitEntity owner;

		public class_1469(RabbitEntity rabbitEntity, double d) {
			super(rabbitEntity, d);
			this.owner = rabbitEntity;
		}

		@Override
		public void tick() {
			super.tick();
			this.owner.setSpeed(this.speed);
		}
	}

	static class class_1470 extends MoveToTargetPosGoal {
		private final RabbitEntity field_6863;
		private boolean field_6862;
		private boolean field_6861;

		public class_1470(RabbitEntity rabbitEntity) {
			super(rabbitEntity, 0.7F, 16);
			this.field_6863 = rabbitEntity;
		}

		@Override
		public boolean canStart() {
			if (this.counter <= 0) {
				if (!this.field_6863.field_6002.getGameRules().getBoolean("mobGriefing")) {
					return false;
				}

				this.field_6861 = false;
				this.field_6862 = this.field_6863.method_6607();
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
			this.field_6863
				.method_5988()
				.lookAt(
					(double)this.field_6512.getX() + 0.5,
					(double)(this.field_6512.getY() + 1),
					(double)this.field_6512.getZ() + 0.5,
					10.0F,
					(float)this.field_6863.method_5978()
				);
			if (this.hasReached()) {
				World world = this.field_6863.field_6002;
				BlockPos blockPos = this.field_6512.up();
				BlockState blockState = world.method_8320(blockPos);
				Block block = blockState.getBlock();
				if (this.field_6861 && block instanceof CarrotsBlock) {
					Integer integer = blockState.method_11654(CarrotsBlock.field_10835);
					if (integer == 0) {
						world.method_8652(blockPos, Blocks.field_10124.method_9564(), 2);
						world.method_8651(blockPos, true);
					} else {
						world.method_8652(blockPos, blockState.method_11657(CarrotsBlock.field_10835, Integer.valueOf(integer - 1)), 2);
						world.method_8535(2001, blockPos, Block.method_9507(blockState));
					}

					this.field_6863.field_6847 = 40;
				}

				this.field_6861 = false;
				this.counter = 10;
			}
		}

		@Override
		protected boolean method_6296(ViewableWorld viewableWorld, BlockPos blockPos) {
			Block block = viewableWorld.method_8320(blockPos).getBlock();
			if (block == Blocks.field_10362 && this.field_6862 && !this.field_6861) {
				blockPos = blockPos.up();
				BlockState blockState = viewableWorld.method_8320(blockPos);
				block = blockState.getBlock();
				if (block instanceof CarrotsBlock && ((CarrotsBlock)block).method_9825(blockState)) {
					this.field_6861 = true;
					return true;
				}
			}

			return false;
		}
	}
}
