package arithmetic;

import java.util.Random;

/**
 * 题目生成器工厂。
 *
 * <p>根据界面选择的题型创建对应的生成器，界面层无需了解具体生成器类，
 * 从而降低模块之间的耦合。新增题型时只需增加枚举值、生成器类和这里的
 * 一个分支，符合开闭原则。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public final class QuestionGeneratorFactory {

    /** 工具类不允许实例化。 */
    private QuestionGeneratorFactory() {
    }

    /**
     * 根据题型创建对应的题目生成器。
     *
     * @param type   题型
     * @param random 随机数对象
     * @return 题目生成器
     * @throws IllegalArgumentException 当题型为空时抛出
     */
    public static QuestionGenerator create(QuestionType type, Random random) {
        if (type == null) {
            throw new IllegalArgumentException("题型不能为空");
        }
        switch (type) {
            case TWO_ADD:
                return new TwoOperandAddGenerator(random);
            case TWO_SUB:
                return new TwoOperandSubGenerator(random);
            case TWO_MUL:
                return new TwoOperandMulGenerator(random);
            case TWO_DIV:
                return new TwoOperandDivGenerator(random);
            case THREE_ADD:
                return new ThreeOperandAddGenerator(random);
            case THREE_SUB:
                return new ThreeOperandSubGenerator(random);
            case THREE_MUL:
                return new ThreeOperandMulGenerator(random);
            case THREE_DIV:
                return new ThreeOperandDivGenerator(random);
            case THREE_MIXED:
                return new ThreeOperandMixedGenerator(random);
            case THREE_MUL_DIV:
                return new ThreeOperandMulDivGenerator(random);
            case THREE_FOUR_MIXED:
                return new ThreeOperandFourMixedGenerator(random);
            case RANDOM:
                return new RandomGenerator(random);
            default:
                throw new IllegalArgumentException("未知题型：" + type);
        }
    }
}
