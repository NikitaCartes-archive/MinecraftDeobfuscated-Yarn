package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.List;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;

public class DatapackCommand {
	private static final DynamicCommandExceptionType UNKNOWN_DATAPACK_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.datapack.unknown", object)
	);
	private static final DynamicCommandExceptionType ALREADY_ENABLED_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.datapack.enable.failed", object)
	);
	private static final DynamicCommandExceptionType ALREADY_DISABLED_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.datapack.disable.failed", object)
	);
	private static final SuggestionProvider<ServerCommandSource> ENABLED_CONTAINERS_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
			commandContext.getSource()
				.getMinecraftServer()
				.getDataPackManager()
				.getEnabledProfiles()
				.stream()
				.map(ResourcePackProfile::getName)
				.map(StringArgumentType::escapeIfRequired),
			suggestionsBuilder
		);
	private static final SuggestionProvider<ServerCommandSource> DISABLED_CONTAINERS_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> CommandSource.suggestMatching(
			commandContext.getSource()
				.getMinecraftServer()
				.getDataPackManager()
				.getDisabledProfiles()
				.stream()
				.map(ResourcePackProfile::getName)
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
											(list, resourcePackProfile) -> resourcePackProfile.getInitialPosition()
													.insert(list, resourcePackProfile, resourcePackProfilex -> resourcePackProfilex, false)
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
															(list, resourcePackProfile) -> list.add(list.indexOf(getPackContainer(commandContext, "existing", false)) + 1, resourcePackProfile)
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
															(list, resourcePackProfile) -> list.add(list.indexOf(getPackContainer(commandContext, "existing", false)), resourcePackProfile)
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
													commandContext.getSource(), getPackContainer(commandContext, "name", true), (list, resourcePackProfile) -> list.add(0, resourcePackProfile)
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

	private static int executeEnable(ServerCommandSource serverCommandSource, ResourcePackProfile resourcePackProfile, DatapackCommand.PackAdder packAdder) throws CommandSyntaxException {
		ResourcePackManager<ResourcePackProfile> resourcePackManager = serverCommandSource.getMinecraftServer().getDataPackManager();
		List<ResourcePackProfile> list = Lists.<ResourcePackProfile>newArrayList(resourcePackManager.getEnabledProfiles());
		packAdder.apply(list, resourcePackProfile);
		resourcePackManager.setEnabledProfiles(list);
		LevelProperties levelProperties = serverCommandSource.getMinecraftServer().getWorld(DimensionType.OVERWORLD).getLevelProperties();
		levelProperties.getEnabledDataPacks().clear();
		resourcePackManager.getEnabledProfiles().forEach(resourcePackProfilex -> levelProperties.getEnabledDataPacks().add(resourcePackProfilex.getName()));
		levelProperties.getDisabledDataPacks().remove(resourcePackProfile.getName());
		serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.enable.success", resourcePackProfile.getInformationText(true)), true);
		serverCommandSource.getMinecraftServer().reload();
		return resourcePackManager.getEnabledProfiles().size();
	}

	private static int executeDisable(ServerCommandSource serverCommandSource, ResourcePackProfile resourcePackProfile) {
		ResourcePackManager<ResourcePackProfile> resourcePackManager = serverCommandSource.getMinecraftServer().getDataPackManager();
		List<ResourcePackProfile> list = Lists.<ResourcePackProfile>newArrayList(resourcePackManager.getEnabledProfiles());
		list.remove(resourcePackProfile);
		resourcePackManager.setEnabledProfiles(list);
		LevelProperties levelProperties = serverCommandSource.getMinecraftServer().getWorld(DimensionType.OVERWORLD).getLevelProperties();
		levelProperties.getEnabledDataPacks().clear();
		resourcePackManager.getEnabledProfiles().forEach(resourcePackProfilex -> levelProperties.getEnabledDataPacks().add(resourcePackProfilex.getName()));
		levelProperties.getDisabledDataPacks().add(resourcePackProfile.getName());
		serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.disable.success", resourcePackProfile.getInformationText(true)), true);
		serverCommandSource.getMinecraftServer().reload();
		return resourcePackManager.getEnabledProfiles().size();
	}

	private static int executeList(ServerCommandSource serverCommandSource) {
		return executeListEnabled(serverCommandSource) + executeListAvailable(serverCommandSource);
	}

	private static int executeListAvailable(ServerCommandSource serverCommandSource) {
		ResourcePackManager<ResourcePackProfile> resourcePackManager = serverCommandSource.getMinecraftServer().getDataPackManager();
		if (resourcePackManager.getDisabledProfiles().isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.list.available.none"), false);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableText(
					"commands.datapack.list.available.success",
					resourcePackManager.getDisabledProfiles().size(),
					Texts.join(resourcePackManager.getDisabledProfiles(), resourcePackProfile -> resourcePackProfile.getInformationText(false))
				),
				false
			);
		}

		return resourcePackManager.getDisabledProfiles().size();
	}

	private static int executeListEnabled(ServerCommandSource serverCommandSource) {
		ResourcePackManager<ResourcePackProfile> resourcePackManager = serverCommandSource.getMinecraftServer().getDataPackManager();
		if (resourcePackManager.getEnabledProfiles().isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.datapack.list.enabled.none"), false);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableText(
					"commands.datapack.list.enabled.success",
					resourcePackManager.getEnabledProfiles().size(),
					Texts.join(resourcePackManager.getEnabledProfiles(), resourcePackProfile -> resourcePackProfile.getInformationText(true))
				),
				false
			);
		}

		return resourcePackManager.getEnabledProfiles().size();
	}

	private static ResourcePackProfile getPackContainer(CommandContext<ServerCommandSource> commandContext, String string, boolean bl) throws CommandSyntaxException {
		String string2 = StringArgumentType.getString(commandContext, string);
		ResourcePackManager<ResourcePackProfile> resourcePackManager = commandContext.getSource().getMinecraftServer().getDataPackManager();
		ResourcePackProfile resourcePackProfile = resourcePackManager.getProfile(string2);
		if (resourcePackProfile == null) {
			throw UNKNOWN_DATAPACK_EXCEPTION.create(string2);
		} else {
			boolean bl2 = resourcePackManager.getEnabledProfiles().contains(resourcePackProfile);
			if (bl && bl2) {
				throw ALREADY_ENABLED_EXCEPTION.create(string2);
			} else if (!bl && !bl2) {
				throw ALREADY_DISABLED_EXCEPTION.create(string2);
			} else {
				return resourcePackProfile;
			}
		}
	}

	interface PackAdder {
		void apply(List<ResourcePackProfile> list, ResourcePackProfile resourcePackProfile) throws CommandSyntaxException;
	}
}
