/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
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
    private final IntList offsets = new IntArrayList();
    private final int size;

    public VertexFormat(ImmutableList<VertexFormatElement> immutableList) {
        this.elements = immutableList;
        int i = 0;
        for (VertexFormatElement vertexFormatElement : immutableList) {
            this.offsets.add(i);
            i += vertexFormatElement.getByteLength();
        }
        this.size = i;
    }

    public String toString() {
        return "format: " + this.elements.size() + " elements: " + this.elements.stream().map(Object::toString).collect(Collectors.joining(" "));
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
        return this.elements.equals(vertexFormat.elements);
    }

    public int hashCode() {
        return this.elements.hashCode();
    }

    public void startDrawing(long pointer) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.startDrawing(pointer));
            return;
        }
        int i = this.getVertexSize();
        ImmutableList<VertexFormatElement> list = this.getElements();
        for (int j = 0; j < list.size(); ++j) {
            ((VertexFormatElement)list.get(j)).startDrawing(pointer + (long)this.offsets.getInt(j), i);
        }
    }

    public void endDrawing() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(this::endDrawing);
            return;
        }
        for (VertexFormatElement vertexFormatElement : this.getElements()) {
            vertexFormatElement.endDrawing();
        }
    }
}

