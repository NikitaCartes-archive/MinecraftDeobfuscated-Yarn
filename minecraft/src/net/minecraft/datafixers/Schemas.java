package net.minecraft.datafixers;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.function.BiFunction;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.fixes.AddTrappedChestFix;
import net.minecraft.datafixers.fixes.AdvancementsFix;
import net.minecraft.datafixers.fixes.BedBlockEntityFix;
import net.minecraft.datafixers.fixes.BedItemColorFix;
import net.minecraft.datafixers.fixes.BeehiveRenameFix;
import net.minecraft.datafixers.fixes.BiomeFormatFix;
import net.minecraft.datafixers.fixes.BiomesFix;
import net.minecraft.datafixers.fixes.BlockEntityBannerColorFix;
import net.minecraft.datafixers.fixes.BlockEntityBlockStateFix;
import net.minecraft.datafixers.fixes.BlockEntityCustomNameToComponentFix;
import net.minecraft.datafixers.fixes.BlockEntityIdFix;
import net.minecraft.datafixers.fixes.BlockEntityJukeboxFix;
import net.minecraft.datafixers.fixes.BlockEntityKeepPacked;
import net.minecraft.datafixers.fixes.BlockEntityShulkerBoxColorFix;
import net.minecraft.datafixers.fixes.BlockEntitySignTextStrictJsonFix;
import net.minecraft.datafixers.fixes.BlockNameFix;
import net.minecraft.datafixers.fixes.BlockNameFlatteningFix;
import net.minecraft.datafixers.fixes.BlockStateStructureTemplateFix;
import net.minecraft.datafixers.fixes.CatTypeFix;
import net.minecraft.datafixers.fixes.ChoiceFix;
import net.minecraft.datafixers.fixes.ChoiceTypesFix;
import net.minecraft.datafixers.fixes.ChunkLightRemoveFix;
import net.minecraft.datafixers.fixes.ChunkPalettedStorageFix;
import net.minecraft.datafixers.fixes.ChunkStatusFix;
import net.minecraft.datafixers.fixes.ChunkStatusFix2;
import net.minecraft.datafixers.fixes.ChunkStructuresTemplateRenameFix;
import net.minecraft.datafixers.fixes.ChunkToProtoChunkFix;
import net.minecraft.datafixers.fixes.ColorlessShulkerEntityFix;
import net.minecraft.datafixers.fixes.EntityArmorStandSilentFix;
import net.minecraft.datafixers.fixes.EntityBlockStateFix;
import net.minecraft.datafixers.fixes.EntityCatSplitFix;
import net.minecraft.datafixers.fixes.EntityCodSalmonFix;
import net.minecraft.datafixers.fixes.EntityCustomNameToComponentFix;
import net.minecraft.datafixers.fixes.EntityElderGuardianSplitFix;
import net.minecraft.datafixers.fixes.EntityEquipmentToArmorAndHandFix;
import net.minecraft.datafixers.fixes.EntityHealthFix;
import net.minecraft.datafixers.fixes.EntityHorseSaddleFix;
import net.minecraft.datafixers.fixes.EntityHorseSplitFix;
import net.minecraft.datafixers.fixes.EntityIdFix;
import net.minecraft.datafixers.fixes.EntityItemFrameDirectionFix;
import net.minecraft.datafixers.fixes.EntityMinecartIdentifiersFix;
import net.minecraft.datafixers.fixes.EntityPaintingMotiveFix;
import net.minecraft.datafixers.fixes.EntityPufferfishRenameFix;
import net.minecraft.datafixers.fixes.EntityRavagerRenameFix;
import net.minecraft.datafixers.fixes.EntityRedundantChanceTagsFix;
import net.minecraft.datafixers.fixes.EntityRidingToPassengerFix;
import net.minecraft.datafixers.fixes.EntityShulkerColorFix;
import net.minecraft.datafixers.fixes.EntitySkeletonSplitFix;
import net.minecraft.datafixers.fixes.EntityStringUuidFix;
import net.minecraft.datafixers.fixes.EntityTheRenameningBlock;
import net.minecraft.datafixers.fixes.EntityTippedArrowFix;
import net.minecraft.datafixers.fixes.EntityWolfColorFix;
import net.minecraft.datafixers.fixes.EntityZombieSplitFix;
import net.minecraft.datafixers.fixes.EntityZombieVillagerTypeFix;
import net.minecraft.datafixers.fixes.HangingEntityFix;
import net.minecraft.datafixers.fixes.HeightmapRenamingFix;
import net.minecraft.datafixers.fixes.IglooMetadataRemovalFix;
import net.minecraft.datafixers.fixes.ItemBannerColorFix;
import net.minecraft.datafixers.fixes.ItemCustomNameToComponentFix;
import net.minecraft.datafixers.fixes.ItemIdFix;
import net.minecraft.datafixers.fixes.ItemInstanceMapIdFix;
import net.minecraft.datafixers.fixes.ItemInstanceSpawnEggFix;
import net.minecraft.datafixers.fixes.ItemInstanceTheFlatteningFix;
import net.minecraft.datafixers.fixes.ItemLoreToComponentFix;
import net.minecraft.datafixers.fixes.ItemNameFix;
import net.minecraft.datafixers.fixes.ItemPotionFix;
import net.minecraft.datafixers.fixes.ItemShulkerBoxColorFix;
import net.minecraft.datafixers.fixes.ItemSpawnEggFix;
import net.minecraft.datafixers.fixes.ItemStackEnchantmentFix;
import net.minecraft.datafixers.fixes.ItemWaterPotionFix;
import net.minecraft.datafixers.fixes.ItemWrittenBookPagesStrictJsonFix;
import net.minecraft.datafixers.fixes.LeavesFix;
import net.minecraft.datafixers.fixes.LevelDataGeneratorOptionsFix;
import net.minecraft.datafixers.fixes.LevelFlatGeneratorInfoFix;
import net.minecraft.datafixers.fixes.MapIdFix;
import net.minecraft.datafixers.fixes.MobSpawnerEntityIdentifiersFix;
import net.minecraft.datafixers.fixes.NewVillageFix;
import net.minecraft.datafixers.fixes.ObjectiveDisplayNameFix;
import net.minecraft.datafixers.fixes.ObjectiveRenderTypeFix;
import net.minecraft.datafixers.fixes.OminousBannerBlockEntityRenameFix;
import net.minecraft.datafixers.fixes.OminousBannerItemRenameFix;
import net.minecraft.datafixers.fixes.OptionsAddTextBackgroundFix;
import net.minecraft.datafixers.fixes.OptionsForceVBOFix;
import net.minecraft.datafixers.fixes.OptionsKeyLwjgl3Fix;
import net.minecraft.datafixers.fixes.OptionsKeyTranslationFix;
import net.minecraft.datafixers.fixes.OptionsLowerCaseLanguageFix;
import net.minecraft.datafixers.fixes.PointOfInterestReorganizationFix;
import net.minecraft.datafixers.fixes.RecipeFix;
import net.minecraft.datafixers.fixes.RecipeRenamingFix;
import net.minecraft.datafixers.fixes.SavedDataVillageCropFix;
import net.minecraft.datafixers.fixes.StatsCounterFix;
import net.minecraft.datafixers.fixes.SwimStatsRenameFix;
import net.minecraft.datafixers.fixes.TeamDisplayNameFix;
import net.minecraft.datafixers.fixes.VillagerProfessionFix;
import net.minecraft.datafixers.fixes.VillagerTradeFix;
import net.minecraft.datafixers.fixes.VillagerXpRebuildFix;
import net.minecraft.datafixers.fixes.WriteAndReadFix;
import net.minecraft.datafixers.fixes.ZombieVillagerXpRebuildFix;
import net.minecraft.datafixers.mapping.LegacyCoralBlockMapping;
import net.minecraft.datafixers.mapping.LegacyCoralFanBlockMapping;
import net.minecraft.datafixers.mapping.LegacyDyeItemMapping;
import net.minecraft.datafixers.schemas.Schema100;
import net.minecraft.datafixers.schemas.Schema102;
import net.minecraft.datafixers.schemas.Schema1022;
import net.minecraft.datafixers.schemas.Schema106;
import net.minecraft.datafixers.schemas.Schema107;
import net.minecraft.datafixers.schemas.Schema1125;
import net.minecraft.datafixers.schemas.Schema135;
import net.minecraft.datafixers.schemas.Schema143;
import net.minecraft.datafixers.schemas.Schema1451;
import net.minecraft.datafixers.schemas.Schema1451v1;
import net.minecraft.datafixers.schemas.Schema1451v2;
import net.minecraft.datafixers.schemas.Schema1451v3;
import net.minecraft.datafixers.schemas.Schema1451v4;
import net.minecraft.datafixers.schemas.Schema1451v5;
import net.minecraft.datafixers.schemas.Schema1451v6;
import net.minecraft.datafixers.schemas.Schema1451v7;
import net.minecraft.datafixers.schemas.Schema1460;
import net.minecraft.datafixers.schemas.Schema1466;
import net.minecraft.datafixers.schemas.Schema1470;
import net.minecraft.datafixers.schemas.Schema1481;
import net.minecraft.datafixers.schemas.Schema1483;
import net.minecraft.datafixers.schemas.Schema1486;
import net.minecraft.datafixers.schemas.Schema1510;
import net.minecraft.datafixers.schemas.Schema1800;
import net.minecraft.datafixers.schemas.Schema1801;
import net.minecraft.datafixers.schemas.Schema1904;
import net.minecraft.datafixers.schemas.Schema1906;
import net.minecraft.datafixers.schemas.Schema1909;
import net.minecraft.datafixers.schemas.Schema1920;
import net.minecraft.datafixers.schemas.Schema1928;
import net.minecraft.datafixers.schemas.Schema1929;
import net.minecraft.datafixers.schemas.Schema1931;
import net.minecraft.datafixers.schemas.Schema2100;
import net.minecraft.datafixers.schemas.Schema501;
import net.minecraft.datafixers.schemas.Schema700;
import net.minecraft.datafixers.schemas.Schema701;
import net.minecraft.datafixers.schemas.Schema702;
import net.minecraft.datafixers.schemas.Schema703;
import net.minecraft.datafixers.schemas.Schema704;
import net.minecraft.datafixers.schemas.Schema705;
import net.minecraft.datafixers.schemas.Schema808;
import net.minecraft.datafixers.schemas.Schema99;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;
import net.minecraft.util.Util;

public class Schemas {
	private static final BiFunction<Integer, Schema, Schema> EMPTY = Schema::new;
	private static final BiFunction<Integer, Schema, Schema> EMPTY_IDENTIFIER_NORMALIZE = SchemaIdentifierNormalize::new;
	private static final DataFixer fixer = create();

	private static DataFixer create() {
		DataFixerBuilder dataFixerBuilder = new DataFixerBuilder(SharedConstants.getGameVersion().getWorldVersion());
		build(dataFixerBuilder);
		return dataFixerBuilder.build(Util.getServerWorkerExecutor());
	}

	public static DataFixer getFixer() {
		return fixer;
	}

	private static void build(DataFixerBuilder builder) {
		Schema schema = builder.addSchema(99, Schema99::new);
		Schema schema2 = builder.addSchema(100, Schema100::new);
		builder.addFixer(new EntityEquipmentToArmorAndHandFix(schema2, true));
		Schema schema3 = builder.addSchema(101, EMPTY);
		builder.addFixer(new BlockEntitySignTextStrictJsonFix(schema3, false));
		Schema schema4 = builder.addSchema(102, Schema102::new);
		builder.addFixer(new ItemIdFix(schema4, true));
		builder.addFixer(new ItemPotionFix(schema4, false));
		Schema schema5 = builder.addSchema(105, EMPTY);
		builder.addFixer(new ItemSpawnEggFix(schema5, true));
		Schema schema6 = builder.addSchema(106, Schema106::new);
		builder.addFixer(new MobSpawnerEntityIdentifiersFix(schema6, true));
		Schema schema7 = builder.addSchema(107, Schema107::new);
		builder.addFixer(new EntityMinecartIdentifiersFix(schema7, true));
		Schema schema8 = builder.addSchema(108, EMPTY);
		builder.addFixer(new EntityStringUuidFix(schema8, true));
		Schema schema9 = builder.addSchema(109, EMPTY);
		builder.addFixer(new EntityHealthFix(schema9, true));
		Schema schema10 = builder.addSchema(110, EMPTY);
		builder.addFixer(new EntityHorseSaddleFix(schema10, true));
		Schema schema11 = builder.addSchema(111, EMPTY);
		builder.addFixer(new HangingEntityFix(schema11, true));
		Schema schema12 = builder.addSchema(113, EMPTY);
		builder.addFixer(new EntityRedundantChanceTagsFix(schema12, true));
		Schema schema13 = builder.addSchema(135, Schema135::new);
		builder.addFixer(new EntityRidingToPassengerFix(schema13, true));
		Schema schema14 = builder.addSchema(143, Schema143::new);
		builder.addFixer(new EntityTippedArrowFix(schema14, true));
		Schema schema15 = builder.addSchema(147, EMPTY);
		builder.addFixer(new EntityArmorStandSilentFix(schema15, true));
		Schema schema16 = builder.addSchema(165, EMPTY);
		builder.addFixer(new ItemWrittenBookPagesStrictJsonFix(schema16, true));
		Schema schema17 = builder.addSchema(501, Schema501::new);
		builder.addFixer(new ChoiceTypesFix(schema17, "Add 1.10 entities fix", TypeReferences.ENTITY));
		Schema schema18 = builder.addSchema(502, EMPTY);
		builder.addFixer(
			ItemNameFix.create(
				schema18,
				"cooked_fished item renamer",
				string -> Objects.equals(SchemaIdentifierNormalize.normalize(string), "minecraft:cooked_fished") ? "minecraft:cooked_fish" : string
			)
		);
		builder.addFixer(new EntityZombieVillagerTypeFix(schema18, false));
		Schema schema19 = builder.addSchema(505, EMPTY);
		builder.addFixer(new OptionsForceVBOFix(schema19, false));
		Schema schema20 = builder.addSchema(700, Schema700::new);
		builder.addFixer(new EntityElderGuardianSplitFix(schema20, true));
		Schema schema21 = builder.addSchema(701, Schema701::new);
		builder.addFixer(new EntitySkeletonSplitFix(schema21, true));
		Schema schema22 = builder.addSchema(702, Schema702::new);
		builder.addFixer(new EntityZombieSplitFix(schema22, true));
		Schema schema23 = builder.addSchema(703, Schema703::new);
		builder.addFixer(new EntityHorseSplitFix(schema23, true));
		Schema schema24 = builder.addSchema(704, Schema704::new);
		builder.addFixer(new BlockEntityIdFix(schema24, true));
		Schema schema25 = builder.addSchema(705, Schema705::new);
		builder.addFixer(new EntityIdFix(schema25, true));
		Schema schema26 = builder.addSchema(804, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemBannerColorFix(schema26, true));
		Schema schema27 = builder.addSchema(806, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemWaterPotionFix(schema27, false));
		Schema schema28 = builder.addSchema(808, Schema808::new);
		builder.addFixer(new ChoiceTypesFix(schema28, "added shulker box", TypeReferences.BLOCK_ENTITY));
		Schema schema29 = builder.addSchema(808, 1, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityShulkerColorFix(schema29, false));
		Schema schema30 = builder.addSchema(813, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemShulkerBoxColorFix(schema30, false));
		builder.addFixer(new BlockEntityShulkerBoxColorFix(schema30, false));
		Schema schema31 = builder.addSchema(816, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsLowerCaseLanguageFix(schema31, false));
		Schema schema32 = builder.addSchema(820, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(schema32, "totem item renamer", string -> Objects.equals(string, "minecraft:totem") ? "minecraft:totem_of_undying" : string)
		);
		Schema schema33 = builder.addSchema(1022, Schema1022::new);
		builder.addFixer(new WriteAndReadFix(schema33, "added shoulder entities to players", TypeReferences.PLAYER));
		Schema schema34 = builder.addSchema(1125, Schema1125::new);
		builder.addFixer(new BedBlockEntityFix(schema34, true));
		builder.addFixer(new BedItemColorFix(schema34, false));
		Schema schema35 = builder.addSchema(1344, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsKeyLwjgl3Fix(schema35, false));
		Schema schema36 = builder.addSchema(1446, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsKeyTranslationFix(schema36, false));
		Schema schema37 = builder.addSchema(1450, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BlockStateStructureTemplateFix(schema37, false));
		Schema schema38 = builder.addSchema(1451, Schema1451::new);
		builder.addFixer(new ChoiceTypesFix(schema38, "AddTrappedChestFix", TypeReferences.BLOCK_ENTITY));
		Schema schema39 = builder.addSchema(1451, 1, Schema1451v1::new);
		builder.addFixer(new ChunkPalettedStorageFix(schema39, true));
		Schema schema40 = builder.addSchema(1451, 2, Schema1451v2::new);
		builder.addFixer(new BlockEntityBlockStateFix(schema40, true));
		Schema schema41 = builder.addSchema(1451, 3, Schema1451v3::new);
		builder.addFixer(new EntityBlockStateFix(schema41, true));
		builder.addFixer(new ItemInstanceMapIdFix(schema41, false));
		Schema schema42 = builder.addSchema(1451, 4, Schema1451v4::new);
		builder.addFixer(new BlockNameFlatteningFix(schema42, true));
		builder.addFixer(new ItemInstanceTheFlatteningFix(schema42, false));
		Schema schema43 = builder.addSchema(1451, 5, Schema1451v5::new);
		builder.addFixer(new ChoiceTypesFix(schema43, "RemoveNoteBlockFlowerPotFix", TypeReferences.BLOCK_ENTITY));
		builder.addFixer(new ItemInstanceSpawnEggFix(schema43, false));
		builder.addFixer(new EntityWolfColorFix(schema43, false));
		builder.addFixer(new BlockEntityBannerColorFix(schema43, false));
		builder.addFixer(new LevelFlatGeneratorInfoFix(schema43, false));
		Schema schema44 = builder.addSchema(1451, 6, Schema1451v6::new);
		builder.addFixer(new StatsCounterFix(schema44, true));
		builder.addFixer(new BlockEntityJukeboxFix(schema44, false));
		Schema schema45 = builder.addSchema(1451, 7, Schema1451v7::new);
		builder.addFixer(new SavedDataVillageCropFix(schema45, true));
		Schema schema46 = builder.addSchema(1451, 7, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new VillagerTradeFix(schema46, false));
		Schema schema47 = builder.addSchema(1456, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityItemFrameDirectionFix(schema47, false));
		Schema schema48 = builder.addSchema(1458, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityCustomNameToComponentFix(schema48, false));
		builder.addFixer(new ItemCustomNameToComponentFix(schema48, false));
		builder.addFixer(new BlockEntityCustomNameToComponentFix(schema48, false));
		Schema schema49 = builder.addSchema(1460, Schema1460::new);
		builder.addFixer(new EntityPaintingMotiveFix(schema49, false));
		Schema schema50 = builder.addSchema(1466, Schema1466::new);
		builder.addFixer(new ChunkToProtoChunkFix(schema50, true));
		Schema schema51 = builder.addSchema(1470, Schema1470::new);
		builder.addFixer(new ChoiceTypesFix(schema51, "Add 1.13 entities fix", TypeReferences.ENTITY));
		Schema schema52 = builder.addSchema(1474, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ColorlessShulkerEntityFix(schema52, false));
		builder.addFixer(
			BlockNameFix.create(
				schema52,
				"Colorless shulker block fixer",
				string -> Objects.equals(SchemaIdentifierNormalize.normalize(string), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : string
			)
		);
		builder.addFixer(
			ItemNameFix.create(
				schema52,
				"Colorless shulker item fixer",
				string -> Objects.equals(SchemaIdentifierNormalize.normalize(string), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : string
			)
		);
		Schema schema53 = builder.addSchema(1475, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			BlockNameFix.create(
				schema53,
				"Flowing fixer",
				string -> ImmutableMap.of("minecraft:flowing_water", "minecraft:water", "minecraft:flowing_lava", "minecraft:lava").getOrDefault(string, string)
			)
		);
		Schema schema54 = builder.addSchema(1480, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(BlockNameFix.create(schema54, "Rename coral blocks", string -> (String)LegacyCoralBlockMapping.MAP.getOrDefault(string, string)));
		builder.addFixer(ItemNameFix.create(schema54, "Rename coral items", string -> (String)LegacyCoralBlockMapping.MAP.getOrDefault(string, string)));
		Schema schema55 = builder.addSchema(1481, Schema1481::new);
		builder.addFixer(new ChoiceTypesFix(schema55, "Add conduit", TypeReferences.BLOCK_ENTITY));
		Schema schema56 = builder.addSchema(1483, Schema1483::new);
		builder.addFixer(new EntityPufferfishRenameFix(schema56, true));
		builder.addFixer(
			ItemNameFix.create(schema56, "Rename pufferfish egg item", string -> (String)EntityPufferfishRenameFix.RENAMED_FISHES.getOrDefault(string, string))
		);
		Schema schema57 = builder.addSchema(1484, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(
				schema57,
				"Rename seagrass items",
				string -> ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass").getOrDefault(string, string)
			)
		);
		builder.addFixer(
			BlockNameFix.create(
				schema57,
				"Rename seagrass blocks",
				string -> ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass").getOrDefault(string, string)
			)
		);
		builder.addFixer(new HeightmapRenamingFix(schema57, false));
		Schema schema58 = builder.addSchema(1486, Schema1486::new);
		builder.addFixer(new EntityCodSalmonFix(schema58, true));
		builder.addFixer(ItemNameFix.create(schema58, "Rename cod/salmon egg items", string -> (String)EntityCodSalmonFix.SPAWN_EGGS.getOrDefault(string, string)));
		Schema schema59 = builder.addSchema(1487, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(
				schema59,
				"Rename prismarine_brick(s)_* blocks",
				string -> ImmutableMap.of(
							"minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"
						)
						.getOrDefault(string, string)
			)
		);
		builder.addFixer(
			BlockNameFix.create(
				schema59,
				"Rename prismarine_brick(s)_* items",
				string -> ImmutableMap.of(
							"minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"
						)
						.getOrDefault(string, string)
			)
		);
		Schema schema60 = builder.addSchema(1488, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			BlockNameFix.create(
				schema60,
				"Rename kelp/kelptop",
				string -> ImmutableMap.of("minecraft:kelp_top", "minecraft:kelp", "minecraft:kelp", "minecraft:kelp_plant").getOrDefault(string, string)
			)
		);
		builder.addFixer(ItemNameFix.create(schema60, "Rename kelptop", string -> Objects.equals(string, "minecraft:kelp_top") ? "minecraft:kelp" : string));
		builder.addFixer(new ChoiceFix(schema60, false, "Command block block entity custom name fix", TypeReferences.BLOCK_ENTITY, "minecraft:command_block") {
			@Override
			protected Typed<?> transform(Typed<?> typed) {
				return typed.update(DSL.remainderFinder(), EntityCustomNameToComponentFix::fixCustomName);
			}
		});
		builder.addFixer(new ChoiceFix(schema60, false, "Command block minecart custom name fix", TypeReferences.ENTITY, "minecraft:commandblock_minecart") {
			@Override
			protected Typed<?> transform(Typed<?> typed) {
				return typed.update(DSL.remainderFinder(), EntityCustomNameToComponentFix::fixCustomName);
			}
		});
		builder.addFixer(new IglooMetadataRemovalFix(schema60, false));
		Schema schema61 = builder.addSchema(1490, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(BlockNameFix.create(schema61, "Rename melon_block", string -> Objects.equals(string, "minecraft:melon_block") ? "minecraft:melon" : string));
		builder.addFixer(
			ItemNameFix.create(
				schema61,
				"Rename melon_block/melon/speckled_melon",
				string -> ImmutableMap.of(
							"minecraft:melon_block", "minecraft:melon", "minecraft:melon", "minecraft:melon_slice", "minecraft:speckled_melon", "minecraft:glistering_melon_slice"
						)
						.getOrDefault(string, string)
			)
		);
		Schema schema62 = builder.addSchema(1492, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ChunkStructuresTemplateRenameFix(schema62, false));
		Schema schema63 = builder.addSchema(1494, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemStackEnchantmentFix(schema63, false));
		Schema schema64 = builder.addSchema(1496, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new LeavesFix(schema64, false));
		Schema schema65 = builder.addSchema(1500, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BlockEntityKeepPacked(schema65, false));
		Schema schema66 = builder.addSchema(1501, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new AdvancementsFix(schema66, false));
		Schema schema67 = builder.addSchema(1502, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new RecipeFix(schema67, false));
		Schema schema68 = builder.addSchema(1506, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new LevelDataGeneratorOptionsFix(schema68, false));
		Schema schema69 = builder.addSchema(1508, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BiomesFix(schema69, false));
		Schema schema70 = builder.addSchema(1510, Schema1510::new);
		builder.addFixer(BlockNameFix.create(schema70, "Block renamening fix", string -> (String)EntityTheRenameningBlock.BLOCKS.getOrDefault(string, string)));
		builder.addFixer(ItemNameFix.create(schema70, "Item renamening fix", string -> (String)EntityTheRenameningBlock.ITEMS.getOrDefault(string, string)));
		builder.addFixer(new RecipeRenamingFix(schema70, false));
		builder.addFixer(new EntityTheRenameningBlock(schema70, true));
		builder.addFixer(new SwimStatsRenameFix(schema70, false));
		Schema schema71 = builder.addSchema(1514, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ObjectiveDisplayNameFix(schema71, false));
		builder.addFixer(new TeamDisplayNameFix(schema71, false));
		builder.addFixer(new ObjectiveRenderTypeFix(schema71, false));
		Schema schema72 = builder.addSchema(1515, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(BlockNameFix.create(schema72, "Rename coral fan blocks", string -> (String)LegacyCoralFanBlockMapping.MAP.getOrDefault(string, string)));
		Schema schema73 = builder.addSchema(1624, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new AddTrappedChestFix(schema73, false));
		Schema schema74 = builder.addSchema(1800, Schema1800::new);
		builder.addFixer(new ChoiceTypesFix(schema74, "Added 1.14 mobs fix", TypeReferences.ENTITY));
		builder.addFixer(ItemNameFix.create(schema74, "Rename dye items", string -> (String)LegacyDyeItemMapping.MAP.getOrDefault(string, string)));
		Schema schema75 = builder.addSchema(1801, Schema1801::new);
		builder.addFixer(new ChoiceTypesFix(schema75, "Added Illager Beast", TypeReferences.ENTITY));
		Schema schema76 = builder.addSchema(1802, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			BlockNameFix.create(
				schema76,
				"Rename sign blocks & stone slabs",
				string -> ImmutableMap.of(
							"minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign", "minecraft:wall_sign", "minecraft:oak_wall_sign"
						)
						.getOrDefault(string, string)
			)
		);
		builder.addFixer(
			ItemNameFix.create(
				schema76,
				"Rename sign item & stone slabs",
				string -> ImmutableMap.of("minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign").getOrDefault(string, string)
			)
		);
		Schema schema77 = builder.addSchema(1803, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemLoreToComponentFix(schema77, false));
		Schema schema78 = builder.addSchema(1904, Schema1904::new);
		builder.addFixer(new ChoiceTypesFix(schema78, "Added Cats", TypeReferences.ENTITY));
		builder.addFixer(new EntityCatSplitFix(schema78, false));
		Schema schema79 = builder.addSchema(1905, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ChunkStatusFix(schema79, false));
		Schema schema80 = builder.addSchema(1906, Schema1906::new);
		builder.addFixer(new ChoiceTypesFix(schema80, "Add POI Blocks", TypeReferences.BLOCK_ENTITY));
		Schema schema81 = builder.addSchema(1909, Schema1909::new);
		builder.addFixer(new ChoiceTypesFix(schema81, "Add jigsaw", TypeReferences.BLOCK_ENTITY));
		Schema schema82 = builder.addSchema(1911, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ChunkStatusFix2(schema82, false));
		Schema schema83 = builder.addSchema(1917, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new CatTypeFix(schema83, false));
		Schema schema84 = builder.addSchema(1918, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new VillagerProfessionFix(schema84, "minecraft:villager"));
		builder.addFixer(new VillagerProfessionFix(schema84, "minecraft:zombie_villager"));
		Schema schema85 = builder.addSchema(1920, Schema1920::new);
		builder.addFixer(new NewVillageFix(schema85, false));
		builder.addFixer(new ChoiceTypesFix(schema85, "Add campfire", TypeReferences.BLOCK_ENTITY));
		Schema schema86 = builder.addSchema(1925, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new MapIdFix(schema86, false));
		Schema schema87 = builder.addSchema(1928, Schema1928::new);
		builder.addFixer(new EntityRavagerRenameFix(schema87, true));
		builder.addFixer(ItemNameFix.create(schema87, "Rename ravager egg item", string -> (String)EntityRavagerRenameFix.ITEMS.getOrDefault(string, string)));
		Schema schema88 = builder.addSchema(1929, Schema1929::new);
		builder.addFixer(new ChoiceTypesFix(schema88, "Add Wandering Trader and Trader Llama", TypeReferences.ENTITY));
		Schema schema89 = builder.addSchema(1931, Schema1931::new);
		builder.addFixer(new ChoiceTypesFix(schema89, "Added Fox", TypeReferences.ENTITY));
		Schema schema90 = builder.addSchema(1936, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsAddTextBackgroundFix(schema90, false));
		Schema schema91 = builder.addSchema(1946, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new PointOfInterestReorganizationFix(schema91, false));
		Schema schema92 = builder.addSchema(1948, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OminousBannerItemRenameFix(schema92, false));
		Schema schema93 = builder.addSchema(1953, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OminousBannerBlockEntityRenameFix(schema93, false));
		Schema schema94 = builder.addSchema(1955, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new VillagerXpRebuildFix(schema94, false));
		builder.addFixer(new ZombieVillagerXpRebuildFix(schema94, false));
		Schema schema95 = builder.addSchema(1961, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ChunkLightRemoveFix(schema95, false));
		Schema schema96 = builder.addSchema(2100, Schema2100::new);
		builder.addFixer(new ChoiceTypesFix(schema96, "Added Bee and Bee Stinger", TypeReferences.ENTITY));
		builder.addFixer(new ChoiceTypesFix(schema96, "Add beehive", TypeReferences.BLOCK_ENTITY));
		Schema schema97 = builder.addSchema(2202, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BiomeFormatFix(schema97, false));
		Schema schema98 = builder.addSchema(2209, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(schema98, "Rename bee_hive item to beehive", string -> Objects.equals(string, "minecraft:bee_hive") ? "minecraft:beehive" : string)
		);
		builder.addFixer(new BeehiveRenameFix(schema98));
		builder.addFixer(
			BlockNameFix.create(
				schema98, "Rename bee_hive block to beehive", string -> ImmutableMap.of("minecraft:bee_hive", "minecraft:beehive").getOrDefault(string, string)
			)
		);
		Schema schema99 = builder.addSchema(2211, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new StructureReferenceFixer(schema99, false));
	}
}
