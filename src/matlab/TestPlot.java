package matlab;


import drawplot2.*;

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

public class TestPlot {

    public static void draw(double[] inputY) {
        MWNumericArray x = null; // ���xֵ������
        MWNumericArray y = null; // ���yֵ������
        DrawPlot thePlot = null; // plotter���ʵ������MatLab����ʱ���½����ࣩ
        int n = inputY.length; // ��ͼ����

        try {
            // ����x��y��ֵ
            int[] dims = { 1, n };
            x = MWNumericArray.newInstance(dims, MWClassID.DOUBLE,
                    MWComplexity.REAL);
            y = MWNumericArray.newInstance(dims, MWClassID.DOUBLE,
                    MWComplexity.REAL);

            for (int i = 1; i <= n; i++) {
                x.set(i, i);
                y.set(i, inputY[i-1]);
            }

            // ��ʼ��plotter�Ķ���
            thePlot = new DrawPlot();
            thePlot.drawplot(x, y);
            thePlot.waitForFigures();
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }

        finally {
            // �ͷű�����Դ
            MWArray.disposeArray(x);
            MWArray.disposeArray(y);
            if (thePlot != null)
                thePlot.dispose();
        }
    }

}