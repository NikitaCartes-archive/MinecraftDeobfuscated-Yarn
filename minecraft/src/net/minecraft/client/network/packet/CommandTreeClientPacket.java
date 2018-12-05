package net.minecraft.client.network.packet;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.command.CommandSource;
import net.minecraft.util.PacketByteBuf;

public class CommandTreeClientPacket implements Packet<ClientPlayPacketListener> {
	private RootCommandNode<CommandSource> field_12123;

	public CommandTreeClientPacket() {
	}

	public CommandTreeClientPacket(RootCommandNode<CommandSource> rootCommandNode) {
		this.field_12123 = rootCommandNode;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		CommandTreeClientPacket.class_2642[] lvs = new CommandTreeClientPacket.class_2642[packetByteBuf.readVarInt()];
		Deque<CommandTreeClientPacket.class_2642> deque = new ArrayDeque(lvs.length);

		for (int i = 0; i < lvs.length; i++) {
			lvs[i] = this.method_11405(packetByteBuf);
			deque.add(lvs[i]);
		}

		while (!deque.isEmpty()) {
			boolean bl = false;
			Iterator<CommandTreeClientPacket.class_2642> iterator = deque.iterator();

			while (iterator.hasNext()) {
				CommandTreeClientPacket.class_2642 lv = (CommandTreeClientPacket.class_2642)iterator.next();
				if (lv.method_11406(lvs)) {
					iterator.remove();
					bl = true;
				}
			}

			if (!bl) {
				throw new IllegalStateException("Server sent an impossible command tree");
			}
		}

		this.field_12123 = (RootCommandNode<CommandSource>)lvs[packetByteBuf.readVarInt()].field_12128;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		Map<CommandNode<CommandSource>, Integer> map = Maps.<CommandNode<CommandSource>, Integer>newHashMap();
		Deque<CommandNode<CommandSource>> deque = new ArrayDeque();
		deque.add(this.field_12123);

		while (!deque.isEmpty()) {
			CommandNode<CommandSource> commandNode = (CommandNode<CommandSource>)deque.pollFirst();
			if (!map.containsKey(commandNode)) {
				int i = map.size();
				map.put(commandNode, i);
				deque.addAll(commandNode.getChildren());
				if (commandNode.getRedirect() != null) {
					deque.add(commandNode.getRedirect());
				}
			}
		}

		CommandNode<CommandSource>[] commandNodes = new CommandNode[map.size()];

		for (Entry<CommandNode<CommandSource>, Integer> entry : map.entrySet()) {
			commandNodes[entry.getValue()] = (CommandNode<CommandSource>)entry.getKey();
		}

		packetByteBuf.writeVarInt(commandNodes.length);

		for (CommandNode<CommandSource> commandNode2 : commandNodes) {
			this.method_11401(packetByteBuf, commandNode2, map);
		}

		packetByteBuf.writeVarInt((Integer)map.get(this.field_12123));
	}

	private CommandTreeClientPacket.class_2642 method_11405(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		int[] is = packetByteBuf.readIntArray();
		int i = (b & 8) != 0 ? packetByteBuf.readVarInt() : 0;
		ArgumentBuilder<CommandSource, ?> argumentBuilder = this.method_11402(packetByteBuf, b);
		return new CommandTreeClientPacket.class_2642(argumentBuilder, b, i, is);
	}

	@Nullable
	private ArgumentBuilder<CommandSource, ?> method_11402(PacketByteBuf packetByteBuf, byte b) {
		int i = b & 3;
		if (i == 2) {
			String string = packetByteBuf.readString(32767);
			ArgumentType<?> argumentType = ArgumentTypes.fromPacket(packetByteBuf);
			if (argumentType == null) {
				return null;
			} else {
				RequiredArgumentBuilder<CommandSource, ?> requiredArgumentBuilder = RequiredArgumentBuilder.argument(string, argumentType);
				if ((b & 16) != 0) {
					requiredArgumentBuilder.suggests(SuggestionProviders.byId(packetByteBuf.readIdentifier()));
				}

				return requiredArgumentBuilder;
			}
		} else {
			return i == 1 ? LiteralArgumentBuilder.literal(packetByteBuf.readString(32767)) : null;
		}
	}

	private void method_11401(PacketByteBuf packetByteBuf, CommandNode<CommandSource> commandNode, Map<CommandNode<CommandSource>, Integer> map) {
		byte b = 0;
		if (commandNode.getRedirect() != null) {
			b = (byte)(b | 8);
		}

		if (commandNode.getCommand() != null) {
			b = (byte)(b | 4);
		}

		if (commandNode instanceof RootCommandNode) {
			b = (byte)(b | 0);
		} else if (commandNode instanceof ArgumentCommandNode) {
			b = (byte)(b | 2);
			if (((ArgumentCommandNode)commandNode).getCustomSuggestions() != null) {
				b = (byte)(b | 16);
			}
		} else {
			if (!(commandNode instanceof LiteralCommandNode)) {
				throw new UnsupportedOperationException("Unknown node type " + commandNode);
			}

			b = (byte)(b | 1);
		}

		packetByteBuf.writeByte(b);
		packetByteBuf.writeVarInt(commandNode.getChildren().size());

		for (CommandNode<CommandSource> commandNode2 : commandNode.getChildren()) {
			packetByteBuf.writeVarInt((Integer)map.get(commandNode2));
		}

		if (commandNode.getRedirect() != null) {
			packetByteBuf.writeVarInt((Integer)map.get(commandNode.getRedirect()));
		}

		if (commandNode instanceof ArgumentCommandNode) {
			ArgumentCommandNode<CommandSource, ?> argumentCommandNode = (ArgumentCommandNode<CommandSource, ?>)commandNode;
			packetByteBuf.writeString(argumentCommandNode.getName());
			ArgumentTypes.toPacket(packetByteBuf, argumentCommandNode.getType());
			if (argumentCommandNode.getCustomSuggestions() != null) {
				packetByteBuf.writeIdentifier(SuggestionProviders.computeName(argumentCommandNode.getCustomSuggestions()));
			}
		} else if (commandNode instanceof LiteralCommandNode) {
			packetByteBuf.writeString(((LiteralCommandNode)commandNode).getLiteral());
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCommandTree(this);
	}

	@Environment(EnvType.CLIENT)
	public RootCommandNode<CommandSource> method_11403() {
		return this.field_12123;
	}

	static class class_2642 {
		@Nullable
		private final ArgumentBuilder<CommandSource, ?> field_12127;
		private final byte field_12124;
		private final int field_12126;
		private final int[] field_12125;
		private CommandNode<CommandSource> field_12128;

		private class_2642(@Nullable ArgumentBuilder<CommandSource, ?> argumentBuilder, byte b, int i, int[] is) {
			this.field_12127 = argumentBuilder;
			this.field_12124 = b;
			this.field_12126 = i;
			this.field_12125 = is;
		}

		public boolean method_11406(CommandTreeClientPacket.class_2642[] args) {
			if (this.field_12128 == null) {
				if (this.field_12127 == null) {
					this.field_12128 = new RootCommandNode<>();
				} else {
					if ((this.field_12124 & 8) != 0) {
						if (args[this.field_12126].field_12128 == null) {
							return false;
						}

						this.field_12127.redirect(args[this.field_12126].field_12128);
					}

					if ((this.field_12124 & 4) != 0) {
						this.field_12127.executes(commandContext -> 0);
					}

					this.field_12128 = this.field_12127.build();
				}
			}

			for (int i : this.field_12125) {
				if (args[i].field_12128 == null) {
					return false;
				}
			}

			for (int ix : this.field_12125) {
				CommandNode<CommandSource> commandNode = args[ix].field_12128;
				if (!(commandNode instanceof RootCommandNode)) {
					this.field_12128.addChild(commandNode);
				}
			}

			return true;
		}
	}
}
