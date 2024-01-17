package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;

@Environment(EnvType.CLIENT)
public class class_9111 {
	private static long field_48394 = 0L;

	public static long method_56143() {
		if (field_48394 == 0L) {
			try (MemoryStack memoryStack = MemoryStack.stackPush()) {
				PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
				method_56145(FreeType.FT_Init_FreeType(pointerBuffer), "Initializing FreeType library");
				field_48394 = pointerBuffer.get();
			}
		}

		return field_48394;
	}

	public static void method_56145(int i, String string) {
		if (i != 0) {
			throw new IllegalStateException("FreeType error: " + method_56144(i) + " (" + string + ")");
		}
	}

	private static String method_56144(int i) {
		String string = FreeType.FT_Error_String(i);
		return string != null ? string : "Unrecognized error: 0x" + Integer.toHexString(i);
	}

	public static FT_Vector method_56147(FT_Vector fT_Vector, float f, float g) {
		long l = (long)Math.round(f * 64.0F);
		long m = (long)Math.round(g * 64.0F);
		return fT_Vector.set(l, m);
	}

	public static float method_56146(FT_Vector fT_Vector) {
		return (float)fT_Vector.x() / 64.0F;
	}

	public static void method_56148() {
		if (field_48394 != 0L) {
			FreeType.FT_Done_Library(field_48394);
			field_48394 = 0L;
		}
	}
}
