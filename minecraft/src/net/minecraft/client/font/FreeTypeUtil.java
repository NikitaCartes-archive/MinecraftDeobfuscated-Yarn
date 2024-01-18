package net.minecraft.client.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;

@Environment(EnvType.CLIENT)
public class FreeTypeUtil {
	private static long freeType = 0L;

	public static long initialize() {
		if (freeType == 0L) {
			try (MemoryStack memoryStack = MemoryStack.stackPush()) {
				PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
				checkError(FreeType.FT_Init_FreeType(pointerBuffer), "Initializing FreeType library");
				freeType = pointerBuffer.get();
			}
		}

		return freeType;
	}

	public static void checkError(int code, String description) {
		if (code != 0) {
			throw new IllegalStateException("FreeType error: " + getErrorMessage(code) + " (" + description + ")");
		}
	}

	private static String getErrorMessage(int code) {
		String string = FreeType.FT_Error_String(code);
		return string != null ? string : "Unrecognized error: 0x" + Integer.toHexString(code);
	}

	public static FT_Vector set(FT_Vector vec, float x, float y) {
		long l = (long)Math.round(x * 64.0F);
		long m = (long)Math.round(y * 64.0F);
		return vec.set(l, m);
	}

	public static float getX(FT_Vector vec) {
		return (float)vec.x() / 64.0F;
	}

	public static void release() {
		if (freeType != 0L) {
			FreeType.FT_Done_Library(freeType);
			freeType = 0L;
		}
	}
}
