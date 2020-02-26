package net.minecraft.item;

import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AxeItem extends MiningToolItem {
	private static final Set<Block> EFFECTIVE_BLOCKS = Sets.<Block>newHashSet(
		Blocks.OAK_PLANKS,
		Blocks.SPRUCE_PLANKS,
		Blocks.BIRCH_PLANKS,
		Blocks.JUNGLE_PLANKS,
		Blocks.ACACIA_PLANKS,
		Blocks.DARK_OAK_PLANKS,
		Blocks.BOOKSHELF,
		Blocks.OAK_WOOD,
		Blocks.SPRUCE_WOOD,
		Blocks.BIRCH_WOOD,
		Blocks.JUNGLE_WOOD,
		Blocks.ACACIA_WOOD,
		Blocks.DARK_OAK_WOOD,
		Blocks.OAK_LOG,
		Blocks.SPRUCE_LOG,
		Blocks.BIRCH_LOG,
		Blocks.JUNGLE_LOG,
		Blocks.ACACIA_LOG,
		Blocks.DARK_OAK_LOG,
		Blocks.CHEST,
		Blocks.PUMPKIN,
		Blocks.CARVED_PUMPKIN,
		Blocks.JACK_O_LANTERN,
		Blocks.MELON,
		Blocks.LADDER,
		Blocks.SCAFFOLDING,
		Blocks.OAK_BUTTON,
		Blocks.SPRUCE_BUTTON,
		Blocks.BIRCH_BUTTON,
		Blocks.JUNGLE_BUTTON,
		Blocks.DARK_OAK_BUTTON,
		Blocks.ACACIA_BUTTON,
		Blocks.OAK_PRESSURE_PLATE,
		Blocks.SPRUCE_PRESSURE_PLATE,
		Blocks.BIRCH_PRESSURE_PLATE,
		Blocks.JUNGLE_PRESSURE_PLATE,
		Blocks.DARK_OAK_PRESSURE_PLATE,
		Blocks.ACACIA_PRESSURE_PLATE,
		Blocks.CRIMSON_PLANKS,
		Blocks.CRIMSON_STEM,
		Blocks.CRIMSON_BUTTON,
		Blocks.CRIMSON_PRESSURE_PLATE,
		Blocks.CRIMSON_FENCE,
		Blocks.CRIMSON_FENCE_GATE,
		Blocks.CRIMSON_STAIRS,
		Blocks.CRIMSON_DOOR,
		Blocks.CRIMSON_TRAPDOOR,
		Blocks.CRIMSON_SIGN,
		Blocks.CRIMSON_SLAB,
		Blocks.WARPED_PLANKS,
		Blocks.WARPED_STEM,
		Blocks.WARPED_BUTTON,
		Blocks.WARPED_PRESSURE_PLATE,
		Blocks.WARPED_FENCE,
		Blocks.WARPED_FENCE_GATE,
		Blocks.WARPED_STAIRS,
		Blocks.WARPED_DOOR,
		Blocks.WARPED_TRAPDOOR,
		Blocks.WARPED_SIGN,
		Blocks.WARPED_SLAB
	);
	protected static final Map<Block, Block> STRIPPED_BLOCKS = new Builder<Block, Block>()
		.put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD)
		.put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG)
		.put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD)
		.put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG)
		.put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD)
		.put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG)
		.put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD)
		.put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG)
		.put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD)
		.put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG)
		.put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD)
		.put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG)
		.put(Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM)
		.put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM)
		.build();

	protected AxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Settings settings) {
		super(attackDamage, attackSpeed, material, EFFECTIVE_BLOCKS, settings);
	}

	@Override
	public float getMiningSpeed(ItemStack stack, BlockState state) {
		Material material = state.getMaterial();
		return material != Material.WOOD && material != Material.PLANT && material != Material.REPLACEABLE_PLANT && material != Material.BAMBOO
			? super.getMiningSpeed(stack, state)
			: this.miningSpeed;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		Block block = (Block)STRIPPED_BLOCKS.get(blockState.getBlock());
		if (block != null) {
			PlayerEntity playerEntity = context.getPlayer();
			world.playSound(playerEntity, blockPos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
			if (!world.isClient) {
				world.setBlockState(blockPos, block.getDefaultState().with(PillarBlock.AXIS, blockState.get(PillarBlock.AXIS)), 11);
				if (playerEntity != null) {
					context.getStack().damage(1, playerEntity, p -> p.sendToolBreakStatus(context.getHand()));
				}
			}

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}
}
