/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.argument.BrigadierArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class DoubleArgumentSerializer
implements ArgumentSerializer<DoubleArgumentType> {
    @Override
    public void toPacket(DoubleArgumentType doubleArgumentType, PacketByteBuf packetByteBuf) {
        boolean bl = doubleArgumentType.getMinimum() != -1.7976931348623157E308;
        boolean bl2 = doubleArgumentType.getMaximum() != Double.MAX_VALUE;
        packetByteBuf.writeByte(BrigadierArgumentTypes.createFlag(bl, bl2));
        if (bl) {
            packetByteBuf.writeDouble(doubleArgumentType.getMinimum());
        }
        if (bl2) {
            packetByteBuf.writeDouble(doubleArgumentType.getMaximum());
        }
    }

    @Override
    public DoubleArgumentType fromPacket(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        double d = BrigadierArgumentTypes.hasMin(b) ? packetByteBuf.readDouble() : -1.7976931348623157E308;
        double e = BrigadierArgumentTypes.hasMax(b) ? packetByteBuf.readDouble() : Double.MAX_VALUE;
        return DoubleArgumentType.doubleArg(d, e);
    }

    @Override
    public void toJson(DoubleArgumentType doubleArgumentType, JsonObject jsonObject) {
        if (doubleArgumentType.getMinimum() != -1.7976931348623157E308) {
            jsonObject.addProperty("min", doubleArgumentType.getMinimum());
        }
        if (doubleArgumentType.getMaximum() != Double.MAX_VALUE) {
            jsonObject.addProperty("max", doubleArgumentType.getMaximum());
        }
    }

    @Override
    public /* synthetic */ ArgumentType fromPacket(PacketByteBuf buf) {
        return this.fromPacket(buf);
    }
}

