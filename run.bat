@echo off
chcp 936 >nul
cd /d "%~dp0"
if not exist build\classes\arithmetic\ArithmeticExerciseApp.class (
    call compile.bat -batch
    if errorlevel 1 exit /b 1
)
java -Dfile.encoding=UTF-8 -cp build\classes arithmetic.ArithmeticExerciseApp
if errorlevel 1 (
    echo.
    echo 程序退出时发生错误。
    pause
)
