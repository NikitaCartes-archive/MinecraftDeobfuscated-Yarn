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
import net.minecraft.data.client.BlockStateDefinitionProvider;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.data.report.BiomeListProvider;
import net.minecraft.data.report.BlockListProvider;
import net.minecraft.data.report.CommandSyntaxProvider;
import net.minecraft.data.report.RegistryDumpProvider;
import net.minecraft.data.server.AdvancementsProvider;
import net.minecraft.data.server.BlockTagsProvider;
import net.minecraft.data.server.EntityTypeTagsProvider;
import net.minecraft.data.server.FluidTagsProvider;
import net.minecraft.data.server.GameEventTagsProvider;
import net.minecraft.data.server.ItemTagsProvider;
import net.minecraft.data.server.LootTablesProvider;
import net.minecraft.data.server.RecipesProvider;
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
			dataGenerator.addProvider(new BlockStateDefinitionProvider(dataGenerator));
		}

		if (includeServer) {
			dataGenerator.addProvider(new FluidTagsProvider(dataGenerator));
			BlockTagsProvider blockTagsProvider = new BlockTagsProvider(dataGenerator);
			dataGenerator.addProvider(blockTagsProvider);
			dataGenerator.addProvider(new ItemTagsProvider(dataGenerator, blockTagsProvider));
			dataGenerator.addProvider(new EntityTypeTagsProvider(dataGenerator));
			dataGenerator.addProvider(new RecipesProvider(dataGenerator));
			dataGenerator.addProvider(new AdvancementsProvider(dataGenerator));
			dataGenerator.addProvider(new LootTablesProvider(dataGenerator));
			dataGenerator.addProvider(new GameEventTagsProvider(dataGenerator));
		}

		if (includeDev) {
			dataGenerator.addProvider(new NbtProvider(dataGenerator));
		}

		if (includeReports) {
			dataGenerator.addProvider(new BlockListProvider(dataGenerator));
			dataGenerator.addProvider(new RegistryDumpProvider(dataGenerator));
			dataGenerator.addProvider(new CommandSyntaxProvider(dataGenerator));
			dataGenerator.addProvider(new BiomeListProvider(dataGenerator));
		}

		return dataGenerator;
	}
}
