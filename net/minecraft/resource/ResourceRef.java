/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.io.IOException;
import net.minecraft.resource.Resource;

public class ResourceRef {
    private final String packName;
    private final Opener opener;

    public ResourceRef(String name, Opener opener) {
        this.packName = name;
        this.opener = opener;
    }

    public String getPackName() {
        return this.packName;
    }

    public Resource open() throws IOException {
        return this.opener.open();
    }

    @FunctionalInterface
    public static interface Opener {
        public Resource open() throws IOException;
    }
}

