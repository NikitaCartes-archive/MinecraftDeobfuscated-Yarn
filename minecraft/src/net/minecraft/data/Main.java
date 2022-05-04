package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;
import net.minecraft.data.client.ModelProvider;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.data.report.BlockListProvider;
import net.minecraft.data.report.CommandSyntaxProvider;
import net.minecraft.data.report.RegistryDumpProvider;
import net.minecraft.data.report.WorldgenProvider;
import net.minecraft.data.server.AdvancementProvider;
import net.minecraft.data.server.BannerPatternTagProvider;
import net.minecraft.data.server.BiomeParametersProvider;
import net.minecraft.data.server.BiomeTagProvider;
import net.minecraft.data.server.BlockTagProvider;
import net.minecraft.data.server.CatVariantTagProvider;
import net.minecraft.data.server.ConfiguredStructureFeatureTagProvider;
import net.minecraft.data.server.EntityTypeTagProvider;
import net.minecraft.data.server.FlatLevelGeneratorPresetTagProvider;
import net.minecraft.data.server.FluidTagProvider;
import net.minecraft.data.server.GameEventTagProvider;
import net.minecraft.data.server.InstrumentTagProvider;
import net.minecraft.data.server.ItemTagProvider;
import net.minecraft.data.server.LootTableProvider;
import net.minecraft.data.server.PaintingVariantTagProvider;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.WorldPresetTagProvider;
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
				path,
				(Collection<Path>)optionSet.valuesOf(optionSpec9).stream().map(input -> Paths.get(input)).collect(Collectors.toList()),
				bl2,
				bl3,
				bl4,
				bl5,
				bl6,
				SharedConstants.getGameVersion(),
				true
			);
			dataGenerator.run();
		} else {
			optionParser.printHelpOn(System.out);
		}
	}

	public static DataGenerator create(
		Path output,
		Collection<Path> inputs,
		boolean includeClient,
		boolean includeServer,
		boolean includeDev,
		boolean includeReports,
		boolean validate,
		GameVersion gameVersion,
		boolean ignoreCache
	) {
		DataGenerator dataGenerator = new DataGenerator(output, inputs, gameVersion, ignoreCache);
		dataGenerator.addProvider(includeClient || includeServer, new SnbtProvider(dataGenerator).addWriter(new StructureValidatorProvider()));
		dataGenerator.addProvider(includeClient, new ModelProvider(dataGenerator));
		dataGenerator.addProvider(includeServer, new FluidTagProvider(dataGenerator));
		BlockTagProvider blockTagProvider = new BlockTagProvider(dataGenerator);
		dataGenerator.addProvider(includeServer, blockTagProvider);
		dataGenerator.addProvider(includeServer, new ItemTagProvider(dataGenerator, blockTagProvider));
		dataGenerator.addProvider(includeServer, new EntityTypeTagProvider(dataGenerator));
		dataGenerator.addProvider(includeServer, new RecipeProvider(dataGenerator));
		dataGenerator.addProvider(includeServer, new AdvancementProvider(dataGenerator));
		dataGenerator.addProvider(includeServer, new LootTableProvider(dataGenerator));
		dataGenerator.addProvider(includeServer, new GameEventTagProvider(dataGenerator));
		dataGenerator.addProvider(includeServer, new BiomeTagProvider(dataGenerator));
		dataGenerator.addProvider(includeServer, new ConfiguredStructureFeatureTagProvider(dataGenerator));
		dataGenerator.addProvider(includeServer, new WorldPresetTagProvider(dataGenerator));
		dataGenerator.addProvider(includeServer, new FlatLevelGeneratorPresetTagProvider(dataGenerator));
		dataGenerator.addProvider(includeServer, new CatVariantTagProvider(dataGenerator));
		dataGenerator.addProvider(includeServer, new PaintingVariantTagProvider(dataGenerator));
		dataGenerator.addProvider(includeServer, new BannerPatternTagProvider(dataGenerator));
		dataGenerator.addProvider(includeServer, new InstrumentTagProvider(dataGenerator));
		dataGenerator.addProvider(includeDev, new NbtProvider(dataGenerator));
		dataGenerator.addProvider(includeReports, new BlockListProvider(dataGenerator));
		dataGenerator.addProvider(includeReports, new RegistryDumpProvider(dataGenerator));
		dataGenerator.addProvider(includeReports, new CommandSyntaxProvider(dataGenerator));
		dataGenerator.addProvider(includeReports, new WorldgenProvider(dataGenerator));
		dataGenerator.addProvider(includeReports, new BiomeParametersProvider(dataGenerator));
		return dataGenerator;
	}
}
