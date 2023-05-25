package net.minecraft.datafixer;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.fix.AddFlagIfNotPresentFix;
import net.minecraft.datafixer.fix.AddTrappedChestFix;
import net.minecraft.datafixer.fix.AdvancementCriteriaRenameFix;
import net.minecraft.datafixer.fix.AdvancementRenameFix;
import net.minecraft.datafixer.fix.AdvancementsFix;
import net.minecraft.datafixer.fix.ArrowPickupFix;
import net.minecraft.datafixer.fix.BedBlockEntityFix;
import net.minecraft.datafixer.fix.BedItemColorFix;
import net.minecraft.datafixer.fix.BiomeFormatFix;
import net.minecraft.datafixer.fix.BitStorageAlignFix;
import net.minecraft.datafixer.fix.BlendingDataFix;
import net.minecraft.datafixer.fix.BlendingDataRemoveFromNetherEndFix;
import net.minecraft.datafixer.fix.BlockEntityBannerColorFix;
import net.minecraft.datafixer.fix.BlockEntityBlockStateFix;
import net.minecraft.datafixer.fix.BlockEntityCustomNameToTextFix;
import net.minecraft.datafixer.fix.BlockEntityIdFix;
import net.minecraft.datafixer.fix.BlockEntityJukeboxFix;
import net.minecraft.datafixer.fix.BlockEntityKeepPackedFix;
import net.minecraft.datafixer.fix.BlockEntityShulkerBoxColorFix;
import net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix;
import net.minecraft.datafixer.fix.BlockEntityUuidFix;
import net.minecraft.datafixer.fix.BlockNameFix;
import net.minecraft.datafixer.fix.BlockNameFlatteningFix;
import net.minecraft.datafixer.fix.BlockStateStructureTemplateFix;
import net.minecraft.datafixer.fix.CatTypeFix;
import net.minecraft.datafixer.fix.CauldronRenameFix;
import net.minecraft.datafixer.fix.ChoiceFix;
import net.minecraft.datafixer.fix.ChoiceTypesFix;
import net.minecraft.datafixer.fix.ChunkDeleteIgnoredLightDataFix;
import net.minecraft.datafixer.fix.ChunkDeleteLightFix;
import net.minecraft.datafixer.fix.ChunkHeightAndBiomeFix;
import net.minecraft.datafixer.fix.ChunkLevelTagRenameFix;
import net.minecraft.datafixer.fix.ChunkLightRemoveFix;
import net.minecraft.datafixer.fix.ChunkPalettedStorageFix;
import net.minecraft.datafixer.fix.ChunkStatusFix;
import net.minecraft.datafixer.fix.ChunkStatusFix2;
import net.minecraft.datafixer.fix.ChunkStructuresTemplateRenameFix;
import net.minecraft.datafixer.fix.ChunkToProtoChunkFix;
import net.minecraft.datafixer.fix.ColorlessShulkerEntityFix;
import net.minecraft.datafixer.fix.DecoratedPotFieldRenameFix;
import net.minecraft.datafixer.fix.EntityArmorStandSilentFix;
import net.minecraft.datafixer.fix.EntityBlockStateFix;
import net.minecraft.datafixer.fix.EntityBrushableBlockFieldsRenameFix;
import net.minecraft.datafixer.fix.EntityCatSplitFix;
import net.minecraft.datafixer.fix.EntityCodSalmonFix;
import net.minecraft.datafixer.fix.EntityCustomNameToTextFix;
import net.minecraft.datafixer.fix.EntityElderGuardianSplitFix;
import net.minecraft.datafixer.fix.EntityEquipmentToArmorAndHandFix;
import net.minecraft.datafixer.fix.EntityHealthFix;
import net.minecraft.datafixer.fix.EntityHorseSaddleFix;
import net.minecraft.datafixer.fix.EntityHorseSplitFix;
import net.minecraft.datafixer.fix.EntityIdFix;
import net.minecraft.datafixer.fix.EntityItemFrameDirectionFix;
import net.minecraft.datafixer.fix.EntityMinecartIdentifiersFix;
import net.minecraft.datafixer.fix.EntityPaintingFieldsRenameFix;
import net.minecraft.datafixer.fix.EntityPaintingMotiveFix;
import net.minecraft.datafixer.fix.EntityProjectileOwnerFix;
import net.minecraft.datafixer.fix.EntityPufferfishRenameFix;
import net.minecraft.datafixer.fix.EntityRavagerRenameFix;
import net.minecraft.datafixer.fix.EntityRedundantChanceTagsFix;
import net.minecraft.datafixer.fix.EntityRidingToPassengerFix;
import net.minecraft.datafixer.fix.EntityShulkerColorFix;
import net.minecraft.datafixer.fix.EntityShulkerRotationFix;
import net.minecraft.datafixer.fix.EntitySkeletonSplitFix;
import net.minecraft.datafixer.fix.EntityStringUuidFix;
import net.minecraft.datafixer.fix.EntityTheRenameningBlockFix;
import net.minecraft.datafixer.fix.EntityTippedArrowFix;
import net.minecraft.datafixer.fix.EntityUuidFix;
import net.minecraft.datafixer.fix.EntityVariantTypeFix;
import net.minecraft.datafixer.fix.EntityWolfColorFix;
import net.minecraft.datafixer.fix.EntityZombieSplitFix;
import net.minecraft.datafixer.fix.EntityZombieVillagerTypeFix;
import net.minecraft.datafixer.fix.EntityZombifiedPiglinRenameFix;
import net.minecraft.datafixer.fix.FurnaceRecipesFix;
import net.minecraft.datafixer.fix.GameEventRenamesFix;
import net.minecraft.datafixer.fix.GoatHornIdFix;
import net.minecraft.datafixer.fix.GoatMissingStateFix;
import net.minecraft.datafixer.fix.HangingEntityFix;
import net.minecraft.datafixer.fix.HeightmapRenamingFix;
import net.minecraft.datafixer.fix.IglooMetadataRemovalFix;
import net.minecraft.datafixer.fix.ItemBannerColorFix;
import net.minecraft.datafixer.fix.ItemCustomNameToComponentFix;
import net.minecraft.datafixer.fix.ItemIdFix;
import net.minecraft.datafixer.fix.ItemInstanceMapIdFix;
import net.minecraft.datafixer.fix.ItemInstanceSpawnEggFix;
import net.minecraft.datafixer.fix.ItemInstanceTheFlatteningFix;
import net.minecraft.datafixer.fix.ItemLoreToTextFix;
import net.minecraft.datafixer.fix.ItemNameFix;
import net.minecraft.datafixer.fix.ItemPotionFix;
import net.minecraft.datafixer.fix.ItemRemoveBlockEntityTagFix;
import net.minecraft.datafixer.fix.ItemShulkerBoxColorFix;
import net.minecraft.datafixer.fix.ItemSpawnEggFix;
import net.minecraft.datafixer.fix.ItemStackEnchantmentFix;
import net.minecraft.datafixer.fix.ItemStackUuidFix;
import net.minecraft.datafixer.fix.ItemWaterPotionFix;
import net.minecraft.datafixer.fix.ItemWrittenBookPagesStrictJsonFix;
import net.minecraft.datafixer.fix.JigsawBlockNameFix;
import net.minecraft.datafixer.fix.JigsawPropertiesFix;
import net.minecraft.datafixer.fix.JigsawRotationFix;
import net.minecraft.datafixer.fix.LeavesFix;
import net.minecraft.datafixer.fix.LegacyDragonFightFix;
import net.minecraft.datafixer.fix.LevelDataGeneratorOptionsFix;
import net.minecraft.datafixer.fix.LevelFlatGeneratorInfoFix;
import net.minecraft.datafixer.fix.MapIdFix;
import net.minecraft.datafixer.fix.MemoryExpiryDataFix;
import net.minecraft.datafixer.fix.MissingDimensionFix;
import net.minecraft.datafixer.fix.MobSpawnerEntityIdentifiersFix;
import net.minecraft.datafixer.fix.NewVillageFix;
import net.minecraft.datafixer.fix.ObjectiveDisplayNameFix;
import net.minecraft.datafixer.fix.ObjectiveRenderTypeFix;
import net.minecraft.datafixer.fix.OminousBannerBlockEntityRenameFix;
import net.minecraft.datafixer.fix.OminousBannerItemRenameFix;
import net.minecraft.datafixer.fix.OptionFix;
import net.minecraft.datafixer.fix.OptionsAccessibilityOnboardFix;
import net.minecraft.datafixer.fix.OptionsAddTextBackgroundFix;
import net.minecraft.datafixer.fix.OptionsAmbientOcclusionFix;
import net.minecraft.datafixer.fix.OptionsForceVBOFix;
import net.minecraft.datafixer.fix.OptionsKeyLwjgl3Fix;
import net.minecraft.datafixer.fix.OptionsKeyTranslationFix;
import net.minecraft.datafixer.fix.OptionsLowerCaseLanguageFix;
import net.minecraft.datafixer.fix.OptionsProgrammerArtFix;
import net.minecraft.datafixer.fix.PersistentStateUuidFix;
import net.minecraft.datafixer.fix.PlayerUuidFix;
import net.minecraft.datafixer.fix.PointOfInterestRemoveFix;
import net.minecraft.datafixer.fix.PointOfInterestRenameFix;
import net.minecraft.datafixer.fix.PointOfInterestReorganizationFix;
import net.minecraft.datafixer.fix.ProtoChunkTickListFix;
import net.minecraft.datafixer.fix.RedstoneConnectionsFix;
import net.minecraft.datafixer.fix.RemoveFeatureTogglesFix;
import net.minecraft.datafixer.fix.RemoveFilteredBookTextFix;
import net.minecraft.datafixer.fix.RemoveFilteredSignTextFix;
import net.minecraft.datafixer.fix.RemoveGolemGossipFix;
import net.minecraft.datafixer.fix.RemovePoiValidTagFix;
import net.minecraft.datafixer.fix.RenameBlockEntityFix;
import net.minecraft.datafixer.fix.RenameChunkStatusFix;
import net.minecraft.datafixer.fix.RenameItemStackAttributesFix;
import net.minecraft.datafixer.fix.RenameVariantsFix;
import net.minecraft.datafixer.fix.StatsCounterFix;
import net.minecraft.datafixer.fix.StatsRenameFix;
import net.minecraft.datafixer.fix.StatusEffectDurationFix;
import net.minecraft.datafixer.fix.StriderGravityFix;
import net.minecraft.datafixer.fix.StructureFeatureChildrenPoolElementFix;
import net.minecraft.datafixer.fix.StructureReferenceFix;
import net.minecraft.datafixer.fix.StructureSeparationDataFix;
import net.minecraft.datafixer.fix.StructureSettingsFlattenFix;
import net.minecraft.datafixer.fix.StructuresToConfiguredStructuresFix;
import net.minecraft.datafixer.fix.TeamDisplayNameFix;
import net.minecraft.datafixer.fix.TicksInWrongChunkFix;
import net.minecraft.datafixer.fix.UntaggedSpawnerFix;
import net.minecraft.datafixer.fix.UpdateSignTextFormatFix;
import net.minecraft.datafixer.fix.VillagerFollowRangeFix;
import net.minecraft.datafixer.fix.VillagerGossipFix;
import net.minecraft.datafixer.fix.VillagerProfessionFix;
import net.minecraft.datafixer.fix.VillagerTradeFix;
import net.minecraft.datafixer.fix.VillagerXpRebuildFix;
import net.minecraft.datafixer.fix.WallPropertyFix;
import net.minecraft.datafixer.fix.WeaponsmithChestLootTableFix;
import net.minecraft.datafixer.fix.WorldGenSettingsDisallowOldCustomWorldsFix;
import net.minecraft.datafixer.fix.WorldGenSettingsHeightAndBiomeFix;
import net.minecraft.datafixer.fix.WorldUuidFix;
import net.minecraft.datafixer.fix.WriteAndReadFix;
import net.minecraft.datafixer.fix.ZombieVillagerXpRebuildFix;
import net.minecraft.datafixer.mapping.FlatteningBiomeMapping;
import net.minecraft.datafixer.mapping.FlatteningRecipeMapping;
import net.minecraft.datafixer.mapping.LegacyBiomeMapping;
import net.minecraft.datafixer.mapping.LegacyCoralBlockMapping;
import net.minecraft.datafixer.mapping.LegacyCoralFanBlockMapping;
import net.minecraft.datafixer.mapping.LegacyDyeItemMapping;
import net.minecraft.datafixer.mapping.WoodRecipeMapping;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.datafixer.schema.Schema100;
import net.minecraft.datafixer.schema.Schema102;
import net.minecraft.datafixer.schema.Schema1022;
import net.minecraft.datafixer.schema.Schema106;
import net.minecraft.datafixer.schema.Schema107;
import net.minecraft.datafixer.schema.Schema1125;
import net.minecraft.datafixer.schema.Schema135;
import net.minecraft.datafixer.schema.Schema143;
import net.minecraft.datafixer.schema.Schema1451;
import net.minecraft.datafixer.schema.Schema1451v1;
import net.minecraft.datafixer.schema.Schema1451v2;
import net.minecraft.datafixer.schema.Schema1451v3;
import net.minecraft.datafixer.schema.Schema1451v4;
import net.minecraft.datafixer.schema.Schema1451v5;
import net.minecraft.datafixer.schema.Schema1451v6;
import net.minecraft.datafixer.schema.Schema1460;
import net.minecraft.datafixer.schema.Schema1466;
import net.minecraft.datafixer.schema.Schema1470;
import net.minecraft.datafixer.schema.Schema1481;
import net.minecraft.datafixer.schema.Schema1483;
import net.minecraft.datafixer.schema.Schema1486;
import net.minecraft.datafixer.schema.Schema1510;
import net.minecraft.datafixer.schema.Schema1800;
import net.minecraft.datafixer.schema.Schema1801;
import net.minecraft.datafixer.schema.Schema1904;
import net.minecraft.datafixer.schema.Schema1906;
import net.minecraft.datafixer.schema.Schema1909;
import net.minecraft.datafixer.schema.Schema1920;
import net.minecraft.datafixer.schema.Schema1928;
import net.minecraft.datafixer.schema.Schema1929;
import net.minecraft.datafixer.schema.Schema1931;
import net.minecraft.datafixer.schema.Schema2100;
import net.minecraft.datafixer.schema.Schema2501;
import net.minecraft.datafixer.schema.Schema2502;
import net.minecraft.datafixer.schema.Schema2505;
import net.minecraft.datafixer.schema.Schema2509;
import net.minecraft.datafixer.schema.Schema2519;
import net.minecraft.datafixer.schema.Schema2522;
import net.minecraft.datafixer.schema.Schema2551;
import net.minecraft.datafixer.schema.Schema2568;
import net.minecraft.datafixer.schema.Schema2571;
import net.minecraft.datafixer.schema.Schema2684;
import net.minecraft.datafixer.schema.Schema2686;
import net.minecraft.datafixer.schema.Schema2688;
import net.minecraft.datafixer.schema.Schema2704;
import net.minecraft.datafixer.schema.Schema2707;
import net.minecraft.datafixer.schema.Schema2831;
import net.minecraft.datafixer.schema.Schema2832;
import net.minecraft.datafixer.schema.Schema2842;
import net.minecraft.datafixer.schema.Schema3076;
import net.minecraft.datafixer.schema.Schema3078;
import net.minecraft.datafixer.schema.Schema3081;
import net.minecraft.datafixer.schema.Schema3082;
import net.minecraft.datafixer.schema.Schema3083;
import net.minecraft.datafixer.schema.Schema3202;
import net.minecraft.datafixer.schema.Schema3203;
import net.minecraft.datafixer.schema.Schema3204;
import net.minecraft.datafixer.schema.Schema3325;
import net.minecraft.datafixer.schema.Schema3326;
import net.minecraft.datafixer.schema.Schema3327;
import net.minecraft.datafixer.schema.Schema3328;
import net.minecraft.datafixer.schema.Schema3438;
import net.minecraft.datafixer.schema.Schema3448;
import net.minecraft.datafixer.schema.Schema501;
import net.minecraft.datafixer.schema.Schema700;
import net.minecraft.datafixer.schema.Schema701;
import net.minecraft.datafixer.schema.Schema702;
import net.minecraft.datafixer.schema.Schema703;
import net.minecraft.datafixer.schema.Schema704;
import net.minecraft.datafixer.schema.Schema705;
import net.minecraft.datafixer.schema.Schema808;
import net.minecraft.datafixer.schema.Schema99;
import net.minecraft.util.Util;

public class Schemas {
	private static final BiFunction<Integer, Schema, Schema> EMPTY = Schema::new;
	private static final BiFunction<Integer, Schema, Schema> EMPTY_IDENTIFIER_NORMALIZE = IdentifierNormalizingSchema::new;
	private static final DataFixer FIXER = create(SharedConstants.requiredDataFixTypes);
	public static final int field_38844 = 3441;

	private Schemas() {
	}

	public static DataFixer getFixer() {
		return FIXER;
	}

	private static synchronized DataFixer create(Set<TypeReference> requiredTypes) {
		DataFixerBuilder dataFixerBuilder = new DataFixerBuilder(SharedConstants.getGameVersion().getSaveVersion().getId());
		build(dataFixerBuilder);
		if (requiredTypes.isEmpty()) {
			return dataFixerBuilder.buildUnoptimized();
		} else {
			Executor executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Datafixer Bootstrap").setDaemon(true).setPriority(1).build());
			return dataFixerBuilder.buildOptimized(requiredTypes, executor);
		}
	}

	private static void build(DataFixerBuilder builder) {
		builder.addSchema(99, Schema99::new);
		Schema schema = builder.addSchema(100, Schema100::new);
		builder.addFixer(new EntityEquipmentToArmorAndHandFix(schema, true));
		Schema schema2 = builder.addSchema(101, EMPTY);
		builder.addFixer(new BlockEntitySignTextStrictJsonFix(schema2, false));
		Schema schema3 = builder.addSchema(102, Schema102::new);
		builder.addFixer(new ItemIdFix(schema3, true));
		builder.addFixer(new ItemPotionFix(schema3, false));
		Schema schema4 = builder.addSchema(105, EMPTY);
		builder.addFixer(new ItemSpawnEggFix(schema4, true));
		Schema schema5 = builder.addSchema(106, Schema106::new);
		builder.addFixer(new MobSpawnerEntityIdentifiersFix(schema5, true));
		Schema schema6 = builder.addSchema(107, Schema107::new);
		builder.addFixer(new EntityMinecartIdentifiersFix(schema6, true));
		Schema schema7 = builder.addSchema(108, EMPTY);
		builder.addFixer(new EntityStringUuidFix(schema7, true));
		Schema schema8 = builder.addSchema(109, EMPTY);
		builder.addFixer(new EntityHealthFix(schema8, true));
		Schema schema9 = builder.addSchema(110, EMPTY);
		builder.addFixer(new EntityHorseSaddleFix(schema9, true));
		Schema schema10 = builder.addSchema(111, EMPTY);
		builder.addFixer(new HangingEntityFix(schema10, true));
		Schema schema11 = builder.addSchema(113, EMPTY);
		builder.addFixer(new EntityRedundantChanceTagsFix(schema11, true));
		Schema schema12 = builder.addSchema(135, Schema135::new);
		builder.addFixer(new EntityRidingToPassengerFix(schema12, true));
		Schema schema13 = builder.addSchema(143, Schema143::new);
		builder.addFixer(new EntityTippedArrowFix(schema13, true));
		Schema schema14 = builder.addSchema(147, EMPTY);
		builder.addFixer(new EntityArmorStandSilentFix(schema14, true));
		Schema schema15 = builder.addSchema(165, EMPTY);
		builder.addFixer(new ItemWrittenBookPagesStrictJsonFix(schema15, true));
		Schema schema16 = builder.addSchema(501, Schema501::new);
		builder.addFixer(new ChoiceTypesFix(schema16, "Add 1.10 entities fix", TypeReferences.ENTITY));
		Schema schema17 = builder.addSchema(502, EMPTY);
		builder.addFixer(
			ItemNameFix.create(
				schema17,
				"cooked_fished item renamer",
				id -> Objects.equals(IdentifierNormalizingSchema.normalize(id), "minecraft:cooked_fished") ? "minecraft:cooked_fish" : id
			)
		);
		builder.addFixer(new EntityZombieVillagerTypeFix(schema17, false));
		Schema schema18 = builder.addSchema(505, EMPTY);
		builder.addFixer(new OptionsForceVBOFix(schema18, false));
		Schema schema19 = builder.addSchema(700, Schema700::new);
		builder.addFixer(new EntityElderGuardianSplitFix(schema19, true));
		Schema schema20 = builder.addSchema(701, Schema701::new);
		builder.addFixer(new EntitySkeletonSplitFix(schema20, true));
		Schema schema21 = builder.addSchema(702, Schema702::new);
		builder.addFixer(new EntityZombieSplitFix(schema21, true));
		Schema schema22 = builder.addSchema(703, Schema703::new);
		builder.addFixer(new EntityHorseSplitFix(schema22, true));
		Schema schema23 = builder.addSchema(704, Schema704::new);
		builder.addFixer(new BlockEntityIdFix(schema23, true));
		Schema schema24 = builder.addSchema(705, Schema705::new);
		builder.addFixer(new EntityIdFix(schema24, true));
		Schema schema25 = builder.addSchema(804, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemBannerColorFix(schema25, true));
		Schema schema26 = builder.addSchema(806, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemWaterPotionFix(schema26, false));
		Schema schema27 = builder.addSchema(808, Schema808::new);
		builder.addFixer(new ChoiceTypesFix(schema27, "added shulker box", TypeReferences.BLOCK_ENTITY));
		Schema schema28 = builder.addSchema(808, 1, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityShulkerColorFix(schema28, false));
		Schema schema29 = builder.addSchema(813, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemShulkerBoxColorFix(schema29, false));
		builder.addFixer(new BlockEntityShulkerBoxColorFix(schema29, false));
		Schema schema30 = builder.addSchema(816, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsLowerCaseLanguageFix(schema30, false));
		Schema schema31 = builder.addSchema(820, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(ItemNameFix.create(schema31, "totem item renamer", replacing("minecraft:totem", "minecraft:totem_of_undying")));
		Schema schema32 = builder.addSchema(1022, Schema1022::new);
		builder.addFixer(new WriteAndReadFix(schema32, "added shoulder entities to players", TypeReferences.PLAYER));
		Schema schema33 = builder.addSchema(1125, Schema1125::new);
		builder.addFixer(new BedBlockEntityFix(schema33, true));
		builder.addFixer(new BedItemColorFix(schema33, false));
		Schema schema34 = builder.addSchema(1344, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsKeyLwjgl3Fix(schema34, false));
		Schema schema35 = builder.addSchema(1446, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsKeyTranslationFix(schema35, false));
		Schema schema36 = builder.addSchema(1450, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BlockStateStructureTemplateFix(schema36, false));
		Schema schema37 = builder.addSchema(1451, Schema1451::new);
		builder.addFixer(new ChoiceTypesFix(schema37, "AddTrappedChestFix", TypeReferences.BLOCK_ENTITY));
		Schema schema38 = builder.addSchema(1451, 1, Schema1451v1::new);
		builder.addFixer(new ChunkPalettedStorageFix(schema38, true));
		Schema schema39 = builder.addSchema(1451, 2, Schema1451v2::new);
		builder.addFixer(new BlockEntityBlockStateFix(schema39, true));
		Schema schema40 = builder.addSchema(1451, 3, Schema1451v3::new);
		builder.addFixer(new EntityBlockStateFix(schema40, true));
		builder.addFixer(new ItemInstanceMapIdFix(schema40, false));
		Schema schema41 = builder.addSchema(1451, 4, Schema1451v4::new);
		builder.addFixer(new BlockNameFlatteningFix(schema41, true));
		builder.addFixer(new ItemInstanceTheFlatteningFix(schema41, false));
		Schema schema42 = builder.addSchema(1451, 5, Schema1451v5::new);
		builder.addFixer(
			new ItemRemoveBlockEntityTagFix(
				schema42,
				false,
				Set.of(
					"minecraft:note_block",
					"minecraft:flower_pot",
					"minecraft:dandelion",
					"minecraft:poppy",
					"minecraft:blue_orchid",
					"minecraft:allium",
					"minecraft:azure_bluet",
					"minecraft:red_tulip",
					"minecraft:orange_tulip",
					"minecraft:white_tulip",
					"minecraft:pink_tulip",
					"minecraft:oxeye_daisy",
					"minecraft:cactus",
					"minecraft:brown_mushroom",
					"minecraft:red_mushroom",
					"minecraft:oak_sapling",
					"minecraft:spruce_sapling",
					"minecraft:birch_sapling",
					"minecraft:jungle_sapling",
					"minecraft:acacia_sapling",
					"minecraft:dark_oak_sapling",
					"minecraft:dead_bush",
					"minecraft:fern"
				)
			)
		);
		builder.addFixer(new ChoiceTypesFix(schema42, "RemoveNoteBlockFlowerPotFix", TypeReferences.BLOCK_ENTITY));
		builder.addFixer(new ItemInstanceSpawnEggFix(schema42, false, "minecraft:spawn_egg"));
		builder.addFixer(new EntityWolfColorFix(schema42, false));
		builder.addFixer(new BlockEntityBannerColorFix(schema42, false));
		builder.addFixer(new LevelFlatGeneratorInfoFix(schema42, false));
		Schema schema43 = builder.addSchema(1451, 6, Schema1451v6::new);
		builder.addFixer(new StatsCounterFix(schema43, true));
		builder.addFixer(new WriteAndReadFix(schema43, "Rewrite objectives", TypeReferences.OBJECTIVE));
		builder.addFixer(new BlockEntityJukeboxFix(schema43, false));
		Schema schema44 = builder.addSchema(1451, 7, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new VillagerTradeFix(schema44, false));
		Schema schema45 = builder.addSchema(1456, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityItemFrameDirectionFix(schema45, false));
		Schema schema46 = builder.addSchema(1458, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityCustomNameToTextFix(schema46, false));
		builder.addFixer(new ItemCustomNameToComponentFix(schema46, false));
		builder.addFixer(new BlockEntityCustomNameToTextFix(schema46, false));
		Schema schema47 = builder.addSchema(1460, Schema1460::new);
		builder.addFixer(new EntityPaintingMotiveFix(schema47, false));
		Schema schema48 = builder.addSchema(1466, Schema1466::new);
		builder.addFixer(new ChunkToProtoChunkFix(schema48, true));
		Schema schema49 = builder.addSchema(1470, Schema1470::new);
		builder.addFixer(new ChoiceTypesFix(schema49, "Add 1.13 entities fix", TypeReferences.ENTITY));
		Schema schema50 = builder.addSchema(1474, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ColorlessShulkerEntityFix(schema50, false));
		builder.addFixer(
			BlockNameFix.create(
				schema50,
				"Colorless shulker block fixer",
				id -> Objects.equals(IdentifierNormalizingSchema.normalize(id), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : id
			)
		);
		builder.addFixer(
			ItemNameFix.create(
				schema50,
				"Colorless shulker item fixer",
				id -> Objects.equals(IdentifierNormalizingSchema.normalize(id), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : id
			)
		);
		Schema schema51 = builder.addSchema(1475, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			BlockNameFix.create(
				schema51, "Flowing fixer", replacing(ImmutableMap.of("minecraft:flowing_water", "minecraft:water", "minecraft:flowing_lava", "minecraft:lava"))
			)
		);
		Schema schema52 = builder.addSchema(1480, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(BlockNameFix.create(schema52, "Rename coral blocks", replacing(LegacyCoralBlockMapping.MAP)));
		builder.addFixer(ItemNameFix.create(schema52, "Rename coral items", replacing(LegacyCoralBlockMapping.MAP)));
		Schema schema53 = builder.addSchema(1481, Schema1481::new);
		builder.addFixer(new ChoiceTypesFix(schema53, "Add conduit", TypeReferences.BLOCK_ENTITY));
		Schema schema54 = builder.addSchema(1483, Schema1483::new);
		builder.addFixer(new EntityPufferfishRenameFix(schema54, true));
		builder.addFixer(ItemNameFix.create(schema54, "Rename pufferfish egg item", replacing(EntityPufferfishRenameFix.RENAMED_FISH)));
		Schema schema55 = builder.addSchema(1484, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(
				schema55,
				"Rename seagrass items",
				replacing(ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass"))
			)
		);
		builder.addFixer(
			BlockNameFix.create(
				schema55,
				"Rename seagrass blocks",
				replacing(ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass"))
			)
		);
		builder.addFixer(new HeightmapRenamingFix(schema55, false));
		Schema schema56 = builder.addSchema(1486, Schema1486::new);
		builder.addFixer(new EntityCodSalmonFix(schema56, true));
		builder.addFixer(ItemNameFix.create(schema56, "Rename cod/salmon egg items", replacing(EntityCodSalmonFix.SPAWN_EGGS)));
		Schema schema57 = builder.addSchema(1487, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(
				schema57,
				"Rename prismarine_brick(s)_* blocks",
				replacing(
					ImmutableMap.of(
						"minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"
					)
				)
			)
		);
		builder.addFixer(
			BlockNameFix.create(
				schema57,
				"Rename prismarine_brick(s)_* items",
				replacing(
					ImmutableMap.of(
						"minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"
					)
				)
			)
		);
		Schema schema58 = builder.addSchema(1488, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			BlockNameFix.create(
				schema58, "Rename kelp/kelptop", replacing(ImmutableMap.of("minecraft:kelp_top", "minecraft:kelp", "minecraft:kelp", "minecraft:kelp_plant"))
			)
		);
		builder.addFixer(ItemNameFix.create(schema58, "Rename kelptop", replacing("minecraft:kelp_top", "minecraft:kelp")));
		builder.addFixer(new ChoiceFix(schema58, false, "Command block block entity custom name fix", TypeReferences.BLOCK_ENTITY, "minecraft:command_block") {
			@Override
			protected Typed<?> transform(Typed<?> inputType) {
				return inputType.update(DSL.remainderFinder(), EntityCustomNameToTextFix::fixCustomName);
			}
		});
		builder.addFixer(new ChoiceFix(schema58, false, "Command block minecart custom name fix", TypeReferences.ENTITY, "minecraft:commandblock_minecart") {
			@Override
			protected Typed<?> transform(Typed<?> inputType) {
				return inputType.update(DSL.remainderFinder(), EntityCustomNameToTextFix::fixCustomName);
			}
		});
		builder.addFixer(new IglooMetadataRemovalFix(schema58, false));
		Schema schema59 = builder.addSchema(1490, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(BlockNameFix.create(schema59, "Rename melon_block", replacing("minecraft:melon_block", "minecraft:melon")));
		builder.addFixer(
			ItemNameFix.create(
				schema59,
				"Rename melon_block/melon/speckled_melon",
				replacing(
					ImmutableMap.of(
						"minecraft:melon_block", "minecraft:melon", "minecraft:melon", "minecraft:melon_slice", "minecraft:speckled_melon", "minecraft:glistering_melon_slice"
					)
				)
			)
		);
		Schema schema60 = builder.addSchema(1492, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ChunkStructuresTemplateRenameFix(schema60, false));
		Schema schema61 = builder.addSchema(1494, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemStackEnchantmentFix(schema61, false));
		Schema schema62 = builder.addSchema(1496, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new LeavesFix(schema62, false));
		Schema schema63 = builder.addSchema(1500, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BlockEntityKeepPackedFix(schema63, false));
		Schema schema64 = builder.addSchema(1501, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new AdvancementsFix(schema64, false));
		Schema schema65 = builder.addSchema(1502, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new GameEventRenamesFix(schema65, "Recipes fix", TypeReferences.RECIPE, replacing(FlatteningRecipeMapping.RECIPES)));
		Schema schema66 = builder.addSchema(1506, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new LevelDataGeneratorOptionsFix(schema66, false));
		Schema schema67 = builder.addSchema(1510, Schema1510::new);
		builder.addFixer(BlockNameFix.create(schema67, "Block renamening fix", replacing(EntityTheRenameningBlockFix.BLOCKS)));
		builder.addFixer(ItemNameFix.create(schema67, "Item renamening fix", replacing(EntityTheRenameningBlockFix.ITEMS)));
		builder.addFixer(new GameEventRenamesFix(schema67, "Recipes renamening fix", TypeReferences.RECIPE, replacing(WoodRecipeMapping.RECIPES)));
		builder.addFixer(new EntityTheRenameningBlockFix(schema67, true));
		builder.addFixer(
			new StatsRenameFix(
				schema67,
				"SwimStatsRenameFix",
				ImmutableMap.of("minecraft:swim_one_cm", "minecraft:walk_on_water_one_cm", "minecraft:dive_one_cm", "minecraft:walk_under_water_one_cm")
			)
		);
		Schema schema68 = builder.addSchema(1514, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ObjectiveDisplayNameFix(schema68, false));
		builder.addFixer(new TeamDisplayNameFix(schema68, false));
		builder.addFixer(new ObjectiveRenderTypeFix(schema68, false));
		Schema schema69 = builder.addSchema(1515, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(BlockNameFix.create(schema69, "Rename coral fan blocks", replacing(LegacyCoralFanBlockMapping.MAP)));
		Schema schema70 = builder.addSchema(1624, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new AddTrappedChestFix(schema70, false));
		Schema schema71 = builder.addSchema(1800, Schema1800::new);
		builder.addFixer(new ChoiceTypesFix(schema71, "Added 1.14 mobs fix", TypeReferences.ENTITY));
		builder.addFixer(ItemNameFix.create(schema71, "Rename dye items", replacing(LegacyDyeItemMapping.MAP)));
		Schema schema72 = builder.addSchema(1801, Schema1801::new);
		builder.addFixer(new ChoiceTypesFix(schema72, "Added Illager Beast", TypeReferences.ENTITY));
		Schema schema73 = builder.addSchema(1802, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			BlockNameFix.create(
				schema73,
				"Rename sign blocks & stone slabs",
				replacing(
					ImmutableMap.of(
						"minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign", "minecraft:wall_sign", "minecraft:oak_wall_sign"
					)
				)
			)
		);
		builder.addFixer(
			ItemNameFix.create(
				schema73,
				"Rename sign item & stone slabs",
				replacing(ImmutableMap.of("minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign"))
			)
		);
		Schema schema74 = builder.addSchema(1803, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemLoreToTextFix(schema74, false));
		Schema schema75 = builder.addSchema(1904, Schema1904::new);
		builder.addFixer(new ChoiceTypesFix(schema75, "Added Cats", TypeReferences.ENTITY));
		builder.addFixer(new EntityCatSplitFix(schema75, false));
		Schema schema76 = builder.addSchema(1905, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ChunkStatusFix(schema76, false));
		Schema schema77 = builder.addSchema(1906, Schema1906::new);
		builder.addFixer(new ChoiceTypesFix(schema77, "Add POI Blocks", TypeReferences.BLOCK_ENTITY));
		Schema schema78 = builder.addSchema(1909, Schema1909::new);
		builder.addFixer(new ChoiceTypesFix(schema78, "Add jigsaw", TypeReferences.BLOCK_ENTITY));
		Schema schema79 = builder.addSchema(1911, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ChunkStatusFix2(schema79, false));
		Schema schema80 = builder.addSchema(1914, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new WeaponsmithChestLootTableFix(schema80, false));
		Schema schema81 = builder.addSchema(1917, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new CatTypeFix(schema81, false));
		Schema schema82 = builder.addSchema(1918, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new VillagerProfessionFix(schema82, "minecraft:villager"));
		builder.addFixer(new VillagerProfessionFix(schema82, "minecraft:zombie_villager"));
		Schema schema83 = builder.addSchema(1920, Schema1920::new);
		builder.addFixer(new NewVillageFix(schema83, false));
		builder.addFixer(new ChoiceTypesFix(schema83, "Add campfire", TypeReferences.BLOCK_ENTITY));
		Schema schema84 = builder.addSchema(1925, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new MapIdFix(schema84, false));
		Schema schema85 = builder.addSchema(1928, Schema1928::new);
		builder.addFixer(new EntityRavagerRenameFix(schema85, true));
		builder.addFixer(ItemNameFix.create(schema85, "Rename ravager egg item", replacing(EntityRavagerRenameFix.ITEMS)));
		Schema schema86 = builder.addSchema(1929, Schema1929::new);
		builder.addFixer(new ChoiceTypesFix(schema86, "Add Wandering Trader and Trader Llama", TypeReferences.ENTITY));
		Schema schema87 = builder.addSchema(1931, Schema1931::new);
		builder.addFixer(new ChoiceTypesFix(schema87, "Added Fox", TypeReferences.ENTITY));
		Schema schema88 = builder.addSchema(1936, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsAddTextBackgroundFix(schema88, false));
		Schema schema89 = builder.addSchema(1946, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new PointOfInterestReorganizationFix(schema89, false));
		Schema schema90 = builder.addSchema(1948, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OminousBannerItemRenameFix(schema90));
		Schema schema91 = builder.addSchema(1953, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OminousBannerBlockEntityRenameFix(schema91, false));
		Schema schema92 = builder.addSchema(1955, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new VillagerXpRebuildFix(schema92, false));
		builder.addFixer(new ZombieVillagerXpRebuildFix(schema92, false));
		Schema schema93 = builder.addSchema(1961, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ChunkLightRemoveFix(schema93, false));
		Schema schema94 = builder.addSchema(1963, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new RemoveGolemGossipFix(schema94, false));
		Schema schema95 = builder.addSchema(2100, Schema2100::new);
		builder.addFixer(new ChoiceTypesFix(schema95, "Added Bee and Bee Stinger", TypeReferences.ENTITY));
		builder.addFixer(new ChoiceTypesFix(schema95, "Add beehive", TypeReferences.BLOCK_ENTITY));
		builder.addFixer(new GameEventRenamesFix(schema95, "Rename sugar recipe", TypeReferences.RECIPE, replacing("minecraft:sugar", "sugar_from_sugar_cane")));
		builder.addFixer(
			new AdvancementRenameFix(
				schema95, false, "Rename sugar recipe advancement", replacing("minecraft:recipes/misc/sugar", "minecraft:recipes/misc/sugar_from_sugar_cane")
			)
		);
		Schema schema96 = builder.addSchema(2202, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BiomeFormatFix(schema96, false));
		Schema schema97 = builder.addSchema(2209, EMPTY_IDENTIFIER_NORMALIZE);
		UnaryOperator<String> unaryOperator = replacing("minecraft:bee_hive", "minecraft:beehive");
		builder.addFixer(ItemNameFix.create(schema97, "Rename bee_hive item to beehive", unaryOperator));
		builder.addFixer(new PointOfInterestRenameFix(schema97, "Rename bee_hive poi to beehive", unaryOperator));
		builder.addFixer(BlockNameFix.create(schema97, "Rename bee_hive block to beehive", unaryOperator));
		Schema schema98 = builder.addSchema(2211, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new StructureReferenceFix(schema98, false));
		Schema schema99 = builder.addSchema(2218, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new RemovePoiValidTagFix(schema99, false));
		Schema schema100 = builder.addSchema(2501, Schema2501::new);
		builder.addFixer(new FurnaceRecipesFix(schema100, true));
		Schema schema101 = builder.addSchema(2502, Schema2502::new);
		builder.addFixer(new ChoiceTypesFix(schema101, "Added Hoglin", TypeReferences.ENTITY));
		Schema schema102 = builder.addSchema(2503, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new WallPropertyFix(schema102, false));
		builder.addFixer(
			new AdvancementRenameFix(
				schema102, false, "Composter category change", replacing("minecraft:recipes/misc/composter", "minecraft:recipes/decorations/composter")
			)
		);
		Schema schema103 = builder.addSchema(2505, Schema2505::new);
		builder.addFixer(new ChoiceTypesFix(schema103, "Added Piglin", TypeReferences.ENTITY));
		builder.addFixer(new MemoryExpiryDataFix(schema103, "minecraft:villager"));
		Schema schema104 = builder.addSchema(2508, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(
				schema104,
				"Renamed fungi items to fungus",
				replacing(ImmutableMap.of("minecraft:warped_fungi", "minecraft:warped_fungus", "minecraft:crimson_fungi", "minecraft:crimson_fungus"))
			)
		);
		builder.addFixer(
			BlockNameFix.create(
				schema104,
				"Renamed fungi blocks to fungus",
				replacing(ImmutableMap.of("minecraft:warped_fungi", "minecraft:warped_fungus", "minecraft:crimson_fungi", "minecraft:crimson_fungus"))
			)
		);
		Schema schema105 = builder.addSchema(2509, Schema2509::new);
		builder.addFixer(new EntityZombifiedPiglinRenameFix(schema105));
		builder.addFixer(ItemNameFix.create(schema105, "Rename zombie pigman egg item", replacing(EntityZombifiedPiglinRenameFix.RENAMES)));
		Schema schema106 = builder.addSchema(2511, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityProjectileOwnerFix(schema106));
		Schema schema107 = builder.addSchema(2514, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityUuidFix(schema107));
		builder.addFixer(new BlockEntityUuidFix(schema107));
		builder.addFixer(new PlayerUuidFix(schema107));
		builder.addFixer(new WorldUuidFix(schema107));
		builder.addFixer(new PersistentStateUuidFix(schema107));
		builder.addFixer(new ItemStackUuidFix(schema107));
		Schema schema108 = builder.addSchema(2516, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new VillagerGossipFix(schema108, "minecraft:villager"));
		builder.addFixer(new VillagerGossipFix(schema108, "minecraft:zombie_villager"));
		Schema schema109 = builder.addSchema(2518, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new JigsawPropertiesFix(schema109, false));
		builder.addFixer(new JigsawRotationFix(schema109, false));
		Schema schema110 = builder.addSchema(2519, Schema2519::new);
		builder.addFixer(new ChoiceTypesFix(schema110, "Added Strider", TypeReferences.ENTITY));
		Schema schema111 = builder.addSchema(2522, Schema2522::new);
		builder.addFixer(new ChoiceTypesFix(schema111, "Added Zoglin", TypeReferences.ENTITY));
		Schema schema112 = builder.addSchema(2523, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new RenameItemStackAttributesFix(schema112));
		Schema schema113 = builder.addSchema(2527, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BitStorageAlignFix(schema113));
		Schema schema114 = builder.addSchema(2528, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(
				schema114,
				"Rename soul fire torch and soul fire lantern",
				replacing(ImmutableMap.of("minecraft:soul_fire_torch", "minecraft:soul_torch", "minecraft:soul_fire_lantern", "minecraft:soul_lantern"))
			)
		);
		builder.addFixer(
			BlockNameFix.create(
				schema114,
				"Rename soul fire torch and soul fire lantern",
				replacing(
					ImmutableMap.of(
						"minecraft:soul_fire_torch",
						"minecraft:soul_torch",
						"minecraft:soul_fire_wall_torch",
						"minecraft:soul_wall_torch",
						"minecraft:soul_fire_lantern",
						"minecraft:soul_lantern"
					)
				)
			)
		);
		Schema schema115 = builder.addSchema(2529, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new StriderGravityFix(schema115, false));
		Schema schema116 = builder.addSchema(2531, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new RedstoneConnectionsFix(schema116));
		Schema schema117 = builder.addSchema(2533, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new VillagerFollowRangeFix(schema117));
		Schema schema118 = builder.addSchema(2535, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityShulkerRotationFix(schema118));
		Schema schema119 = builder.addSchema(2550, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new StructureSeparationDataFix(schema119));
		Schema schema120 = builder.addSchema(2551, Schema2551::new);
		builder.addFixer(new WriteAndReadFix(schema120, "add types to WorldGenData", TypeReferences.WORLD_GEN_SETTINGS));
		Schema schema121 = builder.addSchema(2552, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new GameEventRenamesFix(schema121, "Nether biome rename", TypeReferences.BIOME, replacing("minecraft:nether", "minecraft:nether_wastes")));
		Schema schema122 = builder.addSchema(2553, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new GameEventRenamesFix(schema122, "Biomes fix", TypeReferences.BIOME, replacing(FlatteningBiomeMapping.RENAMED_BIOMES)));
		Schema schema123 = builder.addSchema(2558, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new MissingDimensionFix(schema123, false));
		builder.addFixer(new OptionFix(schema123, false, "Rename swapHands setting", "key_key.swapHands", "key_key.swapOffhand"));
		Schema schema124 = builder.addSchema(2568, Schema2568::new);
		builder.addFixer(new ChoiceTypesFix(schema124, "Added Piglin Brute", TypeReferences.ENTITY));
		Schema schema125 = builder.addSchema(2571, Schema2571::new);
		builder.addFixer(new ChoiceTypesFix(schema125, "Added Goat", TypeReferences.ENTITY));
		Schema schema126 = builder.addSchema(2679, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new CauldronRenameFix(schema126, false));
		Schema schema127 = builder.addSchema(2680, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(ItemNameFix.create(schema127, "Renamed grass path item to dirt path", replacing("minecraft:grass_path", "minecraft:dirt_path")));
		builder.addFixer(JigsawBlockNameFix.create(schema127, "Renamed grass path block to dirt path", replacing("minecraft:grass_path", "minecraft:dirt_path")));
		Schema schema128 = builder.addSchema(2684, Schema2684::new);
		builder.addFixer(new ChoiceTypesFix(schema128, "Added Sculk Sensor", TypeReferences.BLOCK_ENTITY));
		Schema schema129 = builder.addSchema(2686, Schema2686::new);
		builder.addFixer(new ChoiceTypesFix(schema129, "Added Axolotl", TypeReferences.ENTITY));
		Schema schema130 = builder.addSchema(2688, Schema2688::new);
		builder.addFixer(new ChoiceTypesFix(schema130, "Added Glow Squid", TypeReferences.ENTITY));
		builder.addFixer(new ChoiceTypesFix(schema130, "Added Glow Item Frame", TypeReferences.ENTITY));
		Schema schema131 = builder.addSchema(2690, EMPTY_IDENTIFIER_NORMALIZE);
		ImmutableMap<String, String> immutableMap = ImmutableMap.<String, String>builder()
			.put("minecraft:weathered_copper_block", "minecraft:oxidized_copper_block")
			.put("minecraft:semi_weathered_copper_block", "minecraft:weathered_copper_block")
			.put("minecraft:lightly_weathered_copper_block", "minecraft:exposed_copper_block")
			.put("minecraft:weathered_cut_copper", "minecraft:oxidized_cut_copper")
			.put("minecraft:semi_weathered_cut_copper", "minecraft:weathered_cut_copper")
			.put("minecraft:lightly_weathered_cut_copper", "minecraft:exposed_cut_copper")
			.put("minecraft:weathered_cut_copper_stairs", "minecraft:oxidized_cut_copper_stairs")
			.put("minecraft:semi_weathered_cut_copper_stairs", "minecraft:weathered_cut_copper_stairs")
			.put("minecraft:lightly_weathered_cut_copper_stairs", "minecraft:exposed_cut_copper_stairs")
			.put("minecraft:weathered_cut_copper_slab", "minecraft:oxidized_cut_copper_slab")
			.put("minecraft:semi_weathered_cut_copper_slab", "minecraft:weathered_cut_copper_slab")
			.put("minecraft:lightly_weathered_cut_copper_slab", "minecraft:exposed_cut_copper_slab")
			.put("minecraft:waxed_semi_weathered_copper", "minecraft:waxed_weathered_copper")
			.put("minecraft:waxed_lightly_weathered_copper", "minecraft:waxed_exposed_copper")
			.put("minecraft:waxed_semi_weathered_cut_copper", "minecraft:waxed_weathered_cut_copper")
			.put("minecraft:waxed_lightly_weathered_cut_copper", "minecraft:waxed_exposed_cut_copper")
			.put("minecraft:waxed_semi_weathered_cut_copper_stairs", "minecraft:waxed_weathered_cut_copper_stairs")
			.put("minecraft:waxed_lightly_weathered_cut_copper_stairs", "minecraft:waxed_exposed_cut_copper_stairs")
			.put("minecraft:waxed_semi_weathered_cut_copper_slab", "minecraft:waxed_weathered_cut_copper_slab")
			.put("minecraft:waxed_lightly_weathered_cut_copper_slab", "minecraft:waxed_exposed_cut_copper_slab")
			.build();
		builder.addFixer(ItemNameFix.create(schema131, "Renamed copper block items to new oxidized terms", replacing(immutableMap)));
		builder.addFixer(JigsawBlockNameFix.create(schema131, "Renamed copper blocks to new oxidized terms", replacing(immutableMap)));
		Schema schema132 = builder.addSchema(2691, EMPTY_IDENTIFIER_NORMALIZE);
		ImmutableMap<String, String> immutableMap2 = ImmutableMap.<String, String>builder()
			.put("minecraft:waxed_copper", "minecraft:waxed_copper_block")
			.put("minecraft:oxidized_copper_block", "minecraft:oxidized_copper")
			.put("minecraft:weathered_copper_block", "minecraft:weathered_copper")
			.put("minecraft:exposed_copper_block", "minecraft:exposed_copper")
			.build();
		builder.addFixer(ItemNameFix.create(schema132, "Rename copper item suffixes", replacing(immutableMap2)));
		builder.addFixer(JigsawBlockNameFix.create(schema132, "Rename copper blocks suffixes", replacing(immutableMap2)));
		Schema schema133 = builder.addSchema(2693, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new AddFlagIfNotPresentFix(schema133, TypeReferences.WORLD_GEN_SETTINGS, "has_increased_height_already", false));
		Schema schema134 = builder.addSchema(2696, EMPTY_IDENTIFIER_NORMALIZE);
		ImmutableMap<String, String> immutableMap3 = ImmutableMap.<String, String>builder()
			.put("minecraft:grimstone", "minecraft:deepslate")
			.put("minecraft:grimstone_slab", "minecraft:cobbled_deepslate_slab")
			.put("minecraft:grimstone_stairs", "minecraft:cobbled_deepslate_stairs")
			.put("minecraft:grimstone_wall", "minecraft:cobbled_deepslate_wall")
			.put("minecraft:polished_grimstone", "minecraft:polished_deepslate")
			.put("minecraft:polished_grimstone_slab", "minecraft:polished_deepslate_slab")
			.put("minecraft:polished_grimstone_stairs", "minecraft:polished_deepslate_stairs")
			.put("minecraft:polished_grimstone_wall", "minecraft:polished_deepslate_wall")
			.put("minecraft:grimstone_tiles", "minecraft:deepslate_tiles")
			.put("minecraft:grimstone_tile_slab", "minecraft:deepslate_tile_slab")
			.put("minecraft:grimstone_tile_stairs", "minecraft:deepslate_tile_stairs")
			.put("minecraft:grimstone_tile_wall", "minecraft:deepslate_tile_wall")
			.put("minecraft:grimstone_bricks", "minecraft:deepslate_bricks")
			.put("minecraft:grimstone_brick_slab", "minecraft:deepslate_brick_slab")
			.put("minecraft:grimstone_brick_stairs", "minecraft:deepslate_brick_stairs")
			.put("minecraft:grimstone_brick_wall", "minecraft:deepslate_brick_wall")
			.put("minecraft:chiseled_grimstone", "minecraft:chiseled_deepslate")
			.build();
		builder.addFixer(ItemNameFix.create(schema134, "Renamed grimstone block items to deepslate", replacing(immutableMap3)));
		builder.addFixer(JigsawBlockNameFix.create(schema134, "Renamed grimstone blocks to deepslate", replacing(immutableMap3)));
		Schema schema135 = builder.addSchema(2700, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			JigsawBlockNameFix.create(
				schema135,
				"Renamed cave vines blocks",
				replacing(ImmutableMap.of("minecraft:cave_vines_head", "minecraft:cave_vines", "minecraft:cave_vines_body", "minecraft:cave_vines_plant"))
			)
		);
		Schema schema136 = builder.addSchema(2701, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new StructureFeatureChildrenPoolElementFix(schema136));
		Schema schema137 = builder.addSchema(2702, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ArrowPickupFix(schema137));
		Schema schema138 = builder.addSchema(2704, Schema2704::new);
		builder.addFixer(new ChoiceTypesFix(schema138, "Added Goat", TypeReferences.ENTITY));
		Schema schema139 = builder.addSchema(2707, Schema2707::new);
		builder.addFixer(new ChoiceTypesFix(schema139, "Added Marker", TypeReferences.ENTITY));
		builder.addFixer(new AddFlagIfNotPresentFix(schema139, TypeReferences.WORLD_GEN_SETTINGS, "has_increased_height_already", true));
		Schema schema140 = builder.addSchema(2710, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			new StatsRenameFix(schema140, "Renamed play_one_minute stat to play_time", ImmutableMap.of("minecraft:play_one_minute", "minecraft:play_time"))
		);
		Schema schema141 = builder.addSchema(2717, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(
				schema141, "Rename azalea_leaves_flowers", replacing(ImmutableMap.of("minecraft:azalea_leaves_flowers", "minecraft:flowering_azalea_leaves"))
			)
		);
		builder.addFixer(
			BlockNameFix.create(
				schema141, "Rename azalea_leaves_flowers items", replacing(ImmutableMap.of("minecraft:azalea_leaves_flowers", "minecraft:flowering_azalea_leaves"))
			)
		);
		Schema schema142 = builder.addSchema(2825, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new AddFlagIfNotPresentFix(schema142, TypeReferences.WORLD_GEN_SETTINGS, "has_increased_height_already", false));
		Schema schema143 = builder.addSchema(2831, Schema2831::new);
		builder.addFixer(new UntaggedSpawnerFix(schema143));
		Schema schema144 = builder.addSchema(2832, Schema2832::new);
		builder.addFixer(new WorldGenSettingsHeightAndBiomeFix(schema144));
		builder.addFixer(new ChunkHeightAndBiomeFix(schema144));
		Schema schema145 = builder.addSchema(2833, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new WorldGenSettingsDisallowOldCustomWorldsFix(schema145));
		Schema schema146 = builder.addSchema(2838, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new GameEventRenamesFix(schema146, "Caves and Cliffs biome renames", TypeReferences.BIOME, replacing(LegacyBiomeMapping.MAP)));
		Schema schema147 = builder.addSchema(2841, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ProtoChunkTickListFix(schema147));
		Schema schema148 = builder.addSchema(2842, Schema2842::new);
		builder.addFixer(new ChunkLevelTagRenameFix(schema148));
		Schema schema149 = builder.addSchema(2843, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new TicksInWrongChunkFix(schema149));
		builder.addFixer(
			new GameEventRenamesFix(schema149, "Remove Deep Warm Ocean", TypeReferences.BIOME, replacing("minecraft:deep_warm_ocean", "minecraft:warm_ocean"))
		);
		Schema schema150 = builder.addSchema(2846, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			new AdvancementRenameFix(
				schema150,
				false,
				"Rename some C&C part 2 advancements",
				replacing(
					ImmutableMap.of(
						"minecraft:husbandry/play_jukebox_in_meadows",
						"minecraft:adventure/play_jukebox_in_meadows",
						"minecraft:adventure/caves_and_cliff",
						"minecraft:adventure/fall_from_world_height",
						"minecraft:adventure/ride_strider_in_overworld_lava",
						"minecraft:nether/ride_strider_in_overworld_lava"
					)
				)
			)
		);
		Schema schema151 = builder.addSchema(2852, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new WorldGenSettingsDisallowOldCustomWorldsFix(schema151));
		Schema schema152 = builder.addSchema(2967, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new StructureSettingsFlattenFix(schema152));
		Schema schema153 = builder.addSchema(2970, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new StructuresToConfiguredStructuresFix(schema153));
		Schema schema154 = builder.addSchema(3076, Schema3076::new);
		builder.addFixer(new ChoiceTypesFix(schema154, "Added Sculk Catalyst", TypeReferences.BLOCK_ENTITY));
		Schema schema155 = builder.addSchema(3077, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ChunkDeleteIgnoredLightDataFix(schema155));
		Schema schema156 = builder.addSchema(3078, Schema3078::new);
		builder.addFixer(new ChoiceTypesFix(schema156, "Added Frog", TypeReferences.ENTITY));
		builder.addFixer(new ChoiceTypesFix(schema156, "Added Tadpole", TypeReferences.ENTITY));
		builder.addFixer(new ChoiceTypesFix(schema156, "Added Sculk Shrieker", TypeReferences.BLOCK_ENTITY));
		Schema schema157 = builder.addSchema(3081, Schema3081::new);
		builder.addFixer(new ChoiceTypesFix(schema157, "Added Warden", TypeReferences.ENTITY));
		Schema schema158 = builder.addSchema(3082, Schema3082::new);
		builder.addFixer(new ChoiceTypesFix(schema158, "Added Chest Boat", TypeReferences.ENTITY));
		Schema schema159 = builder.addSchema(3083, Schema3083::new);
		builder.addFixer(new ChoiceTypesFix(schema159, "Added Allay", TypeReferences.ENTITY));
		Schema schema160 = builder.addSchema(3084, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			new GameEventRenamesFix(
				schema160,
				"game_event_renames_3084",
				TypeReferences.GAME_EVENT_NAME,
				replacing(
					ImmutableMap.<String, String>builder()
						.put("minecraft:block_press", "minecraft:block_activate")
						.put("minecraft:block_switch", "minecraft:block_activate")
						.put("minecraft:block_unpress", "minecraft:block_deactivate")
						.put("minecraft:block_unswitch", "minecraft:block_deactivate")
						.put("minecraft:drinking_finish", "minecraft:drink")
						.put("minecraft:elytra_free_fall", "minecraft:elytra_glide")
						.put("minecraft:entity_damaged", "minecraft:entity_damage")
						.put("minecraft:entity_dying", "minecraft:entity_die")
						.put("minecraft:entity_killed", "minecraft:entity_die")
						.put("minecraft:mob_interact", "minecraft:entity_interact")
						.put("minecraft:ravager_roar", "minecraft:entity_roar")
						.put("minecraft:ring_bell", "minecraft:block_change")
						.put("minecraft:shulker_close", "minecraft:container_close")
						.put("minecraft:shulker_open", "minecraft:container_open")
						.put("minecraft:wolf_shaking", "minecraft:entity_shake")
						.build()
				)
			)
		);
		Schema schema161 = builder.addSchema(3086, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			new EntityVariantTypeFix(
				schema161, "Change cat variant type", TypeReferences.ENTITY, "minecraft:cat", "CatType", Util.make(new Int2ObjectOpenHashMap(), catVariants -> {
					catVariants.defaultReturnValue("minecraft:tabby");
					catVariants.put(0, "minecraft:tabby");
					catVariants.put(1, "minecraft:black");
					catVariants.put(2, "minecraft:red");
					catVariants.put(3, "minecraft:siamese");
					catVariants.put(4, "minecraft:british");
					catVariants.put(5, "minecraft:calico");
					catVariants.put(6, "minecraft:persian");
					catVariants.put(7, "minecraft:ragdoll");
					catVariants.put(8, "minecraft:white");
					catVariants.put(9, "minecraft:jellie");
					catVariants.put(10, "minecraft:all_black");
				})::get
			)
		);
		ImmutableMap<String, String> immutableMap4 = ImmutableMap.<String, String>builder()
			.put("textures/entity/cat/tabby.png", "minecraft:tabby")
			.put("textures/entity/cat/black.png", "minecraft:black")
			.put("textures/entity/cat/red.png", "minecraft:red")
			.put("textures/entity/cat/siamese.png", "minecraft:siamese")
			.put("textures/entity/cat/british_shorthair.png", "minecraft:british")
			.put("textures/entity/cat/calico.png", "minecraft:calico")
			.put("textures/entity/cat/persian.png", "minecraft:persian")
			.put("textures/entity/cat/ragdoll.png", "minecraft:ragdoll")
			.put("textures/entity/cat/white.png", "minecraft:white")
			.put("textures/entity/cat/jellie.png", "minecraft:jellie")
			.put("textures/entity/cat/all_black.png", "minecraft:all_black")
			.build();
		builder.addFixer(
			new AdvancementCriteriaRenameFix(
				schema161, "Migrate cat variant advancement", "minecraft:husbandry/complete_catalogue", string -> immutableMap4.getOrDefault(string, string)
			)
		);
		Schema schema162 = builder.addSchema(3087, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			new EntityVariantTypeFix(
				schema162, "Change frog variant type", TypeReferences.ENTITY, "minecraft:frog", "Variant", Util.make(new Int2ObjectOpenHashMap(), frogVariants -> {
					frogVariants.put(0, "minecraft:temperate");
					frogVariants.put(1, "minecraft:warm");
					frogVariants.put(2, "minecraft:cold");
				})::get
			)
		);
		Schema schema163 = builder.addSchema(3090, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new EntityPaintingFieldsRenameFix(schema163));
		Schema schema164 = builder.addSchema(3093, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new GoatMissingStateFix(schema164));
		Schema schema165 = builder.addSchema(3094, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new GoatHornIdFix(schema165));
		Schema schema166 = builder.addSchema(3097, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new RemoveFilteredBookTextFix(schema166));
		builder.addFixer(new RemoveFilteredSignTextFix(schema166));
		Map<String, String> map = Map.of("minecraft:british", "minecraft:british_shorthair");
		builder.addFixer(new RenameVariantsFix(schema166, "Rename british shorthair", TypeReferences.ENTITY, "minecraft:cat", map));
		builder.addFixer(
			new AdvancementCriteriaRenameFix(
				schema166,
				"Migrate cat variant advancement for british shorthair",
				"minecraft:husbandry/complete_catalogue",
				string -> (String)map.getOrDefault(string, string)
			)
		);
		builder.addFixer(
			new PointOfInterestRemoveFix(schema166, "Remove unpopulated villager PoI types", Set.of("minecraft:unemployed", "minecraft:nitwit")::contains)
		);
		Schema schema167 = builder.addSchema(3108, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BlendingDataRemoveFromNetherEndFix(schema167));
		Schema schema168 = builder.addSchema(3201, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsProgrammerArtFix(schema168));
		Schema schema169 = builder.addSchema(3202, Schema3202::new);
		builder.addFixer(new ChoiceTypesFix(schema169, "Added Hanging Sign", TypeReferences.BLOCK_ENTITY));
		Schema schema170 = builder.addSchema(3203, Schema3203::new);
		builder.addFixer(new ChoiceTypesFix(schema170, "Added Camel", TypeReferences.ENTITY));
		Schema schema171 = builder.addSchema(3204, Schema3204::new);
		builder.addFixer(new ChoiceTypesFix(schema171, "Added Chiseled Bookshelf", TypeReferences.BLOCK_ENTITY));
		Schema schema172 = builder.addSchema(3209, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ItemInstanceSpawnEggFix(schema172, false, "minecraft:pig_spawn_egg"));
		Schema schema173 = builder.addSchema(3214, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsAmbientOcclusionFix(schema173));
		Schema schema174 = builder.addSchema(3319, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new OptionsAccessibilityOnboardFix(schema174));
		Schema schema175 = builder.addSchema(3322, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new StatusEffectDurationFix(schema175));
		Schema schema176 = builder.addSchema(3325, Schema3325::new);
		builder.addFixer(new ChoiceTypesFix(schema176, "Added displays", TypeReferences.ENTITY));
		Schema schema177 = builder.addSchema(3326, Schema3326::new);
		builder.addFixer(new ChoiceTypesFix(schema177, "Added Sniffer", TypeReferences.ENTITY));
		Schema schema178 = builder.addSchema(3327, Schema3327::new);
		builder.addFixer(new ChoiceTypesFix(schema178, "Archaeology", TypeReferences.BLOCK_ENTITY));
		Schema schema179 = builder.addSchema(3328, Schema3328::new);
		builder.addFixer(new ChoiceTypesFix(schema179, "Added interaction", TypeReferences.ENTITY));
		Schema schema180 = builder.addSchema(3438, Schema3438::new);
		builder.addFixer(
			RenameBlockEntityFix.create(schema180, "Rename Suspicious Sand to Brushable Block", replacing("minecraft:suspicious_sand", "minecraft:brushable_block"))
		);
		builder.addFixer(new EntityBrushableBlockFieldsRenameFix(schema180));
		builder.addFixer(
			ItemNameFix.create(
				schema180,
				"Pottery shard renaming",
				replacing(
					ImmutableMap.of(
						"minecraft:pottery_shard_archer",
						"minecraft:archer_pottery_shard",
						"minecraft:pottery_shard_prize",
						"minecraft:prize_pottery_shard",
						"minecraft:pottery_shard_arms_up",
						"minecraft:arms_up_pottery_shard",
						"minecraft:pottery_shard_skull",
						"minecraft:skull_pottery_shard"
					)
				)
			)
		);
		builder.addFixer(new ChoiceTypesFix(schema180, "Added calibrated sculk sensor", TypeReferences.BLOCK_ENTITY));
		Schema schema181 = builder.addSchema(3439, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new UpdateSignTextFormatFix(schema181, "Updated sign text format for Signs", "minecraft:sign"));
		builder.addFixer(new UpdateSignTextFormatFix(schema181, "Updated sign text format for Hanging Signs", "minecraft:hanging_sign"));
		Schema schema182 = builder.addSchema(3440, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			new GameEventRenamesFix(
				schema182,
				"Replace experimental 1.20 overworld",
				TypeReferences.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST,
				replacing("minecraft:overworld_update_1_20", "minecraft:overworld")
			)
		);
		builder.addFixer(new RemoveFeatureTogglesFix(schema182, "Remove 1.20 feature toggle", Set.of("minecraft:update_1_20")));
		Schema schema183 = builder.addSchema(3441, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new BlendingDataFix(schema183));
		Schema schema184 = builder.addSchema(3447, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			ItemNameFix.create(
				schema184,
				"Pottery shard item renaming to Pottery sherd",
				replacing(
					(Map<String, String>)Stream.of(
							"minecraft:angler_pottery_shard",
							"minecraft:archer_pottery_shard",
							"minecraft:arms_up_pottery_shard",
							"minecraft:blade_pottery_shard",
							"minecraft:brewer_pottery_shard",
							"minecraft:burn_pottery_shard",
							"minecraft:danger_pottery_shard",
							"minecraft:explorer_pottery_shard",
							"minecraft:friend_pottery_shard",
							"minecraft:heart_pottery_shard",
							"minecraft:heartbreak_pottery_shard",
							"minecraft:howl_pottery_shard",
							"minecraft:miner_pottery_shard",
							"minecraft:mourner_pottery_shard",
							"minecraft:plenty_pottery_shard",
							"minecraft:prize_pottery_shard",
							"minecraft:sheaf_pottery_shard",
							"minecraft:shelter_pottery_shard",
							"minecraft:skull_pottery_shard",
							"minecraft:snort_pottery_shard"
						)
						.collect(Collectors.toMap(Function.identity(), string -> string.replace("_pottery_shard", "_pottery_sherd")))
				)
			)
		);
		Schema schema185 = builder.addSchema(3448, Schema3448::new);
		builder.addFixer(new DecoratedPotFieldRenameFix(schema185));
		Schema schema186 = builder.addSchema(3450, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(
			new RenameChunkStatusFix(
				schema186,
				"Remove liquid_carvers and heightmap chunk statuses",
				replacing(Map.of("minecraft:liquid_carvers", "minecraft:carvers", "minecraft:heightmaps", "minecraft:spawn"))
			)
		);
		Schema schema187 = builder.addSchema(3451, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new ChunkDeleteLightFix(schema187));
		Schema schema188 = builder.addSchema(3459, EMPTY_IDENTIFIER_NORMALIZE);
		builder.addFixer(new LegacyDragonFightFix(schema188));
	}

	private static UnaryOperator<String> replacing(Map<String, String> replacements) {
		return string -> (String)replacements.getOrDefault(string, string);
	}

	private static UnaryOperator<String> replacing(String old, String current) {
		return string -> Objects.equals(string, old) ? current : string;
	}
}
