package net.minecraft;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;

public class class_3320 extends class_3309<String> {
	public class_3320(String string) {
		this(string, null, null, null, null);
	}

	public class_3320(String string, @Nullable Date date, @Nullable String string2, @Nullable Date date2, @Nullable String string3) {
		super(string, date, string2, date2, string3);
	}

	@Override
	public class_2561 method_14504() {
		return new class_2585(this.method_14626());
	}

	public class_3320(JsonObject jsonObject) {
		super(method_14532(jsonObject), jsonObject);
	}

	private static String method_14532(JsonObject jsonObject) {
		return jsonObject.has("ip") ? jsonObject.get("ip").getAsString() : null;
	}

	@Override
	protected void method_14628(JsonObject jsonObject) {
		if (this.method_14626() != null) {
			jsonObject.addProperty("ip", this.method_14626());
			super.method_14628(jsonObject);
		}
	}
}
