package net.minecraft.network.message;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Decoration;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

/**
 * A message type (also known as "chat type") controls how to display or narrate
 * the chat messages sent to the clients. Message types are registered using data packs. When
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
	public static final PacketCodec<RegistryByteBuf, MessageType> PACKET_CODEC = PacketCodec.tuple(
		Decoration.PACKET_CODEC, MessageType::chat, Decoration.PACKET_CODEC, MessageType::narration, MessageType::new
	);
	public static final PacketCodec<RegistryByteBuf, RegistryEntry<MessageType>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(
		RegistryKeys.MESSAGE_TYPE, PACKET_CODEC
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
	 * The registry key for the incoming message command message type, used by {@linkplain
	 * net.minecraft.server.command.MessageCommand /msg}. The message content is
	 * {@linkplain Decoration#ofIncomingMessage decorated} using the {@code
	 * commands.message.display.incoming} text.
	 * 
	 * <p>An incoming message is a private message received from the sender.
	 */
	public static final RegistryKey<MessageType> MSG_COMMAND_INCOMING = register("msg_command_incoming");
	/**
	 * The registry key for the outgoing message command message type, used by {@linkplain
	 * net.minecraft.server.command.MessageCommand /msg}. The message content is
	 * {@linkplain Decoration#ofOutgoingMessage decorated} using the {@code
	 * commands.message.display.outgoing} text.
	 * 
	 * <p>An outgoing message is a message that the private message's sender sees in the chat.
	 */
	public static final RegistryKey<MessageType> MSG_COMMAND_OUTGOING = register("msg_command_outgoing");
	/**
	 * The registry key for the incoming team message command message type, used by
	 * {@linkplain net.minecraft.server.command.TeamMsgCommand /teammsg}. The message
	 * content is {@linkplain Decoration#ofTeamMessage decorated} using the {@code
	 * chat.type.team.text} text.
	 * 
	 * <p>An incoming message is a team message received from the sender.
	 */
	public static final RegistryKey<MessageType> TEAM_MSG_COMMAND_INCOMING = register("team_msg_command_incoming");
	/**
	 * The registry key for the outgoing team message command message type, used by
	 * {@linkplain net.minecraft.server.command.TeamMsgCommand /teammsg}. The message
	 * content is {@linkplain Decoration#ofTeamMessage decorated} using the {@code
	 * chat.type.team.sent} text.
	 * 
	 * <p>An outgoing message is a message that the team message's sender sees in the chat.
	 */
	public static final RegistryKey<MessageType> TEAM_MSG_COMMAND_OUTGOING = register("team_msg_command_outgoing");
	/**
	 * The registry key for the emote command message type, used by {@linkplain
	 * net.minecraft.server.command.MeCommand /me}. The message content is
	 * {@linkplain Decoration#ofChat decorated} using the {@code chat.type.emote} text.
	 */
	public static final RegistryKey<MessageType> EMOTE_COMMAND = register("emote_command");

	private static RegistryKey<MessageType> register(String id) {
		return RegistryKey.of(RegistryKeys.MESSAGE_TYPE, Identifier.ofVanilla(id));
	}

	public static void bootstrap(Registerable<MessageType> messageTypeRegisterable) {
		messageTypeRegisterable.register(CHAT, new MessageType(CHAT_TEXT_DECORATION, Decoration.ofChat("chat.type.text.narrate")));
		messageTypeRegisterable.register(SAY_COMMAND, new MessageType(Decoration.ofChat("chat.type.announcement"), Decoration.ofChat("chat.type.text.narrate")));
		messageTypeRegisterable.register(
			MSG_COMMAND_INCOMING, new MessageType(Decoration.ofIncomingMessage("commands.message.display.incoming"), Decoration.ofChat("chat.type.text.narrate"))
		);
		messageTypeRegisterable.register(
			MSG_COMMAND_OUTGOING, new MessageType(Decoration.ofOutgoingMessage("commands.message.display.outgoing"), Decoration.ofChat("chat.type.text.narrate"))
		);
		messageTypeRegisterable.register(
			TEAM_MSG_COMMAND_INCOMING, new MessageType(Decoration.ofTeamMessage("chat.type.team.text"), Decoration.ofChat("chat.type.text.narrate"))
		);
		messageTypeRegisterable.register(
			TEAM_MSG_COMMAND_OUTGOING, new MessageType(Decoration.ofTeamMessage("chat.type.team.sent"), Decoration.ofChat("chat.type.text.narrate"))
		);
		messageTypeRegisterable.register(EMOTE_COMMAND, new MessageType(Decoration.ofChat("chat.type.emote"), Decoration.ofChat("chat.type.emote")));
	}

	public static MessageType.Parameters params(RegistryKey<MessageType> typeKey, Entity entity) {
		return params(typeKey, entity.getWorld().getRegistryManager(), entity.getDisplayName());
	}

	public static MessageType.Parameters params(RegistryKey<MessageType> typeKey, ServerCommandSource source) {
		return params(typeKey, source.getRegistryManager(), source.getDisplayName());
	}

	public static MessageType.Parameters params(RegistryKey<MessageType> typeKey, DynamicRegistryManager registryManager, Text name) {
		Registry<MessageType> registry = registryManager.get(RegistryKeys.MESSAGE_TYPE);
		return new MessageType.Parameters(registry.entryOf(typeKey), name);
	}

	/**
	 * A record holding the message type and the decoration parameters.
	 */
	public static record Parameters(RegistryEntry<MessageType> type, Text name, Optional<Text> targetName) {
		public static final PacketCodec<RegistryByteBuf, MessageType.Parameters> CODEC = PacketCodec.tuple(
			MessageType.ENTRY_PACKET_CODEC,
			MessageType.Parameters::type,
			TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC,
			MessageType.Parameters::name,
			TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC,
			MessageType.Parameters::targetName,
			MessageType.Parameters::new
		);

		Parameters(RegistryEntry<MessageType> type, Text name) {
			this(type, name, Optional.empty());
		}

		public Text applyChatDecoration(Text content) {
			return this.type.value().chat().apply(content, this);
		}

		public Text applyNarrationDecoration(Text content) {
			return this.type.value().narration().apply(content, this);
		}

		/**
		 * {@return a new instance with the given target name}
		 * 
		 * <p>Target name is used as the team name in {@link
		 * net.minecraft.server.command.TeamMsgCommand} and as the recipient name in {@link
		 * net.minecraft.server.command.MessageCommand}.
		 */
		public MessageType.Parameters withTargetName(Text targetName) {
			return new MessageType.Parameters(this.type, this.name, Optional.of(targetName));
		}
	}
}
