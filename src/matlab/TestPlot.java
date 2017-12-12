package matlab;


import drawplot2.*;

import com.mathworks.toolbox.javabuilder.MWArray;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWComplexity;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

public class TestPlot {

    public static void draw(double[] inputY) {
        MWNumericArray x = null; // 存放x值的数组
        MWNumericArray y = null; // 存放y值的数组
        DrawPlot thePlot = null; // plotter类的实例（在MatLab编译时，新建的类）
        int n = inputY.length; // 作图点数

        try {
            // 分配x、y的值
            int[] dims = { 1, n };
            x = MWNumericArray.newInstance(dims, MWClassID.DOUBLE,
                    MWComplexity.REAL);
            y = MWNumericArray.newInstance(dims, MWClassID.DOUBLE,
                    MWComplexity.REAL);

            for (int i = 1; i <= n; i++) {
                x.set(i, i);
                y.set(i, inputY[i-1]);
            }

            // 初始化plotter的对象
            thePlot = new DrawPlot();
            thePlot.drawplot(x, y);
            thePlot.waitForFigures();
        }

        catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }

        finally {
            // 释放本地资源
            MWArray.disposeArray(x);
            MWArray.disposeArray(y);
            if (thePlot != null)
                thePlot.dispose();
        }
    }

}