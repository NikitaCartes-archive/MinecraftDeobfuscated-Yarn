package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ElytraSoundInstance extends MovingSoundInstance {
	public static final int field_32996 = 20;
	private final ClientPlayerEntity player;
	private int tickCount;

	public ElytraSoundInstance(ClientPlayerEntity player) {
		super(SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.PLAYERS, SoundInstance.createRandom());
		this.player = player;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.1F;
	}

	@Override
	public void tick() {
		this.tickCount++;
		if (!this.player.isRemoved() && (this.tickCount <= 20 || this.player.isFallFlying())) {
			this.x = (double)((float)this.player.getX());
			this.y = (double)((float)this.player.getY());
			this.z = (double)((float)this.player.getZ());
			float f = (float)this.player.getVelocity().lengthSquared();
			if ((double)f >= 1.0E-7) {
				this.volume = MathHelper.clamp(f / 4.0F, 0.0F, 1.0F);
			} else {
				this.volume = 0.0F;
			}

			if (this.tickCount < 20) {
				this.volume = 0.0F;
			} else if (this.tickCount < 40) {
				this.volume = this.volume * ((float)(this.tickCount - 20) / 20.0F);
			}

			float g = 0.8F;
			if (this.volume > 0.8F) {
				this.pitch = 1.0F + (this.volume - 0.8F);
			} else {
				this.pitch = 1.0F;
			}
		} else {
			this.setDone();
		}
	}
}
