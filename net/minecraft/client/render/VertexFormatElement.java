/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.IntConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class VertexFormatElement {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Format format;
    private final Type type;
    private final int index;
    private final int count;

    public VertexFormatElement(int i, Format format, Type type, int j) {
        if (this.isValidType(i, type)) {
            this.type = type;
        } else {
            LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
            this.type = Type.UV;
        }
        this.format = format;
        this.index = i;
        this.count = j;
    }

    private boolean isValidType(int i, Type type) {
        return i == 0 || type == Type.UV;
    }

    public final Format getFormat() {
        return this.format;
    }

    public final Type getType() {
        return this.type;
    }

    public final int getCount() {
        return this.count;
    }

    public final int getIndex() {
        return this.index;
    }

    public String toString() {
        return this.count + "," + this.type.getName() + "," + this.format.getName();
    }

    public final int getSize() {
        return this.format.getSize() * this.count;
    }

    public final boolean isPosition() {
        return this.type == Type.POSITION;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        VertexFormatElement vertexFormatElement = (VertexFormatElement)object;
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

    public void method_22652(long l, int i) {
        this.type.method_22655(this.count, this.format.getGlId(), i, l, this.index);
    }

    public void method_22653() {
        this.type.method_22654(this.index);
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

        private Format(int j, String string2, int k) {
            this.size = j;
            this.name = string2;
            this.glId = k;
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
        POSITION("Position", (i, j, k, l, m) -> {
            GlStateManager.vertexPointer(i, j, k, l);
            GlStateManager.enableClientState(32884);
        }, i -> GlStateManager.disableClientState(32884)),
        NORMAL("Normal", (i, j, k, l, m) -> {
            GlStateManager.normalPointer(j, k, l);
            GlStateManager.enableClientState(32885);
        }, i -> GlStateManager.disableClientState(32885)),
        COLOR("Vertex Color", (i, j, k, l, m) -> {
            GlStateManager.colorPointer(i, j, k, l);
            GlStateManager.enableClientState(32886);
        }, i -> {
            GlStateManager.disableClientState(32886);
            GlStateManager.clearCurrentColor();
        }),
        UV("UV", (i, j, k, l, m) -> {
            GlStateManager.clientActiveTexture(33984 + m);
            GlStateManager.texCoordPointer(i, j, k, l);
            GlStateManager.enableClientState(32888);
            GlStateManager.clientActiveTexture(33984);
        }, i -> {
            GlStateManager.clientActiveTexture(33984 + i);
            GlStateManager.disableClientState(32888);
            GlStateManager.clientActiveTexture(33984);
        }),
        PADDING("Padding", (i, j, k, l, m) -> {}, i -> {}),
        GENERIC("Generic", (i, j, k, l, m) -> {
            GlStateManager.method_22606(m);
            GlStateManager.method_22609(m, i, j, false, k, l);
        }, GlStateManager::method_22607);

        private final String name;
        private final class_4575 field_20783;
        private final IntConsumer field_20784;

        private Type(String string2, class_4575 arg, IntConsumer intConsumer) {
            this.name = string2;
            this.field_20783 = arg;
            this.field_20784 = intConsumer;
        }

        private void method_22655(int i, int j, int k, long l, int m) {
            this.field_20783.setupBufferState(i, j, k, l, m);
        }

        public void method_22654(int i) {
            this.field_20784.accept(i);
        }

        public String getName() {
            return this.name;
        }

        @Environment(value=EnvType.CLIENT)
        static interface class_4575 {
            public void setupBufferState(int var1, int var2, int var3, long var4, int var6);
        }
    }
}

