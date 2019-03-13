package net.minecraft.world.gen.feature;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureFeatures {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final StructureFeature<?> MINESHAFT = register("Mineshaft", Feature.field_13547);
	public static final StructureFeature<?> PILLAGER_OUTPOST = register("Pillager_Outpost", Feature.field_16655);
	public static final StructureFeature<?> FORTRESS = register("Fortress", Feature.field_13569);
	public static final StructureFeature<?> STRONGHOLD = register("Stronghold", Feature.field_13565);
	public static final StructureFeature<?> JUNGLE_PYRAMID = register("Jungle_Pyramid", Feature.field_13586);
	public static final StructureFeature<?> OCEAN_RUIN = register("Ocean_Ruin", Feature.field_13536);
	public static final StructureFeature<?> DESERT_PYRAMID = register("Desert_Pyramid", Feature.field_13515);
	public static final StructureFeature<?> IGLOO = register("Igloo", Feature.field_13527);
	public static final StructureFeature<?> SWAMP_HUT = register("Swamp_Hut", Feature.field_13520);
	public static final StructureFeature<?> MONUMENT = register("Monument", Feature.field_13588);
	public static final StructureFeature<?> END_CITY = register("EndCity", Feature.field_13553);
	public static final StructureFeature<?> MANSION = register("Mansion", Feature.field_13528);
	public static final StructureFeature<?> BURIED_TREASURE = register("Buried_Treasure", Feature.field_13538);
	public static final StructureFeature<?> SHIPWRECK = register("Shipwreck", Feature.field_13589);
	public static final StructureFeature<?> VILLAGE = register("Village", Feature.field_13587);

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
			StructureFeature<?> structureFeature = Registry.STRUCTURE_FEATURE.method_10223(new Identifier(string.toLowerCase(Locale.ROOT)));
			if (structureFeature == null) {
				LOGGER.error("Unknown feature id: {}", string);
				return null;
			} else {
				int i = compoundTag.getInt("ChunkX");
				int j = compoundTag.getInt("ChunkZ");
				Biome biome = compoundTag.containsKey("biome")
					? Registry.BIOME.method_10223(new Identifier(compoundTag.getString("biome")))
					: biomeSource.method_8758(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
				MutableIntBoundingBox mutableIntBoundingBox = compoundTag.containsKey("BB")
					? new MutableIntBoundingBox(compoundTag.getIntArray("BB"))
					: MutableIntBoundingBox.empty();
				ListTag listTag = compoundTag.method_10554("Children", 10);

				try {
					StructureStart structureStart = structureFeature.getStructureStartFactory()
						.create(structureFeature, i, j, biome, mutableIntBoundingBox, 0, chunkGenerator.getSeed());

					for (int k = 0; k < listTag.size(); k++) {
						CompoundTag compoundTag2 = listTag.getCompoundTag(k);
						String string2 = compoundTag2.getString("id");
						StructurePieceType structurePieceType = Registry.STRUCTURE_PIECE.method_10223(new Identifier(string2.toLowerCase(Locale.ROOT)));
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
