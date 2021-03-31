package net.minecraft.server;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.text.Text;

public abstract class BanEntry<T> extends ServerConfigEntry<T> {
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	public static final String field_29787 = "forever";
	protected final Date creationDate;
	protected final String source;
	protected final Date expiryDate;
	protected final String reason;

	public BanEntry(T key, @Nullable Date creationDate, @Nullable String source, @Nullable Date expiryDate, @Nullable String reason) {
		super(key);
		this.creationDate = creationDate == null ? new Date() : creationDate;
		this.source = source == null ? "(Unknown)" : source;
		this.expiryDate = expiryDate;
		this.reason = reason == null ? "Banned by an operator." : reason;
	}

	protected BanEntry(T key, JsonObject json) {
		super(key);

		Date date;
		try {
			date = json.has("created") ? DATE_FORMAT.parse(json.get("created").getAsString()) : new Date();
		} catch (ParseException var7) {
			date = new Date();
		}

		this.creationDate = date;
		this.source = json.has("source") ? json.get("source").getAsString() : "(Unknown)";

		Date date2;
		try {
			date2 = json.has("expires") ? DATE_FORMAT.parse(json.get("expires").getAsString()) : null;
		} catch (ParseException var6) {
			date2 = null;
		}

		this.expiryDate = date2;
		this.reason = json.has("reason") ? json.get("reason").getAsString() : "Banned by an operator.";
	}

	public Date method_34885() {
		return this.creationDate;
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

	public abstract Text toText();

	@Override
	boolean isInvalid() {
		return this.expiryDate == null ? false : this.expiryDate.before(new Date());
	}

	@Override
	protected void fromJson(JsonObject json) {
		json.addProperty("created", DATE_FORMAT.format(this.creationDate));
		json.addProperty("source", this.source);
		json.addProperty("expires", this.expiryDate == null ? "forever" : DATE_FORMAT.format(this.expiryDate));
		json.addProperty("reason", this.reason);
	}
}
