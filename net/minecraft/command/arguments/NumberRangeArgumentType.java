/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.NumberRange;
import net.minecraft.util.PacketByteBuf;

public interface NumberRangeArgumentType<T extends NumberRange<?>>
extends ArgumentType<T> {
    public static IntRangeArgumentType numberRange() {
        return new IntRangeArgumentType();
    }

    public static abstract class NumberSerializer<T extends NumberRangeArgumentType<?>>
    implements ArgumentSerializer<T> {
        public void method_9429(T numberRangeArgumentType, PacketByteBuf packetByteBuf) {
        }

        public void method_9428(T numberRangeArgumentType, JsonObject jsonObject) {
        }
    }

    public static class FloatRangeArgumentType
    implements NumberRangeArgumentType<NumberRange.FloatRange> {
        private static final Collection<String> EXAMPLES = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

        public NumberRange.FloatRange method_9423(StringReader stringReader) throws CommandSyntaxException {
            return NumberRange.FloatRange.parse(stringReader);
        }

        @Override
        public Collection<String> getExamples() {
            return EXAMPLES;
        }

        @Override
        public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
            return this.method_9423(stringReader);
        }

        public static class Serializer
        extends NumberSerializer<FloatRangeArgumentType> {
            public FloatRangeArgumentType method_9424(PacketByteBuf packetByteBuf) {
                return new FloatRangeArgumentType();
            }

            @Override
            public /* synthetic */ ArgumentType fromPacket(PacketByteBuf packetByteBuf) {
                return this.method_9424(packetByteBuf);
            }
        }
    }

    public static class IntRangeArgumentType
    implements NumberRangeArgumentType<NumberRange.IntRange> {
        private static final Collection<String> EXAMPLES = Arrays.asList("0..5", "0", "-5", "-100..", "..100");

        public static NumberRange.IntRange getRangeArgument(CommandContext<ServerCommandSource> commandContext, String string) {
            return commandContext.getArgument(string, NumberRange.IntRange.class);
        }

        public NumberRange.IntRange method_9426(StringReader stringReader) throws CommandSyntaxException {
            return NumberRange.IntRange.parse(stringReader);
        }

        @Override
        public Collection<String> getExamples() {
            return EXAMPLES;
        }

        @Override
        public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
            return this.method_9426(stringReader);
        }

        public static class Serializer
        extends NumberSerializer<IntRangeArgumentType> {
            public IntRangeArgumentType method_9427(PacketByteBuf packetByteBuf) {
                return new IntRangeArgumentType();
            }

            @Override
            public /* synthetic */ ArgumentType fromPacket(PacketByteBuf packetByteBuf) {
                return this.method_9427(packetByteBuf);
            }
        }
    }
}

