package net.minecraft.item;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

public class AxeItem extends MiningToolItem {
	protected static final Map<Block, Block> STRIPPED_BLOCKS = new Builder<Block, Block>()
		.put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD)
		.put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG)
		.put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD)
		.put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG)
		.put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD)
		.put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG)
		.put(Blocks.CHERRY_WOOD, Blocks.STRIPPED_CHERRY_WOOD)
		.put(Blocks.CHERRY_LOG, Blocks.STRIPPED_CHERRY_LOG)
		.put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD)
		.put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG)
		.put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD)
		.put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG)
		.put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD)
		.put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG)
		.put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM)
		.put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE)
		.put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM)
		.put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE)
		.put(Blocks.MANGROVE_WOOD, Blocks.STRIPPED_MANGROVE_WOOD)
		.put(Blocks.MANGROVE_LOG, Blocks.STRIPPED_MANGROVE_LOG)
		.put(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK)
		.build();

	protected AxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings) {
		super(attackDamage, attackSpeed, material, BlockTags.AXE_MINEABLE, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		PlayerEntity playerEntity = context.getPlayer();
		Optional<BlockState> optional = this.tryStrip(world, blockPos, playerEntity, world.getBlockState(blockPos));
		if (optional.isEmpty()) {
			return ActionResult.PASS;
		} else {
			ItemStack itemStack = context.getStack();
			if (playerEntity instanceof ServerPlayerEntity) {
				Criteria.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity)playerEntity, blockPos, itemStack);
			}

			world.setBlockState(blockPos, (BlockState)optional.get(), Block.NOTIFY_ALL_AND_REDRAW);
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(playerEntity, (BlockState)optional.get()));
			if (playerEntity != null) {
				itemStack.damage(1, playerEntity, LivingEntity.getSlotForHand(context.getHand()));
			}

			return ActionResult.success(world.isClient);
		}
	}

	private Optional<BlockState> tryStrip(World world, BlockPos pos, @Nullable PlayerEntity player, BlockState state) {
		Optional<BlockState> optional = this.getStrippedState(state);
		if (optional.isPresent()) {
			world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return optional;
		} else {
			Optional<BlockState> optional2 = Oxidizable.getDecreasedOxidationState(state);
			if (optional2.isPresent()) {
				world.playSound(player, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0F, 1.0F);
				world.syncWorldEvent(player, WorldEvents.BLOCK_SCRAPED, pos, 0);
				return optional2;
			} else {
				Optional<BlockState> optional3 = Optional.ofNullable((Block)((BiMap)HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get()).get(state.getBlock()))
					.map(block -> block.getStateWithProperties(state));
				if (optional3.isPresent()) {
					world.playSound(player, pos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0F, 1.0F);
					world.syncWorldEvent(player, WorldEvents.WAX_REMOVED, pos, 0);
					return optional3;
				} else {
					return Optional.empty();
				}
			}
		}
	}

	private Optional<BlockState> getStrippedState(BlockState state) {
		return Optional.ofNullable((Block)STRIPPED_BLOCKS.get(state.getBlock()))
			.map(block -> block.getDefaultState().with(PillarBlock.AXIS, (Direction.Axis)state.get(PillarBlock.AXIS)));
	}
}
