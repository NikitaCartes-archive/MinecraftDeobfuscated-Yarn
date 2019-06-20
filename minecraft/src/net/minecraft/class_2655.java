package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2655 extends class_2650 {
	@Nullable
	@Override
	protected class_2944<class_3111> method_11430(Random random) {
		return new class_3207(
			class_3111::method_13565, true, 4 + random.nextInt(7), class_2246.field_10306.method_9564(), class_2246.field_10335.method_9564(), false
		);
	}

	@Nullable
	@Override
	protected class_2944<class_3111> method_11443(Random random) {
		return new class_3092(class_3111::method_13565, true, 10, 20, class_2246.field_10306.method_9564(), class_2246.field_10335.method_9564());
	}
}
