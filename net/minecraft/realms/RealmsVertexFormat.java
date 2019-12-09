/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.realms;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.realms.RealmsVertexFormatElement;

@Environment(value=EnvType.CLIENT)
public class RealmsVertexFormat {
    private VertexFormat v;

    public RealmsVertexFormat(VertexFormat vertexFormat) {
        this.v = vertexFormat;
    }

    public VertexFormat getVertexFormat() {
        return this.v;
    }

    public List<RealmsVertexFormatElement> getElements() {
        ArrayList<RealmsVertexFormatElement> list = Lists.newArrayList();
        for (VertexFormatElement vertexFormatElement : this.v.getElements()) {
            list.add(new RealmsVertexFormatElement(vertexFormatElement));
        }
        return list;
    }

    public boolean equals(Object o) {
        return this.v.equals(o);
    }

    public int hashCode() {
        return this.v.hashCode();
    }

    public String toString() {
        return this.v.toString();
    }
}

