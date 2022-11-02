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
import net.minecraft.util.Util;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryWrapper;

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
		pack2 = dataGenerator.createVanillaPack(includeServer);
		pack2.addProvider(toFactory(DynamicRegistriesProvider::new, completableFuture));
		pack2.addProvider(toFactory(VanillaAdvancementProviders::createVanillaProvider, completableFuture));
		pack2.addProvider(VanillaLootTableProviders::createVanillaProvider);
		pack2.addProvider(VanillaRecipeProvider::new);
		AbstractTagProvider<Block> abstractTagProvider = pack2.addProvider(toFactory(VanillaBlockTagProvider::new, completableFuture));
		pack2.addProvider(outputx -> new VanillaItemTagProvider(outputx, completableFuture, abstractTagProvider));
		pack2.addProvider(toFactory(BannerPatternTagProvider::new, completableFuture));
		pack2.addProvider(toFactory(BiomeTagProvider::new, completableFuture));
		pack2.addProvider(toFactory(CatVariantTagProvider::new, completableFuture));
		pack2.addProvider(toFactory(EntityTypeTagProvider::new, completableFuture));
		pack2.addProvider(toFactory(FlatLevelGeneratorPresetTagProvider::new, completableFuture));
		pack2.addProvider(toFactory(FluidTagProvider::new, completableFuture));
		pack2.addProvider(toFactory(GameEventTagProvider::new, completableFuture));
		pack2.addProvider(toFactory(InstrumentTagProvider::new, completableFuture));
		pack2.addProvider(toFactory(PaintingVariantTagProvider::new, completableFuture));
		pack2.addProvider(toFactory(PointOfInterestTypeTagProvider::new, completableFuture));
		pack2.addProvider(toFactory(StructureTagProvider::new, completableFuture));
		pack2.addProvider(toFactory(WorldPresetTagProvider::new, completableFuture));
		pack2 = dataGenerator.createVanillaPack(includeDev);
		pack2.addProvider(outputx -> new NbtProvider(outputx, inputs));
		pack2 = dataGenerator.createVanillaPack(includeReports);
		pack2.addProvider(toFactory(BiomeParametersProvider::new, completableFuture));
		pack2.addProvider(BlockListProvider::new);
		pack2.addProvider(toFactory(CommandSyntaxProvider::new, completableFuture));
		pack2.addProvider(RegistryDumpProvider::new);
		pack2 = dataGenerator.createVanillaSubPack(includeServer, "bundle");
		pack2.addProvider(BundleRecipeProvider::new);
		pack2.addProvider(outputx -> MetadataProvider.create(outputx, Text.translatable("dataPack.bundle.description"), FeatureSet.of(FeatureFlags.BUNDLE)));
		pack2 = dataGenerator.createVanillaSubPack(includeServer, "update_1_20");
		pack2.addProvider(OneTwentyRecipeProvider::new);
		abstractTagProvider = pack2.addProvider(toFactory(OneTwentyBlockTagProvider::new, completableFuture));
		pack2.addProvider(outputx -> new OneTwentyItemTagProvider(outputx, completableFuture, abstractTagProvider));
		pack2.addProvider(OneTwentyLootTableProviders::createOneTwentyProvider);
		pack2.addProvider(outputx -> MetadataProvider.create(outputx, Text.translatable("dataPack.update_1_20.description"), FeatureSet.of(FeatureFlags.UPDATE_1_20)));
		return dataGenerator;
	}
}
