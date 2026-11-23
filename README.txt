小学加减法练习题生成器
《软件构造》实验 1

学号：542413320650
姓名：徐星宇
专业班级：工业软件 24-06

一、运行环境
JDK 8 及以上（本程序在 JDK 11 下开发与测试）。

二、目录说明
src/arithmetic      源程序（14 个类）
compile.bat         一键编译脚本
run.bat             一键运行脚本
README.txt          本说明文件

三、编译与运行
Windows 下双击 compile.bat，编译成功后双击 run.bat。
也可以在命令行中执行：
    javac -encoding UTF-8 -d bin src\arithmetic\*.java
    java -cp bin arithmetic.ArithmeticExerciseApp

四、功能说明
1. 题型：2 个数纯加、2 个数纯减、3 个数纯加、3 个数纯减、
   3 个数加减混合、随机模式。
2. 题量：可在 1~200 之间输入，默认 50。
3. 约束：操作数、中间结果、最终结果均为大于 0 且小于 100 的正整数。
4. 支持显示/隐藏答案、清空题目；输入为空、非整数或超出范围时会弹出提示。
