package net.minecraft.util.palette;

interface PaletteResizeListener<T> {
	int onResize(int i, T object);
}
