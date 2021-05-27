package net.minecraft.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class HoneycombItem extends Item {
	public static final Supplier<BiMap<Block, Block>> UNWAXED_TO_WAXED_BLOCKS = Suppliers.memoize(
		() -> ImmutableBiMap.<Block, Block>builder()
				.put(Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK)
				.put(Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER)
				.put(Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER)
				.put(Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER)
				.put(Blocks.CUT_COPPER, Blocks.WAXED_CUT_COPPER)
				.put(Blocks.EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER)
				.put(Blocks.WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER)
				.put(Blocks.OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER)
				.put(Blocks.CUT_COPPER_SLAB, Blocks.WAXED_CUT_COPPER_SLAB)
				.put(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB)
				.put(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB)
				.put(Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB)
				.put(Blocks.CUT_COPPER_STAIRS, Blocks.WAXED_CUT_COPPER_STAIRS)
				.put(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS)
				.put(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS)
				.put(Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS)
				.build()
	);
	public static final Supplier<BiMap<Block, Block>> WAXED_TO_UNWAXED_BLOCKS = Suppliers.memoize(() -> ((BiMap)UNWAXED_TO_WAXED_BLOCKS.get()).inverse());

	public HoneycombItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		return (ActionResult)getWaxedState(blockState).map(state -> {
			PlayerEntity playerEntity = context.getPlayer();
			ItemStack itemStack = context.getStack();
			if (playerEntity instanceof ServerPlayerEntity) {
				Criteria.ITEM_USED_ON_BLOCK.test((ServerPlayerEntity)playerEntity, blockPos, itemStack);
			}

			itemStack.decrement(1);
			world.setBlockState(blockPos, state, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
			world.syncWorldEvent(playerEntity, WorldEvents.BLOCK_WAXED, blockPos, 0);
			return ActionResult.success(world.isClient);
		}).orElse(ActionResult.PASS);
	}

	public static Optional<BlockState> getWaxedState(BlockState state) {
		return Optional.ofNullable((Block)((BiMap)UNWAXED_TO_WAXED_BLOCKS.get()).get(state.getBlock())).map(block -> block.getStateWithProperties(state));
	}
}
