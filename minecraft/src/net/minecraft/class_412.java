package net.minecraft;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_412 extends class_437 {
	private static final AtomicInteger field_2408 = new AtomicInteger(0);
	private static final Logger field_2410 = LogManager.getLogger();
	private class_2535 field_2411;
	private boolean field_2409;
	private final class_437 field_2412;
	private class_2561 field_2413 = new class_2588("connect.connecting");
	private long field_19097 = -1L;

	public class_412(class_437 arg, class_310 arg2, class_642 arg3) {
		super(class_333.field_18967);
		this.minecraft = arg2;
		this.field_2412 = arg;
		class_639 lv = class_639.method_2950(arg3.field_3761);
		arg2.method_18099();
		arg2.method_1584(arg3);
		this.method_2130(lv.method_2952(), lv.method_2954());
	}

	public class_412(class_437 arg, class_310 arg2, String string, int i) {
		super(class_333.field_18967);
		this.minecraft = arg2;
		this.field_2412 = arg;
		arg2.method_18099();
		this.method_2130(string, i);
	}

	private void method_2130(String string, int i) {
		field_2410.info("Connecting to {}, {}", string, i);
		Thread thread = new Thread("Server Connector #" + field_2408.incrementAndGet()) {
			public void run() {
				InetAddress inetAddress = null;

				try {
					if (class_412.this.field_2409) {
						return;
					}

					inetAddress = InetAddress.getByName(string);
					class_412.this.field_2411 = class_2535.method_10753(inetAddress, i, class_412.this.minecraft.field_1690.method_1639());
					class_412.this.field_2411
						.method_10763(new class_635(class_412.this.field_2411, class_412.this.minecraft, class_412.this.field_2412, arg2 -> class_412.this.method_2131(arg2)));
					class_412.this.field_2411.method_10743(new class_2889(string, i, class_2539.field_11688));
					class_412.this.field_2411.method_10743(new class_2915(class_412.this.minecraft.method_1548().method_1677()));
				} catch (UnknownHostException var4) {
					if (class_412.this.field_2409) {
						return;
					}

					class_412.field_2410.error("Couldn't connect to server", (Throwable)var4);
					class_412.this.minecraft
						.execute(
							() -> class_412.this.minecraft
									.method_1507(new class_419(class_412.this.field_2412, "connect.failed", new class_2588("disconnect.genericReason", "Unknown host")))
						);
				} catch (Exception var5) {
					if (class_412.this.field_2409) {
						return;
					}

					class_412.field_2410.error("Couldn't connect to server", (Throwable)var5);
					String string = inetAddress == null ? var5.toString() : var5.toString().replaceAll(inetAddress + ":" + i, "");
					class_412.this.minecraft
						.execute(
							() -> class_412.this.minecraft
									.method_1507(new class_419(class_412.this.field_2412, "connect.failed", new class_2588("disconnect.genericReason", string)))
						);
				}
			}
		};
		thread.setUncaughtExceptionHandler(new class_140(field_2410));
		thread.start();
	}

	private void method_2131(class_2561 arg) {
		this.field_2413 = arg;
	}

	@Override
	public void tick() {
		if (this.field_2411 != null) {
			if (this.field_2411.method_10758()) {
				this.field_2411.method_10754();
			} else {
				this.field_2411.method_10768();
			}
		}
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	@Override
	protected void init() {
		this.addButton(new class_4185(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, class_1074.method_4662("gui.cancel"), arg -> {
			this.field_2409 = true;
			if (this.field_2411 != null) {
				this.field_2411.method_10747(new class_2588("connect.aborted"));
			}

			this.minecraft.method_1507(this.field_2412);
		}));
	}

	@Override
	public void render(int i, int j, float f) {
		this.renderBackground();
		long l = class_156.method_658();
		if (l - this.field_19097 > 2000L) {
			this.field_19097 = l;
			class_333.field_2054.method_19788(new class_2588("narrator.joining").getString());
		}

		this.drawCenteredString(this.font, this.field_2413.method_10863(), this.width / 2, this.height / 2 - 50, 16777215);
		super.render(i, j, f);
	}
}
