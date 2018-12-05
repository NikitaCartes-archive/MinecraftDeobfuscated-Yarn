package net.minecraft.client.render.block;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2475;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.util.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockColorMap {
	private final IdList<BlockColorMapper> mappers = new IdList<>(32);

	public static BlockColorMap create() {
		BlockColorMap blockColorMap = new BlockColorMap();
		blockColorMap.register(
			(blockState, extendedBlockView, blockPos, i) -> extendedBlockView != null && blockPos != null
					? BiomeColors.grassColorAt(extendedBlockView, blockState.get(class_2475.field_11484) == BlockHalf.field_12609 ? blockPos.down() : blockPos)
					: -1,
			Blocks.field_10313,
			Blocks.field_10214
		);
		blockColorMap.register(
			(blockState, extendedBlockView, blockPos, i) -> extendedBlockView != null && blockPos != null
					? BiomeColors.grassColorAt(extendedBlockView, blockPos)
					: GrassColorHandler.getColor(0.5, 1.0),
			Blocks.field_10219,
			Blocks.field_10112,
			Blocks.field_10479,
			Blocks.field_10128
		);
		blockColorMap.register((blockState, extendedBlockView, blockPos, i) -> FoliageColorHandler.getSpruceColor(), Blocks.field_9988);
		blockColorMap.register((blockState, extendedBlockView, blockPos, i) -> FoliageColorHandler.getBirchColor(), Blocks.field_10539);
		blockColorMap.register(
			(blockState, extendedBlockView, blockPos, i) -> extendedBlockView != null && blockPos != null
					? BiomeColors.foliageColorAt(extendedBlockView, blockPos)
					: FoliageColorHandler.getDefaultColor(),
			Blocks.field_10503,
			Blocks.field_10335,
			Blocks.field_10098,
			Blocks.field_10035,
			Blocks.field_10597
		);
		blockColorMap.register(
			(blockState, extendedBlockView, blockPos, i) -> extendedBlockView != null && blockPos != null ? BiomeColors.waterColorAt(extendedBlockView, blockPos) : -1,
			Blocks.field_10382,
			Blocks.field_10422,
			Blocks.field_10593
		);
		blockColorMap.register(
			(blockState, extendedBlockView, blockPos, i) -> RedstoneWireBlock.getWireColor((Integer)blockState.get(RedstoneWireBlock.field_11432)), Blocks.field_10091
		);
		blockColorMap.register(
			(blockState, extendedBlockView, blockPos, i) -> extendedBlockView != null && blockPos != null ? BiomeColors.grassColorAt(extendedBlockView, blockPos) : -1,
			Blocks.field_10424
		);
		blockColorMap.register((blockState, extendedBlockView, blockPos, i) -> 14731036, Blocks.field_10150, Blocks.field_10331);
		blockColorMap.register((blockState, extendedBlockView, blockPos, i) -> {
			int j = (Integer)blockState.get(StemBlock.field_11584);
			int k = j * 32;
			int l = 255 - j * 8;
			int m = j * 4;
			return k << 16 | l << 8 | m;
		}, Blocks.field_10168, Blocks.field_9984);
		blockColorMap.register((blockState, extendedBlockView, blockPos, i) -> extendedBlockView != null && blockPos != null ? 2129968 : 7455580, Blocks.field_10588);
		return blockColorMap;
	}

	public int method_1691(BlockState blockState, World world, BlockPos blockPos) {
		BlockColorMapper blockColorMapper = this.mappers.getInt(Registry.BLOCK.getRawId(blockState.getBlock()));
		if (blockColorMapper != null) {
			return blockColorMapper.getColor(blockState, null, null, 0);
		} else {
			MaterialColor materialColor = blockState.getMaterialColor(world, blockPos);
			return materialColor != null ? materialColor.color : -1;
		}
	}

	public int getRenderColor(BlockState blockState, @Nullable ExtendedBlockView extendedBlockView, @Nullable BlockPos blockPos, int i) {
		BlockColorMapper blockColorMapper = this.mappers.getInt(Registry.BLOCK.getRawId(blockState.getBlock()));
		return blockColorMapper == null ? -1 : blockColorMapper.getColor(blockState, extendedBlockView, blockPos, i);
	}

	public void register(BlockColorMapper blockColorMapper, Block... blocks) {
		for (Block block : blocks) {
			this.mappers.set(blockColorMapper, Registry.BLOCK.getRawId(block));
		}
	}
}
