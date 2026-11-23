package arithmetic;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV 导出服务。
 *
 * <p>负责把题目按“每行 N 列”排版并写入 CSV 文件。文件格式约定：</p>
 * <ul>
 *   <li>UTF-8 with BOM，保证 Excel/WPS 打开中文和 ×÷ 不乱码；</li>
 *   <li>CRLF 换行，逗号分隔，不写表头；</li>
 *   <li>每个单元格为“题面=答案”，例如 3×4+5=17；</li>
 *   <li>行数为 ceil(题量/列数)，最后一行不足时补空单元格。</li>
 * </ul>
 *
 * <p>界面预览和文件导出都调用 {@link #layout(List, int)}，从源头上保证
 * “预览内容与 CSV 内容完全一致”。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public final class CsvExporter {

    /** 允许的最小题量。 */
    public static final int MIN_COUNT = 1;

    /** 允许的最大题量。 */
    public static final int MAX_COUNT = 200;

    /** 允许的最小列数。 */
    public static final int MIN_COLUMNS = 1;

    /** 允许的最大列数。 */
    public static final int MAX_COLUMNS = 10;

    /** UTF-8 BOM 的三个字节。 */
    private static final byte[] UTF8_BOM = {
            (byte) 0xEF, (byte) 0xBB, (byte) 0xBF
    };

    /** 工具类不允许实例化。 */
    private CsvExporter() {
    }

    /**
     * 校验题量是否在 1~200 之间。
     *
     * @param count 题量
     * @throws IllegalArgumentException 当题量越界时抛出
     */
    public static void validateCount(int count) {
        if (count < MIN_COUNT || count > MAX_COUNT) {
            throw new IllegalArgumentException(
                    "题量必须在 " + MIN_COUNT + "~" + MAX_COUNT + " 之间：" + count);
        }
    }

    /**
     * 校验列数是否在 1~10 之间。
     *
     * @param columns 列数
     * @throws IllegalArgumentException 当列数越界时抛出
     */
    public static void validateColumns(int columns) {
        if (columns < MIN_COLUMNS || columns > MAX_COLUMNS) {
            throw new IllegalArgumentException(
                    "每行列数必须在 " + MIN_COLUMNS + "~" + MAX_COLUMNS
                            + " 之间：" + columns);
        }
    }

    /**
     * 计算指定题量和列数所需的行数。
     *
     * @param questionCount 题量
     * @param columns       列数
     * @return 行数
     */
    public static int rowCount(int questionCount, int columns) {
        validateCount(questionCount);
        validateColumns(columns);
        return (questionCount + columns - 1) / columns;
    }

    /**
     * 把题目排版为二维单元格文本。
     *
     * @param questions 题目列表
     * @param columns   每行列数
     * @return 行列表，每行恰好包含 columns 个单元格
     * @throws IllegalArgumentException 当题目数量或列数越界时抛出
     */
    public static List<List<String>> layout(List<Question> questions, int columns) {
        if (questions == null) {
            throw new IllegalArgumentException("题目列表不能为空");
        }
        validateCount(questions.size());
        validateColumns(columns);
        int rows = rowCount(questions.size(), columns);
        List<List<String>> result = new ArrayList<>(rows);
        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            List<String> row = new ArrayList<>(columns);
            for (int columnIndex = 0; columnIndex < columns; columnIndex++) {
                int questionIndex = rowIndex * columns + columnIndex;
                if (questionIndex < questions.size()) {
                    row.add(formatCell(questions.get(questionIndex)));
                } else {
                    row.add("");
                }
            }
            result.add(row);
        }
        return result;
    }

    /**
     * 把一道题格式化为“题面=答案”。
     *
     * @param question 题目
     * @return 单元格文本
     */
    public static String formatCell(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("题目不能为空");
        }
        return question.getExpression() + "=" + question.getAnswer();
    }

    /**
     * 把题目导出为 CSV 文件。
     *
     * @param questions 题目列表
     * @param columns   每行列数
     * @param path      目标文件路径
     * @return 导出结果
     * @throws IOException 当文件写入失败时抛出
     */
    public static CsvExportResult export(List<Question> questions, int columns, Path path)
            throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("导出路径不能为空");
        }
        List<List<String>> rows = layout(questions, columns);
        StringBuilder content = new StringBuilder();
        for (List<String> row : rows) {
            for (int index = 0; index < row.size(); index++) {
                if (index > 0) {
                    content.append(',');
                }
                content.append(row.get(index));
            }
            content.append("\r\n");
        }

        Path absolutePath = path.toAbsolutePath().normalize();
        Path parent = absolutePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (OutputStream output = Files.newOutputStream(absolutePath)) {
            output.write(UTF8_BOM);
            output.write(content.toString().getBytes(StandardCharsets.UTF_8));
        }
        return new CsvExportResult(absolutePath, questions.size(), rows.size(), columns);
    }
}
