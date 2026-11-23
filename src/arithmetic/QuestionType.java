package arithmetic;

/**
 * 题目类型枚举，对应界面下拉框中的各个选项。
 *
 * <p>枚举顺序与实验要求中的题型顺序一致；“随机”覆盖其余全部合法题型。</p>
 *
 * @author 徐星宇
 * @version 2.0
 */
public enum QuestionType {

    /** 两个数纯加法。 */
    TWO_ADD("二元加法"),

    /** 两个数纯减法。 */
    TWO_SUB("二元减法"),

    /** 两个数纯乘法。 */
    TWO_MUL("二元乘法"),

    /** 两个数纯除法。 */
    TWO_DIV("二元除法"),

    /** 三个数纯加法。 */
    THREE_ADD("三元加法"),

    /** 三个数纯减法。 */
    THREE_SUB("三元减法"),

    /** 三个数纯乘法。 */
    THREE_MUL("三元乘法"),

    /** 三个数纯除法。 */
    THREE_DIV("三元除法"),

    /** 三个数加减混合。 */
    THREE_MIXED("三元加减混合"),

    /** 三个数乘除混合。 */
    THREE_MUL_DIV("三元乘除混合"),

    /** 三个数四则混合，覆盖加减乘除任意组合。 */
    THREE_FOUR_MIXED("三元四则混合"),

    /** 随机模式：每次从上述十一种题型中随机选择。 */
    RANDOM("随机");

    private final String displayName;

    QuestionType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * 返回适合在界面上显示的中文名称。
     *
     * @return 题型名称
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
