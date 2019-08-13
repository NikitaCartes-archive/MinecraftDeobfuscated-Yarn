package net.minecraft.server;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class BannedIpEntry extends BanEntry<String> {
	public BannedIpEntry(String string) {
		this(string, null, null, null, null);
	}

	public BannedIpEntry(String string, @Nullable Date date, @Nullable String string2, @Nullable Date date2, @Nullable String string3) {
		super(string, date, string2, date2, string3);
	}

	@Override
	public Text toText() {
		return new LiteralText(this.getKey());
	}

	public BannedIpEntry(JsonObject jsonObject) {
		super(getIpFromJson(jsonObject), jsonObject);
	}

	private static String getIpFromJson(JsonObject jsonObject) {
		return jsonObject.has("ip") ? jsonObject.get("ip").getAsString() : null;
	}

	@Override
	protected void serialize(JsonObject jsonObject) {
		if (this.getKey() != null) {
			jsonObject.addProperty("ip", this.getKey());
			super.serialize(jsonObject);
		}
	}
}
