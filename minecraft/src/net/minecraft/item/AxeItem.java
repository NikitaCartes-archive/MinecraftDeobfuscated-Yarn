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
		Blocks.field_10161,
		Blocks.field_9975,
		Blocks.field_10148,
		Blocks.field_10334,
		Blocks.field_10218,
		Blocks.field_10075,
		Blocks.field_10504,
		Blocks.field_10126,
		Blocks.field_10155,
		Blocks.field_10307,
		Blocks.field_10303,
		Blocks.field_9999,
		Blocks.field_10178,
		Blocks.field_10431,
		Blocks.field_10037,
		Blocks.field_10511,
		Blocks.field_10306,
		Blocks.field_10533,
		Blocks.field_10010,
		Blocks.field_10034,
		Blocks.field_10261,
		Blocks.field_10147,
		Blocks.field_10009,
		Blocks.field_10545,
		Blocks.field_9983,
		Blocks.field_16492,
		Blocks.field_10057,
		Blocks.field_10066,
		Blocks.field_10417,
		Blocks.field_10553,
		Blocks.field_10493,
		Blocks.field_10278,
		Blocks.field_10484,
		Blocks.field_10332,
		Blocks.field_10592,
		Blocks.field_10026,
		Blocks.field_10470,
		Blocks.field_10397
	);
	protected static final Map<Block, Block> BLOCK_TRANSFORMATIONS_MAP = new Builder<Block, Block>()
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
		.build();

	protected AxeItem(ToolMaterial toolMaterial, float f, float g, Item.Settings settings) {
		super(f, g, toolMaterial, EFFECTIVE_BLOCKS, settings);
	}

	@Override
	public float getBlockBreakingSpeed(ItemStack itemStack, BlockState blockState) {
		Material material = blockState.getMaterial();
		return material != Material.WOOD && material != Material.PLANT && material != Material.REPLACEABLE_PLANT && material != Material.BAMBOO
			? super.getBlockBreakingSpeed(itemStack, blockState)
			: this.blockBreakingSpeed;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		Block block = (Block)BLOCK_TRANSFORMATIONS_MAP.get(blockState.getBlock());
		if (block != null) {
			PlayerEntity playerEntity = itemUsageContext.getPlayer();
			world.playSound(playerEntity, blockPos, SoundEvents.field_14675, SoundCategory.field_15245, 1.0F, 1.0F);
			if (!world.isClient) {
				world.setBlockState(blockPos, block.getDefaultState().with(PillarBlock.AXIS, blockState.get(PillarBlock.AXIS)), 11);
				if (playerEntity != null) {
					itemUsageContext.getItemStack().applyDamage(1, playerEntity);
				}
			}

			return ActionResult.field_5812;
		} else {
			return ActionResult.PASS;
		}
	}
}
