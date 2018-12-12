package net.minecraft.util.palette;

interface PaletteResizeHandler<T> {
	int resizePaletteAndGetIndex(int i, T object);
}
