package net.minecraft.server.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.ChatFormat;
import net.minecraft.client.network.packet.CommandTreeS2CPacket;
import net.minecraft.command.CommandException;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Components;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final CommandDispatcher<ServerCommandSource> dispatcher = new CommandDispatcher<>();

	public CommandManager(boolean bl) {
		AdvancementCommand.register(this.dispatcher);
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
		KickCommand.register(this.dispatcher);
		KillCommand.register(this.dispatcher);
		ListCommand.register(this.dispatcher);
		LocateCommand.register(this.dispatcher);
		LootCommand.register(this.dispatcher);
		MessageCommand.register(this.dispatcher);
		ParticleCommand.register(this.dispatcher);
		PlaySoundCommand.register(this.dispatcher);
		PublishCommand.register(this.dispatcher);
		ReloadCommand.register(this.dispatcher);
		RecipeCommand.register(this.dispatcher);
		ReplaceItemCommand.register(this.dispatcher);
		SayCommand.register(this.dispatcher);
		ScheduleCommand.register(this.dispatcher);
		ScoreboardCommand.register(this.dispatcher);
		SeedCommand.register(this.dispatcher);
		SetBlockCommand.register(this.dispatcher);
		SpawnPointCommand.register(this.dispatcher);
		SetWorldSpawnCommand.register(this.dispatcher);
		SpreadPlayersCommand.register(this.dispatcher);
		StopSoundCommand.register(this.dispatcher);
		SummonCommand.register(this.dispatcher);
		TagCommand.register(this.dispatcher);
		TeamCommand.register(this.dispatcher);
		TeammsgCommand.register(this.dispatcher);
		TeleportCommand.register(this.dispatcher);
		TellRawCommand.register(this.dispatcher);
		TimeCommand.register(this.dispatcher);
		TitleCommand.register(this.dispatcher);
		TriggerCommand.register(this.dispatcher);
		WeatherCommand.register(this.dispatcher);
		WorldBorderCommand.register(this.dispatcher);
		if (bl) {
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

		this.dispatcher
			.findAmbiguities(
				(commandNode, commandNode2, commandNode3, collection) -> LOGGER.warn(
						"Ambiguity between arguments {} and {} with inputs: {}", this.dispatcher.getPath(commandNode2), this.dispatcher.getPath(commandNode3), collection
					)
			);
		this.dispatcher.setConsumer((commandContext, blx, i) -> commandContext.getSource().onCommandComplete(commandContext, blx, i));
	}

	public int execute(ServerCommandSource serverCommandSource, String string) {
		StringReader stringReader = new StringReader(string);
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		serverCommandSource.getMinecraftServer().getProfiler().push(string);

		byte var20;
		try {
			return this.dispatcher.execute(stringReader, serverCommandSource);
		} catch (CommandException var13) {
			serverCommandSource.sendError(var13.getMessageComponent());
			return 0;
		} catch (CommandSyntaxException var14) {
			serverCommandSource.sendError(Components.message(var14.getRawMessage()));
			if (var14.getInput() != null && var14.getCursor() >= 0) {
				int i = Math.min(var14.getInput().length(), var14.getCursor());
				Component component = new TextComponent("")
					.applyFormat(ChatFormat.field_1080)
					.modifyStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.field_11745, string)));
				if (i > 10) {
					component.append("...");
				}

				component.append(var14.getInput().substring(Math.max(0, i - 10), i));
				if (i < var14.getInput().length()) {
					Component component2 = new TextComponent(var14.getInput().substring(i)).applyFormat(new ChatFormat[]{ChatFormat.field_1061, ChatFormat.field_1073});
					component.append(component2);
				}

				component.append(new TranslatableComponent("command.context.here").applyFormat(new ChatFormat[]{ChatFormat.field_1061, ChatFormat.field_1056}));
				serverCommandSource.sendError(component);
			}

			return 0;
		} catch (Exception var15) {
			Component component3 = new TextComponent(var15.getMessage() == null ? var15.getClass().getName() : var15.getMessage());
			if (LOGGER.isDebugEnabled()) {
				StackTraceElement[] stackTraceElements = var15.getStackTrace();

				for (int j = 0; j < Math.min(stackTraceElements.length, 3); j++) {
					component3.append("\n\n")
						.append(stackTraceElements[j].getMethodName())
						.append("\n ")
						.append(stackTraceElements[j].getFileName())
						.append(":")
						.append(String.valueOf(stackTraceElements[j].getLineNumber()));
				}
			}

			serverCommandSource.sendError(
				new TranslatableComponent("command.failed").modifyStyle(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.field_11762, component3)))
			);
			var20 = 0;
		} finally {
			serverCommandSource.getMinecraftServer().getProfiler().pop();
		}

		return var20;
	}

	public void sendCommandTree(ServerPlayerEntity serverPlayerEntity) {
		Map<CommandNode<ServerCommandSource>, CommandNode<CommandSource>> map = Maps.<CommandNode<ServerCommandSource>, CommandNode<CommandSource>>newHashMap();
		RootCommandNode<CommandSource> rootCommandNode = new RootCommandNode<>();
		map.put(this.dispatcher.getRoot(), rootCommandNode);
		this.makeTreeForSource(this.dispatcher.getRoot(), rootCommandNode, serverPlayerEntity.getCommandSource(), map);
		serverPlayerEntity.networkHandler.sendPacket(new CommandTreeS2CPacket(rootCommandNode));
	}

	private void makeTreeForSource(
		CommandNode<ServerCommandSource> commandNode,
		CommandNode<CommandSource> commandNode2,
		ServerCommandSource serverCommandSource,
		Map<CommandNode<ServerCommandSource>, CommandNode<CommandSource>> map
	) {
		for (CommandNode<ServerCommandSource> commandNode3 : commandNode.getChildren()) {
			if (commandNode3.canUse(serverCommandSource)) {
				ArgumentBuilder<CommandSource, ?> argumentBuilder = commandNode3.createBuilder();
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
					argumentBuilder.redirect((CommandNode<CommandSource>)map.get(argumentBuilder.getRedirect()));
				}

				CommandNode<CommandSource> commandNode4 = argumentBuilder.build();
				map.put(commandNode3, commandNode4);
				commandNode2.addChild(commandNode4);
				if (!commandNode3.getChildren().isEmpty()) {
					this.makeTreeForSource(commandNode3, commandNode4, serverCommandSource, map);
				}
			}
		}
	}

	public static LiteralArgumentBuilder<ServerCommandSource> literal(String string) {
		return LiteralArgumentBuilder.literal(string);
	}

	public static <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(String string, ArgumentType<T> argumentType) {
		return RequiredArgumentBuilder.argument(string, argumentType);
	}

	public static Predicate<String> getCommandValidator(CommandManager.CommandParser commandParser) {
		return string -> {
			try {
				commandParser.parse(new StringReader(string));
				return true;
			} catch (CommandSyntaxException var3) {
				return false;
			}
		};
	}

	public CommandDispatcher<ServerCommandSource> getDispatcher() {
		return this.dispatcher;
	}

	@FunctionalInterface
	public interface CommandParser {
		void parse(StringReader stringReader) throws CommandSyntaxException;
	}
}
