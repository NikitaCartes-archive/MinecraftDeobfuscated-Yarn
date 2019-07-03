/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import org.apache.commons.io.FileUtils;

@Environment(value=EnvType.CLIENT)
public class class_4432 {
    public static class_4433 method_21549() {
        File file = new File(Realms.getGameDirectoryPath(), "realms_persistence.json");
        Gson gson = new Gson();
        try {
            return gson.fromJson(FileUtils.readFileToString(file), class_4433.class);
        } catch (IOException iOException) {
            return new class_4433();
        }
    }

    public static void method_21550(class_4433 arg) {
        File file = new File(Realms.getGameDirectoryPath(), "realms_persistence.json");
        Gson gson = new Gson();
        String string = gson.toJson(arg);
        try {
            FileUtils.writeStringToFile(file, string);
        } catch (IOException iOException) {
            // empty catch block
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class class_4433 {
        public String field_20209;
        public boolean field_20210 = false;

        private class_4433() {
        }
    }
}

