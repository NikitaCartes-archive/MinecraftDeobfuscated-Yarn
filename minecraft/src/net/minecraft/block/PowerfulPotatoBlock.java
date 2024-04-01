package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class PowerfulPotatoBlock extends Block {
	public static final MapCodec<PowerfulPotatoBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Registries.BLOCK.getCodec().fieldOf("roots").forGetter(powerfulPotatoBlock -> powerfulPotatoBlock.field_50881), createSettingsCodec()
				)
				.apply(instance, PowerfulPotatoBlock::new)
	);
	public static final IntProperty AGE = Properties.AGE_3;
	public static final int MAX_AGE = 3;
	private final Block field_50881;

	@Override
	public MapCodec<PowerfulPotatoBlock> getCodec() {
		return CODEC;
	}

	protected PowerfulPotatoBlock(Block block, AbstractBlock.Settings settings) {
		super(settings);
		this.field_50881 = block;
		this.setDefaultState(this.stateManager.getDefaultState().with(AGE, Integer.valueOf(0)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	protected boolean hasRandomTicks(BlockState state) {
		return (Integer)state.get(AGE) < 3;
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		int i = (Integer)state.get(AGE);
		if (i < 3) {
			StrongRootsBlock.field_50889.getDataOrEmpty(random).ifPresent(direction -> {
				List<ItemStack> list = StrongRootsBlock.method_59149(world, pos.offset(direction), random);
				if (list != null) {
					BlockPos blockPos2 = pos.up();
					list.forEach(itemStack -> dropStack(world, blockPos2, itemStack));
					world.setBlockState(pos, state.with(AGE, Integer.valueOf(i + 1)), Block.NO_REDRAW);
				}
			});
		}
	}
}
