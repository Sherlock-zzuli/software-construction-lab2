package arithmetic;

import java.util.Random;

/**
 * 三个数纯减法生成器。
 *
 * <p>委托 {@link OperatorPattern#SUB_SUB} 构造：先保证 a-b≥2，
 * 再取 c<a-b，保证两步减法都得到正整数。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class ThreeOperandSubGenerator extends PatternBasedThreeOperandGenerator {

    /**
     * 创建三个数纯减法生成器。
     *
     * @param random 随机数对象
     */
    public ThreeOperandSubGenerator(Random random) {
        super(random, OperatorPattern.SUB_SUB);
    }
}
