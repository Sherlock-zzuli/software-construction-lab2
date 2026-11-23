package arithmetic;

import java.util.Random;

/**
 * 随机模式生成器。
 *
 * <p>内部组合了十一种具体生成器，每次生成题目时随机委托给其中一个，
 * 从而得到覆盖加减乘除、两个数/三个数的混合练习卷。这里体现的是组合复用，
 * 而不是继承：随机模式不需要知道每种题型的构造细节。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public class RandomGenerator extends QuestionGenerator {

    /** 可用的具体生成器。 */
    private final QuestionGenerator[] candidates;

    /**
     * 创建随机模式生成器。
     *
     * @param random 随机数对象
     */
    public RandomGenerator(Random random) {
        super(random);
        this.candidates = new QuestionGenerator[]{
                new TwoOperandAddGenerator(random),
                new TwoOperandSubGenerator(random),
                new TwoOperandMulGenerator(random),
                new TwoOperandDivGenerator(random),
                new ThreeOperandAddGenerator(random),
                new ThreeOperandSubGenerator(random),
                new ThreeOperandMulGenerator(random),
                new ThreeOperandDivGenerator(random),
                new ThreeOperandMixedGenerator(random),
                new ThreeOperandMulDivGenerator(random),
                new ThreeOperandFourMixedGenerator(random)
        };
    }

    @Override
    public Question generateOne() {
        int index = random.nextInt(candidates.length);
        return candidates[index].generateOne();
    }
}
