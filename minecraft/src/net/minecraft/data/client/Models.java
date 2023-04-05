package net.minecraft.data.client;

import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.util.Identifier;

public class Models {
	public static final Model CUBE = block(
		"cube", TextureKey.PARTICLE, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST, TextureKey.UP, TextureKey.DOWN
	);
	public static final Model CUBE_DIRECTIONAL = block(
		"cube_directional", TextureKey.PARTICLE, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST, TextureKey.UP, TextureKey.DOWN
	);
	public static final Model CUBE_ALL = block("cube_all", TextureKey.ALL);
	public static final Model CUBE_MIRRORED_ALL = block("cube_mirrored_all", "_mirrored", TextureKey.ALL);
	public static final Model CUBE_NORTH_WEST_MIRRORED_ALL = block("cube_north_west_mirrored_all", "_north_west_mirrored", TextureKey.ALL);
	public static final Model CUBE_COLUMN_UV_LOCKED_X = block("cube_column_uv_locked_x", "_x", TextureKey.END, TextureKey.SIDE);
	public static final Model CUBE_COLUMN_UV_LOCKED_Y = block("cube_column_uv_locked_y", "_y", TextureKey.END, TextureKey.SIDE);
	public static final Model CUBE_COLUMN_UV_LOCKED_Z = block("cube_column_uv_locked_z", "_z", TextureKey.END, TextureKey.SIDE);
	public static final Model CUBE_COLUMN = block("cube_column", TextureKey.END, TextureKey.SIDE);
	public static final Model CUBE_COLUMN_HORIZONTAL = block("cube_column_horizontal", "_horizontal", TextureKey.END, TextureKey.SIDE);
	public static final Model CUBE_COLUMN_MIRRORED = block("cube_column_mirrored", "_mirrored", TextureKey.END, TextureKey.SIDE);
	public static final Model CUBE_TOP = block("cube_top", TextureKey.TOP, TextureKey.SIDE);
	public static final Model CUBE_BOTTOM_TOP = block("cube_bottom_top", TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE);
	public static final Model ORIENTABLE = block("orientable", TextureKey.TOP, TextureKey.FRONT, TextureKey.SIDE);
	public static final Model ORIENTABLE_WITH_BOTTOM = block("orientable_with_bottom", TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.FRONT);
	public static final Model ORIENTABLE_VERTICAL = block("orientable_vertical", "_vertical", TextureKey.FRONT, TextureKey.SIDE);
	public static final Model BUTTON = block("button", TextureKey.TEXTURE);
	public static final Model BUTTON_PRESSED = block("button_pressed", "_pressed", TextureKey.TEXTURE);
	public static final Model BUTTON_INVENTORY = block("button_inventory", "_inventory", TextureKey.TEXTURE);
	public static final Model DOOR_BOTTOM_LEFT = block("door_bottom_left", "_bottom_left", TextureKey.TOP, TextureKey.BOTTOM);
	public static final Model DOOR_BOTTOM_LEFT_OPEN = block("door_bottom_left_open", "_bottom_left_open", TextureKey.TOP, TextureKey.BOTTOM);
	public static final Model DOOR_BOTTOM_RIGHT = block("door_bottom_right", "_bottom_right", TextureKey.TOP, TextureKey.BOTTOM);
	public static final Model DOOR_BOTTOM_RIGHT_OPEN = block("door_bottom_right_open", "_bottom_right_open", TextureKey.TOP, TextureKey.BOTTOM);
	public static final Model DOOR_TOP_LEFT = block("door_top_left", "_top_left", TextureKey.TOP, TextureKey.BOTTOM);
	public static final Model DOOR_TOP_LEFT_OPEN = block("door_top_left_open", "_top_left_open", TextureKey.TOP, TextureKey.BOTTOM);
	public static final Model DOOR_TOP_RIGHT = block("door_top_right", "_top_right", TextureKey.TOP, TextureKey.BOTTOM);
	public static final Model DOOR_TOP_RIGHT_OPEN = block("door_top_right_open", "_top_right_open", TextureKey.TOP, TextureKey.BOTTOM);
	public static final Model CUSTOM_FENCE_POST = block("custom_fence_post", "_post", TextureKey.TEXTURE, TextureKey.PARTICLE);
	public static final Model CUSTOM_FENCE_SIDE_NORTH = block("custom_fence_side_north", "_side_north", TextureKey.TEXTURE);
	public static final Model CUSTOM_FENCE_SIDE_EAST = block("custom_fence_side_east", "_side_east", TextureKey.TEXTURE);
	public static final Model CUSTOM_FENCE_SIDE_SOUTH = block("custom_fence_side_south", "_side_south", TextureKey.TEXTURE);
	public static final Model CUSTOM_FENCE_SIDE_WEST = block("custom_fence_side_west", "_side_west", TextureKey.TEXTURE);
	public static final Model CUSTOM_FENCE_INVENTORY = block("custom_fence_inventory", "_inventory", TextureKey.TEXTURE);
	public static final Model FENCE_POST = block("fence_post", "_post", TextureKey.TEXTURE);
	public static final Model FENCE_SIDE = block("fence_side", "_side", TextureKey.TEXTURE);
	public static final Model FENCE_INVENTORY = block("fence_inventory", "_inventory", TextureKey.TEXTURE);
	public static final Model TEMPLATE_WALL_POST = block("template_wall_post", "_post", TextureKey.WALL);
	public static final Model TEMPLATE_WALL_SIDE = block("template_wall_side", "_side", TextureKey.WALL);
	public static final Model TEMPLATE_WALL_SIDE_TALL = block("template_wall_side_tall", "_side_tall", TextureKey.WALL);
	public static final Model WALL_INVENTORY = block("wall_inventory", "_inventory", TextureKey.WALL);
	public static final Model TEMPLATE_CUSTOM_FENCE_GATE = block("template_custom_fence_gate", TextureKey.TEXTURE, TextureKey.PARTICLE);
	public static final Model TEMPLATE_CUSTOM_FENCE_GATE_OPEN = block("template_custom_fence_gate_open", "_open", TextureKey.TEXTURE, TextureKey.PARTICLE);
	public static final Model TEMPLATE_CUSTOM_FENCE_GATE_WALL = block("template_custom_fence_gate_wall", "_wall", TextureKey.TEXTURE, TextureKey.PARTICLE);
	public static final Model TEMPLATE_CUSTOM_FENCE_GATE_WALL_OPEN = block(
		"template_custom_fence_gate_wall_open", "_wall_open", TextureKey.TEXTURE, TextureKey.PARTICLE
	);
	public static final Model TEMPLATE_FENCE_GATE = block("template_fence_gate", TextureKey.TEXTURE);
	public static final Model TEMPLATE_FENCE_GATE_OPEN = block("template_fence_gate_open", "_open", TextureKey.TEXTURE);
	public static final Model TEMPLATE_FENCE_GATE_WALL = block("template_fence_gate_wall", "_wall", TextureKey.TEXTURE);
	public static final Model TEMPLATE_FENCE_GATE_WALL_OPEN = block("template_fence_gate_wall_open", "_wall_open", TextureKey.TEXTURE);
	public static final Model PRESSURE_PLATE_UP = block("pressure_plate_up", TextureKey.TEXTURE);
	public static final Model PRESSURE_PLATE_DOWN = block("pressure_plate_down", "_down", TextureKey.TEXTURE);
	public static final Model PARTICLE = make(TextureKey.PARTICLE);
	public static final Model SLAB = block("slab", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
	public static final Model SLAB_TOP = block("slab_top", "_top", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
	public static final Model LEAVES = block("leaves", TextureKey.ALL);
	public static final Model STAIRS = block("stairs", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
	public static final Model INNER_STAIRS = block("inner_stairs", "_inner", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
	public static final Model OUTER_STAIRS = block("outer_stairs", "_outer", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
	public static final Model TEMPLATE_TRAPDOOR_TOP = block("template_trapdoor_top", "_top", TextureKey.TEXTURE);
	public static final Model TEMPLATE_TRAPDOOR_BOTTOM = block("template_trapdoor_bottom", "_bottom", TextureKey.TEXTURE);
	public static final Model TEMPLATE_TRAPDOOR_OPEN = block("template_trapdoor_open", "_open", TextureKey.TEXTURE);
	public static final Model TEMPLATE_ORIENTABLE_TRAPDOOR_TOP = block("template_orientable_trapdoor_top", "_top", TextureKey.TEXTURE);
	public static final Model TEMPLATE_ORIENTABLE_TRAPDOOR_BOTTOM = block("template_orientable_trapdoor_bottom", "_bottom", TextureKey.TEXTURE);
	public static final Model TEMPLATE_ORIENTABLE_TRAPDOOR_OPEN = block("template_orientable_trapdoor_open", "_open", TextureKey.TEXTURE);
	public static final Model POINTED_DRIPSTONE = block("pointed_dripstone", TextureKey.CROSS);
	public static final Model CROSS = block("cross", TextureKey.CROSS);
	public static final Model TINTED_CROSS = block("tinted_cross", TextureKey.CROSS);
	public static final Model FLOWER_POT_CROSS = block("flower_pot_cross", TextureKey.PLANT);
	public static final Model TINTED_FLOWER_POT_CROSS = block("tinted_flower_pot_cross", TextureKey.PLANT);
	public static final Model RAIL_FLAT = block("rail_flat", TextureKey.RAIL);
	public static final Model RAIL_CURVED = block("rail_curved", "_corner", TextureKey.RAIL);
	public static final Model TEMPLATE_RAIL_RAISED_NE = block("template_rail_raised_ne", "_raised_ne", TextureKey.RAIL);
	public static final Model TEMPLATE_RAIL_RAISED_SW = block("template_rail_raised_sw", "_raised_sw", TextureKey.RAIL);
	public static final Model CARPET = block("carpet", TextureKey.WOOL);
	public static final Model FLOWERBED_1 = block("flowerbed_1", "_1", TextureKey.FLOWERBED, TextureKey.STEM);
	public static final Model FLOWERBED_2 = block("flowerbed_2", "_2", TextureKey.FLOWERBED, TextureKey.STEM);
	public static final Model FLOWERBED_3 = block("flowerbed_3", "_3", TextureKey.FLOWERBED, TextureKey.STEM);
	public static final Model FLOWERBED_4 = block("flowerbed_4", "_4", TextureKey.FLOWERBED, TextureKey.STEM);
	public static final Model CORAL_FAN = block("coral_fan", TextureKey.FAN);
	public static final Model CORAL_WALL_FAN = block("coral_wall_fan", TextureKey.FAN);
	public static final Model TEMPLATE_GLAZED_TERRACOTTA = block("template_glazed_terracotta", TextureKey.PATTERN);
	public static final Model TEMPLATE_CHORUS_FLOWER = block("template_chorus_flower", TextureKey.TEXTURE);
	public static final Model TEMPLATE_DAYLIGHT_DETECTOR = block("template_daylight_detector", TextureKey.TOP, TextureKey.SIDE);
	public static final Model TEMPLATE_GLASS_PANE_NOSIDE = block("template_glass_pane_noside", "_noside", TextureKey.PANE);
	public static final Model TEMPLATE_GLASS_PANE_NOSIDE_ALT = block("template_glass_pane_noside_alt", "_noside_alt", TextureKey.PANE);
	public static final Model TEMPLATE_GLASS_PANE_POST = block("template_glass_pane_post", "_post", TextureKey.PANE, TextureKey.EDGE);
	public static final Model TEMPLATE_GLASS_PANE_SIDE = block("template_glass_pane_side", "_side", TextureKey.PANE, TextureKey.EDGE);
	public static final Model TEMPLATE_GLASS_PANE_SIDE_ALT = block("template_glass_pane_side_alt", "_side_alt", TextureKey.PANE, TextureKey.EDGE);
	public static final Model TEMPLATE_COMMAND_BLOCK = block("template_command_block", TextureKey.FRONT, TextureKey.BACK, TextureKey.SIDE);
	public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_LEFT = block("template_chiseled_bookshelf_slot_top_left", "_slot_top_left", TextureKey.TEXTURE);
	public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_MID = block("template_chiseled_bookshelf_slot_top_mid", "_slot_top_mid", TextureKey.TEXTURE);
	public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_RIGHT = block(
		"template_chiseled_bookshelf_slot_top_right", "_slot_top_right", TextureKey.TEXTURE
	);
	public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT = block(
		"template_chiseled_bookshelf_slot_bottom_left", "_slot_bottom_left", TextureKey.TEXTURE
	);
	public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_MID = block(
		"template_chiseled_bookshelf_slot_bottom_mid", "_slot_bottom_mid", TextureKey.TEXTURE
	);
	public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT = block(
		"template_chiseled_bookshelf_slot_bottom_right", "_slot_bottom_right", TextureKey.TEXTURE
	);
	public static final Model TEMPLATE_ANVIL = block("template_anvil", TextureKey.TOP);
	public static final Model[] STEM_GROWTH_STAGES = (Model[])IntStream.range(0, 8)
		.mapToObj(stage -> block("stem_growth" + stage, "_stage" + stage, TextureKey.STEM))
		.toArray(Model[]::new);
	public static final Model STEM_FRUIT = block("stem_fruit", TextureKey.STEM, TextureKey.UPPERSTEM);
	public static final Model CROP = block("crop", TextureKey.CROP);
	public static final Model TEMPLATE_FARMLAND = block("template_farmland", TextureKey.DIRT, TextureKey.TOP);
	public static final Model TEMPLATE_FIRE_FLOOR = block("template_fire_floor", TextureKey.FIRE);
	public static final Model TEMPLATE_FIRE_SIDE = block("template_fire_side", TextureKey.FIRE);
	public static final Model TEMPLATE_FIRE_SIDE_ALT = block("template_fire_side_alt", TextureKey.FIRE);
	public static final Model TEMPLATE_FIRE_UP = block("template_fire_up", TextureKey.FIRE);
	public static final Model TEMPLATE_FIRE_UP_ALT = block("template_fire_up_alt", TextureKey.FIRE);
	public static final Model TEMPLATE_CAMPFIRE = block("template_campfire", TextureKey.FIRE, TextureKey.LIT_LOG);
	public static final Model TEMPLATE_LANTERN = block("template_lantern", TextureKey.LANTERN);
	public static final Model TEMPLATE_HANGING_LANTERN = block("template_hanging_lantern", "_hanging", TextureKey.LANTERN);
	public static final Model TEMPLATE_TORCH = block("template_torch", TextureKey.TORCH);
	public static final Model TEMPLATE_TORCH_WALL = block("template_torch_wall", TextureKey.TORCH);
	public static final Model TEMPLATE_PISTON = block("template_piston", TextureKey.PLATFORM, TextureKey.BOTTOM, TextureKey.SIDE);
	public static final Model TEMPLATE_PISTON_HEAD = block("template_piston_head", TextureKey.PLATFORM, TextureKey.SIDE, TextureKey.UNSTICKY);
	public static final Model TEMPLATE_PISTON_HEAD_SHORT = block("template_piston_head_short", TextureKey.PLATFORM, TextureKey.SIDE, TextureKey.UNSTICKY);
	public static final Model TEMPLATE_SEAGRASS = block("template_seagrass", TextureKey.TEXTURE);
	public static final Model TEMPLATE_TURTLE_EGG = block("template_turtle_egg", TextureKey.ALL);
	public static final Model TEMPLATE_TWO_TURTLE_EGGS = block("template_two_turtle_eggs", TextureKey.ALL);
	public static final Model TEMPLATE_THREE_TURTLE_EGGS = block("template_three_turtle_eggs", TextureKey.ALL);
	public static final Model TEMPLATE_FOUR_TURTLE_EGGS = block("template_four_turtle_eggs", TextureKey.ALL);
	public static final Model TEMPLATE_SINGLE_FACE = block("template_single_face", TextureKey.TEXTURE);
	public static final Model TEMPLATE_CAULDRON_LEVEL1 = block(
		"template_cauldron_level1", TextureKey.CONTENT, TextureKey.INSIDE, TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE
	);
	public static final Model TEMPLATE_CAULDRON_LEVEL2 = block(
		"template_cauldron_level2", TextureKey.CONTENT, TextureKey.INSIDE, TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE
	);
	public static final Model TEMPLATE_CAULDRON_FULL = block(
		"template_cauldron_full", TextureKey.CONTENT, TextureKey.INSIDE, TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE
	);
	public static final Model TEMPLATE_AZALEA = block("template_azalea", TextureKey.TOP, TextureKey.SIDE);
	public static final Model TEMPLATE_POTTED_AZALEA_BUSH = block("template_potted_azalea_bush", TextureKey.PLANT, TextureKey.TOP, TextureKey.SIDE);
	public static final Model TEMPLATE_POTTED_FLOWERING_AZALEA_BUSH = block("template_potted_azalea_bush", TextureKey.PLANT, TextureKey.TOP, TextureKey.SIDE);
	public static final Model SNIFFER_EGG = block(
		"sniffer_egg", TextureKey.TOP, TextureKey.BOTTOM, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST
	);
	public static final Model GENERATED = item("generated", TextureKey.LAYER0);
	public static final Model TEMPLATE_MUSIC_DISC = item("template_music_disc", TextureKey.LAYER0);
	public static final Model HANDHELD = item("handheld", TextureKey.LAYER0);
	public static final Model HANDHELD_ROD = item("handheld_rod", TextureKey.LAYER0);
	public static final Model GENERATED_TWO_LAYERS = item("generated", TextureKey.LAYER0, TextureKey.LAYER1);
	public static final Model GENERATED_THREE_LAYERS = item("generated", TextureKey.LAYER0, TextureKey.LAYER1, TextureKey.LAYER2);
	public static final Model TEMPLATE_SHULKER_BOX = item("template_shulker_box", TextureKey.PARTICLE);
	public static final Model TEMPLATE_BED = item("template_bed", TextureKey.PARTICLE);
	public static final Model TEMPLATE_BANNER = item("template_banner");
	public static final Model TEMPLATE_SKULL = item("template_skull");
	public static final Model TEMPLATE_CANDLE = block("template_candle", TextureKey.ALL, TextureKey.PARTICLE);
	public static final Model TEMPLATE_TWO_CANDLES = block("template_two_candles", TextureKey.ALL, TextureKey.PARTICLE);
	public static final Model TEMPLATE_THREE_CANDLES = block("template_three_candles", TextureKey.ALL, TextureKey.PARTICLE);
	public static final Model TEMPLATE_FOUR_CANDLES = block("template_four_candles", TextureKey.ALL, TextureKey.PARTICLE);
	public static final Model TEMPLATE_CAKE_WITH_CANDLE = block(
		"template_cake_with_candle", TextureKey.CANDLE, TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.TOP, TextureKey.PARTICLE
	);
	public static final Model TEMPLATE_SCULK_SHRIEKER = block(
		"template_sculk_shrieker", TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.TOP, TextureKey.PARTICLE, TextureKey.INNER_TOP
	);

	private static Model make(TextureKey... requiredTextureKeys) {
		return new Model(Optional.empty(), Optional.empty(), requiredTextureKeys);
	}

	private static Model block(String parent, TextureKey... requiredTextureKeys) {
		return new Model(Optional.of(new Identifier("minecraft", "block/" + parent)), Optional.empty(), requiredTextureKeys);
	}

	private static Model item(String parent, TextureKey... requiredTextureKeys) {
		return new Model(Optional.of(new Identifier("minecraft", "item/" + parent)), Optional.empty(), requiredTextureKeys);
	}

	private static Model block(String parent, String variant, TextureKey... requiredTextureKeys) {
		return new Model(Optional.of(new Identifier("minecraft", "block/" + parent)), Optional.of(variant), requiredTextureKeys);
	}
}
