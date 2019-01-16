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
	private static final DynamicCommandExceptionType field_13503 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.datapack.unknown", object)
	);
	private static final DynamicCommandExceptionType field_13504 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.datapack.enable.failed", object)
	);
	private static final DynamicCommandExceptionType field_13505 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("commands.datapack.disable.failed", object)
	);
	private static final SuggestionProvider<ServerCommandSource> field_13506 = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
			commandContext.getSource()
				.getMinecraftServer()
				.method_3836()
				.getEnabledContainers()
				.stream()
				.map(ResourcePackContainer::getName)
				.map(StringArgumentType::escapeIfRequired),
			suggestionsBuilder
		);
	private static final SuggestionProvider<ServerCommandSource> NAME_SUGGESTION = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
			commandContext.getSource()
				.getMinecraftServer()
				.method_3836()
				.getDisabledContainers()
				.stream()
				.map(ResourcePackContainer::getName)
				.map(StringArgumentType::escapeIfRequired),
			suggestionsBuilder
		);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			ServerCommandManager.literal("datapack")
				.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
				.then(
					ServerCommandManager.literal("enable")
						.then(
							ServerCommandManager.argument("name", StringArgumentType.string())
								.suggests(NAME_SUGGESTION)
								.executes(
									commandContext -> method_13114(
											commandContext.getSource(),
											getPackContainer(commandContext, "name", true),
											(list, resourcePackContainer) -> resourcePackContainer.getSortingDirection()
													.locate(list, resourcePackContainer, resourcePackContainerx -> resourcePackContainerx, false)
										)
								)
								.then(
									ServerCommandManager.literal("after")
										.then(
											ServerCommandManager.argument("existing", StringArgumentType.string())
												.suggests(field_13506)
												.executes(
													commandContext -> method_13114(
															commandContext.getSource(),
															getPackContainer(commandContext, "name", true),
															(list, resourcePackContainer) -> list.add(list.indexOf(getPackContainer(commandContext, "existing", false)) + 1, resourcePackContainer)
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("before")
										.then(
											ServerCommandManager.argument("existing", StringArgumentType.string())
												.suggests(field_13506)
												.executes(
													commandContext -> method_13114(
															commandContext.getSource(),
															getPackContainer(commandContext, "name", true),
															(list, resourcePackContainer) -> list.add(list.indexOf(getPackContainer(commandContext, "existing", false)), resourcePackContainer)
														)
												)
										)
								)
								.then(
									ServerCommandManager.literal("last")
										.executes(commandContext -> method_13114(commandContext.getSource(), getPackContainer(commandContext, "name", true), List::add))
								)
								.then(
									ServerCommandManager.literal("first")
										.executes(
											commandContext -> method_13114(
													commandContext.getSource(), getPackContainer(commandContext, "name", true), (list, resourcePackContainer) -> list.add(0, resourcePackContainer)
												)
										)
								)
						)
				)
				.then(
					ServerCommandManager.literal("disable")
						.then(
							ServerCommandManager.argument("name", StringArgumentType.string())
								.suggests(field_13506)
								.executes(commandContext -> method_13140(commandContext.getSource(), getPackContainer(commandContext, "name", false)))
						)
				)
				.then(
					ServerCommandManager.literal("list")
						.executes(commandContext -> executeList(commandContext.getSource()))
						.then(ServerCommandManager.literal("available").executes(commandContext -> executeAvailable(commandContext.getSource())))
						.then(ServerCommandManager.literal("enabled").executes(commandContext -> executeEnabled(commandContext.getSource())))
				)
		);
	}

	private static int method_13114(ServerCommandSource serverCommandSource, ResourcePackContainer resourcePackContainer, DatapackCommand.class_3028 arg) throws CommandSyntaxException {
		ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = serverCommandSource.getMinecraftServer().method_3836();
		List<ResourcePackContainer> list = Lists.<ResourcePackContainer>newArrayList(resourcePackContainerManager.getEnabledContainers());
		arg.apply(list, resourcePackContainer);
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

	private static int method_13140(ServerCommandSource serverCommandSource, ResourcePackContainer resourcePackContainer) {
		ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = serverCommandSource.getMinecraftServer().method_3836();
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
		return executeEnabled(serverCommandSource) + executeAvailable(serverCommandSource);
	}

	private static int executeAvailable(ServerCommandSource serverCommandSource) {
		ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = serverCommandSource.getMinecraftServer().method_3836();
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

	private static int executeEnabled(ServerCommandSource serverCommandSource) {
		ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = serverCommandSource.getMinecraftServer().method_3836();
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
		ResourcePackContainerManager<ResourcePackContainer> resourcePackContainerManager = commandContext.getSource().getMinecraftServer().method_3836();
		ResourcePackContainer resourcePackContainer = resourcePackContainerManager.getContainer(string2);
		if (resourcePackContainer == null) {
			throw field_13503.create(string2);
		} else {
			boolean bl2 = resourcePackContainerManager.getEnabledContainers().contains(resourcePackContainer);
			if (bl && bl2) {
				throw field_13504.create(string2);
			} else if (!bl && !bl2) {
				throw field_13505.create(string2);
			} else {
				return resourcePackContainer;
			}
		}
	}

	interface class_3028 {
		void apply(List<ResourcePackContainer> list, ResourcePackContainer resourcePackContainer) throws CommandSyntaxException;
	}
}
