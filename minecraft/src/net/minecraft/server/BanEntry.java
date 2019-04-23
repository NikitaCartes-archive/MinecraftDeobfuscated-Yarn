package net.minecraft.server;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public abstract class BanEntry<T> extends ServerConfigEntry<T> {
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	protected final Date creationDate;
	protected final String source;
	protected final Date expiryDate;
	protected final String reason;

	public BanEntry(T object, @Nullable Date date, @Nullable String string, @Nullable Date date2, @Nullable String string2) {
		super(object);
		this.creationDate = date == null ? new Date() : date;
		this.source = string == null ? "(Unknown)" : string;
		this.expiryDate = date2;
		this.reason = string2 == null ? "Banned by an operator." : string2;
	}

	protected BanEntry(T object, JsonObject jsonObject) {
		super(object, jsonObject);

		Date date;
		try {
			date = jsonObject.has("created") ? DATE_FORMAT.parse(jsonObject.get("created").getAsString()) : new Date();
		} catch (ParseException var7) {
			date = new Date();
		}

		this.creationDate = date;
		this.source = jsonObject.has("source") ? jsonObject.get("source").getAsString() : "(Unknown)";

		Date date2;
		try {
			date2 = jsonObject.has("expires") ? DATE_FORMAT.parse(jsonObject.get("expires").getAsString()) : null;
		} catch (ParseException var6) {
			date2 = null;
		}

		this.expiryDate = date2;
		this.reason = jsonObject.has("reason") ? jsonObject.get("reason").getAsString() : "Banned by an operator.";
	}

	public String getSource() {
		return this.source;
	}

	public Date getExpiryDate() {
		return this.expiryDate;
	}

	public String getReason() {
		return this.reason;
	}

	public abstract Component asTextComponent();

	@Override
	boolean isInvalid() {
		return this.expiryDate == null ? false : this.expiryDate.before(new Date());
	}

	@Override
	protected void serialize(JsonObject jsonObject) {
		jsonObject.addProperty("created", DATE_FORMAT.format(this.creationDate));
		jsonObject.addProperty("source", this.source);
		jsonObject.addProperty("expires", this.expiryDate == null ? "forever" : DATE_FORMAT.format(this.expiryDate));
		jsonObject.addProperty("reason", this.reason);
	}
}
