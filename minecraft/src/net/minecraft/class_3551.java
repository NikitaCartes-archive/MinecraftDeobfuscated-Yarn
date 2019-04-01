package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.function.BiFunction;

public class class_3551 {
	private static final BiFunction<Integer, Schema, Schema> field_15776 = Schema::new;
	private static final BiFunction<Integer, Schema, Schema> field_15775 = class_1220::new;
	private static final DataFixer field_15777 = method_15471();

	private static DataFixer method_15471() {
		DataFixerBuilder dataFixerBuilder = new DataFixerBuilder(class_155.method_16673().getWorldVersion());
		method_15451(dataFixerBuilder);
		return dataFixerBuilder.build(class_156.method_18349());
	}

	public static DataFixer method_15450() {
		return field_15777;
	}

	private static void method_15451(DataFixerBuilder dataFixerBuilder) {
		Schema schema = dataFixerBuilder.addSchema(99, class_1254::new);
		Schema schema2 = dataFixerBuilder.addSchema(100, class_1222::new);
		dataFixerBuilder.addFixer(new class_3599(schema2, true));
		Schema schema3 = dataFixerBuilder.addSchema(101, field_15776);
		dataFixerBuilder.addFixer(new class_3577(schema3, false));
		Schema schema4 = dataFixerBuilder.addSchema(102, class_1221::new);
		dataFixerBuilder.addFixer(new class_1181(schema4, true));
		dataFixerBuilder.addFixer(new class_1183(schema4, false));
		Schema schema5 = dataFixerBuilder.addSchema(105, field_15776);
		dataFixerBuilder.addFixer(new class_1184(schema5, true));
		Schema schema6 = dataFixerBuilder.addSchema(106, class_1223::new);
		dataFixerBuilder.addFixer(new class_1198(schema6, true));
		Schema schema7 = dataFixerBuilder.addSchema(107, class_1226::new);
		dataFixerBuilder.addFixer(new class_3605(schema7, true));
		Schema schema8 = dataFixerBuilder.addSchema(108, field_15776);
		dataFixerBuilder.addFixer(new class_1171(schema8, true));
		Schema schema9 = dataFixerBuilder.addSchema(109, field_15776);
		dataFixerBuilder.addFixer(new class_3600(schema9, true));
		Schema schema10 = dataFixerBuilder.addSchema(110, field_15776);
		dataFixerBuilder.addFixer(new class_3601(schema10, true));
		Schema schema11 = dataFixerBuilder.addSchema(111, field_15776);
		dataFixerBuilder.addFixer(new class_3606(schema11, true));
		Schema schema12 = dataFixerBuilder.addSchema(113, field_15776);
		dataFixerBuilder.addFixer(new class_1165(schema12, true));
		Schema schema13 = dataFixerBuilder.addSchema(135, class_1228::new);
		dataFixerBuilder.addFixer(new class_1166(schema13, true));
		Schema schema14 = dataFixerBuilder.addSchema(143, class_1227::new);
		dataFixerBuilder.addFixer(new class_1173(schema14, true));
		Schema schema15 = dataFixerBuilder.addSchema(147, field_15776);
		dataFixerBuilder.addFixer(new class_3594(schema15, true));
		Schema schema16 = dataFixerBuilder.addSchema(165, field_15776);
		dataFixerBuilder.addFixer(new class_1194(schema16, true));
		Schema schema17 = dataFixerBuilder.addSchema(501, class_1247::new);
		dataFixerBuilder.addFixer(new class_3553(schema17, "Add 1.10 entities fix", class_1208.field_5729));
		Schema schema18 = dataFixerBuilder.addSchema(502, field_15776);
		dataFixerBuilder.addFixer(
			class_1182.method_5019(
				schema18,
				"cooked_fished item renamer",
				string -> Objects.equals(class_1220.method_5193(string), "minecraft:cooked_fished") ? "minecraft:cooked_fish" : string
			)
		);
		dataFixerBuilder.addFixer(new class_1174(schema18, false));
		Schema schema19 = dataFixerBuilder.addSchema(505, field_15776);
		dataFixerBuilder.addFixer(new class_1202(schema19, false));
		Schema schema20 = dataFixerBuilder.addSchema(700, class_1246::new);
		dataFixerBuilder.addFixer(new class_3598(schema20, true));
		Schema schema21 = dataFixerBuilder.addSchema(701, class_1249::new);
		dataFixerBuilder.addFixer(new class_1168(schema21, true));
		Schema schema22 = dataFixerBuilder.addSchema(702, class_1248::new);
		dataFixerBuilder.addFixer(new class_1175(schema22, true));
		Schema schema23 = dataFixerBuilder.addSchema(703, class_1251::new);
		dataFixerBuilder.addFixer(new class_3602(schema23, true));
		Schema schema24 = dataFixerBuilder.addSchema(704, class_1250::new);
		dataFixerBuilder.addFixer(new class_3571(schema24, true));
		Schema schema25 = dataFixerBuilder.addSchema(705, class_1253::new);
		dataFixerBuilder.addFixer(new class_3603(schema25, true));
		Schema schema26 = dataFixerBuilder.addSchema(804, field_15775);
		dataFixerBuilder.addFixer(new class_1179(schema26, true));
		Schema schema27 = dataFixerBuilder.addSchema(806, field_15775);
		dataFixerBuilder.addFixer(new class_1190(schema27, false));
		Schema schema28 = dataFixerBuilder.addSchema(808, class_1252::new);
		dataFixerBuilder.addFixer(new class_3553(schema28, "added shulker box", class_1208.field_5727));
		Schema schema29 = dataFixerBuilder.addSchema(808, 1, field_15775);
		dataFixerBuilder.addFixer(new class_1169(schema29, false));
		Schema schema30 = dataFixerBuilder.addSchema(813, field_15775);
		dataFixerBuilder.addFixer(new class_1185(schema30, false));
		dataFixerBuilder.addFixer(new class_3575(schema30, false));
		Schema schema31 = dataFixerBuilder.addSchema(816, field_15775);
		dataFixerBuilder.addFixer(new class_1203(schema31, false));
		Schema schema32 = dataFixerBuilder.addSchema(820, field_15775);
		dataFixerBuilder.addFixer(
			class_1182.method_5019(schema32, "totem item renamer", string -> Objects.equals(string, "minecraft:totem") ? "minecraft:totem_of_undying" : string)
		);
		Schema schema33 = dataFixerBuilder.addSchema(1022, class_1224::new);
		dataFixerBuilder.addFixer(new class_1218(schema33, "added shoulder entities to players", class_1208.field_5715));
		Schema schema34 = dataFixerBuilder.addSchema(1125, class_1225::new);
		dataFixerBuilder.addFixer(new class_3557(schema34, true));
		dataFixerBuilder.addFixer(new class_3559(schema34, false));
		Schema schema35 = dataFixerBuilder.addSchema(1344, field_15775);
		dataFixerBuilder.addFixer(new class_1201(schema35, false));
		Schema schema36 = dataFixerBuilder.addSchema(1446, field_15775);
		dataFixerBuilder.addFixer(new class_1204(schema36, false));
		Schema schema37 = dataFixerBuilder.addSchema(1450, field_15775);
		dataFixerBuilder.addFixer(new class_3581(schema37, false));
		Schema schema38 = dataFixerBuilder.addSchema(1451, class_1230::new);
		dataFixerBuilder.addFixer(new class_3553(schema38, "AddTrappedChestFix", class_1208.field_5727));
		Schema schema39 = dataFixerBuilder.addSchema(1451, 1, class_1229::new);
		dataFixerBuilder.addFixer(new class_3582(schema39, true));
		Schema schema40 = dataFixerBuilder.addSchema(1451, 2, class_1232::new);
		dataFixerBuilder.addFixer(new class_3566(schema40, true));
		Schema schema41 = dataFixerBuilder.addSchema(1451, 3, class_1231::new);
		dataFixerBuilder.addFixer(new class_3595(schema41, true));
		dataFixerBuilder.addFixer(new class_1186(schema41, false));
		Schema schema42 = dataFixerBuilder.addSchema(1451, 4, class_1234::new);
		dataFixerBuilder.addFixer(new class_3578(schema42, true));
		dataFixerBuilder.addFixer(new class_1188(schema42, false));
		Schema schema43 = dataFixerBuilder.addSchema(1451, 5, class_1233::new);
		dataFixerBuilder.addFixer(new class_3553(schema43, "RemoveNoteBlockFlowerPotFix", class_1208.field_5727));
		dataFixerBuilder.addFixer(new class_1189(schema43, false));
		dataFixerBuilder.addFixer(new class_1172(schema43, false));
		dataFixerBuilder.addFixer(new class_3564(schema43, false));
		dataFixerBuilder.addFixer(new class_1195(schema43, false));
		Schema schema44 = dataFixerBuilder.addSchema(1451, 6, class_1236::new);
		dataFixerBuilder.addFixer(new class_1214(schema44, true));
		dataFixerBuilder.addFixer(new class_3573(schema44, false));
		Schema schema45 = dataFixerBuilder.addSchema(1451, 7, class_1235::new);
		dataFixerBuilder.addFixer(new class_1209(schema45, true));
		Schema schema46 = dataFixerBuilder.addSchema(1451, 7, field_15775);
		dataFixerBuilder.addFixer(new class_1219(schema46, false));
		Schema schema47 = dataFixerBuilder.addSchema(1456, field_15775);
		dataFixerBuilder.addFixer(new class_3604(schema47, false));
		Schema schema48 = dataFixerBuilder.addSchema(1458, field_15775);
		dataFixerBuilder.addFixer(new class_3597(schema48, false));
		dataFixerBuilder.addFixer(new class_1178(schema48, false));
		dataFixerBuilder.addFixer(new class_3567(schema48, false));
		Schema schema49 = dataFixerBuilder.addSchema(1460, class_1238::new);
		dataFixerBuilder.addFixer(new class_3607(schema49, false));
		Schema schema50 = dataFixerBuilder.addSchema(1466, class_1237::new);
		dataFixerBuilder.addFixer(new class_3591(schema50, true));
		Schema schema51 = dataFixerBuilder.addSchema(1470, class_1240::new);
		dataFixerBuilder.addFixer(new class_3553(schema51, "Add 1.13 entities fix", class_1208.field_5729));
		Schema schema52 = dataFixerBuilder.addSchema(1474, field_15775);
		dataFixerBuilder.addFixer(new class_3592(schema52, false));
		dataFixerBuilder.addFixer(
			class_3579.method_15589(
				schema52,
				"Colorless shulker block fixer",
				string -> Objects.equals(class_1220.method_5193(string), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : string
			)
		);
		dataFixerBuilder.addFixer(
			class_1182.method_5019(
				schema52,
				"Colorless shulker item fixer",
				string -> Objects.equals(class_1220.method_5193(string), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : string
			)
		);
		Schema schema53 = dataFixerBuilder.addSchema(1475, field_15775);
		dataFixerBuilder.addFixer(
			class_3579.method_15589(
				schema53,
				"Flowing fixer",
				string -> ImmutableMap.of("minecraft:flowing_water", "minecraft:water", "minecraft:flowing_lava", "minecraft:lava").getOrDefault(string, string)
			)
		);
		Schema schema54 = dataFixerBuilder.addSchema(1480, field_15775);
		dataFixerBuilder.addFixer(class_3579.method_15589(schema54, "Rename coral blocks", string -> (String)class_1210.field_5733.getOrDefault(string, string)));
		dataFixerBuilder.addFixer(class_1182.method_5019(schema54, "Rename coral items", string -> (String)class_1210.field_5733.getOrDefault(string, string)));
		Schema schema55 = dataFixerBuilder.addSchema(1481, class_1239::new);
		dataFixerBuilder.addFixer(new class_3553(schema55, "Add conduit", class_1208.field_5727));
		Schema schema56 = dataFixerBuilder.addSchema(1483, class_1242::new);
		dataFixerBuilder.addFixer(new class_3608(schema56, true));
		dataFixerBuilder.addFixer(
			class_1182.method_5019(schema56, "Rename pufferfish egg item", string -> (String)class_3608.field_15899.getOrDefault(string, string))
		);
		Schema schema57 = dataFixerBuilder.addSchema(1484, field_15775);
		dataFixerBuilder.addFixer(
			class_1182.method_5019(
				schema57,
				"Rename seagrass items",
				string -> ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass").getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(
			class_3579.method_15589(
				schema57,
				"Rename seagrass blocks",
				string -> ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass").getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(new class_1177(schema57, false));
		Schema schema58 = dataFixerBuilder.addSchema(1486, class_1241::new);
		dataFixerBuilder.addFixer(new class_3596(schema58, true));
		dataFixerBuilder.addFixer(
			class_1182.method_5019(schema58, "Rename cod/salmon egg items", string -> (String)class_3596.field_15893.getOrDefault(string, string))
		);
		Schema schema59 = dataFixerBuilder.addSchema(1487, field_15775);
		dataFixerBuilder.addFixer(
			class_1182.method_5019(
				schema59,
				"Rename prismarine_brick(s)_* blocks",
				string -> ImmutableMap.of(
							"minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"
						)
						.getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(
			class_3579.method_15589(
				schema59,
				"Rename prismarine_brick(s)_* items",
				string -> ImmutableMap.of(
							"minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs"
						)
						.getOrDefault(string, string)
			)
		);
		Schema schema60 = dataFixerBuilder.addSchema(1488, field_15775);
		dataFixerBuilder.addFixer(
			class_3579.method_15589(
				schema60,
				"Rename kelp/kelptop",
				string -> ImmutableMap.of("minecraft:kelp_top", "minecraft:kelp", "minecraft:kelp", "minecraft:kelp_plant").getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(
			class_1182.method_5019(schema60, "Rename kelptop", string -> Objects.equals(string, "minecraft:kelp_top") ? "minecraft:kelp" : string)
		);
		dataFixerBuilder.addFixer(new class_1197(schema60, false, "Command block block entity custom name fix", class_1208.field_5727, "minecraft:command_block") {
			@Override
			protected Typed<?> method_5105(Typed<?> typed) {
				return typed.update(DSL.remainderFinder(), class_3597::method_15697);
			}
		});
		dataFixerBuilder.addFixer(
			new class_1197(schema60, false, "Command block minecart custom name fix", class_1208.field_5729, "minecraft:commandblock_minecart") {
				@Override
				protected Typed<?> method_5105(Typed<?> typed) {
					return typed.update(DSL.remainderFinder(), class_3597::method_15697);
				}
			}
		);
		dataFixerBuilder.addFixer(new class_1176(schema60, false));
		Schema schema61 = dataFixerBuilder.addSchema(1490, field_15775);
		dataFixerBuilder.addFixer(
			class_3579.method_15589(schema61, "Rename melon_block", string -> Objects.equals(string, "minecraft:melon_block") ? "minecraft:melon" : string)
		);
		dataFixerBuilder.addFixer(
			class_1182.method_5019(
				schema61,
				"Rename melon_block/melon/speckled_melon",
				string -> ImmutableMap.of(
							"minecraft:melon_block", "minecraft:melon", "minecraft:melon", "minecraft:melon_slice", "minecraft:speckled_melon", "minecraft:glistering_melon_slice"
						)
						.getOrDefault(string, string)
			)
		);
		Schema schema62 = dataFixerBuilder.addSchema(1492, field_15775);
		dataFixerBuilder.addFixer(new class_3590(schema62, false));
		Schema schema63 = dataFixerBuilder.addSchema(1494, field_15775);
		dataFixerBuilder.addFixer(new class_1187(schema63, false));
		Schema schema64 = dataFixerBuilder.addSchema(1496, field_15775);
		dataFixerBuilder.addFixer(new class_1191(schema64, false));
		Schema schema65 = dataFixerBuilder.addSchema(1500, field_15775);
		dataFixerBuilder.addFixer(new class_3574(schema65, false));
		Schema schema66 = dataFixerBuilder.addSchema(1501, field_15775);
		dataFixerBuilder.addFixer(new class_3555(schema66, false));
		Schema schema67 = dataFixerBuilder.addSchema(1502, field_15775);
		dataFixerBuilder.addFixer(new class_1206(schema67, false));
		Schema schema68 = dataFixerBuilder.addSchema(1506, field_15775);
		dataFixerBuilder.addFixer(new class_1196(schema68, false));
		Schema schema69 = dataFixerBuilder.addSchema(1508, field_15775);
		dataFixerBuilder.addFixer(new class_3561(schema69, false));
		Schema schema70 = dataFixerBuilder.addSchema(1510, class_1243::new);
		dataFixerBuilder.addFixer(class_3579.method_15589(schema70, "Block renamening fix", string -> (String)class_1170.field_5672.getOrDefault(string, string)));
		dataFixerBuilder.addFixer(class_1182.method_5019(schema70, "Item renamening fix", string -> (String)class_1170.field_5673.getOrDefault(string, string)));
		dataFixerBuilder.addFixer(new class_1205(schema70, false));
		dataFixerBuilder.addFixer(new class_1170(schema70, true));
		dataFixerBuilder.addFixer(new class_1213(schema70, false));
		Schema schema71 = dataFixerBuilder.addSchema(1514, field_15775);
		dataFixerBuilder.addFixer(new class_1200(schema71, false));
		dataFixerBuilder.addFixer(new class_1217(schema71, false));
		dataFixerBuilder.addFixer(new class_1199(schema71, false));
		Schema schema72 = dataFixerBuilder.addSchema(1515, field_15775);
		dataFixerBuilder.addFixer(class_3579.method_15589(schema72, "Rename coral fan blocks", string -> (String)class_1207.field_5709.getOrDefault(string, string)));
		Schema schema73 = dataFixerBuilder.addSchema(1624, field_15775);
		dataFixerBuilder.addFixer(new class_1215(schema73, false));
		Schema schema74 = dataFixerBuilder.addSchema(1800, class_1245::new);
		dataFixerBuilder.addFixer(new class_3553(schema74, "Added 1.14 mobs fix", class_1208.field_5729));
		dataFixerBuilder.addFixer(class_1182.method_5019(schema74, "Rename dye items", string -> (String)class_3593.field_15890.getOrDefault(string, string)));
		Schema schema75 = dataFixerBuilder.addSchema(1801, class_1244::new);
		dataFixerBuilder.addFixer(new class_3553(schema75, "Added Illager Beast", class_1208.field_5729));
		Schema schema76 = dataFixerBuilder.addSchema(1802, field_15775);
		dataFixerBuilder.addFixer(
			class_3579.method_15589(
				schema76,
				"Rename sign blocks & stone slabs",
				string -> ImmutableMap.of(
							"minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign", "minecraft:wall_sign", "minecraft:oak_wall_sign"
						)
						.getOrDefault(string, string)
			)
		);
		dataFixerBuilder.addFixer(
			class_1182.method_5019(
				schema76,
				"Rename sign item & stone slabs",
				string -> ImmutableMap.of("minecraft:stone_slab", "minecraft:smooth_stone_slab", "minecraft:sign", "minecraft:oak_sign").getOrDefault(string, string)
			)
		);
		Schema schema77 = dataFixerBuilder.addSchema(1803, field_15775);
		dataFixerBuilder.addFixer(new class_1180(schema77, false));
		Schema schema78 = dataFixerBuilder.addSchema(1904, class_3686::new);
		dataFixerBuilder.addFixer(new class_3553(schema78, "Added Cats", class_1208.field_5729));
		dataFixerBuilder.addFixer(new class_3725(schema78, false));
		Schema schema79 = dataFixerBuilder.addSchema(1905, field_15775);
		dataFixerBuilder.addFixer(new class_3589(schema79, false));
		Schema schema80 = dataFixerBuilder.addSchema(1906, class_3687::new);
		dataFixerBuilder.addFixer(new class_3553(schema80, "Add POI Blocks", class_1208.field_5727));
		Schema schema81 = dataFixerBuilder.addSchema(1907, field_15775);
		dataFixerBuilder.addFixer(new class_3755(schema81, false));
		Schema schema82 = dataFixerBuilder.addSchema(1909, class_3743::new);
		dataFixerBuilder.addFixer(new class_3553(schema82, "Add jigsaw", class_1208.field_5727));
		Schema schema83 = dataFixerBuilder.addSchema(1911, field_15775);
		dataFixerBuilder.addFixer(new class_3788(schema83, false));
		Schema schema84 = dataFixerBuilder.addSchema(1917, field_15775);
		dataFixerBuilder.addFixer(new class_3903(schema84, false));
		Schema schema85 = dataFixerBuilder.addSchema(1918, field_15775);
		dataFixerBuilder.addFixer(new class_3845(schema85, "minecraft:villager"));
		dataFixerBuilder.addFixer(new class_3845(schema85, "minecraft:zombie_villager"));
		Schema schema86 = dataFixerBuilder.addSchema(1920, class_3905::new);
		dataFixerBuilder.addFixer(new class_3904(schema86, false));
		dataFixerBuilder.addFixer(new class_3553(schema86, "Add campfire", class_1208.field_5727));
		Schema schema87 = dataFixerBuilder.addSchema(1925, field_15775);
		dataFixerBuilder.addFixer(new class_3970(schema87, false));
		Schema schema88 = dataFixerBuilder.addSchema(1928, class_3984::new);
		dataFixerBuilder.addFixer(new class_3983(schema88, true));
		dataFixerBuilder.addFixer(class_1182.method_5019(schema88, "Rename ravager egg item", string -> (String)class_3983.field_17712.getOrDefault(string, string)));
		Schema schema89 = dataFixerBuilder.addSchema(1929, class_3985::new);
		dataFixerBuilder.addFixer(new class_3553(schema89, "Add Wandering Trader and Trader Llama", class_1208.field_5729));
		Schema schema90 = dataFixerBuilder.addSchema(1931, class_4016::new);
		dataFixerBuilder.addFixer(new class_3553(schema90, "Added Fox", class_1208.field_5729));
		Schema schema91 = dataFixerBuilder.addSchema(1936, field_15775);
		dataFixerBuilder.addFixer(new class_4092(schema91, false));
	}
}
