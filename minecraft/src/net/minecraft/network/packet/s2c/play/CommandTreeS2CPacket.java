package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class CommandTreeS2CPacket implements Packet<ClientPlayPacketListener> {
	private RootCommandNode<CommandSource> commandTree;

	public CommandTreeS2CPacket() {
	}

	public CommandTreeS2CPacket(RootCommandNode<CommandSource> commandTree) {
		this.commandTree = commandTree;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		CommandTreeS2CPacket.CommandNodeData[] commandNodeDatas = new CommandTreeS2CPacket.CommandNodeData[buf.readVarInt()];

		for (int i = 0; i < commandNodeDatas.length; i++) {
			commandNodeDatas[i] = readCommandNode(buf);
		}

		method_30946(commandNodeDatas);
		this.commandTree = (RootCommandNode<CommandSource>)commandNodeDatas[buf.readVarInt()].node;
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		Object2IntMap<CommandNode<CommandSource>> object2IntMap = method_30944(this.commandTree);
		CommandNode<CommandSource>[] commandNodes = method_30945(object2IntMap);
		buf.writeVarInt(commandNodes.length);

		for (CommandNode<CommandSource> commandNode : commandNodes) {
			writeNode(buf, commandNode, object2IntMap);
		}

		buf.writeVarInt(object2IntMap.get(this.commandTree));
	}

	private static void method_30946(CommandTreeS2CPacket.CommandNodeData[] commandNodeDatas) {
		List<CommandTreeS2CPacket.CommandNodeData> list = Lists.<CommandTreeS2CPacket.CommandNodeData>newArrayList(commandNodeDatas);

		while (!list.isEmpty()) {
			boolean bl = list.removeIf(commandNodeData -> commandNodeData.build(commandNodeDatas));
			if (!bl) {
				throw new IllegalStateException("Server sent an impossible command tree");
			}
		}
	}

	private static Object2IntMap<CommandNode<CommandSource>> method_30944(RootCommandNode<CommandSource> rootCommandNode) {
		Object2IntMap<CommandNode<CommandSource>> object2IntMap = new Object2IntOpenHashMap<>();
		Queue<CommandNode<CommandSource>> queue = Queues.<CommandNode<CommandSource>>newArrayDeque();
		queue.add(rootCommandNode);

		CommandNode<CommandSource> commandNode;
		while ((commandNode = (CommandNode<CommandSource>)queue.poll()) != null) {
			if (!object2IntMap.containsKey(commandNode)) {
				int i = object2IntMap.size();
				object2IntMap.put(commandNode, i);
				queue.addAll(commandNode.getChildren());
				if (commandNode.getRedirect() != null) {
					queue.add(commandNode.getRedirect());
				}
			}
		}

		return object2IntMap;
	}

	private static CommandNode<CommandSource>[] method_30945(Object2IntMap<CommandNode<CommandSource>> object2IntMap) {
		CommandNode<CommandSource>[] commandNodes = new CommandNode[object2IntMap.size()];

		for (Entry<CommandNode<CommandSource>> entry : Object2IntMaps.fastIterable(object2IntMap)) {
			commandNodes[entry.getIntValue()] = (CommandNode<CommandSource>)entry.getKey();
		}

		return commandNodes;
	}

	private static CommandTreeS2CPacket.CommandNodeData readCommandNode(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		int[] is = packetByteBuf.readIntArray();
		int i = (b & 8) != 0 ? packetByteBuf.readVarInt() : 0;
		ArgumentBuilder<CommandSource, ?> argumentBuilder = readArgumentBuilder(packetByteBuf, b);
		return new CommandTreeS2CPacket.CommandNodeData(argumentBuilder, b, i, is);
	}

	@Nullable
	private static ArgumentBuilder<CommandSource, ?> readArgumentBuilder(PacketByteBuf packetByteBuf, byte b) {
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

	private static void writeNode(PacketByteBuf packetByteBuf, CommandNode<CommandSource> commandNode, Map<CommandNode<CommandSource>, Integer> map) {
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
	public RootCommandNode<CommandSource> getCommandTree() {
		return this.commandTree;
	}

	static class CommandNodeData {
		@Nullable
		private final ArgumentBuilder<CommandSource, ?> argumentBuilder;
		private final byte flags;
		private final int redirectNodeIndex;
		private final int[] childNodeIndices;
		@Nullable
		private CommandNode<CommandSource> node;

		private CommandNodeData(@Nullable ArgumentBuilder<CommandSource, ?> argumentBuilder, byte flags, int redirectNodeIndex, int[] childNodeIndices) {
			this.argumentBuilder = argumentBuilder;
			this.flags = flags;
			this.redirectNodeIndex = redirectNodeIndex;
			this.childNodeIndices = childNodeIndices;
		}

		public boolean build(CommandTreeS2CPacket.CommandNodeData[] previousNodes) {
			if (this.node == null) {
				if (this.argumentBuilder == null) {
					this.node = new RootCommandNode<>();
				} else {
					if ((this.flags & 8) != 0) {
						if (previousNodes[this.redirectNodeIndex].node == null) {
							return false;
						}

						this.argumentBuilder.redirect(previousNodes[this.redirectNodeIndex].node);
					}

					if ((this.flags & 4) != 0) {
						this.argumentBuilder.executes(commandContext -> 0);
					}

					this.node = this.argumentBuilder.build();
				}
			}

			for (int i : this.childNodeIndices) {
				if (previousNodes[i].node == null) {
					return false;
				}
			}

			for (int ix : this.childNodeIndices) {
				CommandNode<CommandSource> commandNode = previousNodes[ix].node;
				if (!(commandNode instanceof RootCommandNode)) {
					this.node.addChild(commandNode);
				}
			}

			return true;
		}
	}
}
