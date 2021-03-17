package net.minecraft.command.argument;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;

public class BlockStateArgument implements Predicate<CachedBlockPosition> {
	private final BlockState state;
	private final Set<Property<?>> properties;
	@Nullable
	private final NbtCompound data;

	public BlockStateArgument(BlockState state, Set<Property<?>> properties, @Nullable NbtCompound data) {
		this.state = state;
		this.properties = properties;
		this.data = data;
	}

	public BlockState getBlockState() {
		return this.state;
	}

	public boolean test(CachedBlockPosition cachedBlockPosition) {
		BlockState blockState = cachedBlockPosition.getBlockState();
		if (!blockState.isOf(this.state.getBlock())) {
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
				return blockEntity != null && NbtHelper.matches(this.data, blockEntity.writeNbt(new NbtCompound()), true);
			}
		}
	}

	public boolean setBlockState(ServerWorld serverWorld, BlockPos blockPos, int i) {
		BlockState blockState = Block.postProcessState(this.state, serverWorld, blockPos);
		if (blockState.isAir()) {
			blockState = this.state;
		}

		if (!serverWorld.setBlockState(blockPos, blockState, i)) {
			return false;
		} else {
			if (this.data != null) {
				BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
				if (blockEntity != null) {
					NbtCompound nbtCompound = this.data.copy();
					nbtCompound.putInt("x", blockPos.getX());
					nbtCompound.putInt("y", blockPos.getY());
					nbtCompound.putInt("z", blockPos.getZ());
					blockEntity.readNbt(nbtCompound);
				}
			}

			return true;
		}
	}
}
