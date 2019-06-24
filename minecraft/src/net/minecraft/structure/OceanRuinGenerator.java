package net.minecraft.structure;

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
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.loot.LootTables;

public class OceanRuinGenerator {
	private static final Identifier[] WARM_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/warm_1"),
		new Identifier("underwater_ruin/warm_2"),
		new Identifier("underwater_ruin/warm_3"),
		new Identifier("underwater_ruin/warm_4"),
		new Identifier("underwater_ruin/warm_5"),
		new Identifier("underwater_ruin/warm_6"),
		new Identifier("underwater_ruin/warm_7"),
		new Identifier("underwater_ruin/warm_8")
	};
	private static final Identifier[] BRICK_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/brick_1"),
		new Identifier("underwater_ruin/brick_2"),
		new Identifier("underwater_ruin/brick_3"),
		new Identifier("underwater_ruin/brick_4"),
		new Identifier("underwater_ruin/brick_5"),
		new Identifier("underwater_ruin/brick_6"),
		new Identifier("underwater_ruin/brick_7"),
		new Identifier("underwater_ruin/brick_8")
	};
	private static final Identifier[] CRACKED_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/cracked_1"),
		new Identifier("underwater_ruin/cracked_2"),
		new Identifier("underwater_ruin/cracked_3"),
		new Identifier("underwater_ruin/cracked_4"),
		new Identifier("underwater_ruin/cracked_5"),
		new Identifier("underwater_ruin/cracked_6"),
		new Identifier("underwater_ruin/cracked_7"),
		new Identifier("underwater_ruin/cracked_8")
	};
	private static final Identifier[] MOSSY_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/mossy_1"),
		new Identifier("underwater_ruin/mossy_2"),
		new Identifier("underwater_ruin/mossy_3"),
		new Identifier("underwater_ruin/mossy_4"),
		new Identifier("underwater_ruin/mossy_5"),
		new Identifier("underwater_ruin/mossy_6"),
		new Identifier("underwater_ruin/mossy_7"),
		new Identifier("underwater_ruin/mossy_8")
	};
	private static final Identifier[] BIG_BRICK_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/big_brick_1"),
		new Identifier("underwater_ruin/big_brick_2"),
		new Identifier("underwater_ruin/big_brick_3"),
		new Identifier("underwater_ruin/big_brick_8")
	};
	private static final Identifier[] BIG_MOSSY_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/big_mossy_1"),
		new Identifier("underwater_ruin/big_mossy_2"),
		new Identifier("underwater_ruin/big_mossy_3"),
		new Identifier("underwater_ruin/big_mossy_8")
	};
	private static final Identifier[] BIG_CRACKED_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/big_cracked_1"),
		new Identifier("underwater_ruin/big_cracked_2"),
		new Identifier("underwater_ruin/big_cracked_3"),
		new Identifier("underwater_ruin/big_cracked_8")
	};
	private static final Identifier[] BIG_WARM_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/big_warm_4"),
		new Identifier("underwater_ruin/big_warm_5"),
		new Identifier("underwater_ruin/big_warm_6"),
		new Identifier("underwater_ruin/big_warm_7")
	};

	private static Identifier getRandomWarmRuin(Random random) {
		return WARM_RUINS[random.nextInt(WARM_RUINS.length)];
	}

	private static Identifier getRandomBigWarmRuin(Random random) {
		return BIG_WARM_RUINS[random.nextInt(BIG_WARM_RUINS.length)];
	}

	public static void addPieces(
		StructureManager structureManager,
		BlockPos blockPos,
		BlockRotation blockRotation,
		List<StructurePiece> list,
		Random random,
		OceanRuinFeatureConfig oceanRuinFeatureConfig
	) {
		boolean bl = random.nextFloat() <= oceanRuinFeatureConfig.largeProbability;
		float f = bl ? 0.9F : 0.8F;
		method_14822(structureManager, blockPos, blockRotation, list, random, oceanRuinFeatureConfig, bl, f);
		if (bl && random.nextFloat() <= oceanRuinFeatureConfig.clusterProbability) {
			method_14825(structureManager, random, blockRotation, blockPos, oceanRuinFeatureConfig, list);
		}
	}

	private static void method_14825(
		StructureManager structureManager,
		Random random,
		BlockRotation blockRotation,
		BlockPos blockPos,
		OceanRuinFeatureConfig oceanRuinFeatureConfig,
		List<StructurePiece> list
	) {
		int i = blockPos.getX();
		int j = blockPos.getZ();
		BlockPos blockPos2 = Structure.method_15168(new BlockPos(15, 0, 15), BlockMirror.NONE, blockRotation, BlockPos.ORIGIN).add(i, 0, j);
		MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.create(i, 0, j, blockPos2.getX(), 0, blockPos2.getZ());
		BlockPos blockPos3 = new BlockPos(Math.min(i, blockPos2.getX()), 0, Math.min(j, blockPos2.getZ()));
		List<BlockPos> list2 = getRoomPositions(random, blockPos3.getX(), blockPos3.getZ());
		int k = MathHelper.nextInt(random, 4, 8);

		for (int l = 0; l < k; l++) {
			if (!list2.isEmpty()) {
				int m = random.nextInt(list2.size());
				BlockPos blockPos4 = (BlockPos)list2.remove(m);
				int n = blockPos4.getX();
				int o = blockPos4.getZ();
				BlockRotation blockRotation2 = BlockRotation.values()[random.nextInt(BlockRotation.values().length)];
				BlockPos blockPos5 = Structure.method_15168(new BlockPos(5, 0, 6), BlockMirror.NONE, blockRotation2, BlockPos.ORIGIN).add(n, 0, o);
				MutableIntBoundingBox mutableIntBoundingBox2 = MutableIntBoundingBox.create(n, 0, o, blockPos5.getX(), 0, blockPos5.getZ());
				if (!mutableIntBoundingBox2.intersects(mutableIntBoundingBox)) {
					method_14822(structureManager, blockPos4, blockRotation2, list, random, oceanRuinFeatureConfig, false, 0.8F);
				}
			}
		}
	}

	private static List<BlockPos> getRoomPositions(Random random, int i, int j) {
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
		BlockRotation blockRotation,
		List<StructurePiece> list,
		Random random,
		OceanRuinFeatureConfig oceanRuinFeatureConfig,
		boolean bl,
		float f
	) {
		if (oceanRuinFeatureConfig.biomeType == OceanRuinFeature.BiomeType.WARM) {
			Identifier identifier = bl ? getRandomBigWarmRuin(random) : getRandomWarmRuin(random);
			list.add(new OceanRuinGenerator.Piece(structureManager, identifier, blockPos, blockRotation, f, oceanRuinFeatureConfig.biomeType, bl));
		} else if (oceanRuinFeatureConfig.biomeType == OceanRuinFeature.BiomeType.COLD) {
			Identifier[] identifiers = bl ? BIG_BRICK_RUINS : BRICK_RUINS;
			Identifier[] identifiers2 = bl ? BIG_CRACKED_RUINS : CRACKED_RUINS;
			Identifier[] identifiers3 = bl ? BIG_MOSSY_RUINS : MOSSY_RUINS;
			int i = random.nextInt(identifiers.length);
			list.add(new OceanRuinGenerator.Piece(structureManager, identifiers[i], blockPos, blockRotation, f, oceanRuinFeatureConfig.biomeType, bl));
			list.add(new OceanRuinGenerator.Piece(structureManager, identifiers2[i], blockPos, blockRotation, 0.7F, oceanRuinFeatureConfig.biomeType, bl));
			list.add(new OceanRuinGenerator.Piece(structureManager, identifiers3[i], blockPos, blockRotation, 0.5F, oceanRuinFeatureConfig.biomeType, bl));
		}
	}

	public static class Piece extends SimpleStructurePiece {
		private final OceanRuinFeature.BiomeType biomeType;
		private final float integrity;
		private final Identifier template;
		private final BlockRotation rotation;
		private final boolean large;

		public Piece(
			StructureManager structureManager,
			Identifier identifier,
			BlockPos blockPos,
			BlockRotation blockRotation,
			float f,
			OceanRuinFeature.BiomeType biomeType,
			boolean bl
		) {
			super(StructurePieceType.ORP, 0);
			this.template = identifier;
			this.pos = blockPos;
			this.rotation = blockRotation;
			this.integrity = f;
			this.biomeType = biomeType;
			this.large = bl;
			this.initialize(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.ORP, compoundTag);
			this.template = new Identifier(compoundTag.getString("Template"));
			this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
			this.integrity = compoundTag.getFloat("Integrity");
			this.biomeType = OceanRuinFeature.BiomeType.valueOf(compoundTag.getString("BiomeType"));
			this.large = compoundTag.getBoolean("IsLarge");
			this.initialize(structureManager);
		}

		private void initialize(StructureManager structureManager) {
			Structure structure = structureManager.getStructureOrBlank(this.template);
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setRotation(this.rotation)
				.setMirrored(BlockMirror.NONE)
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
			this.setStructureData(structure, this.pos, structurePlacementData);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putString("Template", this.template.toString());
			compoundTag.putString("Rot", this.rotation.name());
			compoundTag.putFloat("Integrity", this.integrity);
			compoundTag.putString("BiomeType", this.biomeType.toString());
			compoundTag.putBoolean("IsLarge", this.large);
		}

		@Override
		protected void handleMetadata(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox) {
			if ("chest".equals(string)) {
				iWorld.setBlockState(
					blockPos, Blocks.CHEST.getDefaultState().with(ChestBlock.WATERLOGGED, Boolean.valueOf(iWorld.getFluidState(blockPos).matches(FluidTags.WATER))), 2
				);
				BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
				if (blockEntity instanceof ChestBlockEntity) {
					((ChestBlockEntity)blockEntity)
						.setLootTable(this.large ? LootTables.UNDERWATER_RUIN_BIG_CHEST : LootTables.UNDERWATER_RUIN_SMALL_CHEST, random.nextLong());
				}
			} else if ("drowned".equals(string)) {
				DrownedEntity drownedEntity = EntityType.DROWNED.create(iWorld.getWorld());
				drownedEntity.setPersistent();
				drownedEntity.setPositionAndAngles(blockPos, 0.0F, 0.0F);
				drownedEntity.initialize(iWorld, iWorld.getLocalDifficulty(blockPos), SpawnType.STRUCTURE, null, null);
				iWorld.spawnEntity(drownedEntity);
				if (blockPos.getY() > iWorld.getSeaLevel()) {
					iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 2);
				} else {
					iWorld.setBlockState(blockPos, Blocks.WATER.getDefaultState(), 2);
				}
			}
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			this.placementData
				.clearProcessors()
				.addProcessor(new BlockRotStructureProcessor(this.integrity))
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
			int i = iWorld.getTop(Heightmap.Type.OCEAN_FLOOR_WG, this.pos.getX(), this.pos.getZ());
			this.pos = new BlockPos(this.pos.getX(), i, this.pos.getZ());
			BlockPos blockPos = Structure.method_15168(
					new BlockPos(this.structure.getSize().getX() - 1, 0, this.structure.getSize().getZ() - 1), BlockMirror.NONE, this.rotation, BlockPos.ORIGIN
				)
				.add(this.pos);
			this.pos = new BlockPos(this.pos.getX(), this.method_14829(this.pos, iWorld, blockPos), this.pos.getZ());
			return super.generate(iWorld, random, mutableIntBoundingBox, chunkPos);
		}

		private int method_14829(BlockPos blockPos, BlockView blockView, BlockPos blockPos2) {
			int i = blockPos.getY();
			int j = 512;
			int k = i - 1;
			int l = 0;

			for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
				int m = blockPos3.getX();
				int n = blockPos3.getZ();
				int o = blockPos.getY() - 1;
				BlockPos.Mutable mutable = new BlockPos.Mutable(m, o, n);
				BlockState blockState = blockView.getBlockState(mutable);

				for (FluidState fluidState = blockView.getFluidState(mutable);
					(blockState.isAir() || fluidState.matches(FluidTags.WATER) || blockState.getBlock().matches(BlockTags.ICE)) && o > 1;
					fluidState = blockView.getFluidState(mutable)
				) {
					mutable.set(m, --o, n);
					blockState = blockView.getBlockState(mutable);
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
