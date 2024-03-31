@echo off
setlocal

REM Delete build folder
rd /s /q "lwjgl3\build"

REM Build command
call gradlew lwjgl:jar

REM Initialize flag
set "freshdb=0"

REM Check for -f argument
:parseArgs
if "%~1"=="" goto copyDb
if "%~1"=="-f" set "freshdb=1" & goto copyDb
shift
goto parseArgs

:copyDb
if "%freshdb%"=="1" (
    REM Copy fresh database if -f is present
    echo Copying fresh database...
    if not exist ".\lwjgl3\build\lib\" mkdir ".\lwjgl3\build\lib"
    copy /Y ".\typing-game-fresh.db" ".\lwjgl3\build\lib\typing-game.db"
) else (
    REM Default copy command
    echo Copying existing database...
    if not exist ".\lwjgl3\build\lib\" mkdir ".\lwjgl3\build\lib"
    copy /Y ".\typing-game.db" ".\lwjgl3\build\lib\"
)

:end
endlocal
