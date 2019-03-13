package net.minecraft.structure.generator;

import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.loot.LootTables;

public class ShipwreckGenerator {
	private static final BlockPos field_14536 = new BlockPos(4, 0, 15);
	private static final Identifier[] field_14534 = new Identifier[]{
		new Identifier("shipwreck/with_mast"),
		new Identifier("shipwreck/sideways_full"),
		new Identifier("shipwreck/sideways_fronthalf"),
		new Identifier("shipwreck/sideways_backhalf"),
		new Identifier("shipwreck/rightsideup_full"),
		new Identifier("shipwreck/rightsideup_fronthalf"),
		new Identifier("shipwreck/rightsideup_backhalf"),
		new Identifier("shipwreck/with_mast_degraded"),
		new Identifier("shipwreck/rightsideup_full_degraded"),
		new Identifier("shipwreck/rightsideup_fronthalf_degraded"),
		new Identifier("shipwreck/rightsideup_backhalf_degraded")
	};
	private static final Identifier[] field_14535 = new Identifier[]{
		new Identifier("shipwreck/with_mast"),
		new Identifier("shipwreck/upsidedown_full"),
		new Identifier("shipwreck/upsidedown_fronthalf"),
		new Identifier("shipwreck/upsidedown_backhalf"),
		new Identifier("shipwreck/sideways_full"),
		new Identifier("shipwreck/sideways_fronthalf"),
		new Identifier("shipwreck/sideways_backhalf"),
		new Identifier("shipwreck/rightsideup_full"),
		new Identifier("shipwreck/rightsideup_fronthalf"),
		new Identifier("shipwreck/rightsideup_backhalf"),
		new Identifier("shipwreck/with_mast_degraded"),
		new Identifier("shipwreck/upsidedown_full_degraded"),
		new Identifier("shipwreck/upsidedown_fronthalf_degraded"),
		new Identifier("shipwreck/upsidedown_backhalf_degraded"),
		new Identifier("shipwreck/sideways_full_degraded"),
		new Identifier("shipwreck/sideways_fronthalf_degraded"),
		new Identifier("shipwreck/sideways_backhalf_degraded"),
		new Identifier("shipwreck/rightsideup_full_degraded"),
		new Identifier("shipwreck/rightsideup_fronthalf_degraded"),
		new Identifier("shipwreck/rightsideup_backhalf_degraded")
	};

	public static void method_14834(
		StructureManager structureManager,
		BlockPos blockPos,
		Rotation rotation,
		List<StructurePiece> list,
		Random random,
		ShipwreckFeatureConfig shipwreckFeatureConfig
	) {
		Identifier identifier = shipwreckFeatureConfig.isBeached ? field_14534[random.nextInt(field_14534.length)] : field_14535[random.nextInt(field_14535.length)];
		list.add(new ShipwreckGenerator.Piece(structureManager, identifier, blockPos, rotation, shipwreckFeatureConfig.isBeached));
	}

	public static class Piece extends SimpleStructurePiece {
		private final Rotation rotation;
		private final Identifier field_14537;
		private final boolean isBeached;

		public Piece(StructureManager structureManager, Identifier identifier, BlockPos blockPos, Rotation rotation, boolean bl) {
			super(StructurePieceType.SHIPWRECK, 0);
			this.field_15432 = blockPos;
			this.rotation = rotation;
			this.field_14537 = identifier;
			this.isBeached = bl;
			this.method_14837(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.SHIPWRECK, compoundTag);
			this.field_14537 = new Identifier(compoundTag.getString("Template"));
			this.isBeached = compoundTag.getBoolean("isBeached");
			this.rotation = Rotation.valueOf(compoundTag.getString("Rot"));
			this.method_14837(structureManager);
		}

		@Override
		protected void method_14943(CompoundTag compoundTag) {
			super.method_14943(compoundTag);
			compoundTag.putString("Template", this.field_14537.toString());
			compoundTag.putBoolean("isBeached", this.isBeached);
			compoundTag.putString("Rot", this.rotation.name());
		}

		private void method_14837(StructureManager structureManager) {
			Structure structure = structureManager.method_15091(this.field_14537);
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setRotation(this.rotation)
				.setMirrored(Mirror.NONE)
				.method_15119(ShipwreckGenerator.field_14536)
				.method_16184(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
			this.method_15027(structure, this.field_15432, structurePlacementData);
		}

		@Override
		protected void method_15026(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox) {
			if ("map_chest".equals(string)) {
				LootableContainerBlockEntity.method_11287(iWorld, random, blockPos.down(), LootTables.field_841);
			} else if ("treasure_chest".equals(string)) {
				LootableContainerBlockEntity.method_11287(iWorld, random, blockPos.down(), LootTables.field_665);
			} else if ("supply_chest".equals(string)) {
				LootableContainerBlockEntity.method_11287(iWorld, random, blockPos.down(), LootTables.field_880);
			}
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			int i = 256;
			int j = 0;
			BlockPos blockPos = this.field_15432.add(this.field_15433.method_15160().getX() - 1, 0, this.field_15433.method_15160().getZ() - 1);

			for (BlockPos blockPos2 : BlockPos.iterateBoxPositions(this.field_15432, blockPos)) {
				int k = iWorld.method_8589(this.isBeached ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG, blockPos2.getX(), blockPos2.getZ());
				j += k;
				i = Math.min(i, k);
			}

			j /= this.field_15433.method_15160().getX() * this.field_15433.method_15160().getZ();
			int l = this.isBeached ? i - this.field_15433.method_15160().getY() / 2 - random.nextInt(3) : j;
			this.field_15432 = new BlockPos(this.field_15432.getX(), l, this.field_15432.getZ());
			return super.generate(iWorld, random, mutableIntBoundingBox, chunkPos);
		}
	}
}
