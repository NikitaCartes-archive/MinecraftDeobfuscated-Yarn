/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class StringArgumentSerializer
implements ArgumentSerializer<StringArgumentType, Properties> {
    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeEnumConstant(properties.type);
    }

    @Override
    public Properties fromPacket(PacketByteBuf packetByteBuf) {
        StringArgumentType.StringType stringType = packetByteBuf.readEnumConstant(StringArgumentType.StringType.class);
        return new Properties(stringType);
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        jsonObject.addProperty("type", switch (properties.type) {
            default -> throw new IncompatibleClassChangeError();
            case StringArgumentType.StringType.SINGLE_WORD -> "word";
            case StringArgumentType.StringType.QUOTABLE_PHRASE -> "phrase";
            case StringArgumentType.StringType.GREEDY_PHRASE -> "greedy";
        });
    }

    @Override
    public Properties getArgumentTypeProperties(StringArgumentType stringArgumentType) {
        return new Properties(stringArgumentType.getType());
    }

    @Override
    public /* synthetic */ ArgumentSerializer.ArgumentTypeProperties fromPacket(PacketByteBuf buf) {
        return this.fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<StringArgumentType> {
        final StringArgumentType.StringType type;

        public Properties(StringArgumentType.StringType type) {
            this.type = type;
        }

        @Override
        public StringArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return switch (this.type) {
                default -> throw new IncompatibleClassChangeError();
                case StringArgumentType.StringType.SINGLE_WORD -> StringArgumentType.word();
                case StringArgumentType.StringType.QUOTABLE_PHRASE -> StringArgumentType.string();
                case StringArgumentType.StringType.GREEDY_PHRASE -> StringArgumentType.greedyString();
            };
        }

        @Override
        public ArgumentSerializer<StringArgumentType, ?> getSerializer() {
            return StringArgumentSerializer.this;
        }

        @Override
        public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return this.createType(commandRegistryAccess);
        }
    }
}

