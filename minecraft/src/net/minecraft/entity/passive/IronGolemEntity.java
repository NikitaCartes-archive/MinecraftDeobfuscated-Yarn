package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1369;
import net.minecraft.class_1370;
import net.minecraft.class_1372;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.TrackIronGolemTargetGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sortme.SpawnHelper;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.VillageProperties;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class IronGolemEntity extends GolemEntity {
	protected static final TrackedData<Byte> IRON_GOLEM_FLAGS = DataTracker.registerData(IronGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
	private int findVillageDelay;
	@Nullable
	private VillageProperties villageProperties;
	private int field_6762;
	private int field_6759;

	public IronGolemEntity(World world) {
		super(EntityType.IRON_GOLEM, world);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
		this.goalSelector.add(2, new class_1369(this, 0.9, 32.0F));
		this.goalSelector.add(3, new MoveThroughVillageGoal(this, 0.6, true));
		this.goalSelector.add(4, new class_1370(this, 1.0));
		this.goalSelector.add(5, new class_1372(this));
		this.goalSelector.add(6, new class_1394(this, 0.6));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector.add(1, new TrackIronGolemTargetGoal(this));
		this.targetSelector.add(2, new class_1399(this));
		this.targetSelector
			.add(
				3,
				new FollowTargetGoal(
					this, MobEntity.class, 10, false, true, mobEntity -> mobEntity != null && Monster.field_7271.test(mobEntity) && !(mobEntity instanceof CreeperEntity)
				)
			);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(IRON_GOLEM_FLAGS, (byte)0);
	}

	@Override
	protected void mobTick() {
		if (--this.findVillageDelay <= 0) {
			this.findVillageDelay = 70 + this.random.nextInt(50);
			this.villageProperties = this.world.getVillageManager().getNearestVillage(new BlockPos(this), 32);
			if (this.villageProperties == null) {
				this.setAiRangeUnlimited();
			} else {
				BlockPos blockPos = this.villageProperties.getCenter();
				this.setAiHome(blockPos, (int)((float)this.villageProperties.getRadius() * 0.6F));
			}
		}

		super.mobTick();
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(100.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
		this.getAttributeInstance(EntityAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
	}

	@Override
	protected int method_6130(int i) {
		return i;
	}

	@Override
	protected void pushAway(Entity entity) {
		if (entity instanceof Monster && !(entity instanceof CreeperEntity) && this.getRand().nextInt(20) == 0) {
			this.setTarget((LivingEntity)entity);
		}

		super.pushAway(entity);
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		if (this.field_6762 > 0) {
			this.field_6762--;
		}

		if (this.field_6759 > 0) {
			this.field_6759--;
		}

		if (this.velocityX * this.velocityX + this.velocityZ * this.velocityZ > 2.5000003E-7F && this.random.nextInt(5) == 0) {
			int i = MathHelper.floor(this.x);
			int j = MathHelper.floor(this.y - 0.2F);
			int k = MathHelper.floor(this.z);
			BlockState blockState = this.world.getBlockState(new BlockPos(i, j, k));
			if (!blockState.isAir()) {
				this.world
					.addParticle(
						new BlockStateParticleParameters(ParticleTypes.field_11217, blockState),
						this.x + ((double)this.random.nextFloat() - 0.5) * (double)this.getWidth(),
						this.getBoundingBox().minY + 0.1,
						this.z + ((double)this.random.nextFloat() - 0.5) * (double)this.getWidth(),
						4.0 * ((double)this.random.nextFloat() - 0.5),
						0.5,
						((double)this.random.nextFloat() - 0.5) * 4.0
					);
			}
		}
	}

	@Override
	public boolean canTrack(Class<? extends LivingEntity> class_) {
		if (this.isPlayerCreated() && PlayerEntity.class.isAssignableFrom(class_)) {
			return false;
		} else {
			return class_ == CreeperEntity.class ? false : super.canTrack(class_);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("PlayerCreated", this.isPlayerCreated());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setPlayerCreated(compoundTag.getBoolean("PlayerCreated"));
	}

	@Override
	public boolean method_6121(Entity entity) {
		this.field_6762 = 10;
		this.world.summonParticle(this, (byte)4);
		boolean bl = entity.damage(DamageSource.mob(this), (float)(7 + this.random.nextInt(15)));
		if (bl) {
			entity.velocityY += 0.4F;
			this.method_5723(this, entity);
		}

		this.playSound(SoundEvents.field_14649, 1.0F, 1.0F);
		return bl;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 4) {
			this.field_6762 = 10;
			this.playSound(SoundEvents.field_14649, 1.0F, 1.0F);
		} else if (b == 11) {
			this.field_6759 = 400;
		} else if (b == 34) {
			this.field_6759 = 0;
		} else {
			super.method_5711(b);
		}
	}

	public VillageProperties getVillageProperties() {
		return this.villageProperties;
	}

	@Environment(EnvType.CLIENT)
	public int method_6501() {
		return this.field_6762;
	}

	public void method_6497(boolean bl) {
		if (bl) {
			this.field_6759 = 400;
			this.world.summonParticle(this, (byte)11);
		} else {
			this.field_6759 = 0;
			this.world.summonParticle(this, (byte)34);
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14959;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15055;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.field_15233, 1.0F, 1.0F);
	}

	public int method_6502() {
		return this.field_6759;
	}

	public boolean isPlayerCreated() {
		return (this.dataTracker.get(IRON_GOLEM_FLAGS) & 1) != 0;
	}

	public void setPlayerCreated(boolean bl) {
		byte b = this.dataTracker.get(IRON_GOLEM_FLAGS);
		if (bl) {
			this.dataTracker.set(IRON_GOLEM_FLAGS, (byte)(b | 1));
		} else {
			this.dataTracker.set(IRON_GOLEM_FLAGS, (byte)(b & -2));
		}
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		if (!this.isPlayerCreated() && this.field_6258 != null && this.villageProperties != null) {
			this.villageProperties.changeRating(this.field_6258.getGameProfile().getName(), -5);
		}

		super.onDeath(damageSource);
	}

	@Override
	public boolean method_5957(ViewableWorld viewableWorld) {
		BlockPos blockPos = new BlockPos(this);
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState = viewableWorld.getBlockState(blockPos2);
		if (!blockState.hasSolidTopSurface(viewableWorld, blockPos2)) {
			return false;
		} else {
			BlockPos blockPos3 = blockPos.up();
			BlockState blockState2 = viewableWorld.getBlockState(blockPos3);
			return SpawnHelper.isClearForSpawn(viewableWorld, blockPos3, blockState2, blockState2.getFluidState())
				&& SpawnHelper.isClearForSpawn(viewableWorld, blockPos, viewableWorld.getBlockState(blockPos), Fluids.EMPTY.getDefaultState())
				&& viewableWorld.method_8606(this);
		}
	}
}
