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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class CommandTreeS2CPacket implements Packet<ClientPlayPacketListener> {
	private static final byte field_33317 = 3;
	private static final byte field_33318 = 4;
	private static final byte field_33319 = 8;
	private static final byte field_33320 = 16;
	private static final byte field_33321 = 0;
	private static final byte field_33322 = 1;
	private static final byte field_33323 = 2;
	private final RootCommandNode<CommandSource> commandTree;

	public CommandTreeS2CPacket(RootCommandNode<CommandSource> commandTree) {
		this.commandTree = commandTree;
	}

	public CommandTreeS2CPacket(PacketByteBuf buf) {
		List<CommandTreeS2CPacket.CommandNodeData> list = buf.readList(CommandTreeS2CPacket::readCommandNode);
		build(list);
		int i = buf.readVarInt();
		this.commandTree = (RootCommandNode<CommandSource>)((CommandTreeS2CPacket.CommandNodeData)list.get(i)).node;
	}

	@Override
	public void write(PacketByteBuf buf) {
		Object2IntMap<CommandNode<CommandSource>> object2IntMap = traverse(this.commandTree);
		List<CommandNode<CommandSource>> list = collectNodes(object2IntMap);
		buf.writeCollection(list, (packetByteBuf, node) -> writeNode(packetByteBuf, node, object2IntMap));
		buf.writeVarInt(object2IntMap.get(this.commandTree));
	}

	private static void build(List<CommandTreeS2CPacket.CommandNodeData> nodeDatas) {
		List<CommandTreeS2CPacket.CommandNodeData> list = Lists.<CommandTreeS2CPacket.CommandNodeData>newArrayList(nodeDatas);

		while (!list.isEmpty()) {
			boolean bl = list.removeIf(nodeData -> nodeData.build(nodeDatas));
			if (!bl) {
				throw new IllegalStateException("Server sent an impossible command tree");
			}
		}
	}

	private static Object2IntMap<CommandNode<CommandSource>> traverse(RootCommandNode<CommandSource> commandTree) {
		Object2IntMap<CommandNode<CommandSource>> object2IntMap = new Object2IntOpenHashMap<>();
		Queue<CommandNode<CommandSource>> queue = Queues.<CommandNode<CommandSource>>newArrayDeque();
		queue.add(commandTree);

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

	private static List<CommandNode<CommandSource>> collectNodes(Object2IntMap<CommandNode<CommandSource>> nodes) {
		ObjectArrayList<CommandNode<CommandSource>> objectArrayList = new ObjectArrayList<>(nodes.size());
		objectArrayList.size(nodes.size());

		for (Entry<CommandNode<CommandSource>> entry : Object2IntMaps.fastIterable(nodes)) {
			objectArrayList.set(entry.getIntValue(), (CommandNode<CommandSource>)entry.getKey());
		}

		return objectArrayList;
	}

	private static CommandTreeS2CPacket.CommandNodeData readCommandNode(PacketByteBuf buf) {
		byte b = buf.readByte();
		int[] is = buf.readIntArray();
		int i = (b & 8) != 0 ? buf.readVarInt() : 0;
		ArgumentBuilder<CommandSource, ?> argumentBuilder = readArgumentBuilder(buf, b);
		return new CommandTreeS2CPacket.CommandNodeData(argumentBuilder, b, i, is);
	}

	@Nullable
	private static ArgumentBuilder<CommandSource, ?> readArgumentBuilder(PacketByteBuf buf, byte b) {
		int i = b & 3;
		if (i == 2) {
			String string = buf.readString();
			ArgumentType<?> argumentType = ArgumentTypes.fromPacket(buf);
			if (argumentType == null) {
				return null;
			} else {
				RequiredArgumentBuilder<CommandSource, ?> requiredArgumentBuilder = RequiredArgumentBuilder.argument(string, argumentType);
				if ((b & 16) != 0) {
					requiredArgumentBuilder.suggests(SuggestionProviders.byId(buf.readIdentifier()));
				}

				return requiredArgumentBuilder;
			}
		} else {
			return i == 1 ? LiteralArgumentBuilder.literal(buf.readString()) : null;
		}
	}

	private static void writeNode(PacketByteBuf buf, CommandNode<CommandSource> node, Map<CommandNode<CommandSource>, Integer> nodeToIndex) {
		byte b = 0;
		if (node.getRedirect() != null) {
			b = (byte)(b | 8);
		}

		if (node.getCommand() != null) {
			b = (byte)(b | 4);
		}

		if (node instanceof RootCommandNode) {
			b = (byte)(b | 0);
		} else if (node instanceof ArgumentCommandNode) {
			b = (byte)(b | 2);
			if (((ArgumentCommandNode)node).getCustomSuggestions() != null) {
				b = (byte)(b | 16);
			}
		} else {
			if (!(node instanceof LiteralCommandNode)) {
				throw new UnsupportedOperationException("Unknown node type " + node);
			}

			b = (byte)(b | 1);
		}

		buf.writeByte(b);
		buf.writeVarInt(node.getChildren().size());

		for (CommandNode<CommandSource> commandNode : node.getChildren()) {
			buf.writeVarInt((Integer)nodeToIndex.get(commandNode));
		}

		if (node.getRedirect() != null) {
			buf.writeVarInt((Integer)nodeToIndex.get(node.getRedirect()));
		}

		if (node instanceof ArgumentCommandNode) {
			ArgumentCommandNode<CommandSource, ?> argumentCommandNode = (ArgumentCommandNode<CommandSource, ?>)node;
			buf.writeString(argumentCommandNode.getName());
			ArgumentTypes.toPacket(buf, argumentCommandNode.getType());
			if (argumentCommandNode.getCustomSuggestions() != null) {
				buf.writeIdentifier(SuggestionProviders.computeName(argumentCommandNode.getCustomSuggestions()));
			}
		} else if (node instanceof LiteralCommandNode) {
			buf.writeString(((LiteralCommandNode)node).getLiteral());
		}
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCommandTree(this);
	}

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

		public boolean build(List<CommandTreeS2CPacket.CommandNodeData> list) {
			if (this.node == null) {
				if (this.argumentBuilder == null) {
					this.node = new RootCommandNode<>();
				} else {
					if ((this.flags & 8) != 0) {
						if (((CommandTreeS2CPacket.CommandNodeData)list.get(this.redirectNodeIndex)).node == null) {
							return false;
						}

						this.argumentBuilder.redirect(((CommandTreeS2CPacket.CommandNodeData)list.get(this.redirectNodeIndex)).node);
					}

					if ((this.flags & 4) != 0) {
						this.argumentBuilder.executes(context -> 0);
					}

					this.node = this.argumentBuilder.build();
				}
			}

			for (int i : this.childNodeIndices) {
				if (((CommandTreeS2CPacket.CommandNodeData)list.get(i)).node == null) {
					return false;
				}
			}

			for (int ix : this.childNodeIndices) {
				CommandNode<CommandSource> commandNode = ((CommandTreeS2CPacket.CommandNodeData)list.get(ix)).node;
				if (!(commandNode instanceof RootCommandNode)) {
					this.node.addChild(commandNode);
				}
			}

			return true;
		}
	}
}
