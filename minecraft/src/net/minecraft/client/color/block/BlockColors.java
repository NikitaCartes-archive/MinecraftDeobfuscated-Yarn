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
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockColors {
	private final IdList<BlockColorProvider> providers = new IdList<>(32);
	private final Map<Block, Set<Property<?>>> field_20271 = Maps.<Block, Set<Property<?>>>newHashMap();

	public static BlockColors create() {
		BlockColors blockColors = new BlockColors();
		blockColors.register(
			(blockState, extendedBlockView, blockPos, i) -> extendedBlockView != null && blockPos != null
					? BiomeColors.getGrassColor(extendedBlockView, blockState.get(ReplaceableTallPlantBlock.HALF) == DoubleBlockHalf.field_12609 ? blockPos.down() : blockPos)
					: -1,
			Blocks.field_10313,
			Blocks.field_10214
		);
		blockColors.method_21593(ReplaceableTallPlantBlock.HALF, Blocks.field_10313, Blocks.field_10214);
		blockColors.register(
			(blockState, extendedBlockView, blockPos, i) -> extendedBlockView != null && blockPos != null
					? BiomeColors.getGrassColor(extendedBlockView, blockPos)
					: GrassColors.getColor(0.5, 1.0),
			Blocks.field_10219,
			Blocks.field_10112,
			Blocks.field_10479,
			Blocks.field_10128
		);
		blockColors.register((blockState, extendedBlockView, blockPos, i) -> FoliageColors.getSpruceColor(), Blocks.field_9988);
		blockColors.register((blockState, extendedBlockView, blockPos, i) -> FoliageColors.getBirchColor(), Blocks.field_10539);
		blockColors.register(
			(blockState, extendedBlockView, blockPos, i) -> extendedBlockView != null && blockPos != null
					? BiomeColors.getFoliageColor(extendedBlockView, blockPos)
					: FoliageColors.getDefaultColor(),
			Blocks.field_10503,
			Blocks.field_10335,
			Blocks.field_10098,
			Blocks.field_10035,
			Blocks.field_10597
		);
		blockColors.register(
			(blockState, extendedBlockView, blockPos, i) -> extendedBlockView != null && blockPos != null ? BiomeColors.getWaterColor(extendedBlockView, blockPos) : -1,
			Blocks.field_10382,
			Blocks.field_10422,
			Blocks.field_10593
		);
		blockColors.register(
			(blockState, extendedBlockView, blockPos, i) -> RedstoneWireBlock.getWireColor((Integer)blockState.get(RedstoneWireBlock.POWER)), Blocks.field_10091
		);
		blockColors.method_21593(RedstoneWireBlock.POWER, Blocks.field_10091);
		blockColors.register(
			(blockState, extendedBlockView, blockPos, i) -> extendedBlockView != null && blockPos != null ? BiomeColors.getGrassColor(extendedBlockView, blockPos) : -1,
			Blocks.field_10424
		);
		blockColors.register((blockState, extendedBlockView, blockPos, i) -> 14731036, Blocks.field_10150, Blocks.field_10331);
		blockColors.register((blockState, extendedBlockView, blockPos, i) -> {
			int j = (Integer)blockState.get(StemBlock.AGE);
			int k = j * 32;
			int l = 255 - j * 8;
			int m = j * 4;
			return k << 16 | l << 8 | m;
		}, Blocks.field_10168, Blocks.field_9984);
		blockColors.method_21593(StemBlock.AGE, Blocks.field_10168, Blocks.field_9984);
		blockColors.register((blockState, extendedBlockView, blockPos, i) -> extendedBlockView != null && blockPos != null ? 2129968 : 7455580, Blocks.field_10588);
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

	public int getColorMultiplier(BlockState blockState, @Nullable ExtendedBlockView extendedBlockView, @Nullable BlockPos blockPos, int i) {
		BlockColorProvider blockColorProvider = this.providers.get(Registry.BLOCK.getRawId(blockState.getBlock()));
		return blockColorProvider == null ? -1 : blockColorProvider.getColor(blockState, extendedBlockView, blockPos, i);
	}

	public void register(BlockColorProvider blockColorProvider, Block... blocks) {
		for (Block block : blocks) {
			this.providers.set(blockColorProvider, Registry.BLOCK.getRawId(block));
		}
	}

	private void method_21594(Set<Property<?>> set, Block... blocks) {
		for (Block block : blocks) {
			this.field_20271.put(block, set);
		}
	}

	private void method_21593(Property<?> property, Block... blocks) {
		this.method_21594(ImmutableSet.of(property), blocks);
	}

	public Set<Property<?>> method_21592(Block block) {
		return (Set<Property<?>>)this.field_20271.getOrDefault(block, ImmutableSet.of());
	}
}
