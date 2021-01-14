package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
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

	public void setLocation(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos.toImmutable();
	}

	public boolean hasWorld() {
		return this.world != null;
	}

	public void fromTag(BlockState state, NbtCompound tag) {
		this.pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
	}

	public NbtCompound writeNbt(NbtCompound nbt) {
		return this.writeIdentifyingData(nbt);
	}

	private NbtCompound writeIdentifyingData(NbtCompound nbt) {
		Identifier identifier = BlockEntityType.getId(this.getType());
		if (identifier == null) {
			throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
		} else {
			nbt.putString("id", identifier.toString());
			nbt.putInt("x", this.pos.getX());
			nbt.putInt("y", this.pos.getY());
			nbt.putInt("z", this.pos.getZ());
			return nbt;
		}
	}

	@Nullable
	public static BlockEntity createFromTag(BlockState state, NbtCompound tag) {
		String string = tag.getString("id");
		return (BlockEntity)Registry.BLOCK_ENTITY_TYPE.getOrEmpty(new Identifier(string)).map(blockEntityType -> {
			try {
				return blockEntityType.instantiate();
			} catch (Throwable var3) {
				LOGGER.error("Failed to create block entity {}", string, var3);
				return null;
			}
		}).map(blockEntity -> {
			try {
				blockEntity.fromTag(state, tag);
				return blockEntity;
			} catch (Throwable var5) {
				LOGGER.error("Failed to load data for block entity {}", string, var5);
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
				this.world.updateComparators(this.pos, this.cachedState.getBlock());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public double getRenderDistance() {
		return 64.0;
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

	public NbtCompound toInitialChunkDataNbt() {
		return this.writeIdentifyingData(new NbtCompound());
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

	public void resetBlock() {
		this.cachedState = null;
	}

	public void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Name", (CrashCallable<String>)(() -> Registry.BLOCK_ENTITY_TYPE.getId(this.getType()) + " // " + this.getClass().getCanonicalName()));
		if (this.world != null) {
			CrashReportSection.addBlockInfo(crashReportSection, this.pos, this.getCachedState());
			CrashReportSection.addBlockInfo(crashReportSection, this.pos, this.world.getBlockState(this.pos));
		}
	}

	public void setPos(BlockPos pos) {
		this.pos = pos.toImmutable();
	}

	public boolean copyItemDataRequiresOperator() {
		return false;
	}

	public void applyRotation(BlockRotation rotation) {
	}

	public void applyMirror(BlockMirror mirror) {
	}

	public BlockEntityType<?> getType() {
		return this.type;
	}

	public void markInvalid() {
		if (!this.invalid) {
			this.invalid = true;
			LOGGER.warn("Block entity invalid: {} @ {}", () -> Registry.BLOCK_ENTITY_TYPE.getId(this.getType()), this::getPos);
		}
	}
}
