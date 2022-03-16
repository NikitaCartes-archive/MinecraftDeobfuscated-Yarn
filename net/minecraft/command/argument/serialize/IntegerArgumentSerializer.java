/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class IntegerArgumentSerializer
implements ArgumentSerializer<IntegerArgumentType, Properties> {
    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        boolean bl = properties.min != Integer.MIN_VALUE;
        boolean bl2 = properties.max != Integer.MAX_VALUE;
        packetByteBuf.writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
        if (bl) {
            packetByteBuf.writeInt(properties.min);
        }
        if (bl2) {
            packetByteBuf.writeInt(properties.max);
        }
    }

    @Override
    public Properties fromPacket(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        int i = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readInt() : Integer.MIN_VALUE;
        int j = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readInt() : Integer.MAX_VALUE;
        return new Properties(i, j);
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        if (properties.min != Integer.MIN_VALUE) {
            jsonObject.addProperty("min", properties.min);
        }
        if (properties.max != Integer.MAX_VALUE) {
            jsonObject.addProperty("max", properties.max);
        }
    }

    @Override
    public Properties getArgumentTypeProperties(IntegerArgumentType integerArgumentType) {
        return new Properties(integerArgumentType.getMinimum(), integerArgumentType.getMaximum());
    }

    @Override
    public /* synthetic */ ArgumentSerializer.ArgumentTypeProperties fromPacket(PacketByteBuf buf) {
        return this.fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<IntegerArgumentType> {
        final int min;
        final int max;

        Properties(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public IntegerArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return IntegerArgumentType.integer(this.min, this.max);
        }

        @Override
        public ArgumentSerializer<IntegerArgumentType, ?> getSerializer() {
            return IntegerArgumentSerializer.this;
        }

        @Override
        public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return this.createType(commandRegistryAccess);
        }
    }
}

