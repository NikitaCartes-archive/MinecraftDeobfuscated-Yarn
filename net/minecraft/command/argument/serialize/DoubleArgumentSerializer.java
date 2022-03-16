/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class DoubleArgumentSerializer
implements ArgumentSerializer<DoubleArgumentType, Properties> {
    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        boolean bl = properties.min != -1.7976931348623157E308;
        boolean bl2 = properties.max != Double.MAX_VALUE;
        packetByteBuf.writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
        if (bl) {
            packetByteBuf.writeDouble(properties.min);
        }
        if (bl2) {
            packetByteBuf.writeDouble(properties.max);
        }
    }

    @Override
    public Properties fromPacket(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        double d = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readDouble() : -1.7976931348623157E308;
        double e = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readDouble() : Double.MAX_VALUE;
        return new Properties(d, e);
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        if (properties.min != -1.7976931348623157E308) {
            jsonObject.addProperty("min", properties.min);
        }
        if (properties.max != Double.MAX_VALUE) {
            jsonObject.addProperty("max", properties.max);
        }
    }

    @Override
    public Properties getArgumentTypeProperties(DoubleArgumentType doubleArgumentType) {
        return new Properties(doubleArgumentType.getMinimum(), doubleArgumentType.getMaximum());
    }

    @Override
    public /* synthetic */ ArgumentSerializer.ArgumentTypeProperties fromPacket(PacketByteBuf buf) {
        return this.fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<DoubleArgumentType> {
        final double min;
        final double max;

        Properties(double min, double max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public DoubleArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return DoubleArgumentType.doubleArg(this.min, this.max);
        }

        @Override
        public ArgumentSerializer<DoubleArgumentType, ?> getSerializer() {
            return DoubleArgumentSerializer.this;
        }

        @Override
        public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return this.createType(commandRegistryAccess);
        }
    }
}

