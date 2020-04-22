package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvent;

public class class_5195 {
	private final SoundEvent field_24057;
	private final int field_24058;
	private final int field_24059;
	private final boolean field_24060;

	public class_5195(SoundEvent soundEvent, int i, int j, boolean bl) {
		this.field_24057 = soundEvent;
		this.field_24058 = i;
		this.field_24059 = j;
		this.field_24060 = bl;
	}

	@Environment(EnvType.CLIENT)
	public SoundEvent method_27279() {
		return this.field_24057;
	}

	@Environment(EnvType.CLIENT)
	public int method_27280() {
		return this.field_24058;
	}

	@Environment(EnvType.CLIENT)
	public int method_27281() {
		return this.field_24059;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_27282() {
		return this.field_24060;
	}
}
