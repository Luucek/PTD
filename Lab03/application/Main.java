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
//    final private double f = 20;
    final int fs = 700;
    final int T = 2;
//    final private double phi = -Math.PI;
    final int N = T * fs;

    final private double Am = 1.0;
    final private double Fn = 10.0;
    final private double Fm = 9.0;

    final private double Ka = 0.5;
    final private double Kp = 42;

    // choose function to calculate
    final char userModulation = 'a';


    public void start(Stage stage) {

        int closestPower = calculatePower(N);

        // calculating data
        Complex[] m = calculateData(closestPower, userModulation);

        // FFT
        Complex[] z = FFT.fft(m);

        // widmo
        Vector<Double> z2 = spectrum(z, closestPower);

        // populating the series with data
        List<XYChart.Data<Integer, Float>> seriesData = new ArrayList<>();
        for (int i = 0; i < z2.size(); i++) {
            seriesData.add(new XYChart.Data(i, z2.elementAt(i)));
        }

        // creating chart
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        XYChart.Series<Integer, Float> series = new XYChart.Series<>();
        series.getData().addAll(seriesData);
        if (userModulation == 'a')
            series.setName("Z" + userModulation + "(t) , K" + userModulation + " = " + Ka);
        else
            series.setName("Z" + userModulation + "(t) , K" + userModulation + " = " + Kp);

        LineChart<Integer, Float> chart = new LineChart(xAxis, yAxis, FXCollections.observableArrayList(series));

        chart.setCreateSymbols(false);

        Scene scene = new Scene(chart, 800.0, 600.0);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private int calculatePower(int n) {
        // calculating closest, equal or smaller power to n
        for (int i = n; i >= 1; i--) {
            if ((i & (i - 1)) == 0) {
                return i;
            }
        }
        return 0;
    }

    private Complex[] calculateData(int n, char modulation) {
        Complex[] data = new Complex[n];
        for (int i = 0; i < n; i++) {
            double t = (double) i / (double) fs;
            double result = getResult(modulation, t);

            data[i] = new Complex(result, 0);
        }
        return data;
    }

    private Double getResult(char modulation, double t) {
        double m = Am * Math.sin((2 * Math.PI) * Fm * t);
        double z = 0.0;
        if (modulation == 'a') {
            z = (Ka * m + 1) * Math.cos((2 * Math.PI) * Fn * t);
        } else if (modulation == 'p') {
            z = Math.cos(((2 * Math.PI) * Fn * t) + (Kp * m));
        }
        return z;
    }

    private Vector<Double> spectrum(Complex[] data, int n) {
        Vector<Double> m2 = new Vector();
        for (int i = 0; i < n / 2; ++i) {
            double m = Math.sqrt(Math.pow(data[i].re(), 2.0) + Math.pow(data[i].im(), 2.0));
            m = 10.0 * Math.log10(m);
            m = m * fs / n;
            m2.addElement(m);
        }
        return m2;
    }
}
