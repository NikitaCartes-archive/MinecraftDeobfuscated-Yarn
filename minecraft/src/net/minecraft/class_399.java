package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_399 extends class_437 {
	private final RealmsScreen field_2340;
	private static final Logger field_2341 = LogManager.getLogger();

	public class_399(RealmsScreen realmsScreen) {
		this.field_2340 = realmsScreen;
	}

	public RealmsScreen method_2069() {
		return this.field_2340;
	}

	@Override
	public void method_2233(class_310 arg, int i, int j) {
		this.field_2340.init(arg, i, j);
		super.method_2233(arg, i, j);
	}

	@Override
	protected void method_2224() {
		this.field_2340.init();
		super.method_2224();
	}

	public void method_2075(String string, int i, int j, int k) {
		super.method_1789(this.field_2554, string, i, j, k);
	}

	public void method_2081(String string, int i, int j, int k, boolean bl) {
		if (bl) {
			super.method_1780(this.field_2554, string, i, j, k);
		} else {
			this.field_2554.method_1729(string, (float)i, (float)j, k);
		}
	}

	@Override
	public void method_1788(int i, int j, int k, int l, int m, int n) {
		this.field_2340.blit(i, j, k, l, m, n);
		super.method_1788(i, j, k, l, m, n);
	}

	@Override
	public void method_1782(int i, int j, int k, int l, int m, int n) {
		super.method_1782(i, j, k, l, m, n);
	}

	@Override
	public void method_2240() {
		super.method_2240();
	}

	@Override
	public boolean method_2222() {
		return super.method_2222();
	}

	@Override
	public void method_2236(int i) {
		super.method_2236(i);
	}

	@Override
	public void method_2214(int i, int j, float f) {
		this.field_2340.render(i, j, f);
	}

	@Override
	public void method_2218(class_1799 arg, int i, int j) {
		super.method_2218(arg, i, j);
	}

	@Override
	public void method_2215(String string, int i, int j) {
		super.method_2215(string, i, j);
	}

	@Override
	public void method_2211(List<String> list, int i, int j) {
		super.method_2211(list, i, j);
	}

	@Override
	public void method_2225() {
		this.field_2340.tick();
		super.method_2225();
	}

	public int method_2080() {
		return 9;
	}

	public int method_2076(String string) {
		return this.field_2554.method_1727(string);
	}

	public void method_2070(String string, int i, int j, int k) {
		this.field_2554.method_1720(string, (float)i, (float)j, k);
	}

	public List<String> method_2072(String string, int i) {
		return this.field_2554.method_1728(string, i);
	}

	public void method_2084() {
		this.field_2557.clear();
	}

	public void method_2073(RealmsGuiEventListener realmsGuiEventListener) {
		if (this.method_2083(realmsGuiEventListener) || !this.field_2557.add(realmsGuiEventListener.getProxy())) {
			field_2341.error("Tried to add the same widget multiple times: " + realmsGuiEventListener);
		}
	}

	public void method_2079(RealmsGuiEventListener realmsGuiEventListener) {
		if (!this.method_2083(realmsGuiEventListener) || !this.field_2557.remove(realmsGuiEventListener.getProxy())) {
			field_2341.error("Tried to add the same widget multiple times: " + realmsGuiEventListener);
		}
	}

	public boolean method_2083(RealmsGuiEventListener realmsGuiEventListener) {
		return this.field_2557.contains(realmsGuiEventListener.getProxy());
	}

	public void method_2077(RealmsButton realmsButton) {
		this.method_2219(realmsButton.getProxy());
	}

	public List<RealmsButton> method_2074() {
		List<RealmsButton> list = Lists.<RealmsButton>newArrayListWithExpectedSize(this.field_2564.size());

		for (class_339 lv : this.field_2564) {
			list.add(((class_398)lv).method_2064());
		}

		return list;
	}

	public void method_2071() {
		Set<class_364> set = Sets.<class_364>newHashSet(this.field_2564);
		this.field_2557.removeIf(set::contains);
		this.field_2564.clear();
	}

	public void method_2078(RealmsButton realmsButton) {
		this.field_2557.remove(realmsButton.getProxy());
		this.field_2564.remove(realmsButton.getProxy());
	}

	@Override
	public boolean method_16807(double d, double e, int i) {
		return this.field_2340.mouseClicked(d, e, i) ? true : method_2068(this, d, e, i);
	}

	@Override
	public boolean method_16804(double d, double e, int i) {
		return this.field_2340.mouseReleased(d, e, i);
	}

	@Override
	public boolean method_16801(double d, double e, int i, double f, double g) {
		return this.field_2340.mouseDragged(d, e, i, f, g) ? true : super.method_16801(d, e, i, f, g);
	}

	@Override
	public boolean method_16805(int i, int j, int k) {
		return this.field_2340.keyPressed(i, j, k) ? true : super.method_16805(i, j, k);
	}

	@Override
	public boolean method_16806(char c, int i) {
		return this.field_2340.charTyped(c, i) ? true : super.method_16806(c, i);
	}

	@Override
	public void confirmResult(boolean bl, int i) {
		this.field_2340.confirmResult(bl, i);
	}

	@Override
	public void method_2234() {
		this.field_2340.removed();
		super.method_2234();
	}

	public int method_2082(String string, int i, int j, int k, boolean bl) {
		return bl ? this.field_2554.method_1720(string, (float)i, (float)j, k) : this.field_2554.method_1729(string, (float)i, (float)j, k);
	}
}
