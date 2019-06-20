package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1045 extends class_1044 {
	private static final Logger field_5207 = LogManager.getLogger();
	private final class_2960 field_5209;
	private final List<String> field_5206;
	private final List<class_1767> field_5208;

	public class_1045(class_2960 arg, List<String> list, List<class_1767> list2) {
		this.field_5209 = arg;
		this.field_5206 = list;
		this.field_5208 = list2;
	}

	@Override
	public void method_4625(class_3300 arg) throws IOException {
		try {
			class_3298 lv = arg.method_14486(this.field_5209);
			Throwable var3 = null;

			try (
				class_1011 lv2 = class_1011.method_4309(lv.method_14482());
				class_1011 lv3 = new class_1011(lv2.method_4307(), lv2.method_4323(), false);
			) {
				lv3.method_4317(lv2);

				for (int i = 0; i < 17 && i < this.field_5206.size() && i < this.field_5208.size(); i++) {
					String string = (String)this.field_5206.get(i);
					if (string != null) {
						class_3298 lv4 = arg.method_14486(new class_2960(string));
						Throwable var11 = null;

						try (class_1011 lv5 = class_1011.method_4309(lv4.method_14482())) {
							int j = ((class_1767)this.field_5208.get(i)).method_7788();
							if (lv5.method_4307() == lv3.method_4307() && lv5.method_4323() == lv3.method_4323()) {
								for (int k = 0; k < lv5.method_4323(); k++) {
									for (int l = 0; l < lv5.method_4307(); l++) {
										int m = lv5.method_4315(l, k);
										if ((m & 0xFF000000) != 0) {
											int n = (m & 0xFF) << 24 & 0xFF000000;
											int o = lv2.method_4315(l, k);
											int p = class_3532.method_15377(o, j) & 16777215;
											lv3.method_4328(l, k, n | p);
										}
									}
								}
							}
						} catch (Throwable var142) {
							var11 = var142;
							throw var142;
						} finally {
							if (lv4 != null) {
								if (var11 != null) {
									try {
										lv4.close();
									} catch (Throwable var138) {
										var11.addSuppressed(var138);
									}
								} else {
									lv4.close();
								}
							}
						}
					}
				}

				TextureUtil.prepareImage(this.method_4624(), lv3.method_4307(), lv3.method_4323());
				GlStateManager.pixelTransfer(3357, Float.MAX_VALUE);
				lv3.method_4301(0, 0, 0, false);
				GlStateManager.pixelTransfer(3357, 0.0F);
			} catch (Throwable var148) {
				var3 = var148;
				throw var148;
			} finally {
				if (lv != null) {
					if (var3 != null) {
						try {
							lv.close();
						} catch (Throwable var135) {
							var3.addSuppressed(var135);
						}
					} else {
						lv.close();
					}
				}
			}
		} catch (IOException var150) {
			field_5207.error("Couldn't load layered color mask image", (Throwable)var150);
		}
	}
}
