package net.minecraft.datafixer.fix;

import com.google.common.base.Splitter;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.util.math.MathHelper;

public class ItemStackComponentizationFix extends DataFix {
	private static final int HIDE_ENCHANTMENTS_FLAG = 1;
	private static final int HIDE_MODIFIERS_FLAG = 2;
	private static final int HIDE_UNBREAKABLE_FLAG = 4;
	private static final int HIDE_CAN_DESTROY_FLAG = 8;
	private static final int HIDE_CAN_PLACE_FLAG = 16;
	private static final int HIDE_ADDITIONAL_FLAG = 32;
	private static final int HIDE_DYED_FLAG = 64;
	private static final int HIDE_UPGRADE_FLAG = 128;
	private static final Set<String> POTION_ITEM_IDS = Set.of(
		"minecraft:potion", "minecraft:splash_potion", "minecraft:lingering_potion", "minecraft:tipped_arrow"
	);
	private static final Set<String> ENTITY_BUCKET_ITEM_IDS = Set.of(
		"minecraft:pufferfish_bucket",
		"minecraft:salmon_bucket",
		"minecraft:cod_bucket",
		"minecraft:tropical_fish_bucket",
		"minecraft:axolotl_bucket",
		"minecraft:tadpole_bucket"
	);
	private static final List<String> RELEVANT_ENTITY_NBT_KEYS = List.of(
		"NoAI", "Silent", "NoGravity", "Glowing", "Invulnerable", "Health", "Age", "Variant", "HuntingCooldown", "BucketVariantTag"
	);
	private static final Set<String> BOOLEAN_BLOCK_STATE_PROPERTIES = Set.of(
		"attached",
		"bottom",
		"conditional",
		"disarmed",
		"drag",
		"enabled",
		"extended",
		"eye",
		"falling",
		"hanging",
		"has_bottle_0",
		"has_bottle_1",
		"has_bottle_2",
		"has_record",
		"has_book",
		"inverted",
		"in_wall",
		"lit",
		"locked",
		"occupied",
		"open",
		"persistent",
		"powered",
		"short",
		"signal_fire",
		"snowy",
		"triggered",
		"unstable",
		"waterlogged",
		"berries",
		"bloom",
		"shrieking",
		"can_summon",
		"up",
		"down",
		"north",
		"east",
		"south",
		"west",
		"slot_0_occupied",
		"slot_1_occupied",
		"slot_2_occupied",
		"slot_3_occupied",
		"slot_4_occupied",
		"slot_5_occupied",
		"cracked",
		"crafting"
	);
	private static final Splitter COMMA_SPLITTER = Splitter.on(',');

	public ItemStackComponentizationFix(Schema outputSchema) {
		super(outputSchema, true);
	}

	private static void fixStack(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic) {
		int i = data.getAndRemove("HideFlags").asInt(0);
		data.moveToComponent("Damage", "minecraft:damage", dynamic.createInt(0));
		data.moveToComponent("RepairCost", "minecraft:repair_cost", dynamic.createInt(0));
		data.moveToComponent("CustomModelData", "minecraft:custom_model_data");
		data.getAndRemove("BlockStateTag")
			.result()
			.ifPresent(blockStateTagDynamic -> data.setComponent("minecraft:block_state", fixBlockStateTag(blockStateTagDynamic)));
		data.moveToComponent("EntityTag", "minecraft:entity_data");
		data.applyFixer("BlockEntityTag", false, blockEntityTagDynamic -> {
			String string = IdentifierNormalizingSchema.normalize(blockEntityTagDynamic.get("id").asString(""));
			blockEntityTagDynamic = fixBlockEntityData(data, blockEntityTagDynamic, string);
			Dynamic<?> dynamicx = blockEntityTagDynamic.remove("id");
			return dynamicx.equals(blockEntityTagDynamic.emptyMap()) ? dynamicx : blockEntityTagDynamic;
		});
		data.moveToComponent("BlockEntityTag", "minecraft:block_entity_data");
		if (data.getAndRemove("Unbreakable").asBoolean(false)) {
			Dynamic<?> dynamic2 = dynamic.emptyMap();
			if ((i & 4) != 0) {
				dynamic2 = dynamic2.set("show_in_tooltip", dynamic.createBoolean(false));
			}

			data.setComponent("minecraft:unbreakable", dynamic2);
		}

		fixEnchantments(data, dynamic, "Enchantments", "minecraft:enchantments", (i & 1) != 0);
		if (data.itemEquals("minecraft:enchanted_book")) {
			fixEnchantments(data, dynamic, "StoredEnchantments", "minecraft:stored_enchantments", (i & 32) != 0);
		}

		data.applyFixer("display", false, displayDynamic -> fixDisplay(data, displayDynamic, i));
		fixAdventureModePredicates(data, dynamic, i);
		fixAttributeModifiers(data, dynamic, i);
		Optional<? extends Dynamic<?>> optional = data.getAndRemove("Trim").result();
		if (optional.isPresent()) {
			Dynamic<?> dynamic3 = (Dynamic<?>)optional.get();
			if ((i & 128) != 0) {
				dynamic3 = dynamic3.set("show_in_tooltip", dynamic3.createBoolean(false));
			}

			data.setComponent("minecraft:trim", dynamic3);
		}

		if ((i & 32) != 0) {
			data.setComponent("minecraft:hide_additional_tooltip", dynamic.emptyMap());
		}

		if (data.itemEquals("minecraft:crossbow")) {
			data.getAndRemove("Charged");
			data.moveToComponent("ChargedProjectiles", "minecraft:charged_projectiles", dynamic.createList(Stream.empty()));
		}

		if (data.itemEquals("minecraft:bundle")) {
			data.moveToComponent("Items", "minecraft:bundle_contents", dynamic.createList(Stream.empty()));
		}

		if (data.itemEquals("minecraft:filled_map")) {
			data.moveToComponent("map", "minecraft:map_id");
			Map<? extends Dynamic<?>, ? extends Dynamic<?>> map = (Map<? extends Dynamic<?>, ? extends Dynamic<?>>)data.getAndRemove("Decorations")
				.asStream()
				.map(ItemStackComponentizationFix::fixMapDecorations)
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond, (dynamicx, dynamic2) -> dynamicx));
			if (!map.isEmpty()) {
				data.setComponent("minecraft:map_decorations", dynamic.createMap(map));
			}
		}

		if (data.itemMatches(POTION_ITEM_IDS)) {
			fixPotionContents(data, dynamic);
		}

		if (data.itemEquals("minecraft:writable_book")) {
			fixWritableBookContent(data, dynamic);
		}

		if (data.itemEquals("minecraft:written_book")) {
			fixWrittenBookContent(data, dynamic);
		}

		if (data.itemEquals("minecraft:suspicious_stew")) {
			data.moveToComponent("effects", "minecraft:suspicious_stew_effects");
		}

		if (data.itemEquals("minecraft:debug_stick")) {
			data.moveToComponent("DebugProperty", "minecraft:debug_stick_state");
		}

		if (data.itemMatches(ENTITY_BUCKET_ITEM_IDS)) {
			fixBucketEntityData(data, dynamic);
		}

		if (data.itemEquals("minecraft:goat_horn")) {
			data.moveToComponent("instrument", "minecraft:instrument");
		}

		if (data.itemEquals("minecraft:knowledge_book")) {
			data.moveToComponent("Recipes", "minecraft:recipes");
		}

		if (data.itemEquals("minecraft:compass")) {
			fixLodestoneTarget(data, dynamic);
		}

		if (data.itemEquals("minecraft:firework_rocket")) {
			fixFireworks(data);
		}

		if (data.itemEquals("minecraft:firework_star")) {
			fixExplosion(data);
		}

		if (data.itemEquals("minecraft:player_head")) {
			data.getAndRemove("SkullOwner").result().ifPresent(skullOwnerDynamic -> data.setComponent("minecraft:profile", createProfileDynamic(skullOwnerDynamic)));
		}
	}

	private static Dynamic<?> fixBlockStateTag(Dynamic<?> dynamic) {
		return DataFixUtils.orElse(dynamic.asMapOpt().result().map(stream -> (Map)stream.collect(Collectors.toMap(Pair::getFirst, pair -> {
				String string = ((Dynamic)pair.getFirst()).asString("");
				Dynamic<?> dynamicx = (Dynamic<?>)pair.getSecond();
				if (BOOLEAN_BLOCK_STATE_PROPERTIES.contains(string)) {
					Optional<Boolean> optional = dynamicx.asBoolean().result();
					if (optional.isPresent()) {
						return dynamicx.createString(String.valueOf(optional.get()));
					}
				}

				Optional<Number> optional = dynamicx.asNumber().result();
				return optional.isPresent() ? dynamicx.createString(((Number)optional.get()).toString()) : dynamicx;
			}))).map(dynamic::createMap), dynamic);
	}

	private static Dynamic<?> fixDisplay(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic, int hideFlags) {
		data.setComponent("minecraft:custom_name", dynamic.get("Name"));
		data.setComponent("minecraft:lore", dynamic.get("Lore"));
		Optional<Integer> optional = dynamic.get("color").asNumber().result().map(Number::intValue);
		boolean bl = (hideFlags & 64) != 0;
		if (optional.isPresent() || bl) {
			Dynamic<?> dynamic2 = dynamic.emptyMap().set("rgb", dynamic.createInt((Integer)optional.orElse(10511680)));
			if (bl) {
				dynamic2 = dynamic2.set("show_in_tooltip", dynamic.createBoolean(false));
			}

			data.setComponent("minecraft:dyed_color", dynamic2);
		}

		Optional<String> optional2 = dynamic.get("LocName").asString().result();
		if (optional2.isPresent()) {
			data.setComponent("minecraft:item_name", TextFixes.translate(dynamic.getOps(), (String)optional2.get()));
		}

		if (data.itemEquals("minecraft:filled_map")) {
			data.setComponent("minecraft:map_color", dynamic.get("MapColor"));
			dynamic = dynamic.remove("MapColor");
		}

		return dynamic.remove("Name").remove("Lore").remove("color").remove("LocName");
	}

	private static <T> Dynamic<T> fixBlockEntityData(ItemStackComponentizationFix.StackData data, Dynamic<T> dynamic, String blockEntityId) {
		data.setComponent("minecraft:lock", dynamic.get("Lock"));
		dynamic = dynamic.remove("Lock");
		Optional<Dynamic<T>> optional = dynamic.get("LootTable").result();
		if (optional.isPresent()) {
			Dynamic<T> dynamic2 = dynamic.emptyMap().set("loot_table", (Dynamic<?>)optional.get());
			long l = dynamic.get("LootTableSeed").asLong(0L);
			if (l != 0L) {
				dynamic2 = dynamic2.set("seed", dynamic.createLong(l));
			}

			data.setComponent("minecraft:container_loot", dynamic2);
			dynamic = dynamic.remove("LootTable").remove("LootTableSeed");
		}
		return switch (blockEntityId) {
			case "minecraft:skull" -> {
				data.setComponent("minecraft:note_block_sound", dynamic.get("note_block_sound"));
				yield dynamic.remove("note_block_sound");
			}
			case "minecraft:decorated_pot" -> {
				data.setComponent("minecraft:pot_decorations", dynamic.get("sherds"));
				Optional<Dynamic<T>> optional2 = dynamic.get("item").result();
				if (optional2.isPresent()) {
					data.setComponent(
						"minecraft:container", dynamic.createList(Stream.of(dynamic.emptyMap().set("slot", dynamic.createInt(0)).set("item", (Dynamic<?>)optional2.get())))
					);
				}

				yield dynamic.remove("sherds").remove("item");
			}
			case "minecraft:banner" -> {
				data.setComponent("minecraft:banner_patterns", dynamic.get("patterns"));
				Optional<Number> optional2 = dynamic.get("Base").asNumber().result();
				if (optional2.isPresent()) {
					data.setComponent("minecraft:base_color", dynamic.createString(BannerPatternFormatFix.getColorFromInt(((Number)optional2.get()).intValue())));
				}

				yield dynamic.remove("patterns").remove("Base");
			}
			case "minecraft:shulker_box", "minecraft:chest", "minecraft:trapped_chest", "minecraft:furnace", "minecraft:ender_chest", "minecraft:dispenser", "minecraft:dropper", "minecraft:brewing_stand", "minecraft:hopper", "minecraft:barrel", "minecraft:smoker", "minecraft:blast_furnace", "minecraft:campfire", "minecraft:chiseled_bookshelf", "minecraft:crafter" -> {
				List<Dynamic<T>> list = dynamic.get("Items")
					.asList(
						itemsDynamic -> itemsDynamic.emptyMap()
								.set("slot", itemsDynamic.createInt(itemsDynamic.get("Slot").asByte((byte)0) & 255))
								.set("item", itemsDynamic.remove("Slot"))
					);
				if (!list.isEmpty()) {
					data.setComponent("minecraft:container", dynamic.createList(list.stream()));
				}

				yield dynamic.remove("Items");
			}
			case "minecraft:beehive" -> {
				data.setComponent("minecraft:bees", dynamic.get("bees"));
				yield dynamic.remove("bees");
			}
			default -> dynamic;
		};
	}

	private static void fixEnchantments(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic, String nbtKey, String componentId, boolean hideInTooltip) {
		OptionalDynamic<?> optionalDynamic = data.getAndRemove(nbtKey);
		List<Pair<String, Integer>> list = optionalDynamic.asList(Function.identity())
			.stream()
			.flatMap(enchantmentsDynamic -> getEnchantmentAndLevelPair(enchantmentsDynamic).stream())
			.toList();
		if (!list.isEmpty() || hideInTooltip) {
			Dynamic<?> dynamic2 = dynamic.emptyMap();
			Dynamic<?> dynamic3 = dynamic.emptyMap();

			for (Pair<String, Integer> pair : list) {
				dynamic3 = dynamic3.set(pair.getFirst(), dynamic.createInt(pair.getSecond()));
			}

			dynamic2 = dynamic2.set("levels", dynamic3);
			if (hideInTooltip) {
				dynamic2 = dynamic2.set("show_in_tooltip", dynamic.createBoolean(false));
			}

			data.setComponent(componentId, dynamic2);
		}

		if (optionalDynamic.result().isPresent() && list.isEmpty()) {
			data.setComponent("minecraft:enchantment_glint_override", dynamic.createBoolean(true));
		}
	}

	private static Optional<Pair<String, Integer>> getEnchantmentAndLevelPair(Dynamic<?> dynamic) {
		return dynamic.get("id")
			.asString()
			.<Number, Pair<String, Integer>>apply2stable(
				(enchantmentId, level) -> Pair.of(enchantmentId, MathHelper.clamp(level.intValue(), 0, 255)), dynamic.get("lvl").asNumber()
			)
			.result();
	}

	private static void fixAdventureModePredicates(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic, int hideFlags) {
		fixBlockPredicateList(data, dynamic, "CanDestroy", "minecraft:can_break", (hideFlags & 8) != 0);
		fixBlockPredicateList(data, dynamic, "CanPlaceOn", "minecraft:can_place_on", (hideFlags & 16) != 0);
	}

	private static void fixBlockPredicateList(
		ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic, String nbtKey, String componentId, boolean hideInTooltip
	) {
		Optional<? extends Dynamic<?>> optional = data.getAndRemove(nbtKey).result();
		if (!optional.isEmpty()) {
			Dynamic<?> dynamic2 = dynamic.emptyMap()
				.set(
					"predicates",
					dynamic.createList(
						((Dynamic)optional.get())
							.asStream()
							.map(
								predicatesDynamic -> DataFixUtils.orElse(
										predicatesDynamic.asString().map(string -> createBlockPredicateListDynamic(predicatesDynamic, string)).result(), predicatesDynamic
									)
							)
					)
				);
			if (hideInTooltip) {
				dynamic2 = dynamic2.set("show_in_tooltip", dynamic.createBoolean(false));
			}

			data.setComponent(componentId, dynamic2);
		}
	}

	private static Dynamic<?> createBlockPredicateListDynamic(Dynamic<?> dynamic, String listAsString) {
		int i = listAsString.indexOf(91);
		int j = listAsString.indexOf(123);
		int k = listAsString.length();
		if (i != -1) {
			k = i;
		}

		if (j != -1) {
			k = Math.min(k, j);
		}

		String string = listAsString.substring(0, k);
		Dynamic<?> dynamic2 = dynamic.emptyMap().set("blocks", dynamic.createString(string.trim()));
		int l = listAsString.indexOf(93);
		if (i != -1 && l != -1) {
			Dynamic<?> dynamic3 = dynamic.emptyMap();

			for (String string2 : COMMA_SPLITTER.split(listAsString.substring(i + 1, l))) {
				int m = string2.indexOf(61);
				if (m != -1) {
					String string3 = string2.substring(0, m).trim();
					String string4 = string2.substring(m + 1).trim();
					dynamic3 = dynamic3.set(string3, dynamic.createString(string4));
				}
			}

			dynamic2 = dynamic2.set("state", dynamic3);
		}

		int n = listAsString.indexOf(125);
		if (j != -1 && n != -1) {
			dynamic2 = dynamic2.set("nbt", dynamic.createString(listAsString.substring(j, n + 1)));
		}

		return dynamic2;
	}

	private static void fixAttributeModifiers(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic, int hideFlags) {
		List<? extends Dynamic<?>> list = data.getAndRemove("AttributeModifiers").asList(ItemStackComponentizationFix::fixAttributeModifier);
		boolean bl = (hideFlags & 2) != 0;
		if (!list.isEmpty() || bl) {
			Dynamic<?> dynamic2 = dynamic.emptyMap().set("modifiers", dynamic.createList(list.stream()));
			if (bl) {
				dynamic2 = dynamic2.set("show_in_tooltip", dynamic.createBoolean(false));
			}

			data.setComponent("minecraft:attribute_modifiers", dynamic2);
		}
	}

	private static Dynamic<?> fixAttributeModifier(Dynamic<?> dynamic) {
		Dynamic<?> dynamic2 = dynamic.emptyMap()
			.set("name", dynamic.createString(""))
			.set("amount", dynamic.createDouble(0.0))
			.set("operation", dynamic.createString("add_value"));
		dynamic2 = Dynamic.copyField(dynamic, "AttributeName", dynamic2, "type");
		dynamic2 = Dynamic.copyField(dynamic, "Slot", dynamic2, "slot");
		dynamic2 = Dynamic.copyField(dynamic, "UUID", dynamic2, "uuid");
		dynamic2 = Dynamic.copyField(dynamic, "Name", dynamic2, "name");
		dynamic2 = Dynamic.copyField(dynamic, "Amount", dynamic2, "amount");
		return Dynamic.copyAndFixField(dynamic, "Operation", dynamic2, "operation", operationDynamic -> {
			return operationDynamic.createString(switch (operationDynamic.asInt(0)) {
				case 1 -> "add_multiplied_base";
				case 2 -> "add_multiplied_total";
				default -> "add_value";
			});
		});
	}

	private static Pair<Dynamic<?>, Dynamic<?>> fixMapDecorations(Dynamic<?> dynamic) {
		Dynamic<?> dynamic2 = DataFixUtils.orElseGet(dynamic.get("id").result(), () -> dynamic.createString(""));
		Dynamic<?> dynamic3 = dynamic.emptyMap()
			.set("type", dynamic.createString(getMapDecorationName(dynamic.get("type").asInt(0))))
			.set("x", dynamic.createDouble(dynamic.get("x").asDouble(0.0)))
			.set("z", dynamic.createDouble(dynamic.get("z").asDouble(0.0)))
			.set("rotation", dynamic.createFloat((float)dynamic.get("rot").asDouble(0.0)));
		return Pair.of(dynamic2, dynamic3);
	}

	private static String getMapDecorationName(int index) {
		return switch (index) {
			case 1 -> "frame";
			case 2 -> "red_marker";
			case 3 -> "blue_marker";
			case 4 -> "target_x";
			case 5 -> "target_point";
			case 6 -> "player_off_map";
			case 7 -> "player_off_limits";
			case 8 -> "mansion";
			case 9 -> "monument";
			case 10 -> "banner_white";
			case 11 -> "banner_orange";
			case 12 -> "banner_magenta";
			case 13 -> "banner_light_blue";
			case 14 -> "banner_yellow";
			case 15 -> "banner_lime";
			case 16 -> "banner_pink";
			case 17 -> "banner_gray";
			case 18 -> "banner_light_gray";
			case 19 -> "banner_cyan";
			case 20 -> "banner_purple";
			case 21 -> "banner_blue";
			case 22 -> "banner_brown";
			case 23 -> "banner_green";
			case 24 -> "banner_red";
			case 25 -> "banner_black";
			case 26 -> "red_x";
			case 27 -> "village_desert";
			case 28 -> "village_plains";
			case 29 -> "village_savanna";
			case 30 -> "village_snowy";
			case 31 -> "village_taiga";
			case 32 -> "jungle_temple";
			case 33 -> "swamp_hut";
			default -> "player";
		};
	}

	private static void fixPotionContents(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic) {
		Dynamic<?> dynamic2 = dynamic.emptyMap();
		Optional<String> optional = data.getAndRemove("Potion").asString().result().filter(potionId -> !potionId.equals("minecraft:empty"));
		if (optional.isPresent()) {
			dynamic2 = dynamic2.set("potion", dynamic.createString((String)optional.get()));
		}

		dynamic2 = data.moveToComponent("CustomPotionColor", dynamic2, "custom_color");
		dynamic2 = data.moveToComponent("custom_potion_effects", dynamic2, "custom_effects");
		if (!dynamic2.equals(dynamic.emptyMap())) {
			data.setComponent("minecraft:potion_contents", dynamic2);
		}
	}

	private static void fixWritableBookContent(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic) {
		Dynamic<?> dynamic2 = fixBookPages(data, dynamic);
		if (dynamic2 != null) {
			data.setComponent("minecraft:writable_book_content", dynamic.emptyMap().set("pages", dynamic2));
		}
	}

	private static void fixWrittenBookContent(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic) {
		Dynamic<?> dynamic2 = fixBookPages(data, dynamic);
		String string = data.getAndRemove("title").asString("");
		Optional<String> optional = data.getAndRemove("filtered_title").asString().result();
		Dynamic<?> dynamic3 = dynamic.emptyMap();
		dynamic3 = dynamic3.set("title", createFilterableTextDynamic(dynamic, string, optional));
		dynamic3 = data.moveToComponent("author", dynamic3, "author");
		dynamic3 = data.moveToComponent("resolved", dynamic3, "resolved");
		dynamic3 = data.moveToComponent("generation", dynamic3, "generation");
		if (dynamic2 != null) {
			dynamic3 = dynamic3.set("pages", dynamic2);
		}

		data.setComponent("minecraft:written_book_content", dynamic3);
	}

	@Nullable
	private static Dynamic<?> fixBookPages(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic) {
		List<String> list = data.getAndRemove("pages").asList(pagesDynamic -> pagesDynamic.asString(""));
		Map<String, String> map = data.getAndRemove("filtered_pages")
			.asMap(filteredPagesKeyDynamic -> filteredPagesKeyDynamic.asString("0"), filteredPagesValueDynamic -> filteredPagesValueDynamic.asString(""));
		if (list.isEmpty()) {
			return null;
		} else {
			List<Dynamic<?>> list2 = new ArrayList(list.size());

			for (int i = 0; i < list.size(); i++) {
				String string = (String)list.get(i);
				String string2 = (String)map.get(String.valueOf(i));
				list2.add(createFilterableTextDynamic(dynamic, string, Optional.ofNullable(string2)));
			}

			return dynamic.createList(list2.stream());
		}
	}

	private static Dynamic<?> createFilterableTextDynamic(Dynamic<?> dynamic, String unfiltered, Optional<String> filtered) {
		Dynamic<?> dynamic2 = dynamic.emptyMap().set("raw", dynamic.createString(unfiltered));
		if (filtered.isPresent()) {
			dynamic2 = dynamic2.set("filtered", dynamic.createString((String)filtered.get()));
		}

		return dynamic2;
	}

	private static void fixBucketEntityData(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic) {
		Dynamic<?> dynamic2 = dynamic.emptyMap();

		for (String string : RELEVANT_ENTITY_NBT_KEYS) {
			dynamic2 = data.moveToComponent(string, dynamic2, string);
		}

		if (!dynamic2.equals(dynamic.emptyMap())) {
			data.setComponent("minecraft:bucket_entity_data", dynamic2);
		}
	}

	private static void fixLodestoneTarget(ItemStackComponentizationFix.StackData data, Dynamic<?> dynamic) {
		Optional<? extends Dynamic<?>> optional = data.getAndRemove("LodestonePos").result();
		Optional<? extends Dynamic<?>> optional2 = data.getAndRemove("LodestoneDimension").result();
		if (!optional.isEmpty() || !optional2.isEmpty()) {
			boolean bl = data.getAndRemove("LodestoneTracked").asBoolean(true);
			Dynamic<?> dynamic2 = dynamic.emptyMap();
			if (optional.isPresent() && optional2.isPresent()) {
				dynamic2 = dynamic2.set("target", dynamic.emptyMap().set("pos", (Dynamic<?>)optional.get()).set("dimension", (Dynamic<?>)optional2.get()));
			}

			if (!bl) {
				dynamic2 = dynamic2.set("tracked", dynamic.createBoolean(false));
			}

			data.setComponent("minecraft:lodestone_tracker", dynamic2);
		}
	}

	private static void fixExplosion(ItemStackComponentizationFix.StackData data) {
		data.applyFixer("Explosion", true, explosionDynamic -> {
			data.setComponent("minecraft:firework_explosion", fixExplosion(explosionDynamic));
			return explosionDynamic.remove("Type").remove("Colors").remove("FadeColors").remove("Trail").remove("Flicker");
		});
	}

	private static void fixFireworks(ItemStackComponentizationFix.StackData data) {
		data.applyFixer(
			"Fireworks",
			true,
			fireworksDynamic -> {
				Stream<? extends Dynamic<?>> stream = fireworksDynamic.get("Explosions").asStream().map(ItemStackComponentizationFix::fixExplosion);
				int i = fireworksDynamic.get("Flight").asInt(0);
				data.setComponent(
					"minecraft:fireworks",
					fireworksDynamic.emptyMap().set("explosions", fireworksDynamic.createList(stream)).set("flight_duration", fireworksDynamic.createByte((byte)i))
				);
				return fireworksDynamic.remove("Explosions").remove("Flight");
			}
		);
	}

	private static Dynamic<?> fixExplosion(Dynamic<?> dynamic) {
		dynamic = dynamic.set("shape", dynamic.createString(switch (dynamic.get("Type").asInt(0)) {
			case 1 -> "large_ball";
			case 2 -> "star";
			case 3 -> "creeper";
			case 4 -> "burst";
			default -> "small_ball";
		})).remove("Type");
		dynamic = dynamic.renameField("Colors", "colors");
		dynamic = dynamic.renameField("FadeColors", "fade_colors");
		dynamic = dynamic.renameField("Trail", "has_trail");
		return dynamic.renameField("Flicker", "has_twinkle");
	}

	public static Dynamic<?> createProfileDynamic(Dynamic<?> dynamic) {
		Optional<String> optional = dynamic.asString().result();
		if (optional.isPresent()) {
			return isValidUsername((String)optional.get()) ? dynamic.emptyMap().set("name", dynamic.createString((String)optional.get())) : dynamic.emptyMap();
		} else {
			String string = dynamic.get("Name").asString("");
			Optional<? extends Dynamic<?>> optional2 = dynamic.get("Id").result();
			Dynamic<?> dynamic2 = createPropertiesDynamic(dynamic.get("Properties"));
			Dynamic<?> dynamic3 = dynamic.emptyMap();
			if (isValidUsername(string)) {
				dynamic3 = dynamic3.set("name", dynamic.createString(string));
			}

			if (optional2.isPresent()) {
				dynamic3 = dynamic3.set("id", (Dynamic<?>)optional2.get());
			}

			if (dynamic2 != null) {
				dynamic3 = dynamic3.set("properties", dynamic2);
			}

			return dynamic3;
		}
	}

	private static boolean isValidUsername(String username) {
		return username.length() > 16 ? false : username.chars().filter(c -> c <= 32 || c >= 127).findAny().isEmpty();
	}

	@Nullable
	private static Dynamic<?> createPropertiesDynamic(OptionalDynamic<?> propertiesDynamic) {
		Map<String, List<Pair<String, Optional<String>>>> map = propertiesDynamic.asMap(dynamic -> dynamic.asString(""), dynamic -> dynamic.asList(dynamicx -> {
				String string = dynamicx.get("Value").asString("");
				Optional<String> optional = dynamicx.get("Signature").asString().result();
				return Pair.of(string, optional);
			}));
		return map.isEmpty()
			? null
			: propertiesDynamic.createList(
				map.entrySet()
					.stream()
					.flatMap(
						entry -> ((List)entry.getValue())
								.stream()
								.map(
									pair -> {
										Dynamic<?> dynamic = propertiesDynamic.emptyMap()
											.set("name", propertiesDynamic.createString((String)entry.getKey()))
											.set("value", propertiesDynamic.createString((String)pair.getFirst()));
										Optional<String> optional = (Optional<String>)pair.getSecond();
										return optional.isPresent() ? dynamic.set("signature", propertiesDynamic.createString((String)optional.get())) : dynamic;
									}
								)
					)
			);
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.writeFixAndRead(
			"ItemStack componentization",
			this.getInputSchema().getType(TypeReferences.ITEM_STACK),
			this.getOutputSchema().getType(TypeReferences.ITEM_STACK),
			dynamic -> {
				Optional<? extends Dynamic<?>> optional = ItemStackComponentizationFix.StackData.fromDynamic(dynamic).map(data -> {
					fixStack(data, data.nbt);
					return data.finalize();
				});
				return DataFixUtils.orElse(optional, dynamic);
			}
		);
	}

	static class StackData {
		private final String itemId;
		private final int count;
		private Dynamic<?> components;
		private final Dynamic<?> leftoverNbt;
		Dynamic<?> nbt;

		private StackData(String itemId, int count, Dynamic<?> dynamic) {
			this.itemId = IdentifierNormalizingSchema.normalize(itemId);
			this.count = count;
			this.components = dynamic.emptyMap();
			this.nbt = dynamic.get("tag").orElseEmptyMap();
			this.leftoverNbt = dynamic.remove("tag");
		}

		public static Optional<ItemStackComponentizationFix.StackData> fromDynamic(Dynamic<?> dynamic) {
			return dynamic.get("id")
				.asString()
				.<Number, ItemStackComponentizationFix.StackData>apply2stable(
					(itemId, count) -> new ItemStackComponentizationFix.StackData(itemId, count.intValue(), dynamic.remove("id").remove("Count")),
					dynamic.get("Count").asNumber()
				)
				.result();
		}

		public OptionalDynamic<?> getAndRemove(String key) {
			OptionalDynamic<?> optionalDynamic = this.nbt.get(key);
			this.nbt = this.nbt.remove(key);
			return optionalDynamic;
		}

		public void setComponent(String key, Dynamic<?> value) {
			this.components = this.components.set(key, value);
		}

		public void setComponent(String key, OptionalDynamic<?> optionalValue) {
			optionalValue.result().ifPresent(value -> this.components = this.components.set(key, value));
		}

		public Dynamic<?> moveToComponent(String nbtKey, Dynamic<?> components, String componentId) {
			Optional<? extends Dynamic<?>> optional = this.getAndRemove(nbtKey).result();
			return optional.isPresent() ? components.set(componentId, (Dynamic<?>)optional.get()) : components;
		}

		public void moveToComponent(String nbtKey, String componentId, Dynamic<?> defaultValue) {
			Optional<? extends Dynamic<?>> optional = this.getAndRemove(nbtKey).result();
			if (optional.isPresent() && !((Dynamic)optional.get()).equals(defaultValue)) {
				this.setComponent(componentId, (Dynamic<?>)optional.get());
			}
		}

		public void moveToComponent(String nbtKey, String componentId) {
			this.getAndRemove(nbtKey).result().ifPresent(nbt -> this.setComponent(componentId, nbt));
		}

		public void applyFixer(String nbtKey, boolean removeIfEmpty, UnaryOperator<Dynamic<?>> fixer) {
			OptionalDynamic<?> optionalDynamic = this.nbt.get(nbtKey);
			if (!removeIfEmpty || !optionalDynamic.result().isEmpty()) {
				Dynamic<?> dynamic = optionalDynamic.orElseEmptyMap();
				dynamic = (Dynamic<?>)fixer.apply(dynamic);
				if (dynamic.equals(dynamic.emptyMap())) {
					this.nbt = this.nbt.remove(nbtKey);
				} else {
					this.nbt = this.nbt.set(nbtKey, dynamic);
				}
			}
		}

		public Dynamic<?> finalize() {
			Dynamic<?> dynamic = this.nbt.emptyMap().set("id", this.nbt.createString(this.itemId)).set("count", this.nbt.createInt(this.count));
			if (!this.nbt.equals(this.nbt.emptyMap())) {
				this.components = this.components.set("minecraft:custom_data", this.nbt);
			}

			if (!this.components.equals(this.nbt.emptyMap())) {
				dynamic = dynamic.set("components", this.components);
			}

			return mergeLeftoverNbt(dynamic, this.leftoverNbt);
		}

		private static <T> Dynamic<T> mergeLeftoverNbt(Dynamic<T> data, Dynamic<?> leftoverNbt) {
			DynamicOps<T> dynamicOps = data.getOps();
			return (Dynamic<T>)dynamicOps.getMap(data.getValue())
				.flatMap(mapLike -> dynamicOps.mergeToMap(leftoverNbt.convert(dynamicOps).getValue(), mapLike))
				.map(object -> new Dynamic<>(dynamicOps, (T)object))
				.result()
				.orElse(data);
		}

		public boolean itemEquals(String itemId) {
			return this.itemId.equals(itemId);
		}

		public boolean itemMatches(Set<String> itemIds) {
			return itemIds.contains(this.itemId);
		}

		public boolean itemContains(String componentId) {
			return this.components.get(componentId).result().isPresent();
		}
	}
}
