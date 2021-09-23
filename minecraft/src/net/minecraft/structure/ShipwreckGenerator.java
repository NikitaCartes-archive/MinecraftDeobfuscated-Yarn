package net.minecraft.structure;

import java.util.Map;
import java.util.Random;
import net.minecraft.class_6625;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;

public class ShipwreckGenerator {
	static final BlockPos DEFAULT_POSITION = new BlockPos(4, 0, 15);
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
	static final Map<String, Identifier> field_34939 = Map.of(
		"map_chest", LootTables.SHIPWRECK_MAP_CHEST, "treasure_chest", LootTables.SHIPWRECK_TREASURE_CHEST, "supply_chest", LootTables.SHIPWRECK_SUPPLY_CHEST
	);

	public static void addParts(
		StructureManager structureManager,
		BlockPos pos,
		BlockRotation rotation,
		StructurePiecesHolder structurePiecesHolder,
		Random random,
		ShipwreckFeatureConfig config
	) {
		Identifier identifier = Util.getRandom(config.isBeached ? BEACHED_TEMPLATES : REGULAR_TEMPLATES, random);
		structurePiecesHolder.addPiece(new ShipwreckGenerator.Piece(structureManager, identifier, pos, rotation, config.isBeached));
	}

	public static class Piece extends SimpleStructurePiece {
		private final boolean grounded;

		public Piece(StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation, boolean grounded) {
			super(StructurePieceType.SHIPWRECK, 0, manager, identifier, identifier.toString(), createPlacementData(rotation), pos);
			this.grounded = grounded;
		}

		public Piece(StructureManager structureManager, NbtCompound nbt) {
			super(StructurePieceType.SHIPWRECK, nbt, structureManager, identifier -> createPlacementData(BlockRotation.valueOf(nbt.getString("Rot"))));
			this.grounded = nbt.getBoolean("isBeached");
		}

		@Override
		protected void writeNbt(class_6625 arg, NbtCompound nbt) {
			super.writeNbt(arg, nbt);
			nbt.putBoolean("isBeached", this.grounded);
			nbt.putString("Rot", this.placementData.getRotation().name());
		}

		private static StructurePlacementData createPlacementData(BlockRotation rotation) {
			return new StructurePlacementData()
				.setRotation(rotation)
				.setMirror(BlockMirror.NONE)
				.setPosition(ShipwreckGenerator.DEFAULT_POSITION)
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
			Identifier identifier = (Identifier)ShipwreckGenerator.field_34939.get(metadata);
			if (identifier != null) {
				LootableContainerBlockEntity.setLootTable(world, random, pos.down(), identifier);
			}
		}

		@Override
		public void generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox boundingBox,
			ChunkPos chunkPos,
			BlockPos pos
		) {
			int i = world.getTopY();
			int j = 0;
			Vec3i vec3i = this.structure.getSize();
			Heightmap.Type type = this.grounded ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
			int k = vec3i.getX() * vec3i.getZ();
			if (k == 0) {
				j = world.getTopY(type, this.pos.getX(), this.pos.getZ());
			} else {
				BlockPos blockPos = this.pos.add(vec3i.getX() - 1, 0, vec3i.getZ() - 1);

				for (BlockPos blockPos2 : BlockPos.iterate(this.pos, blockPos)) {
					int l = world.getTopY(type, blockPos2.getX(), blockPos2.getZ());
					j += l;
					i = Math.min(i, l);
				}

				j /= k;
			}

			int m = this.grounded ? i - vec3i.getY() / 2 - random.nextInt(3) : j;
			this.pos = new BlockPos(this.pos.getX(), m, this.pos.getZ());
			super.generate(world, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, pos);
		}
	}
}
