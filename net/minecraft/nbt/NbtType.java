/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.nbt.scanner.NbtScanner;

/**
 * Represents an NBT type.
 */
public interface NbtType<T extends NbtElement> {
    public T read(DataInput var1, int var2, NbtTagSizeTracker var3) throws IOException;

    public NbtScanner.Result doAccept(DataInput var1, NbtScanner var2) throws IOException;

    default public void accept(DataInput input, NbtScanner visitor) throws IOException {
        switch (visitor.start(this)) {
            case CONTINUE: {
                this.doAccept(input, visitor);
                break;
            }
            case HALT: {
                break;
            }
            case BREAK: {
                this.skip(input);
            }
        }
    }

    public void skip(DataInput var1, int var2) throws IOException;

    public void skip(DataInput var1) throws IOException;

    /**
     * Determines the immutability of this type.
     * <p>
     * The mutability of an NBT type means the held value can be modified
     * after the NBT element is instantiated.
     * 
     * @return {@code true} if this NBT type is immutable, else {@code false}
     */
    default public boolean isImmutable() {
        return false;
    }

    public String getCrashReportName();

    public String getCommandFeedbackName();

    public static NbtType<NbtEnd> createInvalid(final int type) {
        return new NbtType<NbtEnd>(){

            private IOException createException() {
                return new IOException("Invalid tag id: " + type);
            }

            @Override
            public NbtEnd read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
                throw this.createException();
            }

            @Override
            public NbtScanner.Result doAccept(DataInput input, NbtScanner visitor) throws IOException {
                throw this.createException();
            }

            @Override
            public void skip(DataInput input, int count) throws IOException {
                throw this.createException();
            }

            @Override
            public void skip(DataInput input) throws IOException {
                throw this.createException();
            }

            @Override
            public String getCrashReportName() {
                return "INVALID[" + type + "]";
            }

            @Override
            public String getCommandFeedbackName() {
                return "UNKNOWN_" + type;
            }

            @Override
            public /* synthetic */ NbtElement read(DataInput input, int depth, NbtTagSizeTracker tracker) throws IOException {
                return this.read(input, depth, tracker);
            }
        };
    }

    public static interface OfVariableSize<T extends NbtElement>
    extends NbtType<T> {
        @Override
        default public void skip(DataInput input, int count) throws IOException {
            for (int i = 0; i < count; ++i) {
                this.skip(input);
            }
        }
    }

    public static interface OfFixedSize<T extends NbtElement>
    extends NbtType<T> {
        @Override
        default public void skip(DataInput input) throws IOException {
            input.skipBytes(this.getSizeInBytes());
        }

        @Override
        default public void skip(DataInput input, int count) throws IOException {
            input.skipBytes(this.getSizeInBytes() * count);
        }

        public int getSizeInBytes();
    }
}

