/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ShaderParseException
extends IOException {
    private final List<JsonStackTrace> traces = Lists.newArrayList();
    private final String message;

    public ShaderParseException(String string) {
        this.traces.add(new JsonStackTrace());
        this.message = string;
    }

    public ShaderParseException(String string, Throwable throwable) {
        super(throwable);
        this.traces.add(new JsonStackTrace());
        this.message = string;
    }

    public void addFaultyElement(String string) {
        this.traces.get(0).add(string);
    }

    public void addFaultyFile(String string) {
        this.traces.get(0).fileName = string;
        this.traces.add(0, new JsonStackTrace());
    }

    @Override
    public String getMessage() {
        return "Invalid " + this.traces.get(this.traces.size() - 1) + ": " + this.message;
    }

    public static ShaderParseException wrap(Exception exception) {
        if (exception instanceof ShaderParseException) {
            return (ShaderParseException)exception;
        }
        String string = exception.getMessage();
        if (exception instanceof FileNotFoundException) {
            string = "File not found";
        }
        return new ShaderParseException(string, exception);
    }

    @Environment(value=EnvType.CLIENT)
    public static class JsonStackTrace {
        @Nullable
        private String fileName;
        private final List<String> faultyElements = Lists.newArrayList();

        private JsonStackTrace() {
        }

        private void add(String string) {
            this.faultyElements.add(0, string);
        }

        public String joinStackTrace() {
            return StringUtils.join(this.faultyElements, "->");
        }

        public String toString() {
            if (this.fileName != null) {
                if (this.faultyElements.isEmpty()) {
                    return this.fileName;
                }
                return this.fileName + " " + this.joinStackTrace();
            }
            if (this.faultyElements.isEmpty()) {
                return "(Unknown file)";
            }
            return "(Unknown file) " + this.joinStackTrace();
        }
    }
}

