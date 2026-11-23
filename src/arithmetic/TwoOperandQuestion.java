package arithmetic;

/**
 * 两个操作数的四则运算题目，例如 “36+52=88”“6×7=42”。
 *
 * <p>两个操作数直接得到最终结果，不存在中间结果；构造时校验最终结果
 * 在 1~99 之间，除法还必须整除。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class TwoOperandQuestion extends Question {

    /**
     * 构造一道两个操作数的四则运算题目。
     *
     * @param left     左操作数
     * @param operator 运算符（'+', '-', '×', '÷'）
     * @param right    右操作数
     * @throws IllegalArgumentException 当操作数、运算符或运算结果越界时抛出
     */
    public TwoOperandQuestion(int left, char operator, int right) {
        super(new int[]{left, right}, new char[]{operator});
        int answer = evaluate();
        if (answer <= 0 || answer >= 100) {
            throw new IllegalArgumentException(
                    "运算结果必须大于 0 且小于 100：" + answer);
        }
    }

    @Override
    public int getAnswer() {
        return evaluate();
    }

    @Override
    public String getExpression() {
        return operands[0] + String.valueOf(operators[0]) + operands[1];
    }

    @Override
    public int[] getIntermediateResults() {
        // 两个操作数的题目直接得到结果，不存在中间结果
        return new int[0];
    }
}
