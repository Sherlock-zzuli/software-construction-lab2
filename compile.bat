@echo off
chcp 936 >nul
cd /d "%~dp0"
if not exist build\classes mkdir build\classes
javac -encoding UTF-8 -d build\classes src\arithmetic\*.java
if errorlevel 1 (
    echo.
    echo 编译失败，请检查上方错误信息。
    if /i not "%~1"=="-batch" pause
    exit /b 1
)
echo.
echo 编译成功：build\classes
if /i not "%~1"=="-batch" pause
exit /b 0
