package net.minecraft;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.BlockProxy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;

public class class_2247 implements Predicate<BlockProxy> {
	private final BlockState field_10632;
	private final Set<Property<?>> field_10631;
	@Nullable
	private final CompoundTag field_10633;

	public class_2247(BlockState blockState, Set<Property<?>> set, @Nullable CompoundTag compoundTag) {
		this.field_10632 = blockState;
		this.field_10631 = set;
		this.field_10633 = compoundTag;
	}

	public BlockState method_9494() {
		return this.field_10632;
	}

	public boolean method_9493(BlockProxy blockProxy) {
		BlockState blockState = blockProxy.getBlockState();
		if (blockState.getBlock() != this.field_10632.getBlock()) {
			return false;
		} else {
			for (Property<?> property : this.field_10631) {
				if (blockState.get(property) != this.field_10632.get(property)) {
					return false;
				}
			}

			if (this.field_10633 == null) {
				return true;
			} else {
				BlockEntity blockEntity = blockProxy.getBlockEntity();
				return blockEntity != null && TagHelper.areTagsEqual(this.field_10633, blockEntity.toTag(new CompoundTag()), true);
			}
		}
	}

	public boolean method_9495(ServerWorld serverWorld, BlockPos blockPos, int i) {
		if (!serverWorld.setBlockState(blockPos, this.field_10632, i)) {
			return false;
		} else {
			if (this.field_10633 != null) {
				BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
				if (blockEntity != null) {
					CompoundTag compoundTag = this.field_10633.copy();
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
