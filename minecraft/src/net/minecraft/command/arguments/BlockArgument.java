package net.minecraft.command.arguments;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;

public class BlockArgument implements Predicate<CachedBlockPosition> {
	private final BlockState state;
	private final Set<Property<?>> properties;
	@Nullable
	private final CompoundTag field_10633;

	public BlockArgument(BlockState blockState, Set<Property<?>> set, @Nullable CompoundTag compoundTag) {
		this.state = blockState;
		this.properties = set;
		this.field_10633 = compoundTag;
	}

	public BlockState getBlockState() {
		return this.state;
	}

	public boolean method_9493(CachedBlockPosition cachedBlockPosition) {
		BlockState blockState = cachedBlockPosition.getBlockState();
		if (blockState.getBlock() != this.state.getBlock()) {
			return false;
		} else {
			for (Property<?> property : this.properties) {
				if (blockState.method_11654(property) != this.state.method_11654(property)) {
					return false;
				}
			}

			if (this.field_10633 == null) {
				return true;
			} else {
				BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
				return blockEntity != null && TagHelper.method_10687(this.field_10633, blockEntity.method_11007(new CompoundTag()), true);
			}
		}
	}

	public boolean method_9495(ServerWorld serverWorld, BlockPos blockPos, int i) {
		if (!serverWorld.method_8652(blockPos, this.state, i)) {
			return false;
		} else {
			if (this.field_10633 != null) {
				BlockEntity blockEntity = serverWorld.method_8321(blockPos);
				if (blockEntity != null) {
					CompoundTag compoundTag = this.field_10633.method_10553();
					compoundTag.putInt("x", blockPos.getX());
					compoundTag.putInt("y", blockPos.getY());
					compoundTag.putInt("z", blockPos.getZ());
					blockEntity.method_11014(compoundTag);
				}
			}

			return true;
		}
	}
}
