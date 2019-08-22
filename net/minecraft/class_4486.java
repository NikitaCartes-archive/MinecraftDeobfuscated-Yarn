/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import org.jetbrains.annotations.Nullable;

public class class_4486 {
    private static final Int2ObjectMap<class_4486> field_20445 = new Int2ObjectOpenHashMap<class_4486>();
    public static final class_4486 field_20442 = class_4486.method_21884(new class_4486(1, GZIPInputStream::new, GZIPOutputStream::new));
    public static final class_4486 field_20443 = class_4486.method_21884(new class_4486(2, InflaterInputStream::new, DeflaterOutputStream::new));
    public static final class_4486 field_20444 = class_4486.method_21884(new class_4486(3, inputStream -> inputStream, outputStream -> outputStream));
    private final int field_20446;
    private final class_4487<InputStream> field_20447;
    private final class_4487<OutputStream> field_20448;

    private class_4486(int i, class_4487<InputStream> arg, class_4487<OutputStream> arg2) {
        this.field_20446 = i;
        this.field_20447 = arg;
        this.field_20448 = arg2;
    }

    private static class_4486 method_21884(class_4486 arg) {
        field_20445.put(arg.field_20446, arg);
        return arg;
    }

    @Nullable
    public static class_4486 method_21883(int i) {
        return (class_4486)field_20445.get(i);
    }

    public static boolean method_21887(int i) {
        return field_20445.containsKey(i);
    }

    public int method_21882() {
        return this.field_20446;
    }

    public OutputStream method_21886(OutputStream outputStream) throws IOException {
        return this.field_20448.wrap(outputStream);
    }

    public InputStream method_21885(InputStream inputStream) throws IOException {
        return this.field_20447.wrap(inputStream);
    }

    @FunctionalInterface
    static interface class_4487<O> {
        public O wrap(O var1) throws IOException;
    }
}

