package arithmetic;

import java.util.Random;

/**
 * 两个数纯减法生成器。
 *
 * <p>生成策略：先取被减数 a（2~99），再取减数 b（1~a-1），
 * 从而保证差大于 0 且小于 100。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class TwoOperandSubGenerator extends QuestionGenerator {

    /**
     * 创建两个数纯减法生成器。
     *
     * @param random 随机数对象
     */
    public TwoOperandSubGenerator(Random random) {
        super(random);
    }

    @Override
    public Question generateOne() {
        int left = randomBetween(2, 99);
        int right = randomBetween(1, left - 1);
        return new TwoOperandQuestion(left, '-', right);
    }
}
