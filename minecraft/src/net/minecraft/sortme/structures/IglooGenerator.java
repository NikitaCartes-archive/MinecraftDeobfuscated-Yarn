package net.minecraft.sortme.structures;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.class_3443;
import net.minecraft.class_3470;
import net.minecraft.class_3485;
import net.minecraft.class_3492;
import net.minecraft.class_3499;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sortme.StructurePiece;
import net.minecraft.sortme.structures.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.config.feature.DefaultFeatureConfig;
import net.minecraft.world.loot.LootTables;

public class IglooGenerator {
	private static final Identifier field_14409 = new Identifier("igloo/top");
	private static final Identifier field_14407 = new Identifier("igloo/middle");
	private static final Identifier field_14410 = new Identifier("igloo/bottom");
	private static final Map<Identifier, BlockPos> field_14408 = ImmutableMap.of(
		field_14409, new BlockPos(3, 5, 5), field_14407, new BlockPos(1, 3, 1), field_14410, new BlockPos(3, 6, 7)
	);
	private static final Map<Identifier, BlockPos> field_14406 = ImmutableMap.of(
		field_14409, new BlockPos(0, 0, 0), field_14407, new BlockPos(2, -3, 4), field_14410, new BlockPos(0, -3, -2)
	);

	public static void method_14705(
		class_3485 arg, BlockPos blockPos, Rotation rotation, List<class_3443> list, Random random, DefaultFeatureConfig defaultFeatureConfig
	) {
		if (random.nextDouble() < 0.5) {
			int i = random.nextInt(8) + 4;
			list.add(new IglooGenerator.class_3352(arg, field_14410, blockPos, rotation, i * 3));

			for (int j = 0; j < i - 1; j++) {
				list.add(new IglooGenerator.class_3352(arg, field_14407, blockPos, rotation, j * 3));
			}
		}

		list.add(new IglooGenerator.class_3352(arg, field_14409, blockPos, rotation, 0));
	}

	public static class class_3352 extends class_3470 {
		private final Identifier field_14411;
		private final Rotation field_14412;

		public class_3352(class_3485 arg, Identifier identifier, BlockPos blockPos, Rotation rotation, int i) {
			super(StructurePiece.field_16909, 0);
			this.field_14411 = identifier;
			BlockPos blockPos2 = (BlockPos)IglooGenerator.field_14406.get(identifier);
			this.field_15432 = blockPos.add(blockPos2.getX(), blockPos2.getY() - i, blockPos2.getZ());
			this.field_14412 = rotation;
			this.method_14708(arg);
		}

		public class_3352(class_3485 arg, CompoundTag compoundTag) {
			super(StructurePiece.field_16909, compoundTag);
			this.field_14411 = new Identifier(compoundTag.getString("Template"));
			this.field_14412 = Rotation.valueOf(compoundTag.getString("Rot"));
			this.method_14708(arg);
		}

		private void method_14708(class_3485 arg) {
			class_3499 lv = arg.method_15091(this.field_14411);
			class_3492 lv2 = new class_3492()
				.method_15123(this.field_14412)
				.method_15125(Mirror.NONE)
				.method_15119((BlockPos)IglooGenerator.field_14408.get(this.field_14411))
				.method_16184(BlockIgnoreStructureProcessor.field_16718);
			this.method_15027(lv, this.field_15432, lv2);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putString("Template", this.field_14411.toString());
			compoundTag.putString("Rot", this.field_14412.name());
		}

		@Override
		protected void method_15026(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox) {
			if ("chest".equals(string)) {
				iWorld.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 3);
				BlockEntity blockEntity = iWorld.getBlockEntity(blockPos.down());
				if (blockEntity instanceof ChestBlockEntity) {
					((ChestBlockEntity)blockEntity).setLootTable(LootTables.CHEST_IGLOO, random.nextLong());
				}
			}
		}

		@Override
		public boolean method_14931(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			class_3492 lv = new class_3492()
				.method_15123(this.field_14412)
				.method_15125(Mirror.NONE)
				.method_15119((BlockPos)IglooGenerator.field_14408.get(this.field_14411))
				.method_16184(BlockIgnoreStructureProcessor.field_16718);
			BlockPos blockPos = (BlockPos)IglooGenerator.field_14406.get(this.field_14411);
			BlockPos blockPos2 = this.field_15432.add(class_3499.method_15171(lv, new BlockPos(3 - blockPos.getX(), 0, 0 - blockPos.getZ())));
			int i = iWorld.getTop(Heightmap.Type.WORLD_SURFACE_WG, blockPos2.getX(), blockPos2.getZ());
			BlockPos blockPos3 = this.field_15432;
			this.field_15432 = this.field_15432.add(0, i - 90 - 1, 0);
			boolean bl = super.method_14931(iWorld, random, mutableIntBoundingBox, chunkPos);
			if (this.field_14411.equals(IglooGenerator.field_14409)) {
				BlockPos blockPos4 = this.field_15432.add(class_3499.method_15171(lv, new BlockPos(3, 0, 5)));
				BlockState blockState = iWorld.getBlockState(blockPos4.down());
				if (!blockState.isAir() && blockState.getBlock() != Blocks.field_9983) {
					iWorld.setBlockState(blockPos4, Blocks.field_10491.getDefaultState(), 3);
				}
			}

			this.field_15432 = blockPos3;
			return bl;
		}
	}
}
