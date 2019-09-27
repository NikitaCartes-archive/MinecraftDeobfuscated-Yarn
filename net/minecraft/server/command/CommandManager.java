/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.SharedConstants;
import net.minecraft.client.network.packet.CommandTreeS2CPacket;
import net.minecraft.command.CommandException;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.AdvancementCommand;
import net.minecraft.server.command.BossBarCommand;
import net.minecraft.server.command.ClearCommand;
import net.minecraft.server.command.CloneCommand;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.DatapackCommand;
import net.minecraft.server.command.DebugCommand;
import net.minecraft.server.command.DefaultGameModeCommand;
import net.minecraft.server.command.DifficultyCommand;
import net.minecraft.server.command.EffectCommand;
import net.minecraft.server.command.EnchantCommand;
import net.minecraft.server.command.ExecuteCommand;
import net.minecraft.server.command.ExperienceCommand;
import net.minecraft.server.command.FillCommand;
import net.minecraft.server.command.ForceLoadCommand;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.GameModeCommand;
import net.minecraft.server.command.GameRuleCommand;
import net.minecraft.server.command.GiveCommand;
import net.minecraft.server.command.HelpCommand;
import net.minecraft.server.command.KickCommand;
import net.minecraft.server.command.KillCommand;
import net.minecraft.server.command.ListCommand;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.command.LootCommand;
import net.minecraft.server.command.MeCommand;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ParticleCommand;
import net.minecraft.server.command.PlaySoundCommand;
import net.minecraft.server.command.PublishCommand;
import net.minecraft.server.command.RecipeCommand;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.command.ReplaceItemCommand;
import net.minecraft.server.command.SayCommand;
import net.minecraft.server.command.ScheduleCommand;
import net.minecraft.server.command.ScoreboardCommand;
import net.minecraft.server.command.SeedCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.SetBlockCommand;
import net.minecraft.server.command.SetWorldSpawnCommand;
import net.minecraft.server.command.SpawnPointCommand;
import net.minecraft.server.command.SpreadPlayersCommand;
import net.minecraft.server.command.StopSoundCommand;
import net.minecraft.server.command.SummonCommand;
import net.minecraft.server.command.TagCommand;
import net.minecraft.server.command.TeamCommand;
import net.minecraft.server.command.TeammsgCommand;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.server.command.TellRawCommand;
import net.minecraft.server.command.TestCommand;
import net.minecraft.server.command.TimeCommand;
import net.minecraft.server.command.TitleCommand;
import net.minecraft.server.command.TriggerCommand;
import net.minecraft.server.command.WeatherCommand;
import net.minecraft.server.command.WorldBorderCommand;
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
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.SystemUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final CommandDispatcher<ServerCommandSource> dispatcher = new CommandDispatcher();

    public CommandManager(boolean bl2) {
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
        if (SharedConstants.isDevelopment) {
            TestCommand.register(this.dispatcher);
        }
        if (bl2) {
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
        this.dispatcher.findAmbiguities((commandNode, commandNode2, commandNode3, collection) -> LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", (Object)this.dispatcher.getPath(commandNode2), (Object)this.dispatcher.getPath(commandNode3), (Object)collection));
        this.dispatcher.setConsumer((commandContext, bl, i) -> ((ServerCommandSource)commandContext.getSource()).onCommandComplete(commandContext, bl, i));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int execute(ServerCommandSource serverCommandSource, String string) {
        StringReader stringReader = new StringReader(string);
        if (stringReader.canRead() && stringReader.peek() == '/') {
            stringReader.skip();
        }
        serverCommandSource.getMinecraftServer().getProfiler().push(string);
        try {
            int n = this.dispatcher.execute(stringReader, serverCommandSource);
            return n;
        } catch (CommandException commandException) {
            serverCommandSource.sendError(commandException.getTextMessage());
            int n = 0;
            return n;
        } catch (CommandSyntaxException commandSyntaxException) {
            int i;
            serverCommandSource.sendError(Texts.toText(commandSyntaxException.getRawMessage()));
            if (commandSyntaxException.getInput() != null && commandSyntaxException.getCursor() >= 0) {
                i = Math.min(commandSyntaxException.getInput().length(), commandSyntaxException.getCursor());
                Text text = new LiteralText("").formatted(Formatting.GRAY).styled(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, string)));
                if (i > 10) {
                    text.append("...");
                }
                text.append(commandSyntaxException.getInput().substring(Math.max(0, i - 10), i));
                if (i < commandSyntaxException.getInput().length()) {
                    Text text2 = new LiteralText(commandSyntaxException.getInput().substring(i)).formatted(Formatting.RED, Formatting.UNDERLINE);
                    text.append(text2);
                }
                text.append(new TranslatableText("command.context.here", new Object[0]).formatted(Formatting.RED, Formatting.ITALIC));
                serverCommandSource.sendError(text);
            }
            i = 0;
            return i;
        } catch (Exception exception) {
            LiteralText text3 = new LiteralText(exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("Command exception: {}", (Object)string, (Object)exception);
                StackTraceElement[] stackTraceElements = exception.getStackTrace();
                for (int j = 0; j < Math.min(stackTraceElements.length, 3); ++j) {
                    text3.append("\n\n").append(stackTraceElements[j].getMethodName()).append("\n ").append(stackTraceElements[j].getFileName()).append(":").append(String.valueOf(stackTraceElements[j].getLineNumber()));
                }
            }
            serverCommandSource.sendError(new TranslatableText("command.failed", new Object[0]).styled(style -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text3))));
            if (SharedConstants.isDevelopment) {
                serverCommandSource.sendError(new LiteralText(SystemUtil.getInnermostMessage(exception)));
                LOGGER.error("'" + string + "' threw an exception", (Throwable)exception);
            }
            int n = 0;
            return n;
        } finally {
            serverCommandSource.getMinecraftServer().getProfiler().pop();
        }
    }

    public void sendCommandTree(ServerPlayerEntity serverPlayerEntity) {
        HashMap<CommandNode<ServerCommandSource>, CommandNode<CommandSource>> map = Maps.newHashMap();
        RootCommandNode<CommandSource> rootCommandNode = new RootCommandNode<CommandSource>();
        map.put(this.dispatcher.getRoot(), rootCommandNode);
        this.makeTreeForSource(this.dispatcher.getRoot(), rootCommandNode, serverPlayerEntity.getCommandSource(), map);
        serverPlayerEntity.networkHandler.sendPacket(new CommandTreeS2CPacket(rootCommandNode));
    }

    private void makeTreeForSource(CommandNode<ServerCommandSource> commandNode, CommandNode<CommandSource> commandNode2, ServerCommandSource serverCommandSource, Map<CommandNode<ServerCommandSource>, CommandNode<CommandSource>> map) {
        for (CommandNode<ServerCommandSource> commandNode3 : commandNode.getChildren()) {
            RequiredArgumentBuilder requiredArgumentBuilder;
            if (!commandNode3.canUse(serverCommandSource)) continue;
            ArgumentBuilder<ServerCommandSource, ?> argumentBuilder = commandNode3.createBuilder();
            argumentBuilder.requires(commandSource -> true);
            if (argumentBuilder.getCommand() != null) {
                argumentBuilder.executes(commandContext -> 0);
            }
            if (argumentBuilder instanceof RequiredArgumentBuilder && (requiredArgumentBuilder = (RequiredArgumentBuilder)argumentBuilder).getSuggestionsProvider() != null) {
                requiredArgumentBuilder.suggests(SuggestionProviders.getLocalProvider(requiredArgumentBuilder.getSuggestionsProvider()));
            }
            if (argumentBuilder.getRedirect() != null) {
                argumentBuilder.redirect(map.get(argumentBuilder.getRedirect()));
            }
            CommandNode<CommandSource> commandNode4 = argumentBuilder.build();
            map.put(commandNode3, commandNode4);
            commandNode2.addChild(commandNode4);
            if (commandNode3.getChildren().isEmpty()) continue;
            this.makeTreeForSource(commandNode3, commandNode4, serverCommandSource, map);
        }
    }

    public static LiteralArgumentBuilder<ServerCommandSource> literal(String string) {
        return LiteralArgumentBuilder.literal(string);
    }

    public static <T> RequiredArgumentBuilder<ServerCommandSource, T> argument(String string, ArgumentType<T> argumentType) {
        return RequiredArgumentBuilder.argument(string, argumentType);
    }

    public static Predicate<String> getCommandValidator(CommandParser commandParser) {
        return string -> {
            try {
                commandParser.parse(new StringReader((String)string));
                return true;
            } catch (CommandSyntaxException commandSyntaxException) {
                return false;
            }
        };
    }

    public CommandDispatcher<ServerCommandSource> getDispatcher() {
        return this.dispatcher;
    }

    @FunctionalInterface
    public static interface CommandParser {
        public void parse(StringReader var1) throws CommandSyntaxException;
    }
}

