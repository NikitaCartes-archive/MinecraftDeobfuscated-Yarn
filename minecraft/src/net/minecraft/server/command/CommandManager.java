package net.minecraft.server.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.class_8262;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.dedicated.command.BanCommand;
import net.minecraft.server.dedicated.command.BanIpCommand;
import net.minecraft.server.dedicated.command.BanListCommand;
import net.minecraft.server.dedicated.command.DeOpCommand;
import net.minecraft.server.dedicated.command.OpCommand;
import net.minecraft.server.dedicated.command.PardonCommand;
import net.minecraft.server.dedicated.command.PardonIpCommand;
import net.minecraft.server.dedicated.command.PerfCommand;
import net.minecraft.server.dedicated.command.SaveAllCommand;
import net.minecraft.server.dedicated.command.SaveOffCommand;
import net.minecraft.server.dedicated.command.SaveOnCommand;
import net.minecraft.server.dedicated.command.SetIdleTimeoutCommand;
import net.minecraft.server.dedicated.command.StopCommand;
import net.minecraft.server.dedicated.command.WhitelistCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.jfr.FlightProfiler;
import org.slf4j.Logger;

public class CommandManager {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int field_31837 = 0;
	public static final int field_31838 = 1;
	public static final int field_31839 = 2;
	public static final int field_31840 = 3;
	public static final int field_31841 = 4;
	private final CommandDispatcher<ServerCommandSource> dispatcher = new CommandDispatcher<>();

	public CommandManager(CommandManager.RegistrationEnvironment environment, CommandRegistryAccess commandRegistryAccess) {
		AdvancementCommand.register(this.dispatcher);
		AttributeCommand.register(this.dispatcher, commandRegistryAccess);
		ExecuteCommand.register(this.dispatcher, commandRegistryAccess);
		BossBarCommand.register(this.dispatcher);
		ClearCommand.register(this.dispatcher, commandRegistryAccess);
		CloneCommand.register(this.dispatcher, commandRegistryAccess);
		DamageCommand.register(this.dispatcher, commandRegistryAccess);
		DataCommand.register(this.dispatcher);
		DatapackCommand.register(this.dispatcher);
		DebugCommand.register(this.dispatcher);
		DefaultGameModeCommand.register(this.dispatcher);
		DifficultyCommand.register(this.dispatcher);
		EffectCommand.register(this.dispatcher, commandRegistryAccess);
		MeCommand.register(this.dispatcher);
		EnchantCommand.register(this.dispatcher, commandRegistryAccess);
		ExperienceCommand.register(this.dispatcher);
		FillCommand.register(this.dispatcher, commandRegistryAccess);
		FillBiomeCommand.register(this.dispatcher, commandRegistryAccess);
		ForceLoadCommand.register(this.dispatcher);
		FunctionCommand.register(this.dispatcher);
		GameModeCommand.register(this.dispatcher);
		GameRuleCommand.register(this.dispatcher);
		GiveCommand.register(this.dispatcher, commandRegistryAccess);
		HelpCommand.register(this.dispatcher);
		ItemCommand.register(this.dispatcher, commandRegistryAccess);
		KickCommand.register(this.dispatcher);
		KillCommand.register(this.dispatcher);
		ListCommand.register(this.dispatcher);
		LocateCommand.register(this.dispatcher, commandRegistryAccess);
		LootCommand.register(this.dispatcher, commandRegistryAccess);
		MessageCommand.register(this.dispatcher);
		ParticleCommand.register(this.dispatcher, commandRegistryAccess);
		PlaceCommand.register(this.dispatcher);
		PlaySoundCommand.register(this.dispatcher);
		ReloadCommand.register(this.dispatcher);
		RecipeCommand.register(this.dispatcher);
		RideCommand.register(this.dispatcher);
		SayCommand.register(this.dispatcher);
		ScheduleCommand.register(this.dispatcher);
		ScoreboardCommand.register(this.dispatcher);
		SeedCommand.register(this.dispatcher, environment != CommandManager.RegistrationEnvironment.INTEGRATED);
		SetBlockCommand.register(this.dispatcher, commandRegistryAccess);
		SpawnPointCommand.register(this.dispatcher);
		SetWorldSpawnCommand.register(this.dispatcher);
		SpectateCommand.register(this.dispatcher);
		SpreadPlayersCommand.register(this.dispatcher);
		StopSoundCommand.register(this.dispatcher);
		SummonCommand.register(this.dispatcher, commandRegistryAccess);
		TagCommand.register(this.dispatcher);
		TeamCommand.register(this.dispatcher);
		TeamMsgCommand.register(this.dispatcher);
		TeleportCommand.register(this.dispatcher);
		TellRawCommand.register(this.dispatcher);
		TimeCommand.register(this.dispatcher);
		TitleCommand.register(this.dispatcher);
		TriggerCommand.register(this.dispatcher);
		WeatherCommand.register(this.dispatcher);
		WorldBorderCommand.register(this.dispatcher);
		class_8262.method_50063(this.dispatcher, commandRegistryAccess);
		TransformCommand.register(this.dispatcher, commandRegistryAccess);
		if (FlightProfiler.INSTANCE.isAvailable()) {
			JfrCommand.register(this.dispatcher);
		}

		if (SharedConstants.isDevelopment) {
			TestCommand.register(this.dispatcher);
			SpawnArmorTrimsCommand.register(this.dispatcher);
		}

		if (environment.dedicated) {
			BanIpCommand.register(this.dispatcher);
			BanListCommand.register(this.dispatcher);
			BanCommand.register(this.dispatcher);
			DeOpCommand.register(this.dispatcher);
			OpCommand.register(this.dispatcher);
			PardonCommand.register(this.dispatcher);
			PardonIpCommand.register(this.dispatcher);
			PerfCommand.register(this.dispatcher);
			SaveAllCommand.register(this.dispatcher);
			SaveOffCommand.register(this.dispatcher);
			SaveOnCommand.register(this.dispatcher);
			SetIdleTimeoutCommand.register(this.dispatcher);
			StopCommand.register(this.dispatcher);
			WhitelistCommand.register(this.dispatcher);
		}

		if (environment.integrated) {
			PublishCommand.register(this.dispatcher);
		}

		this.dispatcher.setConsumer((context, success, result) -> context.getSource().onCommandComplete(context, success, result));
	}

	/**
	 * {@return {@code parseResults} with {@code sourceMapper} applied to the
	 * command source}
	 */
	public static <S> ParseResults<S> withCommandSource(ParseResults<S> parseResults, UnaryOperator<S> sourceMapper) {
		CommandContextBuilder<S> commandContextBuilder = parseResults.getContext();
		CommandContextBuilder<S> commandContextBuilder2 = commandContextBuilder.withSource((S)sourceMapper.apply(commandContextBuilder.getSource()));
		return new ParseResults<>(commandContextBuilder2, parseResults.getReader(), parseResults.getExceptions());
	}

	/**
	 * Executes {@code command}. Unlike {@link #execute} the command can be prefixed
	 * with a slash.
	 */
	public int executeWithPrefix(ServerCommandSource source, String command) {
		command = command.startsWith("/") ? command.substring(1) : command;
		return this.execute(this.dispatcher.parse(command, source), command);
	}

	/**
	 * Executes {@code command}. The command cannot be prefixed with a slash.
	 * 
	 * @see #executeWithPrefix(ServerCommandSource, String)
	 */
	public int execute(ParseResults<ServerCommandSource> parseResults, String command) {
		ServerCommandSource serverCommandSource = parseResults.getContext().getSource();
		serverCommandSource.getServer().getProfiler().push((Supplier<String>)(() -> "/" + command));

		byte var20;
		try {
			return this.dispatcher.execute(parseResults);
		} catch (CommandException var13) {
			serverCommandSource.sendError(var13.getTextMessage());
			return 0;
		} catch (CommandSyntaxException var14) {
			serverCommandSource.sendError(Texts.toText(var14.getRawMessage()));
			if (var14.getInput() != null && var14.getCursor() >= 0) {
				int i = Math.min(var14.getInput().length(), var14.getCursor());
				MutableText mutableText = Text.empty()
					.formatted(Formatting.GRAY)
					.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + command)));
				if (i > 10) {
					mutableText.append(ScreenTexts.ELLIPSIS);
				}

				mutableText.append(var14.getInput().substring(Math.max(0, i - 10), i));
				if (i < var14.getInput().length()) {
					Text text = Text.literal(var14.getInput().substring(i)).formatted(Formatting.RED, Formatting.UNDERLINE);
					mutableText.append(text);
				}

				mutableText.append(Text.translatable("command.context.here").formatted(Formatting.RED, Formatting.ITALIC));
				serverCommandSource.sendError(mutableText);
			}

			return 0;
		} catch (Exception var15) {
			MutableText mutableText2 = Text.literal(var15.getMessage() == null ? var15.getClass().getName() : var15.getMessage());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.error("Command exception: /{}", command, var15);
				StackTraceElement[] stackTraceElements = var15.getStackTrace();

				for (int j = 0; j < Math.min(stackTraceElements.length, 3); j++) {
					mutableText2.append("\n\n")
						.append(stackTraceElements[j].getMethodName())
						.append("\n ")
						.append(stackTraceElements[j].getFileName())
						.append(":")
						.append(String.valueOf(stackTraceElements[j].getLineNumber()));
				}
			}

			serverCommandSource.sendError(
				Text.translatable("command.failed").styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, mutableText2)))
			);
			if (SharedConstants.isDevelopment) {
				serverCommandSource.sendError(Text.literal(Util.getInnermostMessage(var15)));
				LOGGER.error("'/{}' threw an exception", command, var15);
			}

			var20 = 0;
		} finally {
			serverCommandSource.getServer().getProfiler().pop();
		}

		return var20;
	}

	public void sendCommandTree(ServerPlayerEntity player) {
		Map<CommandNode<ServerCommandSource>, CommandNode<CommandSource>> map = Maps.<CommandNode<ServerCommandSource>, CommandNode<CommandSource>>newHashMap();
		RootCommandNode<CommandSource> rootCommandNode = new RootCommandNode<>();
		map.put(this.dispatcher.getRoot(), rootCommandNode);
		this.makeTreeForSource(this.dispatcher.getRoot(), rootCommandNode, player.getCommandSource(), map);
		player.networkHandler.sendPacket(new CommandTreeS2CPacket(rootCommandNode));
	}

	private void makeTreeForSource(
		CommandNode<ServerCommandSource> tree,
		CommandNode<CommandSource> result,
		ServerCommandSource source,
		Map<CommandNode<ServerCommandSource>, CommandNode<CommandSource>> resultNodes
	) {
		for (CommandNode<ServerCommandSource> commandNode : tree.getChildren()) {
			if (commandNode.canUse(source)) {
				ArgumentBuilder<CommandSource, ?> argumentBuilder = commandNode.createBuilder();
				argumentBuilder.requires(sourcex -> true);
				if (argumentBuilder.getCommand() != null) {
					argumentBuilder.executes(context -> 0);
				}

				if (argumentBuilder instanceof RequiredArgumentBuilder) {
					RequiredArgumentBuilder<CommandSource, ?> requiredArgumentBuilder = (RequiredArgumentBuilder<CommandSource, ?>)argumentBuilder;
					if (requiredArgumentBuilder.getSuggestionsProvider() != null) {
						requiredArgumentBuilder.suggests(SuggestionProviders.getLocalProvider(requiredArgumentBuilder.getSuggestionsProvider()));
					}
				}

				if (argumentBuilder.getRedirect() != null) {
					argumentBuilder.redirect((CommandNode<CommandSource>)resultNodes.get(argumentBuilder.getRedirect()));
				}

				CommandNode<CommandSource> commandNode2 = argumentBuilder.build();
				resultNodes.put(commandNode, commandNode2);
				result.addChild(commandNode2);
				if (!commandNode.getChildren().isEmpty()) {
					this.makeTreeForSource(commandNode, commandNode2, source, resultNodes);
				}
			}
		}
	}

	public static LiteralArgumentBuilder<ServerCommandSource> literal(String literal) {
		return LiteralArgumentBuilder.literal(literal);
	}

	public static <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}

	public static Predicate<String> getCommandValidator(CommandManager.CommandParser parser) {
		return string -> {
			try {
				parser.parse(new StringReader(string));
				return true;
			} catch (CommandSyntaxException var3) {
				return false;
			}
		};
	}

	public CommandDispatcher<ServerCommandSource> getDispatcher() {
		return this.dispatcher;
	}

	@Nullable
	public static <S> CommandSyntaxException getException(ParseResults<S> parse) {
		if (!parse.getReader().canRead()) {
			return null;
		} else if (parse.getExceptions().size() == 1) {
			return (CommandSyntaxException)parse.getExceptions().values().iterator().next();
		} else {
			return parse.getContext().getRange().isEmpty()
				? CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parse.getReader())
				: CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parse.getReader());
		}
	}

	public static CommandRegistryAccess createRegistryAccess(RegistryWrapper.WrapperLookup registryLookup) {
		return new CommandRegistryAccess() {
			@Override
			public <T> RegistryWrapper<T> createWrapper(RegistryKey<? extends Registry<T>> registryRef) {
				final RegistryWrapper.Impl<T> impl = registryLookup.getWrapperOrThrow(registryRef);
				return new RegistryWrapper.Delegating<T>(impl) {
					@Override
					public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
						return Optional.of(this.getOrThrow(tag));
					}

					@Override
					public RegistryEntryList.Named<T> getOrThrow(TagKey<T> tag) {
						Optional<RegistryEntryList.Named<T>> optional = impl.getOptional(tag);
						return (RegistryEntryList.Named<T>)optional.orElseGet(() -> RegistryEntryList.of(impl, tag));
					}
				};
			}
		};
	}

	public static void checkMissing() {
		CommandRegistryAccess commandRegistryAccess = createRegistryAccess(BuiltinRegistries.createWrapperLookup());
		CommandDispatcher<ServerCommandSource> commandDispatcher = new CommandManager(CommandManager.RegistrationEnvironment.ALL, commandRegistryAccess)
			.getDispatcher();
		RootCommandNode<ServerCommandSource> rootCommandNode = commandDispatcher.getRoot();
		commandDispatcher.findAmbiguities(
			(parent, child, sibling, inputs) -> LOGGER.warn(
					"Ambiguity between arguments {} and {} with inputs: {}", commandDispatcher.getPath(child), commandDispatcher.getPath(sibling), inputs
				)
		);
		Set<ArgumentType<?>> set = ArgumentHelper.collectUsedArgumentTypes(rootCommandNode);
		Set<ArgumentType<?>> set2 = (Set<ArgumentType<?>>)set.stream().filter(type -> !ArgumentTypes.has(type.getClass())).collect(Collectors.toSet());
		if (!set2.isEmpty()) {
			LOGGER.warn("Missing type registration for following arguments:\n {}", set2.stream().map(type -> "\t" + type).collect(Collectors.joining(",\n")));
			throw new IllegalStateException("Unregistered argument types");
		}
	}

	@FunctionalInterface
	public interface CommandParser {
		void parse(StringReader reader) throws CommandSyntaxException;
	}

	/**
	 * Describes the environment in which commands are registered.
	 */
	public static enum RegistrationEnvironment {
		ALL(true, true),
		DEDICATED(false, true),
		INTEGRATED(true, false);

		final boolean integrated;
		final boolean dedicated;

		private RegistrationEnvironment(boolean integrated, boolean dedicated) {
			this.integrated = integrated;
			this.dedicated = dedicated;
		}
	}
}
