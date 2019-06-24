package net.minecraft.client.render.entity.feature;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VillagerResourceMetadata {
	public static final VillagerResourceMetadataReader READER = new VillagerResourceMetadataReader();
	private final VillagerResourceMetadata.HatType hatType;

	public VillagerResourceMetadata(VillagerResourceMetadata.HatType hatType) {
		this.hatType = hatType;
	}

	public VillagerResourceMetadata.HatType getHatType() {
		return this.hatType;
	}

	@Environment(EnvType.CLIENT)
	public static enum HatType {
		NONE("none"),
		PARTIAL("partial"),
		FULL("full");

		private static final Map<String, VillagerResourceMetadata.HatType> byName = (Map<String, VillagerResourceMetadata.HatType>)Arrays.stream(values())
			.collect(Collectors.toMap(VillagerResourceMetadata.HatType::getName, hatType -> hatType));
		private final String name;

		private HatType(String string2) {
			this.name = string2;
		}

		public String getName() {
			return this.name;
		}

		public static VillagerResourceMetadata.HatType from(String string) {
			return (VillagerResourceMetadata.HatType)byName.getOrDefault(string, NONE);
		}
	}
}
