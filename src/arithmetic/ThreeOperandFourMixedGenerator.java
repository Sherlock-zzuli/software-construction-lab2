package arithmetic;

import java.util.Random;

/**
 * 三个数四则混合生成器。
 *
 * <p>覆盖加、减、乘、除的任意组合，共 16 种运算符序列。每种序列由
 * {@link OperatorPattern} 中对应的构造式策略生成，保证操作数、中间结果
 * 和最终结果都满足 1~99 的约束。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class ThreeOperandFourMixedGenerator extends PatternBasedThreeOperandGenerator {

    /**
     * 创建三个数四则混合生成器。
     *
     * @param random 随机数对象
     */
    public ThreeOperandFourMixedGenerator(Random random) {
        super(random, OperatorPattern.values());
    }
}
