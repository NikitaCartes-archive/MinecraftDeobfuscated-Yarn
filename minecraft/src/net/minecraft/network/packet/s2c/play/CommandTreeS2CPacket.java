package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Queues;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import java.util.Queue;
import java.util.function.BiPredicate;
import javax.annotation.Nullable;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CommandTreeS2CPacket implements Packet<ClientPlayPacketListener> {
	private static final byte field_33317 = 3;
	private static final byte field_33318 = 4;
	private static final byte field_33319 = 8;
	private static final byte field_33320 = 16;
	private static final byte field_33321 = 0;
	private static final byte field_33322 = 1;
	private static final byte field_33323 = 2;
	private final int rootSize;
	private final List<CommandTreeS2CPacket.CommandNodeData> nodes;

	public CommandTreeS2CPacket(RootCommandNode<CommandSource> rootNode) {
		Object2IntMap<CommandNode<CommandSource>> object2IntMap = traverse(rootNode);
		this.nodes = collectNodes(object2IntMap);
		this.rootSize = object2IntMap.getInt(rootNode);
	}

	public CommandTreeS2CPacket(PacketByteBuf buf) {
		this.nodes = buf.readList(CommandTreeS2CPacket::readCommandNode);
		this.rootSize = buf.readVarInt();
		validate(this.nodes);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeCollection(this.nodes, (buf2, node) -> node.write(buf2));
		buf.writeVarInt(this.rootSize);
	}

	private static void validate(List<CommandTreeS2CPacket.CommandNodeData> nodeDatas, BiPredicate<CommandTreeS2CPacket.CommandNodeData, IntSet> validater) {
		IntSet intSet = new IntOpenHashSet(IntSets.fromTo(0, nodeDatas.size()));

		while (!intSet.isEmpty()) {
			boolean bl = intSet.removeIf(i -> validater.test((CommandTreeS2CPacket.CommandNodeData)nodeDatas.get(i), intSet));
			if (!bl) {
				throw new IllegalStateException("Server sent an impossible command tree");
			}
		}
	}

	private static void validate(List<CommandTreeS2CPacket.CommandNodeData> nodeDatas) {
		validate(nodeDatas, CommandTreeS2CPacket.CommandNodeData::validateRedirectNodeIndex);
		validate(nodeDatas, CommandTreeS2CPacket.CommandNodeData::validateChildNodeIndices);
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

	private static List<CommandTreeS2CPacket.CommandNodeData> collectNodes(Object2IntMap<CommandNode<CommandSource>> nodes) {
		ObjectArrayList<CommandTreeS2CPacket.CommandNodeData> objectArrayList = new ObjectArrayList<>(nodes.size());
		objectArrayList.size(nodes.size());

		for (Entry<CommandNode<CommandSource>> entry : Object2IntMaps.fastIterable(nodes)) {
			objectArrayList.set(entry.getIntValue(), createNodeData((CommandNode<CommandSource>)entry.getKey(), nodes));
		}

		return objectArrayList;
	}

	private static CommandTreeS2CPacket.CommandNodeData readCommandNode(PacketByteBuf buf) {
		byte b = buf.readByte();
		int[] is = buf.readIntArray();
		int i = (b & 8) != 0 ? buf.readVarInt() : 0;
		CommandTreeS2CPacket.SuggestableNode suggestableNode = readArgumentBuilder(buf, b);
		return new CommandTreeS2CPacket.CommandNodeData(suggestableNode, b, i, is);
	}

	@Nullable
	private static CommandTreeS2CPacket.SuggestableNode readArgumentBuilder(PacketByteBuf buf, byte flags) {
		int i = flags & 3;
		if (i == 2) {
			String string = buf.readString();
			int j = buf.readVarInt();
			ArgumentSerializer<?, ?> argumentSerializer = Registry.COMMAND_ARGUMENT_TYPE.get(j);
			if (argumentSerializer == null) {
				return null;
			} else {
				ArgumentSerializer.ArgumentTypeProperties<?> argumentTypeProperties = argumentSerializer.fromPacket(buf);
				Identifier identifier = (flags & 16) != 0 ? buf.readIdentifier() : null;
				return new CommandTreeS2CPacket.ArgumentNode(string, argumentTypeProperties, identifier);
			}
		} else if (i == 1) {
			String string = buf.readString();
			return new CommandTreeS2CPacket.LiteralNode(string);
		} else {
			return null;
		}
	}

	private static CommandTreeS2CPacket.CommandNodeData createNodeData(CommandNode<CommandSource> node, Object2IntMap<CommandNode<CommandSource>> nodes) {
		int i = 0;
		int j;
		if (node.getRedirect() != null) {
			i |= 8;
			j = nodes.getInt(node.getRedirect());
		} else {
			j = 0;
		}

		if (node.getCommand() != null) {
			i |= 4;
		}

		CommandTreeS2CPacket.SuggestableNode suggestableNode;
		if (node instanceof RootCommandNode) {
			i |= 0;
			suggestableNode = null;
		} else if (node instanceof ArgumentCommandNode<CommandSource, ?> argumentCommandNode) {
			suggestableNode = new CommandTreeS2CPacket.ArgumentNode(argumentCommandNode);
			i |= 2;
			if (argumentCommandNode.getCustomSuggestions() != null) {
				i |= 16;
			}
		} else {
			if (!(node instanceof LiteralCommandNode literalCommandNode)) {
				throw new UnsupportedOperationException("Unknown node type " + node);
			}

			suggestableNode = new CommandTreeS2CPacket.LiteralNode(literalCommandNode.getLiteral());
			i |= 1;
		}

		int[] is = node.getChildren().stream().mapToInt(nodes::getInt).toArray();
		return new CommandTreeS2CPacket.CommandNodeData(suggestableNode, i, j, is);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCommandTree(this);
	}

	public RootCommandNode<CommandSource> getCommandTree(CommandRegistryAccess commandRegistryAccess) {
		return (RootCommandNode<CommandSource>)new CommandTreeS2CPacket.CommandTree(commandRegistryAccess, this.nodes).getNode(this.rootSize);
	}

	static class ArgumentNode implements CommandTreeS2CPacket.SuggestableNode {
		private final String name;
		private final ArgumentSerializer.ArgumentTypeProperties<?> properties;
		@Nullable
		private final Identifier id;

		@Nullable
		private static Identifier computeId(@Nullable SuggestionProvider<CommandSource> provider) {
			return provider != null ? SuggestionProviders.computeId(provider) : null;
		}

		ArgumentNode(String name, ArgumentSerializer.ArgumentTypeProperties<?> properties, @Nullable Identifier id) {
			this.name = name;
			this.properties = properties;
			this.id = id;
		}

		public ArgumentNode(ArgumentCommandNode<CommandSource, ?> node) {
			this(node.getName(), ArgumentTypes.getArgumentTypeProperties(node.getType()), computeId(node.getCustomSuggestions()));
		}

		@Override
		public ArgumentBuilder<CommandSource, ?> createArgumentBuilder(CommandRegistryAccess commandRegistryAccess) {
			ArgumentType<?> argumentType = this.properties.createType(commandRegistryAccess);
			RequiredArgumentBuilder<CommandSource, ?> requiredArgumentBuilder = RequiredArgumentBuilder.argument(this.name, argumentType);
			if (this.id != null) {
				requiredArgumentBuilder.suggests(SuggestionProviders.byId(this.id));
			}

			return requiredArgumentBuilder;
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeString(this.name);
			write(buf, this.properties);
			if (this.id != null) {
				buf.writeIdentifier(this.id);
			}
		}

		private static <A extends ArgumentType<?>> void write(PacketByteBuf buf, ArgumentSerializer.ArgumentTypeProperties<A> properties) {
			write(buf, properties.getSerializer(), properties);
		}

		private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> void write(
			PacketByteBuf buf, ArgumentSerializer<A, T> serializer, ArgumentSerializer.ArgumentTypeProperties<A> properties
		) {
			buf.writeVarInt(Registry.COMMAND_ARGUMENT_TYPE.getRawId(serializer));
			serializer.writePacket((T)properties, buf);
		}
	}

	static class CommandNodeData {
		@Nullable
		final CommandTreeS2CPacket.SuggestableNode suggestableNode;
		final int flags;
		final int redirectNodeIndex;
		final int[] childNodeIndices;

		CommandNodeData(@Nullable CommandTreeS2CPacket.SuggestableNode suggestableNode, int flags, int redirectNodeIndex, int[] childNodeIndices) {
			this.suggestableNode = suggestableNode;
			this.flags = flags;
			this.redirectNodeIndex = redirectNodeIndex;
			this.childNodeIndices = childNodeIndices;
		}

		public void write(PacketByteBuf buf) {
			buf.writeByte(this.flags);
			buf.writeIntArray(this.childNodeIndices);
			if ((this.flags & 8) != 0) {
				buf.writeVarInt(this.redirectNodeIndex);
			}

			if (this.suggestableNode != null) {
				this.suggestableNode.write(buf);
			}
		}

		public boolean validateRedirectNodeIndex(IntSet indices) {
			return (this.flags & 8) != 0 ? !indices.contains(this.redirectNodeIndex) : true;
		}

		public boolean validateChildNodeIndices(IntSet indices) {
			for (int i : this.childNodeIndices) {
				if (indices.contains(i)) {
					return false;
				}
			}

			return true;
		}
	}

	static class CommandTree {
		private final CommandRegistryAccess commandRegistryAccess;
		private final List<CommandTreeS2CPacket.CommandNodeData> nodeDatas;
		private final List<CommandNode<CommandSource>> nodes;

		CommandTree(CommandRegistryAccess commandRegistryAccess, List<CommandTreeS2CPacket.CommandNodeData> nodeDatas) {
			this.commandRegistryAccess = commandRegistryAccess;
			this.nodeDatas = nodeDatas;
			ObjectArrayList<CommandNode<CommandSource>> objectArrayList = new ObjectArrayList<>();
			objectArrayList.size(nodeDatas.size());
			this.nodes = objectArrayList;
		}

		public CommandNode<CommandSource> getNode(int index) {
			CommandNode<CommandSource> commandNode = (CommandNode<CommandSource>)this.nodes.get(index);
			if (commandNode != null) {
				return commandNode;
			} else {
				CommandTreeS2CPacket.CommandNodeData commandNodeData = (CommandTreeS2CPacket.CommandNodeData)this.nodeDatas.get(index);
				CommandNode<CommandSource> commandNode2;
				if (commandNodeData.suggestableNode == null) {
					commandNode2 = new RootCommandNode<>();
				} else {
					ArgumentBuilder<CommandSource, ?> argumentBuilder = commandNodeData.suggestableNode.createArgumentBuilder(this.commandRegistryAccess);
					if ((commandNodeData.flags & 8) != 0) {
						argumentBuilder.redirect(this.getNode(commandNodeData.redirectNodeIndex));
					}

					if ((commandNodeData.flags & 4) != 0) {
						argumentBuilder.executes(context -> 0);
					}

					commandNode2 = argumentBuilder.build();
				}

				this.nodes.set(index, commandNode2);

				for (int i : commandNodeData.childNodeIndices) {
					CommandNode<CommandSource> commandNode3 = this.getNode(i);
					if (!(commandNode3 instanceof RootCommandNode)) {
						commandNode2.addChild(commandNode3);
					}
				}

				return commandNode2;
			}
		}
	}

	static class LiteralNode implements CommandTreeS2CPacket.SuggestableNode {
		private final String literal;

		LiteralNode(String literal) {
			this.literal = literal;
		}

		@Override
		public ArgumentBuilder<CommandSource, ?> createArgumentBuilder(CommandRegistryAccess commandRegistryAccess) {
			return LiteralArgumentBuilder.literal(this.literal);
		}

		@Override
		public void write(PacketByteBuf buf) {
			buf.writeString(this.literal);
		}
	}

	interface SuggestableNode {
		ArgumentBuilder<CommandSource, ?> createArgumentBuilder(CommandRegistryAccess commandRegistryAccess);

		void write(PacketByteBuf buf);
	}
}
