package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_634 implements class_2602 {
	private static final Logger field_3695 = LogManager.getLogger();
	private final class_2535 field_3689;
	private final GameProfile field_3697;
	private final class_437 field_3701;
	private class_310 field_3690;
	private class_638 field_3699;
	private boolean field_3698;
	private final Map<UUID, class_640> field_3693 = Maps.<UUID, class_640>newHashMap();
	private final class_632 field_3700;
	private final class_637 field_3691;
	private class_3505 field_3694 = new class_3505();
	private final class_300 field_3692 = new class_300(this);
	private int field_19144 = 3;
	private final Random field_3687 = new Random();
	private CommandDispatcher<class_2172> field_3696 = new CommandDispatcher<>();
	private final class_1863 field_3688 = new class_1863();
	private final UUID field_16771 = UUID.randomUUID();

	public class_634(class_310 arg, class_437 arg2, class_2535 arg3, GameProfile gameProfile) {
		this.field_3690 = arg;
		this.field_3701 = arg2;
		this.field_3689 = arg3;
		this.field_3697 = gameProfile;
		this.field_3700 = new class_632(arg);
		this.field_3691 = new class_637(this, arg);
	}

	public class_637 method_2875() {
		return this.field_3691;
	}

	public void method_2868() {
		this.field_3699 = null;
	}

	public class_1863 method_2877() {
		return this.field_3688;
	}

	@Override
	public void method_11120(class_2678 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3690.field_1761 = new class_636(this.field_3690, this);
		this.field_19144 = arg.method_20204();
		this.field_3699 = new class_638(
			this,
			new class_1940(0L, arg.method_11561(), false, arg.method_11568(), arg.method_11563()),
			arg.method_11565(),
			this.field_19144,
			this.field_3690.method_16011(),
			this.field_3690.field_1769
		);
		this.field_3690.method_1481(this.field_3699);
		if (this.field_3690.field_1724 == null) {
			this.field_3690.field_1724 = this.field_3690.field_1761.method_2901(this.field_3699, new class_3469(), new class_299(this.field_3699.method_8433()));
			this.field_3690.field_1724.field_6031 = -180.0F;
			if (this.field_3690.method_1576() != null) {
				this.field_3690.method_1576().method_4817(this.field_3690.field_1724.method_5667());
			}
		}

		this.field_3690.field_1724.method_5823();
		int i = arg.method_11564();
		this.field_3699.method_18107(i, this.field_3690.field_1724);
		this.field_3690.field_1724.field_3913 = new class_743(this.field_3690.field_1690);
		this.field_3690.field_1761.method_2903(this.field_3690.field_1724);
		this.field_3690.field_1719 = this.field_3690.field_1724;
		this.field_3690.field_1724.field_6026 = arg.method_11565();
		this.field_3690.method_1507(new class_434());
		this.field_3690.field_1724.method_5838(i);
		this.field_3690.field_1724.method_7268(arg.method_11562());
		this.field_3690.field_1761.method_2907(arg.method_11561());
		this.field_3690.field_1690.method_1643();
		this.field_3689.method_10743(new class_2817(class_2817.field_12831, new class_2540(Unpooled.buffer()).method_10814(ClientBrandRetriever.getClientModName())));
		this.field_3690.method_16689().method_16687();
	}

	@Override
	public void method_11112(class_2604 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		double d = arg.method_11175();
		double e = arg.method_11174();
		double f = arg.method_11176();
		class_1299<?> lv = arg.method_11169();
		class_1297 lv2;
		if (lv == class_1299.field_6126) {
			lv2 = new class_1694(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6080) {
			lv2 = new class_1696(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6053) {
			lv2 = new class_1701(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6142) {
			lv2 = new class_1699(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6058) {
			lv2 = new class_1700(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6136) {
			lv2 = new class_1697(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6096) {
			lv2 = new class_1695(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6103) {
			class_1297 lv3 = this.field_3699.method_8469(arg.method_11166());
			if (lv3 instanceof class_1657) {
				lv2 = new class_1536(this.field_3699, (class_1657)lv3, d, e, f);
			} else {
				lv2 = null;
			}
		} else if (lv == class_1299.field_6122) {
			lv2 = new class_1667(this.field_3699, d, e, f);
			class_1297 lv3 = this.field_3699.method_8469(arg.method_11166());
			if (lv3 != null) {
				((class_1665)lv2).method_7432(lv3);
			}
		} else if (lv == class_1299.field_6135) {
			lv2 = new class_1679(this.field_3699, d, e, f);
			class_1297 lv3 = this.field_3699.method_8469(arg.method_11166());
			if (lv3 != null) {
				((class_1665)lv2).method_7432(lv3);
			}
		} else if (lv == class_1299.field_6127) {
			lv2 = new class_1685(this.field_3699, d, e, f);
			class_1297 lv3 = this.field_3699.method_8469(arg.method_11166());
			if (lv3 != null) {
				((class_1665)lv2).method_7432(lv3);
			}
		} else if (lv == class_1299.field_6068) {
			lv2 = new class_1680(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6124) {
			lv2 = new class_1673(this.field_3699, d, e, f, arg.method_11170(), arg.method_11172(), arg.method_11173());
		} else if (lv == class_1299.field_6043) {
			lv2 = new class_1533(this.field_3699, new class_2338(d, e, f), class_2350.method_10143(arg.method_11166()));
		} else if (lv == class_1299.field_6138) {
			lv2 = new class_1532(this.field_3699, new class_2338(d, e, f));
		} else if (lv == class_1299.field_6082) {
			lv2 = new class_1684(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6061) {
			lv2 = new class_1672(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6133) {
			lv2 = new class_1671(this.field_3699, d, e, f, class_1799.field_8037);
		} else if (lv == class_1299.field_6066) {
			lv2 = new class_1674(this.field_3699, d, e, f, arg.method_11170(), arg.method_11172(), arg.method_11173());
		} else if (lv == class_1299.field_6129) {
			lv2 = new class_1670(this.field_3699, d, e, f, arg.method_11170(), arg.method_11172(), arg.method_11173());
		} else if (lv == class_1299.field_6049) {
			lv2 = new class_1677(this.field_3699, d, e, f, arg.method_11170(), arg.method_11172(), arg.method_11173());
		} else if (lv == class_1299.field_6130) {
			lv2 = new class_1687(this.field_3699, d, e, f, arg.method_11170(), arg.method_11172(), arg.method_11173());
		} else if (lv == class_1299.field_6100) {
			lv2 = new class_1678(this.field_3699, d, e, f, arg.method_11170(), arg.method_11172(), arg.method_11173());
		} else if (lv == class_1299.field_6144) {
			lv2 = new class_1681(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6060) {
			lv2 = new class_1669(this.field_3699, d, e, f, 0.0F, 0, null);
		} else if (lv == class_1299.field_6045) {
			lv2 = new class_1686(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6064) {
			lv2 = new class_1683(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6121) {
			lv2 = new class_1690(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6063) {
			lv2 = new class_1541(this.field_3699, d, e, f, null);
		} else if (lv == class_1299.field_6131) {
			lv2 = new class_1531(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6110) {
			lv2 = new class_1511(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6052) {
			lv2 = new class_1542(this.field_3699, d, e, f);
		} else if (lv == class_1299.field_6089) {
			lv2 = new class_1540(this.field_3699, d, e, f, class_2248.method_9531(arg.method_11166()));
		} else if (lv == class_1299.field_6083) {
			lv2 = new class_1295(this.field_3699, d, e, f);
		} else {
			lv2 = null;
		}

		if (lv2 != null) {
			int i = arg.method_11167();
			lv2.method_18003(d, e, f);
			lv2.field_5965 = (float)(arg.method_11171() * 360) / 256.0F;
			lv2.field_6031 = (float)(arg.method_11168() * 360) / 256.0F;
			lv2.method_5838(i);
			lv2.method_5826(arg.method_11164());
			this.field_3699.method_2942(i, lv2);
			if (lv2 instanceof class_1688) {
				this.field_3690.method_1483().method_4873(new class_1108((class_1688)lv2));
			}
		}
	}

	@Override
	public void method_11091(class_2606 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		double d = arg.method_11185();
		double e = arg.method_11181();
		double f = arg.method_11180();
		class_1297 lv = new class_1303(this.field_3699, d, e, f, arg.method_11184());
		lv.method_18003(d, e, f);
		lv.field_6031 = 0.0F;
		lv.field_5965 = 0.0F;
		lv.method_5838(arg.method_11183());
		this.field_3699.method_2942(arg.method_11183(), lv);
	}

	@Override
	public void method_11156(class_2607 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		double d = arg.method_11191();
		double e = arg.method_11187();
		double f = arg.method_11186();
		if (arg.method_11190() == 1) {
			class_1538 lv = new class_1538(this.field_3699, d, e, f, false);
			lv.method_18003(d, e, f);
			lv.field_6031 = 0.0F;
			lv.field_5965 = 0.0F;
			lv.method_5838(arg.method_11189());
			this.field_3699.method_18108(lv);
		}
	}

	@Override
	public void method_11114(class_2612 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1534 lv = new class_1534(this.field_3699, arg.method_11226(), arg.method_11223(), arg.method_11221());
		lv.method_5838(arg.method_11225());
		lv.method_5826(arg.method_11222());
		this.field_3699.method_2942(arg.method_11225(), lv);
	}

	@Override
	public void method_11132(class_2743 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = this.field_3699.method_8469(arg.method_11818());
		if (lv != null) {
			lv.method_5750((double)arg.method_11815() / 8000.0, (double)arg.method_11816() / 8000.0, (double)arg.method_11819() / 8000.0);
		}
	}

	@Override
	public void method_11093(class_2739 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = this.field_3699.method_8469(arg.method_11807());
		if (lv != null && arg.method_11809() != null) {
			lv.method_5841().method_12779(arg.method_11809());
		}
	}

	@Override
	public void method_11097(class_2613 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		double d = arg.method_11231();
		double e = arg.method_11232();
		double f = arg.method_11233();
		float g = (float)(arg.method_11234() * 360) / 256.0F;
		float h = (float)(arg.method_11228() * 360) / 256.0F;
		int i = arg.method_11227();
		class_745 lv = new class_745(this.field_3690.field_1687, this.method_2871(arg.method_11230()).method_2966());
		lv.method_5838(i);
		lv.field_6014 = d;
		lv.field_6038 = d;
		lv.field_6036 = e;
		lv.field_5971 = e;
		lv.field_5969 = f;
		lv.field_5989 = f;
		lv.method_18003(d, e, f);
		lv.method_5641(d, e, f, g, h);
		this.field_3699.method_18107(i, lv);
		List<class_2945.class_2946<?>> list = arg.method_11229();
		if (list != null) {
			lv.method_5841().method_12779(list);
		}
	}

	@Override
	public void method_11086(class_2777 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = this.field_3699.method_8469(arg.method_11916());
		if (lv != null) {
			double d = arg.method_11917();
			double e = arg.method_11919();
			double f = arg.method_11918();
			lv.method_18003(d, e, f);
			if (!lv.method_5787()) {
				float g = (float)(arg.method_11920() * 360) / 256.0F;
				float h = (float)(arg.method_11921() * 360) / 256.0F;
				if (!(Math.abs(lv.field_5987 - d) >= 0.03125) && !(Math.abs(lv.field_6010 - e) >= 0.015625) && !(Math.abs(lv.field_6035 - f) >= 0.03125)) {
					lv.method_5759(lv.field_5987, lv.field_6010, lv.field_6035, g, h, 0, true);
				} else {
					lv.method_5759(d, e, f, g, h, 3, true);
				}

				lv.field_5952 = arg.method_11923();
			}
		}
	}

	@Override
	public void method_11135(class_2735 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		if (class_1661.method_7380(arg.method_11803())) {
			this.field_3690.field_1724.field_7514.field_7545 = arg.method_11803();
		}
	}

	@Override
	public void method_11155(class_2684 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = arg.method_11645(this.field_3699);
		if (lv != null) {
			lv.field_6001 = lv.field_6001 + (long)arg.method_11648();
			lv.field_6023 = lv.field_6023 + (long)arg.method_11646();
			lv.field_5954 = lv.field_5954 + (long)arg.method_11647();
			class_243 lv2 = class_2684.method_18695(lv.field_6001, lv.field_6023, lv.field_5954);
			if (!lv.method_5787()) {
				float f = arg.method_11652() ? (float)(arg.method_11649() * 360) / 256.0F : lv.field_6031;
				float g = arg.method_11652() ? (float)(arg.method_11650() * 360) / 256.0F : lv.field_5965;
				lv.method_5759(lv2.field_1352, lv2.field_1351, lv2.field_1350, f, g, 3, false);
				lv.field_5952 = arg.method_11653();
			}
		}
	}

	@Override
	public void method_11139(class_2726 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = arg.method_11786(this.field_3699);
		if (lv != null) {
			float f = (float)(arg.method_11787() * 360) / 256.0F;
			lv.method_5683(f, 3);
		}
	}

	@Override
	public void method_11095(class_2716 arg) {
		class_2600.method_11074(arg, this, this.field_3690);

		for (int i = 0; i < arg.method_11763().length; i++) {
			int j = arg.method_11763()[i];
			this.field_3699.method_2945(j);
		}
	}

	@Override
	public void method_11157(class_2708 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1657 lv = this.field_3690.field_1724;
		double d = arg.method_11734();
		double e = arg.method_11735();
		double f = arg.method_11738();
		float g = arg.method_11736();
		float h = arg.method_11739();
		class_243 lv2 = lv.method_18798();
		double i = lv2.field_1352;
		double j = lv2.field_1351;
		double k = lv2.field_1350;
		if (arg.method_11733().contains(class_2708.class_2709.field_12400)) {
			lv.field_6038 += d;
			d += lv.field_5987;
		} else {
			lv.field_6038 = d;
			i = 0.0;
		}

		if (arg.method_11733().contains(class_2708.class_2709.field_12398)) {
			lv.field_5971 += e;
			e += lv.field_6010;
		} else {
			lv.field_5971 = e;
			j = 0.0;
		}

		if (arg.method_11733().contains(class_2708.class_2709.field_12403)) {
			lv.field_5989 += f;
			f += lv.field_6035;
		} else {
			lv.field_5989 = f;
			k = 0.0;
		}

		lv.method_18800(i, j, k);
		if (arg.method_11733().contains(class_2708.class_2709.field_12397)) {
			h += lv.field_5965;
		}

		if (arg.method_11733().contains(class_2708.class_2709.field_12401)) {
			g += lv.field_6031;
		}

		lv.method_5641(d, e, f, g, h);
		this.field_3689.method_10743(new class_2793(arg.method_11737()));
		this.field_3689.method_10743(new class_2828.class_2830(lv.field_5987, lv.method_5829().field_1322, lv.field_6035, lv.field_6031, lv.field_5965, false));
		if (!this.field_3698) {
			this.field_3690.field_1724.field_6014 = this.field_3690.field_1724.field_5987;
			this.field_3690.field_1724.field_6036 = this.field_3690.field_1724.field_6010;
			this.field_3690.field_1724.field_5969 = this.field_3690.field_1724.field_6035;
			this.field_3698 = true;
			this.field_3690.method_1507(null);
		}
	}

	@Override
	public void method_11100(class_2637 arg) {
		class_2600.method_11074(arg, this, this.field_3690);

		for (class_2637.class_2638 lv : arg.method_11391()) {
			this.field_3699.method_2937(lv.method_11394(), lv.method_11395());
		}
	}

	@Override
	public void method_11128(class_2672 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		int i = arg.method_11523();
		int j = arg.method_11524();
		class_2818 lv = this.field_3699
			.method_2935()
			.method_16020(this.field_3699, i, j, arg.method_11521(), arg.method_16123(), arg.method_11526(), arg.method_11530());
		if (lv != null) {
			this.field_3699.method_18115(lv);
		}

		for (int k = 0; k < 16; k++) {
			this.field_3699.method_18113(i, k, j);
		}

		for (class_2487 lv2 : arg.method_11525()) {
			class_2338 lv3 = new class_2338(lv2.method_10550("x"), lv2.method_10550("y"), lv2.method_10550("z"));
			class_2586 lv4 = this.field_3699.method_8321(lv3);
			if (lv4 != null) {
				lv4.method_11014(lv2);
			}
		}
	}

	@Override
	public void method_11107(class_2666 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		int i = arg.method_11487();
		int j = arg.method_11485();
		this.field_3699.method_2935().method_2859(i, j);

		for (int k = 0; k < 16; k++) {
			this.field_3699.method_18113(i, k, j);
		}
	}

	@Override
	public void method_11136(class_2626 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3699.method_2937(arg.method_11309(), arg.method_11308());
	}

	@Override
	public void method_11083(class_2661 arg) {
		this.field_3689.method_10747(arg.method_11468());
	}

	@Override
	public void method_10839(class_2561 arg) {
		this.field_3690.method_18099();
		if (this.field_3701 != null) {
			if (this.field_3701 instanceof RealmsScreenProxy) {
				this.field_3690.method_1507(new DisconnectedRealmsScreen(((RealmsScreenProxy)this.field_3701).getScreen(), "disconnect.lost", arg).getProxy());
			} else {
				this.field_3690.method_1507(new class_419(this.field_3701, "disconnect.lost", arg));
			}
		} else {
			this.field_3690.method_1507(new class_419(new class_500(new class_442()), "disconnect.lost", arg));
		}
	}

	public void method_2883(class_2596<?> arg) {
		this.field_3689.method_10743(arg);
	}

	@Override
	public void method_11150(class_2775 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = this.field_3699.method_8469(arg.method_11915());
		class_1309 lv2 = (class_1309)this.field_3699.method_8469(arg.method_11912());
		if (lv2 == null) {
			lv2 = this.field_3690.field_1724;
		}

		if (lv != null) {
			if (lv instanceof class_1303) {
				this.field_3699
					.method_8486(
						lv.field_5987,
						lv.field_6010,
						lv.field_6035,
						class_3417.field_14627,
						class_3419.field_15248,
						0.1F,
						(this.field_3687.nextFloat() - this.field_3687.nextFloat()) * 0.35F + 0.9F,
						false
					);
			} else {
				this.field_3699
					.method_8486(
						lv.field_5987,
						lv.field_6010,
						lv.field_6035,
						class_3417.field_15197,
						class_3419.field_15248,
						0.2F,
						(this.field_3687.nextFloat() - this.field_3687.nextFloat()) * 1.4F + 2.0F,
						false
					);
			}

			if (lv instanceof class_1542) {
				((class_1542)lv).method_6983().method_7939(arg.method_11913());
			}

			this.field_3690.field_1713.method_3058(new class_693(this.field_3699, lv, lv2, 0.5F));
			this.field_3699.method_2945(arg.method_11915());
		}
	}

	@Override
	public void method_11121(class_2635 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3690.field_1705.method_1755(arg.method_11389(), arg.method_11388());
	}

	@Override
	public void method_11160(class_2616 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = this.field_3699.method_8469(arg.method_11269());
		if (lv != null) {
			if (arg.method_11267() == 0) {
				class_1309 lv2 = (class_1309)lv;
				lv2.method_6104(class_1268.field_5808);
			} else if (arg.method_11267() == 3) {
				class_1309 lv2 = (class_1309)lv;
				lv2.method_6104(class_1268.field_5810);
			} else if (arg.method_11267() == 1) {
				lv.method_5879();
			} else if (arg.method_11267() == 2) {
				class_1657 lv3 = (class_1657)lv;
				lv3.method_7358(false, false, false);
			} else if (arg.method_11267() == 4) {
				this.field_3690.field_1713.method_3061(lv, class_2398.field_11205);
			} else if (arg.method_11267() == 5) {
				this.field_3690.field_1713.method_3061(lv, class_2398.field_11208);
			}
		}
	}

	@Override
	public void method_11138(class_2610 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		double d = arg.method_11214();
		double e = arg.method_11215();
		double f = arg.method_11216();
		float g = (float)(arg.method_11205() * 360) / 256.0F;
		float h = (float)(arg.method_11206() * 360) / 256.0F;
		class_1309 lv = (class_1309)class_1299.method_5889(arg.method_11210(), this.field_3690.field_1687);
		if (lv != null) {
			lv.method_18003(d, e, f);
			lv.field_6283 = (float)(arg.method_11204() * 360) / 256.0F;
			lv.field_6241 = (float)(arg.method_11204() * 360) / 256.0F;
			if (lv instanceof class_1510) {
				class_1508[] lvs = ((class_1510)lv).method_5690();

				for (int i = 0; i < lvs.length; i++) {
					lvs[i].method_5838(i + arg.method_11207());
				}
			}

			lv.method_5838(arg.method_11207());
			lv.method_5826(arg.method_11213());
			lv.method_5641(d, e, f, g, h);
			lv.method_18800((double)((float)arg.method_11212() / 8000.0F), (double)((float)arg.method_11211() / 8000.0F), (double)((float)arg.method_11209() / 8000.0F));
			this.field_3699.method_2942(arg.method_11207(), lv);
			List<class_2945.class_2946<?>> list = arg.method_11208();
			if (list != null) {
				lv.method_5841().method_12779(list);
			}
		} else {
			field_3695.warn("Skipping Entity with id {}", arg.method_11210());
		}
	}

	@Override
	public void method_11079(class_2761 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3690.field_1687.method_8516(arg.method_11871());
		this.field_3690.field_1687.method_8435(arg.method_11873());
	}

	@Override
	public void method_11142(class_2759 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3690.field_1724.method_7289(arg.method_11870(), true);
		this.field_3690.field_1687.method_8401().method_187(arg.method_11870());
	}

	@Override
	public void method_11080(class_2752 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = this.field_3699.method_8469(arg.method_11841());
		if (lv == null) {
			field_3695.warn("Received passengers for unknown entity");
		} else {
			boolean bl = lv.method_5821(this.field_3690.field_1724);
			lv.method_5772();

			for (int i : arg.method_11840()) {
				class_1297 lv2 = this.field_3699.method_8469(i);
				if (lv2 != null) {
					lv2.method_5873(lv, true);
					if (lv2 == this.field_3690.field_1724 && !bl) {
						this.field_3690.field_1705.method_1764(class_1074.method_4662("mount.onboard", this.field_3690.field_1690.field_1832.method_16007()), false);
					}
				}
			}
		}
	}

	@Override
	public void method_11110(class_2740 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = this.field_3699.method_8469(arg.method_11812());
		if (lv instanceof class_1308) {
			((class_1308)lv).method_18810(arg.method_11810());
		}
	}

	private static class_1799 method_19691(class_1657 arg) {
		for (class_1268 lv : class_1268.values()) {
			class_1799 lv2 = arg.method_5998(lv);
			if (lv2.method_7909() == class_1802.field_8288) {
				return lv2;
			}
		}

		return new class_1799(class_1802.field_8288);
	}

	@Override
	public void method_11148(class_2663 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = arg.method_11469(this.field_3699);
		if (lv != null) {
			if (arg.method_11470() == 21) {
				this.field_3690.method_1483().method_4873(new class_1105((class_1577)lv));
			} else if (arg.method_11470() == 35) {
				int i = 40;
				this.field_3690.field_1713.method_3051(lv, class_2398.field_11220, 30);
				this.field_3699.method_8486(lv.field_5987, lv.field_6010, lv.field_6035, class_3417.field_14931, lv.method_5634(), 1.0F, 1.0F, false);
				if (lv == this.field_3690.field_1724) {
					this.field_3690.field_1773.method_3189(method_19691(this.field_3690.field_1724));
				}
			} else {
				lv.method_5711(arg.method_11470());
			}
		}
	}

	@Override
	public void method_11122(class_2749 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3690.field_1724.method_3138(arg.method_11833());
		this.field_3690.field_1724.method_7344().method_7580(arg.method_11831());
		this.field_3690.field_1724.method_7344().method_7581(arg.method_11834());
	}

	@Override
	public void method_11101(class_2748 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3690.field_1724.method_3145(arg.method_11830(), arg.method_11827(), arg.method_11828());
	}

	@Override
	public void method_11117(class_2724 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_2874 lv = arg.method_11779();
		class_746 lv2 = this.field_3690.field_1724;
		int i = lv2.method_5628();
		if (lv != lv2.field_6026) {
			this.field_3698 = false;
			class_269 lv3 = this.field_3699.method_8428();
			this.field_3699 = new class_638(
				this,
				new class_1940(0L, arg.method_11780(), false, this.field_3690.field_1687.method_8401().method_152(), arg.method_11781()),
				arg.method_11779(),
				this.field_19144,
				this.field_3690.method_16011(),
				this.field_3690.field_1769
			);
			this.field_3699.method_2944(lv3);
			this.field_3690.method_1481(this.field_3699);
			this.field_3690.method_1507(new class_434());
		}

		this.field_3699.method_8513();
		this.field_3699.method_2936();
		String string = lv2.method_3135();
		this.field_3690.field_1719 = null;
		class_746 lv4 = this.field_3690.field_1761.method_2901(this.field_3699, lv2.method_3143(), lv2.method_3130());
		lv4.method_5838(i);
		lv4.field_6026 = lv;
		this.field_3690.field_1724 = lv4;
		this.field_3690.field_1719 = lv4;
		lv4.method_5841().method_12779(lv2.method_5841().method_12793());
		lv4.method_5823();
		lv4.method_3146(string);
		this.field_3699.method_18107(i, lv4);
		lv4.field_6031 = -180.0F;
		lv4.field_3913 = new class_743(this.field_3690.field_1690);
		this.field_3690.field_1761.method_2903(lv4);
		lv4.method_7268(lv2.method_7302());
		if (this.field_3690.field_1755 instanceof class_418) {
			this.field_3690.method_1507(null);
		}

		this.field_3690.field_1761.method_2907(arg.method_11780());
	}

	@Override
	public void method_11124(class_2664 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1927 lv = new class_1927(
			this.field_3690.field_1687, null, arg.method_11475(), arg.method_11477(), arg.method_11478(), arg.method_11476(), arg.method_11479()
		);
		lv.method_8350(true);
		this.field_3690
			.field_1724
			.method_18799(this.field_3690.field_1724.method_18798().method_1031((double)arg.method_11472(), (double)arg.method_11473(), (double)arg.method_11474()));
	}

	@Override
	public void method_11089(class_2648 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = this.field_3699.method_8469(arg.method_11433());
		if (lv instanceof class_1496) {
			class_746 lv2 = this.field_3690.field_1724;
			class_1496 lv3 = (class_1496)lv;
			class_1277 lv4 = new class_1277(arg.method_11434());
			class_1724 lv5 = new class_1724(arg.method_11432(), lv2.field_7514, lv4, lv3);
			lv2.field_7512 = lv5;
			this.field_3690.method_1507(new class_491(lv5, lv2.field_7514, lv3));
		}
	}

	@Override
	public void method_17587(class_3944 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_3929.method_17541(arg.method_17593(), this.field_3690, arg.method_17592(), arg.method_17594());
	}

	@Override
	public void method_11109(class_2653 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1657 lv = this.field_3690.field_1724;
		class_1799 lv2 = arg.method_11449();
		int i = arg.method_11450();
		this.field_3690.method_1577().method_4906(lv2);
		if (arg.method_11452() == -1) {
			if (!(this.field_3690.field_1755 instanceof class_481)) {
				lv.field_7514.method_7396(lv2);
			}
		} else if (arg.method_11452() == -2) {
			lv.field_7514.method_5447(i, lv2);
		} else {
			boolean bl = false;
			if (this.field_3690.field_1755 instanceof class_481) {
				class_481 lv3 = (class_481)this.field_3690.field_1755;
				bl = lv3.method_2469() != class_1761.field_7918.method_7741();
			}

			if (arg.method_11452() == 0 && arg.method_11450() >= 36 && i < 45) {
				if (!lv2.method_7960()) {
					class_1799 lv4 = lv.field_7498.method_7611(i).method_7677();
					if (lv4.method_7960() || lv4.method_7947() < lv2.method_7947()) {
						lv2.method_7912(5);
					}
				}

				lv.field_7498.method_7619(i, lv2);
			} else if (arg.method_11452() == lv.field_7512.field_7763 && (arg.method_11452() != 0 || !bl)) {
				lv.field_7512.method_7619(i, lv2);
			}
		}
	}

	@Override
	public void method_11123(class_2644 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1703 lv = null;
		class_1657 lv2 = this.field_3690.field_1724;
		if (arg.method_11425() == 0) {
			lv = lv2.field_7498;
		} else if (arg.method_11425() == lv2.field_7512.field_7763) {
			lv = lv2.field_7512;
		}

		if (lv != null && !arg.method_11426()) {
			this.method_2883(new class_2809(arg.method_11425(), arg.method_11423(), true));
		}
	}

	@Override
	public void method_11153(class_2649 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1657 lv = this.field_3690.field_1724;
		if (arg.method_11440() == 0) {
			lv.field_7498.method_7610(arg.method_11441());
		} else if (arg.method_11440() == lv.field_7512.field_7763) {
			lv.field_7512.method_7610(arg.method_11441());
		}
	}

	@Override
	public void method_11108(class_2693 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_2586 lv = this.field_3699.method_8321(arg.method_11677());
		if (!(lv instanceof class_2625)) {
			lv = new class_2625();
			lv.method_11009(this.field_3699);
			lv.method_10998(arg.method_11677());
		}

		this.field_3690.field_1724.method_7311((class_2625)lv);
	}

	@Override
	public void method_11094(class_2622 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		if (this.field_3690.field_1687.method_8591(arg.method_11293())) {
			class_2586 lv = this.field_3690.field_1687.method_8321(arg.method_11293());
			int i = arg.method_11291();
			boolean bl = i == 2 && lv instanceof class_2593;
			if (i == 1 && lv instanceof class_2636
				|| bl
				|| i == 3 && lv instanceof class_2580
				|| i == 4 && lv instanceof class_2631
				|| i == 6 && lv instanceof class_2573
				|| i == 7 && lv instanceof class_2633
				|| i == 8 && lv instanceof class_2643
				|| i == 9 && lv instanceof class_2625
				|| i == 11 && lv instanceof class_2587
				|| i == 5 && lv instanceof class_2597
				|| i == 12 && lv instanceof class_3751
				|| i == 13 && lv instanceof class_3924) {
				lv.method_11014(arg.method_11290());
			}

			if (bl && this.field_3690.field_1755 instanceof class_477) {
				((class_477)this.field_3690.field_1755).method_2457();
			}
		}
	}

	@Override
	public void method_11131(class_2651 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1657 lv = this.field_3690.field_1724;
		if (lv.field_7512 != null && lv.field_7512.field_7763 == arg.method_11448()) {
			lv.field_7512.method_7606(arg.method_11445(), arg.method_11446());
		}
	}

	@Override
	public void method_11151(class_2744 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = this.field_3699.method_8469(arg.method_11820());
		if (lv != null) {
			lv.method_5673(arg.method_11821(), arg.method_11822());
		}
	}

	@Override
	public void method_11102(class_2645 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3690.field_1724.method_3137();
	}

	@Override
	public void method_11158(class_2623 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3690.field_1687.method_8427(arg.method_11298(), arg.method_11295(), arg.method_11294(), arg.method_11296());
	}

	@Override
	public void method_11116(class_2620 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3690.field_1687.method_8517(arg.method_11280(), arg.method_11277(), arg.method_11278());
	}

	@Override
	public void method_11085(class_2668 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1657 lv = this.field_3690.field_1724;
		int i = arg.method_11491();
		float f = arg.method_11492();
		int j = class_3532.method_15375(f + 0.5F);
		if (i >= 0 && i < class_2668.field_12200.length && class_2668.field_12200[i] != null) {
			lv.method_7353(new class_2588(class_2668.field_12200[i]), false);
		}

		if (i == 1) {
			this.field_3699.method_8401().method_157(true);
			this.field_3699.method_8519(0.0F);
		} else if (i == 2) {
			this.field_3699.method_8401().method_157(false);
			this.field_3699.method_8519(1.0F);
		} else if (i == 3) {
			this.field_3690.field_1761.method_2907(class_1934.method_8384(j));
		} else if (i == 4) {
			if (j == 0) {
				this.field_3690.field_1724.field_3944.method_2883(new class_2799(class_2799.class_2800.field_12774));
				this.field_3690.method_1507(new class_434());
			} else if (j == 1) {
				this.field_3690
					.method_1507(new class_445(true, () -> this.field_3690.field_1724.field_3944.method_2883(new class_2799(class_2799.class_2800.field_12774))));
			}
		} else if (i == 5) {
			class_315 lv2 = this.field_3690.field_1690;
			if (f == 0.0F) {
				this.field_3690.method_1507(new class_417());
			} else if (f == 101.0F) {
				this.field_3690
					.field_1705
					.method_1743()
					.method_1812(
						new class_2588(
							"demo.help.movement", lv2.field_1894.method_16007(), lv2.field_1913.method_16007(), lv2.field_1881.method_16007(), lv2.field_1849.method_16007()
						)
					);
			} else if (f == 102.0F) {
				this.field_3690.field_1705.method_1743().method_1812(new class_2588("demo.help.jump", lv2.field_1903.method_16007()));
			} else if (f == 103.0F) {
				this.field_3690.field_1705.method_1743().method_1812(new class_2588("demo.help.inventory", lv2.field_1822.method_16007()));
			} else if (f == 104.0F) {
				this.field_3690.field_1705.method_1743().method_1812(new class_2588("demo.day.6", lv2.field_1835.method_16007()));
			}
		} else if (i == 6) {
			this.field_3699
				.method_8465(lv, lv.field_5987, lv.field_6010 + (double)lv.method_5751(), lv.field_6035, class_3417.field_15224, class_3419.field_15248, 0.18F, 0.45F);
		} else if (i == 7) {
			this.field_3699.method_8519(f);
		} else if (i == 8) {
			this.field_3699.method_8496(f);
		} else if (i == 9) {
			this.field_3699.method_8465(lv, lv.field_5987, lv.field_6010, lv.field_6035, class_3417.field_14848, class_3419.field_15254, 1.0F, 1.0F);
		} else if (i == 10) {
			this.field_3699.method_8406(class_2398.field_11250, lv.field_5987, lv.field_6010, lv.field_6035, 0.0, 0.0, 0.0);
			this.field_3699.method_8465(lv, lv.field_5987, lv.field_6010, lv.field_6035, class_3417.field_15203, class_3419.field_15251, 1.0F, 1.0F);
		} else if (i == 11) {
			this.field_3690.method_1507(new class_4281());
		}
	}

	@Override
	public void method_11088(class_2683 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_330 lv = this.field_3690.field_1773.method_3194();
		String string = class_1806.method_17440(arg.method_11644());
		class_22 lv2 = this.field_3690.field_1687.method_17891(string);
		if (lv2 == null) {
			lv2 = new class_22(string);
			if (lv.method_1768(string) != null) {
				class_22 lv3 = lv.method_1772(lv.method_1768(string));
				if (lv3 != null) {
					lv2 = lv3;
				}
			}

			this.field_3690.field_1687.method_17890(lv2);
		}

		arg.method_11642(lv2);
		lv.method_1769(lv2);
	}

	@Override
	public void method_11098(class_2673 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		if (arg.method_11533()) {
			this.field_3690.field_1687.method_8474(arg.method_11532(), arg.method_11531(), arg.method_11534());
		} else {
			this.field_3690.field_1687.method_8535(arg.method_11532(), arg.method_11531(), arg.method_11534());
		}
	}

	@Override
	public void method_11130(class_2779 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3700.method_2861(arg);
	}

	@Override
	public void method_11161(class_2729 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_2960 lv = arg.method_11793();
		if (lv == null) {
			this.field_3700.method_2864(null, false);
		} else {
			class_161 lv2 = this.field_3700.method_2863().method_716(lv);
			this.field_3700.method_2864(lv2, false);
		}
	}

	@Override
	public void method_11145(class_2641 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3696 = new CommandDispatcher<>(arg.method_11403());
	}

	@Override
	public void method_11082(class_2770 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3690.method_1483().method_4875(arg.method_11904(), arg.method_11903());
	}

	@Override
	public void method_11081(class_2639 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3691.method_2931(arg.method_11399(), arg.method_11397());
	}

	@Override
	public void method_11106(class_2788 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3688.method_8133();

		for (class_1860<?> lv : arg.method_11998()) {
			this.field_3688.method_8125(lv);
		}

		class_1123<class_516> lv2 = this.field_3690.method_1484(class_1124.field_5496);
		lv2.method_4797();
		class_299 lv3 = this.field_3690.field_1724.method_3130();
		lv3.method_1401();
		lv3.method_1393().forEach(lv2::method_4798);
		lv2.method_4799();
	}

	@Override
	public void method_11092(class_2707 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_243 lv = arg.method_11732(this.field_3699);
		if (lv != null) {
			this.field_3690.field_1724.method_5702(arg.method_11730(), lv);
		}
	}

	@Override
	public void method_11127(class_2774 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		if (!this.field_3692.method_1404(arg.method_11910(), arg.method_11911())) {
			field_3695.debug("Got unhandled response to tag query {}", arg.method_11910());
		}
	}

	@Override
	public void method_11129(class_2617 arg) {
		class_2600.method_11074(arg, this, this.field_3690);

		for (Entry<class_3445<?>, Integer> entry : arg.method_11273().entrySet()) {
			class_3445<?> lv = (class_3445<?>)entry.getKey();
			int i = (Integer)entry.getValue();
			this.field_3690.field_1724.method_3143().method_15023(this.field_3690.field_1724, lv, i);
		}

		if (this.field_3690.field_1755 instanceof class_452) {
			((class_452)this.field_3690.field_1755).method_2300();
		}
	}

	@Override
	public void method_11115(class_2713 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_299 lv = this.field_3690.field_1724.method_3130();
		lv.method_14884(arg.method_11754());
		lv.method_14889(arg.method_11752());
		lv.method_14882(arg.method_11755());
		lv.method_14888(arg.method_11756());
		class_2713.class_2714 lv2 = arg.method_11751();
		switch (lv2) {
			case field_12417:
				for (class_2960 lv3 : arg.method_11750()) {
					this.field_3688.method_8130(lv3).ifPresent(lv::method_14893);
				}
				break;
			case field_12416:
				for (class_2960 lv3 : arg.method_11750()) {
					this.field_3688.method_8130(lv3).ifPresent(lv::method_14876);
				}

				for (class_2960 lv3 : arg.method_11757()) {
					this.field_3688.method_8130(lv3).ifPresent(lv::method_14885);
				}
				break;
			case field_12415:
				for (class_2960 lv3 : arg.method_11750()) {
					this.field_3688.method_8130(lv3).ifPresent(arg2 -> {
						lv.method_14876(arg2);
						lv.method_14885(arg2);
						class_366.method_1985(this.field_3690.method_1566(), arg2);
					});
				}
		}

		lv.method_1393().forEach(arg2 -> arg2.method_2647(lv));
		if (this.field_3690.field_1755 instanceof class_518) {
			((class_518)this.field_3690.field_1755).method_16891();
		}
	}

	@Override
	public void method_11084(class_2783 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = this.field_3699.method_8469(arg.method_11943());
		if (lv instanceof class_1309) {
			class_1291 lv2 = class_1291.method_5569(arg.method_11946());
			if (lv2 != null) {
				class_1293 lv3 = new class_1293(lv2, arg.method_11944(), arg.method_11945(), arg.method_11950(), arg.method_11949(), arg.method_11942());
				lv3.method_5580(arg.method_11947());
				((class_1309)lv).method_6092(lv3);
			}
		}
	}

	@Override
	public void method_11126(class_2790 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3694 = arg.method_12000();
		if (!this.field_3689.method_10756()) {
			class_3481.method_15070(this.field_3694.method_15202());
			class_3489.method_15103(this.field_3694.method_15201());
			class_3486.method_15096(this.field_3694.method_15205());
			class_3483.method_15078(this.field_3694.method_15203());
		}

		this.field_3690.method_1484(class_1124.field_5494).method_4799();
	}

	@Override
	public void method_11133(class_2698 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		if (arg.field_12347 == class_2698.class_2699.field_12350) {
			class_1297 lv = this.field_3699.method_8469(arg.field_12349);
			if (lv == this.field_3690.field_1724) {
				this.field_3690.method_1507(new class_418(arg.field_12346, this.field_3699.method_8401().method_152()));
			}
		}
	}

	@Override
	public void method_11140(class_2632 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3690.field_1687.method_8401().method_208(arg.method_11342());
		this.field_3690.field_1687.method_8401().method_186(arg.method_11340());
	}

	@Override
	public void method_11111(class_2734 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = arg.method_11800(this.field_3699);
		if (lv != null) {
			this.field_3690.method_1504(lv);
		}
	}

	@Override
	public void method_11096(class_2730 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		arg.method_11795(this.field_3699.method_8621());
	}

	@Override
	public void method_11103(class_2762 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_2762.class_2763 lv = arg.method_11878();
		String string = null;
		String string2 = null;
		String string3 = arg.method_11877() != null ? arg.method_11877().method_10863() : "";
		switch (lv) {
			case field_12630:
				string = string3;
				break;
			case field_12632:
				string2 = string3;
				break;
			case field_12627:
				this.field_3690.field_1705.method_1764(string3, false);
				return;
			case field_12628:
				this.field_3690.field_1705.method_1763("", "", -1, -1, -1);
				this.field_3690.field_1705.method_1742();
				return;
		}

		this.field_3690.field_1705.method_1763(string, string2, arg.method_11874(), arg.method_11876(), arg.method_11875());
	}

	@Override
	public void method_11105(class_2772 arg) {
		this.field_3690.field_1705.method_1750().method_1925(arg.method_11908().method_10863().isEmpty() ? null : arg.method_11908());
		this.field_3690.field_1705.method_1750().method_1924(arg.method_11906().method_10863().isEmpty() ? null : arg.method_11906());
	}

	@Override
	public void method_11119(class_2718 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = arg.method_11767(this.field_3699);
		if (lv instanceof class_1309) {
			((class_1309)lv).method_6111(arg.method_11768());
		}
	}

	@Override
	public void method_11113(class_2703 arg) {
		class_2600.method_11074(arg, this, this.field_3690);

		for (class_2703.class_2705 lv : arg.method_11722()) {
			if (arg.method_11723() == class_2703.class_2704.field_12376) {
				this.field_3693.remove(lv.method_11726().getId());
			} else {
				class_640 lv2 = (class_640)this.field_3693.get(lv.method_11726().getId());
				if (arg.method_11723() == class_2703.class_2704.field_12372) {
					lv2 = new class_640(lv);
					this.field_3693.put(lv2.method_2966().getId(), lv2);
				}

				if (lv2 != null) {
					switch (arg.method_11723()) {
						case field_12372:
							lv2.method_2963(lv.method_11725());
							lv2.method_2970(lv.method_11727());
							lv2.method_2962(lv.method_11724());
							break;
						case field_12375:
							lv2.method_2963(lv.method_11725());
							break;
						case field_12371:
							lv2.method_2970(lv.method_11727());
							break;
						case field_12374:
							lv2.method_2962(lv.method_11724());
					}
				}
			}
		}
	}

	@Override
	public void method_11147(class_2670 arg) {
		this.method_2883(new class_2827(arg.method_11517()));
	}

	@Override
	public void method_11154(class_2696 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1657 lv = this.field_3690.field_1724;
		lv.field_7503.field_7479 = arg.method_11698();
		lv.field_7503.field_7477 = arg.method_11696();
		lv.field_7503.field_7480 = arg.method_11695();
		lv.field_7503.field_7478 = arg.method_11699();
		lv.field_7503.method_7248(arg.method_11690());
		lv.field_7503.method_7250(arg.method_11691());
	}

	@Override
	public void method_11146(class_2767 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3690
			.field_1687
			.method_8465(
				this.field_3690.field_1724,
				arg.method_11890(),
				arg.method_11889(),
				arg.method_11893(),
				arg.method_11894(),
				arg.method_11888(),
				arg.method_11891(),
				arg.method_11892()
			);
	}

	@Override
	public void method_11125(class_2765 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = this.field_3699.method_8469(arg.method_11883());
		if (lv != null) {
			this.field_3690.field_1687.method_8449(this.field_3690.field_1724, lv, arg.method_11882(), arg.method_11881(), arg.method_11885(), arg.method_11880());
		}
	}

	@Override
	public void method_11104(class_2660 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3690
			.method_1483()
			.method_4873(
				new class_1109(
					arg.method_11460(),
					arg.method_11459(),
					arg.method_11463(),
					arg.method_11464(),
					false,
					0,
					class_1113.class_1114.field_5476,
					(float)arg.method_11462(),
					(float)arg.method_11461(),
					(float)arg.method_11465(),
					false
				)
			);
	}

	@Override
	public void method_11141(class_2720 arg) {
		String string = arg.method_11772();
		String string2 = arg.method_11773();
		if (this.method_2888(string)) {
			if (string.startsWith("level://")) {
				try {
					String string3 = URLDecoder.decode(string.substring("level://".length()), StandardCharsets.UTF_8.toString());
					File file = new File(this.field_3690.field_1697, "saves");
					File file2 = new File(file, string3);
					if (file2.isFile()) {
						this.method_2873(class_2856.class_2857.field_13016);
						CompletableFuture<?> completableFuture = this.field_3690.method_1516().method_4638(file2);
						this.method_2885(completableFuture);
						return;
					}
				} catch (UnsupportedEncodingException var8) {
				}

				this.method_2873(class_2856.class_2857.field_13015);
			} else {
				class_642 lv = this.field_3690.method_1558();
				if (lv != null && lv.method_2990() == class_642.class_643.field_3768) {
					this.method_2873(class_2856.class_2857.field_13016);
					this.method_2885(this.field_3690.method_1516().method_4640(string, string2));
				} else if (lv != null && lv.method_2990() != class_642.class_643.field_3767) {
					this.method_2873(class_2856.class_2857.field_13018);
				} else {
					this.field_3690.execute(() -> this.field_3690.method_1507(new class_410((bl, i) -> {
							this.field_3690 = class_310.method_1551();
							class_642 lvx = this.field_3690.method_1558();
							if (bl) {
								if (lvx != null) {
									lvx.method_2995(class_642.class_643.field_3768);
								}

								this.method_2873(class_2856.class_2857.field_13016);
								this.method_2885(this.field_3690.method_1516().method_4640(string, string2));
							} else {
								if (lvx != null) {
									lvx.method_2995(class_642.class_643.field_3764);
								}

								this.method_2873(class_2856.class_2857.field_13018);
							}

							class_641.method_2986(lvx);
							this.field_3690.method_1507(null);
						}, new class_2588("multiplayer.texturePrompt.line1"), new class_2588("multiplayer.texturePrompt.line2"), 0)));
				}
			}
		}
	}

	private boolean method_2888(String string) {
		try {
			URI uRI = new URI(string);
			String string2 = uRI.getScheme();
			boolean bl = "level".equals(string2);
			if (!"http".equals(string2) && !"https".equals(string2) && !bl) {
				throw new URISyntaxException(string, "Wrong protocol");
			} else if (!bl || !string.contains("..") && string.endsWith("/resources.zip")) {
				return true;
			} else {
				throw new URISyntaxException(string, "Invalid levelstorage resourcepack path");
			}
		} catch (URISyntaxException var5) {
			this.method_2873(class_2856.class_2857.field_13015);
			return false;
		}
	}

	private void method_2885(CompletableFuture<?> completableFuture) {
		completableFuture.thenRun(() -> this.method_2873(class_2856.class_2857.field_13017)).exceptionally(throwable -> {
			this.method_2873(class_2856.class_2857.field_13015);
			return null;
		});
	}

	private void method_2873(class_2856.class_2857 arg) {
		this.field_3689.method_10743(new class_2856(arg));
	}

	@Override
	public void method_11078(class_2629 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_3690.field_1705.method_1740().method_1795(arg);
	}

	@Override
	public void method_11087(class_2656 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		if (arg.method_11454() == 0) {
			this.field_3690.field_1724.method_7357().method_7900(arg.method_11453());
		} else {
			this.field_3690.field_1724.method_7357().method_7906(arg.method_11453(), arg.method_11454());
		}
	}

	@Override
	public void method_11134(class_2692 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = this.field_3690.field_1724.method_5668();
		if (lv != this.field_3690.field_1724 && lv.method_5787()) {
			lv.method_5641(arg.method_11673(), arg.method_11674(), arg.method_11670(), arg.method_11675(), arg.method_11671());
			this.field_3689.method_10743(new class_2833(lv));
		}
	}

	@Override
	public void method_17186(class_3895 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1799 lv = this.field_3690.field_1724.method_5998(arg.method_17188());
		if (lv.method_7909() == class_1802.field_8360) {
			this.field_3690.method_1507(new class_3872(new class_3872.class_3933(lv)));
		}
	}

	@Override
	public void method_11152(class_2658 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_2960 lv = arg.method_11456();
		class_2540 lv2 = null;

		try {
			lv2 = arg.method_11458();
			if (class_2658.field_12158.equals(lv)) {
				this.field_3690.field_1724.method_3146(lv2.method_10800(32767));
			} else if (class_2658.field_12161.equals(lv)) {
				int i = lv2.readInt();
				float f = lv2.readFloat();
				class_11 lv3 = class_11.method_34(lv2);
				this.field_3690.field_1709.field_4523.method_3869(i, lv3, f);
			} else if (class_2658.field_12157.equals(lv)) {
				long l = lv2.method_10792();
				class_2338 lv4 = lv2.method_10811();
				((class_869)this.field_3690.field_1709.field_4535).method_3870(l, lv4);
			} else if (class_2658.field_12156.equals(lv)) {
				class_2338 lv5 = lv2.method_10811();
				int j = lv2.readInt();
				List<class_2338> list = Lists.<class_2338>newArrayList();
				List<Float> list2 = Lists.<Float>newArrayList();

				for (int k = 0; k < j; k++) {
					list.add(lv2.method_10811());
					list2.add(lv2.readFloat());
				}

				this.field_3690.field_1709.field_4529.method_3704(lv5, list, list2);
			} else if (class_2658.field_12163.equals(lv)) {
				int i = lv2.readInt();
				class_3341 lv6 = new class_3341(lv2.readInt(), lv2.readInt(), lv2.readInt(), lv2.readInt(), lv2.readInt(), lv2.readInt());
				int m = lv2.readInt();
				List<class_3341> list2 = Lists.<class_3341>newArrayList();
				List<Boolean> list3 = Lists.<Boolean>newArrayList();

				for (int n = 0; n < m; n++) {
					list2.add(new class_3341(lv2.readInt(), lv2.readInt(), lv2.readInt(), lv2.readInt(), lv2.readInt(), lv2.readInt()));
					list3.add(lv2.readBoolean());
				}

				this.field_3690.field_1709.field_4539.method_3871(lv6, list2, list3, i);
			} else if (class_2658.field_12164.equals(lv)) {
				((class_873)this.field_3690.field_1709.field_4537)
					.method_3872(lv2.method_10811(), lv2.readFloat(), lv2.readFloat(), lv2.readFloat(), lv2.readFloat(), lv2.readFloat());
			} else if (class_2658.field_18960.equals(lv)) {
				int i = lv2.readInt();

				for (int j = 0; j < i; j++) {
					this.field_3690.field_1709.field_18777.method_19433(lv2.method_19456());
				}

				int j = lv2.readInt();

				for (int m = 0; m < j; m++) {
					this.field_3690.field_1709.field_18777.method_19435(lv2.method_19456());
				}
			} else if (class_2658.field_18958.equals(lv)) {
				class_2338 lv5 = lv2.method_10811();
				String string = lv2.method_19772();
				int m = lv2.readInt();
				class_4207.class_4233 lv7 = new class_4207.class_4233(lv5, string, m);
				this.field_3690.field_1709.field_18777.method_19701(lv7);
			} else if (class_2658.field_18959.equals(lv)) {
				class_2338 lv5 = lv2.method_10811();
				this.field_3690.field_1709.field_18777.method_19434(lv5);
			} else if (class_2658.field_18957.equals(lv)) {
				class_2338 lv5 = lv2.method_10811();
				int j = lv2.readInt();
				this.field_3690.field_1709.field_18777.method_19702(lv5, j);
			} else if (class_2658.field_18799.equals(lv)) {
				int i = lv2.readInt();
				class_2338 lv8 = lv2.method_10811();
				int m = lv2.readInt();
				int o = lv2.readInt();
				List<class_4205.class_4206> list3 = Lists.<class_4205.class_4206>newArrayList();

				for (int n = 0; n < o; n++) {
					int p = lv2.readInt();
					boolean bl = lv2.readBoolean();
					String string2 = lv2.method_10800(255);
					list3.add(new class_4205.class_4206(lv8, p, string2, bl));
				}

				this.field_3690.field_1709.field_18778.method_19430(m, list3);
			} else if (class_2658.field_18800.equals(lv)) {
				double d = lv2.readDouble();
				double e = lv2.readDouble();
				double g = lv2.readDouble();
				class_2374 lv9 = new class_2376(d, e, g);
				UUID uUID = lv2.method_10790();
				int q = lv2.readInt();
				String string3 = lv2.method_19772();
				class_4207.class_4232 lv10 = new class_4207.class_4232(uUID, q, string3, lv9);
				int r = lv2.readInt();

				for (int s = 0; s < r; s++) {
					String string4 = lv2.method_19772();
					lv10.field_18927.add(string4);
				}

				int s = lv2.readInt();

				for (int t = 0; t < s; t++) {
					String string5 = lv2.method_19772();
					lv10.field_18928.add(string5);
				}

				int t = lv2.readInt();

				for (int u = 0; u < t; u++) {
					String string6 = lv2.method_19772();
					lv10.field_18929.add(string6);
				}

				int u = lv2.readInt();

				for (int v = 0; v < u; v++) {
					class_2338 lv11 = lv2.method_10811();
					lv10.field_18930.add(lv11);
				}

				this.field_3690.field_1709.field_18777.method_19432(lv10);
			} else {
				field_3695.warn("Unknown custom packed identifier: {}", lv);
			}
		} finally {
			if (lv2 != null) {
				lv2.release();
			}
		}
	}

	@Override
	public void method_11144(class_2751 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_269 lv = this.field_3699.method_8428();
		String string = arg.method_11835();
		if (arg.method_11837() == 0) {
			lv.method_1168(string, class_274.field_1468, arg.method_11836(), arg.method_11839());
		} else if (lv.method_1181(string)) {
			class_266 lv2 = lv.method_1170(string);
			if (arg.method_11837() == 1) {
				lv.method_1194(lv2);
			} else if (arg.method_11837() == 2) {
				lv2.method_1115(arg.method_11839());
				lv2.method_1121(arg.method_11836());
			}
		}
	}

	@Override
	public void method_11118(class_2757 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_269 lv = this.field_3699.method_8428();
		String string = arg.method_11864();
		switch (arg.method_11863()) {
			case field_13431:
				class_266 lv2 = lv.method_1165(string);
				class_267 lv3 = lv.method_1180(arg.method_11862(), lv2);
				lv3.method_1128(arg.method_11865());
				break;
			case field_13430:
				lv.method_1155(arg.method_11862(), lv.method_1170(string));
		}
	}

	@Override
	public void method_11159(class_2736 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_269 lv = this.field_3699.method_8428();
		String string = arg.method_11804();
		class_266 lv2 = string == null ? null : lv.method_1165(string);
		lv.method_1158(arg.method_11806(), lv2);
	}

	@Override
	public void method_11099(class_2755 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_269 lv = this.field_3699.method_8428();
		class_268 lv2;
		if (arg.method_11853() == 0) {
			lv2 = lv.method_1171(arg.method_11855());
		} else {
			lv2 = lv.method_1153(arg.method_11855());
		}

		if (arg.method_11853() == 0 || arg.method_11853() == 2) {
			lv2.method_1137(arg.method_11859());
			lv2.method_1141(arg.method_11858());
			lv2.method_1146(arg.method_11852());
			class_270.class_272 lv3 = class_270.class_272.method_1213(arg.method_11851());
			if (lv3 != null) {
				lv2.method_1149(lv3);
			}

			class_270.class_271 lv4 = class_270.class_271.method_1210(arg.method_11861());
			if (lv4 != null) {
				lv2.method_1145(lv4);
			}

			lv2.method_1138(arg.method_11856());
			lv2.method_1139(arg.method_11854());
		}

		if (arg.method_11853() == 0 || arg.method_11853() == 3) {
			for (String string : arg.method_11857()) {
				lv.method_1172(string, lv2);
			}
		}

		if (arg.method_11853() == 4) {
			for (String string : arg.method_11857()) {
				lv.method_1157(string, lv2);
			}
		}

		if (arg.method_11853() == 1) {
			lv.method_1191(lv2);
		}
	}

	@Override
	public void method_11077(class_2675 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		if (arg.method_11545() == 0) {
			double d = (double)(arg.method_11543() * arg.method_11548());
			double e = (double)(arg.method_11543() * arg.method_11549());
			double f = (double)(arg.method_11543() * arg.method_11550());

			try {
				this.field_3699.method_8466(arg.method_11551(), arg.method_11552(), arg.method_11544(), arg.method_11547(), arg.method_11546(), d, e, f);
			} catch (Throwable var17) {
				field_3695.warn("Could not spawn particle effect {}", arg.method_11551());
			}
		} else {
			for (int i = 0; i < arg.method_11545(); i++) {
				double g = this.field_3687.nextGaussian() * (double)arg.method_11548();
				double h = this.field_3687.nextGaussian() * (double)arg.method_11549();
				double j = this.field_3687.nextGaussian() * (double)arg.method_11550();
				double k = this.field_3687.nextGaussian() * (double)arg.method_11543();
				double l = this.field_3687.nextGaussian() * (double)arg.method_11543();
				double m = this.field_3687.nextGaussian() * (double)arg.method_11543();

				try {
					this.field_3699.method_8466(arg.method_11551(), arg.method_11552(), arg.method_11544() + g, arg.method_11547() + h, arg.method_11546() + j, k, l, m);
				} catch (Throwable var16) {
					field_3695.warn("Could not spawn particle effect {}", arg.method_11551());
					return;
				}
			}
		}
	}

	@Override
	public void method_11149(class_2781 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1297 lv = this.field_3699.method_8469(arg.method_11937());
		if (lv != null) {
			if (!(lv instanceof class_1309)) {
				throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + lv + ")");
			} else {
				class_1325 lv2 = ((class_1309)lv).method_6127();

				for (class_2781.class_2782 lv3 : arg.method_11938()) {
					class_1324 lv4 = lv2.method_6207(lv3.method_11940());
					if (lv4 == null) {
						lv4 = lv2.method_6208(new class_1329(null, lv3.method_11940(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE));
					}

					lv4.method_6192(lv3.method_11941());
					lv4.method_6203();

					for (class_1322 lv5 : lv3.method_11939()) {
						lv4.method_6197(lv5);
					}
				}
			}
		}
	}

	@Override
	public void method_11090(class_2695 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1703 lv = this.field_3690.field_1724.field_7512;
		if (lv.field_7763 == arg.method_11685() && lv.method_7622(this.field_3690.field_1724)) {
			this.field_3688.method_8130(arg.method_11684()).ifPresent(arg2 -> {
				if (this.field_3690.field_1755 instanceof class_518) {
					class_507 lvx = ((class_518)this.field_3690.field_1755).method_2659();
					lvx.method_2596(arg2, lv.field_7761);
				}
			});
		}
	}

	@Override
	public void method_11143(class_2676 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		int i = arg.method_11558();
		int j = arg.method_11554();
		class_3568 lv = this.field_3699.method_2935().method_12130();
		int k = arg.method_11556();
		int l = arg.method_16124();
		Iterator<byte[]> iterator = arg.method_11555().iterator();
		this.method_2870(i, j, lv, class_1944.field_9284, k, l, iterator);
		int m = arg.method_11559();
		int n = arg.method_16125();
		Iterator<byte[]> iterator2 = arg.method_11557().iterator();
		this.method_2870(i, j, lv, class_1944.field_9282, m, n, iterator2);
	}

	@Override
	public void method_17586(class_3943 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		class_1703 lv = this.field_3690.field_1724.field_7512;
		if (arg.method_17589() == lv.field_7763 && lv instanceof class_1728) {
			((class_1728)lv).method_17437(arg.method_17590());
			((class_1728)lv).method_19255(arg.method_19459());
			((class_1728)lv).method_19257(arg.method_19458());
			((class_1728)lv).method_19253(arg.method_19460());
		}
	}

	@Override
	public void method_20203(class_4273 arg) {
		class_2600.method_11074(arg, this, this.field_3690);
		this.field_19144 = arg.method_20206();
		this.field_3699.method_2935().method_20180(arg.method_20206());
	}

	private void method_2870(int i, int j, class_3568 arg, class_1944 arg2, int k, int l, Iterator<byte[]> iterator) {
		for (int m = 0; m < 18; m++) {
			int n = -1 + m;
			boolean bl = (k & 1 << m) != 0;
			boolean bl2 = (l & 1 << m) != 0;
			if (bl || bl2) {
				arg.method_15558(arg2, class_4076.method_18676(i, n, j), bl ? new class_2804((byte[])((byte[])iterator.next()).clone()) : new class_2804());
				this.field_3699.method_18113(i, n, j);
			}
		}
	}

	public class_2535 method_2872() {
		return this.field_3689;
	}

	public Collection<class_640> method_2880() {
		return this.field_3693.values();
	}

	@Nullable
	public class_640 method_2871(UUID uUID) {
		return (class_640)this.field_3693.get(uUID);
	}

	@Nullable
	public class_640 method_2874(String string) {
		for (class_640 lv : this.field_3693.values()) {
			if (lv.method_2966().getName().equals(string)) {
				return lv;
			}
		}

		return null;
	}

	public GameProfile method_2879() {
		return this.field_3697;
	}

	public class_632 method_2869() {
		return this.field_3700;
	}

	public CommandDispatcher<class_2172> method_2886() {
		return this.field_3696;
	}

	public class_638 method_2890() {
		return this.field_3699;
	}

	public class_3505 method_2867() {
		return this.field_3694;
	}

	public class_300 method_2876() {
		return this.field_3692;
	}

	public UUID method_16690() {
		return this.field_16771;
	}
}
