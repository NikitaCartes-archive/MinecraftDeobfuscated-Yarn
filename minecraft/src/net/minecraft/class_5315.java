package net.minecraft;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_5315 {
	private final int field_25024;
	private final long field_25025;
	private final String field_25026;
	private final int field_25027;
	private final boolean field_25028;

	public class_5315(int i, long l, String string, int j, boolean bl) {
		this.field_25024 = i;
		this.field_25025 = l;
		this.field_25026 = string;
		this.field_25027 = j;
		this.field_25028 = bl;
	}

	public static class_5315 method_29023(Dynamic<?> dynamic) {
		int i = dynamic.get("version").asInt(0);
		long l = dynamic.get("LastPlayed").asLong(0L);
		OptionalDynamic<?> optionalDynamic = dynamic.get("Version");
		return optionalDynamic.result().isPresent()
			? new class_5315(
				i,
				l,
				optionalDynamic.get("Name").asString(SharedConstants.getGameVersion().getName()),
				optionalDynamic.get("Id").asInt(SharedConstants.getGameVersion().getWorldVersion()),
				optionalDynamic.get("Snapshot").asBoolean(!SharedConstants.getGameVersion().isStable())
			)
			: new class_5315(i, l, "", 0, false);
	}

	public int method_29022() {
		return this.field_25024;
	}

	public long method_29024() {
		return this.field_25025;
	}

	@Environment(EnvType.CLIENT)
	public String method_29025() {
		return this.field_25026;
	}

	@Environment(EnvType.CLIENT)
	public int method_29026() {
		return this.field_25027;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_29027() {
		return this.field_25028;
	}
}
