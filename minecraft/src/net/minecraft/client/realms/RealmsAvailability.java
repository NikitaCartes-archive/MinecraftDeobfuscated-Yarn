package net.minecraft.client.realms;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.gui.screen.RealmsClientIncompatibleScreen;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsParentalConsentScreen;
import net.minecraft.client.session.Session;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsAvailability {
	private static final Logger LOGGER = LogUtils.getLogger();
	@Nullable
	private static CompletableFuture<RealmsAvailability.Info> currentFuture;

	public static CompletableFuture<RealmsAvailability.Info> check() {
		if (currentFuture == null || wasUnsuccessful(currentFuture)) {
			currentFuture = checkInternal();
		}

		return currentFuture;
	}

	private static boolean wasUnsuccessful(CompletableFuture<RealmsAvailability.Info> future) {
		RealmsAvailability.Info info = (RealmsAvailability.Info)future.getNow(null);
		return info != null && info.exception() != null;
	}

	private static CompletableFuture<RealmsAvailability.Info> checkInternal() {
		Session session = MinecraftClient.getInstance().getSession();
		return session.getAccountType() != Session.AccountType.MSA
			? CompletableFuture.completedFuture(new RealmsAvailability.Info(RealmsAvailability.Type.AUTHENTICATION_ERROR))
			: CompletableFuture.supplyAsync(
				() -> {
					RealmsClient realmsClient = RealmsClient.create();

					try {
						if (realmsClient.clientCompatible() != RealmsClient.CompatibleVersionResponse.COMPATIBLE) {
							return new RealmsAvailability.Info(RealmsAvailability.Type.INCOMPATIBLE_CLIENT);
						} else {
							return !realmsClient.mcoEnabled()
								? new RealmsAvailability.Info(RealmsAvailability.Type.NEEDS_PARENTAL_CONSENT)
								: new RealmsAvailability.Info(RealmsAvailability.Type.SUCCESS);
						}
					} catch (RealmsServiceException var2) {
						LOGGER.error("Couldn't connect to realms", (Throwable)var2);
						return var2.error.getErrorCode() == 401 ? new RealmsAvailability.Info(RealmsAvailability.Type.AUTHENTICATION_ERROR) : new RealmsAvailability.Info(var2);
					}
				},
				Util.getIoWorkerExecutor()
			);
	}

	@Environment(EnvType.CLIENT)
	public static record Info(RealmsAvailability.Type type, @Nullable RealmsServiceException exception) {
		public Info(RealmsAvailability.Type type) {
			this(type, null);
		}

		public Info(RealmsServiceException exception) {
			this(RealmsAvailability.Type.UNEXPECTED_ERROR, exception);
		}

		@Nullable
		public Screen createScreen(Screen parent) {
			return (Screen)(switch (this.type) {
				case SUCCESS -> null;
				case INCOMPATIBLE_CLIENT -> new RealmsClientIncompatibleScreen(parent);
				case NEEDS_PARENTAL_CONSENT -> new RealmsParentalConsentScreen(parent);
				case AUTHENTICATION_ERROR -> new RealmsGenericErrorScreen(
				Text.translatable("mco.error.invalid.session.title"), Text.translatable("mco.error.invalid.session.message"), parent
			);
				case UNEXPECTED_ERROR -> new RealmsGenericErrorScreen((RealmsServiceException)Objects.requireNonNull(this.exception), parent);
			});
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		SUCCESS,
		INCOMPATIBLE_CLIENT,
		NEEDS_PARENTAL_CONSENT,
		AUTHENTICATION_ERROR,
		UNEXPECTED_ERROR;
	}
}
