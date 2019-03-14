package net.minecraft.client.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ElytraSoundInstance extends MovingSoundInstance {
	private final ClientPlayerEntity player;
	private int tickCount;

	public ElytraSoundInstance(ClientPlayerEntity clientPlayerEntity) {
		super(SoundEvents.field_14572, SoundCategory.field_15248);
		this.player = clientPlayerEntity;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.1F;
	}

	@Override
	public void tick() {
		this.tickCount++;
		if (!this.player.invalid && (this.tickCount <= 20 || this.player.isFallFlying())) {
			this.x = (float)this.player.x;
			this.y = (float)this.player.y;
			this.z = (float)this.player.z;
			float f = (float)this.player.getVelocity().lengthSquared();
			if ((double)f >= 1.0E-7) {
				this.volume = MathHelper.clamp(f / 4.0F, 0.0F, 1.0F);
			} else {
				this.volume = 0.0F;
			}

			if (this.tickCount < 20) {
				this.volume = 0.0F;
			} else if (this.tickCount < 40) {
				this.volume = (float)((double)this.volume * ((double)(this.tickCount - 20) / 20.0));
			}

			float g = 0.8F;
			if (this.volume > 0.8F) {
				this.pitch = 1.0F + (this.volume - 0.8F);
			} else {
				this.pitch = 1.0F;
			}
		} else {
			this.done = true;
		}
	}
}
