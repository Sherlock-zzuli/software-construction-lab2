package arithmetic;

import java.util.Random;

/**
 * 三个数乘除混合生成器。
 *
 * <p>在 a×b÷c 与 a÷b×c 两种组合中等概率选择。前者先算 a×b，
 * 再取积的因数作为 c；后者先算 a÷b，再限制 c 使最终结果不超过 99。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class ThreeOperandMulDivGenerator extends PatternBasedThreeOperandGenerator {

    /**
     * 创建三个数乘除混合生成器。
     *
     * @param random 随机数对象
     */
    public ThreeOperandMulDivGenerator(Random random) {
        super(random, OperatorPattern.MUL_DIV, OperatorPattern.DIV_MUL);
    }
}
