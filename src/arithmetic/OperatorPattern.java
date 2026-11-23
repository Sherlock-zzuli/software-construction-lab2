package arithmetic;

import java.util.Random;

/**
 * 三元运算的 16 种运算符组合及其构造式生成策略。
 *
 * <p>枚举把“运算符组合”和“如何构造合法操作数”绑定在一起，每个常量只负责
 * 一种组合的构造逻辑，避免把 16 种情况写成一个巨型 if-else。所有构造都遵循
 * 标准数学优先级：乘除优先，同级从左到右；“中间结果”指先算出的那一步结果。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public enum OperatorPattern {

    /** a + b + c：先保证 a+b≤98，再给 c 留出至少 1 的空间。 */
    ADD_ADD('+', '+', random -> {
        int a = between(random, 1, 97);
        int b = between(random, 1, 98 - a);
        int c = between(random, 1, 99 - a - b);
        return new ThreeOperandQuestion(a, '+', b, '+', c);
    }),

    /** a + b - c：先保证 a+b∈[2,98]，再取 c<a+b。 */
    ADD_SUB('+', '-', random -> {
        int a = between(random, 1, 97);
        int b = between(random, 1, 98 - a);
        int sum = a + b;
        int c = between(random, 1, sum - 1);
        return new ThreeOperandQuestion(a, '+', b, '-', c);
    }),

    /** a - b + c：先保证 a-b∈[1,98]，再取 c≤99-(a-b)。 */
    SUB_ADD('-', '+', random -> {
        int a = between(random, 2, 99);
        int b = between(random, 1, a - 1);
        int difference = a - b;
        int c = between(random, 1, 99 - difference);
        return new ThreeOperandQuestion(a, '-', b, '+', c);
    }),

    /** a - b - c：先保证 a-b∈[2,98]，再取 c<a-b。 */
    SUB_SUB('-', '-', random -> {
        int a = between(random, 3, 99);
        int b = between(random, 1, a - 2);
        int difference = a - b;
        int c = between(random, 1, difference - 1);
        return new ThreeOperandQuestion(a, '-', b, '-', c);
    }),

    /** a × b + c：先取 a、b 使 a×b≤98，再取 c≤99-a×b。 */
    MUL_ADD('×', '+', random -> {
        int a = between(random, 1, 98);
        int b = between(random, 1, 98 / a);
        int product = a * b;
        int c = between(random, 1, 99 - product);
        return new ThreeOperandQuestion(a, '×', b, '+', c);
    }),

    /** a × b - c：先取 a、b 使 a×b≤99，再取 c<a×b。 */
    MUL_SUB('×', '-', random -> {
        int a = between(random, 2, 99);
        int b = between(random, 1, 99 / a);
        int product = a * b;
        int c = between(random, 1, product - 1);
        return new ThreeOperandQuestion(a, '×', b, '-', c);
    }),

    /** a ÷ b + c：先取 b、q 使 a=b×q≤99，再取 c≤99-q。 */
    DIV_ADD('÷', '+', random -> {
        int b = between(random, 2, 99);
        int quotient = between(random, 1, 99 / b);
        int a = b * quotient;
        int c = between(random, 1, 99 - quotient);
        return new ThreeOperandQuestion(a, '÷', b, '+', c);
    }),

    /** a ÷ b - c：先取 b、q 使 a=b×q 且 q≥2，再取 c<q。 */
    DIV_SUB('÷', '-', random -> {
        // b 最大取 49，保证 99/b ≥ 2，从而商 q 至少可以取到 2
        int b = between(random, 2, 49);
        int quotient = between(random, 2, 99 / b);
        int a = b * quotient;
        int c = between(random, 1, quotient - 1);
        return new ThreeOperandQuestion(a, '÷', b, '-', c);
    }),

    /** a + b × c：先取 b、c 使 b×c≤98，再取 a≤99-b×c。 */
    ADD_MUL('+', '×', random -> {
        int b = between(random, 1, 98);
        int c = between(random, 1, 98 / b);
        int product = b * c;
        int a = between(random, 1, 99 - product);
        return new ThreeOperandQuestion(a, '+', b, '×', c);
    }),

    /** a + b ÷ c：先取 c、q 使 b=c×q≤99，再取 a≤99-q。 */
    ADD_DIV('+', '÷', random -> {
        int c = between(random, 2, 99);
        int quotient = between(random, 1, 99 / c);
        int b = c * quotient;
        int a = between(random, 1, 99 - quotient);
        return new ThreeOperandQuestion(a, '+', b, '÷', c);
    }),

    /** a - b × c：先取 b、c 使 b×c≤98，再取 a>b×c。 */
    SUB_MUL('-', '×', random -> {
        int b = between(random, 1, 98);
        int c = between(random, 1, 98 / b);
        int product = b * c;
        int a = between(random, product + 1, 99);
        return new ThreeOperandQuestion(a, '-', b, '×', c);
    }),

    /** a - b ÷ c：先取 c、q 使 b=c×q≤99，再取 a>q。 */
    SUB_DIV('-', '÷', random -> {
        // c 最大取 98，保证 98/c ≥ 1，从而商 q 至少可以取到 1
        int c = between(random, 2, 98);
        int quotient = between(random, 1, 98 / c);
        int b = c * quotient;
        int a = between(random, quotient + 1, 99);
        return new ThreeOperandQuestion(a, '-', b, '÷', c);
    }),

    /** a × b × c：先取 a、b 使 a×b≤98，再取 c≤99/(a×b)。 */
    MUL_MUL('×', '×', random -> {
        int a = between(random, 1, 98);
        int b = between(random, 1, 98 / a);
        int product = a * b;
        int c = between(random, 1, 99 / product);
        return new ThreeOperandQuestion(a, '×', b, '×', c);
    }),

    /** a × b ÷ c：先取 a、b 使 a×b≤99，再取 c 为 a×b 的因数。 */
    MUL_DIV('×', '÷', random -> {
        int a = between(random, 1, 99);
        int b = between(random, 1, 99 / a);
        int product = a * b;
        int c = randomDivisor(random, product);
        return new ThreeOperandQuestion(a, '×', b, '÷', c);
    }),

    /** a ÷ b × c：先取 b、q 使 a=b×q≤99，再取 c≤99/q。 */
    DIV_MUL('÷', '×', random -> {
        int b = between(random, 2, 99);
        int quotient = between(random, 1, 99 / b);
        int a = b * quotient;
        int c = between(random, 1, 99 / quotient);
        return new ThreeOperandQuestion(a, '÷', b, '×', c);
    }),

    /** a ÷ b ÷ c：先取 b、q 使 a=b×q≤99，再取 c 为 q 的因数。 */
    DIV_DIV('÷', '÷', random -> {
        int b = between(random, 2, 99);
        int quotient = between(random, 1, 99 / b);
        int a = b * quotient;
        int c = randomDivisor(random, quotient);
        return new ThreeOperandQuestion(a, '÷', b, '÷', c);
    });

    /** 构造一道题的函数式接口。 */
    @FunctionalInterface
    private interface Builder {
        /**
         * 按当前运算符组合构造一道合法题目。
         *
         * @param random 随机数对象
         * @return 三元题目
         */
        ThreeOperandQuestion build(Random random);
    }

    private final char firstOperator;
    private final char secondOperator;
    private final Builder builder;

    OperatorPattern(char firstOperator, char secondOperator, Builder builder) {
        this.firstOperator = firstOperator;
        this.secondOperator = secondOperator;
        this.builder = builder;
    }

    /**
     * 按当前组合生成一道满足全部约束的三元题目。
     *
     * @param random 随机数对象
     * @return 三元题目
     */
    public Question generate(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("随机数对象不能为空");
        }
        return builder.build(random);
    }

    /**
     * 返回第一个运算符。
     *
     * @return 第一个运算符
     */
    public char getFirstOperator() {
        return firstOperator;
    }

    /**
     * 返回第二个运算符。
     *
     * @return 第二个运算符
     */
    public char getSecondOperator() {
        return secondOperator;
    }

    /**
     * 生成指定闭区间内的随机整数。
     *
     * @param random       随机数对象
     * @param minInclusive 最小值（包含）
     * @param maxInclusive 最大值（包含）
     * @return 区间内的随机整数
     */
    private static int between(Random random, int minInclusive, int maxInclusive) {
        if (maxInclusive < minInclusive) {
            throw new IllegalArgumentException(
                    "随机区间非法：[" + minInclusive + ", " + maxInclusive + "]");
        }
        return minInclusive + random.nextInt(maxInclusive - minInclusive + 1);
    }

    /**
     * 从 value 的因数中随机选择一个，用于构造可整除的除法。
     *
     * @param random 随机数对象
     * @param value  被除数
     * @return value 的一个正因数
     */
    private static int randomDivisor(Random random, int value) {
        int count = 0;
        for (int divisor = 1; divisor <= value; divisor++) {
            if (value % divisor == 0) {
                count++;
            }
        }
        int target = 1 + random.nextInt(count);
        int seen = 0;
        for (int divisor = 1; divisor <= value; divisor++) {
            if (value % divisor == 0) {
                seen++;
                if (seen == target) {
                    return divisor;
                }
            }
        }
        throw new IllegalStateException("无法找到因数：" + value);
    }
}
