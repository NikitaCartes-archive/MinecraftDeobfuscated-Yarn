package net.minecraft.block.pattern;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ViewableWorld;

public class BlockProxy {
	private final ViewableWorld field_12330;
	private final BlockPos pos;
	private final boolean forceLoad;
	private BlockState state;
	private BlockEntity blockEntity;
	private boolean cachedEntity;

	public BlockProxy(ViewableWorld viewableWorld, BlockPos blockPos, boolean bl) {
		this.field_12330 = viewableWorld;
		this.pos = blockPos;
		this.forceLoad = bl;
	}

	public BlockState getBlockState() {
		if (this.state == null && (this.forceLoad || this.field_12330.isBlockLoaded(this.pos))) {
			this.state = this.field_12330.getBlockState(this.pos);
		}

		return this.state;
	}

	@Nullable
	public BlockEntity getBlockEntity() {
		if (this.blockEntity == null && !this.cachedEntity) {
			this.blockEntity = this.field_12330.getBlockEntity(this.pos);
			this.cachedEntity = true;
		}

		return this.blockEntity;
	}

	public ViewableWorld method_11679() {
		return this.field_12330;
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public static Predicate<BlockProxy> method_11678(Predicate<BlockState> predicate) {
		return blockProxy -> blockProxy != null && predicate.test(blockProxy.getBlockState());
	}
}
