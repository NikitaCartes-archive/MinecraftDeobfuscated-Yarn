package net.minecraft.world.gen.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

public class ChunkGeneratorConfig {
	protected int villageSpacing = 32;
	protected final int villageSeparation = 8;
	protected int oceanMonumentSpacing = 32;
	protected int oceanMonumentSeparation = 5;
	protected int strongholdSpacing = 32;
	protected int strongholdCount = 128;
	protected int strongholdSpread = 3;
	protected int templeSpacing = 32;
	protected final int templeSeparation = 8;
	protected final int shipwreckSpacing = 20;
	protected final int shipwreckSeparation = 8;
	protected int endCitySpacing = 20;
	protected final int endCitySeparation = 11;
	protected final int oceanRuinSpacing = 24;
	protected final int oceanRuinSeparation = 4;
	protected int mansionSpacing = 80;
	protected final int mansionSeparation = 20;
	protected final int netherStructureSpacing = 30;
	protected final int netherStructureSeparation = 4;
	protected final int netherStructureSeedModifier = 30084232;
	protected int ruinedPortalSpacing = 40;
	protected int ruinedPortalSeparation = 15;

	public int getNetherStructureSpacing() {
		return 30;
	}

	public int getNetherStructureSeparation() {
		return 4;
	}

	public int getNetherStructureSeedModifier() {
		return 30084232;
	}

	public int getVillageSpacing() {
		return this.villageSpacing;
	}

	public int getVillageSeparation() {
		return 8;
	}

	public int getOceanMonumentSpacing() {
		return this.oceanMonumentSpacing;
	}

	public int getOceanMonumentSeparation() {
		return this.oceanMonumentSeparation;
	}

	public int getStrongholdSpacing() {
		return this.strongholdSpacing;
	}

	public int getStrongholdCount() {
		return this.strongholdCount;
	}

	public int getStrongholdSpread() {
		return this.strongholdSpread;
	}

	public int getTempleSpacing() {
		return this.templeSpacing;
	}

	public int getTempleSeparation() {
		return 8;
	}

	public int getShipwreckSpacing() {
		return 24;
	}

	public int getShipwreckSeparation() {
		return 4;
	}

	public int getOceanRuinSpacing() {
		return 20;
	}

	public int getOceanRuinSeparation() {
		return 8;
	}

	public int getEndCitySpacing() {
		return this.endCitySpacing;
	}

	public int getEndCitySeparation() {
		return 11;
	}

	public int getMansionSpacing() {
		return this.mansionSpacing;
	}

	public int getMansionSeparation() {
		return 20;
	}

	public int getRuinedPortalSpacing() {
		return this.ruinedPortalSpacing;
	}

	public int getRuinedPortalSeparation() {
		return this.ruinedPortalSeparation;
	}

	@Environment(EnvType.CLIENT)
	public void method_28000(String string, String string2, String string3) {
		if ("village".equals(string) && "distance".equals(string2)) {
			this.villageSpacing = MathHelper.parseInt(string3, this.villageSpacing, 9);
		}

		if ("biome_1".equals(string) && "distance".equals(string2)) {
			this.templeSpacing = MathHelper.parseInt(string3, this.templeSpacing, 9);
		}

		if ("stronghold".equals(string)) {
			if ("distance".equals(string2)) {
				this.strongholdSpacing = MathHelper.parseInt(string3, this.strongholdSpacing, 1);
			} else if ("count".equals(string2)) {
				this.strongholdCount = MathHelper.parseInt(string3, this.strongholdCount, 1);
			} else if ("spread".equals(string2)) {
				this.strongholdSpread = MathHelper.parseInt(string3, this.strongholdSpread, 1);
			}
		}

		if ("oceanmonument".equals(string)) {
			if ("separation".equals(string2)) {
				this.oceanMonumentSeparation = MathHelper.parseInt(string3, this.oceanMonumentSeparation, 1);
			} else if ("spacing".equals(string2)) {
				this.oceanMonumentSpacing = MathHelper.parseInt(string3, this.oceanMonumentSpacing, 1);
			}
		}

		if ("endcity".equals(string) && "distance".equals(string2)) {
			this.endCitySpacing = MathHelper.parseInt(string3, this.endCitySpacing, 1);
		}

		if ("mansion".equals(string) && "distance".equals(string2)) {
			this.mansionSpacing = MathHelper.parseInt(string3, this.mansionSpacing, 1);
		}
	}
}
