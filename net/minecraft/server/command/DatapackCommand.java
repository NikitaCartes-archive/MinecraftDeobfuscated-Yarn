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
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackContainerManager;
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
    private static final SuggestionProvider<ServerCommandSource> ENABLED_CONTAINERS_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getDataPackContainerManager().getEnabledContainers().stream().map(ResourcePackContainer::getName).map(StringArgumentType::escapeIfRequired), suggestionsBuilder);
    private static final SuggestionProvider<ServerCommandSource> DISABLED_CONTAINERS_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(((ServerCommandSource)commandContext.getSource()).getMinecraftServer().getDataPackContainerManager().getDisabledContainers().stream().map(ResourcePackContainer::getName).map(StringArgumentType::escapeIfRequired), suggestionsBuilder);

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("datapack").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))).then(CommandManager.literal("enable").then((ArgumentBuilder<ServerCommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("name", StringArgumentType.string()).suggests(DISABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), (list, resourcePackContainer2) -> resourcePackContainer2.getInitialPosition().insert(list, resourcePackContainer2, resourcePackContainer -> resourcePackContainer, false)))).then(CommandManager.literal("after").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("existing", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), (list, resourcePackContainer) -> list.add(list.indexOf(DatapackCommand.getPackContainer(commandContext, "existing", false)) + 1, resourcePackContainer)))))).then(CommandManager.literal("before").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("existing", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), (list, resourcePackContainer) -> list.add(list.indexOf(DatapackCommand.getPackContainer(commandContext, "existing", false)), resourcePackContainer)))))).then(CommandManager.literal("last").executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), List::add)))).then(CommandManager.literal("first").executes(commandContext -> DatapackCommand.executeEnable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", true), (list, resourcePackContainer) -> list.add(0, resourcePackContainer))))))).then(CommandManager.literal("disable").then((ArgumentBuilder<ServerCommandSource, ?>)CommandManager.argument("name", StringArgumentType.string()).suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER).executes(commandContext -> DatapackCommand.executeDisable((ServerCommandSource)commandContext.getSource(), DatapackCommand.getPackContainer(commandContext, "name", false)))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("list").executes(commandContext -> DatapackCommand.executeList((ServerCommandSource)commandContext.getSource()))).then(CommandManager.literal("available").executes(commandContext -> DatapackCommand.executeListAvailable((ServerCommandSource)commandContext.getSource())))).then(CommandManager.literal("enabled").executes(commandContext -> DatapackCommand.executeListEnabled((ServerCommandSource)commandContext.getSource())))));
    }

    private static int executeEnable(ServerCommandSource serverCommandSource, ResourcePackContainer resourcePackContainer2, PackAdder packAdder) throws CommandSyntaxException {
        ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = serverCommandSource.getMinecraftServer().getDataPackContainerManager();
        ArrayList<ResourcePackContainer> list = Lists.newArrayList(resourcePackContainerManager.getEnabledContainers());
        packAdder.apply(list, resourcePackContainer2);
        resourcePackContainerManager.setEnabled(list);
        LevelProperties levelProperties = serverCommandSource.getMinecraftServer().getWorld(DimensionType.OVERWORLD).getLevelProperties();
        levelProperties.getEnabledDataPacks().clear();
        resourcePackContainerManager.getEnabledContainers().forEach(resourcePackContainer -> levelProperties.getEnabledDataPacks().add(resourcePackContainer.getName()));
        levelProperties.getDisabledDataPacks().remove(resourcePackContainer2.getName());
        serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.enable.success", resourcePackContainer2.getInformationText(true)), true);
        serverCommandSource.getMinecraftServer().reload();
        return resourcePackContainerManager.getEnabledContainers().size();
    }

    private static int executeDisable(ServerCommandSource serverCommandSource, ResourcePackContainer resourcePackContainer2) {
        ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = serverCommandSource.getMinecraftServer().getDataPackContainerManager();
        ArrayList<ResourcePackContainer> list = Lists.newArrayList(resourcePackContainerManager.getEnabledContainers());
        list.remove(resourcePackContainer2);
        resourcePackContainerManager.setEnabled(list);
        LevelProperties levelProperties = serverCommandSource.getMinecraftServer().getWorld(DimensionType.OVERWORLD).getLevelProperties();
        levelProperties.getEnabledDataPacks().clear();
        resourcePackContainerManager.getEnabledContainers().forEach(resourcePackContainer -> levelProperties.getEnabledDataPacks().add(resourcePackContainer.getName()));
        levelProperties.getDisabledDataPacks().add(resourcePackContainer2.getName());
        serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.disable.success", resourcePackContainer2.getInformationText(true)), true);
        serverCommandSource.getMinecraftServer().reload();
        return resourcePackContainerManager.getEnabledContainers().size();
    }

    private static int executeList(ServerCommandSource serverCommandSource) {
        return DatapackCommand.executeListEnabled(serverCommandSource) + DatapackCommand.executeListAvailable(serverCommandSource);
    }

    private static int executeListAvailable(ServerCommandSource serverCommandSource) {
        ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = serverCommandSource.getMinecraftServer().getDataPackContainerManager();
        if (resourcePackContainerManager.getDisabledContainers().isEmpty()) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.list.available.none", new Object[0]), false);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.list.available.success", resourcePackContainerManager.getDisabledContainers().size(), Texts.join(resourcePackContainerManager.getDisabledContainers(), resourcePackContainer -> resourcePackContainer.getInformationText(false))), false);
        }
        return resourcePackContainerManager.getDisabledContainers().size();
    }

    private static int executeListEnabled(ServerCommandSource serverCommandSource) {
        ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = serverCommandSource.getMinecraftServer().getDataPackContainerManager();
        if (resourcePackContainerManager.getEnabledContainers().isEmpty()) {
            serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.list.enabled.none", new Object[0]), false);
        } else {
            serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.list.enabled.success", resourcePackContainerManager.getEnabledContainers().size(), Texts.join(resourcePackContainerManager.getEnabledContainers(), resourcePackContainer -> resourcePackContainer.getInformationText(true))), false);
        }
        return resourcePackContainerManager.getEnabledContainers().size();
    }

    private static ResourcePackContainer getPackContainer(CommandContext<ServerCommandSource> commandContext, String string, boolean bl) throws CommandSyntaxException {
        String string2 = StringArgumentType.getString(commandContext, string);
        ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = commandContext.getSource().getMinecraftServer().getDataPackContainerManager();
        ResourcePackContainer resourcePackContainer = resourcePackContainerManager.getContainer(string2);
        if (resourcePackContainer == null) {
            throw UNKNOWN_DATAPACK_EXCEPTION.create(string2);
        }
        boolean bl2 = resourcePackContainerManager.getEnabledContainers().contains(resourcePackContainer);
        if (bl && bl2) {
            throw ALREADY_ENABLED_EXCEPTION.create(string2);
        }
        if (!bl && !bl2) {
            throw ALREADY_DISABLED_EXCEPTION.create(string2);
        }
        return resourcePackContainer;
    }

    static interface PackAdder {
        public void apply(List<ResourcePackContainer> var1, ResourcePackContainer var2) throws CommandSyntaxException;
    }
}

