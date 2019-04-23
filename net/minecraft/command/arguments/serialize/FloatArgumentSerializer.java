/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.arguments.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.arguments.BrigadierArgumentTypes;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.util.PacketByteBuf;

public class FloatArgumentSerializer
implements ArgumentSerializer<FloatArgumentType> {
    public void method_10044(FloatArgumentType floatArgumentType, PacketByteBuf packetByteBuf) {
        boolean bl = floatArgumentType.getMinimum() != -3.4028235E38f;
        boolean bl2 = floatArgumentType.getMaximum() != Float.MAX_VALUE;
        packetByteBuf.writeByte(BrigadierArgumentTypes.createFlag(bl, bl2));
        if (bl) {
            packetByteBuf.writeFloat(floatArgumentType.getMinimum());
        }
        if (bl2) {
            packetByteBuf.writeFloat(floatArgumentType.getMaximum());
        }
    }

    public FloatArgumentType method_10045(PacketByteBuf packetByteBuf) {
        byte b = packetByteBuf.readByte();
        float f = BrigadierArgumentTypes.hasMin(b) ? packetByteBuf.readFloat() : -3.4028235E38f;
        float g = BrigadierArgumentTypes.hasMax(b) ? packetByteBuf.readFloat() : Float.MAX_VALUE;
        return FloatArgumentType.floatArg(f, g);
    }

    public void method_10046(FloatArgumentType floatArgumentType, JsonObject jsonObject) {
        if (floatArgumentType.getMinimum() != -3.4028235E38f) {
            jsonObject.addProperty("min", Float.valueOf(floatArgumentType.getMinimum()));
        }
        if (floatArgumentType.getMaximum() != Float.MAX_VALUE) {
            jsonObject.addProperty("max", Float.valueOf(floatArgumentType.getMaximum()));
        }
    }

    @Override
    public /* synthetic */ ArgumentType fromPacket(PacketByteBuf packetByteBuf) {
        return this.method_10045(packetByteBuf);
    }
}

