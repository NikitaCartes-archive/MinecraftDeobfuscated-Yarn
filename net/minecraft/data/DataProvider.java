/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.data.DataWriter;

public interface DataProvider {
    public static final HashFunction SHA1 = Hashing.sha1();

    public void run(DataWriter var1) throws IOException;

    public String getName();

    public static void writeToPath(Gson gson, DataWriter writer, JsonElement output, Path path) throws IOException {
        String string = gson.toJson(output);
        writer.write(path, string);
    }
}

