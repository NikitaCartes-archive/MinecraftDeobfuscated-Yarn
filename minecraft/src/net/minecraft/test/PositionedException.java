package net.minecraft.test;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class PositionedException extends TimeMismatchException {
	private final BlockPos pos;
	private final BlockPos relativePos;
	private final long field_21449;

	public String getMessage() {
		String string = ""
			+ this.pos.getX()
			+ ","
			+ this.pos.getY()
			+ ","
			+ this.pos.getZ()
			+ " (relative: "
			+ this.relativePos.getX()
			+ ","
			+ this.relativePos.getY()
			+ ","
			+ this.relativePos.getZ()
			+ ")";
		return super.getMessage() + " at " + string + " (t=" + this.field_21449 + ")";
	}

	@Nullable
	public String getDebugMessage() {
		return super.getMessage() + " here";
	}

	@Nullable
	public BlockPos getPos() {
		return this.pos;
	}
}
