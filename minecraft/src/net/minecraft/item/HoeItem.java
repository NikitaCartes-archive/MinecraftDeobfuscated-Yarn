package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class HoeItem extends MiningToolItem {
	/**
	 * A map of input blocks to predicate-consumer action pairs.
	 * 
	 * <p>Tilling works so that if the predicate succeeds, the consumer (the real action)
	 * is executed, and the hoe is damaged.
	 */
	protected static final Map<Block, Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>> TILLING_ACTIONS = Maps.<Block, Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>>newHashMap(
		ImmutableMap.of(
			Blocks.GRASS_BLOCK,
			Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())),
			Blocks.DIRT_PATH,
			Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())),
			Blocks.DIRT,
			Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.FARMLAND.getDefaultState())),
			Blocks.COARSE_DIRT,
			Pair.of(HoeItem::canTillFarmland, createTillAction(Blocks.DIRT.getDefaultState())),
			Blocks.ROOTED_DIRT,
			Pair.of(itemUsageContext -> true, createTillAndDropAction(Blocks.DIRT.getDefaultState(), Items.HANGING_ROOTS))
		)
	);

	public HoeItem(ToolMaterial toolMaterial, Item.Settings settings) {
		super(toolMaterial, BlockTags.HOE_MINEABLE, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>> pair = (Pair<Predicate<ItemUsageContext>, Consumer<ItemUsageContext>>)TILLING_ACTIONS.get(
			world.getBlockState(blockPos).getBlock()
		);
		if (pair == null) {
			return ActionResult.PASS;
		} else {
			Predicate<ItemUsageContext> predicate = pair.getFirst();
			Consumer<ItemUsageContext> consumer = pair.getSecond();
			if (predicate.test(context)) {
				PlayerEntity playerEntity = context.getPlayer();
				world.playSound(playerEntity, blockPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				if (!world.isClient) {
					consumer.accept(context);
					if (playerEntity != null) {
						context.getStack().damage(1, playerEntity, LivingEntity.getSlotForHand(context.getHand()));
					}
				}

				return ActionResult.success(world.isClient);
			} else {
				return ActionResult.PASS;
			}
		}
	}

	/**
	 * {@return a tilling action that sets a block state}
	 * 
	 * @param result the tilled block state
	 */
	public static Consumer<ItemUsageContext> createTillAction(BlockState result) {
		return context -> {
			context.getWorld().setBlockState(context.getBlockPos(), result, Block.NOTIFY_ALL_AND_REDRAW);
			context.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, context.getBlockPos(), GameEvent.Emitter.of(context.getPlayer(), result));
		};
	}

	/**
	 * {@return a tilling action that sets a block state and drops an item}
	 * 
	 * @param droppedItem the item to drop
	 * @param result the tilled block state
	 */
	public static Consumer<ItemUsageContext> createTillAndDropAction(BlockState result, ItemConvertible droppedItem) {
		return context -> {
			context.getWorld().setBlockState(context.getBlockPos(), result, Block.NOTIFY_ALL_AND_REDRAW);
			context.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, context.getBlockPos(), GameEvent.Emitter.of(context.getPlayer(), result));
			Block.dropStack(context.getWorld(), context.getBlockPos(), context.getSide(), new ItemStack(droppedItem));
		};
	}

	/**
	 * {@return whether the used block can be tilled into farmland}
	 * This method is used as the tilling predicate for most vanilla blocks except rooted dirt.
	 */
	public static boolean canTillFarmland(ItemUsageContext context) {
		return context.getSide() != Direction.DOWN && context.getWorld().getBlockState(context.getBlockPos().up()).isAir();
	}
}
