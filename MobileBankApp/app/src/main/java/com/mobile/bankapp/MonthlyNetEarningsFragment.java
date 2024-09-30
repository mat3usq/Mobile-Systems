package com.mobile.bankapp;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.bankapp.tools.DateFormatConverter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;

public class MonthlyNetEarningsFragment extends DialogFragment {
    public MonthlyNetEarningsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Ładowanie layoutu dla tego fragmentu
        View view = inflater.inflate(R.layout.fragment_monthly_net_earnings, container, false);

        // Pobranie instancji kalendarza, aby obliczyć miesiące dla etykiet
        Calendar calendar = Calendar.getInstance();

        // Pobranie danych zarobków netto przekazanych do fragmentu
        double[] netEarnings = getArguments().getDoubleArray("Y_VALUES");
        String[] months = new String[6];

        // Iteracja przez ostatnie 6 miesięcy, aby uzyskać etykiety miesięcy
        for (int i = 0; i < 6; i++) {
            months[5 - i] = DateFormatConverter.MONTH_STRINGS[calendar.get(Calendar.MONTH)];
            calendar.add(Calendar.MONTH, -1);
        }

        // Znalezienie GraphView w layout i ustawienie formattera etykiet
        GraphView graphView = view.findViewById(R.id.netEarnings_graph_monthly);
        StaticLabelsFormatter formatter = new StaticLabelsFormatter(graphView);
        formatter.setHorizontalLabels(months);
        graphView.getGridLabelRenderer().setLabelFormatter(formatter);

        // Sprawdzenie, czy dane zarobków netto nie są null
        assert netEarnings != null;
        // Utworzenie serii danych do wykresu
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, netEarnings[5]),
                new DataPoint(1, netEarnings[4]),
                new DataPoint(2, netEarnings[3]),
                new DataPoint(3, netEarnings[2]),
                new DataPoint(4, netEarnings[1]),
                new DataPoint(5, netEarnings[0]),
        });

        // Ustawienie stylu wykresu
        graphView.setTitleTextSize(20.0f);
        GridLabelRenderer renderer = graphView.getGridLabelRenderer();
        renderer.setTextSize(32.0f);
        series.setThickness(8);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10.0f);
        // Dodanie serii danych do GraphView
        graphView.addSeries(series);

        // Zwrócenie widoku fragmentu
        return view;
    }
}
