package net.minecraft.server.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.server.dedicated.command.BanCommand;
import net.minecraft.server.dedicated.command.BanIpCommand;
import net.minecraft.server.dedicated.command.BanListCommand;
import net.minecraft.server.dedicated.command.DeOpCommand;
import net.minecraft.server.dedicated.command.OpCommand;
import net.minecraft.server.dedicated.command.PardonCommand;
import net.minecraft.server.dedicated.command.PardonIpCommand;
import net.minecraft.server.dedicated.command.SaveAllCommand;
import net.minecraft.server.dedicated.command.SaveOffCommand;
import net.minecraft.server.dedicated.command.SaveOnCommand;
import net.minecraft.server.dedicated.command.SetIdleTimeoutCommand;
import net.minecraft.server.dedicated.command.StopCommand;
import net.minecraft.server.dedicated.command.WhitelistCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandManager {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final int field_31837 = 0;
	public static final int field_31838 = 1;
	public static final int field_31839 = 2;
	public static final int field_31840 = 3;
	public static final int field_31841 = 4;
	private final CommandDispatcher<ServerCommandSource> dispatcher = new CommandDispatcher<>();

	public CommandManager(CommandManager.RegistrationEnvironment environment) {
		AdvancementCommand.register(this.dispatcher);
		AttributeCommand.register(this.dispatcher);
		ExecuteCommand.register(this.dispatcher);
		BossBarCommand.register(this.dispatcher);
		ClearCommand.register(this.dispatcher);
		CloneCommand.register(this.dispatcher);
		DataCommand.register(this.dispatcher);
		DatapackCommand.register(this.dispatcher);
		DebugCommand.register(this.dispatcher);
		DefaultGameModeCommand.register(this.dispatcher);
		DifficultyCommand.register(this.dispatcher);
		EffectCommand.register(this.dispatcher);
		MeCommand.register(this.dispatcher);
		EnchantCommand.register(this.dispatcher);
		ExperienceCommand.register(this.dispatcher);
		FillCommand.register(this.dispatcher);
		ForceLoadCommand.register(this.dispatcher);
		FunctionCommand.register(this.dispatcher);
		GameModeCommand.register(this.dispatcher);
		GameRuleCommand.register(this.dispatcher);
		GiveCommand.register(this.dispatcher);
		HelpCommand.register(this.dispatcher);
		ItemCommand.register(this.dispatcher);
		KickCommand.register(this.dispatcher);
		KillCommand.register(this.dispatcher);
		ListCommand.register(this.dispatcher);
		LocateCommand.register(this.dispatcher);
		LocateBiomeCommand.register(this.dispatcher);
		LootCommand.register(this.dispatcher);
		MessageCommand.register(this.dispatcher);
		ParticleCommand.register(this.dispatcher);
		PlaySoundCommand.register(this.dispatcher);
		ReloadCommand.register(this.dispatcher);
		RecipeCommand.register(this.dispatcher);
		SayCommand.register(this.dispatcher);
		ScheduleCommand.register(this.dispatcher);
		ScoreboardCommand.register(this.dispatcher);
		SeedCommand.register(this.dispatcher, environment != CommandManager.RegistrationEnvironment.INTEGRATED);
		SetBlockCommand.register(this.dispatcher);
		SpawnPointCommand.register(this.dispatcher);
		SetWorldSpawnCommand.register(this.dispatcher);
		SpectateCommand.register(this.dispatcher);
		SpreadPlayersCommand.register(this.dispatcher);
		StopSoundCommand.register(this.dispatcher);
		SummonCommand.register(this.dispatcher);
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
		if (SharedConstants.isDevelopment) {
			TestCommand.register(this.dispatcher);
		}

		if (environment.dedicated) {
			BanIpCommand.register(this.dispatcher);
			BanListCommand.register(this.dispatcher);
			BanCommand.register(this.dispatcher);
			DeOpCommand.register(this.dispatcher);
			OpCommand.register(this.dispatcher);
			PardonCommand.register(this.dispatcher);
			PardonIpCommand.register(this.dispatcher);
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

		this.dispatcher
			.findAmbiguities(
				(commandNode, commandNode2, commandNode3, collection) -> LOGGER.warn(
						"Ambiguity between arguments {} and {} with inputs: {}", this.dispatcher.getPath(commandNode2), this.dispatcher.getPath(commandNode3), collection
					)
			);
		this.dispatcher.setConsumer((commandContext, bl, i) -> commandContext.getSource().onCommandComplete(commandContext, bl, i));
	}

	public int execute(ServerCommandSource commandSource, String command) {
		StringReader stringReader = new StringReader(command);
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		commandSource.getMinecraftServer().getProfiler().push(command);

		byte var20;
		try {
			return this.dispatcher.execute(stringReader, commandSource);
		} catch (CommandException var13) {
			commandSource.sendError(var13.getTextMessage());
			return 0;
		} catch (CommandSyntaxException var14) {
			commandSource.sendError(Texts.toText(var14.getRawMessage()));
			if (var14.getInput() != null && var14.getCursor() >= 0) {
				int i = Math.min(var14.getInput().length(), var14.getCursor());
				MutableText mutableText = new LiteralText("")
					.formatted(Formatting.GRAY)
					.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
				if (i > 10) {
					mutableText.append("...");
				}

				mutableText.append(var14.getInput().substring(Math.max(0, i - 10), i));
				if (i < var14.getInput().length()) {
					Text text = new LiteralText(var14.getInput().substring(i)).formatted(new Formatting[]{Formatting.RED, Formatting.UNDERLINE});
					mutableText.append(text);
				}

				mutableText.append(new TranslatableText("command.context.here").formatted(new Formatting[]{Formatting.RED, Formatting.ITALIC}));
				commandSource.sendError(mutableText);
			}

			return 0;
		} catch (Exception var15) {
			MutableText mutableText2 = new LiteralText(var15.getMessage() == null ? var15.getClass().getName() : var15.getMessage());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.error("Command exception: {}", command, var15);
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

			commandSource.sendError(
				new TranslatableText("command.failed").styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, mutableText2)))
			);
			if (SharedConstants.isDevelopment) {
				commandSource.sendError(new LiteralText(Util.getInnermostMessage(var15)));
				LOGGER.error("'{}' threw an exception", command, var15);
			}

			var20 = 0;
		} finally {
			commandSource.getMinecraftServer().getProfiler().pop();
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
				argumentBuilder.requires(commandSource -> true);
				if (argumentBuilder.getCommand() != null) {
					argumentBuilder.executes(commandContext -> 0);
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

	public static void checkMissing() {
		RootCommandNode<ServerCommandSource> rootCommandNode = new CommandManager(CommandManager.RegistrationEnvironment.ALL).getDispatcher().getRoot();
		Set<ArgumentType<?>> set = ArgumentTypes.getAllArgumentTypes(rootCommandNode);
		Set<ArgumentType<?>> set2 = (Set<ArgumentType<?>>)set.stream().filter(argumentType -> !ArgumentTypes.hasClass(argumentType)).collect(Collectors.toSet());
		if (!set2.isEmpty()) {
			LOGGER.warn(
				"Missing type registration for following arguments:\n {}", set2.stream().map(argumentType -> "\t" + argumentType).collect(Collectors.joining(",\n"))
			);
			throw new IllegalStateException("Unregistered argument types");
		}
	}

	@FunctionalInterface
	public interface CommandParser {
		void parse(StringReader stringReader) throws CommandSyntaxException;
	}

	/**
	 * Describes the environment in which commands are registered.
	 */
	public static enum RegistrationEnvironment {
		ALL(true, true),
		DEDICATED(false, true),
		INTEGRATED(true, false);

		private final boolean integrated;
		private final boolean dedicated;

		private RegistrationEnvironment(boolean integrated, boolean dedicated) {
			this.integrated = integrated;
			this.dedicated = dedicated;
		}
	}
}
