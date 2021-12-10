/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

/**
 * A listener for when a palette requires more bits to hold a newly indexed
 * object. A no-op listener may be used if the palette does not have to
 * resize.
 * 
 * @see Palette#index(Object)
 */
interface PaletteResizeListener<T> {
    /**
     * Callback for a palette's request to resize to at least {@code newBits}
     * for each entry and to update the storage correspondingly in order to
     * accommodate the new object. After the resize is completed in this method,
     * returns the ID assigned to the {@code object} in the updated palette.
     * 
     * @return the ID for the {@code object} in the (possibly new) palette
     */
    public int onResize(int var1, T var2);
}

