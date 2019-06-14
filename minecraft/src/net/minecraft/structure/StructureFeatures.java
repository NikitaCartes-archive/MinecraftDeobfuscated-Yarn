package net.minecraft.structure;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureFeatures {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final StructureFeature<?> field_16709 = register("Mineshaft", Feature.field_13547);
	public static final StructureFeature<?> field_16706 = register("Pillager_Outpost", Feature.field_16655);
	public static final StructureFeature<?> field_16707 = register("Fortress", Feature.field_13569);
	public static final StructureFeature<?> field_16697 = register("Stronghold", Feature.field_13565);
	public static final StructureFeature<?> field_16710 = register("Jungle_Pyramid", Feature.field_13586);
	public static final StructureFeature<?> field_16705 = register("Ocean_Ruin", Feature.field_13536);
	public static final StructureFeature<?> field_16700 = register("Desert_Pyramid", Feature.field_13515);
	public static final StructureFeature<?> field_16708 = register("Igloo", Feature.field_13527);
	public static final StructureFeature<?> field_16703 = register("Swamp_Hut", Feature.field_13520);
	public static final StructureFeature<?> field_16699 = register("Monument", Feature.field_13588);
	public static final StructureFeature<?> field_16701 = register("EndCity", Feature.field_13553);
	public static final StructureFeature<?> field_16704 = register("Mansion", Feature.field_13528);
	public static final StructureFeature<?> field_16711 = register("Buried_Treasure", Feature.field_13538);
	public static final StructureFeature<?> field_16702 = register("Shipwreck", Feature.field_13589);
	public static final StructureFeature<?> field_16698 = register("Village", Feature.field_13587);

	private static StructureFeature<?> register(String string, StructureFeature<?> structureFeature) {
		return Registry.register(Registry.STRUCTURE_FEATURE, string.toLowerCase(Locale.ROOT), structureFeature);
	}

	public static void initialize() {
	}

	@Nullable
	public static StructureStart method_14842(
		ChunkGenerator<?> chunkGenerator, StructureManager structureManager, BiomeSource biomeSource, CompoundTag compoundTag
	) {
		String string = compoundTag.getString("id");
		if ("INVALID".equals(string)) {
			return StructureStart.DEFAULT;
		} else {
			StructureFeature<?> structureFeature = Registry.STRUCTURE_FEATURE.get(new Identifier(string.toLowerCase(Locale.ROOT)));
			if (structureFeature == null) {
				LOGGER.error("Unknown feature id: {}", string);
				return null;
			} else {
				int i = compoundTag.getInt("ChunkX");
				int j = compoundTag.getInt("ChunkZ");
				Biome biome = compoundTag.containsKey("biome")
					? Registry.BIOME.get(new Identifier(compoundTag.getString("biome")))
					: biomeSource.getBiome(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
				MutableIntBoundingBox mutableIntBoundingBox = compoundTag.containsKey("BB")
					? new MutableIntBoundingBox(compoundTag.getIntArray("BB"))
					: MutableIntBoundingBox.empty();
				ListTag listTag = compoundTag.getList("Children", 10);

				try {
					StructureStart structureStart = structureFeature.getStructureStartFactory()
						.create(structureFeature, i, j, biome, mutableIntBoundingBox, 0, chunkGenerator.getSeed());

					for (int k = 0; k < listTag.size(); k++) {
						CompoundTag compoundTag2 = listTag.getCompoundTag(k);
						String string2 = compoundTag2.getString("id");
						StructurePieceType structurePieceType = Registry.STRUCTURE_PIECE.get(new Identifier(string2.toLowerCase(Locale.ROOT)));
						if (structurePieceType == null) {
							LOGGER.error("Unknown structure piece id: {}", string2);
						} else {
							try {
								StructurePiece structurePiece = structurePieceType.load(structureManager, compoundTag2);
								structureStart.children.add(structurePiece);
							} catch (Exception var17) {
								LOGGER.error("Exception loading structure piece with id {}", string2, var17);
							}
						}
					}

					return structureStart;
				} catch (Exception var18) {
					LOGGER.error("Failed Start with id {}", string, var18);
					return null;
				}
			}
		}
	}
}
