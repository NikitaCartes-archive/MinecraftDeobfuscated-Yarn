package net.minecraft;

import com.google.common.collect.Queues;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class class_310 extends class_4093<Runnable> implements class_1279, class_3678, AutoCloseable {
	private static final Logger field_1762 = LogManager.getLogger();
	public static final boolean field_1703 = class_156.method_668() == class_156.class_158.field_1137;
	public static final class_2960 field_1740 = new class_2960("default");
	public static final class_2960 field_1749 = new class_2960("alt");
	public static CompletableFuture<class_3902> field_18009 = CompletableFuture.completedFuture(class_3902.field_17274);
	public static byte[] field_1718 = new byte[10485760];
	private static int field_1754 = -1;
	private final File field_1757;
	private final PropertyMap field_1694;
	private final class_543 field_1725;
	private class_642 field_1699;
	private class_1060 field_1764;
	private static class_310 field_1700;
	private final DataFixer field_1768;
	public class_636 field_1761;
	private class_3682 field_1686;
	public class_1041 field_1704;
	private boolean field_1691;
	private class_128 field_1747;
	private boolean field_1744;
	private final class_317 field_1728 = new class_317(20.0F, 0L);
	private final class_1276 field_1775 = new class_1276("client", this, class_156.method_658());
	public class_638 field_1687;
	public class_761 field_1769;
	private class_898 field_1731;
	private class_918 field_1742;
	private class_759 field_1737;
	public class_746 field_1724;
	@Nullable
	public class_1297 field_1719;
	@Nullable
	public class_1297 field_1692;
	public class_702 field_1713;
	private final class_1124 field_1733 = new class_1124();
	private final class_320 field_1726;
	private boolean field_1734;
	private float field_1741;
	public class_327 field_1772;
	@Nullable
	public class_437 field_1755;
	@Nullable
	public class_4071 field_18175;
	public class_757 field_1773;
	public class_863 field_1709;
	protected int field_1771;
	@Nullable
	private class_1132 field_1766;
	private final AtomicReference<class_3953> field_17405 = new AtomicReference();
	public class_329 field_1705;
	public boolean field_1743;
	public class_239 field_1765;
	public class_315 field_1690;
	private class_302 field_1732;
	public class_312 field_1729;
	public class_309 field_1774;
	public final File field_1697;
	private final File field_1753;
	private final String field_1711;
	private final String field_1720;
	private final Proxy field_1739;
	private class_32 field_1748;
	private static int field_1738;
	private int field_1752;
	private String field_1706;
	private int field_1710;
	public final class_3517 field_1688 = new class_3517();
	private long field_1750 = class_156.method_648();
	private final boolean field_1693;
	private final boolean field_1721;
	@Nullable
	private class_2535 field_1746;
	private boolean field_1759;
	private final class_3689 field_16240 = new class_3689(() -> this.field_1728.field_1972);
	private class_3296 field_1745;
	private final class_1066 field_1722;
	private final class_3283<class_1075> field_1715;
	private class_1076 field_1717;
	private class_324 field_1751;
	private class_325 field_1760;
	private class_276 field_1689;
	private class_1059 field_1767;
	private class_1144 field_1727;
	private class_1142 field_1714;
	private class_378 field_1708;
	private class_4008 field_17763;
	private final MinecraftSessionService field_1723;
	private class_1071 field_1707;
	private final Thread field_1696 = Thread.currentThread();
	private class_1092 field_1763;
	private class_776 field_1756;
	private class_4044 field_18008;
	private class_4074 field_18173;
	private final class_374 field_1702;
	private final class_3799 field_16762 = new class_3799(this);
	private volatile boolean field_1698 = true;
	public String field_1770 = "";
	public boolean field_1730 = true;
	private long field_1712;
	private int field_1735;
	private final class_1156 field_1758;
	private boolean field_1695;
	private final Queue<Runnable> field_17404 = Queues.<Runnable>newConcurrentLinkedQueue();
	private CompletableFuture<Void> field_18174;
	private static final class_2960 field_19184 = new class_2960("textures/gui/boss_mode.png");
	private final Random field_19185 = new Random();
	private boolean field_19186;
	private boolean field_19187;
	private int field_19188 = 1800;
	private String field_1701 = "root";

	public class_310(class_542 arg) {
		super("Client");
		this.field_1725 = arg.field_3279;
		field_1700 = this;
		this.field_1697 = arg.field_3277.field_3287;
		this.field_1753 = arg.field_3277.field_3289;
		this.field_1757 = arg.field_3277.field_3290;
		this.field_1711 = arg.field_3280.field_3293;
		this.field_1720 = arg.field_3280.field_3291;
		this.field_1694 = arg.field_3278.field_3297;
		this.field_1722 = new class_1066(new File(this.field_1697, "server-resource-packs"), arg.field_3277.method_2788());
		this.field_1715 = new class_3283<>((string, bl, supplier, argx, arg2, arg3) -> {
			Supplier<class_3262> supplier2;
			if (arg2.method_14424() < class_155.method_16673().getPackVersion()) {
				supplier2 = () -> new class_1073((class_3262)supplier.get(), class_1073.field_5317);
			} else {
				supplier2 = supplier;
			}

			return new class_1075(string, bl, supplier2, argx, arg2, arg3);
		});
		this.field_1715.method_14443(this.field_1722);
		this.field_1715.method_14443(new class_3279(this.field_1757));
		this.field_1739 = arg.field_3278.field_3296 == null ? Proxy.NO_PROXY : arg.field_3278.field_3296;
		this.field_1723 = new YggdrasilAuthenticationService(this.field_1739, UUID.randomUUID().toString()).createMinecraftSessionService();
		this.field_1726 = arg.field_3278.field_3299;
		field_1762.info("Setting user: {}", this.field_1726.method_1676());
		field_1762.debug("(Session ID is {})", this.field_1726.method_1675());
		this.field_1721 = arg.field_3280.field_3292;
		this.field_1693 = method_1476();
		this.field_1766 = null;
		if (arg.field_3281.field_3294 != null) {
			this.field_1706 = arg.field_3281.field_3294;
			this.field_1710 = arg.field_3281.field_3295;
		}

		class_2966.method_12851();
		class_2966.method_17598();
		class_2572.field_11766 = class_304::method_1419;
		this.field_1768 = class_3551.method_15450();
		this.field_1702 = new class_374(this);
		this.field_1758 = new class_1156(this);
	}

	public void method_1514() {
		this.field_1698 = true;

		try {
			this.method_1503();
		} catch (Throwable var10) {
			class_128 lv = class_128.method_560(var10, "Initializing game");
			lv.method_562("Initialization");
			this.method_1565(this.method_1587(lv));
			return;
		}

		try {
			try {
				while (this.field_1698) {
					if (this.field_1691 && this.field_1747 != null) {
						this.method_1565(this.field_1747);
						return;
					} else {
						try {
							this.method_1523(true);
						} catch (OutOfMemoryError var9) {
							this.method_1519();
							this.method_1507(new class_428());
							System.gc();
						}
					}
				}

				return;
			} catch (class_148 var11) {
				this.method_1587(var11.method_631());
				this.method_1519();
				field_1762.fatal("Reported exception thrown!", (Throwable)var11);
				this.method_1565(var11.method_631());
			} catch (Throwable var12) {
				class_128 lv = this.method_1587(new class_128("Unexpected error", var12));
				field_1762.fatal("Unreported exception thrown!", var12);
				this.method_1519();
				this.method_1565(lv);
			}
		} finally {
			this.method_1592();
		}
	}

	private void method_1503() {
		this.field_1690 = new class_315(this, this.field_1697);
		this.field_1732 = new class_302(this.field_1697, this.field_1768);
		this.method_1527();
		field_1762.info("LWJGL Version: {}", GLX.getLWJGLVersion());
		class_543 lv = this.field_1725;
		if (this.field_1690.field_1885 > 0 && this.field_1690.field_1872 > 0) {
			lv = new class_543(this.field_1690.field_1872, this.field_1690.field_1885, lv.field_3282, lv.field_3286, lv.field_3283);
		}

		LongSupplier longSupplier = GLX.initGlfw();
		if (longSupplier != null) {
			class_156.field_1128 = longSupplier;
		}

		this.field_1686 = new class_3682(this);
		this.field_1704 = this.field_1686.method_16038(lv, this.field_1690.field_1828, "Minecraft " + class_155.method_16673().getName());
		this.method_15995(true);

		try {
			InputStream inputStream = this.method_1516().method_4633().method_14405(class_3264.field_14188, new class_2960("icons/icon_16x16.png"));
			InputStream inputStream2 = this.method_1516().method_4633().method_14405(class_3264.field_14188, new class_2960("icons/icon_32x32.png"));
			this.field_1704.method_4491(inputStream, inputStream2);
		} catch (IOException var6) {
			field_1762.error("Couldn't set icon", (Throwable)var6);
		}

		this.field_1704.method_15999(this.field_1690.field_1909);
		this.field_1729 = new class_312(this);
		this.field_1729.method_1607(this.field_1704.method_4490());
		this.field_1774 = new class_309(this);
		this.field_1774.method_1472(this.field_1704.method_4490());
		GLX.init();
		class_1008.method_4227(this.field_1690.field_1901, false);
		this.field_1689 = new class_276(this.field_1704.method_4489(), this.field_1704.method_4506(), true, field_1703);
		this.field_1689.method_1236(0.0F, 0.0F, 0.0F, 0.0F);
		this.field_1745 = new class_3304(class_3264.field_14188, this.field_1696);
		this.field_1690.method_1627(this.field_1715);
		this.field_1715.method_14445();
		List<class_3262> list = (List<class_3262>)this.field_1715.method_14444().stream().map(class_3288::method_14458).collect(Collectors.toList());

		for (class_3262 lv2 : list) {
			this.field_1745.method_14475(lv2);
		}

		this.field_1717 = new class_1076(this.field_1690.field_1883);
		this.field_1745.method_14477(this.field_1717);
		this.field_1717.method_4670(list);
		this.field_1764 = new class_1060(this.field_1745);
		this.field_1745.method_14477(this.field_1764);
		this.method_15993();
		this.field_1707 = new class_1071(this.field_1764, new File(this.field_1753, "skins"), this.field_1723);
		this.field_1748 = new class_32(this.field_1697.toPath().resolve("saves"), this.field_1697.toPath().resolve("backups"), this.field_1768);
		this.field_1727 = new class_1144(this.field_1745, this.field_1690);
		this.field_1745.method_14477(this.field_1727);
		this.field_17763 = new class_4008(this.field_1726);
		this.field_1745.method_14477(this.field_17763);
		this.field_1714 = new class_1142(this);
		this.field_1708 = new class_378(this.field_1764, this.method_1573());
		this.field_1745.method_14477(this.field_1708.method_18627());
		this.field_1772 = this.field_1708.method_2019(field_1740);
		if (this.field_1690.field_1883 != null) {
			this.field_1772.method_1719(this.field_1717.method_4666());
		}

		this.field_1745.method_14477(new class_1069());
		this.field_1745.method_14477(new class_1070());
		this.field_1704.method_4474("Startup");
		GlStateManager.enableTexture();
		GlStateManager.shadeModel(7425);
		GlStateManager.clearDepth(1.0);
		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.cullFace(GlStateManager.class_1024.field_5070);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		this.field_1704.method_4474("Post startup");
		this.field_1767 = new class_1059("textures");
		this.field_1767.method_4605(this.field_1690.field_1856);
		this.field_1764.method_4620(class_1059.field_5275, this.field_1767);
		this.field_1764.method_4618(class_1059.field_5275);
		this.field_1767.method_4527(false, this.field_1690.field_1856 > 0);
		this.field_1763 = new class_1092(this.field_1767);
		this.field_1745.method_14477(this.field_1763);
		this.field_1751 = class_324.method_1689();
		this.field_1760 = class_325.method_1706(this.field_1751);
		this.field_1742 = new class_918(this.field_1764, this.field_1763, this.field_1760);
		this.field_1731 = new class_898(this.field_1764, this.field_1742, this.field_1745);
		this.field_1737 = new class_759(this);
		this.field_1745.method_14477(this.field_1742);
		this.field_1773 = new class_757(this, this.field_1745);
		this.field_1745.method_14477(this.field_1773);
		this.field_1756 = new class_776(this.field_1763.method_4743(), this.field_1751);
		this.field_1745.method_14477(this.field_1756);
		this.field_1769 = new class_761(this);
		this.field_1745.method_14477(this.field_1769);
		this.method_1546();
		this.field_1745.method_14477(this.field_1733);
		GlStateManager.viewport(0, 0, this.field_1704.method_4489(), this.field_1704.method_4506());
		this.field_1713 = new class_702(this.field_1687, this.field_1764);
		this.field_1745.method_14477(this.field_1713);
		this.field_18008 = new class_4044(this.field_1764);
		this.field_1745.method_14477(this.field_18008);
		this.field_18173 = new class_4074(this.field_1764);
		this.field_1745.method_14477(this.field_18173);
		this.field_1705 = new class_329(this);
		this.field_1709 = new class_863(this);
		GLX.setGlfwErrorCallback(this::method_1506);
		if (this.field_1690.field_1857 && !this.field_1704.method_4498()) {
			this.field_1704.method_4500();
			this.field_1690.field_1857 = this.field_1704.method_4498();
		}

		this.field_1704.method_4497(this.field_1690.field_1884);
		this.field_1704.method_4513();
		if (this.field_1706 != null) {
			this.method_1507(new class_412(new class_442(), this, this.field_1706, this.field_1710));
		} else {
			this.method_1507(new class_4294());
		}

		class_425.method_18819(this);
		class_4289.method_20277(this);
		class_4281.method_20260(this);
		this.field_1764.method_4616(field_19184, new class_425.class_4070(field_19184));
		this.method_18502(
			new class_4287(this, this.field_1745.method_18230(class_156.method_18349(), this, CompletableFuture.completedFuture(class_3902.field_17274)), () -> {
				if (class_155.field_1125) {
					this.method_17044();
				}
			})
		);
	}

	private void method_1546() {
		class_1126<class_1799> lv = new class_1126<>(
			arg -> arg.method_7950(null, class_1836.class_1837.field_8934)
					.stream()
					.map(argx -> class_124.method_539(argx.getString()).trim())
					.filter(string -> !string.isEmpty()),
			arg -> Stream.of(class_2378.field_11142.method_10221(arg.method_7909()))
		);
		class_1121<class_1799> lv2 = new class_1121<>(arg -> class_3489.method_15106().method_15191(arg.method_7909()).stream());
		class_2371<class_1799> lv3 = class_2371.method_10211();

		for (class_1792 lv4 : class_2378.field_11142) {
			lv4.method_7850(class_1761.field_7915, lv3);
		}

		lv3.forEach(arg3 -> {
			lv.method_4798(arg3);
			lv2.method_4798(arg3);
		});
		class_1126<class_516> lv5 = new class_1126<>(
			arg -> arg.method_2650()
					.stream()
					.flatMap(argx -> argx.method_8110().method_7950(null, class_1836.class_1837.field_8934).stream())
					.map(argx -> class_124.method_539(argx.getString()).trim())
					.filter(string -> !string.isEmpty()),
			arg -> arg.method_2650().stream().map(argx -> class_2378.field_11142.method_10221(argx.method_8110().method_7909()))
		);
		this.field_1733.method_4801(class_1124.field_5495, lv);
		this.field_1733.method_4801(class_1124.field_5494, lv2);
		this.field_1733.method_4801(class_1124.field_5496, lv5);
	}

	private void method_1506(int i, long l) {
		this.field_1690.field_1884 = false;
		this.field_1690.method_1640();
	}

	private static boolean method_1476() {
		String[] strings = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

		for (String string : strings) {
			String string2 = System.getProperty(string);
			if (string2 != null && string2.contains("64")) {
				return true;
			}
		}

		return false;
	}

	public class_276 method_1522() {
		return this.field_1689;
	}

	public String method_1515() {
		return this.field_1711;
	}

	public String method_1547() {
		return this.field_1720;
	}

	private void method_1527() {
		Thread thread = new Thread("Timer hack thread") {
			public void run() {
				while (class_310.this.field_1698) {
					try {
						Thread.sleep(2147483647L);
					} catch (InterruptedException var2) {
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.setUncaughtExceptionHandler(new class_140(field_1762));
		thread.start();
	}

	public void method_1494(class_128 arg) {
		this.field_1691 = true;
		this.field_1747 = arg;
	}

	public void method_1565(class_128 arg) {
		File file = new File(method_1551().field_1697, "crash-reports");
		File file2 = new File(file, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
		class_2966.method_12847(arg.method_568());
		if (arg.method_572() != null) {
			class_2966.method_12847("#@!@# Game crashed! Crash report saved to: #@!@# " + arg.method_572());
			System.exit(-1);
		} else if (arg.method_569(file2)) {
			class_2966.method_12847("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
			System.exit(-1);
		} else {
			class_2966.method_12847("#@?@# Game crashed! Crash report could not be saved. #@?@#");
			System.exit(-2);
		}
	}

	public boolean method_1573() {
		return this.field_1690.field_1819;
	}

	public CompletableFuture<Void> method_1521() {
		if (this.field_18174 != null) {
			return this.field_18174;
		} else {
			CompletableFuture<Void> completableFuture = new CompletableFuture();
			if (this.field_18175 instanceof class_425) {
				this.field_18174 = completableFuture;
				return completableFuture;
			} else {
				this.field_1715.method_14445();
				List<class_3262> list = (List<class_3262>)this.field_1715.method_14444().stream().map(class_3288::method_14458).collect(Collectors.toList());
				this.method_18502(new class_425(this, this.field_1745.method_18232(class_156.method_18349(), this, field_18009, list), () -> {
					this.field_1717.method_4670(list);
					if (this.field_1769 != null) {
						this.field_1769.method_3279();
					}

					completableFuture.complete(null);
				}, true));
				return completableFuture;
			}
		}
	}

	private void method_17044() {
		boolean bl = false;
		class_773 lv = this.method_1541().method_3351();
		class_1087 lv2 = lv.method_3333().method_4744();

		for (class_2248 lv3 : class_2378.field_11146) {
			for (class_2680 lv4 : lv3.method_9595().method_11662()) {
				if (lv4.method_11610() == class_2464.field_11458) {
					class_1087 lv5 = lv.method_3335(lv4);
					if (lv5 == lv2) {
						field_1762.debug("Missing model for: {}", lv4);
						bl = true;
					}
				}
			}
		}

		class_1058 lv6 = lv2.method_4711();

		for (class_2248 lv7 : class_2378.field_11146) {
			for (class_2680 lv8 : lv7.method_9595().method_11662()) {
				class_1058 lv9 = lv.method_3339(lv8);
				if (!lv8.method_11588() && lv9 == lv6) {
					field_1762.debug("Missing particle icon for: {}", lv8);
					bl = true;
				}
			}
		}

		class_2371<class_1799> lv10 = class_2371.method_10211();

		for (class_1792 lv11 : class_2378.field_11142) {
			lv10.clear();
			lv11.method_7850(class_1761.field_7915, lv10);

			for (class_1799 lv12 : lv10) {
				String string = new class_2588(lv12.method_7922()).getString();
				if (string.toLowerCase(Locale.ROOT).equals(string)) {
					field_1762.debug("Missing translation for: {} {} {}", lv12, lv12.method_7922(), lv12.method_7909());
					bl = true;
				}
			}
		}

		bl |= class_3929.method_17539();
		if (bl) {
			throw new IllegalStateException("Your game data is foobar, fix the errors above!");
		}
	}

	public class_32 method_1586() {
		return this.field_1748;
	}

	public void method_1507(@Nullable class_437 arg) {
		if (this.field_1755 != null) {
			this.field_1755.removed();
		}

		if (arg == null && this.field_1687 == null) {
			arg = new class_442();
		} else if (arg == null && this.field_1724.method_6032() <= 0.0F) {
			arg = new class_418(null, this.field_1687.method_8401().method_152());
		}

		if (arg instanceof class_442 || arg instanceof class_500) {
			this.field_1690.field_1866 = false;
			this.field_1705.method_1743().method_1808(true);
		}

		this.field_1755 = arg;
		if (arg != null) {
			this.field_1729.method_1610();
			class_304.method_1437();
			arg.init(this, this.field_1704.method_4486(), this.field_1704.method_4502());
			this.field_1743 = false;
			class_333.field_2054.method_19788(arg.getNarrationMessage());
		} else {
			this.field_1727.method_4880();
			this.field_1729.method_1612();
		}
	}

	public void method_18502(@Nullable class_4071 arg) {
		this.field_18175 = arg;
	}

	public void method_1592() {
		try {
			this.method_1490();
			if (this.field_1755 != null) {
				this.field_1755.removed();
			}

			this.close();
		} finally {
			class_156.field_1128 = System::nanoTime;
			if (!this.field_1691) {
				System.exit(0);
			}
		}
	}

	private void method_1490() {
		field_1762.info("Stopping!");

		try {
			if (this.field_1687 != null) {
				this.field_1687.method_8525();
			}

			this.method_18099();
		} catch (Throwable var2) {
		}
	}

	@Override
	public void close() {
		try {
			this.field_1767.method_4601();
			this.field_1772.close();
			this.field_1708.close();
			this.field_1773.close();
			this.field_1769.close();
			this.field_1727.method_4882();
			this.field_1715.close();
			this.field_1713.method_18829();
			this.field_18173.close();
			this.field_18008.close();
			class_156.method_18350();
		} finally {
			this.field_1686.close();
			this.field_1704.close();
		}
	}

	private void method_1523(boolean bl) {
		this.field_1704.method_4474("Pre render");
		long l = class_156.method_648();
		this.field_16240.method_16065();
		if (GLX.shouldClose(this.field_1704)) {
			this.method_20258();
		}

		if (this.field_18174 != null && !(this.field_18175 instanceof class_425)) {
			CompletableFuture<Void> completableFuture = this.field_18174;
			this.field_18174 = null;
			this.method_1521().thenRun(() -> completableFuture.complete(null));
		}

		Runnable runnable;
		while ((runnable = (Runnable)this.field_17404.poll()) != null) {
			runnable.run();
		}

		if (bl) {
			this.field_1728.method_1658(class_156.method_658());
			this.field_16240.method_15396("scheduledExecutables");
			this.method_5383();
			this.field_16240.method_15407();
		}

		long m = class_156.method_648();
		this.field_16240.method_15396("tick");
		if (this.field_1755 == null && this.field_19188-- < 0) {
			this.method_1507(new class_4296());
			this.field_19188 = (30 + this.field_19185.nextInt(120)) * 60;
		}

		if (bl) {
			for (int i = 0; i < Math.min(10, this.field_1728.field_1972); i++) {
				this.method_1574();
			}
		}

		this.field_1729.method_1606();
		this.field_1704.method_4474("Render");
		GLX.pollEvents();
		long n = class_156.method_648() - m;
		this.field_16240.method_15405("sound");
		this.field_1727.method_4876(this.field_1773.method_19418());
		this.field_16240.method_15407();
		this.field_16240.method_15396("render");
		GlStateManager.pushMatrix();
		GlStateManager.clear(16640, field_1703);
		this.field_1689.method_1235(true);
		this.field_16240.method_15396("display");
		GlStateManager.enableTexture();
		this.field_16240.method_15407();
		if (this.field_1690.field_19189.method_1434()) {
			this.method_20256();
		} else if (!this.field_1743) {
			this.field_16240.method_15405("gameRenderer");
			this.field_1773.method_3192(0.0F, l, bl);
			this.field_16240.method_15405("toasts");
			this.field_1702.method_1996();
			this.field_16240.method_15407();
		}

		this.field_16240.method_16066();
		if (this.field_1690.field_1866 && this.field_1690.field_1880 && !this.field_1690.field_1842) {
			this.field_16240.method_16055().method_16060();
			this.method_1492();
		} else {
			this.field_16240.method_16055().method_16058();
		}

		this.field_1689.method_1240();
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		this.field_1689.method_1237(this.field_1704.method_4489(), this.field_1704.method_4506());
		GlStateManager.popMatrix();
		this.field_16240.method_16065();
		this.method_15994(true);
		Thread.yield();
		this.field_1704.method_4474("Post render");
		this.field_1735++;
		boolean bl2 = this.method_1496()
			&& (this.field_1755 != null && this.field_1755.isPauseScreen() || this.field_18175 != null && this.field_18175.method_18640())
			&& !this.field_1766.method_3860();
		if (this.field_1734 != bl2) {
			if (this.field_1734) {
				this.field_1741 = this.field_1728.field_1970;
			} else {
				this.field_1728.field_1970 = this.field_1741;
			}

			this.field_1734 = bl2;
		}

		long o = class_156.method_648();
		this.field_1688.method_15247(o - this.field_1750);
		this.field_1750 = o;

		while (class_156.method_658() >= this.field_1712 + 1000L) {
			field_1738 = this.field_1735;
			this.field_1770 = String.format(
				"%d fps (%d chunk update%s) T: %s%s%s%s%s",
				field_1738,
				class_851.field_4460,
				class_851.field_4460 == 1 ? "" : "s",
				(double)this.field_1690.field_1909 == class_316.field_1935.method_18617() ? "inf" : this.field_1690.field_1909,
				this.field_1690.field_1884 ? " vsync" : "",
				this.field_1690.field_1833 ? "" : " fast",
				this.field_1690.field_1814 == class_4063.field_18162 ? "" : (this.field_1690.field_1814 == class_4063.field_18163 ? " fast-clouds" : " fancy-clouds"),
				GLX.useVbo() ? " vbo" : ""
			);
			class_851.field_4460 = 0;
			this.field_1712 += 1000L;
			this.field_1735 = 0;
			this.field_1775.method_5485();
			if (!this.field_1775.method_5483()) {
				this.field_1775.method_5482();
			}
		}

		this.field_16240.method_16066();
	}

	private void method_20256() {
		GlStateManager.clear(16640, field_1703);
		this.field_1704.method_4493(field_1703);
		this.field_1764.method_4618(field_19184);
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		double d = (double)this.field_1704.method_4489() / this.field_1704.method_4495();
		double e = (double)this.field_1704.method_4506() / this.field_1704.method_4495();
		lv2.method_1328(7, class_290.field_1585);
		lv2.method_1315(0.0, 0.0, 0.0).method_1312(0.0, 0.0).method_1344();
		lv2.method_1315(0.0, e, 0.0).method_1312(0.0, 0.390625).method_1344();
		lv2.method_1315(d, e, 0.0).method_1312(0.625, 0.390625).method_1344();
		lv2.method_1315(d, 0.0, 0.0).method_1312(0.625, 0.0).method_1344();
		lv.method_1350();
	}

	@Override
	public void method_15994(boolean bl) {
		this.field_16240.method_15396("display_update");
		this.field_1704.method_15998(this.field_1690.field_1857);
		this.field_16240.method_15407();
		if (bl && this.method_16008()) {
			this.field_16240.method_15396("fpslimit_wait");
			this.field_1704.method_15996();
			this.field_16240.method_15407();
		}
	}

	@Override
	public void method_15993() {
		int i = this.field_1704.method_4476(this.field_1690.field_1868, this.method_1573());
		this.field_1704.method_15997((double)i);
		if (this.field_1755 != null) {
			this.field_1755.resize(this, this.field_1704.method_4486(), this.field_1704.method_4502());
		}

		class_276 lv = this.method_1522();
		if (lv != null) {
			lv.method_1234(this.field_1704.method_4489(), this.field_1704.method_4506(), field_1703);
		}

		if (this.field_1773 != null) {
			this.field_1773.method_3169(this.field_1704.method_4489(), this.field_1704.method_4506());
		}

		if (this.field_1729 != null) {
			this.field_1729.method_1599();
		}
	}

	private int method_16009() {
		return this.field_1687 != null || this.field_1755 == null && this.field_18175 == null ? this.field_1704.method_16000() : 60;
	}

	private boolean method_16008() {
		return (double)this.method_16009() < class_316.field_1935.method_18617();
	}

	public void method_1519() {
		try {
			field_1718 = new byte[0];
			this.field_1769.method_3267();
		} catch (Throwable var3) {
		}

		try {
			System.gc();
			if (this.method_1496()) {
				this.field_1766.method_3747(true);
			}

			this.method_18096(new class_424(new class_2588("menu.savingLevel")));
		} catch (Throwable var2) {
		}

		System.gc();
	}

	void method_1524(int i) {
		class_3696 lv = this.field_16240.method_16055().method_16059();
		List<class_3534> list = lv.method_16067(this.field_1701);
		if (!list.isEmpty()) {
			class_3534 lv2 = (class_3534)list.remove(0);
			if (i == 0) {
				if (!lv2.field_15738.isEmpty()) {
					int j = this.field_1701.lastIndexOf(46);
					if (j >= 0) {
						this.field_1701 = this.field_1701.substring(0, j);
					}
				}
			} else {
				i--;
				if (i < list.size() && !"unspecified".equals(((class_3534)list.get(i)).field_15738)) {
					if (!this.field_1701.isEmpty()) {
						this.field_1701 = this.field_1701 + ".";
					}

					this.field_1701 = this.field_1701 + ((class_3534)list.get(i)).field_15738;
				}
			}
		}
	}

	private void method_1492() {
		if (this.field_16240.method_16055().method_16057()) {
			class_3696 lv = this.field_16240.method_16055().method_16059();
			List<class_3534> list = lv.method_16067(this.field_1701);
			class_3534 lv2 = (class_3534)list.remove(0);
			GlStateManager.clear(256, field_1703);
			GlStateManager.matrixMode(5889);
			GlStateManager.enableColorMaterial();
			GlStateManager.loadIdentity();
			GlStateManager.ortho(0.0, (double)this.field_1704.method_4489(), (double)this.field_1704.method_4506(), 0.0, 1000.0, 3000.0);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
			GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
			GlStateManager.lineWidth(1.0F);
			GlStateManager.disableTexture();
			class_289 lv3 = class_289.method_1348();
			class_287 lv4 = lv3.method_1349();
			int i = 160;
			int j = this.field_1704.method_4489() - 160 - 10;
			int k = this.field_1704.method_4506() - 320;
			GlStateManager.enableBlend();
			lv4.method_1328(7, class_290.field_1576);
			lv4.method_1315((double)((float)j - 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0).method_1323(200, 0, 0, 0).method_1344();
			lv4.method_1315((double)((float)j - 176.0F), (double)(k + 320), 0.0).method_1323(200, 0, 0, 0).method_1344();
			lv4.method_1315((double)((float)j + 176.0F), (double)(k + 320), 0.0).method_1323(200, 0, 0, 0).method_1344();
			lv4.method_1315((double)((float)j + 176.0F), (double)((float)k - 96.0F - 16.0F), 0.0).method_1323(200, 0, 0, 0).method_1344();
			lv3.method_1350();
			GlStateManager.disableBlend();
			double d = 0.0;

			for (int l = 0; l < list.size(); l++) {
				class_3534 lv5 = (class_3534)list.get(l);
				int m = class_3532.method_15357(lv5.field_15739 / 4.0) + 1;
				lv4.method_1328(6, class_290.field_1576);
				int n = lv5.method_15409();
				int o = n >> 16 & 0xFF;
				int p = n >> 8 & 0xFF;
				int q = n & 0xFF;
				lv4.method_1315((double)j, (double)k, 0.0).method_1323(o, p, q, 255).method_1344();

				for (int r = m; r >= 0; r--) {
					float f = (float)((d + lv5.field_15739 * (double)r / (double)m) * (float) (Math.PI * 2) / 100.0);
					float g = class_3532.method_15374(f) * 160.0F;
					float h = class_3532.method_15362(f) * 160.0F * 0.5F;
					lv4.method_1315((double)((float)j + g), (double)((float)k - h), 0.0).method_1323(o, p, q, 255).method_1344();
				}

				lv3.method_1350();
				lv4.method_1328(5, class_290.field_1576);

				for (int r = m; r >= 0; r--) {
					float f = (float)((d + lv5.field_15739 * (double)r / (double)m) * (float) (Math.PI * 2) / 100.0);
					float g = class_3532.method_15374(f) * 160.0F;
					float h = class_3532.method_15362(f) * 160.0F * 0.5F;
					lv4.method_1315((double)((float)j + g), (double)((float)k - h), 0.0).method_1323(o >> 1, p >> 1, q >> 1, 255).method_1344();
					lv4.method_1315((double)((float)j + g), (double)((float)k - h + 10.0F), 0.0).method_1323(o >> 1, p >> 1, q >> 1, 255).method_1344();
				}

				lv3.method_1350();
				d += lv5.field_15739;
			}

			DecimalFormat decimalFormat = new DecimalFormat("##0.00");
			decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
			GlStateManager.enableTexture();
			String string = "";
			if (!"unspecified".equals(lv2.field_15738)) {
				string = string + "[0] ";
			}

			if (lv2.field_15738.isEmpty()) {
				string = string + "ROOT ";
			} else {
				string = string + lv2.field_15738 + ' ';
			}

			int m = 16777215;
			this.field_1772.method_1720(string, (float)(j - 160), (float)(k - 80 - 16), 16777215);
			string = decimalFormat.format(lv2.field_15737) + "%";
			this.field_1772.method_1720(string, (float)(j + 160 - this.field_1772.method_1727(string)), (float)(k - 80 - 16), 16777215);

			for (int s = 0; s < list.size(); s++) {
				class_3534 lv6 = (class_3534)list.get(s);
				StringBuilder stringBuilder = new StringBuilder();
				if ("unspecified".equals(lv6.field_15738)) {
					stringBuilder.append("[?] ");
				} else {
					stringBuilder.append("[").append(s + 1).append("] ");
				}

				String string2 = stringBuilder.append(lv6.field_15738).toString();
				this.field_1772.method_1720(string2, (float)(j - 160), (float)(k + 80 + s * 8 + 20), lv6.method_15409());
				string2 = decimalFormat.format(lv6.field_15739) + "%";
				this.field_1772.method_1720(string2, (float)(j + 160 - 50 - this.field_1772.method_1727(string2)), (float)(k + 80 + s * 8 + 20), lv6.method_15409());
				string2 = decimalFormat.format(lv6.field_15737) + "%";
				this.field_1772.method_1720(string2, (float)(j + 160 - this.field_1772.method_1727(string2)), (float)(k + 80 + s * 8 + 20), lv6.method_15409());
			}
		}
	}

	public void method_20258() {
		if (this.field_19187) {
			this.field_1698 = false;
		} else {
			GLFW.glfwSetWindowShouldClose(this.field_1704.method_4490(), false);
			if (!this.field_19186) {
				this.method_20231(() -> {
					this.method_1490();
					this.method_18502(new class_4292(this));
					this.field_19187 = true;
				});
			}

			this.field_19186 = true;
		}
	}

	public void method_16889() {
		if (this.field_1755 == null) {
			this.method_1507(new class_433());
			if (this.method_1496() && !this.field_1766.method_3860()) {
				this.field_1727.method_4879();
			}
		}
	}

	private void method_1590(boolean bl) {
		if (!bl) {
			this.field_1771 = 0;
		}

		if (this.field_1771 <= 0 && !this.field_1724.method_6115()) {
			if (bl && this.field_1765 != null && this.field_1765.method_17783() == class_239.class_240.field_1332) {
				class_3965 lv = (class_3965)this.field_1765;
				class_2338 lv2 = lv.method_17777();
				if (!this.field_1687.method_8320(lv2).method_11588()) {
					class_2350 lv3 = lv.method_17780();
					if (this.field_1761.method_2902(lv2, lv3)) {
						this.field_1713.method_3054(lv2, lv3);
						this.field_1724.method_6104(class_1268.field_5808);
					}
				}
			} else {
				this.field_1761.method_2925();
			}
		}
	}

	private void method_1536() {
		if (this.field_1771 <= 0) {
			if (this.field_1765 == null) {
				field_1762.error("Null returned as 'hitResult', this shouldn't happen!");
				if (this.field_1761.method_2924()) {
					this.field_1771 = 10;
				}
			} else if (!this.field_1724.method_3144()) {
				switch (this.field_1765.method_17783()) {
					case field_1331:
						this.field_1761.method_2918(this.field_1724, ((class_3966)this.field_1765).method_17782());
						break;
					case field_1332:
						class_3965 lv = (class_3965)this.field_1765;
						class_2338 lv2 = lv.method_17777();
						if (!this.field_1687.method_8320(lv2).method_11588()) {
							this.field_1761.method_2910(lv2, lv.method_17780());
							break;
						}
					case field_1333:
						if (this.field_1761.method_2924()) {
							this.field_1771 = 10;
						}

						this.field_1724.method_7350();
				}

				this.field_1724.method_6104(class_1268.field_5808);
			}
		}
	}

	private void method_1583() {
		if (!this.field_1761.method_2923()) {
			this.field_1752 = 4;
			if (!this.field_1724.method_3144()) {
				if (this.field_1765 == null) {
					field_1762.warn("Null returned as 'hitResult', this shouldn't happen!");
				}

				for (class_1268 lv : class_1268.values()) {
					class_1799 lv2 = this.field_1724.method_5998(lv);
					if (this.field_1765 != null) {
						switch (this.field_1765.method_17783()) {
							case field_1331:
								class_3966 lv3 = (class_3966)this.field_1765;
								class_1297 lv4 = lv3.method_17782();
								if (this.field_1761.method_2917(this.field_1724, lv4, lv3, lv) == class_1269.field_5812) {
									return;
								}

								if (this.field_1761.method_2905(this.field_1724, lv4, lv) == class_1269.field_5812) {
									return;
								}
								break;
							case field_1332:
								class_3965 lv5 = (class_3965)this.field_1765;
								int i = lv2.method_7947();
								class_1269 lv6 = this.field_1761.method_2896(this.field_1724, this.field_1687, lv, lv5);
								if (lv6 == class_1269.field_5812) {
									this.field_1724.method_6104(lv);
									if (!lv2.method_7960() && (lv2.method_7947() != i || this.field_1761.method_2914())) {
										this.field_1773.field_4012.method_3215(lv);
									}

									return;
								}

								if (lv6 == class_1269.field_5814) {
									return;
								}
						}
					}

					if (!lv2.method_7960() && this.field_1761.method_2919(this.field_1724, this.field_1687, lv) == class_1269.field_5812) {
						this.field_1773.field_4012.method_3215(lv);
						return;
					}
				}
			}
		}
	}

	public class_1142 method_1538() {
		return this.field_1714;
	}

	public void method_1574() {
		if (this.field_1752 > 0) {
			this.field_1752--;
		}

		this.field_16240.method_15396("gui");
		if (!this.field_1734) {
			this.field_1705.method_1748();
		}

		this.field_16240.method_15407();
		this.field_1773.method_3190(1.0F);
		this.field_1758.method_4911(this.field_1687, this.field_1765);
		this.field_16240.method_15396("gameMode");
		if (!this.field_1734 && this.field_1687 != null) {
			this.field_1761.method_2927();
		}

		this.field_16240.method_15405("textures");
		if (this.field_1687 != null) {
			this.field_1764.method_4622();
		}

		if (this.field_1755 == null && this.field_1724 != null) {
			if (this.field_1724.method_6032() <= 0.0F && !(this.field_1755 instanceof class_418)) {
				this.method_1507(null);
			} else if (this.field_1724.method_6113() && this.field_1687 != null) {
				this.method_1507(new class_423());
			}
		} else if (this.field_1755 != null && this.field_1755 instanceof class_423 && !this.field_1724.method_6113()) {
			this.method_1507(null);
		}

		if (this.field_1755 != null) {
			this.field_1771 = 10000;
		}

		if (this.field_1755 != null) {
			class_437.wrapScreenError(() -> this.field_1755.tick(), "Ticking screen", this.field_1755.getClass().getCanonicalName());
		}

		if (!this.field_1690.field_1866) {
			this.field_1705.method_1745();
		}

		if (this.field_18175 == null && (this.field_1755 == null || this.field_1755.passEvents)) {
			this.field_16240.method_15405("GLFW events");
			GLX.pollEvents();
			this.method_1508();
			if (this.field_1771 > 0) {
				this.field_1771--;
			}
		}

		if (this.field_1687 != null) {
			this.field_16240.method_15405("gameRenderer");
			if (!this.field_1734) {
				this.field_1773.method_3182();
			}

			this.field_16240.method_15405("levelRenderer");
			if (!this.field_1734) {
				this.field_1769.method_3252();
			}

			this.field_16240.method_15405("level");
			if (!this.field_1734) {
				if (this.field_1687.method_8529() > 0) {
					this.field_1687.method_8509(this.field_1687.method_8529() - 1);
				}

				this.field_1687.method_18116();
			}
		} else if (this.field_1773.method_3175()) {
			this.field_1773.method_3207();
		}

		if (!this.field_1734 && !(this.field_1755 instanceof class_4294)) {
			this.field_1714.method_18669();
		}

		this.field_1727.method_18670(this.field_1734);
		if (this.field_1687 != null) {
			if (!this.field_1734) {
				this.field_1687.method_8424(this.field_1687.method_8407() != class_1267.field_5801, true);
				this.field_1758.method_4917();

				try {
					this.field_1687.method_8441(() -> true);
				} catch (Throwable var4) {
					class_128 lv = class_128.method_560(var4, "Exception in world tick");
					if (this.field_1687 == null) {
						class_129 lv2 = lv.method_562("Affected level");
						lv2.method_578("Problem", "Level is null!");
					} else {
						this.field_1687.method_8538(lv);
					}

					throw new class_148(lv);
				}
			}

			this.field_16240.method_15405("animateTick");
			if (!this.field_1734 && this.field_1687 != null) {
				this.field_1687
					.method_2941(
						class_3532.method_15357(this.field_1724.field_5987),
						class_3532.method_15357(this.field_1724.field_6010),
						class_3532.method_15357(this.field_1724.field_6035)
					);
			}

			this.field_16240.method_15405("particles");
			if (!this.field_1734) {
				this.field_1713.method_3057();
			}
		} else if (this.field_1746 != null) {
			this.field_16240.method_15405("pendingConnection");
			this.field_1746.method_10754();
		}

		this.field_16240.method_15405("keyboard");
		this.field_1774.method_1474();
		this.field_16240.method_15407();
	}

	private void method_1508() {
		while (this.field_1690.field_1824.method_1436()) {
			this.field_1690.field_1850++;
			if (this.field_1690.field_1850 > 2) {
				this.field_1690.field_1850 = 0;
			}

			if (this.field_1690.field_1850 == 0) {
				this.field_1773.method_3167(this.method_1560());
			} else if (this.field_1690.field_1850 == 1) {
				this.field_1773.method_3167(null);
			}

			this.field_1769.method_3292();
		}

		while (this.field_1690.field_1816.method_1436()) {
			this.field_1690.field_1914 = !this.field_1690.field_1914;
		}

		for (int i = 0; i < 9; i++) {
			boolean bl = this.field_1690.field_1879.method_1434();
			boolean bl2 = this.field_1690.field_1874.method_1434();
			if (this.field_1690.field_1852[i].method_1436()) {
				if (this.field_1724.method_7325()) {
					this.field_1705.method_1739().method_1977(i);
				} else if (!this.field_1724.method_7337() || this.field_1755 != null || !bl2 && !bl) {
					this.field_1724.field_7514.field_7545 = i;
				} else {
					class_481.method_2462(this, i, bl2, bl);
				}
			}
		}

		while (this.field_1690.field_1822.method_1436()) {
			if (this.field_1761.method_2895()) {
				this.field_1724.method_3132();
			} else {
				this.field_1758.method_4912();
				this.method_1507(new class_490(this.field_1724));
			}
		}

		while (this.field_1690.field_1844.method_1436()) {
			this.method_1507(new class_457(this.field_1724.field_3944.method_2869()));
		}

		while (this.field_1690.field_1831.method_1436()) {
			if (!this.field_1724.method_7325()) {
				this.method_1562().method_2883(new class_2846(class_2846.class_2847.field_12969, class_2338.field_10980, class_2350.field_11033));
			}
		}

		while (this.field_1690.field_1869.method_1436()) {
			if (!this.field_1724.method_7325()) {
				this.field_1724.method_7290(class_437.hasControlDown());
			}
		}

		boolean bl3 = this.field_1690.field_1877 != class_1659.field_7536;
		if (bl3) {
			while (this.field_1690.field_1890.method_1436()) {
				this.method_1507(new class_408(""));
			}

			if (this.field_1755 == null && this.field_18175 == null && this.field_1690.field_1845.method_1436()) {
				this.method_1507(new class_408("/"));
			}
		}

		if (this.field_1724.method_6115()) {
			if (!this.field_1690.field_1904.method_1434()) {
				this.field_1761.method_2897(this.field_1724);
			}

			while (this.field_1690.field_1886.method_1436()) {
			}

			while (this.field_1690.field_1904.method_1436()) {
			}

			while (this.field_1690.field_1871.method_1436()) {
			}
		} else {
			while (this.field_1690.field_1886.method_1436()) {
				this.method_1536();
			}

			while (this.field_1690.field_1904.method_1436()) {
				this.method_1583();
			}

			while (this.field_1690.field_1871.method_1436()) {
				this.method_1511();
			}
		}

		if (this.field_1690.field_1904.method_1434() && this.field_1752 == 0 && !this.field_1724.method_6115()) {
			this.method_1583();
		}

		this.method_1590(this.field_1755 == null && this.field_1690.field_1886.method_1434() && this.field_1729.method_1613());
	}

	public void method_1559(String string, String string2, @Nullable class_1940 arg) {
		this.method_18099();
		class_29 lv = this.field_1748.method_242(string, null);
		class_31 lv2 = lv.method_133();
		if (lv2 == null && arg != null) {
			lv2 = new class_31(arg, string);
			lv.method_136(lv2);
		}

		if (arg == null) {
			arg = new class_1940(lv2);
		}

		this.field_17405.set(null);

		try {
			YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(this.field_1739, UUID.randomUUID().toString());
			MinecraftSessionService minecraftSessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
			GameProfileRepository gameProfileRepository = yggdrasilAuthenticationService.createProfileRepository();
			class_3312 lv3 = new class_3312(gameProfileRepository, new File(this.field_1697, MinecraftServer.field_4588.getName()));
			class_2631.method_11337(lv3);
			class_2631.method_11336(minecraftSessionService);
			class_3312.method_14510(false);
			this.field_1766 = new class_1132(this, string, string2, arg, yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, lv3, i -> {
				class_3953 lvx = new class_3953(i + 0);
				lvx.method_17675();
				this.field_17405.set(lvx);
				return new class_3952(lvx, this.field_17404::add);
			});
			this.field_1766.method_3717();
			this.field_1759 = true;
		} catch (Throwable var11) {
			class_128 lv4 = class_128.method_560(var11, "Starting integrated server");
			class_129 lv5 = lv4.method_562("Starting integrated server");
			lv5.method_578("Level ID", string);
			lv5.method_578("Level Name", string2);
			throw new class_148(lv4);
		}

		while (this.field_17405.get() == null) {
			Thread.yield();
		}

		class_3928 lv6 = new class_3928((class_3953)this.field_17405.get());
		this.method_1507(lv6);

		while (!this.field_1766.method_3820()) {
			lv6.tick();
			this.method_1523(false);

			try {
				Thread.sleep(16L);
			} catch (InterruptedException var10) {
			}

			if (this.field_1691 && this.field_1747 != null) {
				this.method_1565(this.field_1747);
				return;
			}
		}

		SocketAddress socketAddress = this.field_1766.method_3787().method_14353();
		class_2535 lv7 = class_2535.method_10769(socketAddress);
		lv7.method_10763(new class_635(lv7, this, null, argx -> {
		}));
		lv7.method_10743(new class_2889(socketAddress.toString(), 0, class_2539.field_11688));
		lv7.method_10743(new class_2915(this.method_1548().method_1677()));
		this.field_1746 = lv7;
	}

	public void method_1481(class_638 arg) {
		class_435 lv = new class_435();
		lv.method_15412(new class_2588("connect.joining"));
		this.method_18098(lv);
		this.field_1687 = arg;
		this.method_18097(arg);
		if (!this.field_1759) {
			AuthenticationService authenticationService = new YggdrasilAuthenticationService(this.field_1739, UUID.randomUUID().toString());
			MinecraftSessionService minecraftSessionService = authenticationService.createMinecraftSessionService();
			GameProfileRepository gameProfileRepository = authenticationService.createProfileRepository();
			class_3312 lv2 = new class_3312(gameProfileRepository, new File(this.field_1697, MinecraftServer.field_4588.getName()));
			class_2631.method_11337(lv2);
			class_2631.method_11336(minecraftSessionService);
			class_3312.method_14510(false);
		}
	}

	public void method_18099() {
		this.method_18096(new class_435());
	}

	public void method_18096(class_437 arg) {
		class_634 lv = this.method_1562();
		if (lv != null) {
			this.method_18855();
			lv.method_2868();
		}

		class_1132 lv2 = this.field_1766;
		this.field_1766 = null;
		this.field_1773.method_3203();
		this.field_1761 = null;
		class_333.field_2054.method_1793();
		this.method_18098(arg);
		if (this.field_1687 != null) {
			if (lv2 != null) {
				while (!lv2.method_16043()) {
					this.method_1523(false);
				}
			}

			this.field_1722.method_4642();
			this.field_1705.method_1747();
			this.method_1584(null);
			this.field_1759 = false;
			this.field_16762.method_16688();
		}

		this.field_1687 = null;
		this.method_18097(null);
		this.field_1724 = null;
	}

	private void method_18098(class_437 arg) {
		this.field_1714.method_4859();
		this.field_1727.method_4881();
		this.field_1719 = null;
		this.field_1746 = null;
		this.method_1507(arg);
		this.method_1523(false);
	}

	private void method_18097(@Nullable class_638 arg) {
		if (this.field_1769 != null) {
			this.field_1769.method_3244(arg);
		}

		if (this.field_1713 != null) {
			this.field_1713.method_3045(arg);
		}

		class_824.field_4346.method_3551(arg);
	}

	public final boolean method_1530() {
		return this.field_1721;
	}

	@Nullable
	public class_634 method_1562() {
		return this.field_1724 == null ? null : this.field_1724.field_3944;
	}

	public static boolean method_1498() {
		return field_1700 == null || !field_1700.field_1690.field_1842;
	}

	public static boolean method_1517() {
		return field_1700 != null && field_1700.field_1690.field_1833;
	}

	public static boolean method_1588() {
		return field_1700 != null && field_1700.field_1690.field_1841 != class_4060.field_18144;
	}

	private void method_1511() {
		if (this.field_1765 != null && this.field_1765.method_17783() != class_239.class_240.field_1333) {
			boolean bl = this.field_1724.field_7503.field_7477;
			class_2586 lv = null;
			class_239.class_240 lv2 = this.field_1765.method_17783();
			class_1799 lv6;
			if (lv2 == class_239.class_240.field_1332) {
				class_2338 lv3 = ((class_3965)this.field_1765).method_17777();
				class_2680 lv4 = this.field_1687.method_8320(lv3);
				class_2248 lv5 = lv4.method_11614();
				if (lv4.method_11588()) {
					return;
				}

				lv6 = lv5.method_9574(this.field_1687, lv3, lv4);
				if (lv6.method_7960()) {
					return;
				}

				if (bl && class_437.hasControlDown() && lv5.method_9570()) {
					lv = this.field_1687.method_8321(lv3);
				}
			} else {
				if (lv2 != class_239.class_240.field_1331 || !bl) {
					return;
				}

				class_1297 lv7 = ((class_3966)this.field_1765).method_17782();
				if (lv7 instanceof class_1534) {
					lv6 = new class_1799(class_1802.field_8892);
				} else if (lv7 instanceof class_1532) {
					lv6 = new class_1799(class_1802.field_8719);
				} else if (lv7 instanceof class_1533) {
					class_1533 lv8 = (class_1533)lv7;
					class_1799 lv9 = lv8.method_6940();
					if (lv9.method_7960()) {
						lv6 = new class_1799(class_1802.field_8143);
					} else {
						lv6 = lv9.method_7972();
					}
				} else if (lv7 instanceof class_1688) {
					class_1688 lv10 = (class_1688)lv7;
					class_1792 lv11;
					switch (lv10.method_7518()) {
						case field_7679:
							lv11 = class_1802.field_8063;
							break;
						case field_7678:
							lv11 = class_1802.field_8388;
							break;
						case field_7675:
							lv11 = class_1802.field_8069;
							break;
						case field_7677:
							lv11 = class_1802.field_8836;
							break;
						case field_7681:
							lv11 = class_1802.field_8220;
							break;
						default:
							lv11 = class_1802.field_8045;
					}

					lv6 = new class_1799(lv11);
				} else if (lv7 instanceof class_1690) {
					lv6 = new class_1799(((class_1690)lv7).method_7557());
				} else if (lv7 instanceof class_1531) {
					lv6 = new class_1799(class_1802.field_8694);
				} else if (lv7 instanceof class_1511) {
					lv6 = new class_1799(class_1802.field_8301);
				} else {
					class_1826 lv12 = class_1826.method_8019(lv7.method_5864());
					if (lv12 == null) {
						return;
					}

					lv6 = new class_1799(lv12);
				}
			}

			if (lv6.method_7960()) {
				String string = "";
				if (lv2 == class_239.class_240.field_1332) {
					string = class_2378.field_11146.method_10221(this.field_1687.method_8320(((class_3965)this.field_1765).method_17777()).method_11614()).toString();
				} else if (lv2 == class_239.class_240.field_1331) {
					string = class_2378.field_11145.method_10221(((class_3966)this.field_1765).method_17782().method_5864()).toString();
				}

				field_1762.warn("Picking on: [{}] {} gave null item", lv2, string);
			} else {
				class_1661 lv13 = this.field_1724.field_7514;
				if (lv != null) {
					this.method_1499(lv6, lv);
				}

				int i = lv13.method_7395(lv6);
				if (bl) {
					lv13.method_7374(lv6);
					this.field_1761.method_2909(this.field_1724.method_5998(class_1268.field_5808), 36 + lv13.field_7545);
				} else if (i != -1) {
					if (class_1661.method_7380(i)) {
						lv13.field_7545 = i;
					} else {
						this.field_1761.method_2916(i);
					}
				}
			}
		}
	}

	private class_1799 method_1499(class_1799 arg, class_2586 arg2) {
		class_2487 lv = arg2.method_11007(new class_2487());
		if (arg.method_7909() instanceof class_1809 && lv.method_10545("Owner")) {
			class_2487 lv2 = lv.method_10562("Owner");
			arg.method_7948().method_10566("SkullOwner", lv2);
			return arg;
		} else {
			arg.method_7959("BlockEntityTag", lv);
			class_2487 lv2 = new class_2487();
			class_2499 lv3 = new class_2499();
			lv3.add(new class_2519("\"(+NBT)\""));
			lv2.method_10566("Lore", lv3);
			arg.method_7959("display", lv2);
			return arg;
		}
	}

	public class_128 method_1587(class_128 arg) {
		class_129 lv = arg.method_567();
		lv.method_577("Launched Version", () -> this.field_1711);
		lv.method_577("LWJGL", GLX::getLWJGLVersion);
		lv.method_577("OpenGL", GLX::getOpenGLVersionString);
		lv.method_577("GL Caps", GLX::getCapsString);
		lv.method_577("Using VBOs", () -> "Yes");
		lv.method_577(
			"Is Modded",
			() -> {
				String string = ClientBrandRetriever.getClientModName();
				if (!"vanilla".equals(string)) {
					return "Definitely; Client brand changed to '" + string + "'";
				} else {
					return class_310.class.getSigners() == null
						? "Very likely; Jar signature invalidated"
						: "Probably not. Jar signature remains and client brand is untouched.";
				}
			}
		);
		lv.method_578("Type", "Client (map_client.txt)");
		lv.method_577("Resource Packs", () -> {
			StringBuilder stringBuilder = new StringBuilder();

			for (String string : this.field_1690.field_1887) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(", ");
				}

				stringBuilder.append(string);
				if (this.field_1690.field_1846.contains(string)) {
					stringBuilder.append(" (incompatible)");
				}
			}

			return stringBuilder.toString();
		});
		lv.method_577("Current Language", () -> this.field_1717.method_4669().toString());
		lv.method_577("CPU", GLX::getCpuInfo);
		if (this.field_1687 != null) {
			this.field_1687.method_8538(arg);
		}

		return arg;
	}

	public static class_310 method_1551() {
		return field_1700;
	}

	public CompletableFuture<Void> method_1513() {
		return this.method_5385(this::method_1521).thenCompose(completableFuture -> completableFuture);
	}

	@Override
	public void method_5495(class_1276 arg) {
		arg.method_5481("fps", field_1738);
		arg.method_5481("vsync_enabled", this.field_1690.field_1884);
		int i = GLX.getRefreshRate(this.field_1704);
		arg.method_5481("display_frequency", i);
		arg.method_5481("display_type", this.field_1704.method_4498() ? "fullscreen" : "windowed");
		arg.method_5481("run_time", (class_156.method_658() - arg.method_5484()) / 60L * 1000L);
		arg.method_5481("current_action", this.method_1563());
		arg.method_5481("language", this.field_1690.field_1883 == null ? "en_us" : this.field_1690.field_1883);
		String string = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
		arg.method_5481("endianness", string);
		arg.method_5481("subtitles", this.field_1690.field_1818);
		arg.method_5481("touch", this.field_1690.field_1854 ? "touch" : "mouse");
		int j = 0;

		for (class_1075 lv : this.field_1715.method_14444()) {
			if (!lv.method_14464() && !lv.method_14465()) {
				arg.method_5481("resource_pack[" + j++ + "]", lv.method_14463());
			}
		}

		arg.method_5481("resource_packs", j);
		if (this.field_1766 != null && this.field_1766.method_3795() != null) {
			arg.method_5481("snooper_partner", this.field_1766.method_3795().method_5479());
		}
	}

	private String method_1563() {
		if (this.field_1766 != null) {
			return this.field_1766.method_3860() ? "hosting_lan" : "singleplayer";
		} else if (this.field_1699 != null) {
			return this.field_1699.method_2994() ? "playing_lan" : "multiplayer";
		} else {
			return "out_of_game";
		}
	}

	public static int method_1580() {
		if (field_1754 == -1) {
			for (int i = 16384; i > 0; i >>= 1) {
				GlStateManager.texImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, null);
				int j = GlStateManager.getTexLevelParameter(32868, 0, 4096);
				if (j != 0) {
					field_1754 = i;
					return i;
				}
			}
		}

		return field_1754;
	}

	public void method_1584(class_642 arg) {
		this.field_1699 = arg;
	}

	@Nullable
	public class_642 method_1558() {
		return this.field_1699;
	}

	public boolean method_1542() {
		return this.field_1759;
	}

	public boolean method_1496() {
		return this.field_1759 && this.field_1766 != null;
	}

	@Nullable
	public class_1132 method_1576() {
		return this.field_1766;
	}

	public class_1276 method_1552() {
		return this.field_1775;
	}

	public class_320 method_1548() {
		return this.field_1726;
	}

	public PropertyMap method_1539() {
		if (this.field_1694.isEmpty()) {
			GameProfile gameProfile = this.method_1495().fillProfileProperties(this.field_1726.method_1677(), false);
			this.field_1694.putAll(gameProfile.getProperties());
		}

		return this.field_1694;
	}

	public Proxy method_1487() {
		return this.field_1739;
	}

	public class_1060 method_1531() {
		return this.field_1764;
	}

	public class_3300 method_1478() {
		return this.field_1745;
	}

	public class_3283<class_1075> method_1520() {
		return this.field_1715;
	}

	public class_1066 method_1516() {
		return this.field_1722;
	}

	public File method_1479() {
		return this.field_1757;
	}

	public class_1076 method_1526() {
		return this.field_1717;
	}

	public class_1059 method_1549() {
		return this.field_1767;
	}

	public boolean method_1540() {
		return this.field_1693;
	}

	public boolean method_1493() {
		return this.field_1734;
	}

	public class_1144 method_1483() {
		return this.field_1727;
	}

	public class_1142.class_1143 method_1544() {
		if (this.field_1755 instanceof class_445) {
			return class_1142.class_1143.field_5578;
		} else if (this.field_1724 == null) {
			return class_1142.class_1143.field_5585;
		} else if (this.field_1724.field_6002.field_9247 instanceof class_2872) {
			return class_1142.class_1143.field_5582;
		} else if (this.field_1724.field_6002.field_9247 instanceof class_2880) {
			return this.field_1705.method_1740().method_1798() ? class_1142.class_1143.field_5580 : class_1142.class_1143.field_5583;
		} else {
			class_1959.class_1961 lv = this.field_1724
				.field_6002
				.method_8310(new class_2338(this.field_1724.field_5987, this.field_1724.field_6010, this.field_1724.field_6035))
				.method_8688();
			if (!this.field_1714.method_4860(class_1142.class_1143.field_5576)
				&& (
					!this.field_1724.method_5869()
						|| this.field_1714.method_4860(class_1142.class_1143.field_5586)
						|| lv != class_1959.class_1961.field_9367 && lv != class_1959.class_1961.field_9369
				)) {
				return this.field_1724.field_7503.field_7477 && this.field_1724.field_7503.field_7478 ? class_1142.class_1143.field_5581 : class_1142.class_1143.field_5586;
			} else {
				return class_1142.class_1143.field_5576;
			}
		}
	}

	public MinecraftSessionService method_1495() {
		return this.field_1723;
	}

	public class_1071 method_1582() {
		return this.field_1707;
	}

	@Nullable
	public class_1297 method_1560() {
		return this.field_1719;
	}

	public void method_1504(class_1297 arg) {
		this.field_1719 = arg;
		this.field_1773.method_3167(arg);
	}

	@Override
	protected Thread method_3777() {
		return this.field_1696;
	}

	@Override
	protected Runnable method_16211(Runnable runnable) {
		return runnable;
	}

	@Override
	protected boolean method_18856(Runnable runnable) {
		return true;
	}

	public class_776 method_1541() {
		return this.field_1756;
	}

	public class_898 method_1561() {
		return this.field_1731;
	}

	public class_918 method_1480() {
		return this.field_1742;
	}

	public class_759 method_1489() {
		return this.field_1737;
	}

	public <T> class_1123<T> method_1484(class_1124.class_1125<T> arg) {
		return this.field_1733.method_4800(arg);
	}

	public static int method_1497() {
		return field_1738;
	}

	public class_3517 method_1570() {
		return this.field_1688;
	}

	public boolean method_1589() {
		return this.field_1744;
	}

	public void method_1537(boolean bl) {
		this.field_1744 = bl;
	}

	public DataFixer method_1543() {
		return this.field_1768;
	}

	public float method_1488() {
		return 0.0F;
	}

	public float method_1534() {
		return this.field_1728.field_1969;
	}

	public class_324 method_1505() {
		return this.field_1751;
	}

	public boolean method_1555() {
		return this.field_1724 != null && this.field_1724.method_7302() || this.field_1690.field_1910;
	}

	public class_374 method_1566() {
		return this.field_1702;
	}

	public class_1156 method_1577() {
		return this.field_1758;
	}

	public boolean method_1569() {
		return this.field_1695;
	}

	public class_302 method_1571() {
		return this.field_1732;
	}

	public class_1092 method_1554() {
		return this.field_1763;
	}

	public class_378 method_1568() {
		return this.field_1708;
	}

	public class_4044 method_18321() {
		return this.field_18008;
	}

	public class_4074 method_18505() {
		return this.field_18173;
	}

	@Override
	public void method_15995(boolean bl) {
		this.field_1695 = bl;
	}

	public class_3695 method_16011() {
		return this.field_16240;
	}

	public class_3799 method_16689() {
		return this.field_16762;
	}

	public class_4008 method_18095() {
		return this.field_17763;
	}

	@Nullable
	public class_4071 method_18506() {
		return this.field_18175;
	}
}
