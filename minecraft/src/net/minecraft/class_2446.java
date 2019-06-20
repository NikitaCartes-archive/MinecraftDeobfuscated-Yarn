package net.minecraft;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2446 implements class_2405 {
	private static final Logger field_11374 = LogManager.getLogger();
	private static final Gson field_11375 = new GsonBuilder().setPrettyPrinting().create();
	private final class_2403 field_11373;

	public class_2446(class_2403 arg) {
		this.field_11373 = arg;
	}

	@Override
	public void method_10319(class_2408 arg) throws IOException {
		Path path = this.field_11373.method_10313();
		Set<class_2960> set = Sets.<class_2960>newHashSet();
		this.method_10419(
			arg2 -> {
				if (!set.add(arg2.method_10417())) {
					throw new IllegalStateException("Duplicate recipe " + arg2.method_10417());
				} else {
					this.method_10425(
						arg, arg2.method_17799(), path.resolve("data/" + arg2.method_10417().method_12836() + "/recipes/" + arg2.method_10417().method_12832() + ".json")
					);
					JsonObject jsonObject = arg2.method_10415();
					if (jsonObject != null) {
						this.method_10427(
							arg, jsonObject, path.resolve("data/" + arg2.method_10417().method_12836() + "/advancements/" + arg2.method_10418().method_12832() + ".json")
						);
					}
				}
			}
		);
		this.method_10427(
			arg,
			class_161.class_162.method_707().method_709("impossible", new class_2062.class_2063()).method_698(),
			path.resolve("data/minecraft/advancements/recipes/root.json")
		);
	}

	private void method_10425(class_2408 arg, JsonObject jsonObject, Path path) {
		try {
			String string = field_11375.toJson((JsonElement)jsonObject);
			String string2 = field_11280.hashUnencodedChars(string).toString();
			if (!Objects.equals(arg.method_10323(path), string2) || !Files.exists(path, new LinkOption[0])) {
				Files.createDirectories(path.getParent());
				BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
				Throwable var7 = null;

				try {
					bufferedWriter.write(string);
				} catch (Throwable var17) {
					var7 = var17;
					throw var17;
				} finally {
					if (bufferedWriter != null) {
						if (var7 != null) {
							try {
								bufferedWriter.close();
							} catch (Throwable var16) {
								var7.addSuppressed(var16);
							}
						} else {
							bufferedWriter.close();
						}
					}
				}
			}

			arg.method_10325(path, string2);
		} catch (IOException var19) {
			field_11374.error("Couldn't save recipe {}", path, var19);
		}
	}

	private void method_10427(class_2408 arg, JsonObject jsonObject, Path path) {
		try {
			String string = field_11375.toJson((JsonElement)jsonObject);
			String string2 = field_11280.hashUnencodedChars(string).toString();
			if (!Objects.equals(arg.method_10323(path), string2) || !Files.exists(path, new LinkOption[0])) {
				Files.createDirectories(path.getParent());
				BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
				Throwable var7 = null;

				try {
					bufferedWriter.write(string);
				} catch (Throwable var17) {
					var7 = var17;
					throw var17;
				} finally {
					if (bufferedWriter != null) {
						if (var7 != null) {
							try {
								bufferedWriter.close();
							} catch (Throwable var16) {
								var7.addSuppressed(var16);
							}
						} else {
							bufferedWriter.close();
						}
					}
				}
			}

			arg.method_10325(path, string2);
		} catch (IOException var19) {
			field_11374.error("Couldn't save recipe advancement {}", path, var19);
		}
	}

	private void method_10419(Consumer<class_2444> consumer) {
		class_2447.method_10436(class_2246.field_9999, 3)
			.method_10434('#', class_2246.field_10533)
			.method_10439("##")
			.method_10439("##")
			.method_10435("bark")
			.method_10429("has_log", this.method_10426(class_2246.field_10533))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8094)
			.method_10434('#', class_2246.field_10218)
			.method_10439("# #")
			.method_10439("###")
			.method_10435("boat")
			.method_10429("in_water", this.method_10422(class_2246.field_10382))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10278)
			.method_10454(class_2246.field_10218)
			.method_10452("wooden_button")
			.method_10442("has_planks", this.method_10426(class_2246.field_10218))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10232, 3)
			.method_10434('#', class_2246.field_10218)
			.method_10439("##")
			.method_10439("##")
			.method_10439("##")
			.method_10435("wooden_door")
			.method_10429("has_planks", this.method_10426(class_2246.field_10218))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10144, 3)
			.method_10434('#', class_1802.field_8600)
			.method_10434('W', class_2246.field_10218)
			.method_10439("W#W")
			.method_10439("W#W")
			.method_10435("wooden_fence")
			.method_10429("has_planks", this.method_10426(class_2246.field_10218))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10457)
			.method_10434('#', class_1802.field_8600)
			.method_10434('W', class_2246.field_10218)
			.method_10439("#W#")
			.method_10439("#W#")
			.method_10435("wooden_fence_gate")
			.method_10429("has_planks", this.method_10426(class_2246.field_10218))
			.method_10431(consumer);
		class_2450.method_10448(class_2246.field_10218, 4)
			.method_10446(class_3489.field_15525)
			.method_10452("planks")
			.method_10442("has_logs", this.method_10420(class_3489.field_15525))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10397)
			.method_10434('#', class_2246.field_10218)
			.method_10439("##")
			.method_10435("wooden_pressure_plate")
			.method_10429("has_planks", this.method_10426(class_2246.field_10218))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10031, 6)
			.method_10434('#', class_2246.field_10218)
			.method_10439("###")
			.method_10435("wooden_slab")
			.method_10429("has_planks", this.method_10426(class_2246.field_10218))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10256, 4)
			.method_10434('#', class_2246.field_10218)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10435("wooden_stairs")
			.method_10429("has_planks", this.method_10426(class_2246.field_10218))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10608, 2)
			.method_10434('#', class_2246.field_10218)
			.method_10439("###")
			.method_10439("###")
			.method_10435("wooden_trapdoor")
			.method_10429("has_planks", this.method_10426(class_2246.field_10218))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10546, 6)
			.method_10434('#', class_2246.field_10523)
			.method_10434('S', class_1802.field_8600)
			.method_10434('X', class_1802.field_8620)
			.method_10439("XSX")
			.method_10439("X#X")
			.method_10439("XSX")
			.method_10429("has_rail", this.method_10426(class_2246.field_10167))
			.method_10431(consumer);
		class_2450.method_10448(class_2246.field_10115, 2)
			.method_10454(class_2246.field_10508)
			.method_10454(class_2246.field_10445)
			.method_10442("has_stone", this.method_10426(class_2246.field_10508))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10535)
			.method_10434('I', class_2246.field_10085)
			.method_10434('i', class_1802.field_8620)
			.method_10439("III")
			.method_10439(" i ")
			.method_10439("iii")
			.method_10429("has_iron_block", this.method_10426(class_2246.field_10085))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8694)
			.method_10434('/', class_1802.field_8600)
			.method_10434('_', class_2246.field_10136)
			.method_10439("///")
			.method_10439(" / ")
			.method_10439("/_/")
			.method_10429("has_stone_slab", this.method_10426(class_2246.field_10136))
			.method_10431(consumer);
		class_2447.method_10436(class_1802.field_8107, 4)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8145)
			.method_10434('Y', class_1802.field_8153)
			.method_10439("X")
			.method_10439("#")
			.method_10439("Y")
			.method_10429("has_feather", this.method_10426(class_1802.field_8153))
			.method_10429("has_flint", this.method_10426(class_1802.field_8145))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_16328, 1)
			.method_10433('P', class_3489.field_15537)
			.method_10433('S', class_3489.field_15534)
			.method_10439("PSP")
			.method_10439("P P")
			.method_10439("PSP")
			.method_10429("has_planks", this.method_10420(class_3489.field_15537))
			.method_10429("has_wood_slab", this.method_10420(class_3489.field_15534))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10327)
			.method_10434('S', class_1802.field_8137)
			.method_10434('G', class_2246.field_10033)
			.method_10434('O', class_2246.field_10540)
			.method_10439("GGG")
			.method_10439("GSG")
			.method_10439("OOO")
			.method_10429("has_nether_star", this.method_10426(class_1802.field_8137))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8515)
			.method_10454(class_1802.field_8428)
			.method_10449(class_1802.field_8186, 6)
			.method_10442("has_beetroot", this.method_10426(class_1802.field_8186))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10307, 3)
			.method_10434('#', class_2246.field_10511)
			.method_10439("##")
			.method_10439("##")
			.method_10435("bark")
			.method_10429("has_log", this.method_10426(class_2246.field_10511))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8442)
			.method_10434('#', class_2246.field_10148)
			.method_10439("# #")
			.method_10439("###")
			.method_10435("boat")
			.method_10429("in_water", this.method_10422(class_2246.field_10382))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10417)
			.method_10454(class_2246.field_10148)
			.method_10452("wooden_button")
			.method_10442("has_planks", this.method_10426(class_2246.field_10148))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10352, 3)
			.method_10434('#', class_2246.field_10148)
			.method_10439("##")
			.method_10439("##")
			.method_10439("##")
			.method_10435("wooden_door")
			.method_10429("has_planks", this.method_10426(class_2246.field_10148))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10299, 3)
			.method_10434('#', class_1802.field_8600)
			.method_10434('W', class_2246.field_10148)
			.method_10439("W#W")
			.method_10439("W#W")
			.method_10435("wooden_fence")
			.method_10429("has_planks", this.method_10426(class_2246.field_10148))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10513)
			.method_10434('#', class_1802.field_8600)
			.method_10434('W', class_2246.field_10148)
			.method_10439("#W#")
			.method_10439("#W#")
			.method_10435("wooden_fence_gate")
			.method_10429("has_planks", this.method_10426(class_2246.field_10148))
			.method_10431(consumer);
		class_2450.method_10448(class_2246.field_10148, 4)
			.method_10446(class_3489.field_15554)
			.method_10452("planks")
			.method_10442("has_log", this.method_10420(class_3489.field_15554))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10592)
			.method_10434('#', class_2246.field_10148)
			.method_10439("##")
			.method_10435("wooden_pressure_plate")
			.method_10429("has_planks", this.method_10426(class_2246.field_10148))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10257, 6)
			.method_10434('#', class_2246.field_10148)
			.method_10439("###")
			.method_10435("wooden_slab")
			.method_10429("has_planks", this.method_10426(class_2246.field_10148))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10408, 4)
			.method_10434('#', class_2246.field_10148)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10435("wooden_stairs")
			.method_10429("has_planks", this.method_10426(class_2246.field_10148))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10486, 2)
			.method_10434('#', class_2246.field_10148)
			.method_10439("###")
			.method_10439("###")
			.method_10435("wooden_trapdoor")
			.method_10429("has_planks", this.method_10426(class_2246.field_10148))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8572)
			.method_10434('#', class_2246.field_10146)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_black_wool", this.method_10426(class_2246.field_10146))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8112)
			.method_10434('#', class_2246.field_10146)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_black_wool", this.method_10426(class_2246.field_10146))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8112)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8226)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "black_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_10106, 3)
			.method_10434('#', class_2246.field_10146)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_black_wool", this.method_10426(class_2246.field_10146))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10106, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8226)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_black_dye", this.method_10426(class_1802.field_8226))
			.method_10438(consumer, "black_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10506, 8)
			.method_10454(class_1802.field_8226)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8226)
			.method_10454(class_1802.field_8794)
			.method_10452("black_dye")
			.method_10442("has_ink_sac", this.method_10426(class_1802.field_8794))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8226)
			.method_10454(class_2246.field_10606)
			.method_10452("black_dye")
			.method_10442("has_black_flower", this.method_10426(class_2246.field_10606))
			.method_10450(consumer, "black_dye_from_wither_rose");
		class_2447.method_10436(class_2246.field_9997, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8226)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10070, 16)
			.method_10434('#', class_2246.field_9997)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10070, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8226)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_black_dye", this.method_10426(class_1802.field_8226))
			.method_10438(consumer, "black_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10626, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8226)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10146)
			.method_10454(class_1802.field_8226)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2450.method_10448(class_1802.field_8183, 2)
			.method_10454(class_1802.field_8894)
			.method_10442("has_blaze_rod", this.method_10426(class_1802.field_8894))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8128)
			.method_10434('#', class_2246.field_10514)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_blue_wool", this.method_10426(class_2246.field_10514))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8893)
			.method_10434('#', class_2246.field_10514)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_blue_wool", this.method_10426(class_2246.field_10514))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8893)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8345)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "blue_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_10043, 3)
			.method_10434('#', class_2246.field_10514)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_blue_wool", this.method_10426(class_2246.field_10514))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10043, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8345)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_blue_dye", this.method_10426(class_1802.field_8345))
			.method_10438(consumer, "blue_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10456, 8)
			.method_10454(class_1802.field_8345)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8345)
			.method_10454(class_1802.field_8759)
			.method_10452("blue_dye")
			.method_10442("has_lapis_lazuli", this.method_10426(class_1802.field_8759))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8345)
			.method_10454(class_2246.field_9995)
			.method_10452("blue_dye")
			.method_10442("has_blue_flower", this.method_10426(class_2246.field_9995))
			.method_10450(consumer, "blue_dye_from_cornflower");
		class_2447.method_10437(class_2246.field_10384)
			.method_10434('#', class_2246.field_10225)
			.method_10439("###")
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_at_least_9_packed_ice", this.method_10424(class_2096.class_2100.method_9053(9), class_2246.field_10225))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10060, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8345)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_9982, 16)
			.method_10434('#', class_2246.field_10060)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_9982, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8345)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_blue_dye", this.method_10426(class_1802.field_8345))
			.method_10438(consumer, "blue_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10409, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8345)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10514)
			.method_10454(class_1802.field_8345)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8533)
			.method_10434('#', class_2246.field_10161)
			.method_10439("# #")
			.method_10439("###")
			.method_10435("boat")
			.method_10429("in_water", this.method_10422(class_2246.field_10382))
			.method_10431(consumer);
		class_1792 lv = class_1802.field_8324;
		class_2447.method_10437(class_2246.field_10166)
			.method_10434('X', class_1802.field_8324)
			.method_10439("XXX")
			.method_10439("XXX")
			.method_10439("XXX")
			.method_10429("has_at_least_9_bonemeal", this.method_10424(class_2096.class_2100.method_9053(9), lv))
			.method_10431(consumer);
		class_2450.method_10448(class_1802.field_8324, 3)
			.method_10454(class_1802.field_8606)
			.method_10452("bonemeal")
			.method_10442("has_bone", this.method_10426(class_1802.field_8606))
			.method_10444(consumer);
		class_2450.method_10448(class_1802.field_8324, 9)
			.method_10454(class_2246.field_10166)
			.method_10452("bonemeal")
			.method_10442("has_at_least_9_bonemeal", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8324))
			.method_10442("has_bone_block", this.method_10426(class_2246.field_10166))
			.method_10450(consumer, "bone_meal_from_bone_block");
		class_2450.method_10447(class_1802.field_8529)
			.method_10449(class_1802.field_8407, 3)
			.method_10454(class_1802.field_8745)
			.method_10442("has_paper", this.method_10426(class_1802.field_8407))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10504)
			.method_10433('#', class_3489.field_15537)
			.method_10434('X', class_1802.field_8529)
			.method_10439("###")
			.method_10439("XXX")
			.method_10439("###")
			.method_10429("has_book", this.method_10426(class_1802.field_8529))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8102)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8276)
			.method_10439(" #X")
			.method_10439("# X")
			.method_10439(" #X")
			.method_10429("has_string", this.method_10426(class_1802.field_8276))
			.method_10431(consumer);
		class_2447.method_10436(class_1802.field_8428, 4)
			.method_10433('#', class_3489.field_15537)
			.method_10439("# #")
			.method_10439(" # ")
			.method_10429("has_brown_mushroom", this.method_10426(class_2246.field_10251))
			.method_10429("has_red_mushroom", this.method_10426(class_2246.field_10559))
			.method_10429("has_mushroom_stew", this.method_10426(class_1802.field_8208))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8229)
			.method_10434('#', class_1802.field_8861)
			.method_10439("###")
			.method_10429("has_wheat", this.method_10426(class_1802.field_8861))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10333)
			.method_10434('B', class_1802.field_8894)
			.method_10434('#', class_2246.field_10445)
			.method_10439(" B ")
			.method_10439("###")
			.method_10429("has_blaze_rod", this.method_10426(class_1802.field_8894))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10104)
			.method_10434('#', class_1802.field_8621)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_brick", this.method_10426(class_1802.field_8621))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10191, 6)
			.method_10434('#', class_2246.field_10104)
			.method_10439("###")
			.method_10429("has_brick_block", this.method_10426(class_2246.field_10104))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10089, 4)
			.method_10434('#', class_2246.field_10104)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_brick_block", this.method_10426(class_2246.field_10104))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8124)
			.method_10434('#', class_2246.field_10113)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_brown_wool", this.method_10426(class_2246.field_10113))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8464)
			.method_10434('#', class_2246.field_10113)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_brown_wool", this.method_10426(class_2246.field_10113))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8464)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8099)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "brown_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_10473, 3)
			.method_10434('#', class_2246.field_10113)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_brown_wool", this.method_10426(class_2246.field_10113))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10473, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8099)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_brown_dye", this.method_10426(class_1802.field_8099))
			.method_10438(consumer, "brown_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10023, 8)
			.method_10454(class_1802.field_8099)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8099)
			.method_10454(class_1802.field_8116)
			.method_10452("brown_dye")
			.method_10442("has_cocoa_beans", this.method_10426(class_1802.field_8116))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10073, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8099)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10163, 16)
			.method_10434('#', class_2246.field_10073)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10163, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8099)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_brown_dye", this.method_10426(class_1802.field_8099))
			.method_10438(consumer, "brown_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10123, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8099)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10113)
			.method_10454(class_1802.field_8099)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8550)
			.method_10434('#', class_1802.field_8620)
			.method_10439("# #")
			.method_10439(" # ")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10183)
			.method_10434('A', class_1802.field_8103)
			.method_10434('B', class_1802.field_8479)
			.method_10434('C', class_1802.field_8861)
			.method_10434('E', class_1802.field_8803)
			.method_10439("AAA")
			.method_10439("BEB")
			.method_10439("CCC")
			.method_10429("has_egg", this.method_10426(class_1802.field_8803))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_17350)
			.method_10433('L', class_3489.field_15539)
			.method_10434('S', class_1802.field_8600)
			.method_10433('C', class_3489.field_17487)
			.method_10439(" S ")
			.method_10439("SCS")
			.method_10439("LLL")
			.method_10429("has_stick", this.method_10426(class_1802.field_8600))
			.method_10429("has_coal", this.method_10420(class_3489.field_17487))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8184)
			.method_10434('#', class_1802.field_8378)
			.method_10434('X', class_1802.field_8179)
			.method_10439("# ")
			.method_10439(" X")
			.method_10429("has_carrot", this.method_10426(class_1802.field_8179))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10593)
			.method_10434('#', class_1802.field_8620)
			.method_10439("# #")
			.method_10439("# #")
			.method_10439("###")
			.method_10429("has_water_bucket", this.method_10426(class_1802.field_8705))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_17563)
			.method_10433('F', class_3489.field_17620)
			.method_10433('#', class_3489.field_15537)
			.method_10439("F F")
			.method_10439("F F")
			.method_10439("###")
			.method_10429("has_wooden_fences", this.method_10420(class_3489.field_17620))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10034)
			.method_10433('#', class_3489.field_15537)
			.method_10439("###")
			.method_10439("# #")
			.method_10439("###")
			.method_10429(
				"has_lots_of_items",
				new class_2066.class_2068(class_2096.class_2100.method_9053(10), class_2096.class_2100.field_9708, class_2096.class_2100.field_9708, new class_2073[0])
			)
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8388)
			.method_10434('A', class_2246.field_10034)
			.method_10434('B', class_1802.field_8045)
			.method_10439("A")
			.method_10439("B")
			.method_10429("has_minecart", this.method_10426(class_1802.field_8045))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10044)
			.method_10434('#', class_2246.field_10237)
			.method_10439("#")
			.method_10439("#")
			.method_10429("has_chiseled_quartz_block", this.method_10426(class_2246.field_10044))
			.method_10429("has_quartz_block", this.method_10426(class_2246.field_10153))
			.method_10429("has_quartz_pillar", this.method_10426(class_2246.field_10437))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10552)
			.method_10434('#', class_2246.field_10131)
			.method_10439("#")
			.method_10439("#")
			.method_10429("has_stone_bricks", this.method_10420(class_3489.field_15531))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10460)
			.method_10434('#', class_1802.field_8696)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_clay_ball", this.method_10426(class_1802.field_8696))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8557)
			.method_10434('#', class_1802.field_8695)
			.method_10434('X', class_1802.field_8725)
			.method_10439(" # ")
			.method_10439("#X#")
			.method_10439(" # ")
			.method_10429("has_redstone", this.method_10426(class_1802.field_8725))
			.method_10431(consumer);
		class_2450.method_10448(class_1802.field_8713, 9)
			.method_10454(class_2246.field_10381)
			.method_10442("has_at_least_9_coal", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8713))
			.method_10442("has_coal_block", this.method_10426(class_2246.field_10381))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10381)
			.method_10434('#', class_1802.field_8713)
			.method_10439("###")
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_at_least_9_coal", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8713))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10253, 4)
			.method_10434('D', class_2246.field_10566)
			.method_10434('G', class_2246.field_10255)
			.method_10439("DG")
			.method_10439("GD")
			.method_10429("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10351, 6)
			.method_10434('#', class_2246.field_10445)
			.method_10439("###")
			.method_10429("has_cobblestone", this.method_10426(class_2246.field_10445))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10625, 6)
			.method_10434('#', class_2246.field_10445)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_cobblestone", this.method_10426(class_2246.field_10445))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10377)
			.method_10434('#', class_2246.field_10523)
			.method_10434('X', class_1802.field_8155)
			.method_10434('I', class_2246.field_10340)
			.method_10439(" # ")
			.method_10439("#X#")
			.method_10439("III")
			.method_10429("has_quartz", this.method_10426(class_1802.field_8155))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8251)
			.method_10434('#', class_1802.field_8620)
			.method_10434('X', class_1802.field_8725)
			.method_10439(" # ")
			.method_10439("#X#")
			.method_10439(" # ")
			.method_10429("has_redstone", this.method_10426(class_1802.field_8725))
			.method_10431(consumer);
		class_2447.method_10436(class_1802.field_8423, 8)
			.method_10434('#', class_1802.field_8861)
			.method_10434('X', class_1802.field_8116)
			.method_10439("#X#")
			.method_10429("has_cocoa", this.method_10426(class_1802.field_8116))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_9980)
			.method_10433('#', class_3489.field_15537)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_planks", this.method_10420(class_3489.field_15537))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8399)
			.method_10434('~', class_1802.field_8276)
			.method_10434('#', class_1802.field_8600)
			.method_10434('&', class_1802.field_8620)
			.method_10434('$', class_2246.field_10348)
			.method_10439("#&#")
			.method_10439("~$~")
			.method_10439(" # ")
			.method_10429("has_string", this.method_10426(class_1802.field_8276))
			.method_10429("has_stick", this.method_10426(class_1802.field_8600))
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10429("has_tripwire_hook", this.method_10426(class_2246.field_10348))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10083)
			.method_10433('#', class_3489.field_15537)
			.method_10434('@', class_1802.field_8276)
			.method_10439("@@")
			.method_10439("##")
			.method_10429("has_string", this.method_10426(class_1802.field_8276))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10117)
			.method_10434('#', class_2246.field_10624)
			.method_10439("#")
			.method_10439("#")
			.method_10429("has_red_sandstone", this.method_10426(class_2246.field_10344))
			.method_10429("has_chiseled_red_sandstone", this.method_10426(class_2246.field_10117))
			.method_10429("has_cut_red_sandstone", this.method_10426(class_2246.field_10518))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10292)
			.method_10434('#', class_2246.field_10007)
			.method_10439("#")
			.method_10439("#")
			.method_10429("has_stone_slab", this.method_10426(class_2246.field_10007))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8629)
			.method_10434('#', class_2246.field_10619)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_cyan_wool", this.method_10426(class_2246.field_10619))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8390)
			.method_10434('#', class_2246.field_10619)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_cyan_wool", this.method_10426(class_2246.field_10619))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8390)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8632)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "cyan_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_10433, 3)
			.method_10434('#', class_2246.field_10619)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_cyan_wool", this.method_10426(class_2246.field_10619))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10433, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8632)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_cyan_dye", this.method_10426(class_1802.field_8632))
			.method_10438(consumer, "cyan_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10233, 8)
			.method_10454(class_1802.field_8632)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10448(class_1802.field_8632, 2)
			.method_10454(class_1802.field_8345)
			.method_10454(class_1802.field_8408)
			.method_10442("has_green_dye", this.method_10426(class_1802.field_8408))
			.method_10442("has_blue_dye", this.method_10426(class_1802.field_8345))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10248, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8632)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10355, 16)
			.method_10434('#', class_2246.field_10248)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10355, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8632)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_cyan_dye", this.method_10426(class_1802.field_8632))
			.method_10438(consumer, "cyan_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10235, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8632)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10619)
			.method_10454(class_1802.field_8632)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10178, 3)
			.method_10434('#', class_2246.field_10010)
			.method_10439("##")
			.method_10439("##")
			.method_10435("bark")
			.method_10429("has_log", this.method_10426(class_2246.field_10010))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8138)
			.method_10434('#', class_2246.field_10075)
			.method_10439("# #")
			.method_10439("###")
			.method_10435("boat")
			.method_10429("in_water", this.method_10422(class_2246.field_10382))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10493)
			.method_10454(class_2246.field_10075)
			.method_10452("wooden_button")
			.method_10442("has_planks", this.method_10426(class_2246.field_10075))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10403, 3)
			.method_10434('#', class_2246.field_10075)
			.method_10439("##")
			.method_10439("##")
			.method_10439("##")
			.method_10435("wooden_door")
			.method_10429("has_planks", this.method_10426(class_2246.field_10075))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10132, 3)
			.method_10434('#', class_1802.field_8600)
			.method_10434('W', class_2246.field_10075)
			.method_10439("W#W")
			.method_10439("W#W")
			.method_10435("wooden_fence")
			.method_10429("has_planks", this.method_10426(class_2246.field_10075))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10196)
			.method_10434('#', class_1802.field_8600)
			.method_10434('W', class_2246.field_10075)
			.method_10439("#W#")
			.method_10439("#W#")
			.method_10435("wooden_fence_gate")
			.method_10429("has_planks", this.method_10426(class_2246.field_10075))
			.method_10431(consumer);
		class_2450.method_10448(class_2246.field_10075, 4)
			.method_10446(class_3489.field_15546)
			.method_10452("planks")
			.method_10442("has_logs", this.method_10420(class_3489.field_15546))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10470)
			.method_10434('#', class_2246.field_10075)
			.method_10439("##")
			.method_10435("wooden_pressure_plate")
			.method_10429("has_planks", this.method_10426(class_2246.field_10075))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10500, 6)
			.method_10434('#', class_2246.field_10075)
			.method_10439("###")
			.method_10435("wooden_slab")
			.method_10429("has_planks", this.method_10426(class_2246.field_10075))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10616, 4)
			.method_10434('#', class_2246.field_10075)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10435("wooden_stairs")
			.method_10429("has_planks", this.method_10426(class_2246.field_10075))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10246, 2)
			.method_10434('#', class_2246.field_10075)
			.method_10439("###")
			.method_10439("###")
			.method_10435("wooden_trapdoor")
			.method_10429("has_planks", this.method_10426(class_2246.field_10075))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10297)
			.method_10434('S', class_1802.field_8662)
			.method_10434('I', class_1802.field_8794)
			.method_10439("SSS")
			.method_10439("SIS")
			.method_10439("SSS")
			.method_10429("has_prismarine_shard", this.method_10426(class_1802.field_8662))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10350, 4)
			.method_10434('#', class_2246.field_10135)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_prismarine", this.method_10426(class_2246.field_10135))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10190, 4)
			.method_10434('#', class_2246.field_10006)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_prismarine_bricks", this.method_10426(class_2246.field_10006))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10130, 4)
			.method_10434('#', class_2246.field_10297)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_dark_prismarine", this.method_10426(class_2246.field_10297))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10429)
			.method_10434('Q', class_1802.field_8155)
			.method_10434('G', class_2246.field_10033)
			.method_10428('W', class_1856.method_8106(class_3489.field_15534))
			.method_10439("GGG")
			.method_10439("QQQ")
			.method_10439("WWW")
			.method_10429("has_quartz", this.method_10426(class_1802.field_8155))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10025, 6)
			.method_10434('R', class_1802.field_8725)
			.method_10434('#', class_2246.field_10158)
			.method_10434('X', class_1802.field_8620)
			.method_10439("X X")
			.method_10439("X#X")
			.method_10439("XRX")
			.method_10429("has_rail", this.method_10426(class_2246.field_10167))
			.method_10431(consumer);
		class_2450.method_10448(class_1802.field_8477, 9)
			.method_10454(class_2246.field_10201)
			.method_10442("has_at_least_9_diamond", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8477))
			.method_10442("has_diamond_block", this.method_10426(class_2246.field_10201))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8556)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8477)
			.method_10439("XX")
			.method_10439("X#")
			.method_10439(" #")
			.method_10429("has_diamond", this.method_10426(class_1802.field_8477))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10201)
			.method_10434('#', class_1802.field_8477)
			.method_10439("###")
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_at_least_9_diamond", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8477))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8285)
			.method_10434('X', class_1802.field_8477)
			.method_10439("X X")
			.method_10439("X X")
			.method_10429("has_diamond", this.method_10426(class_1802.field_8477))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8058)
			.method_10434('X', class_1802.field_8477)
			.method_10439("X X")
			.method_10439("XXX")
			.method_10439("XXX")
			.method_10429("has_diamond", this.method_10426(class_1802.field_8477))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8805)
			.method_10434('X', class_1802.field_8477)
			.method_10439("XXX")
			.method_10439("X X")
			.method_10429("has_diamond", this.method_10426(class_1802.field_8477))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8527)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8477)
			.method_10439("XX")
			.method_10439(" #")
			.method_10439(" #")
			.method_10429("has_diamond", this.method_10426(class_1802.field_8477))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8348)
			.method_10434('X', class_1802.field_8477)
			.method_10439("XXX")
			.method_10439("X X")
			.method_10439("X X")
			.method_10429("has_diamond", this.method_10426(class_1802.field_8477))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8377)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8477)
			.method_10439("XXX")
			.method_10439(" # ")
			.method_10439(" # ")
			.method_10429("has_diamond", this.method_10426(class_1802.field_8477))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8250)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8477)
			.method_10439("X")
			.method_10439("#")
			.method_10439("#")
			.method_10429("has_diamond", this.method_10426(class_1802.field_8477))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8802)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8477)
			.method_10439("X")
			.method_10439("X")
			.method_10439("#")
			.method_10429("has_diamond", this.method_10426(class_1802.field_8477))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10508, 2)
			.method_10434('Q', class_1802.field_8155)
			.method_10434('C', class_2246.field_10445)
			.method_10439("CQ")
			.method_10439("QC")
			.method_10429("has_quartz", this.method_10426(class_1802.field_8155))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10200)
			.method_10434('R', class_1802.field_8725)
			.method_10434('#', class_2246.field_10445)
			.method_10434('X', class_1802.field_8102)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("#R#")
			.method_10429("has_bow", this.method_10426(class_1802.field_8102))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10228)
			.method_10434('R', class_1802.field_8725)
			.method_10434('#', class_2246.field_10445)
			.method_10439("###")
			.method_10439("# #")
			.method_10439("#R#")
			.method_10429("has_redstone", this.method_10426(class_1802.field_8725))
			.method_10431(consumer);
		class_2450.method_10448(class_1802.field_8687, 9)
			.method_10454(class_2246.field_10234)
			.method_10442("has_at_least_9_emerald", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8687))
			.method_10442("has_emerald_block", this.method_10426(class_2246.field_10234))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10234)
			.method_10434('#', class_1802.field_8687)
			.method_10439("###")
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_at_least_9_emerald", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8687))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10485)
			.method_10434('B', class_1802.field_8529)
			.method_10434('#', class_2246.field_10540)
			.method_10434('D', class_1802.field_8477)
			.method_10439(" B ")
			.method_10439("D#D")
			.method_10439("###")
			.method_10429("has_obsidian", this.method_10426(class_2246.field_10540))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10443)
			.method_10434('#', class_2246.field_10540)
			.method_10434('E', class_1802.field_8449)
			.method_10439("###")
			.method_10439("#E#")
			.method_10439("###")
			.method_10429("has_ender_eye", this.method_10426(class_1802.field_8449))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8449)
			.method_10454(class_1802.field_8634)
			.method_10454(class_1802.field_8183)
			.method_10442("has_blaze_powder", this.method_10426(class_1802.field_8183))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10462, 4)
			.method_10434('#', class_2246.field_10471)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_end_stone", this.method_10426(class_2246.field_10471))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8301)
			.method_10434('T', class_1802.field_8070)
			.method_10434('E', class_1802.field_8449)
			.method_10434('G', class_2246.field_10033)
			.method_10439("GGG")
			.method_10439("GEG")
			.method_10439("GTG")
			.method_10429("has_ender_eye", this.method_10426(class_1802.field_8449))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10455, 4)
			.method_10434('#', class_1802.field_8882)
			.method_10434('/', class_1802.field_8894)
			.method_10439("/")
			.method_10439("#")
			.method_10429("has_chorus_fruit_popped", this.method_10426(class_1802.field_8882))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10620, 3)
			.method_10434('#', class_1802.field_8600)
			.method_10434('W', class_2246.field_10161)
			.method_10439("W#W")
			.method_10439("W#W")
			.method_10435("wooden_fence")
			.method_10429("has_planks", this.method_10426(class_2246.field_10161))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10188)
			.method_10434('#', class_1802.field_8600)
			.method_10434('W', class_2246.field_10161)
			.method_10439("#W#")
			.method_10439("#W#")
			.method_10435("wooden_fence_gate")
			.method_10429("has_planks", this.method_10426(class_2246.field_10161))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8711)
			.method_10454(class_1802.field_8680)
			.method_10454(class_2246.field_10251)
			.method_10454(class_1802.field_8479)
			.method_10442("has_spider_eye", this.method_10426(class_1802.field_8680))
			.method_10444(consumer);
		class_2450.method_10448(class_1802.field_8814, 3)
			.method_10454(class_1802.field_8054)
			.method_10454(class_1802.field_8183)
			.method_10451(class_1856.method_8091(class_1802.field_8713, class_1802.field_8665))
			.method_10442("has_blaze_powder", this.method_10426(class_1802.field_8183))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8378)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8276)
			.method_10439("  #")
			.method_10439(" #X")
			.method_10439("# X")
			.method_10429("has_string", this.method_10426(class_1802.field_8276))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8884)
			.method_10454(class_1802.field_8620)
			.method_10454(class_1802.field_8145)
			.method_10442("has_flint", this.method_10426(class_1802.field_8145))
			.method_10442("has_obsidian", this.method_10426(class_2246.field_10540))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10495)
			.method_10434('#', class_1802.field_8621)
			.method_10439("# #")
			.method_10439(" # ")
			.method_10429("has_brick", this.method_10426(class_1802.field_8621))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10181)
			.method_10434('#', class_2246.field_10445)
			.method_10439("###")
			.method_10439("# #")
			.method_10439("###")
			.method_10429("has_cobblestone", this.method_10426(class_2246.field_10445))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8063)
			.method_10434('A', class_2246.field_10181)
			.method_10434('B', class_1802.field_8045)
			.method_10439("A")
			.method_10439("B")
			.method_10429("has_minecart", this.method_10426(class_1802.field_8045))
			.method_10431(consumer);
		class_2447.method_10436(class_1802.field_8469, 3)
			.method_10434('#', class_2246.field_10033)
			.method_10439("# #")
			.method_10439(" # ")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10285, 16)
			.method_10434('#', class_2246.field_10033)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10171)
			.method_10434('#', class_1802.field_8601)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_glowstone_dust", this.method_10426(class_1802.field_8601))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8463)
			.method_10434('#', class_1802.field_8695)
			.method_10434('X', class_1802.field_8279)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10429("has_gold_ingot", this.method_10426(class_1802.field_8695))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8825)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8695)
			.method_10439("XX")
			.method_10439("X#")
			.method_10439(" #")
			.method_10429("has_gold_ingot", this.method_10426(class_1802.field_8695))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8753)
			.method_10434('X', class_1802.field_8695)
			.method_10439("X X")
			.method_10439("X X")
			.method_10429("has_gold_ingot", this.method_10426(class_1802.field_8695))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8071)
			.method_10434('#', class_1802.field_8397)
			.method_10434('X', class_1802.field_8179)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10429("has_gold_nugget", this.method_10426(class_1802.field_8397))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8678)
			.method_10434('X', class_1802.field_8695)
			.method_10439("X X")
			.method_10439("XXX")
			.method_10439("XXX")
			.method_10429("has_gold_ingot", this.method_10426(class_1802.field_8695))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8862)
			.method_10434('X', class_1802.field_8695)
			.method_10439("XXX")
			.method_10439("X X")
			.method_10429("has_gold_ingot", this.method_10426(class_1802.field_8695))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8303)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8695)
			.method_10439("XX")
			.method_10439(" #")
			.method_10439(" #")
			.method_10429("has_gold_ingot", this.method_10426(class_1802.field_8695))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8416)
			.method_10434('X', class_1802.field_8695)
			.method_10439("XXX")
			.method_10439("X X")
			.method_10439("X X")
			.method_10429("has_gold_ingot", this.method_10426(class_1802.field_8695))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8335)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8695)
			.method_10439("XXX")
			.method_10439(" # ")
			.method_10439(" # ")
			.method_10429("has_gold_ingot", this.method_10426(class_1802.field_8695))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10425, 6)
			.method_10434('R', class_1802.field_8725)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8695)
			.method_10439("X X")
			.method_10439("X#X")
			.method_10439("XRX")
			.method_10429("has_rail", this.method_10426(class_2246.field_10167))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8322)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8695)
			.method_10439("X")
			.method_10439("#")
			.method_10439("#")
			.method_10429("has_gold_ingot", this.method_10426(class_1802.field_8695))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8845)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8695)
			.method_10439("X")
			.method_10439("X")
			.method_10439("#")
			.method_10429("has_gold_ingot", this.method_10426(class_1802.field_8695))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10205)
			.method_10434('#', class_1802.field_8695)
			.method_10439("###")
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_at_least_9_gold_ingot", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8695))
			.method_10431(consumer);
		class_2450.method_10448(class_1802.field_8695, 9)
			.method_10454(class_2246.field_10205)
			.method_10452("gold_ingot")
			.method_10442("has_at_least_9_gold_ingot", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8695))
			.method_10442("has_gold_block", this.method_10426(class_2246.field_10205))
			.method_10450(consumer, "gold_ingot_from_gold_block");
		class_2447.method_10437(class_1802.field_8695)
			.method_10434('#', class_1802.field_8397)
			.method_10439("###")
			.method_10439("###")
			.method_10439("###")
			.method_10435("gold_ingot")
			.method_10429("has_at_least_9_gold_nugget", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8397))
			.method_10438(consumer, "gold_ingot_from_nuggets");
		class_2450.method_10448(class_1802.field_8397, 9)
			.method_10454(class_1802.field_8695)
			.method_10442("has_at_least_9_gold_nugget", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8397))
			.method_10442("has_gold_ingot", this.method_10426(class_1802.field_8695))
			.method_10444(consumer);
		class_2450.method_10447(class_2246.field_10474)
			.method_10454(class_2246.field_10508)
			.method_10454(class_1802.field_8155)
			.method_10442("has_quartz", this.method_10426(class_1802.field_8155))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8617)
			.method_10434('#', class_2246.field_10423)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_gray_wool", this.method_10426(class_2246.field_10423))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8754)
			.method_10434('#', class_2246.field_10423)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_gray_wool", this.method_10426(class_2246.field_10423))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8754)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8298)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "gray_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_10591, 3)
			.method_10434('#', class_2246.field_10423)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_gray_wool", this.method_10426(class_2246.field_10423))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10591, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8298)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_gray_dye", this.method_10426(class_1802.field_8298))
			.method_10438(consumer, "gray_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10353, 8)
			.method_10454(class_1802.field_8298)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10448(class_1802.field_8298, 2)
			.method_10454(class_1802.field_8226)
			.method_10454(class_1802.field_8446)
			.method_10442("has_white_dye", this.method_10426(class_1802.field_8446))
			.method_10442("has_black_dye", this.method_10426(class_1802.field_8226))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10555, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8298)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10077, 16)
			.method_10434('#', class_2246.field_10555)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10077, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8298)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_gray_dye", this.method_10426(class_1802.field_8298))
			.method_10438(consumer, "gray_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10349, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8298)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10423)
			.method_10454(class_1802.field_8298)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8295)
			.method_10434('#', class_2246.field_10170)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_green_wool", this.method_10426(class_2246.field_10170))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8368)
			.method_10434('#', class_2246.field_10170)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_green_wool", this.method_10426(class_2246.field_10170))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8368)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8408)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "green_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_10338, 3)
			.method_10434('#', class_2246.field_10170)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_green_wool", this.method_10426(class_2246.field_10170))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10338, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8408)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_green_dye", this.method_10426(class_1802.field_8408))
			.method_10438(consumer, "green_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10529, 8)
			.method_10454(class_1802.field_8408)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10357, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8408)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10419, 16)
			.method_10434('#', class_2246.field_10357)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10419, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8408)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_green_dye", this.method_10426(class_1802.field_8408))
			.method_10438(consumer, "green_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10526, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8408)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10170)
			.method_10454(class_1802.field_8408)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10359)
			.method_10434('#', class_1802.field_8861)
			.method_10439("###")
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_at_least_9_wheat", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8861))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10582)
			.method_10434('#', class_1802.field_8620)
			.method_10439("##")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10312)
			.method_10434('C', class_2246.field_10034)
			.method_10434('I', class_1802.field_8620)
			.method_10439("I I")
			.method_10439("ICI")
			.method_10439(" I ")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8836)
			.method_10434('A', class_2246.field_10312)
			.method_10434('B', class_1802.field_8045)
			.method_10439("A")
			.method_10439("B")
			.method_10429("has_minecart", this.method_10426(class_1802.field_8045))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8475)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8620)
			.method_10439("XX")
			.method_10439("X#")
			.method_10439(" #")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10576, 16)
			.method_10434('#', class_1802.field_8620)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10085)
			.method_10434('#', class_1802.field_8620)
			.method_10439("###")
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_at_least_9_iron_ingot", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8660)
			.method_10434('X', class_1802.field_8620)
			.method_10439("X X")
			.method_10439("X X")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8523)
			.method_10434('X', class_1802.field_8620)
			.method_10439("X X")
			.method_10439("XXX")
			.method_10439("XXX")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_9973, 3)
			.method_10434('#', class_1802.field_8620)
			.method_10439("##")
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8743)
			.method_10434('X', class_1802.field_8620)
			.method_10439("XXX")
			.method_10439("X X")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8609)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8620)
			.method_10439("XX")
			.method_10439(" #")
			.method_10439(" #")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2450.method_10448(class_1802.field_8620, 9)
			.method_10454(class_2246.field_10085)
			.method_10452("iron_ingot")
			.method_10442("has_at_least_9_iron_ingot", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8620))
			.method_10442("has_iron_block", this.method_10426(class_2246.field_10085))
			.method_10450(consumer, "iron_ingot_from_iron_block");
		class_2447.method_10437(class_1802.field_8620)
			.method_10434('#', class_1802.field_8675)
			.method_10439("###")
			.method_10439("###")
			.method_10439("###")
			.method_10435("iron_ingot")
			.method_10429("has_at_least_9_iron_nugget", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8675))
			.method_10438(consumer, "iron_ingot_from_nuggets");
		class_2447.method_10437(class_1802.field_8396)
			.method_10434('X', class_1802.field_8620)
			.method_10439("XXX")
			.method_10439("X X")
			.method_10439("X X")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2450.method_10448(class_1802.field_8675, 9)
			.method_10454(class_1802.field_8620)
			.method_10442("has_at_least_9_iron_nugget", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8675))
			.method_10442("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8403)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8620)
			.method_10439("XXX")
			.method_10439(" # ")
			.method_10439(" # ")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8699)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8620)
			.method_10439("X")
			.method_10439("#")
			.method_10439("#")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8371)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8620)
			.method_10439("X")
			.method_10439("X")
			.method_10439("#")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10453)
			.method_10434('#', class_1802.field_8620)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8143)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8745)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10429("has_leather", this.method_10426(class_1802.field_8745))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10223)
			.method_10433('#', class_3489.field_15537)
			.method_10434('X', class_1802.field_8477)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10429("has_diamond", this.method_10426(class_1802.field_8477))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10303, 3)
			.method_10434('#', class_2246.field_10306)
			.method_10439("##")
			.method_10439("##")
			.method_10435("bark")
			.method_10429("has_log", this.method_10426(class_2246.field_10306))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8730)
			.method_10434('#', class_2246.field_10334)
			.method_10439("# #")
			.method_10439("###")
			.method_10435("boat")
			.method_10429("in_water", this.method_10422(class_2246.field_10382))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10553)
			.method_10454(class_2246.field_10334)
			.method_10452("wooden_button")
			.method_10442("has_planks", this.method_10426(class_2246.field_10334))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10627, 3)
			.method_10434('#', class_2246.field_10334)
			.method_10439("##")
			.method_10439("##")
			.method_10439("##")
			.method_10435("wooden_door")
			.method_10429("has_planks", this.method_10426(class_2246.field_10334))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10319, 3)
			.method_10434('#', class_1802.field_8600)
			.method_10434('W', class_2246.field_10334)
			.method_10439("W#W")
			.method_10439("W#W")
			.method_10435("wooden_fence")
			.method_10429("has_planks", this.method_10426(class_2246.field_10334))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10041)
			.method_10434('#', class_1802.field_8600)
			.method_10434('W', class_2246.field_10334)
			.method_10439("#W#")
			.method_10439("#W#")
			.method_10435("wooden_fence_gate")
			.method_10429("has_planks", this.method_10426(class_2246.field_10334))
			.method_10431(consumer);
		class_2450.method_10448(class_2246.field_10334, 4)
			.method_10446(class_3489.field_15538)
			.method_10452("planks")
			.method_10442("has_log", this.method_10420(class_3489.field_15538))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10026)
			.method_10434('#', class_2246.field_10334)
			.method_10439("##")
			.method_10435("wooden_pressure_plate")
			.method_10429("has_planks", this.method_10426(class_2246.field_10334))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10617, 6)
			.method_10434('#', class_2246.field_10334)
			.method_10439("###")
			.method_10435("wooden_slab")
			.method_10429("has_planks", this.method_10426(class_2246.field_10334))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10122, 4)
			.method_10434('#', class_2246.field_10334)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10435("wooden_stairs")
			.method_10429("has_planks", this.method_10426(class_2246.field_10334))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10017, 2)
			.method_10434('#', class_2246.field_10334)
			.method_10439("###")
			.method_10439("###")
			.method_10435("wooden_trapdoor")
			.method_10429("has_planks", this.method_10426(class_2246.field_10334))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_9983, 3)
			.method_10434('#', class_1802.field_8600)
			.method_10439("# #")
			.method_10439("###")
			.method_10439("# #")
			.method_10429("has_stick", this.method_10426(class_1802.field_8600))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10441)
			.method_10434('#', class_1802.field_8759)
			.method_10439("###")
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_at_least_9_lapis", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8759))
			.method_10431(consumer);
		class_2450.method_10448(class_1802.field_8759, 9)
			.method_10454(class_2246.field_10441)
			.method_10442("has_at_least_9_lapis", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8759))
			.method_10442("has_lapis_block", this.method_10426(class_2246.field_10441))
			.method_10444(consumer);
		class_2447.method_10436(class_1802.field_8719, 2)
			.method_10434('~', class_1802.field_8276)
			.method_10434('O', class_1802.field_8777)
			.method_10439("~~ ")
			.method_10439("~O ")
			.method_10439("  ~")
			.method_10429("has_slime_ball", this.method_10426(class_1802.field_8777))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8745)
			.method_10434('#', class_1802.field_8245)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_rabbit_hide", this.method_10426(class_1802.field_8245))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8370)
			.method_10434('X', class_1802.field_8745)
			.method_10439("X X")
			.method_10439("X X")
			.method_10429("has_leather", this.method_10426(class_1802.field_8745))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8577)
			.method_10434('X', class_1802.field_8745)
			.method_10439("X X")
			.method_10439("XXX")
			.method_10439("XXX")
			.method_10429("has_leather", this.method_10426(class_1802.field_8745))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8267)
			.method_10434('X', class_1802.field_8745)
			.method_10439("XXX")
			.method_10439("X X")
			.method_10429("has_leather", this.method_10426(class_1802.field_8745))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8570)
			.method_10434('X', class_1802.field_8745)
			.method_10439("XXX")
			.method_10439("X X")
			.method_10439("X X")
			.method_10429("has_leather", this.method_10426(class_1802.field_8745))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_18138)
			.method_10434('X', class_1802.field_8745)
			.method_10439("X X")
			.method_10439("XXX")
			.method_10439("X X")
			.method_10429("has_leather", this.method_10426(class_1802.field_8745))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_16330)
			.method_10433('S', class_3489.field_15534)
			.method_10434('B', class_2246.field_10504)
			.method_10439("SSS")
			.method_10439(" B ")
			.method_10439(" S ")
			.method_10429("has_book", this.method_10426(class_1802.field_8529))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10363)
			.method_10434('#', class_2246.field_10445)
			.method_10434('X', class_1802.field_8600)
			.method_10439("X")
			.method_10439("#")
			.method_10429("has_cobblestone", this.method_10426(class_2246.field_10445))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8379)
			.method_10434('#', class_2246.field_10294)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_light_blue_wool", this.method_10426(class_2246.field_10294))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8286)
			.method_10434('#', class_2246.field_10294)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_light_blue_wool", this.method_10426(class_2246.field_10294))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8286)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8273)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "light_blue_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_10290, 3)
			.method_10434('#', class_2246.field_10294)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_light_blue_wool", this.method_10426(class_2246.field_10294))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10290, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8273)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_light_blue_dye", this.method_10426(class_1802.field_8273))
			.method_10438(consumer, "light_blue_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10321, 8)
			.method_10454(class_1802.field_8273)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8273)
			.method_10454(class_2246.field_10086)
			.method_10452("light_blue_dye")
			.method_10442("has_red_flower", this.method_10426(class_2246.field_10086))
			.method_10450(consumer, "light_blue_dye_from_blue_orchid");
		class_2450.method_10448(class_1802.field_8273, 2)
			.method_10454(class_1802.field_8345)
			.method_10454(class_1802.field_8446)
			.method_10452("light_blue_dye")
			.method_10442("has_blue_dye", this.method_10426(class_1802.field_8345))
			.method_10442("has_white_dye", this.method_10426(class_1802.field_8446))
			.method_10450(consumer, "light_blue_dye_from_blue_white_dye");
		class_2447.method_10436(class_2246.field_10271, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8273)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10193, 16)
			.method_10434('#', class_2246.field_10271)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10193, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8273)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_light_blue_dye", this.method_10426(class_1802.field_8273))
			.method_10438(consumer, "light_blue_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10325, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8273)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10294)
			.method_10454(class_1802.field_8273)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8855)
			.method_10434('#', class_2246.field_10222)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_light_gray_wool", this.method_10426(class_2246.field_10222))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8146)
			.method_10434('#', class_2246.field_10222)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_light_gray_wool", this.method_10426(class_2246.field_10222))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8146)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8851)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "light_gray_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_10209, 3)
			.method_10434('#', class_2246.field_10222)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_light_gray_wool", this.method_10426(class_2246.field_10222))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10209, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8851)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_light_gray_dye", this.method_10426(class_1802.field_8851))
			.method_10438(consumer, "light_gray_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10628, 8)
			.method_10454(class_1802.field_8851)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8851)
			.method_10454(class_2246.field_10573)
			.method_10452("light_gray_dye")
			.method_10442("has_red_flower", this.method_10426(class_2246.field_10573))
			.method_10450(consumer, "light_gray_dye_from_azure_bluet");
		class_2450.method_10448(class_1802.field_8851, 2)
			.method_10454(class_1802.field_8298)
			.method_10454(class_1802.field_8446)
			.method_10452("light_gray_dye")
			.method_10442("has_gray_dye", this.method_10426(class_1802.field_8298))
			.method_10442("has_white_dye", this.method_10426(class_1802.field_8446))
			.method_10450(consumer, "light_gray_dye_from_gray_white_dye");
		class_2450.method_10448(class_1802.field_8851, 3)
			.method_10454(class_1802.field_8226)
			.method_10449(class_1802.field_8446, 2)
			.method_10452("light_gray_dye")
			.method_10442("has_white_dye", this.method_10426(class_1802.field_8446))
			.method_10442("has_black_dye", this.method_10426(class_1802.field_8226))
			.method_10450(consumer, "light_gray_dye_from_black_white_dye");
		class_2450.method_10447(class_1802.field_8851)
			.method_10454(class_2246.field_10554)
			.method_10452("light_gray_dye")
			.method_10442("has_red_flower", this.method_10426(class_2246.field_10554))
			.method_10450(consumer, "light_gray_dye_from_oxeye_daisy");
		class_2450.method_10447(class_1802.field_8851)
			.method_10454(class_2246.field_10156)
			.method_10452("light_gray_dye")
			.method_10442("has_red_flower", this.method_10426(class_2246.field_10156))
			.method_10450(consumer, "light_gray_dye_from_white_tulip");
		class_2447.method_10436(class_2246.field_9996, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8851)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10129, 16)
			.method_10434('#', class_2246.field_9996)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10129, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8851)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_light_gray_dye", this.method_10426(class_1802.field_8851))
			.method_10438(consumer, "light_gray_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10590, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8851)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10222)
			.method_10454(class_1802.field_8851)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10224)
			.method_10434('#', class_1802.field_8695)
			.method_10439("##")
			.method_10429("has_gold_ingot", this.method_10426(class_1802.field_8695))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8778)
			.method_10434('#', class_2246.field_10028)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_lime_wool", this.method_10426(class_2246.field_10028))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8679)
			.method_10434('#', class_2246.field_10028)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_lime_wool", this.method_10426(class_2246.field_10028))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8679)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8131)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "lime_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_10040, 3)
			.method_10434('#', class_2246.field_10028)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_lime_wool", this.method_10426(class_2246.field_10028))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10040, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8131)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_lime_dye", this.method_10426(class_1802.field_8131))
			.method_10438(consumer, "lime_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10133, 8)
			.method_10454(class_1802.field_8131)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10448(class_1802.field_8131, 2)
			.method_10454(class_1802.field_8408)
			.method_10454(class_1802.field_8446)
			.method_10442("has_green_dye", this.method_10426(class_1802.field_8408))
			.method_10442("has_white_dye", this.method_10426(class_1802.field_8446))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10157, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8131)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10305, 16)
			.method_10434('#', class_2246.field_10157)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10305, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8131)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_lime_dye", this.method_10426(class_1802.field_8131))
			.method_10438(consumer, "lime_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10014, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8131)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10028)
			.method_10454(class_1802.field_8131)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10009)
			.method_10434('A', class_2246.field_10147)
			.method_10434('B', class_2246.field_10336)
			.method_10439("A")
			.method_10439("B")
			.method_10429("has_carved_pumpkin", this.method_10426(class_2246.field_10147))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8671)
			.method_10434('#', class_2246.field_10215)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_magenta_wool", this.method_10426(class_2246.field_10215))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8349)
			.method_10434('#', class_2246.field_10215)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_magenta_wool", this.method_10426(class_2246.field_10215))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8349)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8669)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "magenta_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_10482, 3)
			.method_10434('#', class_2246.field_10215)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_magenta_wool", this.method_10426(class_2246.field_10215))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10482, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8669)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_magenta_dye", this.method_10426(class_1802.field_8669))
			.method_10438(consumer, "magenta_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10300, 8)
			.method_10454(class_1802.field_8669)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8669)
			.method_10454(class_2246.field_10226)
			.method_10452("magenta_dye")
			.method_10442("has_red_flower", this.method_10426(class_2246.field_10226))
			.method_10450(consumer, "magenta_dye_from_allium");
		class_2450.method_10448(class_1802.field_8669, 4)
			.method_10454(class_1802.field_8345)
			.method_10449(class_1802.field_8264, 2)
			.method_10454(class_1802.field_8446)
			.method_10452("magenta_dye")
			.method_10442("has_blue_dye", this.method_10426(class_1802.field_8345))
			.method_10442("has_rose_red", this.method_10426(class_1802.field_8264))
			.method_10442("has_white_dye", this.method_10426(class_1802.field_8446))
			.method_10450(consumer, "magenta_dye_from_blue_red_white_dye");
		class_2450.method_10448(class_1802.field_8669, 3)
			.method_10454(class_1802.field_8345)
			.method_10454(class_1802.field_8264)
			.method_10454(class_1802.field_8330)
			.method_10452("magenta_dye")
			.method_10442("has_pink_dye", this.method_10426(class_1802.field_8330))
			.method_10442("has_blue_dye", this.method_10426(class_1802.field_8345))
			.method_10442("has_red_dye", this.method_10426(class_1802.field_8264))
			.method_10450(consumer, "magenta_dye_from_blue_red_pink");
		class_2450.method_10448(class_1802.field_8669, 2)
			.method_10454(class_2246.field_10378)
			.method_10452("magenta_dye")
			.method_10442("has_double_plant", this.method_10426(class_2246.field_10378))
			.method_10450(consumer, "magenta_dye_from_lilac");
		class_2450.method_10448(class_1802.field_8669, 2)
			.method_10454(class_1802.field_8296)
			.method_10454(class_1802.field_8330)
			.method_10452("magenta_dye")
			.method_10442("has_pink_dye", this.method_10426(class_1802.field_8330))
			.method_10442("has_purple_dye", this.method_10426(class_1802.field_8296))
			.method_10450(consumer, "magenta_dye_from_purple_and_pink");
		class_2447.method_10436(class_2246.field_10574, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8669)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10469, 16)
			.method_10434('#', class_2246.field_10574)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10469, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8669)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_magenta_dye", this.method_10426(class_1802.field_8669))
			.method_10438(consumer, "magenta_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10015, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8669)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10215)
			.method_10454(class_1802.field_8669)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10092)
			.method_10434('#', class_1802.field_8135)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_magma_cream", this.method_10426(class_1802.field_8135))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8135)
			.method_10454(class_1802.field_8183)
			.method_10454(class_1802.field_8777)
			.method_10442("has_blaze_powder", this.method_10426(class_1802.field_8183))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8895)
			.method_10434('#', class_1802.field_8407)
			.method_10434('X', class_1802.field_8251)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10429("has_compass", this.method_10426(class_1802.field_8251))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10545)
			.method_10434('M', class_1802.field_8497)
			.method_10439("MMM")
			.method_10439("MMM")
			.method_10439("MMM")
			.method_10429("has_melon", this.method_10426(class_1802.field_8497))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8188)
			.method_10454(class_1802.field_8497)
			.method_10442("has_melon", this.method_10426(class_1802.field_8497))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8045)
			.method_10434('#', class_1802.field_8620)
			.method_10439("# #")
			.method_10439("###")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_9989)
			.method_10454(class_2246.field_10445)
			.method_10454(class_2246.field_10597)
			.method_10442("has_vine", this.method_10426(class_2246.field_10597))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_9990, 6)
			.method_10434('#', class_2246.field_9989)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_mossy_cobblestone", this.method_10426(class_2246.field_9989))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10065)
			.method_10454(class_2246.field_10056)
			.method_10454(class_2246.field_10597)
			.method_10442("has_mossy_cobblestone", this.method_10426(class_2246.field_9989))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8208)
			.method_10454(class_2246.field_10251)
			.method_10454(class_2246.field_10559)
			.method_10454(class_1802.field_8428)
			.method_10442("has_mushroom_stew", this.method_10426(class_1802.field_8208))
			.method_10442("has_bowl", this.method_10426(class_1802.field_8428))
			.method_10442("has_brown_mushroom", this.method_10426(class_2246.field_10251))
			.method_10442("has_red_mushroom", this.method_10426(class_2246.field_10559))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10266)
			.method_10434('N', class_1802.field_8729)
			.method_10439("NN")
			.method_10439("NN")
			.method_10429("has_netherbrick", this.method_10426(class_1802.field_8729))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10364, 6)
			.method_10434('#', class_2246.field_10266)
			.method_10434('-', class_1802.field_8729)
			.method_10439("#-#")
			.method_10439("#-#")
			.method_10429("has_nether_brick", this.method_10426(class_2246.field_10266))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10390, 6)
			.method_10434('#', class_2246.field_10266)
			.method_10439("###")
			.method_10429("has_nether_brick", this.method_10426(class_2246.field_10266))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10159, 4)
			.method_10434('#', class_2246.field_10266)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_nether_brick", this.method_10426(class_2246.field_10266))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10541)
			.method_10434('#', class_1802.field_8790)
			.method_10439("###")
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_nether_wart", this.method_10426(class_1802.field_8790))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10179)
			.method_10433('#', class_3489.field_15537)
			.method_10434('X', class_1802.field_8725)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10429("has_redstone", this.method_10426(class_1802.field_8725))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10126, 3)
			.method_10434('#', class_2246.field_10431)
			.method_10439("##")
			.method_10439("##")
			.method_10435("bark")
			.method_10429("has_log", this.method_10426(class_2246.field_10431))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10057)
			.method_10454(class_2246.field_10161)
			.method_10452("wooden_button")
			.method_10442("has_planks", this.method_10426(class_2246.field_10161))
			.method_10444(consumer);
		class_2450.method_10448(class_2246.field_10161, 4)
			.method_10446(class_3489.field_15545)
			.method_10452("planks")
			.method_10442("has_log", this.method_10420(class_3489.field_15545))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10484)
			.method_10434('#', class_2246.field_10161)
			.method_10439("##")
			.method_10435("wooden_pressure_plate")
			.method_10429("has_planks", this.method_10426(class_2246.field_10161))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10119, 6)
			.method_10434('#', class_2246.field_10161)
			.method_10439("###")
			.method_10435("wooden_slab")
			.method_10429("has_planks", this.method_10426(class_2246.field_10161))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10563, 4)
			.method_10434('#', class_2246.field_10161)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10435("wooden_stairs")
			.method_10429("has_planks", this.method_10426(class_2246.field_10161))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10137, 2)
			.method_10434('#', class_2246.field_10161)
			.method_10439("###")
			.method_10439("###")
			.method_10435("wooden_trapdoor")
			.method_10429("has_planks", this.method_10426(class_2246.field_10161))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10282)
			.method_10434('Q', class_1802.field_8155)
			.method_10434('R', class_1802.field_8725)
			.method_10434('#', class_2246.field_10445)
			.method_10439("###")
			.method_10439("RRQ")
			.method_10439("###")
			.method_10429("has_quartz", this.method_10426(class_1802.field_8155))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8824)
			.method_10434('#', class_2246.field_10095)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_orange_wool", this.method_10426(class_2246.field_10095))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8059)
			.method_10434('#', class_2246.field_10095)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_orange_wool", this.method_10426(class_2246.field_10095))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8059)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8492)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "orange_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_9977, 3)
			.method_10434('#', class_2246.field_10095)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_orange_wool", this.method_10426(class_2246.field_10095))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_9977, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8492)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_oramge_dye", this.method_10426(class_1802.field_8492))
			.method_10438(consumer, "orange_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10022, 8)
			.method_10454(class_1802.field_8492)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8492)
			.method_10454(class_2246.field_10048)
			.method_10452("orange_dye")
			.method_10442("has_red_flower", this.method_10426(class_2246.field_10048))
			.method_10450(consumer, "orange_dye_from_orange_tulip");
		class_2450.method_10448(class_1802.field_8492, 2)
			.method_10454(class_1802.field_8264)
			.method_10454(class_1802.field_8192)
			.method_10452("orange_dye")
			.method_10442("has_red_dye", this.method_10426(class_1802.field_8264))
			.method_10442("has_yellow_dye", this.method_10426(class_1802.field_8192))
			.method_10450(consumer, "orange_dye_from_red_yellow");
		class_2447.method_10436(class_2246.field_10227, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8492)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10496, 16)
			.method_10434('#', class_2246.field_10227)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10496, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8492)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_orange_dye", this.method_10426(class_1802.field_8492))
			.method_10438(consumer, "orange_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10184, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8492)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10095)
			.method_10454(class_1802.field_8492)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8892)
			.method_10434('#', class_1802.field_8600)
			.method_10428('X', class_1856.method_8106(class_3489.field_15544))
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10429("has_wool", this.method_10420(class_3489.field_15544))
			.method_10431(consumer);
		class_2447.method_10436(class_1802.field_8407, 3)
			.method_10434('#', class_2246.field_10424)
			.method_10439("###")
			.method_10429("has_reeds", this.method_10426(class_2246.field_10424))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10437, 2)
			.method_10434('#', class_2246.field_10153)
			.method_10439("#")
			.method_10439("#")
			.method_10429("has_chiseled_quartz_block", this.method_10426(class_2246.field_10044))
			.method_10429("has_quartz_block", this.method_10426(class_2246.field_10153))
			.method_10429("has_quartz_pillar", this.method_10426(class_2246.field_10437))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10225)
			.method_10449(class_2246.field_10295, 9)
			.method_10442("has_at_least_9_ice", this.method_10424(class_2096.class_2100.method_9053(9), class_2246.field_10295))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8329)
			.method_10434('#', class_2246.field_10459)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_pink_wool", this.method_10426(class_2246.field_10459))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8417)
			.method_10434('#', class_2246.field_10459)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_pink_wool", this.method_10426(class_2246.field_10459))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8417)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8330)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "pink_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_10393, 3)
			.method_10434('#', class_2246.field_10459)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_pink_wool", this.method_10426(class_2246.field_10459))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10393, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8330)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_pink_dye", this.method_10426(class_1802.field_8330))
			.method_10438(consumer, "pink_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10522, 8)
			.method_10454(class_1802.field_8330)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10448(class_1802.field_8330, 2)
			.method_10454(class_2246.field_10003)
			.method_10452("pink_dye")
			.method_10442("has_double_plant", this.method_10426(class_2246.field_10003))
			.method_10450(consumer, "pink_dye_from_peony");
		class_2450.method_10447(class_1802.field_8330)
			.method_10454(class_2246.field_10315)
			.method_10452("pink_dye")
			.method_10442("has_red_flower", this.method_10426(class_2246.field_10315))
			.method_10450(consumer, "pink_dye_from_pink_tulip");
		class_2450.method_10448(class_1802.field_8330, 2)
			.method_10454(class_1802.field_8264)
			.method_10454(class_1802.field_8446)
			.method_10452("pink_dye")
			.method_10442("has_white_dye", this.method_10426(class_1802.field_8446))
			.method_10442("has_red_dye", this.method_10426(class_1802.field_8264))
			.method_10450(consumer, "pink_dye_from_red_white_dye");
		class_2447.method_10436(class_2246.field_10317, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8330)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10565, 16)
			.method_10434('#', class_2246.field_10317)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10565, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8330)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_pink_dye", this.method_10426(class_1802.field_8330))
			.method_10438(consumer, "pink_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10444, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8330)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10459)
			.method_10454(class_1802.field_8330)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10560)
			.method_10434('R', class_1802.field_8725)
			.method_10434('#', class_2246.field_10445)
			.method_10433('T', class_3489.field_15537)
			.method_10434('X', class_1802.field_8620)
			.method_10439("TTT")
			.method_10439("#X#")
			.method_10439("#R#")
			.method_10429("has_redstone", this.method_10426(class_1802.field_8725))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10289, 4)
			.method_10434('S', class_2246.field_10474)
			.method_10439("SS")
			.method_10439("SS")
			.method_10429("has_stone", this.method_10426(class_2246.field_10474))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10346, 4)
			.method_10434('S', class_2246.field_10508)
			.method_10439("SS")
			.method_10439("SS")
			.method_10429("has_stone", this.method_10426(class_2246.field_10508))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10093, 4)
			.method_10434('S', class_2246.field_10115)
			.method_10439("SS")
			.method_10439("SS")
			.method_10429("has_stone", this.method_10426(class_2246.field_10115))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10135)
			.method_10434('S', class_1802.field_8662)
			.method_10439("SS")
			.method_10439("SS")
			.method_10429("has_prismarine_shard", this.method_10426(class_1802.field_8662))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10006)
			.method_10434('S', class_1802.field_8662)
			.method_10439("SSS")
			.method_10439("SSS")
			.method_10439("SSS")
			.method_10429("has_prismarine_shard", this.method_10426(class_1802.field_8662))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10389, 6)
			.method_10434('#', class_2246.field_10135)
			.method_10439("###")
			.method_10429("has_prismarine", this.method_10426(class_2246.field_10135))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10236, 6)
			.method_10434('#', class_2246.field_10006)
			.method_10439("###")
			.method_10429("has_prismarine_bricks", this.method_10426(class_2246.field_10006))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10623, 6)
			.method_10434('#', class_2246.field_10297)
			.method_10439("###")
			.method_10429("has_dark_prismarine", this.method_10426(class_2246.field_10297))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8741)
			.method_10454(class_2246.field_10261)
			.method_10454(class_1802.field_8479)
			.method_10454(class_1802.field_8803)
			.method_10442("has_carved_pumpkin", this.method_10426(class_2246.field_10147))
			.method_10442("has_pumpkin", this.method_10426(class_2246.field_10261))
			.method_10444(consumer);
		class_2450.method_10448(class_1802.field_8706, 4)
			.method_10454(class_2246.field_10261)
			.method_10442("has_pumpkin", this.method_10426(class_2246.field_10261))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8405)
			.method_10434('#', class_2246.field_10259)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_purple_wool", this.method_10426(class_2246.field_10259))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8262)
			.method_10434('#', class_2246.field_10259)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_purple_wool", this.method_10426(class_2246.field_10259))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8262)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8296)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "purple_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_10510, 3)
			.method_10434('#', class_2246.field_10259)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_purple_wool", this.method_10426(class_2246.field_10259))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10510, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8296)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_purple_dye", this.method_10426(class_1802.field_8296))
			.method_10438(consumer, "purple_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10404, 8)
			.method_10454(class_1802.field_8296)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10448(class_1802.field_8296, 2)
			.method_10454(class_1802.field_8345)
			.method_10454(class_1802.field_8264)
			.method_10442("has_blue_dye", this.method_10426(class_1802.field_8345))
			.method_10442("has_red_dye", this.method_10426(class_1802.field_8264))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10603)
			.method_10434('#', class_2246.field_10034)
			.method_10434('-', class_1802.field_8815)
			.method_10439("-")
			.method_10439("#")
			.method_10439("-")
			.method_10429("has_shulker_shell", this.method_10426(class_1802.field_8815))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10399, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8296)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10152, 16)
			.method_10434('#', class_2246.field_10399)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10152, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8296)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_purple_dye", this.method_10426(class_1802.field_8296))
			.method_10438(consumer, "purple_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10570, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8296)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10259)
			.method_10454(class_1802.field_8296)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10286, 4)
			.method_10434('F', class_1802.field_8882)
			.method_10439("FF")
			.method_10439("FF")
			.method_10429("has_chorus_fruit_popped", this.method_10426(class_1802.field_8882))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10505)
			.method_10434('#', class_2246.field_10175)
			.method_10439("#")
			.method_10439("#")
			.method_10429("has_purpur_block", this.method_10426(class_2246.field_10286))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10175, 6)
			.method_10428('#', class_1856.method_8091(class_2246.field_10286, class_2246.field_10505))
			.method_10439("###")
			.method_10429("has_purpur_block", this.method_10426(class_2246.field_10286))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_9992, 4)
			.method_10428('#', class_1856.method_8091(class_2246.field_10286, class_2246.field_10505))
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_purpur_block", this.method_10426(class_2246.field_10286))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10153)
			.method_10434('#', class_1802.field_8155)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_quartz", this.method_10426(class_1802.field_8155))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10237, 6)
			.method_10428('#', class_1856.method_8091(class_2246.field_10044, class_2246.field_10153, class_2246.field_10437))
			.method_10439("###")
			.method_10429("has_chiseled_quartz_block", this.method_10426(class_2246.field_10044))
			.method_10429("has_quartz_block", this.method_10426(class_2246.field_10153))
			.method_10429("has_quartz_pillar", this.method_10426(class_2246.field_10437))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10451, 4)
			.method_10428('#', class_1856.method_8091(class_2246.field_10044, class_2246.field_10153, class_2246.field_10437))
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_chiseled_quartz_block", this.method_10426(class_2246.field_10044))
			.method_10429("has_quartz_block", this.method_10426(class_2246.field_10153))
			.method_10429("has_quartz_pillar", this.method_10426(class_2246.field_10437))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8308)
			.method_10454(class_1802.field_8512)
			.method_10454(class_1802.field_8752)
			.method_10454(class_1802.field_8428)
			.method_10454(class_1802.field_8179)
			.method_10454(class_2246.field_10251)
			.method_10452("rabbit_stew")
			.method_10442("has_cooked_rabbit", this.method_10426(class_1802.field_8752))
			.method_10450(consumer, "rabbit_stew_from_brown_mushroom");
		class_2450.method_10447(class_1802.field_8308)
			.method_10454(class_1802.field_8512)
			.method_10454(class_1802.field_8752)
			.method_10454(class_1802.field_8428)
			.method_10454(class_1802.field_8179)
			.method_10454(class_2246.field_10559)
			.method_10452("rabbit_stew")
			.method_10442("has_cooked_rabbit", this.method_10426(class_1802.field_8752))
			.method_10450(consumer, "rabbit_stew_from_red_mushroom");
		class_2447.method_10436(class_2246.field_10167, 16)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8620)
			.method_10439("X X")
			.method_10439("X#X")
			.method_10439("X X")
			.method_10429("has_minecart", this.method_10426(class_1802.field_8045))
			.method_10431(consumer);
		class_2450.method_10448(class_1802.field_8725, 9)
			.method_10454(class_2246.field_10002)
			.method_10442("has_redstone_block", this.method_10426(class_2246.field_10002))
			.method_10442("has_at_least_9_redstone", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8725))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10002)
			.method_10434('#', class_1802.field_8725)
			.method_10439("###")
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_at_least_9_redstone", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8725))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10524)
			.method_10434('R', class_1802.field_8725)
			.method_10434('G', class_2246.field_10171)
			.method_10439(" R ")
			.method_10439("RGR")
			.method_10439(" R ")
			.method_10429("has_glowstone", this.method_10426(class_2246.field_10171))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10523)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_1802.field_8725)
			.method_10439("X")
			.method_10439("#")
			.method_10429("has_redstone", this.method_10426(class_1802.field_8725))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8586)
			.method_10434('#', class_2246.field_10314)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_red_wool", this.method_10426(class_2246.field_10314))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8789)
			.method_10434('#', class_2246.field_10314)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_red_wool", this.method_10426(class_2246.field_10314))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8789)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8264)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "red_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_10536, 3)
			.method_10434('#', class_2246.field_10314)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_red_wool", this.method_10426(class_2246.field_10314))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10536, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8264)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_red_dye", this.method_10426(class_1802.field_8264))
			.method_10438(consumer, "red_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10287, 8)
			.method_10454(class_1802.field_8264)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8264)
			.method_10454(class_1802.field_8186)
			.method_10452("red_dye")
			.method_10442("has_beetroot", this.method_10426(class_1802.field_8186))
			.method_10450(consumer, "red_dye_from_beetroot");
		class_2450.method_10447(class_1802.field_8264)
			.method_10454(class_2246.field_10449)
			.method_10452("red_dye")
			.method_10442("has_red_flower", this.method_10426(class_2246.field_10449))
			.method_10450(consumer, "red_dye_from_poppy");
		class_2450.method_10448(class_1802.field_8264, 2)
			.method_10454(class_2246.field_10430)
			.method_10452("red_dye")
			.method_10442("has_double_plant", this.method_10426(class_2246.field_10430))
			.method_10450(consumer, "red_dye_from_rose_bush");
		class_2450.method_10447(class_1802.field_8264)
			.method_10454(class_2246.field_10270)
			.method_10452("red_dye")
			.method_10442("has_red_flower", this.method_10426(class_2246.field_10270))
			.method_10450(consumer, "red_dye_from_tulip");
		class_2447.method_10437(class_2246.field_9986)
			.method_10434('W', class_1802.field_8790)
			.method_10434('N', class_1802.field_8729)
			.method_10439("NW")
			.method_10439("WN")
			.method_10429("has_nether_wart", this.method_10426(class_1802.field_8790))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10344)
			.method_10434('#', class_2246.field_10534)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_sand", this.method_10426(class_2246.field_10534))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10624, 6)
			.method_10428('#', class_1856.method_8091(class_2246.field_10344, class_2246.field_10117))
			.method_10439("###")
			.method_10429("has_red_sandstone", this.method_10426(class_2246.field_10344))
			.method_10429("has_chiseled_red_sandstone", this.method_10426(class_2246.field_10117))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_18891, 6)
			.method_10434('#', class_2246.field_10518)
			.method_10439("###")
			.method_10429("has_cut_red_sandstone", this.method_10426(class_2246.field_10518))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10420, 4)
			.method_10428('#', class_1856.method_8091(class_2246.field_10344, class_2246.field_10117, class_2246.field_10518))
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_red_sandstone", this.method_10426(class_2246.field_10344))
			.method_10429("has_chiseled_red_sandstone", this.method_10426(class_2246.field_10117))
			.method_10429("has_cut_red_sandstone", this.method_10426(class_2246.field_10518))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10272, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8264)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10118, 16)
			.method_10434('#', class_2246.field_10272)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10118, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8264)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_red_dye", this.method_10426(class_1802.field_8264))
			.method_10438(consumer, "red_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10328, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8264)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10314)
			.method_10454(class_1802.field_8264)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10450)
			.method_10434('#', class_2246.field_10523)
			.method_10434('X', class_1802.field_8725)
			.method_10434('I', class_2246.field_10340)
			.method_10439("#X#")
			.method_10439("III")
			.method_10429("has_redstone_torch", this.method_10426(class_2246.field_10523))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_9979)
			.method_10434('#', class_2246.field_10102)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_sand", this.method_10426(class_2246.field_10102))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10007, 6)
			.method_10428('#', class_1856.method_8091(class_2246.field_9979, class_2246.field_10292))
			.method_10439("###")
			.method_10429("has_sandstone", this.method_10426(class_2246.field_9979))
			.method_10429("has_chiseled_sandstone", this.method_10426(class_2246.field_10292))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_18890, 6)
			.method_10434('#', class_2246.field_10361)
			.method_10439("###")
			.method_10429("has_cut_sandstone", this.method_10426(class_2246.field_10361))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10142, 4)
			.method_10428('#', class_1856.method_8091(class_2246.field_9979, class_2246.field_10292, class_2246.field_10361))
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_sandstone", this.method_10426(class_2246.field_9979))
			.method_10429("has_chiseled_sandstone", this.method_10426(class_2246.field_10292))
			.method_10429("has_cut_sandstone", this.method_10426(class_2246.field_10361))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10174)
			.method_10434('S', class_1802.field_8662)
			.method_10434('C', class_1802.field_8434)
			.method_10439("SCS")
			.method_10439("CCC")
			.method_10439("SCS")
			.method_10429("has_prismarine_crystals", this.method_10426(class_1802.field_8434))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8868)
			.method_10434('#', class_1802.field_8620)
			.method_10439(" #")
			.method_10439("# ")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8255)
			.method_10433('W', class_3489.field_15537)
			.method_10434('o', class_1802.field_8620)
			.method_10439("WoW")
			.method_10439("WWW")
			.method_10439(" W ")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10436(class_1802.field_8788, 3)
			.method_10434('#', class_1802.field_8118)
			.method_10434('X', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" X ")
			.method_10429("has_oak_planks", this.method_10426(class_1802.field_8118))
			.method_10431(consumer);
		class_2447.method_10436(class_1802.field_8111, 3)
			.method_10434('#', class_1802.field_8113)
			.method_10434('X', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" X ")
			.method_10429("has_spruce_planks", this.method_10426(class_1802.field_8113))
			.method_10431(consumer);
		class_2447.method_10436(class_1802.field_8422, 3)
			.method_10434('#', class_1802.field_8191)
			.method_10434('X', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" X ")
			.method_10429("has_birch_planks", this.method_10426(class_1802.field_8191))
			.method_10431(consumer);
		class_2447.method_10436(class_1802.field_8203, 3)
			.method_10434('#', class_1802.field_8651)
			.method_10434('X', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" X ")
			.method_10429("has_acacia_planks", this.method_10426(class_1802.field_8651))
			.method_10431(consumer);
		class_2447.method_10436(class_1802.field_8867, 3)
			.method_10434('#', class_1802.field_8842)
			.method_10434('X', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" X ")
			.method_10429("has_jungle_planks", this.method_10426(class_1802.field_8842))
			.method_10431(consumer);
		class_2447.method_10436(class_1802.field_8496, 3)
			.method_10434('#', class_1802.field_8404)
			.method_10434('X', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" X ")
			.method_10429("has_dark_oak_planks", this.method_10426(class_1802.field_8404))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10030)
			.method_10434('#', class_1802.field_8777)
			.method_10439("###")
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_at_least_9_slime_ball", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8777))
			.method_10431(consumer);
		class_2450.method_10448(class_1802.field_8777, 9)
			.method_10454(class_2246.field_10030)
			.method_10442("has_at_least_9_slime_ball", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8777))
			.method_10442("has_slime", this.method_10426(class_2246.field_10030))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10518, 4)
			.method_10434('#', class_2246.field_10344)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_red_sandstone", this.method_10426(class_2246.field_10344))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10361, 4)
			.method_10434('#', class_2246.field_9979)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_sandstone", this.method_10426(class_2246.field_9979))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10491)
			.method_10434('#', class_1802.field_8543)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_snowball", this.method_10426(class_1802.field_8543))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10477, 6)
			.method_10434('#', class_2246.field_10491)
			.method_10439("###")
			.method_10429("has_snowball", this.method_10426(class_1802.field_8543))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8597)
			.method_10434('#', class_1802.field_8397)
			.method_10434('X', class_1802.field_8497)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10429("has_melon", this.method_10426(class_1802.field_8497))
			.method_10431(consumer);
		class_2447.method_10436(class_1802.field_8236, 2)
			.method_10434('#', class_1802.field_8601)
			.method_10434('X', class_1802.field_8107)
			.method_10439(" # ")
			.method_10439("#X#")
			.method_10439(" # ")
			.method_10429("has_glowstone_dust", this.method_10426(class_1802.field_8601))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10155, 3)
			.method_10434('#', class_2246.field_10037)
			.method_10439("##")
			.method_10439("##")
			.method_10435("bark")
			.method_10429("has_log", this.method_10426(class_2246.field_10037))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8486)
			.method_10434('#', class_2246.field_9975)
			.method_10439("# #")
			.method_10439("###")
			.method_10435("boat")
			.method_10429("in_water", this.method_10422(class_2246.field_10382))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10066)
			.method_10454(class_2246.field_9975)
			.method_10452("wooden_button")
			.method_10442("has_planks", this.method_10426(class_2246.field_9975))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10521, 3)
			.method_10434('#', class_2246.field_9975)
			.method_10439("##")
			.method_10439("##")
			.method_10439("##")
			.method_10435("wooden_door")
			.method_10429("has_planks", this.method_10426(class_2246.field_9975))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10020, 3)
			.method_10434('#', class_1802.field_8600)
			.method_10434('W', class_2246.field_9975)
			.method_10439("W#W")
			.method_10439("W#W")
			.method_10435("wooden_fence")
			.method_10429("has_planks", this.method_10426(class_2246.field_9975))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10291)
			.method_10434('#', class_1802.field_8600)
			.method_10434('W', class_2246.field_9975)
			.method_10439("#W#")
			.method_10439("#W#")
			.method_10435("wooden_fence_gate")
			.method_10429("has_planks", this.method_10426(class_2246.field_9975))
			.method_10431(consumer);
		class_2450.method_10448(class_2246.field_9975, 4)
			.method_10446(class_3489.field_15549)
			.method_10452("planks")
			.method_10442("has_log", this.method_10420(class_3489.field_15549))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10332)
			.method_10434('#', class_2246.field_9975)
			.method_10439("##")
			.method_10435("wooden_pressure_plate")
			.method_10429("has_planks", this.method_10426(class_2246.field_9975))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10071, 6)
			.method_10434('#', class_2246.field_9975)
			.method_10439("###")
			.method_10435("wooden_slab")
			.method_10429("has_planks", this.method_10426(class_2246.field_9975))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10569, 4)
			.method_10434('#', class_2246.field_9975)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10435("wooden_stairs")
			.method_10429("has_planks", this.method_10426(class_2246.field_9975))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10323, 2)
			.method_10434('#', class_2246.field_9975)
			.method_10439("###")
			.method_10439("###")
			.method_10435("wooden_trapdoor")
			.method_10429("has_planks", this.method_10426(class_2246.field_9975))
			.method_10431(consumer);
		class_2447.method_10436(class_1802.field_8600, 4)
			.method_10433('#', class_3489.field_15537)
			.method_10439("#")
			.method_10439("#")
			.method_10435("sticks")
			.method_10429("has_planks", this.method_10420(class_3489.field_15537))
			.method_10431(consumer);
		class_2447.method_10436(class_1802.field_8600, 1)
			.method_10434('#', class_2246.field_10211)
			.method_10439("#")
			.method_10439("#")
			.method_10435("sticks")
			.method_10429("has_bamboo", this.method_10426(class_2246.field_10211))
			.method_10438(consumer, "stick_from_bamboo_item");
		class_2447.method_10437(class_2246.field_10615)
			.method_10434('P', class_2246.field_10560)
			.method_10434('S', class_1802.field_8777)
			.method_10439("S")
			.method_10439("P")
			.method_10429("has_slime_ball", this.method_10426(class_1802.field_8777))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10056, 4)
			.method_10434('#', class_2246.field_10340)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_stone", this.method_10426(class_2246.field_10340))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8062)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_2246.field_10445)
			.method_10439("XX")
			.method_10439("X#")
			.method_10439(" #")
			.method_10429("has_cobblestone", this.method_10426(class_2246.field_10445))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10131, 6)
			.method_10434('#', class_2246.field_10056)
			.method_10439("###")
			.method_10429("has_stone_bricks", this.method_10420(class_3489.field_15531))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10392, 4)
			.method_10434('#', class_2246.field_10056)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_stone_bricks", this.method_10420(class_3489.field_15531))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10494)
			.method_10454(class_2246.field_10340)
			.method_10442("has_stone", this.method_10426(class_2246.field_10340))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8431)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_2246.field_10445)
			.method_10439("XX")
			.method_10439(" #")
			.method_10439(" #")
			.method_10429("has_cobblestone", this.method_10426(class_2246.field_10445))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8387)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_2246.field_10445)
			.method_10439("XXX")
			.method_10439(" # ")
			.method_10439(" # ")
			.method_10429("has_cobblestone", this.method_10426(class_2246.field_10445))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10158)
			.method_10434('#', class_2246.field_10340)
			.method_10439("##")
			.method_10429("has_stone", this.method_10426(class_2246.field_10340))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8776)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_2246.field_10445)
			.method_10439("X")
			.method_10439("#")
			.method_10439("#")
			.method_10429("has_cobblestone", this.method_10426(class_2246.field_10445))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10454, 6)
			.method_10434('#', class_2246.field_10340)
			.method_10439("###")
			.method_10429("has_stone", this.method_10426(class_2246.field_10340))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10136, 6)
			.method_10434('#', class_2246.field_10360)
			.method_10439("###")
			.method_10429("has_smooth_stone", this.method_10426(class_2246.field_10360))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10596, 4)
			.method_10434('#', class_2246.field_10445)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_cobblestone", this.method_10426(class_2246.field_10445))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8528)
			.method_10434('#', class_1802.field_8600)
			.method_10434('X', class_2246.field_10445)
			.method_10439("X")
			.method_10439("X")
			.method_10439("#")
			.method_10429("has_cobblestone", this.method_10426(class_2246.field_10445))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_10446)
			.method_10434('#', class_1802.field_8276)
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_string", this.method_10426(class_1802.field_8276))
			.method_10438(consumer, "white_wool_from_string");
		class_2450.method_10447(class_1802.field_8479)
			.method_10454(class_2246.field_10424)
			.method_10442("has_reeds", this.method_10426(class_2246.field_10424))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10375)
			.method_10428('#', class_1856.method_8091(class_2246.field_10102, class_2246.field_10534))
			.method_10434('X', class_1802.field_8054)
			.method_10439("X#X")
			.method_10439("#X#")
			.method_10439("X#X")
			.method_10429("has_gunpowder", this.method_10426(class_1802.field_8054))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8069)
			.method_10434('A', class_2246.field_10375)
			.method_10434('B', class_1802.field_8045)
			.method_10439("A")
			.method_10439("B")
			.method_10429("has_minecart", this.method_10426(class_1802.field_8045))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10336, 4)
			.method_10434('#', class_1802.field_8600)
			.method_10428('X', class_1856.method_8091(class_1802.field_8713, class_1802.field_8665))
			.method_10439("X")
			.method_10439("#")
			.method_10429("has_stone_pickaxe", this.method_10426(class_1802.field_8387))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_16541)
			.method_10434('#', class_1802.field_8810)
			.method_10434('X', class_1802.field_8675)
			.method_10439("XXX")
			.method_10439("X#X")
			.method_10439("XXX")
			.method_10429("has_iron_nugget", this.method_10426(class_1802.field_8675))
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10380)
			.method_10454(class_2246.field_10034)
			.method_10454(class_2246.field_10348)
			.method_10442("has_tripwire_hook", this.method_10426(class_2246.field_10348))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_10348, 2)
			.method_10433('#', class_3489.field_15537)
			.method_10434('S', class_1802.field_8600)
			.method_10434('I', class_1802.field_8620)
			.method_10439("I")
			.method_10439("S")
			.method_10439("#")
			.method_10429("has_string", this.method_10426(class_1802.field_8276))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8090)
			.method_10434('X', class_1802.field_8161)
			.method_10439("XXX")
			.method_10439("X X")
			.method_10429("has_scute", this.method_10426(class_1802.field_8161))
			.method_10431(consumer);
		class_2450.method_10448(class_1802.field_8861, 9)
			.method_10454(class_2246.field_10359)
			.method_10442("has_at_least_9_wheat", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8861))
			.method_10442("has_hay_block", this.method_10426(class_2246.field_10359))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8539)
			.method_10434('#', class_2246.field_10446)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8258)
			.method_10434('#', class_2246.field_10446)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10466, 3)
			.method_10434('#', class_2246.field_10446)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10431(consumer);
		class_2450.method_10448(class_2246.field_10197, 8)
			.method_10454(class_1802.field_8446)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8446)
			.method_10454(class_1802.field_8324)
			.method_10452("white_dye")
			.method_10442("has_bone_meal", this.method_10426(class_1802.field_8324))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8446)
			.method_10454(class_2246.field_10548)
			.method_10452("white_dye")
			.method_10442("has_white_flower", this.method_10426(class_2246.field_10548))
			.method_10450(consumer, "white_dye_from_lily_of_the_valley");
		class_2447.method_10436(class_2246.field_10087, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8446)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_9991, 16)
			.method_10434('#', class_2246.field_10087)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_9991, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8446)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_white_dye", this.method_10426(class_1802.field_8446))
			.method_10438(consumer, "white_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10611, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8446)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8406)
			.method_10434('#', class_1802.field_8600)
			.method_10433('X', class_3489.field_15537)
			.method_10439("XX")
			.method_10439("X#")
			.method_10439(" #")
			.method_10429("has_stick", this.method_10426(class_1802.field_8600))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10149, 3)
			.method_10434('#', class_2246.field_10161)
			.method_10439("##")
			.method_10439("##")
			.method_10439("##")
			.method_10435("wooden_door")
			.method_10429("has_planks", this.method_10426(class_2246.field_10161))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8167)
			.method_10434('#', class_1802.field_8600)
			.method_10433('X', class_3489.field_15537)
			.method_10439("XX")
			.method_10439(" #")
			.method_10439(" #")
			.method_10429("has_stick", this.method_10426(class_1802.field_8600))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8647)
			.method_10434('#', class_1802.field_8600)
			.method_10433('X', class_3489.field_15537)
			.method_10439("XXX")
			.method_10439(" # ")
			.method_10439(" # ")
			.method_10429("has_stick", this.method_10426(class_1802.field_8600))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8876)
			.method_10434('#', class_1802.field_8600)
			.method_10433('X', class_3489.field_15537)
			.method_10439("X")
			.method_10439("#")
			.method_10439("#")
			.method_10429("has_stick", this.method_10426(class_1802.field_8600))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8091)
			.method_10434('#', class_1802.field_8600)
			.method_10433('X', class_3489.field_15537)
			.method_10439("X")
			.method_10439("X")
			.method_10439("#")
			.method_10429("has_stick", this.method_10426(class_1802.field_8600))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8674)
			.method_10454(class_1802.field_8529)
			.method_10454(class_1802.field_8794)
			.method_10454(class_1802.field_8153)
			.method_10442("has_book", this.method_10426(class_1802.field_8529))
			.method_10444(consumer);
		class_2447.method_10437(class_1802.field_8049)
			.method_10434('#', class_2246.field_10490)
			.method_10434('|', class_1802.field_8600)
			.method_10439("###")
			.method_10439("###")
			.method_10439(" | ")
			.method_10435("banner")
			.method_10429("has_yellow_wool", this.method_10426(class_2246.field_10490))
			.method_10431(consumer);
		class_2447.method_10437(class_1802.field_8863)
			.method_10434('#', class_2246.field_10490)
			.method_10433('X', class_3489.field_15537)
			.method_10439("###")
			.method_10439("XXX")
			.method_10435("bed")
			.method_10429("has_yellow_wool", this.method_10426(class_2246.field_10490))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8863)
			.method_10454(class_1802.field_8258)
			.method_10454(class_1802.field_8192)
			.method_10452("dyed_bed")
			.method_10442("has_bed", this.method_10426(class_1802.field_8258))
			.method_10450(consumer, "yellow_bed_from_white_bed");
		class_2447.method_10436(class_2246.field_10512, 3)
			.method_10434('#', class_2246.field_10490)
			.method_10439("##")
			.method_10435("carpet")
			.method_10429("has_yellow_wool", this.method_10426(class_2246.field_10490))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10512, 8)
			.method_10434('#', class_2246.field_10466)
			.method_10434('$', class_1802.field_8192)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("carpet")
			.method_10429("has_white_carpet", this.method_10426(class_2246.field_10466))
			.method_10429("has_yellow_dye", this.method_10426(class_1802.field_8192))
			.method_10438(consumer, "yellow_carpet_from_white_carpet");
		class_2450.method_10448(class_2246.field_10145, 8)
			.method_10454(class_1802.field_8192)
			.method_10449(class_2246.field_10102, 4)
			.method_10449(class_2246.field_10255, 4)
			.method_10452("concrete_powder")
			.method_10442("has_sand", this.method_10426(class_2246.field_10102))
			.method_10442("has_gravel", this.method_10426(class_2246.field_10255))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8192)
			.method_10454(class_2246.field_10182)
			.method_10452("yellow_dye")
			.method_10442("has_yellow_flower", this.method_10426(class_2246.field_10182))
			.method_10450(consumer, "yellow_dye_from_dandelion");
		class_2450.method_10448(class_1802.field_8192, 2)
			.method_10454(class_2246.field_10583)
			.method_10452("yellow_dye")
			.method_10442("has_double_plant", this.method_10426(class_2246.field_10583))
			.method_10450(consumer, "yellow_dye_from_sunflower");
		class_2447.method_10436(class_2246.field_10049, 8)
			.method_10434('#', class_2246.field_10033)
			.method_10434('X', class_1802.field_8192)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_glass")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10578, 16)
			.method_10434('#', class_2246.field_10049)
			.method_10439("###")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass", this.method_10426(class_2246.field_10033))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10578, 8)
			.method_10434('#', class_2246.field_10285)
			.method_10434('$', class_1802.field_8192)
			.method_10439("###")
			.method_10439("#$#")
			.method_10439("###")
			.method_10435("stained_glass_pane")
			.method_10429("has_glass_pane", this.method_10426(class_2246.field_10285))
			.method_10429("has_yellow_dye", this.method_10426(class_1802.field_8192))
			.method_10438(consumer, "yellow_stained_glass_pane_from_glass_pane");
		class_2447.method_10436(class_2246.field_10143, 8)
			.method_10434('#', class_2246.field_10415)
			.method_10434('X', class_1802.field_8192)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10435("stained_terracotta")
			.method_10429("has_terracotta", this.method_10426(class_2246.field_10415))
			.method_10431(consumer);
		class_2450.method_10447(class_2246.field_10490)
			.method_10454(class_1802.field_8192)
			.method_10454(class_2246.field_10446)
			.method_10452("wool")
			.method_10442("has_white_wool", this.method_10426(class_2246.field_10446))
			.method_10444(consumer);
		class_2450.method_10448(class_1802.field_8551, 9)
			.method_10454(class_2246.field_10342)
			.method_10442("has_at_least_9_dried_kelp", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8551))
			.method_10442("has_dried_kelp_block", this.method_10426(class_2246.field_10342))
			.method_10444(consumer);
		class_2450.method_10447(class_2246.field_10342)
			.method_10449(class_1802.field_8551, 9)
			.method_10442("has_at_least_9_dried_kelp", this.method_10424(class_2096.class_2100.method_9053(9), class_1802.field_8551))
			.method_10442("has_dried_kelp_block", this.method_10426(class_2246.field_10342))
			.method_10444(consumer);
		class_2447.method_10437(class_2246.field_10502)
			.method_10434('#', class_1802.field_8864)
			.method_10434('X', class_1802.field_8207)
			.method_10439("###")
			.method_10439("#X#")
			.method_10439("###")
			.method_10429("has_nautilus_core", this.method_10426(class_1802.field_8207))
			.method_10429("has_nautilus_shell", this.method_10426(class_1802.field_8864))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10435, 4)
			.method_10434('#', class_2246.field_10289)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_polished_granite", this.method_10426(class_2246.field_10289))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10039, 4)
			.method_10434('#', class_2246.field_10483)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_smooth_red_sandstone", this.method_10426(class_2246.field_10483))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10173, 4)
			.method_10434('#', class_2246.field_10065)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_mossy_stone_bricks", this.method_10426(class_2246.field_10065))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10310, 4)
			.method_10434('#', class_2246.field_10346)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_polished_diorite", this.method_10426(class_2246.field_10346))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10207, 4)
			.method_10434('#', class_2246.field_9989)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_mossy_cobblestone", this.method_10426(class_2246.field_9989))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10012, 4)
			.method_10434('#', class_2246.field_10462)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_end_stone_bricks", this.method_10426(class_2246.field_10462))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10440, 4)
			.method_10434('#', class_2246.field_10340)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_stone", this.method_10426(class_2246.field_10340))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10549, 4)
			.method_10434('#', class_2246.field_10467)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_smooth_sandstone", this.method_10426(class_2246.field_10467))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10245, 4)
			.method_10434('#', class_2246.field_9978)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_smooth_quartz", this.method_10426(class_2246.field_9978))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10607, 4)
			.method_10434('#', class_2246.field_10474)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_granite", this.method_10426(class_2246.field_10474))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10386, 4)
			.method_10434('#', class_2246.field_10115)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_andesite", this.method_10426(class_2246.field_10115))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10497, 4)
			.method_10434('#', class_2246.field_9986)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_red_nether_bricks", this.method_10426(class_2246.field_9986))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_9994, 4)
			.method_10434('#', class_2246.field_10093)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_polished_andesite", this.method_10426(class_2246.field_10093))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10216, 4)
			.method_10434('#', class_2246.field_10508)
			.method_10439("#  ")
			.method_10439("## ")
			.method_10439("###")
			.method_10429("has_diorite", this.method_10426(class_2246.field_10508))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10329, 6)
			.method_10434('#', class_2246.field_10289)
			.method_10439("###")
			.method_10429("has_polished_granite", this.method_10426(class_2246.field_10289))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10283, 6)
			.method_10434('#', class_2246.field_10483)
			.method_10439("###")
			.method_10429("has_smooth_red_sandstone", this.method_10426(class_2246.field_10483))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10024, 6)
			.method_10434('#', class_2246.field_10065)
			.method_10439("###")
			.method_10429("has_mossy_stone_bricks", this.method_10426(class_2246.field_10065))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10412, 6)
			.method_10434('#', class_2246.field_10346)
			.method_10439("###")
			.method_10429("has_polished_diorite", this.method_10426(class_2246.field_10346))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10405, 6)
			.method_10434('#', class_2246.field_9989)
			.method_10439("###")
			.method_10429("has_mossy_cobblestone", this.method_10426(class_2246.field_9989))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10064, 6)
			.method_10434('#', class_2246.field_10462)
			.method_10439("###")
			.method_10429("has_end_stone_bricks", this.method_10426(class_2246.field_10462))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10262, 6)
			.method_10434('#', class_2246.field_10467)
			.method_10439("###")
			.method_10429("has_smooth_sandstone", this.method_10426(class_2246.field_10467))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10601, 6)
			.method_10434('#', class_2246.field_9978)
			.method_10439("###")
			.method_10429("has_smooth_quartz", this.method_10426(class_2246.field_9978))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10189, 6)
			.method_10434('#', class_2246.field_10474)
			.method_10439("###")
			.method_10429("has_granite", this.method_10426(class_2246.field_10474))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10016, 6)
			.method_10434('#', class_2246.field_10115)
			.method_10439("###")
			.method_10429("has_andesite", this.method_10426(class_2246.field_10115))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10478, 6)
			.method_10434('#', class_2246.field_9986)
			.method_10439("###")
			.method_10429("has_red_nether_bricks", this.method_10426(class_2246.field_9986))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10322, 6)
			.method_10434('#', class_2246.field_10093)
			.method_10439("###")
			.method_10429("has_polished_andesite", this.method_10426(class_2246.field_10093))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10507, 6)
			.method_10434('#', class_2246.field_10508)
			.method_10439("###")
			.method_10429("has_diorite", this.method_10426(class_2246.field_10508))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10269, 6)
			.method_10434('#', class_2246.field_10104)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_bricks", this.method_10426(class_2246.field_10104))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10530, 6)
			.method_10434('#', class_2246.field_10135)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_prismarine", this.method_10426(class_2246.field_10135))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10413, 6)
			.method_10434('#', class_2246.field_10344)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_red_sandstone", this.method_10426(class_2246.field_10344))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10059, 6)
			.method_10434('#', class_2246.field_10065)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_mossy_stone_bricks", this.method_10426(class_2246.field_10065))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10072, 6)
			.method_10434('#', class_2246.field_10474)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_granite", this.method_10426(class_2246.field_10474))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10252, 6)
			.method_10434('#', class_2246.field_10056)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_stone_bricks", this.method_10426(class_2246.field_10056))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10127, 6)
			.method_10434('#', class_2246.field_10266)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_nether_bricks", this.method_10426(class_2246.field_10266))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10489, 6)
			.method_10434('#', class_2246.field_10115)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_andesite", this.method_10426(class_2246.field_10115))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10311, 6)
			.method_10434('#', class_2246.field_9986)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_red_nether_bricks", this.method_10426(class_2246.field_9986))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10630, 6)
			.method_10434('#', class_2246.field_9979)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_sandstone", this.method_10426(class_2246.field_9979))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10001, 6)
			.method_10434('#', class_2246.field_10462)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_end_stone_bricks", this.method_10426(class_2246.field_10462))
			.method_10431(consumer);
		class_2447.method_10436(class_2246.field_10517, 6)
			.method_10434('#', class_2246.field_10508)
			.method_10439("###")
			.method_10439("###")
			.method_10429("has_diorite", this.method_10426(class_2246.field_10508))
			.method_10431(consumer);
		class_2450.method_10447(class_1802.field_8573)
			.method_10454(class_1802.field_8407)
			.method_10454(class_1802.field_8681)
			.method_10442("has_creeper_head", this.method_10426(class_1802.field_8681))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8891)
			.method_10454(class_1802.field_8407)
			.method_10454(class_1802.field_8791)
			.method_10442("has_wither_skeleton_skull", this.method_10426(class_1802.field_8791))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8498)
			.method_10454(class_1802.field_8407)
			.method_10454(class_2246.field_10554)
			.method_10442("has_oxeye_daisy", this.method_10426(class_2246.field_10554))
			.method_10444(consumer);
		class_2450.method_10447(class_1802.field_8159)
			.method_10454(class_1802.field_8407)
			.method_10454(class_1802.field_8367)
			.method_10442("has_enchanted_golden_apple", this.method_10426(class_1802.field_8367))
			.method_10444(consumer);
		class_2447.method_10436(class_2246.field_16492, 6)
			.method_10434('~', class_1802.field_8276)
			.method_10434('I', class_2246.field_10211)
			.method_10439("I~I")
			.method_10439("I I")
			.method_10439("I I")
			.method_10429("has_bamboo", this.method_10426(class_2246.field_10211))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_16337)
			.method_10434('I', class_1802.field_8600)
			.method_10434('-', class_2246.field_10454)
			.method_10433('#', class_3489.field_15537)
			.method_10439("I-I")
			.method_10439("# #")
			.method_10429("has_stone_slab", this.method_10426(class_2246.field_10454))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_16333)
			.method_10434('#', class_2246.field_10360)
			.method_10434('X', class_2246.field_10181)
			.method_10434('I', class_1802.field_8620)
			.method_10439("III")
			.method_10439("IXI")
			.method_10439("###")
			.method_10429("has_smooth_stone", this.method_10426(class_2246.field_10360))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_16334)
			.method_10433('#', class_3489.field_15539)
			.method_10434('X', class_2246.field_10181)
			.method_10439(" # ")
			.method_10439("#X#")
			.method_10439(" # ")
			.method_10429("has_furnace", this.method_10426(class_2246.field_10181))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_16336)
			.method_10433('#', class_3489.field_15537)
			.method_10434('@', class_1802.field_8407)
			.method_10439("@@")
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_string", this.method_10426(class_1802.field_8276))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_16329)
			.method_10433('#', class_3489.field_15537)
			.method_10434('@', class_1802.field_8620)
			.method_10439("@@")
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_iron_ingot", this.method_10426(class_1802.field_8620))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_16331)
			.method_10433('#', class_3489.field_15537)
			.method_10434('@', class_1802.field_8145)
			.method_10439("@@")
			.method_10439("##")
			.method_10439("##")
			.method_10429("has_flint", this.method_10426(class_1802.field_8145))
			.method_10431(consumer);
		class_2447.method_10437(class_2246.field_16335)
			.method_10434('I', class_1802.field_8620)
			.method_10434('#', class_2246.field_10340)
			.method_10439(" I ")
			.method_10439("###")
			.method_10429("has_stone", this.method_10426(class_2246.field_10340))
			.method_10431(consumer);
		class_2456.method_10476(class_1865.field_9028).method_10475(consumer, "armor_dye");
		class_2456.method_10476(class_1865.field_9038).method_10475(consumer, "banner_duplicate");
		class_2456.method_10476(class_1865.field_9029).method_10475(consumer, "book_cloning");
		class_2456.method_10476(class_1865.field_9043).method_10475(consumer, "firework_rocket");
		class_2456.method_10476(class_1865.field_9036).method_10475(consumer, "firework_star");
		class_2456.method_10476(class_1865.field_9034).method_10475(consumer, "firework_star_fade");
		class_2456.method_10476(class_1865.field_9044).method_10475(consumer, "map_cloning");
		class_2456.method_10476(class_1865.field_9039).method_10475(consumer, "map_extending");
		class_2456.method_10476(class_1865.field_19421).method_10475(consumer, "repair_item");
		class_2456.method_10476(class_1865.field_9040).method_10475(consumer, "shield_decoration");
		class_2456.method_10476(class_1865.field_9041).method_10475(consumer, "shulker_box_coloring");
		class_2456.method_10476(class_1865.field_9037).method_10475(consumer, "tipped_arrow");
		class_2456.method_10476(class_1865.field_9030).method_10475(consumer, "suspicious_stew");
		class_2454.method_17802(class_1856.method_8091(class_1802.field_8567), class_1802.field_8512, 0.35F, 200)
			.method_10469("has_potato", this.method_10426(class_1802.field_8567))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_1802.field_8696), class_1802.field_8621, 0.3F, 200)
			.method_10469("has_clay_ball", this.method_10426(class_1802.field_8696))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8106(class_3489.field_15539), class_1802.field_8665, 0.15F, 200)
			.method_10469("has_log", this.method_10420(class_3489.field_15539))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_1802.field_8233), class_1802.field_8882, 0.1F, 200)
			.method_10469("has_chorus_fruit", this.method_10426(class_1802.field_8233))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10418.method_8389()), class_1802.field_8713, 0.1F, 200)
			.method_10469("has_coal_ore", this.method_10426(class_2246.field_10418))
			.method_10472(consumer, "coal_from_smelting");
		class_2454.method_17802(class_1856.method_8091(class_1802.field_8046), class_1802.field_8176, 0.35F, 200)
			.method_10469("has_beef", this.method_10426(class_1802.field_8046))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_1802.field_8726), class_1802.field_8544, 0.35F, 200)
			.method_10469("has_chicken", this.method_10426(class_1802.field_8726))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_1802.field_8429), class_1802.field_8373, 0.35F, 200)
			.method_10469("has_cod", this.method_10426(class_1802.field_8429))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_9993), class_1802.field_8551, 0.1F, 200)
			.method_10469("has_kelp", this.method_10426(class_2246.field_9993))
			.method_10472(consumer, "dried_kelp_from_smelting");
		class_2454.method_17802(class_1856.method_8091(class_1802.field_8209), class_1802.field_8509, 0.35F, 200)
			.method_10469("has_salmon", this.method_10426(class_1802.field_8209))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_1802.field_8748), class_1802.field_8347, 0.35F, 200)
			.method_10469("has_mutton", this.method_10426(class_1802.field_8748))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_1802.field_8389), class_1802.field_8261, 0.35F, 200)
			.method_10469("has_porkchop", this.method_10426(class_1802.field_8389))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_1802.field_8504), class_1802.field_8752, 0.35F, 200)
			.method_10469("has_rabbit", this.method_10426(class_1802.field_8504))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10442.method_8389()), class_1802.field_8477, 1.0F, 200)
			.method_10469("has_diamond_ore", this.method_10426(class_2246.field_10442))
			.method_10472(consumer, "diamond_from_smelting");
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10090.method_8389()), class_1802.field_8759, 0.2F, 200)
			.method_10469("has_lapis_ore", this.method_10426(class_2246.field_10090))
			.method_10472(consumer, "lapis_from_smelting");
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10013.method_8389()), class_1802.field_8687, 1.0F, 200)
			.method_10469("has_emerald_ore", this.method_10426(class_2246.field_10013))
			.method_10472(consumer, "emerald_from_smelting");
		class_2454.method_17802(class_1856.method_8106(class_3489.field_15532), class_2246.field_10033.method_8389(), 0.1F, 200)
			.method_10469("has_sand", this.method_10420(class_3489.field_15532))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10571.method_8389()), class_1802.field_8695, 1.0F, 200)
			.method_10469("has_gold_ore", this.method_10426(class_2246.field_10571))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10476.method_8389()), class_1802.field_8131, 0.1F, 200)
			.method_10469("has_sea_pickle", this.method_10426(class_2246.field_10476))
			.method_10472(consumer, "lime_dye_from_smelting");
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10029.method_8389()), class_1802.field_8408, 1.0F, 200)
			.method_10469("has_cactus", this.method_10426(class_2246.field_10029))
			.method_10470(consumer);
		class_2454.method_17802(
				class_1856.method_8091(
					class_1802.field_8335,
					class_1802.field_8322,
					class_1802.field_8825,
					class_1802.field_8303,
					class_1802.field_8845,
					class_1802.field_8862,
					class_1802.field_8678,
					class_1802.field_8416,
					class_1802.field_8753,
					class_1802.field_8560
				),
				class_1802.field_8397,
				0.1F,
				200
			)
			.method_10469("has_golden_pickaxe", this.method_10426(class_1802.field_8335))
			.method_10469("has_golden_shovel", this.method_10426(class_1802.field_8322))
			.method_10469("has_golden_axe", this.method_10426(class_1802.field_8825))
			.method_10469("has_golden_hoe", this.method_10426(class_1802.field_8303))
			.method_10469("has_golden_sword", this.method_10426(class_1802.field_8845))
			.method_10469("has_golden_helmet", this.method_10426(class_1802.field_8862))
			.method_10469("has_golden_chestplate", this.method_10426(class_1802.field_8678))
			.method_10469("has_golden_leggings", this.method_10426(class_1802.field_8416))
			.method_10469("has_golden_boots", this.method_10426(class_1802.field_8753))
			.method_10469("has_golden_horse_armor", this.method_10426(class_1802.field_8560))
			.method_10472(consumer, "gold_nugget_from_smelting");
		class_2454.method_17802(
				class_1856.method_8091(
					class_1802.field_8403,
					class_1802.field_8699,
					class_1802.field_8475,
					class_1802.field_8609,
					class_1802.field_8371,
					class_1802.field_8743,
					class_1802.field_8523,
					class_1802.field_8396,
					class_1802.field_8660,
					class_1802.field_8578,
					class_1802.field_8283,
					class_1802.field_8873,
					class_1802.field_8218,
					class_1802.field_8313
				),
				class_1802.field_8675,
				0.1F,
				200
			)
			.method_10469("has_iron_pickaxe", this.method_10426(class_1802.field_8403))
			.method_10469("has_iron_shovel", this.method_10426(class_1802.field_8699))
			.method_10469("has_iron_axe", this.method_10426(class_1802.field_8475))
			.method_10469("has_iron_hoe", this.method_10426(class_1802.field_8609))
			.method_10469("has_iron_sword", this.method_10426(class_1802.field_8371))
			.method_10469("has_iron_helmet", this.method_10426(class_1802.field_8743))
			.method_10469("has_iron_chestplate", this.method_10426(class_1802.field_8523))
			.method_10469("has_iron_leggings", this.method_10426(class_1802.field_8396))
			.method_10469("has_iron_boots", this.method_10426(class_1802.field_8660))
			.method_10469("has_iron_horse_armor", this.method_10426(class_1802.field_8578))
			.method_10469("has_chainmail_helmet", this.method_10426(class_1802.field_8283))
			.method_10469("has_chainmail_chestplate", this.method_10426(class_1802.field_8873))
			.method_10469("has_chainmail_leggings", this.method_10426(class_1802.field_8218))
			.method_10469("has_chainmail_boots", this.method_10426(class_1802.field_8313))
			.method_10472(consumer, "iron_nugget_from_smelting");
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10212.method_8389()), class_1802.field_8620, 0.7F, 200)
			.method_10469("has_iron_ore", this.method_10426(class_2246.field_10212.method_8389()))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10460), class_2246.field_10415.method_8389(), 0.35F, 200)
			.method_10469("has_clay_block", this.method_10426(class_2246.field_10460))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10515), class_1802.field_8729, 0.1F, 200)
			.method_10469("has_netherrack", this.method_10426(class_2246.field_10515))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10213), class_1802.field_8155, 0.2F, 200)
			.method_10469("has_nether_quartz_ore", this.method_10426(class_2246.field_10213))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10080), class_1802.field_8725, 0.7F, 200)
			.method_10469("has_redstone_ore", this.method_10426(class_2246.field_10080))
			.method_10472(consumer, "redstone_from_smelting");
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10562), class_2246.field_10258.method_8389(), 0.15F, 200)
			.method_10469("has_wet_sponge", this.method_10426(class_2246.field_10562))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10445), class_2246.field_10340.method_8389(), 0.1F, 200)
			.method_10469("has_cobblestone", this.method_10426(class_2246.field_10445))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10340), class_2246.field_10360.method_8389(), 0.1F, 200)
			.method_10469("has_stone", this.method_10426(class_2246.field_10340))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_9979), class_2246.field_10467.method_8389(), 0.1F, 200)
			.method_10469("has_sandstone", this.method_10426(class_2246.field_9979))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10344), class_2246.field_10483.method_8389(), 0.1F, 200)
			.method_10469("has_red_sandstone", this.method_10426(class_2246.field_10344))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10153), class_2246.field_9978.method_8389(), 0.1F, 200)
			.method_10469("has_quartz_block", this.method_10426(class_2246.field_10153))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10056), class_2246.field_10416.method_8389(), 0.1F, 200)
			.method_10469("has_stone_bricks", this.method_10426(class_2246.field_10056))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10626), class_2246.field_10501.method_8389(), 0.1F, 200)
			.method_10469("has_black_terracotta", this.method_10426(class_2246.field_10626))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10409), class_2246.field_10550.method_8389(), 0.1F, 200)
			.method_10469("has_blue_terracotta", this.method_10426(class_2246.field_10409))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10123), class_2246.field_10004.method_8389(), 0.1F, 200)
			.method_10469("has_brown_terracotta", this.method_10426(class_2246.field_10123))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10235), class_2246.field_10078.method_8389(), 0.1F, 200)
			.method_10469("has_cyan_terracotta", this.method_10426(class_2246.field_10235))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10349), class_2246.field_10220.method_8389(), 0.1F, 200)
			.method_10469("has_gray_terracotta", this.method_10426(class_2246.field_10349))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10526), class_2246.field_10475.method_8389(), 0.1F, 200)
			.method_10469("has_green_terracotta", this.method_10426(class_2246.field_10526))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10325), class_2246.field_10345.method_8389(), 0.1F, 200)
			.method_10469("has_light_blue_terracotta", this.method_10426(class_2246.field_10325))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10590), class_2246.field_10052.method_8389(), 0.1F, 200)
			.method_10469("has_light_gray_terracotta", this.method_10426(class_2246.field_10590))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10014), class_2246.field_10046.method_8389(), 0.1F, 200)
			.method_10469("has_lime_terracotta", this.method_10426(class_2246.field_10014))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10015), class_2246.field_10538.method_8389(), 0.1F, 200)
			.method_10469("has_magenta_terracotta", this.method_10426(class_2246.field_10015))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10184), class_2246.field_10280.method_8389(), 0.1F, 200)
			.method_10469("has_orange_terracotta", this.method_10426(class_2246.field_10184))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10444), class_2246.field_10567.method_8389(), 0.1F, 200)
			.method_10469("has_pink_terracotta", this.method_10426(class_2246.field_10444))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10570), class_2246.field_10426.method_8389(), 0.1F, 200)
			.method_10469("has_purple_terracotta", this.method_10426(class_2246.field_10570))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10328), class_2246.field_10383.method_8389(), 0.1F, 200)
			.method_10469("has_red_terracotta", this.method_10426(class_2246.field_10328))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10611), class_2246.field_10595.method_8389(), 0.1F, 200)
			.method_10469("has_white_terracotta", this.method_10426(class_2246.field_10611))
			.method_10470(consumer);
		class_2454.method_17802(class_1856.method_8091(class_2246.field_10143), class_2246.field_10096.method_8389(), 0.1F, 200)
			.method_10469("has_yellow_terracotta", this.method_10426(class_2246.field_10143))
			.method_10470(consumer);
		class_2454.method_10473(class_1856.method_8091(class_2246.field_10212.method_8389()), class_1802.field_8620, 0.7F, 100)
			.method_10469("has_iron_ore", this.method_10426(class_2246.field_10212.method_8389()))
			.method_10472(consumer, "iron_ingot_from_blasting");
		class_2454.method_10473(class_1856.method_8091(class_2246.field_10571.method_8389()), class_1802.field_8695, 1.0F, 100)
			.method_10469("has_gold_ore", this.method_10426(class_2246.field_10571))
			.method_10472(consumer, "gold_ingot_from_blasting");
		class_2454.method_10473(class_1856.method_8091(class_2246.field_10442.method_8389()), class_1802.field_8477, 1.0F, 100)
			.method_10469("has_diamond_ore", this.method_10426(class_2246.field_10442))
			.method_10472(consumer, "diamond_from_blasting");
		class_2454.method_10473(class_1856.method_8091(class_2246.field_10090.method_8389()), class_1802.field_8759, 0.2F, 100)
			.method_10469("has_lapis_ore", this.method_10426(class_2246.field_10090))
			.method_10472(consumer, "lapis_from_blasting");
		class_2454.method_10473(class_1856.method_8091(class_2246.field_10080), class_1802.field_8725, 0.7F, 100)
			.method_10469("has_redstone_ore", this.method_10426(class_2246.field_10080))
			.method_10472(consumer, "redstone_from_blasting");
		class_2454.method_10473(class_1856.method_8091(class_2246.field_10418.method_8389()), class_1802.field_8713, 0.1F, 100)
			.method_10469("has_coal_ore", this.method_10426(class_2246.field_10418))
			.method_10472(consumer, "coal_from_blasting");
		class_2454.method_10473(class_1856.method_8091(class_2246.field_10013.method_8389()), class_1802.field_8687, 1.0F, 100)
			.method_10469("has_emerald_ore", this.method_10426(class_2246.field_10013))
			.method_10472(consumer, "emerald_from_blasting");
		class_2454.method_10473(class_1856.method_8091(class_2246.field_10213), class_1802.field_8155, 0.2F, 100)
			.method_10469("has_nether_quartz_ore", this.method_10426(class_2246.field_10213))
			.method_10472(consumer, "quartz_from_blasting");
		class_2454.method_10473(
				class_1856.method_8091(
					class_1802.field_8335,
					class_1802.field_8322,
					class_1802.field_8825,
					class_1802.field_8303,
					class_1802.field_8845,
					class_1802.field_8862,
					class_1802.field_8678,
					class_1802.field_8416,
					class_1802.field_8753,
					class_1802.field_8560
				),
				class_1802.field_8397,
				0.1F,
				100
			)
			.method_10469("has_golden_pickaxe", this.method_10426(class_1802.field_8335))
			.method_10469("has_golden_shovel", this.method_10426(class_1802.field_8322))
			.method_10469("has_golden_axe", this.method_10426(class_1802.field_8825))
			.method_10469("has_golden_hoe", this.method_10426(class_1802.field_8303))
			.method_10469("has_golden_sword", this.method_10426(class_1802.field_8845))
			.method_10469("has_golden_helmet", this.method_10426(class_1802.field_8862))
			.method_10469("has_golden_chestplate", this.method_10426(class_1802.field_8678))
			.method_10469("has_golden_leggings", this.method_10426(class_1802.field_8416))
			.method_10469("has_golden_boots", this.method_10426(class_1802.field_8753))
			.method_10469("has_golden_horse_armor", this.method_10426(class_1802.field_8560))
			.method_10472(consumer, "gold_nugget_from_blasting");
		class_2454.method_10473(
				class_1856.method_8091(
					class_1802.field_8403,
					class_1802.field_8699,
					class_1802.field_8475,
					class_1802.field_8609,
					class_1802.field_8371,
					class_1802.field_8743,
					class_1802.field_8523,
					class_1802.field_8396,
					class_1802.field_8660,
					class_1802.field_8578,
					class_1802.field_8283,
					class_1802.field_8873,
					class_1802.field_8218,
					class_1802.field_8313
				),
				class_1802.field_8675,
				0.1F,
				100
			)
			.method_10469("has_iron_pickaxe", this.method_10426(class_1802.field_8403))
			.method_10469("has_iron_shovel", this.method_10426(class_1802.field_8699))
			.method_10469("has_iron_axe", this.method_10426(class_1802.field_8475))
			.method_10469("has_iron_hoe", this.method_10426(class_1802.field_8609))
			.method_10469("has_iron_sword", this.method_10426(class_1802.field_8371))
			.method_10469("has_iron_helmet", this.method_10426(class_1802.field_8743))
			.method_10469("has_iron_chestplate", this.method_10426(class_1802.field_8523))
			.method_10469("has_iron_leggings", this.method_10426(class_1802.field_8396))
			.method_10469("has_iron_boots", this.method_10426(class_1802.field_8660))
			.method_10469("has_iron_horse_armor", this.method_10426(class_1802.field_8578))
			.method_10469("has_chainmail_helmet", this.method_10426(class_1802.field_8283))
			.method_10469("has_chainmail_chestplate", this.method_10426(class_1802.field_8873))
			.method_10469("has_chainmail_leggings", this.method_10426(class_1802.field_8218))
			.method_10469("has_chainmail_boots", this.method_10426(class_1802.field_8313))
			.method_10472(consumer, "iron_nugget_from_blasting");
		this.method_17585(consumer, "smoking", class_1865.field_17085, 100);
		this.method_17585(consumer, "campfire_cooking", class_1865.field_17347, 600);
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10340), class_2246.field_10454, 2)
			.method_17970("has_stone", this.method_10426(class_2246.field_10340))
			.method_17971(consumer, "stone_slab_from_stone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10340), class_2246.field_10440)
			.method_17970("has_stone", this.method_10426(class_2246.field_10340))
			.method_17971(consumer, "stone_stairs_from_stone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10340), class_2246.field_10056)
			.method_17970("has_stone", this.method_10426(class_2246.field_10340))
			.method_17971(consumer, "stone_bricks_from_stone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10340), class_2246.field_10131, 2)
			.method_17970("has_stone", this.method_10426(class_2246.field_10340))
			.method_17971(consumer, "stone_brick_slab_from_stone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10340), class_2246.field_10392)
			.method_17970("has_stone", this.method_10426(class_2246.field_10340))
			.method_17971(consumer, "stone_brick_stairs_from_stone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10340), class_2246.field_10552)
			.method_17970("has_stone", this.method_10426(class_2246.field_10340))
			.method_17971(consumer, "chiseled_stone_bricks_stone_from_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10340), class_2246.field_10252)
			.method_17970("has_stone", this.method_10426(class_2246.field_10340))
			.method_17971(consumer, "stone_brick_walls_from_stone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_9979), class_2246.field_10361)
			.method_17970("has_sandstone", this.method_10426(class_2246.field_9979))
			.method_17971(consumer, "cut_sandstone_from_sandstone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_9979), class_2246.field_10007, 2)
			.method_17970("has_sandstone", this.method_10426(class_2246.field_9979))
			.method_17971(consumer, "sandstone_slab_from_sandstone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_9979), class_2246.field_18890, 2)
			.method_17970("has_sandstone", this.method_10426(class_2246.field_9979))
			.method_17971(consumer, "cut_sandstone_slab_from_sandstone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10361), class_2246.field_18890, 2)
			.method_17970("has_cut_sandstone", this.method_10426(class_2246.field_9979))
			.method_17971(consumer, "cut_sandstone_slab_from_cut_sandstone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_9979), class_2246.field_10142)
			.method_17970("has_sandstone", this.method_10426(class_2246.field_9979))
			.method_17971(consumer, "sandstone_stairs_from_sandstone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_9979), class_2246.field_10630)
			.method_17970("has_sandstone", this.method_10426(class_2246.field_9979))
			.method_17971(consumer, "sandstone_wall_from_sandstone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_9979), class_2246.field_10292)
			.method_17970("has_sandstone", this.method_10426(class_2246.field_9979))
			.method_17971(consumer, "chiseled_sandstone_from_sandstone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10344), class_2246.field_10518)
			.method_17970("has_red_sandstone", this.method_10426(class_2246.field_10344))
			.method_17971(consumer, "cut_red_sandstone_from_red_sandstone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10344), class_2246.field_10624, 2)
			.method_17970("has_red_sandstone", this.method_10426(class_2246.field_10344))
			.method_17971(consumer, "red_sandstone_slab_from_red_sandstone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10344), class_2246.field_18891, 2)
			.method_17970("has_red_sandstone", this.method_10426(class_2246.field_10344))
			.method_17971(consumer, "cut_red_sandstone_slab_from_red_sandstone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10518), class_2246.field_18891, 2)
			.method_17970("has_cut_red_sandstone", this.method_10426(class_2246.field_10344))
			.method_17971(consumer, "cut_red_sandstone_slab_from_cut_red_sandstone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10344), class_2246.field_10420)
			.method_17970("has_red_sandstone", this.method_10426(class_2246.field_10344))
			.method_17971(consumer, "red_sandstone_stairs_from_red_sandstone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10344), class_2246.field_10413)
			.method_17970("has_red_sandstone", this.method_10426(class_2246.field_10344))
			.method_17971(consumer, "red_sandstone_wall_from_red_sandstone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10344), class_2246.field_10117)
			.method_17970("has_red_sandstone", this.method_10426(class_2246.field_10344))
			.method_17971(consumer, "chiseled_red_sandstone_from_red_sandstone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10153), class_2246.field_10237, 2)
			.method_17970("has_quartz_block", this.method_10426(class_2246.field_10153))
			.method_17971(consumer, "quartz_slab_from_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10153), class_2246.field_10451)
			.method_17970("has_quartz_block", this.method_10426(class_2246.field_10153))
			.method_17971(consumer, "quartz_stairs_from_quartz_block_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10153), class_2246.field_10437)
			.method_17970("has_quartz_block", this.method_10426(class_2246.field_10153))
			.method_17971(consumer, "quartz_pillar_from_quartz_block_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10153), class_2246.field_10044)
			.method_17970("has_quartz_block", this.method_10426(class_2246.field_10153))
			.method_17971(consumer, "chiseled_quartz_block_from_quartz_block_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10445), class_2246.field_10596)
			.method_17970("has_cobblestone", this.method_10426(class_2246.field_10445))
			.method_17971(consumer, "cobblestone_stairs_from_cobblestone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10445), class_2246.field_10351, 2)
			.method_17970("has_cobblestone", this.method_10426(class_2246.field_10445))
			.method_17971(consumer, "cobblestone_slab_from_cobblestone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10445), class_2246.field_10625)
			.method_17970("has_cobblestone", this.method_10426(class_2246.field_10445))
			.method_17971(consumer, "cobblestone_wall_from_cobblestone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10056), class_2246.field_10131, 2)
			.method_17970("has_stone_bricks", this.method_10426(class_2246.field_10056))
			.method_17971(consumer, "stone_brick_slab_from_stone_bricks_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10056), class_2246.field_10392)
			.method_17970("has_stone_bricks", this.method_10426(class_2246.field_10056))
			.method_17971(consumer, "stone_brick_stairs_from_stone_bricks_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10056), class_2246.field_10252)
			.method_17970("has_stone_bricks", this.method_10426(class_2246.field_10056))
			.method_17971(consumer, "stone_brick_wall_from_stone_bricks_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10056), class_2246.field_10552)
			.method_17970("has_stone_bricks", this.method_10426(class_2246.field_10056))
			.method_17971(consumer, "chiseled_stone_bricks_from_stone_bricks_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10104), class_2246.field_10191, 2)
			.method_17970("has_bricks", this.method_10426(class_2246.field_10104))
			.method_17971(consumer, "brick_slab_from_bricks_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10104), class_2246.field_10089)
			.method_17970("has_bricks", this.method_10426(class_2246.field_10104))
			.method_17971(consumer, "brick_stairs_from_bricks_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10104), class_2246.field_10269)
			.method_17970("has_bricks", this.method_10426(class_2246.field_10104))
			.method_17971(consumer, "brick_wall_from_bricks_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10266), class_2246.field_10390, 2)
			.method_17970("has_nether_bricks", this.method_10426(class_2246.field_10266))
			.method_17971(consumer, "nether_brick_slab_from_nether_bricks_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10266), class_2246.field_10159)
			.method_17970("has_nether_bricks", this.method_10426(class_2246.field_10266))
			.method_17971(consumer, "nether_brick_stairs_from_nether_bricks_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10266), class_2246.field_10127)
			.method_17970("has_nether_bricks", this.method_10426(class_2246.field_10266))
			.method_17971(consumer, "nether_brick_wall_from_nether_bricks_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_9986), class_2246.field_10478, 2)
			.method_17970("has_nether_bricks", this.method_10426(class_2246.field_9986))
			.method_17971(consumer, "red_nether_brick_slab_from_red_nether_bricks_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_9986), class_2246.field_10497)
			.method_17970("has_nether_bricks", this.method_10426(class_2246.field_9986))
			.method_17971(consumer, "red_nether_brick_stairs_from_red_nether_bricks_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_9986), class_2246.field_10311)
			.method_17970("has_nether_bricks", this.method_10426(class_2246.field_9986))
			.method_17971(consumer, "red_nether_brick_wall_from_red_nether_bricks_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10286), class_2246.field_10175, 2)
			.method_17970("has_purpur_block", this.method_10426(class_2246.field_10286))
			.method_17971(consumer, "purpur_slab_from_purpur_block_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10286), class_2246.field_9992)
			.method_17970("has_purpur_block", this.method_10426(class_2246.field_10286))
			.method_17971(consumer, "purpur_stairs_from_purpur_block_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10286), class_2246.field_10505)
			.method_17970("has_purpur_block", this.method_10426(class_2246.field_10286))
			.method_17971(consumer, "purpur_pillar_from_purpur_block_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10135), class_2246.field_10389, 2)
			.method_17970("has_prismarine", this.method_10426(class_2246.field_10135))
			.method_17971(consumer, "prismarine_slab_from_prismarine_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10135), class_2246.field_10350)
			.method_17970("has_prismarine", this.method_10426(class_2246.field_10135))
			.method_17971(consumer, "prismarine_stairs_from_prismarine_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10135), class_2246.field_10530)
			.method_17970("has_prismarine", this.method_10426(class_2246.field_10135))
			.method_17971(consumer, "prismarine_wall_from_prismarine_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10006), class_2246.field_10236, 2)
			.method_17970("has_prismarine_brick", this.method_10426(class_2246.field_10006))
			.method_17971(consumer, "prismarine_brick_slab_from_prismarine_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10006), class_2246.field_10190)
			.method_17970("has_prismarine_brick", this.method_10426(class_2246.field_10006))
			.method_17971(consumer, "prismarine_brick_stairs_from_prismarine_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10297), class_2246.field_10623, 2)
			.method_17970("has_dark_prismarine", this.method_10426(class_2246.field_10297))
			.method_17971(consumer, "dark_prismarine_slab_from_dark_prismarine_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10297), class_2246.field_10130)
			.method_17970("has_dark_prismarine", this.method_10426(class_2246.field_10297))
			.method_17971(consumer, "dark_prismarine_stairs_from_dark_prismarine_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10115), class_2246.field_10016, 2)
			.method_17970("has_andesite", this.method_10426(class_2246.field_10115))
			.method_17971(consumer, "andesite_slab_from_andesite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10115), class_2246.field_10386)
			.method_17970("has_andesite", this.method_10426(class_2246.field_10115))
			.method_17971(consumer, "andesite_stairs_from_andesite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10115), class_2246.field_10489)
			.method_17970("has_andesite", this.method_10426(class_2246.field_10115))
			.method_17971(consumer, "andesite_wall_from_andesite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10115), class_2246.field_10093)
			.method_17970("has_andesite", this.method_10426(class_2246.field_10115))
			.method_17971(consumer, "polished_andesite_from_andesite_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10115), class_2246.field_10322, 2)
			.method_17970("has_andesite", this.method_10426(class_2246.field_10115))
			.method_17971(consumer, "polished_andesite_slab_from_andesite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10115), class_2246.field_9994)
			.method_17970("has_andesite", this.method_10426(class_2246.field_10115))
			.method_17971(consumer, "polished_andesite_stairs_from_andesite_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10093), class_2246.field_10322, 2)
			.method_17970("has_polished_andesite", this.method_10426(class_2246.field_10093))
			.method_17971(consumer, "polished_andesite_slab_from_polished_andesite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10093), class_2246.field_9994)
			.method_17970("has_polished_andesite", this.method_10426(class_2246.field_10093))
			.method_17971(consumer, "polished_andesite_stairs_from_polished_andesite_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10474), class_2246.field_10189, 2)
			.method_17970("has_granite", this.method_10426(class_2246.field_10474))
			.method_17971(consumer, "granite_slab_from_granite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10474), class_2246.field_10607)
			.method_17970("has_granite", this.method_10426(class_2246.field_10474))
			.method_17971(consumer, "granite_stairs_from_granite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10474), class_2246.field_10072)
			.method_17970("has_granite", this.method_10426(class_2246.field_10474))
			.method_17971(consumer, "granite_wall_from_granite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10474), class_2246.field_10289)
			.method_17970("has_granite", this.method_10426(class_2246.field_10474))
			.method_17971(consumer, "polished_granite_from_granite_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10474), class_2246.field_10329, 2)
			.method_17970("has_granite", this.method_10426(class_2246.field_10474))
			.method_17971(consumer, "polished_granite_slab_from_granite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10474), class_2246.field_10435)
			.method_17970("has_granite", this.method_10426(class_2246.field_10474))
			.method_17971(consumer, "polished_granite_stairs_from_granite_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10289), class_2246.field_10329, 2)
			.method_17970("has_polished_granite", this.method_10426(class_2246.field_10289))
			.method_17971(consumer, "polished_granite_slab_from_polished_granite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10289), class_2246.field_10435)
			.method_17970("has_polished_granite", this.method_10426(class_2246.field_10289))
			.method_17971(consumer, "polished_granite_stairs_from_polished_granite_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10508), class_2246.field_10507, 2)
			.method_17970("has_diorite", this.method_10426(class_2246.field_10508))
			.method_17971(consumer, "diorite_slab_from_diorite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10508), class_2246.field_10216)
			.method_17970("has_diorite", this.method_10426(class_2246.field_10508))
			.method_17971(consumer, "diorite_stairs_from_diorite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10508), class_2246.field_10517)
			.method_17970("has_diorite", this.method_10426(class_2246.field_10508))
			.method_17971(consumer, "diorite_wall_from_diorite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10508), class_2246.field_10346)
			.method_17970("has_diorite", this.method_10426(class_2246.field_10508))
			.method_17971(consumer, "polished_diorite_from_diorite_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10508), class_2246.field_10412, 2)
			.method_17970("has_diorite", this.method_10426(class_2246.field_10346))
			.method_17971(consumer, "polished_diorite_slab_from_diorite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10508), class_2246.field_10310)
			.method_17970("has_diorite", this.method_10426(class_2246.field_10346))
			.method_17971(consumer, "polished_diorite_stairs_from_diorite_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10346), class_2246.field_10412, 2)
			.method_17970("has_polished_diorite", this.method_10426(class_2246.field_10346))
			.method_17971(consumer, "polished_diorite_slab_from_polished_diorite_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10346), class_2246.field_10310)
			.method_17970("has_polished_diorite", this.method_10426(class_2246.field_10346))
			.method_17971(consumer, "polished_diorite_stairs_from_polished_diorite_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10065), class_2246.field_10024, 2)
			.method_17970("has_mossy_stone_bricks", this.method_10426(class_2246.field_10065))
			.method_17971(consumer, "mossy_stone_brick_slab_from_mossy_stone_brick_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10065), class_2246.field_10173)
			.method_17970("has_mossy_stone_bricks", this.method_10426(class_2246.field_10065))
			.method_17971(consumer, "mossy_stone_brick_stairs_from_mossy_stone_brick_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10065), class_2246.field_10059)
			.method_17970("has_mossy_stone_bricks", this.method_10426(class_2246.field_10065))
			.method_17971(consumer, "mossy_stone_brick_wall_from_mossy_stone_brick_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_9989), class_2246.field_10405, 2)
			.method_17970("has_mossy_cobblestone", this.method_10426(class_2246.field_9989))
			.method_17971(consumer, "mossy_cobblestone_slab_from_mossy_cobblestone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_9989), class_2246.field_10207)
			.method_17970("has_mossy_cobblestone", this.method_10426(class_2246.field_9989))
			.method_17971(consumer, "mossy_cobblestone_stairs_from_mossy_cobblestone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_9989), class_2246.field_9990)
			.method_17970("has_mossy_cobblestone", this.method_10426(class_2246.field_9989))
			.method_17971(consumer, "mossy_cobblestone_wall_from_mossy_cobblestone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10467), class_2246.field_10262, 2)
			.method_17970("has_smooth_sandstone", this.method_10426(class_2246.field_10467))
			.method_17971(consumer, "smooth_sandstone_slab_from_smooth_sandstone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10467), class_2246.field_10549)
			.method_17970("has_mossy_cobblestone", this.method_10426(class_2246.field_10467))
			.method_17971(consumer, "smooth_sandstone_stairs_from_smooth_sandstone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10483), class_2246.field_10283, 2)
			.method_17970("has_smooth_red_sandstone", this.method_10426(class_2246.field_10483))
			.method_17971(consumer, "smooth_red_sandstone_slab_from_smooth_red_sandstone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10483), class_2246.field_10039)
			.method_17970("has_smooth_red_sandstone", this.method_10426(class_2246.field_10483))
			.method_17971(consumer, "smooth_red_sandstone_stairs_from_smooth_red_sandstone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_9978), class_2246.field_10601, 2)
			.method_17970("has_smooth_quartz", this.method_10426(class_2246.field_9978))
			.method_17971(consumer, "smooth_quartz_slab_from_smooth_quartz_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_9978), class_2246.field_10245)
			.method_17970("has_smooth_quartz", this.method_10426(class_2246.field_9978))
			.method_17971(consumer, "smooth_quartz_stairs_from_smooth_quartz_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10462), class_2246.field_10064, 2)
			.method_17970("has_end_stone_brick", this.method_10426(class_2246.field_10462))
			.method_17971(consumer, "end_stone_brick_slab_from_end_stone_brick_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10462), class_2246.field_10012)
			.method_17970("has_end_stone_brick", this.method_10426(class_2246.field_10462))
			.method_17971(consumer, "end_stone_brick_stairs_from_end_stone_brick_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10462), class_2246.field_10001)
			.method_17970("has_end_stone_brick", this.method_10426(class_2246.field_10462))
			.method_17971(consumer, "end_stone_brick_wall_from_end_stone_brick_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10471), class_2246.field_10462)
			.method_17970("has_end_stone", this.method_10426(class_2246.field_10471))
			.method_17971(consumer, "end_stone_bricks_from_end_stone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10471), class_2246.field_10064, 2)
			.method_17970("has_end_stone", this.method_10426(class_2246.field_10471))
			.method_17971(consumer, "end_stone_brick_slab_from_end_stone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10471), class_2246.field_10012)
			.method_17970("has_end_stone", this.method_10426(class_2246.field_10471))
			.method_17971(consumer, "end_stone_brick_stairs_from_end_stone_stonecutting");
		class_3981.method_17968(class_1856.method_8091(class_2246.field_10471), class_2246.field_10001)
			.method_17970("has_end_stone", this.method_10426(class_2246.field_10471))
			.method_17971(consumer, "end_stone_brick_wall_from_end_stone_stonecutting");
		class_3981.method_17969(class_1856.method_8091(class_2246.field_10360), class_2246.field_10136, 2)
			.method_17970("has_smooth_stone", this.method_10426(class_2246.field_10360))
			.method_17971(consumer, "smooth_stone_slab_from_smooth_stone_stonecutting");
	}

	private void method_17585(Consumer<class_2444> consumer, String string, class_3957<?> arg, int i) {
		class_2454.method_17801(class_1856.method_8091(class_1802.field_8046), class_1802.field_8176, 0.35F, i, arg)
			.method_10469("has_beef", this.method_10426(class_1802.field_8046))
			.method_10472(consumer, "cooked_beef_from_" + string);
		class_2454.method_17801(class_1856.method_8091(class_1802.field_8726), class_1802.field_8544, 0.35F, i, arg)
			.method_10469("has_chicken", this.method_10426(class_1802.field_8726))
			.method_10472(consumer, "cooked_chicken_from_" + string);
		class_2454.method_17801(class_1856.method_8091(class_1802.field_8429), class_1802.field_8373, 0.35F, i, arg)
			.method_10469("has_cod", this.method_10426(class_1802.field_8429))
			.method_10472(consumer, "cooked_cod_from_" + string);
		class_2454.method_17801(class_1856.method_8091(class_2246.field_9993), class_1802.field_8551, 0.1F, i, arg)
			.method_10469("has_kelp", this.method_10426(class_2246.field_9993))
			.method_10472(consumer, "dried_kelp_from_" + string);
		class_2454.method_17801(class_1856.method_8091(class_1802.field_8209), class_1802.field_8509, 0.35F, i, arg)
			.method_10469("has_salmon", this.method_10426(class_1802.field_8209))
			.method_10472(consumer, "cooked_salmon_from_" + string);
		class_2454.method_17801(class_1856.method_8091(class_1802.field_8748), class_1802.field_8347, 0.35F, i, arg)
			.method_10469("has_mutton", this.method_10426(class_1802.field_8748))
			.method_10472(consumer, "cooked_mutton_from_" + string);
		class_2454.method_17801(class_1856.method_8091(class_1802.field_8389), class_1802.field_8261, 0.35F, i, arg)
			.method_10469("has_porkchop", this.method_10426(class_1802.field_8389))
			.method_10472(consumer, "cooked_porkchop_from_" + string);
		class_2454.method_17801(class_1856.method_8091(class_1802.field_8567), class_1802.field_8512, 0.35F, i, arg)
			.method_10469("has_potato", this.method_10426(class_1802.field_8567))
			.method_10472(consumer, "baked_potato_from_" + string);
		class_2454.method_17801(class_1856.method_8091(class_1802.field_8504), class_1802.field_8752, 0.35F, i, arg)
			.method_10469("has_rabbit", this.method_10426(class_1802.field_8504))
			.method_10472(consumer, "cooked_rabbit_from_" + string);
	}

	private class_2037.class_2039 method_10422(class_2248 arg) {
		return new class_2037.class_2039(arg, null);
	}

	private class_2066.class_2068 method_10424(class_2096.class_2100 arg, class_1935 arg2) {
		return this.method_10423(class_2073.class_2074.method_8973().method_8977(arg2).method_8974(arg).method_8976());
	}

	private class_2066.class_2068 method_10426(class_1935 arg) {
		return this.method_10423(class_2073.class_2074.method_8973().method_8977(arg).method_8976());
	}

	private class_2066.class_2068 method_10420(class_3494<class_1792> arg) {
		return this.method_10423(class_2073.class_2074.method_8973().method_8975(arg).method_8976());
	}

	private class_2066.class_2068 method_10423(class_2073... args) {
		return new class_2066.class_2068(class_2096.class_2100.field_9708, class_2096.class_2100.field_9708, class_2096.class_2100.field_9708, args);
	}

	@Override
	public String method_10321() {
		return "Recipes";
	}
}
