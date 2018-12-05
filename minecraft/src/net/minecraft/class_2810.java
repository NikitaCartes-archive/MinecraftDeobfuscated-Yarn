package net.minecraft;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.BlockView;

public interface class_2810 extends BlockView {
	@Nullable
	class_3449 method_12181(String string);

	void method_12184(String string, class_3449 arg);

	LongSet method_12180(String string);

	void method_12182(String string, long l);

	Map<String, LongSet> method_12179();

	void method_12183(Map<String, LongSet> map);
}
