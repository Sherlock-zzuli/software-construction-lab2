package arithmetic;

/**
 * 三个操作数的四则运算题目，例如 “2+3×4=14”“8÷4+5=7”。
 *
 * <p>三个数按标准数学优先级计算：乘除优先于加减，同级从左到右。
 * “中间结果”指按该优先级先算出的那一步结果，例如 2+3×4 的中间结果是
 * 3×4=12；8÷4+5 的中间结果是 8÷4=2。构造时校验中间结果和最终结果
 * 都在 1~99 之间，减法结果不得为 0 或负数，除法必须整除。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class ThreeOperandQuestion extends Question {

    /**
     * 构造一道三个操作数的四则运算题目。
     *
     * @param first          第一个操作数
     * @param firstOperator  第一个运算符
     * @param second         第二个操作数
     * @param secondOperator 第二个运算符
     * @param third          第三个操作数
     * @throws IllegalArgumentException 当中间结果或最终结果越界时抛出
     */
    public ThreeOperandQuestion(int first, char firstOperator, int second,
                                char secondOperator, int third) {
        super(new int[]{first, second, third},
                new char[]{firstOperator, secondOperator});
        int intermediate = computeIntermediate();
        if (intermediate <= 0 || intermediate >= 100) {
            throw new IllegalArgumentException(
                    "中间结果必须大于 0 且小于 100：" + intermediate);
        }
        int answer = evaluate();
        if (answer <= 0 || answer >= 100) {
            throw new IllegalArgumentException(
                    "运算结果必须大于 0 且小于 100：" + answer);
        }
    }

    /**
     * 按标准优先级计算先算出的那一步结果。
     *
     * @return 中间结果
     */
    private int computeIntermediate() {
        if (isMulDiv(operators[0])) {
            // 先算前两个数，例如 a×b+c、a÷b×c
            return apply(operands[0], operators[0], operands[1]);
        }
        if (isMulDiv(operators[1])) {
            // 先算后两个数，例如 a+b×c、a-b÷c
            return apply(operands[1], operators[1], operands[2]);
        }
        // 两个运算符都是加减，按从左到右先算前两个数
        return apply(operands[0], operators[0], operands[1]);
    }

    @Override
    public int getAnswer() {
        return evaluate();
    }

    @Override
    public String getExpression() {
        return operands[0] + String.valueOf(operators[0]) + operands[1]
                + String.valueOf(operators[1]) + operands[2];
    }

    @Override
    public int[] getIntermediateResults() {
        return new int[]{computeIntermediate()};
    }
}
