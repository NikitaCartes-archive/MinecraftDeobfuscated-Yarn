package net.minecraft.block.pattern;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_4538;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class CachedBlockPosition {
	private final class_4538 world;
	private final BlockPos pos;
	private final boolean forceLoad;
	private BlockState state;
	private BlockEntity blockEntity;
	private boolean cachedEntity;

	public CachedBlockPosition(class_4538 arg, BlockPos blockPos, boolean bl) {
		this.world = arg;
		this.pos = blockPos.toImmutable();
		this.forceLoad = bl;
	}

	public BlockState getBlockState() {
		if (this.state == null && (this.forceLoad || this.world.method_22340(this.pos))) {
			this.state = this.world.getBlockState(this.pos);
		}

		return this.state;
	}

	@Nullable
	public BlockEntity getBlockEntity() {
		if (this.blockEntity == null && !this.cachedEntity) {
			this.blockEntity = this.world.getBlockEntity(this.pos);
			this.cachedEntity = true;
		}

		return this.blockEntity;
	}

	public class_4538 getWorld() {
		return this.world;
	}

	public BlockPos getBlockPos() {
		return this.pos;
	}

	public static Predicate<CachedBlockPosition> matchesBlockState(Predicate<BlockState> predicate) {
		return cachedBlockPosition -> cachedBlockPosition != null && predicate.test(cachedBlockPosition.getBlockState());
	}
}
