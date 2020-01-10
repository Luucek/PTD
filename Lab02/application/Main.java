package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class Main extends Application {
    final private double f = 20;
    final private int fs = 700;
    final private int T = 2;
    final private double phi = -Math.PI;
    final private int N = T * fs;
    int closestPower = 0;

    // choose function to calculate
    final char userFunction = 'x';


    public void start(Stage stage) {

        // calculating closest, equal or smaller power to N
        for (int i = N; i >= 1; i--) {
            if ((i & (i - 1)) == 0) {
                closestPower = i;
                break;
            }
        }

        // calculating data
        Complex[] f = calculateData(closestPower, userFunction);

        /* TODO: DFT
        Complex[] k = new Complex[N];
        double[] cos = new double[N];
        double[] sin = new double[N];
        double real, imag;

        for (int i = 0; i < N; i++) {
            cos[i] = Math.cos((-i * 2 * Math.PI * i) / N);
            sin[i] = Math.sin((-i * 2 * Math.PI * i) / N);
            real = x(i) * -cos[i];
            imag = x(i) * sin[i];
            k[i] = new Complex(real, imag);
        }
        */

        // FFT
        Complex[] m = FFT.fft(f);

        // widmo
        Vector<Double> m2 = spectrum(m, closestPower);

        // populating the series with data
        List<XYChart.Data<Integer, Float>> seriesData = new ArrayList<>();
        for (int i = 0; i < m2.size(); i++) {
            seriesData.add(new XYChart.Data(i, m2.elementAt(i)));
        }

        // creating chart
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        XYChart.Series<Integer, Float> series = new XYChart.Series<>();
        series.getData().addAll(seriesData);
        series.setName(userFunction + "(t)");

        LineChart<Integer, Float> chart = new LineChart(xAxis, yAxis, FXCollections.observableArrayList(series));

        chart.setCreateSymbols(false);

        Scene scene = new Scene(chart, 800.0, 600.0);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Complex[] calculateData(int n, char function) {
        Complex[] data = new Complex[n];
        for (int i = 0; i < n; i++) {
            double t = (double) i / (double) fs;
            double result = getResult(function, t);

            data[i] = new Complex(result, 0);
        }
        return data;
    }

    private Double getResult(char function, double t) {
        double x = 0, y = 0, z = 0, v = 0, u = 0, b = 0;
        x = (0.45 * Math.cos((2.0 * Math.PI) * f * t + phi)) / (t + 0.1)
                + Math.cos((2.0 * Math.PI) * (f / 4.0) * t + (2.5 * phi));
        if (function == 'y')
            y = (2 * Math.pow(x, 2)) + (12 * Math.cos(t));

        if (function == 'z') {
            y = (2 * Math.pow(x, 2)) + (12 * Math.cos(t));
            z = (Math.sin(2 * Math.PI * 7 * t) * x) - (0.2 * Math.log10(Math.abs((y) + Math.PI)));
        }
        if (function == 'v') {
            y = (2 * Math.pow(x, 2)) + (12 * Math.cos(t));
            z = (Math.sin(2 * Math.PI * 7 * t) * x) - (0.2 * Math.log10(Math.abs((y) + Math.PI)));
            v = Math.sqrt(Math.abs(Math.pow(y, 2) * z)) - (1.8 * Math.sin(0.4 * t * z * x));
        }

        if (function == 'u') {
            if (t >= 0 && t < 0.22) {
                u = (1 - (7 * t)) * Math.sin((2 * Math.PI) * (10 / (t + 0.04)));
            } else if (t >= 0.22 && t < 0.57) {
                u = (0.63 * t * Math.sin(125 * t)) + (Math.log(2 * t) / Math.log(2));
            } else if (t >= 0.57 && t < 0.97) {
                u = (Math.pow(t, -0.662) + (0.77 * Math.sin(8 * t)));
            }
        }

        if (function == 'b') {
//            for (int n = 1; n <= 2; n++)
//                b += (Math.cos(12 * t * Math.pow(n, 2)) + (Math.cos(16 * t * n)) / Math.pow(n, 2));
            for (int n = 1; n <= 4; n++)
                b += (Math.cos(12 * t * Math.pow(n, 2)) + (Math.cos(16 * t * n)) / Math.pow(n, 2));
//            for (int n = 1; n <= 16; n++)
//                b += (Math.cos(12 * t * Math.pow(n, 2)) + (Math.cos(16 * t * n)) / Math.pow(n, 2));
        }

        if (function == 'y')
            return y;
        else if (function == 'z')
            return z;
        else if (function == 'v')
            return v;
        else if (function == 'u')
            return u;
        else if (function == 'b')
            return b;
        else // x is always calculated
            return x;

    }

    private Vector<Double> spectrum(Complex[] data, int n) {
        Vector<Double> m2 = new Vector();
        for (int i = 0; i < n / 2; ++i) {
            double m = Math.sqrt(Math.pow(data[i].re(), 2.0) + Math.pow(data[i].im(), 2.0));
//            m2.add(10.0D * Math.log10(m));
            m = 10.0 * Math.log10(m);
            m = m * fs / closestPower;
            m2.addElement(m);
        }
        return m2;
    }
}
