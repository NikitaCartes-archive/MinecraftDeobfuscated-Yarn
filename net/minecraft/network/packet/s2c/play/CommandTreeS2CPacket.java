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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.jetbrains.annotations.Nullable;

public class CommandTreeS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final RootCommandNode<CommandSource> commandTree;

    public CommandTreeS2CPacket(RootCommandNode<CommandSource> commandTree) {
        this.commandTree = commandTree;
    }

    public CommandTreeS2CPacket(PacketByteBuf packetByteBuf) {
        List<CommandNodeData> list = packetByteBuf.method_34066(CommandTreeS2CPacket::readCommandNode);
        CommandTreeS2CPacket.method_30946(list);
        int i = packetByteBuf.readVarInt();
        this.commandTree = (RootCommandNode)list.get(i).node;
    }

    @Override
    public void write(PacketByteBuf buf) {
        Object2IntMap<CommandNode<CommandSource>> object2IntMap = CommandTreeS2CPacket.method_30944(this.commandTree);
        List<CommandNode<CommandSource>> list = CommandTreeS2CPacket.method_30945(object2IntMap);
        buf.method_34062(list, (packetByteBuf, commandNode) -> CommandTreeS2CPacket.writeNode(packetByteBuf, commandNode, object2IntMap));
        buf.writeVarInt(object2IntMap.get(this.commandTree));
    }

    private static void method_30946(List<CommandNodeData> list) {
        ArrayList<CommandNodeData> list2 = Lists.newArrayList(list);
        while (!list2.isEmpty()) {
            boolean bl = list2.removeIf(commandNodeData -> commandNodeData.build(list));
            if (bl) continue;
            throw new IllegalStateException("Server sent an impossible command tree");
        }
    }

    private static Object2IntMap<CommandNode<CommandSource>> method_30944(RootCommandNode<CommandSource> rootCommandNode) {
        CommandNode commandNode;
        Object2IntOpenHashMap<CommandNode<CommandSource>> object2IntMap = new Object2IntOpenHashMap<CommandNode<CommandSource>>();
        ArrayDeque queue = Queues.newArrayDeque();
        queue.add(rootCommandNode);
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

    private static List<CommandNode<CommandSource>> method_30945(Object2IntMap<CommandNode<CommandSource>> object2IntMap) {
        ObjectArrayList<CommandNode<CommandSource>> objectArrayList = new ObjectArrayList<CommandNode<CommandSource>>(object2IntMap.size());
        objectArrayList.size(object2IntMap.size());
        for (Object2IntMap.Entry entry : Object2IntMaps.fastIterable(object2IntMap)) {
            objectArrayList.set(entry.getIntValue(), (CommandNode<CommandSource>)entry.getKey());
        }
        return objectArrayList;
    }

    private static CommandNodeData readCommandNode(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        int[] is = packetByteBuf.readIntArray();
        int i = (b & 8) != 0 ? packetByteBuf.readVarInt() : 0;
        ArgumentBuilder<CommandSource, ?> argumentBuilder = CommandTreeS2CPacket.readArgumentBuilder(packetByteBuf, b);
        return new CommandNodeData(argumentBuilder, b, i, is);
    }

    @Nullable
    private static ArgumentBuilder<CommandSource, ?> readArgumentBuilder(PacketByteBuf packetByteBuf, byte b) {
        int i = b & 3;
        if (i == 2) {
            String string = packetByteBuf.readString();
            ArgumentType<?> argumentType = ArgumentTypes.fromPacket(packetByteBuf);
            if (argumentType == null) {
                return null;
            }
            RequiredArgumentBuilder<CommandSource, ?> requiredArgumentBuilder = RequiredArgumentBuilder.argument(string, argumentType);
            if ((b & 0x10) != 0) {
                requiredArgumentBuilder.suggests(SuggestionProviders.byId(packetByteBuf.readIdentifier()));
            }
            return requiredArgumentBuilder;
        }
        if (i == 1) {
            return LiteralArgumentBuilder.literal(packetByteBuf.readString());
        }
        return null;
    }

    private static void writeNode(PacketByteBuf packetByteBuf, CommandNode<CommandSource> commandNode, Map<CommandNode<CommandSource>, Integer> map) {
        int b = 0;
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
                b = (byte)(b | 0x10);
            }
        } else if (commandNode instanceof LiteralCommandNode) {
            b = (byte)(b | 1);
        } else {
            throw new UnsupportedOperationException("Unknown node type " + commandNode);
        }
        packetByteBuf.writeByte(b);
        packetByteBuf.writeVarInt(commandNode.getChildren().size());
        for (CommandNode<CommandSource> commandNode2 : commandNode.getChildren()) {
            packetByteBuf.writeVarInt(map.get(commandNode2));
        }
        if (commandNode.getRedirect() != null) {
            packetByteBuf.writeVarInt(map.get(commandNode.getRedirect()));
        }
        if (commandNode instanceof ArgumentCommandNode) {
            ArgumentCommandNode argumentCommandNode = (ArgumentCommandNode)commandNode;
            packetByteBuf.writeString(argumentCommandNode.getName());
            ArgumentTypes.toPacket(packetByteBuf, argumentCommandNode.getType());
            if (argumentCommandNode.getCustomSuggestions() != null) {
                packetByteBuf.writeIdentifier(SuggestionProviders.computeName(argumentCommandNode.getCustomSuggestions()));
            }
        } else if (commandNode instanceof LiteralCommandNode) {
            packetByteBuf.writeString(((LiteralCommandNode)commandNode).getLiteral());
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
        @Nullable
        private CommandNode<CommandSource> node;

        private CommandNodeData(@Nullable ArgumentBuilder<CommandSource, ?> argumentBuilder, byte flags, int redirectNodeIndex, int[] childNodeIndices) {
            this.argumentBuilder = argumentBuilder;
            this.flags = flags;
            this.redirectNodeIndex = redirectNodeIndex;
            this.childNodeIndices = childNodeIndices;
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
                        this.argumentBuilder.executes(commandContext -> 0);
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

