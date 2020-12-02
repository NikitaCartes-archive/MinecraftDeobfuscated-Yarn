package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
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
	protected final BlockPos pos;
	protected boolean removed;
	private BlockState cachedState;

	public BlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		this.type = blockEntityType;
		this.pos = blockPos.toImmutable();
		this.cachedState = blockState;
	}

	@Nullable
	public World getWorld() {
		return this.world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public boolean hasWorld() {
		return this.world != null;
	}

	public void fromTag(CompoundTag tag) {
	}

	public CompoundTag toTag(CompoundTag tag) {
		return this.writeIdentifyingData(tag);
	}

	private CompoundTag writeIdentifyingData(CompoundTag tag) {
		Identifier identifier = BlockEntityType.getId(this.getType());
		if (identifier == null) {
			throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
		} else {
			tag.putString("id", identifier.toString());
			tag.putInt("x", this.pos.getX());
			tag.putInt("y", this.pos.getY());
			tag.putInt("z", this.pos.getZ());
			return tag;
		}
	}

	@Nullable
	public static BlockEntity createFromTag(BlockPos blockPos, BlockState blockState, CompoundTag compoundTag) {
		String string = compoundTag.getString("id");
		return (BlockEntity)Registry.BLOCK_ENTITY_TYPE.getOrEmpty(new Identifier(string)).map(blockEntityType -> {
			try {
				return blockEntityType.instantiate(blockPos, blockState);
			} catch (Throwable var5) {
				LOGGER.error("Failed to create block entity {}", string, var5);
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
			markDirty(this.world, this.pos, this.cachedState);
		}
	}

	protected static void markDirty(World world, BlockPos blockPos, BlockState blockState) {
		world.markDirty(blockPos);
		if (!blockState.isAir()) {
			world.updateComparators(blockPos, blockState.getBlock());
		}
	}

	@Environment(EnvType.CLIENT)
	public double getSquaredRenderDistance() {
		return 64.0;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public BlockState getCachedState() {
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

	public boolean onSyncedBlockEvent(int type, int data) {
		return false;
	}

	public void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Name", (CrashCallable<String>)(() -> Registry.BLOCK_ENTITY_TYPE.getId(this.getType()) + " // " + this.getClass().getCanonicalName()));
		if (this.world != null) {
			CrashReportSection.addBlockInfo(crashReportSection, this.world, this.pos, this.getCachedState());
			CrashReportSection.addBlockInfo(crashReportSection, this.world, this.pos, this.world.getBlockState(this.pos));
		}
	}

	public boolean copyItemDataRequiresOperator() {
		return false;
	}

	public BlockEntityType<?> getType() {
		return this.type;
	}

	@Deprecated
	public void setCachedState(BlockState blockState) {
		this.cachedState = blockState;
	}
}
