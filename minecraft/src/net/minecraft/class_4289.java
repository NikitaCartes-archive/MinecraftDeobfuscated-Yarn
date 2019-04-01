package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4289 extends class_4071 {
	private static final class_2960 field_19224 = new class_2960("textures/gui/loading.png");
	protected final class_310 field_19210;
	protected static final class_4289.class_4290 field_19211 = new class_4289.class_4290(0, 0, 44);
	protected static final class_4289.class_4290 field_19212 = new class_4289.class_4290(1, 0, 45);
	protected static final class_4289.class_4290 field_19213 = new class_4289.class_4290(2, 0, 43);
	protected static final class_4289.class_4290 field_19214 = new class_4289.class_4290(3, 0, 20);
	protected static final class_4289.class_4290 field_19215 = new class_4289.class_4290(4, 0, 1);
	protected static final class_4289.class_4290 field_19216 = new class_4289.class_4290(5, 0, 28);
	protected static final class_4289.class_4290 field_19217 = new class_4289.class_4290(5, 0, 30);
	protected static final class_4289.class_4290 field_19218 = new class_4289.class_4290(6, 0, 21);
	protected static final class_4289.class_4290 field_19219 = new class_4289.class_4290(7, 0, 43);
	protected static final class_4289.class_4290 field_19220 = new class_4289.class_4290(8, 0, 20);
	protected static final class_4289.class_4290 field_19221 = new class_4289.class_4290(9, 0, 35);
	protected static final class_4289.class_4290 field_19222 = new class_4289.class_4290(10, 0, 10);
	protected static final class_4289.class_4290 field_19223 = new class_4289.class_4290(10, 11, 1);
	private long field_19225;

	public class_4289(class_310 arg) {
		this.field_19210 = arg;
	}

	public static void method_20277(class_310 arg) {
		arg.method_1531().method_4616(field_19224, new class_425.class_4070(field_19224));
	}

	protected void method_20275() {
		this.field_19210.method_1531().method_4618(field_19224);
	}

	@Override
	public void render(int i, int j, float f) {
		long l = class_156.method_658();
		if (l - this.field_19225 > 100L) {
			this.field_19225 = l;
			field_19215.field_19227 = (field_19215.field_19227 + 1) % 4;
			field_19223.field_19227 = 10 + (field_19223.field_19227 + 1) % 2;
		}
	}

	protected void method_20276(class_287 arg, class_4289.class_4291 arg2, class_4289.class_4290 arg3) {
		int i = 8 + arg2.field_19230 * 8;
		int j = 8 + arg2.field_19229 * 16;
		int k = arg3.field_19228 * 8;
		float f = 512.0F;
		float g = (float)(arg3.field_19227 * 8) / 512.0F;
		float h = (float)((arg3.field_19227 + arg3.field_19228) * 8) / 512.0F;
		float l = (float)(arg3.field_19226 * 20) / 512.0F;
		float m = (float)(arg3.field_19226 * 20 + 16) / 512.0F;
		arg.method_1315((double)(i + 0), (double)(j + 16), (double)this.blitOffset).method_1312((double)g, (double)m).method_1344();
		arg.method_1315((double)(i + k), (double)(j + 16), (double)this.blitOffset).method_1312((double)h, (double)m).method_1344();
		arg.method_1315((double)(i + k), (double)(j + 0), (double)this.blitOffset).method_1312((double)h, (double)l).method_1344();
		arg.method_1315((double)(i + 0), (double)(j + 0), (double)this.blitOffset).method_1312((double)g, (double)l).method_1344();
	}

	@Override
	public boolean method_18640() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public static class class_4290 {
		public final int field_19226;
		public int field_19227;
		public final int field_19228;

		public class_4290(int i, int j, int k) {
			this.field_19226 = i;
			this.field_19227 = j;
			this.field_19228 = k;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_4291 {
		public final int field_19229;
		public final int field_19230;
		protected static final class_4289.class_4291 field_19231 = new class_4289.class_4291(0, 0);

		public class_4291(int i, int j) {
			this.field_19229 = i;
			this.field_19230 = j;
		}
	}
}
