package net.minecraft;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.Closeable;
import java.io.IOException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1049 extends class_1044 {
	private static final Logger field_5225 = LogManager.getLogger();
	protected final class_2960 field_5224;

	public class_1049(class_2960 arg) {
		this.field_5224 = arg;
	}

	@Override
	public void method_4625(class_3300 arg) throws IOException {
		class_1049.class_4006 lv = this.method_18153(arg);
		Throwable var3 = null;

		try {
			boolean bl = false;
			boolean bl2 = false;
			lv.method_18158();
			class_1084 lv2 = lv.method_18155();
			if (lv2 != null) {
				bl = lv2.method_4696();
				bl2 = lv2.method_4697();
			}

			this.method_4623();
			TextureUtil.prepareImage(this.method_4624(), 0, lv.method_18157().method_4307(), lv.method_18157().method_4323());
			lv.method_18157().method_4321(0, 0, 0, 0, 0, lv.method_18157().method_4307(), lv.method_18157().method_4323(), bl, bl2, false);
		} catch (Throwable var14) {
			var3 = var14;
			throw var14;
		} finally {
			if (lv != null) {
				if (var3 != null) {
					try {
						lv.close();
					} catch (Throwable var13) {
						var3.addSuppressed(var13);
					}
				} else {
					lv.close();
				}
			}
		}
	}

	protected class_1049.class_4006 method_18153(class_3300 arg) {
		return class_1049.class_4006.method_18156(arg, this.field_5224);
	}

	@Environment(EnvType.CLIENT)
	public static class class_4006 implements Closeable {
		private final class_1084 field_17895;
		private final class_1011 field_17896;
		private final IOException field_17897;

		public class_4006(IOException iOException) {
			this.field_17897 = iOException;
			this.field_17895 = null;
			this.field_17896 = null;
		}

		public class_4006(@Nullable class_1084 arg, class_1011 arg2) {
			this.field_17897 = null;
			this.field_17895 = arg;
			this.field_17896 = arg2;
		}

		public static class_1049.class_4006 method_18156(class_3300 arg, class_2960 arg2) {
			try {
				class_3298 lv = arg.method_14486(arg2);
				Throwable var3 = null;

				class_1049.class_4006 runtimeException;
				try {
					class_1011 lv2 = class_1011.method_4309(lv.method_14482());
					class_1084 lv3 = null;

					try {
						lv3 = lv.method_14481(class_1084.field_5344);
					} catch (RuntimeException var17) {
						class_1049.field_5225.warn("Failed reading metadata of: {}", arg2, var17);
					}

					runtimeException = new class_1049.class_4006(lv3, lv2);
				} catch (Throwable var18) {
					var3 = var18;
					throw var18;
				} finally {
					if (lv != null) {
						if (var3 != null) {
							try {
								lv.close();
							} catch (Throwable var16) {
								var3.addSuppressed(var16);
							}
						} else {
							lv.close();
						}
					}
				}

				return runtimeException;
			} catch (IOException var20) {
				return new class_1049.class_4006(var20);
			}
		}

		@Nullable
		public class_1084 method_18155() {
			return this.field_17895;
		}

		public class_1011 method_18157() throws IOException {
			if (this.field_17897 != null) {
				throw this.field_17897;
			} else {
				return this.field_17896;
			}
		}

		public void close() {
			if (this.field_17896 != null) {
				this.field_17896.close();
			}
		}

		public void method_18158() throws IOException {
			if (this.field_17897 != null) {
				throw this.field_17897;
			}
		}
	}
}
