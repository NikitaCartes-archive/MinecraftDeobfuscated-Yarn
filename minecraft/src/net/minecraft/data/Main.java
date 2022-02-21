package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.SharedConstants;
import net.minecraft.data.client.ModelProvider;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.data.report.BlockListProvider;
import net.minecraft.data.report.CommandSyntaxProvider;
import net.minecraft.data.report.RegistryDumpProvider;
import net.minecraft.data.report.WorldgenProvider;
import net.minecraft.data.server.AdvancementProvider;
import net.minecraft.data.server.BiomeTagProvider;
import net.minecraft.data.server.BlockTagProvider;
import net.minecraft.data.server.ConfiguredStructureFeatureTagProvider;
import net.minecraft.data.server.EntityTypeTagProvider;
import net.minecraft.data.server.FluidTagProvider;
import net.minecraft.data.server.GameEventTagProvider;
import net.minecraft.data.server.ItemTagProvider;
import net.minecraft.data.server.LootTableProvider;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.validate.StructureValidatorProvider;
import net.minecraft.obfuscate.DontObfuscate;

public class Main {
	@DontObfuscate
	public static void main(String[] args) throws IOException {
		SharedConstants.createGameVersion();
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
		OptionSet optionSet = optionParser.parse(args);
		if (!optionSet.has(optionSpec) && optionSet.hasOptions()) {
			Path path = Paths.get(optionSpec8.value(optionSet));
			boolean bl = optionSet.has(optionSpec7);
			boolean bl2 = bl || optionSet.has(optionSpec3);
			boolean bl3 = bl || optionSet.has(optionSpec2);
			boolean bl4 = bl || optionSet.has(optionSpec4);
			boolean bl5 = bl || optionSet.has(optionSpec5);
			boolean bl6 = bl || optionSet.has(optionSpec6);
			DataGenerator dataGenerator = create(
				path, (Collection<Path>)optionSet.valuesOf(optionSpec9).stream().map(string -> Paths.get(string)).collect(Collectors.toList()), bl2, bl3, bl4, bl5, bl6
			);
			dataGenerator.run();
		} else {
			optionParser.printHelpOn(System.out);
		}
	}

	public static DataGenerator create(
		Path output, Collection<Path> inputs, boolean includeClient, boolean includeServer, boolean includeDev, boolean includeReports, boolean validate
	) {
		DataGenerator dataGenerator = new DataGenerator(output, inputs);
		if (includeClient || includeServer) {
			dataGenerator.addProvider(new SnbtProvider(dataGenerator).addWriter(new StructureValidatorProvider()));
		}

		if (includeClient) {
			dataGenerator.addProvider(new ModelProvider(dataGenerator));
		}

		if (includeServer) {
			dataGenerator.addProvider(new FluidTagProvider(dataGenerator));
			BlockTagProvider blockTagProvider = new BlockTagProvider(dataGenerator);
			dataGenerator.addProvider(blockTagProvider);
			dataGenerator.addProvider(new ItemTagProvider(dataGenerator, blockTagProvider));
			dataGenerator.addProvider(new EntityTypeTagProvider(dataGenerator));
			dataGenerator.addProvider(new RecipeProvider(dataGenerator));
			dataGenerator.addProvider(new AdvancementProvider(dataGenerator));
			dataGenerator.addProvider(new LootTableProvider(dataGenerator));
			dataGenerator.addProvider(new GameEventTagProvider(dataGenerator));
			dataGenerator.addProvider(new BiomeTagProvider(dataGenerator));
			dataGenerator.addProvider(new ConfiguredStructureFeatureTagProvider(dataGenerator));
		}

		if (includeDev) {
			dataGenerator.addProvider(new NbtProvider(dataGenerator));
		}

		if (includeReports) {
			dataGenerator.addProvider(new BlockListProvider(dataGenerator));
			dataGenerator.addProvider(new RegistryDumpProvider(dataGenerator));
			dataGenerator.addProvider(new CommandSyntaxProvider(dataGenerator));
			dataGenerator.addProvider(new WorldgenProvider(dataGenerator));
		}

		return dataGenerator;
	}
}
