# 小学四则运算练习题生成器（实验2）

本项目是《软件构造》实验2的完整实现：在实验1加减法练习题生成器的基础上，
增加乘除法、三元四则混合题型、按可配置题量与列数导出 CSV，并配套 JUnit 4
单元测试、UML 类图和真实运行截图。

## 1. 功能

- 题型：二元加法、二元减法、二元乘法、二元除法、三元加法、三元减法、
  三元乘法、三元除法、三元加减混合、三元乘除混合、三元四则混合、随机。
- 题量：1~200 道；每行列数：1~10 列；默认 30 道、5 列。
- 预览：JTable 按“每行 N 列”显示，单元格内容与导出的 CSV 完全一致。
- 导出：UTF-8 with BOM、CRLF、逗号分隔、无表头，单元格格式为“题面=答案”。
- 校验：题量、列数、路径非法时用 JOptionPane 给出明确中文提示，不崩溃。
- 外观：Windows 系统外观、微软雅黑中文字体、窗口居中、可缩放。

## 2. 运算规则

- 所有操作数、三数运算的中间结果和最终结果都必须是 1~99 的整数。
- 运算顺序按标准数学优先级：×÷ 先于 +−，同级从左到右。
- 三元题的“中间结果”指按上述优先级先算出的那一步结果。例如：
  - `2+3×4`：中间结果 12，最终结果 14；
  - `8÷4+5`：中间结果 2，最终结果 7；
  - `8÷2×3`：中间结果 4，最终结果 12。
- 减法结果不得为 0 或负数；除法必须整除，商不得为 0。
- 生成器采用构造式生成：先确定满足约束的操作数，再组装表达式，不依赖大规模
  随机试错。同一批题目按表达式文本去重。

## 3. 目录结构

```
软件构造实验2/
├─ src/arithmetic/              Java 源码（包名 arithmetic）
│  ├─ ArithmeticExerciseApp.java        程序入口
│  ├─ MainFrame.java                    Swing 主界面
│  ├─ Question.java                     题目抽象基类
│  ├─ TwoOperandQuestion.java           二元题目
│  ├─ ThreeOperandQuestion.java         三元题目
│  ├─ QuestionGenerator.java            生成器抽象基类
│  ├─ PatternBasedThreeOperandGenerator.java  三元组合生成器基类
│  ├─ OperatorPattern.java              16 种运算符组合及构造策略
│  ├─ *Generator.java                   各题型具体生成器
│  ├─ QuestionGeneratorFactory.java     生成器工厂
│  ├─ ExpressionEvaluator.java          独立表达式求值器（测试用）
│  ├─ CsvExporter.java                  CSV 排版与导出服务
│  └─ CsvExportResult.java              导出结果
├─ test/arithmetic/             JUnit 4 测试
│  ├─ QuestionGeneratorTest.java
│  └─ CsvExporterTest.java
├─ lib/                         JUnit 4.13.2 + hamcrest-core-1.3
├─ uml/                         PlantUML 源文件与类图 PNG
├─ screenshots/                 真实运行截图与 manifest.md
├─ output/                      示例 CSV
├─ compile.bat                  编译
├─ run.bat                      运行
├─ test.bat                     编译并运行全部 JUnit 测试
└─ README.md
```

## 4. 环境要求

- Windows 10/11
- JDK 17 或更高版本（源码兼容 Java 8 语法，但本项目在 JDK 17 下验证）
- 无需额外安装 Swing 依赖；JUnit 4.13.2 与 hamcrest-core-1.3 已放入 `lib/`

## 5. 编译

双击 `compile.bat`，或在命令提示符中执行：

```bat
javac -encoding UTF-8 -d build\classes src\arithmetic\*.java
```

## 6. 运行

双击 `run.bat`，或在命令提示符中执行：

```bat
java -Dfile.encoding=UTF-8 -cp build\classes arithmetic.ArithmeticExerciseApp
```

## 7. 测试

双击 `test.bat`，或在命令提示符中执行：

```bat
javac -encoding UTF-8 -d build\classes src\arithmetic\*.java
javac -encoding UTF-8 -cp "build\classes;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" -d build\test-classes test\arithmetic\*.java
java -cp "build\classes;build\test-classes;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" org.junit.runner.JUnitCore arithmetic.QuestionGeneratorTest arithmetic.CsvExporterTest
```

测试覆盖：二元加减乘除各 1000 题；三元各题型各 500 题；16 种运算符组合
各 500 题；减法为正、除法整除、标准优先级中间结果、独立求值器比对、
批量去重、200 题生成性能、CSV 的 6×5 布局、UTF-8 BOM、CRLF、逗号、
1/200 题与 1/10 列边界、31 题 5 列末行补空、非法题量/列数等。

## 8. CSV 格式

- 编码：UTF-8 with BOM（文件开头为 EF BB BF）
- 换行：CRLF（`\r\n`）
- 分隔符：英文逗号 `,`
- 表头：无
- 单元格：`题面=答案`，例如 `3×4+5=17`
- 行数：`ceil(题量 / 列数)`，最后一行不足时补空单元格
- 默认文件名：`四则运算练习_30题_5列.csv`

30 题 5 列时导出 6 行 × 5 列；31 题 5 列时导出 7 行，最后一行只有 1 个
非空单元格和 4 个空单元格。

## 9. 设计说明

- 模型层：`Question` 统一保存操作数和运算符，按标准优先级求值；
  `TwoOperandQuestion`、`ThreeOperandQuestion` 负责各自的结果校验。
- 生成层：`QuestionGenerator` 提供批量生成与去重模板方法；
  `OperatorPattern` 把 16 种三元运算符组合分别封装为构造策略，
  避免巨型 if-else；`PatternBasedThreeOperandGenerator` 复用这些策略。
- 服务层：`CsvExporter` 同时负责预览排版和文件写入，保证两者一致。
- 界面层：`MainFrame` 只收集输入、调用服务和展示结果，不包含生成或写文件逻辑。
- 测试层：`ExpressionEvaluator` 用调度场算法独立求值，避免“自己验证自己”。

## 10. 说明

- 本仓库的截图均为 Windows 本机真实运行捕获，详见 `screenshots/manifest.md`。
- UML 类图由 PlantUML 工具生成，源文件位于 `uml/`。
