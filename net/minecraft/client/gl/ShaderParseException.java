/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public class ShaderParseException
extends IOException {
    private final List<JsonStackTrace> traces = Lists.newArrayList();
    private final String message;

    public ShaderParseException(String message) {
        this.traces.add(new JsonStackTrace());
        this.message = message;
    }

    public ShaderParseException(String message, Throwable cause) {
        super(cause);
        this.traces.add(new JsonStackTrace());
        this.message = message;
    }

    public void addFaultyElement(String jsonKey) {
        this.traces.get(0).add(jsonKey);
    }

    public void addFaultyFile(String path) {
        this.traces.get((int)0).fileName = path;
        this.traces.add(0, new JsonStackTrace());
    }

    @Override
    public String getMessage() {
        return "Invalid " + this.traces.get(this.traces.size() - 1) + ": " + this.message;
    }

    public static ShaderParseException wrap(Exception cause) {
        if (cause instanceof ShaderParseException) {
            return (ShaderParseException)cause;
        }
        String string = cause.getMessage();
        if (cause instanceof FileNotFoundException) {
            string = "File not found";
        }
        return new ShaderParseException(string, cause);
    }

    public static class JsonStackTrace {
        @Nullable
        String fileName;
        private final List<String> faultyElements = Lists.newArrayList();

        JsonStackTrace() {
        }

        void add(String element) {
            this.faultyElements.add(0, element);
        }

        @Nullable
        public String getFileName() {
            return this.fileName;
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

