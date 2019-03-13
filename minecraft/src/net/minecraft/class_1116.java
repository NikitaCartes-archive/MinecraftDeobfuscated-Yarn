package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.audio.SoundLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;

@Environment(EnvType.CLIENT)
public class class_1116 implements class_1104 {
	private final ClientPlayerEntity field_5481;
	private final SoundLoader field_5479;
	private int field_5480 = 0;

	public class_1116(ClientPlayerEntity clientPlayerEntity, SoundLoader soundLoader) {
		this.field_5481 = clientPlayerEntity;
		this.field_5479 = soundLoader;
	}

	@Override
	public void method_4756() {
		this.field_5480--;
		if (this.field_5480 <= 0 && this.field_5481.isInWater()) {
			float f = this.field_5481.field_6002.random.nextFloat();
			if (f < 1.0E-4F) {
				this.field_5480 = 0;
				this.field_5479.play(new class_1118.class_1119(this.field_5481, SoundEvents.field_15178));
			} else if (f < 0.001F) {
				this.field_5480 = 0;
				this.field_5479.play(new class_1118.class_1119(this.field_5481, SoundEvents.field_15068));
			} else if (f < 0.01F) {
				this.field_5480 = 0;
				this.field_5479.play(new class_1118.class_1119(this.field_5481, SoundEvents.field_15028));
			}
		}
	}
}
