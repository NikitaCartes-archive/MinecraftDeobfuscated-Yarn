package net.minecraft.entity.decoration;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;

public abstract class BlockAttachedEntity extends Entity {
	private static final Logger LOGGER = LogUtils.getLogger();
	private int attachCheckTimer;
	protected BlockPos attachedBlockPos;

	protected BlockAttachedEntity(EntityType<? extends BlockAttachedEntity> entityType, World world) {
		super(entityType, world);
	}

	protected BlockAttachedEntity(EntityType<? extends BlockAttachedEntity> type, World world, BlockPos attachedBlockPos) {
		this(type, world);
		this.attachedBlockPos = attachedBlockPos;
	}

	protected abstract void updateAttachmentPosition();

	@Override
	public void tick() {
		if (!this.getWorld().isClient) {
			this.attemptTickInVoid();
			if (this.attachCheckTimer++ == 100) {
				this.attachCheckTimer = 0;
				if (!this.isRemoved() && !this.canStayAttached()) {
					this.discard();
					this.onBreak(null);
				}
			}
		}
	}

	public abstract boolean canStayAttached();

	@Override
	public boolean canHit() {
		return true;
	}

	@Override
	public boolean handleAttack(Entity attacker) {
		if (attacker instanceof PlayerEntity playerEntity) {
			return !this.getWorld().canPlayerModifyAt(playerEntity, this.attachedBlockPos)
				? true
				: this.damage(this.getDamageSources().playerAttack(playerEntity), 0.0F);
		} else {
			return false;
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if (!this.isRemoved() && !this.getWorld().isClient) {
				this.kill();
				this.scheduleVelocityUpdate();
				this.onBreak(source.getAttacker());
			}

			return true;
		}
	}

	@Override
	public void move(MovementType movementType, Vec3d movement) {
		if (!this.getWorld().isClient && !this.isRemoved() && movement.lengthSquared() > 0.0) {
			this.kill();
			this.onBreak(null);
		}
	}

	@Override
	public void addVelocity(double deltaX, double deltaY, double deltaZ) {
		if (!this.getWorld().isClient && !this.isRemoved() && deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > 0.0) {
			this.kill();
			this.onBreak(null);
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		BlockPos blockPos = this.getAttachedBlockPos();
		nbt.putInt("TileX", blockPos.getX());
		nbt.putInt("TileY", blockPos.getY());
		nbt.putInt("TileZ", blockPos.getZ());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		BlockPos blockPos = new BlockPos(nbt.getInt("TileX"), nbt.getInt("TileY"), nbt.getInt("TileZ"));
		if (!blockPos.isWithinDistance(this.getBlockPos(), 16.0)) {
			LOGGER.error("Block-attached entity at invalid position: {}", blockPos);
		} else {
			this.attachedBlockPos = blockPos;
		}
	}

	public abstract void onBreak(@Nullable Entity breaker);

	@Override
	protected boolean shouldSetPositionOnLoad() {
		return false;
	}

	@Override
	public void setPosition(double x, double y, double z) {
		this.attachedBlockPos = BlockPos.ofFloored(x, y, z);
		this.updateAttachmentPosition();
		this.velocityDirty = true;
	}

	public BlockPos getAttachedBlockPos() {
		return this.attachedBlockPos;
	}

	@Override
	public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
	}

	@Override
	public void calculateDimensions() {
	}
}
