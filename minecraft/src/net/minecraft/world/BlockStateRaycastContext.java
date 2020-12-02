package net.minecraft.world;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3d;

public class BlockStateRaycastContext {
	private final Vec3d start;
	private final Vec3d end;
	private final Predicate<BlockState> state;

	public BlockStateRaycastContext(Vec3d start, Vec3d end, Predicate<BlockState> state) {
		this.start = start;
		this.end = end;
		this.state = state;
	}

	public Vec3d getEnd() {
		return this.end;
	}

	public Vec3d getStart() {
		return this.start;
	}

	public Predicate<BlockState> getState() {
		return this.state;
	}
}
