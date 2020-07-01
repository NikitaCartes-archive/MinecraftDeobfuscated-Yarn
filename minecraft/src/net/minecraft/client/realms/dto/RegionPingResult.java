package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;

@Environment(EnvType.CLIENT)
public class RegionPingResult extends ValueObject implements RealmsSerializable {
	@SerializedName("regionName")
	private final String regionName;
	@SerializedName("ping")
	private final int ping;

	public RegionPingResult(String regionName, int ping) {
		this.regionName = regionName;
		this.ping = ping;
	}

	public int getPing() {
		return this.ping;
	}

	@Override
	public String toString() {
		return String.format(Locale.ROOT, "%s --> %.2f ms", this.regionName, (float)this.ping);
	}
}
