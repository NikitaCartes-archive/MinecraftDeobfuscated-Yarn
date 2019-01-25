package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.generator.SwampHutGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SwampHutFeature extends AbstractTempleFeature<DefaultFeatureConfig> {
	private static final List<Biome.SpawnEntry> field_13882 = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.WITCH, 1, 1, 1));
	private static final List<Biome.SpawnEntry> field_16435 = Lists.<Biome.SpawnEntry>newArrayList(new Biome.SpawnEntry(EntityType.CAT, 1, 1, 1));

	public SwampHutFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	@Override
	public String getName() {
		return "Swamp_Hut";
	}

	@Override
	public int method_14021() {
		return 3;
	}

	@Override
	public StructureFeature.StructureStartFactory getStructureStartFactory() {
		return SwampHutFeature.class_3198::new;
	}

	@Override
	protected int method_13774() {
		return 14357620;
	}

	@Override
	public List<Biome.SpawnEntry> getMonsterSpawns() {
		return field_13882;
	}

	@Override
	public List<Biome.SpawnEntry> getCreatureSpawns() {
		return field_16435;
	}

	public boolean method_14029(IWorld iWorld, BlockPos blockPos) {
		StructureStart structureStart = this.method_14025(iWorld, blockPos, true);
		if (structureStart != StructureStart.DEFAULT && structureStart instanceof SwampHutFeature.class_3198 && !structureStart.getChildren().isEmpty()) {
			StructurePiece structurePiece = (StructurePiece)structureStart.getChildren().get(0);
			return structurePiece instanceof SwampHutGenerator;
		} else {
			return false;
		}
	}

	public static class class_3198 extends StructureStart {
		public class_3198(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
			super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
		}

		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
			SwampHutGenerator swampHutGenerator = new SwampHutGenerator(this.random, i * 16, j * 16);
			this.children.add(swampHutGenerator);
			this.setBoundingBoxFromChildren();
		}
	}
}
