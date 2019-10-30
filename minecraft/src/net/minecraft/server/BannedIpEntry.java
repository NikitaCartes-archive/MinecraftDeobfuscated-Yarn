package net.minecraft.server;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class BannedIpEntry extends BanEntry<String> {
	public BannedIpEntry(String ip) {
		this(ip, null, null, null, null);
	}

	public BannedIpEntry(String ip, @Nullable Date created, @Nullable String source, @Nullable Date expiry, @Nullable String reason) {
		super(ip, created, source, expiry, reason);
	}

	@Override
	public Text toText() {
		return new LiteralText(this.getKey());
	}

	public BannedIpEntry(JsonObject jsonObject) {
		super(getIpFromJson(jsonObject), jsonObject);
	}

	private static String getIpFromJson(JsonObject json) {
		return json.has("ip") ? json.get("ip").getAsString() : null;
	}

	@Override
	protected void serialize(JsonObject jsonObject) {
		if (this.getKey() != null) {
			jsonObject.addProperty("ip", this.getKey());
			super.serialize(jsonObject);
		}
	}
}
