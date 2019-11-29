package net.minecraft;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class class_4739<E extends BlockEntity> extends BlockWithEntity {
	protected final Supplier<BlockEntityType<? extends E>> field_21796;

	protected class_4739(Block.Settings settings, Supplier<BlockEntityType<? extends E>> supplier) {
		super(settings);
		this.field_21796 = supplier;
	}

	@Environment(EnvType.CLIENT)
	public abstract DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> method_24167(
		BlockState blockState, World world, BlockPos blockPos, boolean bl
	);
}
