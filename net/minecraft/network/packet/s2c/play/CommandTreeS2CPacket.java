/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.jetbrains.annotations.Nullable;

public class CommandTreeS2CPacket
implements Packet<ClientPlayPacketListener> {
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
        List<CommandNodeData> list = buf.readList(CommandTreeS2CPacket::readCommandNode);
        CommandTreeS2CPacket.build(list);
        int i = buf.readVarInt();
        this.commandTree = (RootCommandNode)list.get((int)i).node;
    }

    @Override
    public void write(PacketByteBuf buf) {
        Object2IntMap<CommandNode<CommandSource>> object2IntMap = CommandTreeS2CPacket.traverse(this.commandTree);
        List<CommandNode<CommandSource>> list = CommandTreeS2CPacket.collectNodes(object2IntMap);
        buf.writeCollection(list, (packetByteBuf, node) -> CommandTreeS2CPacket.writeNode(packetByteBuf, node, object2IntMap));
        buf.writeVarInt(object2IntMap.get(this.commandTree));
    }

    private static void build(List<CommandNodeData> nodeDatas) {
        ArrayList<CommandNodeData> list = Lists.newArrayList(nodeDatas);
        while (!list.isEmpty()) {
            boolean bl = list.removeIf(nodeData -> nodeData.build(nodeDatas));
            if (bl) continue;
            throw new IllegalStateException("Server sent an impossible command tree");
        }
    }

    private static Object2IntMap<CommandNode<CommandSource>> traverse(RootCommandNode<CommandSource> commandTree) {
        CommandNode commandNode;
        Object2IntOpenHashMap<CommandNode<CommandSource>> object2IntMap = new Object2IntOpenHashMap<CommandNode<CommandSource>>();
        ArrayDeque queue = Queues.newArrayDeque();
        queue.add(commandTree);
        while ((commandNode = (CommandNode)queue.poll()) != null) {
            if (object2IntMap.containsKey(commandNode)) continue;
            int i = object2IntMap.size();
            object2IntMap.put((CommandNode<CommandSource>)commandNode, i);
            queue.addAll(commandNode.getChildren());
            if (commandNode.getRedirect() == null) continue;
            queue.add(commandNode.getRedirect());
        }
        return object2IntMap;
    }

    private static List<CommandNode<CommandSource>> collectNodes(Object2IntMap<CommandNode<CommandSource>> nodes) {
        ObjectArrayList<CommandNode<CommandSource>> objectArrayList = new ObjectArrayList<CommandNode<CommandSource>>(nodes.size());
        objectArrayList.size(nodes.size());
        for (Object2IntMap.Entry entry : Object2IntMaps.fastIterable(nodes)) {
            objectArrayList.set(entry.getIntValue(), (CommandNode)entry.getKey());
        }
        return objectArrayList;
    }

    private static CommandNodeData readCommandNode(PacketByteBuf buf) {
        byte b = buf.readByte();
        int[] is = buf.readIntArray();
        int i = (b & 8) != 0 ? buf.readVarInt() : 0;
        ArgumentBuilder<CommandSource, ?> argumentBuilder = CommandTreeS2CPacket.readArgumentBuilder(buf, b);
        return new CommandNodeData(argumentBuilder, b, i, is);
    }

    @Nullable
    private static ArgumentBuilder<CommandSource, ?> readArgumentBuilder(PacketByteBuf buf, byte b) {
        int i = b & 3;
        if (i == 2) {
            String string = buf.readString();
            ArgumentType<?> argumentType = ArgumentTypes.fromPacket(buf);
            if (argumentType == null) {
                return null;
            }
            RequiredArgumentBuilder<CommandSource, ?> requiredArgumentBuilder = RequiredArgumentBuilder.argument(string, argumentType);
            if ((b & 0x10) != 0) {
                requiredArgumentBuilder.suggests(SuggestionProviders.byId(buf.readIdentifier()));
            }
            return requiredArgumentBuilder;
        }
        if (i == 1) {
            return LiteralArgumentBuilder.literal(buf.readString());
        }
        return null;
    }

    private static void writeNode(PacketByteBuf buf, CommandNode<CommandSource> node, Map<CommandNode<CommandSource>, Integer> nodeToIndex) {
        int b = 0;
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
                b = (byte)(b | 0x10);
            }
        } else if (node instanceof LiteralCommandNode) {
            b = (byte)(b | 1);
        } else {
            throw new UnsupportedOperationException("Unknown node type " + node);
        }
        buf.writeByte(b);
        buf.writeVarInt(node.getChildren().size());
        for (CommandNode<CommandSource> commandNode : node.getChildren()) {
            buf.writeVarInt(nodeToIndex.get(commandNode));
        }
        if (node.getRedirect() != null) {
            buf.writeVarInt(nodeToIndex.get(node.getRedirect()));
        }
        if (node instanceof ArgumentCommandNode) {
            ArgumentCommandNode argumentCommandNode = (ArgumentCommandNode)node;
            buf.writeString(argumentCommandNode.getName());
            ArgumentTypes.toPacket(buf, argumentCommandNode.getType());
            if (argumentCommandNode.getCustomSuggestions() != null) {
                buf.writeIdentifier(SuggestionProviders.computeName(argumentCommandNode.getCustomSuggestions()));
            }
        } else if (node instanceof LiteralCommandNode) {
            buf.writeString(((LiteralCommandNode)node).getLiteral());
        }
    }

    @Override
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
        CommandNode<CommandSource> node;

        CommandNodeData(@Nullable ArgumentBuilder<CommandSource, ?> argumentBuilder, byte b, int i, int[] is) {
            this.argumentBuilder = argumentBuilder;
            this.flags = b;
            this.redirectNodeIndex = i;
            this.childNodeIndices = is;
        }

        public boolean build(List<CommandNodeData> list) {
            if (this.node == null) {
                if (this.argumentBuilder == null) {
                    this.node = new RootCommandNode<CommandSource>();
                } else {
                    if ((this.flags & 8) != 0) {
                        if (list.get((int)this.redirectNodeIndex).node == null) {
                            return false;
                        }
                        this.argumentBuilder.redirect(list.get((int)this.redirectNodeIndex).node);
                    }
                    if ((this.flags & 4) != 0) {
                        this.argumentBuilder.executes(context -> 0);
                    }
                    this.node = this.argumentBuilder.build();
                }
            }
            for (int i : this.childNodeIndices) {
                if (list.get((int)i).node != null) continue;
                return false;
            }
            for (int i : this.childNodeIndices) {
                CommandNode<CommandSource> commandNode = list.get((int)i).node;
                if (commandNode instanceof RootCommandNode) continue;
                this.node.addChild(commandNode);
            }
            return true;
        }
    }
}

