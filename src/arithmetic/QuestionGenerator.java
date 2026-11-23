package arithmetic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 题目生成器抽象基类。
 *
 * <p>基类统一持有随机数对象，并提供批量生成题目的模板方法。批量生成时
 * 使用表达式文本去重，保证同一批题目不重复；具体题型由子类实现
 * {@link #generateOne()}。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public abstract class QuestionGenerator {

    /** 随机数对象，所有子类共用同一个实例。 */
    protected final Random random;

    /**
     * 创建题目生成器。
     *
     * @param random 随机数对象
     */
    protected QuestionGenerator(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("随机数对象不能为空");
        }
        this.random = random;
    }

    /**
     * 生成一道符合当前题型约束的题目。
     *
     * @return 一道题目
     */
    public abstract Question generateOne();

    /**
     * 批量生成指定数量的不重复题目。
     *
     * @param count 题目数量
     * @return 题目列表
     * @throws IllegalArgumentException 当数量小于等于 0 时抛出
     * @throws IllegalStateException    当无法在限定次数内生成足够的不重复题目时抛出
     */
    public List<Question> generate(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("题目数量必须大于 0：" + count);
        }
        List<Question> questions = new ArrayList<>(count);
        Set<String> seen = new HashSet<>();
        int attempts = 0;
        int maxAttempts = count * 500 + 5000;
        while (questions.size() < count) {
            if (++attempts > maxAttempts) {
                throw new IllegalStateException(
                        "在限定次数内无法生成足够的不重复题目，请减少题量");
            }
            Question question = generateOne();
            if (seen.add(question.getExpression())) {
                questions.add(question);
            }
        }
        return questions;
    }

    /**
     * 生成指定闭区间内的随机整数。
     *
     * @param minInclusive 最小值（包含）
     * @param maxInclusive 最大值（包含）
     * @return 区间内的随机整数
     */
    protected int randomBetween(int minInclusive, int maxInclusive) {
        return minInclusive + random.nextInt(maxInclusive - minInclusive + 1);
    }
}
