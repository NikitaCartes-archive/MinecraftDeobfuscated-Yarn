package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.class_2403;
import net.minecraft.class_2409;
import net.minecraft.class_2422;
import net.minecraft.class_2425;
import net.minecraft.class_2427;
import net.minecraft.class_2438;
import net.minecraft.class_2446;
import net.minecraft.class_2461;
import net.minecraft.class_2463;
import net.minecraft.class_2466;
import net.minecraft.class_2467;
import net.minecraft.class_2469;
import net.minecraft.class_2471;
import net.minecraft.class_3843;

public class Main {
	public static void main(String[] strings) throws IOException {
		OptionParser optionParser = new OptionParser();
		OptionSpec<Void> optionSpec = optionParser.accepts("help", "Show the help menu").forHelp();
		OptionSpec<Void> optionSpec2 = optionParser.accepts("server", "Include server generators");
		OptionSpec<Void> optionSpec3 = optionParser.accepts("client", "Include client generators");
		OptionSpec<Void> optionSpec4 = optionParser.accepts("dev", "Include development tools");
		OptionSpec<Void> optionSpec5 = optionParser.accepts("reports", "Include data reports");
		OptionSpec<Void> optionSpec6 = optionParser.accepts("validate", "Validate inputs");
		OptionSpec<Void> optionSpec7 = optionParser.accepts("all", "Include all generators");
		OptionSpec<String> optionSpec8 = optionParser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated");
		OptionSpec<String> optionSpec9 = optionParser.accepts("input", "Input folder").withRequiredArg();
		OptionSet optionSet = optionParser.parse(strings);
		if (!optionSet.has(optionSpec) && optionSet.hasOptions()) {
			Path path = Paths.get(optionSpec8.value(optionSet));
			boolean bl = optionSet.has(optionSpec7);
			boolean bl2 = bl || optionSet.has(optionSpec3);
			boolean bl3 = bl || optionSet.has(optionSpec2);
			boolean bl4 = bl || optionSet.has(optionSpec4);
			boolean bl5 = bl || optionSet.has(optionSpec5);
			boolean bl6 = bl || optionSet.has(optionSpec6);
			class_2403 lv = method_4968(
				path, (Collection<Path>)optionSet.valuesOf(optionSpec9).stream().map(string -> Paths.get(string)).collect(Collectors.toList()), bl2, bl3, bl4, bl5, bl6
			);
			lv.method_10315();
		} else {
			optionParser.printHelpOn(System.out);
		}
	}

	public static class_2403 method_4968(Path path, Collection<Path> collection, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5) {
		class_2403 lv = new class_2403(path, collection);
		if (bl || bl2) {
			lv.method_10314(new class_2463(lv));
		}

		if (bl2) {
			lv.method_10314(new class_2469(lv));
			lv.method_10314(new class_2466(lv));
			lv.method_10314(new class_2471(lv));
			lv.method_10314(new class_2467(lv));
			lv.method_10314(new class_2446(lv));
			lv.method_10314(new class_2409(lv));
			lv.method_10314(new class_2438(lv));
		}

		if (bl3) {
			lv.method_10314(new class_2461(lv));
		}

		if (bl4) {
			lv.method_10314(new class_2422(lv));
			lv.method_10314(new class_2427(lv));
			lv.method_10314(new class_2425(lv));
		}

		if (bl5) {
			lv.method_10314(new class_3843(lv));
		}

		return lv;
	}
}
