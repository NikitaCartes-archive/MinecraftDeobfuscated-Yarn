package net.minecraft.client.realms.dto;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;

@Environment(EnvType.CLIENT)
public class PingResult extends ValueObject implements RealmsSerializable {
	@SerializedName("pingResults")
	public List<RegionPingResult> pingResults = Lists.<RegionPingResult>newArrayList();
	@SerializedName("worldIds")
	public List<Long> worldIds = Lists.<Long>newArrayList();
}
