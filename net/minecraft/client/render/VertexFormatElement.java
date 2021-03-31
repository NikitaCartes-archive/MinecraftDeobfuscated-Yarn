/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class VertexFormatElement {
    private final Format format;
    private final Type type;
    private final int index;
    private final int count;
    private final int size;

    public VertexFormatElement(int index, Format format, Type type, int count) {
        if (!this.isValidType(index, type)) {
            throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
        }
        this.type = type;
        this.format = format;
        this.index = index;
        this.count = count;
        this.size = format.getSize() * this.count;
    }

    private boolean isValidType(int index, Type type) {
        return index == 0 || type == Type.UV;
    }

    public final Format getFormat() {
        return this.format;
    }

    public final Type getType() {
        return this.type;
    }

    public final int method_34451() {
        return this.count;
    }

    public final int getIndex() {
        return this.index;
    }

    public String toString() {
        return this.count + "," + this.type.getName() + "," + this.format.getName();
    }

    public final int getSize() {
        return this.size;
    }

    public final boolean method_35667() {
        return this.type == Type.POSITION;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        VertexFormatElement vertexFormatElement = (VertexFormatElement)o;
        if (this.count != vertexFormatElement.count) {
            return false;
        }
        if (this.index != vertexFormatElement.index) {
            return false;
        }
        if (this.format != vertexFormatElement.format) {
            return false;
        }
        return this.type == vertexFormatElement.type;
    }

    public int hashCode() {
        int i = this.format.hashCode();
        i = 31 * i + this.type.hashCode();
        i = 31 * i + this.index;
        i = 31 * i + this.count;
        return i;
    }

    public void startDrawing(int i, long l, int j) {
        this.type.startDrawing(this.count, this.format.getGlId(), j, l, this.index, i);
    }

    public void endDrawing(int i) {
        this.type.endDrawing(this.index, i);
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Format {
        FLOAT(4, "Float", 5126),
        UBYTE(1, "Unsigned Byte", 5121),
        BYTE(1, "Byte", 5120),
        USHORT(2, "Unsigned Short", 5123),
        SHORT(2, "Short", 5122),
        UINT(4, "Unsigned Int", 5125),
        INT(4, "Int", 5124);

        private final int size;
        private final String name;
        private final int glId;

        private Format(int size, String name, int glId) {
            this.size = size;
            this.name = name;
            this.glId = glId;
        }

        public int getSize() {
            return this.size;
        }

        public String getName() {
            return this.name;
        }

        public int getGlId() {
            return this.glId;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        POSITION("Position", (i, j, k, l, m, n) -> {
            GlStateManager._enableVertexAttribArray(n);
            GlStateManager._vertexAttribPointer(n, i, j, false, k, l);
        }, (i, j) -> GlStateManager._disableVertexAttribArray(j)),
        NORMAL("Normal", (i, j, k, l, m, n) -> {
            GlStateManager._enableVertexAttribArray(n);
            GlStateManager._vertexAttribPointer(n, i, j, true, k, l);
        }, (i, j) -> GlStateManager._disableVertexAttribArray(j)),
        COLOR("Vertex Color", (i, j, k, l, m, n) -> {
            GlStateManager._enableVertexAttribArray(n);
            GlStateManager._vertexAttribPointer(n, i, j, true, k, l);
        }, (i, j) -> GlStateManager._disableVertexAttribArray(j)),
        UV("UV", (i, j, k, l, m, n) -> {
            GlStateManager._enableVertexAttribArray(n);
            if (j == 5126) {
                GlStateManager._vertexAttribPointer(n, i, j, false, k, l);
            } else {
                GlStateManager._vertexAttribIPointer(n, i, j, k, l);
            }
        }, (i, j) -> GlStateManager._disableVertexAttribArray(j)),
        PADDING("Padding", (i, j, k, l, m, n) -> {}, (i, j) -> {}),
        GENERIC("Generic", (i, j, k, l, m, n) -> {
            GlStateManager._enableVertexAttribArray(n);
            GlStateManager._vertexAttribPointer(n, i, j, false, k, l);
        }, (i, j) -> GlStateManager._disableVertexAttribArray(j));

        private final String name;
        private final Starter starter;
        private final class_5938 finisher;

        private Type(String name, Starter starter, class_5938 arg) {
            this.name = name;
            this.starter = starter;
            this.finisher = arg;
        }

        private void startDrawing(int count, int glId, int stride, long pointer, int elementIndex, int i) {
            this.starter.setupBufferState(count, glId, stride, pointer, elementIndex, i);
        }

        public void endDrawing(int elementIndex, int i) {
            this.finisher.clearBufferState(elementIndex, i);
        }

        public String getName() {
            return this.name;
        }

        @FunctionalInterface
        @Environment(value=EnvType.CLIENT)
        static interface class_5938 {
            public void clearBufferState(int var1, int var2);
        }

        @FunctionalInterface
        @Environment(value=EnvType.CLIENT)
        static interface Starter {
            public void setupBufferState(int var1, int var2, int var3, long var4, int var6, int var7);
        }
    }
}

