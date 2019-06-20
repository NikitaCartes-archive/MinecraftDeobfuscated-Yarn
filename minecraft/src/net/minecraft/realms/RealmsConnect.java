package net.minecraft.realms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2535;
import net.minecraft.class_2539;
import net.minecraft.class_2588;
import net.minecraft.class_2889;
import net.minecraft.class_2915;
import net.minecraft.class_310;
import net.minecraft.class_635;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsConnect {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RealmsScreen onlineScreen;
	private volatile boolean aborted;
	private class_2535 connection;

	public RealmsConnect(RealmsScreen realmsScreen) {
		this.onlineScreen = realmsScreen;
	}

	public void connect(String string, int i) {
		Realms.setConnectedToRealms(true);
		Realms.narrateNow(Realms.getLocalizedString("mco.connect.success"));
		(new Thread("Realms-connect-task") {
				public void run() {
					InetAddress inetAddress = null;

					try {
						inetAddress = InetAddress.getByName(string);
						if (RealmsConnect.this.aborted) {
							return;
						}

						RealmsConnect.this.connection = class_2535.method_10753(inetAddress, i, class_310.method_1551().field_1690.method_1639());
						if (RealmsConnect.this.aborted) {
							return;
						}

						RealmsConnect.this.connection
							.method_10763(new class_635(RealmsConnect.this.connection, class_310.method_1551(), RealmsConnect.this.onlineScreen.getProxy(), arg -> {
							}));
						if (RealmsConnect.this.aborted) {
							return;
						}

						RealmsConnect.this.connection.method_10743(new class_2889(string, i, class_2539.field_11688));
						if (RealmsConnect.this.aborted) {
							return;
						}

						RealmsConnect.this.connection.method_10743(new class_2915(class_310.method_1551().method_1548().method_1677()));
					} catch (UnknownHostException var5) {
						Realms.clearResourcePack();
						if (RealmsConnect.this.aborted) {
							return;
						}

						RealmsConnect.LOGGER.error("Couldn't connect to world", (Throwable)var5);
						Realms.setScreen(
							new DisconnectedRealmsScreen(
								RealmsConnect.this.onlineScreen, "connect.failed", new class_2588("disconnect.genericReason", "Unknown host '" + string + "'")
							)
						);
					} catch (Exception var6) {
						Realms.clearResourcePack();
						if (RealmsConnect.this.aborted) {
							return;
						}

						RealmsConnect.LOGGER.error("Couldn't connect to world", (Throwable)var6);
						String string = var6.toString();
						if (inetAddress != null) {
							String string2 = inetAddress + ":" + i;
							string = string.replaceAll(string2, "");
						}

						Realms.setScreen(new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new class_2588("disconnect.genericReason", string)));
					}
				}
			})
			.start();
	}

	public void abort() {
		this.aborted = true;
		if (this.connection != null && this.connection.method_10758()) {
			this.connection.method_10747(new class_2588("disconnect.genericReason"));
			this.connection.method_10768();
		}
	}

	public void tick() {
		if (this.connection != null) {
			if (this.connection.method_10758()) {
				this.connection.method_10754();
			} else {
				this.connection.method_10768();
			}
		}
	}
}
