package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class SnowGolemEntity extends GolemEntity implements Shearable, RangedAttackMob {
	private static final TrackedData<Byte> SNOW_GOLEM_FLAGS = DataTracker.registerData(SnowGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final byte HAS_PUMPKIN_FLAG = 16;

	public SnowGolemEntity(EntityType<? extends SnowGolemEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.25, 20, 10.0F));
		this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0, 1.0000001E-5F));
		this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(4, new LookAroundGoal(this));
		this.targetSelector.add(1, new ActiveTargetGoal(this, MobEntity.class, 10, true, false, (entity, serverWorld) -> entity instanceof Monster));
	}

	public static DefaultAttributeContainer.Builder createSnowGolemAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.MAX_HEALTH, 4.0).add(EntityAttributes.MOVEMENT_SPEED, 0.2F);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(SNOW_GOLEM_FLAGS, (byte)16);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("Pumpkin", this.hasPumpkin());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("Pumpkin")) {
			this.setHasPumpkin(nbt.getBoolean("Pumpkin"));
		}
	}

	@Override
	public boolean hurtByWater() {
		return true;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.getWorld() instanceof ServerWorld serverWorld) {
			if (this.getWorld().getBiome(this.getBlockPos()).isIn(BiomeTags.SNOW_GOLEM_MELTS)) {
				this.damage(serverWorld, this.getDamageSources().onFire(), 1.0F);
			}

			if (!serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
				return;
			}

			BlockState blockState = Blocks.SNOW.getDefaultState();

			for (int i = 0; i < 4; i++) {
				int j = MathHelper.floor(this.getX() + (double)((float)(i % 2 * 2 - 1) * 0.25F));
				int k = MathHelper.floor(this.getY());
				int l = MathHelper.floor(this.getZ() + (double)((float)(i / 2 % 2 * 2 - 1) * 0.25F));
				BlockPos blockPos = new BlockPos(j, k, l);
				if (this.getWorld().getBlockState(blockPos).isAir() && blockState.canPlaceAt(this.getWorld(), blockPos)) {
					this.getWorld().setBlockState(blockPos, blockState);
					this.getWorld().emitGameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Emitter.of(this, blockState));
				}
			}
		}
	}

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		double d = target.getX() - this.getX();
		double e = target.getEyeY() - 1.1F;
		double f = target.getZ() - this.getZ();
		double g = Math.sqrt(d * d + f * f) * 0.2F;
		if (this.getWorld() instanceof ServerWorld serverWorld) {
			ItemStack itemStack = new ItemStack(Items.SNOWBALL);
			ProjectileEntity.spawn(
				new SnowballEntity(serverWorld, this, itemStack), serverWorld, itemStack, entity -> entity.setVelocity(d, e + g - entity.getY(), f, 1.6F, 12.0F)
			);
		}

		this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.isOf(Items.SHEARS) && this.isShearable()) {
			if (this.getWorld() instanceof ServerWorld serverWorld) {
				this.sheared(serverWorld, SoundCategory.PLAYERS, itemStack);
				this.emitGameEvent(GameEvent.SHEAR, player);
				itemStack.damage(1, player, getSlotForHand(hand));
			}

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public void sheared(ServerWorld world, SoundCategory shearedSoundCategory, ItemStack shears) {
		world.playSoundFromEntity(null, this, SoundEvents.ENTITY_SNOW_GOLEM_SHEAR, shearedSoundCategory, 1.0F, 1.0F);
		this.setHasPumpkin(false);
		this.forEachShearedItem(
			world, LootTables.SNOW_GOLEM_SHEARING, shears, (serverWorld, itemStack) -> this.dropStack(serverWorld, itemStack, this.getStandingEyeHeight())
		);
	}

	@Override
	public boolean isShearable() {
		return this.isAlive() && this.hasPumpkin();
	}

	public boolean hasPumpkin() {
		return (this.dataTracker.get(SNOW_GOLEM_FLAGS) & 16) != 0;
	}

	public void setHasPumpkin(boolean hasPumpkin) {
		byte b = this.dataTracker.get(SNOW_GOLEM_FLAGS);
		if (hasPumpkin) {
			this.dataTracker.set(SNOW_GOLEM_FLAGS, (byte)(b | 16));
		} else {
			this.dataTracker.set(SNOW_GOLEM_FLAGS, (byte)(b & -17));
		}
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SNOW_GOLEM_AMBIENT;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SNOW_GOLEM_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SNOW_GOLEM_DEATH;
	}

	@Override
	public Vec3d getLeashOffset() {
		return new Vec3d(0.0, (double)(0.75F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
	}
}
