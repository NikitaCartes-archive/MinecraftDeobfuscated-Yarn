package net.minecraft.command.arguments;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;

public class BlockStateArgument implements Predicate<CachedBlockPosition> {
	private final BlockState state;
	private final Set<Property<?>> properties;
	@Nullable
	private final CompoundTag data;

	public BlockStateArgument(BlockState state, Set<Property<?>> properties, @Nullable CompoundTag data) {
		this.state = state;
		this.properties = properties;
		this.data = data;
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
				if (blockState.get(property) != this.state.get(property)) {
					return false;
				}
			}

			if (this.data == null) {
				return true;
			} else {
				BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
				return blockEntity != null && NbtHelper.matches(this.data, blockEntity.toTag(new CompoundTag()), true);
			}
		}
	}

	public boolean setBlockState(ServerWorld serverWorld, BlockPos blockPos, int i) {
		if (!serverWorld.setBlockState(blockPos, this.state, i)) {
			return false;
		} else {
			if (this.data != null) {
				BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
				if (blockEntity != null) {
					CompoundTag compoundTag = this.data.method_10553();
					compoundTag.putInt("x", blockPos.getX());
					compoundTag.putInt("y", blockPos.getY());
					compoundTag.putInt("z", blockPos.getZ());
					blockEntity.fromTag(compoundTag);
				}
			}

			return true;
		}
	}
}
