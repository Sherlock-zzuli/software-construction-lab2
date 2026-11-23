package arithmetic;

import java.util.Random;

/**
 * 基于 {@link OperatorPattern} 的三元题目生成器基类。
 *
 * <p>子类只需在构造时传入自己支持的运算符组合，批量生成、去重和随机数管理
 * 都由基类完成。这样新增题型时不需要重复编写循环和去重逻辑。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public abstract class PatternBasedThreeOperandGenerator extends QuestionGenerator {

    /** 当前生成器支持的运算符组合。 */
    private final OperatorPattern[] patterns;

    /**
     * 创建基于运算符组合的三元生成器。
     *
     * @param random   随机数对象
     * @param patterns 支持的运算符组合，至少一个
     */
    protected PatternBasedThreeOperandGenerator(Random random,
                                                OperatorPattern... patterns) {
        super(random);
        if (patterns == null || patterns.length == 0) {
            throw new IllegalArgumentException("至少需要一种运算符组合");
        }
        this.patterns = patterns.clone();
    }

    @Override
    public Question generateOne() {
        OperatorPattern pattern = patterns[random.nextInt(patterns.length)];
        return pattern.generate(random);
    }

    /**
     * 返回当前生成器支持的运算符组合副本。
     *
     * @return 运算符组合数组
     */
    public OperatorPattern[] getPatterns() {
        return patterns.clone();
    }
}
