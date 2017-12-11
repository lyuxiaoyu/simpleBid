package bid;

import java.util.Arrays;
import java.util.Random;

public class ReAlgorithm {
protected double[] p;
protected double[] q;
protected double c;
protected int num;
protected double e = 0.55;    //0.97;
protected double r = 0.03;
protected double k = 1;
protected int choose;
Random rand;

// 收敛		=》	仅利用
// 平均分布 	=》	仅探索 
// c									// 动态调整，可调节k
// k 									// exp(k)级别，较敏感，越小越偏利用
// r									// 衰减系数，取较小
// e < r * num 							// 1 - r + e / num < 1,否则，不收敛
// reward >= e * q / ((1 - e) * num)	// 此处q为初始值，R略大于不易过大


	ReAlgorithm(int num){
		this.num = num;
		p = new double[num];
		q = new double[num];
		Arrays.fill(q, 1.0);
		rand = new Random();
	}
	
	ReAlgorithm(int num, double e, double r, double k) {
		this.e = e;
		this.r = r;
		this.k = k;
		this.num = num;
		p = new double[num];
		q = new double[num];
		Arrays.fill(q, 1.0);
		rand = new Random();
	}


	public void reStart() {
		Arrays.fill(q, 1.0);
		rand = new Random();
	}
	
	public int makeDecison(){
		double sumQ = 0;
		double sumExpQ = 0;
		double sumPartExpQ = 0;
		double x = rand.nextDouble();
		
		for (double eq: q) {
			sumQ += eq;
		}
//		System.out.println(sumQ);
		c = sumQ / num * k;
		
		for (double eq: q) {
			sumExpQ += Math.exp(eq / c);
		}
		
		choose = -1;
		for (double eq: q) {
			choose++;
			sumPartExpQ += Math.exp(eq / c);
			if (sumExpQ * x < sumPartExpQ) {	// p = Math.exp(q[i] / c) / sumPartExpQ;
				break;
			}
		}
		
		return choose;
	}
	
	
	public double[] showProbability() {
		double sumQ = 0;
		double sumExpQ = 0;
		
		for (double eq: q) {
			sumQ += eq;
		}
		c = sumQ / num * k;
		
		for (double eq: q) {
			sumExpQ += Math.exp(eq / c);
		}
		
		for (int i = 0; i < num; i++) {
			p[i] = Math.exp(q[i] / c) / sumExpQ;
			System.out.print(p[i] + " ");
			System.out.println(" ");
		}
		return p;
	}
	
	
	public void improveDecision(double reward){
		
		for (int i = 0; i < num; i++) {
			if (i != choose) {
				q[i] = (1 - r) * q[i] + e * q[i] / num;
			}
			else {
				q[i] = (1 - r) * q[i] + (1 - e) * reward;
			}
		}
	}
}
