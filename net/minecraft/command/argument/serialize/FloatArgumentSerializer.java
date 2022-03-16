/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ArgumentHelper;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class FloatArgumentSerializer
implements ArgumentSerializer<FloatArgumentType, Properties> {
    @Override
    public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
        boolean bl = properties.min != -3.4028235E38f;
        boolean bl2 = properties.max != Float.MAX_VALUE;
        packetByteBuf.writeByte(ArgumentHelper.getMinMaxFlag(bl, bl2));
        if (bl) {
            packetByteBuf.writeFloat(properties.min);
        }
        if (bl2) {
            packetByteBuf.writeFloat(properties.max);
        }
    }

    @Override
    public Properties fromPacket(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        float f = ArgumentHelper.hasMinFlag(b) ? packetByteBuf.readFloat() : -3.4028235E38f;
        float g = ArgumentHelper.hasMaxFlag(b) ? packetByteBuf.readFloat() : Float.MAX_VALUE;
        return new Properties(f, g);
    }

    @Override
    public void writeJson(Properties properties, JsonObject jsonObject) {
        if (properties.min != -3.4028235E38f) {
            jsonObject.addProperty("min", Float.valueOf(properties.min));
        }
        if (properties.max != Float.MAX_VALUE) {
            jsonObject.addProperty("max", Float.valueOf(properties.max));
        }
    }

    @Override
    public Properties getArgumentTypeProperties(FloatArgumentType floatArgumentType) {
        return new Properties(floatArgumentType.getMinimum(), floatArgumentType.getMaximum());
    }

    @Override
    public /* synthetic */ ArgumentSerializer.ArgumentTypeProperties fromPacket(PacketByteBuf buf) {
        return this.fromPacket(buf);
    }

    public final class Properties
    implements ArgumentSerializer.ArgumentTypeProperties<FloatArgumentType> {
        final float min;
        final float max;

        Properties(float min, float max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public FloatArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return FloatArgumentType.floatArg(this.min, this.max);
        }

        @Override
        public ArgumentSerializer<FloatArgumentType, ?> getSerializer() {
            return FloatArgumentSerializer.this;
        }

        @Override
        public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return this.createType(commandRegistryAccess);
        }
    }
}

