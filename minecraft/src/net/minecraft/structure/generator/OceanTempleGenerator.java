package net.minecraft.structure.generator;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.loot.LootTables;

public class OceanTempleGenerator {
	private static final Identifier[] field_14521 = new Identifier[]{
		new Identifier("underwater_ruin/warm_1"),
		new Identifier("underwater_ruin/warm_2"),
		new Identifier("underwater_ruin/warm_3"),
		new Identifier("underwater_ruin/warm_4"),
		new Identifier("underwater_ruin/warm_5"),
		new Identifier("underwater_ruin/warm_6"),
		new Identifier("underwater_ruin/warm_7"),
		new Identifier("underwater_ruin/warm_8")
	};
	private static final Identifier[] field_14518 = new Identifier[]{
		new Identifier("underwater_ruin/brick_1"),
		new Identifier("underwater_ruin/brick_2"),
		new Identifier("underwater_ruin/brick_3"),
		new Identifier("underwater_ruin/brick_4"),
		new Identifier("underwater_ruin/brick_5"),
		new Identifier("underwater_ruin/brick_6"),
		new Identifier("underwater_ruin/brick_7"),
		new Identifier("underwater_ruin/brick_8")
	};
	private static final Identifier[] field_14519 = new Identifier[]{
		new Identifier("underwater_ruin/cracked_1"),
		new Identifier("underwater_ruin/cracked_2"),
		new Identifier("underwater_ruin/cracked_3"),
		new Identifier("underwater_ruin/cracked_4"),
		new Identifier("underwater_ruin/cracked_5"),
		new Identifier("underwater_ruin/cracked_6"),
		new Identifier("underwater_ruin/cracked_7"),
		new Identifier("underwater_ruin/cracked_8")
	};
	private static final Identifier[] field_14522 = new Identifier[]{
		new Identifier("underwater_ruin/mossy_1"),
		new Identifier("underwater_ruin/mossy_2"),
		new Identifier("underwater_ruin/mossy_3"),
		new Identifier("underwater_ruin/mossy_4"),
		new Identifier("underwater_ruin/mossy_5"),
		new Identifier("underwater_ruin/mossy_6"),
		new Identifier("underwater_ruin/mossy_7"),
		new Identifier("underwater_ruin/mossy_8")
	};
	private static final Identifier[] field_14516 = new Identifier[]{
		new Identifier("underwater_ruin/big_brick_1"),
		new Identifier("underwater_ruin/big_brick_2"),
		new Identifier("underwater_ruin/big_brick_3"),
		new Identifier("underwater_ruin/big_brick_8")
	};
	private static final Identifier[] field_14517 = new Identifier[]{
		new Identifier("underwater_ruin/big_mossy_1"),
		new Identifier("underwater_ruin/big_mossy_2"),
		new Identifier("underwater_ruin/big_mossy_3"),
		new Identifier("underwater_ruin/big_mossy_8")
	};
	private static final Identifier[] field_14520 = new Identifier[]{
		new Identifier("underwater_ruin/big_cracked_1"),
		new Identifier("underwater_ruin/big_cracked_2"),
		new Identifier("underwater_ruin/big_cracked_3"),
		new Identifier("underwater_ruin/big_cracked_8")
	};
	private static final Identifier[] field_14515 = new Identifier[]{
		new Identifier("underwater_ruin/big_warm_4"),
		new Identifier("underwater_ruin/big_warm_5"),
		new Identifier("underwater_ruin/big_warm_6"),
		new Identifier("underwater_ruin/big_warm_7")
	};

	private static Identifier method_14824(Random random) {
		return field_14521[random.nextInt(field_14521.length)];
	}

	private static Identifier method_14826(Random random) {
		return field_14515[random.nextInt(field_14515.length)];
	}

	public static void method_14827(
		StructureManager structureManager,
		BlockPos blockPos,
		Rotation rotation,
		List<StructurePiece> list,
		Random random,
		OceanRuinFeatureConfig oceanRuinFeatureConfig
	) {
		boolean bl = random.nextFloat() <= oceanRuinFeatureConfig.largeProbability;
		float f = bl ? 0.9F : 0.8F;
		method_14822(structureManager, blockPos, rotation, list, random, oceanRuinFeatureConfig, bl, f);
		if (bl && random.nextFloat() <= oceanRuinFeatureConfig.clusterProbability) {
			method_14825(structureManager, random, rotation, blockPos, oceanRuinFeatureConfig, list);
		}
	}

	private static void method_14825(
		StructureManager structureManager,
		Random random,
		Rotation rotation,
		BlockPos blockPos,
		OceanRuinFeatureConfig oceanRuinFeatureConfig,
		List<StructurePiece> list
	) {
		int i = blockPos.getX();
		int j = blockPos.getZ();
		BlockPos blockPos2 = Structure.method_15168(new BlockPos(15, 0, 15), Mirror.NONE, rotation, BlockPos.ORIGIN).add(i, 0, j);
		MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.create(i, 0, j, blockPos2.getX(), 0, blockPos2.getZ());
		BlockPos blockPos3 = new BlockPos(Math.min(i, blockPos2.getX()), 0, Math.min(j, blockPos2.getZ()));
		List<BlockPos> list2 = method_14821(random, blockPos3.getX(), blockPos3.getZ());
		int k = MathHelper.nextInt(random, 4, 8);

		for (int l = 0; l < k; l++) {
			if (!list2.isEmpty()) {
				int m = random.nextInt(list2.size());
				BlockPos blockPos4 = (BlockPos)list2.remove(m);
				int n = blockPos4.getX();
				int o = blockPos4.getZ();
				Rotation rotation2 = Rotation.values()[random.nextInt(Rotation.values().length)];
				BlockPos blockPos5 = Structure.method_15168(new BlockPos(5, 0, 6), Mirror.NONE, rotation2, BlockPos.ORIGIN).add(n, 0, o);
				MutableIntBoundingBox mutableIntBoundingBox2 = MutableIntBoundingBox.create(n, 0, o, blockPos5.getX(), 0, blockPos5.getZ());
				if (!mutableIntBoundingBox2.intersects(mutableIntBoundingBox)) {
					method_14822(structureManager, blockPos4, rotation2, list, random, oceanRuinFeatureConfig, false, 0.8F);
				}
			}
		}
	}

	private static List<BlockPos> method_14821(Random random, int i, int j) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();
		list.add(new BlockPos(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j + 16 + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j - 16 + MathHelper.nextInt(random, 4, 8)));
		list.add(new BlockPos(i + MathHelper.nextInt(random, 1, 7), 90, j + 16 + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(i + MathHelper.nextInt(random, 1, 7), 90, j - 16 + MathHelper.nextInt(random, 4, 6)));
		list.add(new BlockPos(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j + 16 + MathHelper.nextInt(random, 3, 8)));
		list.add(new BlockPos(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j - 16 + MathHelper.nextInt(random, 4, 8)));
		return list;
	}

	private static void method_14822(
		StructureManager structureManager,
		BlockPos blockPos,
		Rotation rotation,
		List<StructurePiece> list,
		Random random,
		OceanRuinFeatureConfig oceanRuinFeatureConfig,
		boolean bl,
		float f
	) {
		if (oceanRuinFeatureConfig.field_13709 == OceanRuinFeature.BiomeType.WARM) {
			Identifier identifier = bl ? method_14826(random) : method_14824(random);
			list.add(new OceanTempleGenerator.Piece(structureManager, identifier, blockPos, rotation, f, oceanRuinFeatureConfig.field_13709, bl));
		} else if (oceanRuinFeatureConfig.field_13709 == OceanRuinFeature.BiomeType.COLD) {
			Identifier[] identifiers = bl ? field_14516 : field_14518;
			Identifier[] identifiers2 = bl ? field_14520 : field_14519;
			Identifier[] identifiers3 = bl ? field_14517 : field_14522;
			int i = random.nextInt(identifiers.length);
			list.add(new OceanTempleGenerator.Piece(structureManager, identifiers[i], blockPos, rotation, f, oceanRuinFeatureConfig.field_13709, bl));
			list.add(new OceanTempleGenerator.Piece(structureManager, identifiers2[i], blockPos, rotation, 0.7F, oceanRuinFeatureConfig.field_13709, bl));
			list.add(new OceanTempleGenerator.Piece(structureManager, identifiers3[i], blockPos, rotation, 0.5F, oceanRuinFeatureConfig.field_13709, bl));
		}
	}

	public static class Piece extends SimpleStructurePiece {
		private final OceanRuinFeature.BiomeType biomeType;
		private final float integrity;
		private final Identifier field_14523;
		private final Rotation rotation;
		private final boolean large;

		public Piece(
			StructureManager structureManager, Identifier identifier, BlockPos blockPos, Rotation rotation, float f, OceanRuinFeature.BiomeType biomeType, boolean bl
		) {
			super(StructurePieceType.OCEAN_TEMPLE, 0);
			this.field_14523 = identifier;
			this.field_15432 = blockPos;
			this.rotation = rotation;
			this.integrity = f;
			this.biomeType = biomeType;
			this.large = bl;
			this.method_14828(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.OCEAN_TEMPLE, compoundTag);
			this.field_14523 = new Identifier(compoundTag.getString("Template"));
			this.rotation = Rotation.valueOf(compoundTag.getString("Rot"));
			this.integrity = compoundTag.getFloat("Integrity");
			this.biomeType = OceanRuinFeature.BiomeType.valueOf(compoundTag.getString("BiomeType"));
			this.large = compoundTag.getBoolean("IsLarge");
			this.method_14828(structureManager);
		}

		private void method_14828(StructureManager structureManager) {
			Structure structure = structureManager.method_15091(this.field_14523);
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setRotation(this.rotation)
				.setMirrored(Mirror.NONE)
				.method_16184(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
			this.method_15027(structure, this.field_15432, structurePlacementData);
		}

		@Override
		protected void method_14943(CompoundTag compoundTag) {
			super.method_14943(compoundTag);
			compoundTag.putString("Template", this.field_14523.toString());
			compoundTag.putString("Rot", this.rotation.name());
			compoundTag.putFloat("Integrity", this.integrity);
			compoundTag.putString("BiomeType", this.biomeType.toString());
			compoundTag.putBoolean("IsLarge", this.large);
		}

		@Override
		protected void method_15026(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox) {
			if ("chest".equals(string)) {
				iWorld.method_8652(
					blockPos,
					Blocks.field_10034.method_9564().method_11657(ChestBlock.field_10772, Boolean.valueOf(iWorld.method_8316(blockPos).method_15767(FluidTags.field_15517))),
					2
				);
				BlockEntity blockEntity = iWorld.method_8321(blockPos);
				if (blockEntity instanceof ChestBlockEntity) {
					((ChestBlockEntity)blockEntity).method_11285(this.large ? LootTables.field_300 : LootTables.field_397, random.nextLong());
				}
			} else if ("drowned".equals(string)) {
				DrownedEntity drownedEntity = EntityType.DROWNED.method_5883(iWorld.getWorld());
				drownedEntity.setPersistent();
				drownedEntity.method_5725(blockPos, 0.0F, 0.0F);
				drownedEntity.method_5943(iWorld, iWorld.method_8404(blockPos), SpawnType.field_16474, null, null);
				iWorld.spawnEntity(drownedEntity);
				if (blockPos.getY() > iWorld.getSeaLevel()) {
					iWorld.method_8652(blockPos, Blocks.field_10124.method_9564(), 2);
				} else {
					iWorld.method_8652(blockPos, Blocks.field_10382.method_9564(), 2);
				}
			}
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.field_15434
				.clearProcessors()
				.method_16184(new BlockRotStructureProcessor(this.integrity))
				.method_16184(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
			int i = iWorld.method_8589(Heightmap.Type.OCEAN_FLOOR_WG, this.field_15432.getX(), this.field_15432.getZ());
			this.field_15432 = new BlockPos(this.field_15432.getX(), i, this.field_15432.getZ());
			BlockPos blockPos = Structure.method_15168(
					new BlockPos(this.field_15433.method_15160().getX() - 1, 0, this.field_15433.method_15160().getZ() - 1), Mirror.NONE, this.rotation, BlockPos.ORIGIN
				)
				.method_10081(this.field_15432);
			this.field_15432 = new BlockPos(this.field_15432.getX(), this.method_14829(this.field_15432, iWorld, blockPos), this.field_15432.getZ());
			return super.generate(iWorld, random, mutableIntBoundingBox, chunkPos);
		}

		private int method_14829(BlockPos blockPos, BlockView blockView, BlockPos blockPos2) {
			int i = blockPos.getY();
			int j = 512;
			int k = i - 1;
			int l = 0;

			for (BlockPos blockPos3 : BlockPos.iterateBoxPositions(blockPos, blockPos2)) {
				int m = blockPos3.getX();
				int n = blockPos3.getZ();
				int o = blockPos.getY() - 1;
				BlockPos.Mutable mutable = new BlockPos.Mutable(m, o, n);
				BlockState blockState = blockView.method_8320(mutable);

				for (FluidState fluidState = blockView.method_8316(mutable);
					(blockState.isAir() || fluidState.method_15767(FluidTags.field_15517) || blockState.getBlock().method_9525(BlockTags.field_15467)) && o > 1;
					fluidState = blockView.method_8316(mutable)
				) {
					mutable.set(m, --o, n);
					blockState = blockView.method_8320(mutable);
				}

				j = Math.min(j, o);
				if (o < k - 2) {
					l++;
				}
			}

			int p = Math.abs(blockPos.getX() - blockPos2.getX());
			if (k - j > 2 && l > p - 2) {
				i = j + 1;
			}

			return i;
		}
	}
}
