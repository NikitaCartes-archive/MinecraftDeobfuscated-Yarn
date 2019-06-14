package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.sound.SoundEvents;

@Environment(EnvType.CLIENT)
public class AmbientSoundPlayer implements ClientPlayerTickable {
	private final ClientPlayerEntity player;
	private final SoundManager field_5479;
	private int ticksUntilPlay = 0;

	public AmbientSoundPlayer(ClientPlayerEntity clientPlayerEntity, SoundManager soundManager) {
		this.player = clientPlayerEntity;
		this.field_5479 = soundManager;
	}

	@Override
	public void tick() {
		this.ticksUntilPlay--;
		if (this.ticksUntilPlay <= 0 && this.player.isInWater()) {
			float f = this.player.field_6002.random.nextFloat();
			if (f < 1.0E-4F) {
				this.ticksUntilPlay = 0;
				this.field_5479.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.field_15178));
			} else if (f < 0.001F) {
				this.ticksUntilPlay = 0;
				this.field_5479.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.field_15068));
			} else if (f < 0.01F) {
				this.ticksUntilPlay = 0;
				this.field_5479.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.field_15028));
			}
		}
	}
}
