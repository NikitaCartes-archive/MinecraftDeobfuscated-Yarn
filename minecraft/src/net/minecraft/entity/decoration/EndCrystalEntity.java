package net.minecraft.entity.decoration;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EndCrystalEntity extends Entity {
	private static final TrackedData<Optional<BlockPos>> BEAM_TARGET = DataTracker.registerData(
		EndCrystalEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_POS
	);
	private static final TrackedData<Boolean> SHOW_BOTTOM = DataTracker.registerData(EndCrystalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public int endCrystalAge;

	public EndCrystalEntity(EntityType<? extends EndCrystalEntity> entityType, World world) {
		super(entityType, world);
		this.intersectionChecked = true;
		this.endCrystalAge = this.random.nextInt(100000);
	}

	public EndCrystalEntity(World world, double x, double y, double z) {
		this(EntityType.END_CRYSTAL, world);
		this.setPosition(x, y, z);
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.NONE;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(BEAM_TARGET, Optional.empty());
		builder.add(SHOW_BOTTOM, true);
	}

	@Override
	public void tick() {
		this.endCrystalAge++;
		this.checkBlockCollision();
		this.tickPortalTeleportation();
		if (this.getWorld() instanceof ServerWorld) {
			BlockPos blockPos = this.getBlockPos();
			if (((ServerWorld)this.getWorld()).getEnderDragonFight() != null && this.getWorld().getBlockState(blockPos).isAir()) {
				this.getWorld().setBlockState(blockPos, AbstractFireBlock.getState(this.getWorld(), blockPos));
			}
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		if (this.getBeamTarget() != null) {
			nbt.put("beam_target", NbtHelper.fromBlockPos(this.getBeamTarget()));
		}

		nbt.putBoolean("ShowBottom", this.shouldShowBottom());
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		NbtHelper.toBlockPos(nbt, "beam_target").ifPresent(this::setBeamTarget);
		if (nbt.contains("ShowBottom", NbtElement.BYTE_TYPE)) {
			this.setShowBottom(nbt.getBoolean("ShowBottom"));
		}
	}

	@Override
	public boolean canHit() {
		return true;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (source.getAttacker() instanceof EnderDragonEntity) {
			return false;
		} else {
			if (!this.isRemoved() && !this.getWorld().isClient) {
				this.remove(Entity.RemovalReason.KILLED);
				if (!source.isIn(DamageTypeTags.IS_EXPLOSION)) {
					DamageSource damageSource = source.getAttacker() != null ? this.getDamageSources().explosion(this, source.getAttacker()) : null;
					this.getWorld().createExplosion(this, damageSource, null, this.getX(), this.getY(), this.getZ(), 6.0F, false, World.ExplosionSourceType.BLOCK);
				}

				this.crystalDestroyed(source);
			}

			return true;
		}
	}

	@Override
	public void kill() {
		this.crystalDestroyed(this.getDamageSources().generic());
		super.kill();
	}

	private void crystalDestroyed(DamageSource source) {
		if (this.getWorld() instanceof ServerWorld) {
			EnderDragonFight enderDragonFight = ((ServerWorld)this.getWorld()).getEnderDragonFight();
			if (enderDragonFight != null) {
				enderDragonFight.crystalDestroyed(this, source);
			}
		}
	}

	public void setBeamTarget(@Nullable BlockPos beamTarget) {
		this.getDataTracker().set(BEAM_TARGET, Optional.ofNullable(beamTarget));
	}

	@Nullable
	public BlockPos getBeamTarget() {
		return (BlockPos)this.getDataTracker().get(BEAM_TARGET).orElse(null);
	}

	public void setShowBottom(boolean showBottom) {
		this.getDataTracker().set(SHOW_BOTTOM, showBottom);
	}

	public boolean shouldShowBottom() {
		return this.getDataTracker().get(SHOW_BOTTOM);
	}

	@Override
	public boolean shouldRender(double distance) {
		return super.shouldRender(distance) || this.getBeamTarget() != null;
	}

	@Override
	public ItemStack getPickBlockStack() {
		return new ItemStack(Items.END_CRYSTAL);
	}
}
