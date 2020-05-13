package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.SwampHutGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class SwampHutFeature extends AbstractTempleFeature<DefaultFeatureConfig> {
	private static final List<Biome.SpawnEntry> MONSTER_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.WITCH, 1, 1, 1));
	private static final List<Biome.SpawnEntry> CREATURE_SPAWNS = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.CAT, 1, 1, 1));

	public SwampHutFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public String getName() {
		return "Swamp_Hut";
	}

	@Override
	public int getRadius() {
		return 3;
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return SwampHutFeature.Start::new;
	}

	@Override
	protected int getSeedModifier(ChunkGeneratorConfig chunkGeneratorConfig) {
		return 14357620;
	}

	@Override
	public List<Biome.SpawnEntry> getMonsterSpawns() {
		return MONSTER_SPAWNS;
	}

	@Override
	public List<Biome.SpawnEntry> getCreatureSpawns() {
		return CREATURE_SPAWNS;
	}

	public boolean method_14029(StructureAccessor structureAccessor, BlockPos blockPos) {
		StructureStart structureStart = this.isInsideStructure(structureAccessor, blockPos, true);
		if (structureStart.hasChildren() && structureStart instanceof SwampHutFeature.Start) {
			StructurePiece structurePiece = (StructurePiece)structureStart.getChildren().get(0);
			return structurePiece instanceof SwampHutGenerator;
		} else {
			return false;
		}
	}

	public static class Start extends StructureStart {
		public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		@Override
		public void init(ChunkGenerator chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
			SwampHutGenerator swampHutGenerator = new SwampHutGenerator(this.random, x * 16, z * 16);
			this.children.add(swampHutGenerator);
			this.setBoundingBoxFromChildren();
		}
	}
}
