package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum GlUsage {
	DYNAMIC_WRITE(35048, false, true),
	STATIC_WRITE(35044, false, true),
	STREAM_WRITE(35040, false, true),
	STATIC_READ(35045, true, false),
	DYNAMIC_READ(35049, true, false),
	STREAM_READ(35041, true, false),
	DYNAMIC_COPY(35050, false, false),
	STATIC_COPY(35046, false, false),
	STREAM_COPY(35042, false, false);

	final int id;
	final boolean readable;
	final boolean writable;

	private GlUsage(final int id, final boolean readable, final boolean writable) {
		this.id = id;
		this.readable = readable;
		this.writable = writable;
	}
}
