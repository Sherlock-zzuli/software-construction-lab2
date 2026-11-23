package arithmetic;

import java.util.Random;

/**
 * 三个数加减混合生成器。
 *
 * <p>在 a+b-c 与 a-b+c 两种组合中等概率选择，两者都按从左到右计算。
 * 保留实验1的类名，内部改为委托 {@link OperatorPattern} 构造。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class ThreeOperandMixedGenerator extends PatternBasedThreeOperandGenerator {

    /**
     * 创建三个数加减混合生成器。
     *
     * @param random 随机数对象
     */
    public ThreeOperandMixedGenerator(Random random) {
        super(random, OperatorPattern.ADD_SUB, OperatorPattern.SUB_ADD);
    }
}
