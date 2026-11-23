package arithmetic;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * 题目模型与生成器的 JUnit 测试。
 *
 * <p>覆盖二元加减乘除各 1000 题、三元各题型各 500 题、16 种运算符组合
 * 各 500 题，并用独立的 {@link ExpressionEvaluator} 对每道题的题面重新求值，
 * 验证题面与答案一致。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class QuestionGeneratorTest {

    /** 固定随机种子，保证测试可重复。 */
    private static final long SEED = 20261123L;

    /**
     * 使用固定种子生成指定题型的题目。
     *
     * @param type  题型
     * @param count 题量
     * @return 题目列表
     */
    private static List<Question> generate(QuestionType type, int count) {
        QuestionGenerator generator = QuestionGeneratorFactory.create(type, new Random(SEED));
        return generator.generate(count);
    }

    /**
     * 生成指定数量的题目样本，允许表达式重复。
     *
     * <p>用于 1000 题压力测试。二元乘法、二元除法的不同表达式总数小于 1000，
     * 无法在不重复的前提下生成 1000 题；实际界面的 1~200 题批次仍由
     * {@link QuestionGenerator#generate(int)} 严格去重。</p>
     *
     * @param type  题型
     * @param count 样本数量
     * @return 题目列表
     */
    private static List<Question> generateSamples(QuestionType type, int count) {
        QuestionGenerator generator = QuestionGeneratorFactory.create(type, new Random(SEED));
        List<Question> questions = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            questions.add(generator.generateOne());
        }
        return questions;
    }

    /**
     * 检查一道题的操作数、运算符、中间结果、最终结果和题面求值。
     *
     * @param question         题目
     * @param expectedOperands 期望操作数个数
     */
    private static void checkQuestion(Question question, int expectedOperands) {
        assertNotNull(question);
        assertEquals("操作数个数错误", expectedOperands, question.getOperandCount());
        assertEquals("运算符个数错误", expectedOperands - 1, question.getOperators().length);
        for (int operand : question.getOperands()) {
            assertTrue("操作数越界：" + operand, operand >= 1 && operand <= 99);
        }
        for (char operator : question.getOperators()) {
            assertTrue("非法运算符：" + operator,
                    operator == '+' || operator == '-' || operator == '×' || operator == '÷');
        }
        for (int intermediate : question.getIntermediateResults()) {
            assertTrue("中间结果越界：" + intermediate,
                    intermediate >= 1 && intermediate <= 99);
        }
        int answer = question.getAnswer();
        assertTrue("最终结果越界：" + answer, answer >= 1 && answer <= 99);
        assertEquals("题面与答案不一致：" + question.getExpression(),
                answer, ExpressionEvaluator.evaluate(question.getExpression()));
        assertEquals(question.getExpression() + "=" + answer, question.toString());
    }

    /**
     * 判断题型是否为两个操作数。
     *
     * @param type 题型
     * @return 是否为二元题型
     */
    private static boolean isTwoOperandType(QuestionType type) {
        return type == QuestionType.TWO_ADD || type == QuestionType.TWO_SUB
                || type == QuestionType.TWO_MUL || type == QuestionType.TWO_DIV;
    }

    @Test
    public void twoOperandAddGenerates1000ValidQuestions() {
        List<Question> questions = generateSamples(QuestionType.TWO_ADD, 1000);
        assertEquals(1000, questions.size());
        for (Question question : questions) {
            checkQuestion(question, 2);
            assertEquals('+', question.getOperators()[0]);
        }
    }

    @Test
    public void twoOperandSubGenerates1000ValidQuestions() {
        List<Question> questions = generateSamples(QuestionType.TWO_SUB, 1000);
        assertEquals(1000, questions.size());
        for (Question question : questions) {
            checkQuestion(question, 2);
            assertEquals('-', question.getOperators()[0]);
            assertTrue("减法结果必须大于 0", question.getAnswer() > 0);
        }
    }

    @Test
    public void twoOperandMulGenerates1000ValidQuestions() {
        List<Question> questions = generateSamples(QuestionType.TWO_MUL, 1000);
        assertEquals(1000, questions.size());
        for (Question question : questions) {
            checkQuestion(question, 2);
            assertEquals('×', question.getOperators()[0]);
        }
    }

    @Test
    public void twoOperandDivGenerates1000ExactQuestions() {
        List<Question> questions = generateSamples(QuestionType.TWO_DIV, 1000);
        assertEquals(1000, questions.size());
        for (Question question : questions) {
            checkQuestion(question, 2);
            assertEquals('÷', question.getOperators()[0]);
            int[] operands = question.getOperands();
            assertEquals("除法必须整除：" + question.getExpression(),
                    0, operands[0] % operands[1]);
            assertTrue("商必须大于 0", question.getAnswer() > 0);
        }
    }

    @Test
    public void threeOperandAddGenerates500ValidQuestions() {
        List<Question> questions = generate(QuestionType.THREE_ADD, 500);
        assertEquals(500, questions.size());
        for (Question question : questions) {
            checkQuestion(question, 3);
            assertArrayEquals(new char[]{'+', '+'}, question.getOperators());
        }
    }

    @Test
    public void threeOperandSubGenerates500ValidQuestions() {
        List<Question> questions = generate(QuestionType.THREE_SUB, 500);
        assertEquals(500, questions.size());
        for (Question question : questions) {
            checkQuestion(question, 3);
            assertArrayEquals(new char[]{'-', '-'}, question.getOperators());
            assertTrue("减法结果必须大于 0", question.getAnswer() > 0);
        }
    }

    @Test
    public void threeOperandMulGenerates500ValidQuestions() {
        List<Question> questions = generate(QuestionType.THREE_MUL, 500);
        assertEquals(500, questions.size());
        for (Question question : questions) {
            checkQuestion(question, 3);
            assertArrayEquals(new char[]{'×', '×'}, question.getOperators());
        }
    }

    @Test
    public void threeOperandDivGenerates500ExactQuestions() {
        List<Question> questions = generate(QuestionType.THREE_DIV, 500);
        assertEquals(500, questions.size());
        for (Question question : questions) {
            checkQuestion(question, 3);
            assertArrayEquals(new char[]{'÷', '÷'}, question.getOperators());
            int[] operands = question.getOperands();
            assertEquals(0, operands[0] % operands[1]);
            assertEquals(0, (operands[0] / operands[1]) % operands[2]);
            assertTrue(question.getAnswer() > 0);
        }
    }

    @Test
    public void threeOperandAddSubMixGenerates500ValidQuestions() {
        List<Question> questions = generate(QuestionType.THREE_MIXED, 500);
        assertEquals(500, questions.size());
        Set<String> pairs = new HashSet<>();
        for (Question question : questions) {
            checkQuestion(question, 3);
            char[] operators = question.getOperators();
            String pair = "" + operators[0] + operators[1];
            assertTrue("加减混合出现了乘除：" + pair,
                    pair.equals("+-") || pair.equals("-+"));
            pairs.add(pair);
        }
        assertEquals("加减混合应同时覆盖两种顺序", 2, pairs.size());
    }

    @Test
    public void threeOperandMulDivMixGenerates500ValidQuestions() {
        List<Question> questions = generate(QuestionType.THREE_MUL_DIV, 500);
        assertEquals(500, questions.size());
        Set<String> pairs = new HashSet<>();
        for (Question question : questions) {
            checkQuestion(question, 3);
            char[] operators = question.getOperators();
            String pair = "" + operators[0] + operators[1];
            assertTrue("乘除混合出现了加减：" + pair,
                    pair.equals("×÷") || pair.equals("÷×"));
            pairs.add(pair);
        }
        assertEquals("乘除混合应同时覆盖两种顺序", 2, pairs.size());
    }

    @Test
    public void threeOperandAllSixteenPatternsGenerate500Each() {
        for (OperatorPattern pattern : OperatorPattern.values()) {
            QuestionGenerator generator = new FixedPatternGenerator(
                    new Random(SEED), pattern);
            List<Question> questions = generator.generate(500);
            assertEquals("组合 " + pattern + " 数量错误", 500, questions.size());
            for (Question question : questions) {
                checkQuestion(question, 3);
                assertArrayEquals("组合 " + pattern + " 的运算符不一致",
                        new char[]{pattern.getFirstOperator(),
                                pattern.getSecondOperator()},
                        question.getOperators());
            }
        }
    }

    @Test
    public void threeOperandFourMixedCoversAllSixteenPatterns() {
        List<Question> questions = generate(QuestionType.THREE_FOUR_MIXED, 5000);
        Set<String> pairs = new HashSet<>();
        for (Question question : questions) {
            checkQuestion(question, 3);
            pairs.add("" + question.getOperators()[0] + question.getOperators()[1]);
        }
        assertEquals("四则混合应覆盖 16 种运算符组合", 16, pairs.size());
    }

    @Test
    public void intermediateResultsFollowStandardPrecedence() {
        assertThreeOperand(2, '+', 3, '×', 4, 12, 14);
        assertThreeOperand(8, '÷', 4, '+', 5, 2, 7);
        assertThreeOperand(2, '×', 3, '+', 4, 6, 10);
        assertThreeOperand(10, '-', 2, '×', 3, 6, 4);
        assertThreeOperand(8, '÷', 2, '×', 3, 4, 12);
        assertThreeOperand(2, '×', 8, '÷', 4, 16, 4);
        assertThreeOperand(20, '-', 5, '-', 3, 15, 12);
        assertThreeOperand(20, '-', 5, '+', 3, 15, 18);
        assertThreeOperand(2, '+', 3, '+', 4, 5, 9);
        assertThreeOperand(6, '÷', 3, '÷', 2, 2, 1);
    }

    /**
     * 断言一道三元题的中间结果和最终结果。
     *
     * @param first          第一个操作数
     * @param firstOperator  第一个运算符
     * @param second         第二个操作数
     * @param secondOperator 第二个运算符
     * @param third          第三个操作数
     * @param intermediate   期望中间结果
     * @param answer         期望最终结果
     */
    private static void assertThreeOperand(int first, char firstOperator, int second,
                                           char secondOperator, int third,
                                           int intermediate, int answer) {
        ThreeOperandQuestion question = new ThreeOperandQuestion(
                first, firstOperator, second, secondOperator, third);
        assertArrayEquals(new int[]{intermediate}, question.getIntermediateResults());
        assertEquals(answer, question.getAnswer());
        assertEquals(answer, ExpressionEvaluator.evaluate(question.getExpression()));
    }

    @Test
    public void independentEvaluatorMatchesAnswersForAllTypes() {
        for (QuestionType type : QuestionType.values()) {
            List<Question> questions = generate(type, 100);
            for (Question question : questions) {
                assertEquals("题型 " + type + " 的题面求值不一致："
                                + question.getExpression(),
                        question.getAnswer(),
                        ExpressionEvaluator.evaluate(question.getExpression()));
            }
        }
    }

    @Test
    public void batchGenerationProducesNoDuplicates() {
        for (QuestionType type : QuestionType.values()) {
            List<Question> questions = generate(type, 200);
            Set<String> expressions = new HashSet<>();
            for (Question question : questions) {
                expressions.add(question.getExpression());
            }
            assertEquals("题型 " + type + " 存在重复题目",
                    200, expressions.size());
        }
    }

    @Test
    public void generating200QuestionsTakesLessThanOneSecond() {
        QuestionGenerator warmup = QuestionGeneratorFactory.create(
                QuestionType.THREE_FOUR_MIXED, new Random(SEED));
        warmup.generate(200);

        long start = System.nanoTime();
        List<Question> questions = QuestionGeneratorFactory.create(
                QuestionType.THREE_FOUR_MIXED, new Random(SEED + 1)).generate(200);
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000L;

        assertEquals(200, questions.size());
        assertTrue("200 题生成耗时 " + elapsedMillis + " ms，超过 1000 ms",
                elapsedMillis < 1000);
    }

    @Test
    public void invalidQuestionParametersAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new TwoOperandQuestion(0, '+', 10));
        assertThrows(IllegalArgumentException.class,
                () -> new TwoOperandQuestion(100, '+', 1));
        assertThrows(IllegalArgumentException.class,
                () -> new TwoOperandQuestion(10, '*', 5));
        assertThrows(IllegalArgumentException.class,
                () -> new TwoOperandQuestion(50, '+', 60));
        assertThrows(IllegalArgumentException.class,
                () -> new TwoOperandQuestion(50, '-', 50));
        assertThrows(IllegalArgumentException.class,
                () -> new TwoOperandQuestion(10, '÷', 3));
        assertThrows(IllegalArgumentException.class,
                () -> new ThreeOperandQuestion(80, '+', 30, '-', 10));
        assertThrows(IllegalArgumentException.class,
                () -> new ThreeOperandQuestion(50, '-', 60, '+', 10));
        assertThrows(IllegalArgumentException.class,
                () -> new ThreeOperandQuestion(1, '+', 98, '+', 1));
        assertThrows(IllegalArgumentException.class,
                () -> new ThreeOperandQuestion(2, '÷', 3, '+', 1));
    }

    @Test
    public void factoryCreatesGeneratorForEveryType() {
        for (QuestionType type : QuestionType.values()) {
            QuestionGenerator generator = QuestionGeneratorFactory.create(
                    type, new Random(SEED));
            assertNotNull(generator);
            Question question = generator.generateOne();
            int expectedOperands = type == QuestionType.RANDOM
                    ? question.getOperandCount()
                    : (isTwoOperandType(type) ? 2 : 3);
            checkQuestion(question, expectedOperands);
        }
        assertThrows(IllegalArgumentException.class,
                () -> QuestionGeneratorFactory.create(null, new Random(SEED)));
    }

    @Test
    public void randomModeCoversAllOperatorsAndOperandCounts() {
        List<Question> questions = generate(QuestionType.RANDOM, 5000);
        Set<Character> operators = new HashSet<>();
        Set<Integer> operandCounts = new HashSet<>();
        for (Question question : questions) {
            for (char operator : question.getOperators()) {
                operators.add(operator);
            }
            operandCounts.add(question.getOperandCount());
        }
        assertEquals(new HashSet<>(Arrays.asList('+', '-', '×', '÷')), operators);
        assertEquals(new HashSet<>(Arrays.asList(2, 3)), operandCounts);
    }

    @Test
    public void expressionFormatIsCompactAndUsesChineseOperators() {
        for (QuestionType type : QuestionType.values()) {
            if (type == QuestionType.RANDOM) {
                continue;
            }
            for (Question question : generate(type, 100)) {
                String expression = question.getExpression();
                assertFalse("表达式不应包含空格：" + expression, expression.contains(" "));
                assertTrue("表达式格式错误：" + expression,
                        expression.matches("[0-9]+([+×÷-][0-9]+){1,2}"));
                assertFalse("表达式不应包含等号：" + expression, expression.contains("="));
                assertTrue(question.toString().endsWith("=" + question.getAnswer()));
                assertNotEquals(0, question.getAnswer());
            }
        }
    }

    @Test
    public void subtractionResultsAreAlwaysPositive() {
        for (QuestionType type : new QuestionType[]{
                QuestionType.TWO_SUB, QuestionType.THREE_SUB,
                QuestionType.THREE_MIXED, QuestionType.THREE_FOUR_MIXED}) {
            for (Question question : generateSamples(type, 1000)) {
                for (int intermediate : question.getIntermediateResults()) {
                    assertTrue("中间结果必须大于 0：" + intermediate, intermediate > 0);
                }
                assertTrue("最终结果必须大于 0：" + question.getAnswer(),
                        question.getAnswer() > 0);
            }
        }
    }

    @Test
    public void divisionResultsAreAlwaysExactAndPositive() {
        for (QuestionType type : new QuestionType[]{
                QuestionType.TWO_DIV, QuestionType.THREE_DIV,
                QuestionType.THREE_MUL_DIV, QuestionType.THREE_FOUR_MIXED}) {
            for (Question question : generateSamples(type, 1000)) {
                String expression = question.getExpression();
                if (expression.contains("÷")) {
                    assertEquals("题面求值不一致：" + expression,
                            question.getAnswer(),
                            ExpressionEvaluator.evaluate(expression));
                    assertTrue("商必须大于 0：" + expression, question.getAnswer() > 0);
                }
            }
        }
    }

    /**
     * 测试专用的固定组合生成器。
     */
    private static final class FixedPatternGenerator
            extends PatternBasedThreeOperandGenerator {

        /**
         * 创建固定组合生成器。
         *
         * @param random  随机数对象
         * @param pattern 运算符组合
         */
        private FixedPatternGenerator(Random random, OperatorPattern pattern) {
            super(random, pattern);
        }
    }
}
