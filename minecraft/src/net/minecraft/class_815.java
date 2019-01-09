package net.minecraft;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface class_815 {
	class_815 field_16900 = arg -> argx -> true;
	class_815 field_16901 = arg -> argx -> false;

	Predicate<class_2680> getPredicate(class_2689<class_2248, class_2680> arg);
}
