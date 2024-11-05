@echo off
setlocal

if "%~1"=="" (
    echo Port is absent
    exit /b 1
)

set "port=%~1"

FOR /F "tokens=5 delims= " %%P IN ('netstat -a -n -o ^| findstr 0.0.0.0:%port% ^| findstr LISTENING') DO TaskKill.exe /PID %%P /F

echo Stop exception code server successfully

exit /b 0