package arithmetic;

import java.util.Random;

/**
 * 三个数纯加法生成器。
 *
 * <p>委托 {@link OperatorPattern#ADD_ADD} 构造：先保证 a+b≤98，
 * 再为 c 留出至少 1 的取值空间。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class ThreeOperandAddGenerator extends PatternBasedThreeOperandGenerator {

    /**
     * 创建三个数纯加法生成器。
     *
     * @param random 随机数对象
     */
    public ThreeOperandAddGenerator(Random random) {
        super(random, OperatorPattern.ADD_ADD);
    }
}
