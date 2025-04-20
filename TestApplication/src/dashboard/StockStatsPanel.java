package dashboard;

import db.DBHelper;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.AttributedString;
import java.text.DecimalFormat;

public class StockStatsPanel extends JPanel {
    public StockStatsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(27, 34, 44));
        setBorder(BorderFactory.createTitledBorder("Stock Distribution"));

        DefaultPieDataset dataset = new DefaultPieDataset();

        // Load actual data from the stock database
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT fish_type, SUM(fish_load_kg) as total_kg FROM fish_stocks GROUP BY fish_type";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            boolean hasData = false;
            while (rs.next()) {
                String fishType = rs.getString("fish_type");
                double totalKg = rs.getDouble("total_kg");
                dataset.setValue(fishType, totalKg);
                hasData = true;
            }
            if (!hasData) {
                dataset.setValue("No Data", 1);
            }
        } catch (Exception e) {
            dataset.setValue("Error", 1);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                null, dataset, false, true, false);

        chart.setBackgroundPaint(new Color(27, 34, 44));
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(27, 34, 44));
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setSectionOutlinesVisible(false);

        // Modern color palette (flat, vibrant)
        Color[] sectionColors = new Color[]{
                new Color(33, 99, 186),    // Blue
                new Color(240, 220, 170),  // Sand
                new Color(80, 200, 120),   // Green
                new Color(241, 91, 181),   // Pink
                new Color(255, 99, 71),    // Coral
                new Color(255, 195, 0),    // Yellow
                new Color(128, 128, 128)   // Gray (fallback)
        };
        int colorIndex = 0;
        for (Object key : dataset.getKeys()) {
            plot.setSectionPaint((Comparable) key, sectionColors[colorIndex % sectionColors.length]);
            colorIndex++;
        }

        // --- Strong, visible, modern labels ---
        plot.setLabelFont(new Font("Segoe UI", Font.BOLD, 18));
        plot.setLabelPaint(Color.WHITE);
        plot.setLabelShadowPaint(null); // No shadow
        plot.setLabelBackgroundPaint(null); // No background
        plot.setLabelOutlinePaint(null); // No outline
        plot.setSimpleLabels(true); // Simple, flat label placement
        plot.setLabelGap(0.012);

        // Only show label if > 3% of total, and show percent
        plot.setLabelGenerator(new PieSectionLabelGenerator() {
            private final DecimalFormat percentFormat = new DecimalFormat("0.0");
            @Override
            public String generateSectionLabel(org.jfree.data.general.PieDataset dataset, Comparable key) {
                Number value = dataset.getValue(key);
                if (value == null || value.doubleValue() <= 0) return null;
                double total = 0;
                for (int i = 0; i < dataset.getItemCount(); i++)
                    total += dataset.getValue(i).doubleValue();
                double percent = value.doubleValue() / total * 100.0;
                if (percent < 3.0) return null; // hide small slices
                return key + " (" + percentFormat.format(percent) + "%)";
            }
            @Override
            public AttributedString generateAttributedSectionLabel(org.jfree.data.general.PieDataset dataset, Comparable key) {
                return null;
            }
        });

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(new Color(0,0,0,0));
        chartPanel.setPreferredSize(new Dimension(400, 320));
        chartPanel.setMouseWheelEnabled(true);
        add(chartPanel, BorderLayout.CENTER);
    }
}