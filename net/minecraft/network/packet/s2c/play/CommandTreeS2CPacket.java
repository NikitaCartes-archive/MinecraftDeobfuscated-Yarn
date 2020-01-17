/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.command.CommandSource;
import net.minecraft.util.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public class CommandTreeS2CPacket
implements Packet<ClientPlayPacketListener> {
    private RootCommandNode<CommandSource> commandTree;

    public CommandTreeS2CPacket() {
    }

    public CommandTreeS2CPacket(RootCommandNode<CommandSource> commandTree) {
        this.commandTree = commandTree;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        CommandNodeData[] commandNodeDatas = new CommandNodeData[buf.readVarInt()];
        ArrayDeque<CommandNodeData> deque = new ArrayDeque<CommandNodeData>(commandNodeDatas.length);
        for (int i = 0; i < commandNodeDatas.length; ++i) {
            commandNodeDatas[i] = this.readCommandNode(buf);
            deque.add(commandNodeDatas[i]);
        }
        while (!deque.isEmpty()) {
            boolean bl = false;
            Iterator iterator = deque.iterator();
            while (iterator.hasNext()) {
                CommandNodeData commandNodeData = (CommandNodeData)iterator.next();
                if (!commandNodeData.build(commandNodeDatas)) continue;
                iterator.remove();
                bl = true;
            }
            if (bl) continue;
            throw new IllegalStateException("Server sent an impossible command tree");
        }
        this.commandTree = (RootCommandNode)commandNodeDatas[buf.readVarInt()].node;
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        HashMap<CommandNode<CommandSource>, Integer> map = Maps.newHashMap();
        ArrayDeque deque = new ArrayDeque();
        deque.add(this.commandTree);
        while (!deque.isEmpty()) {
            CommandNode commandNode = (CommandNode)deque.pollFirst();
            if (map.containsKey(commandNode)) continue;
            int i = map.size();
            map.put(commandNode, i);
            deque.addAll(commandNode.getChildren());
            if (commandNode.getRedirect() == null) continue;
            deque.add(commandNode.getRedirect());
        }
        CommandNode[] commandNodes = new CommandNode[map.size()];
        for (Map.Entry entry : map.entrySet()) {
            commandNodes[((Integer)entry.getValue()).intValue()] = (CommandNode)entry.getKey();
        }
        buf.writeVarInt(commandNodes.length);
        for (CommandNode commandNode2 : commandNodes) {
            this.writeNode(buf, commandNode2, map);
        }
        buf.writeVarInt((Integer)map.get(this.commandTree));
    }

    private CommandNodeData readCommandNode(PacketByteBuf buf) {
        byte b = buf.readByte();
        int[] is = buf.readIntArray();
        int i = (b & 8) != 0 ? buf.readVarInt() : 0;
        ArgumentBuilder<CommandSource, ?> argumentBuilder = this.readArgumentBuilder(buf, b);
        return new CommandNodeData(argumentBuilder, b, i, is);
    }

    @Nullable
    private ArgumentBuilder<CommandSource, ?> readArgumentBuilder(PacketByteBuf buf, byte b) {
        int i = b & 3;
        if (i == 2) {
            String string = buf.readString(Short.MAX_VALUE);
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
            return LiteralArgumentBuilder.literal(buf.readString(Short.MAX_VALUE));
        }
        return null;
    }

    private void writeNode(PacketByteBuf buf, CommandNode<CommandSource> node, Map<CommandNode<CommandSource>, Integer> map) {
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
            buf.writeVarInt(map.get(commandNode));
        }
        if (node.getRedirect() != null) {
            buf.writeVarInt(map.get(node.getRedirect()));
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

    @Environment(value=EnvType.CLIENT)
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

        private CommandNodeData(@Nullable ArgumentBuilder<CommandSource, ?> argumentBuilder, byte flags, int redirectNodeIndex, int[] childNodeIndices) {
            this.argumentBuilder = argumentBuilder;
            this.flags = flags;
            this.redirectNodeIndex = redirectNodeIndex;
            this.childNodeIndices = childNodeIndices;
        }

        public boolean build(CommandNodeData[] previousNodes) {
            if (this.node == null) {
                if (this.argumentBuilder == null) {
                    this.node = new RootCommandNode<CommandSource>();
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
                if (previousNodes[i].node != null) continue;
                return false;
            }
            for (int i : this.childNodeIndices) {
                CommandNode<CommandSource> commandNode = previousNodes[i].node;
                if (commandNode instanceof RootCommandNode) continue;
                this.node.addChild(commandNode);
            }
            return true;
        }
    }
}

