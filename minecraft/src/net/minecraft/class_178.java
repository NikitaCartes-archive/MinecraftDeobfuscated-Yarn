package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class class_178 {
	private static final SimpleDateFormat field_1220 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	private Date field_1219;

	public boolean method_784() {
		return this.field_1219 != null;
	}

	public void method_789() {
		this.field_1219 = new Date();
	}

	public void method_790() {
		this.field_1219 = null;
	}

	public Date method_786() {
		return this.field_1219;
	}

	public String toString() {
		return "CriterionProgress{obtained=" + (this.field_1219 == null ? "false" : this.field_1219) + '}';
	}

	public void method_787(class_2540 arg) {
		arg.writeBoolean(this.field_1219 != null);
		if (this.field_1219 != null) {
			arg.method_10796(this.field_1219);
		}
	}

	public JsonElement method_783() {
		return (JsonElement)(this.field_1219 != null ? new JsonPrimitive(field_1220.format(this.field_1219)) : JsonNull.INSTANCE);
	}

	public static class_178 method_785(class_2540 arg) {
		class_178 lv = new class_178();
		if (arg.readBoolean()) {
			lv.field_1219 = arg.method_10802();
		}

		return lv;
	}

	public static class_178 method_788(String string) {
		class_178 lv = new class_178();

		try {
			lv.field_1219 = field_1220.parse(string);
			return lv;
		} catch (ParseException var3) {
			throw new JsonSyntaxException("Invalid datetime: " + string, var3);
		}
	}
}
