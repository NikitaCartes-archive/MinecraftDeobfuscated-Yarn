package net.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3248 implements class_2911 {
	private static final AtomicInteger field_14157 = new AtomicInteger(0);
	private static final Logger field_14166 = LogManager.getLogger();
	private static final Random field_14164 = new Random();
	private final byte[] field_14167 = new byte[4];
	private final MinecraftServer field_14162;
	public final class_2535 field_14158;
	private class_3248.class_3249 field_14163 = class_3248.class_3249.field_14170;
	private int field_14156;
	private GameProfile field_14160;
	private final String field_14165 = "";
	private SecretKey field_14159;
	private class_3222 field_14161;

	public class_3248(MinecraftServer minecraftServer, class_2535 arg) {
		this.field_14162 = minecraftServer;
		this.field_14158 = arg;
		field_14164.nextBytes(this.field_14167);
	}

	public void method_18785() {
		if (this.field_14163 == class_3248.class_3249.field_14168) {
			this.method_14384();
		} else if (this.field_14163 == class_3248.class_3249.field_14171) {
			class_3222 lv = this.field_14162.method_3760().method_14602(this.field_14160.getId());
			if (lv == null) {
				this.field_14163 = class_3248.class_3249.field_14168;
				this.field_14162.method_3760().method_14570(this.field_14158, this.field_14161);
				this.field_14161 = null;
			}
		}

		if (this.field_14156++ == 600) {
			this.method_14380(new class_2588("multiplayer.disconnect.slow_login"));
		}
	}

	public void method_14380(class_2561 arg) {
		try {
			field_14166.info("Disconnecting {}: {}", this.method_14383(), arg.getString());
			this.field_14158.method_10743(new class_2909(arg));
			this.field_14158.method_10747(arg);
		} catch (Exception var3) {
			field_14166.error("Error whilst disconnecting player", (Throwable)var3);
		}
	}

	public void method_14384() {
		if (!this.field_14160.isComplete()) {
			this.field_14160 = this.method_14375(this.field_14160);
		}

		class_2561 lv = this.field_14162.method_3760().method_14586(this.field_14158.method_10755(), this.field_14160);
		if (lv != null) {
			this.method_14380(lv);
		} else {
			this.field_14163 = class_3248.class_3249.field_14172;
			if (this.field_14162.method_3773() >= 0 && !this.field_14158.method_10756()) {
				this.field_14158
					.method_10752(new class_2907(this.field_14162.method_3773()), channelFuture -> this.field_14158.method_10760(this.field_14162.method_3773()));
			}

			this.field_14158.method_10743(new class_2901(this.field_14160));
			class_3222 lv2 = this.field_14162.method_3760().method_14602(this.field_14160.getId());
			if (lv2 != null) {
				this.field_14163 = class_3248.class_3249.field_14171;
				this.field_14161 = this.field_14162.method_3760().method_14613(this.field_14160);
			} else {
				this.field_14162.method_3760().method_14570(this.field_14158, this.field_14162.method_3760().method_14613(this.field_14160));
			}
		}
	}

	@Override
	public void method_10839(class_2561 arg) {
		field_14166.info("{} lost connection: {}", this.method_14383(), arg.getString());
	}

	public String method_14383() {
		return this.field_14160 != null ? this.field_14160 + " (" + this.field_14158.method_10755() + ")" : String.valueOf(this.field_14158.method_10755());
	}

	@Override
	public void method_12641(class_2915 arg) {
		Validate.validState(this.field_14163 == class_3248.class_3249.field_14170, "Unexpected hello packet");
		this.field_14160 = arg.method_12650();
		if (this.field_14162.method_3828() && !this.field_14158.method_10756()) {
			this.field_14163 = class_3248.class_3249.field_14175;
			this.field_14158.method_10743(new class_2905("", this.field_14162.method_3716().getPublic(), this.field_14167));
		} else {
			this.field_14163 = class_3248.class_3249.field_14168;
		}
	}

	@Override
	public void method_12642(class_2917 arg) {
		Validate.validState(this.field_14163 == class_3248.class_3249.field_14175, "Unexpected key packet");
		PrivateKey privateKey = this.field_14162.method_3716().getPrivate();
		if (!Arrays.equals(this.field_14167, arg.method_12655(privateKey))) {
			throw new IllegalStateException("Invalid nonce!");
		} else {
			this.field_14159 = arg.method_12654(privateKey);
			this.field_14163 = class_3248.class_3249.field_14169;
			this.field_14158.method_10746(this.field_14159);
			Thread thread = new Thread("User Authenticator #" + field_14157.incrementAndGet()) {
				public void run() {
					GameProfile gameProfile = class_3248.this.field_14160;

					try {
						String string = new BigInteger(class_3515.method_15240("", class_3248.this.field_14162.method_3716().getPublic(), class_3248.this.field_14159))
							.toString(16);
						class_3248.this.field_14160 = class_3248.this.field_14162
							.method_3844()
							.hasJoinedServer(new GameProfile(null, gameProfile.getName()), string, this.method_14386());
						if (class_3248.this.field_14160 != null) {
							class_3248.field_14166.info("UUID of player {} is {}", class_3248.this.field_14160.getName(), class_3248.this.field_14160.getId());
							class_3248.this.field_14163 = class_3248.class_3249.field_14168;
						} else if (class_3248.this.field_14162.method_3724()) {
							class_3248.field_14166.warn("Failed to verify username but will let them in anyway!");
							class_3248.this.field_14160 = class_3248.this.method_14375(gameProfile);
							class_3248.this.field_14163 = class_3248.class_3249.field_14168;
						} else {
							class_3248.this.method_14380(new class_2588("multiplayer.disconnect.unverified_username"));
							class_3248.field_14166.error("Username '{}' tried to join with an invalid session", gameProfile.getName());
						}
					} catch (AuthenticationUnavailableException var3) {
						if (class_3248.this.field_14162.method_3724()) {
							class_3248.field_14166.warn("Authentication servers are down but will let them in anyway!");
							class_3248.this.field_14160 = class_3248.this.method_14375(gameProfile);
							class_3248.this.field_14163 = class_3248.class_3249.field_14168;
						} else {
							class_3248.this.method_14380(new class_2588("multiplayer.disconnect.authservers_down"));
							class_3248.field_14166.error("Couldn't verify username because servers are unavailable");
						}
					}
				}

				@Nullable
				private InetAddress method_14386() {
					SocketAddress socketAddress = class_3248.this.field_14158.method_10755();
					return class_3248.this.field_14162.method_3775() && socketAddress instanceof InetSocketAddress ? ((InetSocketAddress)socketAddress).getAddress() : null;
				}
			};
			thread.setUncaughtExceptionHandler(new class_140(field_14166));
			thread.start();
		}
	}

	@Override
	public void method_12640(class_2913 arg) {
		this.method_14380(new class_2588("multiplayer.disconnect.unexpected_query_response"));
	}

	protected GameProfile method_14375(GameProfile gameProfile) {
		UUID uUID = class_1657.method_7310(gameProfile.getName());
		return new GameProfile(uUID, gameProfile.getName());
	}

	static enum class_3249 {
		field_14170,
		field_14175,
		field_14169,
		field_14173,
		field_14168,
		field_14171,
		field_14172;
	}
}
