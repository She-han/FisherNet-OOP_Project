package dashboard;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;

public class BoatStatsPanel extends JPanel {
    public BoatStatsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(27, 34, 44));
        setBorder(BorderFactory.createTitledBorder("Boat Capacity (kg)"));

        // Sample data
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(400, "Capacity", "Sea Falcon");
        dataset.addValue(600, "Capacity", "Aqua Rider");

        JFreeChart chart = ChartFactory.createBarChart(
                null, "Boat", "Capacity (kg)", dataset);
        chart.setBackgroundPaint(new Color(27, 34, 44));
        chart.getPlot().setBackgroundPaint(new Color(44, 44, 60));
        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, new Color(33, 99, 186));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(new Color(0,0,0,0));
        add(chartPanel, BorderLayout.CENTER);
    }
}