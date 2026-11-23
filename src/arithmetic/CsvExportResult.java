package arithmetic;

import java.nio.file.Path;

/**
 * CSV 导出结果。
 *
 * <p>封装导出后的完整路径、题量、行数和列数，供界面弹窗和状态栏显示。
 * 使用不可变对象，避免界面层修改结果。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public final class CsvExportResult {

    private final Path path;
    private final int questionCount;
    private final int rowCount;
    private final int columnCount;

    /**
     * 创建导出结果。
     *
     * @param path          导出的完整路径
     * @param questionCount 题量
     * @param rowCount      行数
     * @param columnCount   列数
     */
    public CsvExportResult(Path path, int questionCount, int rowCount, int columnCount) {
        if (path == null) {
            throw new IllegalArgumentException("导出路径不能为空");
        }
        this.path = path;
        this.questionCount = questionCount;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
    }

    /**
     * 返回导出的完整路径。
     *
     * @return 完整路径
     */
    public Path getPath() {
        return path;
    }

    /**
     * 返回题量。
     *
     * @return 题量
     */
    public int getQuestionCount() {
        return questionCount;
    }

    /**
     * 返回行数。
     *
     * @return 行数
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * 返回列数。
     *
     * @return 列数
     */
    public int getColumnCount() {
        return columnCount;
    }
}
