package arithmetic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 独立的表达式求值器。
 *
 * <p>该类的目的不是替代 {@link Question#evaluate()}，而是在测试中用一个
 * 完全独立的算法（调度场算法 + 逆波兰求值）重新计算题面，验证题目保存的
 * 答案与题面一致。这样可以避免“用同一个求值方法验证自己”的循环验证问题。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public final class ExpressionEvaluator {

    /** 工具类不允许实例化。 */
    private ExpressionEvaluator() {
    }

    /**
     * 按标准数学优先级计算表达式，例如 “3×4+5” 返回 17。
     *
     * @param expression 表达式文本，支持 +、-、×、÷ 和多位数
     * @return 计算结果
     * @throws IllegalArgumentException 当表达式为空、含非法字符或格式错误时抛出
     * @throws ArithmeticException      当除法不能整除或除数为 0 时抛出
     */
    public static int evaluate(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("表达式不能为空");
        }
        List<String> tokens = tokenize(expression);
        List<String> rpn = toReversePolish(tokens);
        return evaluateReversePolish(rpn);
    }

    /**
     * 将表达式拆分为数字和运算符。
     *
     * @param expression 表达式文本
     * @return 记号列表
     */
    private static List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        int index = 0;
        while (index < expression.length()) {
            char current = expression.charAt(index);
            if (Character.isWhitespace(current)) {
                index++;
                continue;
            }
            if (Character.isDigit(current)) {
                int start = index;
                while (index < expression.length()
                        && Character.isDigit(expression.charAt(index))) {
                    index++;
                }
                tokens.add(expression.substring(start, index));
                continue;
            }
            if (isOperator(current)) {
                tokens.add(String.valueOf(current));
                index++;
                continue;
            }
            throw new IllegalArgumentException("表达式中含有非法字符：" + current);
        }
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("表达式不能为空");
        }
        return tokens;
    }

    /**
     * 调度场算法：把中缀表达式转换为逆波兰表达式。
     *
     * @param tokens 中缀记号
     * @return 逆波兰记号
     */
    private static List<String> toReversePolish(List<String> tokens) {
        List<String> output = new ArrayList<>();
        Deque<Character> operators = new ArrayDeque<>();
        for (String token : tokens) {
            if (token.length() == 1 && isOperator(token.charAt(0))) {
                char operator = token.charAt(0);
                while (!operators.isEmpty()
                        && precedence(operators.peek()) >= precedence(operator)) {
                    output.add(String.valueOf(operators.pop()));
                }
                operators.push(operator);
            } else {
                output.add(token);
            }
        }
        while (!operators.isEmpty()) {
            output.add(String.valueOf(operators.pop()));
        }
        return output;
    }

    /**
     * 计算逆波兰表达式。
     *
     * @param rpn 逆波兰记号
     * @return 计算结果
     */
    private static int evaluateReversePolish(List<String> rpn) {
        Deque<Integer> values = new ArrayDeque<>();
        for (String token : rpn) {
            if (token.length() == 1 && isOperator(token.charAt(0))) {
                if (values.size() < 2) {
                    throw new IllegalArgumentException("表达式格式错误");
                }
                int right = values.pop();
                int left = values.pop();
                values.push(apply(left, token.charAt(0), right));
            } else {
                try {
                    values.push(Integer.parseInt(token));
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("非法的数字：" + token, ex);
                }
            }
        }
        if (values.size() != 1) {
            throw new IllegalArgumentException("表达式格式错误");
        }
        return values.pop();
    }

    /**
     * 执行一步二元运算。
     *
     * @param left     左操作数
     * @param operator 运算符
     * @param right    右操作数
     * @return 运算结果
     */
    private static int apply(int left, char operator, int right) {
        switch (operator) {
            case '+':
                return left + right;
            case '-':
                return left - right;
            case '×':
                return left * right;
            case '÷':
                if (right == 0) {
                    throw new ArithmeticException("除数不能为 0");
                }
                if (left % right != 0) {
                    throw new ArithmeticException(
                            "除法不能整除：" + left + "÷" + right);
                }
                return left / right;
            default:
                throw new IllegalArgumentException("不支持的运算符：" + operator);
        }
    }

    /**
     * 判断是否为四则运算符。
     *
     * @param character 字符
     * @return 是否为运算符
     */
    private static boolean isOperator(char character) {
        return character == '+' || character == '-'
                || character == '×' || character == '÷';
    }

    /**
     * 返回运算符优先级，乘除高于加减。
     *
     * @param operator 运算符
     * @return 优先级
     */
    private static int precedence(char operator) {
        return (operator == '×' || operator == '÷') ? 2 : 1;
    }
}
