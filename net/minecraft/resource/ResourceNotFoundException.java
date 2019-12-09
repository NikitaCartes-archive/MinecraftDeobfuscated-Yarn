/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.io.File;
import java.io.FileNotFoundException;

public class ResourceNotFoundException
extends FileNotFoundException {
    public ResourceNotFoundException(File packSource, String resource) {
        super(String.format("'%s' in ResourcePack '%s'", resource, packSource));
    }
}

