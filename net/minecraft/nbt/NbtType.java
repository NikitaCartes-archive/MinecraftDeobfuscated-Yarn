/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.class_6836;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtNull;
import net.minecraft.nbt.NbtTagSizeTracker;

/**
 * Represents an NBT type.
 */
public interface NbtType<T extends NbtElement> {
    public T read(DataInput var1, int var2, NbtTagSizeTracker var3) throws IOException;

    public class_6836.class_6838 method_39852(DataInput var1, class_6836 var2) throws IOException;

    default public void method_39877(DataInput dataInput, class_6836 arg) throws IOException {
        switch (arg.method_39871(this)) {
            case CONTINUE: {
                this.method_39852(dataInput, arg);
                break;
            }
            case HALT: {
                break;
            }
            case BREAK: {
                this.method_39851(dataInput);
            }
        }
    }

    public void method_39854(DataInput var1, int var2) throws IOException;

    public void method_39851(DataInput var1) throws IOException;

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

    public static NbtType<NbtNull> createInvalid(final int type) {
        return new NbtType<NbtNull>(){

            private IOException method_39878() {
                return new IOException("Invalid tag id: " + type);
            }

            @Override
            public NbtNull read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
                throw this.method_39878();
            }

            @Override
            public class_6836.class_6838 method_39852(DataInput dataInput, class_6836 arg) throws IOException {
                throw this.method_39878();
            }

            @Override
            public void method_39854(DataInput dataInput, int i) throws IOException {
                throw this.method_39878();
            }

            @Override
            public void method_39851(DataInput dataInput) throws IOException {
                throw this.method_39878();
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

    public static interface class_6840<T extends NbtElement>
    extends NbtType<T> {
        @Override
        default public void method_39854(DataInput dataInput, int i) throws IOException {
            for (int j = 0; j < i; ++j) {
                this.method_39851(dataInput);
            }
        }
    }

    public static interface class_6839<T extends NbtElement>
    extends NbtType<T> {
        @Override
        default public void method_39851(DataInput dataInput) throws IOException {
            dataInput.skipBytes(this.method_39853());
        }

        @Override
        default public void method_39854(DataInput dataInput, int i) throws IOException {
            dataInput.skipBytes(this.method_39853() * i);
        }

        public int method_39853();
    }
}

