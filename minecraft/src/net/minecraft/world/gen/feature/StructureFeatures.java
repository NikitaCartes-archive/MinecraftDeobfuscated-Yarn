package net.minecraft.world.gen.feature;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.class_3443;
import net.minecraft.class_3449;
import net.minecraft.class_3485;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sortme.StructurePiece;
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
	private static final Logger logger = LogManager.getLogger();
	public static final StructureFeature<?> field_16709 = registerStructure("Mineshaft", Feature.MINESHAFT);
	public static final StructureFeature<?> field_16698 = registerStructure("Village", Feature.VILLAGE);
	public static final StructureFeature<?> field_16706 = registerStructure("Pillager_Outpost", Feature.PILLAGER_OUTPOST);
	public static final StructureFeature<?> field_16707 = registerStructure("Fortress", Feature.NETHER_BRIDGE);
	public static final StructureFeature<?> field_16697 = registerStructure("Stronghold", Feature.STRONGHOLD);
	public static final StructureFeature<?> field_16710 = registerStructure("Jungle_Pyramid", Feature.JUNGLE_TEMPLE);
	public static final StructureFeature<?> field_16705 = registerStructure("Ocean_Ruin", Feature.OCEAN_RUIN);
	public static final StructureFeature<?> field_16700 = registerStructure("Desert_Pyramid", Feature.DESERT_PYRAMID);
	public static final StructureFeature<?> field_16708 = registerStructure("Igloo", Feature.IGLOO);
	public static final StructureFeature<?> field_16703 = registerStructure("Swamp_Hut", Feature.SWAMP_HUT);
	public static final StructureFeature<?> field_16699 = registerStructure("Monument", Feature.OCEAN_MONUMENT);
	public static final StructureFeature<?> field_16701 = registerStructure("EndCity", Feature.END_CITY);
	public static final StructureFeature<?> field_16704 = registerStructure("Mansion", Feature.WOODLAND_MANSION);
	public static final StructureFeature<?> field_16711 = registerStructure("Buried_Treasure", Feature.BURIED_TREASURE);
	public static final StructureFeature<?> field_16702 = registerStructure("Shipwreck", Feature.SHIPWRECK);
	public static final StructureFeature<?> field_16867 = registerStructure("New_Village", Feature.NEW_VILLAGE);

	private static StructureFeature<?> registerStructure(String string, StructureFeature<?> structureFeature) {
		return Registry.register(Registry.STRUCTURE_FEATURE, string.toLowerCase(Locale.ROOT), structureFeature);
	}

	public static void method_16651() {
	}

	@Nullable
	public static class_3449 method_14842(ChunkGenerator<?> chunkGenerator, class_3485 arg, BiomeSource biomeSource, CompoundTag compoundTag) {
		String string = compoundTag.getString("id");
		if ("INVALID".equals(string)) {
			return class_3449.field_16713;
		} else {
			StructureFeature<?> structureFeature = Registry.STRUCTURE_FEATURE.get(new Identifier(string.toLowerCase(Locale.ROOT)));
			if (structureFeature == null) {
				logger.error("Unknown feature id: {}", string);
				return null;
			} else {
				int i = compoundTag.getInt("ChunkX");
				int j = compoundTag.getInt("ChunkZ");
				Biome biome = compoundTag.containsKey("biome")
					? Registry.BIOME.get(new Identifier(compoundTag.getString("biome")))
					: biomeSource.method_8758(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
				MutableIntBoundingBox mutableIntBoundingBox = compoundTag.containsKey("BB")
					? new MutableIntBoundingBox(compoundTag.getIntArray("BB"))
					: MutableIntBoundingBox.maxSize();
				ListTag listTag = compoundTag.getList("Children", 10);

				try {
					class_3449 lv = structureFeature.method_14016().create(structureFeature, i, j, biome, mutableIntBoundingBox, 0, chunkGenerator.getSeed());

					for (int k = 0; k < listTag.size(); k++) {
						CompoundTag compoundTag2 = listTag.getCompoundTag(k);
						String string2 = compoundTag2.getString("id");
						StructurePiece structurePiece = Registry.STRUCTURE_PIECE.get(new Identifier(string2.toLowerCase(Locale.ROOT)));
						if (structurePiece == null) {
							logger.error("Unknown structure piece id: {}", string2);
						} else {
							try {
								class_3443 lv2 = structurePiece.load(arg, compoundTag2);
								lv.children.add(lv2);
							} catch (Exception var17) {
								logger.error("Exception loading structure piece with id {}", string2, var17);
							}
						}
					}

					return lv;
				} catch (Exception var18) {
					logger.error("Failed Start with id {}", string, var18);
					return null;
				}
			}
		}
	}
}
