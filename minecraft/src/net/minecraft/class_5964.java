package net.minecraft;

import java.util.Date;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.profiler.ProfileResult;

@Environment(EnvType.CLIENT)
public final class class_5964 {
	public final Date field_29595;
	public final int field_29596;
	public final ProfileResult field_29597;

	public class_5964(Date date, int i, ProfileResult profileResult) {
		this.field_29595 = date;
		this.field_29596 = i;
		this.field_29597 = profileResult;
	}
}
