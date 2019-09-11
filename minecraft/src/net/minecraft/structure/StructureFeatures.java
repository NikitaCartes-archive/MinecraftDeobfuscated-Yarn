package net.minecraft.structure;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureFeatures {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final StructureFeature<?> MINESHAFT = register("Mineshaft", Feature.MINESHAFT);
	public static final StructureFeature<?> PILLAGER_OUTPOST = register("Pillager_Outpost", Feature.PILLAGER_OUTPOST);
	public static final StructureFeature<?> FORTRESS = register("Fortress", Feature.NETHER_BRIDGE);
	public static final StructureFeature<?> STRONGHOLD = register("Stronghold", Feature.STRONGHOLD);
	public static final StructureFeature<?> JUNGLE_PYRAMID = register("Jungle_Pyramid", Feature.JUNGLE_TEMPLE);
	public static final StructureFeature<?> OCEAN_RUIN = register("Ocean_Ruin", Feature.OCEAN_RUIN);
	public static final StructureFeature<?> DESERT_PYRAMID = register("Desert_Pyramid", Feature.DESERT_PYRAMID);
	public static final StructureFeature<?> IGLOO = register("Igloo", Feature.IGLOO);
	public static final StructureFeature<?> SWAMP_HUT = register("Swamp_Hut", Feature.SWAMP_HUT);
	public static final StructureFeature<?> MONUMENT = register("Monument", Feature.OCEAN_MONUMENT);
	public static final StructureFeature<?> END_CITY = register("EndCity", Feature.END_CITY);
	public static final StructureFeature<?> MANSION = register("Mansion", Feature.WOODLAND_MANSION);
	public static final StructureFeature<?> BURIED_TREASURE = register("Buried_Treasure", Feature.BURIED_TREASURE);
	public static final StructureFeature<?> SHIPWRECK = register("Shipwreck", Feature.SHIPWRECK);
	public static final StructureFeature<?> VILLAGE = register("Village", Feature.VILLAGE);

	private static StructureFeature<?> register(String string, StructureFeature<?> structureFeature) {
		return Registry.register(Registry.STRUCTURE_FEATURE, string.toLowerCase(Locale.ROOT), structureFeature);
	}

	public static void initialize() {
	}

	@Nullable
	public static StructureStart readStructureStart(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, CompoundTag compoundTag) {
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
				int k = compoundTag.getInt("references");
				BlockBox blockBox = compoundTag.containsKey("BB") ? new BlockBox(compoundTag.getIntArray("BB")) : BlockBox.empty();
				ListTag listTag = compoundTag.getList("Children", 10);

				try {
					StructureStart structureStart = structureFeature.getStructureStartFactory().create(structureFeature, i, j, blockBox, k, chunkGenerator.getSeed());

					for (int l = 0; l < listTag.size(); l++) {
						CompoundTag compoundTag2 = listTag.getCompoundTag(l);
						String string2 = compoundTag2.getString("id");
						StructurePieceType structurePieceType = Registry.STRUCTURE_PIECE.get(new Identifier(string2.toLowerCase(Locale.ROOT)));
						if (structurePieceType == null) {
							LOGGER.error("Unknown structure piece id: {}", string2);
						} else {
							try {
								StructurePiece structurePiece = structurePieceType.load(structureManager, compoundTag2);
								structureStart.children.add(structurePiece);
							} catch (Exception var16) {
								LOGGER.error("Exception loading structure piece with id {}", string2, var16);
							}
						}
					}

					return structureStart;
				} catch (Exception var17) {
					LOGGER.error("Failed Start with id {}", string, var17);
					return null;
				}
			}
		}
	}
}
