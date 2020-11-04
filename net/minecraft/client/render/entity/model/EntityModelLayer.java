/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public final class EntityModelLayer {
    private final Identifier identifier;
    private final String layer;

    public EntityModelLayer(Identifier identifier, String layer) {
        this.identifier = identifier;
        this.layer = layer;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof EntityModelLayer) {
            EntityModelLayer entityModelLayer = (EntityModelLayer)other;
            return this.identifier.equals(entityModelLayer.identifier) && this.layer.equals(entityModelLayer.layer);
        }
        return false;
    }

    public int hashCode() {
        int i = this.identifier.hashCode();
        i = 31 * i + this.layer.hashCode();
        return i;
    }

    public String toString() {
        return this.identifier + "#" + this.layer;
    }
}

