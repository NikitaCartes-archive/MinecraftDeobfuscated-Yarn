package net.minecraft.item;

import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_5510;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.PillarBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AxeItem extends MiningToolItem {
	private static final Set<Material> field_23139 = Sets.<Material>newHashSet(
		Material.WOOD, Material.NETHER_WOOD, Material.PLANT, Material.REPLACEABLE_PLANT, Material.BAMBOO, Material.GOURD
	);
	private static final Set<Block> EFFECTIVE_BLOCKS = Sets.<Block>newHashSet(
		Blocks.LADDER,
		Blocks.SCAFFOLDING,
		Blocks.OAK_BUTTON,
		Blocks.SPRUCE_BUTTON,
		Blocks.BIRCH_BUTTON,
		Blocks.JUNGLE_BUTTON,
		Blocks.DARK_OAK_BUTTON,
		Blocks.ACACIA_BUTTON,
		Blocks.CRIMSON_BUTTON,
		Blocks.WARPED_BUTTON
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
		.put(Blocks.WARPED_HYPHAE, Blocks.STRIPPED_WARPED_HYPHAE)
		.put(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM)
		.put(Blocks.CRIMSON_HYPHAE, Blocks.STRIPPED_CRIMSON_HYPHAE)
		.build();

	protected AxeItem(ToolMaterial toolMaterial, Item.Settings settings) {
		super(toolMaterial, EFFECTIVE_BLOCKS, settings);
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		Material material = state.getMaterial();
		return field_23139.contains(material) ? this.miningSpeed : super.getMiningSpeedMultiplier(stack, state);
	}

	@Override
	protected class_5510 method_31243() {
		return class_5510.field_26792;
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

			return ActionResult.success(world.isClient);
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damage(1, attacker, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		return true;
	}
}
