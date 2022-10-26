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
import net.minecraft.block.Block;
import net.minecraft.data.client.ModelProvider;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.data.report.BlockListProvider;
import net.minecraft.data.report.CommandSyntaxProvider;
import net.minecraft.data.report.RegistryDumpProvider;
import net.minecraft.data.report.WorldgenProvider;
import net.minecraft.data.server.BiomeParametersProvider;
import net.minecraft.data.server.advancement.VanillaAdvancementProviders;
import net.minecraft.data.server.loottable.OneTwentyLootTableProviders;
import net.minecraft.data.server.loottable.VanillaLootTableProviders;
import net.minecraft.data.server.recipe.BundleRecipeProvider;
import net.minecraft.data.server.recipe.OneTwentyRecipeProvider;
import net.minecraft.data.server.recipe.VanillaRecipeProvider;
import net.minecraft.data.server.tag.AbstractTagProvider;
import net.minecraft.data.server.tag.BannerPatternTagProvider;
import net.minecraft.data.server.tag.BiomeTagProvider;
import net.minecraft.data.server.tag.CatVariantTagProvider;
import net.minecraft.data.server.tag.EntityTypeTagProvider;
import net.minecraft.data.server.tag.FlatLevelGeneratorPresetTagProvider;
import net.minecraft.data.server.tag.FluidTagProvider;
import net.minecraft.data.server.tag.GameEventTagProvider;
import net.minecraft.data.server.tag.InstrumentTagProvider;
import net.minecraft.data.server.tag.OneTwentyBlockTagProvider;
import net.minecraft.data.server.tag.OneTwentyItemTagProvider;
import net.minecraft.data.server.tag.PaintingVariantTagProvider;
import net.minecraft.data.server.tag.PointOfInterestTypeTagProvider;
import net.minecraft.data.server.tag.StructureTagProvider;
import net.minecraft.data.server.tag.VanillaBlockTagProvider;
import net.minecraft.data.server.tag.VanillaItemTagProvider;
import net.minecraft.data.server.tag.WorldPresetTagProvider;
import net.minecraft.data.validate.StructureValidatorProvider;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;

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
		DataGenerator dataGenerator = new DataGenerator(output, gameVersion, ignoreCache);
		DataGenerator.Pack pack = dataGenerator.createVanillaPack(includeClient || includeServer);
		pack.addProvider(outputx -> new SnbtProvider(outputx, inputs).addWriter(new StructureValidatorProvider()));
		pack = dataGenerator.createVanillaPack(includeClient);
		pack.addProvider(ModelProvider::new);
		pack = dataGenerator.createVanillaPack(includeServer);
		pack.addProvider(WorldgenProvider::new);
		pack.addProvider(VanillaAdvancementProviders::createVanillaProvider);
		pack.addProvider(VanillaLootTableProviders::createVanillaProvider);
		pack.addProvider(VanillaRecipeProvider::new);
		AbstractTagProvider<Block> abstractTagProvider = pack.addProvider(VanillaBlockTagProvider::new);
		pack.addProvider(outputx -> new VanillaItemTagProvider(outputx, abstractTagProvider));
		pack.addProvider(BannerPatternTagProvider::new);
		pack.addProvider(BiomeTagProvider::new);
		pack.addProvider(CatVariantTagProvider::new);
		pack.addProvider(EntityTypeTagProvider::new);
		pack.addProvider(FlatLevelGeneratorPresetTagProvider::new);
		pack.addProvider(FluidTagProvider::new);
		pack.addProvider(GameEventTagProvider::new);
		pack.addProvider(InstrumentTagProvider::new);
		pack.addProvider(PaintingVariantTagProvider::new);
		pack.addProvider(PointOfInterestTypeTagProvider::new);
		pack.addProvider(StructureTagProvider::new);
		pack.addProvider(WorldPresetTagProvider::new);
		pack = dataGenerator.createVanillaPack(includeDev);
		pack.addProvider(outputx -> new NbtProvider(outputx, inputs));
		pack = dataGenerator.createVanillaPack(includeReports);
		pack.addProvider(BiomeParametersProvider::new);
		pack.addProvider(BlockListProvider::new);
		pack.addProvider(CommandSyntaxProvider::new);
		pack.addProvider(RegistryDumpProvider::new);
		pack = dataGenerator.createVanillaSubPack(includeServer, "bundle");
		pack.addProvider(BundleRecipeProvider::new);
		pack.addProvider(outputx -> MetadataProvider.create(outputx, Text.translatable("dataPack.bundle.description"), FeatureSet.of(FeatureFlags.BUNDLE)));
		pack = dataGenerator.createVanillaSubPack(includeServer, "update_1_20");
		pack.addProvider(OneTwentyRecipeProvider::new);
		abstractTagProvider = pack.addProvider(OneTwentyBlockTagProvider::new);
		pack.addProvider(outputx -> new OneTwentyItemTagProvider(outputx, abstractTagProvider));
		pack.addProvider(OneTwentyLootTableProviders::createOneTwentyProvider);
		pack.addProvider(outputx -> MetadataProvider.create(outputx, Text.translatable("dataPack.update_1_20.description"), FeatureSet.of(FeatureFlags.UPDATE_1_20)));
		return dataGenerator;
	}
}
