/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.MetadataProvider;
import net.minecraft.data.SnbtProvider;
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
        AbstractOptionSpec optionSpec = optionParser.accepts("help", "Show the help menu").forHelp();
        OptionSpecBuilder optionSpec2 = optionParser.accepts("server", "Include server generators");
        OptionSpecBuilder optionSpec3 = optionParser.accepts("client", "Include client generators");
        OptionSpecBuilder optionSpec4 = optionParser.accepts("dev", "Include development tools");
        OptionSpecBuilder optionSpec5 = optionParser.accepts("reports", "Include data reports");
        OptionSpecBuilder optionSpec6 = optionParser.accepts("validate", "Validate inputs");
        OptionSpecBuilder optionSpec7 = optionParser.accepts("all", "Include all generators");
        ArgumentAcceptingOptionSpec<String> optionSpec8 = optionParser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated", (String[])new String[0]);
        ArgumentAcceptingOptionSpec<String> optionSpec9 = optionParser.accepts("input", "Input folder").withRequiredArg();
        OptionSet optionSet = optionParser.parse(args);
        if (optionSet.has(optionSpec) || !optionSet.hasOptions()) {
            optionParser.printHelpOn(System.out);
            return;
        }
        Path path = Paths.get((String)optionSpec8.value(optionSet), new String[0]);
        boolean bl = optionSet.has(optionSpec7);
        boolean bl2 = bl || optionSet.has(optionSpec3);
        boolean bl3 = bl || optionSet.has(optionSpec2);
        boolean bl4 = bl || optionSet.has(optionSpec4);
        boolean bl5 = bl || optionSet.has(optionSpec5);
        boolean bl6 = bl || optionSet.has(optionSpec6);
        DataGenerator dataGenerator = Main.create(path, optionSet.valuesOf(optionSpec9).stream().map(input -> Paths.get(input, new String[0])).collect(Collectors.toList()), bl2, bl3, bl4, bl5, bl6, SharedConstants.getGameVersion(), true);
        dataGenerator.run();
    }

    private static <T extends DataProvider> DataProvider.Factory<T> toFactory(BiFunction<DataOutput, CompletableFuture<RegistryWrapper.WrapperLookup>, T> baseFactory, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        return output -> (DataProvider)baseFactory.apply(output, registryLookupFuture);
    }

    public static DataGenerator create(Path output2, Collection<Path> inputs, boolean includeClient, boolean includeServer, boolean includeDev, boolean includeReports, boolean validate, GameVersion gameVersion, boolean ignoreCache) {
        DataGenerator dataGenerator = new DataGenerator(output2, gameVersion, ignoreCache);
        DataGenerator.Pack pack = dataGenerator.createVanillaPack(includeClient || includeServer);
        pack.addProvider(output -> new SnbtProvider(output, inputs).addWriter(new StructureValidatorProvider()));
        CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture = CompletableFuture.supplyAsync(BuiltinRegistries::createWrapperLookup, Util.getMainWorkerExecutor());
        DataGenerator.Pack pack2 = dataGenerator.createVanillaPack(includeClient);
        pack2.addProvider(ModelProvider::new);
        pack2 = dataGenerator.createVanillaPack(includeServer);
        pack2.addProvider(Main.toFactory(DynamicRegistriesProvider::new, completableFuture));
        pack2.addProvider(Main.toFactory(VanillaAdvancementProviders::createVanillaProvider, completableFuture));
        pack2.addProvider(VanillaLootTableProviders::createVanillaProvider);
        pack2.addProvider(VanillaRecipeProvider::new);
        AbstractTagProvider abstractTagProvider = pack2.addProvider(Main.toFactory(VanillaBlockTagProvider::new, completableFuture));
        pack2.addProvider(output -> new VanillaItemTagProvider(output, completableFuture, abstractTagProvider));
        pack2.addProvider(Main.toFactory(BannerPatternTagProvider::new, completableFuture));
        pack2.addProvider(Main.toFactory(BiomeTagProvider::new, completableFuture));
        pack2.addProvider(Main.toFactory(CatVariantTagProvider::new, completableFuture));
        pack2.addProvider(Main.toFactory(EntityTypeTagProvider::new, completableFuture));
        pack2.addProvider(Main.toFactory(FlatLevelGeneratorPresetTagProvider::new, completableFuture));
        pack2.addProvider(Main.toFactory(FluidTagProvider::new, completableFuture));
        pack2.addProvider(Main.toFactory(GameEventTagProvider::new, completableFuture));
        pack2.addProvider(Main.toFactory(InstrumentTagProvider::new, completableFuture));
        pack2.addProvider(Main.toFactory(PaintingVariantTagProvider::new, completableFuture));
        pack2.addProvider(Main.toFactory(PointOfInterestTypeTagProvider::new, completableFuture));
        pack2.addProvider(Main.toFactory(StructureTagProvider::new, completableFuture));
        pack2.addProvider(Main.toFactory(WorldPresetTagProvider::new, completableFuture));
        pack2 = dataGenerator.createVanillaPack(includeDev);
        pack2.addProvider(output -> new NbtProvider(output, inputs));
        pack2 = dataGenerator.createVanillaPack(includeReports);
        pack2.addProvider(Main.toFactory(BiomeParametersProvider::new, completableFuture));
        pack2.addProvider(BlockListProvider::new);
        pack2.addProvider(Main.toFactory(CommandSyntaxProvider::new, completableFuture));
        pack2.addProvider(RegistryDumpProvider::new);
        pack2 = dataGenerator.createVanillaSubPack(includeServer, "bundle");
        pack2.addProvider(BundleRecipeProvider::new);
        pack2.addProvider(output -> MetadataProvider.create(output, Text.translatable("dataPack.bundle.description"), FeatureSet.of(FeatureFlags.BUNDLE)));
        pack2 = dataGenerator.createVanillaSubPack(includeServer, "update_1_20");
        pack2.addProvider(OneTwentyRecipeProvider::new);
        abstractTagProvider = pack2.addProvider(Main.toFactory(OneTwentyBlockTagProvider::new, completableFuture));
        pack2.addProvider(output -> new OneTwentyItemTagProvider(output, completableFuture, abstractTagProvider));
        pack2.addProvider(OneTwentyLootTableProviders::createOneTwentyProvider);
        pack2.addProvider(output -> MetadataProvider.create(output, Text.translatable("dataPack.update_1_20.description"), FeatureSet.of(FeatureFlags.UPDATE_1_20)));
        return dataGenerator;
    }
}

