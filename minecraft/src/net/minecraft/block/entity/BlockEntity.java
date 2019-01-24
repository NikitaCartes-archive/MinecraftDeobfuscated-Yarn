package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockEntity {
	private static final Logger LOGGER = LogManager.getLogger();
	private final BlockEntityType<?> type;
	protected World world;
	protected BlockPos pos = BlockPos.ORIGIN;
	protected boolean invalid;
	@Nullable
	private BlockState cachedState;

	public BlockEntity(BlockEntityType<?> blockEntityType) {
		this.type = blockEntityType;
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
		return (BlockEntity)Registry.BLOCK_ENTITY.method_17966(new Identifier(string)).map(blockEntityType -> {
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
	public double getSquaredDistance(double d, double e, double f) {
		double g = (double)this.pos.getX() + 0.5 - d;
		double h = (double)this.pos.getY() + 0.5 - e;
		double i = (double)this.pos.getZ() + 0.5 - f;
		return g * g + h * h + i * i;
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
	public BlockEntityUpdateClientPacket toUpdatePacket() {
		return null;
	}

	public CompoundTag toInitialChunkDataTag() {
		return this.writeIdentifyingData(new CompoundTag());
	}

	public boolean isInvalid() {
		return this.invalid;
	}

	public void invalidate() {
		this.invalid = true;
	}

	public void validate() {
		this.invalid = false;
	}

	public boolean onBlockAction(int i, int j) {
		return false;
	}

	public void resetBlock() {
		this.cachedState = null;
	}

	public void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Name", (ICrashCallable<String>)(() -> Registry.BLOCK_ENTITY.getId(this.getType()) + " // " + this.getClass().getCanonicalName()));
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

	public void applyRotation(Rotation rotation) {
	}

	public void applyMirror(Mirror mirror) {
	}

	public BlockEntityType<?> getType() {
		return this.type;
	}
}
