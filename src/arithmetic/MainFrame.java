package arithmetic;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * 程序主窗口。
 *
 * <p>提供题型选择、题量输入、每行列数输入、题目预览、CSV 路径选择和导出功能。
 * 界面层只负责收集输入和展示结果，题目生成委托给 {@link QuestionGenerator}，
 * CSV 排版与写入委托给 {@link CsvExporter}，按钮监听器中没有生成算法或文件
 * 写入细节，体现单一职责原则。</p>
 *
 * <p>预览区使用 JTable 按“每行 N 列”显示，单元格内容与导出的 CSV 完全一致。
 * 输入非法时使用 JOptionPane 给出明确中文提示，并将焦点移回出错的输入框。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 2L;

    /** 默认题量。 */
    private static final int DEFAULT_COUNT = 30;

    /** 默认每行列数。 */
    private static final int DEFAULT_COLUMNS = 5;

    /** 界面使用的字体名称。 */
    private static final String UI_FONT = pickUiFont();

    private final JComboBox<QuestionType> typeBox =
            new JComboBox<>(QuestionType.values());
    private final JTextField countField =
            new JTextField(String.valueOf(DEFAULT_COUNT), 5);
    private final JTextField columnField =
            new JTextField(String.valueOf(DEFAULT_COLUMNS), 5);
    private final JButton generateButton = new JButton("生成题目");
    private final JTextField pathField = new JTextField();
    private final JButton browseButton = new JButton("浏览...");
    private final JButton exportButton = new JButton("导出CSV");
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable previewTable = new JTable(tableModel);
    private final JLabel previewHintLabel = new JLabel();
    private final JLabel statusLabel = new JLabel();
    private final Random random = new Random();

    /** 当前已生成的题目。 */
    private List<Question> currentQuestions;

    /** 当前题目对应的题量。 */
    private int generatedCount;

    /** 当前题目对应的列数。 */
    private int generatedColumns = DEFAULT_COLUMNS;

    /** 当前题目对应的题型。 */
    private QuestionType generatedType;

    /** 上一次自动生成的建议路径，用于判断是否应随题量/列数更新。 */
    private String suggestedPath;

    /**
     * 创建并初始化主窗口。
     */
    public MainFrame() {
        super("小学四则运算练习题生成器（实验2）");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
    }

    /**
     * 初始化界面组件与事件监听。
     */
    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBorder(new EmptyBorder(14, 20, 12, 20));

        root.add(createNorthPanel(), BorderLayout.NORTH);
        root.add(createPreviewPanel(), BorderLayout.CENTER);
        root.add(createStatusPanel(), BorderLayout.SOUTH);

        setContentPane(root);
        setPreferredSize(new Dimension(1200, 820));
        setMinimumSize(new Dimension(960, 640));
        pack();
        setLocationRelativeTo(null);

        typeBox.setSelectedItem(QuestionType.THREE_FOUR_MIXED);
        generateButton.addActionListener(this::onGenerate);
        exportButton.addActionListener(this::onExport);
        browseButton.addActionListener(this::onBrowse);
        countField.addActionListener(this::onGenerate);
        columnField.addActionListener(this::onGenerate);

        updateSuggestedPath(DEFAULT_COUNT, DEFAULT_COLUMNS);
        showWelcome();
    }

    /**
     * 创建标题与控制栏区域。
     *
     * @return 北侧面板
     */
    private JPanel createNorthPanel() {
        JLabel titleLabel = new JLabel("小学四则运算练习题生成器", SwingConstants.CENTER);
        titleLabel.setFont(new Font(UI_FONT, Font.BOLD, 22));
        titleLabel.setForeground(new Color(0x1F4E79));

        JLabel subtitleLabel = new JLabel(
                "软件构造实验 2 · 加减乘除 · 可配置题量与列数 · CSV 导出 · JUnit 测试",
                SwingConstants.CENTER);
        subtitleLabel.setFont(new Font(UI_FONT, Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(0x666666));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 6));
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        controlPanel.add(createFirstControlRow());
        controlPanel.add(createSecondControlRow());

        JPanel northPanel = new JPanel(new BorderLayout(0, 12));
        northPanel.add(titlePanel, BorderLayout.NORTH);
        northPanel.add(controlPanel, BorderLayout.SOUTH);
        return northPanel;
    }

    /**
     * 创建第一行控件：题型、题量、列数和生成按钮。
     *
     * @return 控件面板
     */
    private JPanel createFirstControlRow() {
        Font controlFont = new Font(UI_FONT, Font.PLAIN, 14);
        Font hintFont = new Font(UI_FONT, Font.PLAIN, 12);

        JLabel typeLabel = new JLabel("题型：");
        typeLabel.setFont(controlFont);
        typeBox.setFont(controlFont);
        typeBox.setPreferredSize(new Dimension(170, 30));

        JLabel countLabel = new JLabel("题量：");
        countLabel.setFont(controlFont);
        countField.setFont(controlFont);
        countField.setHorizontalAlignment(JTextField.CENTER);
        countField.setPreferredSize(new Dimension(70, 30));
        countField.setToolTipText("请输入 1~200 之间的整数");

        JLabel countRangeLabel = new JLabel("（1~200 道）");
        countRangeLabel.setFont(hintFont);
        countRangeLabel.setForeground(new Color(0x777777));

        JLabel columnLabel = new JLabel("每行列数：");
        columnLabel.setFont(controlFont);
        columnField.setFont(controlFont);
        columnField.setHorizontalAlignment(JTextField.CENTER);
        columnField.setPreferredSize(new Dimension(70, 30));
        columnField.setToolTipText("请输入 1~10 之间的整数");

        JLabel columnRangeLabel = new JLabel("（1~10 列）");
        columnRangeLabel.setFont(hintFont);
        columnRangeLabel.setForeground(new Color(0x777777));

        generateButton.setFont(controlFont);
        generateButton.setPreferredSize(new Dimension(110, 32));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row.add(typeLabel);
        row.add(typeBox);
        row.add(Box.createHorizontalStrut(10));
        row.add(countLabel);
        row.add(countField);
        row.add(countRangeLabel);
        row.add(Box.createHorizontalStrut(10));
        row.add(columnLabel);
        row.add(columnField);
        row.add(columnRangeLabel);
        row.add(Box.createHorizontalStrut(10));
        row.add(generateButton);
        return row;
    }

    /**
     * 创建第二行控件：导出路径、浏览按钮和导出按钮。
     *
     * @return 控件面板
     */
    private JPanel createSecondControlRow() {
        Font controlFont = new Font(UI_FONT, Font.PLAIN, 14);
        JLabel pathLabel = new JLabel("导出路径：");
        pathLabel.setFont(controlFont);

        pathField.setFont(controlFont);
        pathField.setPreferredSize(new Dimension(600, 30));
        pathField.setToolTipText("CSV 文件的完整路径，默认文件名随题量和列数变化");

        browseButton.setFont(controlFont);
        browseButton.setPreferredSize(new Dimension(90, 32));
        exportButton.setFont(controlFont);
        exportButton.setPreferredSize(new Dimension(110, 32));

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        labelPanel.add(pathLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        buttonPanel.add(browseButton);
        buttonPanel.add(exportButton);

        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.add(labelPanel, BorderLayout.WEST);
        row.add(pathField, BorderLayout.CENTER);
        row.add(buttonPanel, BorderLayout.EAST);
        return row;
    }

    /**
     * 创建题目预览区域。
     *
     * @return 预览面板
     */
    private JPanel createPreviewPanel() {
        previewHintLabel.setFont(new Font(UI_FONT, Font.PLAIN, 12));
        previewHintLabel.setForeground(new Color(0x666666));
        previewHintLabel.setBorder(new EmptyBorder(2, 4, 2, 4));

        previewTable.setTableHeader(null);
        previewTable.setFont(new Font(UI_FONT, Font.PLAIN, 14));
        previewTable.setRowHeight(30);
        previewTable.setShowGrid(true);
        previewTable.setGridColor(new Color(0xD0D0D0));
        previewTable.setIntercellSpacing(new Dimension(1, 1));
        previewTable.setFillsViewportHeight(true);
        previewTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        previewTable.setFocusable(false);
        previewTable.setRowSelectionAllowed(false);
        previewTable.setColumnSelectionAllowed(false);
        previewTable.setCellSelectionEnabled(false);
        previewTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected,
                                                           boolean hasFocus,
                                                           int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(0, 6, 0, 6));
                if (!isSelected) {
                    setBackground(Color.WHITE);
                    setForeground(new Color(0x1F1F1F));
                }
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(previewTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        TitledBorder border = BorderFactory.createTitledBorder("题目预览（与导出的 CSV 内容一致）");
        border.setTitleFont(new Font(UI_FONT, Font.BOLD, 13));
        scrollPane.setBorder(border);

        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.add(previewHintLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 创建底部状态栏。
     *
     * @return 状态栏面板
     */
    private JPanel createStatusPanel() {
        statusLabel.setFont(new Font(UI_FONT, Font.PLAIN, 13));
        statusLabel.setForeground(new Color(0x333333));

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(4, 0, 0, 0));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        return statusPanel;
    }

    /**
     * 生成题目按钮的事件处理。
     *
     * @param event 动作事件
     */
    private void onGenerate(ActionEvent event) {
        Integer count = readInt(countField, "题量", CsvExporter.MIN_COUNT,
                CsvExporter.MAX_COUNT);
        if (count == null) {
            return;
        }
        Integer columns = readInt(columnField, "每行列数", CsvExporter.MIN_COLUMNS,
                CsvExporter.MAX_COLUMNS);
        if (columns == null) {
            return;
        }
        QuestionType type = (QuestionType) typeBox.getSelectedItem();
        try {
            QuestionGenerator generator = QuestionGeneratorFactory.create(type, random);
            currentQuestions = generator.generate(count);
            generatedCount = count;
            generatedColumns = columns;
            generatedType = type;
            updateSuggestedPath(count, columns);
            renderPreview();
            statusLabel.setText(String.format(
                    "题型：%s｜题量：%d｜布局：%d 行 × %d 列｜状态：生成成功",
                    type, count, CsvExporter.rowCount(count, columns), columns));
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "生成题目时发生错误：" + ex.getMessage(),
                    "生成失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 导出 CSV 按钮的事件处理。
     *
     * @param event 动作事件
     */
    private void onExport(ActionEvent event) {
        if (currentQuestions == null || currentQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "请先点击“生成题目”生成练习题，再导出 CSV。",
                    "尚未生成题目", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Integer count = readInt(countField, "题量", CsvExporter.MIN_COUNT,
                CsvExporter.MAX_COUNT);
        if (count == null) {
            return;
        }
        Integer columns = readInt(columnField, "每行列数", CsvExporter.MIN_COLUMNS,
                CsvExporter.MAX_COLUMNS);
        if (columns == null) {
            return;
        }
        QuestionType type = (QuestionType) typeBox.getSelectedItem();
        if (type != generatedType) {
            JOptionPane.showMessageDialog(this,
                    "题型已修改，请重新点击“生成题目”后再导出。",
                    "题型不一致", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (count != generatedCount) {
            JOptionPane.showMessageDialog(this,
                    "题量与已生成题目数量不一致，请重新点击“生成题目”后再导出。",
                    "题量不一致", JOptionPane.WARNING_MESSAGE);
            countField.requestFocusInWindow();
            countField.selectAll();
            return;
        }
        if (columns != generatedColumns) {
            generatedColumns = columns;
            renderPreview();
        }

        String pathText = pathField.getText().trim();
        if (pathText.isEmpty()) {
            showInputError(pathField, "导出路径不能为空，请选择或输入 CSV 文件路径。");
            return;
        }
        Path path;
        try {
            path = Paths.get(pathText);
        } catch (InvalidPathException ex) {
            showInputError(pathField, "导出路径格式不正确，请重新输入。");
            return;
        }
        if (Files.isDirectory(path)) {
            showInputError(pathField, "导出路径是文件夹，请填写完整的 CSV 文件名。");
            return;
        }
        if (path.getFileName() == null) {
            showInputError(pathField, "导出路径缺少文件名，请填写完整的 CSV 文件路径。");
            return;
        }
        if (!path.getFileName().toString().toLowerCase(Locale.ROOT)
                .endsWith(".csv")) {
            path = path.resolveSibling(path.getFileName() + ".csv");
            pathField.setText(path.toAbsolutePath().toString());
        }

        try {
            CsvExportResult result = CsvExporter.export(currentQuestions, generatedColumns, path);
            statusLabel.setText(String.format(
                    "题型：%s｜题量：%d｜布局：%d 行 × %d 列｜状态：已导出至 %s",
                    generatedType, result.getQuestionCount(), result.getRowCount(),
                    result.getColumnCount(), result.getPath()));
            JOptionPane.showMessageDialog(this,
                    "导出成功！\n\n文件路径：" + result.getPath()
                            + "\n题量：" + result.getQuestionCount() + " 道"
                            + "\n布局：" + result.getRowCount() + " 行 × "
                            + result.getColumnCount() + " 列",
                    "导出成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "导出 CSV 失败：" + ex.getMessage(),
                    "导出失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 浏览按钮的事件处理，选择 CSV 保存路径。
     *
     * @param event 动作事件
     */
    private void onBrowse(ActionEvent event) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择 CSV 导出路径");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV 文件 (*.csv)", "csv"));
        String currentText = pathField.getText().trim();
        if (!currentText.isEmpty()) {
            try {
                Path currentPath = Paths.get(currentText);
                Path parent = currentPath.getParent();
                if (parent != null && Files.isDirectory(parent)) {
                    chooser.setCurrentDirectory(parent.toFile());
                }
                Path fileName = currentPath.getFileName();
                if (fileName != null) {
                    chooser.setSelectedFile(fileName.toFile());
                }
            } catch (InvalidPathException ignored) {
                // 当前路径非法时使用默认目录
            }
        }
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
                file = new File(file.getParentFile(), file.getName() + ".csv");
            }
            pathField.setText(file.getAbsolutePath());
        }
    }

    /**
     * 读取并校验整数输入。
     *
     * @param field 输入框
     * @param name  字段中文名
     * @param min   最小值
     * @param max   最大值
     * @return 合法整数；非法时返回 null 并弹出提示
     */
    private Integer readInt(JTextField field, String name, int min, int max) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            showInputError(field, name + "不能为空，请输入 " + min + "~" + max
                    + " 之间的整数。");
            return null;
        }
        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            showInputError(field, name + "必须是整数，请输入 " + min + "~" + max
                    + " 之间的整数，不要输入字母或小数。");
            return null;
        }
        if (value < min || value > max) {
            showInputError(field, name + "必须在 " + min + "~" + max
                    + " 之间，请重新输入。");
            return null;
        }
        return value;
    }

    /**
     * 显示输入错误提示，并把焦点移回出错的输入框。
     *
     * @param field   出错的输入框
     * @param message 错误提示
     */
    private void showInputError(JTextField field, String message) {
        JOptionPane.showMessageDialog(this,
                "<html><b>输入有误</b><br><br>" + message + "</html>",
                "输入有误", JOptionPane.WARNING_MESSAGE);
        field.requestFocusInWindow();
        field.selectAll();
    }

    /**
     * 根据题量和列数更新默认导出路径。
     *
     * <p>只有当用户没有手动修改路径时才自动更新，避免覆盖用户的选择。</p>
     *
     * @param count   题量
     * @param columns 列数
     */
    private void updateSuggestedPath(int count, int columns) {
        String fileName = "四则运算练习_" + count + "题_" + columns + "列.csv";
        String candidate = new File(System.getProperty("user.dir"), fileName)
                .getAbsolutePath();
        String current = pathField.getText().trim();
        if (current.isEmpty() || current.equals(suggestedPath)) {
            pathField.setText(candidate);
            suggestedPath = candidate;
        }
    }

    /**
     * 将当前题目刷新到预览表格。
     */
    private void renderPreview() {
        if (currentQuestions == null || currentQuestions.isEmpty()) {
            tableModel.setDataVector(new Object[0][0], new Object[0]);
            previewTable.setTableHeader(null);
            previewHintLabel.setText("尚未生成题目；点击“生成题目”后按每行 "
                    + generatedColumns + " 列显示，内容与导出的 CSV 完全一致。");
            return;
        }
        List<List<String>> rows = CsvExporter.layout(currentQuestions, generatedColumns);
        Object[] columnNames = new Object[generatedColumns];
        Arrays.fill(columnNames, "");
        Object[][] data = new Object[rows.size()][generatedColumns];
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < generatedColumns; columnIndex++) {
                data[rowIndex][columnIndex] = rows.get(rowIndex).get(columnIndex);
            }
        }
        tableModel.setDataVector(data, columnNames);
        previewTable.setTableHeader(null);
        previewHintLabel.setText(String.format(
                "当前布局：每行 %d 列，共 %d 行；单元格内容与导出的 CSV 完全一致。",
                generatedColumns, rows.size()));
        previewTable.scrollRectToVisible(previewTable.getCellRect(0, 0, true));
    }

    /**
     * 恢复初始欢迎状态。
     */
    private void showWelcome() {
        currentQuestions = new java.util.ArrayList<>();
        generatedCount = 0;
        generatedColumns = DEFAULT_COLUMNS;
        generatedType = null;
        tableModel.setDataVector(new Object[0][0], new Object[0]);
        previewTable.setTableHeader(null);
        previewHintLabel.setText("尚未生成题目；点击“生成题目”后按每行 "
                + DEFAULT_COLUMNS + " 列显示，内容与导出的 CSV 完全一致。");
        statusLabel.setText("就绪：请选择题型、题量与列数，然后点击“生成题目”。");
    }

    /**
     * 从系统可用字体中选择一款适合显示中文的字体。
     *
     * @return 字体名称
     */
    private static String pickUiFont() {
        String[] candidates = {
                "Microsoft YaHei", "微软雅黑", "Noto Sans CJK SC",
                "Source Han Sans SC", "PingFang SC",
                "WenQuanYi Micro Hei", "SimSun"
        };
        List<String> available = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames());
        for (String candidate : candidates) {
            if (available.contains(candidate)) {
                return candidate;
            }
        }
        return Font.SANS_SERIF;
    }
}
