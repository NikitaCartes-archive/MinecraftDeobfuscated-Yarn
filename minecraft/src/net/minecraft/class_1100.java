package net.minecraft;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface class_1100 {
	Collection<class_2960> method_4755();

	Collection<class_2960> method_4754(Function<class_2960, class_1100> function, Set<String> set);

	@Nullable
	class_1087 method_4753(class_1088 arg, Function<class_2960, class_1058> function, class_3665 arg2);
}
