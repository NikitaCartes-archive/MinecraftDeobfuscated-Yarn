package net.minecraft;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;

public abstract class class_3309<T> extends class_3330<T> {
	public static final SimpleDateFormat field_14308 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	protected final Date field_14306;
	protected final String field_14304;
	protected final Date field_14305;
	protected final String field_14307;

	public class_3309(T object, @Nullable Date date, @Nullable String string, @Nullable Date date2, @Nullable String string2) {
		super(object);
		this.field_14306 = date == null ? new Date() : date;
		this.field_14304 = string == null ? "(Unknown)" : string;
		this.field_14305 = date2;
		this.field_14307 = string2 == null ? "Banned by an operator." : string2;
	}

	protected class_3309(T object, JsonObject jsonObject) {
		super(object, jsonObject);

		Date date;
		try {
			date = jsonObject.has("created") ? field_14308.parse(jsonObject.get("created").getAsString()) : new Date();
		} catch (ParseException var7) {
			date = new Date();
		}

		this.field_14306 = date;
		this.field_14304 = jsonObject.has("source") ? jsonObject.get("source").getAsString() : "(Unknown)";

		Date date2;
		try {
			date2 = jsonObject.has("expires") ? field_14308.parse(jsonObject.get("expires").getAsString()) : null;
		} catch (ParseException var6) {
			date2 = null;
		}

		this.field_14305 = date2;
		this.field_14307 = jsonObject.has("reason") ? jsonObject.get("reason").getAsString() : "Banned by an operator.";
	}

	public String method_14501() {
		return this.field_14304;
	}

	public Date method_14502() {
		return this.field_14305;
	}

	public String method_14503() {
		return this.field_14307;
	}

	public abstract class_2561 method_14504();

	@Override
	boolean method_14627() {
		return this.field_14305 == null ? false : this.field_14305.before(new Date());
	}

	@Override
	protected void method_14628(JsonObject jsonObject) {
		jsonObject.addProperty("created", field_14308.format(this.field_14306));
		jsonObject.addProperty("source", this.field_14304);
		jsonObject.addProperty("expires", this.field_14305 == null ? "forever" : field_14308.format(this.field_14305));
		jsonObject.addProperty("reason", this.field_14307);
	}
}
