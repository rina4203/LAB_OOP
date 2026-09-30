@echo off
rem ----------------------------------------------------------------------
rem  Запуск програми "Фургон кави" з консолі.
rem
rem    run              стандартні дані з src\main\resources
rem    run list         перелік готових сценаріїв
rem    run <сценарій>   дані з теки scenarios\<сценарій>
rem
rem  Програма збирається автоматично, якщо її ще не зібрано
rem  або код змінився після останнього збирання.
rem ----------------------------------------------------------------------
setlocal

for /f "tokens=2 delims=:." %%c in ('chcp') do set "OLD_CP=%%c"
chcp 65001 >nul

set "MODULE=%~dp0"
set "JAR=%MODULE%target\coffee-van-1.0.jar"
set "DATA=%MODULE%src\main\resources"
set "SCENARIOS=%MODULE%scenarios"
set "JAVA=java"
if defined JAVA_HOME set "JAVA=%JAVA_HOME%\bin\java.exe"

if /i "%~1"=="list" goto list
if /i "%~1"=="help" goto usage
if "%~1"=="/?" goto usage

set "CONFIG=%DATA%\van.properties"
set "CATALOG=%DATA%\coffee-catalog.csv"
if "%~1"=="" goto run

set "SCENARIO=%SCENARIOS%\%~1"
if not exist "%SCENARIO%\about.txt" goto unknown
if exist "%SCENARIO%\van.properties" set "CONFIG=%SCENARIO%\van.properties"
if exist "%SCENARIO%\coffee-catalog.csv" set "CATALOG=%SCENARIO%\coffee-catalog.csv"
echo ##### Сценарій: %~1
type "%SCENARIO%\about.txt"
echo.

:run
call :build || goto finish
"%JAVA%" -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -jar "%JAR%" "%CONFIG%" "%CATALOG%"
goto finish

:unknown
echo Сценарій "%~1" не знайдено.
echo.

:list
echo Готові сценарії. Запуск: run ^<назва^>
echo.
for /d %%s in ("%SCENARIOS%\*") do call :describe "%%s"
goto finish

:usage
echo Запуск програми "Фургон кави":
echo   run              стандартні дані з src\main\resources
echo   run list         перелік готових сценаріїв
echo   run ^<назва^>      дані з теки scenarios\^<назва^>
goto finish

:describe
setlocal EnableDelayedExpansion
set "TITLE="
set /p TITLE=<"%~1\about.txt"
set "NAME=%~nx1                        "
echo   !NAME:~0,24!!TITLE!
endlocal
exit /b 0

:build
if exist "%JAR%" (
    powershell -NoProfile -Command "$jar = (Get-Item -LiteralPath $env:JAR).LastWriteTime; $src = Get-ChildItem -LiteralPath (Join-Path $env:MODULE 'src\main\java'), (Join-Path $env:MODULE 'pom.xml'), (Join-Path $env:MODULE '..\pom.xml') -Recurse -File; if ($src | Where-Object { $_.LastWriteTime -gt $jar }) { exit 1 }" && exit /b 0
)
echo Збираю програму, зачекайте...
call "%MODULE%..\mvnw.cmd" -q -f "%MODULE%..\pom.xml" -pl coffee-van -am package -DskipTests
exit /b %errorlevel%

:finish
chcp %OLD_CP% >nul
endlocal
