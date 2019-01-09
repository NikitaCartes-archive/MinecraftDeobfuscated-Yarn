package net.minecraft;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
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
		class_3298 lv = arg.method_14486(this.field_5224);
		Throwable var3 = null;

		try (class_1011 lv2 = class_1011.method_4309(lv.method_14482())) {
			boolean bl = false;
			boolean bl2 = false;
			if (lv.method_14484()) {
				try {
					class_1084 lv3 = lv.method_14481(class_1084.field_5344);
					if (lv3 != null) {
						bl = lv3.method_4696();
						bl2 = lv3.method_4697();
					}
				} catch (RuntimeException var32) {
					field_5225.warn("Failed reading metadata of: {}", this.field_5224, var32);
				}
			}

			this.method_4623();
			TextureUtil.prepareImage(this.method_4624(), 0, lv2.method_4307(), lv2.method_4323());
			lv2.method_4321(0, 0, 0, 0, 0, lv2.method_4307(), lv2.method_4323(), bl, bl2, false);
		} catch (Throwable var35) {
			var3 = var35;
			throw var35;
		} finally {
			if (lv != null) {
				if (var3 != null) {
					try {
						lv.close();
					} catch (Throwable var30) {
						var3.addSuppressed(var30);
					}
				} else {
					lv.close();
				}
			}
		}
	}
}
