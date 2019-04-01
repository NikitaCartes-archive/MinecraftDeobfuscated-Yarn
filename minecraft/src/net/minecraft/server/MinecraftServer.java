package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1257;
import net.minecraft.class_1267;
import net.minecraft.class_1276;
import net.minecraft.class_1279;
import net.minecraft.class_128;
import net.minecraft.class_1282;
import net.minecraft.class_140;
import net.minecraft.class_148;
import net.minecraft.class_155;
import net.minecraft.class_156;
import net.minecraft.class_16;
import net.minecraft.class_1657;
import net.minecraft.class_1863;
import net.minecraft.class_1923;
import net.minecraft.class_1928;
import net.minecraft.class_1932;
import net.minecraft.class_1934;
import net.minecraft.class_1937;
import net.minecraft.class_1939;
import net.minecraft.class_1940;
import net.minecraft.class_1942;
import net.minecraft.class_2165;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2585;
import net.minecraft.class_2588;
import net.minecraft.class_26;
import net.minecraft.class_2632;
import net.minecraft.class_273;
import net.minecraft.class_2761;
import net.minecraft.class_2874;
import net.minecraft.class_29;
import net.minecraft.class_2926;
import net.minecraft.class_2966;
import net.minecraft.class_2981;
import net.minecraft.class_2989;
import net.minecraft.class_2991;
import net.minecraft.class_2995;
import net.minecraft.class_3004;
import net.minecraft.class_31;
import net.minecraft.class_3176;
import net.minecraft.class_32;
import net.minecraft.class_3202;
import net.minecraft.class_3215;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3230;
import net.minecraft.class_3242;
import net.minecraft.class_3262;
import net.minecraft.class_3264;
import net.minecraft.class_3279;
import net.minecraft.class_3283;
import net.minecraft.class_3286;
import net.minecraft.class_3288;
import net.minecraft.class_3296;
import net.minecraft.class_3304;
import net.minecraft.class_3312;
import net.minecraft.class_3324;
import net.minecraft.class_3327;
import net.minecraft.class_3337;
import net.minecraft.class_3505;
import net.minecraft.class_3517;
import net.minecraft.class_3532;
import net.minecraft.class_3536;
import net.minecraft.class_3551;
import net.minecraft.class_3689;
import net.minecraft.class_3738;
import net.minecraft.class_3807;
import net.minecraft.class_3902;
import net.minecraft.class_3949;
import net.minecraft.class_3950;
import net.minecraft.class_3951;
import net.minecraft.class_4093;
import net.minecraft.class_4158;
import net.minecraft.class_60;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer extends class_4093<class_3738> implements class_1279, class_2165, AutoCloseable, Runnable {
	private static final Logger field_4546 = LogManager.getLogger();
	public static final File field_4588 = new File("usercache.json");
	private final class_32 field_4606;
	private final class_1276 field_4582 = new class_1276("server", this, class_156.method_658());
	private final File field_4605;
	private final List<Runnable> field_4568 = Lists.<Runnable>newArrayList();
	private final class_3689 field_16258 = new class_3689(this::method_3780);
	private final class_3242 field_4563;
	protected final class_3950 field_17439;
	private final class_2926 field_4593 = new class_2926();
	private final Random field_4602 = new Random();
	private final DataFixer field_4587;
	private String field_4585;
	private int field_4555 = -1;
	private final Map<class_2874, class_3218> field_4589 = Maps.<class_2874, class_3218>newIdentityHashMap();
	private class_3324 field_4550;
	private volatile boolean field_4544 = true;
	private boolean field_4561;
	private int field_4572;
	protected final Proxy field_4599;
	private boolean field_4543;
	private boolean field_4560;
	private boolean field_4575;
	private boolean field_4590;
	private boolean field_4604;
	private boolean field_4554;
	@Nullable
	private String field_4564;
	private int field_4579;
	private int field_4596;
	public final long[] field_4573 = new long[100];
	protected final Map<class_2874, long[]> field_4600 = Maps.<class_2874, long[]>newIdentityHashMap();
	@Nullable
	private KeyPair field_4552;
	@Nullable
	private String field_4578;
	private final String field_4565;
	@Nullable
	@Environment(EnvType.CLIENT)
	private String field_4574;
	private boolean field_4549 = true;
	private boolean field_4569;
	private String field_4607 = "";
	private String field_4584 = "";
	private volatile boolean field_4547;
	private long field_4557;
	@Nullable
	private class_2561 field_4601;
	private boolean field_4597;
	private boolean field_4545;
	@Nullable
	private final YggdrasilAuthenticationService field_4594;
	private final MinecraftSessionService field_4603;
	private final GameProfileRepository field_4608;
	private final class_3312 field_4556;
	private long field_4551;
	protected final Thread field_16257 = class_156.method_654(
		new Thread(this, "Server thread"), thread -> thread.setUncaughtExceptionHandler((threadx, throwable) -> field_4546.error(throwable))
	);
	private long field_4571 = class_156.method_658();
	@Environment(EnvType.CLIENT)
	private boolean field_4577;
	private final class_3296 field_4576 = new class_3304(class_3264.field_14190, this.field_16257);
	private final class_3283<class_3288> field_4595 = new class_3283<>(class_3288::new);
	@Nullable
	private class_3279 field_4553;
	private final class_2170 field_4562;
	private final class_1863 field_4566 = new class_1863();
	private final class_3505 field_4583 = new class_3505();
	private final class_2995 field_4558 = new class_2995(this);
	private final class_3004 field_4548 = new class_3004(this);
	private final class_60 field_4580 = new class_60();
	private final class_2989 field_4567 = new class_2989();
	private final class_2991 field_4591 = new class_2991(this);
	private final class_3517 field_16205 = new class_3517();
	private boolean field_4570;
	private boolean field_4586;
	private float field_4592;
	private final Executor field_17200;
	@Nullable
	private String field_17601;
	private boolean field_19259;

	public MinecraftServer(
		File file,
		Proxy proxy,
		DataFixer dataFixer,
		class_2170 arg,
		YggdrasilAuthenticationService yggdrasilAuthenticationService,
		MinecraftSessionService minecraftSessionService,
		GameProfileRepository gameProfileRepository,
		class_3312 arg2,
		class_3950 arg3,
		String string
	) {
		super("Server");
		this.field_4599 = proxy;
		this.field_4562 = arg;
		this.field_4594 = yggdrasilAuthenticationService;
		this.field_4603 = minecraftSessionService;
		this.field_4608 = gameProfileRepository;
		this.field_4556 = arg2;
		this.field_4605 = file;
		this.field_4563 = new class_3242(this);
		this.field_17439 = arg3;
		this.field_4606 = new class_32(file.toPath(), file.toPath().resolve("../backups"), dataFixer);
		this.field_4587 = dataFixer;
		this.field_4576.method_14477(this.field_4583);
		this.field_4576.method_14477(this.field_4566);
		this.field_4576.method_14477(this.field_4580);
		this.field_4576.method_14477(this.field_4591);
		this.field_4576.method_14477(this.field_4567);
		this.field_4576.method_14477(class_4158::method_19515);
		this.field_17200 = class_156.method_18349();
		this.field_4565 = string;
	}

	private void method_17976(class_26 arg) {
		class_273 lv = arg.method_17924(class_273::new, "scoreboard");
		lv.method_1218(this.method_3845());
		this.method_3845().method_12935(new class_16(lv));
	}

	protected abstract boolean method_3823() throws IOException;

	protected void method_3755(String string) {
		if (this.method_3781().method_244(string)) {
			field_4546.info("Converting map!");
			this.method_3768(new class_2588("menu.convertingLevel"));
			this.method_3781().method_17927(string, new class_3536() {
				private long field_4609 = class_156.method_658();

				@Override
				public void method_15412(class_2561 arg) {
				}

				@Environment(EnvType.CLIENT)
				@Override
				public void method_15413(class_2561 arg) {
				}

				@Override
				public void method_15410(int i) {
					if (class_156.method_658() - this.field_4609 >= 1000L) {
						this.field_4609 = class_156.method_658();
						MinecraftServer.field_4546.info("Converting... {}%", i);
					}
				}

				@Environment(EnvType.CLIENT)
				@Override
				public void method_15411() {
				}

				@Override
				public void method_15414(class_2561 arg) {
				}
			});
		}

		if (this.field_4586) {
			field_4546.info("Forcing world upgrade!");
			class_31 lv = this.method_3781().method_231(this.method_3865());
			if (lv != null) {
				class_1257 lv2 = new class_1257(this.method_3865(), this.method_3781(), lv);
				class_2561 lv3 = null;

				while (!lv2.method_5403()) {
					class_2561 lv4 = lv2.method_5394();
					if (lv3 != lv4) {
						lv3 = lv4;
						field_4546.info(lv2.method_5394().getString());
					}

					int i = lv2.method_5397();
					if (i > 0) {
						int j = lv2.method_5400() + lv2.method_5399();
						field_4546.info("{}% completed ({} / {} chunks)...", class_3532.method_15375((float)j / (float)i * 100.0F), j, i);
					}

					if (this.method_3750()) {
						lv2.method_5402();
					} else {
						try {
							Thread.sleep(1000L);
						} catch (InterruptedException var8) {
						}
					}
				}
			}
		}
	}

	protected synchronized void method_3768(class_2561 arg) {
		this.field_4601 = arg;
	}

	protected void method_3735(String string, String string2, long l, class_1942 arg, JsonElement jsonElement) {
		this.method_3755(string);
		this.method_3768(new class_2588("menu.loadingLevel"));
		class_29 lv = this.method_3781().method_242(string, this);
		this.method_3861(this.method_3865(), lv);
		class_31 lv2 = lv.method_133();
		class_1940 lv3;
		if (lv2 == null) {
			lv3 = new class_1940(l, this.method_3790(), this.method_3792(), this.method_3754(), arg);
			lv3.method_8579(jsonElement);
			if (this.field_4569) {
				lv3.method_8575();
			}

			lv2 = new class_31(lv3, string2);
		} else {
			lv2.method_182(string2);
			lv3 = new class_1940(lv2);
		}

		this.method_3800(lv.method_132(), lv2);
		class_3949 lv4 = this.field_17439.create(11);
		this.method_3786(lv, lv2, lv3, lv4);
		this.method_3776(this.method_3722(), true);
		this.method_3774(lv4);
	}

	protected void method_3786(class_29 arg, class_31 arg2, class_1940 arg3, class_3949 arg4) {
		class_3218 lv = new class_3218(this, this.field_17200, arg, arg2, class_2874.field_13072, this.field_16258, arg4);
		this.field_4589.put(class_2874.field_13072, lv);
		this.method_17976(lv.method_17983());
		lv.method_8621().method_17905(arg2);
		class_3218 lv2 = this.method_3847(class_2874.field_13072);
		if (!arg2.method_222()) {
			try {
				lv2.method_8414(arg3);
				if (arg2.method_153() == class_1942.field_9266) {
					this.method_17977(arg2);
				}

				arg2.method_223(true);
			} catch (Throwable var11) {
				class_128 lv3 = class_128.method_560(var11, "Exception initializing level");

				try {
					lv2.method_8538(lv3);
				} catch (Throwable var10) {
				}

				throw new class_148(lv3);
			}

			arg2.method_223(true);
		}

		this.method_3760().method_14591(lv2);
		if (arg2.method_228() != null) {
			this.method_3837().method_12972(arg2.method_228());
		}

		for (class_2874 lv4 : class_2874.method_12482()) {
			if (lv4 != class_2874.field_13072) {
				this.field_4589.put(lv4, new class_3202(lv2, this, this.field_17200, arg, lv4, this.field_16258, arg4));
			}
		}
	}

	private void method_17977(class_31 arg) {
		arg.method_196(false);
		arg.method_211(true);
		arg.method_157(false);
		arg.method_147(false);
		arg.method_167(1000000000);
		arg.method_165(6000L);
		arg.method_193(class_1934.field_9219);
		arg.method_198(false);
		arg.method_208(class_1267.field_5801);
		arg.method_186(true);
		arg.method_146().method_8359("doDaylightCycle", "false", this);
	}

	protected void method_3800(File file, class_31 arg) {
		this.field_4595.method_14443(new class_3286());
		this.field_4553 = new class_3279(new File(file, "datapacks"));
		this.field_4595.method_14443(this.field_4553);
		this.field_4595.method_14445();
		List<class_3288> list = Lists.<class_3288>newArrayList();

		for (String string : arg.method_179()) {
			class_3288 lv = this.field_4595.method_14449(string);
			if (lv != null) {
				list.add(lv);
			} else {
				field_4546.warn("Missing data pack {}", string);
			}
		}

		this.field_4595.method_14447(list);
		this.method_3752(arg);
	}

	protected void method_3774(class_3949 arg) {
		this.method_3768(new class_2588("menu.generatingTerrain"));
		class_3218 lv = this.method_3847(class_2874.field_13072);
		field_4546.info("Preparing start region for dimension " + class_2874.method_12485(lv.field_9247.method_12460()));
		class_2338 lv2 = lv.method_8395();
		arg.method_17669(new class_1923(lv2));
		class_3215 lv3 = lv.method_14178();
		lv3.method_17293().method_17304(500);
		this.field_4571 = class_156.method_658();
		lv3.method_17297(class_3230.field_14030, new class_1923(lv2), 11, class_3902.field_17274);

		while (lv3.method_17301() != 441) {
			this.field_4571 += 100L;
			this.method_16208();
		}

		this.field_4571 += 100L;
		this.method_16208();

		for (class_2874 lv4 : class_2874.method_12482()) {
			class_1932 lv5 = this.method_3847(lv4).method_17983().method_120(class_1932::new, "chunks");
			if (lv5 != null) {
				class_3218 lv6 = this.method_3847(lv4);
				LongIterator longIterator = lv5.method_8375().iterator();

				while (longIterator.hasNext()) {
					long l = longIterator.nextLong();
					class_1923 lv7 = new class_1923(l);
					lv6.method_14178().method_12124(lv7, true);
				}
			}
		}

		this.field_4571 += 100L;
		this.method_16208();
		arg.method_17671();
		lv3.method_17293().method_17304(5);
	}

	protected void method_3861(String string, class_29 arg) {
		File file = new File(arg.method_132(), "resources.zip");
		if (file.isFile()) {
			try {
				this.method_3843("level://" + URLEncoder.encode(string, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
			} catch (UnsupportedEncodingException var5) {
				field_4546.warn("Something went wrong url encoding {}", string);
			}
		}
	}

	public abstract boolean method_3792();

	public abstract class_1934 method_3790();

	public abstract class_1267 method_3722();

	public abstract boolean method_3754();

	public abstract int method_3798();

	public abstract boolean method_3732();

	public boolean method_3723(boolean bl, boolean bl2, boolean bl3) {
		boolean bl4 = false;

		for (class_3218 lv : this.method_3738()) {
			if (!bl) {
				field_4546.info("Saving chunks for level '{}'/{}", lv.method_8401().method_150(), class_2874.method_12485(lv.field_9247.method_12460()));
			}

			try {
				lv.method_14176(null, bl2, lv.field_13957 && !bl3);
			} catch (class_1939 var8) {
				field_4546.warn(var8.getMessage());
			}

			bl4 = true;
		}

		class_3218 lv3 = this.method_3847(class_2874.field_13072);
		class_31 lv4 = lv3.method_8401();
		lv3.method_8621().method_17904(lv4);
		lv4.method_221(this.method_3837().method_12974());
		lv3.method_17982().method_131(lv4, this.method_3760().method_14567());
		return bl4;
	}

	@Override
	public void close() {
		this.method_3782();
	}

	protected void method_3782() {
		field_4546.info("Stopping server");
		if (this.method_3787() != null) {
			this.method_3787().method_14356();
		}

		if (this.field_4550 != null) {
			field_4546.info("Saving players");
			this.field_4550.method_14617();
			this.field_4550.method_14597();
		}

		field_4546.info("Saving worlds");

		for (class_3218 lv : this.method_3738()) {
			if (lv != null) {
				lv.field_13957 = false;
			}
		}

		this.method_3723(false, true, false);

		for (class_3218 lvx : this.method_3738()) {
			if (lvx != null) {
				try {
					lvx.close();
				} catch (IOException var4) {
					field_4546.error("Exception closing the level", (Throwable)var4);
				}
			}
		}

		if (this.field_4582.method_5483()) {
			this.field_4582.method_5487();
		}
	}

	public String method_3819() {
		return this.field_4585;
	}

	public void method_3842(String string) {
		this.field_4585 = string;
	}

	public boolean method_3806() {
		return this.field_4544;
	}

	public void method_3747(boolean bl) {
		this.field_4544 = false;
		if (bl) {
			try {
				this.field_16257.join();
			} catch (InterruptedException var3) {
				field_4546.error("Error while shutting down", (Throwable)var3);
			}
		}
	}

	public void run() {
		try {
			if (this.method_3823()) {
				this.field_4571 = class_156.method_658();
				this.field_4593.method_12684(new class_2585(this.field_4564));
				this.field_4593.method_12679(new class_2926.class_2930(class_155.method_16673().getName(), class_155.method_16673().getProtocolVersion()));
				this.method_3791(this.field_4593);

				while (this.field_4544) {
					long l = class_156.method_658() - this.field_4571;
					if (l > 2000L && this.field_4571 - this.field_4557 >= 15000L) {
						long m = l / 50L;
						field_4546.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", l, m);
						this.field_4571 += m * 50L;
						this.field_4557 = this.field_4571;
					}

					this.field_4571 += 50L;
					if (this.field_4597) {
						this.field_4597 = false;
						this.field_16258.method_16055().method_16060();
					}

					this.field_16258.method_16065();
					this.field_16258.method_15396("tick");
					this.method_3748(this::method_3866);
					this.field_16258.method_15405("nextTickWait");
					this.method_16208();
					this.field_16258.method_15407();
					this.field_16258.method_16066();
					this.field_4547 = true;
				}
			} else {
				this.method_3744(null);
			}
		} catch (Throwable var44) {
			field_4546.error("Encountered an unexpected exception", var44);
			class_128 lv;
			if (var44 instanceof class_148) {
				lv = this.method_3859(((class_148)var44).method_631());
			} else {
				lv = this.method_3859(new class_128("Exception in server tick loop", var44));
			}

			File file = new File(
				new File(this.method_3831(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt"
			);
			if (lv.method_569(file)) {
				field_4546.error("This crash report has been saved to: {}", file.getAbsolutePath());
			} else {
				field_4546.error("We were unable to save this crash report to disk.");
			}

			this.method_3744(lv);
		} finally {
			try {
				this.field_4561 = true;
				this.method_3782();
			} catch (Throwable var42) {
				field_4546.error("Exception stopping the server", var42);
			} finally {
				this.method_3821();
			}
		}
	}

	private boolean method_3866() {
		return this.method_18860() || class_156.method_658() < this.field_4571;
	}

	protected void method_16208() {
		this.method_5383();
		this.method_18857(() -> !this.method_3866());
	}

	protected class_3738 method_16209(Runnable runnable) {
		return new class_3738(this.field_4572, runnable);
	}

	protected boolean method_19464(class_3738 arg) {
		return arg.method_16338() + 3 < this.field_4572 || this.method_3866();
	}

	@Override
	public boolean method_16075() {
		if (super.method_16075()) {
			return true;
		} else {
			if (this.method_3866()) {
				for (class_3218 lv : this.method_3738()) {
					if (lv.method_14178().method_19492()) {
						return true;
					}
				}
			}

			return false;
		}
	}

	public void method_3791(class_2926 arg) {
		File file = this.method_3758("server-icon.png");
		if (!file.exists()) {
			file = this.method_3781().method_239(this.method_3865(), "icon.png");
		}

		if (file.isFile()) {
			ByteBuf byteBuf = Unpooled.buffer();

			try {
				BufferedImage bufferedImage = ImageIO.read(file);
				Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide");
				Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high");
				ImageIO.write(bufferedImage, "PNG", new ByteBufOutputStream(byteBuf));
				ByteBuffer byteBuffer = Base64.getEncoder().encode(byteBuf.nioBuffer());
				arg.method_12677("data:image/png;base64," + StandardCharsets.UTF_8.decode(byteBuffer));
			} catch (Exception var9) {
				field_4546.error("Couldn't load server icon", (Throwable)var9);
			} finally {
				byteBuf.release();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean method_3771() {
		this.field_4577 = this.field_4577 || this.method_3725().isFile();
		return this.field_4577;
	}

	@Environment(EnvType.CLIENT)
	public File method_3725() {
		return this.method_3781().method_239(this.method_3865(), "icon.png");
	}

	public File method_3831() {
		return new File(".");
	}

	protected void method_3744(class_128 arg) {
	}

	protected void method_3821() {
	}

	protected void method_3748(BooleanSupplier booleanSupplier) {
		long l = class_156.method_648();
		this.field_4572++;
		this.method_3813(booleanSupplier);
		if (l - this.field_4551 >= 5000000000L) {
			this.field_4551 = l;
			this.field_4593.method_12681(new class_2926.class_2927(this.method_3802(), this.method_3788()));
			GameProfile[] gameProfiles = new GameProfile[Math.min(this.method_3788(), 12)];
			int i = class_3532.method_15395(this.field_4602, 0, this.method_3788() - gameProfiles.length);

			for (int j = 0; j < gameProfiles.length; j++) {
				gameProfiles[j] = ((class_3222)this.field_4550.method_14571().get(i + j)).method_7334();
			}

			Collections.shuffle(Arrays.asList(gameProfiles));
			this.field_4593.method_12682().method_12686(gameProfiles);
		}

		if (this.field_4572 % 900 == 0) {
			this.field_16258.method_15396("save");
			this.field_4550.method_14617();
			this.method_3723(true, false, false);
			this.field_16258.method_15407();
		}

		this.field_16258.method_15396("snooper");
		if (!this.field_4582.method_5483() && this.field_4572 > 100) {
			this.field_4582.method_5482();
		}

		if (this.field_4572 % 6000 == 0) {
			this.field_4582.method_5485();
		}

		this.field_16258.method_15407();
		this.field_16258.method_15396("tallying");
		long m = this.field_4573[this.field_4572 % 100] = class_156.method_648() - l;
		this.field_4592 = this.field_4592 * 0.8F + (float)m / 1000000.0F * 0.19999999F;
		long n = class_156.method_648();
		this.field_16205.method_15247(n - l);
		this.field_16258.method_15407();
	}

	protected void method_3813(BooleanSupplier booleanSupplier) {
		this.field_16258.method_15396("commandFunctions");
		this.method_3740().method_18699();
		this.field_16258.method_15405("levels");

		for (class_3218 lv : this.method_3738()) {
			long l = class_156.method_648();
			if (lv.field_9247.method_12460() == class_2874.field_13072 || this.method_3839()) {
				this.field_16258.method_15400(() -> lv.method_8401().method_150() + " " + class_2378.field_11155.method_10221(lv.field_9247.method_12460()));
				if (this.field_4572 % 20 == 0) {
					this.field_16258.method_15396("timeSync");
					this.field_4550
						.method_14589(new class_2761(lv.method_8510(), lv.method_8532(), lv.method_8450().method_8355("doDaylightCycle")), lv.field_9247.method_12460());
					this.field_16258.method_15407();
				}

				if (this.field_19259) {
					this.field_4550.method_14571().forEach(arg -> arg.method_5643(class_1282.field_19169, 0.5F));
				}

				this.field_16258.method_15396("tick");

				try {
					lv.method_18765(booleanSupplier);
				} catch (Throwable var8) {
					class_128 lv2 = class_128.method_560(var8, "Exception ticking world");
					lv.method_8538(lv2);
					throw new class_148(lv2);
				}

				this.field_16258.method_15407();
				this.field_16258.method_15407();
			}

			((long[])this.field_4600.computeIfAbsent(lv.field_9247.method_12460(), arg -> new long[100]))[this.field_4572 % 100] = class_156.method_648() - l;
		}

		this.field_16258.method_15405("connection");
		this.method_3787().method_14357();
		this.field_16258.method_15405("players");
		this.field_4550.method_14601();
		this.field_16258.method_15405("server gui refresh");

		for (int i = 0; i < this.field_4568.size(); i++) {
			((Runnable)this.field_4568.get(i)).run();
		}

		this.field_16258.method_15407();
	}

	public boolean method_3839() {
		return true;
	}

	public void method_3742(Runnable runnable) {
		this.field_4568.add(runnable);
	}

	public static void main(String[] strings) {
		OptionParser optionParser = new OptionParser();
		OptionSpec<Void> optionSpec = optionParser.accepts("nogui");
		OptionSpec<Void> optionSpec2 = optionParser.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
		OptionSpec<Void> optionSpec3 = optionParser.accepts("demo");
		OptionSpec<Void> optionSpec4 = optionParser.accepts("bonusChest");
		OptionSpec<Void> optionSpec5 = optionParser.accepts("forceUpgrade");
		OptionSpec<Void> optionSpec6 = optionParser.accepts("help").forHelp();
		OptionSpec<String> optionSpec7 = optionParser.accepts("singleplayer").withRequiredArg();
		OptionSpec<String> optionSpec8 = optionParser.accepts("universe").withRequiredArg().defaultsTo(".");
		OptionSpec<String> optionSpec9 = optionParser.accepts("world").withRequiredArg();
		OptionSpec<Integer> optionSpec10 = optionParser.accepts("port").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(-1);
		OptionSpec<String> optionSpec11 = optionParser.accepts("serverId").withRequiredArg();
		OptionSpec<String> optionSpec12 = optionParser.nonOptions();

		try {
			OptionSet optionSet = optionParser.parse(strings);
			if (optionSet.has(optionSpec6)) {
				optionParser.printHelpOn(System.err);
				return;
			}

			Path path = Paths.get("server.properties");
			class_3807 lv = new class_3807(path);
			lv.method_16719();
			Path path2 = Paths.get("eula.txt");
			class_2981 lv2 = new class_2981(path2);
			if (optionSet.has(optionSpec2)) {
				field_4546.info("Initialized '" + path.toAbsolutePath().toString() + "' and '" + path2.toAbsolutePath().toString() + "'");
				return;
			}

			if (!lv2.method_12866()) {
				field_4546.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
				return;
			}

			class_2966.method_12851();
			class_2966.method_17598();
			String string = optionSet.valueOf(optionSpec8);
			YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
			MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
			GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
			class_3312 lv3 = new class_3312(gameProfileRepository, new File(string, field_4588.getName()));
			String string2 = (String)Optional.ofNullable(optionSet.valueOf(optionSpec9)).orElse(lv.method_16717().field_16820);
			final class_3176 lv4 = new class_3176(
				new File(string),
				lv,
				class_3551.method_15450(),
				yggdrasilAuthenticationService,
				minecraftSessionService,
				gameProfileRepository,
				lv3,
				class_3951::new,
				string2
			);
			lv4.method_3817(optionSet.valueOf(optionSpec7));
			lv4.method_3779(optionSet.valueOf(optionSpec10));
			lv4.method_3730(true);
			lv4.method_3778(optionSet.has(optionSpec4));
			lv4.method_3797(optionSet.has(optionSpec5));
			lv4.method_17819(optionSet.valueOf(optionSpec11));
			boolean bl = !optionSet.has(optionSpec) && !optionSet.valuesOf(optionSpec12).contains("nogui");
			if (bl && !GraphicsEnvironment.isHeadless()) {
				lv4.method_13948();
			}

			lv4.method_3717();
			Thread thread = new Thread("Server Shutdown Thread") {
				public void run() {
					lv4.method_3747(true);
				}
			};
			thread.setUncaughtExceptionHandler(new class_140(field_4546));
			Runtime.getRuntime().addShutdownHook(thread);
		} catch (Exception var28) {
			field_4546.fatal("Failed to start the minecraft server", (Throwable)var28);
		}
	}

	protected void method_17819(String string) {
		this.field_17601 = string;
	}

	protected void method_3797(boolean bl) {
		this.field_4586 = bl;
	}

	public void method_3717() {
		this.field_16257.start();
	}

	@Environment(EnvType.CLIENT)
	public boolean method_16043() {
		return !this.field_16257.isAlive();
	}

	public File method_3758(String string) {
		return new File(this.method_3831(), string);
	}

	public void method_3720(String string) {
		field_4546.info(string);
	}

	public void method_3743(String string) {
		field_4546.warn(string);
	}

	public class_3218 method_3847(class_2874 arg) {
		return (class_3218)this.field_4589.get(arg);
	}

	public Iterable<class_3218> method_3738() {
		return this.field_4589.values();
	}

	public String method_3827() {
		return class_155.method_16673().getName();
	}

	public int method_3788() {
		return this.field_4550.method_14574();
	}

	public int method_3802() {
		return this.field_4550.method_14592();
	}

	public String[] method_3858() {
		return this.field_4550.method_14580();
	}

	public boolean method_3766() {
		return false;
	}

	public void method_3762(String string) {
		field_4546.error(string);
	}

	public void method_3770(String string) {
		if (this.method_3766()) {
			field_4546.info(string);
		}
	}

	public String getServerModName() {
		return "vanilla";
	}

	public class_128 method_3859(class_128 arg) {
		if (this.field_4550 != null) {
			arg.method_567()
				.method_577("Player Count", () -> this.field_4550.method_14574() + " / " + this.field_4550.method_14592() + "; " + this.field_4550.method_14571());
		}

		arg.method_567().method_577("Data Packs", () -> {
			StringBuilder stringBuilder = new StringBuilder();

			for (class_3288 lv : this.field_4595.method_14444()) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(", ");
				}

				stringBuilder.append(lv.method_14463());
				if (!lv.method_14460().method_14437()) {
					stringBuilder.append(" (incompatible)");
				}
			}

			return stringBuilder.toString();
		});
		if (this.field_17601 != null) {
			arg.method_567().method_577("Server Id", () -> this.field_17601);
		}

		return arg;
	}

	public boolean method_3814() {
		return this.field_4605 != null;
	}

	@Override
	public void method_9203(class_2561 arg) {
		field_4546.info(arg.getString());
	}

	public KeyPair method_3716() {
		return this.field_4552;
	}

	public int method_3756() {
		return this.field_4555;
	}

	public void method_3779(int i) {
		this.field_4555 = i;
	}

	public String method_3811() {
		return this.field_4578;
	}

	public void method_3817(String string) {
		this.field_4578 = string;
	}

	public boolean method_3724() {
		return this.field_4578 != null;
	}

	public String method_3865() {
		return this.field_4565;
	}

	@Environment(EnvType.CLIENT)
	public void method_3849(String string) {
		this.field_4574 = string;
	}

	@Environment(EnvType.CLIENT)
	public String method_3726() {
		return this.field_4574;
	}

	public void method_3853(KeyPair keyPair) {
		this.field_4552 = keyPair;
	}

	public void method_3776(class_1267 arg, boolean bl) {
		for (class_3218 lv : this.method_3738()) {
			class_31 lv2 = lv.method_8401();
			if (bl || !lv2.method_197()) {
				if (lv2.method_152()) {
					lv2.method_208(class_1267.field_5807);
					lv.method_8424(true, true);
				} else if (this.method_3724()) {
					lv2.method_208(arg);
					lv.method_8424(lv.method_8407() != class_1267.field_5801, true);
				} else {
					lv2.method_208(arg);
					lv.method_8424(this.method_3783(), this.field_4575);
				}
			}
		}

		this.method_3760().method_14571().forEach(this::method_19465);
	}

	public void method_19467(boolean bl) {
		for (class_3218 lv : this.method_3738()) {
			class_31 lv2 = lv.method_8401();
			lv2.method_186(bl);
		}

		this.method_3760().method_14571().forEach(this::method_19465);
	}

	private void method_19465(class_3222 arg) {
		class_31 lv = arg.method_14220().method_8401();
		arg.field_13987.method_14364(new class_2632(lv.method_207(), lv.method_197()));
	}

	protected boolean method_3783() {
		return true;
	}

	public boolean method_3799() {
		return this.field_4549;
	}

	public void method_3730(boolean bl) {
		this.field_4549 = bl;
	}

	public void method_3778(boolean bl) {
		this.field_4569 = bl;
	}

	public class_32 method_3781() {
		return this.field_4606;
	}

	public String method_3784() {
		return this.field_4607;
	}

	public String method_3805() {
		return this.field_4584;
	}

	public void method_3843(String string, String string2) {
		this.field_4607 = string;
		this.field_4584 = string2;
	}

	@Override
	public void method_5495(class_1276 arg) {
		arg.method_5481("whitelist_enabled", false);
		arg.method_5481("whitelist_count", 0);
		if (this.field_4550 != null) {
			arg.method_5481("players_current", this.method_3788());
			arg.method_5481("players_max", this.method_3802());
			arg.method_5481("players_seen", this.method_3847(class_2874.field_13072).method_17982().method_263().length);
		}

		arg.method_5481("uses_auth", this.field_4543);
		arg.method_5481("gui_state", this.method_3727() ? "enabled" : "disabled");
		arg.method_5481("run_time", (class_156.method_658() - arg.method_5484()) / 60L * 1000L);
		arg.method_5481("avg_tick_ms", (int)(class_3532.method_15373(this.field_4573) * 1.0E-6));
		int i = 0;

		for (class_3218 lv : this.method_3738()) {
			if (lv != null) {
				class_31 lv2 = lv.method_8401();
				arg.method_5481("world[" + i + "][dimension]", lv.field_9247.method_12460());
				arg.method_5481("world[" + i + "][mode]", lv2.method_210());
				arg.method_5481("world[" + i + "][difficulty]", lv.method_8407());
				arg.method_5481("world[" + i + "][hardcore]", lv2.method_152());
				arg.method_5481("world[" + i + "][generator_name]", lv2.method_153().method_8635());
				arg.method_5481("world[" + i + "][generator_version]", lv2.method_153().method_8636());
				arg.method_5481("world[" + i + "][height]", this.field_4579);
				arg.method_5481("world[" + i + "][chunks_loaded]", lv.method_14178().method_14151());
				i++;
			}
		}

		arg.method_5481("worlds", i);
	}

	public abstract boolean method_3816();

	public boolean method_3828() {
		return this.field_4543;
	}

	public void method_3864(boolean bl) {
		this.field_4543 = bl;
	}

	public boolean method_3775() {
		return this.field_4560;
	}

	public void method_3764(boolean bl) {
		this.field_4560 = bl;
	}

	public boolean method_3796() {
		return this.field_4575;
	}

	public void method_3840(boolean bl) {
		this.field_4575 = bl;
	}

	public boolean method_3736() {
		return this.field_4590;
	}

	public abstract boolean method_3759();

	public void method_3769(boolean bl) {
		this.field_4590 = bl;
	}

	public boolean method_3852() {
		return this.field_4604;
	}

	public void method_3815(boolean bl) {
		this.field_4604 = bl;
	}

	public boolean method_3718() {
		return this.field_4554;
	}

	public void method_3745(boolean bl) {
		this.field_4554 = bl;
	}

	public abstract boolean method_3812();

	public String method_3818() {
		return this.field_4564;
	}

	public void method_3834(String string) {
		this.field_4564 = string;
	}

	public int method_3833() {
		return this.field_4579;
	}

	public void method_3850(int i) {
		this.field_4579 = i;
	}

	public boolean method_3750() {
		return this.field_4561;
	}

	public class_3324 method_3760() {
		return this.field_4550;
	}

	public void method_3846(class_3324 arg) {
		this.field_4550 = arg;
	}

	public abstract boolean method_3860();

	public void method_3838(class_1934 arg) {
		for (class_3218 lv : this.method_3738()) {
			lv.method_8401().method_193(arg);
		}
	}

	@Nullable
	public class_3242 method_3787() {
		return this.field_4563;
	}

	@Environment(EnvType.CLIENT)
	public boolean method_3820() {
		return this.field_4547;
	}

	public boolean method_3727() {
		return false;
	}

	public abstract boolean method_3763(class_1934 arg, boolean bl, int i);

	public int method_3780() {
		return this.field_4572;
	}

	public void method_3832() {
		this.field_4597 = true;
	}

	@Environment(EnvType.CLIENT)
	public class_1276 method_3795() {
		return this.field_4582;
	}

	public int method_3841() {
		return 16;
	}

	public boolean method_3785(class_1937 arg, class_2338 arg2, class_1657 arg3) {
		return false;
	}

	public void method_3794(boolean bl) {
		this.field_4545 = bl;
	}

	public boolean method_3761() {
		return this.field_4545;
	}

	public int method_3862() {
		return this.field_4596;
	}

	public void method_3803(int i) {
		this.field_4596 = i;
	}

	public MinecraftSessionService method_3844() {
		return this.field_4603;
	}

	public GameProfileRepository method_3719() {
		return this.field_4608;
	}

	public class_3312 method_3793() {
		return this.field_4556;
	}

	public class_2926 method_3765() {
		return this.field_4593;
	}

	public void method_3856() {
		this.field_4551 = 0L;
	}

	public int method_3749() {
		return 29999984;
	}

	@Override
	public boolean method_5384() {
		return super.method_5384() && !this.method_3750();
	}

	@Override
	public Thread method_3777() {
		return this.field_16257;
	}

	public int method_3773() {
		return 256;
	}

	public long method_3826() {
		return this.field_4571;
	}

	public DataFixer method_3855() {
		return this.field_4587;
	}

	public int method_3829(@Nullable class_3218 arg) {
		return arg != null ? arg.method_8450().method_8356("spawnRadius") : 10;
	}

	public class_2989 method_3851() {
		return this.field_4567;
	}

	public class_2991 method_3740() {
		return this.field_4591;
	}

	public void method_3848() {
		if (!this.method_18854()) {
			this.execute(this::method_3848);
		} else {
			this.method_3760().method_14617();
			this.field_4595.method_14445();
			this.method_3752(this.method_3847(class_2874.field_13072).method_8401());
			this.method_3760().method_14572();
		}
	}

	private void method_3752(class_31 arg) {
		List<class_3288> list = Lists.<class_3288>newArrayList(this.field_4595.method_14444());

		for (class_3288 lv : this.field_4595.method_14441()) {
			if (!arg.method_209().contains(lv.method_14463()) && !list.contains(lv)) {
				field_4546.info("Found new data pack {}, loading it automatically", lv.method_14463());
				lv.method_14466().method_14468(list, lv, argx -> argx, false);
			}
		}

		this.field_4595.method_14447(list);
		List<class_3262> list2 = Lists.<class_3262>newArrayList();
		this.field_4595.method_14444().forEach(argx -> list2.add(argx.method_14458()));
		CompletableFuture<class_3902> completableFuture = this.field_4576
			.method_14478(this.field_17200, this, list2, CompletableFuture.completedFuture(class_3902.field_17274));
		this.method_18857(completableFuture::isDone);
		arg.method_179().clear();
		arg.method_209().clear();
		this.field_4595.method_14444().forEach(arg2 -> arg.method_179().add(arg2.method_14463()));
		this.field_4595.method_14441().forEach(arg2 -> {
			if (!this.field_4595.method_14444().contains(arg2)) {
				arg.method_209().add(arg2.method_14463());
			}
		});
	}

	public void method_3728(class_2168 arg) {
		if (this.method_3729()) {
			class_3324 lv = arg.method_9211().method_3760();
			class_3337 lv2 = lv.method_14590();
			if (lv2.method_14639()) {
				for (class_3222 lv3 : Lists.newArrayList(lv.method_14571())) {
					if (!lv2.method_14653(lv3.method_7334())) {
						lv3.field_13987.method_14367(new class_2588("multiplayer.disconnect.not_whitelisted"));
					}
				}
			}
		}
	}

	public class_3296 method_3809() {
		return this.field_4576;
	}

	public class_3283<class_3288> method_3836() {
		return this.field_4595;
	}

	public class_2170 method_3734() {
		return this.field_4562;
	}

	public class_2168 method_3739() {
		return new class_2168(
			this,
			this.method_3847(class_2874.field_13072) == null ? class_243.field_1353 : new class_243(this.method_3847(class_2874.field_13072).method_8395()),
			class_241.field_1340,
			this.method_3847(class_2874.field_13072),
			4,
			"Server",
			new class_2585("Server"),
			this,
			null
		);
	}

	@Override
	public boolean method_9200() {
		return true;
	}

	@Override
	public boolean method_9202() {
		return true;
	}

	public class_1863 method_3772() {
		return this.field_4566;
	}

	public class_3505 method_3801() {
		return this.field_4583;
	}

	public class_2995 method_3845() {
		return this.field_4558;
	}

	public class_60 method_3857() {
		return this.field_4580;
	}

	public class_1928 method_3767() {
		return this.method_3847(class_2874.field_13072).method_8450();
	}

	public class_3004 method_3837() {
		return this.field_4548;
	}

	public boolean method_3729() {
		return this.field_4570;
	}

	public void method_3731(boolean bl) {
		this.field_4570 = bl;
	}

	@Environment(EnvType.CLIENT)
	public float method_3830() {
		return this.field_4592;
	}

	public int method_3835(GameProfile gameProfile) {
		if (this.method_3760().method_14569(gameProfile)) {
			class_3327 lv = this.method_3760().method_14603().method_14640(gameProfile);
			if (lv != null) {
				return lv.method_14623();
			} else if (this.method_19466(gameProfile)) {
				return 4;
			} else if (this.method_3724()) {
				return this.method_3760().method_14579() ? 4 : 0;
			} else {
				return this.method_3798();
			}
		} else {
			return 0;
		}
	}

	@Environment(EnvType.CLIENT)
	public class_3517 method_15876() {
		return this.field_16205;
	}

	public class_3689 method_16044() {
		return this.field_16258;
	}

	public Executor method_17191() {
		return this.field_17200;
	}

	public abstract boolean method_19466(GameProfile gameProfile);

	@Environment(EnvType.CLIENT)
	public void method_20288(boolean bl) {
		this.field_19259 = bl;
	}
}
