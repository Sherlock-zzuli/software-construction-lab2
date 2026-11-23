package arithmetic;

import java.util.Random;

/**
 * 两个数纯乘法生成器。
 *
 * <p>先取 a∈[1,99]，再取 b∈[1,99/a]，保证 a×b 在 1~99 之间。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class TwoOperandMulGenerator extends QuestionGenerator {

    /**
     * 创建两个数纯乘法生成器。
     *
     * @param random 随机数对象
     */
    public TwoOperandMulGenerator(Random random) {
        super(random);
    }

    @Override
    public Question generateOne() {
        int left = randomBetween(1, 99);
        int right = randomBetween(1, 99 / left);
        return new TwoOperandQuestion(left, '×', right);
    }
}
