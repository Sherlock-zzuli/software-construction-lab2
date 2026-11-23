package arithmetic;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * CSV 导出服务的 JUnit 测试。
 *
 * <p>覆盖 30 题 5 列、1 题 1 列、200 题 10 列、31 题 5 列等边界，
 * 检查行列布局、UTF-8 BOM、CRLF、逗号分隔、无表头、单元格格式，
 * 以及预览布局与文件内容的一致性。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class CsvExporterTest {

    /** 固定随机种子。 */
    private static final long SEED = 20261123L;

    /** JUnit 提供的临时目录，测试结束后自动清理。 */
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * 生成指定数量的四则混合题。
     *
     * @param count 题量
     * @return 题目列表
     */
    private static List<Question> generate(int count) {
        QuestionGenerator generator = QuestionGeneratorFactory.create(
                QuestionType.THREE_FOUR_MIXED, new Random(SEED));
        return generator.generate(count);
    }

    @Test
    public void thirtyQuestionsFiveColumnsProduceSixRowsAndFiveColumns() {
        List<Question> questions = generate(30);
        List<List<String>> rows = CsvExporter.layout(questions, 5);
        assertEquals(6, rows.size());
        for (List<String> row : rows) {
            assertEquals(5, row.size());
        }
        assertEquals(CsvExporter.formatCell(questions.get(0)), rows.get(0).get(0));
        assertEquals(CsvExporter.formatCell(questions.get(29)), rows.get(5).get(4));
    }

    @Test
    public void csvStartsWithUtf8BomAndUsesCrlfAndComma() throws IOException {
        List<Question> questions = generate(30);
        Path file = temporaryFolder.newFile("bom.csv").toPath();
        CsvExporter.export(questions, 5, file);

        byte[] bytes = Files.readAllBytes(file);
        assertTrue("文件至少应有 BOM 和内容", bytes.length > 3);
        assertEquals("UTF-8 BOM 第 1 字节", (byte) 0xEF, bytes[0]);
        assertEquals("UTF-8 BOM 第 2 字节", (byte) 0xBB, bytes[1]);
        assertEquals("UTF-8 BOM 第 3 字节", (byte) 0xBF, bytes[2]);

        String content = new String(bytes, 3, bytes.length - 3,
                StandardCharsets.UTF_8);
        assertTrue("应使用 CRLF 换行", content.contains("\r\n"));
        for (int index = 0; index < content.length(); index++) {
            if (content.charAt(index) == '\n') {
                assertTrue("发现孤立的 LF 换行",
                        index > 0 && content.charAt(index - 1) == '\r');
            }
        }
        String[] lines = content.split("\r\n", -1);
        assertEquals("6 行数据 + 末尾空串", 7, lines.length);
        for (int row = 0; row < 6; row++) {
            long commaCount = lines[row].chars().filter(ch -> ch == ',').count();
            assertEquals("第 " + (row + 1) + " 行应有 4 个逗号", 4, commaCount);
        }
        assertTrue("CSV 应包含乘号或除号", content.contains("×") || content.contains("÷"));
        assertFalse("CSV 不应出现乱码替换符", content.contains("�"));
    }

    @Test
    public void csvCellFormatIsExpressionEqualsAnswer() throws IOException {
        List<Question> questions = generate(30);
        Path file = temporaryFolder.newFile("cells.csv").toPath();
        CsvExporter.export(questions, 5, file);
        String content = new String(Files.readAllBytes(file), 3,
                (int) (Files.size(file) - 3), StandardCharsets.UTF_8);
        String firstCell = content.split("\r\n", -1)[0].split(",", -1)[0];
        assertTrue("单元格格式应为“题面=答案”：" + firstCell,
                firstCell.matches(".+=[0-9]+"));
        int equalsIndex = firstCell.lastIndexOf('=');
        String expression = firstCell.substring(0, equalsIndex);
        int answer = Integer.parseInt(firstCell.substring(equalsIndex + 1));
        assertEquals(answer, ExpressionEvaluator.evaluate(expression));
    }

    @Test
    public void boundaryOneQuestionOneColumn() {
        List<Question> questions = generate(1);
        List<List<String>> rows = CsvExporter.layout(questions, 1);
        assertEquals(1, rows.size());
        assertEquals(1, rows.get(0).size());
        assertEquals(CsvExporter.formatCell(questions.get(0)), rows.get(0).get(0));
    }

    @Test
    public void boundaryTwoHundredQuestionsTenColumns() {
        List<Question> questions = generate(200);
        List<List<String>> rows = CsvExporter.layout(questions, 10);
        assertEquals(20, rows.size());
        for (List<String> row : rows) {
            assertEquals(10, row.size());
        }
        assertEquals(CsvExporter.formatCell(questions.get(199)), rows.get(19).get(9));
    }

    @Test
    public void boundaryThirtyOneQuestionsFiveColumnsHasSevenRowsAndPaddedLastRow() {
        List<Question> questions = generate(31);
        List<List<String>> rows = CsvExporter.layout(questions, 5);
        assertEquals(7, rows.size());
        assertEquals(5, rows.get(6).size());
        assertFalse("最后一行第一个单元格应有题目", rows.get(6).get(0).isEmpty());
        for (int column = 1; column < 5; column++) {
            assertTrue("最后一行不足部分应补空单元格",
                    rows.get(6).get(column).isEmpty());
        }
    }

    @Test
    public void exportResultContainsPathCountAndLayout() throws IOException {
        List<Question> questions = generate(30);
        Path file = temporaryFolder.newFile("result.csv").toPath();
        CsvExportResult result = CsvExporter.export(questions, 5, file);
        assertEquals(file.toAbsolutePath().normalize(), result.getPath());
        assertEquals(30, result.getQuestionCount());
        assertEquals(6, result.getRowCount());
        assertEquals(5, result.getColumnCount());
    }

    @Test
    public void exportCreatesMissingParentDirectories() throws IOException {
        List<Question> questions = generate(5);
        Path nested = temporaryFolder.getRoot().toPath()
                .resolve("a").resolve("b").resolve("c.csv");
        CsvExporter.export(questions, 5, nested);
        assertTrue(Files.exists(nested));
        assertTrue(Files.size(nested) > 3);
    }

    @Test
    public void previewLayoutMatchesCsvContentLineByLine() throws IOException {
        List<Question> questions = generate(31);
        Path file = temporaryFolder.newFile("preview.csv").toPath();
        CsvExporter.export(questions, 5, file);
        String content = new String(Files.readAllBytes(file), 3,
                (int) (Files.size(file) - 3), StandardCharsets.UTF_8);
        String[] lines = content.split("\r\n", -1);
        List<List<String>> layout = CsvExporter.layout(questions, 5);
        assertEquals(layout.size(), lines.length - 1);
        for (int row = 0; row < layout.size(); row++) {
            assertEquals(String.join(",", layout.get(row)), lines[row]);
        }
    }

    @Test
    public void csvHasNoHeaderAndStartsWithAQuestion() throws IOException {
        List<Question> questions = generate(30);
        Path file = temporaryFolder.newFile("no-header.csv").toPath();
        CsvExporter.export(questions, 5, file);
        String content = new String(Files.readAllBytes(file), 3,
                (int) (Files.size(file) - 3), StandardCharsets.UTF_8);
        String firstLine = content.split("\r\n", -1)[0];
        assertFalse(firstLine.contains("题面"));
        assertFalse(firstLine.contains("答案"));
        assertTrue("第一行应以数字题面开头",
                Character.isDigit(firstLine.charAt(0)));
    }

    @Test
    public void invalidCountZeroIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> CsvExporter.validateCount(0));
        assertThrows(IllegalArgumentException.class,
                () -> CsvExporter.layout(Collections.emptyList(), 5));
        assertThrows(IllegalArgumentException.class, () -> CsvExporter.rowCount(0, 5));
    }

    @Test
    public void invalidCountTwoHundredOneIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> CsvExporter.validateCount(201));
        assertThrows(IllegalArgumentException.class, () -> CsvExporter.rowCount(201, 5));
    }

    @Test
    public void invalidColumnsZeroIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> CsvExporter.validateColumns(0));
        assertThrows(IllegalArgumentException.class,
                () -> CsvExporter.layout(generate(5), 0));
    }

    @Test
    public void invalidColumnsElevenIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> CsvExporter.validateColumns(11));
        assertThrows(IllegalArgumentException.class,
                () -> CsvExporter.layout(generate(5), 11));
    }

    @Test
    public void csvRoundTripPreservesEveryCell() throws IOException {
        List<Question> questions = generate(30);
        Path file = temporaryFolder.newFile("round-trip.csv").toPath();
        CsvExporter.export(questions, 5, file);
        String content = new String(Files.readAllBytes(file), 3,
                (int) (Files.size(file) - 3), StandardCharsets.UTF_8);
        String[] lines = content.split("\r\n", -1);
        for (int index = 0; index < questions.size(); index++) {
            int row = index / 5;
            int column = index % 5;
            String[] cells = lines[row].split(",", -1);
            assertEquals("第 " + (index + 1) + " 题",
                    CsvExporter.formatCell(questions.get(index)), cells[column]);
        }
    }
}
