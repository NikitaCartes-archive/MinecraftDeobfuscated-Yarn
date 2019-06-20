package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.function.Consumer;

public class class_73 extends class_85 {
	private class_73(int i, int j, class_209[] args, class_117[] args2) {
		super(i, j, args, args2);
	}

	@Override
	public void method_433(Consumer<class_1799> consumer, class_47 arg) {
	}

	public static class_85.class_86<?> method_401() {
		return method_434(class_73::new);
	}

	public static class class_74 extends class_85.class_90<class_73> {
		public class_74() {
			super(new class_2960("empty"), class_73.class);
		}

		protected class_73 method_402(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, int i, int j, class_209[] args, class_117[] args2) {
			return new class_73(i, j, args, args2);
		}
	}
}
