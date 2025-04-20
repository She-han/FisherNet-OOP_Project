package dashboard;

// (Paste your full StatsPanel code here, see below for the improved, ready-to-use version)

import db.DBHelper;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class StatsPanel extends JPanel {
    private LocalDate fromDate;
    private LocalDate toDate;

    // Chart panels for updating
    private ChartPanel pieChartPanel;
    private ChartPanel trendChartPanel;
    private ChartPanel barChartPanel;
    private ChartPanel boatChartPanel;

    private JSpinner fromDateSpinner;
    private JSpinner toDateSpinner;

    public StatsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(27, 34, 44));

        try {
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setOpaque(false);

            JLabel topic = new JLabel("Fishery Stock Statistics");
            topic.setFont(new Font("Segoe UI", Font.BOLD, 32));
            topic.setForeground(new Color(33, 99, 186));
            topic.setBorder(new EmptyBorder(15, 20, 10, 0));
            topPanel.add(topic, BorderLayout.WEST);

            // Time filter controls
            JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 2));
            filterPanel.setOpaque(false);
            filterPanel.setBorder(new EmptyBorder(0,0,0,30));
            filterPanel.add(new JLabel("From:"));
            fromDateSpinner = createDateSpinner(LocalDate.now().minusMonths(1));
            filterPanel.add(fromDateSpinner);
            filterPanel.add(new JLabel("To:"));
            toDateSpinner = createDateSpinner(LocalDate.now());
            filterPanel.add(toDateSpinner);
            JButton updateBtn = new JButton("Update");
            updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            updateBtn.setBackground(new Color(33,99,186));
            updateBtn.setForeground(Color.WHITE);
            updateBtn.setFocusPainted(false);
            updateBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            filterPanel.add(updateBtn);
            topPanel.add(filterPanel, BorderLayout.EAST);

            add(topPanel, BorderLayout.NORTH);

            // Charts area
            JPanel chartsGrid = new JPanel();
            chartsGrid.setLayout(new GridLayout(2,2,20,20));
            chartsGrid.setOpaque(false);
            chartsGrid.setBorder(new EmptyBorder(30, 20, 30, 20));

            // Initialize charts
            fromDate = ((Date)fromDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            toDate = ((Date)toDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            pieChartPanel = createPieChartPanel(fromDate, toDate);
            trendChartPanel = createTrendChartPanel(fromDate, toDate);
            barChartPanel = createTopFishBarChartPanel(fromDate, toDate);
            boatChartPanel = createBoatContributionChartPanel(fromDate, toDate);

            chartsGrid.add(pieChartPanel);
            chartsGrid.add(trendChartPanel);
            chartsGrid.add(barChartPanel);
            chartsGrid.add(boatChartPanel);

            add(chartsGrid, BorderLayout.CENTER);

            // Update charts on time frame selection
            updateBtn.addActionListener((ActionEvent e) -> {
                fromDate = ((Date)fromDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                toDate = ((Date)toDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                refreshAllCharts();
            });
        } catch(Exception ex) {
            ex.printStackTrace();
            removeAll();
            JLabel error = new JLabel("Failed to load statistics: " + ex.getMessage());
            error.setForeground(Color.RED);
            add(error, BorderLayout.CENTER);
        }
    }

    private JSpinner createDateSpinner(LocalDate defaultVal) {
        SpinnerDateModel model = new SpinnerDateModel(java.sql.Date.valueOf(defaultVal), null, null, java.util.Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        spinner.setPreferredSize(new Dimension(120, 28));
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        spinner.setValue(java.sql.Date.valueOf(defaultVal));
        return spinner;
    }

    private void refreshAllCharts() {
        pieChartPanel.setChart(createPieChart(fromDate, toDate));
        trendChartPanel.setChart(createTrendChart(fromDate, toDate));
        barChartPanel.setChart(createTopFishBarChart(fromDate, toDate));
        boatChartPanel.setChart(createBoatContributionChart(fromDate, toDate));
    }

    // ---- Pie Chart: Stock Distribution by Fish Type ----
    private ChartPanel createPieChartPanel(LocalDate from, LocalDate to) {
        ChartPanel panel = new ChartPanel(createPieChart(from, to));
        panel.setOpaque(false);
        panel.setBackground(new Color(0,0,0,0));
        panel.setPreferredSize(new Dimension(400, 320));
        panel.setMouseWheelEnabled(true);
        return panel;
    }
    private JFreeChart createPieChart(LocalDate from, LocalDate to) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT fish_type, SUM(fish_load_kg) as total_kg FROM fish_stocks WHERE date >= ? AND date <= ? GROUP BY fish_type";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ResultSet rs = ps.executeQuery();
            boolean hasData = false;
            while (rs.next()) {
                String fishType = rs.getString("fish_type");
                double totalKg = rs.getDouble("total_kg");
                dataset.setValue(fishType, totalKg);
                hasData = true;
            }
            if (!hasData) dataset.setValue("No Data", 1);
        } catch (Exception e) {
            dataset.setValue("Error", 1);
        }
        JFreeChart chart = ChartFactory.createPieChart(
                "Stock Distribution", dataset, false, true, false);
        chart.setBackgroundPaint(new Color(27, 34, 44));
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(27, 34, 44));
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setSectionOutlinesVisible(false);
        Color[] sectionColors = {new Color(33, 99, 186),new Color(240, 220, 170),new Color(80, 200, 120),new Color(241, 91, 181),new Color(255, 99, 71),new Color(255, 195, 0),new Color(128, 128, 128)};
        int colorIndex = 0;
        for (Object key : dataset.getKeys()) {
            plot.setSectionPaint((Comparable) key, sectionColors[colorIndex % sectionColors.length]);
            colorIndex++;
        }
        plot.setLabelFont(new Font("Segoe UI", Font.BOLD, 17));
        plot.setLabelPaint(Color.WHITE);
        plot.setLabelShadowPaint(null);
        plot.setLabelBackgroundPaint(null);
        plot.setLabelOutlinePaint(null);
        plot.setSimpleLabels(true);
        plot.setLabelGap(0.012);
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
                if (percent < 3.0) return null;
                return key + " (" + percentFormat.format(percent) + "%)";
            }
            @Override
            public AttributedString generateAttributedSectionLabel(org.jfree.data.general.PieDataset dataset, Comparable key) {
                return null;
            }
        });
        return chart;
    }

    // ---- Line Chart: Stock Trend Over Time ----
    private ChartPanel createTrendChartPanel(LocalDate from, LocalDate to) {
        ChartPanel panel = new ChartPanel(createTrendChart(from, to));
        panel.setOpaque(false);
        panel.setBackground(new Color(0,0,0,0));
        panel.setPreferredSize(new Dimension(400, 320));
        panel.setMouseWheelEnabled(true);
        return panel;
    }
    private JFreeChart createTrendChart(LocalDate from, LocalDate to) {
        TimeSeries series = new TimeSeries("Total Stock");
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT date, SUM(fish_load_kg) as total_kg FROM fish_stocks WHERE date >= ? AND date <= ? GROUP BY date ORDER BY date ASC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate d = LocalDate.parse(rs.getString("date"));
                double v = rs.getDouble("total_kg");
                series.add(new Day(Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant())), v);
            }
        } catch (Exception e) {}
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Stock Trend Over Time", "Date", "Total Stock (Kg)", dataset, false, true, false);
        chart.setBackgroundPaint(new Color(27, 34, 44));
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(new Color(34, 44, 60));
        plot.setDomainGridlinePaint(new Color(70, 90, 120, 80));
        plot.setRangeGridlinePaint(new Color(70, 90, 120, 80));
        plot.getRenderer().setSeriesPaint(0, new Color(33, 99, 186));
        plot.getRenderer().setSeriesStroke(0, new BasicStroke(3.0f));
        ValueAxis axis = plot.getDomainAxis();
        axis.setLabelFont(new Font("Segoe UI", Font.BOLD, 15));
        axis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 13));
        plot.getRangeAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 15));
        plot.getRangeAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 13));
        return chart;
    }

    // ---- Bar Chart: Top Fish Types ----
    private ChartPanel createTopFishBarChartPanel(LocalDate from, LocalDate to) {
        ChartPanel panel = new ChartPanel(createTopFishBarChart(from, to));
        panel.setOpaque(false);
        panel.setBackground(new Color(0,0,0,0));
        panel.setPreferredSize(new Dimension(400, 320));
        panel.setMouseWheelEnabled(true);
        return panel;
    }
    private JFreeChart createTopFishBarChart(LocalDate from, LocalDate to) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT fish_type, SUM(fish_load_kg) as total_kg FROM fish_stocks WHERE date >= ? AND date <= ? GROUP BY fish_type ORDER BY total_kg DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String fishType = rs.getString("fish_type");
                double totalKg = rs.getDouble("total_kg");
                dataset.addValue(totalKg, "Fish Load (Kg)", fishType);
            }
        } catch (Exception e) {}
        JFreeChart chart = ChartFactory.createBarChart(
                "Top Fish Types", "Fish Type", "Total Stock (Kg)", dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(new Color(27, 34, 44));
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(34, 44, 60));
        plot.setDomainGridlinePaint(new Color(70, 90, 120, 80));
        plot.setRangeGridlinePaint(new Color(70, 90, 120, 80));
        plot.getRenderer().setSeriesPaint(0, new Color(33, 99, 186));
        plot.getRenderer().setBaseItemLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        plot.getRenderer().setBaseItemLabelsVisible(true);
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 15));
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 13));
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 15));
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 13));
        return chart;
    }

    // ---- Bar Chart: Boat-wise Stock Contribution ----
    private ChartPanel createBoatContributionChartPanel(LocalDate from, LocalDate to) {
        ChartPanel panel = new ChartPanel(createBoatContributionChart(from, to));
        panel.setOpaque(false);
        panel.setBackground(new Color(0,0,0,0));
        panel.setPreferredSize(new Dimension(400, 320));
        panel.setMouseWheelEnabled(true);
        return panel;
    }
    private JFreeChart createBoatContributionChart(LocalDate from, LocalDate to) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT b.name AS boat_name, SUM(s.fish_load_kg) AS total_kg FROM fish_stocks s JOIN boats b ON s.boat_id = b.id WHERE s.date >= ? AND s.date <= ? GROUP BY b.name ORDER BY total_kg DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String boatName = rs.getString("boat_name");
                double totalKg = rs.getDouble("total_kg");
                dataset.addValue(totalKg, "Fish Load (Kg)", boatName);
            }
        } catch (Exception e) {}
        JFreeChart chart = ChartFactory.createBarChart(
                "Boat-wise Stock Contribution", "Boat", "Total Stock (Kg)", dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(new Color(27, 34, 44));
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(34, 44, 60));
        plot.setDomainGridlinePaint(new Color(70, 90, 120, 80));
        plot.setRangeGridlinePaint(new Color(70, 90, 120, 80));
        plot.getRenderer().setSeriesPaint(0, new Color(80, 200, 120));
        plot.getRenderer().setBaseItemLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        plot.getRenderer().setBaseItemLabelsVisible(true);
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 15));
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 13));
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelFont(new Font("Segoe UI", Font.BOLD, 15));
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 13));
        return chart;
    }
}