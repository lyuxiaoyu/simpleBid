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

// ����		=��	������
// ƽ���ֲ� 	=��	��̽�� 
// c									// ��̬�������ɵ���k
// k 									// exp(k)���𣬽����У�ԽСԽƫ����
// r									// ˥��ϵ����ȡ��С
// e < r * num 							// 1 - r + e / num < 1,���򣬲�����
// reward >= e * q / ((1 - e) * num)	// �˴�qΪ��ʼֵ��R�Դ��ڲ��׹���


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
