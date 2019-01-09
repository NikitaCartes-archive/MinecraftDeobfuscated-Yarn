package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2662 extends class_2647 {
	@Nullable
	@Override
	protected class_2944<class_3111> method_11430(Random random) {
		return (class_2944<class_3111>)(random.nextInt(10) == 0 ? new class_2948(class_3111::method_13565, true) : new class_3207(class_3111::method_13565, true));
	}
}
