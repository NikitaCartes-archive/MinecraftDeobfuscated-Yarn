package net.minecraft.entity.passive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1369;
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
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class IronGolemEntity extends GolemEntity {
	protected static final TrackedData<Byte> field_6763 = DataTracker.registerData(IronGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
	private int field_6762;
	private int field_6759;

	public IronGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
		super(entityType, world);
		this.stepHeight = 1.0F;
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(1, new MeleeAttackGoal(this, 1.0, true));
		this.field_6201.add(2, new class_1369(this, 0.9, 32.0F));
		this.field_6201.add(3, new MoveThroughVillageGoal(this, 0.6, false, 4, () -> false));
		this.field_6201.add(5, new class_1372(this));
		this.field_6201.add(6, new class_1394(this, 0.6));
		this.field_6201.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.field_6201.add(8, new LookAroundGoal(this));
		this.field_6185.add(1, new TrackIronGolemTargetGoal(this));
		this.field_6185.add(2, new class_1399(this));
		this.field_6185
			.add(
				3,
				new FollowTargetGoal(this, MobEntity.class, 5, false, false, livingEntity -> livingEntity instanceof Monster && !(livingEntity instanceof CreeperEntity))
			);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_6763, (byte)0);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(100.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
		this.method_5996(EntityAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
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

		if (method_17996(this.method_18798()) > 2.5000003E-7F && this.random.nextInt(5) == 0) {
			int i = MathHelper.floor(this.x);
			int j = MathHelper.floor(this.y - 0.2F);
			int k = MathHelper.floor(this.z);
			BlockState blockState = this.field_6002.method_8320(new BlockPos(i, j, k));
			if (!blockState.isAir()) {
				this.field_6002
					.method_8406(
						new BlockStateParticleParameters(ParticleTypes.field_11217, blockState),
						this.x + ((double)this.random.nextFloat() - 0.5) * (double)this.getWidth(),
						this.method_5829().minY + 0.1,
						this.z + ((double)this.random.nextFloat() - 0.5) * (double)this.getWidth(),
						4.0 * ((double)this.random.nextFloat() - 0.5),
						0.5,
						((double)this.random.nextFloat() - 0.5) * 4.0
					);
			}
		}
	}

	@Override
	public boolean method_5973(EntityType<?> entityType) {
		if (this.isPlayerCreated() && entityType == EntityType.PLAYER) {
			return false;
		} else {
			return entityType == EntityType.CREEPER ? false : super.method_5973(entityType);
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putBoolean("PlayerCreated", this.isPlayerCreated());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.setPlayerCreated(compoundTag.getBoolean("PlayerCreated"));
	}

	@Override
	public boolean attack(Entity entity) {
		this.field_6762 = 10;
		this.field_6002.summonParticle(this, (byte)4);
		boolean bl = entity.damage(DamageSource.method_5511(this), (float)(7 + this.random.nextInt(15)));
		if (bl) {
			entity.method_18799(entity.method_18798().add(0.0, 0.4F, 0.0));
			this.method_5723(this, entity);
		}

		this.method_5783(SoundEvents.field_14649, 1.0F, 1.0F);
		return bl;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 4) {
			this.field_6762 = 10;
			this.method_5783(SoundEvents.field_14649, 1.0F, 1.0F);
		} else if (b == 11) {
			this.field_6759 = 400;
		} else if (b == 34) {
			this.field_6759 = 0;
		} else {
			super.method_5711(b);
		}
	}

	@Environment(EnvType.CLIENT)
	public int method_6501() {
		return this.field_6762;
	}

	public void method_6497(boolean bl) {
		if (bl) {
			this.field_6759 = 400;
			this.field_6002.summonParticle(this, (byte)11);
		} else {
			this.field_6759 = 0;
			this.field_6002.summonParticle(this, (byte)34);
		}
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14959;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_15055;
	}

	@Override
	protected void method_5712(BlockPos blockPos, BlockState blockState) {
		this.method_5783(SoundEvents.field_15233, 1.0F, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	public int method_6502() {
		return this.field_6759;
	}

	public boolean isPlayerCreated() {
		return (this.field_6011.get(field_6763) & 1) != 0;
	}

	public void setPlayerCreated(boolean bl) {
		byte b = this.field_6011.get(field_6763);
		if (bl) {
			this.field_6011.set(field_6763, (byte)(b | 1));
		} else {
			this.field_6011.set(field_6763, (byte)(b & -2));
		}
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		super.onDeath(damageSource);
	}

	@Override
	public boolean method_5957(ViewableWorld viewableWorld) {
		BlockPos blockPos = new BlockPos(this);
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState = viewableWorld.method_8320(blockPos2);
		if (!blockState.method_11631(viewableWorld, blockPos2)) {
			return false;
		} else {
			BlockPos blockPos3 = blockPos.up();
			BlockState blockState2 = viewableWorld.method_8320(blockPos3);
			return SpawnHelper.method_8662(viewableWorld, blockPos3, blockState2, blockState2.method_11618())
				&& SpawnHelper.method_8662(viewableWorld, blockPos, viewableWorld.method_8320(blockPos), Fluids.EMPTY.method_15785())
				&& viewableWorld.method_8606(this);
		}
	}
}
