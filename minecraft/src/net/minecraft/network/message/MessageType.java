package net.minecraft.network.message;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.text.Decoration;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

/**
 * A message type (also known as "chat type") controls whether to display or narrate
 * the messages sent to the clients, and if so, how. Message types are registered
 * at {@link net.minecraft.util.registry.BuiltinRegistries#MESSAGE_TYPE}. When
 * sending a message, the registry key of the message type can be passed to indicate
 * which message type should be used.
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
 */
public record MessageType(Optional<MessageType.DisplayRule> chat, Optional<MessageType.DisplayRule> overlay, Optional<MessageType.NarrationRule> narration) {
	public static final Codec<MessageType> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					MessageType.DisplayRule.CODEC.optionalFieldOf("chat").forGetter(MessageType::chat),
					MessageType.DisplayRule.CODEC.optionalFieldOf("overlay").forGetter(MessageType::overlay),
					MessageType.NarrationRule.CODEC.optionalFieldOf("narration").forGetter(MessageType::narration)
				)
				.apply(instance, MessageType::new)
	);
	/**
	 * The registry key for the message type used by {@link
	 * net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket chat messages}.
	 * The message content is {@linkplain Decoration#ofChat decorated} using the
	 * {@code chat.type.text} text.
	 */
	public static final RegistryKey<MessageType> CHAT = register("chat");
	/**
	 * The registry key for the system message message type. This is also referred to
	 * as {@linkplain net.minecraft.network.packet.s2c.play.GameMessageS2CPacket game
	 * messages}. This message type does not have a decoration and its narrations will
	 * interrupt others.
	 * 
	 * @apiNote System messages include join/leave messages, death messages,
	 * advancement messages, and other messages that are not sent by players.
	 */
	public static final RegistryKey<MessageType> SYSTEM = register("system");
	/**
	 * The registry key for the game info message type. This appears on the overlay only
	 * and is not narrated at all. This message type does not have a decoration.
	 * 
	 * @apiNote This is most often seen when the player uses a bed.
	 */
	public static final RegistryKey<MessageType> GAME_INFO = register("game_info");
	/**
	 * The registry key for the say command message type, used by {@linkplain
	 * net.minecraft.server.command.SayCommand /say}. The message content is
	 * {@linkplain Decoration#ofChat decorated} using the {@code chat.type.announcement}
	 * text.
	 */
	public static final RegistryKey<MessageType> SAY_COMMAND = register("say_command");
	/**
	 * The registry key for the message command message type, used by {@linkplain
	 * net.minecraft.server.command.MessageCommand /msg}. The message content is
	 * {@linkplain Decoration#ofDirectMessage decorated} using the
	 * {@code commands.message.display.incoming} text, and the text is italicized and colored
	 * gray.
	 */
	public static final RegistryKey<MessageType> MSG_COMMAND = register("msg_command");
	/**
	 * The registry key for the team message command message type, used by {@linkplain
	 * net.minecraft.server.command.TeamMsgCommand /teammsg}. The message content is
	 * {@linkplain Decoration#ofTeamMessage decorated} using the
	 * {@code chat.type.team.text} text.
	 */
	public static final RegistryKey<MessageType> TEAM_MSG_COMMAND = register("team_msg_command");
	/**
	 * The registry key for the emote command message type, used by {@linkplain
	 * net.minecraft.server.command.MeCommand /me}. The message content is
	 * {@linkplain Decoration#ofChat decorated} using the {@code chat.type.emote} text.
	 */
	public static final RegistryKey<MessageType> EMOTE_COMMAND = register("emote_command");
	/**
	 * The registry key for the tellraw command message type, used by {@linkplain
	 * net.minecraft.server.command.TellRawCommand /tellraw}. This message type
	 * does not have a decoration.
	 */
	public static final RegistryKey<MessageType> TELLRAW_COMMAND = register("tellraw_command");

	private static RegistryKey<MessageType> register(String id) {
		return RegistryKey.of(Registry.MESSAGE_TYPE_KEY, new Identifier(id));
	}

	public static RegistryEntry<MessageType> initialize(Registry<MessageType> registry) {
		BuiltinRegistries.add(
			registry,
			CHAT,
			new MessageType(
				Optional.of(MessageType.DisplayRule.of(Decoration.ofChat("chat.type.text"))),
				Optional.empty(),
				Optional.of(MessageType.NarrationRule.of(Decoration.ofChat("chat.type.text.narrate"), MessageType.NarrationRule.Kind.CHAT))
			)
		);
		BuiltinRegistries.add(
			registry,
			SYSTEM,
			new MessageType(
				Optional.of(MessageType.DisplayRule.of()), Optional.empty(), Optional.of(MessageType.NarrationRule.of(MessageType.NarrationRule.Kind.SYSTEM))
			)
		);
		BuiltinRegistries.add(registry, GAME_INFO, new MessageType(Optional.empty(), Optional.of(MessageType.DisplayRule.of()), Optional.empty()));
		BuiltinRegistries.add(
			registry,
			SAY_COMMAND,
			new MessageType(
				Optional.of(MessageType.DisplayRule.of(Decoration.ofChat("chat.type.announcement"))),
				Optional.empty(),
				Optional.of(MessageType.NarrationRule.of(Decoration.ofChat("chat.type.text.narrate"), MessageType.NarrationRule.Kind.CHAT))
			)
		);
		BuiltinRegistries.add(
			registry,
			MSG_COMMAND,
			new MessageType(
				Optional.of(MessageType.DisplayRule.of(Decoration.ofDirectMessage("commands.message.display.incoming"))),
				Optional.empty(),
				Optional.of(MessageType.NarrationRule.of(Decoration.ofChat("chat.type.text.narrate"), MessageType.NarrationRule.Kind.CHAT))
			)
		);
		BuiltinRegistries.add(
			registry,
			TEAM_MSG_COMMAND,
			new MessageType(
				Optional.of(MessageType.DisplayRule.of(Decoration.ofTeamMessage("chat.type.team.text"))),
				Optional.empty(),
				Optional.of(MessageType.NarrationRule.of(Decoration.ofChat("chat.type.text.narrate"), MessageType.NarrationRule.Kind.CHAT))
			)
		);
		BuiltinRegistries.add(
			registry,
			EMOTE_COMMAND,
			new MessageType(
				Optional.of(MessageType.DisplayRule.of(Decoration.ofChat("chat.type.emote"))),
				Optional.empty(),
				Optional.of(MessageType.NarrationRule.of(Decoration.ofChat("chat.type.emote"), MessageType.NarrationRule.Kind.CHAT))
			)
		);
		return BuiltinRegistries.add(
			registry,
			TELLRAW_COMMAND,
			new MessageType(Optional.of(MessageType.DisplayRule.of()), Optional.empty(), Optional.of(MessageType.NarrationRule.of(MessageType.NarrationRule.Kind.CHAT)))
		);
	}

	/**
	 * The display rule for the message type. This contains the decoration applied
	 * to the message.
	 */
	public static record DisplayRule(Optional<Decoration> decoration) {
		public static final Codec<MessageType.DisplayRule> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(Decoration.CODEC.optionalFieldOf("decoration").forGetter(MessageType.DisplayRule::decoration))
					.apply(instance, MessageType.DisplayRule::new)
		);

		/**
		 * {@return the display rule with no decoration}
		 * 
		 * <p>This does not mean the message is not displayed; this means that the message
		 * is displayed as is without any extra processing.
		 */
		public static MessageType.DisplayRule of() {
			return new MessageType.DisplayRule(Optional.empty());
		}

		/**
		 * {@return the display rule with the specified decoration}
		 */
		public static MessageType.DisplayRule of(Decoration decoration) {
			return new MessageType.DisplayRule(Optional.of(decoration));
		}

		/**
		 * {@return the message with the decoration applied, or {@code message} if there is
		 * no decoration}
		 */
		public Text apply(Text message, @Nullable MessageSender sender) {
			return (Text)this.decoration.map(decoration -> decoration.apply(message, sender)).orElse(message);
		}
	}

	/**
	 * The narration rule for the message type. This contains the decoration applied
	 * to the message and the kind of the narration ({@code priority} when serialized).
	 */
	public static record NarrationRule(Optional<Decoration> decoration, MessageType.NarrationRule.Kind kind) {
		public static final Codec<MessageType.NarrationRule> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Decoration.CODEC.optionalFieldOf("decoration").forGetter(MessageType.NarrationRule::decoration),
						MessageType.NarrationRule.Kind.CODEC.fieldOf("priority").forGetter(MessageType.NarrationRule::kind)
					)
					.apply(instance, MessageType.NarrationRule::new)
		);

		/**
		 * {@return the narration rule with no decoration and the specified kind}
		 * 
		 * <p>This does not mean the message is not narrated; this means that the message
		 * is narrated as is without any extra processing.
		 */
		public static MessageType.NarrationRule of(MessageType.NarrationRule.Kind priority) {
			return new MessageType.NarrationRule(Optional.empty(), priority);
		}

		/**
		 * {@return the narration rule with the specified decoration and kind}
		 */
		public static MessageType.NarrationRule of(Decoration decoration, MessageType.NarrationRule.Kind kind) {
			return new MessageType.NarrationRule(Optional.of(decoration), kind);
		}

		/**
		 * {@return the message with the decoration applied, or {@code message} if there is
		 * no decoration}
		 */
		public Text apply(Text message, @Nullable MessageSender sender) {
			return (Text)this.decoration.map(decoration -> decoration.apply(message, sender)).orElse(message);
		}

		/**
		 * The kind of narration. This is also known as priority, because it determines
		 * if the incoming narration should interrupt the current one. This is also used
		 * to check if the message {@linkplain net.minecraft.client.option.NarratorMode#shouldNarrate
		 * should be narrated} when the narrator option is set to "Chat" or "System".
		 */
		public static enum Kind implements StringIdentifiable {
			CHAT("chat", false),
			SYSTEM("system", true);

			public static final com.mojang.serialization.Codec<MessageType.NarrationRule.Kind> CODEC = StringIdentifiable.createCodec(
				MessageType.NarrationRule.Kind::values
			);
			private final String name;
			private final boolean interrupt;

			private Kind(String name, boolean interrupt) {
				this.name = name;
				this.interrupt = interrupt;
			}

			/**
			 * {@return whether the message has priority over others and should interrupt
			 * their narrations}
			 */
			public boolean shouldInterrupt() {
				return this.interrupt;
			}

			@Override
			public String asString() {
				return this.name;
			}
		}
	}
}
