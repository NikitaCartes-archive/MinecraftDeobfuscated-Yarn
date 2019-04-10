package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.List;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackContainerManager;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;

public class DatapackCommand {
	private static final DynamicCommandExceptionType UNKNOWN_DATAPACK_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.datapack.unknown", object)
	);
	private static final DynamicCommandExceptionType ALREADY_ENABLED_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.datapack.enable.failed", object)
	);
	private static final DynamicCommandExceptionType ALREADY_DISABLED_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.datapack.disable.failed", object)
	);
	private static final SuggestionProvider<ServerCommandSource> ENABLED_CONTAINERS_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
			commandContext.getSource()
				.getMinecraftServer()
				.getResourcePackContainerManager()
				.getEnabledContainers()
				.stream()
				.map(ResourcePackContainer::getName)
				.map(StringArgumentType::escapeIfRequired),
			suggestionsBuilder
		);
	private static final SuggestionProvider<ServerCommandSource> DISABLED_CONTAINERS_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
			commandContext.getSource()
				.getMinecraftServer()
				.getResourcePackContainerManager()
				.getDisabledContainers()
				.stream()
				.map(ResourcePackContainer::getName)
				.map(StringArgumentType::escapeIfRequired),
			suggestionsBuilder
		);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			CommandManager.literal("datapack")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					CommandManager.literal("enable")
						.then(
							CommandManager.argument("name", StringArgumentType.string())
								.suggests(DISABLED_CONTAINERS_SUGGESTION_PROVIDER)
								.executes(
									commandContext -> executeEnable(
											commandContext.getSource(),
											getPackContainer(commandContext, "name", true),
											(list, resourcePackContainer) -> resourcePackContainer.getSortingDirection()
													.locate(list, resourcePackContainer, resourcePackContainerx -> resourcePackContainerx, false)
										)
								)
								.then(
									CommandManager.literal("after")
										.then(
											CommandManager.argument("existing", StringArgumentType.string())
												.suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeEnable(
															commandContext.getSource(),
															getPackContainer(commandContext, "name", true),
															(list, resourcePackContainer) -> list.add(list.indexOf(getPackContainer(commandContext, "existing", false)) + 1, resourcePackContainer)
														)
												)
										)
								)
								.then(
									CommandManager.literal("before")
										.then(
											CommandManager.argument("existing", StringArgumentType.string())
												.suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeEnable(
															commandContext.getSource(),
															getPackContainer(commandContext, "name", true),
															(list, resourcePackContainer) -> list.add(list.indexOf(getPackContainer(commandContext, "existing", false)), resourcePackContainer)
														)
												)
										)
								)
								.then(
									CommandManager.literal("last")
										.executes(commandContext -> executeEnable(commandContext.getSource(), getPackContainer(commandContext, "name", true), List::add))
								)
								.then(
									CommandManager.literal("first")
										.executes(
											commandContext -> executeEnable(
													commandContext.getSource(), getPackContainer(commandContext, "name", true), (list, resourcePackContainer) -> list.add(0, resourcePackContainer)
												)
										)
								)
						)
				)
				.then(
					CommandManager.literal("disable")
						.then(
							CommandManager.argument("name", StringArgumentType.string())
								.suggests(ENABLED_CONTAINERS_SUGGESTION_PROVIDER)
								.executes(commandContext -> executeDisable(commandContext.getSource(), getPackContainer(commandContext, "name", false)))
						)
				)
				.then(
					CommandManager.literal("list")
						.executes(commandContext -> executeList(commandContext.getSource()))
						.then(CommandManager.literal("available").executes(commandContext -> executeListAvailable(commandContext.getSource())))
						.then(CommandManager.literal("enabled").executes(commandContext -> executeListEnabled(commandContext.getSource())))
				)
		);
	}

	private static int executeEnable(ServerCommandSource serverCommandSource, ResourcePackContainer resourcePackContainer, DatapackCommand.PackAdder packAdder) throws CommandSyntaxException {
		ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = serverCommandSource.getMinecraftServer().getResourcePackContainerManager();
		List<ResourcePackContainer> list = Lists.<ResourcePackContainer>newArrayList(resourcePackContainerManager.getEnabledContainers());
		packAdder.apply(list, resourcePackContainer);
		resourcePackContainerManager.setEnabled(list);
		LevelProperties levelProperties = serverCommandSource.getMinecraftServer().getWorld(DimensionType.field_13072).getLevelProperties();
		levelProperties.getEnabledDataPacks().clear();
		resourcePackContainerManager.getEnabledContainers()
			.forEach(resourcePackContainerx -> levelProperties.getEnabledDataPacks().add(resourcePackContainerx.getName()));
		levelProperties.getDisabledDataPacks().remove(resourcePackContainer.getName());
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.datapack.enable.success", resourcePackContainer.getInformationText(true)), true);
		serverCommandSource.getMinecraftServer().reload();
		return resourcePackContainerManager.getEnabledContainers().size();
	}

	private static int executeDisable(ServerCommandSource serverCommandSource, ResourcePackContainer resourcePackContainer) {
		ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = serverCommandSource.getMinecraftServer().getResourcePackContainerManager();
		List<ResourcePackContainer> list = Lists.<ResourcePackContainer>newArrayList(resourcePackContainerManager.getEnabledContainers());
		list.remove(resourcePackContainer);
		resourcePackContainerManager.setEnabled(list);
		LevelProperties levelProperties = serverCommandSource.getMinecraftServer().getWorld(DimensionType.field_13072).getLevelProperties();
		levelProperties.getEnabledDataPacks().clear();
		resourcePackContainerManager.getEnabledContainers()
			.forEach(resourcePackContainerx -> levelProperties.getEnabledDataPacks().add(resourcePackContainerx.getName()));
		levelProperties.getDisabledDataPacks().add(resourcePackContainer.getName());
		serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.datapack.disable.success", resourcePackContainer.getInformationText(true)), true);
		serverCommandSource.getMinecraftServer().reload();
		return resourcePackContainerManager.getEnabledContainers().size();
	}

	private static int executeList(ServerCommandSource serverCommandSource) {
		return executeListEnabled(serverCommandSource) + executeListAvailable(serverCommandSource);
	}

	private static int executeListAvailable(ServerCommandSource serverCommandSource) {
		ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = serverCommandSource.getMinecraftServer().getResourcePackContainerManager();
		if (resourcePackContainerManager.getDisabledContainers().isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.datapack.list.available.none"), false);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent(
					"commands.datapack.list.available.success",
					resourcePackContainerManager.getDisabledContainers().size(),
					TextFormatter.join(resourcePackContainerManager.getDisabledContainers(), resourcePackContainer -> resourcePackContainer.getInformationText(false))
				),
				false
			);
		}

		return resourcePackContainerManager.getDisabledContainers().size();
	}

	private static int executeListEnabled(ServerCommandSource serverCommandSource) {
		ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = serverCommandSource.getMinecraftServer().getResourcePackContainerManager();
		if (resourcePackContainerManager.getEnabledContainers().isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableTextComponent("commands.datapack.list.enabled.none"), false);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableTextComponent(
					"commands.datapack.list.enabled.success",
					resourcePackContainerManager.getEnabledContainers().size(),
					TextFormatter.join(resourcePackContainerManager.getEnabledContainers(), resourcePackContainer -> resourcePackContainer.getInformationText(true))
				),
				false
			);
		}

		return resourcePackContainerManager.getEnabledContainers().size();
	}

	private static ResourcePackContainer getPackContainer(CommandContext<ServerCommandSource> commandContext, String string, boolean bl) throws CommandSyntaxException {
		String string2 = StringArgumentType.getString(commandContext, string);
		ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = commandContext.getSource()
			.getMinecraftServer()
			.getResourcePackContainerManager();
		ResourcePackContainer resourcePackContainer = resourcePackContainerManager.getContainer(string2);
		if (resourcePackContainer == null) {
			throw UNKNOWN_DATAPACK_EXCEPTION.create(string2);
		} else {
			boolean bl2 = resourcePackContainerManager.getEnabledContainers().contains(resourcePackContainer);
			if (bl && bl2) {
				throw ALREADY_ENABLED_EXCEPTION.create(string2);
			} else if (!bl && !bl2) {
				throw ALREADY_DISABLED_EXCEPTION.create(string2);
			} else {
				return resourcePackContainer;
			}
		}
	}

	interface PackAdder {
		void apply(List<ResourcePackContainer> list, ResourcePackContainer resourcePackContainer) throws CommandSyntaxException;
	}
}
