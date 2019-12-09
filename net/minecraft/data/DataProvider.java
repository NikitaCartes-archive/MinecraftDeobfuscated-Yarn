/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;
import net.minecraft.data.DataCache;

public interface DataProvider {
    public static final HashFunction SHA1 = Hashing.sha1();

    public void run(DataCache var1) throws IOException;

    public String getName();

    public static void writeToPath(Gson gson, DataCache cache, JsonElement output, Path path) throws IOException {
        String string = gson.toJson(output);
        String string2 = SHA1.hashUnencodedChars(string).toString();
        if (!Objects.equals(cache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, new OpenOption[0]);){
                bufferedWriter.write(string);
            }
        }
        cache.updateSha1(path, string2);
    }
}

