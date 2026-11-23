package arithmetic;

/**
 * 算术题抽象基类。
 *
 * <p>基类负责保存操作数与运算符，并按标准数学优先级统一计算表达式结果：
 * 乘除优先于加减，同级运算从左到右。具体题目的表达式形式与中间结果由子类实现，
 * 体现面向对象中的继承与多态。</p>
 *
 * <p>本实验约定：操作数、三数运算的中间结果（先算出的那一步结果）和最终结果
 * 都必须是 1~99 的整数；减法结果不得为 0 或负数；除法必须整除，商不得为 0。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public abstract class Question {

    /** 操作数，按表达式中从左到右的顺序保存，取值范围为 1~99。 */
    protected final int[] operands;

    /** 运算符，允许 '+', '-', '×', '÷'，其个数总比操作数少 1。 */
    protected final char[] operators;

    /**
     * 构造一道算术题，并校验操作数与运算符的基本合法性。
     *
     * @param operands  操作数数组
     * @param operators 运算符数组
     * @throws IllegalArgumentException 当操作数或运算符不合法时抛出
     */
    protected Question(int[] operands, char[] operators) {
        if (operands == null || operands.length < 2) {
            throw new IllegalArgumentException("一道题至少需要两个操作数");
        }
        if (operators == null || operators.length != operands.length - 1) {
            throw new IllegalArgumentException("运算符个数必须比操作数少 1");
        }
        for (int operand : operands) {
            if (operand <= 0 || operand >= 100) {
                throw new IllegalArgumentException(
                        "操作数必须大于 0 且小于 100：" + operand);
            }
        }
        for (char operator : operators) {
            if (!isSupportedOperator(operator)) {
                throw new IllegalArgumentException("不支持的运算符：" + operator);
            }
        }
        this.operands = operands.clone();
        this.operators = operators.clone();
    }

    /**
     * 判断运算符是否为四则运算符之一。
     *
     * @param operator 运算符
     * @return 是否支持
     */
    protected static boolean isSupportedOperator(char operator) {
        return operator == '+' || operator == '-'
                || operator == '×' || operator == '÷';
    }

    /**
     * 判断运算符是否属于乘除运算。
     *
     * @param operator 运算符
     * @return 是否为乘或除
     */
    protected static boolean isMulDiv(char operator) {
        return operator == '×' || operator == '÷';
    }

    /**
     * 按标准数学优先级计算表达式的值：乘除优先，同级从左到右。
     *
     * <p>三个操作数时，先判断第一个运算符是否为乘除；若第一个运算符是加减，
     * 再判断第二个运算符是否为乘除。这样可正确处理 a+b×c、a×b+c、a-b×c 等
     * 不同优先级的组合。</p>
     *
     * @return 表达式的最终结果
     * @throws IllegalArgumentException 当除法不能整除时抛出
     */
    protected final int evaluate() {
        if (operands.length == 2) {
            return apply(operands[0], operators[0], operands[1]);
        }
        if (operands.length == 3) {
            char first = operators[0];
            char second = operators[1];
            if (isMulDiv(first)) {
                // 先算 a 与 b，再与 c 运算，例如 a×b+c、a÷b×c
                return apply(apply(operands[0], first, operands[1]), second, operands[2]);
            }
            if (isMulDiv(second)) {
                // 先算 b 与 c，再与 a 运算，例如 a+b×c、a-b÷c
                return apply(operands[0], first, apply(operands[1], second, operands[2]));
            }
            // 两个运算符都是加减，按从左到右计算
            return apply(apply(operands[0], first, operands[1]), second, operands[2]);
        }
        throw new IllegalStateException("仅支持两个或三个操作数");
    }

    /**
     * 执行一步二元运算，除法要求整除。
     *
     * @param left     左操作数
     * @param operator 运算符
     * @param right    右操作数
     * @return 运算结果
     * @throws IllegalArgumentException 当运算符非法或除法不能整除时抛出
     */
    protected static int apply(int left, char operator, int right) {
        switch (operator) {
            case '+':
                return left + right;
            case '-':
                return left - right;
            case '×':
                return left * right;
            case '÷':
                if (right == 0) {
                    throw new IllegalArgumentException("除数不能为 0");
                }
                if (left % right != 0) {
                    throw new IllegalArgumentException(
                            "除法必须整除：" + left + "÷" + right);
                }
                return left / right;
            default:
                throw new IllegalArgumentException("不支持的运算符：" + operator);
        }
    }

    /**
     * 返回题目中操作数的个数。
     *
     * @return 操作数个数（2 或 3）
     */
    public int getOperandCount() {
        return operands.length;
    }

    /**
     * 返回操作数数组的副本，避免调用方修改题目内部状态。
     *
     * @return 操作数数组副本
     */
    public int[] getOperands() {
        return operands.clone();
    }

    /**
     * 返回运算符数组的副本。
     *
     * @return 运算符数组副本
     */
    public char[] getOperators() {
        return operators.clone();
    }

    /**
     * 返回题目的最终运算结果。
     *
     * @return 最终结果
     */
    public abstract int getAnswer();

    /**
     * 返回题目表达式，例如 “3×4+5”。
     *
     * @return 表达式文本（不含等号与空格）
     */
    public abstract String getExpression();

    /**
     * 返回计算过程中的全部中间结果（按计算顺序）。
     * 两个操作数的题目没有中间结果，返回空数组。
     *
     * @return 中间结果数组
     */
    public abstract int[] getIntermediateResults();

    /**
     * 返回“题面=答案”形式的完整文本，例如 “3×4+5=17”。
     *
     * @return 完整题目文本
     */
    @Override
    public String toString() {
        return getExpression() + "=" + getAnswer();
    }
}
