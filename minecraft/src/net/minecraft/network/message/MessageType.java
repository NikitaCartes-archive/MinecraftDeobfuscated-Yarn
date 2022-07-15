package net.minecraft.network.message;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Decoration;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
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
			registry, TEAM_MSG_COMMAND_INCOMING, new MessageType(Decoration.ofTeamMessage("chat.type.team.text"), Decoration.ofChat("chat.type.text.narrate"))
		);
		BuiltinRegistries.add(
			registry, TEAM_MSG_COMMAND_OUTGOING, new MessageType(Decoration.ofTeamMessage("chat.type.team.sent"), Decoration.ofChat("chat.type.text.narrate"))
		);
		return BuiltinRegistries.add(registry, EMOTE_COMMAND, new MessageType(Decoration.ofChat("chat.type.emote"), Decoration.ofChat("chat.type.emote")));
	}

	public static MessageType.Parameters params(RegistryKey<MessageType> typeKey, Entity entity) {
		return params(typeKey, entity.world.getRegistryManager(), entity.getDisplayName());
	}

	public static MessageType.Parameters params(RegistryKey<MessageType> typeKey, ServerCommandSource source) {
		return params(typeKey, source.getRegistryManager(), source.getDisplayName());
	}

	public static MessageType.Parameters params(RegistryKey<MessageType> typeKey, DynamicRegistryManager registryManager, Text name) {
		Registry<MessageType> registry = registryManager.get(Registry.MESSAGE_TYPE_KEY);
		return registry.getOrThrow(typeKey).params(name);
	}

	public MessageType.Parameters params(Text name) {
		return new MessageType.Parameters(this, name);
	}

	/**
	 * A record holding the message type and the decoration parameters.
	 */
	public static record Parameters(MessageType type, Text name, @Nullable Text targetName) {
		Parameters(MessageType type, Text name) {
			this(type, name, null);
		}

		public Text applyChatDecoration(Text content) {
			return this.type.chat().apply(content, this);
		}

		public Text applyNarrationDecoration(Text content) {
			return this.type.narration().apply(content, this);
		}

		/**
		 * {@return a new instance with the given target name}
		 * 
		 * <p>Target name is used as the team name in {@link
		 * net.minecraft.server.command.TeamMsgCommand} and as the recipient name in {@link
		 * net.minecraft.server.command.MessageCommand}.
		 */
		public MessageType.Parameters withTargetName(Text targetName) {
			return new MessageType.Parameters(this.type, this.name, targetName);
		}

		/**
		 * {@return a serialized version of this instance used in packets}
		 */
		public MessageType.Serialized toSerialized(DynamicRegistryManager registryManager) {
			Registry<MessageType> registry = registryManager.get(Registry.MESSAGE_TYPE_KEY);
			return new MessageType.Serialized(registry.getRawId(this.type), this.name, this.targetName);
		}
	}

	/**
	 * The serialized version of {@link MessageType.Parameters} that is used in packets.
	 */
	public static record Serialized(int typeId, Text name, @Nullable Text targetName) {
		public Serialized(PacketByteBuf buf) {
			this(buf.readVarInt(), buf.readText(), buf.readNullable(PacketByteBuf::readText));
		}

		public void write(PacketByteBuf buf) {
			buf.writeVarInt(this.typeId);
			buf.writeText(this.name);
			buf.writeNullable(this.targetName, PacketByteBuf::writeText);
		}

		/**
		 * {@return a deserialized version of this instance}
		 */
		public MessageType.Parameters toParameters(DynamicRegistryManager registryManager) {
			Registry<MessageType> registry = registryManager.get(Registry.MESSAGE_TYPE_KEY);
			return new MessageType.Parameters((MessageType)Objects.requireNonNull(registry.get(this.typeId), "Invalid chat type"), this.name, this.targetName);
		}
	}
}
