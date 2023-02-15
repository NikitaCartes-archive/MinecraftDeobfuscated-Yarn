/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client;

import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

public class Models {
    public static final Model CUBE = Models.block("cube", TextureKey.PARTICLE, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST, TextureKey.UP, TextureKey.DOWN);
    public static final Model CUBE_DIRECTIONAL = Models.block("cube_directional", TextureKey.PARTICLE, TextureKey.NORTH, TextureKey.SOUTH, TextureKey.EAST, TextureKey.WEST, TextureKey.UP, TextureKey.DOWN);
    public static final Model CUBE_ALL = Models.block("cube_all", TextureKey.ALL);
    public static final Model CUBE_MIRRORED_ALL = Models.block("cube_mirrored_all", "_mirrored", TextureKey.ALL);
    public static final Model CUBE_NORTH_WEST_MIRRORED_ALL = Models.block("cube_north_west_mirrored_all", "_north_west_mirrored", TextureKey.ALL);
    public static final Model CUBE_COLUMN_UV_LOCKED_X = Models.block("cube_column_uv_locked_x", "_x", TextureKey.END, TextureKey.SIDE);
    public static final Model CUBE_COLUMN_UV_LOCKED_Y = Models.block("cube_column_uv_locked_y", "_y", TextureKey.END, TextureKey.SIDE);
    public static final Model CUBE_COLUMN_UV_LOCKED_Z = Models.block("cube_column_uv_locked_z", "_z", TextureKey.END, TextureKey.SIDE);
    public static final Model CUBE_COLUMN = Models.block("cube_column", TextureKey.END, TextureKey.SIDE);
    public static final Model CUBE_COLUMN_HORIZONTAL = Models.block("cube_column_horizontal", "_horizontal", TextureKey.END, TextureKey.SIDE);
    public static final Model CUBE_COLUMN_MIRRORED = Models.block("cube_column_mirrored", "_mirrored", TextureKey.END, TextureKey.SIDE);
    public static final Model CUBE_TOP = Models.block("cube_top", TextureKey.TOP, TextureKey.SIDE);
    public static final Model CUBE_BOTTOM_TOP = Models.block("cube_bottom_top", TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE);
    public static final Model ORIENTABLE = Models.block("orientable", TextureKey.TOP, TextureKey.FRONT, TextureKey.SIDE);
    public static final Model ORIENTABLE_WITH_BOTTOM = Models.block("orientable_with_bottom", TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.FRONT);
    public static final Model ORIENTABLE_VERTICAL = Models.block("orientable_vertical", "_vertical", TextureKey.FRONT, TextureKey.SIDE);
    public static final Model BUTTON = Models.block("button", TextureKey.TEXTURE);
    public static final Model BUTTON_PRESSED = Models.block("button_pressed", "_pressed", TextureKey.TEXTURE);
    public static final Model BUTTON_INVENTORY = Models.block("button_inventory", "_inventory", TextureKey.TEXTURE);
    public static final Model DOOR_BOTTOM_LEFT = Models.block("door_bottom_left", "_bottom_left", TextureKey.TOP, TextureKey.BOTTOM);
    public static final Model DOOR_BOTTOM_LEFT_OPEN = Models.block("door_bottom_left_open", "_bottom_left_open", TextureKey.TOP, TextureKey.BOTTOM);
    public static final Model DOOR_BOTTOM_RIGHT = Models.block("door_bottom_right", "_bottom_right", TextureKey.TOP, TextureKey.BOTTOM);
    public static final Model DOOR_BOTTOM_RIGHT_OPEN = Models.block("door_bottom_right_open", "_bottom_right_open", TextureKey.TOP, TextureKey.BOTTOM);
    public static final Model DOOR_TOP_LEFT = Models.block("door_top_left", "_top_left", TextureKey.TOP, TextureKey.BOTTOM);
    public static final Model DOOR_TOP_LEFT_OPEN = Models.block("door_top_left_open", "_top_left_open", TextureKey.TOP, TextureKey.BOTTOM);
    public static final Model DOOR_TOP_RIGHT = Models.block("door_top_right", "_top_right", TextureKey.TOP, TextureKey.BOTTOM);
    public static final Model DOOR_TOP_RIGHT_OPEN = Models.block("door_top_right_open", "_top_right_open", TextureKey.TOP, TextureKey.BOTTOM);
    public static final Model CUSTOM_FENCE_POST = Models.block("custom_fence_post", "_post", TextureKey.TEXTURE, TextureKey.PARTICLE);
    public static final Model CUSTOM_FENCE_SIDE_NORTH = Models.block("custom_fence_side_north", "_side_north", TextureKey.TEXTURE);
    public static final Model CUSTOM_FENCE_SIDE_EAST = Models.block("custom_fence_side_east", "_side_east", TextureKey.TEXTURE);
    public static final Model CUSTOM_FENCE_SIDE_SOUTH = Models.block("custom_fence_side_south", "_side_south", TextureKey.TEXTURE);
    public static final Model CUSTOM_FENCE_SIDE_WEST = Models.block("custom_fence_side_west", "_side_west", TextureKey.TEXTURE);
    public static final Model CUSTOM_FENCE_INVENTORY = Models.block("custom_fence_inventory", "_inventory", TextureKey.TEXTURE);
    public static final Model FENCE_POST = Models.block("fence_post", "_post", TextureKey.TEXTURE);
    public static final Model FENCE_SIDE = Models.block("fence_side", "_side", TextureKey.TEXTURE);
    public static final Model FENCE_INVENTORY = Models.block("fence_inventory", "_inventory", TextureKey.TEXTURE);
    public static final Model TEMPLATE_WALL_POST = Models.block("template_wall_post", "_post", TextureKey.WALL);
    public static final Model TEMPLATE_WALL_SIDE = Models.block("template_wall_side", "_side", TextureKey.WALL);
    public static final Model TEMPLATE_WALL_SIDE_TALL = Models.block("template_wall_side_tall", "_side_tall", TextureKey.WALL);
    public static final Model WALL_INVENTORY = Models.block("wall_inventory", "_inventory", TextureKey.WALL);
    public static final Model TEMPLATE_CUSTOM_FENCE_GATE = Models.block("template_custom_fence_gate", TextureKey.TEXTURE, TextureKey.PARTICLE);
    public static final Model TEMPLATE_CUSTOM_FENCE_GATE_OPEN = Models.block("template_custom_fence_gate_open", "_open", TextureKey.TEXTURE, TextureKey.PARTICLE);
    public static final Model TEMPLATE_CUSTOM_FENCE_GATE_WALL = Models.block("template_custom_fence_gate_wall", "_wall", TextureKey.TEXTURE, TextureKey.PARTICLE);
    public static final Model TEMPLATE_CUSTOM_FENCE_GATE_WALL_OPEN = Models.block("template_custom_fence_gate_wall_open", "_wall_open", TextureKey.TEXTURE, TextureKey.PARTICLE);
    public static final Model TEMPLATE_FENCE_GATE = Models.block("template_fence_gate", TextureKey.TEXTURE);
    public static final Model TEMPLATE_FENCE_GATE_OPEN = Models.block("template_fence_gate_open", "_open", TextureKey.TEXTURE);
    public static final Model TEMPLATE_FENCE_GATE_WALL = Models.block("template_fence_gate_wall", "_wall", TextureKey.TEXTURE);
    public static final Model TEMPLATE_FENCE_GATE_WALL_OPEN = Models.block("template_fence_gate_wall_open", "_wall_open", TextureKey.TEXTURE);
    public static final Model PRESSURE_PLATE_UP = Models.block("pressure_plate_up", TextureKey.TEXTURE);
    public static final Model PRESSURE_PLATE_DOWN = Models.block("pressure_plate_down", "_down", TextureKey.TEXTURE);
    public static final Model PARTICLE = Models.make(TextureKey.PARTICLE);
    public static final Model SLAB = Models.block("slab", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    public static final Model SLAB_TOP = Models.block("slab_top", "_top", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    public static final Model LEAVES = Models.block("leaves", TextureKey.ALL);
    public static final Model STAIRS = Models.block("stairs", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    public static final Model INNER_STAIRS = Models.block("inner_stairs", "_inner", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    public static final Model OUTER_STAIRS = Models.block("outer_stairs", "_outer", TextureKey.BOTTOM, TextureKey.TOP, TextureKey.SIDE);
    public static final Model TEMPLATE_TRAPDOOR_TOP = Models.block("template_trapdoor_top", "_top", TextureKey.TEXTURE);
    public static final Model TEMPLATE_TRAPDOOR_BOTTOM = Models.block("template_trapdoor_bottom", "_bottom", TextureKey.TEXTURE);
    public static final Model TEMPLATE_TRAPDOOR_OPEN = Models.block("template_trapdoor_open", "_open", TextureKey.TEXTURE);
    public static final Model TEMPLATE_ORIENTABLE_TRAPDOOR_TOP = Models.block("template_orientable_trapdoor_top", "_top", TextureKey.TEXTURE);
    public static final Model TEMPLATE_ORIENTABLE_TRAPDOOR_BOTTOM = Models.block("template_orientable_trapdoor_bottom", "_bottom", TextureKey.TEXTURE);
    public static final Model TEMPLATE_ORIENTABLE_TRAPDOOR_OPEN = Models.block("template_orientable_trapdoor_open", "_open", TextureKey.TEXTURE);
    public static final Model POINTED_DRIPSTONE = Models.block("pointed_dripstone", TextureKey.CROSS);
    public static final Model CROSS = Models.block("cross", TextureKey.CROSS);
    public static final Model TINTED_CROSS = Models.block("tinted_cross", TextureKey.CROSS);
    public static final Model FLOWER_POT_CROSS = Models.block("flower_pot_cross", TextureKey.PLANT);
    public static final Model TINTED_FLOWER_POT_CROSS = Models.block("tinted_flower_pot_cross", TextureKey.PLANT);
    public static final Model RAIL_FLAT = Models.block("rail_flat", TextureKey.RAIL);
    public static final Model RAIL_CURVED = Models.block("rail_curved", "_corner", TextureKey.RAIL);
    public static final Model TEMPLATE_RAIL_RAISED_NE = Models.block("template_rail_raised_ne", "_raised_ne", TextureKey.RAIL);
    public static final Model TEMPLATE_RAIL_RAISED_SW = Models.block("template_rail_raised_sw", "_raised_sw", TextureKey.RAIL);
    public static final Model CARPET = Models.block("carpet", TextureKey.WOOL);
    public static final Model FLOWERBED_1 = Models.block("flowerbed_1", "_1", TextureKey.FLOWERBED, TextureKey.STEM);
    public static final Model FLOWERBED_2 = Models.block("flowerbed_2", "_2", TextureKey.FLOWERBED, TextureKey.STEM);
    public static final Model FLOWERBED_3 = Models.block("flowerbed_3", "_3", TextureKey.FLOWERBED, TextureKey.STEM);
    public static final Model FLOWERBED_4 = Models.block("flowerbed_4", "_4", TextureKey.FLOWERBED, TextureKey.STEM);
    public static final Model CORAL_FAN = Models.block("coral_fan", TextureKey.FAN);
    public static final Model CORAL_WALL_FAN = Models.block("coral_wall_fan", TextureKey.FAN);
    public static final Model TEMPLATE_GLAZED_TERRACOTTA = Models.block("template_glazed_terracotta", TextureKey.PATTERN);
    public static final Model TEMPLATE_CHORUS_FLOWER = Models.block("template_chorus_flower", TextureKey.TEXTURE);
    public static final Model TEMPLATE_DAYLIGHT_DETECTOR = Models.block("template_daylight_detector", TextureKey.TOP, TextureKey.SIDE);
    public static final Model TEMPLATE_GLASS_PANE_NOSIDE = Models.block("template_glass_pane_noside", "_noside", TextureKey.PANE);
    public static final Model TEMPLATE_GLASS_PANE_NOSIDE_ALT = Models.block("template_glass_pane_noside_alt", "_noside_alt", TextureKey.PANE);
    public static final Model TEMPLATE_GLASS_PANE_POST = Models.block("template_glass_pane_post", "_post", TextureKey.PANE, TextureKey.EDGE);
    public static final Model TEMPLATE_GLASS_PANE_SIDE = Models.block("template_glass_pane_side", "_side", TextureKey.PANE, TextureKey.EDGE);
    public static final Model TEMPLATE_GLASS_PANE_SIDE_ALT = Models.block("template_glass_pane_side_alt", "_side_alt", TextureKey.PANE, TextureKey.EDGE);
    public static final Model TEMPLATE_COMMAND_BLOCK = Models.block("template_command_block", TextureKey.FRONT, TextureKey.BACK, TextureKey.SIDE);
    public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_LEFT = Models.block("template_chiseled_bookshelf_slot_top_left", "_slot_top_left", TextureKey.TEXTURE);
    public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_MID = Models.block("template_chiseled_bookshelf_slot_top_mid", "_slot_top_mid", TextureKey.TEXTURE);
    public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_TOP_RIGHT = Models.block("template_chiseled_bookshelf_slot_top_right", "_slot_top_right", TextureKey.TEXTURE);
    public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT = Models.block("template_chiseled_bookshelf_slot_bottom_left", "_slot_bottom_left", TextureKey.TEXTURE);
    public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_MID = Models.block("template_chiseled_bookshelf_slot_bottom_mid", "_slot_bottom_mid", TextureKey.TEXTURE);
    public static final Model TEMPLATE_CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT = Models.block("template_chiseled_bookshelf_slot_bottom_right", "_slot_bottom_right", TextureKey.TEXTURE);
    public static final Model TEMPLATE_ANVIL = Models.block("template_anvil", TextureKey.TOP);
    public static final Model[] STEM_GROWTH_STAGES = (Model[])IntStream.range(0, 8).mapToObj(stage -> Models.block("stem_growth" + stage, "_stage" + stage, TextureKey.STEM)).toArray(Model[]::new);
    public static final Model STEM_FRUIT = Models.block("stem_fruit", TextureKey.STEM, TextureKey.UPPERSTEM);
    public static final Model CROP = Models.block("crop", TextureKey.CROP);
    public static final Model TEMPLATE_FARMLAND = Models.block("template_farmland", TextureKey.DIRT, TextureKey.TOP);
    public static final Model TEMPLATE_FIRE_FLOOR = Models.block("template_fire_floor", TextureKey.FIRE);
    public static final Model TEMPLATE_FIRE_SIDE = Models.block("template_fire_side", TextureKey.FIRE);
    public static final Model TEMPLATE_FIRE_SIDE_ALT = Models.block("template_fire_side_alt", TextureKey.FIRE);
    public static final Model TEMPLATE_FIRE_UP = Models.block("template_fire_up", TextureKey.FIRE);
    public static final Model TEMPLATE_FIRE_UP_ALT = Models.block("template_fire_up_alt", TextureKey.FIRE);
    public static final Model TEMPLATE_CAMPFIRE = Models.block("template_campfire", TextureKey.FIRE, TextureKey.LIT_LOG);
    public static final Model TEMPLATE_LANTERN = Models.block("template_lantern", TextureKey.LANTERN);
    public static final Model TEMPLATE_HANGING_LANTERN = Models.block("template_hanging_lantern", "_hanging", TextureKey.LANTERN);
    public static final Model TEMPLATE_TORCH = Models.block("template_torch", TextureKey.TORCH);
    public static final Model TEMPLATE_TORCH_WALL = Models.block("template_torch_wall", TextureKey.TORCH);
    public static final Model TEMPLATE_PISTON = Models.block("template_piston", TextureKey.PLATFORM, TextureKey.BOTTOM, TextureKey.SIDE);
    public static final Model TEMPLATE_PISTON_HEAD = Models.block("template_piston_head", TextureKey.PLATFORM, TextureKey.SIDE, TextureKey.UNSTICKY);
    public static final Model TEMPLATE_PISTON_HEAD_SHORT = Models.block("template_piston_head_short", TextureKey.PLATFORM, TextureKey.SIDE, TextureKey.UNSTICKY);
    public static final Model TEMPLATE_SEAGRASS = Models.block("template_seagrass", TextureKey.TEXTURE);
    public static final Model TEMPLATE_TURTLE_EGG = Models.block("template_turtle_egg", TextureKey.ALL);
    public static final Model TEMPLATE_TWO_TURTLE_EGGS = Models.block("template_two_turtle_eggs", TextureKey.ALL);
    public static final Model TEMPLATE_THREE_TURTLE_EGGS = Models.block("template_three_turtle_eggs", TextureKey.ALL);
    public static final Model TEMPLATE_FOUR_TURTLE_EGGS = Models.block("template_four_turtle_eggs", TextureKey.ALL);
    public static final Model TEMPLATE_SINGLE_FACE = Models.block("template_single_face", TextureKey.TEXTURE);
    public static final Model TEMPLATE_CAULDRON_LEVEL1 = Models.block("template_cauldron_level1", TextureKey.CONTENT, TextureKey.INSIDE, TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE);
    public static final Model TEMPLATE_CAULDRON_LEVEL2 = Models.block("template_cauldron_level2", TextureKey.CONTENT, TextureKey.INSIDE, TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE);
    public static final Model TEMPLATE_CAULDRON_FULL = Models.block("template_cauldron_full", TextureKey.CONTENT, TextureKey.INSIDE, TextureKey.PARTICLE, TextureKey.TOP, TextureKey.BOTTOM, TextureKey.SIDE);
    public static final Model TEMPLATE_AZALEA = Models.block("template_azalea", TextureKey.TOP, TextureKey.SIDE);
    public static final Model TEMPLATE_POTTED_AZALEA_BUSH = Models.block("template_potted_azalea_bush", TextureKey.TOP, TextureKey.SIDE);
    public static final Model GENERATED = Models.item("generated", TextureKey.LAYER0);
    public static final Model HANDHELD = Models.item("handheld", TextureKey.LAYER0);
    public static final Model HANDHELD_ROD = Models.item("handheld_rod", TextureKey.LAYER0);
    public static final Model GENERATED_TWO_LAYERS = Models.item("generated", TextureKey.LAYER0, TextureKey.LAYER1);
    public static final Model GENERATED_THREE_LAYERS = Models.item("generated", TextureKey.LAYER0, TextureKey.LAYER1, TextureKey.LAYER2);
    public static final Model TEMPLATE_SHULKER_BOX = Models.item("template_shulker_box", TextureKey.PARTICLE);
    public static final Model TEMPLATE_BED = Models.item("template_bed", TextureKey.PARTICLE);
    public static final Model TEMPLATE_BANNER = Models.item("template_banner", new TextureKey[0]);
    public static final Model TEMPLATE_SKULL = Models.item("template_skull", new TextureKey[0]);
    public static final Model TEMPLATE_CANDLE = Models.block("template_candle", TextureKey.ALL, TextureKey.PARTICLE);
    public static final Model TEMPLATE_TWO_CANDLES = Models.block("template_two_candles", TextureKey.ALL, TextureKey.PARTICLE);
    public static final Model TEMPLATE_THREE_CANDLES = Models.block("template_three_candles", TextureKey.ALL, TextureKey.PARTICLE);
    public static final Model TEMPLATE_FOUR_CANDLES = Models.block("template_four_candles", TextureKey.ALL, TextureKey.PARTICLE);
    public static final Model TEMPLATE_CAKE_WITH_CANDLE = Models.block("template_cake_with_candle", TextureKey.CANDLE, TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.TOP, TextureKey.PARTICLE);
    public static final Model TEMPLATE_SCULK_SHRIEKER = Models.block("template_sculk_shrieker", TextureKey.BOTTOM, TextureKey.SIDE, TextureKey.TOP, TextureKey.PARTICLE, TextureKey.INNER_TOP);

    private static Model make(TextureKey ... requiredTextureKeys) {
        return new Model(Optional.empty(), Optional.empty(), requiredTextureKeys);
    }

    private static Model block(String parent, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(new Identifier("minecraft", "block/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    private static Model item(String parent, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(new Identifier("minecraft", "item/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    private static Model block(String parent, String variant, TextureKey ... requiredTextureKeys) {
        return new Model(Optional.of(new Identifier("minecraft", "block/" + parent)), Optional.of(variant), requiredTextureKeys);
    }
}

