@echo off
setlocal

:: check if required parameter are provided
if "%~1"=="" (
    echo Error: No healthcheck endpoint provided.
    exit /b 1
)

if "%~2"=="" (
    echo Error: No timeout provided.
    exit /b 1
)

set "url=%~1"
set "timeout=%~2"
set elapsed=0
set interval=2


:check

echo Checking health of exception code server...
for /f %%i in ('curl --max-time %interval% --silent --write-out "%%{http_code}" --output NUL %url%') do set response_code=%%i

if "%response_code%"=="200" (
    echo echo exception code server is healthy
    exit /b 0
)

set /a elapsed+=interval

if %elapsed% geq %timeout% (
    echo echo exception code server is unhealthy
    exit /b 1
)

timeout /t %interval% /nobreak >NUL

goto check