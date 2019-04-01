package net.minecraft;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4281 extends class_437 {
	private static final class_2960 field_19193 = new class_2960("sound.sys");

	public class_4281() {
		super(new class_2585("Credits"));
	}

	public static void method_20260(class_310 arg) {
		arg.method_1531().method_4616(field_19193, new class_4281.class_4282(field_19193));
	}

	@Override
	public void render(int i, int j, float f) {
		this.minecraft.method_1531().method_4618(field_19193);
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315(0.0, (double)this.height, (double)this.blitOffset).method_1312(0.0, 1.0).method_1344();
		lv2.method_1315((double)this.width, (double)this.height, (double)this.blitOffset).method_1312(1.0, 1.0).method_1344();
		lv2.method_1315((double)this.width, 0.0, (double)this.blitOffset).method_1312(1.0, 0.0).method_1344();
		lv2.method_1315(0.0, 0.0, (double)this.blitOffset).method_1312(0.0, 0.0).method_1344();
		lv.method_1350();
	}

	@Environment(EnvType.CLIENT)
	public static class class_4282 extends class_1049 {
		private final class_2960 field_19194;

		public class_4282(class_2960 arg) {
			super(arg);
			this.field_19194 = arg;
		}

		@Override
		protected class_1049.class_4006 method_18153(class_3300 arg) {
			class_310 lv = class_310.method_1551();
			class_3268 lv2 = lv.method_1516().method_4633();
			byte[] bs = new byte[4];

			try {
				InputStream inputStream = lv2.method_14405(class_3264.field_14188, this.field_19194);
				Throwable var6 = null;

				class_1049.class_4006 var9;
				try {
					inputStream.read(bs);
					InputStream inputStream2 = new FilterInputStream(inputStream) {
						public int read() throws IOException {
							return super.read() ^ 113;
						}

						public int read(byte[] bs, int i, int j) throws IOException {
							int k = super.read(bs, i, j);

							for (int l = 0; l < k; l++) {
								bs[l + i] = (byte)(bs[l + i] ^ 113);
							}

							return k;
						}
					};
					Throwable var8 = null;

					try {
						var9 = new class_1049.class_4006(null, class_1011.method_4309(inputStream2));
					} catch (Throwable var34) {
						var8 = var34;
						throw var34;
					} finally {
						if (inputStream2 != null) {
							if (var8 != null) {
								try {
									inputStream2.close();
								} catch (Throwable var33) {
									var8.addSuppressed(var33);
								}
							} else {
								inputStream2.close();
							}
						}
					}
				} catch (Throwable var36) {
					var6 = var36;
					throw var36;
				} finally {
					if (inputStream != null) {
						if (var6 != null) {
							try {
								inputStream.close();
							} catch (Throwable var32) {
								var6.addSuppressed(var32);
							}
						} else {
							inputStream.close();
						}
					}
				}

				return var9;
			} catch (IOException var38) {
				return new class_1049.class_4006(var38);
			}
		}
	}
}
