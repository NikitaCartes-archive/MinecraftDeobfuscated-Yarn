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
import java.util.List;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;

public class DatapackCommand {
    private static final DynamicCommandExceptionType UNKNOWN_DATAPACK_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.datapack.unknown", object));
    private static final DynamicCommandExceptionType ALREADY_ENABLED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.datapack.enable.failed", object));
    private static final DynamicCommandExceptionType ALREADY_DISABLED_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("commands.datapack.disable.failed", object));
    private static final SuggestionProvider<ServerCommandSource> ENABLED_CONTAINERS_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getDataPackContainerManager().getEnabledProfiles().stream().map(ResourcePackProfile::getName).map(StringArgumentType::escapeIfRequired), suggestionsBuilder);
    private static final SuggestionProvider<ServerCommandSource> DISABLED_CONTAINERS_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getDataPackContainerManager().getDisabledProfiles().stream().map(ResourcePackProfile::getName).map(StringArgumentType::escapeIfRequired), suggestionsBuilder);

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("datapack").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("enable").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("name", StringArgumentType.string()).suggests(DISABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), (list, resourcePackProfile2) -> resourcePackProfile2.getInitialPosition().insert(list, resourcePackProfile2, resourcePackProfile -> resourcePackProfile, false)))).then(CommandManager.literal("after").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("existing", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), (list, resourcePackProfile) -> list.add(list.indexOf(DatapackCommand.getPackContainer(commandContext, "existing", false)) + 1, resourcePackProfile)))))).then(CommandManager.literal("before").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("existing", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), (list, resourcePackProfile) -> list.add(list.indexOf(DatapackCommand.getPackContainer(commandContext, "existing", false)), resourcePackProfile)))))).then(CommandManager.literal("last").executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), List::add)))).then(CommandManager.literal("first").executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), (list, resourcePackProfile) -> list.add(0, resourcePackProfile))))))).then(CommandManager.literal("disable").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeDisable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", false)))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("list").executes(commandContext -> DatapackCommand.executeList((ServerCommandSource)commandContext.getSource()))).then(CommandManager.literal("available").executes(commandContext -> DatapackCommand.executeListAvailable((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("enabled").executes(commandContext -> DatapackCommand.executeListEnabled((ServerCommandSource)commandContext.getSource())))));
    }

    private static int executeEnable(ServerCommandSource serverCommandSource, ResourcePackProfile resourcePackProfile2, PackAdder packAdder) throws CommandSyntaxException {
        ResourcePackManager<ResourcePackProfile> resourcePackManager = serverCommandSource.getMinecraftServer().getDataPackContainerManager();
        ArrayList<ResourcePackProfile> list = Lists.newArrayList(resourcePackManager.getEnabledProfiles());
        packAdder.apply(list, resourcePackProfile2);
        resourcePackManager.setEnabledProfiles(list);
        LevelProperties levelProperties = serverCommandSource.getMinecraftServer().getWorld(DimensionType.OVERWORLD).getLevelProperties();
        levelProperties.getEnabledDataPacks().clear();
        resourcePackManager.getEnabledProfiles().forEach(resourcePackProfile -> levelProperties.getEnabledDataPacks().add(resourcePackProfile.getName()));
        levelProperties.getDisabledDataPacks().remove(resourcePackProfile2.getName());
        serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.enable.success", resourcePackProfile2.getInformationText(true)), true);
        serverCommandSource.getMinecraftServer().reload();
        return resourcePackManager.getEnabledProfiles().size();
    }

    private static int executeDisable(ServerCommandSource serverCommandSource, ResourcePackProfile resourcePackProfile2) {
        ResourcePackManager<ResourcePackProfile> resourcePackManager = serverCommandSource.getMinecraftServer().getDataPackContainerManager();
        ArrayList<ResourcePackProfile> list = Lists.newArrayList(resourcePackManager.getEnabledProfiles());
        list.remove(resourcePackProfile2);
        resourcePackManager.setEnabledProfiles(list);
        LevelProperties levelProperties = serverCommandSource.getMinecraftServer().getWorld(DimensionType.OVERWORLD).getLevelProperties();
        levelProperties.getEnabledDataPacks().clear();
        resourcePackManager.getEnabledProfiles().forEach(resourcePackProfile -> levelProperties.getEnabledDataPacks().add(resourcePackProfile.getName()));
        levelProperties.getDisabledDataPacks().add(resourcePackProfile2.getName());
        serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.disable.success", resourcePackProfile2.getInformationText(true)), true);
        serverCommandSource.getMinecraftServer().reload();
        return resourcePackManager.getEnabledProfiles().size();
    }

    private static int executeList(ServerCommandSource serverCommandSource) {
        return DatapackCommand.executeListEnabled(serverCommandSource) + DatapackCommand.executeListAvailable(serverCommandSource);
    }

    private static int executeListAvailable(ServerCommandSource serverCommandSource) {
        ResourcePackManager<ResourcePackProfile> resourcePackManager = serverCommandSource.getMinecraftServer().getDataPackContainerManager();
        if (resourcePackManager.getDisabledProfiles().isEmpty()) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.list.available.none", new Object[0]), false);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.list.available.success", resourcePackManager.getDisabledProfiles().size(), Texts.join(resourcePackManager.getDisabledProfiles(), resourcePackProfile -> resourcePackProfile.getInformationText(false))), false);
        }
        return resourcePackManager.getDisabledProfiles().size();
    }

    private static int executeListEnabled(ServerCommandSource serverCommandSource) {
        ResourcePackManager<ResourcePackProfile> resourcePackManager = serverCommandSource.getMinecraftServer().getDataPackContainerManager();
        if (resourcePackManager.getEnabledProfiles().isEmpty()) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.list.enabled.none", new Object[0]), false);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.list.enabled.success", resourcePackManager.getEnabledProfiles().size(), Texts.join(resourcePackManager.getEnabledProfiles(), resourcePackProfile -> resourcePackProfile.getInformationText(true))), false);
        }
        return resourcePackManager.getEnabledProfiles().size();
    }

    private static ResourcePackProfile getPackContainer(CommandContext<ServerCommandSource> commandContext, String string, boolean bl) throws CommandSyntaxException {
        String string2 = StringArgumentType.getString(commandContext, string);
        ResourcePackManager<ResourcePackProfile> resourcePackManager = commandContext.getSource().getMinecraftServer().getDataPackContainerManager();
        ResourcePackProfile resourcePackProfile = resourcePackManager.getProfile(string2);
        if (resourcePackProfile == null) {
            throw UNKNOWN_DATAPACK_EXCEPTION.create(string2);
        }
        boolean bl2 = resourcePackManager.getEnabledProfiles().contains(resourcePackProfile);
        if (bl && bl2) {
            throw ALREADY_ENABLED_EXCEPTION.create(string2);
        }
        if (!bl && !bl2) {
            throw ALREADY_DISABLED_EXCEPTION.create(string2);
        }
        return resourcePackProfile;
    }

    static interface PackAdder {
        public void apply(List<ResourcePackProfile> var1, ResourcePackProfile var2) throws CommandSyntaxException;
    }
}

