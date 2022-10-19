/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
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
import net.minecraft.data.MetadataProvider;
import net.minecraft.data.SnbtProvider;
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

    public static DataGenerator create(Path output, Collection<Path> inputs, boolean includeClient, boolean includeServer, boolean includeDev, boolean includeReports, boolean validate, GameVersion gameVersion, boolean ignoreCache) {
        DataGenerator dataGenerator = new DataGenerator(output, gameVersion, ignoreCache);
        DataOutput dataOutput = dataGenerator.getOutput();
        dataGenerator.addProvider(includeClient || includeServer, new SnbtProvider(dataOutput, inputs).addWriter(new StructureValidatorProvider()));
        dataGenerator.addProvider(includeClient, new ModelProvider(dataOutput));
        dataGenerator.addProvider(includeServer, new WorldgenProvider(dataOutput));
        dataGenerator.addProvider(includeServer, VanillaAdvancementProviders.createVanillaProvider(dataOutput));
        dataGenerator.addProvider(includeServer, VanillaLootTableProviders.createVanillaProvider(dataOutput));
        dataGenerator.addProvider(includeServer, new VanillaRecipeProvider(dataOutput));
        VanillaBlockTagProvider abstractTagProvider = new VanillaBlockTagProvider(dataOutput);
        dataGenerator.addProvider(includeServer, abstractTagProvider);
        dataGenerator.addProvider(includeServer, new VanillaItemTagProvider(dataOutput, abstractTagProvider));
        dataGenerator.addProvider(includeServer, new BannerPatternTagProvider(dataOutput));
        dataGenerator.addProvider(includeServer, new BiomeTagProvider(dataOutput));
        dataGenerator.addProvider(includeServer, new CatVariantTagProvider(dataOutput));
        dataGenerator.addProvider(includeServer, new EntityTypeTagProvider(dataOutput));
        dataGenerator.addProvider(includeServer, new FlatLevelGeneratorPresetTagProvider(dataOutput));
        dataGenerator.addProvider(includeServer, new FluidTagProvider(dataOutput));
        dataGenerator.addProvider(includeServer, new GameEventTagProvider(dataOutput));
        dataGenerator.addProvider(includeServer, new InstrumentTagProvider(dataOutput));
        dataGenerator.addProvider(includeServer, new PaintingVariantTagProvider(dataOutput));
        dataGenerator.addProvider(includeServer, new PointOfInterestTypeTagProvider(dataOutput));
        dataGenerator.addProvider(includeServer, new StructureTagProvider(dataOutput));
        dataGenerator.addProvider(includeServer, new WorldPresetTagProvider(dataOutput));
        dataGenerator.addProvider(includeDev, new NbtProvider(dataOutput, inputs));
        dataGenerator.addProvider(includeReports, new BiomeParametersProvider(dataOutput));
        dataGenerator.addProvider(includeReports, new BlockListProvider(dataOutput));
        dataGenerator.addProvider(includeReports, new CommandSyntaxProvider(dataOutput));
        dataGenerator.addProvider(includeReports, new RegistryDumpProvider(dataOutput));
        DataOutput dataOutput2 = dataGenerator.getOutputFor("bundle");
        dataGenerator.addProvider(includeServer, new BundleRecipeProvider(dataOutput2));
        dataGenerator.addProvider(includeServer, MetadataProvider.create(dataOutput2, "bundle", Text.translatable("dataPack.bundle.description"), FeatureSet.of(FeatureFlags.BUNDLE)));
        DataOutput dataOutput3 = dataGenerator.getOutputFor("update_1_20");
        dataGenerator.addProvider(includeServer, new OneTwentyRecipeProvider(dataOutput3));
        OneTwentyBlockTagProvider oneTwentyBlockTagProvider = new OneTwentyBlockTagProvider(dataOutput3);
        dataGenerator.addProvider(includeServer, oneTwentyBlockTagProvider);
        dataGenerator.addProvider(includeServer, new OneTwentyItemTagProvider(dataOutput3, oneTwentyBlockTagProvider));
        dataGenerator.addProvider(includeServer, OneTwentyLootTableProviders.createOneTwentyProvider(dataOutput3));
        dataGenerator.addProvider(includeServer, MetadataProvider.create(dataOutput3, "update_1_20", Text.translatable("dataPack.update_1_20.description"), FeatureSet.of(FeatureFlags.UPDATE_1_20)));
        return dataGenerator;
    }
}

