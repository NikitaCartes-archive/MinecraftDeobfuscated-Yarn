package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockEntity {
	private static final Logger LOGGER = LogManager.getLogger();
	private final BlockEntityType<?> field_11864;
	protected World world;
	protected BlockPos field_11867 = BlockPos.ORIGIN;
	protected boolean invalid;
	@Nullable
	private BlockState field_11866;

	public BlockEntity(BlockEntityType<?> blockEntityType) {
		this.field_11864 = blockEntityType;
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

	public void method_11014(CompoundTag compoundTag) {
		this.field_11867 = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
	}

	public CompoundTag method_11007(CompoundTag compoundTag) {
		return this.method_10999(compoundTag);
	}

	private CompoundTag method_10999(CompoundTag compoundTag) {
		Identifier identifier = BlockEntityType.method_11033(this.method_11017());
		if (identifier == null) {
			throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
		} else {
			compoundTag.putString("id", identifier.toString());
			compoundTag.putInt("x", this.field_11867.getX());
			compoundTag.putInt("y", this.field_11867.getY());
			compoundTag.putInt("z", this.field_11867.getZ());
			return compoundTag;
		}
	}

	@Nullable
	public static BlockEntity method_11005(CompoundTag compoundTag) {
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
				blockEntity.method_11014(compoundTag);
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
			this.field_11866 = this.world.method_8320(this.field_11867);
			this.world.method_8524(this.field_11867, this);
			if (!this.field_11866.isAir()) {
				this.world.method_8455(this.field_11867, this.field_11866.getBlock());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public double getSquaredDistance(double d, double e, double f) {
		double g = (double)this.field_11867.getX() + 0.5 - d;
		double h = (double)this.field_11867.getY() + 0.5 - e;
		double i = (double)this.field_11867.getZ() + 0.5 - f;
		return g * g + h * h + i * i;
	}

	@Environment(EnvType.CLIENT)
	public double getSquaredRenderDistance() {
		return 4096.0;
	}

	public BlockPos method_11016() {
		return this.field_11867;
	}

	public BlockState method_11010() {
		if (this.field_11866 == null) {
			this.field_11866 = this.world.method_8320(this.field_11867);
		}

		return this.field_11866;
	}

	@Nullable
	public BlockEntityUpdateS2CPacket method_16886() {
		return null;
	}

	public CompoundTag method_16887() {
		return this.method_10999(new CompoundTag());
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
		this.field_11866 = null;
	}

	public void method_11003(CrashReportSection crashReportSection) {
		crashReportSection.method_577("Name", () -> Registry.BLOCK_ENTITY.method_10221(this.method_11017()) + " // " + this.getClass().getCanonicalName());
		if (this.world != null) {
			CrashReportSection.method_586(crashReportSection, this.field_11867, this.method_11010());
			CrashReportSection.method_586(crashReportSection, this.field_11867, this.world.method_8320(this.field_11867));
		}
	}

	public void method_10998(BlockPos blockPos) {
		this.field_11867 = blockPos.toImmutable();
	}

	public boolean shouldNotCopyTagFromItem() {
		return false;
	}

	public void applyRotation(Rotation rotation) {
	}

	public void applyMirror(Mirror mirror) {
	}

	public BlockEntityType<?> method_11017() {
		return this.field_11864;
	}
}
