package arithmetic;

import java.util.Random;

/**
 * 两个数纯除法生成器。
 *
 * <p>先取除数 b∈[2,99]，再取商 q∈[1,99/b]，令被除数 a=b×q，
 * 从而保证 a≤99 且除法整除、商大于 0。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class TwoOperandDivGenerator extends QuestionGenerator {

    /**
     * 创建两个数纯除法生成器。
     *
     * @param random 随机数对象
     */
    public TwoOperandDivGenerator(Random random) {
        super(random);
    }

    @Override
    public Question generateOne() {
        int divisor = randomBetween(2, 99);
        int quotient = randomBetween(1, 99 / divisor);
        int dividend = divisor * quotient;
        return new TwoOperandQuestion(dividend, '÷', divisor);
    }
}
