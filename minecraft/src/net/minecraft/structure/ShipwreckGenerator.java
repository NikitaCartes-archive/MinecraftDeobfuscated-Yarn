package net.minecraft.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;

public class ShipwreckGenerator {
	private static final BlockPos field_14536 = new BlockPos(4, 0, 15);
	private static final Identifier[] BEACHED_TEMPLATES = new Identifier[]{
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
	private static final Identifier[] REGULAR_TEMPLATES = new Identifier[]{
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

	public static void addParts(
		StructureManager structureManager, BlockPos pos, BlockRotation rotation, List<StructurePiece> children, Random random, ShipwreckFeatureConfig config
	) {
		Identifier identifier = Util.getRandom(config.isBeached ? BEACHED_TEMPLATES : REGULAR_TEMPLATES, random);
		children.add(new ShipwreckGenerator.Piece(structureManager, identifier, pos, rotation, config.isBeached));
	}

	public static class Piece extends SimpleStructurePiece {
		private final BlockRotation rotation;
		private final Identifier template;
		private final boolean grounded;

		public Piece(StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation, boolean grounded) {
			super(StructurePieceType.SHIPWRECK, 0);
			this.pos = pos;
			this.rotation = rotation;
			this.template = identifier;
			this.grounded = grounded;
			this.initializeStructureData(manager);
		}

		public Piece(StructureManager manager, CompoundTag tag) {
			super(StructurePieceType.SHIPWRECK, tag);
			this.template = new Identifier(tag.getString("Template"));
			this.grounded = tag.getBoolean("isBeached");
			this.rotation = BlockRotation.valueOf(tag.getString("Rot"));
			this.initializeStructureData(manager);
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putString("Template", this.template.toString());
			tag.putBoolean("isBeached", this.grounded);
			tag.putString("Rot", this.rotation.name());
		}

		private void initializeStructureData(StructureManager manager) {
			Structure structure = manager.getStructureOrBlank(this.template);
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setRotation(this.rotation)
				.setMirror(BlockMirror.NONE)
				.setPosition(ShipwreckGenerator.field_14536)
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
			this.setStructureData(structure, this.pos, structurePlacementData);
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess serverWorldAccess, Random random, BlockBox boundingBox) {
			if ("map_chest".equals(metadata)) {
				LootableContainerBlockEntity.setLootTable(serverWorldAccess, random, pos.down(), LootTables.SHIPWRECK_MAP_CHEST);
			} else if ("treasure_chest".equals(metadata)) {
				LootableContainerBlockEntity.setLootTable(serverWorldAccess, random, pos.down(), LootTables.SHIPWRECK_TREASURE_CHEST);
			} else if ("supply_chest".equals(metadata)) {
				LootableContainerBlockEntity.setLootTable(serverWorldAccess, random, pos.down(), LootTables.SHIPWRECK_SUPPLY_CHEST);
			}
		}

		@Override
		public boolean generate(
			StructureWorldAccess structureWorldAccess,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos blockPos
		) {
			int i = 256;
			int j = 0;
			BlockPos blockPos2 = this.structure.getSize();
			Heightmap.Type type = this.grounded ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
			int k = blockPos2.getX() * blockPos2.getZ();
			if (k == 0) {
				j = structureWorldAccess.getTopY(type, this.pos.getX(), this.pos.getZ());
			} else {
				BlockPos blockPos3 = this.pos.add(blockPos2.getX() - 1, 0, blockPos2.getZ() - 1);

				for (BlockPos blockPos4 : BlockPos.iterate(this.pos, blockPos3)) {
					int l = structureWorldAccess.getTopY(type, blockPos4.getX(), blockPos4.getZ());
					j += l;
					i = Math.min(i, l);
				}

				j /= k;
			}

			int m = this.grounded ? i - blockPos2.getY() / 2 - random.nextInt(3) : j;
			this.pos = new BlockPos(this.pos.getX(), m, this.pos.getZ());
			return super.generate(structureWorldAccess, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, blockPos);
		}
	}
}
