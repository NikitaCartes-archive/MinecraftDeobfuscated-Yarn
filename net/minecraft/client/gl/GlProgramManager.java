/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
import net.minecraft.client.gl.GlProgram;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class GlProgramManager {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void method_22094(int i) {
        class_4493.method_22045(i);
    }

    public static void deleteProgram(GlProgram glProgram) {
        glProgram.getFragmentShader().release();
        glProgram.getVertexShader().release();
        class_4493.method_22048(glProgram.getProgramRef());
    }

    public static int createProgram() throws IOException {
        int i = class_4493.method_22062();
        if (i <= 0) {
            throw new IOException("Could not create shader program (returned program ID " + i + ")");
        }
        return i;
    }

    public static void linkProgram(GlProgram glProgram) throws IOException {
        glProgram.getFragmentShader().attachTo(glProgram);
        glProgram.getVertexShader().attachTo(glProgram);
        class_4493.method_22051(glProgram.getProgramRef());
        int i = class_4493.method_22002(glProgram.getProgramRef(), 35714);
        if (i == 0) {
            LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", (Object)glProgram.getVertexShader().getName(), (Object)glProgram.getFragmentShader().getName());
            LOGGER.warn(class_4493.method_22052(glProgram.getProgramRef(), 32768));
        }
    }
}

