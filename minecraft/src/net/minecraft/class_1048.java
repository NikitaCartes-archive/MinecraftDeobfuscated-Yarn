package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1048 extends class_1044 {
	private static final Logger field_5223 = LogManager.getLogger();
	public final List<String> field_5222;

	public class_1048(String... strings) {
		this.field_5222 = Lists.<String>newArrayList(strings);
		if (this.field_5222.isEmpty()) {
			throw new IllegalStateException("Layered texture with no layers.");
		}
	}

	@Override
	public void method_4625(class_3300 arg) throws IOException {
		Iterator<String> iterator = this.field_5222.iterator();
		String string = (String)iterator.next();

		try {
			class_3298 lv = arg.method_14486(new class_2960(string));
			Throwable var5 = null;

			try (class_1011 lv2 = class_1011.method_4309(lv.method_14482())) {
				while (true) {
					if (!iterator.hasNext()) {
						TextureUtil.prepareImage(this.method_4624(), lv2.method_4307(), lv2.method_4323());
						lv2.method_4301(0, 0, 0, false);
						break;
					}

					String string2 = (String)iterator.next();
					if (string2 != null) {
						class_3298 lv3 = arg.method_14486(new class_2960(string2));
						Throwable var10 = null;

						try (class_1011 lv4 = class_1011.method_4309(lv3.method_14482())) {
							for (int i = 0; i < lv4.method_4323(); i++) {
								for (int j = 0; j < lv4.method_4307(); j++) {
									lv2.method_4328(j, i, lv4.method_4315(j, i));
								}
							}
						} catch (Throwable var91) {
							var10 = var91;
							throw var91;
						} finally {
							if (lv3 != null) {
								if (var10 != null) {
									try {
										lv3.close();
									} catch (Throwable var87) {
										var10.addSuppressed(var87);
									}
								} else {
									lv3.close();
								}
							}
						}
					}
				}
			} catch (Throwable var95) {
				var5 = var95;
				throw var95;
			} finally {
				if (lv != null) {
					if (var5 != null) {
						try {
							lv.close();
						} catch (Throwable var85) {
							var5.addSuppressed(var85);
						}
					} else {
						lv.close();
					}
				}
			}
		} catch (IOException var97) {
			field_5223.error("Couldn't load layered image", (Throwable)var97);
		}
	}
}
