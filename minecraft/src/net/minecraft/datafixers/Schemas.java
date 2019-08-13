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
import net.minecraft.datafixers.fixes.FixChoiceTypes;
import net.minecraft.datafixers.fixes.FixItemName;
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
import net.minecraft.util.SystemUtil;

public class Schemas {
	private static final BiFunction<Integer, Schema, Schema> empty = Schema::new;
	private static final BiFunction<Integer, Schema, Schema> identNormalize = SchemaIdentifierNormalize::new;
	private static final DataFixer fixer = create();

	private static DataFixer create() {
		DataFixerBuilder dataFixerBuilder = new DataFixerBuilder(SharedConstants.getGameVersion().getWorldVersion());
		build(dataFixerBuilder);
		return dataFixerBuilder.build(SystemUtil.getServerWorkerExecutor());
	}

	public static DataFixer getFixer() {
		return fixer;
	}

	private static void build(DataFixerBuilder dataFixerBuilder) {
		Schema schema = dataFixerBuilder.addSchema(99, Schema99::new);
		Schema schema2 = dataFixerBuilder.addSchema(100, Schema100::new);
		dataFixerBuilder.addFixer(new EntityEquipmentToArmorAndHandFix(schema2, true));
		Schema schema3 = dataFixerBuilder.addSchema(101, empty);
		dataFixerBuilder.addFixer(new BlockEntitySignTextStrictJsonFix(schema3, false));
		Schema schema4 = dataFixerBuilder.addSchema(102, Schema102::new);
		dataFixerBuilder.addFixer(new ItemIdFix(schema4, true));
		dataFixerBuilder.addFixer(new ItemPotionFix(schema4, false));
		Schema schema5 = dataFixerBuilder.addSchema(105, empty);
		dataFixerBuilder.addFixer(new ItemSpawnEggFix(schema5, true));
		Schema schema6 = dataFixerBuilder.addSchema(106, Schema106::new);
		dataFixerBuilder.addFixer(new MobSpawnerEntityIdentifiersFix(schema6, true));
		Schema schema7 = dataFixerBuilder.addSchema(107, Schema107::new);
		dataFixerBuilder.addFixer(new EntityMinecartIdentifiersFix(schema7, true));
		Schema schema8 = dataFixerBuilder.addSchema(108, empty);
		dataFixerBuilder.addFixer(new EntityStringUuidFix(schema8, true));
		Schema schema9 = dataFixerBuilder.addSchema(109, empty);
		dataFixerBuilder.addFixer(new EntityHealthFix(schema9, true));
		Schema schema10 = dataFixerBuilder.addSchema(110, empty);
		dataFixerBuilder.addFixer(new EntityHorseSaddleFix(schema10, true));
		Schema schema11 = dataFixerBuilder.addSchema(111, empty);
		dataFixerBuilder.addFixer(new HangingEntityFix(schema11, true));
		Schema schema12 = dataFixerBuilder.addSchema(113, empty);
		dataFixerBuilder.addFixer(new EntityRedundantChanceTagsFix(schema12, true));
		Schema schema13 = dataFixerBuilder.addSchema(135, Schema135::new);
		dataFixerBuilder.addFixer(new EntityRidingToPassengerFix(schema13, true));
		Schema schema14 = dataFixerBuilder.addSchema(143, Schema143::new);
		dataFixerBuilder.addFixer(new EntityTippedArrowFix(schema14, true));
		Schema schema15 = dataFixerBuilder.addSchema(147, empty);
		dataFixerBuilder.addFixer(new EntityArmorStandSilentFix(schema15, true));
		Schema schema16 = dataFixerBuilder.addSchema(165, empty);
		dataFixerBuilder.addFixer(new ItemWrittenBookPagesStrictJsonFix(schema16, true));
		Schema schema17 = dataFixerBuilder.addSchema(501, Schema501::new);
		dataFixerBuilder.addFixer(new FixChoiceTypes(schema17, "Add 1.10 entities fix", TypeReferences.ENTITY));
		Schema schema18 = dataFixerBuilder.addSchema(502, empty);
		dataFixerBuilder.addFixer(
			FixItemName.create(
				schema18,
				"cooked_fished item renamer",
				string -> Objects.equals(SchemaIdentifierNormalize.normalize(string), "minecraft:cooked_fished") ? "minecraft:cooked_fish" : string
			)
		);
		dataFixerBuilder.addFixer(new EntityZombieVillagerTypeFix(schema18, false));
		Schema schema19 = dataFixerBuilder.addSchema(505, empty);
		dataFixerBuilder.addFixer(new OptionsForceVBOFix(schema19, false));
		Schema schema20 = dataFixerBuilder.addSchema(700, Schema700::new);
		dataFixerBuilder.addFixer(new EntityElderGuardianSplitFix(schema20, true));
		Schema schema21 = dataFixerBuilder.addSchema(701, Schema701::new);
		dataFixerBuilder.addFixer(new EntitySkeletonSplitFix(schema21, true));
		Schema schema22 = dataFixerBuilder.addSchema(702, Schema702::new);
		dataFixerBuilder.addFixer(new EntityZombieSplitFix(schema22, true));
		Schema schema23 = dataFixerBuilder.addSchema(703, Schema703::new);
		dataFixerBuilder.addFixer(new EntityHorseSplitFix(schema23, true));
		Schema schema24 = dataFixerBuilder.addSchema(704, Schema704::new);
		dataFixerBuilder.addFixer(new BlockEntityIdFix(schema24, true));
		Schema schema25 = dataFixerBuilder.addSchema(705, Schema705::new);
		dataFixerBuilder.addFixer(new EntityIdFix(schema25, true));
		Schema schema26 = dataFixerBuilder.addSchema(804, identNormalize);
		dataFixerBuilder.addFixer(new ItemBannerColorFix(schema26, true));
		Schema schema27 = dataFixerBuilder.addSchema(806, identNormalize);
		dataFixerBuilder.addFixer(new ItemWaterPotionFix(schema27, false));
		Schema schema28 = dataFixerBuilder.addSchema(808, Schema808::new);
		dataFixerBuilder.addFixer(new FixChoiceTypes(schema28, "added shulker box", TypeReferences.BLOCK_ENTITY));
		Schema schema29 = dataFixerBuilder.addSchema(808, 1, identNormalize);
		dataFixerBuilder.addFixer(new EntityShulkerColorFix(schema29, false));
		Schema schema30 = dataFixerBuilder.addSchema(813, identNormalize);
		dataFixerBuilder.addFixer(new ItemShulkerBoxColorFix(schema30, false));
		dataFixerBuilder.addFixer(new BlockEntityShulkerBoxColorFix(schema30, false));
		Schema schema31 = dataFixerBuilder.addSchema(816, identNormalize);
		dataFixerBuilder.addFixer(new OptionsLowerCaseLanguageFix(schema31, false));
		Schema schema32 = dataFixerBuilder.addSchema(820, identNormalize);
		dataFixerBuilder.addFixer(
			FixItemName.create(schema32, "totem item renamer", string -> Objects.equals(string, "minecraft:totem") ? "minecraft:totem_of_undying" : string)
		);
		Schema schema33 = dataFixerBuilder.addSchema(1022, Schema1022::new);
		dataFixerBuilder.addFixer(new WriteAndReadFix(schema33, "added shoulder entities to players", TypeReferences.PLAYER));
		Schema schema34 = dataFixerBuilder.addSchema(1125, Schema1125::new);
		dataFixerBuilder.addFixer(new BedBlockEntityFix(schema34, true));
		dataFixerBuilder.addFixer(new BedItemColorFix(schema34, false));
		Schema schema35 = dataFixerBuilder.addSchema(1344, identNormalize);
		dataFixerBuilder.addFixer(new OptionsKeyLwjgl3Fix(schema35, false));
		Schema schema36 = dataFixerBuilder.addSchema(1446, identNormalize);
		dataFixerBuilder.addFixer(new OptionsKeyTranslationFix(schema36, false));
		Schema schema37 = dataFixerBuilder.addSchema(1450, identNormalize);
		dataFixerBuilder.addFixer(new BlockStateStructureTemplateFix(schema37, false));
		Schema schema38 = dataFixerBuilder.addSchema(1451, Schema1451::new);
		dataFixerBuilder.addFixer(new FixChoiceTypes(schema38, "AddTrappedChestFix", TypeReferences.BLOCK_ENTITY));
		Schema schema39 = dataFixerBuilder.addSchema(1451, 1, Schema1451v1::new);
		dataFixerBuilder.addFixer(new ChunkPalettedStorageFix(schema39, true));
		Schema schema40 = dataFixerBuilder.addSchema(1451, 2, Schema1451v2::new);
		dataFixerBuilder.addFixer(new BlockEntityBlockStateFix(schema40, true));
		Schema schema41 = dataFixerBuilder.addSchema(1451, 3, Schema1451v3::new);
		dataFixerBuilder.addFixer(new EntityBlockStateFix(schema41, true));
		dataFixerBuilder.addFixer(new ItemInstanceMapIdFix(schema41, false));
		Schema schema42 = dataFixerBuilder.addSchema(1451, 4, Schema1451v4::new);
		dataFixerBuilder.addFixer(new BlockNameFlatteningFix(schema42, true));
		dataFixerBuilder.addFixer(new ItemInstanceTheFlatteningFix(schema42, false));
		Schema schema43 = dataFixerBuilder.addSchema(1451, 5, Schema1451v5::new);
		dataFixerBuilder.addFixer(new FixChoiceTypes(schema43, "RemoveNoteBlockFlowerPotFix", TypeReferences.BLOCK_ENTITY));
		dataFixerBuilder.addFixer(new ItemInstanceSpawnEggFix(schema43, false));
		dataFixerBuilder.addFixer(new EntityWolfColorFix(schema43, false));
		dataFixerBuilder.addFixer(new BlockEntityBannerColorFix(schema43, false));
		dataFixerBuilder.addFixer(new LevelFlatGeneratorInfoFix(schema43, false));
		Schema schema44 = dataFixerBuilder.addSchema(1451, 6, Schema1451v6::new);
		dataFixerBuilder.addFixer(new StatsCounterFix(schema44, true));
		dataFixerBuilder.addFixer(new BlockEntityJukeboxFix(schema44, false));
		Schema schema45 = dataFixerBuilder.addSchema(1451, 7, Schema1451v7::new);
		dataFixerBuilder.addFixer(new SavedDataVillageCropFix(schema45, true));
		Schema schema46 = dataFixerBuilder.addSchema(1451, 7, identNormalize);
		dataFixerBuilder.addFixer(new VillagerTradeFix(schema46, false));
		Schema schema47 = dataFixerBuilder.addSchema(1456, identNormalize);
		dataFixerBuilder.addFixer(new EntityItemFrameDirectionFix(schema47, false));
		Schema schema48 = dataFixerBuilder.addSchema(1458, identNormalize);
		dataFixerBuilder.addFixer(new EntityCustomNameToComponentFix(schema48, false));
		dataFixerBuilder.addFixer(new ItemCustomNameToComponentFix(schema48, false));
		dataFixerBuilder.addFixer(new BlockEntityCustomNameToComponentFix(schema48, false));
		Schema schema49 = dataFixerBuilder.addSchema(1460, Schema1460::new);
		dataFixerBuilder.addFixer(new EntityPaintingMotiveFix(schema49, false));
		Schema schema50 = dataFixerBuilder.addSchema(1466, Schema1466::new);
		dataFixerBuilder.addFixer(new ChunkToProtoChunkFix(schema50, true));
		Schema schema51 = dataFixerBuilder.addSchema(1470, Schema1470::new);
		dataFixerBuilder.addFixer(new FixChoiceTypes(schema51, "Add 1.13 entities fix", TypeReferences.ENTITY));
		Schema schema52 = dataFixerBuilder.addSchema(1474, identNormalize);
		dataFixerBuilder.addFixer(new ColorlessShulkerEntityFix(schema52, false));
		dataFixerBuilder.addFixer(
			BlockNameFix.create(
				schema52,
				"Colorless shulker block fixer",
				string -> Objects.equals(SchemaIdentifierNormalize.normalize(string), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : string
			)
		);
		dataFixerBuilder.addFixer(
			FixItemName.create(
				schema52,
				"Colorless shulker item fixer",
				string -> Objects.equals(SchemaIdentifierNormalize.normalize(string), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : string
			)
		);
		Schema schema53 = dataFixerBuilder.addSchema(1475, identNormalize);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(
				schema53,
				"Flowing fixer",
				string -> (String)ImmutableMap.of("minecraft:flowing_water", "minecraft:water", "minecraft:flowing_lava", "minecraft:lava").getOrDefault(string, string)
			)
		);
		Schema schema54 = dataFixerBuilder.addSchema(1480, identNormalize);
		dataFixerBuilder.addFixer(BlockNameFix.create(schema54, "Rename coral blocks", string -> (String)LegacyCoralBlockMapping.MAP.getOrDefault(string, string)));
		dataFixerBuilder.addFixer(FixItemName.create(schema54, "Rename coral items", string -> (String)LegacyCoralBlockMapping.MAP.getOrDefault(string, string)));
		Schema schema55 = dataFixerBuilder.addSchema(1481, Schema1481::new);
		dataFixerBuilder.addFixer(new FixChoiceTypes(schema55, "Add conduit", TypeReferences.BLOCK_ENTITY));
		Schema schema56 = dataFixerBuilder.addSchema(1483, Schema1483::new);
		dataFixerBuilder.addFixer(new EntityPufferfishRenameFix(schema56, true));
		dataFixerBuilder.addFixer(
			FixItemName.create(schema56, "Rename pufferfish egg item", string -> (String)EntityPufferfishRenameFix.RENAMED_FISHES.getOrDefault(string, string))
		);
		Schema schema57 = dataFixerBuilder.addSchema(1484, identNormalize);
		dataFixerBuilder.addFixer(
			FixItemName.create(
				schema57,
				"Rename seagrass items",
				string -> (String)ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass")
						.getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(
				schema57,
				"Rename seagrass blocks",
				string -> (String)ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass")
						.getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(new HeightmapRenamingFix(schema57, false));
		Schema schema58 = dataFixerBuilder.addSchema(1486, Schema1486::new);
		dataFixerBuilder.addFixer(new EntityCodSalmonFix(schema58, true));
		dataFixerBuilder.addFixer(
			FixItemName.create(schema58, "Rename cod/salmon egg items", string -> (String)EntityCodSalmonFix.SPAWN_EGGS.getOrDefault(string, string))
		);
		Schema schema59 = dataFixerBuilder.addSchema(1487, identNormalize);
		dataFixerBuilder.addFixer(
			FixItemName.create(
				schema59,
				"Rename prismarine_brick(s)_* blocks",
				string -> (String)ImmutableMap.of(
							"minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"
						)
						.getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(
				schema59,
				"Rename prismarine_brick(s)_* items",
				string -> (String)ImmutableMap.of(
							"minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"
						)
						.getOrDefault(string, string)
			)
		);
		Schema schema60 = dataFixerBuilder.addSchema(1488, identNormalize);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(
				schema60,
				"Rename kelp/kelptop",
				string -> (String)ImmutableMap.of("minecraft:kelp_top", "minecraft:kelp", "minecraft:kelp", "minecraft:kelp_plant").getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(FixItemName.create(schema60, "Rename kelptop", string -> Objects.equals(string, "minecraft:kelp_top") ? "minecraft:kelp" : string));
		dataFixerBuilder.addFixer(
			new ChoiceFix(schema60, false, "Command block block entity custom name fix", TypeReferences.BLOCK_ENTITY, "minecraft:command_block") {
				@Override
				protected Typed<?> transform(Typed<?> typed) {
					return typed.update(DSL.remainderFinder(), EntityCustomNameToComponentFix::fixCustomName);
				}
			}
		);
		dataFixerBuilder.addFixer(
			new ChoiceFix(schema60, false, "Command block minecart custom name fix", TypeReferences.ENTITY, "minecraft:commandblock_minecart") {
				@Override
				protected Typed<?> transform(Typed<?> typed) {
					return typed.update(DSL.remainderFinder(), EntityCustomNameToComponentFix::fixCustomName);
				}
			}
		);
		dataFixerBuilder.addFixer(new IglooMetadataRemovalFix(schema60, false));
		Schema schema61 = dataFixerBuilder.addSchema(1490, identNormalize);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(schema61, "Rename melon_block", string -> Objects.equals(string, "minecraft:melon_block") ? "minecraft:melon" : string)
		);
		dataFixerBuilder.addFixer(
			FixItemName.create(
				schema61,
				"Rename melon_block/melon/speckled_melon",
				string -> (String)ImmutableMap.of(
							"minecraft:melon_block", "minecraft:melon", "minecraft:melon", "minecraft:melon_slice", "minecraft:speckled_melon", "minecraft:glistering_melon_slice"
						)
						.getOrDefault(string, string)
			)
		);
		Schema schema62 = dataFixerBuilder.addSchema(1492, identNormalize);
		dataFixerBuilder.addFixer(new ChunkStructuresTemplateRenameFix(schema62, false));
		Schema schema63 = dataFixerBuilder.addSchema(1494, identNormalize);
		dataFixerBuilder.addFixer(new ItemStackEnchantmentFix(schema63, false));
		Schema schema64 = dataFixerBuilder.addSchema(1496, identNormalize);
		dataFixerBuilder.addFixer(new LeavesFix(schema64, false));
		Schema schema65 = dataFixerBuilder.addSchema(1500, identNormalize);
		dataFixerBuilder.addFixer(new BlockEntityKeepPacked(schema65, false));
		Schema schema66 = dataFixerBuilder.addSchema(1501, identNormalize);
		dataFixerBuilder.addFixer(new AdvancementsFix(schema66, false));
		Schema schema67 = dataFixerBuilder.addSchema(1502, identNormalize);
		dataFixerBuilder.addFixer(new RecipeFix(schema67, false));
		Schema schema68 = dataFixerBuilder.addSchema(1506, identNormalize);
		dataFixerBuilder.addFixer(new LevelDataGeneratorOptionsFix(schema68, false));
		Schema schema69 = dataFixerBuilder.addSchema(1508, identNormalize);
		dataFixerBuilder.addFixer(new BiomesFix(schema69, false));
		Schema schema70 = dataFixerBuilder.addSchema(1510, Schema1510::new);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(schema70, "Block renamening fix", string -> (String)EntityTheRenameningBlock.BLOCKS.getOrDefault(string, string))
		);
		dataFixerBuilder.addFixer(FixItemName.create(schema70, "Item renamening fix", string -> (String)EntityTheRenameningBlock.ITEMS.getOrDefault(string, string)));
		dataFixerBuilder.addFixer(new RecipeRenamingFix(schema70, false));
		dataFixerBuilder.addFixer(new EntityTheRenameningBlock(schema70, true));
		dataFixerBuilder.addFixer(new SwimStatsRenameFix(schema70, false));
		Schema schema71 = dataFixerBuilder.addSchema(1514, identNormalize);
		dataFixerBuilder.addFixer(new ObjectiveDisplayNameFix(schema71, false));
		dataFixerBuilder.addFixer(new TeamDisplayNameFix(schema71, false));
		dataFixerBuilder.addFixer(new ObjectiveRenderTypeFix(schema71, false));
		Schema schema72 = dataFixerBuilder.addSchema(1515, identNormalize);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(schema72, "Rename coral fan blocks", string -> (String)LegacyCoralFanBlockMapping.MAP.getOrDefault(string, string))
		);
		Schema schema73 = dataFixerBuilder.addSchema(1624, identNormalize);
		dataFixerBuilder.addFixer(new AddTrappedChestFix(schema73, false));
		Schema schema74 = dataFixerBuilder.addSchema(1800, Schema1800::new);
		dataFixerBuilder.addFixer(new FixChoiceTypes(schema74, "Added 1.14 mobs fix", TypeReferences.ENTITY));
		dataFixerBuilder.addFixer(FixItemName.create(schema74, "Rename dye items", string -> (String)LegacyDyeItemMapping.MAP.getOrDefault(string, string)));
		Schema schema75 = dataFixerBuilder.addSchema(1801, Schema1801::new);
		dataFixerBuilder.addFixer(new FixChoiceTypes(schema75, "Added Illager Beast", TypeReferences.ENTITY));
		Schema schema76 = dataFixerBuilder.addSchema(1802, identNormalize);
		dataFixerBuilder.addFixer(
			BlockNameFix.create(
				schema76,
				"Rename sign blocks & stone slabs",
				string -> (String)ImmutableMap.of(
							"minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign", "minecraft:wall_sign", "minecraft:oak_wall_sign"
						)
						.getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(
			FixItemName.create(
				schema76,
				"Rename sign item & stone slabs",
				string -> (String)ImmutableMap.of("minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign")
						.getOrDefault(string, string)
			)
		);
		Schema schema77 = dataFixerBuilder.addSchema(1803, identNormalize);
		dataFixerBuilder.addFixer(new ItemLoreToComponentFix(schema77, false));
		Schema schema78 = dataFixerBuilder.addSchema(1904, Schema1904::new);
		dataFixerBuilder.addFixer(new FixChoiceTypes(schema78, "Added Cats", TypeReferences.ENTITY));
		dataFixerBuilder.addFixer(new EntityCatSplitFix(schema78, false));
		Schema schema79 = dataFixerBuilder.addSchema(1905, identNormalize);
		dataFixerBuilder.addFixer(new ChunkStatusFix(schema79, false));
		Schema schema80 = dataFixerBuilder.addSchema(1906, Schema1906::new);
		dataFixerBuilder.addFixer(new FixChoiceTypes(schema80, "Add POI Blocks", TypeReferences.BLOCK_ENTITY));
		Schema schema81 = dataFixerBuilder.addSchema(1909, Schema1909::new);
		dataFixerBuilder.addFixer(new FixChoiceTypes(schema81, "Add jigsaw", TypeReferences.BLOCK_ENTITY));
		Schema schema82 = dataFixerBuilder.addSchema(1911, identNormalize);
		dataFixerBuilder.addFixer(new ChunkStatusFix2(schema82, false));
		Schema schema83 = dataFixerBuilder.addSchema(1917, identNormalize);
		dataFixerBuilder.addFixer(new CatTypeFix(schema83, false));
		Schema schema84 = dataFixerBuilder.addSchema(1918, identNormalize);
		dataFixerBuilder.addFixer(new VillagerProfessionFix(schema84, "minecraft:villager"));
		dataFixerBuilder.addFixer(new VillagerProfessionFix(schema84, "minecraft:zombie_villager"));
		Schema schema85 = dataFixerBuilder.addSchema(1920, Schema1920::new);
		dataFixerBuilder.addFixer(new NewVillageFix(schema85, false));
		dataFixerBuilder.addFixer(new FixChoiceTypes(schema85, "Add campfire", TypeReferences.BLOCK_ENTITY));
		Schema schema86 = dataFixerBuilder.addSchema(1925, identNormalize);
		dataFixerBuilder.addFixer(new MapIdFix(schema86, false));
		Schema schema87 = dataFixerBuilder.addSchema(1928, Schema1928::new);
		dataFixerBuilder.addFixer(new EntityRavagerRenameFix(schema87, true));
		dataFixerBuilder.addFixer(
			FixItemName.create(schema87, "Rename ravager egg item", string -> (String)EntityRavagerRenameFix.ITEMS.getOrDefault(string, string))
		);
		Schema schema88 = dataFixerBuilder.addSchema(1929, Schema1929::new);
		dataFixerBuilder.addFixer(new FixChoiceTypes(schema88, "Add Wandering Trader and Trader Llama", TypeReferences.ENTITY));
		Schema schema89 = dataFixerBuilder.addSchema(1931, Schema1931::new);
		dataFixerBuilder.addFixer(new FixChoiceTypes(schema89, "Added Fox", TypeReferences.ENTITY));
		Schema schema90 = dataFixerBuilder.addSchema(1936, identNormalize);
		dataFixerBuilder.addFixer(new OptionsAddTextBackgroundFix(schema90, false));
		Schema schema91 = dataFixerBuilder.addSchema(1946, identNormalize);
		dataFixerBuilder.addFixer(new PointOfInterestReorganizationFix(schema91, false));
		Schema schema92 = dataFixerBuilder.addSchema(1948, identNormalize);
		dataFixerBuilder.addFixer(new OminousBannerItemRenameFix(schema92, false));
		Schema schema93 = dataFixerBuilder.addSchema(1953, identNormalize);
		dataFixerBuilder.addFixer(new OminousBannerBlockEntityRenameFix(schema93, false));
		Schema schema94 = dataFixerBuilder.addSchema(1955, identNormalize);
		dataFixerBuilder.addFixer(new VillagerXpRebuildFix(schema94, false));
		dataFixerBuilder.addFixer(new ZombieVillagerXpRebuildFix(schema94, false));
		Schema schema95 = dataFixerBuilder.addSchema(1961, identNormalize);
		dataFixerBuilder.addFixer(new ChunkLightRemoveFix(schema95, false));
	}
}
