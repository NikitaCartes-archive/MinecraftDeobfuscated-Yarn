package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockEntity {
	private static final Logger LOGGER = LogManager.getLogger();
	private final BlockEntityType<?> type;
	@Nullable
	protected World world;
	protected BlockPos pos = BlockPos.ORIGIN;
	protected boolean removed;
	@Nullable
	private BlockState cachedState;
	private boolean invalid;

	public BlockEntity(BlockEntityType<?> type) {
		this.type = type;
	}

	@Nullable
	public World getWorld() {
		return this.world;
	}

	public void setWorld(World world, BlockPos blockPos) {
		this.world = world;
		this.pos = blockPos;
	}

	public boolean hasWorld() {
		return this.world != null;
	}

	public void fromTag(CompoundTag compoundTag) {
		this.pos = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
	}

	public CompoundTag toTag(CompoundTag compoundTag) {
		return this.writeIdentifyingData(compoundTag);
	}

	private CompoundTag writeIdentifyingData(CompoundTag compoundTag) {
		Identifier identifier = BlockEntityType.getId(this.getType());
		if (identifier == null) {
			throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
		} else {
			compoundTag.putString("id", identifier.toString());
			compoundTag.putInt("x", this.pos.getX());
			compoundTag.putInt("y", this.pos.getY());
			compoundTag.putInt("z", this.pos.getZ());
			return compoundTag;
		}
	}

	@Nullable
	public static BlockEntity createFromTag(CompoundTag compoundTag) {
		String string = compoundTag.getString("id");
		return (BlockEntity)Registry.BLOCK_ENTITY.getOrEmpty(new Identifier(string)).map(blockEntityType -> {
			try {
				return blockEntityType.instantiate();
			} catch (Throwable var3) {
				LOGGER.error("Failed to create block entity {}", string, var3);
				return null;
			}
		}).map(blockEntity -> {
			try {
				blockEntity.fromTag(compoundTag);
				return blockEntity;
			} catch (Throwable var4) {
				LOGGER.error("Failed to load data for block entity {}", string, var4);
				return null;
			}
		}).orElseGet(() -> {
			LOGGER.warn("Skipping BlockEntity with id {}", string);
			return null;
		});
	}

	public void markDirty() {
		if (this.world != null) {
			this.cachedState = this.world.getBlockState(this.pos);
			this.world.markDirty(this.pos, this);
			if (!this.cachedState.isAir()) {
				this.world.updateHorizontalAdjacent(this.pos, this.cachedState.getBlock());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public double getSquaredDistance(double x, double y, double z) {
		double d = (double)this.pos.getX() + 0.5 - x;
		double e = (double)this.pos.getY() + 0.5 - y;
		double f = (double)this.pos.getZ() + 0.5 - z;
		return d * d + e * e + f * f;
	}

	@Environment(EnvType.CLIENT)
	public double getSquaredRenderDistance() {
		return 4096.0;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public BlockState getCachedState() {
		if (this.cachedState == null) {
			this.cachedState = this.world.getBlockState(this.pos);
		}

		return this.cachedState;
	}

	@Nullable
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return null;
	}

	public CompoundTag toInitialChunkDataTag() {
		return this.writeIdentifyingData(new CompoundTag());
	}

	public boolean isRemoved() {
		return this.removed;
	}

	public void markRemoved() {
		this.removed = true;
	}

	public void cancelRemoval() {
		this.removed = false;
	}

	public boolean onBlockAction(int i, int j) {
		return false;
	}

	public void resetBlock() {
		this.cachedState = null;
	}

	public void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Name", (CrashCallable<String>)(() -> Registry.BLOCK_ENTITY.getId(this.getType()) + " // " + this.getClass().getCanonicalName()));
		if (this.world != null) {
			CrashReportSection.addBlockInfo(crashReportSection, this.pos, this.getCachedState());
			CrashReportSection.addBlockInfo(crashReportSection, this.pos, this.world.getBlockState(this.pos));
		}
	}

	public void setPos(BlockPos blockPos) {
		this.pos = blockPos.toImmutable();
	}

	public boolean shouldNotCopyTagFromItem() {
		return false;
	}

	public void applyRotation(BlockRotation blockRotation) {
	}

	public void applyMirror(BlockMirror blockMirror) {
	}

	public BlockEntityType<?> getType() {
		return this.type;
	}

	public void markInvalid() {
		if (!this.invalid) {
			this.invalid = true;
			LOGGER.warn("Block entity invalid: {} @ {}", () -> Registry.BLOCK_ENTITY.getId(this.getType()), this::getPos);
		}
	}
}
