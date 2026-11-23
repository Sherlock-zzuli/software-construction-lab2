@echo off
chcp 936 >nul
cd /d "%~dp0"
if not exist build\classes mkdir build\classes
if not exist build\test-classes mkdir build\test-classes
javac -encoding UTF-8 -d build\classes src\arithmetic\*.java
if errorlevel 1 goto fail
javac -encoding UTF-8 -cp "build\classes;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" -d build\test-classes test\arithmetic\*.java
if errorlevel 1 goto fail
java -Dfile.encoding=UTF-8 -cp "build\classes;build\test-classes;lib\junit-4.13.2.jar;lib\hamcrest-core-1.3.jar" org.junit.runner.JUnitCore arithmetic.QuestionGeneratorTest arithmetic.CsvExporterTest
if errorlevel 1 goto fail
echo.
echo 测试全部通过。
if /i not "%~1"=="-batch" pause
exit /b 0
:fail
echo.
echo 编译或测试失败，请查看上方错误信息。
if /i not "%~1"=="-batch" pause
exit /b 1
