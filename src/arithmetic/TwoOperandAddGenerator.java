package arithmetic;

import java.util.Random;

/**
 * 两个数纯加法生成器。
 *
 * <p>生成策略：先取左操作数 a（1~98），再取右操作数 b（1~99-a），
 * 从而保证 a+b 大于 0 且小于 100，且两个操作数均为正整数。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class TwoOperandAddGenerator extends QuestionGenerator {

    /**
     * 创建两个数纯加法生成器。
     *
     * @param random 随机数对象
     */
    public TwoOperandAddGenerator(Random random) {
        super(random);
    }

    @Override
    public Question generateOne() {
        int left = randomBetween(1, 98);
        int right = randomBetween(1, 99 - left);
        return new TwoOperandQuestion(left, '+', right);
    }
}
