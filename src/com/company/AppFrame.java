package com.company;

import com.company.util.ArrayUtils;
import com.company.util.JTableUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

import com.company.util.SwingUtils;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

public class AppFrame extends JFrame{
    private JPanel mainPanel;
    private JButton randomButton1000;
    private JButton randomButton10;
    private JTable tableArr;
    private JButton sortButton;
    private JButton compareButton;
    private JButton saveSVGButton;
    private JPanel panelPerformance;
    private JButton randomButton100;

    private ChartPanel chartPanel = null;
    private JFileChooser fileChooserSave = null;

    public AppFrame(String title) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();

        JTableUtils.initJTableForArray(tableArr, 60, false, true, false , true);
        tableArr.setRowHeight(30);

        fileChooserSave = new JFileChooser();
        fileChooserSave.setCurrentDirectory(new File("./src"));
        FileFilter filter = new FileNameExtensionFilter("SVG images", "svg");
        fileChooserSave.addChoosableFileFilter(filter);
        fileChooserSave.setAcceptAllFileFilterUsed(false);
        fileChooserSave.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooserSave.setApproveButtonText("Save");


        randomButton10.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] arr = ArrayUtils.createRandomIntArray(10, 100);
                JTableUtils.writeArrayToJTable(tableArr, arr);
            }
        });

        randomButton100.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int[] arr = ArrayUtils.createRandomIntArray(100, 1000);
                JTableUtils.writeArrayToJTable(tableArr, arr);
            }
        });

        randomButton1000.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int[] arr = ArrayUtils.createRandomIntArray(1000, 10000);
                JTableUtils.writeArrayToJTable(tableArr, arr);
            }
        });

        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sortDemo((arr) -> RadixSort.sort(arr, 10));
            }
        });
        compareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] sortNames = {
                        "Встроенная (Arrays.sort)",
                        "Поразрядная (RadixSort)"
                };
                Consumer<Integer[]>[] sorts = new Consumer[]{
                        (Consumer<Integer[]>) (arr) -> Arrays.sort(arr),
                        (Consumer<Integer[]>) (arr) -> RadixSort.sort(arr, 10)
                };
                int[] sizes = {
                        1000, 2000, 3000, 4000, 5000,
                        6000, 7000, 8000, 9000, 10000,
                        20000, 50000, 100000, 200000
                };
                performanceTestDemo(sortNames, sorts, sizes);
            }
        });

        saveSVGButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (chartPanel == null) {
                    return;
                }
                try {
                    if (fileChooserSave.showSaveDialog(AppFrame.this) == JFileChooser.APPROVE_OPTION) {
                        String filename = fileChooserSave.getSelectedFile().getPath();
                        if (!filename.toLowerCase().endsWith(".svg")) {
                            filename += ".svg";
                        }
                        saveChartIntoFile(filename);
                        JOptionPane.showMessageDialog(AppFrame.this,
                                "Файл '" + fileChooserSave.getSelectedFile() + "' успешно сохранен");
                    }
                } catch (Exception e) {
                    SwingUtils.showErrorMessageBox(e);
                }
            }
        });
    }

    public static boolean checkSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) {
                return false;
            }
        }
        return true;
    }

    private void chartDefault(JFreeChart chart) { //Настройка диаграммы
        XYPlot plot = chart.getXYPlot();
        XYDataset ds = plot.getDataset();

        for (int i = 0; i < ds.getSeriesCount(); i++) {
            chart.getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2));
        }

        Font font = compareButton.getFont();
        chart.getLegend().setItemFont(font);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.getRangeAxis().setTickLabelFont(font);
        plot.getRangeAxis().setLabelFont(font);
        plot.getDomainAxis().setTickLabelFont(font);
        plot.getDomainAxis().setLabelFont(font);
    }

    private void saveChartIntoFile(String filename) throws IOException { //Сохранение диаграммы в svg файл
        JFreeChart chart = chartPanel.getChart();
        SVGGraphics2D g2 = new SVGGraphics2D(800, 600);
        Rectangle r = new Rectangle(0, 0, 800, 600);
        chart.draw(g2, r);
        SVGUtils.writeToSVG(new File(filename), g2.getSVGElement());
    }

    private static double[][] performanceTest(Consumer<Integer[]>[] sorts, int[] sizes) {
        Random rnd = new Random();
        double[][] result = new double[sorts.length][sizes.length];

        // надо, по правилам, многократно тестировать, но и так сойдет
        for (int i = 0; i < sizes.length; i++) {
            Integer[] arr1 = new Integer[sizes[i]];
            Integer[] arr2 = new Integer[sizes[i]];
            for (int j = 0; j < arr1.length; j++) {
                arr1[j] = rnd.nextInt((int) 1E6);
            }

            for (int j = 0; j < sorts.length; j++) {
                long moment1, moment2;
                System.arraycopy(arr1, 0, arr2, 0, arr1.length);
                System.gc();
                moment1 = System.nanoTime();
                sorts[j].accept(arr2);
                moment2 = System.nanoTime();
                result[j][i] = (moment2 - moment1) / 1E6;
            }
        }

        return result;
    }

    private void performanceTestDemo(String[] sortNames, Consumer<Integer[]>[] sorts, int[] sizes) {
        double[][] result = performanceTest(sorts, sizes);

        DefaultXYDataset ds = new DefaultXYDataset();
        double[][] data = new double[2][result.length];
        data[0] = Arrays.stream(sizes).asDoubleStream().toArray();
        for (int i = 0; i < sorts.length; i++) {
            data = data.clone();
            data[1] = result[i];
            ds.addSeries(sortNames[i], data);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Быстродействие сортировок",
                "Размерность массива, элементов",
                "Время выполнения, мс",
                ds
        );
        chartDefault(chart);


        if (chartPanel == null) {
            chartPanel = new ChartPanel(chart);
            panelPerformance.add(chartPanel, BorderLayout.CENTER);
            panelPerformance.updateUI();
        } else {
            chartPanel.setChart(chart);
        }
    }

    private void sortDemo(Consumer<Integer[]> sort) {
        try {
            Integer[] arr = ArrayUtils.toObject(JTableUtils.readIntArrayFromJTable(tableArr));

            sort.accept(arr);

            int[] primitiveArr = ArrayUtils.toPrimitive(arr);
            JTableUtils.writeArrayToJTable(tableArr, primitiveArr);

            if (!checkSorted(primitiveArr)) {
                SwingUtils.showInfoMessageBox("Массив неправильно отсортирован");
            }

        } catch (Exception ex) {
            SwingUtils.showErrorMessageBox(ex);
        }
    }


}
