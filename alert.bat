@REM 开发提示音工具
@REM 用法: alert.bat confirm   — 需要你确认时播放
@REM        alert.bat done      — 任务完成时播放

@echo off
if "%1"=="confirm" (
    echo 
    timeout /t 0 /nobreak >nul
    echo 
    goto :eof
)
if "%1"=="done" (
    echo 
    timeout /t 0 /nobreak >nul
    echo 
    timeout /t 0 /nobreak >nul
    echo 
    goto :eof
)
echo Usage: alert.bat [confirm|done]
