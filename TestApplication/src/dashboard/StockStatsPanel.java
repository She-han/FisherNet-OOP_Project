package dashboard;

import db.DBHelper;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StockStatsPanel extends JPanel {
    public StockStatsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(27, 34, 44));
        setBorder(BorderFactory.createTitledBorder("Current Fish Stock (kg)"));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Load actual data from the stock database
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT fish_type, SUM(fish_load_kg) as total_kg FROM fish_stocks GROUP BY fish_type";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            boolean hasData = false;
            while (rs.next()) {
                String fishType = rs.getString("fish_type");
                double totalKg = rs.getDouble("total_kg");
                dataset.addValue(totalKg, "Stock", fishType);
                hasData = true;
            }
            if (!hasData) {
                dataset.addValue(0, "Stock", "No Data");
            }
        } catch (Exception e) {
            dataset.addValue(0, "Stock", "Error");
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null, // No chart title
                "Fish Type",
                "Total Stock (kg)",
                dataset
        );

        chart.setBackgroundPaint(new Color(27, 34, 44));
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(44, 44, 60));
        plot.setOutlineVisible(false);
        plot.setDomainGridlinePaint(new Color(190, 200, 255, 60));
        plot.setRangeGridlinePaint(new Color(190, 200, 255, 60));

        // Bar colors and styling
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        Color[] barColors = new Color[]{
                new Color(33, 99, 186),    // Blue
                new Color(80, 200, 120),   // Green
                new Color(241, 91, 181),   // Pink
                new Color(255, 99, 71),    // Coral
                new Color(255, 195, 0),    // Yellow
                new Color(128, 128, 128)   // Gray (fallback)
        };
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            renderer.setSeriesPaint(0, barColors[i % barColors.length]);
        }
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());

        // Axis styling
        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.BOLD, 15));
        plot.getDomainAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 17));
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getDomainAxis().setLabelPaint(Color.WHITE);

        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.BOLD, 15));
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 17));
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(Color.WHITE);

        // No legend
        chart.removeLegend();

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false);
        chartPanel.setBackground(new Color(0, 0, 0, 0));
        chartPanel.setPreferredSize(new Dimension(420, 320));
        chartPanel.setMouseWheelEnabled(true);
        add(chartPanel, BorderLayout.CENTER);
    }
}