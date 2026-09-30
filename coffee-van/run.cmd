@echo off
rem ----------------------------------------------------------------------
rem  Запуск програми "Фургон кави" з консолі.
rem
rem    run                      звіт на стандартних даних (src\main\resources)
rem    run <сценарій>           звіт на даних з теки scenarios\<сценарій>
rem    run shell [<сценарій>]   інтерактивний режим: команди add, edit,
rem                             remove, find, load, sort, search тощо
rem    run list                 перелік готових сценаріїв
rem
rem  Програма збирається автоматично, якщо її ще не зібрано
rem  або код змінився після останнього збирання.
rem ----------------------------------------------------------------------
setlocal

for /f "tokens=2 delims=:." %%c in ('chcp ^<nul') do set "OLD_CP=%%c"
chcp 65001 >nul <nul

set "MODULE=%~dp0"
set "JAR=%MODULE%target\coffee-van-1.0.jar"
set "DATA=%MODULE%src\main\resources"
set "SCENARIOS=%MODULE%scenarios"
set "JAVA=java"
if defined JAVA_HOME set "JAVA=%JAVA_HOME%\bin\java.exe"
set "MAIN=ua.lab.coffeevan.CoffeeVanApplication"

if /i "%~1"=="shell" (
    set "MAIN=ua.lab.coffeevan.console.CoffeeVanShell"
    shift
)

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
"%JAVA%" -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -cp "%JAR%" %MAIN% "%CONFIG%" "%CATALOG%"
goto finish

:unknown
echo Сценарій "%~1" не знайдено.
echo.

:list
echo Готові сценарії. Запуск: run ^<назва^> або run shell ^<назва^>
echo.
for /d %%s in ("%SCENARIOS%\*") do call :describe "%%s"
goto finish

:usage
echo Запуск програми "Фургон кави":
echo   run                    звіт на стандартних даних
echo   run ^<назва^>            звіт на даних сценарію scenarios\^<назва^>
echo   run shell [^<назва^>]    інтерактивний режим (команда help - довідка)
echo   run list               перелік готових сценаріїв
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
    powershell -NoProfile -Command "$jar = (Get-Item -LiteralPath $env:JAR).LastWriteTime; $src = Get-ChildItem -LiteralPath (Join-Path $env:MODULE 'src\main\java'), (Join-Path $env:MODULE 'pom.xml'), (Join-Path $env:MODULE '..\pom.xml') -Recurse -File; if ($src | Where-Object { $_.LastWriteTime -gt $jar }) { exit 1 }" <nul && exit /b 0
)
echo Збираю програму, зачекайте...
call "%MODULE%..\mvnw.cmd" -q -f "%MODULE%..\pom.xml" -pl coffee-van -am package -DskipTests <nul
exit /b %errorlevel%

:finish
chcp %OLD_CP% >nul <nul
endlocal
