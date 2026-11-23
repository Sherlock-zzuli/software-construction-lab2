package arithmetic;

import java.util.Random;

/**
 * 三个数纯乘法生成器。
 *
 * <p>委托 {@link OperatorPattern#MUL_MUL} 构造：先保证 a×b≤98，
 * 再取 c≤99/(a×b)，保证中间结果和最终结果都不超过 99。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class ThreeOperandMulGenerator extends PatternBasedThreeOperandGenerator {

    /**
     * 创建三个数纯乘法生成器。
     *
     * @param random 随机数对象
     */
    public ThreeOperandMulGenerator(Random random) {
        super(random, OperatorPattern.MUL_MUL);
    }
}
