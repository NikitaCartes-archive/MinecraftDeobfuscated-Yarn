package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1118 {
	@Environment(EnvType.CLIENT)
	public static class class_1119 extends class_1101 {
		private final class_746 field_5482;

		protected class_1119(class_746 arg, class_3414 arg2) {
			super(arg2, class_3419.field_15256);
			this.field_5482 = arg;
			this.field_5446 = false;
			this.field_5451 = 0;
			this.field_5442 = 1.0F;
			this.field_5445 = true;
		}

		@Override
		public void method_16896() {
			if (!this.field_5482.field_5988 && this.field_5482.method_5869()) {
				this.field_5439 = (float)this.field_5482.field_5987;
				this.field_5450 = (float)this.field_5482.field_6010;
				this.field_5449 = (float)this.field_5482.field_6035;
			} else {
				this.field_5438 = true;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_1120 extends class_1101 {
		private final class_746 field_5483;
		private int field_5484;

		public class_1120(class_746 arg) {
			super(class_3417.field_14951, class_3419.field_15256);
			this.field_5483 = arg;
			this.field_5446 = true;
			this.field_5451 = 0;
			this.field_5442 = 1.0F;
			this.field_5445 = true;
		}

		@Override
		public void method_16896() {
			if (!this.field_5483.field_5988 && this.field_5484 >= 0) {
				this.field_5439 = (float)this.field_5483.field_5987;
				this.field_5450 = (float)this.field_5483.field_6010;
				this.field_5449 = (float)this.field_5483.field_6035;
				if (this.field_5483.method_5869()) {
					this.field_5484++;
				} else {
					this.field_5484 -= 2;
				}

				this.field_5484 = Math.min(this.field_5484, 40);
				this.field_5442 = Math.max(0.0F, Math.min((float)this.field_5484 / 40.0F, 1.0F));
			} else {
				this.field_5438 = true;
			}
		}
	}
}
