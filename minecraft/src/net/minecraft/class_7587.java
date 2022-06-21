package net.minecraft;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public interface class_7587 {
	class_7587 field_39714 = new class_7587() {
		@Override
		public long method_44643() {
			return 1L;
		}

		@Override
		public long method_44645() {
			return 1L;
		}
	};

	long method_44643();

	long method_44645();

	static class_7587 method_44644(int i) {
		return new class_7587() {
			private static final Logger field_39716 = LogUtils.getLogger();
			private int field_39717;

			@Override
			public long method_44643() {
				this.field_39717 = 0;
				return 1L;
			}

			@Override
			public long method_44645() {
				this.field_39717++;
				long l = Math.min(1L << this.field_39717, (long)i);
				field_39716.debug("Skipping for {} extra cycles", l);
				return l;
			}
		};
	}
}
