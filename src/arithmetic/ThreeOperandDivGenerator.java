package arithmetic;

import java.util.Random;

/**
 * 三个数纯除法生成器。
 *
 * <p>委托 {@link OperatorPattern#DIV_DIV} 构造：先取 b、q 使 a=b×q≤99，
 * 再取 c 为 q 的因数，保证两次除法都整除且商大于 0。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class ThreeOperandDivGenerator extends PatternBasedThreeOperandGenerator {

    /**
     * 创建三个数纯除法生成器。
     *
     * @param random 随机数对象
     */
    public ThreeOperandDivGenerator(Random random) {
        super(random, OperatorPattern.DIV_DIV);
    }
}
