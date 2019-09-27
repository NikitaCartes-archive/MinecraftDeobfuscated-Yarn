/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.nbt.Tag;

public interface TagReader<T extends Tag> {
    public T read(DataInput var1, int var2, PositionTracker var3) throws IOException;

    default public boolean isImmutable() {
        return false;
    }

    public String getCrashReportName();

    public String getCommandFeedbackName();

    public static TagReader<EndTag> createInvalid(final int i) {
        return new TagReader<EndTag>(){

            public EndTag method_23264(DataInput dataInput, int i2, PositionTracker positionTracker) throws IOException {
                throw new IllegalArgumentException("Invalid tag id: " + i);
            }

            @Override
            public String getCrashReportName() {
                return "INVALID[" + i + "]";
            }

            @Override
            public String getCommandFeedbackName() {
                return "UNKNOWN_" + i;
            }

            @Override
            public /* synthetic */ Tag read(DataInput dataInput, int i2, PositionTracker positionTracker) throws IOException {
                return this.method_23264(dataInput, i2, positionTracker);
            }
        };
    }
}

