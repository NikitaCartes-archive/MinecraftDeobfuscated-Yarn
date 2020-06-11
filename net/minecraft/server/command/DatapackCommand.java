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
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class DatapackCommand {
    private static final DynamicCommandExceptionType UNKNOWN_DATAPACK_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.datapack.unknown", object));
    private static final DynamicCommandExceptionType ALREADY_ENABLED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.datapack.enable.failed", object));
    private static final DynamicCommandExceptionType ALREADY_DISABLED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.datapack.disable.failed", object));
    private static final SuggestionProvider<ServerCommandSource> ENABLED_CONTAINERS_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getDataPackManager().getEnabledNames().stream().map(StringArgumentType::escapeIfRequired), suggestionsBuilder);
    private static final SuggestionProvider<ServerCommandSource> DISABLED_CONTAINERS_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
        ResourcePackManager<ResourcePackProfile> resourcePackManager = ((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getDataPackManager();
        Collection<String> collection = resourcePackManager.getEnabledNames();
        return CommandSource.suggestMatching(resourcePackManager.getNames().stream().filter(string -> !collection.contains(string)).map(StringArgumentType::escapeIfRequired), suggestionsBuilder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("datapack").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("enable").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("name", StringArgumentType.string()).suggests(DISABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), (list, resourcePackProfile2) -> resourcePackProfile2.getInitialPosition().insert(list, resourcePackProfile2, resourcePackProfile -> resourcePackProfile, false)))).then(CommandManager.literal("after").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("existing", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), (list, resourcePackProfile) -> list.add(list.indexOf(DatapackCommand.getPackContainer(commandContext, "existing", false)) + 1, resourcePackProfile)))))).then(CommandManager.literal("before").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("existing", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), (list, resourcePackProfile) -> list.add(list.indexOf(DatapackCommand.getPackContainer(commandContext, "existing", false)), resourcePackProfile)))))).then(CommandManager.literal("last").executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), List::add)))).then(CommandManager.literal("first").executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), (list, resourcePackProfile) -> list.add(0, resourcePackProfile))))))).then(CommandManager.literal("disable").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeDisable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", false)))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("list").executes(commandContext -> DatapackCommand.executeList((ServerCommandSource)commandContext.getSource()))).then(CommandManager.literal("available").executes(commandContext -> DatapackCommand.executeListAvailable((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("enabled").executes(commandContext -> DatapackCommand.executeListEnabled((ServerCommandSource)commandContext.getSource())))));
    }

    private static int executeEnable(ServerCommandSource source, ResourcePackProfile container, PackAdder packAdder) throws CommandSyntaxException {
        ResourcePackManager<ResourcePackProfile> resourcePackManager = source.getMinecraftServer().getDataPackManager();
        ArrayList<ResourcePackProfile> list = Lists.newArrayList(resourcePackManager.getEnabledProfiles());
        packAdder.apply(list, container);
        source.sendFeedback(new TranslatableText("commands.datapack.modify.enable", container.getInformationText(true)), true);
        ReloadCommand.method_29480(list.stream().map(ResourcePackProfile::getName).collect(Collectors.toList()), source);
        return list.size();
    }

    private static int executeDisable(ServerCommandSource source, ResourcePackProfile container) {
        ResourcePackManager<ResourcePackProfile> resourcePackManager = source.getMinecraftServer().getDataPackManager();
        ArrayList<ResourcePackProfile> list = Lists.newArrayList(resourcePackManager.getEnabledProfiles());
        list.remove(container);
        source.sendFeedback(new TranslatableText("commands.datapack.modify.disable", container.getInformationText(true)), true);
        ReloadCommand.method_29480(list.stream().map(ResourcePackProfile::getName).collect(Collectors.toList()), source);
        return list.size();
    }

    private static int executeList(ServerCommandSource source) {
        return DatapackCommand.executeListEnabled(source) + DatapackCommand.executeListAvailable(source);
    }

    private static int executeListAvailable(ServerCommandSource source) {
        ResourcePackManager<ResourcePackProfile> resourcePackManager = source.getMinecraftServer().getDataPackManager();
        resourcePackManager.scanPacks();
        Collection<ResourcePackProfile> collection = resourcePackManager.getEnabledProfiles();
        Collection<ResourcePackProfile> collection2 = resourcePackManager.getProfiles();
        List list = collection2.stream().filter(resourcePackProfile -> !collection.contains(resourcePackProfile)).collect(Collectors.toList());
        if (list.isEmpty()) {
            source.sendFeedback(new TranslatableText("commands.datapack.list.available.none"), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.datapack.list.available.success", list.size(), Texts.join(list, resourcePackProfile -> resourcePackProfile.getInformationText(false))), false);
        }
        return list.size();
    }

    private static int executeListEnabled(ServerCommandSource source) {
        ResourcePackManager<ResourcePackProfile> resourcePackManager = source.getMinecraftServer().getDataPackManager();
        resourcePackManager.scanPacks();
        Collection<ResourcePackProfile> collection = resourcePackManager.getEnabledProfiles();
        if (collection.isEmpty()) {
            source.sendFeedback(new TranslatableText("commands.datapack.list.enabled.none"), false);
        } else {
            source.sendFeedback(new TranslatableText("commands.datapack.list.enabled.success", collection.size(), Texts.join(collection, resourcePackProfile -> resourcePackProfile.getInformationText(true))), false);
        }
        return collection.size();
    }

    private static ResourcePackProfile getPackContainer(CommandContext<ServerCommandSource> context, String name, boolean enable) throws CommandSyntaxException {
        String string = StringArgumentType.getString(context, name);
        ResourcePackManager<ResourcePackProfile> resourcePackManager = context.getSource().getMinecraftServer().getDataPackManager();
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
        return resourcePackProfile;
    }

    static interface PackAdder {
        public void apply(List<ResourcePackProfile> var1, ResourcePackProfile var2) throws CommandSyntaxException;
    }
}

