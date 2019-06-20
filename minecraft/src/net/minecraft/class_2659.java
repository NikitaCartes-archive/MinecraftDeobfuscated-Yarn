package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2659 extends class_2650 {
	@Nullable
	@Override
	protected class_2944<class_3111> method_11430(Random random) {
		return new class_3190(class_3111::method_13565, true);
	}

	@Nullable
	@Override
	protected class_2944<class_3111> method_11443(Random random) {
		return new class_3090(class_3111::method_13565, false, random.nextBoolean());
	}
}
