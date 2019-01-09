package net.minecraft;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface class_3300 {
	@Environment(EnvType.CLIENT)
	Set<String> method_14487();

	class_3298 method_14486(class_2960 arg) throws IOException;

	List<class_3298> method_14489(class_2960 arg) throws IOException;

	Collection<class_2960> method_14488(String string, Predicate<String> predicate);
}
