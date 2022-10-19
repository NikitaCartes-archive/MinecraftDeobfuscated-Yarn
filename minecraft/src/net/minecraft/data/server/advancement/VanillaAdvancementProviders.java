package net.minecraft.data.server.advancement;

import java.util.List;
import net.minecraft.data.DataOutput;

public class VanillaAdvancementProviders {
	public static AdvancementProvider createVanillaProvider(DataOutput output) {
		return new AdvancementProvider(
			"Vanilla Advancements",
			output,
			List.of(
				new EndTabAdvancementGenerator(),
				new HusbandryTabAdvancementGenerator(),
				new AdventureTabAdvancementGenerator(),
				new NetherTabAdvancementGenerator(),
				new StoryTabAdvancementGenerator()
			)
		);
	}
}
