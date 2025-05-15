package dashboard;

import org.knowm.xchart.*;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import db.DBHelper;
import java.util.List;
import java.util.ArrayList;

public class StatsPanel extends JPanel {
    private LocalDate fromDate;
    private LocalDate toDate;

    // Chart panels for updating
    private JPanel pieChartPanel;
    private JPanel trendChartPanel;
    private JPanel barChartPanel;
    private JPanel boatChartPanel;

    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;

    // --- Keep border colors consistent
    private static final Color PREFERED_CHART_BORDER_COLOR = new Color(70, 120, 220);

    public StatsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(27, 34, 44));
        

        // Header
        JLabel topic = new JLabel("Fishery Stock Statistics");
        topic.setFont(new Font("Segoe UI Semibold", Font.BOLD, 32));
        topic.setForeground(Color.WHITE);
        topic.setBorder(new EmptyBorder(20, 30, 10, 0));

        // Date filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(new EmptyBorder(10, 0, 10, 30));
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setForeground(new Color(140,180,255));
        fromLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        fromDatePicker = new DatePicker();
        JLabel toLabel = new JLabel("To:");
        toLabel.setForeground(new Color(140,180,255));
        toLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        toDatePicker = new DatePicker();

        JButton updateBtn = new JButton("Update");
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        updateBtn.setBackground(new Color(33,99,186));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false);
        updateBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        updateBtn.setPreferredSize(new Dimension(110, 34));

        filterPanel.add(fromLabel);
        filterPanel.add(fromDatePicker);
        filterPanel.add(toLabel);
        filterPanel.add(toDatePicker);
        filterPanel.add(updateBtn);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(topic, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Charts grid
        JPanel chartsGrid = new JPanel(new GridLayout(2,2,24,24));
        chartsGrid.setBorder(new EmptyBorder(24, 28, 24, 28));
        chartsGrid.setBackground(new Color(27, 34, 44));

        // Determine initial full date range
        LocalDate[] minMax = getMinMaxDates();
        LocalDate minDate = minMax[0];
        LocalDate maxDate = minMax[1];

        // Set date pickers initial values
        fromDatePicker.setDate(minDate);
        toDatePicker.setDate(maxDate);

        fromDate = minDate;
        toDate = maxDate;

        // --- 1. Show all charts initially ---
        pieChartPanel   = createModernChartPanel("Stock Distribution",   createPieChart(fromDate, toDate),   PREFERED_CHART_BORDER_COLOR);
        trendChartPanel = createModernChartPanel("Stock Trend Over Time",createTrendChart(fromDate, toDate), PREFERED_CHART_BORDER_COLOR);
        barChartPanel   = createModernChartPanel("Top Fish Types",       createTopFishBarChart(fromDate, toDate), PREFERED_CHART_BORDER_COLOR);
        boatChartPanel  = createModernChartPanel("Boat-wise Stock Contribution", createBoatContributionChart(fromDate, toDate), PREFERED_CHART_BORDER_COLOR);

        chartsGrid.add(pieChartPanel);
        chartsGrid.add(trendChartPanel);
        chartsGrid.add(barChartPanel);
        chartsGrid.add(boatChartPanel);

        add(chartsGrid, BorderLayout.CENTER);

        // --- Make charts visible immediately ---
        revalidate();
        repaint();
        refreshAllCharts();

        // Update charts on filter
        updateBtn.addActionListener((ActionEvent e) -> {
            fromDate = fromDatePicker.getDate();
            toDate = toDatePicker.getDate();
            refreshAllCharts();
        });
    }

    private LocalDate[] getMinMaxDates() {
        LocalDate min = LocalDate.now().minusYears(10);
        LocalDate max = LocalDate.now();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT MIN(date) AS min_date, MAX(date) AS max_date FROM fish_stocks";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String minStr = rs.getString("min_date");
                String maxStr = rs.getString("max_date");
                if (minStr != null && !minStr.isEmpty()) min = LocalDate.parse(minStr);
                if (maxStr != null && !maxStr.isEmpty()) max = LocalDate.parse(maxStr);
            }
        } catch (Exception e) {
            // if error, fallback to last month to today
            min = LocalDate.now().minusMonths(1);
            max = LocalDate.now();
        }
        return new LocalDate[]{min, max};
    }

    private void refreshAllCharts() {
        replaceChartPanel(pieChartPanel,   createPieChart(fromDate, toDate));
        replaceChartPanel(trendChartPanel, createTrendChart(fromDate, toDate));
        replaceChartPanel(barChartPanel,   createTopFishBarChart(fromDate, toDate));
        replaceChartPanel(boatChartPanel,  createBoatContributionChart(fromDate, toDate));
        revalidate();
        repaint();
    }

    // Helper to replace chart in a panel
    private void replaceChartPanel(JPanel parent, JComponent newChartPanel) {
        parent.removeAll();
        parent.add(newChartPanel, BorderLayout.CENTER);
        parent.revalidate();
        parent.repaint();
    }

    private JPanel createModernChartPanel(String title, JComponent chartPanel, Color borderColor) {
        JLabel chartTitle = new JLabel(title, SwingConstants.CENTER);
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        chartTitle.setForeground(Color.WHITE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new CompoundBorder(
                new LineBorder(borderColor, 2, true),
                new EmptyBorder(12, 12, 12, 12)));
        wrapper.add(chartTitle, BorderLayout.NORTH);
        wrapper.add(chartPanel, BorderLayout.CENTER);
        wrapper.setLayout(new BorderLayout());
        return wrapper;
    }

    // ---- Pie Chart: Stock Distribution by Fish Type ----
    private JComponent createPieChart(LocalDate from, LocalDate to) {
        Map<String, Double> data = new LinkedHashMap<>();
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
                data.put(fishType, totalKg);
                hasData = true;
            }
            if (!hasData) data.put("No Data", 1.0);
        } catch (Exception e) {
            data.clear();
            data.put("Error", 1.0);
        }

        PieChart chart = new PieChartBuilder()
               .width(400)
               .height(300)
               .title("Stock Distribution by Fish Type")
               .theme(Styler.ChartTheme.GGPlot2)
               .build();

        // Styling
        chart.getStyler().setChartTitleBoxVisible(false);
        chart.getStyler().setChartBackgroundColor(new Color(27, 34, 44));
        chart.getStyler().setPlotBackgroundColor(new Color(34, 44, 60));
        chart.getStyler().setLegendVisible(true); // Show legend for all types, including "crab"
        chart.getStyler().setLegendFont(new Font("Segoe UI", Font.BOLD, 14));
        chart.getStyler().setLegendBackgroundColor(new Color(27, 34, 44));
        chart.getStyler().setLegendBorderColor(PREFERED_CHART_BORDER_COLOR);
        chart.getStyler().setChartPadding(10);
        chart.getStyler().setCircular(true);
        chart.getStyler().setStartAngleInDegrees(90);
        chart.getStyler().setChartTitleFont(new Font("Segoe UI", Font.BOLD, 20));
        chart.getStyler().setChartFontColor(Color.WHITE);
        chart.getStyler().setChartTitleBoxBorderColor(PREFERED_CHART_BORDER_COLOR);



        // Assign colors for the slices
        Color[] pieColors = new Color[]{
                new Color(33, 99, 186),   // blue
                new Color(140, 200, 140), // green
                new Color(255, 210, 90),  // yellow
                new Color(220, 80, 80),   // red
                new Color(140, 180, 255), // light blue
                new Color(255, 160, 180), // pink
                new Color(120, 120, 120), // gray
                new Color(180, 140, 255), // purple
                new Color(100, 200, 220), // cyan
                new Color(200, 200, 80)   // olive
        };
        chart.getStyler().setSeriesColors(pieColors);

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            chart.addSeries(entry.getKey(), entry.getValue());
        }
        return new XChartPanel<>(chart);
    }

    // ---- Line Chart: Stock Trend Over Time ----
    private JComponent createTrendChart(LocalDate from, LocalDate to) {
        List<Date> dateList = new ArrayList<>();
        List<Double> valueList = new ArrayList<>();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT date, SUM(fish_load_kg) as total_kg FROM fish_stocks WHERE date >= ? AND date <= ? GROUP BY date ORDER BY date ASC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ResultSet rs = ps.executeQuery();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                String dateStr = rs.getString("date");
                Date d = fmt.parse(dateStr); // convert String to java.util.Date
                double v = rs.getDouble("total_kg");
                dateList.add(d);
                valueList.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        XYChart chart = new XYChartBuilder().width(400).height(300)
                .title("Stock Trend Over Time")
                .xAxisTitle("Date").yAxisTitle("Total Stock (Kg)")
                .theme(Styler.ChartTheme.GGPlot2).build();
        chart.getStyler().setChartBackgroundColor(new Color(27, 34, 44));
        chart.getStyler().setPlotBackgroundColor(new Color(34, 44, 60));
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setPlotGridLinesColor(new Color(190, 200, 255, 60));
        chart.getStyler().setAxisTickLabelsColor(Color.WHITE);
        chart.getStyler().setAxisTitleFont(new Font("Segoe UI", Font.BOLD, 17));
        chart.getStyler().setAxisTickLabelsFont(new Font("Segoe UI", Font.PLAIN, 15));
        chart.getStyler().setSeriesColors(new Color[]{new Color(33, 99, 186)});
        chart.getStyler().setChartTitleFont(new Font("Segoe UI", Font.BOLD, 20));
        chart.getStyler().setChartFontColor(Color.WHITE);
        chart.getStyler().setChartTitleBoxVisible(false);
        chart.getStyler().setChartTitleBoxBorderColor(PREFERED_CHART_BORDER_COLOR);
        chart.getStyler().setXAxisLabelRotation(20);

        if (!dateList.isEmpty()) {
            chart.addSeries("Total Stock", dateList, valueList);
        }
        return new XChartPanel<>(chart);
    }

    // ---- Bar Chart: Top Fish Types ----
    private JComponent createTopFishBarChart(LocalDate from, LocalDate to) {
        List<String> types = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT fish_type, SUM(fish_load_kg) as total_kg FROM fish_stocks WHERE date >= ? AND date <= ? GROUP BY fish_type ORDER BY total_kg DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                types.add(rs.getString("fish_type"));
                values.add(rs.getDouble("total_kg"));
            }
        } catch (Exception e) {}
        CategoryChart chart = new CategoryChartBuilder().width(400).height(300)
                .title("Top Fish Types")
                .xAxisTitle("Fish Type").yAxisTitle("Total Stock (Kg)")
                .theme(Styler.ChartTheme.GGPlot2).build();
        chart.getStyler().setChartFontColor(Color.WHITE);
        chart.getStyler().setChartBackgroundColor(new Color(27, 34, 44));
        chart.getStyler().setPlotBackgroundColor(new Color(34, 44, 60));
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setPlotGridLinesColor(new Color(190, 200, 255, 60));
        chart.getStyler().setAxisTickLabelsColor(Color.WHITE);
        chart.getStyler().setAxisTitleFont(new Font("Segoe UI", Font.BOLD, 17));
        chart.getStyler().setAxisTickLabelsFont(new Font("Segoe UI", Font.PLAIN, 15));
        chart.getStyler().setSeriesColors(new Color[]{new Color(130, 180, 255)});
        chart.getStyler().setChartTitleFont(new Font("Segoe UI", Font.BOLD, 20));
        chart.getStyler().setChartTitleBoxVisible(false);
        chart.getStyler().setChartTitleBoxBorderColor(PREFERED_CHART_BORDER_COLOR);

        if (!types.isEmpty()) {
            chart.addSeries("Fish Load (Kg)", types, values);
        }
        return new XChartPanel<>(chart);
    }

    // ---- Bar Chart: Boat-wise Stock Contribution ----
    private JComponent createBoatContributionChart(LocalDate from, LocalDate to) {
        List<String> boats = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        try (Connection con = DBHelper.getConnection()) {
            String sql = "SELECT b.name AS boat_name, SUM(s.fish_load_kg) AS total_kg FROM fish_stocks s JOIN boats b ON s.boat_id = b.id WHERE s.date >= ? AND s.date <= ? GROUP BY b.name ORDER BY total_kg DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, from.toString());
            ps.setString(2, to.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                boats.add(rs.getString("boat_name"));
                values.add(rs.getDouble("total_kg"));
            }
        } catch (Exception e) {}
        CategoryChart chart = new CategoryChartBuilder().width(400).height(300)
                .title("Boat-wise Stock Contribution")
                .xAxisTitle("Boat").yAxisTitle("Total Stock (Kg)")
                .theme(Styler.ChartTheme.GGPlot2).build();
        chart.getStyler().setChartFontColor(Color.WHITE);
        chart.getStyler().setChartBackgroundColor(new Color(27, 34, 44));
        chart.getStyler().setPlotBackgroundColor(new Color(34, 44, 60));
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setPlotGridLinesColor(new Color(190, 200, 255, 60));
        chart.getStyler().setAxisTickLabelsColor(Color.WHITE);
        chart.getStyler().setAxisTitleFont(new Font("Segoe UI", Font.BOLD, 17));
        chart.getStyler().setAxisTickLabelsFont(new Font("Segoe UI", Font.PLAIN, 15));
        chart.getStyler().setSeriesColors(new Color[]{new Color(130, 180, 255)});
        chart.getStyler().setChartTitleFont(new Font("Segoe UI", Font.BOLD, 20));
        chart.getStyler().setChartTitleBoxVisible(false);
        chart.getStyler().setChartTitleBoxBorderColor(PREFERED_CHART_BORDER_COLOR);

        if (!boats.isEmpty()) {
            chart.addSeries("Fish Load (Kg)", boats, values);
        }
        return new XChartPanel<>(chart);
    }
}