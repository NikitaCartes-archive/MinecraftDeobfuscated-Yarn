package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
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
import net.minecraft.data.report.DynamicRegistriesProvider;
import net.minecraft.data.report.RegistryDumpProvider;
import net.minecraft.data.server.BiomeParametersProvider;
import net.minecraft.data.server.advancement.vanilla.VanillaAdvancementProviders;
import net.minecraft.data.server.loottable.vanilla.VanillaLootTableProviders;
import net.minecraft.data.server.recipe.BundleRecipeProvider;
import net.minecraft.data.server.recipe.VanillaRecipeProvider;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaBannerPatternTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaBiomeTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaBlockTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaCatVariantTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaDamageTypeTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaEntityTypeTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaFlatLevelGeneratorPresetTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaFluidTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaGameEventTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaInstrumentTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaItemTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaPaintingVariantTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaPointOfInterestTypeTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaStructureTagProvider;
import net.minecraft.data.server.tag.vanilla.VanillaWorldPresetTagProvider;
import net.minecraft.data.validate.StructureValidatorProvider;
import net.minecraft.item.Item;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

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

	private static <T extends DataProvider> DataProvider.Factory<T> toFactory(
		BiFunction<DataOutput, CompletableFuture<RegistryWrapper.WrapperLookup>, T> baseFactory,
		CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture
	) {
		return output -> (T)baseFactory.apply(output, registryLookupFuture);
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
		CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture = CompletableFuture.supplyAsync(
			BuiltinRegistries::createWrapperLookup, Util.getMainWorkerExecutor()
		);
		DataGenerator.Pack pack2 = dataGenerator.createVanillaPack(includeClient);
		pack2.addProvider(ModelProvider::new);
		DataGenerator.Pack pack3 = dataGenerator.createVanillaPack(includeServer);
		pack3.addProvider(toFactory(DynamicRegistriesProvider::new, completableFuture));
		pack3.addProvider(toFactory(VanillaAdvancementProviders::createVanillaProvider, completableFuture));
		pack3.addProvider(VanillaLootTableProviders::createVanillaProvider);
		pack3.addProvider(VanillaRecipeProvider::new);
		TagProvider<Block> tagProvider = pack3.addProvider(toFactory(VanillaBlockTagProvider::new, completableFuture));
		TagProvider<Item> tagProvider2 = pack3.addProvider(outputx -> new VanillaItemTagProvider(outputx, completableFuture, tagProvider.getTagLookupFuture()));
		pack3.addProvider(toFactory(VanillaBannerPatternTagProvider::new, completableFuture));
		pack3.addProvider(toFactory(VanillaBiomeTagProvider::new, completableFuture));
		pack3.addProvider(toFactory(VanillaCatVariantTagProvider::new, completableFuture));
		pack3.addProvider(toFactory(VanillaDamageTypeTagProvider::new, completableFuture));
		pack3.addProvider(toFactory(VanillaEntityTypeTagProvider::new, completableFuture));
		pack3.addProvider(toFactory(VanillaFlatLevelGeneratorPresetTagProvider::new, completableFuture));
		pack3.addProvider(toFactory(VanillaFluidTagProvider::new, completableFuture));
		pack3.addProvider(toFactory(VanillaGameEventTagProvider::new, completableFuture));
		pack3.addProvider(toFactory(VanillaInstrumentTagProvider::new, completableFuture));
		pack3.addProvider(toFactory(VanillaPaintingVariantTagProvider::new, completableFuture));
		pack3.addProvider(toFactory(VanillaPointOfInterestTypeTagProvider::new, completableFuture));
		pack3.addProvider(toFactory(VanillaStructureTagProvider::new, completableFuture));
		pack3.addProvider(toFactory(VanillaWorldPresetTagProvider::new, completableFuture));
		pack3 = dataGenerator.createVanillaPack(includeDev);
		pack3.addProvider(outputx -> new NbtProvider(outputx, inputs));
		pack3 = dataGenerator.createVanillaPack(includeReports);
		pack3.addProvider(toFactory(BiomeParametersProvider::new, completableFuture));
		pack3.addProvider(BlockListProvider::new);
		pack3.addProvider(toFactory(CommandSyntaxProvider::new, completableFuture));
		pack3.addProvider(RegistryDumpProvider::new);
		pack3 = dataGenerator.createVanillaSubPack(includeServer, "bundle");
		pack3.addProvider(BundleRecipeProvider::new);
		pack3.addProvider(outputx -> MetadataProvider.create(outputx, Text.translatable("dataPack.bundle.description"), FeatureSet.of(FeatureFlags.BUNDLE)));
		return dataGenerator;
	}
}
