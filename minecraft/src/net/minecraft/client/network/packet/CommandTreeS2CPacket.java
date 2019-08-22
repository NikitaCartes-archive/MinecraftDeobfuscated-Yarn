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

public class CommandTreeS2CPacket implements Packet<ClientPlayPacketListener> {
	private RootCommandNode<CommandSource> commandTree;

	public CommandTreeS2CPacket() {
	}

	public CommandTreeS2CPacket(RootCommandNode<CommandSource> rootCommandNode) {
		this.commandTree = rootCommandNode;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		CommandTreeS2CPacket.CommandNodeData[] commandNodeDatas = new CommandTreeS2CPacket.CommandNodeData[packetByteBuf.readVarInt()];
		Deque<CommandTreeS2CPacket.CommandNodeData> deque = new ArrayDeque(commandNodeDatas.length);

		for (int i = 0; i < commandNodeDatas.length; i++) {
			commandNodeDatas[i] = this.readCommandNode(packetByteBuf);
			deque.add(commandNodeDatas[i]);
		}

		while (!deque.isEmpty()) {
			boolean bl = false;
			Iterator<CommandTreeS2CPacket.CommandNodeData> iterator = deque.iterator();

			while (iterator.hasNext()) {
				CommandTreeS2CPacket.CommandNodeData commandNodeData = (CommandTreeS2CPacket.CommandNodeData)iterator.next();
				if (commandNodeData.build(commandNodeDatas)) {
					iterator.remove();
					bl = true;
				}
			}

			if (!bl) {
				throw new IllegalStateException("Server sent an impossible command tree");
			}
		}

		this.commandTree = (RootCommandNode<CommandSource>)commandNodeDatas[packetByteBuf.readVarInt()].node;
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		Map<CommandNode<CommandSource>, Integer> map = Maps.<CommandNode<CommandSource>, Integer>newHashMap();
		Deque<CommandNode<CommandSource>> deque = new ArrayDeque();
		deque.add(this.commandTree);

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
			this.writeNode(packetByteBuf, commandNode2, map);
		}

		packetByteBuf.writeVarInt((Integer)map.get(this.commandTree));
	}

	private CommandTreeS2CPacket.CommandNodeData readCommandNode(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		int[] is = packetByteBuf.readIntArray();
		int i = (b & 8) != 0 ? packetByteBuf.readVarInt() : 0;
		ArgumentBuilder<CommandSource, ?> argumentBuilder = this.readArgumentBuilder(packetByteBuf, b);
		return new CommandTreeS2CPacket.CommandNodeData(argumentBuilder, b, i, is);
	}

	@Nullable
	private ArgumentBuilder<CommandSource, ?> readArgumentBuilder(PacketByteBuf packetByteBuf, byte b) {
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

	private void writeNode(PacketByteBuf packetByteBuf, CommandNode<CommandSource> commandNode, Map<CommandNode<CommandSource>, Integer> map) {
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

	public void method_11404(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCommandTree(this);
	}

	@Environment(EnvType.CLIENT)
	public RootCommandNode<CommandSource> getCommandTree() {
		return this.commandTree;
	}

	static class CommandNodeData {
		@Nullable
		private final ArgumentBuilder<CommandSource, ?> argumentBuilder;
		private final byte flags;
		private final int redirectNodeIndex;
		private final int[] childNodeIndices;
		private CommandNode<CommandSource> node;

		private CommandNodeData(@Nullable ArgumentBuilder<CommandSource, ?> argumentBuilder, byte b, int i, int[] is) {
			this.argumentBuilder = argumentBuilder;
			this.flags = b;
			this.redirectNodeIndex = i;
			this.childNodeIndices = is;
		}

		public boolean build(CommandTreeS2CPacket.CommandNodeData[] commandNodeDatas) {
			if (this.node == null) {
				if (this.argumentBuilder == null) {
					this.node = new RootCommandNode<>();
				} else {
					if ((this.flags & 8) != 0) {
						if (commandNodeDatas[this.redirectNodeIndex].node == null) {
							return false;
						}

						this.argumentBuilder.redirect(commandNodeDatas[this.redirectNodeIndex].node);
					}

					if ((this.flags & 4) != 0) {
						this.argumentBuilder.executes(commandContext -> 0);
					}

					this.node = this.argumentBuilder.build();
				}
			}

			for (int i : this.childNodeIndices) {
				if (commandNodeDatas[i].node == null) {
					return false;
				}
			}

			for (int ix : this.childNodeIndices) {
				CommandNode<CommandSource> commandNode = commandNodeDatas[ix].node;
				if (!(commandNode instanceof RootCommandNode)) {
					this.node.addChild(commandNode);
				}
			}

			return true;
		}
	}
}
