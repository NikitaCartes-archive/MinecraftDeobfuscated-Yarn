/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.network.MessageSender;
import net.minecraft.text.Decoration;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

/**
 * A message type (also known as "chat type") controls whether to display or narrate
 * the messages sent to the clients, and if so, how. Message types are registered
 * at {@link Registry#MESSAGE_TYPE}. When sending a message, the registry key of the
 * message type can be passed to indicate which message type should be used.
 * 
 * <p>Message type has three fields, all of which are optional. If the field is empty,
 * the message is not displayed or narrated there.
 * <ul>
 * <li>{@link #chat} controls the content displayed in the {@linkplain
 * net.minecraft.client.gui.hud.ChatHud chat hud}.</li>
 * <li>{@link #overlay} controls the content displayed as the overlay (above the hotbar).</li>
 * <li>{@link #narration} controls the narrated content.</li>
 * </ul>
 * 
 * <p>The display rules and the narration rule can optionally have a "decoration", which is an
 * instance of {@link Decoration}. Decorations are pre-defined message formatting and
 * styling rules, which can be {@linkplain Decoration#apply applied} to the message to
 * produce the displayed or narrated text. If there is no decoration, the message is used
 * without any extra processing. See the documentation for {@link MessageType.DisplayRule}
 * and {@link MessageType.NarrationRule} for details.
 * 
 * @see net.minecraft.server.PlayerManager#broadcast(Text, RegistryKey)
 * 
 *  * @param chat the display rule for the content displayed in the chat hud, or\u000a{@link Optional#empty()} if it should not be displayed in the chat hud
 * @param overlay the display rule for the content displayed as the overlay, or\u000a{@link Optional#empty()} if it should not be displayed as the overlay
 * @param narration the narration rule for the content, or {@link Optional#empty()}\u000aif it should not be narrated
 */
public record MessageType(Optional<DisplayRule> chat, Optional<DisplayRule> overlay, Optional<NarrationRule> narration) {
    public static final Codec<MessageType> CODEC = RecordCodecBuilder.create(instance -> instance.group(DisplayRule.CODEC.optionalFieldOf("chat").forGetter(MessageType::chat), DisplayRule.CODEC.optionalFieldOf("overlay").forGetter(MessageType::overlay), NarrationRule.CODEC.optionalFieldOf("narration").forGetter(MessageType::narration)).apply((Applicative<MessageType, ?>)instance, MessageType::new));
    /**
     * The registry key for the message type used by {@link
     * net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket chat messages}.
     * The message content is {@linkplain Decoration#ofChat decorated} using the
     * {@code chat.type.text} text.
     */
    public static final RegistryKey<MessageType> CHAT = MessageType.register("chat");
    /**
     * The registry key for the system message message type. This is also referred to
     * as {@linkplain net.minecraft.network.packet.s2c.play.GameMessageS2CPacket game
     * messages}. This message type does not have a decoration and its narrations will
     * interrupt others.
     * 
     * @apiNote System messages include join/leave messages, death messages,
     * advancement messages, and other messages that are not sent by players.
     */
    public static final RegistryKey<MessageType> SYSTEM = MessageType.register("system");
    /**
     * The registry key for the game info message type. This appears on the overlay only
     * and is not narrated at all. This message type does not have a decoration.
     * 
     * @apiNote This is most often seen when the player uses a bed.
     */
    public static final RegistryKey<MessageType> GAME_INFO = MessageType.register("game_info");
    /**
     * The registry key for the say command message type, used by {@linkplain
     * net.minecraft.server.command.SayCommand /say}. The message content is
     * {@linkplain Decoration#ofChat decorated} using the {@code chat.type.announcement}
     * text.
     */
    public static final RegistryKey<MessageType> SAY_COMMAND = MessageType.register("say_command");
    /**
     * The registry key for the message command message type, used by {@linkplain
     * net.minecraft.server.command.MessageCommand /msg}. The message content is
     * {@linkplain Decoration#ofDirectMessage decorated} using the
     * {@code commands.message.display.incoming} text, and the text is italicized and colored
     * gray.
     */
    public static final RegistryKey<MessageType> MSG_COMMAND = MessageType.register("msg_command");
    /**
     * The registry key for the team message command message type, used by {@linkplain
     * net.minecraft.server.command.TeamMsgCommand /teammsg}. The message content is
     * {@linkplain Decoration#ofTeamMessage decorated} using the
     * {@code chat.type.team.text} text.
     */
    public static final RegistryKey<MessageType> TEAM_MSG_COMMAND = MessageType.register("team_msg_command");
    /**
     * The registry key for the emote command message type, used by {@linkplain
     * net.minecraft.server.command.MeCommand /me}. The message content is
     * {@linkplain Decoration#ofChat decorated} using the {@code chat.type.emote} text.
     */
    public static final RegistryKey<MessageType> EMOTE_COMMAND = MessageType.register("emote_command");
    /**
     * The registry key for the tellraw command message type, used by {@linkplain
     * net.minecraft.server.command.TellRawCommand /tellraw}. This message type
     * does not have a decoration.
     */
    public static final RegistryKey<MessageType> TELLRAW_COMMAND = MessageType.register("tellraw_command");

    private static RegistryKey<MessageType> register(String id) {
        return RegistryKey.of(Registry.MESSAGE_TYPE_KEY, new Identifier(id));
    }

    public static MessageType registerAndGetDefault(Registry<MessageType> registry) {
        MessageType messageType = Registry.register(registry, CHAT, new MessageType(Optional.of(DisplayRule.of(Decoration.ofChat("chat.type.text"))), Optional.empty(), Optional.of(NarrationRule.of(Decoration.ofChat("chat.type.text.narrate"), NarrationRule.Kind.CHAT))));
        Registry.register(registry, SYSTEM, new MessageType(Optional.of(DisplayRule.of()), Optional.empty(), Optional.of(NarrationRule.of(NarrationRule.Kind.SYSTEM))));
        Registry.register(registry, GAME_INFO, new MessageType(Optional.empty(), Optional.of(DisplayRule.of()), Optional.empty()));
        Registry.register(registry, SAY_COMMAND, new MessageType(Optional.of(DisplayRule.of(Decoration.ofChat("chat.type.announcement"))), Optional.empty(), Optional.of(NarrationRule.of(Decoration.ofChat("chat.type.text.narrate"), NarrationRule.Kind.CHAT))));
        Registry.register(registry, MSG_COMMAND, new MessageType(Optional.of(DisplayRule.of(Decoration.ofDirectMessage("commands.message.display.incoming"))), Optional.empty(), Optional.of(NarrationRule.of(Decoration.ofChat("chat.type.text.narrate"), NarrationRule.Kind.CHAT))));
        Registry.register(registry, TEAM_MSG_COMMAND, new MessageType(Optional.of(DisplayRule.of(Decoration.ofTeamMessage("chat.type.team.text"))), Optional.empty(), Optional.of(NarrationRule.of(Decoration.ofChat("chat.type.text.narrate"), NarrationRule.Kind.CHAT))));
        Registry.register(registry, EMOTE_COMMAND, new MessageType(Optional.of(DisplayRule.of(Decoration.ofChat("chat.type.emote"))), Optional.empty(), Optional.of(NarrationRule.of(Decoration.ofChat("chat.type.emote"), NarrationRule.Kind.CHAT))));
        Registry.register(registry, TELLRAW_COMMAND, new MessageType(Optional.of(DisplayRule.of()), Optional.empty(), Optional.of(NarrationRule.of(NarrationRule.Kind.CHAT))));
        return messageType;
    }

    public record DisplayRule(Optional<Decoration> decoration) {
        public static final Codec<DisplayRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(Decoration.CODEC.optionalFieldOf("decoration").forGetter(DisplayRule::decoration)).apply((Applicative<DisplayRule, ?>)instance, DisplayRule::new));

        public static DisplayRule of() {
            return new DisplayRule(Optional.empty());
        }

        public static DisplayRule of(Decoration decoration) {
            return new DisplayRule(Optional.of(decoration));
        }

        public Text apply(Text message, @Nullable MessageSender sender) {
            return this.decoration.map(decoration -> decoration.apply(message, sender)).orElse(message);
        }
    }

    public static final class NarrationRule
    extends Record {
        private final Optional<Decoration> decoration;
        private final Kind kind;
        public static final Codec<NarrationRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(Decoration.CODEC.optionalFieldOf("decoration").forGetter(NarrationRule::decoration), ((MapCodec)Kind.CODEC.fieldOf("priority")).forGetter(NarrationRule::priority)).apply((Applicative<NarrationRule, ?>)instance, NarrationRule::new));

        public NarrationRule(Optional<Decoration> optional, Kind kind) {
            this.decoration = optional;
            this.kind = kind;
        }

        public static NarrationRule of(Kind priority) {
            return new NarrationRule(Optional.empty(), priority);
        }

        public static NarrationRule of(Decoration decoration, Kind kind) {
            return new NarrationRule(Optional.of(decoration), kind);
        }

        public Text apply(Text message, @Nullable MessageSender sender) {
            return this.decoration.map(decoration -> decoration.apply(message, sender)).orElse(message);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{NarrationRule.class, "decoration;priority", "decoration", "kind"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{NarrationRule.class, "decoration;priority", "decoration", "kind"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{NarrationRule.class, "decoration;priority", "decoration", "kind"}, this, object);
        }

        public Optional<Decoration> decoration() {
            return this.decoration;
        }

        public Kind priority() {
            return this.kind;
        }

        public static enum Kind implements StringIdentifiable
        {
            CHAT("chat", false),
            SYSTEM("system", true);

            public static final Codec<Kind> CODEC;
            private final String name;
            private final boolean interrupt;

            private Kind(String name, boolean interrupt) {
                this.name = name;
                this.interrupt = interrupt;
            }

            public boolean shouldInterrupt() {
                return this.interrupt;
            }

            @Override
            public String asString() {
                return this.name;
            }

            static {
                CODEC = StringIdentifiable.createCodec(Kind::values);
            }
        }
    }
}

