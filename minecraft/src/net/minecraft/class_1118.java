package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.audio.MovingSoundInstance;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

@Environment(EnvType.CLIENT)
public class class_1118 {
	@Environment(EnvType.CLIENT)
	public static class class_1119 extends MovingSoundInstance {
		private final ClientPlayerEntity field_5482;

		protected class_1119(ClientPlayerEntity clientPlayerEntity, SoundEvent soundEvent) {
			super(soundEvent, SoundCategory.field_15256);
			this.field_5482 = clientPlayerEntity;
			this.repeat = false;
			this.repeatDelay = 0;
			this.volume = 1.0F;
			this.field_5445 = true;
		}

		@Override
		public void method_16896() {
			if (!this.field_5482.invalid && this.field_5482.isInWater()) {
				this.x = (float)this.field_5482.x;
				this.y = (float)this.field_5482.y;
				this.z = (float)this.field_5482.z;
			} else {
				this.done = true;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static class class_1120 extends MovingSoundInstance {
		private final ClientPlayerEntity field_5483;
		private int field_5484;

		public class_1120(ClientPlayerEntity clientPlayerEntity) {
			super(SoundEvents.field_14951, SoundCategory.field_15256);
			this.field_5483 = clientPlayerEntity;
			this.repeat = true;
			this.repeatDelay = 0;
			this.volume = 1.0F;
			this.field_5445 = true;
		}

		@Override
		public void method_16896() {
			if (!this.field_5483.invalid && this.field_5484 >= 0) {
				this.x = (float)this.field_5483.x;
				this.y = (float)this.field_5483.y;
				this.z = (float)this.field_5483.z;
				if (this.field_5483.isInWater()) {
					this.field_5484++;
				} else {
					this.field_5484 -= 2;
				}

				this.field_5484 = Math.min(this.field_5484, 40);
				this.volume = Math.max(0.0F, Math.min((float)this.field_5484 / 40.0F, 1.0F));
			} else {
				this.done = true;
			}
		}
	}
}
