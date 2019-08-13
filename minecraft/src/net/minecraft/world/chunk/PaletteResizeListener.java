package net.minecraft.world.chunk;

interface PaletteResizeListener<T> {
	int onResize(int i, T object);
}
