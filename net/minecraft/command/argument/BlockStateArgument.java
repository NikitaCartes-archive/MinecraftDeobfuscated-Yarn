/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.NbtCompound;
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
    private final NbtCompound data;

    public BlockStateArgument(BlockState state, Set<Property<?>> properties, @Nullable NbtCompound data) {
        this.state = state;
        this.properties = properties;
        this.data = data;
    }

    public BlockState getBlockState() {
        return this.state;
    }

    public Set<Property<?>> getProperties() {
        return this.properties;
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
            return blockEntity != null && NbtHelper.matches(this.data, blockEntity.createNbtWithIdentifyingData(), true);
        }
        return true;
    }

    public boolean test(ServerWorld world, BlockPos pos) {
        return this.test(new CachedBlockPosition(world, pos, false));
    }

    public boolean setBlockState(ServerWorld world, BlockPos pos, int flags) {
        BlockEntity blockEntity;
        BlockState blockState = Block.postProcessState(this.state, world, pos);
        if (blockState.isAir()) {
            blockState = this.state;
        }
        if (!world.setBlockState(pos, blockState, flags)) {
            return false;
        }
        if (this.data != null && (blockEntity = world.getBlockEntity(pos)) != null) {
            blockEntity.readNbt(this.data);
        }
        return true;
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((CachedBlockPosition)context);
    }
}

