package net.minecraft.data.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.EquipmentModel;
import net.minecraft.item.equipment.EquipmentModels;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ItemModelGenerator {
	public static final Identifier TRIM_TYPE = Identifier.ofVanilla("trim_type");
	private static final List<ItemModelGenerator.TrimMaterial> TRIM_MATERIALS = List.of(
		new ItemModelGenerator.TrimMaterial("quartz", 0.1F, Map.of()),
		new ItemModelGenerator.TrimMaterial("iron", 0.2F, Map.of(EquipmentModels.IRON, "iron_darker")),
		new ItemModelGenerator.TrimMaterial("netherite", 0.3F, Map.of(EquipmentModels.NETHERITE, "netherite_darker")),
		new ItemModelGenerator.TrimMaterial("redstone", 0.4F, Map.of()),
		new ItemModelGenerator.TrimMaterial("copper", 0.5F, Map.of()),
		new ItemModelGenerator.TrimMaterial("gold", 0.6F, Map.of(EquipmentModels.GOLD, "gold_darker")),
		new ItemModelGenerator.TrimMaterial("emerald", 0.7F, Map.of()),
		new ItemModelGenerator.TrimMaterial("diamond", 0.8F, Map.of(EquipmentModels.DIAMOND, "diamond_darker")),
		new ItemModelGenerator.TrimMaterial("lapis", 0.9F, Map.of()),
		new ItemModelGenerator.TrimMaterial("amethyst", 1.0F, Map.of())
	);
	private final BiConsumer<Identifier, Supplier<JsonElement>> writer;

	public ItemModelGenerator(BiConsumer<Identifier, Supplier<JsonElement>> writer) {
		this.writer = writer;
	}

	private void register(Item item, Model model) {
		model.upload(ModelIds.getItemModelId(item), TextureMap.layer0(item), this.writer);
	}

	private void register(Item item, String suffix, Model model) {
		model.upload(ModelIds.getItemSubModelId(item, suffix), TextureMap.layer0(TextureMap.getSubId(item, suffix)), this.writer);
	}

	private void register(Item item, Item texture, Model model) {
		model.upload(ModelIds.getItemModelId(item), TextureMap.layer0(texture), this.writer);
	}

	private void registerWolfArmor(Item armor) {
		this.uploadArmor(ModelIds.getItemModelId(armor), TextureMap.getId(armor), TextureMap.getSubId(armor, "_overlay"));
	}

	private void registerCompass(Item compass) {
		for (int i = 0; i < 32; i++) {
			if (i != 16) {
				this.register(compass, String.format(Locale.ROOT, "_%02d", i), Models.GENERATED);
			}
		}
	}

	private void registerClock(Item clock) {
		for (int i = 1; i < 64; i++) {
			this.register(clock, String.format(Locale.ROOT, "_%02d", i), Models.GENERATED);
		}
	}

	private void uploadArmor(Identifier id, Identifier layer0, Identifier layer1) {
		Models.GENERATED_TWO_LAYERS.upload(id, TextureMap.layered(layer0, layer1), this.writer);
	}

	private void uploadArmor(Identifier id, Identifier layer0, Identifier layer1, Identifier layer2) {
		Models.GENERATED_THREE_LAYERS.upload(id, TextureMap.layered(layer0, layer1, layer2), this.writer);
	}

	private Identifier suffixTrim(Identifier id, String trimMaterialName) {
		return id.withSuffixedPath("_" + trimMaterialName + "_trim");
	}

	private JsonObject createArmorJson(Identifier id, Map<TextureKey, Identifier> textures, Identifier modelId) {
		JsonObject jsonObject = Models.GENERATED_TWO_LAYERS.createJson(id, textures);
		JsonArray jsonArray = new JsonArray();

		for (ItemModelGenerator.TrimMaterial trimMaterial : TRIM_MATERIALS) {
			JsonObject jsonObject2 = new JsonObject();
			JsonObject jsonObject3 = new JsonObject();
			jsonObject3.addProperty(TRIM_TYPE.getPath(), trimMaterial.itemModelIndex());
			jsonObject2.add("predicate", jsonObject3);
			jsonObject2.addProperty("model", this.suffixTrim(id, trimMaterial.getAppliedName(modelId)).toString());
			jsonArray.add(jsonObject2);
		}

		jsonObject.add("overrides", jsonArray);
		return jsonObject;
	}

	private void registerArmor(Item item, Identifier modelId, EquipmentModel model, EquipmentSlot slot) {
		List<EquipmentModel.Layer> list = model.getLayers(EquipmentModel.LayerType.HUMANOID);
		if (!list.isEmpty()) {
			boolean bl = list.size() == 2 && ((EquipmentModel.Layer)list.getFirst()).dyeable().isPresent();
			Identifier identifier = ModelIds.getItemModelId(item);
			Identifier identifier2 = TextureMap.getId(item);
			Identifier identifier3 = TextureMap.getSubId(item, "_overlay");
			if (bl) {
				Models.GENERATED_TWO_LAYERS
					.upload(identifier, TextureMap.layered(identifier2, identifier3), this.writer, (id, textures) -> this.createArmorJson(id, textures, modelId));
			} else {
				Models.GENERATED.upload(identifier, TextureMap.layer0(identifier2), this.writer, (id, textures) -> this.createArmorJson(id, textures, modelId));
			}
			String string = switch (slot) {
				case HEAD -> "helmet";
				case CHEST -> "chestplate";
				case LEGS -> "leggings";
				case FEET -> "boots";
				default -> throw new UnsupportedOperationException();
			};

			for (ItemModelGenerator.TrimMaterial trimMaterial : TRIM_MATERIALS) {
				String string2 = trimMaterial.getAppliedName(modelId);
				Identifier identifier4 = this.suffixTrim(identifier, string2);
				String string3 = string + "_trim_" + string2;
				Identifier identifier5 = Identifier.ofVanilla(string3).withPrefixedPath("trims/items/");
				if (bl) {
					this.uploadArmor(identifier4, identifier2, identifier3, identifier5);
				} else {
					this.uploadArmor(identifier4, identifier2, identifier5);
				}
			}
		}
	}

	public void register() {
		this.register(Items.ACACIA_BOAT, Models.GENERATED);
		this.register(Items.CHERRY_BOAT, Models.GENERATED);
		this.register(Items.ACACIA_CHEST_BOAT, Models.GENERATED);
		this.register(Items.CHERRY_CHEST_BOAT, Models.GENERATED);
		this.register(Items.AMETHYST_SHARD, Models.GENERATED);
		this.register(Items.APPLE, Models.GENERATED);
		this.register(Items.ARMADILLO_SCUTE, Models.GENERATED);
		this.register(Items.ARMOR_STAND, Models.GENERATED);
		this.register(Items.ARROW, Models.GENERATED);
		this.register(Items.BAKED_POTATO, Models.GENERATED);
		this.register(Items.BAMBOO, Models.HANDHELD);
		this.register(Items.BEEF, Models.GENERATED);
		this.register(Items.BEETROOT, Models.GENERATED);
		this.register(Items.BEETROOT_SOUP, Models.GENERATED);
		this.register(Items.BIRCH_BOAT, Models.GENERATED);
		this.register(Items.BIRCH_CHEST_BOAT, Models.GENERATED);
		this.register(Items.BLACK_DYE, Models.GENERATED);
		this.register(Items.BLAZE_POWDER, Models.GENERATED);
		this.register(Items.BLAZE_ROD, Models.HANDHELD);
		this.register(Items.BLUE_DYE, Models.GENERATED);
		this.register(Items.BONE_MEAL, Models.GENERATED);
		this.register(Items.BORDURE_INDENTED_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.BOOK, Models.GENERATED);
		this.register(Items.BOWL, Models.GENERATED);
		this.register(Items.BREAD, Models.GENERATED);
		this.register(Items.BRICK, Models.GENERATED);
		this.register(Items.BREEZE_ROD, Models.HANDHELD);
		this.register(Items.BROWN_DYE, Models.GENERATED);
		this.register(Items.BUCKET, Models.GENERATED);
		this.register(Items.CARROT_ON_A_STICK, Models.HANDHELD_ROD);
		this.register(Items.WARPED_FUNGUS_ON_A_STICK, Models.HANDHELD_ROD);
		this.register(Items.CHARCOAL, Models.GENERATED);
		this.register(Items.CHEST_MINECART, Models.GENERATED);
		this.register(Items.CHICKEN, Models.GENERATED);
		this.register(Items.CHORUS_FRUIT, Models.GENERATED);
		this.register(Items.CLAY_BALL, Models.GENERATED);
		this.registerClock(Items.CLOCK);
		this.register(Items.COAL, Models.GENERATED);
		this.register(Items.COD_BUCKET, Models.GENERATED);
		this.register(Items.COMMAND_BLOCK_MINECART, Models.GENERATED);
		this.registerCompass(Items.COMPASS);
		this.registerCompass(Items.RECOVERY_COMPASS);
		this.register(Items.COOKED_BEEF, Models.GENERATED);
		this.register(Items.COOKED_CHICKEN, Models.GENERATED);
		this.register(Items.COOKED_COD, Models.GENERATED);
		this.register(Items.COOKED_MUTTON, Models.GENERATED);
		this.register(Items.COOKED_PORKCHOP, Models.GENERATED);
		this.register(Items.COOKED_RABBIT, Models.GENERATED);
		this.register(Items.COOKED_SALMON, Models.GENERATED);
		this.register(Items.COOKIE, Models.GENERATED);
		this.register(Items.RAW_COPPER, Models.GENERATED);
		this.register(Items.COPPER_INGOT, Models.GENERATED);
		this.register(Items.CREEPER_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.CYAN_DYE, Models.GENERATED);
		this.register(Items.DARK_OAK_BOAT, Models.GENERATED);
		this.register(Items.DARK_OAK_CHEST_BOAT, Models.GENERATED);
		this.register(Items.DIAMOND, Models.GENERATED);
		this.register(Items.DIAMOND_AXE, Models.HANDHELD);
		this.register(Items.DIAMOND_HOE, Models.HANDHELD);
		this.register(Items.DIAMOND_HORSE_ARMOR, Models.GENERATED);
		this.register(Items.DIAMOND_PICKAXE, Models.HANDHELD);
		this.register(Items.DIAMOND_SHOVEL, Models.HANDHELD);
		this.register(Items.DIAMOND_SWORD, Models.HANDHELD);
		this.register(Items.DRAGON_BREATH, Models.GENERATED);
		this.register(Items.DRIED_KELP, Models.GENERATED);
		this.register(Items.EGG, Models.GENERATED);
		this.register(Items.EMERALD, Models.GENERATED);
		this.register(Items.ENCHANTED_BOOK, Models.GENERATED);
		this.register(Items.ENDER_EYE, Models.GENERATED);
		this.register(Items.ENDER_PEARL, Models.GENERATED);
		this.register(Items.END_CRYSTAL, Models.GENERATED);
		this.register(Items.EXPERIENCE_BOTTLE, Models.GENERATED);
		this.register(Items.FERMENTED_SPIDER_EYE, Models.GENERATED);
		this.register(Items.FIELD_MASONED_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.FIREWORK_ROCKET, Models.GENERATED);
		this.register(Items.FIRE_CHARGE, Models.GENERATED);
		this.register(Items.FLINT, Models.GENERATED);
		this.register(Items.FLINT_AND_STEEL, Models.GENERATED);
		this.register(Items.FLOW_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.FLOWER_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.FURNACE_MINECART, Models.GENERATED);
		this.register(Items.GHAST_TEAR, Models.GENERATED);
		this.register(Items.GLASS_BOTTLE, Models.GENERATED);
		this.register(Items.GLISTERING_MELON_SLICE, Models.GENERATED);
		this.register(Items.GLOBE_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.GLOW_BERRIES, Models.GENERATED);
		this.register(Items.GLOWSTONE_DUST, Models.GENERATED);
		this.register(Items.GLOW_INK_SAC, Models.GENERATED);
		this.register(Items.GLOW_ITEM_FRAME, Models.GENERATED);
		this.register(Items.RAW_GOLD, Models.GENERATED);
		this.register(Items.GOLDEN_APPLE, Models.GENERATED);
		this.register(Items.GOLDEN_AXE, Models.HANDHELD);
		this.register(Items.GOLDEN_CARROT, Models.GENERATED);
		this.register(Items.GOLDEN_HOE, Models.HANDHELD);
		this.register(Items.GOLDEN_HORSE_ARMOR, Models.GENERATED);
		this.register(Items.GOLDEN_PICKAXE, Models.HANDHELD);
		this.register(Items.GOLDEN_SHOVEL, Models.HANDHELD);
		this.register(Items.GOLDEN_SWORD, Models.HANDHELD);
		this.register(Items.GOLD_INGOT, Models.GENERATED);
		this.register(Items.GOLD_NUGGET, Models.GENERATED);
		this.register(Items.GRAY_DYE, Models.GENERATED);
		this.register(Items.GREEN_DYE, Models.GENERATED);
		this.register(Items.GUNPOWDER, Models.GENERATED);
		this.register(Items.GUSTER_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.HEART_OF_THE_SEA, Models.GENERATED);
		this.register(Items.HONEYCOMB, Models.GENERATED);
		this.register(Items.HONEY_BOTTLE, Models.GENERATED);
		this.register(Items.HOPPER_MINECART, Models.GENERATED);
		this.register(Items.INK_SAC, Models.GENERATED);
		this.register(Items.RAW_IRON, Models.GENERATED);
		this.register(Items.IRON_AXE, Models.HANDHELD);
		this.register(Items.IRON_HOE, Models.HANDHELD);
		this.register(Items.IRON_HORSE_ARMOR, Models.GENERATED);
		this.register(Items.IRON_INGOT, Models.GENERATED);
		this.register(Items.IRON_NUGGET, Models.GENERATED);
		this.register(Items.IRON_PICKAXE, Models.HANDHELD);
		this.register(Items.IRON_SHOVEL, Models.HANDHELD);
		this.register(Items.IRON_SWORD, Models.HANDHELD);
		this.register(Items.ITEM_FRAME, Models.GENERATED);
		this.register(Items.JUNGLE_BOAT, Models.GENERATED);
		this.register(Items.JUNGLE_CHEST_BOAT, Models.GENERATED);
		this.register(Items.KNOWLEDGE_BOOK, Models.GENERATED);
		this.register(Items.LAPIS_LAZULI, Models.GENERATED);
		this.register(Items.LAVA_BUCKET, Models.GENERATED);
		this.register(Items.LEATHER, Models.GENERATED);
		this.register(Items.LEATHER_HORSE_ARMOR, Models.GENERATED);
		this.register(Items.LIGHT_BLUE_DYE, Models.GENERATED);
		this.register(Items.LIGHT_GRAY_DYE, Models.GENERATED);
		this.register(Items.LIME_DYE, Models.GENERATED);
		this.register(Items.MAGENTA_DYE, Models.GENERATED);
		this.register(Items.MAGMA_CREAM, Models.GENERATED);
		this.register(Items.MANGROVE_BOAT, Models.GENERATED);
		this.register(Items.MANGROVE_CHEST_BOAT, Models.GENERATED);
		this.register(Items.BAMBOO_RAFT, Models.GENERATED);
		this.register(Items.BAMBOO_CHEST_RAFT, Models.GENERATED);
		this.register(Items.MAP, Models.GENERATED);
		this.register(Items.MELON_SLICE, Models.GENERATED);
		this.register(Items.MILK_BUCKET, Models.GENERATED);
		this.register(Items.MINECART, Models.GENERATED);
		this.register(Items.MOJANG_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.MUSHROOM_STEW, Models.GENERATED);
		this.register(Items.DISC_FRAGMENT_5, Models.GENERATED);
		this.register(Items.MUSIC_DISC_11, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_13, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_BLOCKS, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_CAT, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_CHIRP, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_CREATOR, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_CREATOR_MUSIC_BOX, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_FAR, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_MALL, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_MELLOHI, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_PIGSTEP, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_PRECIPICE, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_STAL, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_STRAD, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_WAIT, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_WARD, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_OTHERSIDE, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_RELIC, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUSIC_DISC_5, Models.TEMPLATE_MUSIC_DISC);
		this.register(Items.MUTTON, Models.GENERATED);
		this.register(Items.NAME_TAG, Models.GENERATED);
		this.register(Items.NAUTILUS_SHELL, Models.GENERATED);
		this.register(Items.NETHERITE_AXE, Models.HANDHELD);
		this.register(Items.NETHERITE_HOE, Models.HANDHELD);
		this.register(Items.NETHERITE_INGOT, Models.GENERATED);
		this.register(Items.NETHERITE_PICKAXE, Models.HANDHELD);
		this.register(Items.NETHERITE_SCRAP, Models.GENERATED);
		this.register(Items.NETHERITE_SHOVEL, Models.HANDHELD);
		this.register(Items.NETHERITE_SWORD, Models.HANDHELD);
		this.register(Items.NETHER_BRICK, Models.GENERATED);
		this.register(Items.NETHER_STAR, Models.GENERATED);
		this.register(Items.OAK_BOAT, Models.GENERATED);
		this.register(Items.OAK_CHEST_BOAT, Models.GENERATED);
		this.register(Items.ORANGE_DYE, Models.GENERATED);
		this.register(Items.PAINTING, Models.GENERATED);
		this.register(Items.PAPER, Models.GENERATED);
		this.register(Items.PHANTOM_MEMBRANE, Models.GENERATED);
		this.register(Items.PIGLIN_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.PINK_DYE, Models.GENERATED);
		this.register(Items.POISONOUS_POTATO, Models.GENERATED);
		this.register(Items.POPPED_CHORUS_FRUIT, Models.GENERATED);
		this.register(Items.PORKCHOP, Models.GENERATED);
		this.register(Items.POWDER_SNOW_BUCKET, Models.GENERATED);
		this.register(Items.PRISMARINE_CRYSTALS, Models.GENERATED);
		this.register(Items.PRISMARINE_SHARD, Models.GENERATED);
		this.register(Items.PUFFERFISH, Models.GENERATED);
		this.register(Items.PUFFERFISH_BUCKET, Models.GENERATED);
		this.register(Items.PUMPKIN_PIE, Models.GENERATED);
		this.register(Items.PURPLE_DYE, Models.GENERATED);
		this.register(Items.QUARTZ, Models.GENERATED);
		this.register(Items.RABBIT, Models.GENERATED);
		this.register(Items.RABBIT_FOOT, Models.GENERATED);
		this.register(Items.RABBIT_HIDE, Models.GENERATED);
		this.register(Items.RABBIT_STEW, Models.GENERATED);
		this.register(Items.RED_DYE, Models.GENERATED);
		this.register(Items.ROTTEN_FLESH, Models.GENERATED);
		this.register(Items.SADDLE, Models.GENERATED);
		this.register(Items.SALMON, Models.GENERATED);
		this.register(Items.SALMON_BUCKET, Models.GENERATED);
		this.register(Items.TURTLE_SCUTE, Models.GENERATED);
		this.register(Items.SHEARS, Models.GENERATED);
		this.register(Items.SHULKER_SHELL, Models.GENERATED);
		this.register(Items.SKULL_BANNER_PATTERN, Models.GENERATED);
		this.register(Items.SLIME_BALL, Models.GENERATED);
		this.register(Items.SNOWBALL, Models.GENERATED);
		this.register(Items.ECHO_SHARD, Models.GENERATED);
		this.register(Items.SPECTRAL_ARROW, Models.GENERATED);
		this.register(Items.SPIDER_EYE, Models.GENERATED);
		this.register(Items.SPRUCE_BOAT, Models.GENERATED);
		this.register(Items.SPRUCE_CHEST_BOAT, Models.GENERATED);
		this.register(Items.SPYGLASS, Models.GENERATED);
		this.register(Items.STICK, Models.HANDHELD);
		this.register(Items.STONE_AXE, Models.HANDHELD);
		this.register(Items.STONE_HOE, Models.HANDHELD);
		this.register(Items.STONE_PICKAXE, Models.HANDHELD);
		this.register(Items.STONE_SHOVEL, Models.HANDHELD);
		this.register(Items.STONE_SWORD, Models.HANDHELD);
		this.register(Items.SUGAR, Models.GENERATED);
		this.register(Items.SUSPICIOUS_STEW, Models.GENERATED);
		this.register(Items.TNT_MINECART, Models.GENERATED);
		this.register(Items.TOTEM_OF_UNDYING, Models.GENERATED);
		this.register(Items.TRIDENT, Models.GENERATED);
		this.register(Items.TROPICAL_FISH, Models.GENERATED);
		this.register(Items.TROPICAL_FISH_BUCKET, Models.GENERATED);
		this.register(Items.AXOLOTL_BUCKET, Models.GENERATED);
		this.register(Items.TADPOLE_BUCKET, Models.GENERATED);
		this.register(Items.WATER_BUCKET, Models.GENERATED);
		this.register(Items.WHEAT, Models.GENERATED);
		this.register(Items.WHITE_DYE, Models.GENERATED);
		this.register(Items.WIND_CHARGE, Models.GENERATED);
		this.register(Items.MACE, Models.HANDHELD_MACE);
		this.registerWolfArmor(Items.WOLF_ARMOR);
		this.register(Items.WOODEN_AXE, Models.HANDHELD);
		this.register(Items.WOODEN_HOE, Models.HANDHELD);
		this.register(Items.WOODEN_PICKAXE, Models.HANDHELD);
		this.register(Items.WOODEN_SHOVEL, Models.HANDHELD);
		this.register(Items.WOODEN_SWORD, Models.HANDHELD);
		this.register(Items.WRITABLE_BOOK, Models.GENERATED);
		this.register(Items.WRITTEN_BOOK, Models.GENERATED);
		this.register(Items.YELLOW_DYE, Models.GENERATED);
		this.register(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, Models.GENERATED);
		this.register(Items.DEBUG_STICK, Items.STICK, Models.HANDHELD);
		this.register(Items.ENCHANTED_GOLDEN_APPLE, Items.GOLDEN_APPLE, Models.GENERATED);
		Map<Identifier, EquipmentModel> map = new HashMap();
		EquipmentModels.accept(map::put);

		for (Item item : Registries.ITEM) {
			EquippableComponent equippableComponent = item.getComponents().get(DataComponentTypes.EQUIPPABLE);
			if (equippableComponent != null && equippableComponent.slot().getType() == EquipmentSlot.Type.HUMANOID_ARMOR && equippableComponent.model().isPresent()) {
				Identifier identifier = (Identifier)equippableComponent.model().get();
				EquipmentModel equipmentModel = (EquipmentModel)map.get(identifier);
				if (equipmentModel == null) {
					throw new IllegalStateException("Referenced equipment model does not exist: " + identifier);
				}

				this.registerArmor(item, identifier, equipmentModel, equippableComponent.slot());
			}
		}

		this.register(Items.ANGLER_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.ARCHER_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.ARMS_UP_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.BLADE_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.BREWER_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.BURN_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.DANGER_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.EXPLORER_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.FLOW_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.FRIEND_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.GUSTER_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.HEART_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.HEARTBREAK_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.HOWL_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.MINER_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.MOURNER_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.PLENTY_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.PRIZE_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.SCRAPE_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.SHEAF_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.SHELTER_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.SKULL_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.SNORT_POTTERY_SHERD, Models.GENERATED);
		this.register(Items.TRIAL_KEY, Models.GENERATED);
		this.register(Items.OMINOUS_TRIAL_KEY, Models.GENERATED);
		this.register(Items.OMINOUS_BOTTLE, Models.GENERATED);
		this.register(Items.BUNDLE, Models.GENERATED);
		this.register(Items.BLACK_BUNDLE, Models.GENERATED);
		this.register(Items.WHITE_BUNDLE, Models.GENERATED);
		this.register(Items.GRAY_BUNDLE, Models.GENERATED);
		this.register(Items.LIGHT_GRAY_BUNDLE, Models.GENERATED);
		this.register(Items.LIGHT_BLUE_BUNDLE, Models.GENERATED);
		this.register(Items.BLUE_BUNDLE, Models.GENERATED);
		this.register(Items.CYAN_BUNDLE, Models.GENERATED);
		this.register(Items.YELLOW_BUNDLE, Models.GENERATED);
		this.register(Items.RED_BUNDLE, Models.GENERATED);
		this.register(Items.PURPLE_BUNDLE, Models.GENERATED);
		this.register(Items.MAGENTA_BUNDLE, Models.GENERATED);
		this.register(Items.PINK_BUNDLE, Models.GENERATED);
		this.register(Items.GREEN_BUNDLE, Models.GENERATED);
		this.register(Items.LIME_BUNDLE, Models.GENERATED);
		this.register(Items.BROWN_BUNDLE, Models.GENERATED);
		this.register(Items.ORANGE_BUNDLE, Models.GENERATED);
	}

	static record TrimMaterial(String name, float itemModelIndex, Map<Identifier, String> overrideArmorMaterials) {
		public String getAppliedName(Identifier modelId) {
			return (String)this.overrideArmorMaterials.getOrDefault(modelId, this.name);
		}
	}
}
