package net.minecraft.item;

import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_5508;
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
		Blocks.field_9983,
		Blocks.field_16492,
		Blocks.field_10057,
		Blocks.field_10066,
		Blocks.field_10417,
		Blocks.field_10553,
		Blocks.field_10493,
		Blocks.field_10278,
		Blocks.field_22100,
		Blocks.field_22101
	);
	protected static final Map<Block, Block> STRIPPED_BLOCKS = new Builder<Block, Block>()
		.put(Blocks.field_10126, Blocks.field_10250)
		.put(Blocks.field_10431, Blocks.field_10519)
		.put(Blocks.field_10178, Blocks.field_10374)
		.put(Blocks.field_10010, Blocks.field_10244)
		.put(Blocks.field_9999, Blocks.field_10103)
		.put(Blocks.field_10533, Blocks.field_10622)
		.put(Blocks.field_10307, Blocks.field_10204)
		.put(Blocks.field_10511, Blocks.field_10366)
		.put(Blocks.field_10303, Blocks.field_10084)
		.put(Blocks.field_10306, Blocks.field_10254)
		.put(Blocks.field_10155, Blocks.field_10558)
		.put(Blocks.field_10037, Blocks.field_10436)
		.put(Blocks.field_22111, Blocks.field_22112)
		.put(Blocks.field_22503, Blocks.field_22504)
		.put(Blocks.field_22118, Blocks.field_22119)
		.put(Blocks.field_22505, Blocks.field_22506)
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
	protected class_5508 method_31212() {
		return class_5508.field_26764;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		Block block = (Block)STRIPPED_BLOCKS.get(blockState.getBlock());
		if (block != null) {
			PlayerEntity playerEntity = context.getPlayer();
			world.playSound(playerEntity, blockPos, SoundEvents.field_14675, SoundCategory.field_15245, 1.0F, 1.0F);
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
		stack.damage(1, attacker, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.field_6173));
		return true;
	}
}
