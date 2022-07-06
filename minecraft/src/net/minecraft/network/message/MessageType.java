package net.minecraft.network.message;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Decoration;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

/**
 * A message type (also known as "chat type") controls how to display or narrate
 * the chat messages sent to the clients. Message types are registered at
 * {@link net.minecraft.util.registry.BuiltinRegistries#MESSAGE_TYPE}. When
 * sending a chat message, the registry key of the message type can be passed to indicate
 * which message type should be used.
 * 
 * <p>Message type has two fields.
 * <ul>
 * <li>{@link #chat} controls the content displayed in the {@linkplain
 * net.minecraft.client.gui.hud.ChatHud chat hud}.</li>
 * <li>{@link #narration} controls the narrated content.</li>
 * </ul>
 * 
 * <p>The fields are "decoration", which is an instance of {@link Decoration}.
 * Decorations are pre-defined message formatting and styling rules, which can be
 * {@linkplain Decoration#apply applied} to the message to produce the displayed or
 * narrated text.
 */
public record MessageType(Decoration chat, Decoration narration) {
	public static final Codec<MessageType> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Decoration.CODEC.fieldOf("chat").forGetter(MessageType::chat), Decoration.CODEC.fieldOf("narration").forGetter(MessageType::narration)
				)
				.apply(instance, MessageType::new)
	);
	public static final Decoration CHAT_TEXT_DECORATION = Decoration.ofChat("chat.type.text");
	/**
	 * The registry key for the message type used by {@link
	 * net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket chat messages}.
	 * The message content is {@linkplain Decoration#ofChat decorated} using the
	 * {@code chat.type.text} text.
	 */
	public static final RegistryKey<MessageType> CHAT = register("chat");
	/**
	 * The registry key for the say command message type, used by {@linkplain
	 * net.minecraft.server.command.SayCommand /say}. The message content is
	 * {@linkplain Decoration#ofChat decorated} using the {@code chat.type.announcement}
	 * text.
	 */
	public static final RegistryKey<MessageType> SAY_COMMAND = register("say_command");
	/**
	 * The registry key for the msg command message type, used by {@linkplain
	 * net.minecraft.server.command.MessageCommand /msg} for incoming messages.
	 * The message content is {@linkplain Decoration#ofIncomingMessage decorated} using
	 * the {@code commands.message.display.incoming} text.
	 */
	public static final RegistryKey<MessageType> MSG_COMMAND_INCOMING = register("msg_command_incoming");
	/**
	 * The registry key for the msg command message type, used by {@linkplain
	 * net.minecraft.server.command.MessageCommand /msg} for outgoing messages.
	 * The message content is {@linkplain Decoration#ofOutgoingMessage decorated} using
	 * the {@code commands.message.display.outgoing} text.
	 */
	public static final RegistryKey<MessageType> MSG_COMMAND_OUTGOING = register("msg_command_outgoing");
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

	private static RegistryKey<MessageType> register(String id) {
		return RegistryKey.of(Registry.MESSAGE_TYPE_KEY, new Identifier(id));
	}

	public static RegistryEntry<MessageType> initialize(Registry<MessageType> registry) {
		BuiltinRegistries.add(registry, CHAT, new MessageType(CHAT_TEXT_DECORATION, Decoration.ofChat("chat.type.text.narrate")));
		BuiltinRegistries.add(registry, SAY_COMMAND, new MessageType(Decoration.ofChat("chat.type.announcement"), Decoration.ofChat("chat.type.text.narrate")));
		BuiltinRegistries.add(
			registry,
			MSG_COMMAND_INCOMING,
			new MessageType(Decoration.ofIncomingMessage("commands.message.display.incoming"), Decoration.ofChat("chat.type.text.narrate"))
		);
		BuiltinRegistries.add(
			registry,
			MSG_COMMAND_OUTGOING,
			new MessageType(Decoration.ofOutgoingMessage("commands.message.display.outgoing"), Decoration.ofChat("chat.type.text.narrate"))
		);
		BuiltinRegistries.add(
			registry, TEAM_MSG_COMMAND, new MessageType(Decoration.ofTeamMessage("chat.type.team.text"), Decoration.ofChat("chat.type.text.narrate"))
		);
		return BuiltinRegistries.add(registry, EMOTE_COMMAND, new MessageType(Decoration.ofChat("chat.type.emote"), Decoration.ofChat("chat.type.emote")));
	}
}
