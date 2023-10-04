package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractChestBlock<E extends BlockEntity> extends BlockWithEntity {
	protected final Supplier<BlockEntityType<? extends E>> entityTypeRetriever;

	protected AbstractChestBlock(AbstractBlock.Settings settings, Supplier<BlockEntityType<? extends E>> entityTypeSupplier) {
		super(settings);
		this.entityTypeRetriever = entityTypeSupplier;
	}

	@Override
	protected abstract MapCodec<? extends AbstractChestBlock<E>> getCodec();

	public abstract DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(
		BlockState state, World world, BlockPos pos, boolean ignoreBlocked
	);
}
