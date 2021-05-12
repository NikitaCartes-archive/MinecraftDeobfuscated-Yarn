/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Represents a singular field within a larger vertex format.
 * <p>
 * This element comprises a data type, a field length,
 * and the corresponding GL element type to which this field corresponds.
 */
@Environment(value=EnvType.CLIENT)
public class VertexFormatElement {
    private final DataType dataType;
    private final Type type;
    private final int textureIndex;
    private final int length;
    /**
     * The total length of this element (in bytes).
     */
    private final int byteLength;

    public VertexFormatElement(int textureIndex, DataType dataType, Type type, int length) {
        if (!this.isValidType(textureIndex, type)) {
            throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
        }
        this.type = type;
        this.dataType = dataType;
        this.textureIndex = textureIndex;
        this.length = length;
        this.byteLength = dataType.getByteLength() * this.length;
    }

    private boolean isValidType(int index, Type type) {
        return index == 0 || type == Type.UV;
    }

    public final DataType getDataType() {
        return this.dataType;
    }

    public final Type getType() {
        return this.type;
    }

    public final int getLength() {
        return this.length;
    }

    public final int getTextureIndex() {
        return this.textureIndex;
    }

    public String toString() {
        return this.length + "," + this.type.getName() + "," + this.dataType.getName();
    }

    public final int getByteLength() {
        return this.byteLength;
    }

    public final boolean isPosition() {
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
        if (this.length != vertexFormatElement.length) {
            return false;
        }
        if (this.textureIndex != vertexFormatElement.textureIndex) {
            return false;
        }
        if (this.dataType != vertexFormatElement.dataType) {
            return false;
        }
        return this.type == vertexFormatElement.type;
    }

    public int hashCode() {
        int i = this.dataType.hashCode();
        i = 31 * i + this.type.hashCode();
        i = 31 * i + this.textureIndex;
        i = 31 * i + this.length;
        return i;
    }

    public void startDrawing(int elementIndex, long pointer, int stride) {
        this.type.startDrawing(this.length, this.dataType.getId(), stride, pointer, this.textureIndex, elementIndex);
    }

    public void endDrawing(int elementIndex) {
        this.type.endDrawing(this.textureIndex, elementIndex);
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        POSITION("Position", (size, type, stride, pointer, textureIndex, elementIndex) -> {
            GlStateManager._enableVertexAttribArray(elementIndex);
            GlStateManager._vertexAttribPointer(elementIndex, size, type, false, stride, pointer);
        }, (textureIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex)),
        NORMAL("Normal", (size, type, stride, pointer, textureIndex, elementIndex) -> {
            GlStateManager._enableVertexAttribArray(elementIndex);
            GlStateManager._vertexAttribPointer(elementIndex, size, type, true, stride, pointer);
        }, (textureIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex)),
        COLOR("Vertex Color", (size, type, stride, pointer, textureIndex, elementIndex) -> {
            GlStateManager._enableVertexAttribArray(elementIndex);
            GlStateManager._vertexAttribPointer(elementIndex, size, type, true, stride, pointer);
        }, (textureIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex)),
        UV("UV", (size, type, stride, pointer, textureIndex, elementIndex) -> {
            GlStateManager._enableVertexAttribArray(elementIndex);
            if (type == 5126) {
                GlStateManager._vertexAttribPointer(elementIndex, size, type, false, stride, pointer);
            } else {
                GlStateManager._vertexAttribIPointer(elementIndex, size, type, stride, pointer);
            }
        }, (textureIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex)),
        PADDING("Padding", (size, type, stride, pointer, textureIndex, elementIndex) -> {}, (textureIndex, elementIndex) -> {}),
        GENERIC("Generic", (size, type, stride, pointer, textureIndex, elementIndex) -> {
            GlStateManager._enableVertexAttribArray(elementIndex);
            GlStateManager._vertexAttribPointer(elementIndex, size, type, false, stride, pointer);
        }, (textureIndex, elementIndex) -> GlStateManager._disableVertexAttribArray(elementIndex));

        private final String name;
        private final Starter starter;
        private final Finisher finisher;

        private Type(String name, Starter starter, Finisher finisher) {
            this.name = name;
            this.starter = starter;
            this.finisher = finisher;
        }

        void startDrawing(int size, int type, int stride, long pointer, int textureIndex, int elementIndex) {
            this.starter.setupBufferState(size, type, stride, pointer, textureIndex, elementIndex);
        }

        public void endDrawing(int textureIndex, int elementIndex) {
            this.finisher.clearBufferState(textureIndex, elementIndex);
        }

        public String getName() {
            return this.name;
        }

        @FunctionalInterface
        @Environment(value=EnvType.CLIENT)
        static interface Starter {
            public void setupBufferState(int var1, int var2, int var3, long var4, int var6, int var7);
        }

        @FunctionalInterface
        @Environment(value=EnvType.CLIENT)
        static interface Finisher {
            public void clearBufferState(int var1, int var2);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum DataType {
        FLOAT(4, "Float", 5126),
        UBYTE(1, "Unsigned Byte", 5121),
        BYTE(1, "Byte", 5120),
        USHORT(2, "Unsigned Short", 5123),
        SHORT(2, "Short", 5122),
        UINT(4, "Unsigned Int", 5125),
        INT(4, "Int", 5124);

        private final int byteLength;
        private final String name;
        private final int id;

        private DataType(int byteCount, String name, int id) {
            this.byteLength = byteCount;
            this.name = name;
            this.id = id;
        }

        public int getByteLength() {
            return this.byteLength;
        }

        public String getName() {
            return this.name;
        }

        public int getId() {
            return this.id;
        }
    }
}

