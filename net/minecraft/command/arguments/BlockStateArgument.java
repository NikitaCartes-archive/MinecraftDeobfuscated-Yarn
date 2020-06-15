/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.arguments;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BlockStateArgument
implements Predicate<CachedBlockPosition> {
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

    @Override
    public boolean test(CachedBlockPosition cachedBlockPosition) {
        BlockState blockState = cachedBlockPosition.getBlockState();
        if (!blockState.isOf(this.state.getBlock())) {
            return false;
        }
        for (Property<?> property : this.properties) {
            if (blockState.get(property) == this.state.get(property)) continue;
            return false;
        }
        if (this.data != null) {
            BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
            return blockEntity != null && NbtHelper.matches(this.data, blockEntity.toTag(new CompoundTag()), true);
        }
        return true;
    }

    public boolean setBlockState(ServerWorld serverWorld, BlockPos blockPos, int i) {
        BlockEntity blockEntity;
        BlockState blockState = Block.postProcessState(this.state, serverWorld, blockPos);
        if (!serverWorld.setBlockState(blockPos, blockState, i)) {
            return false;
        }
        if (this.data != null && (blockEntity = serverWorld.getBlockEntity(blockPos)) != null) {
            CompoundTag compoundTag = this.data.copy();
            compoundTag.putInt("x", blockPos.getX());
            compoundTag.putInt("y", blockPos.getY());
            compoundTag.putInt("z", blockPos.getZ());
            blockEntity.fromTag(blockState, compoundTag);
        }
        return true;
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((CachedBlockPosition)context);
    }
}

