package net.minecraft.world.chunk;

interface PaletteResizeListener<T> {
	int onResize(int newSize, T objectAdded);
}
