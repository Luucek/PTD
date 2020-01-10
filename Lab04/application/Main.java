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

    public void start(Stage stage) {

        // choose function to calculate
        final String userModulation = "ASK";

        // czas trwania bitu
        final double Tb = 1.0;

        // częstotliwość dla każdego bitu
        final int N = 100;

        // tworzenie Bitstream
        final String s = "ptd";
        final String bits = getBitstream(s);

        // częśtotliwość całego wykresu
        final int fn = N * bits.length();

        // stałe
        final double A1 = 1.0;
        final double A2 = 2.0;

        // najbliższa potęga 2 wyliczana z częstotliwości, potrzebna do fft
        final int closestPower = calculatePower(fn);

        // wyliczanie całego wykresu
        Complex[] m = calculateData(closestPower, userModulation, bits);

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
        series.setName("Kluczowanie");

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

    private String getBitstream(String s) {
        byte[] bytes = s.getBytes();
        StringBuilder bitstream = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                bitstream.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        return bitstream.toString();
    }

    private Complex[] calculateData(int n, String keying, String modulation) {
        Complex[] data = new Complex[n];
        for (int i = 0; i < n; i++) {
            double result = getResult(keying, modulation.charAt(i));
            data[i] = new Complex(result, 0);
        }
        

        return data;
    }

    private Vector<Double> spectrum(Complex[] data, int n) {
        Vector<Double> m2 = new Vector();
        for (int i = 0; i < n / 2; ++i) {
            double m = Math.sqrt(Math.pow(data[i].re(), 2.0) + Math.pow(data[i].im(), 2.0));
            m = 10.0 * Math.log10(m);
            m2.addElement(m);
        }
        return m2;
    }

    private Double getResult(String keying, int m) {
        double z = 0.0;
        if (keying == "ASK") {
            z = Math.sin(m);
        } else if (keying == "FSK") {
        } else if (keying == "PSK") {
        }

        return z;
    }

    private  Double ASK(double t) {

        return 0.0;
    }
}
