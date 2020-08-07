package net.minecraft.data.client.model;

import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.util.Identifier;

public class Models {
	public static final Model field_22942 = block(
		"cube",
		TextureKey.field_23012,
		TextureKey.field_23019,
		TextureKey.field_23020,
		TextureKey.field_23021,
		TextureKey.field_23022,
		TextureKey.field_23023,
		TextureKey.field_23024
	);
	public static final Model field_23400 = block(
		"cube_directional",
		TextureKey.field_23012,
		TextureKey.field_23019,
		TextureKey.field_23020,
		TextureKey.field_23021,
		TextureKey.field_23022,
		TextureKey.field_23023,
		TextureKey.field_23024
	);
	public static final Model field_22972 = block("cube_all", TextureKey.field_23010);
	public static final Model field_22973 = block("cube_mirrored_all", "_mirrored", TextureKey.field_23010);
	public static final Model field_22974 = block("cube_column", TextureKey.field_23013, TextureKey.field_23018);
	public static final Model field_22975 = block("cube_column_horizontal", "_horizontal", TextureKey.field_23013, TextureKey.field_23018);
	public static final Model field_22976 = block("cube_top", TextureKey.field_23015, TextureKey.field_23018);
	public static final Model field_22977 = block("cube_bottom_top", TextureKey.field_23015, TextureKey.field_23014, TextureKey.field_23018);
	public static final Model field_22978 = block("orientable", TextureKey.field_23015, TextureKey.field_23016, TextureKey.field_23018);
	public static final Model field_22979 = block(
		"orientable_with_bottom", TextureKey.field_23015, TextureKey.field_23014, TextureKey.field_23018, TextureKey.field_23016
	);
	public static final Model field_22980 = block("orientable_vertical", "_vertical", TextureKey.field_23016, TextureKey.field_23018);
	public static final Model field_22981 = block("button", TextureKey.field_23011);
	public static final Model field_22982 = block("button_pressed", "_pressed", TextureKey.field_23011);
	public static final Model field_22983 = block("button_inventory", "_inventory", TextureKey.field_23011);
	public static final Model field_22984 = block("door_bottom", "_bottom", TextureKey.field_23015, TextureKey.field_23014);
	public static final Model field_22985 = block("door_bottom_rh", "_bottom_hinge", TextureKey.field_23015, TextureKey.field_23014);
	public static final Model field_22986 = block("door_top", "_top", TextureKey.field_23015, TextureKey.field_23014);
	public static final Model field_22987 = block("door_top_rh", "_top_hinge", TextureKey.field_23015, TextureKey.field_23014);
	public static final Model field_22988 = block("fence_post", "_post", TextureKey.field_23011);
	public static final Model field_22989 = block("fence_side", "_side", TextureKey.field_23011);
	public static final Model field_22990 = block("fence_inventory", "_inventory", TextureKey.field_23011);
	public static final Model field_22991 = block("template_wall_post", "_post", TextureKey.field_23027);
	public static final Model field_22992 = block("template_wall_side", "_side", TextureKey.field_23027);
	public static final Model field_22993 = block("template_wall_side_tall", "_side_tall", TextureKey.field_23027);
	public static final Model field_22994 = block("wall_inventory", "_inventory", TextureKey.field_23027);
	public static final Model field_22995 = block("template_fence_gate", TextureKey.field_23011);
	public static final Model field_22996 = block("template_fence_gate_open", "_open", TextureKey.field_23011);
	public static final Model field_22904 = block("template_fence_gate_wall", "_wall", TextureKey.field_23011);
	public static final Model field_22905 = block("template_fence_gate_wall_open", "_wall_open", TextureKey.field_23011);
	public static final Model field_22906 = block("pressure_plate_up", TextureKey.field_23011);
	public static final Model field_22907 = block("pressure_plate_down", "_down", TextureKey.field_23011);
	public static final Model PARTICLE = make(TextureKey.field_23012);
	public static final Model field_22909 = block("slab", TextureKey.field_23014, TextureKey.field_23015, TextureKey.field_23018);
	public static final Model field_22910 = block("slab_top", "_top", TextureKey.field_23014, TextureKey.field_23015, TextureKey.field_23018);
	public static final Model field_22911 = block("leaves", TextureKey.field_23010);
	public static final Model field_22912 = block("stairs", TextureKey.field_23014, TextureKey.field_23015, TextureKey.field_23018);
	public static final Model field_22913 = block("inner_stairs", "_inner", TextureKey.field_23014, TextureKey.field_23015, TextureKey.field_23018);
	public static final Model field_22914 = block("outer_stairs", "_outer", TextureKey.field_23014, TextureKey.field_23015, TextureKey.field_23018);
	public static final Model field_22915 = block("template_trapdoor_top", "_top", TextureKey.field_23011);
	public static final Model field_22916 = block("template_trapdoor_bottom", "_bottom", TextureKey.field_23011);
	public static final Model field_22917 = block("template_trapdoor_open", "_open", TextureKey.field_23011);
	public static final Model field_22918 = block("template_orientable_trapdoor_top", "_top", TextureKey.field_23011);
	public static final Model field_22919 = block("template_orientable_trapdoor_bottom", "_bottom", TextureKey.field_23011);
	public static final Model field_22920 = block("template_orientable_trapdoor_open", "_open", TextureKey.field_23011);
	public static final Model field_22921 = block("cross", TextureKey.field_23025);
	public static final Model field_22922 = block("tinted_cross", TextureKey.field_23025);
	public static final Model field_22923 = block("flower_pot_cross", TextureKey.field_23026);
	public static final Model field_22924 = block("tinted_flower_pot_cross", TextureKey.field_23026);
	public static final Model field_22925 = block("rail_flat", TextureKey.field_23028);
	public static final Model field_22926 = block("rail_curved", "_corner", TextureKey.field_23028);
	public static final Model field_22927 = block("template_rail_raised_ne", "_raised_ne", TextureKey.field_23028);
	public static final Model field_22928 = block("template_rail_raised_sw", "_raised_sw", TextureKey.field_23028);
	public static final Model field_22929 = block("carpet", TextureKey.field_23029);
	public static final Model field_22946 = block("coral_fan", TextureKey.field_23033);
	public static final Model field_22947 = block("coral_wall_fan", TextureKey.field_23033);
	public static final Model field_22948 = block("template_glazed_terracotta", TextureKey.field_23030);
	public static final Model field_22949 = block("template_chorus_flower", TextureKey.field_23011);
	public static final Model field_22950 = block("template_daylight_detector", TextureKey.field_23015, TextureKey.field_23018);
	public static final Model field_22951 = block("template_glass_pane_noside", "_noside", TextureKey.field_23031);
	public static final Model field_22952 = block("template_glass_pane_noside_alt", "_noside_alt", TextureKey.field_23031);
	public static final Model field_22953 = block("template_glass_pane_post", "_post", TextureKey.field_23031, TextureKey.field_23032);
	public static final Model field_22954 = block("template_glass_pane_side", "_side", TextureKey.field_23031, TextureKey.field_23032);
	public static final Model field_22955 = block("template_glass_pane_side_alt", "_side_alt", TextureKey.field_23031, TextureKey.field_23032);
	public static final Model field_22956 = block("template_command_block", TextureKey.field_23016, TextureKey.field_23017, TextureKey.field_23018);
	public static final Model field_22957 = block("template_anvil", TextureKey.field_23015);
	public static final Model[] STEM_GROWTH_STAGES = (Model[])IntStream.range(0, 8)
		.mapToObj(i -> block("stem_growth" + i, "_stage" + i, TextureKey.field_23034))
		.toArray(Model[]::new);
	public static final Model field_22959 = block("stem_fruit", TextureKey.field_23034, TextureKey.field_23035);
	public static final Model field_22960 = block("crop", TextureKey.field_22999);
	public static final Model field_22961 = block("template_farmland", TextureKey.field_23000, TextureKey.field_23015);
	public static final Model field_22962 = block("template_fire_floor", TextureKey.field_23001);
	public static final Model field_22963 = block("template_fire_side", TextureKey.field_23001);
	public static final Model field_22964 = block("template_fire_side_alt", TextureKey.field_23001);
	public static final Model field_22965 = block("template_fire_up", TextureKey.field_23001);
	public static final Model field_22966 = block("template_fire_up_alt", TextureKey.field_23001);
	public static final Model field_23957 = block("template_campfire", TextureKey.field_23001, TextureKey.field_23958);
	public static final Model field_22967 = block("template_lantern", TextureKey.field_23002);
	public static final Model field_22968 = block("template_hanging_lantern", "_hanging", TextureKey.field_23002);
	public static final Model field_22969 = block("template_torch", TextureKey.field_23005);
	public static final Model field_22970 = block("template_torch_wall", TextureKey.field_23005);
	public static final Model field_22971 = block("template_piston", TextureKey.field_23003, TextureKey.field_23014, TextureKey.field_23018);
	public static final Model field_22930 = block("template_piston_head", TextureKey.field_23003, TextureKey.field_23018, TextureKey.field_23004);
	public static final Model field_22931 = block("template_piston_head_short", TextureKey.field_23003, TextureKey.field_23018, TextureKey.field_23004);
	public static final Model field_22932 = block("template_seagrass", TextureKey.field_23011);
	public static final Model field_22933 = block("template_turtle_egg", TextureKey.field_23010);
	public static final Model field_22934 = block("template_two_turtle_eggs", TextureKey.field_23010);
	public static final Model field_22935 = block("template_three_turtle_eggs", TextureKey.field_23010);
	public static final Model field_22936 = block("template_four_turtle_eggs", TextureKey.field_23010);
	public static final Model field_22937 = block("template_single_face", TextureKey.field_23011);
	public static final Model field_22938 = item("generated", TextureKey.field_23006);
	public static final Model field_22939 = item("handheld", TextureKey.field_23006);
	public static final Model field_22940 = item("handheld_rod", TextureKey.field_23006);
	public static final Model field_22941 = item("template_shulker_box", TextureKey.field_23012);
	public static final Model field_22943 = item("template_bed", TextureKey.field_23012);
	public static final Model field_22944 = item("template_banner");
	public static final Model field_22945 = item("template_skull");

	private static Model make(TextureKey... requiredTextures) {
		return new Model(Optional.empty(), Optional.empty(), requiredTextures);
	}

	private static Model block(String parent, TextureKey... requiredTextures) {
		return new Model(Optional.of(new Identifier("minecraft", "block/" + parent)), Optional.empty(), requiredTextures);
	}

	private static Model item(String parent, TextureKey... requiredTextures) {
		return new Model(Optional.of(new Identifier("minecraft", "item/" + parent)), Optional.empty(), requiredTextures);
	}

	private static Model block(String parent, String variant, TextureKey... requiredTextures) {
		return new Model(Optional.of(new Identifier("minecraft", "block/" + parent)), Optional.of(variant), requiredTextures);
	}
}
