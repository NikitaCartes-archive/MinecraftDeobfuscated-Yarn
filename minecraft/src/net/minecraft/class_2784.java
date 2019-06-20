package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2784 {
	private final List<class_2780> field_12730 = Lists.<class_2780>newArrayList();
	private double field_12733 = 0.2;
	private double field_12731 = 5.0;
	private int field_12735 = 15;
	private int field_12734 = 5;
	private double field_12738;
	private double field_12737;
	private int field_12732 = 29999984;
	private class_2784.class_2785 field_12736 = new class_2784.class_2787(6.0E7);

	public boolean method_11952(class_2338 arg) {
		return (double)(arg.method_10263() + 1) > this.method_11976()
			&& (double)arg.method_10263() < this.method_11963()
			&& (double)(arg.method_10260() + 1) > this.method_11958()
			&& (double)arg.method_10260() < this.method_11977();
	}

	public boolean method_11951(class_1923 arg) {
		return (double)arg.method_8327() > this.method_11976()
			&& (double)arg.method_8326() < this.method_11963()
			&& (double)arg.method_8329() > this.method_11958()
			&& (double)arg.method_8328() < this.method_11977();
	}

	public boolean method_11966(class_238 arg) {
		return arg.field_1320 > this.method_11976()
			&& arg.field_1323 < this.method_11963()
			&& arg.field_1324 > this.method_11958()
			&& arg.field_1321 < this.method_11977();
	}

	public double method_11979(class_1297 arg) {
		return this.method_11961(arg.field_5987, arg.field_6035);
	}

	public class_265 method_17903() {
		return this.field_12736.method_17906();
	}

	public double method_11961(double d, double e) {
		double f = e - this.method_11958();
		double g = this.method_11977() - e;
		double h = d - this.method_11976();
		double i = this.method_11963() - d;
		double j = Math.min(h, i);
		j = Math.min(j, f);
		return Math.min(j, g);
	}

	@Environment(EnvType.CLIENT)
	public class_2789 method_11968() {
		return this.field_12736.method_11995();
	}

	public double method_11976() {
		return this.field_12736.method_11994();
	}

	public double method_11958() {
		return this.field_12736.method_11992();
	}

	public double method_11963() {
		return this.field_12736.method_11991();
	}

	public double method_11977() {
		return this.field_12736.method_11985();
	}

	public double method_11964() {
		return this.field_12738;
	}

	public double method_11980() {
		return this.field_12737;
	}

	public void method_11978(double d, double e) {
		this.field_12738 = d;
		this.field_12737 = e;
		this.field_12736.method_11990();

		for (class_2780 lv : this.method_11970()) {
			lv.method_11930(this, d, e);
		}
	}

	public double method_11965() {
		return this.field_12736.method_11984();
	}

	public long method_11962() {
		return this.field_12736.method_11993();
	}

	public double method_11954() {
		return this.field_12736.method_11988();
	}

	public void method_11969(double d) {
		this.field_12736 = new class_2784.class_2787(d);

		for (class_2780 lv : this.method_11970()) {
			lv.method_11934(this, d);
		}
	}

	public void method_11957(double d, double e, long l) {
		this.field_12736 = (class_2784.class_2785)(d == e ? new class_2784.class_2787(e) : new class_2784.class_2786(d, e, l));

		for (class_2780 lv : this.method_11970()) {
			lv.method_11931(this, d, e, l);
		}
	}

	protected List<class_2780> method_11970() {
		return Lists.<class_2780>newArrayList(this.field_12730);
	}

	public void method_11983(class_2780 arg) {
		this.field_12730.add(arg);
	}

	public void method_11973(int i) {
		this.field_12732 = i;
		this.field_12736.method_11989();
	}

	public int method_11959() {
		return this.field_12732;
	}

	public double method_11971() {
		return this.field_12731;
	}

	public void method_11981(double d) {
		this.field_12731 = d;

		for (class_2780 lv : this.method_11970()) {
			lv.method_11935(this, d);
		}
	}

	public double method_11953() {
		return this.field_12733;
	}

	public void method_11955(double d) {
		this.field_12733 = d;

		for (class_2780 lv : this.method_11970()) {
			lv.method_11929(this, d);
		}
	}

	@Environment(EnvType.CLIENT)
	public double method_11974() {
		return this.field_12736.method_11987();
	}

	public int method_11956() {
		return this.field_12735;
	}

	public void method_11975(int i) {
		this.field_12735 = i;

		for (class_2780 lv : this.method_11970()) {
			lv.method_11932(this, i);
		}
	}

	public int method_11972() {
		return this.field_12734;
	}

	public void method_11967(int i) {
		this.field_12734 = i;

		for (class_2780 lv : this.method_11970()) {
			lv.method_11933(this, i);
		}
	}

	public void method_11982() {
		this.field_12736 = this.field_12736.method_11986();
	}

	public void method_17904(class_31 arg) {
		arg.method_162(this.method_11965());
		arg.method_200(this.method_11964());
		arg.method_189(this.method_11980());
		arg.method_216(this.method_11971());
		arg.method_229(this.method_11953());
		arg.method_201(this.method_11972());
		arg.method_192(this.method_11956());
		arg.method_174(this.method_11954());
		arg.method_195(this.method_11962());
	}

	public void method_17905(class_31 arg) {
		this.method_11978(arg.method_204(), arg.method_139());
		this.method_11955(arg.method_202());
		this.method_11981(arg.method_178());
		this.method_11967(arg.method_227());
		this.method_11975(arg.method_161());
		if (arg.method_183() > 0L) {
			this.method_11957(arg.method_206(), arg.method_159(), arg.method_183());
		} else {
			this.method_11969(arg.method_206());
		}
	}

	interface class_2785 {
		double method_11994();

		double method_11991();

		double method_11992();

		double method_11985();

		double method_11984();

		@Environment(EnvType.CLIENT)
		double method_11987();

		long method_11993();

		double method_11988();

		@Environment(EnvType.CLIENT)
		class_2789 method_11995();

		void method_11989();

		void method_11990();

		class_2784.class_2785 method_11986();

		class_265 method_17906();
	}

	class class_2786 implements class_2784.class_2785 {
		private final double field_12740;
		private final double field_12739;
		private final long field_12742;
		private final long field_12741;
		private final double field_12744;

		private class_2786(double d, double e, long l) {
			this.field_12740 = d;
			this.field_12739 = e;
			this.field_12744 = (double)l;
			this.field_12741 = class_156.method_658();
			this.field_12742 = this.field_12741 + l;
		}

		@Override
		public double method_11994() {
			return Math.max(class_2784.this.method_11964() - this.method_11984() / 2.0, (double)(-class_2784.this.field_12732));
		}

		@Override
		public double method_11992() {
			return Math.max(class_2784.this.method_11980() - this.method_11984() / 2.0, (double)(-class_2784.this.field_12732));
		}

		@Override
		public double method_11991() {
			return Math.min(class_2784.this.method_11964() + this.method_11984() / 2.0, (double)class_2784.this.field_12732);
		}

		@Override
		public double method_11985() {
			return Math.min(class_2784.this.method_11980() + this.method_11984() / 2.0, (double)class_2784.this.field_12732);
		}

		@Override
		public double method_11984() {
			double d = (double)(class_156.method_658() - this.field_12741) / this.field_12744;
			return d < 1.0 ? class_3532.method_16436(d, this.field_12740, this.field_12739) : this.field_12739;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public double method_11987() {
			return Math.abs(this.field_12740 - this.field_12739) / (double)(this.field_12742 - this.field_12741);
		}

		@Override
		public long method_11993() {
			return this.field_12742 - class_156.method_658();
		}

		@Override
		public double method_11988() {
			return this.field_12739;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public class_2789 method_11995() {
			return this.field_12739 < this.field_12740 ? class_2789.field_12756 : class_2789.field_12754;
		}

		@Override
		public void method_11990() {
		}

		@Override
		public void method_11989() {
		}

		@Override
		public class_2784.class_2785 method_11986() {
			return (class_2784.class_2785)(this.method_11993() <= 0L ? class_2784.this.new class_2787(this.field_12739) : this);
		}

		@Override
		public class_265 method_17906() {
			return class_259.method_1072(
				class_259.field_17669,
				class_259.method_1081(
					Math.floor(this.method_11994()),
					Double.NEGATIVE_INFINITY,
					Math.floor(this.method_11992()),
					Math.ceil(this.method_11991()),
					Double.POSITIVE_INFINITY,
					Math.ceil(this.method_11985())
				),
				class_247.field_16886
			);
		}
	}

	class class_2787 implements class_2784.class_2785 {
		private final double field_12747;
		private double field_12746;
		private double field_12745;
		private double field_12750;
		private double field_12749;
		private class_265 field_17653;

		public class_2787(double d) {
			this.field_12747 = d;
			this.method_11996();
		}

		@Override
		public double method_11994() {
			return this.field_12746;
		}

		@Override
		public double method_11991() {
			return this.field_12750;
		}

		@Override
		public double method_11992() {
			return this.field_12745;
		}

		@Override
		public double method_11985() {
			return this.field_12749;
		}

		@Override
		public double method_11984() {
			return this.field_12747;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public class_2789 method_11995() {
			return class_2789.field_12753;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public double method_11987() {
			return 0.0;
		}

		@Override
		public long method_11993() {
			return 0L;
		}

		@Override
		public double method_11988() {
			return this.field_12747;
		}

		private void method_11996() {
			this.field_12746 = Math.max(class_2784.this.method_11964() - this.field_12747 / 2.0, (double)(-class_2784.this.field_12732));
			this.field_12745 = Math.max(class_2784.this.method_11980() - this.field_12747 / 2.0, (double)(-class_2784.this.field_12732));
			this.field_12750 = Math.min(class_2784.this.method_11964() + this.field_12747 / 2.0, (double)class_2784.this.field_12732);
			this.field_12749 = Math.min(class_2784.this.method_11980() + this.field_12747 / 2.0, (double)class_2784.this.field_12732);
			this.field_17653 = class_259.method_1072(
				class_259.field_17669,
				class_259.method_1081(
					Math.floor(this.method_11994()),
					Double.NEGATIVE_INFINITY,
					Math.floor(this.method_11992()),
					Math.ceil(this.method_11991()),
					Double.POSITIVE_INFINITY,
					Math.ceil(this.method_11985())
				),
				class_247.field_16886
			);
		}

		@Override
		public void method_11989() {
			this.method_11996();
		}

		@Override
		public void method_11990() {
			this.method_11996();
		}

		@Override
		public class_2784.class_2785 method_11986() {
			return this;
		}

		@Override
		public class_265 method_17906() {
			return this.field_17653;
		}
	}
}
