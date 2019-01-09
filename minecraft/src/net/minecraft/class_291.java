package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_291 {
	private int field_1594;
	private final class_293 field_1595;
	private int field_1593;

	public class_291(class_293 arg) {
		this.field_1595 = arg;
		this.field_1594 = GLX.glGenBuffers();
	}

	public void method_1353() {
		GLX.glBindBuffer(GLX.GL_ARRAY_BUFFER, this.field_1594);
	}

	public void method_1352(ByteBuffer byteBuffer) {
		this.method_1353();
		GLX.glBufferData(GLX.GL_ARRAY_BUFFER, byteBuffer, 35044);
		method_1354();
		this.field_1593 = byteBuffer.limit() / this.field_1595.method_1362();
	}

	public void method_1351(int i) {
		GlStateManager.drawArrays(i, 0, this.field_1593);
	}

	public static void method_1354() {
		GLX.glBindBuffer(GLX.GL_ARRAY_BUFFER, 0);
	}

	public void method_1355() {
		if (this.field_1594 >= 0) {
			GLX.glDeleteBuffers(this.field_1594);
			this.field_1594 = -1;
		}
	}
}
