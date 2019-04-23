/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.arguments.Vec3ArgumentType;
import net.minecraft.network.chat.TranslatableComponent;

public class CoordinateArgument {
    public static final SimpleCommandExceptionType MISSING_COORDINATE = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.missing.double", new Object[0]));
    public static final SimpleCommandExceptionType MISSING_BLOCK_POSITION = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.missing.int", new Object[0]));
    private final boolean relative;
    private final double value;

    public CoordinateArgument(boolean bl, double d) {
        this.relative = bl;
        this.value = d;
    }

    public double toAbsoluteCoordinate(double d) {
        if (this.relative) {
            return this.value + d;
        }
        return this.value;
    }

    public static CoordinateArgument parse(StringReader stringReader, boolean bl) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '^') {
            throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext(stringReader);
        }
        if (!stringReader.canRead()) {
            throw MISSING_COORDINATE.createWithContext(stringReader);
        }
        boolean bl2 = CoordinateArgument.isRelative(stringReader);
        int i = stringReader.getCursor();
        double d = stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readDouble() : 0.0;
        String string = stringReader.getString().substring(i, stringReader.getCursor());
        if (bl2 && string.isEmpty()) {
            return new CoordinateArgument(true, 0.0);
        }
        if (!string.contains(".") && !bl2 && bl) {
            d += 0.5;
        }
        return new CoordinateArgument(bl2, d);
    }

    public static CoordinateArgument parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '^') {
            throw Vec3ArgumentType.MIXED_COORDINATE_EXCEPTION.createWithContext(stringReader);
        }
        if (!stringReader.canRead()) {
            throw MISSING_BLOCK_POSITION.createWithContext(stringReader);
        }
        boolean bl = CoordinateArgument.isRelative(stringReader);
        double d = stringReader.canRead() && stringReader.peek() != ' ' ? (bl ? stringReader.readDouble() : (double)stringReader.readInt()) : 0.0;
        return new CoordinateArgument(bl, d);
    }

    private static boolean isRelative(StringReader stringReader) {
        boolean bl;
        if (stringReader.peek() == '~') {
            bl = true;
            stringReader.skip();
        } else {
            bl = false;
        }
        return bl;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CoordinateArgument)) {
            return false;
        }
        CoordinateArgument coordinateArgument = (CoordinateArgument)object;
        if (this.relative != coordinateArgument.relative) {
            return false;
        }
        return Double.compare(coordinateArgument.value, this.value) == 0;
    }

    public int hashCode() {
        int i = this.relative ? 1 : 0;
        long l = Double.doubleToLongBits(this.value);
        i = 31 * i + (int)(l ^ l >>> 32);
        return i;
    }

    public boolean isRelative() {
        return this.relative;
    }
}

