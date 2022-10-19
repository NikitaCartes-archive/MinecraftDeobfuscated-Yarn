/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.command.CommandSource;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class DatapackCommand {
    private static final DynamicCommandExceptionType UNKNOWN_DATAPACK_EXCEPTION = new DynamicCommandExceptionType(name -> Text.translatable("commands.datapack.unknown", name));
    private static final DynamicCommandExceptionType ALREADY_ENABLED_EXCEPTION = new DynamicCommandExceptionType(name -> Text.translatable("commands.datapack.enable.failed", name));
    private static final DynamicCommandExceptionType ALREADY_DISABLED_EXCEPTION = new DynamicCommandExceptionType(name -> Text.translatable("commands.datapack.disable.failed", name));
    private static final Dynamic2CommandExceptionType NO_FLAGS_EXCEPTION = new Dynamic2CommandExceptionType((name, flags) -> Text.translatable("commands.datapack.enable.failed.no_flags", name, flags));
    private static final SuggestionProvider<ServerCommandSource> ENABLED_CONTAINERS_SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestMatching(((ServerCommandSource)context.getSource()).getServer().getDataPackManager().getEnabledNames().stream().map(StringArgumentType::escapeIfRequired), builder);
    private static final SuggestionProvider<ServerCommandSource> DISABLED_CONTAINERS_SUGGESTION_PROVIDER = (context, builder) -> {
        ResourcePackManager resourcePackManager = ((ServerCommandSource)context.getSource()).getServer().getDataPackManager();
        Collection<String> collection = resourcePackManager.getEnabledNames();
        FeatureSet featureSet = ((ServerCommandSource)context.getSource()).getEnabledFeatures();
        return CommandSource.suggestMatching(resourcePackManager.getProfiles().stream().filter(profile -> profile.getRequestedFeatures().isSubsetOf(featureSet)).map(ResourcePackProfile::getName).filter(name -> !collection.contains(name)).map(StringArgumentType::escapeIfRequired), builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("datapack").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.literal("enable").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("name", StringArgumentType.string()).suggests(DISABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(context -> DatapackCommand.executeEnable((ServerCommandSource)context.getSource(), DatapackCommand.getPackContainer(context, "name", true), (profiles, profile2) -> profile2.getInitialPosition().insert(profiles, profile2, profile -> profile, false)))).then(CommandManager.literal("after").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("existing", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(context -> DatapackCommand.executeEnable((ServerCommandSource)context.getSource(), DatapackCommand.getPackContainer(context, "name", true), (profiles, profile) -> profiles.add(profiles.indexOf(DatapackCommand.getPackContainer(context, "existing", false)) + 1, profile)))))).then(CommandManager.literal("before").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("existing", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(context -> DatapackCommand.executeEnable((ServerCommandSource)context.getSource(), DatapackCommand.getPackContainer(context, "name", true), (profiles, profile) -> profiles.add(profiles.indexOf(DatapackCommand.getPackContainer(context, "existing", false)), profile)))))).then(CommandManager.literal("last").executes(context -> DatapackCommand.executeEnable((ServerCommandSource)context.getSource(), DatapackCommand.getPackContainer(context, "name", true), List::add)))).then(CommandManager.literal("first").executes(context -> DatapackCommand.executeEnable((ServerCommandSource)context.getSource(), DatapackCommand.getPackContainer(context, "name", true), (profiles, profile) -> profiles.add(0, profile))))))).then(CommandManager.literal("disable").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(context -> DatapackCommand.executeDisable((ServerCommandSource)context.getSource(), DatapackCommand.getPackContainer(context, "name", false)))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("list").executes(context -> DatapackCommand.executeList((ServerCommandSource)context.getSource()))).then(CommandManager.literal("available").executes(context -> DatapackCommand.executeListAvailable((ServerCommandSource)context.getSource())))).then(CommandManager.literal("enabled").executes(context -> DatapackCommand.executeListEnabled((ServerCommandSource)context.getSource())))));
    }

    private static int executeEnable(ServerCommandSource source, ResourcePackProfile container, PackAdder packAdder) throws CommandSyntaxException {
        ResourcePackManager resourcePackManager = source.getServer().getDataPackManager();
        ArrayList<ResourcePackProfile> list = Lists.newArrayList(resourcePackManager.getEnabledProfiles());
        packAdder.apply(list, container);
        source.sendFeedback(Text.translatable("commands.datapack.modify.enable", container.getInformationText(true)), true);
        ReloadCommand.tryReloadDataPacks(list.stream().map(ResourcePackProfile::getName).collect(Collectors.toList()), source);
        return list.size();
    }

    private static int executeDisable(ServerCommandSource source, ResourcePackProfile container) {
        ResourcePackManager resourcePackManager = source.getServer().getDataPackManager();
        ArrayList<ResourcePackProfile> list = Lists.newArrayList(resourcePackManager.getEnabledProfiles());
        list.remove(container);
        source.sendFeedback(Text.translatable("commands.datapack.modify.disable", container.getInformationText(true)), true);
        ReloadCommand.tryReloadDataPacks(list.stream().map(ResourcePackProfile::getName).collect(Collectors.toList()), source);
        return list.size();
    }

    private static int executeList(ServerCommandSource source) {
        return DatapackCommand.executeListEnabled(source) + DatapackCommand.executeListAvailable(source);
    }

    private static int executeListAvailable(ServerCommandSource source) {
        ResourcePackManager resourcePackManager = source.getServer().getDataPackManager();
        resourcePackManager.scanPacks();
        Collection<ResourcePackProfile> collection = resourcePackManager.getEnabledProfiles();
        Collection<ResourcePackProfile> collection2 = resourcePackManager.getProfiles();
        FeatureSet featureSet = source.getEnabledFeatures();
        List<ResourcePackProfile> list = collection2.stream().filter(profile -> !collection.contains(profile) && profile.getRequestedFeatures().isSubsetOf(featureSet)).toList();
        if (list.isEmpty()) {
            source.sendFeedback(Text.translatable("commands.datapack.list.available.none"), false);
        } else {
            source.sendFeedback(Text.translatable("commands.datapack.list.available.success", list.size(), Texts.join(list, profile -> profile.getInformationText(false))), false);
        }
        return list.size();
    }

    private static int executeListEnabled(ServerCommandSource source) {
        ResourcePackManager resourcePackManager = source.getServer().getDataPackManager();
        resourcePackManager.scanPacks();
        Collection<ResourcePackProfile> collection = resourcePackManager.getEnabledProfiles();
        if (collection.isEmpty()) {
            source.sendFeedback(Text.translatable("commands.datapack.list.enabled.none"), false);
        } else {
            source.sendFeedback(Text.translatable("commands.datapack.list.enabled.success", collection.size(), Texts.join(collection, profile -> profile.getInformationText(true))), false);
        }
        return collection.size();
    }

    private static ResourcePackProfile getPackContainer(CommandContext<ServerCommandSource> context, String name, boolean enable) throws CommandSyntaxException {
        String string = StringArgumentType.getString(context, name);
        ResourcePackManager resourcePackManager = context.getSource().getServer().getDataPackManager();
        ResourcePackProfile resourcePackProfile = resourcePackManager.getProfile(string);
        if (resourcePackProfile == null) {
            throw UNKNOWN_DATAPACK_EXCEPTION.create(string);
        }
        boolean bl = resourcePackManager.getEnabledProfiles().contains(resourcePackProfile);
        if (enable && bl) {
            throw ALREADY_ENABLED_EXCEPTION.create(string);
        }
        if (!enable && !bl) {
            throw ALREADY_DISABLED_EXCEPTION.create(string);
        }
        FeatureSet featureSet = context.getSource().getEnabledFeatures();
        FeatureSet featureSet2 = resourcePackProfile.getRequestedFeatures();
        if (!featureSet2.isSubsetOf(featureSet)) {
            throw NO_FLAGS_EXCEPTION.create(string, FeatureFlags.printMissingFlags(featureSet, featureSet2));
        }
        return resourcePackProfile;
    }

    static interface PackAdder {
        public void apply(List<ResourcePackProfile> var1, ResourcePackProfile var2) throws CommandSyntaxException;
    }
}

