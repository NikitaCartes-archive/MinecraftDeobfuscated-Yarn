package net.minecraft.entity.decoration;

import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;

public class EnderCrystalEntity extends Entity {
	private static final TrackedData<Optional<BlockPos>> BEAM_TARGET = DataTracker.registerData(
		EnderCrystalEntity.class, TrackedDataHandlerRegistry.OPTIONA_BLOCK_POS
	);
	private static final TrackedData<Boolean> SHOW_BOTTOM = DataTracker.registerData(EnderCrystalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public int field_7034;

	public EnderCrystalEntity(EntityType<? extends EnderCrystalEntity> entityType, World world) {
		super(entityType, world);
		this.field_6033 = true;
		this.field_7034 = this.random.nextInt(100000);
	}

	public EnderCrystalEntity(World world, double d, double e, double f) {
		this(EntityType.END_CRYSTAL, world);
		this.setPosition(d, e, f);
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(BEAM_TARGET, Optional.empty());
		this.getDataTracker().startTracking(SHOW_BOTTOM, true);
	}

	@Override
	public void update() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.field_7034++;
		if (!this.world.isClient) {
			BlockPos blockPos = new BlockPos(this);
			if (this.world.dimension instanceof TheEndDimension && this.world.getBlockState(blockPos).isAir()) {
				this.world.setBlockState(blockPos, Blocks.field_10036.getDefaultState());
			}
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		if (this.getBeamTarget() != null) {
			compoundTag.put("BeamTarget", TagHelper.serializeBlockPos(this.getBeamTarget()));
		}

		compoundTag.putBoolean("ShowBottom", this.method_6836());
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		if (compoundTag.containsKey("BeamTarget", 10)) {
			this.setBeamTarget(TagHelper.deserializeBlockPos(compoundTag.getCompound("BeamTarget")));
		}

		if (compoundTag.containsKey("ShowBottom", 1)) {
			this.setShowBottom(compoundTag.getBoolean("ShowBottom"));
		}
	}

	@Override
	public boolean doesCollide() {
		return true;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (damageSource.getAttacker() instanceof EnderDragonEntity) {
			return false;
		} else {
			if (!this.invalid && !this.world.isClient) {
				this.invalidate();
				if (!this.world.isClient) {
					if (!damageSource.isExplosive()) {
						this.world.createExplosion(null, this.x, this.y, this.z, 6.0F, true);
					}

					this.crystalDestroyed(damageSource);
				}
			}

			return true;
		}
	}

	@Override
	public void kill() {
		this.crystalDestroyed(DamageSource.GENERIC);
		super.kill();
	}

	private void crystalDestroyed(DamageSource damageSource) {
		if (this.world.dimension instanceof TheEndDimension) {
			TheEndDimension theEndDimension = (TheEndDimension)this.world.dimension;
			EnderDragonFight enderDragonFight = theEndDimension.method_12513();
			if (enderDragonFight != null) {
				enderDragonFight.crystalDestroyed(this, damageSource);
			}
		}
	}

	public void setBeamTarget(@Nullable BlockPos blockPos) {
		this.getDataTracker().set(BEAM_TARGET, Optional.ofNullable(blockPos));
	}

	@Nullable
	public BlockPos getBeamTarget() {
		return (BlockPos)this.getDataTracker().get(BEAM_TARGET).orElse(null);
	}

	public void setShowBottom(boolean bl) {
		this.getDataTracker().set(SHOW_BOTTOM, bl);
	}

	public boolean method_6836() {
		return this.getDataTracker().get(SHOW_BOTTOM);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		return super.shouldRenderAtDistance(d) || this.getBeamTarget() != null;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
