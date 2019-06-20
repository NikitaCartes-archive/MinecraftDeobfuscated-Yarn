package net.minecraft;

import com.mojang.bridge.game.GameSession;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3801 implements GameSession {
	private final int field_16765;
	private final boolean field_16766;
	private final String field_16763;
	private final String field_16764;
	private final UUID field_16767;

	public class_3801(class_638 arg, class_746 arg2, class_634 arg3) {
		this.field_16765 = arg3.method_2880().size();
		this.field_16766 = !arg3.method_2872().method_10756();
		this.field_16763 = arg.method_8407().method_5460();
		class_640 lv = arg3.method_2871(arg2.method_5667());
		if (lv != null) {
			this.field_16764 = lv.method_2958().method_8381();
		} else {
			this.field_16764 = "unknown";
		}

		this.field_16767 = arg3.method_16690();
	}

	@Override
	public int getPlayerCount() {
		return this.field_16765;
	}

	@Override
	public boolean isRemoteServer() {
		return this.field_16766;
	}

	@Override
	public String getDifficulty() {
		return this.field_16763;
	}

	@Override
	public String getGameMode() {
		return this.field_16764;
	}

	@Override
	public UUID getSessionId() {
		return this.field_16767;
	}
}
