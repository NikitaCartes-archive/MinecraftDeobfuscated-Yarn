/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.nbt.scanner;

import net.minecraft.nbt.NbtType;

/**
 * An NBT scanner may reduce unnecessary data parsing to allow finding
 * desired information in an NBT structure as fast as possible.
 * 
 * <p>Call format: start -> VisitBody
 * <br>
 * VisitBody:<br>
 * { visitNull | visitString | visitByte | visitShort | visitInt<br>
 * | visitLong | visitFloat | visitDouble | visitByteArray<br>
 * | visitIntArray | visitLongArray<br>
 * | visitListMeta -> [startListItem -> VisitBody]* -> endNested<br>
 * | [visitSubNbtType -> startSubNbt -> VisitBody]* -> endNested<br>
 * }
 * 
 * <p>The visit order is depth-first.
 */
public interface NbtScanner {
    public Result visitNull();

    public Result visitString(String var1);

    public Result visitByte(byte var1);

    public Result visitShort(short var1);

    public Result visitInt(int var1);

    public Result visitLong(long var1);

    public Result visitFloat(float var1);

    public Result visitDouble(double var1);

    public Result visitByteArray(byte[] var1);

    public Result visitIntArray(int[] var1);

    public Result visitLongArray(long[] var1);

    public Result visitListMeta(NbtType<?> var1, int var2);

    public NestedResult visitSubNbtType(NbtType<?> var1);

    /**
     * Starts a visit to an NBT structure in the sub NBT of a compound NBT.
     * 
     * @see #start
     * @see #startListItem
     */
    public NestedResult startSubNbt(NbtType<?> var1, String var2);

    /**
     * Starts a visit to an NBT structure in an element of a list NBT.
     * 
     * @see #startSubNbt
     * @see #start
     */
    public NestedResult startListItem(NbtType<?> var1, int var2);

    /**
     * Ends a nested visit.
     * 
     * <p>This is guaranteed to be called once for each call to {@link #start},
     * {@link #visitSubNbtType}, and {@link #startListItem} where the list or
     * the compound NBT type is passed, unless the visit is halted.
     */
    public Result endNested();

    /**
     * Starts a visit to an NBT structure.
     * 
     * @see #startSubNbt
     * @see #startListItem
     */
    public Result start(NbtType<?> var1);

    public static enum NestedResult {
        ENTER,
        SKIP,
        BREAK,
        HALT;

    }

    public static enum Result {
        CONTINUE,
        BREAK,
        HALT;

    }
}

