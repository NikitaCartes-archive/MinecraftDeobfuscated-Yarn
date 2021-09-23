package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.SwampHutGenerator;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.SpawnSettings;

public class SwampHutFeature extends StructureFeature<DefaultFeatureConfig> {
	public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(new SpawnSettings.SpawnEntry(EntityType.WITCH, 1, 1, 1));
	public static final Pool<SpawnSettings.SpawnEntry> CREATURE_SPAWNS = Pool.of(new SpawnSettings.SpawnEntry(EntityType.CAT, 1, 1, 1));

	public SwampHutFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec, SwampHutFeature::method_38693);
	}

	private static void method_38693(class_6626 arg, DefaultFeatureConfig defaultFeatureConfig, class_6622.class_6623 arg2) {
		if (arg2.method_38707(Heightmap.Type.WORLD_SURFACE_WG)) {
			arg.addPiece(new SwampHutGenerator(arg2.random(), arg2.chunkPos().getStartX(), arg2.chunkPos().getStartZ()));
		}
	}
}
