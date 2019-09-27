package net.minecraft.client.color.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.ReplaceableTallPlantBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.state.property.Property;
import net.minecraft.util.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockColors {
	private final IdList<BlockColorProvider> providers = new IdList<>(32);
	private final Map<Block, Set<Property<?>>> properties = Maps.<Block, Set<Property<?>>>newHashMap();

	public static BlockColors create() {
		BlockColors blockColors = new BlockColors();
		blockColors.registerColorProvider(
			(blockState, blockRenderView, blockPos, i) -> blockRenderView != null && blockPos != null
					? BiomeColors.getGrassColor(blockRenderView, blockState.get(ReplaceableTallPlantBlock.HALF) == DoubleBlockHalf.UPPER ? blockPos.method_10074() : blockPos)
					: -1,
			Blocks.LARGE_FERN,
			Blocks.TALL_GRASS
		);
		blockColors.registerColorProperty(ReplaceableTallPlantBlock.HALF, Blocks.LARGE_FERN, Blocks.TALL_GRASS);
		blockColors.registerColorProvider(
			(blockState, blockRenderView, blockPos, i) -> blockRenderView != null && blockPos != null
					? BiomeColors.getGrassColor(blockRenderView, blockPos)
					: GrassColors.getColor(0.5, 1.0),
			Blocks.GRASS_BLOCK,
			Blocks.FERN,
			Blocks.GRASS,
			Blocks.POTTED_FERN
		);
		blockColors.registerColorProvider((blockState, blockRenderView, blockPos, i) -> FoliageColors.getSpruceColor(), Blocks.SPRUCE_LEAVES);
		blockColors.registerColorProvider((blockState, blockRenderView, blockPos, i) -> FoliageColors.getBirchColor(), Blocks.BIRCH_LEAVES);
		blockColors.registerColorProvider(
			(blockState, blockRenderView, blockPos, i) -> blockRenderView != null && blockPos != null
					? BiomeColors.getFoliageColor(blockRenderView, blockPos)
					: FoliageColors.getDefaultColor(),
			Blocks.OAK_LEAVES,
			Blocks.JUNGLE_LEAVES,
			Blocks.ACACIA_LEAVES,
			Blocks.DARK_OAK_LEAVES,
			Blocks.VINE
		);
		blockColors.registerColorProvider(
			(blockState, blockRenderView, blockPos, i) -> blockRenderView != null && blockPos != null ? BiomeColors.getWaterColor(blockRenderView, blockPos) : -1,
			Blocks.WATER,
			Blocks.BUBBLE_COLUMN,
			Blocks.CAULDRON
		);
		blockColors.registerColorProvider(
			(blockState, blockRenderView, blockPos, i) -> RedstoneWireBlock.getWireColor((Integer)blockState.get(RedstoneWireBlock.POWER)), Blocks.REDSTONE_WIRE
		);
		blockColors.registerColorProperty(RedstoneWireBlock.POWER, Blocks.REDSTONE_WIRE);
		blockColors.registerColorProvider(
			(blockState, blockRenderView, blockPos, i) -> blockRenderView != null && blockPos != null ? BiomeColors.getGrassColor(blockRenderView, blockPos) : -1,
			Blocks.SUGAR_CANE
		);
		blockColors.registerColorProvider((blockState, blockRenderView, blockPos, i) -> 14731036, Blocks.ATTACHED_MELON_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
		blockColors.registerColorProvider((blockState, blockRenderView, blockPos, i) -> {
			int j = (Integer)blockState.get(StemBlock.AGE);
			int k = j * 32;
			int l = 255 - j * 8;
			int m = j * 4;
			return k << 16 | l << 8 | m;
		}, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
		blockColors.registerColorProperty(StemBlock.AGE, Blocks.MELON_STEM, Blocks.PUMPKIN_STEM);
		blockColors.registerColorProvider(
			(blockState, blockRenderView, blockPos, i) -> blockRenderView != null && blockPos != null ? 2129968 : 7455580, Blocks.LILY_PAD
		);
		return blockColors;
	}

	public int getColor(BlockState blockState, World world, BlockPos blockPos) {
		BlockColorProvider blockColorProvider = this.providers.get(Registry.BLOCK.getRawId(blockState.getBlock()));
		if (blockColorProvider != null) {
			return blockColorProvider.getColor(blockState, null, null, 0);
		} else {
			MaterialColor materialColor = blockState.getTopMaterialColor(world, blockPos);
			return materialColor != null ? materialColor.color : -1;
		}
	}

	public int getColorMultiplier(BlockState blockState, @Nullable BlockRenderView blockRenderView, @Nullable BlockPos blockPos, int i) {
		BlockColorProvider blockColorProvider = this.providers.get(Registry.BLOCK.getRawId(blockState.getBlock()));
		return blockColorProvider == null ? -1 : blockColorProvider.getColor(blockState, blockRenderView, blockPos, i);
	}

	public void registerColorProvider(BlockColorProvider blockColorProvider, Block... blocks) {
		for (Block block : blocks) {
			this.providers.set(blockColorProvider, Registry.BLOCK.getRawId(block));
		}
	}

	private void registerColorProperties(Set<Property<?>> set, Block... blocks) {
		for (Block block : blocks) {
			this.properties.put(block, set);
		}
	}

	private void registerColorProperty(Property<?> property, Block... blocks) {
		this.registerColorProperties(ImmutableSet.of(property), blocks);
	}

	public Set<Property<?>> getProperties(Block block) {
		return (Set<Property<?>>)this.properties.getOrDefault(block, ImmutableSet.of());
	}
}
