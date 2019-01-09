package net.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.DisconnectedRealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_635 implements class_2896 {
	private static final Logger field_3710 = LogManager.getLogger();
	private final class_310 field_3708;
	@Nullable
	private final class_437 field_3706;
	private final Consumer<class_2561> field_3711;
	private final class_2535 field_3707;
	private GameProfile field_3709;

	public class_635(class_2535 arg, class_310 arg2, @Nullable class_437 arg3, Consumer<class_2561> consumer) {
		this.field_3707 = arg;
		this.field_3708 = arg2;
		this.field_3706 = arg3;
		this.field_3711 = consumer;
	}

	@Override
	public void method_12587(class_2905 arg) {
		SecretKey secretKey = class_3515.method_15239();
		PublicKey publicKey = arg.method_12611();
		String string = new BigInteger(class_3515.method_15240(arg.method_12610(), publicKey, secretKey)).toString(16);
		class_2917 lv = new class_2917(secretKey, publicKey, arg.method_12613());
		this.field_3711.accept(new class_2588("connect.authorizing"));
		class_3521.field_15664.submit((Runnable)(() -> {
			class_2561 lvx = this.method_2892(string);
			if (lvx != null) {
				if (this.field_3708.method_1558() == null || !this.field_3708.method_1558().method_2994()) {
					this.field_3707.method_10747(lvx);
					return;
				}

				field_3710.warn(lvx.getString());
			}

			this.field_3711.accept(new class_2588("connect.encrypting"));
			this.field_3707.method_10752(lv, future -> this.field_3707.method_10746(secretKey));
		}));
	}

	@Nullable
	private class_2561 method_2892(String string) {
		try {
			this.method_2891().joinServer(this.field_3708.method_1548().method_1677(), this.field_3708.method_1548().method_1674(), string);
			return null;
		} catch (AuthenticationUnavailableException var3) {
			return new class_2588("disconnect.loginFailedInfo", new class_2588("disconnect.loginFailedInfo.serversUnavailable"));
		} catch (InvalidCredentialsException var4) {
			return new class_2588("disconnect.loginFailedInfo", new class_2588("disconnect.loginFailedInfo.invalidSession"));
		} catch (AuthenticationException var5) {
			return new class_2588("disconnect.loginFailedInfo", var5.getMessage());
		}
	}

	private MinecraftSessionService method_2891() {
		return this.field_3708.method_1495();
	}

	@Override
	public void method_12588(class_2901 arg) {
		this.field_3711.accept(new class_2588("connect.joining"));
		this.field_3709 = arg.method_12593();
		this.field_3707.method_10750(class_2539.field_11690);
		this.field_3707.method_10763(new class_634(this.field_3708, this.field_3706, this.field_3707, this.field_3709));
	}

	@Override
	public void method_10839(class_2561 arg) {
		if (this.field_3706 != null && this.field_3706 instanceof class_399) {
			this.field_3708.method_1507(new DisconnectedRealmsScreen(((class_399)this.field_3706).method_2069(), "connect.failed", arg).getProxy());
		} else {
			this.field_3708.method_1507(new class_419(this.field_3706, "connect.failed", arg));
		}
	}

	@Override
	public void method_12584(class_2909 arg) {
		this.field_3707.method_10747(arg.method_12638());
	}

	@Override
	public void method_12585(class_2907 arg) {
		if (!this.field_3707.method_10756()) {
			this.field_3707.method_10760(arg.method_12634());
		}
	}

	@Override
	public void method_12586(class_2899 arg) {
		this.field_3711.accept(new class_2588("connect.negotiating"));
		this.field_3707.method_10743(new class_2913(arg.method_12592(), null));
	}
}
