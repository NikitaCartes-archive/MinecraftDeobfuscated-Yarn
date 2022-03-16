/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class LongArgumentSerializer
implements ArgumentSerializer<LongArgumentType, Properties> {
    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        boolean bl = properties.min != Long.MIN_VALUE;
        boolean bl2 = properties.max != Long.MAX_VALUE;
        packetByteBuf.writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
        if (bl) {
            packetByteBuf.writeLong(properties.min);
        }
        if (bl2) {
            packetByteBuf.writeLong(properties.max);
        }
    }

    @Override
    public Properties fromPacket(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        long l = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readLong() : Long.MIN_VALUE;
        long m = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readLong() : Long.MAX_VALUE;
        return new Properties(l, m);
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        if (properties.min != Long.MIN_VALUE) {
            jsonObject.addProperty("min", properties.min);
        }
        if (properties.max != Long.MAX_VALUE) {
            jsonObject.addProperty("max", properties.max);
        }
    }

    @Override
    public Properties getArgumentTypeProperties(LongArgumentType longArgumentType) {
        return new Properties(longArgumentType.getMinimum(), longArgumentType.getMaximum());
    }

    @Override
    public /* synthetic */ ArgumentSerializer.ArgumentTypeProperties fromPacket(PacketByteBuf buf) {
        return this.fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<LongArgumentType> {
        final long min;
        final long max;

        Properties(long min, long max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public LongArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return LongArgumentType.longArg(this.min, this.max);
        }

        @Override
        public ArgumentSerializer<LongArgumentType, ?> getSerializer() {
            return LongArgumentSerializer.this;
        }

        @Override
        public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return this.createType(commandRegistryAccess);
        }
    }
}

