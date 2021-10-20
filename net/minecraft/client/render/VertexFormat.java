/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormatElement;

@Environment(value=EnvType.CLIENT)
public class VertexFormat {
    private final ImmutableList<VertexFormatElement> elements;
    private final ImmutableMap<String, VertexFormatElement> elementMap;
    private final IntList offsets = new IntArrayList();
    private final int size;
    private int vertexArray;
    private int vertexBuffer;
    private int elementBuffer;

    public VertexFormat(ImmutableMap<String, VertexFormatElement> elementMap) {
        this.elementMap = elementMap;
        this.elements = ((ImmutableCollection)elementMap.values()).asList();
        int i = 0;
        for (VertexFormatElement vertexFormatElement : elementMap.values()) {
            this.offsets.add(i);
            i += vertexFormatElement.getByteLength();
        }
        this.size = i;
    }

    public String toString() {
        return "format: " + this.elementMap.size() + " elements: " + this.elementMap.entrySet().stream().map(Object::toString).collect(Collectors.joining(" "));
    }

    public int getVertexSizeInteger() {
        return this.getVertexSize() / 4;
    }

    public int getVertexSize() {
        return this.size;
    }

    public ImmutableList<VertexFormatElement> getElements() {
        return this.elements;
    }

    public ImmutableList<String> getShaderAttributes() {
        return ((ImmutableCollection)((Object)this.elementMap.keySet())).asList();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        VertexFormat vertexFormat = (VertexFormat)o;
        if (this.size != vertexFormat.size) {
            return false;
        }
        return this.elementMap.equals(vertexFormat.elementMap);
    }

    public int hashCode() {
        return this.elementMap.hashCode();
    }

    public void startDrawing() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::innerStartDrawing);
            return;
        }
        this.innerStartDrawing();
    }

    private void innerStartDrawing() {
        int i = this.getVertexSize();
        ImmutableList<VertexFormatElement> list = this.getElements();
        for (int j = 0; j < list.size(); ++j) {
            ((VertexFormatElement)list.get(j)).startDrawing(j, this.offsets.getInt(j), i);
        }
    }

    public void endDrawing() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::innerEndDrawing);
            return;
        }
        this.innerEndDrawing();
    }

    private void innerEndDrawing() {
        ImmutableList<VertexFormatElement> immutableList = this.getElements();
        for (int i = 0; i < immutableList.size(); ++i) {
            VertexFormatElement vertexFormatElement = (VertexFormatElement)immutableList.get(i);
            vertexFormatElement.endDrawing(i);
        }
    }

    public int getVertexArray() {
        if (this.vertexArray == 0) {
            this.vertexArray = GlStateManager._glGenVertexArrays();
        }
        return this.vertexArray;
    }

    public int getVertexBuffer() {
        if (this.vertexBuffer == 0) {
            this.vertexBuffer = GlStateManager._glGenBuffers();
        }
        return this.vertexBuffer;
    }

    public int getElementBuffer() {
        if (this.elementBuffer == 0) {
            this.elementBuffer = GlStateManager._glGenBuffers();
        }
        return this.elementBuffer;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum DrawMode {
        LINES(4, 2, 2),
        LINE_STRIP(5, 2, 1),
        DEBUG_LINES(1, 2, 2),
        DEBUG_LINE_STRIP(3, 2, 1),
        TRIANGLES(4, 3, 3),
        TRIANGLE_STRIP(5, 3, 1),
        TRIANGLE_FAN(6, 3, 1),
        QUADS(4, 4, 4);

        public final int mode;
        public final int vertexCount;
        public final int size;

        private DrawMode(int mode, int vertexCount, int size) {
            this.mode = mode;
            this.vertexCount = vertexCount;
            this.size = size;
        }

        public int getSize(int vertexCount) {
            return switch (this) {
                case LINE_STRIP, DEBUG_LINES, DEBUG_LINE_STRIP, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN -> vertexCount;
                case LINES, QUADS -> vertexCount / 4 * 6;
                default -> 0;
            };
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum IntType {
        BYTE(5121, 1),
        SHORT(5123, 2),
        INT(5125, 4);

        public final int count;
        public final int size;

        private IntType(int count, int size) {
            this.count = count;
            this.size = size;
        }

        public static IntType getSmallestTypeFor(int number) {
            if ((number & 0xFFFF0000) != 0) {
                return INT;
            }
            if ((number & 0xFF00) != 0) {
                return SHORT;
            }
            return BYTE;
        }
    }
}

