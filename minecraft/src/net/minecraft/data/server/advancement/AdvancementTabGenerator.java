package net.minecraft.data.server.advancement;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;

public interface AdvancementTabGenerator {
	void accept(Consumer<Advancement> exporter);
}
