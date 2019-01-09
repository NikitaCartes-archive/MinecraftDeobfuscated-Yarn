package net.minecraft;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;

public abstract class class_1918 implements class_2165 {
	private static final SimpleDateFormat field_9169 = new SimpleDateFormat("HH:mm:ss");
	private long field_9167 = -1L;
	private boolean field_9166 = true;
	private int field_9163;
	private boolean field_9164 = true;
	private class_2561 field_9165;
	private String field_9168 = "";
	private class_2561 field_9162 = new class_2585("@");

	public int method_8304() {
		return this.field_9163;
	}

	public void method_8298(int i) {
		this.field_9163 = i;
	}

	public class_2561 method_8292() {
		return (class_2561)(this.field_9165 == null ? new class_2585("") : this.field_9165);
	}

	public class_2487 method_8297(class_2487 arg) {
		arg.method_10582("Command", this.field_9168);
		arg.method_10569("SuccessCount", this.field_9163);
		arg.method_10582("CustomName", class_2561.class_2562.method_10867(this.field_9162));
		arg.method_10556("TrackOutput", this.field_9164);
		if (this.field_9165 != null && this.field_9164) {
			arg.method_10582("LastOutput", class_2561.class_2562.method_10867(this.field_9165));
		}

		arg.method_10556("UpdateLastExecution", this.field_9166);
		if (this.field_9166 && this.field_9167 > 0L) {
			arg.method_10544("LastExecution", this.field_9167);
		}

		return arg;
	}

	public void method_8305(class_2487 arg) {
		this.field_9168 = arg.method_10558("Command");
		this.field_9163 = arg.method_10550("SuccessCount");
		if (arg.method_10573("CustomName", 8)) {
			this.field_9162 = class_2561.class_2562.method_10877(arg.method_10558("CustomName"));
		}

		if (arg.method_10573("TrackOutput", 1)) {
			this.field_9164 = arg.method_10577("TrackOutput");
		}

		if (arg.method_10573("LastOutput", 8) && this.field_9164) {
			try {
				this.field_9165 = class_2561.class_2562.method_10877(arg.method_10558("LastOutput"));
			} catch (Throwable var3) {
				this.field_9165 = new class_2585(var3.getMessage());
			}
		} else {
			this.field_9165 = null;
		}

		if (arg.method_10545("UpdateLastExecution")) {
			this.field_9166 = arg.method_10577("UpdateLastExecution");
		}

		if (this.field_9166 && arg.method_10545("LastExecution")) {
			this.field_9167 = arg.method_10537("LastExecution");
		} else {
			this.field_9167 = -1L;
		}
	}

	public void method_8286(String string) {
		this.field_9168 = string;
		this.field_9163 = 0;
	}

	public String method_8289() {
		return this.field_9168;
	}

	public boolean method_8301(class_1937 arg) {
		if (arg.field_9236 || arg.method_8510() == this.field_9167) {
			return false;
		} else if ("Searge".equalsIgnoreCase(this.field_9168)) {
			this.field_9165 = new class_2585("#itzlipofutzli");
			this.field_9163 = 1;
			return true;
		} else {
			this.field_9163 = 0;
			MinecraftServer minecraftServer = this.method_8293().method_8503();
			if (minecraftServer != null && minecraftServer.method_3814() && minecraftServer.method_3812() && !class_3544.method_15438(this.field_9168)) {
				try {
					this.field_9165 = null;
					class_2168 lv = this.method_8303().method_9231((commandContext, bl, i) -> {
						if (bl) {
							this.field_9163++;
						}
					});
					minecraftServer.method_3734().method_9249(lv, this.field_9168);
				} catch (Throwable var6) {
					class_128 lv2 = class_128.method_560(var6, "Executing command block");
					class_129 lv3 = lv2.method_562("Command to be executed");
					lv3.method_577("Command", this::method_8289);
					lv3.method_577("Name", () -> this.method_8299().getString());
					throw new class_148(lv2);
				}
			}

			if (this.field_9166) {
				this.field_9167 = arg.method_8510();
			} else {
				this.field_9167 = -1L;
			}

			return true;
		}
	}

	public class_2561 method_8299() {
		return this.field_9162;
	}

	public void method_8290(class_2561 arg) {
		this.field_9162 = arg;
	}

	@Override
	public void method_9203(class_2561 arg) {
		if (this.field_9164) {
			this.field_9165 = new class_2585("[" + field_9169.format(new Date()) + "] ").method_10852(arg);
			this.method_8295();
		}
	}

	public abstract class_3218 method_8293();

	public abstract void method_8295();

	public void method_8291(@Nullable class_2561 arg) {
		this.field_9165 = arg;
	}

	public void method_8287(boolean bl) {
		this.field_9164 = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_8296() {
		return this.field_9164;
	}

	public boolean method_8288(class_1657 arg) {
		if (!arg.method_7338()) {
			return false;
		} else {
			if (arg.method_5770().field_9236) {
				arg.method_7257(this);
			}

			return true;
		}
	}

	@Environment(EnvType.CLIENT)
	public abstract class_243 method_8300();

	public abstract class_2168 method_8303();

	@Override
	public boolean method_9200() {
		return this.method_8293().method_8450().method_8355("sendCommandFeedback") && this.field_9164;
	}

	@Override
	public boolean method_9202() {
		return this.field_9164;
	}

	@Override
	public boolean method_9201() {
		return this.method_8293().method_8450().method_8355("commandBlockOutput");
	}
}
